/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;

import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOException;

public class SDODataObjectGetBooleanWithIndexConversionTest extends SDODataObjectConversionTestCases {
    public SDODataObjectGetBooleanWithIndexConversionTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetBooleanWithIndexConversionTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionFromDefinedBooleanProperty() {
        // dataObject's type add boolean property
        SDOProperty property = type.getProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BOOLEAN);

        boolean b = true;

        dataObject.setBoolean(PROPERTY_INDEX, b);// add it to instance list

        assertEquals(b, dataObject.getBoolean(PROPERTY_INDEX));
    }

    //2. purpose: getBoolean with Undefined  Property
    public void testGetBooleanConversionFromUnDefinedProperty() {

        try {
            dataObject.getBoolean(1);
       } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");
    }

    //3. purpose: getBoolean with Byte property
    public void testGetBooleanFromByte() {
        SDOProperty property = type.getProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTE);
        byte theValue= 0;
        dataObject.set(property, theValue);

        try {
            boolean value =dataObject.getBoolean(PROPERTY_INDEX);
            boolean controlValue = false;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //4. purpose: getBoolean with character property
    public void testGetBooleanFromCharacter() {
        SDOProperty property = type.getProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_CHARACTER);

        Character b = '0';

        dataObject.setChar(PROPERTY_INDEX, b);// add it to instance list

        assertEquals(false, dataObject.getBoolean(PROPERTY_INDEX));

        /*SDOProperty property = new SDOProperty();
property.setName(PROPERTY_NAME);
property.setType(SDOConstants.SDO_CHARACTER);
type.addDeclaredProperty(property);

try {
    dataObject.getBoolean(PROPERTY_INDEX);
    fail("ClassCastException should be thrown.");
} catch (ClassCastException e) {
}*/
    }

    //5. purpose: getBoolean with Double Property
    public void testGetBooleanFromDouble() {
        SDOProperty property = type.getProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DOUBLE);

        Double b = (double) 0;

        dataObject.setDouble(PROPERTY_INDEX, b);// add it to instance list

        assertEquals(false, dataObject.getBoolean(PROPERTY_INDEX));

        /*SDOProperty property = new SDOProperty();
property.setName(PROPERTY_NAME);
property.setType(SDOConstants.SDO_DOUBLE);
type.addDeclaredProperty(property);

try {
    dataObject.getBoolean(PROPERTY_INDEX);
    fail("ClassCastException should be thrown.");
} catch (ClassCastException e) {
}*/
    }

    //6. purpose: getBoolean with float Property
    public void testGetBooleanFromFloat() {
        SDOProperty property = type.getProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_FLOAT);

        Float b = 1.01f;

        dataObject.setFloat(PROPERTY_INDEX, b);// add it to instance list

        assertEquals(true, dataObject.getBoolean(PROPERTY_INDEX));

        /*SDOProperty property = new SDOProperty();
property.setName(PROPERTY_NAME);
property.setType(SDOConstants.SDO_FLOAT);
type.addDeclaredProperty(property);

try {
    dataObject.getBoolean(PROPERTY_INDEX);
    fail("ClassCastException should be thrown.");
} catch (ClassCastException e) {
}*/
    }

    //7. purpose: getBooleab with int Property
    public void testGetBooleanFromInt() {
        SDOProperty property = type.getProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_INT);

        Integer b = 1;

        dataObject.setLong(PROPERTY_INDEX, b);// add it to instance list

        assertEquals(true, dataObject.getBoolean(PROPERTY_INDEX));

        /*SDOProperty property = new SDOProperty();
property.setName(PROPERTY_NAME);
property.setType(SDOConstants.SDO_FLOAT);
type.addDeclaredProperty(property);

try {
    dataObject.getBoolean(property);
    fail("ClassCastException should be thrown.");
} catch (ClassCastException e) {
}*/
    }

    //8. purpose: getBoolea with long Property
    public void testGetBooleanFromLong() {
        SDOProperty property = type.getProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_LONG);

        Long b = 1L;

        dataObject.setLong(PROPERTY_INDEX, b);// add it to instance list

        assertEquals(true, dataObject.getBoolean(PROPERTY_INDEX));

        /*SDOProperty property = new SDOProperty();
property.setName(PROPERTY_NAME);
property.setType(SDOConstants.SDO_LONG);
type.addDeclaredProperty(property);

try {
    dataObject.getBoolean(PROPERTY_INDEX);
    fail("ClassCastException should be thrown.");
} catch (ClassCastException e) {
}*/
    }

    //9. purpose: getBytes with short Property
    public void testGetBooleanFromShort() {

        /*SDOProperty property = new SDOProperty();
property.setName(PROPERTY_NAME);
property.setType(SDOConstants.SDO_SHORT);
type.addDeclaredProperty(property);

try {
    dataObject.getBoolean(PROPERTY_INDEX);
    fail("ClassCastException should be thrown.");
} catch (ClassCastException e) {
}*/
    }

    //10. purpose: getDouble with Defined String Property
    public void testGetBooleanConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        SDOProperty property = type.getProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_STRING);

        String str = "true";
        Boolean B_STR = Boolean.valueOf(str);
        dataObject.setString(PROPERTY_INDEX, str);// add it to instance list

        assertEquals(B_STR.booleanValue(), dataObject.getBoolean(PROPERTY_INDEX));
    }


    //12. purpose: getBoolean with bytes property
    public void testGetBooleanFromBytes() {
        SDOProperty property = type.getProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTES);
        dataObject.set(property, new byte[]{10, 100});

        try {
            dataObject.getBoolean(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //13. purpose: getBoolean with decimal property
    public void testGetBooleanFromDecimal() {
        SDOProperty property = type.getProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DECIMAL);
        dataObject.set(property, new BigDecimal(10));

        try {
            boolean value = dataObject.getBoolean(PROPERTY_INDEX);
            boolean controlValue = true;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //14. purpose: getBoolean with integer property
    public void testGetBooleanFromInteger() {
        SDOProperty property = type.getProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_INTEGER);
        dataObject.set(property, new BigInteger("0"));

        try {
            boolean value = dataObject.getBoolean(PROPERTY_INDEX);
            boolean controlValue = false;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //22. purpose: getDouble with date property
    public void testGetBooleanFromDate() {
        SDOProperty property = type.getProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DATE);
        dataObject.set(property, Calendar.getInstance().getTime());
        try {
            dataObject.getBoolean(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //purpose: getDouble with nul value
    public void testGetBooleanWithNullArgument() {
        try {
            int p = -1;
            dataObject.getDouble(p);
        } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");
    }
}
