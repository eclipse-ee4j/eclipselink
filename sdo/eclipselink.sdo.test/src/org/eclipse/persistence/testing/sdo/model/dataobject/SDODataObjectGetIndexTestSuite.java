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

public class SDODataObjectGetIndexTestSuite {
    public SDODataObjectGetIndexTestSuite() {
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
    *  Inherited suite method for generating all test cases.
    * @return
    */
    public static Test suite() {
        TestSuite suite = new TestSuite("All SDODataObject Tests");

        suite.addTest(new TestSuite(SDODataObjectGetStringWithIndexConversionTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetShortWithIndexConversionTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetLongWithIndexConversionTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetIntegerWithIndexConversionTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetIntWithIndexConversionTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetFloatWithIndexConversionTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetDoubleWithIndexConversionTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetDecimalWithIndexConversionTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetDateWithIndexConversionTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetCharacterWithIndexConversionTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetBytesWithIndexConversionTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetByteWithIndexConversionTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetBooleanWithIndexConversionTest.class));

        return suite;
    }
}
