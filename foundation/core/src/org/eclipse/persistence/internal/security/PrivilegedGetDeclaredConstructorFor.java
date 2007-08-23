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

import java.security.PrivilegedExceptionAction;

public class PrivilegedGetDeclaredConstructorFor implements PrivilegedExceptionAction {

    private Class javaClass;
    private Class[] args;
    private boolean shouldSetAccessible;
    
    public PrivilegedGetDeclaredConstructorFor(Class javaClass, Class[] args, boolean shouldSetAccessible) {
        this.javaClass = javaClass;
        this.args = args;
        this.shouldSetAccessible = shouldSetAccessible;
    }

    public Object run() throws NoSuchMethodException {
        return PrivilegedAccessHelper.getDeclaredConstructorFor(javaClass, args, shouldSetAccessible);
    }

}

