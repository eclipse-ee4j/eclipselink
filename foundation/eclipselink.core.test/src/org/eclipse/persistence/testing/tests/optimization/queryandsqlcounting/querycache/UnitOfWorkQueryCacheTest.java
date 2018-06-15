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
package org.eclipse.persistence.testing.tests.optimization.queryandsqlcounting.querycache;

import java.util.*;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.*;

/**
 * Test a cached query when executed in a UnitOfWork
 *
 * Ensure that all the results of a cached query are registerd when used in a UOW
 */
public class UnitOfWorkQueryCacheTest extends NamedQueryQueryCacheTest {
    protected UnitOfWork uow = null;

    public UnitOfWorkQueryCacheTest() {
        setDescription("Ensure cached queries work in a UnitOfWork.");
    }

    public void setup() {
        super.setup();
        uow = getSession().acquireUnitOfWork();
    }

    public Session getSessionForQueryTest() {
        return uow;
    }

    public void verify() {
        super.verify();
        Iterator employees = ((Vector)results).iterator();
        while (employees.hasNext()) {
            if (!((UnitOfWorkImpl)getSessionForQueryTest()).isObjectRegistered(employees.next())) {
                throw new TestErrorException("Query results were not registered in the UOW " + " after being returned from a query with cached results");
            }
        }
    }
}
