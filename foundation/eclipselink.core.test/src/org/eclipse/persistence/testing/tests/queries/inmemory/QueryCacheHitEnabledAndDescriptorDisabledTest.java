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
