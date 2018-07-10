/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.directmap;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.directmap.*;

/**
 * Tests the merge change set into a distributed cache with a direct map mapping
 * BUG# 2783391
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date March 04, 2003
 */
public class MergeChangeSetWithIndirectDirectMapMappingTest extends TransactionalTestCase {
    boolean m_exceptionCaught;
    DirectMapMappings mapsQueryResult;

    public MergeChangeSetWithIndirectDirectMapMappingTest() {
        setDescription("Tests the merge change set over a distributed cache using a indirect direct map mapping");
    }

    public void setup() {
        m_exceptionCaught = false;
        super.setup();
    }

    public void test() throws Exception {
        // put a new value in, will now be in the cache
        UnitOfWork uow1 = getSession().acquireUnitOfWork();
        DirectMapMappings maps = (DirectMapMappings)uow1.registerObject(new DirectMapMappings());
        maps.indirectionDirectMap.put(new Integer(1), "bogus");
        maps.indirectionDirectMap.put(new Integer(3), "third");
        uow1.commit();

        UnitOfWork uow2 = getSession().acquireUnitOfWork();
        DirectMapMappings mapsClone = (DirectMapMappings)uow2.registerObject(maps);
        mapsClone.indirectionDirectMap.put(new Integer(2), "axemen");
        mapsClone.indirectionDirectMap.put(new Integer(1), "guy");

        UnitOfWorkChangeSet changes = (UnitOfWorkChangeSet)uow2.getCurrentChanges();

        getSession().refreshObject(mapsClone);
        uow2.commit();

        ReadObjectQuery query = new ReadObjectQuery(mapsClone);
        query.shouldCheckCacheOnly();
        mapsQueryResult = (DirectMapMappings)getSession().executeQuery(query);
    }

    public void verify() throws Exception {
        if (m_exceptionCaught) {
            throw new TestErrorException("Merge change set into distributed cache failed with direct-map mapping");
        }

        // Some checks to ensure it actually worked as expected

        if (!mapsQueryResult.indirectionDirectMap.containsKey(new Integer(1))) {
            throw new TestErrorException("Change set did not merge into cache properly");
        } else if (!mapsQueryResult.indirectionDirectMap.get(new Integer(1)).equals("guy")) {
            throw new TestErrorException("Change set did not merge into cache properly");
        } else if (!mapsQueryResult.indirectionDirectMap.containsKey(new Integer(2))) {
            throw new TestErrorException("Change set did not merge into cache properly");
        } else if (!mapsQueryResult.indirectionDirectMap.get(new Integer(2)).equals("axemen")) {
            throw new TestErrorException("Change set did not merge into cache properly");
        } else if (!mapsQueryResult.indirectionDirectMap.containsKey(new Integer(3))) {
            throw new TestErrorException("Change set did not merge into cache properly");
        }
    }
}
