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

public class PrivilegedGetFields implements PrivilegedExceptionAction {

    private Class javaClass;   
    
    public PrivilegedGetFields(Class javaClass) {
        this.javaClass = javaClass;
    }

    public Object run(){
        return PrivilegedAccessHelper.getFields(javaClass);
    }
}



