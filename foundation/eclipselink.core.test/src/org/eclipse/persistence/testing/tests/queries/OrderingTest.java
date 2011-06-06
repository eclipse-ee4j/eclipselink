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
package org.eclipse.persistence.testing.tests.queries;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;

/**
 * Test the ordering feature
 */
public class OrderingTest extends TestCase {
    protected Vector customSQLRows;
    protected Vector orderedQueryObjects;

    public OrderingTest() {
        setDescription("This test verifies the ordering feature works properly");
    }

    protected void setup() {
        customSQLRows = getSession().executeSelectingCall(new org.eclipse.persistence.queries.SQLCall("SELECT * FROM EMPLOYEE ORDER BY L_NAME, F_NAME"));
    }

    public void test() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(new org.eclipse.persistence.testing.models.employee.domain.Employee().getClass());
        query.addAscendingOrdering("lastName");
        query.addAscendingOrdering("firstName");

        orderedQueryObjects = (Vector)getSession().executeQuery(query);

    }

    protected void verify() {
        Record row;
        Employee employee;
        String firstName;
        String lastName;

        for (int i = 0; i < orderedQueryObjects.size(); i++) {
            row = (Record)customSQLRows.elementAt(i);
            employee = (Employee)orderedQueryObjects.elementAt(i);
            firstName = (String)row.get("F_NAME");
            lastName = (String)row.get("L_NAME");

            if (!(employee.getFirstName().equals(firstName) && employee.getLastName().equals(lastName))) {
                throw new TestErrorException("The ordering test failed.  The results are not in the right order");
            }
        }
    }
}
