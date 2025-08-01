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
package org.eclipse.persistence.testing.tests.queries.optimization;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.ReadAllTest;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.Menu;
import org.eclipse.persistence.testing.models.collections.MenuItem;
import org.eclipse.persistence.testing.models.collections.Restaurant;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class NestedOneToManyBatchReadAllTest extends ReadAllTest {

    public NestedOneToManyBatchReadAllTest(Class<?> referenceClass, int originalObjectsSize) {
        super(referenceClass, originalObjectsSize);
    }

    @Override
    public void reset() {
        org.eclipse.persistence.mappings.OneToManyMapping mapping =
            (OneToManyMapping)getSession().getDescriptor(Restaurant.class).getMappingForAttributeName("menus");
        ((ReadAllQuery)mapping.getSelectionQuery()).getBatchReadAttributeExpressions().clear();

    }

    @Override
    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        // Change the default query for menus to batch read menuItems
        org.eclipse.persistence.mappings.OneToManyMapping mapping =
            (OneToManyMapping)getSession().getDescriptor(Restaurant.class).getMappingForAttributeName("menus");
        ((ReadAllQuery)mapping.getSelectionQuery()).addBatchReadAttribute("items");

    }

    @Override
    protected void test() {
        super.test();

        // Trigger the indirection (if any) on the menuItem which should do a batch query.
        Hashtable menus = (Hashtable)((Restaurant)((Vector)objectsFromDatabase).get(0)).getMenus();
        Enumeration hashEnum = menus.elements();
        ((Menu)hashEnum.nextElement()).getItems();

    }

    @Override
    public void verify() {

        // Check the identity map and count that all the MenuItems got read in.
        org.eclipse.persistence.internal.identitymaps.IdentityMap menuItemIM =
            ((AbstractSession)getSession()).getIdentityMapAccessorInstance().getIdentityMap(MenuItem.class);
        if (menuItemIM.getSize() != getOriginalObjectsSize()) {
            throw new TestErrorException("The number of menuItems read into the cache (" + menuItemIM.getSize() +
                                         ") does not match the number in the database (" +
                                         getOriginalObjectsSize() + ").");
        }
    }
}
