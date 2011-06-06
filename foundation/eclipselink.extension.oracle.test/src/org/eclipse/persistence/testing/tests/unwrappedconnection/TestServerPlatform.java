/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.unwrappedconnection;

import java.sql.Connection;

/**
 * Dummy server platform.
 */

import org.eclipse.persistence.logging.DefaultSessionLog;
import org.eclipse.persistence.platform.server.ServerPlatformBase;
import org.eclipse.persistence.sessions.DatabaseSession;

public class TestServerPlatform extends ServerPlatformBase {
    public TestServerPlatform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
        this.disableRuntimeServices();
        this.disableJTA();
    }

    public String getServerNameAndVersion() {
        return null;
    }

    public Class getExternalTransactionControllerClass() {
        return null;
    }

    public void launchContainerThread(Thread thread) {
    }

    public org.eclipse.persistence.logging.SessionLog getServerLog() {
        return new DefaultSessionLog();
    }    

    public java.sql.Connection unwrapConnection(java.sql.Connection connection){
        if(connection instanceof TestOracleConnection){
            Connection conn = ((TestOracleConnection)connection).getPhysicalConnection();
            return conn;
        }
        return connection;
    }
 }
