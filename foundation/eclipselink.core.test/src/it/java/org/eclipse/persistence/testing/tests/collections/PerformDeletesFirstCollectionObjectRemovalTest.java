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
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.tests.collections;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.Menu;
import org.eclipse.persistence.testing.models.collections.Restaurant;

import java.util.Collection;

/*
 * Test that removing a child object from a parent 1:M collection in a UoW also removes
 * the child object from the original parent's 1:M collection. This scenario is executed
 * with uow.setShouldPerformDeletesFirst(true) and uow.setShouldPerformDeletesFirst(false)
 * @author dminsky
 */
public class PerformDeletesFirstCollectionObjectRemovalTest extends TestCase {

    protected int startSize;
    protected int endSize;
    protected boolean shouldPerformDeletesFirst;
    protected boolean privateOwnedSetting;
    protected OneToManyMapping menusMapping;

    public PerformDeletesFirstCollectionObjectRemovalTest(boolean shouldPerformDeletesFirst) {
        super();
        setDescription("Test removing an element from a collection with UoW shouldPerformDeletesFirst");
        this.shouldPerformDeletesFirst = shouldPerformDeletesFirst;
    }

    @Override
    protected void setup() {
        menusMapping = (OneToManyMapping)getSession().getDescriptor(
            Restaurant.class).getMappingForAttributeName("menus");
        privateOwnedSetting = menusMapping.isPrivateOwned();
        menusMapping.setIsPrivateOwned(false); // switch private owned off for programmatic delete
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        beginTransaction();
    }

    @Override
    protected void test() {
        Restaurant restaurantOriginal = (Restaurant)getSession().readObject(
            Restaurant.class,
            new ExpressionBuilder().get("name").equal("Chez Abuse"));
        startSize = restaurantOriginal.getMenus().size();

        UnitOfWork uow = getSession().acquireUnitOfWork();

        Restaurant restaurantClone = (Restaurant) uow.registerObject(restaurantOriginal);

        assertNotNull(restaurantClone.getMenus());
        Collection menus = restaurantClone.getMenus().values();
        assertFalse(menus.isEmpty());

        Menu menuClone = (Menu)menus.toArray()[0];
        assertNotNull(menuClone);

        restaurantClone.setName("Chez Snooty"); // force an update of the parent
        restaurantClone.removeMenu(menuClone);

        assertFalse(restaurantClone.getMenus().containsValue(menuClone));

        uow.deleteObject(menuClone);
        uow.setShouldPerformDeletesFirst(shouldPerformDeletesFirst);
        uow.commit();

        endSize = restaurantOriginal.getMenus().size();
    }

    @Override
    protected void verify() {
        // we remove 1 element from the collection, so expected size is startSize - 1
        if (endSize != (startSize - 1)) {
            String buffer = "Parent collection was not updated correctly." +
                    System.lineSeparator() +
                    "Start size: " +
                    startSize +
                    " End size: " +
                    endSize +
                    " Perform deletes first: " +
                    this.shouldPerformDeletesFirst;
            throw new TestErrorException(buffer);
        }
    }

    @Override
    public void reset() {
        rollbackTransaction();
        menusMapping.setIsPrivateOwned(privateOwnedSetting);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

}
