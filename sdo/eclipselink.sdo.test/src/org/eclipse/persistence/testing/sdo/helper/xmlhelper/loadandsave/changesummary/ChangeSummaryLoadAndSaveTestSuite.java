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

import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.modify.ChangeSummaryLoadAndSaveModifyTestSuite;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.save.SDOXMLHelperSaveTestSuite;
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

        //logging cs on child
        suite.addTest(new TestSuite(ChangeSummaryChildLoggingOffLoadAndSaveTestCases.class));
        suite.addTest(new TestSuite(ChangeSummaryChildLoggingOnLoadAndSaveTestCases.class));
        //logging cs on root
        suite.addTest(new TestSuite(ChangeSummaryRootLoggingOffLoadAndSaveTestCases.class));
        suite.addTest(new TestSuite(ChangeSummaryRootLoggingOnLoadAndSaveTestCases.class));
        //create cs on root
        suite.addTest(new TestSuite(ChangeSummaryRootSimpleCreateTestCases.class));
        suite.addTest(new TestSuite(ChangeSummaryRootSimpleCreateNestedTestCases.class));
        suite.addTest(new TestSuite(ChangeSummaryRootSimpleCreateManyTestCases.class));
        suite.addTest(new TestSuite(ChangeSummaryRootCreateInvalidTestCases.class));        
        //create cs on child
        suite.addTest(new TestSuite(ChangeSummaryChildCreateSimpleNestedManyTestCases.class));        

        return suite;
    }
}
