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
/*
   DESCRIPTION
 */

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
    *  Inherited suite method for generating all test cases.
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
        return suite;
    }
}
