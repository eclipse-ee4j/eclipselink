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
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * INTERNAL:
 *
 * Asynchronously starts validation.xml file processing when created. Holds a map of classes with BV annotations.
 *
 * @author Marcel Valovy, Dmitry Kornilov
 * @since 2.6
 */
final public class BeanValidationHelper {

    private static final Logger LOGGER = Logger.getLogger(BeanValidationHelper.class.getName());

    private Future<Map<Class<?>, Boolean>> future;

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
                    LOGGER.log(Level.WARNING, "Error parsing validation.xml the async way", e);
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
            LOGGER.log(Level.WARNING, "Error creating/submitting async validation.xml parsing task.", e);
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
            LOGGER.log(Level.WARNING, "Error parsing validation.xml synchronously", e);
            result = new HashMap<>();
        }
        return result;
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