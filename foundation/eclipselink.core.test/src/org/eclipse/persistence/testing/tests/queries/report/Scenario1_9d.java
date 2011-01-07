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

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.helper.*;
import java.math.BigDecimal;

public class Scenario1_9d extends ReportQueryTestCase {
    public Scenario1_9d() {
        setDescription("MAX aggregate function");
    }

    protected void buildExpectedResults() {
        Vector employees = getSession().readAllObjects(Employee.class);
        BigDecimal max = new BigDecimal(0);

        for (Enumeration e = employees.elements(); e.hasMoreElements();) {
            Employee emp = (Employee)e.nextElement();
            BigDecimal tmp;
            if ((tmp = new BigDecimal(emp.getSalary())).compareTo(max) == 1) {
                max = tmp;
            }
        }

        addResult(new Object[] { max }, null);
    }
protected void setup()  throws Exception
{
        super.setup();
        reportQuery = new ReportQuery(new ExpressionBuilder());

        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addMaximum("salary");
    }

    protected void verify() {
        if (results.size() != expectedResults.size()) {
            throw new TestErrorException("ReportQuery test failed: The result size are different");
        }
        BigDecimal expected = (BigDecimal)((ReportQueryResult)expectedResults.firstElement()).getByIndex(0);
        BigDecimal result = (BigDecimal)ConversionManager.getDefaultManager().convertObject(((ReportQueryResult)results.firstElement()).getByIndex(0), BigDecimal.class);
        if (!Helper.compareBigDecimals(expected, result)) {
            throw new TestErrorException("ReportQuery test failed: The results did not match (" + expected + ", " + result + ")");
        }
    }
}
