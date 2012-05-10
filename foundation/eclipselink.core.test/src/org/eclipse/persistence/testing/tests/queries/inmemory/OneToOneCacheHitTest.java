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
package org.eclipse.persistence.testing.tests.queries.inmemory;

import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Test for cache on 1-1 traversal.
 *
 */
public class OneToOneCacheHitTest extends CacheHitTest {
    protected Employee employee;

    public OneToOneCacheHitTest() {
        setDescription("Test cache hit on a 1-1 traversal.");
    }

    /**
     * Load the object into the cache.
     */
    protected void loadObjectIntoCache() {
        employee = (Employee)getSession().readObject(Employee.class);
        objectToRead = employee.getAddress();// Find its address
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        // Read them again
        employee = (Employee)getSession().readObject(Employee.class);
        objectToRead = getSession().readObject(objectToRead);
        getSession().readAllObjects(Address.class);
    }

    /**
     * Query the object by primary key.
     */
    protected Object readObject() {
        return employee.getAddress();
    }
}
