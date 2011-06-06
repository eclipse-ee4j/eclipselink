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
package org.eclipse.persistence.testing.tests.clientserver;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.ConnectionPool;

public class Server1 {
    public org.eclipse.persistence.sessions.server.Server serverSession;
    public boolean errorOccured;

    public Server1(ClientServerReadingTest testCase) {
        if (testCase.type == 1) {
            this.serverSession = new Project(testCase.login).createServerSession(2, 2);
            this.serverSession.useExclusiveReadConnectionPool(2, 2);
        }
        if (testCase.type == 2) {
            this.serverSession = new Project(testCase.login).createServerSession(5, 5);
            this.serverSession.useReadConnectionPool(5, 5);
        }
        if (testCase.type == 3) {
            this.serverSession = new Project(testCase.login).createServerSession(1, 5);
            this.serverSession.useReadConnectionPool(1, 5);
        }
    }

    public void copyDescriptors(Session session) {
        Vector descriptors = new Vector();

        for (Iterator iterator = session.getDescriptors().values().iterator(); iterator.hasNext();) {
            descriptors.addElement(iterator.next());
        }

        serverSession.addDescriptors(descriptors);
    }

    public void login() {
        this.serverSession.login();

        ConnectionPool cp = this.serverSession.getConnectionPool("default");
        try {
            //close off one of the connections.  This part of the test sees if our reconnect on the fly works.
            ((org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor)cp.getConnectionsAvailable().get(0)).getConnection().close();
        } catch (java.sql.SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    public void logout() {
        this.serverSession.logout();
    }
}
