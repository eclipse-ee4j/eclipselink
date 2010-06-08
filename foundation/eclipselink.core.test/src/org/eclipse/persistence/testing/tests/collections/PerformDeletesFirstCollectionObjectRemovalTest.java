/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dminsky - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.collections;

import java.util.*;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.helper.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.collections.*;

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

    protected void setup() {
        menusMapping = (OneToManyMapping)getSession().getDescriptor(
            Restaurant.class).getMappingForAttributeName("menus");
        privateOwnedSetting = menusMapping.isPrivateOwned();
        menusMapping.setIsPrivateOwned(false); // switch private owned off for programmatic delete
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        beginTransaction();
    }

    protected void test() {
        Restaurant restaurantOriginal = (Restaurant)getSession().readObject(
            Restaurant.class,
            new ExpressionBuilder().get("name").equal("Chez Abuse"));
        startSize = restaurantOriginal.getMenus().size();
                
        UnitOfWork uow = getSession().acquireUnitOfWork();

        Restaurant restaurantClone = (Restaurant) uow.registerObject(restaurantOriginal);
        
        assertTrue(restaurantClone.getMenus() != null);
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
    
    protected void verify() {
        // we remove 1 element from the collection, so expected size is startSize - 1
        if (endSize != (startSize - 1)) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("Parent collection was not updated correctly.");
            buffer.append(Helper.cr());
            buffer.append("Start size: ");
            buffer.append(startSize);
            buffer.append(" End size: ");
            buffer.append(endSize);
            buffer.append(" Perform deletes first: ");
            buffer.append(this.shouldPerformDeletesFirst);
            throw new TestErrorException(buffer.toString());
        }
    }

    public void reset() {
        rollbackTransaction();
        menusMapping.setIsPrivateOwned(privateOwnedSetting);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

}
