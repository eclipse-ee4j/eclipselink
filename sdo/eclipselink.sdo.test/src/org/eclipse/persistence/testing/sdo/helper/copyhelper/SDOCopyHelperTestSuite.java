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
package org.eclipse.persistence.testing.sdo.helper.copyhelper;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SDOCopyHelperTestSuite {
    public SDOCopyHelperTestSuite() {
    }

    /**
    *
    * @param args
    */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
    *  Inherited suite mthod for generating all test cases.
    * @return
    */
    public static Test suite() {
        TestSuite suite = new TestSuite("All CopyHelper Tests");

        suite.addTest(new TestSuite(SDOCopyHelperDeepCopyTest.class));
         suite.addTest(new TestSuite(SDOCopyHelperOriginalDeepCopyTestCases.class));
         suite.addTest(new TestSuite(SDOCopyHelperOpenContentTestCases.class));
         suite.addTest(new TestSuite(SDOCopyHelperOpenContentWithCSTestCases.class));
        return suite;
    }
}
