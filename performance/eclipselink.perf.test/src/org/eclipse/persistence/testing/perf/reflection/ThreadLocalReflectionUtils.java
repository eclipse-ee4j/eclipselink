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
enum ThreadLocalReflectionUtils {
    UTILS;

    private int random = 0;

    Field[] getDeclaredFieldsTL(Class<?> clazz) {
        return PrivilegedAccessHelper.shouldUsePrivilegedAccess()
                ? AccessController.doPrivileged(threadLocal.get())
                : getDeclaredFieldsInternal(clazz);
    }

    ThreadLocal<PrivilegedAction<Field[]>> threadLocal = new ThreadLocal<PrivilegedAction<Field[]>>(){
        @Override
        protected PrivilegedAction<Field[]> initialValue() {
            return new PrivilegedAction<Field[]>() {
                @Override
                public Field[] run() {
                    return random++ % 2 == 1 ? String.class.getDeclaredFields() : Boolean.class.getDeclaredFields();
                }
            };
        }
    };


    /**
     * INTERNAL:
     */
    private static Field[] getDeclaredFieldsInternal(Class<?> clazz) {
        return clazz.getDeclaredFields();
    }

}
