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

public class SetAndGetWithManyPropertyViaPathTestCases extends SDOTestCase {

	public SetAndGetWithManyPropertyViaPathTestCases(String name) {
        super(name);
        super.setUp();
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SetAndGetWithManyPropertyViaPathTestCases" };
        TestRunner.main(arguments);
    }

    // set via path tests
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
	        double db = 12;
	        BigDecimal bd = new BigDecimal(db);
	        myDO.setBigDecimal("myBigDecimal", bd);
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
            double db = 12;
            BigInteger bi = new BigInteger(String.valueOf((int)db));
        	myDO.setBigInteger("myBigInteger", bi);
        	BigInteger mybigint = myDO.getBigInteger("myBigInteger");
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
        	boolean b = new Boolean("true").booleanValue();
        	myDO.setBoolean("myBoolean", b);
        	boolean myboolean = myDO.getBoolean("myBoolean");
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
        	byte b = new Byte("8").byteValue();
        	myDO.setByte("myByte", b);
        	byte mybyte = myDO.getByte("myByte");
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
            byte b1 = new Byte("16").byteValue();
        	byte b2 = new Byte("8").byteValue();
        	byte[] bytes = new byte[] {b1, b2};
        	
        	myDO.setBytes("myBytes", bytes);
        	byte[] mybytes = myDO.getBytes("myBytes");
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
        	char c = 'x';
        	myDO.setChar("myCharDO", c);
        	char mychar = myDO.getChar("myCharDO");
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
            
            Date controlDate = controlCalendar.getTime();
            myDO.setDate("myDateDO", controlDate);
        	Date mydate = myDO.getDate("myDateDO");
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
            double db = 12;
        	myDO.setDouble("myDouble", db);
        	double mydouble = myDO.getDouble("myDouble");
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
            float fl = 12;
        	myDO.setFloat("myFloat", fl);
        	float myfloat = myDO.getFloat("myFloat");
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
        	int i = 69;
        	myDO.setInt("myInt", i);
        	int myint = myDO.getInt("myInt");
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
        	long l = 666L;
        	myDO.setLong("myLong", l);
        	long mylong = myDO.getLong("myLong");
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
        	short s = new Short("66").shortValue();
        	myDO.setShort("myShort", s);
        	short myshort = myDO.getShort("myShort");
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
        	String s = new String("This is my string.");
        	myDO.setString("myString", s);
        	String mystring = myDO.getString("myString");
        	assertTrue("Expected string [" + s + "], but was [" + mystring + "]", mystring.equals(s));
        } catch (IllegalArgumentException iex) {
        	fail("An IllegalArgumentException occurred.");
        }
    }
}
