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

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.validation.Constraint;
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
 * Utility class for bean validation related tasks.
 *  - Singleton.
 *  - Thread-safe.
 */
public final class BeanValidationHelper {
    private static final Logger LOGGER = Logger.getLogger(BeanValidationHelper.class.getName());

    private Future<Map<Class<?>, Boolean>> future;

    /**
     * Set of all default BeanValidation field annotations and discovered custom field constraints.
     * Implemented as a ConcurrentHashMap with "Null Object" idiom.
     */
    private final Set<String> KNOWN_CONSTRAINTS = new HashSet<>();

    /**
     * Map of all classes that have undergone check for bean validation constraints.
     * Maps the key with boolean value telling whether the class contains an annotation from {@link #KNOWN_CONSTRAINTS}.
     */
    private Map<Class<?>, Boolean> CONSTRAINTS_ON_CLASSES = null;

    {
        initializeKnownConstraints();
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
            CONSTRAINTS_ON_CLASSES = new HashMap<>();
        }
    }

    /**
     * Tells whether any of the class's fields, methods or constructors are constrained by Bean Validation annotations
     * or custom constraints.
     *
     * @param clazz checked class
     * @return true or false
     */
    public boolean isConstrained(Class<?> clazz) {
        Boolean annotated = getConstraintsMap().get(clazz);
        if (annotated == null) {
            annotated = detectConstraints(clazz);
            getConstraintsMap().put(clazz, annotated);
        }
        return annotated;
    }

    /**
     * Lazy getter for constraintsOnClasses property. Waits until the map is
     * returned by async XML reader.
     */
    public Map<Class<?>, Boolean> getConstraintsMap() {
        if (CONSTRAINTS_ON_CLASSES == null) {
            if (future == null) {
                // This happens when submission of async task is failed. Run
                // validation.xml parsing synchronously.
                CONSTRAINTS_ON_CLASSES = parseValidationXml();
            } else {
                // Async task was successfully submitted. get a result from
                // future
                try {
                    CONSTRAINTS_ON_CLASSES = future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // For some reason the async parsing attempt failed. Call it
                    // synchronously.
                    LOGGER.log(Level.WARNING, "Error parsing validation.xml the async way", e);
                    CONSTRAINTS_ON_CLASSES = parseValidationXml();
                }
            }
        }
        return CONSTRAINTS_ON_CLASSES;
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
            // In the rare cases submitting a task throws OutOfMemoryError. In
            // this case we call validation.xml
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
                if (KNOWN_CONSTRAINTS.contains(type.getName())){
                    return true;
                }
                // Check for custom annotations on the field (+ check inheritance on class annotations).
                // Custom bean validation annotation is defined by having @Constraint annotation on its class.
                for (Annotation typesClassAnnotation : type.getAnnotations()) {
                    final Class<? extends Annotation> classAnnotationType = typesClassAnnotation.annotationType();
                    if (Constraint.class == classAnnotationType) {
                        KNOWN_CONSTRAINTS.add(type.getName());
                        return true;
                    }
                }
            }
        }
        for (Method m : ReflectionUtils.getDeclaredMethods(clazz)) {
            for (Annotation a : m.getDeclaredAnnotations()) {
                final Class<? extends Annotation> type = a.annotationType();
                if (KNOWN_CONSTRAINTS.contains(type.getName())){
                    return true;
                }
                // Check for custom annotations on the method (+ check inheritance on class annotations).
                // Custom bean validation annotation is defined by having @Constraint annotation on its class.
                for (Annotation typesClassAnnotation : type.getAnnotations()) {
                    final Class<? extends Annotation> classAnnotationType = typesClassAnnotation.annotationType();
                    if (Constraint.class == classAnnotationType) {
                        KNOWN_CONSTRAINTS.add(type.getName());
                        return true;
                    }
                }
            }
        }
        for (Constructor<?> c : ReflectionUtils.getDeclaredConstructors(clazz)) {
            for (Annotation a : c.getDeclaredAnnotations()) {
                final Class<? extends Annotation> type = a.annotationType();
                if (KNOWN_CONSTRAINTS.contains(type.getName())){
                    return true;
                }
                // Check for custom annotations on the constructor (+ check inheritance on class annotations).
                // Custom bean validation annotation is defined by having @Constraint annotation on its class.
                for (Annotation typesClassAnnotation : type.getAnnotations()) {
                    final Class<? extends Annotation> classAnnotationType = typesClassAnnotation.annotationType();
                    if (Constraint.class == classAnnotationType) {
                        KNOWN_CONSTRAINTS.add(type.getName());
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
            // type: javax.enterprise.concurrent.ManagedExecutorService
            // jndi-name: concurrent/ThreadPool
            return new Executor((ExecutorService) jndiCtx.lookup("java:comp/env/concurrent/ThreadPool"), false);
        } catch (NamingException ignored) {
            // aka continue to proceed with retrieving jdk executor
        }
        return new Executor(Executors.newFixedThreadPool(1), true);
    }

    /**
     * This class holds a managed or JDK executor instance with a flag
     * indicating do we need to shut it down or not.
     */
    private static class Executor {
        ExecutorService executorService;
        boolean shutdownNeeded;

        Executor(ExecutorService executorService, boolean shutdownNeeded) {
            this.executorService = executorService;
            this.shutdownNeeded = shutdownNeeded;
        }
    }

    /**
     * Adds canonical names of bean validation constraints into set of known
     * constraints (internally represented by map). Canonical name is the name
     * that would be used in an import statement and uniquely identifies the
     * class, i.e. anonymous classes receive a 'null' value as their canonical
     * name, which allows no ambiguity and is what we are looking for.
     */
    private void initializeKnownConstraints() {
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
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.Max.List");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.Min.List");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.DecimalMax.List");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.DecimalMin.List");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.Digits.List");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.NotNull.List");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.Pattern.List");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.Size.List");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.AssertTrue.List");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.AssertFalse.List");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.Future.List");
        KNOWN_CONSTRAINTS.add("javax.validation.constraints.Past.List");
    }
}
