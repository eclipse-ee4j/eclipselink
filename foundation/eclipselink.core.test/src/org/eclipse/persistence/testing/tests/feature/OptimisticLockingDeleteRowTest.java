/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.exceptions.*;

/**
 * Test the optimistic locking feature by removing an underlying row from
 * the database.
 */
public class OptimisticLockingDeleteRowTest extends AutoVerifyTestCase {
    protected Object originalObject;

    public OptimisticLockingDeleteRowTest() {
        setDescription("This test verifies that an optimistic lock exception is thrown when underlying database row is delete");
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    protected void setup() {
        beginTransaction();

        Employee guy = new org.eclipse.persistence.testing.models.employee.domain.Employee();
        guy.setFirstName("guy");
        getDatabaseSession().writeObject(guy);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        originalObject = getSession().readObject(guy);
    }

    public void test() {
        // Delete the row from the database
        getSession().executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("DELETE FROM SALARY WHERE EMP_ID = (Select EMP_ID from EMPLOYEE where F_NAME = 'guy')"));
        getSession().executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("DELETE FROM EMPLOYEE WHERE F_NAME = 'guy'"));
    }

    protected void verify() {
        boolean exceptionCaught = false;

        try {
            getDatabaseSession().deleteObject(originalObject);
        } catch (OptimisticLockException exception) {
            exceptionCaught = true;
        }

        if (!exceptionCaught) {
            throw new TestErrorException("No Optimistic Lock exception was thrown");
        }
    }
}
