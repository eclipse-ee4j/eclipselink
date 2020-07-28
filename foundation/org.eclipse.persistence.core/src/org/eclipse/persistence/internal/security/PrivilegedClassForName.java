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

import java.security.PrivilegedExceptionAction;

public class PrivilegedClassForName  implements PrivilegedExceptionAction<Class> {

    private final String className;
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

    @Override
    public Class run() throws ClassNotFoundException {
        if (loader == null){
            return PrivilegedAccessHelper.getClassForName(className);
        } else {
            return PrivilegedAccessHelper.getClassForName(className, initialize, loader);
        }
    }


}
