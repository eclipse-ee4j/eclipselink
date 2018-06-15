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
// bdoughan - July 8/2008 - 1.1
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nodenullpolicy;

import junit.framework.Test;
import junit.framework.TestSuite;

public class IsSetLoadAndSaveTestSuite {
    public IsSetLoadAndSaveTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Is Set Load and Save Tests");

        suite.addTest(new TestSuite(IsSetNillableOptionalWithDefaultSetNullTestCases.class));
        suite.addTest(new TestSuite(IsSetOptionalAttributeWithoutDefaultSetNOPTestCases.class));
        suite.addTest(new TestSuite(IsSetOptionalWithoutDefaultSetNonNullTestCases.class));
        suite.addTest(new TestSuite(IsSetOptionalWithoutDefaultSetNOPTestCases.class));
        suite.addTest(new TestSuite(IsSetOptionalWithoutDefaultSetNullTestCases.class));
        suite.addTest(new TestSuite(IsSetNillableWithoutDefaultSetNonNullTestCases.class));
        suite.addTest(new TestSuite(IsSetNillableWithoutDefaultSetNOPTestCases.class));
        suite.addTest(new TestSuite(IsSetNillableWithoutDefaultSetNullTestCases.class));
        suite.addTest(new TestSuite(IsSetOptionalWithDefaultSetNonNullTestCases.class));
        suite.addTest(new TestSuite(IsSetOptionalWithDefaultSetNOPTestCases.class));
        suite.addTest(new TestSuite(IsSetOptionalWithDefaultSetNullTestCases.class));
        suite.addTest(new TestSuite(IsSetOptionalWithDefaultSetDefaultTestCases.class));
        suite.addTest(new TestSuite(IsSetNillableWithDefaultSetNonNullTestCases.class));
        suite.addTest(new TestSuite(IsSetNillableWithDefaultSetNOPTestCases.class));
        suite.addTest(new TestSuite(IsSetNillableWithDefaultSetNullTestCases.class));
        suite.addTest(new TestSuite(IsSetNillableWithDefaultSetDefaultTestCases.class));
        suite.addTest(new TestSuite(IsSetOptionalWithoutDefaultSetNOPNumericPrimsTestCases.class));

        return suite;
    }
}
