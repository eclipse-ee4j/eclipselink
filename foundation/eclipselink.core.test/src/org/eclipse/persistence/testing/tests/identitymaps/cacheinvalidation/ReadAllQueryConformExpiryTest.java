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
package org.eclipse.persistence.testing.tests.identitymaps.cacheinvalidation;

import java.util.*;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

public class ReadAllQueryConformExpiryTest extends CacheExpiryTest {
    protected int originalNumber = 0;
    protected int postInvalidationNumber = 0;

    public ReadAllQueryConformExpiryTest() {
        setDescription("Test to ensure expired objects are still returned when conform results is used.");
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();

        ExpressionBuilder employees = new ExpressionBuilder();
        Expression smiths = employees.get("lastName").equal("Smith");
        Vector employeeSmiths = uow.readAllObjects(Employee.class, smiths);
        originalNumber = employeeSmiths.size();

        ExpressionBuilder emp = new ExpressionBuilder();
        Expression way = emp.get("lastName").equal("Way");
        Employee employee = (Employee)uow.readObject(Employee.class, way);
        employee.setLastName("Smith");
        uow.getIdentityMapAccessor().invalidateObject(employee);

        ReadAllQuery query = new ReadAllQuery(Employee.class, smiths);
        query.conformResultsInUnitOfWork();
        employeeSmiths = (Vector)uow.executeQuery(query);
        postInvalidationNumber = employeeSmiths.size();

        uow.release();
    }

    public void verify() {
        if ((originalNumber + 1) != postInvalidationNumber) {
            throw new TestErrorException("Conforming with invalidated items did not return the correct number of objects.");
        }
    }
}
