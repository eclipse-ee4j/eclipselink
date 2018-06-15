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

import org.eclipse.persistence.internal.sessions.factories.model.platform.JBossPlatformConfig;

/**
 * Session Configuration model adapter class for the
 * TopLink Foudation Library class JBossPlatformConfig
 *
 * @see JBossPlatformConfig
 *
 * @author Tran Le
 */
public class JBossPlatformAdapter extends ServerPlatformAdapter {

    /**
     * Default constructor
     */
    JBossPlatformAdapter() {
        super();
    }
    /**
     * Creates a new Platform for the specified model object.
     */
    JBossPlatformAdapter( SCAdapter parent, JBossPlatformConfig scConfig) {

        super( parent, scConfig);
    }
    /**
     * Creates a new Platform.
     */
    protected JBossPlatformAdapter( SCAdapter parent) {

        super( parent);
    }
    /**
     * Returns this Config Model Object.
     */
    private final JBossPlatformConfig platformConfig() {

        return ( JBossPlatformConfig)this.getModel();
    }
    /**
     * Factory method for building this model.
     */
    protected Object buildModel() {
        return new JBossPlatformConfig();
    }
}
