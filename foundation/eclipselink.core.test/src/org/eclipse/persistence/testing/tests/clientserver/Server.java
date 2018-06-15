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
package org.eclipse.persistence.testing.tests.clientserver;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.server.ConnectionPool;

public class Server {
    public org.eclipse.persistence.sessions.server.Server serverSession;
    public boolean errorOccured;

    public Server(Project project) {
        this.serverSession = project.createServerSession(2, 5);
        this.serverSession.useReadConnectionPool(2, 2);
        this.serverSession.addConnectionPool("master", project.getLogin(), 1, 1);
    }

    public Server(DatabaseLogin login) {
        this.serverSession = new Project(login).createServerSession(2, 5);
        this.serverSession.useReadConnectionPool(2, 2);
        this.serverSession.addConnectionPool("master", login, 1, 1);
    }

    public Server(DatabaseLogin login, int min, int max) {
        this.serverSession = new Project(login).createServerSession(min, max);
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
