/*******************************************************************************
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Marcel Valovy - 2.6 - initial API and implementation
 ******************************************************************************/
 package org.eclipse.persistence.internal.jpa.metadata.beanvalidation;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import static java.security.AccessController.doPrivileged;

/**
 * INTERNAL:
 *
 * Utility class for bean validation related tasks.
 *  - Singleton.
 *  - Thread-safe.
 */
public enum BeanValidationHelper {
    BEAN_VALIDATION_HELPER;

    /**
     * States whether security is enabled.
     */
    private static final boolean SECURITY_MANAGED = PrivilegedAccessHelper.shouldUsePrivilegedAccess();

    /**
     * Set of all default BeanValidation field annotations and known custom field constraints.
     */
    private final Map<String, Void> knownConstraints = new ConcurrentHashMap<>();

    {
        // Adding canonical names of bean validation constraints.
        // Canonical name is the name that would be used in an import statement and uniquely identifies the class,
        // i.e. anonymous classes receive a 'null' value as their canonical name,
        // which allows no ambiguity and is what we are looking for.
        knownConstraints.put("javax.validation.Valid", null);
        knownConstraints.put("javax.validation.constraints.Max", null);
        knownConstraints.put("javax.validation.constraints.Min", null);
        knownConstraints.put("javax.validation.constraints.DecimalMax", null);
        knownConstraints.put("javax.validation.constraints.DecimalMin", null);
        knownConstraints.put("javax.validation.constraints.Digits", null);
        knownConstraints.put("javax.validation.constraints.NotNull", null);
        knownConstraints.put("javax.validation.constraints.Pattern", null);
        knownConstraints.put("javax.validation.constraints.Size", null);
        knownConstraints.put("javax.validation.constraints.AssertTrue", null);
        knownConstraints.put("javax.validation.constraints.AssertFalse", null);
        knownConstraints.put("javax.validation.constraints.Future", null);
        knownConstraints.put("javax.validation.constraints.Past", null);
        knownConstraints.put("javax.validation.constraints.List.Max", null);
        knownConstraints.put("javax.validation.constraints.List.Min", null);
        knownConstraints.put("javax.validation.constraints.List.DecimalMax", null);
        knownConstraints.put("javax.validation.constraints.List.DecimalMin", null);
        knownConstraints.put("javax.validation.constraints.List.Digits", null);
        knownConstraints.put("javax.validation.constraints.List.NotNull", null);
        knownConstraints.put("javax.validation.constraints.List.Pattern", null);
        knownConstraints.put("javax.validation.constraints.List.Size", null);
        knownConstraints.put("javax.validation.constraints.List.AssertTrue", null);
        knownConstraints.put("javax.validation.constraints.List.AssertFalse", null);
        knownConstraints.put("javax.validation.constraints.List.Future", null);
        knownConstraints.put("javax.validation.constraints.List.Past", null);
    }

    /**
     * Map of all classes that have undergone check for bean validation constraints.
     * Maps the key with boolean value telling whether the class contains an annotation from {@link #knownConstraints}.
     */
    private final Map<Class<?>, Boolean> constraintsOnClasses = new ConcurrentHashMap<>();

    /**
     * Tells whether any of the class's fields are constrained by Bean Validation annotations or custom constraints.
     *
     * @param clazz checked class
     * @return true or false
     */
    public boolean isConstrained(Class<?> clazz) {
        Boolean annotated = constraintsOnClasses.get(clazz);
        if (annotated == null) {
            annotated = detectConstraints(clazz);
            constraintsOnClasses.put(clazz, annotated);
        }
        return annotated;
    }

    /**
     * INTERNAL:
     * Reveals whether any of the class's fields are constrained by Bean Validation annotations or custom constraints.
     * Uses reflection.
     * @see Class#getDeclaredFields
     */
    private Boolean detectConstraints(Class<?> clazz) {
        for (Field f : getDeclaredFields(clazz)) {
            for (Annotation a : f.getDeclaredAnnotations()) {
                final Class<? extends Annotation> type = a.annotationType();
                final String typeCanonicalName = type.getCanonicalName();

                if (knownConstraints.containsKey(typeCanonicalName)){
                    return true;
                }

                // Check for custom annotations on the field (+ check inheritance on class annotations).
                // Custom bean validation annotation is defined by having @Constraint annotation on its class.
                for (Annotation typesClassAnnotation : type.getAnnotations()) {
                    final Class<? extends Annotation> classAnnotationType = typesClassAnnotation.annotationType();
                    if ("javax.validation.Constraint".equals(classAnnotationType.getCanonicalName())) {
                        // Avoid adding anonymous class constraints.
                        if (typeCanonicalName != null) {
                            knownConstraints.put(typeCanonicalName, null);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Retrieves declared fields.
     * <p/>
     * If security is enabled, makes {@linkplain java.security.AccessController#doPrivileged(PrivilegedAction)
     * privileged calls}.
     *
     * @param clazz fields of that class will be returned
     * @return array of declared fields
     * @see Class#getDeclaredFields()
     */
    static Field[] getDeclaredFields(Class<?> clazz) {
        return SECURITY_MANAGED
                ? doPrivileged(DeclaredFieldsIODH.PREDICATE_ACTION.with(clazz))
                : getDeclaredFieldsInternal(clazz);
    }

    /**
     * INTERNAL:
     */
    private static Field[] getDeclaredFieldsInternal(Class<?> clazz) {
        return clazz.getDeclaredFields();
    }

    /**
     * IODH for enhanced getDeclaredFields privileged action using predicates.
     */
    private static final class DeclaredFieldsIODH {

        /**
         * Enhanced {@link PrivilegedAction} using predicates.
         *  - Thread-safe.
         *  - Singleton.
         *  - Throws {@link NoSuchMethodException}.
         */
        private static final Predicate<Field[]> PREDICATE_ACTION = new Predicate<Field[]>() {

            /* Predicates */
            private Class<?> clazz;

            /**
             * Lock that allows for concurrent usage of this instance.
             */
            private final ReentrantLock lock = new ReentrantLock();

            @Override
            public Field[] run() {
                // We are still inside memory barrier, thus it is guaranteed that value of #clazz was assigned by
                // current thread.
                Class<?> localReference = clazz;
                // Leaving memory barrier before executing cpu extensive operation.
                lock.unlock();
                // Computation on value that is guaranteed to have been assigned by current thread.
                return getDeclaredFieldsInternal(localReference);
            }

            @Override
            public Predicate<Field[]> with(Class<?> clazz) {
                lock.lock();
                this.clazz = clazz;
                return this;
            }
        };
    }

    /**
     * Predicate-providing wrapper for {@link PrivilegedAction}.
     *
     * @param <T> return type of {@linkplain PrivilegedAction#run() computation}
     */
    private interface Predicate<T> extends PrivilegedAction<T> {

        /**
         * Assigns a predicate to the underlying privileged action.
         * Any previous predicate of the same type will be overwritten.
         *
         * @param with predicate
         * @return {@code this}
         */
        Predicate<T> with(Class<?> with);
    }

}
