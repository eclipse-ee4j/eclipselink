/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

// 05/28/2008-1.0M8 Andrei Ilitchev 
//   - 224964: Provide support for Proxy Authentication through JPA.
//      Changed behaviour of ExclusiveIsolatedClientSession - it no longer throws exception
//      on attempt to instantiate value holder for isolated object when the session is closed,
//      it rather obtains the connection again and releases connection immediately after the query is done.
public class ExclusiveConnectionClosedExceptionTest extends AutoVerifyTestCase {
    protected DatabaseLogin login;
    protected ServerSession server;
    protected int numAcquireExclusive;
    protected int numReleaseExclusive;
    protected String errorMsg = "";
    class Listener extends SessionEventAdapter {
        public void postAcquireExclusiveConnection(SessionEvent event) {
            numAcquireExclusive++;
            // Bug 299048 - Triggering indirection on closed ExclusiveIsolatedSession may cause exception 
            event.getSession().executeNonSelectingSQL("UPDATE ISOLATED_EMPLOYEE SET F_NAME = 'A' WHERE EMP_ID = 0");
        }
    
        public void preReleaseExclusiveConnection(SessionEvent event) {
            numReleaseExclusive++;
            // Bug 299048 - Triggering indirection on closed ExclusiveIsolatedSession may cause exception 
            event.getSession().executeNonSelectingSQL("UPDATE EMPLOYEE SET F_NAME = 'A' WHERE EMP_ID = 0");
        }
    }
    Listener listener = new Listener();
    protected Vector emps;

    public ExclusiveConnectionClosedExceptionTest() {
        setDescription("This test will verify that when indirection is triggered after the session is released callbacks are called and connection released. Exception is no longer thrown.");
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
            errorMsg = "";
            numAcquireExclusive = 0;
            numReleaseExclusive = 0;
            this.server.getEventManager().removeListener(listener);
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
            this.server.getDefaultConnectionPolicy().setExclusiveMode(ConnectionPolicy.ExclusiveMode.Isolated);
            this.server.setSessionLog(getSession().getSessionLog());
            copyDescriptors(getSession());
            this.server.getEventManager().addListener(listener);
            this.server.login();
        } catch (RuntimeException ex) {
            getSession().logMessage("This test requires that the connected user has privleges to \"Creat any context\", \"Drop any context\" and \"execute Sys.DBMS_RLS package\" and \"execute Sys.DBMS_SESSION package\".");
            throw ex;
        }
    }

    public void test() {
        ReadObjectQuery query = new ReadObjectQuery(IsolatedEmployee.class);
        ClientSession client1 = this.server.acquireClientSession();
        IsolatedEmployee employee = (IsolatedEmployee)client1.executeQuery(query);
        // executing the query causes exclusive connection to be acquired.
        if(numAcquireExclusive != 1 || numReleaseExclusive != 0) {
            errorMsg += "After executing query numAcquireExclusive == " +numAcquireExclusive+" and numReleaseExclusive = "+numReleaseExclusive+"; 1 and 0 were expected.\n";
        }
        client1.release();
        // releasing the session causes exclusive connection to be acquired.
        if(numAcquireExclusive != 1 || numReleaseExclusive != 1) {
            errorMsg += "After releasing ExclusiveIsolatedClientSession numAcquireExclusive == " +numAcquireExclusive+" and numReleaseExclusive = "+numReleaseExclusive+"; 1 and 1 were expected.\n";
        }
        if(client1.getWriteConnection() != null) {
            errorMsg += "After releasing ExclusiveIsolatedClientSession its writeConnection is not null.\n";
        }
        employee.getPhoneNumbers();
        // instantiating of a ValueHolder on released session causes exclusive connection to be acquired for the query, released as soon as the query is completed.
        if(numAcquireExclusive != 2 || numReleaseExclusive != 2) {
            errorMsg += "After instantiating ValueHolder on released ExclusiveIsolatedClientSession numAcquireExclusive == " +numAcquireExclusive+" and numReleaseExclusive = "+numReleaseExclusive+"; 2 and 2 were expected.\n";
        }
        if(client1.getWriteConnection() != null) {
            errorMsg += "After instantiating ValueHolder on released ExclusiveIsolatedClientSession its writeConnection is not null.\n";
        }
    }

    public void verify() {
        if(errorMsg.length() > 0) {
            throw new TestErrorException(errorMsg);
        }
    }
}
