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
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;

/**
 * ReportQuery test for Scenario 1.1
 * SELECT F_NAME, L_NAME FROM EMPLOYEE
 */
public class AttributeConversionTest extends TestCase {
    protected ReportQuery reportQuery;

    public void test() {
        reportQuery = new ReportQuery(new ExpressionBuilder());
        reportQuery.setReferenceClass(ReportEmployee.class);
        reportQuery.addAttribute("name");
        reportQuery.addAttribute("StartDate", reportQuery.getExpressionBuilder().get("history").get("startDate"));
        Vector results = (Vector)getSession().executeQuery(reportQuery);
        for (Enumeration result = results.elements(); result.hasMoreElements();) {
            ReportQueryResult reportQueryResult = (ReportQueryResult)result.nextElement();
            Object startDate = reportQueryResult.get("StartDate");
            if (!(startDate instanceof java.sql.Date)) {
                throw new TestErrorException("Exception thrown because Report Query attribute was not converted ; see CR 4290");
            }
        }
    }

}
