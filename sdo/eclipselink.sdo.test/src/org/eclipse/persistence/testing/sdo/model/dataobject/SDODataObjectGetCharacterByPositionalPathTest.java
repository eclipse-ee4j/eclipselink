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
package org.eclipse.persistence.testing.sdo.model.dataobject;

import commonj.sdo.Property;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;

public class SDODataObjectGetCharacterByPositionalPathTest extends SDODataObjectGetByPositionalPathTestCases {
    public SDODataObjectGetCharacterByPositionalPathTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetCharacterByPositionalPathTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getChar with Boolean property
    public void testGetCharacterFromBoolean() {
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_BOOLEAN);
        dataObject_c.set(property, true);

        try {
            dataObject_a.getChar(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //2. purpose: getChar with Byte property
    public void testGetCharacterFromByte() {
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_BYTE);
        byte theValue = 10;
        dataObject_c.set(property, theValue);
        try {
            char value = dataObject_a.getChar(propertyPath_a_b_c);
            char controlValue = (char)theValue;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //3. purpose: getChar with Defined Character Property
    public void testGetCharacterConversionFromDefinedCharacterProperty() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_CHARACTER);

        char b = '1';

        dataObject_a.setChar(propertyPath_a_b_c, b);// add it to instance list

        this.assertEquals(b, dataObject_a.getChar(propertyPath_a_b_c));
    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetCharacterConversionWithPathFromDefinedBooleanPropertyBracketPositionalSet() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_CHARACTER);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        char bb = '1';
        List b = new ArrayList();

        //b.add(bb);
        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setChar(property3, bb);

        this.assertEquals(bb, dataObject_a.getChar(property3));

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetCharacterConversionWithPathFromDefinedBooleanPropertyDotPositionalSet() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_CHARACTER);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        char bb = '1';
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setChar(property + ".0", bb);

        this.assertEquals(bb, dataObject_a.getChar(property + ".0"));

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetCharacterConversionWithPathFromDefinedBooleanPropertyBracketInPathMiddle() {
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_CHARACTER);

        char bb = '1';

        dataObject_a.setChar(property1, bb);// c dataobject's a property has value boolean 'true'

        this.assertEquals(bb, dataObject_a.getChar(property1));
    }

    /* public void testGetCharacterConversionWithPathFromDefinedBooleanPropertyEqualSignBracketInPathDotSet() {
        property_c = new SDOProperty();
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_CHARACTER);
        property_c.setMany(true);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        char bb = '1';
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setChar(property2+"[number=1]", bb);

        this.assertEquals(bb, dataObject_a.getChar(property2+"[number=1]"));

    }*/

    //4. purpose: getChar with Undefined Character Property
    public void testGetCharacterConversionFromUnDefinedProperty() {        
        try {
            dataObject_a.getChar(UNDEFINED_PATH);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //5. purpose: getChar with Double Property
    public void testGetCharacterFromDouble() {
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_DOUBLE);
        double theValue = 10;
        dataObject_c.set(property, theValue);

        try {
            char value = dataObject_a.getChar(propertyPath_a_b_c);
            char controlValue = (char)theValue;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //6. purpose: getChar with float Property
    public void testGetCharacterFromFloat() {
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_FLOAT);
        float theValue = 10;
        dataObject_c.set(property, theValue);

        try {
            char value = dataObject_a.getChar(propertyPath_a_b_c);
            char controlValue = (char)theValue;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //7. purpose: getChar with int Property
    public void testGetCharacterFromInt() {
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_INT);
        int theValue = 10;
        dataObject_c.set(property, theValue);

        try {
            char value = dataObject_a.getChar(propertyPath_a_b_c);
            char controlValue = (char)theValue;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //8. purpose: getChar with long Property
    public void testGetCharacterFromLong() {
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_LONG);
        long theValue = 10;
        dataObject_c.set(property, theValue);
        try {
            char value = dataObject_a.getChar(propertyPath_a_b_c);
            char controlValue = (char)theValue;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //9. purpose: getChar with short Property
    public void testGetCharacterFromShort() {
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_SHORT);
        short theValue = 10;
        dataObject_c.set(property, theValue);

        try {
            char value = dataObject_a.getChar(propertyPath_a_b_c);
            char controlValue = (char)theValue;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //10. purpose: getChar with Defined String Property
    public void testGetCharacterConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_STRING);

        char str = 'c';
        Character B_STR = new Character(str);
        dataObject_a.setString(propertyPath_a_b_c, B_STR.toString());// add it to instance list

        this.assertEquals(str, dataObject_a.getChar(property));
    }

   

    //12. purpose: getChar with bytes property
    public void testGetCharacterFromBytes() {
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_BYTES);        
        dataObject_c.set(property, new byte[]{10, 100});;

        try {
            dataObject_a.getChar(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //13. purpose: getChar with decimal property
    public void testGetCharacterFromDecimal() {
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_DECIMAL);
        BigDecimal theValue = new BigDecimal(10);
        dataObject_c.set(property, theValue);

        try {
            char value = dataObject_a.getChar(propertyPath_a_b_c);
            char controlValue = (char)theValue.byteValue();
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //14. purpose: getChar with integer property
    public void testGetCharacterFromInteger() {
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_INTEGER);
        BigInteger theValue = new BigInteger("10");
        dataObject_c.set(property, theValue);

        try {            
            char value = dataObject_a.getChar(propertyPath_a_b_c);
            char controlValue = (char)theValue.byteValue();
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //22. purpose: getChar with date property
    public void testGetCharacterFromDate() {
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_DATE);
        dataObject_c.set(property, Calendar.getInstance().getTime());

        try {
            dataObject_a.getChar(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //purpose: getChar with null value
    public void testGGetCharacterWithNullArgument() {
        try {
            String p = null;
            dataObject_a.getChar(p);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }
}
