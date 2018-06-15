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

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Test that the read time for objects created on a unit of work is correctly set
 */
public class UnitOfWorkCreateObjectReadTimeTest extends CacheExpiryTest {

    protected Employee employee = null;

    public UnitOfWorkCreateObjectReadTimeTest() {
        setDescription("Test that the read time for objects created on a UnitOfWork is correctly set.");
    }

    public void test() {
        employee = new Employee();
        employee.setFirstName("Charley");
        employee.setLastName("Dickens");
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(employee);
        uow.commit();
    }

    public void verify() {
        if (((AbstractSession)getSession()).getIdentityMapAccessorInstance().getCacheKeyForObject(employee).getReadTime() == 0) {
            throw new TestErrorException("Objects created on a UnitOfWork do not have read time set.");
        }
    }

}
