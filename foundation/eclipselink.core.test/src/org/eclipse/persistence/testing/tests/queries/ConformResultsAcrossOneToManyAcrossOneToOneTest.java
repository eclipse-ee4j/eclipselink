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

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import java.util.Vector;
import java.util.Enumeration;

public class ConformResultsAcrossOneToManyAcrossOneToOneTest extends org.eclipse.persistence.testing.tests.queries.inmemory.MemoryQueryAcrossOneToManyAcrossOneToOneMapping {
    protected ReadAllQuery queryAllObjects;
    protected ReadAllQuery queryAll;
    protected java.util.Vector employeesInCache;
    protected org.eclipse.persistence.sessions.UnitOfWork unitOfWork;

    public ConformResultsAcrossOneToManyAcrossOneToOneTest() {
        super();
    }

    public void reset() {
        //clear the cache
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        unitOfWork = getSession().acquireUnitOfWork();

        allEmployees = new Vector();
        employees = new Vector();
        phones = new Vector();
        Vector allOtherEmps = new Vector();
        Vector address = new Vector();
        Vector managedEmp = new Vector();
        Vector managedEmpVector = new Vector();
        Vector allAddresses = new Vector();
        Vector allEmps = new Vector();
        Vector allAdds = new Vector();

        queryAll = new ReadAllQuery();
        queryAll.setReferenceClass(Employee.class);
        queryAll.setSelectionCriteria(new ExpressionBuilder().anyOf("managedEmployees").get("address").get("city").equal("Perth"));
        allEmployees = (Vector)getSession().executeQuery(queryAll);
        for (Enumeration enumtr = allEmployees.elements(); enumtr.hasMoreElements();) {
            employees = ((Employee)enumtr.nextElement()).getManagedEmployees();
            allEmps.addAll(employees);
            //trigger all the value holders of employees who have managedEmployees
        }

        for (Enumeration enum2 = allEmployees.elements(); enum2.hasMoreElements();) {
            Address addr = ((Employee)enum2.nextElement()).getAddress();
            allAdds.add(addr);
            //trigger all the value holders of employees' addresses
        }

        for (Enumeration enum3 = allEmps.elements(); enum3.hasMoreElements();) {
            Address anotherAddress = ((Employee)enum3.nextElement()).getAddress();
            allAddresses.add(anotherAddress);

            //trigger all the value holders of managed employees' address
        }
        for (Enumeration enum4 = allEmps.elements(); enum4.hasMoreElements();) {
            managedEmpVector = ((Employee)enum4.nextElement()).getManagedEmployees();
            managedEmp.addAll(managedEmpVector);

            //trigger all the value holders of managed employees' managed employees, we don't really need this, but it doesn't hurt
        }

        for (Enumeration enum5 = managedEmp.elements(); enum5.hasMoreElements();) {
            Address anAddress = ((Employee)enum5.nextElement()).getAddress();
            address.add(anAddress);
            //trigger all the value holders of managers' addresses. we don't really need this, but it doesn't hurt
        }
        for (Enumeration enum6 = managedEmp.elements(); enum6.hasMoreElements();) {
            Vector otherEmployees = ((Employee)enum6.nextElement()).getManagedEmployees();
            allOtherEmps.add(otherEmployees);

            //trigger all the value holders of managed employees' managedEmps, we don't really need this, but it doesn't hurt
        }
    }

    public void test() {
        //all the employees with city Perth should be
        //in the cache right now.
        queryAllObjects = new ReadAllQuery();
        queryAllObjects.setReferenceClass(Employee.class);
        ExpressionBuilder bldr = new ExpressionBuilder();
        Expression exp = bldr.anyOf("managedEmployees").get("address").get("city").equal("Perth");
        queryAllObjects.setSelectionCriteria(exp);
        employeesInCache = (Vector)getSession().executeQuery(queryAllObjects);
        unitOfWork.release();

    }

    public void verify() {
        try {
            if (allEmployees.size() == employeesInCache.size()) {
                //?
            }
        } catch (Exception e) {
            throw new TestErrorException("Employees' are not the same as the ones in cache!");
        }
    }
}
