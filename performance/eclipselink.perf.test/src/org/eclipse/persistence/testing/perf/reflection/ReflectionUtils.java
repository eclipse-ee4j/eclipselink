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
package org.eclipse.persistence.testing.perf.reflection;

import java.lang.reflect.Field;
import java.security.PrivilegedAction;

import static java.security.AccessController.doPrivileged;
import static org.eclipse.persistence.internal.security.PrivilegedAccessHelper.shouldUsePrivilegedAccess;

/**
 * Utility class for handling reflection calls and using caller sensitive actions.
 *
 *  - Singleton lazy-loaded actions honoring Initialization On Demand Holder idiom.
 *  - Lazy-loaded inner classes and inner interfaces. Only loaded if security is enabled.
 */
final class ReflectionUtils {

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
        return shouldUsePrivilegedAccess()
                ? doPrivileged(DeclaredFieldsIODH.PREDICATE_ACTION.with(clazz))
                : getDeclaredFieldsInternal(clazz);
    }

    static Field[] getDeclaredFields2(Class<?> clazz) {
        return getDeclaredFieldsInternal(clazz);
    }

    /**
     * INTERNAL:
     */
    private static Field[] getDeclaredFieldsInternal(Class<?> clazz) {
        return clazz.getDeclaredFields();
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
}
