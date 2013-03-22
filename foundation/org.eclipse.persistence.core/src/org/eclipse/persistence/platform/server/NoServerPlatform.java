/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
    public Class getExternalTransactionControllerClass() {
        return null;
    }

    /**
     * INTERNAL: launchContainerThread(Thread thread): Do nothing because container Threads are not launchable
     * in this platform
     *
     * @param Thread thread : the instance of Thread
     * @return void
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
    public org.eclipse.persistence.logging.SessionLog getServerLog() {
        return new DefaultSessionLog();
    }    

    /**
     * INTERNAL:
     * When there is no server, the original connection will be returned
     */
    public java.sql.Connection unwrapConnection(java.sql.Connection connection){
        return connection;
    }
    
}
