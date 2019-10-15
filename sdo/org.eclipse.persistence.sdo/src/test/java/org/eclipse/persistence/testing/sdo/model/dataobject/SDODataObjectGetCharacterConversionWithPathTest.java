/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.model.dataobject;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;

public class SDODataObjectGetCharacterConversionWithPathTest extends SDODataObjectConversionWithPathTestCases {
    public SDODataObjectGetCharacterConversionWithPathTest(String name) {
        super(name);
    }

  public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetCharacterConversionWithPathTest" };
        TestRunner.main(arguments);
    }


    //1. purpose: getChar with Boolean property
    public void testGetCharacterFromBoolean() {
        property_c = new SDOProperty(aHelperContext);

        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BOOLEAN);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        boolean theValue = true;
        dataObject_c.set(property_c, theValue);
        try {
            dataObject_a.getChar(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //2. purpose: getChar with Byte property
    public void testGetCharacterFromByte() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        byte theValue = 1;
        dataObject_c.set(property_c, theValue);
        try {
            dataObject_a.getChar(propertyPath_a_b_c);
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
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_CHARACTER);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        char b = '1';

        dataObject_a.setChar(propertyPath_a_b_c, b);// add it to instance list

        this.assertEquals(b, dataObject_a.getChar(propertyPath_a_b_c));
    }

    //4. purpose: getChar with Undefined Character Property
    public void testGetCharacterConversionFromUnDefinedCharacterProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_CHARACTER);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getChar(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //5. purpose: getChar with Double Property
    public void testGetCharacterFromDouble() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DOUBLE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        double theValue = 2;
        dataObject_c.set(property_c, theValue);
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
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_FLOAT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        float theValue = 2;
        dataObject_c.set(property_c, theValue);
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
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        int theValue = 2;
        dataObject_c.set(property_c, theValue);
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
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_LONG);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        long theValue = 2;
        dataObject_c.set(property_c, theValue);
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
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_SHORT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        short theValue = 2;
        dataObject_c.set(property_c, theValue);
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
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        char str = 'c';
        Character B_STR = new Character(str);
        dataObject_a.setString(propertyPath_a_b_c, B_STR.toString());// add it to instance list

        this.assertEquals(str, dataObject_a.getChar(property));
    }

    //11. purpose: getChar with Undefined boolean Property
    public void testGetCharacterConversionFromUnDefinedStringProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getChar(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //12. purpose: getChar with bytes property
    public void testGetCharacterFromBytes() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTES);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        byte[] theValue = new byte[]{10,100};;
        dataObject_c.set(property_c, theValue);
        try {
            dataObject_a.getChar(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //13. purpose: getChar with decimal property
    public void testGetCharacterFromDecimal() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DECIMAL);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        BigDecimal theValue = new BigDecimal(2);
        dataObject_c.set(property_c, theValue);
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
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INTEGER);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        BigInteger theValue = new BigInteger("2");
        dataObject_c.set(property_c, theValue);
        try {
            char value = dataObject_a.getChar(propertyPath_a_b_c);
            char controlValue = (char)theValue.byteValue();
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink;
        } catch (ClassCastException e) {
        }
    }

    //22. purpose: getChar with date property
    public void testGetCharacterFromDate() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DATE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        dataObject_c.set(property_c, Calendar.getInstance().getTime());
        try {
            dataObject_a.getChar(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //purpose: getChar with nul value
    public void testGGetCharacterWithNullArgument() {
        try {
            String p = null;
            dataObject_a.getChar(p);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

         //22. purpose: getDouble from null
    public void testGetCharFromNull() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_CHARACTER);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        dataObject_c.set(property_c, null);

        char value = dataObject_a.getChar(propertyPath_a_b_c);
        char charValue = '\u0000';
        assertEquals(charValue, value);
        DataObject doNext = dataObject_a.getDataObject("PName-a");
        doNext = doNext.getDataObject("PName-b");
        Property prop  = doNext.getInstanceProperty("PName-c");
        char value2 =  doNext.getChar(prop);
        assertEquals(charValue, value2);
    }
}
