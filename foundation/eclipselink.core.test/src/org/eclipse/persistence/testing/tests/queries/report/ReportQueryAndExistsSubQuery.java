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

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

 //Bug4942640  This is the only way to support EJBQL in a consistent manner as in ReportQueryAndExistsSubQueryWithWhereClause.
 //It generates extra joins.  The generated sql is:  SELECT t0.EMP_ID, t1.EMP_ID, t0.L_NAME, t0.F_NAME, t1.SALARY, 
 //t0.GENDER, t0.END_DATE, t0.START_DATE, t0.MANAGER_ID, t0.ADDR_ID, t0.VERSION FROM EMPLOYEE t0, SALARY t1 
 //WHERE (EXISTS (SELECT DISTINCT t2.TYPE, t2.P_NUMBER, t2.AREA_CODE, t2.EMP_ID FROM SALARY t4, EMPLOYEE t3, PHONE t2 
 //WHERE ((t4.EMP_ID = t3.EMP_ID) AND (t2.EMP_ID = t0.EMP_ID)))  AND (t1.EMP_ID = t0.EMP_ID))

public class ReportQueryAndExistsSubQuery extends ReportQueryTestCase {
    public ReportQueryAndExistsSubQuery() {
        setDescription("ReportQuery and exists subQuery");
    }

    protected void buildExpectedResults() {
        Vector employees = getSession().readAllObjects(Employee.class);

        for (Enumeration e = employees.elements(); e.hasMoreElements();) {
            Employee emp = (Employee)e.nextElement();
            Object[] result = new Object[1];
            result[0] = emp;
            addResult(result, null);
        }
    }

    protected void setup() throws Exception {
        if (getSession().isRemoteSession()) {
            throwWarning("Report queries with objects are not supported on remote session.");
        }
        if (getSession().getPlatform().isDerby() || getSession().getPlatform().isSybase()) {
            throwWarning("Exists with multiple values not supported on " + getSession().getPlatform());
        }
        super.setup();

        reportQuery = new ReportQuery(Employee.class, new ExpressionBuilder()); 
        ExpressionBuilder builder = reportQuery.getExpressionBuilder(); 
        reportQuery.addAttribute("employee", builder); 
        
        ReportQuery innerQuery = new ReportQuery(Employee.class, new ExpressionBuilder()); 
        innerQuery.addAttribute("phoneNumbers", builder.anyOf("phoneNumbers")); 

        Expression exists = builder.exists(innerQuery); 
        reportQuery.setSelectionCriteria(exists);         
    }
}
