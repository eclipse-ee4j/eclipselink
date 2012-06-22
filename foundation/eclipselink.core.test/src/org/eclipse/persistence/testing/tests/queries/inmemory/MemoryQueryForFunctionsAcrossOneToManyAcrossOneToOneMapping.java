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
package org.eclipse.persistence.testing.tests.queries.inmemory;

import java.util.Vector;
import java.util.Enumeration;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class MemoryQueryForFunctionsAcrossOneToManyAcrossOneToOneMapping extends TestCase {
    protected Employee employee;
    protected ReadAllQuery queryAll;
    protected ReadObjectQuery queryObject;
    protected java.util.Vector allEmployees;
    protected java.util.Vector employees;

    /**
     * This tests in-memory querying across one to many across one to one mappings using functions
     * Creation date: (2/7/01 5:14:19 PM)
     */
    public MemoryQueryForFunctionsAcrossOneToManyAcrossOneToOneMapping() {
        super();
    }

    /**
     * This method was created in VisualAge.
     */
    public void reset() {
        //clear the cache.
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    /**
     * This method was created in VisualAge.
     *
     */
    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        allEmployees = new Vector();
        employees = new Vector();
        Vector managedEmps = new Vector();
        Vector emps = new Vector();
        Vector addressVector = new Vector();
        queryAll = new ReadAllQuery();
        queryAll.setReferenceClass(Employee.class);
        queryAll.setSelectionCriteria(new ExpressionBuilder().anyOf("managedEmployees").get("address").get("city").toUpperCase().greaterThan("MONTREAL"));
        getSession().removeQuery("getAllEmployees");
        getSession().addQuery("getAllEmployees", queryAll);
        allEmployees = (Vector)getSession().executeQuery("getAllEmployees");
        for (Enumeration enumtr = allEmployees.elements(); enumtr.hasMoreElements();) {
            Object anEmp = enumtr.nextElement();
            managedEmps = ((Employee)anEmp).getManagedEmployees();
            //employees.addAll(managedEmps);
            for (Enumeration enum2 = managedEmps.elements(); enum2.hasMoreElements();) {
                Object aNewEmp = enum2.nextElement();
                Object address = ((Employee)aNewEmp).getAddress();
                emps = ((Employee)aNewEmp).getManagedEmployees();
                addressVector.add(address);

                //trigger all the value holders of managedEmployees' addresses
            }

            //Object addr = ((Employee) anEmp).getAddress();
            //addressVector.add(addr);
            //trigger all the value holders of managedEmployees
        }
    }

    /**
     * This method was created in VisualAge.
     */
    public void test() {
        //all the employees with address city greater than montreal  should be
        //in the cache right now.
        queryObject = new ReadObjectQuery();
        queryObject.setReferenceClass(Employee.class);
        queryObject.checkCacheOnly();//read from cache only

        ExpressionBuilder bldr = new ExpressionBuilder();
        Expression exp = bldr.anyOf("managedEmployees").get("address").get("city").toUpperCase().equal("OTTAWA");
        queryObject.setSelectionCriteria(exp);
        employee = (Employee)getSession().executeQuery(queryObject);

    }
}
