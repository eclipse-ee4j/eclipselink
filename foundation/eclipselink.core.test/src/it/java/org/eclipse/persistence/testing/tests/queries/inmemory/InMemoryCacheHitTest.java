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
package org.eclipse.persistence.testing.tests.queries.inmemory;

import org.eclipse.persistence.queries.ReadObjectQuery;

/**
 * Test in-memory querying.
 */
public class InMemoryCacheHitTest extends CacheHitTest {
    protected ReadObjectQuery query;

    public InMemoryCacheHitTest(ReadObjectQuery query) {
        this.query = query;
        setDescription("Test cache hits for in-memory querying.");
    }

    /**
     * Load the object into the cache.
     */
    @Override
    protected void loadObjectIntoCache() {
        ReadObjectQuery query = (ReadObjectQuery)this.query.clone();
        query.dontCheckCache();
        objectToRead = getSession().executeQuery(query);
    }

    @Override
    protected Object readObject() {
        return getSession().executeQuery(this.query);
    }
}
