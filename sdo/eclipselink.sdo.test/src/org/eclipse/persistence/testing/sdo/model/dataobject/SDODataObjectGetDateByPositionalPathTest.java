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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;

public class SDODataObjectGetDateByPositionalPathTest extends SDODataObjectGetByPositionalPathTestCases {
    public SDODataObjectGetDateByPositionalPathTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetDateByPositionalPathTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getDate with Boolean property
    public void testGetDateFromBooleane() {
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_BOOLEAN);
        dataObject_c.set(property_c, "true");
        try {
            dataObject_a.getDate(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //2. purpose: getDate with Byte property
    public void testGetDateFromByte() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        Object obj = dataObject_a.getDate(propertyPath_a_b_c);
        assertNull(obj);
    }

    //3. purpose: getDate with character property
    public void testGetDateFromCharacter() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_CHARACTER);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        Object obj = dataObject_a.getDate(propertyPath_a_b_c);
        assertNull(obj);
    }

    //4. purpose: getDate with Double Property
    public void testGetDateFromDouble() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DOUBLE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        Object obj = dataObject_a.getDate(propertyPath_a_b_c);
        assertNull(obj);
    }

    //5. purpose: getDate with float Property
    public void testGetDateFromFloat() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_FLOAT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        Object obj = dataObject_a.getDate(propertyPath_a_b_c);
        assertNull(obj);
    }

    //7. purpose: getDate with int Property
    public void testGetDateFromInt() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        Object obj = dataObject_a.getDate(propertyPath_a_b_c);
        assertNull(obj);
    }

    //8. purpose: getDate with Defined long Property
    public void testGetDateConversionFromDefinedLongProperty() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_LONG);

        long l = 12;
        Date d = new Date(l);

        dataObject_c.setLong(property_c, l);// add it to instance list

        this.assertEquals(d, dataObject_a.getDate(propertyPath_a_b_c));
    }

    //10. purpose: getDate with short Property
    public void testGetDateFromShort() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_SHORT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        Object obj = dataObject_a.getDate(propertyPath_a_b_c);
        assertNull(obj);
    }

    //11. purpose: getDate with Defined String Property
    public void testGetDateConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_STRING);
        ;
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2001);
        controlCalendar.set(Calendar.MONTH, 0);
        controlCalendar.set(Calendar.DATE, 1);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date d = controlCalendar.getTime();

        dataObject_c.setString(property_c, "2001-01-01");// add it to instance list

        this.assertEquals(d, dataObject_a.getDate(propertyPath_a_b_c));
    }

    //11. purpose: getDate with Defined String Property
    public void testGetDateConversionFromDefinedStringPropertyList() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_STRING);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2001);
        controlCalendar.set(Calendar.MONTH, 0);
        controlCalendar.set(Calendar.DATE, 1);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date d = controlCalendar.getTime();

        List objects = new ArrayList();
        objects.add("2001-01-01");
        dataObject_c.set(property_c, objects);// add it to instance list

        this.assertEquals(d, dataObject_a.getDate(propertyPath_a_b_c + ".0"));
    }

    //13. purpose: getDate with bytes property
    public void testGetDateFromBytes() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTES);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        Object obj = dataObject_a.getDate(propertyPath_a_b_c);
        assertNull(obj);
    }

    //14. purpose: getBoolean with decimal property
    public void testGetBooleanFromDecimal() {
        SDOProperty prop = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_DECIMAL);
        dataObject_c.set(property_c, new BigDecimal(15));
        try {
            boolean value = dataObject_a.getBoolean(propertyPath_a_b_c);
            boolean controlValue = true;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //15. purpose: getDate with integer property
    public void testGetDateFromInteger() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INTEGER);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        Object obj = dataObject_a.getDate(propertyPath_a_b_c);
        assertNull(obj);
    }

    //16. purpose: getDate with Defined Date Property
    public void testGetDateConversionFromDefinedDateProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DATE);

        long l = 12000;
        Date d = new Date(l);
        dataObject_a.setDate(propertyPath_a_b_c, d);// add it to instance list

        this.assertEquals(d, dataObject_a.getDate(propertyPath_a_b_c));
    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetDateConversionWithPathFromDefinedBooleanPropertyBracketPositionalSet() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DATE);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        long l = 12000;
        Date d = new Date(l);
        List b = new ArrayList();

        //b.add(bb);
        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setDate(property3, d);

        this.assertEquals(d, dataObject_a.getDate(property3));

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetDateConversionWithPathFromDefinedBooleanPropertyDotPositionalSet() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DATE);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        long l = 12000;
        Date d = new Date(l);
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setDate(property + ".0", d);

        this.assertEquals(d, dataObject_a.getDate(property + ".0"));

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyBracketInPathMiddle() {
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DATE);

        long l = 12000;
        Date d = new Date(l);

        dataObject_a.setDate(property1, d);// c dataobject's a property has value boolean 'true'

        this.assertEquals(d, dataObject_a.getDate(property1));
    }

    /*public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyEqualSignBracketInPathDotSet() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DATE);
        property_c.setMany(true);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        long l = 12000;
        Date d = new Date(l);
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setDate(property2+"[number=1]", d);

        this.assertEquals(d, dataObject_a.getDate(property2+"[number=1]"));

    }*/

    //12. purpose: getDate with Undefined string Property
    public void testGetDateConversionFromUnDefinedProperty() {
        try {
            dataObject_a.getDate(UNDEFINED_PATH);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());                        
        }
    }

    //purpose: getDate with nul value
    public void testGetDateWithNullArgument() {
        try {
            String p = null;
            dataObject_a.getDate(p);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());                        
        }
    }
}
