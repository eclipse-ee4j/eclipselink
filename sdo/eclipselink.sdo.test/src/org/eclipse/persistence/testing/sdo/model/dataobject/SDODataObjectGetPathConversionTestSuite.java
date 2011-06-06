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
* bdoughan - July 8/2008 - 1.1
******************************************************************************/
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

public class SDODataObjectGetPathConversionTestSuite {
    public SDODataObjectGetPathConversionTestSuite() {
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

        suite.addTest(new TestSuite(SDODataObjectGetBooleanConversionWithPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetByteConversionWithPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetBytesConversionWithPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetCharacterConversionWithPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetDateConversionWithPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetDecimalConversionWithpathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetDoubleConversionWithPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetFloatConversionWithPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetIntConversionWithPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetLongConversionWithPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetShortConversionWithPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetStringConversionWithPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetIntegerConversionWithPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetListConversionTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetDataObjectConversionTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetDataObjectConversionWithPathTest.class));

        return suite;
    }
}
