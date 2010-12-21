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

import java.util.Vector;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.testing.models.directmap.DirectMapMappings;

/**
 * BUG# 3147183
 */
public class DirectMapMappingBatchReadTest extends AutoVerifyTestCase {
    DirectMapMappings queryResult;
    SessionEventAdapter sessionListener;
    boolean isPostExecutedQuery;

    public DirectMapMappingBatchReadTest() {
        setDescription("Tests that objects direct map mapping are batch read properly.");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        rollbackTransaction();
        getSession().getEventManager().removeListener(sessionListener);
    }

    public void setup() throws Exception {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        beginTransaction();

        sessionListener = new SessionEventAdapter() {
                    public void postExecuteQuery(SessionEvent event) {
                        isPostExecutedQuery = true;
                    }
                };
        getSession().getEventManager().addListener(sessionListener);

        // Create a directmapmapping with a few items in it
        UnitOfWork uow = getSession().acquireUnitOfWork();
        DirectMapMappings maps1 = (DirectMapMappings)uow.registerObject(new DirectMapMappings());
        maps1.directMap.put(new Integer(1), "guy");
        maps1.directMap.put(new Integer(2), "axemen");

        DirectMapMappings maps2 = (DirectMapMappings)uow.registerObject(new DirectMapMappings());
        maps2.directMap.put(new Integer(1), "steve");
        maps2.directMap.put(new Integer(2), "superman");

        uow.commit();
    }

    public void verify() throws Exception {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        Vector maps = getSession().readAllObjects(DirectMapMappings.class);

        // this should trigger the batch read for all direct map mappings 
        Object batchRead = ((DirectMapMappings)maps.elementAt(0)).directMapForBatchRead;

        isPostExecutedQuery = false;
        // access second element should not execute a query since it should have batch read
        batchRead = ((DirectMapMappings)maps.elementAt(1)).directMapForBatchRead;

        if (isPostExecutedQuery) {
            throw new TestErrorException("Batch read does not work with direct map mapping");
        }
    }
}
