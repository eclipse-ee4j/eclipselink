/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TransactionalTestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

public class NamedQueryNotFoundInUOWTest extends TransactionalTestCase {
    DatabaseQuery query;

    /**
    * Employee.class used to add two identically named NamedQueries
    */
    public NamedQueryNotFoundInUOWTest() {
        setDescription("Verifies if a Named Query Can be found from a UnitOfWork");
    }

    @Override
    public void reset() {
        super.reset();
        // do not want to keep named queries on serverSession
        getSession().removeQuery("someQueryForThisTest_NamedQueryNotFoundInUOWTest");
    }

    @Override
    public void setup() {
        super.setup();
        getSession().addQuery("someQueryForThisTest_NamedQueryNotFoundInUOWTest", new ReadObjectQuery(Employee.class));
    }

    // end of setup()
    @Override
    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        query = uow.getQuery("someQueryForThisTest_NamedQueryNotFoundInUOWTest");
    }

    // end of test()
    @Override
    public void verify() {
        if (query == null) {
            throw new TestErrorException("Named Query not found from UnitOfWork");
        }
    }
    // end of verify()
}
