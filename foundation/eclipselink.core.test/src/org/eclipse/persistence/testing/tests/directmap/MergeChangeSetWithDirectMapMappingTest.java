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
package org.eclipse.persistence.testing.tests.directmap;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.coordination.MergeChangeSetCommand;
import org.eclipse.persistence.internal.sessions.AbstractSession;
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
public class MergeChangeSetWithDirectMapMappingTest extends TransactionalTestCase {
    boolean m_exceptionCaught;
    DirectMapMappings mapsQueryResult;

    public MergeChangeSetWithDirectMapMappingTest() {
        setDescription("Tests the merge change set over a distributed cache using a direct map mapping");
    }

    public void setup() {
        super.setup();
        m_exceptionCaught = false;
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

        UnitOfWorkChangeSet changes = (UnitOfWorkChangeSet)uow2.getCurrentChanges();
        uow2.release();

        try {
            MergeChangeSetCommand cmd = new MergeChangeSetCommand();
            cmd.setChangeSet(changes);
            ((AbstractSession)getSession()).processCommand(cmd);
        } catch (Exception e) {
            m_exceptionCaught = true;
        }

        ReadObjectQuery query = new ReadObjectQuery(mapsClone);
        query.shouldCheckCacheOnly();
        mapsQueryResult = (DirectMapMappings)getSession().executeQuery(query);
    }

    public void verify() throws Exception {
        if (m_exceptionCaught) {
            throw new TestErrorException("Merge change set into distributed cache failed with direct-map mapping");
        }

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
