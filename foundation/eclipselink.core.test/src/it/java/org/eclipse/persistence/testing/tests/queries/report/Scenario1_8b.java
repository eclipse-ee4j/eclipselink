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
package org.eclipse.persistence.testing.tests.queries.report;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Vector;

public class Scenario1_8b extends ReportQueryTestCase {
    public Scenario1_8b() {
        setDescription("Invalid item, transformation mapping (normalHours)");
    }

    @Override
    protected void buildExpectedResults() {
        // Test should throw exception, so no results expected
    }

    @Override
    protected void setup() throws Exception {
        super.setup();
        reportQuery = new ReportQuery(new ExpressionBuilder());

        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addAttribute("normalHours");
    }

    @Override
    public void test() {
        try {
            results = (Vector)getSession().executeQuery(reportQuery);
        } catch (org.eclipse.persistence.exceptions.QueryException qe) {
            results = new Vector();
            results.add(qe);
        }
    }

    @Override
    protected void verify() {
        //Expecting a QueryException...
        if (results == null || results.size() != 1 || !(results.get(0) instanceof QueryException qe)) {
            throw new TestErrorException("ReportQuery test failed: got: " + results);
        }

        if (qe.getErrorCode() != QueryException.INVALID_QUERY_ITEM) {
            throw new TestErrorException("ReportQuery test failed: got: " + qe);
        }
    }
}
