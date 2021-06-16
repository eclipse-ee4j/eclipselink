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
