/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     06/22/2015-2.6.1 Tomas Kraus
//       - 254437: initial API and implementation.
package org.eclipse.persistence.internal.security;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;

/**
 * INTERNAL:
 * Retrieve current {@link Thread} context {@link ClassLoader} with privileges enabled.
 * @since 2.6.1
 */
public class PrivilegedGetClassLoaderFromCurrentThread implements PrivilegedExceptionAction<ClassLoader> {

    /**
     * INTERNAL:
     * Creates an instance of current {@link Thread} context {@link ClassLoader} getter.
     */
    public PrivilegedGetClassLoaderFromCurrentThread() {
    }

    /**
     * INTERNAL:
     * Performs current {@link Thread} context {@link ClassLoader} retrieval.
     * This method will be called by {@link AccessController#doPrivileged(PrivilegedAction)} after enabling privileges.
     * @return The current {@link Thread} context {@link ClassLoader}.
     */
    @Override
    public ClassLoader run() {
        return Thread.currentThread().getContextClassLoader();
    }

}
