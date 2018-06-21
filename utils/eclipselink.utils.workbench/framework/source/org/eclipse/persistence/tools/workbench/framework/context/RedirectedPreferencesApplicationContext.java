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
package org.eclipse.persistence.tools.workbench.framework.context;

import java.util.prefs.Preferences;

/**
 * Wrap another context and redirect its preferences
 * to another node in the preferences tree.
 */
public class RedirectedPreferencesApplicationContext extends ApplicationContextWrapper {

    private String path;


    // ********** constructor/initialization **********

    /**
     * Construct a context that redirects the preferences node
     * to the node at the specified path, relative to the original preferences node.
     */
    public RedirectedPreferencesApplicationContext(ApplicationContext delegate, String path) {
        super(delegate);
        this.path = path;
    }


    // ********** non-delegated behavior **********

    /**
     * @see ApplicationContextWrapper#getPreferences()
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
