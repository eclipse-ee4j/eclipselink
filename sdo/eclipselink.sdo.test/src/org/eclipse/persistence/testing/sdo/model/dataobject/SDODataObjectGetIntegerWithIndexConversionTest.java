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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.model.dataobject;

import commonj.sdo.Property;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.exceptions.SDOException;

public class SDODataObjectGetIntegerWithIndexConversionTest extends SDODataObjectConversionTestCases {
    public SDODataObjectGetIntegerWithIndexConversionTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetIntegerWithIndexConversionTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getBigInteger with boolean property
    public void testGetIntegerFromBoolean() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BOOLEAN);
        dataObject.set(property, true);
        try {
            BigInteger value = dataObject.getBigInteger(PROPERTY_INDEX);
            assertEquals(1, value);
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getBigInteger with byte property
    public void testGetIntegerFromByte() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTE);
        byte theByte = 8;
        dataObject.set(property, theByte);
        try {
            BigInteger value = dataObject.getBigInteger(PROPERTY_INDEX);
            BigInteger controlValue = new BigInteger("8");
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //3. purpose: getBigInteger with character property
    public void testGetIntegerFromCharacter() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_CHARACTER);
        dataObject.set(property, 'e');
        try {
            dataObject.getBigInteger(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //5. purpose: getBigInteger with Defined Double Property
    public void testGetIntegerConversionFromDefinedDoubleProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DOUBLE);

        double db = 12;
        int il = (int)db;
        BigInteger bd = new BigInteger(String.valueOf(il));
        dataObject.setDouble(PROPERTY_INDEX, db);// add it to instance list

        this.assertEquals(bd, dataObject.getBigInteger(PROPERTY_INDEX));
    }

    //7. purpose: getBigInteger with Defined float Property
    public void testGetIntegerConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_FLOAT);

        float fl = 12;
        int il = (int)fl;
        BigInteger bd = new BigInteger(String.valueOf(il));
        dataObject.setFloat(PROPERTY_INDEX, fl);// add it to instance list

        this.assertEquals(bd, dataObject.getBigInteger(PROPERTY_INDEX));
    }

    //9. purpose: getBigInteger with Defined int Property
    public void testGetIntegerConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INT);

        int in = 12;
        BigInteger bd = new BigInteger(String.valueOf(in));
        dataObject.setInt(PROPERTY_INDEX, in);// add it to instance list

        this.assertEquals(bd, dataObject.getBigInteger(PROPERTY_INDEX));
    }

    //11. purpose: getBigInteger with Defined long Property
    public void testGetIntegerConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_LONG);

        long lg = 12;
        BigInteger bd = new BigInteger(String.valueOf(lg));
        dataObject.setLong(PROPERTY_INDEX, lg);// add it to instance list

        this.assertEquals(bd, dataObject.getBigInteger(PROPERTY_INDEX));
    }

    //14. purpose: getBigInteger with Defined String Property
    public void testGetgetIntegerConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_STRING);
        type.addDeclaredProperty(property);

        String str = "12";
        BigInteger bd = new BigInteger(str);
        dataObject.setString(PROPERTY_INDEX, str);// add it to instance list

        this.assertEquals(bd, dataObject.getBigInteger(PROPERTY_INDEX));
    }

    //16. purpose: getBigInteger with Defined Bytes Property !! OX PRO !!
    public void testGetIntegerConversionFromDefinedBytesProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INTEGER);

        byte[] b = { 12, 13 };
        BigInteger bin = new BigInteger(b);

        dataObject.setBytes(PROPERTY_INDEX, b);// add it to instance list

        this.assertEquals(bin, dataObject.getBigInteger(PROPERTY_INDEX));
    }

    //17. purpose: getBigInteger with Undefined Bytes Property
    public void testGetIntegerConversionFromUnDefinedProperty() {
        try {
            dataObject.getBigInteger(1);
        } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");
    }

    //17. purpose: getBigDecimal with bytes property
    public void testGetDecimalFromBytes() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTES);
        dataObject.set(property, new String("eee").getBytes());
        try {
            dataObject.getBigDecimal(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //18. purpose: getBigInteger with Defined Decimal Property
    public void testGetIntegerConversionFromDefinedDecimalProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DECIMAL);

        int db = 12;
        BigDecimal bd = new BigDecimal(db);
        BigInteger bd_ = new BigInteger(String.valueOf(bd));
        dataObject.setBigDecimal(PROPERTY_INDEX, bd);// add it to instance list

        this.assertEquals(bd_, dataObject.getBigInteger(PROPERTY_INDEX));
    }

    //20. purpose: getBigInteger with Defined integer Property
    public void testGetIntegerConversionFromDefinedIntegerProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INTEGER);

        BigInteger bi = new BigInteger("12");
        dataObject.setBigInteger(PROPERTY_INDEX, bi);// add it to instance list

        this.assertEquals(bi, dataObject.getBigInteger(PROPERTY_INDEX));
    }

    //22. purpose: getBigInteger with date property
    public void testGetIntegerFromDate() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DATE);        
        dataObject.set(property, Calendar.getInstance().getTime());
        try {
            dataObject.getBigInteger(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //purpose: getBigInteger with nul value
    public void testGetIntegerWithNullArgument() {
        try {            
            Property p = null;
            dataObject.getBigInteger(p);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getCause() instanceof SDOException);
            assertEquals(SDOException.CANNOT_PERFORM_OPERATION_ON_NULL_ARGUMENT ,((SDOException)e.getCause()).getErrorCode());
            return;
        }
        fail("an IllegalArgumentException should have occurred.");    
      }
}
