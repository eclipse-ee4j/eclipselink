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
 * ReportQuery test for Scenario 1.1
 * SELECT F_NAME, L_NAME FROM EMPLOYEE
 */
public class Scenario5_1b extends ReportQueryTestCase {
    public Scenario5_1b() {
        setDescription("1:1 Join Custom SQL");
    }

    protected void buildExpectedResults() {
        Vector employees = getSession().readAllObjects(Employee.class);

        for (Enumeration e = employees.elements(); e.hasMoreElements();) {
            Employee emp = (Employee)e.nextElement();
            for (Enumeration pe = emp.getPhoneNumbers().elements(); pe.hasMoreElements();) {
                PhoneNumber phone = (PhoneNumber)pe.nextElement();
                if (phone.getAreaCode().equals("613")) {
                    Object[] result = new Object[3];
                    result[0] = emp.getFirstName();
                    result[1] = emp.getLastName();
                    result[2] = phone.getNumber();
                    addResult(result, null);
                }
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
        reportQuery.addAttribute("number", reportQuery.getExpressionBuilder().get("phoneNumbers").get("number"));
        reportQuery.setSQLString("SELECT T0.F_NAME, T0.L_NAME, T1.P_NUMBER FROM EMPLOYEE T0, PHONE T1 WHERE T0.EMP_ID = T1.EMP_ID AND T1.AREA_CODE = '613'");
    }
}
