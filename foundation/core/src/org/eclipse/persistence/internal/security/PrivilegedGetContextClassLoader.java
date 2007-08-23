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

public class PrivilegedGetContextClassLoader implements PrivilegedExceptionAction {

    private Thread thread;
    
    public PrivilegedGetContextClassLoader(Thread thread) {
        this.thread = thread;
    }

    public Object run() {
        return PrivilegedAccessHelper.getContextClassLoader(thread);
    }

}
