/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     06/22/2015-2.6.1 Tomas Kraus
 *       - 254437: initial API and implementation.
 ******************************************************************************/
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
