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
import org.eclipse.persistence.queries.UpdateObjectQuery;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Test a non-cascaded write.
 */
public class ShallowModifyTest extends TestCase {
    protected Employee employeeFromDatabase;
    protected String addressCity;
    protected Number addressId;

    /**
     * ShallowModifyTest constructor comment.
     */
    public ShallowModifyTest() {
        super();
    }

    @Override
    protected void setup() {
        beginTransaction();
        employeeFromDatabase = (Employee)getSession().readObject(Employee.class);
    }

    @Override
    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        rollbackTransaction();
    }

    @Override
    public void test() {
        Address address;

        // Modify the address
        address = employeeFromDatabase.getAddress();
        addressCity = address.getCity();
        addressId = address.getId();
        address.setCity("Chelmsford");

        // Create update query and update the
        UpdateObjectQuery query = new UpdateObjectQuery();
        query.setObject(employeeFromDatabase);
        query.dontCascadeParts();

        getSession().executeQuery(query);
    }

    @Override
    protected void verify() {
        Address address;
        Expression expression;

        expression = new ExpressionBuilder().get("id").equal(addressId.intValue());

        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        address = (Address)getSession().readObject(Address.class, expression);

        if (!(address.getCity().equals(addressCity))) {
            throw new TestErrorException("The shallow modify test failed.  The private owned relationship has been modified");
        }
    }
}
