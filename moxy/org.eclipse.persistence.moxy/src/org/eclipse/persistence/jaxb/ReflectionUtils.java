/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Marcel Valovy - 2.6 - initial implementation
package org.eclipse.persistence.jaxb;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import static java.security.AccessController.doPrivileged;
import static org.eclipse.persistence.internal.security.PrivilegedAccessHelper.shouldUsePrivilegedAccess;

/**
 * Utility class for handling reflection calls and using caller sensitive actions.
 *
 *  - Singleton lazy-loaded actions honoring Initialization On Demand Holder idiom.
 *  - Lazy-loaded inner classes and inner interfaces. Only loaded if security is enabled.
 *
 * @author Marcel Valovy - marcel.valovy@oracle.com
 * @since 2.6
 */
final class ReflectionUtils {

    /**
     * Non-instantiable utility class. Reflection instantiation permitted.
     */
    private ReflectionUtils() {
    }

    /**
     * Retrieves class object.
     * <p>
     * If security is enabled, makes {@linkplain java.security.AccessController#doPrivileged(PrivilegedAction)
     * privileged calls}.
     *
     * @param clazz name of class to be retrieved
     * @return class object
     * @see Class#forName(String)
     */
    static Class<?> forName(String clazz) throws ClassNotFoundException {
        try {
            return shouldUsePrivilegedAccess()
                    ? doPrivileged(ForNameIODH.PREDICATE_EXCEPTION_ACTION.with(clazz))
                    : forNameInternal(clazz);
        } catch (PrivilegedActionException e) {
            throw (ClassNotFoundException) e.getException();
        }
    }

    /**
     * Retrieves declared fields.
     * <p>
     * If security is enabled, makes {@linkplain java.security.AccessController#doPrivileged(PrivilegedAction)
     * privileged calls}.
     *
     * @param clazz fields of that class will be returned
     * @return array of declared fields
     * @see Class#getDeclaredFields()
     */
    static Field[] getDeclaredFields(Class<?> clazz) {
        return shouldUsePrivilegedAccess()
                ? doPrivileged(DeclaredFieldsIODH.PREDICATE_ACTION.with(clazz))
                : getDeclaredFieldsInternal(clazz);
    }

    /**
     * Retrieves declared constructors.
     *
     * If security is enabled, makes {@linkplain java.security.AccessController#doPrivileged(PrivilegedAction)
     * privileged calls}.
     *
     * @param clazz class that will be scanned
     * @return declared constructors
     * @see Class#getDeclaredConstructors()
     */
    static Constructor<?>[] getDeclaredConstructors(Class<?> clazz) {
        return shouldUsePrivilegedAccess()
                ? doPrivileged(DeclaredConstructorsIODH.PREDICATE_ACTION.with(clazz))
                : getDeclaredConstructorsInternal(clazz);
    }

    /**
     * Retrieves declared methods.
     *
     * If security is enabled, makes {@linkplain java.security.AccessController#doPrivileged(PrivilegedAction)
     * privileged calls}.
     *
     * @param clazz class that will be scanned
     * @return declared methods
     * @see Class#getDeclaredMethods()
     */
    static Method[] getDeclaredMethods(Class<?> clazz) {
        return shouldUsePrivilegedAccess()
                ? doPrivileged(DeclaredMethodsIODH.PREDICATE_ACTION.with(clazz))
                : getDeclaredMethodsInternal(clazz);
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
            return shouldUsePrivilegedAccess()
                    ? doPrivileged(DeclaredMethodIODH.PREDICATE_EXCEPTION_ACTION.with(clazz).with(name).with(parameterTypes))
                    : getDeclaredMethodInternal(clazz, name, parameterTypes);
        } catch (PrivilegedActionException e) {
            throw (NoSuchMethodException) e.getException();
        }
    }


    /* Internal Methods */
    /**
     * INTERNAL:
     */
    private static Class<?> forNameInternal(String clazz) throws ClassNotFoundException {
        return Class.forName(clazz);
    }

    /**
     * INTERNAL:
     */
    private static Field[] getDeclaredFieldsInternal(Class<?> clazz) {
        return clazz.getDeclaredFields();
    }

    /**
     * INTERNAL:
     */
    private static Method[] getDeclaredMethodsInternal(Class<?> clazz) {
        return clazz.getDeclaredMethods();
    }

    /**
     * INTERNAL:
     */
    private static Constructor<?>[] getDeclaredConstructorsInternal(Class<?> clazz) {
        return clazz.getDeclaredConstructors();
    }

    /**
     * INTERNAL:
     */
    private static Method getDeclaredMethodInternal(Class<?> clazz, String name, Class<?>... parameterTypes) throws
            NoSuchMethodException {
        return clazz.getDeclaredMethod(name, parameterTypes);
    }


    /* Initialization on Demand Holders */
    /**
     * IODH for enhanced forName privileged action with exception using predicates.
     */
    private static final class ForNameIODH {

        /**
         * Enhanced {@link PrivilegedExceptionAction} using predicates.
         *  - Singleton.
         *  - Throws {@link java.lang.ClassNotFoundException}.
         */
        private static final PredicateWithException<Class<?>> PREDICATE_EXCEPTION_ACTION = new
                PredicateWithException<Class<?>>() {

                    /* Predicates */
                    private String clazz;

                    @Override
                    public PredicateWithException<Class<?>> with(String with) {
                        this.clazz = with;
                        return this;
                    }

                    @Override
                    public PredicateWithException<Class<?>> with(Class<?> with) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public PredicateWithException<Class<?>> with(Class<?>[] with) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public Class<?> run() throws NoSuchMethodException, ClassNotFoundException {
                        return forNameInternal(clazz);
                    }
                };
    }

    /**
     * IODH for enhanced getDeclaredFields privileged action using predicates.
     */
    private static final class DeclaredFieldsIODH {

        /**
         * Enhanced {@link java.security.PrivilegedAction} using predicates.
         *  - Singleton.
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
     * IODH for getDeclaredMethods privileged action using predicates.
     */
    private static final class DeclaredMethodsIODH {

        /**
         * Enhanced {@link PrivilegedAction} using predicates.
         *  - Singleton.
         */
        private static final Predicate<Method[]> PREDICATE_ACTION = new
                Predicate<Method[]>() {

                    /* Predicates */
                    private Class<?> clazz;

                    @Override
                    public Predicate<Method[]> with(Class<?> with) {
                        this.clazz = with;
                        return this;
                    }

                    @Override
                    public Method[] run() {
                        return getDeclaredMethodsInternal(clazz);
                    }
                };
    }
    /**
     * IODH for getDeclaredConstructors privileged action using predicates.
     */
    private static final class DeclaredConstructorsIODH {

        /**
         * Enhanced {@link java.security.PrivilegedAction} using predicates.
         *  - Singleton.
         */
        private static final Predicate<Constructor<?>[]> PREDICATE_ACTION = new
                Predicate<Constructor<?>[]>() {

                    /* Predicates */
                    private Class<?> clazz;

                    @Override
                    public Predicate<Constructor<?>[]> with(Class<?> with) {
                        this.clazz = with;
                        return this;
                    }

                    @Override
                    public Constructor<?>[] run() {
                        return getDeclaredConstructorsInternal(clazz);
                    }
                };
    }


    /**
     * IODH for getMethod predicate wrapped privileged exception action.
     */
    private static final class DeclaredMethodIODH {

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
                        return getDeclaredMethodInternal(clazz, name, parameterTypes);
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
