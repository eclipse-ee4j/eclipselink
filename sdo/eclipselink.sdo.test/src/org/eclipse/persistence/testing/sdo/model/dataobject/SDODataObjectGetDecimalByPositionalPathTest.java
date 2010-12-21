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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;

public class SDODataObjectGetDecimalByPositionalPathTest extends SDODataObjectGetByPositionalPathTestCases {
    public SDODataObjectGetDecimalByPositionalPathTest(String name) {
        super(name);
    }
    
   public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetDecimalByPositionalPathTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getBigDecimal with boolean property
    public void testGetDecimalFromBoolean() {
        SDOProperty prop = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_BOOLEAN);
        dataObject_c.set(prop, true);
        try {
            BigDecimal value = dataObject_a.getBigDecimal(propertyPath_a_b_c);
            assertEquals(null, value);
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getBigDecimal with byte property
    public void testGetDecimalFromByte() {
        
        SDOProperty prop = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_BYTE);
        byte theByte = 100;
        dataObject_c.set(prop, theByte);        

        try {
            BigDecimal value = dataObject_a.getBigDecimal(propertyPath_a_b_c);
            BigDecimal controlValue = new BigDecimal(100);
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //3. purpose: getBigDecimal with character property
    public void testGetDecimalFromCharacter() {
        
        SDOProperty prop = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_CHARACTER);
        dataObject_c.set(prop, 'e');

        try {
            dataObject_a.getBigDecimal(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //5. purpose: getBigDecimal with Defined Double Property
    public void testGetDecimalConversionFromDefinedDoubleProperty() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DOUBLE);

        double db = 12;
        BigDecimal bd = new BigDecimal(db);
        dataObject_a.setDouble(propertyPath_a_b_c, db);// add it to instance list

        this.assertEquals(bd, dataObject_a.getBigDecimal(propertyPath_a_b_c));
    }

  
    //7. purpose: getBigDecimal with Defined float Property
    public void testGetIntConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_FLOAT);

        float fl = 12;
        BigDecimal bd = new BigDecimal(fl);
        dataObject_a.setFloat(propertyPath_a_b_c, fl);// add it to instance list

        this.assertEquals(bd, dataObject_a.getBigDecimal(propertyPath_a_b_c));
    }


    //9. purpose: getBigDecimal with Defined int Property
    public void testGetDecimalConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_INT);

        int in = 12;
        BigDecimal bd = new BigDecimal(in);
        dataObject_a.setInt(propertyPath_a_b_c, in);// add it to instance list

        this.assertEquals(bd, dataObject_a.getBigDecimal(propertyPath_a_b_c));
    }

   
    //11. purpose: getBigDecimal with Defined long Property
    public void testGetDecimalConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_LONG);

        long lg = 12;
        BigDecimal bd = new BigDecimal(lg);
        dataObject_a.setLong(propertyPath_a_b_c, lg);// add it to instance list

        this.assertEquals(bd, dataObject_a.getBigDecimal(propertyPath_a_b_c));
    }

  
    //14. purpose: getBigDecimal with Defined String Property
    public void testGetDecimalConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_STRING);

        String str = "12";
        BigDecimal bd = new BigDecimal(str);
        dataObject_a.setString(propertyPath_a_b_c, str);// add it to instance list

        this.assertEquals(bd, dataObject_a.getBigDecimal(property));
    }

   
    //17. purpose: getBigDecimal with bytes property
    public void testGetDecimalFromBytes() {        
        SDOProperty prop = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_BYTES);
        dataObject_c.set(prop, new String("eee").getBytes());

        try {
            dataObject_a.getBigDecimal(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //18. purpose: getBigDecimal with Defined Decimal Property
    public void testGetDecimalConversionFromDefinedDecimalProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DECIMAL);

        int db = 12;
        BigDecimal bd = new BigDecimal(db);
        dataObject_a.setBigDecimal(propertyPath_a_b_c, bd);// add it to instance list

        this.assertEquals(bd, dataObject_a.getBigDecimal(propertyPath_a_b_c));
    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyBracketPositionalSet() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DECIMAL);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        int db = 12;
        BigDecimal bd = new BigDecimal(db);
        List b = new ArrayList();

        //b.add(bb);
        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setBigDecimal(property3, bd);

        this.assertEquals(bd, dataObject_a.getBigDecimal(property3));

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyDotPositionalSet() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DECIMAL);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        int db = 12;
        BigDecimal bd = new BigDecimal(db);
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setBigDecimal(property + ".0", bd);

        this.assertEquals(bd, dataObject_a.getBigDecimal(property + ".0"));

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyBracketInPathMiddle() {
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DECIMAL);

        int db = 12;
        BigDecimal bd = new BigDecimal(db);

        dataObject_a.setBigDecimal(property1, bd);// c dataobject's a property has value boolean 'true'

        this.assertEquals(bd, dataObject_a.getBigDecimal(property1));
    }

    /*public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyEqualSignBracketInPathDotSet() {
        property_c = new SDOProperty();
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DECIMAL);
        property_c.setMany(true);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        int db = 12;
        BigDecimal bd = new BigDecimal(db);
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setBigDecimal(property2+"[number=1]", bd);

        this.assertEquals(bd, dataObject_a.getBigDecimal(property2+"[number=1]"));

    }*/

    //19. purpose: getBigDecimal with Undefined decimal Property
    public void testGetDecimalConversionFromUnDefinedProperty() {        
        try {
            dataObject_a.getBigDecimal(UNDEFINED_PATH);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //20. purpose: getDecimal with Defined integer Property
    public void testGetDecimalConversionFromDefinedIntegerProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_INTEGER);

        BigInteger bi = new BigInteger("12");
        BigDecimal bd = new BigDecimal(bi);
        dataObject_a.setBigInteger(propertyPath_a_b_c, bi);// add it to instance list

        this.assertEquals(bd, dataObject_a.getBigDecimal(propertyPath_a_b_c));
    }



    //22. purpose: getBigDecimal with date property
    public void testGetDecimalFromDate() {        
        SDOProperty prop = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_DATE);
        dataObject_c.set(prop, Calendar.getInstance().getTime());

        try {
            dataObject_a.getBigDecimal(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //purpose: getBigDecimal with nul value
    public void testGetDecimaltWithNullArgument() {
        try {
            String p = null;
            dataObject_a.getBigDecimal(p);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }
}
