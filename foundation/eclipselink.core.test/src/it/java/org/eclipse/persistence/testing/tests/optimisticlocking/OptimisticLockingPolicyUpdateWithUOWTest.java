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
package org.eclipse.persistence.testing.tests.optimisticlocking;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.optimisticlocking.LockInObject;

/**
 * Test the optimistic locking feature by changing the write lock value on the database.
 */
public class OptimisticLockingPolicyUpdateWithUOWTest extends AutoVerifyTestCase {
    public OptimisticLockingPolicyUpdateWithUOWTest() {
        setDescription("This test verifies that an optimistic lock value is copied into the Original Object Correctly");
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        beginTransaction();
    }

    public void test() {
        try {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            LockInObject lio = (LockInObject)uow.readObject(LockInObject.class);
            lio.value = "Anything But";
            uow.commit();
        } catch (Exception ex) {
            throw new TestErrorException("Failed to merge the write lock value into the object correctly.\n " + ex.toString());
        }
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
