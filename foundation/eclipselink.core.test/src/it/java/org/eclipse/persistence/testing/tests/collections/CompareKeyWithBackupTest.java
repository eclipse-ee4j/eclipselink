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
package org.eclipse.persistence.testing.tests.collections;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.Menu;
import org.eclipse.persistence.testing.models.collections.Restaurant;

import java.util.Iterator;
import java.util.Vector;

/**
 * This test fakes an issue where if you use a perstistant object as the key in a Map
 * EclipseLink may determine that a change in the key has occurred because the clone is a different
 * object than the one in the cache.  the UnitOfWork should not deterine that a change to the
 * identityMap has occurred simply because the clone does not match the original.
 */
public class CompareKeyWithBackupTest extends TestCase {
    protected Restaurant originalRestaurant = null;
    protected String menuType;

    @Override
    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        originalRestaurant = null;
        beginTransaction();
        Vector<Restaurant> restaurants = getSession().readAllObjects(Restaurant.class);
        Iterator<Restaurant> iterator = restaurants.iterator();
        while (originalRestaurant == null && iterator.hasNext()) {
            Restaurant restaurant = iterator.next();
            if (!restaurant.getMenus().isEmpty()) {
                originalRestaurant = restaurant;
            }
        }
        if (originalRestaurant == null) {
            throw new TestErrorException("Unable to SetUp Test need to add restaurant with a menu");
        }
    }

    @Override
    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        rollbackTransaction();
    }

    @Override
    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Restaurant restaurantClone = (Restaurant)uow.registerObject(originalRestaurant);
        restaurantClone.getMenus();
        Iterator enumtr = originalRestaurant.getMenus().values().iterator();

        // has to have a menu as we checked in the setup
        Menu menu = (Menu)enumtr.next();
        menuType = menu.getType();
        menu.setType("Brunch");
        originalRestaurant.getMenus().remove(menuType);
        originalRestaurant.addMenu(menu);
        uow.commit();
    }

    @Override
    public void verify() {
        if (originalRestaurant.getMenus().containsKey(menuType)) {
            throw new TestErrorException("CR 4172 Even though the key in a Map did not change in the clone it was updated in the shared cache");
        }
    }
}
