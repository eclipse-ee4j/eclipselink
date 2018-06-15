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

import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigProject;
import org.eclipse.persistence.internal.sessions.factories.model.pool.ConnectionPolicyConfig;

/**
 * Session Configuration model adapter class for the
 * TopLink Foudation Library class ConnectionPolicyConfig
 *
 * @see ConnectionPolicyConfig
 *
 * @author Tran Le
 */
public class ConnectionPolicyAdapter extends SCAdapter {

    /**
     * Creates a new ConnectionPolicy for the specified model object.
     */
    ConnectionPolicyAdapter( SCAdapter parent, ConnectionPolicyConfig scConfig) {

        super( parent, scConfig);
    }
    /**
     * Creates a new ConnectionPolicy.
     */
    protected ConnectionPolicyAdapter( SCAdapter parent) {

        super( parent);
    }
    /**
     * Returns this Config Model Object.
     */
    private final ConnectionPolicyConfig policyConfig() {

        return ( ConnectionPolicyConfig)this.getModel();
    }
    /**
     * Factory method for building this model.
     */
    protected Object buildModel() {
        return new ConnectionPolicyConfig();
    }

    protected void initializeDefaults() {
        super.initializeDefaults();

        this.setUseExclusiveConnection( XMLSessionConfigProject.EXCLUSIVE_CONNECTION_DEFAULT);
        this.setLazyConnection( XMLSessionConfigProject.LAZY_DEFAULT);
    }

    /**
     * Returns this config model property..
     */
    boolean usesExclusiveConnection() {

        return this.policyConfig().getUseExclusiveConnection();
    }
    /**
     * Sets this config model property.
     */
    void setUseExclusiveConnection( boolean value) {

        this.policyConfig().setUseExclusiveConnection( value);
    }
    /**
     * Returns this config model property..
     */
    boolean usesLazyConnection() {

        return this.policyConfig().getLazy();
    }
    /**
     * Sets this config model property.
     */
    void setLazyConnection( boolean value) {

        this.policyConfig().setLazy( value);
    }
}
