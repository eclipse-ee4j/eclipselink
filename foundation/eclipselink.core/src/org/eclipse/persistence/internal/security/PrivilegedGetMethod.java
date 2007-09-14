/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.security;

import java.lang.reflect.Method;
import java.security.PrivilegedExceptionAction;


public class PrivilegedGetMethod implements PrivilegedExceptionAction<Method> {

    private Class clazz;
    private String methodName;
    private Class[] methodParameterTypes;
    private boolean shouldSetAccessible;
    
    public PrivilegedGetMethod(Class clazz, String methodName, Class[] methodParameterTypes, boolean shouldSetAccessible) {
        this.clazz = clazz;
        this.methodName = methodName;
        this.methodParameterTypes = methodParameterTypes;
    }

    public Method run() throws NoSuchMethodException {
        return PrivilegedAccessHelper.getMethod(clazz, methodName, methodParameterTypes, shouldSetAccessible);
    }

}

