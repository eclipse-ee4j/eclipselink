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
package org.eclipse.persistence.testing.tests.workbenchintegration.QueryOptions;

import java.util.Vector;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class MemoryQueryReturnNotConfirmedTest extends AutoVerifyTestCase {
    protected Employee employee;
    protected ReadAllQuery queryAll;
    protected ReadObjectQuery queryObject;
    protected java.util.Vector allEmployees;
    protected java.util.Vector inMemoryResult;

    public MemoryQueryReturnNotConfirmedTest() {
        setDescription("Test memory query ignore indirection exception return not conformed option");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        queryAll =
                (ReadAllQuery)getSession().getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Employee.class).getQueryManager().getQuery("memoryQueryReturnNotConfirmedQuery");
        allEmployees = (Vector)getSession().executeQuery(queryAll);

        queryObject = new ReadObjectQuery(Employee.class);
        queryObject.setSelectionCriteria(new ExpressionBuilder().get("address").get("city").notEqual("Montreal"));
        employee = (Employee)getSession().executeQuery(queryObject);
        if (employee != null) {
            employee.getAddress();
        }

        //    queryObject = new ReadObjectQuery(Employee.class);
        queryObject.setSelectionCriteria(new ExpressionBuilder().get("address").get("city").equal("Montreal"));
        employee = (Employee)getSession().executeQuery(queryObject);
        if (employee != null) {
            employee.getAddress();
        }
    }

    public void test() {
        ReadAllQuery queryAllCopy = (ReadAllQuery)queryAll.clone();
        queryAllCopy.checkCacheOnly(); //read from cache only
        queryAllCopy.setSelectionCriteria(new ExpressionBuilder().get("address").get("city").notEqual("Montreal"));
        inMemoryResult = (Vector)getSession().executeQuery(queryAllCopy);
    }

    public void verify() {
        if (this.inMemoryResult.size() != 1) {
            throw new TestErrorException("In Memory Query returned incorrect number of results. This is not expected to happen because of database changes");
        }
    }
}
