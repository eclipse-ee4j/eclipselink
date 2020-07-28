/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.security;

import java.lang.reflect.Method;
import java.security.PrivilegedExceptionAction;


public class PrivilegedGetMethod implements PrivilegedExceptionAction<Method> {

    private final Class clazz;
    private final String methodName;
    private final Class[] methodParameterTypes;
    private final boolean shouldSetAccessible;
    private final boolean publicOnly;

    public PrivilegedGetMethod(Class clazz, String methodName, Class[] methodParameterTypes, boolean shouldSetAccessible) {
        this(clazz, methodName, methodParameterTypes, shouldSetAccessible, false);
    }

    public PrivilegedGetMethod(Class clazz, String methodName, Class[] methodParameterTypes, boolean shouldSetAccessible, boolean publicOnly) {
        this.clazz = clazz;
        this.methodName = methodName;
        this.methodParameterTypes = methodParameterTypes;
        this.shouldSetAccessible = shouldSetAccessible;
        this.publicOnly = publicOnly;
    }

    @Override
    public Method run() throws NoSuchMethodException {
        if (publicOnly) {
            return PrivilegedAccessHelper.getPublicMethod(clazz, methodName, methodParameterTypes, shouldSetAccessible);
        }
        return PrivilegedAccessHelper.getMethod(clazz, methodName, methodParameterTypes, shouldSetAccessible);
    }

}

