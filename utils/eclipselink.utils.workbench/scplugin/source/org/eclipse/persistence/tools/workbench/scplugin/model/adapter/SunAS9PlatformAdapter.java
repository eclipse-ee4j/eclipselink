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

import org.eclipse.persistence.internal.sessions.factories.model.platform.SunAS9PlatformConfig;

/**
 * Session Configuration model adapter class for the
 * TopLink Foudation Library class SunAS9PlatformConfig
 *
 * @see SunAS9PlatformConfig
 *
 * @author Les Davis
 */
public final class SunAS9PlatformAdapter extends ServerPlatformAdapter {

    /**
    * Default constructor
    */
    SunAS9PlatformAdapter() {
            super();
        }

        /**
        * Creates a new Platform for the specified model object.
        */
    SunAS9PlatformAdapter( SCAdapter parent, SunAS9PlatformConfig scConfig) {
            super( parent, scConfig);
        }

        /**
        * Creates a new Platform.
        */
        protected SunAS9PlatformAdapter( SCAdapter parent) {
            super( parent);
        }

        /**
        * Returns this Config Model Object.
        */
        private final SunAS9PlatformConfig platformConfig() {
            return ( SunAS9PlatformConfig)this.getModel();
        }

        /**
        * Factory method for building this model.
        */
        protected Object buildModel() {
            return new SunAS9PlatformConfig();
        }

}
