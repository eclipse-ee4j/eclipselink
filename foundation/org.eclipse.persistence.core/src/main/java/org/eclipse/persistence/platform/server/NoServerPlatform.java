/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.platform.server;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.DefaultSessionLog;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.ExternalTransactionController;
import org.eclipse.persistence.transaction.JTA11TransactionController;

/**
 *
 * PUBLIC:
 * <p>
 * This platform is used when EclipseLink is not within any server (Oc4j, WebLogic, ...)
 * This is also the default platform for all newly created DatabaseSessions.
 * <p>
 * This platform has:
 * <p>
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
    public Class<? extends ExternalTransactionController> getExternalTransactionControllerClass() {
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
     * <p>
     * Return the default ServerLog in the base
     *
     * @return org.eclipse.persistence.logging.SessionLog
     */
    @Override
    public org.eclipse.persistence.logging.SessionLog getServerLog() {
        return new DefaultSessionLog();
    }

    @Override
    public void enableJTA() {
        this.ensureNotLoggedIn();
        setJTAEnabled(true);
        setExternalTransactionControllerClass(JTA11TransactionController.class);
    }

    @Override
    public void initializeExternalTransactionController() {
        if (!isJTAEnabled() && !isCMP()) {
            return;
        }
        JTA11TransactionController controller = new JTA11TransactionController();
        controller.setSession((AbstractSession) getDatabaseSession());
        getDatabaseSession().setExternalTransactionController(controller);
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
