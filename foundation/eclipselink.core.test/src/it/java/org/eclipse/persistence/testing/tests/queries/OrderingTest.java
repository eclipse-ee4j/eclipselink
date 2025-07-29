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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.DataRecord;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Vector;

/**
 * Test the ordering feature
 */
public class OrderingTest extends TestCase {
    protected Vector customSQLRows;
    protected Vector orderedQueryObjects;

    public OrderingTest() {
        setDescription("This test verifies the ordering feature works properly");
    }

    @Override
    protected void setup() {
        customSQLRows = getSession().executeSelectingCall(new org.eclipse.persistence.queries.SQLCall("SELECT * FROM EMPLOYEE ORDER BY L_NAME, F_NAME"));
    }

    @Override
    public void test() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        query.addAscendingOrdering("lastName");
        query.addAscendingOrdering("firstName");

        orderedQueryObjects = (Vector)getSession().executeQuery(query);

    }

    @Override
    protected void verify() {
        DataRecord row;
        Employee employee;
        String firstName;
        String lastName;

        for (int i = 0; i < orderedQueryObjects.size(); i++) {
            row = (DataRecord)customSQLRows.get(i);
            employee = (Employee)orderedQueryObjects.get(i);
            firstName = (String)row.get("F_NAME");
            lastName = (String)row.get("L_NAME");

            if (!(employee.getFirstName().equals(firstName) && employee.getLastName().equals(lastName))) {
                throw new TestErrorException("The ordering test failed.  The results are not in the right order");
            }
        }
    }
}
