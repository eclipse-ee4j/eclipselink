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
import org.eclipse.persistence.exceptions.QueryException;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class Scenario5_2c extends ReportQueryTestCase {
    CursoredStream stream;

    public Scenario5_2c() {
        setDescription("Cursored Stream using SQL but less fields selected then items provided");
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
        reportQuery.setSQLString("SELECT F_NAME FROM EMPLOYEE");
        reportQuery.useCursoredStream(1, 1, new ValueReadQuery("SELECT COUNT(*) FROM EMPLOYEE"));
    }

    public void test() {
        try {
            stream = (CursoredStream)getSession().executeQuery(reportQuery);
        } catch (org.eclipse.persistence.exceptions.QueryException qe) {
            results = new Vector();
            results.addElement(qe);
        }
    }

    protected void verify() {
        try {
            if (results == null || results.size() != 1 || !(results.firstElement() instanceof QueryException)) {
                throw new TestErrorException("Should have caught query exception: " + 
                                             QueryException.REPORT_QUERY_RESULT_SIZE_MISMATCH);
            }
            QueryException qe = (QueryException)results.firstElement();
            if (qe.getErrorCode() != QueryException.REPORT_QUERY_RESULT_SIZE_MISMATCH) {
                throw new TestErrorException("Should have caught query exception: " + 
                                             QueryException.REPORT_QUERY_RESULT_SIZE_MISMATCH + 
                                             ", instead caught:" + qe);
            }
        } finally {
            if (stream != null && !stream.isClosed())
                stream.close();
        }
    }
}
