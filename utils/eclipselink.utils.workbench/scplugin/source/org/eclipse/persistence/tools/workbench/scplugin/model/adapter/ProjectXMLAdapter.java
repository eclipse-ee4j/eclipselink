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
package org.eclipse.persistence.tools.workbench.scplugin.model.adapter;

import javax.swing.Icon;

import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectXMLConfig;

/**
 * Session Configuration model adapter class for the
 * TopLink Foudation Library class ProjectXMLConfig
 *
 * @see ProjectXMLConfig
 *
 * @author Tran Le
 */
public class ProjectXMLAdapter extends ProjectAdapter {

    /**
     * Creates a new ProjectXMLAdapter for the specified model object.
     */
    ProjectXMLAdapter( SCAdapter parent, ProjectXMLConfig scConfig) {
        super( parent, scConfig);
    }
    /**
     * Creates a new ProjectXMLAdapter.
     */
    protected ProjectXMLAdapter( SCAdapter parent, String name) {

        super( parent, name);
    }

    public Icon icon() {
        return null;
    }
    /**
     * Returns this Config Model Object.
     */
    private final ProjectXMLConfig projectXml() {

        return ( ProjectXMLConfig)this.getModel();
    }

    protected Object buildModel() {
            return new ProjectXMLConfig();
    }

    public boolean isXml() {
      return true;
    }
}
