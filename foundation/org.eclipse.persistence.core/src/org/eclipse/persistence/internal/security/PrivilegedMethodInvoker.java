/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.security;

import java.security.PrivilegedExceptionAction;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;


public class PrivilegedMethodInvoker implements PrivilegedExceptionAction {

    private final Method method;
    private final Object target;
    private final Object[] args;

    public PrivilegedMethodInvoker(Method method, Object target) {
        this(method, target, (Object[]) null);
    }

    public PrivilegedMethodInvoker(Method method, Object target, Object[] args){
        this.method = method;
        this.target = target;
        this.args = args;
    }

    @Override
    public Object run() throws IllegalAccessException, InvocationTargetException {
        return PrivilegedAccessHelper.invokeMethod(method, target, args);
    }

}
