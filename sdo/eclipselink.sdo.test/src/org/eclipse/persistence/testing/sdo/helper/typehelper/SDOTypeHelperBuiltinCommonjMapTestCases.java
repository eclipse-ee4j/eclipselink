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
package org.eclipse.persistence.testing.sdo.helper.typehelper;

import commonj.sdo.Type;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class SDOTypeHelperBuiltinCommonjMapTestCases extends SDOTestCase {
    private static String BUILT_IN = SDOConstants.SDO_URL;

    public SDOTypeHelperBuiltinCommonjMapTestCases(String name) {
        super(name);
    }

    /**
     * testing if SDO_BOOLEAN is uniquely returned by SDOTypeHelper.
     */
    public void test_Unique_SDO_Boolean() {
        Type Boolean1 = typeHelper.getType(BUILT_IN, "Boolean");
        this.assertNotNull(Boolean1);

        Type Boolean2 = typeHelper.getType(BUILT_IN, "Boolean");
        this.assertNotNull(Boolean2);

        this.assertTrue(Boolean1 == Boolean2);
    }

    /**
     * testing if SDO_BYTE is uuniquely returned by SDOTypeHelper.
     */
    public void test_Unique_SDO_Byte() {
        Type Byte1 = typeHelper.getType(BUILT_IN, "Byte");
        this.assertNotNull(Byte1);

        Type Byte2 = typeHelper.getType(BUILT_IN, "Byte");
        this.assertNotNull(Byte2);

        this.assertTrue(Byte1 == Byte2);
    }

    /**
     * testing if SDO_BYTES is uniquely returned by SDOTypeHelper.
     */
    public void test_Unique_SDO_Bytes() {
        Type Bytes1 = typeHelper.getType(BUILT_IN, "Bytes");
        this.assertNotNull(Bytes1);

        Type Bytes2 = typeHelper.getType(BUILT_IN, "Bytes");
        this.assertNotNull(Bytes2);

        this.assertTrue(Bytes1 == Bytes2);
    }

    /**
     * testing if SDO_CHARACTER is uniquely returned by SDOTypeHelper.
     */
    public void test_Unique_SDO_CHARACTER() {
        Type Character1 = typeHelper.getType(BUILT_IN, "Character");
        this.assertNotNull(Character1);

        Type Character2 = typeHelper.getType(BUILT_IN, "Character");
        this.assertNotNull(Character2);

        this.assertTrue(Character1 == Character2);
    }

    /**
     * testing if SDO_DATAOBJECT is uniquely returned by SDOTypeHelper.
     */
    public void test_Unique_SDO_DATAOBJECT() {
        Type DataObject1 = typeHelper.getType(BUILT_IN, "DataObject");
        this.assertNotNull(DataObject1);

        Type DataObject2 = typeHelper.getType(BUILT_IN, "DataObject");
        this.assertNotNull(DataObject2);

        this.assertTrue(DataObject1 == DataObject2);
    }

    /**
      * testing if SDO_DATE is uniquely returned by SDOTypeHelper.
      */
    public void test_Unique_SDO_DATE() {
        Type Date1 = typeHelper.getType(BUILT_IN, "Date");
        this.assertNotNull(Date1);

        Type Date2 = typeHelper.getType(BUILT_IN, "Date");
        this.assertNotNull(Date2);

        this.assertTrue(Date1 == Date2);
    }

    /**
     * testing if SDO_DATETIME is uniquely returned by SDOTypeHelper.
     */
    public void test_Unique_SDO_DATETIME() {
        Type DateTime1 = typeHelper.getType(BUILT_IN, "DateTime");
        this.assertNotNull(DateTime1);

        Type DateTime2 = typeHelper.getType(BUILT_IN, "DateTime");
        this.assertNotNull(DateTime2);

        this.assertTrue(DateTime1 == DateTime2);
    }

    /**
     * testing if SDO_DAY is uniquely returned by SDOTypeHelper.
     */
    public void test_Unique_SDO_DAY() {
        Type Day1 = typeHelper.getType(BUILT_IN, "Day");
        this.assertNotNull(Day1);

        Type Day2 = typeHelper.getType(BUILT_IN, "Day");
        this.assertNotNull(Day2);

        this.assertTrue(Day1 == Day2);
    }

    /**
     * testing if SDO_DECIMAL is uniquely returned by SDOTypeHelper.
     */
    public void test_Unique_SDO_DECIMAL() {
        Type Decimal1 = typeHelper.getType(BUILT_IN, "Decimal");
        this.assertNotNull(Decimal1);

        Type Decimal2 = typeHelper.getType(BUILT_IN, "Decimal");
        this.assertNotNull(Decimal2);

        this.assertTrue(Decimal1 == Decimal2);
    }

    /**
     * testing if SDO_DOUBLE is uniquely returned by SDOTypeHelper.
     */
    public void test_Unique_SDO_DOUBLE() {
        Type Double1 = typeHelper.getType(BUILT_IN, "Double");
        this.assertNotNull(Double1);

        Type Double2 = typeHelper.getType(BUILT_IN, "Double");
        this.assertNotNull(Double2);

        this.assertTrue(Double1 == Double2);
    }

    /**
     * testing if SDO_DURATION is uniquely returned by SDOTypeHelper.
     */
    public void test_Unique_SDO_DURATION() {
        Type Duration1 = typeHelper.getType(BUILT_IN, "Duration");
        this.assertNotNull(Duration1);

        Type Duration2 = typeHelper.getType(BUILT_IN, "Duration");
        this.assertNotNull(Duration2);

        this.assertTrue(Duration1 == Duration2);
    }

    /**
     * testing if SDO_FLOAT is uniquely returned by SDOTypeHelper.
     */
    public void test_Unique_SDO_FLOAT() {
        Type Float1 = typeHelper.getType(BUILT_IN, "Float");
        this.assertNotNull(Float1);

        Type Float2 = typeHelper.getType(BUILT_IN, "Float");
        this.assertNotNull(Float2);

        this.assertTrue(Float1 == Float2);
    }

    /**
     * testing if SDO_INT is uniquely returned by SDOTypeHelper.
     */
    public void test_Unique_SDO_INT() {
        Type int1 = typeHelper.getType(BUILT_IN, "Int");
        this.assertNotNull(int1);

        Type int2 = typeHelper.getType(BUILT_IN, "Int");
        this.assertNotNull(int2);

        this.assertTrue(int1 == int2);
    }

    /**
     * testing if SDO_INTEGER is uniquely returned by SDOTypeHelper.
     */
    public void test_Unique_SDO_INTEGER() {
        Type Integer1 = typeHelper.getType(BUILT_IN, "Integer");
        this.assertNotNull(Integer1);

        Type Integer2 = typeHelper.getType(BUILT_IN, "Integer");
        this.assertNotNull(Integer2);

        this.assertTrue(Integer1 == Integer2);
    }

    /**
     * testing if SDO_LONG is uniquely returned by SDOTypeHelper.
     */
    public void test_Unique_SDO_LONG() {
        Type Long1 = typeHelper.getType(BUILT_IN, "Long");
        this.assertNotNull(Long1);

        Type Long2 = typeHelper.getType(BUILT_IN, "Long");
        this.assertNotNull(Long2);

        this.assertTrue(Long1 == Long2);
    }

    /**
     * testing if SDO_MONTH is uniquely returned by SDOTypeHelper.
     */
    public void test_Unique_SDO_MONTH() {
        Type Month1 = typeHelper.getType(BUILT_IN, "Month");
        this.assertNotNull(Month1);

        Type Month2 = typeHelper.getType(BUILT_IN, "Month");
        this.assertNotNull(Month2);

        this.assertTrue(Month1 == Month2);
    }

    /**
     * testing if SDO_MONTHDAY is uniquely returned by SDOTypeHelper.
     */
    public void test_Unique_SDO_MONTHDAY() {
        Type MonthDay1 = typeHelper.getType(BUILT_IN, "MonthDay");
        this.assertNotNull(MonthDay1);

        Type MonthDay2 = typeHelper.getType(BUILT_IN, "MonthDay");
        this.assertNotNull(MonthDay2);

        this.assertTrue(MonthDay1 == MonthDay2);
    }

    /**
     * testing if SDO_OBJECT is uniquely returned by SDOTypeHelper.
     */
    public void test_Unique_SDO_OBJECT() {
        Type Object1 = typeHelper.getType(BUILT_IN, "Object");
        this.assertNotNull(Object1);

        Type Object2 = typeHelper.getType(BUILT_IN, "Object");
        this.assertNotNull(Object2);

        this.assertTrue(Object1 == Object2);
    }

    /**
     * testing if SDO_SHORT is uniquely returned by SDOTypeHelper.
     */
    public void test_Unique_SDO_SHORT() {
        Type Short1 = typeHelper.getType(BUILT_IN, "Short");
        this.assertNotNull(Short1);

        Type Short2 = typeHelper.getType(BUILT_IN, "Short");
        this.assertNotNull(Short2);

        this.assertTrue(Short1 == Short2);
    }

    /**
     * testing if SDO_STRING is uniquely returned by SDOTypeHelper.
     */
    public void test_Unique_SDO_STRING() {
        Type String1 = typeHelper.getType(BUILT_IN, "String");
        this.assertNotNull(String1);

        Type String2 = typeHelper.getType(BUILT_IN, "String");
        this.assertNotNull(String2);

        this.assertTrue(String1 == String2);
    }

    /**
     * testing if SDO_STRINGS is uniquely returned by SDOTypeHelper.
     */
    public void test_Unique_SDO_STRINGS() {
        Type Strings1 = typeHelper.getType(BUILT_IN, "Strings");
        this.assertNotNull(Strings1);

        Type Strings2 = typeHelper.getType(BUILT_IN, "Strings");
        this.assertNotNull(Strings2);

        this.assertTrue(Strings1 == Strings2);
    }

    /**
     * testing if SDO_TIME is uniquely returned by SDOTypeHelper.
     */
    public void test_Unique_SDO_Tim() {
        Type Time1 = typeHelper.getType(BUILT_IN, "Time");
        this.assertNotNull(Time1);

        Type Time2 = typeHelper.getType(BUILT_IN, "Time");
        this.assertNotNull(Time2);

        this.assertTrue(Time1 == Time2);
    }

    /**
     * testing if SDO_YEAR is uniquely returned by SDOTypeHelper.
     */
    public void test_Unique_SDO_YEAR() {
        Type Year1 = typeHelper.getType(BUILT_IN, "Year");
        this.assertNotNull(Year1);

        Type Year2 = typeHelper.getType(BUILT_IN, "Year");
        this.assertNotNull(Year2);

        this.assertTrue(Year1 == Year2);
    }

    /**
     * testing if SDO_YEARMONTH is uniquely returned by SDOTypeHelper.
     */
    public void test_Unique_SDO_YEARMONTH() {
        Type YearMonth1 = typeHelper.getType(BUILT_IN, "YearMonth");
        this.assertNotNull(YearMonth1);

        Type YearMonth2 = typeHelper.getType(BUILT_IN, "YearMonth");
        this.assertNotNull(YearMonth2);

        this.assertTrue(YearMonth1 == YearMonth2);
    }

    /**
     * testing if SDO_YEARMONTHDAY is uniquely returned by SDOTypeHelper.
     */
    public void test_Unique_SDO_YEARMONTHDAY() {
        Type YearMonthDay1 = typeHelper.getType(BUILT_IN, "YearMonthDay");
        this.assertNotNull(YearMonthDay1);

        Type YearMonthDay2 = typeHelper.getType(BUILT_IN, "YearMonthDay");
        this.assertNotNull(YearMonthDay2);

        this.assertTrue(YearMonthDay1 == YearMonthDay2);
    }

    /**
     * testing if SDO_URI is uniquely returned by SDOTypeHelper.
     */
    public void test_Unique_SDO_URI() {
        Type URI1 = typeHelper.getType(BUILT_IN, "URI");
        this.assertNotNull(URI1);

        Type URI2 = typeHelper.getType(BUILT_IN, "URI");
        this.assertNotNull(URI2);

        this.assertTrue(URI1 == URI2);
    }

    /**
     * testing if null is returned by SDOTypeHelper when passing in non-built_in
     * uri.
     */
    public void test_Non_Built_in_URI() {
        Type URI1 = typeHelper.getType("somthing.else", "URI");
        this.assertNull(URI1);

        Type URI2 = typeHelper.getType(null, "URI");
        this.assertNull(URI2);

    }

    /**
     * testing if null is returned by SDOTypeHelper when passing in non-built_in
     * type name.
     */
    public void test_Non_Built_Typename() {
        Type URI1 = typeHelper.getType("somthing.else", "not_built_in");
        this.assertNull(URI1);

        Type URI2 = typeHelper.getType("somthing.else", null);
        this.assertNull(URI2);

    }
}
