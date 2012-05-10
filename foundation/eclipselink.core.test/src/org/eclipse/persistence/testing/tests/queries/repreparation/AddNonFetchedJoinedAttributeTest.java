/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Test if SQL is reprepared when the query is modified with a join attribute.
 * Also checks that non fetch join attributes avoid triggering a value holder.
 */
public class AddNonFetchedJoinedAttributeTest extends AutoVerifyTestCase {
    private ReadAllQuery query1, query2;
    private Vector employees1, employees2;
    private static String EXPECTED_SQL1 = "SELECT t1.EMP_ID, t2.EMP_ID, t1.F_NAME, t1.GENDER, t1.L_NAME, t2.SALARY, t1.START_TIME, t1.END_TIME, t1.END_DATE, t1.START_DATE, t1.ADDR_ID, t1.MANAGER_ID, t1.VERSION FROM ADDRESS t0, SALARY t2, EMPLOYEE t1 WHERE ((t2.EMP_ID = t1.EMP_ID) AND (t0.ADDRESS_ID = t1.ADDR_ID))";
    private static String EXPECTED_SQL2 = "SELECT t3.EMP_ID, t4.EMP_ID, t3.F_NAME, t3.GENDER, t3.L_NAME, t4.SALARY, t3.START_TIME, t3.END_TIME, t3.END_DATE, t3.START_DATE, t3.ADDR_ID, t3.MANAGER_ID, t3.VERSION FROM SALARY t4, EMPLOYEE t3, SALARY t2, EMPLOYEE t1, ADDRESS t0 WHERE ((t4.EMP_ID = t3.EMP_ID) AND (((t1.EMP_ID = t3.MANAGER_ID) AND (t2.EMP_ID = t1.EMP_ID)) AND (t0.ADDRESS_ID = t1.ADDR_ID)))";


    public AddNonFetchedJoinedAttributeTest() {
        setDescription("Test if SQL is reprepared the second time and if the non fetch join attribute is instantiated.");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        query1 = new ReadAllQuery(Employee.class);
        employees1 = (Vector)getSession().executeQuery(query1);
    }

    public void test() {
        query1.addNonFetchJoinedAttribute("address");
        employees1 = (Vector)getSession().executeQuery(query1);
        
        query2 = new ReadAllQuery(Employee.class);
        query2.addNonFetchJoinedAttribute(query2.getExpressionBuilder().get("manager").get("address"));
        employees2 = (Vector)getSession().executeQuery(query2);
    }

    public void verify() {
        if (!query1.getCall().getSQLString().equals(EXPECTED_SQL1)) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("AddNonFetchedJoinedAttributeTest failed. \n [Expected] " + EXPECTED_SQL1 + "\n[Found] " + query1.getCall().getSQLString());
        }
        
        Employee employee = (Employee) employees1.firstElement();
    
        if (employee.address.isInstantiated()) {
            throw new TestErrorException("AddNonFetchedJoinedAttributeTest failed. The non fetch join attribute address was instantiated");
        }
        
        Address address = employee.getAddress();
        
        if (address.getPostalCode() == null) {
            throw new TestErrorException("Instantiating the address value holder returned an unpopulated address.");
        }
        
        if (!query2.getCall().getSQLString().equals(EXPECTED_SQL2)) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("AddNonFetchedJoinedAttributeTest failed. \n [Expected] " + EXPECTED_SQL2 + "\n[Found] " + query2.getCall().getSQLString());
        }
        
        employee = (Employee) employees2.firstElement();
    
        if (employee.manager.isInstantiated()) {
            throw new TestErrorException("AddNonFetchedJoinedAttributeTest failed. The non fetch join attribute manager was instantiated");
        }
        
        Employee manager = (Employee) employee.getManager();
        
        if (manager.getFirstName() == null) {
            throw new TestErrorException("The manager object was not correctly populated.");
        }
        
        address = manager.getAddress();
        
        if (address.getPostalCode() == null) {
            throw new TestErrorException("Instantiating the address value holder returned an unpopulated address.");
        }
    }
}
