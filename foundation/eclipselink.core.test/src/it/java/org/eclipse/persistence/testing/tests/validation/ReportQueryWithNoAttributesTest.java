/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReportQuery;


public class ReportQueryWithNoAttributesTest extends ExceptionTest {

    public ReportQueryWithNoAttributesTest() {
        super();
        setDescription("This will test the throwing of an exception when no attributes have been added");
    }

    public void setup() {
        expectedException = org.eclipse.persistence.exceptions.QueryException.noAttributesForReportQuery(null);
    }

    public void test() {

        // Create a ReportQuery with and add no attributes
        ReportQuery query = new ReportQuery();
        query.setReferenceClass(org.eclipse.persistence.testing.models.employee.domain.Employee.class);

        ExpressionBuilder employee = new ExpressionBuilder();
        Expression exp = employee.get("firstName").equalsIgnoreCase("bob");

        // Run the query.
        query.setSelectionCriteria(exp);
        try {
            Object o = getSession().executeQuery(query);
        } catch (org.eclipse.persistence.exceptions.EclipseLinkException e) {
            // This should be a QueryException.noAttributesForReportQuery() exception
            caughtException = e;
        }
    }
}
