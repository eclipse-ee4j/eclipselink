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

public class SDODataObjectGetCharacterConversionTest extends SDODataObjectConversionTestCases {
    public SDODataObjectGetCharacterConversionTest(String name) {
        super(name);
    }

  public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetCharacterConversionTest" };
        TestRunner.main(arguments);
    }


    //1. purpose: getChar with Boolean property
    public void testGetCharacterFromBoolean() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BOOLEAN);
        dataObject.set(property, true);
        try {
            dataObject.getChar(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getChar with Byte property
    public void testGetCharacterFromByte() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTE);
        byte theValue = 1;
        dataObject.set(property,theValue);
        try {
            char value = dataObject.getChar(property);            
            char controlValue = (char)theValue;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //3. purpose: getChar with Defined Character Property
    public void testGetCharacterConversionFromDefinedCharacterProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_CHARACTER);

        char b = '1';

        dataObject.setChar(property, b);// add it to instance list

        this.assertEquals(b, dataObject.getChar(property));
    }

    //4. purpose: getChar with Undefined Boolean Property
    public void testGetCharacterConversionFromUnDefinedCharacterProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_CHARACTER);

        try {
            dataObject.getChar(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //5. purpose: getChar with Double Property
    public void testGetCharacterFromDouble() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DOUBLE);
        double theValue = 1;
        dataObject.set(property, theValue);        
        try {
            char value = dataObject.getChar(property);            
            char controlValue = (char)theValue;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //6. purpose: getChar with float Property
    public void testGetCharacterFromFloat() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_FLOAT);
        float theValue = 1;
        dataObject.set(property, theValue);
        try {
            char value = dataObject.getChar(property);            
            char controlValue = (char)theValue;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //7. purpose: getChar with int Property
    public void testGetCharacterFromInt() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_INT);
        int theValue = 1;
        dataObject.set(property, theValue);
        try {
            char value = dataObject.getChar(property);            
            char controlValue = (char)theValue;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //8. purpose: getChar with long Property
    public void testGetCharacterFromLong() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_LONG);
        long theValue = 1;
        dataObject.set(property, theValue);
        try {
            char value = dataObject.getChar(property);            
            char controlValue = (char)theValue;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //9. purpose: getChar with short Property
    public void testGetCharacterFromShort() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_SHORT);
        short theValue = 1;
        dataObject.set(property, theValue);
        try {
            char value = dataObject.getChar(property);
            char controlValue = (char)theValue;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //10. purpose: getChar with Defined String Property
    public void testGetCharacterConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_STRING);

        char str = 'c';
        Character B_STR = new Character(str);
        dataObject.setString(property, B_STR.toString());// add it to instance list

        this.assertEquals(str, dataObject.getChar(property));
    }

    //11. purpose: getChar with Undefined boolean Property
    public void testGetCharacterConversionFromUnDefinedStringProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_STRING);

        try {
            dataObject.getChar(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //12. purpose: getChar with bytes property
    public void testGetCharacterFromBytes() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTES);
        byte[] bytes = new byte[]{10,100};
        dataObject.set(property, bytes);
        try {
            dataObject.getChar(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //13. purpose: getChar with decimal property
    public void testGetCharacterFromDecimal() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DECIMAL);
        BigDecimal theValue = new BigDecimal(1);
        dataObject.set(property, theValue);
        try {
            char value = dataObject.getChar(property);
            char controlValue = (char)theValue.byteValue();
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //14. purpose: getChar with integer property
    public void testGetCharacterFromInteger() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_INTEGER);
                
        BigInteger theValue = new BigInteger("1");
        dataObject.set(property, theValue);
        try {
            char value = dataObject.getChar(property);
            char controlValue = (char)theValue.byteValue();
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //22. purpose: getChar with date property
    public void testGetCharacterFromDate() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DATE);
        dataObject.set(property, Calendar.getInstance().getTime());
        try {
            dataObject.getChar(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //purpose: getChar with nul value
    public void testGGetCharacterWithNullArgument() {
        try {
            Property p = null;
            dataObject.getChar(p);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //1. purpose: getChar with Boolean property
    public void testGetCharacterObjectFromBoolean() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BOOLEANOBJECT);
        dataObject.set(property, true);
        try {
            dataObject.getChar(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    public void testGetCharacterFromByteObject() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTEOBJECT);
        Byte theValue = new Byte("10");
        dataObject.set(property, theValue);
        try {
            char value = dataObject.getChar(property);
            char controlValue = (char)theValue.byteValue();
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    public void testGetCharacterConversionFromDefinedCharacterObject() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_CHARACTEROBJECT);

        char b = '1';

        dataObject.setChar(property, b);// add it to instance list
    }

    public void testGetCharacterFromDoubleObject() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DOUBLEOBJECT);
        Double theValue = new Double("1");
        dataObject.set(property, theValue);
        try {            
            char value = dataObject.getChar(property);
            char controlValue = (char)theValue.byteValue();
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    public void testGetCharacterFromFloatObject() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_FLOATOBJECT);
        Float theValue = new Float("1");
        dataObject.set(property, theValue);
      
        try {
            char value = dataObject.getChar(property);
            char controlValue = (char)theValue.byteValue();
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //7. purpose: getChar with int Property
    public void testGetCharacterFromIntObject() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_INTOBJECT);
        Integer theValue = new Integer("1");
        dataObject.set(property, theValue);
        
        try {
            char value = dataObject.getChar(property);
            char controlValue = (char)theValue.byteValue();
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //8. purpose: getChar with long Property
    public void testGetCharacterFromLongObject() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_LONGOBJECT);
        Long theValue = new Long("1");
        dataObject.set(property, theValue);
        
        try {
            char value = dataObject.getChar(property);
            char controlValue = (char)theValue.byteValue();
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //9. purpose: getChar with short Property
    public void testGetCharacterFromShortObject() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_SHORTOBJECT);
        Short theValue = new Short("1");
        dataObject.set(property, theValue);        
        try {
            char value = dataObject.getChar(property);
            char controlValue = (char)theValue.byteValue();
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }
}
