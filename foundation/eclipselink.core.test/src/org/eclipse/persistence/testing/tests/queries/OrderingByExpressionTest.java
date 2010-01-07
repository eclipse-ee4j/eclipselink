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
package org.eclipse.persistence.testing.tests.queries;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.*;

/**
 * Test the ordering feature.
 */
public class OrderingByExpressionTest extends OrderingTest {
    protected void setup() {
        this.customSQLRows = getSession().executeSelectingCall(new org.eclipse.persistence.queries.SQLCall("SELECT * FROM EMPLOYEE t1, ADDRESS t2 WHERE t1.ADDR_ID = t2.ADDRESS_ID ORDER BY CITY DESC, STREET"));
    }

    public void test() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        query.addOrdering(query.getExpressionBuilder().get("address").get("city").descending());
        query.addOrdering(query.getExpressionBuilder().get("address").get("street"));

        orderedQueryObjects = (Vector)getSession().executeQuery(query);

    }

    protected void verify() {
        for (int i = 0; i < orderedQueryObjects.size(); i++) {
            Record row = (Record)customSQLRows.elementAt(i);
            Employee employee = (Employee)orderedQueryObjects.elementAt(i);
            String city = (String)row.get("CITY");
            String street = (String)row.get("STREET");

            if (!(employee.getAddress().getCity().equals(city) && employee.getAddress().getStreet().equals(street))) {
                throw new TestErrorException("The ordering test failed.  The results are not in the right order");
            }
        }
    }
}
