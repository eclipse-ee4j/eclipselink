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

public class PrivilegedGetContextClassLoader implements PrivilegedExceptionAction<ClassLoader> {

    private final Thread thread;

    public PrivilegedGetContextClassLoader(Thread thread) {
        this.thread = thread;
    }

    @Override
    public ClassLoader run() {
        return PrivilegedAccessHelper.getContextClassLoader(thread);
    }

}
