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
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.exceptions.*;

public class ExclusiveConnectionClosedExceptionTest extends AutoVerifyTestCase {
    protected DatabaseLogin login;
    protected ServerSession server;
    protected VPDIsolatedSessionEventAdaptor eventAdaptor;
    protected Vector emps;

    public ExclusiveConnectionClosedExceptionTest() {
        setDescription("This test will verify that an exception is thrown when indirection is triggered after the session is released");
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
            this.server.logout();
            getDatabaseSession().logout();
            getDatabaseSession().login();
            String schemaName = getSession().getLogin().getUserName();
        } catch (DatabaseException ex) {
        }
    }

    public void setup() {
        try {
            this.emps = getSession().readAllObjects(IsolatedEmployee.class);
            String schemaName = getSession().getLogin().getUserName();
            this.login = (DatabaseLogin)getSession().getLogin().clone();
            this.server = new ServerSession(this.login, 2, 5);
            this.server.getDefaultConnectionPolicy().setShouldUseExclusiveConnection(true);
            this.server.setSessionLog(getSession().getSessionLog());
            copyDescriptors(getSession());
            this.server.login();
        } catch (RuntimeException ex) {
            getSession().logMessage("This test requires that the connected user has privleges to \"Creat any context\", \"Drop any context\" and \"execute Sys.DBMS_RLS package\" and \"execute Sys.DBMS_SESSION package\".");
            throw ex;
        }
    }

    public void test() {
        ReadObjectQuery query = new ReadObjectQuery(IsolatedEmployee.class);
        Session client1 = this.server.acquireClientSession();
        IsolatedEmployee employee = (IsolatedEmployee)client1.executeQuery(query);
        client1.release();
        try {
            employee.getPhoneNumbers();
            throw new TestErrorException("The Isolated Connection was not closed");
        } catch (ValidationException ex) {
        }
    }

    public void verify() {
    }
}
