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
public class Scenario5_1c extends ReportQueryTestCase {
    public Scenario5_1c() {
        setDescription("3x1:1 Join Custom SQL");
    }

    protected void buildExpectedResults() throws Exception {
        Vector phones = getSession().readAllObjects(PhoneNumber.class);

        for (Enumeration e = phones.elements(); e.hasMoreElements();) {
            PhoneNumber phone = (PhoneNumber)e.nextElement();
            Object[] result = new Object[5];
            result[0] = phone.getAreaCode();
            result[1] = phone.getNumber();
            result[2] = phone.getOwner().getFirstName();
            result[3] = phone.getOwner().getLastName();
            result[4] = phone.getOwner().getAddress().getCity();
            addResult(result, null);
        }
    }
protected void setup()  throws Exception
{
        super.setup();
        reportQuery = new ReportQuery(new ExpressionBuilder());

        reportQuery.setReferenceClass(PhoneNumber.class);
        reportQuery.addAttribute("areaCode");
        reportQuery.addAttribute("number");
        reportQuery.addAttribute("firstName", reportQuery.getExpressionBuilder().get("owner").get("firstName"));
        reportQuery.addAttribute("lastName", reportQuery.getExpressionBuilder().get("owner").get("lastName"));
        reportQuery.addAttribute("city", reportQuery.getExpressionBuilder().get("owner").get("address").get("city"));
        reportQuery.setSQLString("SELECT T0.AREA_CODE, T0.P_NUMBER, T1.F_NAME, T1.L_NAME, T2.CITY FROM PHONE T0, EMPLOYEE T1, ADDRESS T2 WHERE T0.EMP_ID = T1.EMP_ID AND T1.ADDR_ID = T2.ADDRESS_ID");
    }
}
