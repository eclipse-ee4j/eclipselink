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
import org.eclipse.persistence.queries.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * ReportQuery test for Scenario 1.1
 * SELECT F_NAME, L_NAME FROM EMPLOYEE
 */
public class Scenario5_2b extends ReportQueryTestCase {
    CursoredStream stream;

    public Scenario5_2b() {
        setDescription("Cursored Stream using proper SQL");
    }

    protected void buildExpectedResults() {
        Vector employees = getSession().readAllObjects(Employee.class);

        for (Enumeration e = employees.elements(); e.hasMoreElements(); ) {
            Employee emp = (Employee)e.nextElement();
            Object[] result = new Object[2];
            result[0] = emp.getFirstName();
            result[1] = emp.getLastName();
            addResult(result, null);
        }
    }

    protected void setup() throws Exception {
        super.setup();
        reportQuery = new ReportQuery(new ExpressionBuilder());

        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addAttribute("firstName");
        reportQuery.addAttribute("lastName");
        reportQuery.setSQLString("SELECT F_NAME, L_NAME FROM EMPLOYEE");
        reportQuery.useCursoredStream(1, 1, new ValueReadQuery("SELECT COUNT(*) FROM EMPLOYEE"));
    }

    public void test() {
        stream = (CursoredStream)getSession().executeQuery(reportQuery);
    }

    protected void verify() {
        try {
            results = new Vector();
            while (!stream.atEnd()) {
                results.addElement(stream.read());
            }

            if (results.size() != expectedResults.size()) {
                throw new TestErrorException("ReportQuery test failed: The result size are different");
            }

            Vector cloneResults = (Vector)expectedResults.clone();
            for (Enumeration e = results.elements(); e.hasMoreElements(); ) {
                removeFromResult((ReportQueryResult)e.nextElement(), cloneResults);

            }
            if (cloneResults.size() != 0) {
                throw new TestErrorException("ReportQuery test failed: The result didn't match");
            }
        } finally {
            if (!stream.isClosed())
                stream.close();
        }
    }
}
