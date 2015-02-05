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
 ******************************************************************************/
package org.eclipse.persistence.jaxb;

import javax.validation.Constraint;
import javax.validation.Valid;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * INTERNAL:
 *
 * Utility class for bean validation related tasks.
 *  - Singleton.
 *  - Thread-safe.
 *
 * @author Marcel Valovy - marcel.valovy@oracle.com
 * @since 2.6
 */
enum BeanValidationHelper {
    BEAN_VALIDATION_HELPER;

    static {
        ValidationXMLReader.runAsynchronously();
    }

    /**
     * How long to wait for {@link ValidationXMLReader#runAsynchronously()} to finish. This is only used
     * to account for interrupts. If the asynchronous thread is interrupted and flag {@link
     * ValidationXMLReader#firstTime} is false, deadlock occurs. This prevents such deadlock.
     */
    private static final int TIMEOUT = 10; // milliseconds, can specify in single digits since nanoTime is used.

    /**
     * Speed-up flag. Indicates that parsing of validation.xml file has finished.
     */
    private static boolean xmlParsed = false;

    /**
     * Set of all default BeanValidation field or method annotations and known custom field or method constraints.
     */
    private final Set<Class<? extends Annotation>> knownConstraints = new HashSet<>();

     /**
     * Map of all classes that have undergone check for bean validation constraints.
     * Maps the key with boolean value telling whether the class contains an annotation from {@link #knownConstraints}.
     */
    private final Map<Class<?>, Boolean> constraintsOnClasses = new HashMap<>();

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
        knownConstraints.add(Future.class);
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
        knownConstraints.add(Future.List.class);
        knownConstraints.add(Past.List.class);
    }

    /**
     * Put a class to map of constrained classes with value Boolean.TRUE. Specifying value is not allowed because
     * there is no thing to detect that would make class not constrained after it was already found to be constrained.
     * @return value previously associated with the class or null if the class was not in dictionary before
     */
    Boolean putConstrainedClass(Class<?> clazz) {
        return constraintsOnClasses.put(clazz, Boolean.TRUE);
    }

    /**
     * Tells whether any of the class's fields, methods or constructors are constrained by Bean Validation annotations
     * or custom constraints.
     *
     * @param clazz checked class
     * @return true or false
     */
    boolean isConstrained(Class<?> clazz) {
        if (!xmlParsed) ensureValidationXmlWasParsed();

        Boolean annotated = constraintsOnClasses.get(clazz);
        if (annotated == null) {
            annotated = detectConstraints(clazz);
            constraintsOnClasses.put(clazz, annotated);
        }
        return annotated;
    }

    /**
     * INTERNAL:
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

    /**
     * INTERNAL:
     * Ensures that validation.xml was parsed and classes described externally were added to {@link
     * #constraintsOnClasses}.
     * Strategy:
     *  - if {@link ValidationXMLReader#runAsynchronously()} is not doing anything,
     *  run parsing synchronously.
     *  - else if latch count set to 0 aka async call finished, return
     *  - else wait for {@link #TIMEOUT} seconds to allow async thread to finish. If it does not finish till then,
     *  run parsing synchronously.
     *
     *  Note: Run parsing synchronously is force of last resort. If that fails, we proceed with validation and do not
     *  account for classes specified in validation.xml - there is high chance that if we cannot read validation.xml
     *  successfully, neither will be able the Validation implementation.
     */
    private void ensureValidationXmlWasParsed() {
        // loop handles InterruptedException
        while (!xmlParsed) {
            try {
                if (ValidationXMLReader.asyncAttemptFailed()
                        || !ValidationXMLReader.latch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
                    ValidationXMLReader.runSynchronouslyForced();
                }
                xmlParsed = true;
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
