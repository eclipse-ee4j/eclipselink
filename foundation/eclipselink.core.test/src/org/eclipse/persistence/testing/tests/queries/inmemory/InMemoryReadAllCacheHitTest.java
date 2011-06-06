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
package org.eclipse.persistence.testing.tests.queries.inmemory;

import org.eclipse.persistence.queries.*;

/**
 * Test in-memory querying.
 */
public class InMemoryReadAllCacheHitTest extends CacheHitTest {
    protected ReadAllQuery query;
    protected int size;

    public InMemoryReadAllCacheHitTest(ReadAllQuery query, int size) {
        this.query = query;
        this.size = size;
        setDescription("Test cache hits for in-memory querying.");
    }

    /**
     * Load the object into the cache.
     */
    protected void loadObjectIntoCache() {
        objectToRead = getSession().executeQuery(((ReadAllQuery)this.query.clone()));
    }

    protected Object readObject() {
        return getSession().executeQuery(this.query);
    }

    protected void verify() {
        if (((java.util.Vector)objectRead).size() != ((java.util.Vector)this.objectToRead).size()) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("Expecting: " + this.size + " retrieved: " + ((java.util.Vector)objectRead).size());
        }

        if (tempStream.toString().length() > 0) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("The read went to the database, but should not have, '" + tempStream.toString() + "'");
        }
    }
}
