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
package org.eclipse.persistence.testing.tests.optimisticlocking;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.optimisticlocking.RockBand;
import org.eclipse.persistence.testing.framework.*;

/**
 * Performs two updates on an object using ChangedFieldsLockingPolicy - test for bug 5711476
 */
public class FieldsLockingCachedUpdateCallsTest extends TestCase {
    RockBand rockBand;

    public FieldsLockingCachedUpdateCallsTest() {
        setDescription("Performs two updates on an object using ChangedFieldsLockingPolicy - test for bug 5711476");
    }

    protected void setup() {
        rockBand = new RockBand();
        rockBand.name = "Original";
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(rockBand);
        uow.commit();
    }

    protected void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        RockBand clone = (RockBand)uow.registerObject(rockBand);
        clone.name = "New1";
        uow.commit();

        uow = getSession().acquireUnitOfWork();
        clone = (RockBand)uow.registerObject(rockBand);
        clone.name = "New2";
        // this used to throw OptomisticLockException
        uow.commit();
    }

    public void reset() {
        if (rockBand != null) {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            uow.deleteObject(rockBand);
            uow.commit();
            rockBand = null;
        }
    }
}
