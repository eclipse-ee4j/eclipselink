/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.queries.report;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.helper.*;
import java.math.BigDecimal;

public class Scenario1_9e extends ReportQueryTestCase {
    public Scenario1_9e() {
        setDescription("MIN aggregate function");
    }

    protected void buildExpectedResults() {
        Vector employees = getSession().readAllObjects(Employee.class);
        BigDecimal min = new BigDecimal(Integer.MAX_VALUE);

        for (Enumeration e = employees.elements(); e.hasMoreElements();) {
            Employee emp = (Employee)e.nextElement();
            BigDecimal tmp;
            if ((tmp = new BigDecimal(emp.getSalary())).compareTo(min) == -1) {
                min = tmp;
            }
        }

        addResult(new Object[] { min }, null);
    }
protected void setup()  throws Exception
{
        super.setup();
        reportQuery = new ReportQuery(new ExpressionBuilder());

        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addMinimum("salary");
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
