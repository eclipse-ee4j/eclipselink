/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.queries.report;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import java.math.BigDecimal;

public class Scenario1_9i extends ReportQueryTestCase {
    public Scenario1_9i() {
        setDescription("MIN aggregate function with group by on gender");
    }

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
