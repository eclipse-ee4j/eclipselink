/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;

 //Bug4942640  This is the only way to support EJBQL in a consistent manner as in ReportQueryAndExistsSubQuery.
 //It generates extra joins.  The generated sql is:  SELECT t0.EMP_ID, t1.EMP_ID, t0.L_NAME, t0.F_NAME, t1.SALARY, t0.GENDER, 
 //t0.END_DATE, t0.START_DATE, t0.MANAGER_ID, t0.ADDR_ID, t0.VERSION FROM EMPLOYEE t0, SALARY t1 WHERE (EXISTS (SELECT DISTINCT 
 //t2.AREA_CODE FROM PHONE t3, PHONE t2 WHERE (((t3.AREA_CODE LIKE '613') AND (t1.EMP_ID = t0.EMP_ID)) AND ((t3.EMP_ID = t0.EMP_ID) 
 //AND (t2.EMP_ID = t0.EMP_ID))))  AND (t1.EMP_ID = t0.EMP_ID))

public class ReportQueryAndExistsSubQueryWithWhereClause extends ReportQueryTestCase {
    public ReportQueryAndExistsSubQueryWithWhereClause() {
        setDescription("ReportQuery and exists subQuery with selection criteria");
    }

    protected void buildExpectedResults() {
        Vector employees = getSession().readAllObjects(Employee.class);

        for (Enumeration e = employees.elements(); e.hasMoreElements();) {
            Employee emp = (Employee)e.nextElement();
            for (Enumeration pe = emp.getPhoneNumbers().elements(); pe.hasMoreElements();) {
                PhoneNumber phone = (PhoneNumber)pe.nextElement();
                if (phone.getAreaCode().equals("613")) {
                    Object[] result = new Object[1];
                    result[0] = emp;
                    addResult(result, null);
                    break;
                }
            }
        }
    }

    protected void setup() throws Exception {
        if (getSession().isRemoteSession()) {
            throwWarning("Report queries with objects are not supported on remote session.");
        }
        super.setup();

        reportQuery = new ReportQuery(Employee.class, new ExpressionBuilder()); 
        ExpressionBuilder builder = reportQuery.getExpressionBuilder(); 
        reportQuery.addAttribute("employee", builder); 
        
        ReportQuery innerQuery = new ReportQuery(Employee.class, new ExpressionBuilder()); 
        innerQuery.addAttribute("areaCode", builder.anyOf("phoneNumbers").get("areaCode")); 
        Expression innerExp = builder.anyOf("phoneNumbers").get("areaCode").like("613"); 

        innerQuery.setSelectionCriteria(innerExp); 
        Expression exists = builder.exists(innerQuery); 
        reportQuery.setSelectionCriteria(exists);         
    }
}
