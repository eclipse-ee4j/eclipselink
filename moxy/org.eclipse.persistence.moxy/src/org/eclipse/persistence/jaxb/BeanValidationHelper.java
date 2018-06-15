/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Marcel Valovy - 2.6 - initial API and implementation
//     Dmitry Kornilov - 2.6.1 - removed static maps
package org.eclipse.persistence.jaxb;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.naming.InitialContext;
import javax.naming.NamingException;
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

import org.eclipse.persistence.internal.cache.AdvancedProcessor;
import org.eclipse.persistence.internal.cache.ComputableTask;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;

/**
 * INTERNAL:
 * Asynchronously starts validation.xml file processing when created. Holds a map of classes with BV annotations.
 *
 * @author Marcel Valovy, Dmitry Kornilov
 * @since 2.6
 */
final public class BeanValidationHelper {

    private Future<Map<Class<?>, Boolean>> future;

    /**
     * Advanced memoizer.
     */
    private final AdvancedProcessor memoizer = new AdvancedProcessor();

    /**
     * Computable task for memoizer.
     */
    private final ConstraintsDetectorService<Class<?>, Boolean> cds = new ConstraintsDetectorService<>();

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
     * Creates a new instance. Starts asynchronous parsing of validation.xml if it exists.
     */
    public BeanValidationHelper() {
        // Try to run validation.xml parsing asynchronously if validation.xml exists
        if (ValidationXMLReader.isValidationXmlPresent()) {
            parseValidationXmlAsync();
        } else {
            // validation.xml doesn't exist -> create an empty map
            constraintsOnClasses = new HashMap<>();
        }
    }

    /**
     * Tells whether any of the class's {@link java.lang.reflect.AccessibleObject}s are constrained by Bean
     * Validation annotations or custom constraints.
     *
     * @param clazz checked class
     * @return true or false
     */
    boolean isConstrained(Class<?> clazz) {
        return memoizer.compute(cds, clazz);
    }

    @SuppressWarnings("unchecked")
    public class ConstraintsDetectorService<A, V> implements ComputableTask<A, V> {

        @Override
        public V compute(A arg) throws InterruptedException {
            Boolean b = isConstrained0((Class<?>) arg);
            return (V) b;
        }

        private Boolean isConstrained0(Class<?> clazz) {
            Boolean constrained = getConstraintsMap().get(clazz);
            if (constrained == null) {
                constrained = detectConstraints(clazz);
                constraintsOnClasses.put(clazz, constrained);
            }
            return constrained;
        }

        /**
         * INTERNAL:
         * Determines whether this class is subject to validation according to rules in BV spec.
         * Will accept upon first detected annotation - faster.
         * Uses reflection, recursion and DP.
         */
        private Boolean detectConstraints(Class<?> clazz) {
            if (detectAncestorConstraints(clazz)) return true;

            for (Field f : ReflectionUtils.getDeclaredFields(clazz)) {
                if ((f.getModifiers() & Modifier.STATIC) != 0) continue; // section 4.1 of BV spec
                if (detectFirstClassConstraints(f)) return true;
            }

            for (Method m : ReflectionUtils.getDeclaredMethods(clazz)) {
                if ((m.getModifiers() & Modifier.STATIC) != 0) continue; // section 4.1 of BV spec
                if (detectFirstClassConstraints(m) || detectParameterConstraints(m)) return true;
            }

            // length 0 if an interface, a primitive type, an array class, or void
            for (Constructor<?> c : ReflectionUtils.getDeclaredConstructors(clazz)) {
                if (clazz.isEnum()) continue; // cannot construct enum instances during runtime
                if (detectFirstClassConstraints(c) || detectParameterConstraints(c)) return true;
            }
            return false;
        }

        /**
         * Recursively detects constraints on ancestors. Uses strong form of dynamic programming.
         *
         * @param clazz class whose ancestors are to be scanned
         * @return true if any of the ancestors are constrained
         */
        private boolean detectAncestorConstraints(Class<?> clazz) {
            /* If this Class represents either the Object class, an interface, a primitive type, or void, then null is
               returned. If this object represents an array class then the Class object representing the Object class
               is returned. */
            Class<?> superClass = clazz.getSuperclass();
            if (superClass == null) return false;
            return memoizer.compute(cds, superClass);
        }

        private boolean detectFirstClassConstraints(AccessibleObject accessibleObject) {
            for (Annotation a : accessibleObject.getDeclaredAnnotations()) {
                final Class<? extends Annotation> annType = a.annotationType();
                if (knownConstraints.contains(annType)){
                    return true;
                }
                // detect custom annotations
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
                    // detect custom annotations
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
    }

    /**
     * Lazy getter for constraintsOnClasses property. Waits until the map is returned by async XML reader.
     */
    public Map<Class<?>, Boolean> getConstraintsMap() {
        if (constraintsOnClasses == null) {
            if (future == null) {
                // This happens when submission of async task is failed. Run validation.xml parsing synchronously.
                constraintsOnClasses = parseValidationXml();
            } else {
                // Async task was successfully submitted. get a result from future
                try {
                    constraintsOnClasses = future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // For some reason the async parsing attempt failed. Call it synchronously.
                    AbstractSessionLog.getLog().log(SessionLog.WARNING, SessionLog.MOXY, "Error parsing validation.xml the async way", new Object[0], false);
                    AbstractSessionLog.getLog().logThrowable(SessionLog.WARNING, SessionLog.MOXY, e);
                    constraintsOnClasses = parseValidationXml();
                }
            }
        }
        return constraintsOnClasses;
    }

    /**
     * Tries to run validation.xml parsing asynchronously.
     */
    private void parseValidationXmlAsync() {
        Executor executor = null;
        try {
            executor = createExecutor();
            future = executor.executorService.submit(new ValidationXMLReader());
        } catch (Throwable e) {
            // In the rare cases submitting a task throws OutOfMemoryError. In this case we call validation.xml
            // parsing lazily when requested
            AbstractSessionLog.getLog().log(SessionLog.WARNING, SessionLog.MOXY, "Error creating/submitting async validation.xml parsing task.", new Object[0], false);
            AbstractSessionLog.getLog().logThrowable(SessionLog.WARNING, SessionLog.MOXY, e);
            future = null;
        } finally {
            // Shutdown is needed only for JDK executor
            if (executor != null && executor.shutdownNeeded) {
                executor.executorService.shutdown();
            }
        }
    }

    /**
     * Runs validation.xml parsing synchronously.
     */
    private Map<Class<?>, Boolean> parseValidationXml() {
        final ValidationXMLReader reader = new ValidationXMLReader();
        Map<Class<?>, Boolean> result;
        try {
            result = reader.call();
        } catch (Exception e) {
            AbstractSessionLog.getLog().log(SessionLog.WARNING, SessionLog.MOXY, "Error parsing validation.xml synchronously", new Object[0], false);
            AbstractSessionLog.getLog().logThrowable(SessionLog.WARNING, SessionLog.MOXY, e);
            result = new HashMap<>();
        }
        return result;
    }

    /**
     * Creates an executor service. Tries to get a managed executor service. If failed creates a JDK one.
     * Sets shutdownNeeded property to true in case JDK executor is created.
     */
    private Executor createExecutor() {
        try {
            InitialContext jndiCtx = new InitialContext();
            // type:      javax.enterprise.concurrent.ManagedExecutorService
            // jndi-name: concurrent/ThreadPool
            return new Executor((ExecutorService) jndiCtx.lookup("java:comp/env/concurrent/ThreadPool"), false);
        } catch (NamingException ignored) {
            // aka continue to proceed with retrieving jdk executor
        }
        return new Executor(Executors.newFixedThreadPool(1), true);
    }

    /**
     * This class holds a managed or JDK executor instance with a flag indicating do we need to shut it down or not.
     */
    private static class Executor {
        ExecutorService executorService;
        boolean shutdownNeeded;

        Executor(ExecutorService executorService, boolean shutdownNeeded) {
            this.executorService = executorService;
            this.shutdownNeeded = shutdownNeeded;
        }
    }
}
