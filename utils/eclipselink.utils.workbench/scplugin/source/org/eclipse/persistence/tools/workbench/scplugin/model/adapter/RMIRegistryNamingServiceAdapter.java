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

import org.eclipse.persistence.internal.sessions.factories.model.transport.naming.RMIRegistryNamingServiceConfig;

/**
 * Session Configuration model adapter class for the
 * TopLink Foudation Library class RMIRegistryNamingServiceConfig
 *
 * @see RMIRegistryNamingServiceConfig
 *
 * @author Tran Le
 */
public final class RMIRegistryNamingServiceAdapter extends SCAdapter {

    public final static String URL_PROPERTY = "urlConfig";

    /**
     * Creates a new Discovery for the specified model object.
     */
    RMIRegistryNamingServiceAdapter( SCAdapter parent, RMIRegistryNamingServiceConfig scConfig) {

        super( parent, scConfig);
    }
    /**
     * Creates a new Discovery.
     */
    protected RMIRegistryNamingServiceAdapter( SCAdapter parent) {

        super( parent);
    }
    /**
     * Returns this Config Model Object.
     */
    private final RMIRegistryNamingServiceConfig namingService() {

        return ( RMIRegistryNamingServiceConfig)this.getModel();
    }
    /**
     * Factory method for building this model.
     */
    protected Object buildModel() {
        return new RMIRegistryNamingServiceConfig();
    }
    /**
     * Returns this config model property.
     */
    public String getURL() {

        return this.namingService().getURL();
    }
    /**
     * Sets this config model property.
     */
    public void setURL( String url) {

        Object old = this.namingService().getURL();

        this.namingService().setURL( url);
        this.firePropertyChanged( URL_PROPERTY, old, url);
    }
}
