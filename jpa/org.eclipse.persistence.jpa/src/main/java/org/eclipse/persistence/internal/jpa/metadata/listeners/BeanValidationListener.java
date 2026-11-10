/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 2009, 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
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

import java.lang.annotation.ElementType;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.TraversableResolver;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.groups.Default;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.descriptors.FetchGroupManager;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

/**
 * Responsible for performing automatic bean validation on call back events.
 * @author Mitesh Meswani
 */
public class BeanValidationListener extends DescriptorEventAdapter {
    private final ValidatorFactory validatorFactory;
    private final boolean suppliedValidatorFactory;
    private final Class<?>[] groupPrePersist;
    private final Class<?>[] groupPreMerge;
    private final Class<?>[] groupPreRemove;
    private final Class<?>[] groupPreInsert;
    private final Class<?>[] groupPreUpdate;
    private final Class<?>[] groupPreUpsert;
    private final Class<?>[] groupPreDelete;
    private static final Class<?>[] groupDefault = new Class<?>[]{Default.class};
    private final Map<String, Validator> validatorMap;

    public BeanValidationListener(ValidatorFactory validatorFactory, Class<?>[] groupPrePersist,
            Class<?>[] groupPreUpdate, Class<?>[] groupPreRemove) {
        this(validatorFactory, groupPrePersist, null, groupPreRemove, null, groupPreUpdate, null, null);
    }

    public BeanValidationListener(ValidatorFactory validatorFactory, Class<?>[] groupPrePersist,
            Class<?>[] groupPreMerge, Class<?>[] groupPreRemove, Class<?>[] groupPreInsert,
            Class<?>[] groupPreUpdate, Class<?>[] groupPreUpsert, Class<?>[] groupPreDelete) {
        this(validatorFactory, groupPrePersist, groupPreMerge, groupPreRemove, groupPreInsert,
                groupPreUpdate, groupPreUpsert, groupPreDelete, false);
    }

    public BeanValidationListener(ValidatorFactory validatorFactory, Class<?>[] groupPrePersist,
            Class<?>[] groupPreMerge, Class<?>[] groupPreRemove, Class<?>[] groupPreInsert,
            Class<?>[] groupPreUpdate, Class<?>[] groupPreUpsert, Class<?>[] groupPreDelete,
            boolean suppliedValidatorFactory) {
        this.validatorFactory = validatorFactory;
        this.suppliedValidatorFactory = suppliedValidatorFactory;
        this.groupPrePersist = groups(groupPrePersist, false);
        this.groupPreMerge = groups(groupPreMerge, false);
        this.groupPreRemove = groups(groupPreRemove, false);
        this.groupPreInsert = groups(groupPreInsert, true);
        this.groupPreUpdate = groups(groupPreUpdate, true);
        this.groupPreUpsert = groups(groupPreUpsert, true);
        this.groupPreDelete = groups(groupPreDelete, false);
        validatorMap = new ConcurrentHashMap<>();
    }

    private static Class<?>[] groups(Class<?>[] configured, boolean useDefault) {
        return configured == null ? (useDefault ? groupDefault : new Class<?>[0]) : configured.clone();
    }

    @Override
    public void prePersist(DescriptorEvent event) {
        validateOnCallbackEvent(event, "prePersist", groupPrePersist);
    }

    @Override
    public void preMerge(DescriptorEvent event) {
        validateOnCallbackEvent(event, "preMerge", groupPreMerge);
    }

    @Override
    public void preRemove(DescriptorEvent event) {
        validateOnCallbackEvent(event, "preRemove", groupPreRemove);
    }

    @Override
    public void preInsert(DescriptorEvent event) {
        validateOnCallbackEvent(event, "preInsert", groupPreInsert);
    }

    @Override
    public void preUpdateWithChanges(DescriptorEvent event) {
        if (event.getSession() instanceof UnitOfWorkImpl unitOfWork) {
            // Removed entities and version-only lock writes do not validate update groups.
            ObjectChangeSet changeSet = event.getChangeSet();
            if (unitOfWork.isObjectDeleted(event.getSource()) || changeSet == null
                    || (!changeSet.isNew() && (changeSet.getChanges() == null || changeSet.getChanges().isEmpty()))) {
                return;
            }
        }
        validateOnCallbackEvent(event, "preUpdate", groupPreUpdate);
    }

    @Override
    public void preUpsert(DescriptorEvent event) {
        validateOnCallbackEvent(event, "preUpsert", groupPreUpsert);
    }

    @Override
    public void preDelete(DescriptorEvent event) {
        validateOnCallbackEvent(event, "preDelete", groupPreDelete);
    }

    private void validateOnCallbackEvent(DescriptorEvent event, String callbackEventName, Class<?>[] validationGroup) {
        if (validationGroup.length == 0) {
            return;
        }
        Object source = event.getSource();
        Validator validator = getValidator(event);
        boolean noOptimization = "true".equalsIgnoreCase((String) event.getSession().getProperty(PersistenceUnitProperties.BEAN_VALIDATION_NO_OPTIMISATION));
        // A supplied factory controls validation itself and may instrument every call.
        boolean shouldValidate = suppliedValidatorFactory || noOptimization || isBeanConstrained(source, validator);
        if (shouldValidate) {
            Set<ConstraintViolation<Object>> constraintViolations = validate(source, validationGroup, validator);
            if (!constraintViolations.isEmpty()) {
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
        String alias = descriptor.getAlias();
        Validator res = validatorMap.get(alias);
        if (res == null) {
            TraversableResolver traversableResolver = new AutomaticLifeCycleValidationTraversableResolver(descriptor);
            res = validatorFactory.usingContext().traversableResolver(traversableResolver).getValidator();

            Validator t = validatorMap.put(alias, res);
            if (t != null) {
                // Threading collision, use existing
                res = t;
            }
        }

        return res;
    }

    /**
     * Returns if a bean/entity is constrained by calling the bean validation provider's
     * #jakarta.validation.metadata.BeanDescriptor.isBeanConstrained method.
     */
    private boolean isBeanConstrained(final Object source, final Validator validator) {
        // If Java Security is enabled, surround this call with a doPrivileged block.
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            return AccessController.doPrivileged(new PrivilegedAction<>() {
                @Override
                public Boolean run() {
                    return validator.getConstraintsForClass(source.getClass()).isBeanConstrained();

                }
            });
        } else {
            return validator.getConstraintsForClass(source.getClass()).isBeanConstrained();
        }
    }

    private Set<ConstraintViolation<Object>> validate(final Object source, final Class<?>[] validationGroup, final Validator validator) {
        // If Java Security is enabled, surround this call with a doPrivileged block.
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            return AccessController.doPrivileged(new PrivilegedAction<>() {
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


        @Override
        public boolean isReachable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType,
                Path pathToTraversableObject, ElementType elementType) {
            ClassDescriptor current = descriptorForPath(pathToTraversableObject);
            if (current == null || traversableObject == null) {
                return true;
            }
            String attributeName = traversableProperty.getName();
            DatabaseMapping mapping = current.getObjectBuilder().getMappingForAttributeName(attributeName);
            if (mapping == null) {
                return true;
            }
            FetchGroupManager fetchGroupManager = current.getFetchGroupManager();
            if (fetchGroupManager != null && !fetchGroupManager.isAttributeFetched(traversableObject, attributeName)) {
                return false;
            }
            if (mapping.isForeignReferenceMapping() && mapping.isLazy()) {
                Object value = mapping.getAttributeAccessor().getAttributeValueFromObject(traversableObject);
                return ((ForeignReferenceMapping) mapping).getIndirectionPolicy().objectIsInstantiatedOrChanged(value);
            }
            return true;
        }

        @Override
        public boolean isCascadable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType,
                Path pathToTraversableObject, ElementType elementType) {
            ClassDescriptor current = descriptorForPath(pathToTraversableObject);
            if (current == null) {
                return true;
            }
            DatabaseMapping mapping = current.getObjectBuilder().getMappingForAttributeName(traversableProperty.getName());
            // Element collections are owned values, not entity associations.
            return mapping == null || !mapping.isForeignReferenceMapping()
                    || mapping.isAggregateCollectionMapping() || mapping.isDirectCollectionMapping()
                    || mapping.isDirectMapMapping();
        }

        private ClassDescriptor descriptorForPath(Path path) {
            ClassDescriptor current = descriptor;
            for (Path.Node node : path) {
                String name = node.getName();
                if (name == null || name.startsWith("<")) {
                    continue;
                }
                DatabaseMapping mapping = current.getObjectBuilder().getMappingForAttributeName(name);
                if (mapping == null || mapping.getReferenceDescriptor() == null) {
                    return null;
                }
                current = mapping.getReferenceDescriptor();
            }
            return current;
        }
    }

}
