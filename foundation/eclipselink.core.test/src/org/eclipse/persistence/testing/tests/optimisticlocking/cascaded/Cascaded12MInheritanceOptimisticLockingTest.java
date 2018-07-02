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
//     tware - added cascaded locking testing
package org.eclipse.persistence.testing.tests.optimisticlocking.cascaded;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.optimisticlocking.Cat;
import org.eclipse.persistence.testing.models.optimisticlocking.Toy;

/**
 * EclipseLink Bug 247104
 *
 * Ensure cascaded optimistic locking is properly cascaded in inheritance
 *
 */
public class Cascaded12MInheritanceOptimisticLockingTest extends AutoVerifyTestCase {

    private Toy toy = null;
    private Cat cat = null;
    private int catVersion = 0;
    private int toyVersion = 0;

    public void reset()  {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void setup()  {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        getAbstractSession().beginTransaction();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        cat = new Cat();
        cat.setName("Bud");
        toy = new Toy();
        toy.setName("Ball");
        List toys = new ArrayList();
        toys.add(toy);
        cat.setToys(toys);
        toy.setOwner(cat);
        uow.registerObject(cat);
        uow.registerObject(toy);
        uow.commit();
        cat = (Cat)getSession().refreshObject(cat);
        toy = (Toy)getSession().refreshObject(toy);
        catVersion = cat.getVersion();
        toyVersion = toy.getVersion();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        cat = (Cat)uow.readObject(cat);
        toy = (Toy)cat.getToys().get(0);
        toy.setName("Red Ball");
        uow.commit();
    }

    public void verify(){
        cat = (Cat)getSession().refreshObject(cat);
        if (cat.getVersion() <= catVersion){
            throw new TestErrorException("Version of owner was not updated through cascading.");
        }
    }

}
