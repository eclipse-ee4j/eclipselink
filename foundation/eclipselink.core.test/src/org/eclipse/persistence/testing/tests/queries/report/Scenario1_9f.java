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

import java.math.BigDecimal;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

public class Scenario1_9f extends ReportQueryTestCase {
    public Scenario1_9f() {
        setDescription("SUM aggregate function with group by on gender");
    }

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

    protected void setup() throws Exception {
        super.setup();
        reportQuery = new ReportQuery(new ExpressionBuilder());

        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addAttribute("gender");
        reportQuery.addSum("salary");
        reportQuery.addGrouping("gender");
        reportQuery.addAscendingOrdering("gender");
    }

    protected void verify() {
        if (results.size() != expectedResults.size()) {
            throw new TestErrorException("ReportQuery test failed: The result size are different");
        }
        ReportQueryResult male;
        ReportQueryResult female;

        male = (ReportQueryResult)expectedResults.firstElement();
        female = (ReportQueryResult)expectedResults.lastElement();

        if (!expectedResults.firstElement().equals(male) || !expectedResults.lastElement().equals(female)) {
            throw new TestErrorException("ReportQuery test failed: The results don't match");
        }
    }
}
