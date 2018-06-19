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
//     09/10/2008-1.1 Chris Delahunt
//       - 244206: discoverUnregisteredNewObjects doesn't populate knownNewObjects but unregisteredExstingObjects
package org.eclipse.persistence.testing.tests.collections;

import java.math.BigDecimal;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.models.collections.Location;
import org.eclipse.persistence.testing.models.collections.Restaurant;

/**
 * BUG - 244206. Tests unregistered new objects are found in a ManyToMany relation
 *  when dontPerformValidation is specified.
 *
 */
public class CollectionInsertDetectionTest extends AutoVerifyTestCase {
    public CollectionInsertDetectionTest() {
        super();
    }

    public void reset() {
        rollbackTransaction();
    }

    protected void setup() {
        beginTransaction();
    }

    protected void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.dontPerformValidation();
        Restaurant resClone = (Restaurant)uow.readObject(Restaurant.class);

        Location location = Location.example1();
        resClone.addLocation(location);

        uow.assignSequenceNumbers();
        BigDecimal id = location.getId();
        this.assertNotNull("Location ID should be assigned", id );
        uow.commit();

        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        this.assertNotNull("Location object was not inserted", getSession().readObject(location) );
    }
}
