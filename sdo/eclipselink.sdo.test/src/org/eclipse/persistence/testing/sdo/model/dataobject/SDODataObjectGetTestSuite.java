/*******************************************************************************
* Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

public class SDODataObjectGetTestSuite {
    public SDODataObjectGetTestSuite() {
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

        suite.addTest(new TestSuite(SDODataObjectGetStringConversion.class));
        suite.addTest(new TestSuite(SDODataObjectGetShortConversionTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetLongConversionTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetIntegerConversionTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetIntConversionTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetFloatConversionTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetDoubleConversionTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetDecimalConversionTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetDateConversionTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetCharacterConversionTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetBytesConversionTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetByteConversionTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetBooleanConversionTest.class));

        return suite;
    }
}
