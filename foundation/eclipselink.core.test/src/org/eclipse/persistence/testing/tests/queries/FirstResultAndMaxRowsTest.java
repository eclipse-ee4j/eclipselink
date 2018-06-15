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

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import java.util.*;

/**
 * Test to ensure combinations of firstResult and maxRows settings return the
 * correct number of values.
 */
public class FirstResultAndMaxRowsTest extends AutoVerifyTestCase {
    protected int firstResult = 0;
    protected int maxRows = 0;
    protected int expectedResults = 0;
    protected int results = 0;

    public FirstResultAndMaxRowsTest(int firstResult, int maxRows, int expectedResults) {
        this.firstResult = firstResult;
        this.maxRows = maxRows;
        this.expectedResults = expectedResults;
        setName("FirstResultAndMaxRowsTest " + firstResult + "," + maxRows);
    }

    public void setup() {
        if (getSession().getPlatform().isTimesTen()) {
            throw new TestWarningException("This test is not supported on TimesTen");
        }

        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void test() {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.setMaxRows(maxRows);
        query.setFirstResult(firstResult);
        results = ((Vector)getSession().executeQuery(query)).size();
    }

    public void verify() {
        if (results != expectedResults) {
            throw new TestErrorException("Expected " + expectedResults + " and returned " + results + " results. When using maxResults = " + maxRows + "and firstResult = " + firstResult + ".");
        }
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }
}
