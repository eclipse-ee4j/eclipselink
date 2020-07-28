/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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
     * @param key The name of the {@link System} property.
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
