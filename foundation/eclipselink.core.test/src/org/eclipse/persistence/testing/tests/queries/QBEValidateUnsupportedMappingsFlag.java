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

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

public class QBEValidateUnsupportedMappingsFlag extends TestCase {
    public Employee employee;
    public QueryByExamplePolicy policy;

    public QBEValidateUnsupportedMappingsFlag() {
        this.setDescription("To test the Query By Example for a simple Employee with no emply fields.");
    }

    public void setup() {
        //This tests the normal case, with no emply fields:
        employee = new Employee();
        policy = new QueryByExamplePolicy();
        policy.setValidateExample(true);
        ExpressionBuilder bldr = new ExpressionBuilder();
        Expression exp = bldr.get("firstName").equal("Jim-bob");
        ReadObjectQuery query = new ReadObjectQuery();
        query.setReferenceClass(Employee.class);
        query.setSelectionCriteria(exp);
        employee = (Employee)getSession().executeQuery(query);

    }

    public void test() {
        ReadObjectQuery query = new ReadObjectQuery();
        query.setExampleObject(employee);
        query.setQueryByExamplePolicy(policy);
        Employee emp = new Employee();
        try{
            emp = (Employee)getSession().executeQuery(query);
            throw (new TestErrorException("Query By Example does not validate unsupported mappings when set to do so"));
        }catch (Exception e) {
            //ignore test should throw exception.
        }
    }
}
