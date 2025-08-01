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

public class Scenario1_9i extends ReportQueryTestCase {
    public Scenario1_9i() {
        setDescription("MIN aggregate function with group by on gender");
    }

    @Override
    protected void buildExpectedResults() {
        Vector employees = getSession().readAllObjects(Employee.class);
        BigDecimal minMale = new BigDecimal(Integer.MAX_VALUE);
        BigDecimal minFemale = new BigDecimal(Integer.MAX_VALUE);

        for (Enumeration e = employees.elements(); e.hasMoreElements();) {
            Employee emp = (Employee)e.nextElement();
            BigDecimal tmp;

            if (emp.getGender().equals("Male")) {
                if ((tmp = new BigDecimal(emp.getSalary())).compareTo(minMale) < 0) {
                    minMale = tmp;
                }
            } else {
                if ((tmp = new BigDecimal(emp.getSalary())).compareTo(minFemale) < 0) {
                    minFemale = tmp;
                }
            }
        }

        addResult(new Object[] { "Male", minMale }, null);
        addResult(new Object[] { "Female", minFemale }, null);
    }
@Override
protected void setup()  throws Exception
{
        super.setup();
        reportQuery = new ReportQuery(new ExpressionBuilder());

        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addAttribute("gender");
        reportQuery.addSum("salary");
        reportQuery.addGrouping("gender");
    }

    @Override
    protected void verify() {
        if (results.size() != expectedResults.size()) {
            throw new TestErrorException("ReportQuery test failed: The result size are different");
        }
        ReportQueryResult male;
        ReportQueryResult female;

        male = (ReportQueryResult)expectedResults.get(0);
        if (male.getByIndex(0).equals("Male")) {
            female = (ReportQueryResult)expectedResults.lastElement();
        } else {
            female = male;
            male = (ReportQueryResult)expectedResults.lastElement();
        }
        if (!expectedResults.get(0).equals(male) || !expectedResults.lastElement().equals(female)) {
            throw new TestErrorException("ReportQuery test failed: The results don't match");
        }
    }
}
