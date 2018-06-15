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
package org.eclipse.persistence.tools.workbench.framework.context;

import java.util.prefs.Preferences;


/**
 * Wrap another context and redirect its preferences
 * to another node in the preferences tree.
 */
public class RedirectedPreferencesContext
    extends PreferencesContextWrapper
{
    private String path;


    // ********** constructor/initialization **********

    /**
     * Construct a context that redirects the preferences node
     * to the node at the specified path, relative to the original preferences node.
     */
    public RedirectedPreferencesContext(PreferencesContext delegate, String path) {
        super(delegate);
        this.path = path;
    }


    // ********** non-delegated behavior **********

    /**
     * @see PreferencesContextWrapper#getPreferences()
     */
    public Preferences getPreferences() {
        return this.delegatePreferences().node(this.path);
    }


    // ********** additional behavior **********

    /**
     * Return the path to the "redirected" preferences node,
     * relative to the original preferences node.
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Return the original preferences node.
     */
    public Preferences delegatePreferences() {
        return this.getDelegate().getPreferences();
    }

}
