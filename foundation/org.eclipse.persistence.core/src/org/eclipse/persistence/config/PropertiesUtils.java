/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2015 IBM Corporation. All rights reserved.
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
//     02/19/2015 - Rick Curtis
//       - 458877 : Add national character support
//     03/06/2015-2.7.0 Dalia Abo Sheasha
//       - 461607: PropertiesUtils does not process methods with String parameters correctly.
//     05/06/2015-2.7.0 Rick Curtis
//       - 466626: Fix bug in getMethods() when Java 2 security is enabled.
package org.eclipse.persistence.config;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetMethods;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;

/**
 * A static utility class that handles parsing a String
 * "key=value,key1=value1...." and calls an appropriate set[key]([value]) method
 * on the provided instance.
 */
public class PropertiesUtils {
    /**
     * Attempts to parse and then set the provided kvs String into the
     * appropriate set method on the provided instance.
     *
     * Note: Keys and values cannot contain '=' or ','
     *
     * @param instance
     *            An JavaBean instance
     * @param propertyName
     *            The configuration name that is being set into instance. This
     *            is only used for informational purposes.
     * @param kvs
     *            A String of key=value comma delimited ie: k1=v1,k2=v2,...
     * @throws org.eclipse.persistence.exceptions.ConversionException
     *             if unable to process the provided kvs
     */
    public static void set(Object instance, String propertyName, String kvs) {
        if (instance == null || kvs == null || kvs.trim().isEmpty()) {
            return;
        }
        String[] keyValues = kvs.split(",");
        for (String kv : keyValues) {
            processKeyValue(instance, propertyName, kv);
        }
    }

    private static void processKeyValue(Object instance, String propertyName, String kv) {
        String[] t = kv.split("=");

        if (t.length != 2) {
            // We must have a single key and a single value.
            throw ConversionException.couldNotTranslatePropertiesIntoObject(instance, propertyName, kv, null);
        }
        String methodName = "set" + t[0].trim();
        // Stringified value to be set. Need to figure out it's type
        String value = t[1].trim();
        List<Method> methods = getMethodsMatchingName(instance, methodName);
        MethodMatch match = null;
        try {
            match = getMethodMatchingParameter(methods, value);
        } catch (Exception e) {
            throw ConversionException.couldNotTranslatePropertiesIntoObject(instance, propertyName, kv, e);
        }
        if (match != null) {
            Method method = match.getMethod();
            Object parsedValue = match.getParsedValue();
            try {
                invokeMethod(method, instance, parsedValue);
            } catch (Exception e) {
                throw ConversionException.couldNotTranslatePropertiesIntoObject(instance, propertyName, kv, e);
            }
        } else {
            throw ConversionException.couldNotTranslatePropertiesIntoObject(instance, propertyName, kv, null);
        }
    }

    private static MethodMatch getMethodMatchingParameter(List<Method> methods, String parameter) throws Exception {
        Object parsedParameterValue = null;
        Method res = null;
        for (Method method : methods) {
            Class<?>[] params = method.getParameterTypes();
            if (params.length == 1) {
                Class<?> param = params[0];
                if (param == String.class) {
                    parsedParameterValue = parameter;
                } else if (param == Integer.class || param == int.class) {
                    parsedParameterValue = Integer.parseInt(parameter);
                } else if (param == Boolean.class || param == boolean.class) {
                    // Don't use Boolean.parseBoolean(..) as it is to liberal
                    // with it's interpretation of false values
                    String lower = parameter.toLowerCase();
                    if ("true".equals(lower)) {
                        parsedParameterValue = Boolean.TRUE;
                    } else if ("false".equals(lower)) {
                        parsedParameterValue = Boolean.FALSE;
                    }
                } else if (param == Long.class || param == long.class) {
                    parsedParameterValue = Long.parseLong(parameter);
                } else if (param == Float.class || param == float.class) {
                    parsedParameterValue = Float.parseFloat(parameter);
                } else if (param == Double.class || param == double.class) {
                    parsedParameterValue = Double.parseDouble(parameter);
                }
            }
            if (parsedParameterValue != null) {
                return new MethodMatch(method, parsedParameterValue);
            }
        }// end for
        return null;
    }

    private static List<Method> getMethodsMatchingName(Object instance, String methodName) {
        List<Method> methods = new ArrayList<>();
        for (Method method : getMethods(instance.getClass())) {
            if (method.getName().equalsIgnoreCase(methodName)) {
                methods.add(method);
            }
        }
        return methods;
    }

    private static Method[] getMethods(Class<?> cls) {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            return AccessController.doPrivileged(new PrivilegedGetMethods(cls));
        }
        return PrivilegedAccessHelper.getMethods(cls);
    }

    private static Object invokeMethod(Method method, Object instance, Object... params) throws Exception {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            return AccessController.doPrivileged(new PrivilegedMethodInvoker(method, instance, params));
        }
        return method.invoke(instance, params);
    }

    private static class MethodMatch {
        final Method method;
        final Object parsedValue;

        public MethodMatch(Method m, Object p) {
            method = m;
            parsedValue = p;
        }

        public Method getMethod() {
            return method;
        }

        public Object getParsedValue() {
            return parsedValue;
        }
    }
}
