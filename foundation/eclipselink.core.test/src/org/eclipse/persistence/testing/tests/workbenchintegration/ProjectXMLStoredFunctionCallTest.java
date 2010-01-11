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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import java.util.Vector;

import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.DatabaseQuery;

@SuppressWarnings("unchecked")
public class ProjectXMLStoredFunctionCallTest extends TestCase {
    private DatabaseQuery query;
    private ClassDescriptor employeeDescriptor;
    private Object result;

    public ProjectXMLStoredFunctionCallTest() {
        setDescription("Tests that sepecified stored function can read from XML and execute correctly.");
    }

    public void reset() {
    }

    public void setup() {
        // right now only the stored function is set up in Oracle
        if (!(getSession().getPlatform().isOracle())) {
            throw new TestWarningException("This test can only be run in Oracle");
        }
        Project project = null;
        try {
          project = XMLProjectReader.read("MWIntegrationCustomSQLEmployeeProject.xml", getClass().getClassLoader());
        }
        catch (Exception e) {
          throw new TestErrorException(
            "The stored function was read from or written to XML incorrectly", e);
        }
        employeeDescriptor = project.getDescriptor(Employee.class);
        query = employeeDescriptor.getQueryManager().getQuery("StoredFunctionCallInNamedQuery");
        if (query == null) {
            throw new TestErrorException(
              "The query was incorrectly either read from or write to XML. Expected: [StoredFunctionCallInNamedQuery]");
        }
    }

    public void test() {
        Vector parameters = new Vector();
        Long p_inout = new Long(99);
        Long p_in = new Long(100);
        parameters.add(p_inout);
        parameters.add(p_in);
        result = getSession().executeQuery(query, parameters);
    }

    public void verify() {
      DatabaseRecord row = (DatabaseRecord)((Vector)result).firstElement();
      Integer p_inout = (Integer)row.get("P_INOUT");
      if (!p_inout.equals(new Integer(100))) {
        throw new TestErrorException(
          "The stored function did not execute correctly. Expected: [P_INOUT = 100]");
      }
      Integer p_out = (Integer)row.get("P_OUT");
      if (!p_out.equals(new Integer(99))) {
        throw new TestErrorException(
          "The stored function did not execute correctly. Expected: [P_OUT = 99]");
      }
      Integer returnValue = (Integer)row.getValues().firstElement();
      if (!returnValue.equals(new Integer(99))) {
        throw new TestErrorException(
          "The stored function did not execute correctly. Expected: [return value = 99]");
      }

      // bug 5744278: should convert from 'natural' JDBC conversion to
      // what the StoredFunctionCall has specified
      throw new TestWarningException(
        "The stored function did not convert returned results correctly. Expected: [Long], Actual: [Integer]");
    }
}
