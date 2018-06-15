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
package org.eclipse.persistence.testing.tests.queries.report;

import java.util.*;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.exceptions.QueryException;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class Scenario1_8d extends ReportQueryTestCase {
    public Scenario1_8d() {
        setDescription("Invalid item, 1:M mapping (phoneNumbers)");
    }

    protected void buildExpectedResults() {
        // Test should throw exception, so no results expected
    }

    protected void setup() throws Exception {
        super.setup();
        reportQuery = new ReportQuery(new ExpressionBuilder());

        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addAttribute("phoneNumbers");
    }

    public void test() {
        try {
            results = (Vector)getSession().executeQuery(reportQuery);
        } catch (org.eclipse.persistence.exceptions.QueryException qe) {
            results = new Vector();
            results.addElement(qe);
        }
    }

    protected void verify() {
        //Expecting a QueryException...
        if ((results == null) || (results.size() != 1) || !(results.firstElement() instanceof QueryException)) {
            throw new TestErrorException("ReportQuery test failed: got: " + results);
        }

        QueryException qe = (QueryException)results.firstElement();

        if (qe.getErrorCode() != QueryException.INVALID_USE_OF_TO_MANY_QUERY_KEY_IN_EXPRESSION) {
            throw new TestErrorException("ReportQuery test failed: got: " + qe);
        }
    }
}
