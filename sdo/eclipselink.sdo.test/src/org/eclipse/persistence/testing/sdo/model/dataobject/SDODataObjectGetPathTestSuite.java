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
// bdoughan - July 8/2008 - 1.1
package org.eclipse.persistence.testing.sdo.model.dataobject;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.sdo.helper.datahelper.DataHelperToYearMonthDayTest;
import org.eclipse.persistence.testing.sdo.helper.datahelper.DataHelperToYearMonthDayWithCalnTest;
import org.eclipse.persistence.testing.sdo.helper.datahelper.DataHelperToYearMonthTest;
import org.eclipse.persistence.testing.sdo.helper.datahelper.DataHelperToYearMonthWithCalnTest;
import org.eclipse.persistence.testing.sdo.helper.datahelper.DataHelperToYearTest;
import org.eclipse.persistence.testing.sdo.helper.datahelper.DataHelperToYearWithCalnTest;
import org.eclipse.persistence.testing.sdo.model.dataobject.xpathpositional.SDODataObjectXPathPositionalTestSuite;
import org.eclipse.persistence.testing.sdo.model.dataobject.xpathquery.SDODataObjectXPathQueryTestSuite;

public class SDODataObjectGetPathTestSuite {
    public SDODataObjectGetPathTestSuite() {
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
        TestSuite suite = new TestSuite("All SDODataObject Tests");

        suite.addTest(new TestSuite(SDODataObjectGetWithPathTest.class));

        suite.addTest(new TestSuite(SDODataObjectGetByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetBooleanByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetDateByPositionalPathTest.class));

        suite.addTest(new TestSuite(SDODataObjectGetByteByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetBytesByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetCharacterByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetDataObjectByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetDecimalByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetDoubleByPositionalPathTest.class));

        suite.addTest(new TestSuite(SDODataObjectGetFloatByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetIntByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetIntegerByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetLongByPositionalPathTest.class));

        suite.addTest(new TestSuite(SDODataObjectGetShortByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetStringByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetListByPositionalPathTest.class));

        suite.addTest(new TestSuite(SDODataObjectGetPathTest.class));

        return suite;
    }
}
