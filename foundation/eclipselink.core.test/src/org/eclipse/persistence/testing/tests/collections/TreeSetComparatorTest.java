/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.collections;

import java.math.BigDecimal;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.collections.Location;
import org.eclipse.persistence.testing.models.collections.Restaurant;
import org.eclipse.persistence.testing.framework.*;

/**
 * Tests a Collection mapping that uses an TreeSet with a custom comparator.
 * That is, ensures when building new instances of a TreeSet Collection,
 * internally, the comparator is maintained and carried over from TreeSet to
 * TreeSet.
 * BUG# 3233263
 *
 * @author Guy Pelletier
 * @version 1.0 December 5/03
 */
public class TreeSetComparatorTest extends AutoVerifyTestCase {
    public UnitOfWork unitOfWork;
    private boolean m_exceptionCaught;

    public TreeSetComparatorTest() {
        super();
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        rollbackTransaction();
    }

    protected void setup() {
        m_exceptionCaught = false;

        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        beginTransaction();

        UnitOfWork deleteUow = getSession().acquireUnitOfWork();
        deleteUow.deleteAllObjects(getSession().readAllObjects(Location.class));
        deleteUow.deleteAllObjects(getSession().readAllObjects(Restaurant.class));
        deleteUow.commit();
    }

    public void test() {
        try {
            UnitOfWork uow = getSession().acquireUnitOfWork();

            Restaurant restaurantClone = (Restaurant)uow.registerObject(new Restaurant());

            restaurantClone.setName("Guy's XXX Diner");
            restaurantClone.setId(new BigDecimal(1));

            restaurantClone.getLocations2().add(Location.example1());
            restaurantClone.getLocations2().add(Location.example2());

            uow.commit();

        } catch (ClassCastException e) {
            m_exceptionCaught = true;
        }
    }

    public void verify() {
        if (m_exceptionCaught) {
            throw new TestErrorException("Class cast exception caught on TreeSet merge");
        }
    }
}
