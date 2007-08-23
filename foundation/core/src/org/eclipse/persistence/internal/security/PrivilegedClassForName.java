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

public class PrivilegedClassForName  implements PrivilegedExceptionAction {

    private String className;
    private boolean initialize;
    private ClassLoader loader;
    
    public PrivilegedClassForName(String className, boolean initialize, ClassLoader loader){
        this.className = className;
        this.initialize = initialize;
        this.loader = loader;
    }

    public PrivilegedClassForName(String className){
        this.className = className;
    }
    
    public Object run() throws ClassNotFoundException {
        if (loader == null){
            return PrivilegedAccessHelper.getClassForName(className);
        } else {
            return PrivilegedAccessHelper.getClassForName(className, initialize, loader);
        }
    }

    
}