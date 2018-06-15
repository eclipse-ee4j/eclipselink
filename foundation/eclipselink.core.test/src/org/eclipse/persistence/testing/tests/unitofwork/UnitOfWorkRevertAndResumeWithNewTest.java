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
package org.eclipse.persistence.testing.tests.unitofwork;

import java.util.Vector;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class UnitOfWorkRevertAndResumeWithNewTest extends AutoVerifyTestCase {
    public java.math.BigDecimal addressId;

    public UnitOfWorkRevertAndResumeWithNewTest() {
    }

    protected void setup() {
        this.addressId = ((Address)getSession().readObject(Address.class)).getId();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        getAbstractSession().beginTransaction();
    }

    protected void test() {
        if (getSession().isDistributedSession()) {
            throw new TestWarningException("Test unavailable on Remote UnitOfWork");
        }
        if (getSession().getPlatform().isPostgreSQL()) {
            throwWarning("Postgres aborts transaction after error.");
        }
        UnitOfWork uow = getSession().acquireUnitOfWork();

        Vector results = uow.readAllObjects(Employee.class);

        Address address = new Address();
        address.setId(this.addressId);
        address.setStreet("Wallace");
        address.setCity("Wallace");

        address = (Address)uow.registerObject(address);

        Employee emp = (Employee)results.firstElement();
        emp.setAddress(address);
        try {
            uow.commitAndResume();
            // Exception expected because Emp with null PK
        } catch (Exception e) {
            uow.revertAndResume();
        }

        results = uow.readAllObjects(Employee.class);

        address = new Address();
        address.setStreet("Wallace2");
        address.setCity("Wallace2");
        address = (Address)uow.registerObject(address);

        emp = (Employee)results.firstElement();
        emp.setAddress(address);

        uow.commitAndResume();
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
