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

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Run a ReadObjectQuery with checkCacheOnly() and ensure it will ignore expired objects
 */
public class ReadObjectCheckCacheOnlyExpiryTest extends CacheExpiryTest {

    protected Employee employee = null;
    protected Expression readExpression = null;

    public ReadObjectCheckCacheOnlyExpiryTest() {
        setDescription("Test ReadObjectQueries with check cache only to ensure they return the correct data.");
    }

    public void setup() {
        super.setup();
        employee = (Employee)getSession().readObject(Employee.class);
        ExpressionBuilder empBuilder = new ExpressionBuilder();
        Expression readExpression = empBuilder.get("firstName").equal(employee.getFirstName());
        readExpression = readExpression.and(empBuilder.get("lastName").equal(employee.getLastName()));
        readExpression = readExpression.and(empBuilder.get("address").get("street").equal(employee.getAddress().getStreet()));
        getAbstractSession().getIdentityMapAccessor().invalidateObject(employee.getAddress());
        getAbstractSession().getIdentityMapAccessor().invalidateObject(employee);
    }

    public void test() {
        ReadObjectQuery query = new ReadObjectQuery(Employee.class, readExpression);
        query.checkCacheOnly();
        employee = (Employee)getSession().executeQuery(query);
    }

    public void verify() {
        if (employee != null) {
            throw new TestErrorException("ReadObjectQuery does not correctly ignore expired objects when set " +
                                         "to checkCacheOnly.");
        }
    }
}
