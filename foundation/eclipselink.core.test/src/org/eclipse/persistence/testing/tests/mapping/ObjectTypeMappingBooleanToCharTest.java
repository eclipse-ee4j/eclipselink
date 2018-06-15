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
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.tests.mapping;

import java.util.Vector;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TransactionalTestCase;
import org.eclipse.persistence.testing.models.mapping.Peripheral;

/**
 * EL Bug 375463
 * DB exception when executing ReadAllQuery with an additional join expression on ObjectTypeMapping
 * @author dminsky
 */
public class ObjectTypeMappingBooleanToCharTest extends TransactionalTestCase {

    protected Vector<Peripheral> peripheralsRead;

    public ObjectTypeMappingBooleanToCharTest() {
        super();
        setDescription("Read Boolean->Character ObjectTypeMapping in additionalJoinCriteria");
    }

    @Override
    public void setup() {
        super.setup();
        UnitOfWork uow = getAbstractSession().acquireUnitOfWork();
        Peripheral peripheral1 = (Peripheral) uow.registerObject(new Peripheral(1));
        peripheral1.setName("peripheral-1");
        peripheral1.setValid(true);
        Peripheral peripheral2 = (Peripheral) uow.registerObject(new Peripheral(2));
        peripheral2.setName("peripheral-2");
        peripheral2.setValid(false);
        uow.commit();
    }

    @Override
    public void test() {
        peripheralsRead = getAbstractSession().readAllObjects(Peripheral.class);
    }

    @Override
    public void verify() {
        // descriptor amendment method adds additionalJoinCriteria to only read
        // peripherals with 'valid' = true (object type mapping char 'Y'/'N' on db)
        assertNotNull("peripheralsRead should not be null", peripheralsRead);
        assertEquals("One Peripheral object should have been read", 1, peripheralsRead.size());
    }

    @Override
    public void reset() {
        super.reset();
        peripheralsRead = null;
    }

}
