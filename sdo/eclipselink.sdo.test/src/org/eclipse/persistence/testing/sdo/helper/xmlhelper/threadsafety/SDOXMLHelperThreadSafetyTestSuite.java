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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.threadsafety;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SDOXMLHelperThreadSafetyTestSuite {

    public SDOXMLHelperThreadSafetyTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All XMLHelper Thread Safety Tests");

        suite.addTest(new TestSuite(SDOXMLHelperThreadSafetyTestCases.class));

        return suite;
    }

}
