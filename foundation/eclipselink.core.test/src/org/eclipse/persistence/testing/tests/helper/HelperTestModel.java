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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.helper;

import org.eclipse.persistence.testing.framework.*;

public class HelperTestModel extends TestModel {
    public HelperTestModel() {
        setDescription("This model tests functionality in the Helper class");
    }

    public void addTests() {
        addTest(getDataTypeComparisonTestSuite());

    }

    public static TestSuite getDataTypeComparisonTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("DataTypeComparisonTestSuite");
        suite.setDescription("This suite tests methods which compare data types");

        suite.addTest(new CompareArrayContentTest());
        suite.addTest(new CompareArrayLengthTest());
        suite.addTest(new CompareCharArrayLengthTest());
        suite.addTest(new CompareCharArrayContentTest());
        suite.addTest(new CheckAreVectorTypesAssignableWithNullVectorTest());
        suite.addTest(new CheckAreVectorTypesAssignableTest());
        suite.addTest(new CheckCompareByteArraysWithDifferentElementsTest());
        suite.addTest(new CheckCompareBigDecimalsTest());
        suite.addTest(new CheckClassIsSubclassWithNullSuperclassTest());

        suite.addTest(new BasicTest());
        suite.addTest(new TimeFromDateTest());
        suite.addTest(new TimeFromLongTest());
        suite.addTest(new TimeFromStringTest());
        suite.addTest(new TimestampFromDateTest());
        suite.addTest(new TimestampFromLongTest());
        suite.addTest(new TimestampFromStringTest());

        return suite;

    }
}
