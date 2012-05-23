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
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;

/**
 * ReportQuery test for Scenario 2.2a
 */
public class Scenario2_2a extends ReportQueryTestCase {
    public Scenario2_2a() {
        setDescription("Scenario 2.2a: Self join");
    }

    protected void buildExpectedResults() throws Exception {
        Vector employees = getSession().readAllObjects(Employee.class);

        for (Enumeration e = employees.elements(); e.hasMoreElements();) {
            Employee emp = (Employee)e.nextElement();
            Object[] result = new Object[4];
            result[0] = emp.getFirstName();
            result[1] = emp.getLastName();
            if (emp.getManager() != null) {
                result[2] = emp.getManager().getFirstName();
                result[3] = emp.getManager().getLastName();
                addResult(result, null);
            }
        }
    }
protected void setup()  throws Exception
{
        super.setup();
        reportQuery = new ReportQuery(new ExpressionBuilder());

        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addAttribute("firstName");
        reportQuery.addAttribute("lastName");
        reportQuery.addAttribute("manager firstName", reportQuery.getExpressionBuilder().get("manager").get("firstName"));
        reportQuery.addAttribute("manager lastName", reportQuery.getExpressionBuilder().get("manager").get("lastName"));

        //	reportQuery.setSQLString("SELECT t0.F_NAME, t0.L_NAME, t1.F_NAME, t1.L_NAME FROM EMPLOYEE t0, EMPLOYEE t1 WHERE (t1.EMP_ID = t0.MANAGER_ID)");
    }
}
