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
 *     Marcel Valovy - 2.6 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import static java.security.AccessController.doPrivileged;

/**
 * Utility class for handling reflection calls and using caller sensitive actions.
 *
 *  - Singleton lazy-loaded actions honoring Initialization On Demand Holder idiom.
 *  - Lazy-loaded inner classes and inner interfaces. Only loaded if security is enabled.
 *
 * todo: Thread-safety? So far used only in single-threaded environments.
 */
final class ReflectionUtils {

    /**
     * States whether security is enabled.
     * todo: Review underlying {@linkplain PrivilegedAccessHelper#shouldUsePrivilegedAccess() method}. Main concern: It
     * checks for security manager and for system property only once. /endoftodo
     */
    private static final boolean SECURITY_MANAGED = PrivilegedAccessHelper.shouldUsePrivilegedAccess();

    /**
     * Non-instantiable utility class. Reflection instantiation permitted.
     */
    private ReflectionUtils() {
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
     * Retrieves declared method.
     *
     * If security is enabled, makes {@linkplain java.security.AccessController#doPrivileged(PrivilegedAction)
     * privileged calls}.
     *
     * @param clazz class that will be scanned
     * @param name name of the method to be retrieved
     * @param parameterTypes parameter types of the method to be retrieved
     * @return declared method
     * @throws NoSuchMethodException if method was not found
     * @see Class#getDeclaredMethod(String, Class...)
     */
    static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) throws
            NoSuchMethodException {
        try {
            return SECURITY_MANAGED
                    ? doPrivileged(MethodIODH.PREDICATE_EXCEPTION_ACTION.with(clazz).with(name).with(parameterTypes))
                    : getMethodInternal(clazz, name, parameterTypes);
        } catch (PrivilegedActionException e) {
            throw (NoSuchMethodException) e.getException();
        }
    }


    /* Internal Methods */

    /**
     * INTERNAL:
     */
    private static Field[] getDeclaredFieldsInternal(Class<?> clazz) {
        return clazz.getDeclaredFields();
    }

    /**
     * INTERNAL:
     */
    private static Method getMethodInternal(Class<?> clazz, String name, Class<?>... parameterTypes) throws
            NoSuchMethodException {
        return clazz.getDeclaredMethod(name, parameterTypes);
    }


    /* Initialization on Demand Holders */

    /**
     * IODH for enhanced getDeclaredFields privileged action using predicates.
     */
    private static final class DeclaredFieldsIODH {

        /**
         * Enhanced {@link java.security.PrivilegedAction} using predicates.
         *  - Singleton.
         *  - Throws {@link NoSuchMethodException}.
         */
        private static final Predicate<Field[]> PREDICATE_ACTION = new Predicate<Field[]>() {

            /* Predicates */
            private Class<?> clazz;

            @Override
            public Field[] run() {
                return getDeclaredFieldsInternal(clazz);
            }

            @Override
            public Predicate<Field[]> with(Class<?> clazz) {
                this.clazz = clazz;
                return this;
            }
        };
    }

    /**
     * IODH for getMethod predicate wrapped privileged exception action.
     */
    private static final class MethodIODH {

        /**
         * Enhanced {@link PrivilegedExceptionAction} using predicates.
         *  - Singleton.
         *  - Throws {@link NoSuchMethodException}.
         */
        private static final PredicateWithException<Method> PREDICATE_EXCEPTION_ACTION = new
                PredicateWithException<Method>() {

            /* Predicates */
            private Class<?> clazz;
            private String name;
            private Class<?>[] parameterTypes;

            @Override
            public PredicateWithException<Method> with(Class<?> with) {
                this.clazz = with;
                return this;
            }

            @Override
            public PredicateWithException<Method> with(String with) {
                this.name = with;
                return this;
            }

            @Override
            public PredicateWithException<Method> with(Class<?>[] with) {
                this.parameterTypes = with;
                return this;
            }

            @Override
            public Method run() throws NoSuchMethodException {
                return getMethodInternal(clazz, name, parameterTypes);
            }
        };
    }


    /* Inner Interfaces */

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

    /**
     * Predicate-providing wrapper for {@link PrivilegedExceptionAction}.
     *
     * @param <T> return type of {@linkplain PrivilegedExceptionAction#run() computation}
     */
    private interface PredicateWithException<T> extends PrivilegedExceptionAction<T> {

        /**
         * Assigns a predicate to the underlying privileged exception action.
         * Any previous predicate of the same type will be overwritten.
         *
         * @param with predicate
         * @return {@code this}
         */
        PredicateWithException<T> with(Class<?> with);

        /**
         * Assigns a predicate to the underlying privileged exception action.
         * Any previous predicate of the same type will be overwritten.
         *
         * @param with predicate
         * @return {@code this}
         */
        PredicateWithException<T> with(String with);

        /**
         * Assigns a predicate to the underlying privileged exception action.
         * Any previous predicate of the same type will be overwritten.
         *
         * @param with predicate
         * @return {@code this}
         */
        PredicateWithException<T> with(Class<?>[] with);
    }
}
