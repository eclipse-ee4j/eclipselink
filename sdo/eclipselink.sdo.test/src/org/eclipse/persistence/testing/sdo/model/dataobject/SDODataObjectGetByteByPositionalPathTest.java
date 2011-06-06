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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;

public class SDODataObjectGetByteByPositionalPathTest extends SDODataObjectGetByPositionalPathTestCases {
    public SDODataObjectGetByteByPositionalPathTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetByteByPositionalPathTest" };
        TestRunner.main(arguments);
    }


    //1. purpose: getByte with boolean property
    public void testGetByteFromBoolean() {
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_BOOLEAN);
        dataObject_c.set(property, true);
        try {
            dataObject_a.getByte(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //2. purpose: getByte with Defined Byte Property
    public void testGetByteConversionFromDefinedByteProperty() {
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_BYTE);

        byte by = 12;

        dataObject_a.setByte(propertyPath_a_b_c, by);// add it to instance list

        this.assertEquals(by, dataObject_a.getByte(propertyPath_a_b_c));
    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyBracketPositionalSet() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_BYTE);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        byte by = 12;
        List b = new ArrayList();

        //b.add(bb);
        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setByte(property3, by);

        this.assertEquals(by, dataObject_a.getByte(property3));

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyDotPositionalSet() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_BYTE);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        byte by = 12;
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setByte(property + ".0", by);

        this.assertEquals(by, dataObject_a.getByte(property + ".0"));

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyBracketInPathMiddle() {
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_BYTE);
        byte by = 12;

        dataObject_a.setByte(property1, by);// c dataobject's a property has value boolean 'true'

        this.assertEquals(by, dataObject_a.getByte(property1));
    }

    /*public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyEqualSignBracketInPathDotSet() {
        property_c = new SDOProperty();
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTE);
        property_c.setMany(true);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        byte by = 12;
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setByte(property2+"[number=1]", by);

        this.assertEquals(by, dataObject_a.getByte(property2+"[number=1]"));

    }*/

    //3. purpose: getByte with Undefined Byte Property
    public void testGetByteConversionFromUnDefinedProperty() {
        try {
            dataObject_a.getByte(UNDEFINED_PATH);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //4. purpose: getByte with character property
    public void testGetByteFromCharacter() {        
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_CHARACTER);
        char theValue = 'e';
        dataObject_c.set(property, theValue);
        try {
            dataObject_a.getByte(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //5. purpose: getByte with Defined Double Property
    public void testGetByteConversionFromDefinedDoubleProperty() {
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DOUBLE);

        double db = 12;
        dataObject_a.setDouble(propertyPath_a_b_c, db);// add it to instance list

        this.assertEquals((byte)db, dataObject_a.getByte(propertyPath_a_b_c));
    }

  
    //7. purpose: getByte with Defined float Property
    public void testGetByteConversionFromDefinedFloatProperty() {
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_FLOAT);

        float fl = 12;
        dataObject_a.setFloat(propertyPath_a_b_c, fl);// add it to instance list

        this.assertEquals((byte)fl, dataObject_a.getByte(propertyPath_a_b_c));
    }

   
    //9. purpose: getByte with Defined int Property
    public void testGetByteConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_INT);

        int in = 12;
        dataObject_a.setInt(propertyPath_a_b_c, in);// add it to instance list

        this.assertEquals((byte)in, dataObject_a.getByte(propertyPath_a_b_c));
    }



    //11. purpose: getByte with Defined long Property
    public void testGetByteConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_LONG);

        long lg = 12;
        dataObject_a.setLong(propertyPath_a_b_c, lg);// add it to instance list

        this.assertEquals((byte)lg, dataObject_a.getByte(propertyPath_a_b_c));
    }


    //13. purpose: getByte with Defined short Property
    public void testGetByteConversionFromDefinedShortProperty() {
        // dataObject's type add short property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_SHORT);

        short shr = 12;
        dataObject_a.setShort(propertyPath_a_b_c, shr);// add it to instance list

        this.assertEquals((byte)shr, dataObject_a.getByte(propertyPath_a_b_c));
    }

   
    //15. purpose: getByte with Defined String Property
    public void testGetByteConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_STRING);

        String str = "12";
        Byte s_d = new Byte(str);
        dataObject_a.setString(propertyPath_a_b_c, str);// add it to instance list

        this.assertEquals(s_d.byteValue(), dataObject_a.getByte(property));
    }

    //17. purpose: getByte with bytes property
    public void testGetByteFromBytes() {        
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_BYTES);
        
        dataObject_c.set(property, new byte[]{10, 100});
        try {
            dataObject_a.getByte(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //18. purpose: getByte with decimal property
    public void testGetByteFromDecimal() {              
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_DECIMAL);
        BigDecimal theValue =new BigDecimal(10);
        dataObject_c.set(property, theValue);
        try {
            byte value = dataObject_a.getByte(propertyPath_a_b_c);
            byte controlValue = theValue.byteValue();
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //19. purpose: getByte with decimal property
    public void testGetByteFromInteger() {
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_INTEGER);
        BigInteger theValue =new BigInteger("10");
        dataObject_c.set(property, theValue);

        try {            
            byte value = dataObject_a.getByte(propertyPath_a_b_c);
            byte controlValue = theValue.byteValue();
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //20. purpose: getByte with date property
    public void testGetByteFromDate() {
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_DATE);
        dataObject_c.set(property, Calendar.getInstance().getTime());
        try {
            dataObject_a.getByte(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //purpose: getByte with nul value
    public void testGetByteWithNullArgument() {
        try {
            String p = null;
            dataObject_a.getByte(p);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }
}
