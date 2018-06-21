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

import java.lang.reflect.Constructor;
import java.security.PrivilegedExceptionAction;

public class PrivilegedGetDeclaredConstructorFor implements PrivilegedExceptionAction<Constructor> {

    private final Class javaClass;
    private final Class[] args;
    private final boolean shouldSetAccessible;

    public PrivilegedGetDeclaredConstructorFor(Class javaClass, Class[] args, boolean shouldSetAccessible) {
        this.javaClass = javaClass;
        this.args = args;
        this.shouldSetAccessible = shouldSetAccessible;
    }

    @Override
    public Constructor run() throws NoSuchMethodException {
        return PrivilegedAccessHelper.getDeclaredConstructorFor(javaClass, args, shouldSetAccessible);
    }

}

