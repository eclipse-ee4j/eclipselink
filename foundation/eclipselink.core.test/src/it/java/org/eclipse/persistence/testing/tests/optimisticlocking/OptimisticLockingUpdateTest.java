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
package org.eclipse.persistence.testing.tests.optimisticlocking;

import org.eclipse.persistence.exceptions.OptimisticLockException;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.optimisticlocking.LockObject;

/**
 * Test the optimistic locking feature by removing an underlying row from
 * the database.
 */
public class OptimisticLockingUpdateTest extends AutoVerifyTestCase {
    protected UnitOfWork uow;
    protected Object originalObject;
    protected Class<?> domainClass;
    protected boolean useUOW;

    public OptimisticLockingUpdateTest(Class<?> aClass, boolean useUnitOfWork) {
        setName(getName() + "(" + aClass + ")");
        domainClass = aClass;
        this.useUOW = useUnitOfWork;
        setDescription("This test verifies that an object gets updated properly, and that the lock value gets updated in memory");
    }

    @Override
    protected void setup() {
        beginTransaction();
        if (useUOW) {
            uow = getSession().acquireUnitOfWork();
            originalObject = uow.readObject(domainClass);
        } else {
            originalObject = getSession().readObject(domainClass);
        }
    }

    @Override
    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    @Override
    public void test() {
        ((LockObject)originalObject).value = "June is hot";
        if (useUOW) {
            uow.commit();
        } else {
            getDatabaseSession().updateObject(originalObject);
        }
    }

    @Override
    protected void verify() {
        boolean exceptionCaught = false;

        try {
            getDatabaseSession().deleteObject(originalObject);
        } catch (OptimisticLockException exception) {
            exceptionCaught = true;
        }

        if (exceptionCaught) {
            throw new TestErrorException("Lock value not updated in Identity Map");
        }
    }
}
