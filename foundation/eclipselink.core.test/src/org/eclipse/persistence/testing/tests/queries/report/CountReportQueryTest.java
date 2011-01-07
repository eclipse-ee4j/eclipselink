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

import java.math.BigDecimal;

import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.queries.*;

/**
 * Tests generalized report query counts.
 * <p>
 * Added to test count(objectAttribute), where 7 test cases had to be considered
 * without adding a class for each one.
 * @author Stephen McRitchie
 * @since Oracle 10g
 */
public class CountReportQueryTest extends ReportQueryTestCase {
    int expectedCount;

    public CountReportQueryTest(ReportQuery reportQuery, int expectedCount) {
        this.reportQuery = reportQuery;
        this.expectedCount = expectedCount;
        setDescription("COUNT aggregate function");
    }

    protected void buildExpectedResults() {
        Object[] result = new Object[1];
        result[0] = new java.math.BigDecimal(expectedCount);
        addResult(result, null);
    }

    protected void setup() throws Exception {
        super.setup();
        reportQuery = (ReportQuery)reportQuery.clone();
    }

    protected void verify() {
        if (results.size() != expectedResults.size()) {
            throw new TestErrorException("ReportQuery test failed: The result size are different");
        }
        BigDecimal expected = (BigDecimal)((ReportQueryResult)expectedResults.firstElement()).getByIndex(0);
        BigDecimal result = 
            (BigDecimal)ConversionManager.getDefaultManager().convertObject(((ReportQueryResult)results.firstElement()).getByIndex(0), 
                                                                            BigDecimal.class);
        if (!Helper.compareBigDecimals(expected, result)) {
            throw new TestErrorException("ReportQuery test failed: The results did not match (" + expected + ", " + 
                                         result + ")");
        }
    }
}
