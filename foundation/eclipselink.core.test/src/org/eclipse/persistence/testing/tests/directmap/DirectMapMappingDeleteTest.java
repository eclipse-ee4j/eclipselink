/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.directmap;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.directmap.DirectMapMappings;

/**
 * Tests that objects deleted from the properties object in a DirectMapMapping
 * are properly removed.
 * BUG# 2992291
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date June 12, 2003
 */
public class DirectMapMappingDeleteTest extends AutoVerifyTestCase {
    DirectMapMappings queryResult;

    public DirectMapMappingDeleteTest() {
        setDescription("Tests that objects deleted from the properties object in a DirectMapMapping are properly removed.");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        rollbackTransaction();
    }

    public void setup() throws Exception {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        beginTransaction();
    }

    public void test() throws Exception {
        // Create a directmapmapping with a few items in it
        UnitOfWork uow = getSession().acquireUnitOfWork();
        DirectMapMappings maps1 = (DirectMapMappings)uow.registerObject(new DirectMapMappings());
        maps1.directMap.put(new Integer(1), "guy");
        maps1.directMap.put(new Integer(2), "axemen");
        uow.commit();

        // Read the same directmapping back and delete an item from it
        UnitOfWork uow2 = getSession().acquireUnitOfWork();
        DirectMapMappings maps2 = (DirectMapMappings)uow2.readObject(DirectMapMappings.class);
        maps2.directMap.remove(new Integer(2));
        uow2.commit();

        // Clear the cache
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        // Execute query to read back the directmapmapping from the database. 
        // Ensure the item was removed from the database
        queryResult = (DirectMapMappings)getSession().executeQuery(new ReadObjectQuery(DirectMapMappings.class));
    }

    public void verify() throws Exception {
        if (queryResult.directMap.size() != 1) {
            throw new TestErrorException("Deletion from a direct map mapping failed ... object left in database");
        }
    }
}
