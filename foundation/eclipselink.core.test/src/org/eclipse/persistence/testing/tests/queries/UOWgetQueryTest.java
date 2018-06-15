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
