/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Vector;

@SuppressWarnings("unchecked")
public class ProjectXMLStoredFunctionCallTest extends TestCase {
    private DatabaseQuery query;
    private ClassDescriptor employeeDescriptor;
    private Object result;

    public ProjectXMLStoredFunctionCallTest() {
        setDescription("Tests that sepecified stored function can read from XML and execute correctly.");
    }

    @Override
    public void reset() {
    }

    @Override
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

    @Override
    public void test() {
        Vector parameters = new Vector();
        Long p_inout = 99L;
        Long p_in = 100L;
        parameters.add(p_inout);
        parameters.add(p_in);
        result = getSession().executeQuery(query, parameters);
    }

    @Override
    public void verify() {
      DatabaseRecord row = (DatabaseRecord)((Vector)result).get(0);
      Long p_inout = (Long)row.get("P_INOUT");
      if (!p_inout.equals(100L)) {
        throw new TestErrorException(
          "The stored function did not execute correctly. Expected: [P_INOUT = 100]");
      }
        Long p_out = (Long)row.get("P_OUT");
      if (!p_out.equals(99L)) {
        throw new TestErrorException(
          "The stored function did not execute correctly. Expected: [P_OUT = 99]");
      }
        Long returnValue = (Long)row.getValues().get(0);
      if (!returnValue.equals(99L)) {
        throw new TestErrorException(
          "The stored function did not execute correctly. Expected: [return value = 99]");
      }

      // bug 5744278: should convert from 'natural' JDBC conversion to
      // what the StoredFunctionCall has specified
      throw new TestWarningException(
        "The stored function did not convert returned results correctly. Expected: [Long], Actual: [Integer]");
    }
}
