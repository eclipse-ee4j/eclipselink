/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
