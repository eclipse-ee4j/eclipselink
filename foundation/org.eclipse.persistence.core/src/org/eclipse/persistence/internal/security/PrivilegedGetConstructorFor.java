/*******************************************************************************
 * Copyright (c) 1998, 2015 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.reflect.Constructor;
import java.security.PrivilegedExceptionAction;

public class PrivilegedGetConstructorFor implements PrivilegedExceptionAction<Constructor> {

    private final Class javaClass;
    private final Class[] args;
    private final boolean shouldSetAccessible;
    
    public PrivilegedGetConstructorFor(Class javaClass, Class[] args, boolean shouldSetAccessible) {
        this.javaClass = javaClass;
        this.args = args;
        this.shouldSetAccessible = shouldSetAccessible;
    }

    @Override
    public Constructor run() throws NoSuchMethodException {
        return PrivilegedAccessHelper.getConstructorFor(javaClass, args, shouldSetAccessible);
    }

}


