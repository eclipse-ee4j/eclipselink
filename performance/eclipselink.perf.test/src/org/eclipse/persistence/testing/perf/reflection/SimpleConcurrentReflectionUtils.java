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
//     Marcel Valovy - 2.6 - initial API and implementation
package org.eclipse.persistence.testing.perf.reflection;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * INTERNAL:
 *
 * Utility class for bean validation related tasks.
 *  - Singleton.
 *  - Thread-safe.
 */
public class SimpleConcurrentReflectionUtils {

    /**
     * Retrieves declared fields.
     * <p/>
     * If security is enabled, makes {@linkplain java.security.AccessController#doPrivileged(java.security.PrivilegedAction)
     * privileged calls}.
     *
     * @param clazz fields of that class will be returned
     * @return array of declared fields
     * @see Class#getDeclaredFields()
     */
     static Field[] getDeclaredFields(Class<?> clazz) {
         return PrivilegedAccessHelper.shouldUsePrivilegedAccess()
                ? AccessController.doPrivileged(PREDICATE_ACTION.with(clazz))
                : getDeclaredFieldsInternal(clazz);
    }

    /**
     * Enhanced {@link PrivilegedAction} using predicates.
     * - Singleton.
     */
    private static final Predicate<Field[]> PREDICATE_ACTION = new
            Predicate<Field[]>() {

                /* Predicates */
                private Class<?> clazz;

                /**
                 * Lock that allows for concurrent usage of this instance.
                 */
                private final ReentrantLock lock = new ReentrantLock();

                @Override
                public Field[] run() {
                    // We are still inside memory barrier, thus it is guaranteed that value of #clazz was assigned by
                    // current thread.
                    Class<?> localReference = clazz;
                    // Leaving memory barrier before executing cpu extensive operation.
                    lock.unlock();
                    // Possibly we should clear clazz reference here, since the instances are pooled. However,
                    // the field should always be reassigned before running run() method and even if someone
                    // intentionally did not, it poses no security threat.
                    // Computation on value that is guaranteed to have been assigned by current thread.
                    return getDeclaredFieldsInternal(localReference);
                }

                @Override
                public Predicate<Field[]> with(Class<?> clazz) {
                    lock.lock();
                    this.clazz = clazz;
                    return this;
                }
            };

    /**
     * INTERNAL:
     */
    private static Field[] getDeclaredFieldsInternal(Class<?> clazz) {
        return clazz.getDeclaredFields();
    }

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
