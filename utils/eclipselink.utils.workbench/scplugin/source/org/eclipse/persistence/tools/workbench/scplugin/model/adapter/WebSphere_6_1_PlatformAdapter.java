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

import org.eclipse.persistence.internal.sessions.factories.model.platform.WebSphere_6_1_PlatformConfig;

/**
 * Session Configuration model adapter class for the
 * TopLink Foudation Library class WebSphere_6_1_PlatformConfig
 *
 * @see WebSphere_6_1_PlatformConfig
 *
 * @author Les Davis
 */
public class WebSphere_6_1_PlatformAdapter extends ServerPlatformAdapter {

    /**
     * Default constructor
     */
    WebSphere_6_1_PlatformAdapter() {
        super();
    }
    /**
     * Creates a new Platform for the specified model object.
     */
    WebSphere_6_1_PlatformAdapter( SCAdapter parent, WebSphere_6_1_PlatformConfig scConfig) {

        super( parent, scConfig);
    }
    /**
     * Creates a new Platform.
     */
    protected WebSphere_6_1_PlatformAdapter( SCAdapter parent) {

        super( parent);
    }
    /**
     * Returns this Config Model Object.
     */
    private final WebSphere_6_1_PlatformConfig platformConfig() {

        return ( WebSphere_6_1_PlatformConfig)this.getModel();
    }
    /**
     * Factory method for building this model.
     */
    protected Object buildModel() {
        return new WebSphere_6_1_PlatformConfig();
    }

}

