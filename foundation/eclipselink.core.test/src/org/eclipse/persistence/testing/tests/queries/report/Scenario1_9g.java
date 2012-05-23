/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.math.BigDecimal;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class Scenario1_9g extends ReportQueryTestCase {
    public Scenario1_9g() {
        setDescription("AVG aggregate function with group by on gender");
    }

    protected void buildExpectedResults() {
        Vector employees = getSession().readAllObjects(Employee.class);
        BigDecimal maleSum = new BigDecimal(0);
        BigDecimal femaleSum = new BigDecimal(0);
        int numMale = 0;
        int numFemale = 0;

        for (Enumeration e = employees.elements(); e.hasMoreElements(); ) {
            Employee emp = (Employee)e.nextElement();
            if (emp.getGender().equals("Male")) {
                maleSum = maleSum.add(new BigDecimal(emp.getSalary()));
                numMale++;
            } else {
                femaleSum = femaleSum.add(new BigDecimal(emp.getSalary()));
                numFemale++;
            }
        }

        addResult(new Object[] { "Male", maleSum.divide(new BigDecimal(numMale), BigDecimal.ROUND_HALF_UP) }, 
                  null);
        addResult(new Object[] { "Female", femaleSum.divide(new BigDecimal(numMale), BigDecimal.ROUND_HALF_UP) }, 
                  null);
    }

    protected void setup() throws Exception {
        super.setup();
        reportQuery = new ReportQuery(new ExpressionBuilder());

        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addAttribute("gender");
        reportQuery.addAverage("salary");
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
