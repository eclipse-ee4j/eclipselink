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
public class Scenario1_3 extends ReportQueryTestCase {
    public Scenario1_3() {
        setDescription("DTF mappings with 2 Classes & 1 Table (Aggreagte)");
    }

    protected void buildExpectedResults() {
        Vector employees = getSession().readAllObjects(Employee.class);

        for (Enumeration e = employees.elements(); e.hasMoreElements(); ) {
            Employee emp = (Employee)e.nextElement();
            if ((emp.getPeriod() != null) && (emp.getPeriod().getEndDate() != null)) {
                Object[] result = new Object[3];
                result[0] = emp.getId();
                //Convert to timestamp because that is the type that the database will be returning in the results
                result[1] = new java.sql.Timestamp(emp.getPeriod().getStartDate().getTime());
                result[2] = new java.sql.Timestamp(emp.getPeriod().getEndDate().getTime());
                addResult(result, null);
            }
        }
    }

    protected void setup() throws Exception {
        super.setup();

        reportQuery = new ReportQuery(new ExpressionBuilder());

        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addAttribute("id");
        reportQuery.addAttribute("startDate", reportQuery.getExpressionBuilder().get("period").get("startDate"));
        reportQuery.addAttribute("endDate", reportQuery.getExpressionBuilder().get("period").get("endDate"));
        reportQuery.setSelectionCriteria(reportQuery.getExpressionBuilder().get("period").get("endDate").notEqual(null));
    }
}
