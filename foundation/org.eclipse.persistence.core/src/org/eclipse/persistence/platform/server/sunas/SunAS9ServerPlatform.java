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
//     06/30/2010-2.1.1 Michael O'Brien
//       - 316513: Enable JMX MBean functionality for JBoss, Glassfish and WebSphere in addition to WebLogic
//       Move JMX MBean generic registration code up from specific platforms
//       see <link>http://wiki.eclipse.org/EclipseLink/DesignDocs/316513</link>
package org.eclipse.persistence.platform.server.sunas;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.platform.server.glassfish.GlassfishPlatform;

/**
 * PUBLIC:
 *
 * This is the concrete subclass responsible for representing SunAS9-specific server behavior.
 *
 * This platform overrides:
 *
 * getExternalTransactionControllerClass(): to use an SunAS9-specific controller class
 *
 * @deprecated since 2.5 replaced by GlassfishServerPlatform
 */
@Deprecated
public class SunAS9ServerPlatform extends GlassfishPlatform {

    /**
     * INTERNAL:
     * Default Constructor: All behavior for the default constructor is inherited
     */
    public SunAS9ServerPlatform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
    }
}

