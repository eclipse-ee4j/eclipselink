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
package org.eclipse.persistence.testing.tests.directmap;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.directmap.DirectMapMappings;

/**
 * Tests that null objects within a HashMap do not throw a null pointer
 * exception.
 * BUG# 4364944
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date July 26, 2005
 */
public class DirectMapMappingHashMapTest extends AutoVerifyTestCase {
    private NullPointerException m_exception;
    private DirectMapMappings queryResult;

    public DirectMapMappingHashMapTest() {
        setDescription("Tests that nulls are properly supported for DirectMapMappings that use a HashMap.");
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
        // Create a hashmap with a null in it.
        UnitOfWork uow = getSession().acquireUnitOfWork();
        DirectMapMappings maps = (DirectMapMappings)uow.registerObject(new DirectMapMappings());
        maps.directHashMap.put(new Integer(1), "item1");
        maps.directHashMap.put(new Integer(2), "item2");
        maps.directHashMap.put(new Integer(3), null);

        try {
            uow.commit();
        } catch (NullPointerException e) {
            m_exception = e;
        }

        // Clear the cache
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        // Execute query to read back the directmapmapping from the database. 
        queryResult = (DirectMapMappings)getSession().executeQuery(new ReadObjectQuery(DirectMapMappings.class));
    }

    public void verify() throws Exception {
        if (m_exception != null) {
            throw new TestErrorException("Null pointer exception was caught.");
        }

        if (queryResult.directHashMap.size() != 3) {
            throw new TestErrorException("Incorrect amount of items in the hashmap.");
        }

        if (queryResult.directHashMap.get(new Integer(3)) != null) {
            throw new TestErrorException("The null value was not read back in correctly.");
        }
    }
}
