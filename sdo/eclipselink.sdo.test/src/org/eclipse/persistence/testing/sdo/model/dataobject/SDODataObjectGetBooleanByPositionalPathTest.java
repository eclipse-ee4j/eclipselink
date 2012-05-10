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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;

public class SDODataObjectGetBooleanByPositionalPathTest extends SDODataObjectGetByPositionalPathTestCases {
    public SDODataObjectGetBooleanByPositionalPathTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetBooleanByPositionalPathTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyBracketPositionalSet() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_BOOLEAN);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        Boolean bb = new Boolean(true);
        List b = new ArrayList();

        //b.add(bb);
        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setBoolean(property3, true);
        boolean value = dataObject_a.getBoolean(property3);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(false);
        this.assertEquals(bb.booleanValue(), value);

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyDotPositionalSet() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_BOOLEAN);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        Boolean bb = new Boolean(true);
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setBoolean(property + ".0", true);

        boolean value = dataObject_a.getBoolean(property + ".0");
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(false);

        this.assertEquals(true, value);

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyBracketInPathMiddle() {
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_BOOLEAN);              
        
        Boolean b = new Boolean(true);

        dataObject_a.setBoolean(property1, true);// c dataobject's a property has value boolean 'true'

        this.assertEquals(true, dataObject_a.getBoolean(property1));
    }

    /*public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyEqualSignBracketInPathDotSet() {
    property_c = new SDOProperty(aHelperContext);
    property_c.setName(PROPERTY_NAME_C);
    property_c.setType(SDOConstants.SDO_BOOLEAN);
    property_c.setMany(true);
    type_c.addDeclaredProperty(property_c);
    dataObject_c.setType(type_c);

    Boolean bb = new Boolean(true);
    List b = new ArrayList();

    dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
    dataObject_a.setBoolean(property2+"[number=1]", true);

    this.assertEquals(true, dataObject_a.getBoolean(property2+"[number=1]"));

    }*/

    //2. purpose: getBoolean with Undefined Boolean Property
    public void testGetBooleanConversionFromUnDefinedProperty() {
        try {
            dataObject_a.getBoolean(UNDEFINED_PATH);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());                        
        }
    }

    //3. purpose: getBoolean with Byte property
    public void testGetBooleanFromByte() {        
        SDOProperty prop = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_BYTE);
        byte theByte = 0;
        dataObject_c.set(prop, theByte);
        
        try {
            boolean value = dataObject_a.getBoolean(property);
            boolean controlValue = false;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //4. purpose: getBoolean with character property
    public void testGetBooleanFromCharacter() {

        /*property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_CHARACTER);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        try {
        dataObject_a.getBoolean(property);
        fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }*/
    }

    //5. purpose: getBoolean with Double Property
    public void testGetBooleanFromDouble() {

        /*property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DOUBLE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        try {
        dataObject_a.getBoolean(property);
        fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }*/
    }

    //6. purpose: getBoolean with float Property
    public void testGetBooleanFromFloat() {

        /*property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_FLOAT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        try {
        dataObject_a.getBoolean(property);
        fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }*/
    }

    //7. purpose: getBooleab with int Property
    public void testGetBooleanFromInt() {

        /*property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        try {
        dataObject_a.getBoolean(property);
        fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }*/
    }

    //8. purpose: getBoolea with long Property
    public void testGetBooleanFromLong() {

        /*property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_LONG);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        try {
        dataObject_a.getBoolean(property);
        fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }*/
    }

    //9. purpose: getBytes with short Property
    public void testGetBooleanFromShort() {

        /*property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_SHORT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        try {
        dataObject_a.getBoolean(property);
        fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }*/
    }

    //10. purpose: getBoolean with Defined String Property
    public void testGetBooleanConversionFromDefinedStringProperty() {
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_STRING);

        String str = "true";
        Boolean B_STR = new Boolean(str);
        dataObject_c.setString(property_c, str);// add it to instance list

        this.assertEquals(B_STR.booleanValue(), dataObject_a.getBoolean(property));
    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanStringBracketPositionalSet() {
        // dataObject's type add boolean property        
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_BOOLEAN);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        String str = "true";
        Boolean bb = new Boolean(str);
        List b = new ArrayList();

        //b.add(bb);
        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.set(property3, bb);

        boolean value = dataObject_a.getBoolean(property3);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(false);
        this.assertEquals(bb.booleanValue(), value);

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedStringPropertyDotPositionalSet() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_STRING);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        String str = "true";
        Boolean bb = new Boolean(str);
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.set(property + ".0", bb);
        boolean value = dataObject_a.getBoolean(property + ".0");
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(false);
        this.assertEquals(true, value);

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedStringPropertyBracketInPathMiddle() {
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_BOOLEAN);

        String str = "true";
        Boolean b = new Boolean(str);

        dataObject_a.set(property1, b);// c dataobject's a property has value boolean 'true'

        this.assertEquals(true, dataObject_a.getBoolean(property1));
    }

    /* public void testGetBooleanConversionWithPathFromDefinedStringPropertyEqualSignBracketInPathDotSet() {
    property_c = new SDOProperty(aHelperContext);
    property_c.setName(PROPERTY_NAME_C);
    property_c.setType(SDOConstants.SDO_BOOLEAN);
    property_c.setMany(true);
    type_c.addDeclaredProperty(property_c);
    dataObject_c.setType(type_c);

    String str = "true";
    Boolean bb = new Boolean(str);
    List b = new ArrayList();

    dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
    dataObject_a.set(property2+"[number=1]", bb);

    this.assertEquals(true, dataObject_a.getBoolean(property2+"[number=1]"));

    }*/

    //12. purpose: getBoolean with bytes property
    public void testGetBooleanFromBytes() {        
        SDOProperty prop = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_BYTES);
        dataObject_c.set(prop, new String("eee").getBytes());
        try {
            dataObject_a.getBoolean(property);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());                        
        }
    }

    //13. purpose: getBoolean with decimal property
    public void testGetBooleanFromDecimal() {        
        SDOProperty prop = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_DECIMAL);
        dataObject_c.set(prop, new BigDecimal("3"));
        try {
            boolean value = dataObject_a.getBoolean(property);
            boolean controlValue = true;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //14. purpose: getBoolean with integer property
    public void testGetBooleanFromInteger() {        
        SDOProperty prop = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_INTEGER);
        dataObject_c.set(prop, new BigInteger("0"));
        try {
            boolean value = dataObject_a.getBoolean(property);
            boolean controlValue = false;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //22. purpose: getBoolean with date property
    public void testGetBooleanFromDate() {
        SDOProperty prop = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_DATE);
        dataObject_c.set(prop, Calendar.getInstance().getTime());
        try {
            dataObject_a.getBoolean(property);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());                        
        }
    }

    //purpose: getDouble with nul value
    public void testGetBooleanWithNullArgument() {
        try {
            String path = null;
            dataObject_a.getBoolean(path);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());                        
        }
    }
}
