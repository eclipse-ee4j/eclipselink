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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;


public class ProjectXMLStoredProcedureCallTest extends TestCase {
    private DatabaseQuery query, UNamedQuery = null;
    private StoredProcedureCall storedProcedureCall, UNamedstoredProcedureCall = null;
    private ClassDescriptor employeeDescriptor;

    public ProjectXMLStoredProcedureCallTest() {
        setDescription("Tests that sepecified stored procedure can read from XML and execute correctly.");
    }

    public void reset() {
    }

    public void setup() {
        // right now only the stored procedure is set up in Oracle
        if (!(getSession().getPlatform().isOracle())) {
            throw new TestWarningException("This test can only be run in Oracle");
        }
    }

    public void test() {
        Project project = 
            XMLProjectReader.read("MWIntegrationCustomSQLEmployeeProject.xml", getClass().getClassLoader());
        employeeDescriptor = project.getDescriptor(Employee.class);
        testNamedArgumentStoredProcedureCall();
        testUNamedArgumentStoredProcedureCall();
    }

    // for passed-in named argument

    private void testNamedArgumentStoredProcedureCall() {
        query = employeeDescriptor.getQueryManager().getQuery("StoredProcedureCallInDataReadQuery");
        storedProcedureCall = (StoredProcedureCall)query.getCall();
    }

    // for passed-in no named argument

    private void testUNamedArgumentStoredProcedureCall() {
        UNamedQuery = employeeDescriptor.getQueryManager().getQuery("UNamedStoredProcedureCallInDataReadQuery");
        UNamedstoredProcedureCall = (StoredProcedureCall)UNamedQuery.getCall();
    }

    protected void verify() {
        verifyNamedArgumentStoredProcedureCall();
        verifyUNamedArgumentStoredProcedureCall();
    }

    private void verifyNamedArgumentStoredProcedureCall() {
        if (query == null) {
            throw new TestErrorException("The query was incorrectly either read from or write to XML. Expected: [StoredProcedureCallInDataReadQuery]");
        }
        if (storedProcedureCall == null) {
            throw new TestErrorException("The stored procedure was incorrectly either read from or write to XML.");
        }

        Vector parameters = new Vector();

        DatabaseRecord row = (DatabaseRecord)((Vector)getSession().executeQuery(query, parameters)).firstElement();

        Integer P_INOUT_FIELD_NAME = (Integer)row.get("P_INOUT_FIELD_NAME");
        Integer P_OUT_FIELD_NAME = (Integer)row.get("P_OUT_FIELD_NAME");

        if (!P_INOUT_FIELD_NAME.equals(new Integer(1000)) || !P_OUT_FIELD_NAME.equals(new Integer(100))) {
            throw new TestErrorException("Stored Procedure which write to or read from XML does not execute as expected.");
        }
    }

    private void verifyUNamedArgumentStoredProcedureCall() {
        if (UNamedQuery == null) {
            throw new TestErrorException("The UNamed query name was not incorrectly either read from or write to XML. Expected: [UNamedStoredProcedureCallInDataReadQuery]");
        }
        if (UNamedstoredProcedureCall == null) {
            throw new TestErrorException("The UNamed stored procedure was incorrectly either read from or write to XML.");
        }

        Vector parameters = new Vector();
        DatabaseRecord unamedrow = 
            (DatabaseRecord)((Vector)getSession().executeQuery(UNamedQuery, parameters)).firstElement();

        Integer UNAMED_P_INOUT_FIELD_NAME = (Integer)unamedrow.get("P_INOUT_FIELD_NAME");
        Integer UNAMED_P_OUT_FIELD_NAME = (Integer)unamedrow.get("P_OUT_FIELD_NAME");

        if (!UNAMED_P_INOUT_FIELD_NAME.equals(new Integer(1000)) || 
            !UNAMED_P_OUT_FIELD_NAME.equals(new Integer(100))) {
            throw new TestErrorException("UNnamed Stored Procedure which write to or read from XML dose not execute as expected.");
        }
    }
}
