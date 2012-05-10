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
package org.eclipse.persistence.testing.tests.identitymaps.cacheinvalidation;

import java.util.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.descriptors.invalidation.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Test a check cache only query with Cache Expiry
 */
public class ReadAllQueryCheckCacheOnlyExpiryTest extends CacheExpiryTest {

    protected int expectedEmployees = 0;
    protected Vector returnedEmployees = null;
    protected Expression employeeExpression = null;

    public ReadAllQueryCheckCacheOnlyExpiryTest() {
        setDescription("Test Cache Expiry a with a checkCacheOnly ReadAllQuery.");
    }

    public void setup() {
        super.setup();
        getSession().getDescriptor(Employee.class).setCacheInvalidationPolicy(new TimeToLiveCacheInvalidationPolicy(1000000));
        getSession().getDescriptor(Address.class).setCacheInvalidationPolicy(new TimeToLiveCacheInvalidationPolicy(1000000));

        // Put all employees and addresses in the cache
        getSession().readAllObjects(Employee.class);
        getSession().readAllObjects(Address.class);

        // Read all the male employees from canada as a basis for our results.
        ExpressionBuilder employee = new ExpressionBuilder();
        employeeExpression = employee.get("gender").equal("Male");
        employeeExpression = employeeExpression.and(employee.get("address").get("country").equal("Canada"));
        Vector employees = getSession().readAllObjects(Employee.class, employeeExpression);
        expectedEmployees = employees.size();
        Enumeration employeeEnum = employees.elements();
        // trigger indirection on address to allow check cache only query to work
        while (employeeEnum.hasMoreElements()) {
            ((Employee)employeeEnum.nextElement()).getAddress();
        }

        // Read all the employees with last name smith
        ExpressionBuilder smithBuilder = new ExpressionBuilder();
        employees = getSession().readAllObjects(Employee.class, smithBuilder.get("lastName").equal("Smith"));
        // invalidate the smiths.
        getAbstractSession().getIdentityMapAccessor().invalidateObjects(employees);
        expectedEmployees -= employees.size();

        // invalidate the employees from ontario to test whether we can use expressions
        // that contain invalid data.
        ExpressionBuilder address = new ExpressionBuilder();
        Expression bcAddresses = address.get("province").equal("ONT");
        Vector addresses = getSession().readAllObjects(Address.class, bcAddresses);
        getAbstractSession().getIdentityMapAccessor().invalidateObjects(addresses);
    }

    public void test() {
        // Execute a read all check cache only query for all the male employees.
        // It should return only the valid employees in the cache
        ReadAllQuery query = new ReadAllQuery(Employee.class, employeeExpression);
        query.checkCacheOnly();
        returnedEmployees = (Vector)getSession().executeQuery(query);
    }

    public void verify() {
        if (!(returnedEmployees.size() == expectedEmployees)) {
            throw new TestErrorException("Check Cache Only Read All Query does not get the correct number" + 
                                         " of results from the cache");
        }
    }
}
