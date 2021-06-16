/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

public class QBEObjectReferenceMapping extends TestCase {
    public Employee employee;
    public QueryByExamplePolicy policy;

    public QBEObjectReferenceMapping() {
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
