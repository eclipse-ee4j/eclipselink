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
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ReportQueryResult;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.Vector;

public class Scenario1_9b extends ReportQueryTestCase {
    public Scenario1_9b() {
        setDescription("SUM aggregate function");
    }

    @Override
    protected void buildExpectedResults() {
        Vector employees = getSession().readAllObjects(Employee.class);
        BigDecimal sum = new BigDecimal(0);

        for (Enumeration e = employees.elements(); e.hasMoreElements();) {
            Employee emp = (Employee)e.nextElement();
            sum = sum.add(new BigDecimal(emp.getSalary()));
        }

        addResult(new Object[] { sum }, null);
    }
@Override
protected void setup()  throws Exception
{
        super.setup();
        reportQuery = new ReportQuery(new ExpressionBuilder());

        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addSum("salary");
    }

    @Override
    protected void verify() {
        if (results.size() != expectedResults.size()) {
            throw new TestErrorException("ReportQuery test failed: The result size are different");
        }
        BigDecimal expected = (BigDecimal)((ReportQueryResult)expectedResults.get(0)).getByIndex(0);
        BigDecimal result = ConversionManager.getDefaultManager().convertObject(((ReportQueryResult)results.get(0)).getByIndex(0), BigDecimal.class);
        if (!Helper.compareBigDecimals(expected, result)) {
            throw new TestErrorException("ReportQuery test failed: The results did not match (" + expected + ", " + result + ")");
        }
    }
}
