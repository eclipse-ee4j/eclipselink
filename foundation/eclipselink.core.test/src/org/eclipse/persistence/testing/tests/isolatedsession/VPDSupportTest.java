/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;

public class VPDSupportTest extends AutoVerifyTestCase {
    protected DatabaseLogin login;
    protected ServerSession server;
    protected VPDIsolatedSessionEventAdaptor eventAdaptor;
    protected Session nonVPDSession;

    public VPDSupportTest() {
        setDescription("This test will verify that the isolation support will work with VPD");
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
            //String schemaName = getSession().getLogin().getUserName();
            String schemaName = ((AbstractSession)getSession()).getAccessor().getConnection().getMetaData().getUserName();
            getSession().executeNonSelectingCall(new SQLCall("CALL DBMS_RLS.DROP_POLICY ('" + schemaName + "', 'isolated_employee', 'testing_policy')"));
            getSession().executeNonSelectingCall(new SQLCall("DROP CONTEXT testing_ctx"));
        } catch (DatabaseException ex) {
        } catch (java.sql.SQLException se) {
            se.printStackTrace();
            throw new TestErrorException("There is SQLException");
        }
    }

    public void setup() {
        try {
            Vector emps = getSession().readAllObjects(IsolatedEmployee.class);
            //String schemaName = getSession().getLogin().getUserName();
            String schemaName = ((AbstractSession)getSession()).getAccessor().getConnection().getMetaData().getUserName();
            getSession().executeNonSelectingCall(new SQLCall("CREATE OR REPLACE PACKAGE " + schemaName + ".init AS PROCEDURE set_emp_id(e_id NUMBER, identifier NUMBER); END init;"));
            getSession().executeNonSelectingCall(new SQLCall("CREATE OR REPLACE PACKAGE BODY " + schemaName + ".init AS PROCEDURE set_emp_id(e_id NUMBER, identifier NUMBER) IS BEGIN DBMS_SESSION.SET_CONTEXT('testing_ctx', 'e_id', e_id, null, identifier); end set_emp_id; end;"));
            getSession().executeNonSelectingCall(new SQLCall("CREATE OR REPLACE CONTEXT testing_ctx USING " + schemaName + ".init ACCESSED GLOBALLY"));
            getSession().executeNonSelectingCall(new SQLCall("CREATE OR REPLACE PACKAGE testing_security AS FUNCTION Allowed_ids (ns VARCHAR, na VARCHAR) RETURN VARCHAR2;END;"));
            getSession().executeNonSelectingCall(new SQLCall("CREATE OR REPLACE PACKAGE BODY testing_security AS FUNCTION Allowed_ids(ns VARCHAR, na VARCHAR) RETURN VARCHAR2 IS D_predicate VARCHAR2(200); BEGIN  D_predicate := 'EMP_ID = SYS_CONTEXT(''testing_ctx'', ''e_id'')'; RETURN D_predicate; END Allowed_ids; END testing_security;"));
            getSession().executeNonSelectingCall(new SQLCall("CALL DBMS_RLS.ADD_POLICY ('" + schemaName + "', 'isolated_employee', 'testing_policy', '" + schemaName + "', 'testing_security.Allowed_ids', 'select')"));
            getSession().executeNonSelectingCall(new SQLCall("CALL " + schemaName + ".init.set_emp_id (" + ((IsolatedEmployee)emps.get(0)).getId() + ", 1)"));
            getSession().executeNonSelectingCall(new SQLCall("CALL " + schemaName + ".init.set_emp_id (" + ((IsolatedEmployee)emps.get(1)).getId() + ", 2)"));
            this.login = (DatabaseLogin)getSession().getLogin().clone();
            this.server = new ServerSession(this.login, 2, 5);
            this.server.getDefaultConnectionPolicy().setExclusiveMode(ConnectionPolicy.ExclusiveMode.Isolated);
            this.server.setSessionLog(getSession().getSessionLog());
            copyDescriptors(getSession());
            this.server.login();
            this.nonVPDSession = this.server.acquireClientSession();
            // force the acquisition of the connection
            this.nonVPDSession.readAllObjects(IsolatedEmployee.class);
            this.eventAdaptor = new VPDIsolatedSessionEventAdaptor();
            this.server.getEventManager().addListener(this.eventAdaptor);
        } catch (RuntimeException ex) {
            getSession().logMessage("This test requires that the connected user has privleges to \"Creat any context\", \"Drop any context\" and \"execute Sys.DBMS_RLS package\".");
            throw ex;
        } catch (java.sql.SQLException se) {
            se.printStackTrace();
            throw new TestErrorException("There is SQLException");
        }
    }

    public void test() {
        Session client1 = this.server.acquireClientSession();
        Session client2 = this.server.acquireClientSession();
        this.eventAdaptor.setSession_Id(1);
        Vector vector1 = client1.readAllObjects(IsolatedEmployee.class);
        if (vector1.size() > 1) {
            throw new TestErrorException("Too Many Employee's returned from the VPD");
        }

        // the connections are loaded on read.
        this.eventAdaptor.setSession_Id(2);
        Vector vector2 = client2.readAllObjects(IsolatedEmployee.class);
        if (vector2.size() > 1) {
            throw new TestErrorException("Too Many Employee's returned from the VPD");
        }
        if (vector1.get(0).equals(vector2.get(0))) {
            throw new TestErrorException("The data was not isolated from the other session");
        }
        client1.release();
        client2.release();
    }

    public void verify() {
        if (this.nonVPDSession.readAllObjects(IsolatedEmployee.class).size() > 0) {
            throw new TestErrorException("The objects were not isolated by the VPD");
        }
        this.nonVPDSession.release();
    }
}
