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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;

public class SDODataObjectGetShortByPositionalPathTest extends SDODataObjectGetByPositionalPathTestCases {
    public SDODataObjectGetShortByPositionalPathTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetShortByPositionalPathTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getShort with boolean property
    public void testGetShortFromBoolean() {
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_BOOLEAN);
        dataObject_c.set(property, true);
        try {
            short shortValue = dataObject_a.getShort(propertyPath_a_b_c);
            assertEquals(1, shortValue);
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getShort with Defined Byte Property
    public void testGetShortConversionFromDefinedByteProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_BYTE);

        byte by = 12;

        dataObject_a.setByte(propertyPath_a_b_c, by);// add it to instance list

        this.assertEquals((short)by, dataObject_a.getShort(propertyPath_a_b_c));
    }

    //3. purpose: getShort with Undefined Byte Property
    public void testGetShortConversionFromUnDefinedProperty() {
        
        try {
            dataObject_a.getShort(UNDEFINED_PATH);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //4. purpose: getShort with character property
    public void testGetShortFromCharacter() {
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_CHARACTER);
        dataObject_c.set(property, 'e');
        try {
            dataObject_a.getShort(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //5. purpose: getShort with Defined Double Property
    public void testGetShortConversionFromDefinedDoubleProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_DOUBLE);

        double db = 12;
        dataObject_a.setDouble(propertyPath_a_b_c, db);// add it to instance list

        this.assertEquals((short)db, dataObject_a.getShort(propertyPath_a_b_c));
    }

      //7. purpose: getShort with Defined float Property
    public void testGetShortConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_FLOAT);

        float fl = 12;
        dataObject_a.setFloat(propertyPath_a_b_c, fl);// add it to instance list

        this.assertEquals((short)fl, dataObject_a.getShort(propertyPath_a_b_c));
    }

    

    //9. purpose: getShort with Defined int Property
    public void testGetShortConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_INT);

        int in = 12;
        dataObject_a.setInt(propertyPath_a_b_c, in);// add it to instance list

        this.assertEquals((short)in, dataObject_a.getShort(propertyPath_a_b_c));
    }

  

    //11. purpose: getShort with Defined long Property
    public void testGetShortConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_LONG);

        long lg = 12;
        dataObject_a.setLong(propertyPath_a_b_c, lg);// add it to instance list

        this.assertEquals((short)lg, dataObject_a.getShort(propertyPath_a_b_c));
    }


    //13. purpose: getShort with Defined short Property
    public void testGetShortConversionFromDefinedShortProperty() {
        // dataObject's type add short property
       SDOProperty property =  ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
       property.setType(SDOConstants.SDO_SHORT);

        short shr = 12;
        dataObject_a.setShort(propertyPath_a_b_c, shr);// add it to instance list

        this.assertEquals(shr, dataObject_a.getShort(propertyPath_a_b_c));
    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyBracketPositionalSet() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_SHORT);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        short shr = 12;
        List b = new ArrayList();

        //b.add(bb);
        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setShort(property3, shr);

        this.assertEquals(shr, dataObject_a.getShort(property3));

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyDotPositionalSet() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_SHORT);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        short shr = 12;
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setShort(property + ".0", shr);

        this.assertEquals(shr, dataObject_a.getShort(property + ".0"));

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyBracketInPathMiddle() {
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_SHORT);

        short shr = 12;

        dataObject_a.setShort(property1, shr);// c dataobject's a property has value boolean 'true'

        this.assertEquals(shr, dataObject_a.getShort(property1));
    }

    /*public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyEqualSignBracketInPathDotSet() {
        property_c = new SDOProperty();
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_SHORT);
        property_c.setMany(true);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        short shr = 12;
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setShort(property2+"[number=1]", shr);

        this.assertEquals(shr, dataObject_a.getShort(property2+"[number=1]"));

    }*/

   
    //15. purpose: getShort with Defined String Property
    public void testGetShortConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_STRING);

        String str = "12";
        Short s_d = new Short(str);
        dataObject_a.setString(propertyPath_a_b_c, str);// add it to instance list

        this.assertEquals(s_d.shortValue(), dataObject_a.getShort(propertyPath_a_b_c));
    }

  

    //17. purpose: getShort with bytes property
    public void testGetShortFromBytes() {
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_BYTES);
        dataObject_c.set(property, new String("eee").getBytes());
        try {
            dataObject_a.getShort(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //18. purpose: getShort with decimal property
    public void testGetShortFromDecimal() {
       SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
       property.setType(SDOConstants.SDO_DECIMAL);
        dataObject_c.set(property, new BigDecimal(2));
        try {
            short value = dataObject_a.getShort(propertyPath_a_b_c);
            short controlValue = 2;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //19. purpose: getShort with integer property
    public void testGetShortFromInteger() {
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_INTEGER);
        dataObject_c.set(property, new BigInteger("3"));
        try {
            short value = dataObject_a.getShort(propertyPath_a_b_c);
            short controlValue = 3;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //22. purpose: getShort with date property
    public void testGetShortFromDate() {
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_DATE);
        dataObject_c.set(property, Calendar.getInstance().getTime());
        try {
            dataObject_a.getShort(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //purpose: getShort with null value
    public void testGetShortWithNullArgument() {
        try {
            String p = null;
            dataObject_a.getShort(p);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }
}
