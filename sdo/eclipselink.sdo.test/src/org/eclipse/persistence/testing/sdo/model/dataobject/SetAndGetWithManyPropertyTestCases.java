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
* dmccann - May 6/2008 - 1.0M7 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.sdo.model.dataobject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.XMLConstants;

import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

public class SetAndGetWithManyPropertyTestCases extends SDOTestCase {

	public SetAndGetWithManyPropertyTestCases(String name) {
        super(name);
        super.setUp();
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SetAndGetWithManyPropertyTestCases" };
        TestRunner.main(arguments);
    }

    // set via property tests
    public void testMyBigDecimalProperty() {
        try {
            DataObject myDO = dataFactory.create("commonj.sdo", "Type");
            myDO.set("uri", "my.uri");
            myDO.set("name", "myBigDecimalDO");
            DataObject myProp = myDO.createDataObject("property");
            myProp.set("name", "myBigDecimal");
            myProp.set("type", SDOConstants.SDO_DECIMAL);
            myProp.set("many", new Boolean("true"));
            Type myDOType = typeHelper.define(myDO);
            SDOProperty prop = (SDOProperty) myDOType.getProperty("myBigDecimal");
            double db = 12;
            BigDecimal bd = new BigDecimal(db);
        	myDO.setBigDecimal(prop, bd);
        	BigDecimal mybigdecimal = myDO.getBigDecimal(prop);
        	assertFalse("Null was returned unexpectedly.", mybigdecimal == null);
        	assertTrue("Expected BigDecimal [" + bd + "], but was [" + mybigdecimal + "]", mybigdecimal == bd);
        } catch (IllegalArgumentException iex) {
        	fail("An IllegalArgumentException occurred.");
        }
    }

    public void testMyBigIntegerProperty() {
        try {
            DataObject myDO = dataFactory.create("commonj.sdo", "Type");
            myDO.set("uri", "my.uri");
            myDO.set("name", "myBigIntegerDO");
            DataObject myProp = myDO.createDataObject("property");
            myProp.set("name", "myBigInteger");
            myProp.set("type", SDOConstants.SDO_INTEGER);
            myProp.set("many", new Boolean("true"));
            Type myDOType = typeHelper.define(myDO);
            SDOProperty prop = (SDOProperty) myDOType.getProperty("myBigInteger");
            double db = 12;
            BigInteger bi = new BigInteger(String.valueOf((int)db));
        	myDO.setBigInteger(prop, bi);
        	BigInteger mybigint = myDO.getBigInteger(prop);
        	assertFalse("Null was returned unexpectedly.", mybigint == null);
        	assertTrue("Expected BigInteger [" + bi + "], but was [" + mybigint + "]", mybigint == bi);
        } catch (IllegalArgumentException iex) {
        	fail("An IllegalArgumentException occurred.");
        }
    }

    public void testMyBooleanProperty() {
        try {
            DataObject myDO = dataFactory.create("commonj.sdo", "Type");
            myDO.set("uri", "my.uri");
            myDO.set("name", "myBooleanDO");
            DataObject myProp = myDO.createDataObject("property");
            myProp.set("name", "myBoolean");
            myProp.set("type", SDOConstants.SDO_BOOLEAN);
            myProp.set("many", new Boolean("true"));
            Type myDOType = typeHelper.define(myDO);
            SDOProperty prop = (SDOProperty) myDOType.getProperty("myBoolean");
        	boolean b = new Boolean("true").booleanValue();
        	myDO.setBoolean(prop, b);
        	boolean myboolean = myDO.getBoolean(prop);
        	assertTrue("Expected Boolean [" + b + "], but was [" + myboolean + "]", myboolean == b);
        } catch (IllegalArgumentException iex) {
        	fail("An IllegalArgumentException occurred.");
        }
    }

    public void testMyByteProperty() {
        try {
            DataObject myDO = dataFactory.create("commonj.sdo", "Type");
            myDO.set("uri", "my.uri");
            myDO.set("name", "myByteDO");
            DataObject myProp = myDO.createDataObject("property");
            myProp.set("name", "myByte");
            myProp.set("type", SDOConstants.SDO_BYTE);
            myProp.set("many", new Boolean("true"));
            Type myDOType = typeHelper.define(myDO);
            SDOProperty prop = (SDOProperty) myDOType.getProperty("myByte");
        	byte b = new Byte("8").byteValue();
        	myDO.setByte(prop, b);
        	byte mybyte = myDO.getByte(prop);
        	assertTrue("Expected byte [" + b + "], but was [" + mybyte + "]", mybyte == b);
        } catch (IllegalArgumentException iex) {
        	iex.printStackTrace();
        	fail("An IllegalArgumentException occurred.");
        }
    }

    public void testMyBytesProperty() {
        try {
            DataObject myDO = dataFactory.create("commonj.sdo", "Type");
            myDO.set("uri", "my.uri");
            myDO.set("name", "myBytesDO");
            DataObject myProp = myDO.createDataObject("property");
            myProp.set("name", "myBytes");
            myProp.set("type", SDOConstants.SDO_BYTES);
            myProp.set("many", new Boolean("true"));
            Type myDOType = typeHelper.define(myDO);
            SDOProperty prop = (SDOProperty) myDOType.getProperty("myBytes");
            byte b1 = new Byte("16").byteValue();
        	byte b2 = new Byte("8").byteValue();
        	byte[] bytes = new byte[] {b1, b2};
        	
        	myDO.setBytes(prop, bytes);
        	byte[] mybytes = myDO.getBytes(prop);
        	assertFalse("Null was returned unexpectedly.", mybytes == null);
        	assertTrue("Expected byte array [" + bytes + "], but was [" + mybytes + "]", mybytes == bytes);
        } catch (IllegalArgumentException iex) {
        	fail("An IllegalArgumentException occurred.");
        }
    }
    
    public void testMyCharProperty() {
        try {
            DataObject myDO = dataFactory.create("commonj.sdo", "Type");
            myDO.set("uri", "my.uri");
            myDO.set("name", "myCharDO");
            DataObject myProp = myDO.createDataObject("property");
            myProp.set("name", "myChar");
            myProp.set("type", SDOConstants.SDO_CHARACTER);
            myProp.set("many", new Boolean("true"));
            Type myDOType = typeHelper.define(myDO);
            SDOProperty prop = (SDOProperty) myDOType.getProperty("myChar");
        	char c = 'x';
        	myDO.setChar(prop, c);
        	char mychar = myDO.getChar(prop);
        	assertTrue("Expected char [" + c + "], but was [" + mychar + "]", mychar == c);
        } catch (IllegalArgumentException iex) {
        	fail("An IllegalArgumentException occurred.");
        }
    }

    public void testMyDateProperty() {
        try {
            DataObject myDO = dataFactory.create("commonj.sdo", "Type");
            myDO.set("uri", "my.uri");
            myDO.set("name", "myDateDO");
            DataObject myProp = myDO.createDataObject("property");
            myProp.set("name", "myDate");
            myProp.set("type", SDOConstants.SDO_DATE);
            myProp.set("many", new Boolean("true"));
            Type myDOType = typeHelper.define(myDO);
            Calendar controlCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            controlCalendar.clear();
            controlCalendar.set(Calendar.YEAR, 2001);
            controlCalendar.set(Calendar.MONTH, Calendar.MAY);
            SDOProperty prop = (SDOProperty) myDOType.getProperty("myDate");
            
            Date controlDate = controlCalendar.getTime();
            myDO.setDate(prop, controlDate);
        	Date mydate = myDO.getDate(prop);
        	assertFalse("Null was returned unexpectedly.", mydate == null);
        	assertTrue("Expected Date [" + controlDate + "], but was [" + mydate + "]", mydate == controlDate);
        } catch (IllegalArgumentException iex) {
        	fail("An IllegalArgumentException occurred.");
        }
    }

    public void testMyDoubleProperty() {
        try {
            DataObject myDO = dataFactory.create("commonj.sdo", "Type");
            myDO.set("uri", "my.uri");
            myDO.set("name", "myDoubleDO");
            DataObject myProp = myDO.createDataObject("property");
            myProp.set("name", "myDouble");
            myProp.set("type", SDOConstants.SDO_DOUBLE);
            myProp.set("many", new Boolean("true"));
            Type myDOType = typeHelper.define(myDO);
            SDOProperty prop = (SDOProperty) myDOType.getProperty("myDouble");
            double db = 12;
        	myDO.setDouble(prop, db);
        	double mydouble = myDO.getDouble(prop);
        	assertTrue("Expected double [" + db + "], but was [" + mydouble + "]", mydouble == db);
        } catch (IllegalArgumentException iex) {
        	fail("An IllegalArgumentException occurred.");
        }
    }
    
    public void testMyFloatProperty() {
        try {
            DataObject myDO = dataFactory.create("commonj.sdo", "Type");
            myDO.set("uri", "my.uri");
            myDO.set("name", "myFloatDO");
            DataObject myProp = myDO.createDataObject("property");
            myProp.set("name", "myFloat");
            myProp.set("type", SDOConstants.SDO_FLOAT);
            myProp.set("many", new Boolean("true"));
            Type myDOType = typeHelper.define(myDO);
            SDOProperty prop = (SDOProperty) myDOType.getProperty("myFloat");
            float fl = 12;
        	myDO.setFloat(prop, fl);
        	float myfloat = myDO.getFloat(prop);
        	assertTrue("Expected float [" + fl + "], but was [" + myfloat + "]", myfloat == fl);
        } catch (IllegalArgumentException iex) {
        	fail("An IllegalArgumentException occurred.");
        }
    }

    public void testMyIntProperty() {
        try {
            DataObject myDO = dataFactory.create("commonj.sdo", "Type");
            myDO.set("uri", "my.uri");
            myDO.set("name", "myIntDO");
            DataObject myProp = myDO.createDataObject("property");
            myProp.set("name", "myInt");
            myProp.set("type", SDOConstants.SDO_INT);
            myProp.set("many", new Boolean("true"));
            Type myDOType = typeHelper.define(myDO);
            SDOProperty prop = (SDOProperty) myDOType.getProperty("myInt");
        	int i = 69;
        	myDO.setInt(prop, i);
        	int myint = myDO.getInt(prop);
        	assertTrue("Expected int [" + i + "], but was [" + myint + "]", myint == i);
        } catch (IllegalArgumentException iex) {
        	fail("An IllegalArgumentException occurred.");
        } catch (Exception x) {
        	fail("An unexpected exception occurred: " + x.getMessage());
        }
    }
    
    public void testMyLongProperty() {
        try {
            DataObject myDO = dataFactory.create("commonj.sdo", "Type");
            myDO.set("uri", "my.uri");
            myDO.set("name", "myLongDO");
            DataObject myProp = myDO.createDataObject("property");
            myProp.set("name", "myLong");
            myProp.set("type", SDOConstants.SDO_LONG);
            myProp.set("many", new Boolean("true"));
            Type myDOType = typeHelper.define(myDO);
            SDOProperty prop = (SDOProperty) myDOType.getProperty("myLong");
        	long l = 666L;
        	myDO.setLong(prop, l);
        	long mylong = myDO.getLong(prop);
        	assertTrue("Expected long [" + l + "], but was [" + mylong + "]", mylong == l);
        } catch (IllegalArgumentException iex) {
        	fail("An IllegalArgumentException occurred.");
        }
    }
    
    public void testMyShortProperty() {
        try {
            DataObject myDO = dataFactory.create("commonj.sdo", "Type");
            myDO.set("uri", "my.uri");
            myDO.set("name", "myShortDO");
            DataObject myProp = myDO.createDataObject("property");
            myProp.set("name", "myShort");
            myProp.set("type", SDOConstants.SDO_SHORT);
            myProp.set("many", new Boolean("true"));
            Type myDOType = typeHelper.define(myDO);
            SDOProperty prop = (SDOProperty) myDOType.getProperty("myShort");
        	short s = new Short("66").shortValue();
        	myDO.setShort(prop, s);
        	short myshort = myDO.getShort(prop);
        	assertTrue("Expected short [" + s + "], but was [" + myshort + "]", myshort == s);
        } catch (IllegalArgumentException iex) {
        	fail("An IllegalArgumentException occurred.");
        }
    }
    
    public void testMyStringProperty() {
        try {
            DataObject myDO = dataFactory.create("commonj.sdo", "Type");
            myDO.set("uri", "my.uri");
            myDO.set("name", "myStringDO");
            DataObject myProp = myDO.createDataObject("property");
            myProp.set("name", "myString");
            myProp.set("type", SDOConstants.SDO_STRING);
            myProp.set("many", new Boolean("true"));
            Type myDOType = typeHelper.define(myDO);
            SDOProperty prop = (SDOProperty) myDOType.getProperty("myString");
        	String s = new String("This is my string.");
        	myDO.setString(prop, s);
        	String mystring = myDO.getString(prop);
        	assertTrue("Expected string [" + s + "], but was [" + mystring + "]", mystring.equals(s));
        } catch (IllegalArgumentException iex) {
        	fail("An IllegalArgumentException occurred.");
        }
    }
}
