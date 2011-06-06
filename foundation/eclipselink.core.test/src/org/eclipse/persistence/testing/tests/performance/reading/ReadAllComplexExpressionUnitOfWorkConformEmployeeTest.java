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
package org.eclipse.persistence.testing.tests.performance.reading;

import java.util.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.testing.models.performance.toplink.*;
import org.eclipse.persistence.testing.tests.performance.PerformanceTest;

/**
 * This tests the performance of conforming queries.
 * Its purpose is to compare the test result with previous release/label results.
 * It also provides a useful test for profiling performance.
 */
public class ReadAllComplexExpressionUnitOfWorkConformEmployeeTest extends PerformanceTest {
    protected UnitOfWork uow;

    public ReadAllComplexExpressionUnitOfWorkConformEmployeeTest() {
        setDescription("This tests the performance of conforming queries.");
    }

    /**
     * Create a unit of work with changes.
     */
    public void setup() {
        uow = getSession().acquireUnitOfWork();
        List employees = uow.readAllObjects(Employee.class);
        for (Iterator iterator = employees.iterator(); iterator.hasNext();) {
            Employee employee = (Employee)iterator.next();
            employee.getAddress();
            employee.getPhoneNumbers().size();
        }
        uow.deleteObject(employees.get(5));
        uow.registerNewObject(new Employee());
        Employee changedEmployee = (Employee)employees.get(10);
        changedEmployee.setFirstName("Bob");
    }

    /**
     * Read employee and clear the cache, test database read.
     */
    public void test() throws Exception {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        ExpressionBuilder employee = new ExpressionBuilder();
        query.conformResultsInUnitOfWork();
        query.setSelectionCriteria(employee.get("firstName").equal("Brendan").and(employee.get("salary").equal(100000)).and(employee.get("period").get("startDate").equal(Helper.dateFromString("1901-12-31"))).and(employee.get("address").get("city").like("%pean%")).and(employee.anyOf("phoneNumbers").get("areaCode").equal("613")));
        List result = (List)getSession().executeQuery(query);
    }
}
