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
 * ReportQuery test with useDistinct()
 */
public class UseDistinctScenario extends ReportQueryTestCase {

    /**
     * UseDistinctScenario constructor comment.
     */
    public UseDistinctScenario() {
        setDescription("Read query using useDistinct()");
    }

    protected void buildExpectedResults() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);

        Vector employees = (Vector)getSession().executeQuery(query);
        Vector distinctEmployees = new Vector();

        // initialize distinctEmployees
        distinctEmployees.addElement(employees.elementAt(0));

        // check employees with duplicate province and add only distinct employees to distinctEmployees
        for (int i = 1; i < employees.size(); i++) {
            boolean duplicateFound = false;

            // iterate through distinctEmployees to check for duplicate provinces, if found, employee not added
            for (int j = 0; j < distinctEmployees.size(); j++) {
                if ((((Employee)employees.elementAt(i)).getAddress().getProvince()).equals((((Employee)distinctEmployees.elementAt(j)).getAddress().getProvince()))) {
                    duplicateFound = true;
                }
            }
            if (!duplicateFound) {
                distinctEmployees.addElement(employees.elementAt(i));
            }
        }

        for (Enumeration e = distinctEmployees.elements(); e.hasMoreElements();) {
            Employee emp = (Employee)e.nextElement();
            Object[] result = new Object[1];
            result[0] = emp.getAddress().getProvince();
            addResult(result, null);
        }
    }

    protected void setup() throws Exception {
        super.setup();
        reportQuery = new ReportQuery(new ExpressionBuilder());

        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addAttribute("province", reportQuery.getExpressionBuilder().get("address").get("province"));
        reportQuery.useDistinct();
    }
}
