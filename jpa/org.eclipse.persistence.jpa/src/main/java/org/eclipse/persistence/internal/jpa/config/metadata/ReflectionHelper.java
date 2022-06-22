/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.internal.jpa.config.metadata;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import sun.misc.Unsafe;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

final class ReflectionHelper {

    private static Unsafe UNSAFE;
    private static long OFFSET = -1;

    static {
        UNSAFE = getUnsafe();
        if (UNSAFE != null) {
            Field f = PrivilegedAccessHelper.callDoPrivileged(() -> {
                try {
                    return AccessibleObject.class.getDeclaredField("override");
                } catch (NoSuchFieldException | SecurityException ex) {
                    return null;
                }
            });
            if (f == null) {
                try {
                    f = dummy.class.getDeclaredField("override");
                } catch (NoSuchFieldException ignore) {
                }
            }
            OFFSET = UNSAFE.objectFieldOffset(f);
        }
    }


    private ReflectionHelper() {
    }

    static Method getMethod(final Class<?> c, final String methodname, final Class<?>... params) {
        try {
            Method m = c.getDeclaredMethod(methodname, params);
            setAccessible(m);
            return m;
        } catch (NoSuchMethodException e) {
            // impossible
            throw new NoSuchMethodError(e.getMessage());
        }
    }

    private static <T extends AccessibleObject> T setAccessible(T accessor) {
        if (OFFSET != -1) {
            UNSAFE.putBoolean(accessor, OFFSET, true);
        } else {
            accessor.setAccessible(true); // this may fail
        }
        return accessor;
    }

    private static Unsafe getUnsafe() {
        try {
            Field f = PrivilegedAccessHelper.callDoPrivileged(() -> {
                try {
                    return Unsafe.class.getDeclaredField("theUnsafe");
                } catch (NoSuchFieldException | SecurityException ex) {
                    // ignore
                    return null;
                }
            });
            if (f != null) {
                setAccessible(f);
                return (Unsafe) f.get(null);
            }
        } catch (Throwable t) {
            // ignore
        }
        return null;
    }

    static class dummy {

        boolean override;
        Object other;
    }
}
