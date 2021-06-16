/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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

