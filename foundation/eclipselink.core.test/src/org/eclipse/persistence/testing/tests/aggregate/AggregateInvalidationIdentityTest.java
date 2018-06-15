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
package org.eclipse.persistence.testing.tests.aggregate;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.aggregate.*;

/**
 * For Bug 6033380
 * Test to ensure that an Aggregate object's identity is still maintained if the
 * parent is invalidated, and then re-read. TopLink should not re-create a new
 * instance of the aggregate in this scenario; The existing aggregate relationship
 * should be re-used.
 */
public class AggregateInvalidationIdentityTest extends AutoVerifyTestCase {

    DatabaseSession session;
    Employee originalEmployee;
    Employee refreshedEmployee;
    AddressDescription originalAddressDescription;
    AddressDescription refreshedAddressDescription;

    public AggregateInvalidationIdentityTest() {
        super();
        setDescription("Tests that identity is maintained for an aggregate, if the parent is invalidated and then re-read");
    }

    protected void setup() {
        beginTransaction();
        session = (DatabaseSession)getSession();
        UnitOfWork uow = session.acquireUnitOfWork();

        originalEmployee = Employee.example1();
        originalAddressDescription = originalEmployee.getAddressDescription();

        uow.registerObject(originalEmployee);
        uow.commit();
    }

    public void test() {
        // here's the test - invalidate the non-aggregate parent
        session.getIdentityMapAccessor().invalidateObject(originalEmployee);

        // now do a Read query to read the parent object back
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setSelectionObject(originalEmployee);

        // cache the re-read objects
        refreshedEmployee = (Employee) session.executeQuery(query);
        refreshedAddressDescription = refreshedEmployee.getAddressDescription();
    }

    public void verify() {
        // the refreshed employee must be the same object as the original object
        if (refreshedEmployee.hashCode() != originalEmployee.hashCode()) {
            throwError("Employee's identity is different from the original Employee");
        }
        // the refreshed addressdescription (aggregate) must be the same object as the original
        if (refreshedAddressDescription.hashCode() != originalAddressDescription.hashCode()) {
            throwError("AddressDescription's identity is different from the original AddressDescription");
        }
    }

    public void reset() {
        session = null;
        rollbackTransaction();
    }

}
