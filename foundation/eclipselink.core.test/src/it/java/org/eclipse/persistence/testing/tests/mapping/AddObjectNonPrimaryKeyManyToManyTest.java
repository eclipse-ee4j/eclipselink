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
package org.eclipse.persistence.testing.tests.mapping;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.mapping.Cubicle;
import org.eclipse.persistence.testing.models.mapping.EmergencyExit;

public class AddObjectNonPrimaryKeyManyToManyTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public Cubicle cubicle;

    @Override
    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    @Override
    public void setup() {
        if (getSession().getLogin().getPlatform().getDefaultSequence().shouldAcquireValueAfterInsert()) {
            throw new TestWarningException("This test doesn't work with 'after-insert' native sequencing");
        }
        beginTransaction();
    }

    @Override
    public void test() {
        try {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            cubicle = (Cubicle)uow.readObject(Cubicle.class);
            cubicle.emergencyExits.add(EmergencyExit.example1());
            uow.commit();
        } catch (DatabaseException exception) {
            throw new TestErrorException("CR 3819, Sent Null to database when source foreign key for many to many is not a primary key " + exception);
        }
    }

    @Override
    public void verify() {
    }
}
