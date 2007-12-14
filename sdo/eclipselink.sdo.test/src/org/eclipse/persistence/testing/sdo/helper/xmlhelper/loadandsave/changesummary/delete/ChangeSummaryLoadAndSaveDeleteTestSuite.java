/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.delete;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ChangeSummaryLoadAndSaveDeleteTestSuite {
    public ChangeSummaryLoadAndSaveDeleteTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All XMLHelper ChangeSummary Tests");

        suite.addTestSuite(ChangeSummaryRootSimpleDeleteTestCases.class);
        suite.addTestSuite(ChangeSummaryChildSimpleDeleteTestCases.class);
        suite.addTestSuite(ChangeSummaryRootDetachAndDeleteTestCases.class);
        suite.addTestSuite(LoadAndSaveBug6680769TestCases.class);
        return suite;
    }
}