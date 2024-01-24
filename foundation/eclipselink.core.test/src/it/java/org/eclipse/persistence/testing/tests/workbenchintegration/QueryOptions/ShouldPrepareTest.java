/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.workbenchintegration.QueryOptions;

import java.util.Vector;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestWarningException;


/**
 * If SQL is generated only once, sql string should be (t0.F_NAME = NULL) when the query is executed the second time
 * with argument null.  If SQL is generated every time when the query is executed, sql string should be (t0.F_NAME is NULL).
 */
public class ShouldPrepareTest extends AutoVerifyTestCase {
    private ReadObjectQuery query;
    private ReadObjectQuery queryCopy = new ReadObjectQuery();
    private Vector vec = new Vector();

    public ShouldPrepareTest() {
        setDescription("Test SQL prepared once option");
    }

    @Override
    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    @Override
    public void setup() {
        if (getSession().getPlatform().isDB2()) {
            throw new TestWarningException("This test is not supposed to work with DB2. " +
                                           System.lineSeparator() +
                                           "\t\tBecause as expected, sql string contains (t0.F_NAME = NULL) when the query is executed the second time with argument null, and '=NULL' is illegal syntax on DB2." +
                                           System.lineSeparator() +
                                           "\t\tHence, executing the query would result in runtime exception.");
        }
        query =
                (ReadObjectQuery)getSession().getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Employee.class).getQueryManager().getQuery("shouldPrepareQuery");

        queryCopy = (ReadObjectQuery)query.clone();
        ExpressionBuilder ex = new ExpressionBuilder();
        queryCopy.setSelectionCriteria(ex.get("firstName").equal(ex.getParameter("firstName1")));
        queryCopy.addArgument("firstName1");

        vec = new Vector();
        vec.add("Bob");
        getSession().executeQuery(queryCopy, vec);
    }

    @Override
    public void test() {
        vec.set(0, null);
        getSession().executeQuery(queryCopy, vec);
    }

    @Override
    public void verify() {
        if (queryCopy.getCall().getSQLString().contains("NULL")) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("shouldPrepareTest failed, SQL was:" +
                                                                          queryCopy.getCall().getSQLString());
        }
    }
}
