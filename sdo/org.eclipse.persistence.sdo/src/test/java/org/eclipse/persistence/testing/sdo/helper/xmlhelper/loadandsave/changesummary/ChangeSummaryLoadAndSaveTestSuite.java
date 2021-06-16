/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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


        suite.addTest(new TestSuite(LoadAndSaveSetNullPropertyTestCases.class));

        return suite;
    }
}
