/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     Dmitry Kornilov - added ApplicationAccessWLSTest
package org.eclipse.persistence.testing.sdo.helper.helpercontext;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SDOHelperContextTestSuite {
    public SDOHelperContextTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Inherited suite method for generating all test cases.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("All SDOHelperContext Tests");
        suite.addTest(new TestSuite(SDOHelperContextMultiThreadedTest.class));
        /*suite.addTest(new TestSuite(SDOHelperContextMultiThreadedTest.class));
        suite.addTest(new TestSuite(SDOHelperContextMultiThreadedTest.class));
        suite.addTest(new TestSuite(SDOHelperContextMultiThreadedTest.class));
        suite.addTest(new TestSuite(SDOHelperContextMultiThreadedTest.class));
        suite.addTest(new TestSuite(SDOHelperContextMultiThreadedTest.class));
        suite.addTest(new TestSuite(SDOHelperContextMultiThreadedTest.class));
        suite.addTest(new TestSuite(SDOHelperContextMultiThreadedTest.class));
        suite.addTest(new TestSuite(SDOHelperContextMultiThreadedTest.class));
        suite.addTest(new TestSuite(SDOHelperContextMultiThreadedTest.class));*/
        suite.addTest(new TestSuite(SDOHelperContextTest.class));
        // the implementation of this test is pending
        //suite.addTest(new TestSuite(SDOHelperContextMultiClassloaderTest.class));
        suite.addTest(new TestSuite(UserSetContextMapTestCases.class));
        suite.addTest(new TestSuite(ApplicationAccessWLSTest.class));
        return suite;
    }
}
