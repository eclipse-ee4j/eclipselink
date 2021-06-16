/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.queries.inmemory;

import org.eclipse.persistence.queries.*;

/**
 * Test in-memory querying.
 */
public class InMemoryCacheMissTest extends CacheMissTest {
    protected ReadObjectQuery query;

    public InMemoryCacheMissTest(ReadObjectQuery query) {
        this.query = query;
        setDescription("Test cache miss for in-memory querying.");
    }

    /**
     * Load the object into the cache.
     */
    protected void loadObjectIntoCache() {
        ReadObjectQuery query = (ReadObjectQuery)this.query.clone();
        query.dontCheckCache();
        objectToRead = getSession().executeQuery(query);
    }

    protected Object readObject() {
        return getSession().executeQuery(this.query);
    }

    protected void verify() {
        if (tempStream.toString().length() == 0) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("Cache hit occurred, but should not have.");
        }
    }
}
