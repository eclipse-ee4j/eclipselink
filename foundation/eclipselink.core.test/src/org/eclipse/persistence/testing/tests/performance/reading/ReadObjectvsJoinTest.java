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
package org.eclipse.persistence.testing.tests.performance.reading;

import java.util.Vector;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.performance.toplink.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance of read object and access to a 1-1, 1-m vs joining.
 */
public class ReadObjectvsJoinTest extends PerformanceComparisonTestCase {
    protected Employee employee;
    protected ReadObjectQuery query;
    protected ReadObjectQuery joinQuery;

    public ReadObjectvsJoinTest() {
        setDescription("This test compares the performance of read object and access to 1-1, 1-m vs joining.");
        addReadObjectJoinTest();
    }

    public void setup() throws Exception {
        Expression expression = new ExpressionBuilder().get("firstName").equal("Bob");
        employee = (Employee)getSession().readObject(Employee.class, expression);
        
        query = new ReadObjectQuery(Employee.class);
        query.setSelectionCriteria(query.getExpressionBuilder().get("id").equal(query.getExpressionBuilder().getParameter("id")));
        query.addArgument("id");
        
        joinQuery = new ReadObjectQuery(Employee.class);
        joinQuery.setSelectionCriteria(joinQuery.getExpressionBuilder().get("id").equal(joinQuery.getExpressionBuilder().getParameter("id")));
        joinQuery.addJoinedAttribute("address");
        joinQuery.addJoinedAttribute(joinQuery.getExpressionBuilder().anyOf("phoneNumbers"));
        joinQuery.addArgument("id");
    }

    /**
     * Read an employee and access address, phones, cleared cache.
     */
    public void test() throws Exception {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        Vector arguments = new Vector();
        arguments.add(employee.getId());
        Employee result = (Employee)getSession().executeQuery(query, arguments);
        result.getAddress();
        result.getPhoneNumbers().size();
    }

    /**
     * Read an employee and join read their address, phones.
     */
    public void addReadObjectJoinTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                getSession().getIdentityMapAccessor().initializeIdentityMaps();
                Vector arguments = new Vector();
                arguments.add(employee.getId());
                Employee result = (Employee)getSession().executeQuery(joinQuery, arguments);
                result.getAddress();
                result.getPhoneNumbers().size();
            }
        };
        test.setName("ReadObjectJoinTest");
        test.setAllowableDecrease(30);
        addTest(test);
    }
}
