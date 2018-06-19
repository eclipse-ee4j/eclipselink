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

import org.eclipse.persistence.internal.sessions.factories.model.transport.RMIIIOPTransportManagerConfig;

/**
 * Session Configuration model adapter class for the
 * TopLink Foudation Library class RMIIIOPTransportManagerConfig
 *
 * @see RMIIIOPTransportManagerConfig
 *
 * @author Tran Le
 */
public final class RMIIIOPTransportManagerAdapter extends RMITransportManagerAdapter {

        /**
         * Creates a new Discovery for the specified model object.
         */
    RMIIIOPTransportManagerAdapter( SCAdapter parent, RMIIIOPTransportManagerConfig scConfig) {

            super( parent, scConfig);
        }
        /**
         * Creates a new Discovery.
         */
        protected RMIIIOPTransportManagerAdapter( SCAdapter parent) {

            super( parent);
        }
        /**
         * Returns this Config Model Object.
         */
        private final RMIIIOPTransportManagerConfig iiopManager() {

            return ( RMIIIOPTransportManagerConfig)this.getModel();
        }
        /**
         * Factory method for building this model.
         */
        protected Object buildModel() {
            return new RMIIIOPTransportManagerConfig();
        }

}
