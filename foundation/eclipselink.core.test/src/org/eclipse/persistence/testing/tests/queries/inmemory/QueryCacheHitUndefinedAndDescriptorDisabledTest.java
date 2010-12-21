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

import org.eclipse.persistence.testing.framework.*;

//When query's shouldMaintainCache is undefined and descriptor's shouldDisableCacheHits is true,
//cache is not checked, but the same object is returned.
public class QueryCacheHitUndefinedAndDescriptorDisabledTest extends QueryAndDescriptorCacheHitTest {
    public QueryCacheHitUndefinedAndDescriptorDisabledTest() {
        setDescription("Test when cache hit is undefined in query and disabled in descriptor, cache is not checked");
    }

    protected void setup() {
        super.setup();
        descriptor.setShouldDisableCacheHits(true);
    }

    protected void verify() {
        if (!((org.eclipse.persistence.testing.models.employee.domain.Employee)objectRead).getFirstName().equals(firstName)) {
            throw new TestErrorException("Expecting: " + objectToRead + " retrieved: " + objectRead);
        }

        if (tempStream.toString().length() == 0) {
            throw new TestErrorException("The read did not go to the database, but should have");
        }
    }
}
