/*
 * Copyright (c) 2009, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     08/20/2014-2.5 Rick Curtis
//       - 441890: Cache Validator instances.
//     Marcel Valovy - 2.6 - skip validation of objects that are not constrained.
//     02/23/2016-2.6 Dalia Abo Sheasha
//       - 487889: Fix EclipseLink Bean Validation optimization
//     03/09/2016-2.6 Dalia Abo Sheasha
//       - 489298: Wrap EclipseLink's Bean Validation calls in doPrivileged blocks when security is enabled

package org.eclipse.persistence.internal.jpa.metadata.listeners;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.TraversableResolver;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.FetchGroupManager;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.annotation.ElementType;

/**
 * Responsible for performing automatic bean validation on call back events.
 * @author Mitesh Meswani
 */
public class BeanValidationListener extends DescriptorEventAdapter {
    private final ValidatorFactory validatorFactory;
    private final Class[] groupPrePersit;
    private final Class[] groupPreUpdate;
    private final Class[] groupPreRemove;
    private static final Class[] groupDefault = new Class[]{Default.class};
    private final Map<ClassDescriptor, Validator> validatorMap;

    public BeanValidationListener(ValidatorFactory validatorFactory, Class[] groupPrePersit, Class[] groupPreUpdate, Class[] groupPreRemove) {
        this.validatorFactory = validatorFactory;
        //For prePersit and preUpdate, default the group to validation group Default if user has not specified one
        this.groupPrePersit = groupPrePersit != null ? groupPrePersit : groupDefault;
        this.groupPreUpdate = groupPreUpdate != null ? groupPreUpdate : groupDefault;
        //No validation performed on preRemove if user has not explicitly specified a validation group
        this.groupPreRemove = groupPreRemove;

        validatorMap = new ConcurrentHashMap<>();
    }

    @Override
    public void prePersist (DescriptorEvent event) {
        //  since we are using prePersist to perform validation, invlid data may get inserted into database as shown by
        // following example
        //    tx.begin()
        //    e = new MyEntity(...);
        //    em.perist(e); // prePersist validation happens here
        //    em.setXXX("invalid data");
        //    tx.commit();
        //  "invalid data" would get inserted into database.
        //
        //  preInsert can be used to work around above issue. Howerver, the JPA spec does not itent it.
        //  This might be corrected in next iteration of spec
        validateOnCallbackEvent(event, "prePersist", groupPrePersit);
    }

    @Override
    public void preUpdate (DescriptorEvent event) {
        Object source = event.getSource();
        UnitOfWorkImpl unitOfWork = (UnitOfWorkImpl )event.getSession();
        // preUpdate is also generated for deleted objects that were modified in this UOW.
        // Do not perform preUpdate validation for such objects as preRemove would have already been called.
        if(!unitOfWork.isObjectDeleted(source)) {
            validateOnCallbackEvent(event, "preUpdate", groupPreUpdate);
        }
    }

    @Override
    public void preRemove (DescriptorEvent event) {
        if(groupPreRemove != null) { //No validation performed on preRemove if user has not explicitly specified a validation group
           validateOnCallbackEvent(event, "preRemove", groupPreRemove);
        }
    }

    private void validateOnCallbackEvent(DescriptorEvent event, String callbackEventName, Class[] validationGroup) {
        Object source = event.getSource();
        Validator validator = getValidator(event);
        boolean isBeanConstrained = isBeanConstrained(source, validator);
        boolean noOptimization = "true".equalsIgnoreCase((String) event.getSession().getProperty(PersistenceUnitProperties.BEAN_VALIDATION_NO_OPTIMISATION));
        boolean shouldValidate = noOptimization || isBeanConstrained;
        if (shouldValidate) {
            Set<ConstraintViolation<Object>> constraintViolations = validate(source, validationGroup, validator);
            if (constraintViolations.size() > 0) {
                // There were errors while call to validate above.
                // Throw a ConstrainViolationException as required by the spec.
                // The transaction would be rolled back automatically
                throw new ConstraintViolationException(
                        ExceptionLocalization.buildMessage("bean_validation_constraint_violated", 
                                new Object[]{callbackEventName, source.getClass().getName()}),
                        (Set<ConstraintViolation<?>>) (Object) constraintViolations); /* Do not remove the explicit
                        cast. This issue is related to capture#a not being instance of capture#b. */
            }
        }
    }

    private Validator getValidator(DescriptorEvent event) {
        ClassDescriptor descriptor = event.getDescriptor();
        Validator res = validatorMap.get(descriptor);
        if (res == null) {
            TraversableResolver traversableResolver = new AutomaticLifeCycleValidationTraversableResolver(descriptor);
            res = validatorFactory.usingContext().traversableResolver(traversableResolver).getValidator();

            Validator t = validatorMap.put(descriptor, res);
            if (t != null) {
                // Threading collision, use existing
                res = t;
            }
        }

        return res;
    }

    /**
     * Returns if a bean/entity is constrained by calling the bean validation provider's
     * #javax.validation.metadata.BeanDescriptor.isBeanConstrained method.
     */
    private boolean isBeanConstrained(final Object source, final Validator validator) {
        // If Java Security is enabled, surround this call with a doPrivileged block.
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
                @Override
                public Boolean run() {
                    return validator.getConstraintsForClass(source.getClass()).isBeanConstrained();

                }
            });
        } else {
            return validator.getConstraintsForClass(source.getClass()).isBeanConstrained();
        }
    }

    private Set<ConstraintViolation<Object>> validate(final Object source, final Class[] validationGroup, final Validator validator) {
        // If Java Security is enabled, surround this call with a doPrivileged block.
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            return AccessController.doPrivileged(new PrivilegedAction<Set<ConstraintViolation<Object>>>() {
                @Override
                public Set<ConstraintViolation<Object>> run() {
                    return validator.validate(source, validationGroup);

                }
            });
        } else {
            return validator.validate(source, validationGroup);
        }
    }

    /**
     * This traversable resolver ensures that validation is not cascaded to any associations and no lazily loaded
     * attribute is loaded as a side effect of validation
     */
    private static class AutomaticLifeCycleValidationTraversableResolver implements TraversableResolver {

        private final ClassDescriptor descriptor;

        AutomaticLifeCycleValidationTraversableResolver(ClassDescriptor eventDescriptor) {
            descriptor = eventDescriptor;
        }


        /**
         * @return false for any lazily loaded property of root object being validated
         */
        @Override
        public boolean isReachable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType) {
            boolean reachable = true;
            String attributeName = null;
            if (isRootObjectPath(pathToTraversableObject)) {
                attributeName = traversableProperty.getName(); //Refer to section 4.2 of Bean Validation spec for more details about Path.Node
                DatabaseMapping mapping = getMappingForAttributeName(attributeName);
                if(mapping != null) {
                    if(mapping.isForeignReferenceMapping()) {
                        // For lazy relationships check whether it is instantiated
                        if(mapping.isLazy()) {
                            Object attributeValue = mapping.getAttributeAccessor().getAttributeValueFromObject(traversableObject);
                            reachable = ((ForeignReferenceMapping)mapping).getIndirectionPolicy().objectIsInstantiatedOrChanged(attributeValue);
                        }
                    } else {
                        // For lazy non relationship attributes, check whether it is fetched
                        FetchGroupManager fetchGroupManager = descriptor.getFetchGroupManager();
                        if (fetchGroupManager != null) {
                            reachable = fetchGroupManager.isAttributeFetched(traversableObject, attributeName);
                        }
                    }
                }
            }
            return reachable;

        }

        /**
         * Called only if isReachable returns true
         * @return false for any associatons of root object being validated true otherwise
         */
        @Override
        public boolean isCascadable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType) {
            boolean cascadable = true;
            if (isRootObjectPath(pathToTraversableObject)) {
                String attributeName = traversableProperty.getName(); //Refer to section 4.2 of Bean Validation spec for more details about Path
                DatabaseMapping mapping = getMappingForAttributeName(attributeName);
                if(mapping != null && mapping.isForeignReferenceMapping()) {
                    cascadable = false;
                }
            }

            return cascadable;
        }

        /**
         * @return DatabaseMapping for given attribute name
         */
        private DatabaseMapping getMappingForAttributeName(String attributeName) {
            return descriptor.getObjectBuilder().getMappingForAttributeName(attributeName);
        }

        /**
         * @return true if given path corresponds to Root Object else false.
         */
        private boolean isRootObjectPath(Path pathToTraversableObject) {
            // From Bean Validation spec section 3.5.2
            // <quote>
            //    pathToTraversableObject is the Path from the rootBeanType down to the traversableObject (it is composed of
            //    a single Node whose name is null if the root object is traversableObject). The path is described following the
            //    conventions described in Section 4.2 (getPropertyPath).
            // </quote>
            return pathToTraversableObject.iterator().next().getName() == null;
        }
    }

}
