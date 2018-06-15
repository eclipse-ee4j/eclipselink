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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.exceptions.*;

/**
 * Test the optimistic locking feature by changing the write lock value on
 * the database.
 */
public class OptimisticLockingChangedValueUpdateTest extends OptimisticLockingDeleteRowTest {
    public OptimisticLockingChangedValueUpdateTest() {
        setDescription("This test verifies that an optimistic lock exception is thrown on update when the write lock is changed");
    }

    protected void verify() {
        boolean exceptionCaught = false;

        try {
            getDatabaseSession().updateObject(originalObject);
        } catch (OptimisticLockException exception) {
            exceptionCaught = true;
        }

        if (!exceptionCaught) {
            throw new TestErrorException("No Optimistic Lock exception was thrown");
        }
    }
}
