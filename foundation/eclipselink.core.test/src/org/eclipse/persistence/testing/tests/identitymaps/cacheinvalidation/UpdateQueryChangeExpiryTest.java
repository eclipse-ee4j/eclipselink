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

public class UpdateQueryChangeExpiryTest extends CacheExpiryTest {
    protected boolean updateChangeFlag = false;
    protected long originalReadTime = 0;
    protected long secondReadTime = 0;
    protected Employee employee = null;
    protected boolean initialUpdateFlag;

    public UpdateQueryChangeExpiryTest(boolean updateChangeFlag) {
        setDescription("Test Update Queries which will change the Expiry for an object.");
        this.updateChangeFlag = updateChangeFlag;
    }

    public void setup() {
        super.setup();
        initialUpdateFlag =
                getSession().getDescriptor(Employee.class).getCacheInvalidationPolicy().shouldUpdateReadTimeOnUpdate();
        getSession().getDescriptor(Employee.class).getCacheInvalidationPolicy().setShouldUpdateReadTimeOnUpdate(updateChangeFlag);

        getSession().readAllObjects(Employee.class);

    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        employee = (Employee)uow.readObject(Employee.class);
        originalReadTime = ((AbstractSession)getSession()).getIdentityMapAccessorInstance().getCacheKeyForObject(employee).getReadTime();
        employee.setFirstName(employee.getFirstName() + "-mutated");
        try {
            Thread.sleep(100);
        } catch (InterruptedException exc) {
        }
        uow.commit();
        secondReadTime = getAbstractSession().getIdentityMapAccessorInstance().getCacheKeyForObject(employee).getReadTime();
    }

    public void verify() {
        if (updateChangeFlag && (originalReadTime >= secondReadTime)) {
            throw new TestErrorException("Unit of Work Update did not update the expiry time when it was supposed to.");
        } else if (!updateChangeFlag && (originalReadTime != secondReadTime)) {
            throw new TestErrorException("Unit of Work Update update expiry time when it was not supposed to.");
        }
    }

    public void reset() {
        super.reset();
        getSession().getDescriptor(Employee.class).getCacheInvalidationPolicy().setShouldUpdateReadTimeOnUpdate(initialUpdateFlag);

    }
}
