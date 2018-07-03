/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */package org.eclipse.persistence.tools.workbench.scplugin.model.adapter;

import org.eclipse.persistence.internal.sessions.factories.model.platform.ServerPlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.WebSphere_7_0_PlatformConfig;

/**
 * Session Configuration model adapter class for the
 * TopLink Foudation Library class WebSphere_7_0_PlatformConfig
 *
 * @see WebSphere_7_0_PlatformConfig
 *
 * @author Les Davis
 */
// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////package org.eclipse.persistence.tools.workbench.scplugin.model.adapter;

import org.eclipse.persistence.internal.sessions.factories.model.platform.ServerPlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.WebSphere_7_0_PlatformConfig;

/////
// Session Configuration model adapter class for the
// TopLink Foudation Library class WebSphere_7_0_PlatformConfig
//
// @see WebSphere_7_0_PlatformConfig
//
// @author Les Davis
public class WebSphere_7_0_PlatformAdapter extends ServerPlatformAdapter {

    /**
     * Default constructor
     */
    public WebSphere_7_0_PlatformAdapter() {
        super();
    }

    /**
     * Creates a new Platform for the specified model object.
     */
    public WebSphere_7_0_PlatformAdapter(SCAdapter parent,
            ServerPlatformConfig scConfig) {
        super(parent, scConfig);
    }

    /**
     * Creates a new Platform.
     */
    public WebSphere_7_0_PlatformAdapter(SCAdapter parent) {
        super(parent);
    }


    /**
     * Returns this Config Model Object.
     */
    private final WebSphere_7_0_PlatformConfig platformConfig() {

        return ( WebSphere_7_0_PlatformConfig)this.getModel();
    }

    /**
     * Factory method for building this model.
     */
    @Override
    protected Object buildModel() {
        return new WebSphere_7_0_PlatformConfig();
    }

}
