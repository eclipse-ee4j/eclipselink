/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Marcel Valovy - 2.6 - initial API and implementation
 *     Dmitry Kornilov - 2.6.1 - removed static maps
 ******************************************************************************/
package org.eclipse.persistence.jaxb;

import javax.validation.Constraint;
import javax.validation.Valid;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Logger;

/**
 * INTERNAL:
 *
 * Asynchronously starts validation.xml file processing when created. Holds a map of classes with BV annotations.
 *
 * @author Marcel Valovy, Dmitry Kornilov
 * @since 2.6
 */
final class BeanValidationHelper {

    private static final Logger LOGGER = Logger.getLogger(BeanValidationHelper.class.getName());

    private Future<Map<Class<?>, Boolean>> future;
    private ExecutorService executor;

    /**
     * Set of all default BeanValidation field or method annotations and known custom field or method constraints.
     */
    private final Set<Class<? extends Annotation>> knownConstraints = new HashSet<>();

     /**
     * Map of all classes that have undergone check for bean validation constraints.
     * Maps the key with boolean value telling whether the class contains an annotation from {@link #knownConstraints}.
     */
    private Map<Class<?>, Boolean> constraintsOnClasses = null;

    {
        knownConstraints.add(Valid.class);
        knownConstraints.add(Max.class);
        knownConstraints.add(Min.class);
        knownConstraints.add(DecimalMax.class);
        knownConstraints.add(DecimalMin.class);
        knownConstraints.add(Digits.class);
        knownConstraints.add(NotNull.class);
        knownConstraints.add(Pattern.class);
        knownConstraints.add(Size.class);
        knownConstraints.add(AssertTrue.class);
        knownConstraints.add(AssertFalse.class);
        knownConstraints.add(javax.validation.constraints.Future.class);
        knownConstraints.add(Past.class);
        knownConstraints.add(Max.List.class);
        knownConstraints.add(Min.List.class);
        knownConstraints.add(DecimalMax.List.class);
        knownConstraints.add(DecimalMin.List.class);
        knownConstraints.add(Digits.List.class);
        knownConstraints.add(NotNull.List.class);
        knownConstraints.add(Pattern.List.class);
        knownConstraints.add(Size.List.class);
        knownConstraints.add(AssertTrue.List.class);
        knownConstraints.add(AssertFalse.List.class);
        knownConstraints.add(javax.validation.constraints.Future.List.class);
        knownConstraints.add(Past.List.class);
    }

    /**
     * Creates a new instance. Starts asynchronous parsing of validation.xml.
     */
    public BeanValidationHelper() {
        Crate.Tuple<ExecutorService, Boolean> crate = Concurrent.getManagedSingleThreadedExecutorService();
        executor = crate.getPayload1();
        future = executor.submit(new ValidationXMLReader());
    }

    /**
     * Tells whether any of the class's fields, methods or constructors are constrained by Bean Validation annotations
     * or custom constraints.
     *
     * @param clazz checked class
     * @return true or false
     */
    boolean isConstrained(Class<?> clazz) {
        Boolean annotated = getConstraintsMap().get(clazz);
        if (annotated == null) {
            annotated = detectConstraints(clazz);
            getConstraintsMap().put(clazz, annotated);
        }
        return annotated;
    }

    /**
     * Lazy getter for constraintsOnClasses property. Waits until the map is returned by async XML reader.
     */
    private Map<Class<?>, Boolean> getConstraintsMap() {
        if (constraintsOnClasses == null) {
            try {
                constraintsOnClasses = future.get();
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.warning("Error parsing validation.xml");
            }
        }
        return constraintsOnClasses;
    }

    /**
     * Reveals whether any of the class's fields or methods are constrained by Bean Validation annotations or custom
     * constraints.
     * Uses reflection.
     */
    private Boolean detectConstraints(Class<?> clazz) {
        for (Field f : ReflectionUtils.getDeclaredFields(clazz)) {
            for (Annotation a : f.getDeclaredAnnotations()) {
                final Class<? extends Annotation> type = a.annotationType();
                if (knownConstraints.contains(type)){
                    return true;
                }
                // Check for custom annotations on the field (+ check inheritance on class annotations).
                // Custom bean validation annotation is defined by having @Constraint annotation on its class.
                for (Annotation typesClassAnnotation : type.getAnnotations()) {
                    final Class<? extends Annotation> classAnnotationType = typesClassAnnotation.annotationType();
                    if (Constraint.class == classAnnotationType) {
                        knownConstraints.add(type);
                        return true;
                    }
                }
            }
        }
        for (Method m : ReflectionUtils.getDeclaredMethods(clazz)) {
            for (Annotation a : m.getDeclaredAnnotations()) {
                final Class<? extends Annotation> type = a.annotationType();
                if (knownConstraints.contains(type)){
                    return true;
                }
                // Check for custom annotations on the method (+ check inheritance on class annotations).
                // Custom bean validation annotation is defined by having @Constraint annotation on its class.
                for (Annotation typesClassAnnotation : type.getAnnotations()) {
                    final Class<? extends Annotation> classAnnotationType = typesClassAnnotation.annotationType();
                    if (Constraint.class == classAnnotationType) {
                        knownConstraints.add(type);
                        return true;
                    }
                }
            }
        }
        for (Constructor<?> c : ReflectionUtils.getDeclaredConstructors(clazz)) {
            for (Annotation a : c.getDeclaredAnnotations()) {
                final Class<? extends Annotation> type = a.annotationType();
                if (knownConstraints.contains(type)){
                    return true;
                }
                // Check for custom annotations on the constructor (+ check inheritance on class annotations).
                // Custom bean validation annotation is defined by having @Constraint annotation on its class.
                for (Annotation typesClassAnnotation : type.getAnnotations()) {
                    final Class<? extends Annotation> classAnnotationType = typesClassAnnotation.annotationType();
                    if (Constraint.class == classAnnotationType) {
                        knownConstraints.add(type);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
