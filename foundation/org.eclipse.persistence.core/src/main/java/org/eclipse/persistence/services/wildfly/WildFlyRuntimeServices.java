/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.services.wildfly;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.services.RuntimeServices;

import java.util.Locale;

/**
 * <p>
 * <b>Purpose</b>: Provide a dynamic interface into the EclipseLink Session.
 * <p>
 * <b>Description</b>: This class is meant to provide facilities for managing an EclipseLink session external
 * to EclipseLink over JMX.
 *
 * @since EclipseLink 4.0.1
 */
public class WildFlyRuntimeServices extends RuntimeServices {

    static {
        PLATFORM_NAME = "WildFly";
    }

    /**
     * PUBLIC:
     *  Default Constructor
     */
    public WildFlyRuntimeServices() {
        super();
    }

    /**
     *  PUBLIC:
     *  Create an instance of WildFlyRuntimeServices to be associated with the provided session
     *
     *  @param session The session to be used with these RuntimeServices
     */
    public WildFlyRuntimeServices(AbstractSession session) {
        super();
        this.session = session;
        this.updateDeploymentTimeData();
    }

    /**
     *  Create an instance of WildFlyRuntimeServices to be associated with the provided locale
     * <p>
     *  The user must call setSession(Session) afterwards to define the session.
     */
    public WildFlyRuntimeServices(Locale locale) {
    }

}
