/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
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

public class SDODataObjectTestSuite {
    public SDODataObjectTestSuite() {
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

        suite.addTest(new TestSuite(SDODataObjectSetGetWithPropertyTest.class));
        suite.addTest(new TestSuite(SDODataObjectSetGetWithIndexTest.class));

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

        suite.addTest(new TestSuite(DataHelperToYearMonthDayTest.class));
        suite.addTest(new TestSuite(DataHelperToYearMonthDayWithCalnTest.class));
        suite.addTest(new TestSuite(DataHelperToYearMonthTest.class));
        suite.addTest(new TestSuite(DataHelperToYearMonthWithCalnTest.class));
        suite.addTest(new TestSuite(DataHelperToYearTest.class));
        suite.addTest(new TestSuite(DataHelperToYearWithCalnTest.class));

        suite.addTest(new TestSuite(SDODataObjectGetWithPathTest.class));

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
        suite.addTest(new TestSuite(SDODataObjectUnsetIsSetTest.class));
        suite.addTest(new TestSuite(SDODataObjectContainerContainmentPropertyTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetDataObjectConversionWithPathTest.class));

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

        // these suites are discovered and not run twice
        //suite.addTest(new SDODataObjectXPathQueryTestSuite().suite());
        //suite.addTest(new SDODataObjectXPathPositionalTestSuite().suite());

        suite.addTest(new TestSuite(SDODataObjectGetPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectListWrapperTest.class));
        
        suite.addTest(new TestSuite(SDODataObjectJIRA81TestCases.class));
        suite.addTest(new TestSuite(SDODataObjectJIRA90ConversionTestCases.class));

        suite.addTest(new TestSuite(SDODataObjectJIRA102NillableDirectTestCases.class));
        suite.addTest(new TestSuite(SDODataObjectJIRA102NillableCompositeObjectTestCases.class));
        suite.addTest(new TestSuite(SDODataObjectOpenContentBug6011530TestCases.class));
        
        return suite;
    }
}