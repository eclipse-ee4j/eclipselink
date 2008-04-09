/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.isolatedsession;

import java.util.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.exceptions.*;

public class ClientServerTest extends AutoVerifyTestCase {
    protected DatabaseLogin login;
    protected ArrayList clients;
    protected ServerSession server;
    protected boolean isExclusive = false;

    public ClientServerTest(boolean isExclusive) {
        clients = new ArrayList();
        this.isExclusive = isExclusive;
        setDescription("This test acts as a template for tests using the client server framework");
    }

    public void copyDescriptors(Session session) {
        Vector descriptors = new Vector();

        for (Iterator iterator = session.getDescriptors().values().iterator(); iterator.hasNext(); ) {
            descriptors.addElement(iterator.next());
        }
        this.server.addDescriptors(descriptors);
        // Since the descriptors are already initialized, must also set the session to isolated.
        this.server.getProject().setHasIsolatedClasses(true);
    }

    public void reset() {
        try {
            while (!this.clients.isEmpty()) {
                ((Session)this.clients.get(0)).release();
                this.clients.remove(0);
            }
            this.server.logout();
            getDatabaseSession().logout();
            getDatabaseSession().login();

        } catch (Exception ex) {
            if (ex instanceof ValidationException) {
                this.verify();
            }
        }
    }

    public void setup() {
        try {
            this.login = (DatabaseLogin)getSession().getLogin().clone();
            this.server = new ServerSession(this.login, 2, 5);
            this.server.setSessionLog(getSession().getSessionLog());
            copyDescriptors(getSession());
            this.server.login();
            ConnectionPolicy connectionPolicy = this.server.getDefaultConnectionPolicy();
            if (this.isExclusive) {
                connectionPolicy = new ConnectionPolicy();
                connectionPolicy.setShouldUseExclusiveConnection(true);
                connectionPolicy.setProperty("isExclusive", new Boolean(true));
            }
            this.clients.add(this.server.acquireClientSession(connectionPolicy));
            this.clients.add(this.server.acquireClientSession(connectionPolicy));
            this.clients.add(this.server.acquireClientSession(connectionPolicy));
        } catch (ValidationException ex) {
            this.verify();
        }
    }

    public void test() {
    }

    public void verify() {
    }
}
