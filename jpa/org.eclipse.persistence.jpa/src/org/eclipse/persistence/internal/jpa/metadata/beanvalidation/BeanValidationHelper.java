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
 *     Marcel Valovy - marcelv3612@gmail.com - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.beanvalidation;

import org.eclipse.persistence.internal.cache.AdvancedProcessor;
import org.eclipse.persistence.internal.cache.ComputableTask;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.validation.Constraint;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
 *
 * @author Marcel Valovy - marcel.valovy@oracle.com
 * @since 2.6
 */
public final class BeanValidationHelper {
    private static final Logger LOGGER = Logger.getLogger(BeanValidationHelper.class.getName());

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
    private static final int KNOWN_CONSTRAINTS_DEFAULT_SIZE = nextPowerOfTwo((int) (DEFAULT_CONSTRAINTS_QUANTITY / LOAD_FACTOR));

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
     * Set of all default BeanValidation field annotations and discovered custom field constraints.
     * Implemented as a ConcurrentHashMap with "Null Object" idiom.
     */
    private static final Set<String> KNOWN_CONSTRAINTS = Collections.newSetFromMap(new ConcurrentHashMap<>( 
            KNOWN_CONSTRAINTS_DEFAULT_SIZE, LOAD_FACTOR ));

    /**
     * Map of all classes that have undergone check for bean validation constraints.
     * Maps the key with boolean value telling whether the class contains an annotation from {@link #KNOWN_CONSTRAINTS}.
     */
    private static Map<Class<?>, Boolean> CONSTRAINTS_ON_CLASSES = null;

    static {
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
     * Tells whether any of the class's {@link java.lang.reflect.AccessibleObject}s are constrained by Bean
     * Validation annotations or custom constraints.
     *
     * @param clazz checked class
     * @return true or false
     */
    public boolean isConstrained(Class<?> clazz) {
        return memoizer.compute(cds, clazz);
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
     * Creates an executor service. Tries to get a managed executor service. If
     * failed creates a JDK one. Sets shutdownNeeded property to true in case
     * JDK executor is created.
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
                CONSTRAINTS_ON_CLASSES.put(clazz, constrained);
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
                if (KNOWN_CONSTRAINTS.contains(annType.getName())){
                    return true;
                }
                // detect custom annotations
                for (Annotation annOnAnnType : annType.getAnnotations()) {
                    final Class<? extends Annotation> annTypeOnAnnType = annOnAnnType.annotationType();
                    if (Constraint.class == annTypeOnAnnType) {
                        KNOWN_CONSTRAINTS.add(annType.getName());
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
                    if (KNOWN_CONSTRAINTS.contains(annType.getName())) {
                        return true;
                    }
                    // detect custom annotations
                    for (Annotation annOnAnnType : annType.getAnnotations()) {
                        final Class<? extends Annotation> annTypeOnAnnType = annOnAnnType.annotationType();
                        if (Constraint.class == annTypeOnAnnType) {
                            KNOWN_CONSTRAINTS.add(annType.getName());
                            return true;
                        }
                    }
                }
            return false;
        }
    }

    /**
     * Adds canonical names of bean validation constraints into set of known
     * constraints (internally represented by map). Canonical name is the name
     * that would be used in an import statement and uniquely identifies the
     * class, i.e. anonymous classes receive a 'null' value as their canonical
     * name, which allows no ambiguity and is what we are looking for.
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
