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
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;

/**
 * ReportQuery test with constant expression
 * reportQuery.addItem("a constant", builder.value("a constant"));
 */
public class Scenario2_2c extends ReportQueryTestCase {
    public Scenario2_2c() {
        setDescription("Report query with constant expression");
    }

    protected void buildExpectedResults() throws Exception {
        ExpressionBuilder builder = new ExpressionBuilder();

        Vector employees = getSession().readAllObjects(Employee.class);

        for (Enumeration e = employees.elements(); e.hasMoreElements();) {
            Employee emp = (Employee)e.nextElement();
            Object[] result = new Object[2];
            result[0] = emp.getId();
            // Oracle returns a BigDecimal for count
            if (getSession().getPlatform().isOracle() || getSession().getPlatform().isTimesTen7()) {
                result[1] = new java.math.BigDecimal(3);
            } else if (getSession().getPlatform().isMySQL()) {
                result[1] = new java.lang.Long(3);
            } else if (getSession().getPlatform().isSymfoware()) {
                result[1] = new java.lang.Short((short)3);
            } else {
                result[1] = new java.lang.Integer(3);
            }

            addResult(result, null);
        }
    }
    protected void setup()  throws Exception
    {
        super.setup();
        reportQuery = new ReportQuery(new ExpressionBuilder());

        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addItem("id", reportQuery.getExpressionBuilder().get("id"));
        reportQuery.addAttribute("VALUE", reportQuery.getExpressionBuilder().value(3));

        //	reportQuery.setSQLString("SELECT t0.EMP_ID, 3 FROM EMPLOYEE t0, SALARY t1 WHERE (t1.EMP_ID = t0.EMP_ID) 
    }
}
