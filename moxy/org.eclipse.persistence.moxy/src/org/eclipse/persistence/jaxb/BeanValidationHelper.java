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
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
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
 *  - Thread-safe (see note 1).
 *
 *  The same helper class is located in JPA, under org.eclipse.persistence.internal.jpa.metadata.beanvalidation package.
 *  There are two notable differences:
 *   1. This one uses Class objects as keys, and as such is faster. JPA does not depend on BV API so it has to use
 *   string comparisons instead.
 *   2. This one differs in concurrency management.
 *
 * Note 1 - There possibly could be data race on concurrent accesses to the underlying data structure. Here we show that
 * concurrent usage of our class is actually safe.
 *  1. We only add and never remove from data structure.
 *  2. A fresh scan is performed upon not finding an item in data structure, (even if by a stale iterator), so we will
 * always compute with correct values (even if we scan for something which another thread already discovered).
 * After the value is found - here usually comes the problem, but not in our case - we replace the previous entry
 * (there should be none, but due to JMM unspecified behavior in this case, we actually can replace an entry found by
 * another thread) with a new entry, which in our case can have only one possible value, boolean true.
 *
 * @author Marcel Valovy - marcelv3612@gmail.com
 * @since 2.6
 */
enum BeanValidationHelper {
    BEAN_VALIDATION_HELPER;

    static {
        ValidationXMLReader.runAsynchronously();
    }

    /**
     * How much time to allow {@link ValidationXMLReader#runAsynchronously()} to finish its task, in milliseconds.
     * This is an interrupt management system. If the asynchronous thread were interrupted and flag {@link
     * ValidationXMLReader#firstTime} were to be false, deadlock would occur. This prevents such case, as it will
     * await until timeout expires and then run the processing in forced, synchronous way.
     */
    private static final int TIMEOUT = 10; /* milliseconds, can be specified with precision up to single digits since
                                              nanoTime is used. */

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
     * Tells whether any of the class's {@link java.lang.reflect.AccessibleObject}s are constrained by Bean
     * Validation annotations or custom constraints.
     *
     * @param clazz checked class
     * @return true or false
     */
    boolean isConstrained(Class<?> clazz) {
        if (!xmlParsed) ensureValidationXmlWasParsed();

        Boolean constrained = constraintsOnClasses.get(clazz);
        if (constrained == null) {
            constrained = detectConstraints(clazz);
            constraintsOnClasses.put(clazz, constrained);
        }
        return constrained;
    }

    /**
     * INTERNAL:
     * Reveals whether any of the class's {@link java.lang.reflect.AccessibleObject}s are constrained by Bean Validation
     * annotations or custom constraints.
     * Uses reflection.
     * Will accept upon first detected annotation - faster.
     */
    private Boolean detectConstraints(Class<?> clazz) {
        for (Field f : ReflectionUtils.getDeclaredFields(clazz)) {
            if (detectFirstClassConstraints(f)) return true;
        }
        for (Method m : ReflectionUtils.getDeclaredMethods(clazz)) {
            if (detectFirstClassConstraints(m) || detectParameterConstraints(m)) return true;
        }
        for (Constructor<?> c : ReflectionUtils.getDeclaredConstructors(clazz)) {
            if (detectFirstClassConstraints(c) || detectParameterConstraints(c)) return true;
        }
        return false;
    }

    private boolean detectFirstClassConstraints(AccessibleObject accessibleObject) {
        for (Annotation a : accessibleObject.getDeclaredAnnotations()) {
            final Class<? extends Annotation> annType = a.annotationType();
            if (knownConstraints.contains(annType)){
                return true;
            }
            // Check for custom annotations.
            for (Annotation annOnAnnType : annType.getAnnotations()) {
                final Class<? extends Annotation> annTypeOnAnnType = annOnAnnType.annotationType();
                if (Constraint.class == annTypeOnAnnType) {
                    knownConstraints.add(annType);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean detectParameterConstraints(Executable c) {
        for (Annotation[] aa : c.getParameterAnnotations())
            for (Annotation a : aa) {
                final Class<? extends Annotation> annType = a.annotationType();
                if (knownConstraints.contains(annType)) {
                    return true;
                }
                // Check for custom annotations.
                for (Annotation annOnAnnType : annType.getAnnotations()) {
                    final Class<? extends Annotation> annTypeOnAnnType = annOnAnnType.annotationType();
                    if (Constraint.class == annTypeOnAnnType) {
                        knownConstraints.add(annType);
                        return true;
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
     *  - else if latch count is 0, indicating successful finish of asynchronous processing, return.
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
                        || !ValidationXMLReader.getLatch().await(TIMEOUT, TimeUnit.MILLISECONDS)) {
                    ValidationXMLReader.runSynchronouslyForced();
                }
                xmlParsed = true;
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
