/*******************************************************************************
 * Copyright (c) 2009 Sun Microsystems, Inc. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 ******************************************************************************/

package org.eclipse.persistence.internal.jpa.metadata.listeners;

import javax.validation.*;
import javax.validation.groups.Default;

import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.FetchGroupManager;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

import java.util.Set;
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

    public BeanValidationListener(ValidatorFactory validatorFactory, Class[] groupPrePersit, Class[] groupPreUpdate, Class[] groupPreRemove) {
        this.validatorFactory = validatorFactory;
        //For prePersit and preUpdate, default the group to validation group Default if user has not specified one
        this.groupPrePersit = groupPrePersit != null ? groupPrePersit : groupDefault;
        this.groupPreUpdate = groupPreUpdate != null ? groupPreUpdate : groupDefault;
        //No validation performed on preRemove if user has not explicitly specified a validation group
        this.groupPreRemove = groupPreRemove;
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
        Set<ConstraintViolation<Object>> constraintViolations = getValidator(event).validate(event.getSource(), validationGroup);
        if(constraintViolations.size() > 0) {
            // There were errors while call to validate above.
            // Throw a ConstrainViolationException as required by the spec.
            // The transaction would be rolled back automatically
            // TODO need to I18N this.
            throw new ConstraintViolationException(
                    "Bean Validation constraint(s) violated while executing Automatic Bean Validation on callback event:'" +
                            callbackEventName + "'. Please refer to embedded ConstraintViolations for details.",
                    (Set <ConstraintViolation<?>>)(Object)constraintViolations); //TODO The cast looks like an issue with BV API.

        }
    }

    private Validator getValidator(DescriptorEvent event) {
        TraversableResolver traversableResolver = new AutomaticLifeCycleValidationTraversableResolver(event);
        return validatorFactory.usingContext().traversableResolver(traversableResolver).getValidator();
    }


    /**
     * This traversable resolver ensures that validation is not cascaded to any associations and no lazily loaded
     * attribute is loaded as a side effect of validation
     */
    private static class AutomaticLifeCycleValidationTraversableResolver implements TraversableResolver {

        private ClassDescriptor descriptor;

        AutomaticLifeCycleValidationTraversableResolver(DescriptorEvent event) {
            descriptor = event.getClassDescriptor();
        }


        /**
         * @return false for any lazily loaded property of root object being validated
         */
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
