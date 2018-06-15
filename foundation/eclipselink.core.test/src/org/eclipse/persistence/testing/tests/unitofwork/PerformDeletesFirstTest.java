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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TransactionalTestCase;


public class PerformDeletesFirstTest extends TransactionalTestCase {
    protected Weather weather = null;

    /**
     * Added for code coverage, this calss verifies that TopLink will find a new Object when conformed
     * on a read object with no selection criteria
     */
    public PerformDeletesFirstTest() {
        setDescription("Verifies that TopLink will Delete objects first if so instructed");
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.setShouldPerformDeletesFirst(true);
        weather = (Weather)uow.readObject(Weather.class);
        uow.deleteObject(weather);
        long id = weather.id;
        weather = new Weather();
        weather.id = id;
        weather.setStormPattern("Hurricane");
        uow.registerNewObject(weather);
        try {
            uow.commit();
        } catch (QueryException exception) {
            throw new TestErrorException("Test case failed to delete objects first");
        }
    }
    // end of test()

    public void verify() {
        ReadObjectQuery query = new ReadObjectQuery(weather);
        query.dontCheckCache();
        weather = (Weather)getSession().executeQuery(query);
        if (weather == null) {
            throw new TestErrorException("Object was not in the database after performing delete then insert with PerformDeletesFirst.");
        }
    }
    // end of verify()
}
