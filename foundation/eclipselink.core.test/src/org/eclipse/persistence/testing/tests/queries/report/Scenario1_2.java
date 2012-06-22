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

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * ReportQuery test for Scenario 1.1
 * SELECT F_NAME, L_NAME FROM EMPLOYEE
 */
public class Scenario1_2 extends ReportQueryTestCase {
    public Scenario1_2() {
        setDescription("DTF mappings with 1 Class & 2 Tables");
    }

    protected void buildExpectedResults() {
        Vector employees = getSession().readAllObjects(Employee.class);

        for (Enumeration e = employees.elements(); e.hasMoreElements(); ) {
            Employee emp = (Employee)e.nextElement();
            if ((emp.getSalary() > 40000) && (emp.getSalary() < 100000)) {
                Object[] result = new Object[3];
                result[0] = emp.getFirstName();
                result[1] = emp.getLastName();
                result[2] = new Integer(emp.getSalary());
                addResult(result, null);
            }
        }
    }

    protected void setup() throws Exception {
        super.setup();
        reportQuery = new ReportQuery(new ExpressionBuilder());

        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addAttribute("firstName");
        reportQuery.addAttribute("lastName");
        reportQuery.addAttribute("salary");

        Expression exp1 = reportQuery.getExpressionBuilder().get("salary").greaterThan(40000);
        Expression exp2 = reportQuery.getExpressionBuilder().get("salary").lessThan(100000);
        reportQuery.setSelectionCriteria(exp1.and(exp2));
    }
}
