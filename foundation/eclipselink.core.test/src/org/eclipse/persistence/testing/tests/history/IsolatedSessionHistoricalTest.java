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
package org.eclipse.persistence.testing.tests.history;

import java.util.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.history.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.tests.isolatedsession.*;
import org.eclipse.persistence.sessions.*;

public class IsolatedSessionHistoricalTest extends AutoVerifyTestCase {
    protected DatabaseLogin login;
    protected ServerSession server;
    protected VPDIsolatedSessionEventAdaptor eventAdaptor;
    protected AsOfClause clause;

    public IsolatedSessionHistoricalTest(AsOfClause clause) {
        setDescription("This test will verify that the isolation support will work with VPD");
        this.clause = clause;
    }

    public void copyDescriptors(Session session) {
        Vector descriptors = new Vector();

        for (Iterator iterator = session.getDescriptors().values().iterator(); iterator.hasNext(); ) {
            descriptors.addElement(iterator.next());
        }
        this.server.addDescriptors(descriptors);
    }

    public void reset() {
        try {
            this.server.logout();
            ((DatabaseSession)getSession()).logout();
            ((DatabaseSession)getSession()).login();
            //this and following same changes for making tests pass on oc4j server, suggested by James
            //String schemaName = getSession().getLogin().getUserName();
            String schemaName = 
                ((AbstractSession)getSession()).getAccessor().getConnection().getMetaData().getUserName();
            String histName = 
                (String)getSession().getDescriptor(Employee.class).getHistoryPolicy().getHistoryTableNames().get(0);
            getSession().executeNonSelectingCall(new SQLCall("CALL DBMS_RLS.DROP_POLICY ('" + schemaName + "', '" + 
                                                             histName + "', 'testing_policy')"));
            getSession().executeNonSelectingCall(new SQLCall("DROP CONTEXT testing_ctx"));
        } catch (DatabaseException ex) {

        } catch (java.sql.SQLException se) {
            se.printStackTrace();
            throw new TestErrorException("There is SQLException");
        }
    }

    public void setup() {
        if (!getSession().getPlatform().isOracle()) {
            throw new TestWarningException("This test supports Oracle DB only.");
        }
        try {
            //lets make the history available only to particular customers.
            Vector emps = getSession().acquireHistoricalSession(this.clause).readAllObjects(Employee.class);
            //String schemaName = getSession().getLogin().getUserName();
            String schemaName = 
                ((AbstractSession)getSession()).getAccessor().getConnection().getMetaData().getUserName();
            String histName = 
                (String)getSession().getDescriptor(Employee.class).getHistoryPolicy().getHistoryTableNames().get(0);
            getSession().executeNonSelectingCall(new SQLCall("CREATE OR REPLACE PACKAGE " + schemaName + 
                                                             ".init AS PROCEDURE set_emp_id(e_id NUMBER, identifier NUMBER); END init;"));
            getSession().executeNonSelectingCall(new SQLCall("CREATE OR REPLACE PACKAGE BODY " + schemaName + 
                                                             ".init AS PROCEDURE set_emp_id(e_id NUMBER, identifier NUMBER) IS BEGIN DBMS_SESSION.SET_CONTEXT('testing_ctx', 'e_id', e_id, null, identifier); end set_emp_id; end;"));
            getSession().executeNonSelectingCall(new SQLCall("CREATE OR REPLACE CONTEXT testing_ctx USING " + 
                                                             schemaName + ".init ACCESSED GLOBALLY"));
            getSession().executeNonSelectingCall(new SQLCall("CREATE OR REPLACE PACKAGE testing_security AS FUNCTION Allowed_ids (ns VARCHAR, na VARCHAR) RETURN VARCHAR2;END;"));
            getSession().executeNonSelectingCall(new SQLCall("CREATE OR REPLACE PACKAGE BODY testing_security AS FUNCTION Allowed_ids(ns VARCHAR, na VARCHAR) RETURN VARCHAR2 IS D_predicate VARCHAR2(200); BEGIN  D_predicate := 'EMP_ID = SYS_CONTEXT(''testing_ctx'', ''e_id'')'; RETURN D_predicate; END Allowed_ids; END testing_security;"));
            getSession().executeNonSelectingCall(new SQLCall("CALL DBMS_RLS.ADD_POLICY ('" + schemaName + "', '" + 
                                                             histName + "', 'testing_policy', '" + schemaName + 
                                                             "', 'testing_security.Allowed_ids', 'select')"));
            getSession().executeNonSelectingCall(new SQLCall("CALL " + schemaName + ".init.set_emp_id (" + 
                                                             ((Employee)emps.get(0)).getId() + ", 1)"));
            getSession().executeNonSelectingCall(new SQLCall("CALL " + schemaName + ".init.set_emp_id (" + 
                                                             ((Employee)emps.get(1)).getId() + ", 2)"));
            this.login = (DatabaseLogin)getSession().getLogin().clone();
            this.server = new ServerSession(this.login, 2, 5);
            this.server.getDefaultConnectionPolicy().setExclusiveMode(ConnectionPolicy.ExclusiveMode.Isolated);
            this.server.setSessionLog(getSession().getSessionLog());
            copyDescriptors(getSession());
            this.server.addDescriptors(new IsolatedEmployeeProject());
            this.server.getProject().setHasGenericHistorySupport(true);
            this.server.login();
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
        this.eventAdaptor.setSession_Id(1);
        long value = System.currentTimeMillis();
        Session history = client1.acquireHistoricalSession(this.clause);
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        //force employee to be isolated for this query.
        query.setShouldUseExclusiveConnection(true);
        Vector employees = (Vector)history.executeQuery(query);
        if (employees.size() != 1) {
            throw new TestErrorException("Failed to execute query correctly within the Isolated Historic Session's exclusive connection");
        }
        //read the non-isolated employee
        employees = history.readAllObjects(Employee.class);
        if (employees.size() != 0) {
            throw new TestErrorException("Failed to exclude the data from non VPD clients");
        }
        history.release();
        client1.release();
    }

    public void verify() {
    }

}
