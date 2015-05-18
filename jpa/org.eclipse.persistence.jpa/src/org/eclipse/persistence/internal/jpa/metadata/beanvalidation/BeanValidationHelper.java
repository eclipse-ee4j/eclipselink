/*******************************************************************************
 * Copyright (c) 2009, 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      Marcel Valovy - <marcelv3612@gmail.com>
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.beanvalidation;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

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
     * # default constraints in {@link #KNOWN_CONSTRAINTS} map.
     *
     * The value reflects the number of default constraints; if
     * more default constraints are added, update the value.
     */
    private static final int DEFAULT_CONSTRAINTS_QUANTITY = 25;

    /**
     * Load factor for concurrent maps.
     */
    private static final float LOAD_FACTOR = 0.75f;

    /**
     * Size parameter for {@link #KNOWN_CONSTRAINTS} map.
     */
    private static final int KNOWN_CONSTRAINTS_DEFAULT_SIZE = nextPowerOfTwo(
            (int) (DEFAULT_CONSTRAINTS_QUANTITY / LOAD_FACTOR));

    /**
     * Set of all default BeanValidation field annotations and discovered custom field constraints.
     * Implemented as a ConcurrentHashMap with "Null Object" idiom.
     */
    private static final Set<String> KNOWN_CONSTRAINTS = Collections.newSetFromMap(new ConcurrentHashMap<String,
            Boolean>( KNOWN_CONSTRAINTS_DEFAULT_SIZE, LOAD_FACTOR ));

    /**
     * Map of all classes that have undergone check for bean validation constraints.
     * Maps the key with boolean value telling whether the class contains an annotation from {@link #KNOWN_CONSTRAINTS}.
     */
    private static final Map<Class<?>, Boolean> CONSTRAINTS_ON_CLASSES = Collections.synchronizedMap(new
            WeakHashMap<Class<?>, Boolean>());

    static {
        initializeKnownConstraints();
    }

    /**
     * Tells whether any of the class's fields are constrained by Bean Validation annotations or custom constraints.
     *
     * @param clazz checked class
     * @return true or false
     */
    public boolean isConstrained(Class<?> clazz) {
        Boolean annotated = CONSTRAINTS_ON_CLASSES.get(clazz);
        if (annotated == null) {
            annotated = detectConstraints(clazz);
            CONSTRAINTS_ON_CLASSES.put(clazz, annotated);
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

                if (KNOWN_CONSTRAINTS.contains(typeCanonicalName)) {
                    return true;
                }

                // Check for custom annotations on the field (+ check inheritance on class annotations).
                // Custom bean validation annotation is defined by having @Constraint annotation on its class.
                for (Annotation typesClassAnnotation : type.getAnnotations()) {
                    final Class<? extends Annotation> classAnnotationType = typesClassAnnotation.annotationType();
                    if ("javax.validation.Constraint".equals(classAnnotationType.getCanonicalName())) {
                        // Avoid adding anonymous class constraints.
                        if (typeCanonicalName != null) {
                            KNOWN_CONSTRAINTS.add(typeCanonicalName);
                        }
                        return true;
                    }
                }
            }
        }
        for (Method m : getDeclaredMethods(clazz)) {
            for (Annotation a : m.getDeclaredAnnotations()) {
                final Class<? extends Annotation> type = a.annotationType();
                final String typeCanonicalName = type.getCanonicalName();

                if (KNOWN_CONSTRAINTS.contains(typeCanonicalName)) {
                    return true;
                }

                // Check for custom annotations on the field (+ check inheritance on class annotations).
                // Custom bean validation annotation is defined by having @Constraint annotation on its class.
                for (Annotation typesClassAnnotation : type.getAnnotations()) {
                    final Class<? extends Annotation> classAnnotationType = typesClassAnnotation.annotationType();
                    if ("javax.validation.Constraint".equals(classAnnotationType.getCanonicalName())) {
                        // Avoid adding anonymous class constraints.
                        if (typeCanonicalName != null) {
                            KNOWN_CONSTRAINTS.add(typeCanonicalName);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Retrieves declared methods.
     * <p/>
     * If security is enabled, makes {@linkplain java.security.AccessController#doPrivileged(PrivilegedAction)
     * privileged calls}.
     *
     * @param clazz methods of that class will be returned
     * @return array of declared methods
     * @see Class#getDeclaredMethods()
     */
    private Method[] getDeclaredMethods(final Class<?> clazz) {
        return PrivilegedAccessHelper.shouldUsePrivilegedAccess()
                ? AccessController.doPrivileged(
                new PrivilegedAction<Method[]>() {
                    @Override
                    public Method[] run() {
                        return clazz.getDeclaredMethods();
                    }
                })
                : PrivilegedAccessHelper.getDeclaredMethods(clazz);
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
    private Field[] getDeclaredFields(final Class<?> clazz) {
        return PrivilegedAccessHelper.shouldUsePrivilegedAccess()
                ? AccessController.doPrivileged(
                new PrivilegedAction<Field[]>() {
                    @Override
                    public Field[] run() {
                        return clazz.getDeclaredFields();
                    }
                })
                : PrivilegedAccessHelper.getDeclaredFields(clazz);
    }

    /**
     * Adds canonical names of bean validation constraints into set of known constraints (internally represented by
     * map).
     * Canonical name is the name that would be used in an import statement and uniquely identifies the class,
     * i.e. anonymous classes receive a 'null' value as their canonical name,
     * which allows no ambiguity and is what we are looking for.
     */
    private static void initializeKnownConstraints() {
        KNOWN_CONSTRAINTS.add("javax.validation.Valid");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.Max");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.Min");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.DecimalMax");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.DecimalMin");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.Digits");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.NotNull");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.Pattern");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.Size");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.AssertTrue");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.AssertFalse");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.Future");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.Past");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.List.Max");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.List.Min");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.List.DecimalMax");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.List.DecimalMin");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.List.Digits");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.List.NotNull");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.List.Pattern");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.List.Size");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.List.AssertTrue");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.List.AssertFalse");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.List.Future");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.List.Past");
    }


    /**
     * Calculate the next power of 2, greater than or equal to x.<p>
     * From Hacker's Delight, Chapter 3, Harry S. Warren Jr.
     *
     * @param x integer greater than 0
     * @return next power of two
     */
    private static int nextPowerOfTwo(int x) {
        assert x > 0;
        x |= --x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        x |= x >> 16;
        return x + 1;
    }
}
