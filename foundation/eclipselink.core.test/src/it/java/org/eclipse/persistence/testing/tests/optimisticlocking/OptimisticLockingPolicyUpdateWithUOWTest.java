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

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.optimisticlocking.LockInObject;

/**
 * Test the optimistic locking feature by changing the write lock value on the database.
 */
public class OptimisticLockingPolicyUpdateWithUOWTest extends AutoVerifyTestCase {
    public OptimisticLockingPolicyUpdateWithUOWTest() {
        setDescription("This test verifies that an optimistic lock value is copied into the Original Object Correctly");
    }

    @Override
    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        beginTransaction();
    }

    @Override
    public void test() {
        try {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            LockInObject lio = (LockInObject)uow.readObject(LockInObject.class);
            lio.value = "Anything But";
            uow.commit();
        } catch (Exception ex) {
            throw new TestErrorException("Failed to merge the write lock value into the object correctly.\n " + ex);
        }
    }

    @Override
    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
