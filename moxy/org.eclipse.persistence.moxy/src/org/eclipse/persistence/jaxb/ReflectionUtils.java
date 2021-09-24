/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Marcel Valovy - 2.6 - initial implementation
package org.eclipse.persistence.jaxb;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

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
     *
     * @param clazz name of class to be retrieved
     * @return class object
     * @see Class#forName(String)
     */
    static Class<?> forName(String clazz) throws ClassNotFoundException {
        try {
            return PrivilegedAccessHelper.callDoPrivilegedWithException(
                    () -> Class.forName(clazz)
            );
        } catch (ClassNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(String.format("Could not retrieve class %s.", clazz), ex);
        }
    }

    /**
     * Retrieves declared fields.
     *
     * @param clazz fields of that class will be returned
     * @return array of declared fields
     * @see Class#getDeclaredFields()
     */
    static Field[] getDeclaredFields(Class<?> clazz) {
        return PrivilegedAccessHelper.callDoPrivileged(
                () -> clazz.getDeclaredFields()
        );
    }

    /**
     * Retrieves declared constructors.
     *
     * @param clazz class that will be scanned
     * @return declared constructors
     * @see Class#getDeclaredConstructors()
     */
    static Constructor<?>[] getDeclaredConstructors(Class<?> clazz) {
        return PrivilegedAccessHelper.callDoPrivileged(
                () -> clazz.getDeclaredConstructors()
        );
    }

    /**
     * Retrieves declared methods.
     *
     * @param clazz class that will be scanned
     * @return declared methods
     * @see Class#getDeclaredMethods()
     */
    static Method[] getDeclaredMethods(Class<?> clazz) {
        return PrivilegedAccessHelper.callDoPrivileged(
                () -> clazz.getDeclaredMethods()
        );
    }

}
