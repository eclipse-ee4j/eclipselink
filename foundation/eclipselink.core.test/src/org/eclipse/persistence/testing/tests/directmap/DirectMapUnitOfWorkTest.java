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
package org.eclipse.persistence.testing.tests.directmap;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.changesets.UnitOfWorkChangeSet;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.directmap.DirectMapMappings;

/**
 * @author Gordon Yorke
 * @date May 11, 2005
 */
public class DirectMapUnitOfWorkTest extends AutoVerifyTestCase {
    boolean m_exceptionCaught;
    DirectMapMappings mapsQueryResult;

    public DirectMapUnitOfWorkTest() {
        setDescription("Tests changing a DirectMap Withing a UnitOFWork");
    }

    public void setup() throws Exception {
        m_exceptionCaught = false;
        beginTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();// clears the cache and stuff?
        rollbackTransaction();
    }

    public void test() throws Exception {
        // put a new value in, will now be in the cache
        UnitOfWork uow1 = getSession().acquireUnitOfWork();
        DirectMapMappings maps = (DirectMapMappings)uow1.registerObject(new DirectMapMappings());
        maps.directMap.put(new Integer(1), "bogus");
        maps.directMap.put(new Integer(3), "third");
        uow1.commit();

        UnitOfWork uow2 = getSession().acquireUnitOfWork();
        DirectMapMappings mapsClone = (DirectMapMappings)uow2.registerObject(maps);
        mapsClone.directMap.put(new Integer(2), "axemen");
        mapsClone.directMap.put(new Integer(1), "guy");

        UnitOfWorkChangeSet changes = uow2.getCurrentChanges();
        uow2.commit();

        ReadObjectQuery query = new ReadObjectQuery(mapsClone);
        mapsQueryResult = (DirectMapMappings)getSession().executeQuery(query);
    }

    public void verify() throws Exception {
        // Some checks to ensure it actually worked as expected
        if (!mapsQueryResult.directMap.containsKey(new Integer(1))) {
            throw new TestErrorException("Change set did not merge into cache properly");
        } else if (!mapsQueryResult.directMap.get(new Integer(1)).equals("guy")) {
            throw new TestErrorException("Change set did not merge into cache properly");
        } else if (!mapsQueryResult.directMap.containsKey(new Integer(2))) {
            throw new TestErrorException("Change set did not merge into cache properly");
        } else if (!mapsQueryResult.directMap.get(new Integer(2)).equals("axemen")) {
            throw new TestErrorException("Change set did not merge into cache properly");
        } else if (!mapsQueryResult.directMap.containsKey(new Integer(3))) {
            throw new TestErrorException("Change set did not merge into cache properly");
        }
    }
}
