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
package org.eclipse.persistence.tools.workbench.utility;

import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * An implementation of the JDK Preferences that does nothing,
 * in a reasonable fashion.
 */
public class NullPreferences extends AbstractPreferences {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    // singleton
    private static NullPreferences INSTANCE;

    /**
     * Return the singleton.
     */
    public static synchronized Preferences instance() {
        if (INSTANCE == null) {
            INSTANCE = new NullPreferences();
        }
        return INSTANCE;
    }

    /**
     * Ensure non-instantiability.
     */
    private NullPreferences() {
        super(null, "");
    }

    /**
     * @see java.util.prefs.AbstractPreferences#putSpi(String, String)
     */
    protected void putSpi(String key, String value) {
        // ignore
    }

    /**
     * @see java.util.prefs.AbstractPreferences#getSpi(String)
     */
    protected String getSpi(String key) {
        return null;
    }

    /**
     * @see java.util.prefs.AbstractPreferences#removeSpi(String)
     */
    protected void removeSpi(String key) {
        // ignore
    }

    /**
     * @see java.util.prefs.AbstractPreferences#removeNodeSpi()
     */
    protected void removeNodeSpi() throws BackingStoreException {
        // ignore
    }

    /**
     * @see java.util.prefs.AbstractPreferences#keysSpi()
     */
    protected String[] keysSpi() throws BackingStoreException {
        return EMPTY_STRING_ARRAY;
    }

    /**
     * @see java.util.prefs.AbstractPreferences#childrenNamesSpi()
     */
    protected String[] childrenNamesSpi() throws BackingStoreException {
        return EMPTY_STRING_ARRAY;
    }

    /**
     * @see java.util.prefs.AbstractPreferences#childSpi(String)
     */
    protected AbstractPreferences childSpi(String name) {
        return this;
    }

    /**
     * @see java.util.prefs.AbstractPreferences#syncSpi()
     */
    protected void syncSpi() throws BackingStoreException {
        // ignore
    }

    /**
     * @see java.util.prefs.AbstractPreferences#flushSpi()
     */
    protected void flushSpi() throws BackingStoreException {
        // ignore
    }

}
