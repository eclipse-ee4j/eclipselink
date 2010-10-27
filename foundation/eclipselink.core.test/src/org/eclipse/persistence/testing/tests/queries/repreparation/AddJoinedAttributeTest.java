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
package org.eclipse.persistence.testing.tests.queries.repreparation;

import java.util.Vector;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;

/**
 * Test if SQL is reprepared when the query is modified with a join attribute.
 */
public class AddJoinedAttributeTest extends AutoVerifyTestCase {
    private ReadAllQuery query;
    private Vector employees;
    private static String EXPECTED_SQL = "SELECT t1.EMP_ID, t2.EMP_ID, t1.F_NAME, t1.GENDER, t1.L_NAME, t2.SALARY, t1.START_TIME, t1.END_TIME, t1.END_DATE, t1.START_DATE, t1.ADDR_ID, t1.MANAGER_ID, t1.VERSION, t0.ADDRESS_ID, t0.CITY, t0.COUNTRY, t0.P_CODE, t0.PROVINCE, t0.STREET FROM ADDRESS t0, SALARY t2, EMPLOYEE t1 WHERE ((t2.EMP_ID = t1.EMP_ID) AND (t0.ADDRESS_ID = t1.ADDR_ID))";

    public AddJoinedAttributeTest() {
        setDescription("Test if SQL is reprepared the second time");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        query = new ReadAllQuery(Employee.class);
        employees = (Vector)getSession().executeQuery(query);
    }

    public void test() {
        query.addJoinedAttribute("address");
        employees = (Vector)getSession().executeQuery(query);
    }

    public void verify() {
        if (!query.getCall().getSQLString().equals(EXPECTED_SQL)) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("AddJoinedAttributeTest failed. \n [Expected] " + EXPECTED_SQL + "\n[Found] " + query.getCall().getSQLString());
        }
    }
}
