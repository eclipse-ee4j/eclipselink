/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.platform.server;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.logging.DefaultSessionLog;

/**
 *
 * PUBLIC:
 *
 * This platform is used when EclipseLink is not within any server (Oc4j, WebLogic, ...)
 * This is also the default platform for all newly created DatabaseSessions.
 *
 * This platform has:
 *
 * - No external transaction controller class
 * - No runtime services (JMX/MBean)
 * - No launching of container Threads
 *
 */
public final class NoServerPlatform extends ServerPlatformBase {

    /**
     * INTERNAL:
     * Default Constructor: Initialize so that runtime services and JTA are disabled.
     */
    public NoServerPlatform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
        this.disableRuntimeServices();
        this.disableJTA();
    }


    /**
     * PUBLIC: getServerNameAndVersion(): Answer null because this does not apply to NoServerPlatform.
     *
     * @return String serverNameAndVersion
     */
    @Override
    public String getServerNameAndVersion() {
        return null;
    }

    /**
     * INTERNAL: getExternalTransactionControllerClass(): Answer null because this does not apply.
     *
     * @see #isJTAEnabled()
     * @see #disableJTA()
     * @see #initializeExternalTransactionController()
     */
    @Override
    public Class getExternalTransactionControllerClass() {
        return null;
    }

    /**
     * INTERNAL: launchContainerThread(Thread thread): Do nothing because container Threads are not launchable
     * in this platform
     *
     * @param thread the instance of Thread
     */
    public void launchContainerThread(Thread thread) {
    }

    /**
     * INTERNAL: getServerLog(): Return the ServerLog for this platform
     *
     * Return the default ServerLog in the base
     *
     * @return org.eclipse.persistence.logging.SessionLog
     */
    @Override
    public org.eclipse.persistence.logging.SessionLog getServerLog() {
        return new DefaultSessionLog();
    }

    /**
     * INTERNAL:
     * When there is no server, the original connection will be returned
     */
    @Override
    public java.sql.Connection unwrapConnection(java.sql.Connection connection){
        return connection;
    }

}
