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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.collections;

import java.util.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.testing.models.collections.Restaurant;
import org.eclipse.persistence.testing.framework.*;

public class CollectionReadObjectTest extends org.eclipse.persistence.testing.framework.ReadObjectTest {
    Class collectionClass;

    public CollectionReadObjectTest() {
        super();
    }

    public CollectionReadObjectTest(Object originalObject) {
        super(originalObject);
        setDescription("The test reads the intended object from the database and checks if it was read properly");
        setName("CollectionReadObjectTest(" + ((Restaurant)originalObject).getName() + ")");
    }

    /**
     * Verify if the objects match completely through allowing the session to use the descriptors.
     * This will compare the objects and all of their privately owned parts.
     */
    protected void verify() {
        super.verify();
        getSession().logMessage("original Restaurant: " + getOriginalObject());
        getSession().logMessage("from db  Restaurant: " + this.objectFromDatabase);

        Collection waiters = ((Restaurant)this.objectFromDatabase).getWaiters();

        // test waiter collection class.
        CollectionMapping waiterMapping = (CollectionMapping)getSession().getDescriptor(Restaurant.class).getMappingForAttributeName("waiters");
        if (!waiters.getClass().equals(waiterMapping.getContainerPolicy().getContainerClass())) {
            throw new TestErrorException("The waiters collection is not of the correct type.");
        }

        // test the size of the collection of waiters.
        int origSize = ((Restaurant)getOriginalObject()).getWaiters().size();
        if (origSize != waiters.size()) {
            throw new TestErrorException("The waiters read from the database does not match the size of the original. (" + origSize + " != " + waiters.size() + ").");
        }
    }
}
