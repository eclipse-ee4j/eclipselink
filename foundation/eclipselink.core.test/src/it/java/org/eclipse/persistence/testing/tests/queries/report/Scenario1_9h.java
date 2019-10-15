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
package org.eclipse.persistence.testing.tests.queries.report;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import java.math.BigDecimal;

public class Scenario1_9h extends ReportQueryTestCase {
    public Scenario1_9h() {
        setDescription("MAX aggregate function with group by on gender");
    }

    protected void buildExpectedResults() {
        Vector employees = getSession().readAllObjects(Employee.class);
        BigDecimal maxMale = new BigDecimal(0);
        BigDecimal maxFemale = new BigDecimal(0);

        for (Enumeration e = employees.elements(); e.hasMoreElements();) {
            Employee emp = (Employee)e.nextElement();
            BigDecimal tmp;

            if (emp.getGender().equals("Male")) {
                if ((tmp = new BigDecimal(emp.getSalary())).compareTo(maxMale) == 1) {
                    maxMale = tmp;
                }
            } else {
                if ((tmp = new BigDecimal(emp.getSalary())).compareTo(maxFemale) == 1) {
                    maxFemale = tmp;
                }
            }
        }

        addResult(new Object[] { "Male", maxMale }, null);
        addResult(new Object[] { "Female", maxFemale }, null);
    }
protected void setup()  throws Exception
{
        super.setup();
        reportQuery = new ReportQuery(new ExpressionBuilder());

        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addAttribute("gender");
        reportQuery.addSum("salary");
        reportQuery.addGrouping("gender");
    }

    protected void verify() {
        if (results.size() != expectedResults.size()) {
            throw new TestErrorException("ReportQuery test failed: The result size are different");
        }
        ReportQueryResult male;
        ReportQueryResult female;

        male = (ReportQueryResult)expectedResults.firstElement();
        if (male.getByIndex(0).equals("Male")) {
            female = (ReportQueryResult)expectedResults.lastElement();
        } else {
            female = male;
            male = (ReportQueryResult)expectedResults.lastElement();
        }
        if (!expectedResults.firstElement().equals(male) || !expectedResults.lastElement().equals(female)) {
            throw new TestErrorException("ReportQuery test failed: The results don't match");
        }
    }
}
