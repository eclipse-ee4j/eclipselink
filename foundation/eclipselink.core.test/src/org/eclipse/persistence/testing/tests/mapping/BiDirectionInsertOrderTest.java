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
package org.eclipse.persistence.testing.tests.mapping;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.mapping.Baby;
import org.eclipse.persistence.testing.models.mapping.BabyMonitor;
import org.eclipse.persistence.testing.models.mapping.Crib;

public class BiDirectionInsertOrderTest extends AutoVerifyTestCase {
    public BiDirectionInsertOrderTest() {
        setDescription("Test configurable bidirectional insert order.");
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void setup() {
        beginTransaction();
    }

    protected void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();

        Baby baby = new Baby();
        baby.setName("Amy");

        BabyMonitor monitor = new BabyMonitor();
        monitor.setBrandName("Fisher Price");
        monitor.setBaby(baby);

        Crib crib = new Crib();
        crib.setColor("white");
        crib.setBabyMonitor(monitor);
        crib.setBaby(baby);

        monitor.setCrib(crib);

        baby.setBabyMonitor(monitor);
        baby.setCrib(crib);

        uow.registerObject(baby);

        uow.commit();
    }
}
