/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.queries;

import java.util.List;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

public class QBECollectionMapping extends TestCase {
    public Employee employee;
    public QueryByExamplePolicy policy;

    public QBECollectionMapping() {
        this.setDescription("To test the Query By Example for a simple Employee with no emply fields.");
    }

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

    public void test() {
        Employee example = new Employee();
        PhoneNumber number = new PhoneNumber();
        number.setNumber(((PhoneNumber) employee.getPhoneNumbers().get(0)).getNumber());
        number.setType(((PhoneNumber) employee.getPhoneNumbers().get(0)).getType());
        number.setAreaCode(((PhoneNumber) employee.getPhoneNumbers().get(0)).getAreaCode());
        example.getPhoneNumbers().add(number);
        number = new PhoneNumber();
        number.setNumber(((PhoneNumber) employee.getPhoneNumbers().get(1)).getNumber());
        number.setType(((PhoneNumber) employee.getPhoneNumbers().get(1)).getType());
        number.setAreaCode(((PhoneNumber) employee.getPhoneNumbers().get(1)).getAreaCode());
        example.getPhoneNumbers().add(number);
        ReadAllQuery query = new ReadAllQuery();
        query.setExampleObject(example);
        query.setQueryByExamplePolicy(policy);
        Employee emp = new Employee();
        List<Employee> result = (List<Employee>)getSession().executeQuery(query);
        if (!result.contains(employee)) {
            throw (new TestErrorException("Query By Example does not return the correct object."));
        }
    }
}
