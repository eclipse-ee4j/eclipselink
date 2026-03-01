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
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.Vector;

public class Scenario1_9f extends ReportQueryTestCase {
    public Scenario1_9f() {
        setDescription("SUM aggregate function with group by on gender");
    }

    @Override
    protected void buildExpectedResults() {
        Vector employees = getSession().readAllObjects(Employee.class);
        BigDecimal maleSum = new BigDecimal(0);
        BigDecimal femaleSum = new BigDecimal(0);

        for (Enumeration e = employees.elements(); e.hasMoreElements(); ) {
            Employee emp = (Employee)e.nextElement();
            if (emp.getGender().equals("Male")) {
                maleSum = maleSum.add(new BigDecimal(emp.getSalary()));
            } else {
                femaleSum = femaleSum.add(new BigDecimal(emp.getSalary()));
            }
        }

        addResult(new Object[] { "Female", femaleSum }, null);
        addResult(new Object[] { "Male", maleSum }, null);
    }

    @Override
    protected void setup() throws Exception {
        super.setup();
        reportQuery = new ReportQuery(new ExpressionBuilder());

        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addAttribute("gender");
        reportQuery.addSum("salary");
        reportQuery.addGrouping("gender");
        reportQuery.addAscendingOrdering("gender");
    }

    @Override
    protected void verify() {
        if (results.size() != expectedResults.size()) {
            throw new TestErrorException("ReportQuery test failed: The result size are different");
        }
        ReportQueryResult male;
        ReportQueryResult female;

        male = (ReportQueryResult)expectedResults.get(0);
        female = (ReportQueryResult)expectedResults.lastElement();

        if (!expectedResults.get(0).equals(male) || !expectedResults.lastElement().equals(female)) {
            throw new TestErrorException("ReportQuery test failed: The results don't match");
        }
    }
}
