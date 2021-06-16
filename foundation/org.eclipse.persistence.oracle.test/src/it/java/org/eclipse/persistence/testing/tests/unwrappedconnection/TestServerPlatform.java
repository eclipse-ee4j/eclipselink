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
