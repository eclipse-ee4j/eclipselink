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
package org.eclipse.persistence.tools.workbench.scplugin.model.adapter;

import javax.swing.Icon;

import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectClassConfig;

/**
 * Session Configuration model adapter class for the
 * TopLink Foudation Library class ProjectClassConfig
 *
 * @see ProjectClassConfig
 *
 * @author Tran Le
 */
public class ProjectClassAdapter extends ProjectAdapter {
    /**
     * Creates a new ProjectClassAdapter for the specified model object.
     */
    ProjectClassAdapter( SCAdapter parent, ProjectClassConfig scConfig) {
        super( parent, scConfig);
    }
    /**
     * Creates a new ProjectClassAdapter.
     */
    protected ProjectClassAdapter( SCAdapter parent, String name) {

        super( parent, name);
    }

    public Icon icon() {
        return null;
    }
    /**
     * Returns this Config Model Object.
     */
    private final ProjectClassConfig projectClass() {

        return ( ProjectClassConfig)this.getModel();
    }

    protected Object buildModel() {
            return new ProjectClassConfig();
    }

    public boolean isClass() {
      return true;
    }
}
