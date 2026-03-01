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

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ReportQueryResult;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

import java.util.Enumeration;
import java.util.Vector;

/**
 * ReportQuery test for Scenario 1.1
 * SELECT F_NAME, L_NAME FROM EMPLOYEE
 */
public class AttributeConversionTest extends TestCase {
    protected ReportQuery reportQuery;

    @Override
    public void test() {
        reportQuery = new ReportQuery(new ExpressionBuilder());
        reportQuery.setReferenceClass(ReportEmployee.class);
        reportQuery.addAttribute("name");
        reportQuery.addAttribute("StartDate", reportQuery.getExpressionBuilder().get("history").get("startDate"));
        Vector results = (Vector)getSession().executeQuery(reportQuery);
        for (Enumeration result = results.elements(); result.hasMoreElements();) {
            ReportQueryResult reportQueryResult = (ReportQueryResult)result.nextElement();
            Object startDate = reportQueryResult.get("StartDate");
            if (!(startDate instanceof java.sql.Date)) {
                throw new TestErrorException("Exception thrown because Report Query attribute was not converted ; see CR 4290");
            }
        }
    }

}
