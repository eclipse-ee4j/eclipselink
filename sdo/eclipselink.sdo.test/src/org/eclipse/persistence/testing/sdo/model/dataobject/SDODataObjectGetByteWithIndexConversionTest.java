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
package org.eclipse.persistence.testing.sdo.model.dataobject;

import commonj.sdo.Property;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.exceptions.SDOException;

public class SDODataObjectGetByteWithIndexConversionTest extends SDODataObjectConversionTestCases {
    public SDODataObjectGetByteWithIndexConversionTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetByteWithIndexConversionTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getByte with boolean property
    public void testGetByteFromBoolean() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BOOLEAN);
        dataObject.set(property, true);
        try {
            dataObject.getByte(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getByte with Defined Byte Property
    public void testGetByteConversionFromDefinedByteProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTE);

        byte by = 12;

        dataObject.setByte(PROPERTY_INDEX, by);// add it to instance list

        this.assertEquals(by, dataObject.getByte(PROPERTY_INDEX));
    }

    //3. purpose: getByte with Undefined  Property
    public void testGetByteConversionFromUnDefinedProperty() {
        
        try {
            dataObject.getByte(1);
        } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");    
    }

    //4. purpose: getByte with character property
    public void testGetByteFromCharacter() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_CHARACTER);
        char theValue = 'e';
        dataObject.set(property, theValue);
        try {
            dataObject.getByte(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //5. purpose: getByte with Defined Double Property
    public void testGetByteConversionFromDefinedDoubleProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DOUBLE);

        double db = 12;
        dataObject.setDouble(PROPERTY_INDEX, db);// add it to instance list

        this.assertEquals((byte)db, dataObject.getByte(PROPERTY_INDEX));
    }


    //7. purpose: getByte with Defined float Property
    public void testGetByteConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_FLOAT);

        float fl = 12;
        dataObject.setFloat(PROPERTY_INDEX, fl);// add it to instance list

        this.assertEquals((byte)fl, dataObject.getByte(PROPERTY_INDEX));
    }


    //9. purpose: getByte with Defined int Property
    public void testGetByteConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INT);

        int in = 12;
        dataObject.setInt(PROPERTY_INDEX, in);// add it to instance list

        this.assertEquals((byte)in, dataObject.getByte(property));
    }



    //11. purpose: getByte with Defined long Property
    public void testGetByteConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_LONG);

        long lg = 12;
        dataObject.setLong(PROPERTY_INDEX, lg);// add it to instance list

        this.assertEquals((byte)lg, dataObject.getByte(PROPERTY_INDEX));
    }


    //13. purpose: getByte with Defined short Property
    public void testGetByteConversionFromDefinedShortProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_SHORT);

        short shr = 12;
        dataObject.setShort(PROPERTY_INDEX, shr);// add it to instance list

        this.assertEquals((byte)shr, dataObject.getByte(PROPERTY_INDEX));
    }

    //15. purpose: getByte with Defined String Property
    public void testGetByteConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_STRING);

        String str = "12";
        Byte s_d = new Byte(str);
        dataObject.setString(PROPERTY_INDEX, str);// add it to instance list

        this.assertEquals(s_d.byteValue(), dataObject.getByte(PROPERTY_INDEX));
    }

 

    //17. purpose: getByte with bytes property
    public void testGetByteFromBytes() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTES);
        byte[] theValue = new byte[]{10, 100};
        dataObject.set(property, theValue);
        try {
            dataObject.getByte(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //18. purpose: getByte with decimal property
    public void testGetByteFromDecimal() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DECIMAL);
        
        BigDecimal theValue = new BigDecimal("12");
        dataObject.set(property, theValue);
        try {            
            byte value = dataObject.getByte(PROPERTY_INDEX);
            byte controlValue = theValue.byteValue();
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //19. purpose: getByte with decimal property
    public void testGetByteFromInteger() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INTEGER);
        BigInteger theValue = new BigInteger("12");
        dataObject.set(property, theValue);
        try {
            byte value = dataObject.getByte(PROPERTY_INDEX);
            byte controlValue = theValue.byteValue();
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //20. purpose: getByte with date property
    public void testGetByteFromDate() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DATE);
        dataObject.set(property, Calendar.getInstance().getTime());
        try {
            dataObject.getByte(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //purpose: getByte with nul value
    public void testGetByteWithNullArgument() {
        try {
            int p = -1;
            dataObject.getByte(p);
        } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");    
      }
}
