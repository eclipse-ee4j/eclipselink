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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.withoutxsd;

import junit.framework.Test;
import junit.framework.TestSuite;

public class LoadAndSaveWithoutXSDTestSuite {
    public LoadAndSaveWithoutXSDTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All XMLHelper Create ChangeSummary Tests");

        suite.addTestSuite(AllUnknownTestCases.class);
        suite.addTestSuite(SomeKnownSomeUnknownTestCases.class);
        suite.addTestSuite(RootUnknownTestCases.class);

        suite.addTestSuite(RootUnknownWithCSTestCases.class);

        suite.addTestSuite(NotOpenWithUnknownContentTestCases.class);
        suite.addTestSuite(NotOpenWithUnknownContentNestedTestCases.class);
        suite.addTestSuite(LoadAndSaveNestedSchemaTypeTestCases.class);
        suite.addTestSuite(LoadAndSaveWithSimpleTypeTestCases.class);

        return suite;
    }
}
