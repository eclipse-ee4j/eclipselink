/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ChangeSummaryLoadAndSaveTestSuite {
    public ChangeSummaryLoadAndSaveTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All XMLHelper ChangeSummary Tests");

        // Logging CS on child
        suite.addTest(new TestSuite(ChangeSummaryChildLoggingOffLoadAndSaveTestCases.class));
        suite.addTest(new TestSuite(ChangeSummaryChildLoggingOnLoadAndSaveTestCases.class));
        // Logging CS on root
        suite.addTest(new TestSuite(ChangeSummaryRootLoggingOffLoadAndSaveTestCases.class));
        suite.addTest(new TestSuite(ChangeSummaryRootLoggingOnLoadAndSaveTestCases.class));
        // Create CS on root
        suite.addTest(new TestSuite(ChangeSummaryRootSimpleCreateTestCases.class));
        suite.addTest(new TestSuite(ChangeSummaryRootSimpleCreateNestedTestCases.class));
        suite.addTest(new TestSuite(ChangeSummaryRootSimpleCreateManyTestCases.class));
        suite.addTest(new TestSuite(ChangeSummaryRootCreateInvalidTestCases.class));        
        // Create CS on child
        suite.addTest(new TestSuite(ChangeSummaryChildCreateSimpleNestedManyTestCases.class));        

        return suite;
    }
}
