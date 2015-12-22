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
 *     Oracle - initial API and implementation.
 ******************************************************************************/  
package org.eclipse.persistence.internal.security;

import java.security.PrivilegedAction;

/**
 * INTERNAL:
 * Retrieve {@link System} property with privileges enabled.
 */
public class PrivilegedGetSystemProperty implements PrivilegedAction<String> {
    /** The name of the {@link System} property. */
    private final String key;
    /** A default value of the {@link System} property. */
    private final String def;

    /**
     * INTERNAL:
     * Creates an instance of {@link System} property getter with privileges enabled.
     * Selects {@link System} property getter without default value to be executed so getter will return {@code null}
     * if property with {@code key} does not exist.
     * @param key The name of the {@link System} property.
     */
    public PrivilegedGetSystemProperty(final String key) {
        this.key = key;
        this.def = null;
    }

    /**
     * INTERNAL:
     * Creates an instance of {@link System} property getter with privileges enabled.
     * Selects {@link System} property getter with default value to be executed so getter will return {@code def}
     * if property with {@code key} does not exist.
     * @param key The name of the {@link System} property.
     */
    public PrivilegedGetSystemProperty(final String key, final String def) {
        this.key = key;
        this.def = def;
    }

    /**
     * INTERNAL:
     * Performs {@link System} property retrieval.
     * This method will be called by {@link AccessController#doPrivileged(PrivilegedAction)} after enabling privileges.
     * @return The {@link String} value of the system property.
     */
    @Override
    public String run() {
        return def != null ? System.getProperty(key, def) : System.getProperty(key);
    }
}
