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
package org.eclipse.persistence.testing.tests.queries.inmemory;

import org.eclipse.persistence.queries.*;

//When query's shouldMaintainCache is true and descriptor's shouldDisableCacheHits is true,
//cache is checked and the same object is returned.
public class QueryCacheHitEnabledAndDescriptorDisabledTest extends QueryAndDescriptorCacheHitTest {
    public QueryCacheHitEnabledAndDescriptorDisabledTest() {
        setDescription("Test when cache hit is enabled in query and disabled descriptor, cache is checked");
    }

    protected void setup() {
        super.setup();
        descriptor.setShouldDisableCacheHits(true);
    }

    protected Object readObject(ReadObjectQuery query) {
        query.setCacheUsage(ObjectLevelReadQuery.CheckCacheByPrimaryKey);
        return getSession().executeQuery(query);
    }
}
