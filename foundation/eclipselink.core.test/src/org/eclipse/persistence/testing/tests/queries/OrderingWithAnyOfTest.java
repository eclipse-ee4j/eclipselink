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
public class OrderingWithAnyOfTest extends OrderingTest {
    protected void setup() {
        customSQLRows = getSession().executeSelectingCall(new org.eclipse.persistence.queries.SQLCall("SELECT DISTINCT t3.*, t1.* FROM ADDRESS t3, SALARY t2, EMPLOYEE t1, PHONE t0 WHERE ((((t0.AREA_CODE = '613') AND (t1.EMP_ID = t2.EMP_ID)) AND (t0.EMP_ID = t1.EMP_ID)) AND (t3.ADDRESS_ID = t1.ADDR_ID)) ORDER BY t3.CITY DESC , t1.F_NAME"));
    }

    public void test() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        org.eclipse.persistence.expressions.ExpressionBuilder builder = new org.eclipse.persistence.expressions.ExpressionBuilder();
        query.setSelectionCriteria(builder.anyOf("phoneNumbers").get("areaCode").equal("613"));
        query.addOrdering(builder.get("address").get("city").descending());
        query.addOrdering(builder.get("firstName"));

        orderedQueryObjects = (Vector)getSession().executeQuery(query);

    }

    protected void verify() {
        for (int i = 0; i < orderedQueryObjects.size(); i++) {
            Record row = (Record)customSQLRows.elementAt(i);
            Employee employee = (Employee)orderedQueryObjects.elementAt(i);
            String city = (String)row.get("CITY");
            String name = (String)row.get("F_NAME");

            if (!(employee.getAddress().getCity().equals(city) && employee.getFirstName().equals(name))) {
                throw new TestErrorException("The ordering test failed.  The results are not in the right order");
            }
        }
    }
}
