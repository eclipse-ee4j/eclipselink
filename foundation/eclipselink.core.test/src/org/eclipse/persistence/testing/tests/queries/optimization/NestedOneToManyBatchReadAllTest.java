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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.queries.optimization;

import java.util.*;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.testing.models.collections.*;
import org.eclipse.persistence.testing.framework.*;

public class NestedOneToManyBatchReadAllTest extends ReadAllTest {

    public NestedOneToManyBatchReadAllTest(Class referenceClass, int originalObjectsSize) {
        super(referenceClass, originalObjectsSize);
    }

    public void reset() {
        org.eclipse.persistence.mappings.OneToManyMapping mapping = 
            (OneToManyMapping)getSession().getDescriptor(Restaurant.class).getMappingForAttributeName("menus");
        ((ReadAllQuery)mapping.getSelectionQuery()).getBatchReadAttributeExpressions().removeAllElements();

    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        // Change the default query for menus to batch read menuItems
        org.eclipse.persistence.mappings.OneToManyMapping mapping = 
            (OneToManyMapping)getSession().getDescriptor(Restaurant.class).getMappingForAttributeName("menus");
        ((ReadAllQuery)mapping.getSelectionQuery()).addBatchReadAttribute("items");

    }

    protected void test() {
        super.test();

        // Trigger the indirection (if any) on the menuItem which should do a batch query.
        Hashtable menus = (Hashtable)((Restaurant)((Vector)objectsFromDatabase).firstElement()).getMenus();
        Enumeration hashEnum = menus.elements();
        ((Menu)hashEnum.nextElement()).getItems();

    }

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
