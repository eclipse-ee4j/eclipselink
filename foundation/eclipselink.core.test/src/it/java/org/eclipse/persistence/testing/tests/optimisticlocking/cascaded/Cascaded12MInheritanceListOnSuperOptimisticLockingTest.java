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
//     tware - added cascaded locking testing
package org.eclipse.persistence.testing.tests.optimisticlocking.cascaded;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.optimisticlocking.Cat;
import org.eclipse.persistence.testing.models.optimisticlocking.VetAppointment;

import java.util.ArrayList;
import java.util.List;

/**
 * Bug 270017 - Test cascaded locking on a subclass when a list attribute of it's superclass is updated
 * @author tware
 *
 */
public class Cascaded12MInheritanceListOnSuperOptimisticLockingTest extends AutoVerifyTestCase {

    private VetAppointment appt = null;
    private Cat cat = null;
    private int catVersion = 0;
    private int apptVersion = 0;

    @Override
    public void setup()  {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        getAbstractSession().beginTransaction();
    }

    @Override
    public void test(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        cat = new Cat();
        cat.setName("Bud");
        appt = new VetAppointment();
        appt.setCost(100);
        List appts = new ArrayList();
        appts.add(appt);
        appt.getAnimal().setValue(cat);
        uow.registerObject(cat);
        uow.registerObject(appt);
        uow.commit();
        cat = (Cat)getSession().refreshObject(cat);
        appt = (VetAppointment)getSession().refreshObject(appt);
        catVersion = cat.getVersion();
        apptVersion = appt.getVersion();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        uow = getSession().acquireUnitOfWork();
        cat = (Cat)uow.readObject(cat);
        appt = cat.getAppointments().get(0);
        appt.setCost(99);
        uow.commit();
    }

    @Override
    public void verify(){
        cat = (Cat)getSession().refreshObject(cat);
        if (cat.getVersion() <= catVersion){
            throw new TestErrorException("Version of owner was not updated through cascading.");
        }
    }

    @Override
    public void reset()  {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

}
