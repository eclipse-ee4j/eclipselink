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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.datahelper;

import junit.framework.Test;
import junit.framework.TestSuite;

//import org.eclipse.persistence.testing.sdo.helper.typehelper.SDOTypeHelperBuiltinCommonjMapTesting;
public class SDODataHelperTestSuite {
    public SDODataHelperTestSuite() {
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
        TestSuite suite = new TestSuite("All SDODataHelper Tests");
        suite.addTest(new TestSuite(DataHelperToDateTest.class));
        suite.addTest(new TestSuite(DataHelperToCalendarTest.class));
        suite.addTest(new TestSuite(DataHelperToCalendarWithLocale.class));
        suite.addTest(new TestSuite(DataHelperToDateTimeTest.class));
        suite.addTest(new TestSuite(DataHelperToDateTimeWithCalnTest.class));
        suite.addTest(new TestSuite(DataHelperToDayTest.class));
        suite.addTest(new TestSuite(DataHelperToDayWithCalnTest.class));
        suite.addTest(new TestSuite(DataHelperToDurationTest.class));
        suite.addTest(new TestSuite(DataHelperToDurationWithCalnTest.class));
        suite.addTest(new TestSuite(DataHelperToMonthDayTest.class));
        suite.addTest(new TestSuite(DataHelperToMonthDayWithCalnTest.class));
        suite.addTest(new TestSuite(DataHelperToMonthTest.class));
        suite.addTest(new TestSuite(DataHelperToMonthWithCalnTest.class));
        suite.addTest(new TestSuite(DataHelperToTimeTest.class));
        suite.addTest(new TestSuite(DataHelperToTimeWithCalnTest.class));
        suite.addTest(new TestSuite(DataHelperToYearMonthDayTest.class));
        suite.addTest(new TestSuite(DataHelperToYearMonthDayWithCalnTest.class));
        suite.addTest(new TestSuite(DataHelperToYearMonthTest.class));
        suite.addTest(new TestSuite(DataHelperToYearMonthWithCalnTest.class));
        suite.addTest(new TestSuite(DataHelperToYearTest.class));
        suite.addTest(new TestSuite(DataHelperToYearWithCalnTest.class));
        suite.addTest(new TestSuite(DataHelperConvertValueTest.class));
        suite.addTest(new TestSuite(DataHelperConvertFromStringTest.class));
        suite.addTest(new TestSuite(DateConvertBug5672591TestCases.class));
        return suite;
    }
}
