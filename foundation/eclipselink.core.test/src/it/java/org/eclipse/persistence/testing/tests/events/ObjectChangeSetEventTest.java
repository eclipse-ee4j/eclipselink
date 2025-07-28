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
package org.eclipse.persistence.testing.tests.events;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.events.Customer;

public class ObjectChangeSetEventTest extends EventHookTestCase {
    public Customer customer;

    @Override
    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        beginTransaction();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        this.customer = Customer.example1();
        uow.registerObject(this.customer);
        uow.commit();
    }

    @Override
    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Customer cloneCustomer = (Customer)uow.readObject(this.customer);
        cloneCustomer.preWrite = true;
        uow.commit();
    }

    @Override
    public void verify() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        Customer originalCustomer = (Customer)getSession().readObject(this.customer);
        if (!originalCustomer.name.equals("PreWrite")) {
            throw new TestErrorException("Failed to cause update when attribute was changed in the pre-write event");
        }
    }
}
