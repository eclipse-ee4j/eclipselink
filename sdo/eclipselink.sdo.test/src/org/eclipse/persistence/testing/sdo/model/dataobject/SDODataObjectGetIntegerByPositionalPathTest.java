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

public class SDODataObjectGetIntegerByPositionalPathTest extends SDODataObjectGetByPositionalPathTestCases {
    public SDODataObjectGetIntegerByPositionalPathTest(String name) {
        super(name);
    }

   public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetIntegerByPositionalPathTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getBigInteger with boolean property
    public void testGetIntegerFromBoolean() {
        SDOProperty prop =((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_BOOLEAN);      
        dataObject_c.set(prop, true);

        try {
            BigInteger value = dataObject_a.getBigInteger(propertyPath_a_b_c);
            assertEquals(null, value);
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getBigInteger with byte property
    public void testGetIntegerFromByte() {        
        SDOProperty prop =((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_BYTE);      
        byte theByte = 10;
        dataObject_c.set(prop, theByte);

        try {
            BigInteger value = dataObject_a.getBigInteger(propertyPath_a_b_c);
            BigInteger controlValue = new BigInteger("10");
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //3. purpose: getBigInteger with character property
    public void testGetIntegerFromCharacter() {
          
        SDOProperty prop =((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_CHARACTER);      
        dataObject_c.set(prop, 'e');

        try {
            dataObject_a.getBigInteger(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //5. purpose: getBigInteger with Defined Double Property
    public void testGetIntegerConversionFromDefinedDoubleProperty() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DOUBLE);

        double db = 12;
        int il = (int)db;
        BigInteger bd = new BigInteger(String.valueOf(il));
        dataObject_a.setDouble(propertyPath_a_b_c, db);// add it to instance list

        this.assertEquals(bd, dataObject_a.getBigInteger(property));
    }

    //6. purpose: getBigInteger with Undefined Double Property
    public void testGetIntegerConversionFromUnDefinedProperty() {
    

        try {
            dataObject_a.getBigInteger(UNDEFINED_PATH);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //7. purpose: getBigInteger with Defined float Property
    public void testGetIntegerConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_FLOAT);

        float fl = 12;
        int il = (int)fl;
        BigInteger bd = new BigInteger(String.valueOf(il));
        dataObject_a.setFloat(propertyPath_a_b_c, fl);// add it to instance list

        this.assertEquals(bd, dataObject_a.getBigInteger(property));
    }

   
    //9. purpose: getBigInteger with Defined int Property
    public void testGetIntegerConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_INT);

        int in = 12;
        BigInteger bd = new BigInteger(String.valueOf(in));
        dataObject_a.setInt(propertyPath_a_b_c, in);// add it to instance list

        this.assertEquals(bd, dataObject_a.getBigInteger(propertyPath_a_b_c));
    }


    //11. purpose: getBigInteger with Defined long Property
    public void testGetIntegerConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_LONG);

        long lg = 12;
        BigInteger bd = new BigInteger(String.valueOf(lg));
        dataObject_a.setLong(propertyPath_a_b_c, lg);// add it to instance list

        this.assertEquals(bd, dataObject_a.getBigInteger(propertyPath_a_b_c));
    }


    //13. purpose: getBigInteger with Undefined short Property
    public void testGetIntegerConversionFromUnDefinedShortProperty() {
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_SHORT);

        try {
            BigInteger value = dataObject_a.getBigInteger(propertyPath_a_b_c);            
            assertEquals(new BigInteger("0"), value); // 6151874: default used to be null
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //14. purpose: getBigInteger with Defined String Property
    public void testGetgetIntegerConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_STRING);

        String str = "12";
        BigInteger bd = new BigInteger(str);
        dataObject_a.setString(propertyPath_a_b_c, str);// add it to instance list

        this.assertEquals(bd, dataObject_a.getBigInteger(propertyPath_a_b_c));
    }


    /*    //16. purpose: getBigInteger with Defined Bytes Property !! OX Pro    !!
    public void testGetIntegerConversionFromDefinedBytesProperty() {
        // dataObject's type add boolean property
        property_c = new SDOProperty();
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTES);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        byte[] b = { 12, 13 };
        BigInteger bin = new BigInteger(b);

        dataObject_a.setBytes(propertyPath_a_b_c, b);// add it to instance list

        this.assertEquals(bin, dataObject_a.getBigInteger(propertyPath_a_b_c));
    }*/

   
    //18. purpose: getBigInteger with Defined Decimal Property
    public void testGetIntegerConversionFromDefinedDecimalProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DECIMAL);

        int db = 12;
        BigDecimal bd = new BigDecimal(db);
        BigInteger bd_ = new BigInteger(String.valueOf(bd));
        dataObject_a.setBigDecimal(propertyPath_a_b_c, bd);// add it to instance list

        this.assertEquals(bd_, dataObject_a.getBigInteger(property));
    }



    //20. purpose: getBigInteger with Defined integer Property
    public void testGetIntegerConversionFromDefinedIntegerProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_INTEGER);

        BigInteger bi = new BigInteger("12");
        dataObject_a.setBigInteger(propertyPath_a_b_c, bi);// add it to instance list

        this.assertEquals(bi, dataObject_a.getBigInteger(propertyPath_a_b_c));
    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyBracketPositionalSet() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_INTEGER);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        BigInteger bi = new BigInteger("12");
        List b = new ArrayList();

        //b.add(bb);
        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setBigInteger(property3, bi);

        this.assertEquals(bi, dataObject_a.getBigInteger(property3));

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyDotPositionalSet() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_INTEGER);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        BigInteger bi = new BigInteger("12");
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setBigInteger(property + ".0", bi);

        this.assertEquals(bi, dataObject_a.getBigInteger(property + ".0"));

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyBracketInPathMiddle() {
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_INTEGER);

        BigInteger bi = new BigInteger("12");

        dataObject_a.setBigInteger(property1, bi);// c dataobject's a property has value boolean 'true'

        this.assertEquals(bi, dataObject_a.getBigInteger(property1));
    }

    /*public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyEqualSignBracketInPathDotSet() {
        property_c = new SDOProperty();
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BIGINTEGER);
        property_c.setMany(true);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        BigInteger bi = new BigInteger("12");
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setBigInteger(property2+"[number=1]", bi);

        this.assertEquals(bi, dataObject_a.getBigInteger(property2+"[number=1]"));

    }*/

  
    //22. purpose: getBigInteger with date property
    public void testGetIntegerFromDate() {

        SDOProperty prop =((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_DATE);      
        dataObject_c.set(prop, Calendar.getInstance().getTime());
        try {
            dataObject_a.getBigInteger(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //purpose: getBigInteger with nul value
    public void testGetIntegerWithNullArgument() {
        try {
            String p = null;
            dataObject_a.getBigInteger(p);
        } catch (ClassCastException e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }
}
