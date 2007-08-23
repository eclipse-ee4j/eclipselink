/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.framework.*;

/**
 * Test for bug 3760875: UnitOfWork.getQuery(queryName) throws ClasCastexception.
 */
public class UOWgetQueryTest extends AutoVerifyTestCase {
    public UOWgetQueryTest() {
        setDescription("Verifies that UnitOfWork.getQuery(String name) method works");
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.addQuery("test_query", new ReadObjectQuery());
        ReadObjectQuery query = (ReadObjectQuery)uow.getQuery("test_query");
    }
}