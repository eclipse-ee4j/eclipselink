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
 * Purpose: Tests adding Items to a Report Query from Parallel Builders.
 * SELECT h.F_NAME, w.F_NAME, h.L_NAME FROM EMPLOYEE h, EMPLOYEE w WHERE
 *     ((h.L_NAME = w.L_NAME) AND ((h.GENDER = 'M') AND (w.GENDER = 'F'))
 *  ORDER BY (w.L_NAME)
 */
public class ParallelBuilderReportItemTest extends ReportQueryTestCase {
    public ParallelBuilderReportItemTest() {
        setDescription("ReportItems originating from parallel builders.");
    }

    protected void buildExpectedResults() throws Exception {
        ExpressionBuilder husbandsAndWives = new ExpressionBuilder();

        ReadAllQuery query = new ReadAllQuery(Employee.class);

        ExpressionBuilder spouses = new ExpressionBuilder(Employee.class);

        Expression expression = husbandsAndWives.get("lastName").equal(spouses.get("lastName"));
        expression = expression.and(husbandsAndWives.get("gender").notEqual(spouses.get("gender")));

        query.setSelectionCriteria(expression);
        query.addOrdering(husbandsAndWives.get("lastName").ascending());
        query.addOrdering(husbandsAndWives.get("gender").descending());

        Vector employees = (Vector)getSession().executeQuery(query);

        for (Enumeration e = employees.elements(); e.hasMoreElements();) {
            Employee husband = (Employee)e.nextElement();
            Employee wife = (Employee)e.nextElement();
            Object[] result = new Object[8];
            result[0] = husband.getFirstName();
            result[1] = wife.getFirstName();
            result[2] = husband.getLastName();
            result[3] = wife.getLastName();
            result[4] = husband.getGender();
            result[5] = wife.getGender();
            result[6] = new Integer(husband.getSalary());
            result[7] = new Integer(wife.getSalary());
            addResult(result, null);
        }
    }

    protected void setup() throws Exception {
        super.setup();

        ExpressionBuilder husbands = new ExpressionBuilder();
        ExpressionBuilder wives = new ExpressionBuilder(Employee.class);

        reportQuery = new ReportQuery(husbands);

        Expression expression = husbands.get("lastName").equal(wives.get("lastName"));
        expression = expression.and(husbands.get("gender").equal("Male").and(wives.get("gender").equal("Female")));

        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addAttribute("husband firstName", husbands.get("firstName"));
        reportQuery.addAttribute("wife firstName", wives.get("firstName"));
        reportQuery.addAttribute("husband lastName", husbands.get("lastName"));
        reportQuery.addAttribute("wife lastName", wives.get("lastName"));
        reportQuery.addAttribute("husband gender", husbands.get("gender"));
        reportQuery.addAttribute("wife gender", wives.get("gender"));
        reportQuery.addAttribute("husband salary", husbands.get("salary"));
        reportQuery.addAttribute("wife salary", wives.get("salary"));
        reportQuery.addOrdering(wives.get("lastName").ascending());

        reportQuery.setSelectionCriteria(expression);

        //	reportQuery.setSQLString("SELECT h.F_NAME, w.F_NAME, h.L_NAME FROM EMPLOYEE h, EMPLOYEE w WHERE ((h.L_NAME = w.L_NAME) AND ((h.GENDER = 'M') AND (w.GENDER = 'F')) ORDER BY (w.L_NAME)");
    }
}
