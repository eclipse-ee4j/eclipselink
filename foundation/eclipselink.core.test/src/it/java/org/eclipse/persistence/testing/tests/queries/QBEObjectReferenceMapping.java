/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.QueryByExamplePolicy;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

public class QBEObjectReferenceMapping extends TestCase {
    public Employee employee;
    public QueryByExamplePolicy policy;

    public QBEObjectReferenceMapping() {
        this.setDescription("To test the Query By Example for a simple Employee with no emply fields.");
    }

    @Override
    public void setup() {
        //This tests the normal case, with no emply fields:
        employee = new Employee();
        policy = new QueryByExamplePolicy();
        ExpressionBuilder bldr = new ExpressionBuilder();
        Expression exp = bldr.get("firstName").equal("Jim-bob");
        ReadObjectQuery query = new ReadObjectQuery();
        query.setReferenceClass(Employee.class);
        query.setSelectionCriteria(exp);
        employee = (Employee)getSession().executeQuery(query);

    }

    @Override
    public void test() {
        Employee example = new Employee();
        Address addEx = new Address();
        addEx.setCity(employee.getAddress().getCity());
        addEx.setCountry(employee.getAddress().getCountry());
        addEx.setPostalCode(employee.getAddress().getPostalCode());
        addEx.setProvince(employee.getAddress().getProvince());
        addEx.setStreet(employee.getAddress().getStreet());
        example.setAddress(addEx);
        ReadObjectQuery query = new ReadObjectQuery();
        query.setExampleObject(example);
        query.setQueryByExamplePolicy(policy);
        Employee emp = new Employee();
        emp = (Employee)getSession().executeQuery(query);
        if (emp != employee) {
            throw (new TestErrorException("Query By Example does not return the correct object."));
        }
    }
}
