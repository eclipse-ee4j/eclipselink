/*******************************************************************************
* Copyright (c) 1998, 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - Mar 17/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper.classgen.dynamicimpl;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

public class DataTypesTestCases extends SDOTestCase {

    private static String XSD = "org/eclipse/persistence/testing/sdo/helper/classgen/dynamicimpl/DataTypes.xsd";

    private static Object CONTROL_OBJECT = "control";
    private static String CONTROL_STRING = "control";
    private static List CONTROL_STRINGS = new ArrayList();
    private static boolean CONTROL_BOOLEAN = true;
    private static byte CONTROL_BYTE = 1;
    private static byte[] CONTROL_BYTES = new byte[0];
    private static BigDecimal CONTROL_BIG_DECIMAL = BigDecimal.ONE;
    private static double CONTROL_DOUBLE = 1;
    private static float CONTROL_FLOAT = 1;
    private static int CONTROL_INT = 1;
    private static BigInteger CONTROL_BIG_INTEGER = BigInteger.ONE;
    private static long CONTROL_LONG = 1;
    private static short CONTROL_SHORT = 1;

    public DataTypesTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        InputStream xsdInputStream = Thread.currentThread().getContextClassLoader().getSystemResourceAsStream(XSD);
        this.aHelperContext.getXSDHelper().define(xsdInputStream, null);
    }

    public void testCreateObject() {
        DataObject dataTypesDO = aHelperContext.getDataFactory().create("http://www.example.com", "DataTypes");
        DataTypes dataTypes = (DataTypes) dataTypesDO;

        dataTypes.setAnySimpleTypeProperty(CONTROL_OBJECT);
        assertEquals(CONTROL_OBJECT, dataTypes.getAnySimpleTypeProperty());

        dataTypes.setAnyTypeProperty(dataTypesDO);
        assertEquals(dataTypesDO, dataTypes.getAnyTypeProperty());

        dataTypes.setAnyURIProperty(CONTROL_STRING);
        assertEquals(CONTROL_STRING, dataTypes.getAnyURIProperty());

        dataTypes.setBase64BinaryProperty(CONTROL_BYTES);
        assertEquals(CONTROL_BYTES, dataTypes.getBase64BinaryProperty());

        dataTypes.setBooleanProperty(CONTROL_BOOLEAN);
        assertEquals(CONTROL_BOOLEAN, dataTypes.isBooleanProperty());

        dataTypes.setByteProperty(CONTROL_BYTE);
        assertEquals(CONTROL_BYTE, dataTypes.getByteProperty());

        dataTypes.setDateProperty(CONTROL_STRING);
        assertEquals(CONTROL_STRING, dataTypes.getDateProperty());

        dataTypes.setDateTimeProperty(CONTROL_STRING);
        assertEquals(CONTROL_STRING, dataTypes.getDateTimeProperty());

        dataTypes.setDecimalProperty(CONTROL_BIG_DECIMAL);
        assertEquals(CONTROL_BIG_DECIMAL, dataTypes.getDecimalProperty());

        dataTypes.setDoubleProperty(CONTROL_DOUBLE);
        assertEquals(CONTROL_DOUBLE, dataTypes.getDoubleProperty());

        dataTypes.setDurationProperty(CONTROL_STRING);
        assertEquals(CONTROL_STRING, dataTypes.getDurationProperty());

        dataTypes.setENTITIESProperty(CONTROL_STRINGS);
        assertEquals(CONTROL_STRINGS, dataTypes.getENTITIESProperty());

        dataTypes.setENTITYProperty(CONTROL_STRING);
        assertEquals(CONTROL_STRING, dataTypes.getENTITYProperty());

        dataTypes.setFloatProperty(CONTROL_FLOAT);
        assertEquals(CONTROL_FLOAT, dataTypes.getFloatProperty());

        dataTypes.setGDayProperty(CONTROL_STRING);
        assertEquals(CONTROL_STRING, dataTypes.getGDayProperty());

        dataTypes.setGMonthProperty(CONTROL_STRING);
        assertEquals(CONTROL_STRING, dataTypes.getGMonthProperty());

        dataTypes.setGMonthDayProperty(CONTROL_STRING);
        assertEquals(CONTROL_STRING, dataTypes.getGMonthDayProperty());

        dataTypes.setGYearProperty(CONTROL_STRING);
        assertEquals(CONTROL_STRING, dataTypes.getGYearProperty());

        dataTypes.setGYearMonthProperty(CONTROL_STRING);
        assertEquals(CONTROL_STRING, dataTypes.getGYearMonthProperty());

        dataTypes.setHexBinaryProperty(CONTROL_BYTES);
        assertEquals(CONTROL_BYTES, dataTypes.getHexBinaryProperty());

        dataTypes.setIDProperty(CONTROL_STRING);
        assertEquals(CONTROL_STRING, dataTypes.getIDProperty());

        dataTypes.setIDREFProperty(CONTROL_STRING);
        assertEquals(CONTROL_STRING, dataTypes.getIDREFProperty());

        dataTypes.setIntProperty(CONTROL_INT);
        assertEquals(CONTROL_INT, dataTypes.getIntProperty());

        dataTypes.setIntegerProperty(CONTROL_BIG_INTEGER);
        assertEquals(CONTROL_BIG_INTEGER, dataTypes.getIntegerProperty());

        dataTypes.setLanguageProperty(CONTROL_STRING);
        assertEquals(CONTROL_STRING, dataTypes.getLanguageProperty());

        dataTypes.setLongProperty(CONTROL_LONG);
        assertEquals(CONTROL_LONG, dataTypes.getLongProperty());

        dataTypes.setNameProperty(CONTROL_STRING);
        assertEquals(CONTROL_STRING, dataTypes.getNameProperty());

        dataTypes.setNCNameProperty(CONTROL_STRING);
        assertEquals(CONTROL_STRING, dataTypes.getNCNameProperty());

        dataTypes.setNegativeIntegerProperty(CONTROL_BIG_INTEGER);
        assertEquals(CONTROL_BIG_INTEGER, dataTypes.getNegativeIntegerProperty());

        dataTypes.setNMTOKENProperty(CONTROL_STRING);
        assertEquals(CONTROL_STRING, dataTypes.getNMTOKENProperty());

        dataTypes.setNMTOKENSProperty(CONTROL_STRINGS);
        assertEquals(CONTROL_STRINGS, dataTypes.getNMTOKENSProperty());

        dataTypes.setNonNegativeIntegerProperty(CONTROL_BIG_INTEGER);
        assertEquals(CONTROL_BIG_INTEGER, dataTypes.getNonNegativeIntegerProperty());

        dataTypes.setNonPositiveIntegerProperty(CONTROL_BIG_INTEGER);
        assertEquals(CONTROL_BIG_INTEGER, dataTypes.getNonPositiveIntegerProperty());

        dataTypes.setNormalizedStringProperty(CONTROL_STRING);
        assertEquals(CONTROL_STRING, dataTypes.getNormalizedStringProperty());

        dataTypes.setNOTATION(CONTROL_STRING);
        assertEquals(CONTROL_STRING, dataTypes.getNOTATION());

        dataTypes.setPositiveIntegerProperty(CONTROL_BIG_INTEGER);
        assertEquals(CONTROL_BIG_INTEGER, dataTypes.getPositiveIntegerProperty());

        dataTypes.setQName(CONTROL_STRING);
        assertEquals(CONTROL_STRING, dataTypes.getQName());

        dataTypes.setShort(CONTROL_SHORT);
        assertEquals(CONTROL_SHORT, dataTypes.getShort());

        dataTypes.setString(CONTROL_STRING);
        assertEquals(CONTROL_STRING, dataTypes.getString());

        dataTypes.setTime(CONTROL_STRING);
        assertEquals(CONTROL_STRING, dataTypes.getTime());

        dataTypes.setToken(CONTROL_STRING);
        assertEquals(CONTROL_STRING, dataTypes.getToken());

        dataTypes.setUnsignedByte(CONTROL_SHORT);
        assertEquals(CONTROL_SHORT, dataTypes.getUnsignedByte());

        dataTypes.setUnsignedInt(CONTROL_LONG);
        assertEquals(CONTROL_LONG, dataTypes.getUnsignedInt());

        dataTypes.setUnsignedLong(CONTROL_BIG_INTEGER);
        assertEquals(CONTROL_BIG_INTEGER, dataTypes.getUnsignedLong());

        dataTypes.setUnsignedShort(CONTROL_INT);
        assertEquals(CONTROL_INT, dataTypes.getUnsignedInt());
    }
    
    public void testBooleanMethodsExist() throws Exception {
    	
    	SDOType theType  = (SDOType)aHelperContext.getTypeHelper().getType("http://www.example.com", "DataTypes");
    	Class implClass = theType.getImplClass();
    	assertNotNull(implClass);
    	
    	Method isMethod = implClass.getMethod("isBooleanProperty", new Class[0]);
    	assertNotNull(isMethod);
    	Method getMethod = implClass.getMethod("getBooleanProperty", new Class[0]);
    	assertNotNull(getMethod);
    	
    }

}
