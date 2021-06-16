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

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

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

    protected void setup() {
        beginTransaction();
        employeeFromDatabase = (Employee)getSession().readObject(Employee.class);
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        rollbackTransaction();
    }

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
