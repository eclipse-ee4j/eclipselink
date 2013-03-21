/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.security;

import java.security.PrivilegedExceptionAction;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;


public class PrivilegedMethodInvoker implements PrivilegedExceptionAction {

    private Method method;
    private Object target;
    private Object[] args;
    
    public PrivilegedMethodInvoker(Method method, Object target) {
        this(method, target, (Object[]) null);
    }
    
    public PrivilegedMethodInvoker(Method method, Object target, Object[] args){
        this.method = method;
        this.target = target;
        this.args = args;
    }
    
    public Object run() throws IllegalAccessException, InvocationTargetException {
        return PrivilegedAccessHelper.invokeMethod(method, target, args);
    }
    
}
