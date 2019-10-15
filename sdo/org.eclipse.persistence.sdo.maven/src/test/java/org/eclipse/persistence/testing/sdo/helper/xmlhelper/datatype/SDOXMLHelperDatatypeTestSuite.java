/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
// rbarkhouse - May 26 2008 - 1.0M8 - Initial implementation

package org.eclipse.persistence.testing.sdo.helper.xmlhelper.datatype;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SDOXMLHelperDatatypeTestSuite {

    public SDOXMLHelperDatatypeTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("XMLHelper Simple (Primitive) Document Tests");

        suite.addTest(new TestSuite(SDOXMLHelperDatatypeBooleanTestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeByteTestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeHexTestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeBase64TestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeCharacterTestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeDateTestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeDateTimeTestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeDayTestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeDecimalTestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeDoubleTestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeFloatTestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeIntTestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeIntegerTestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeLongTestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeMonthTestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeMonthDayTestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeObjectTestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeQNameTestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeShortTestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeStringTestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeStringsTestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeTimeTestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeUriTestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeYearTestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeYearMonthTestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeYearMonthDayTestCases.class));
        suite.addTestSuite(XsiTypeTestCases.class);

        return suite;
    }

}
