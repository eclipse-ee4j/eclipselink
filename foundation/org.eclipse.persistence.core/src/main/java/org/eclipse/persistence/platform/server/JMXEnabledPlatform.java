/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     06/30/2010-2.1.1 Michael O'Brien
//       - 316513: Enable JMX MBean functionality for JBoss, Glassfish and WebSphere in addition to WebLogic
//       Move JMX MBean generic registration code up from specific platforms
//       see <link>http://wiki.eclipse.org/EclipseLink/DesignDocs/316513</link>
package org.eclipse.persistence.platform.server;

/**
 * PUBLIC:
 *     This interface must be implemented by server platform classes that have
 *     JMX/MBean functionality enabled in EclipseLink.<br>
 *     As of EclipseLink
 * @see org.eclipse.persistence.platform.server.JMXServerPlatformBase
 * @since EclipseLink 2.1.1
 */
public interface JMXEnabledPlatform {

    /**
     * INTERNAL:
     * prepareServerSpecificServicesMBean(): Server specific implementation of the
     * creation and deployment of the JMX MBean to provide runtime services for the
     * databaseSession.
     *
     * JMX Implementing platform classes must override this function and supply
     * the server specific MBean instance for later registration by calling it in the constructor.
     *
     * @see ServerPlatform#isRuntimeServicesEnabledDefault()
     * @see ServerPlatform#isRuntimeServicesEnabled()
     * @see ServerPlatform#disableRuntimeServices()
     * @see ServerPlatform#registerMBean()
     * @since EclipseLink 2.1.1
     */
    void prepareServerSpecificServicesMBean();

    /**
     * INTERNAL:
     * getApplicationName(): Answer the name of the module (EAR name) that this session is associated with.
     * Answer "unknown" if there is no application name available.
     * Default behavior is to return "unknown"
     *
     * There are 4 levels of implementation.
     * 1) use the property override weblogic|jboss|glassfish|websphere.applicationName, or
     * 2) perform a reflective weblogic.work.executeThreadRuntime.getApplicationName() call on WebLogic for example
     * 3) extract the moduleName:persistence_unit from the weblogic classloader string representation, or
     * 3) defer to superclass - usually return "unknown"
     *
     * @return String applicationName
     */
    String getApplicationName();
}
