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
package org.eclipse.persistence.testing.sdo.helper.typehelper.define;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SDOTypeHelperDefineTestSuite {
    public SDOTypeHelperDefineTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All TypeHelper define Tests");
        suite.addTest(new TestSuite(SDOTypeHelperDefineNullTestCases.class));
        suite.addTest(new TestSuite(CustomerDefineTestCases.class));
        suite.addTest(new TestSuite(PurchaseOrderDefineTestCases.class));
        suite.addTest(new TestSuite(OpenContentDefineTestCases.class));
        suite.addTest(new TestSuite(MimeTypeDefineTestCases.class));
        suite.addTest(new TestSuite(MimeTypeOnOtherPropertyDefineTestCases.class));
        suite.addTest(new TestSuite(DataTypeBug5959761TestCases.class));
        suite.addTest(new TestSuite(SDOTypeHelperDefineMixedTestCases.class));
        suite.addTest(new TestSuite(SDOTypeHelperDefineTwiceTestCases.class));
        suite.addTest(new TestSuite(BaseTypeAsDataObjectTestCases.class));
        suite.addTest(new TestSuite(PolymorphicPropertiesJira32TestCases.class));
        suite.addTest(new TestSuite(SDOTypeHelperDefineInvalidTestCases.class));
        return suite;
    }
}
