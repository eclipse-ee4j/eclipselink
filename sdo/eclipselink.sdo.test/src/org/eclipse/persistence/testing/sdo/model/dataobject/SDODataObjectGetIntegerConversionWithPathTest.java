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
import java.util.Calendar;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;

public class SDODataObjectGetIntegerConversionWithPathTest extends SDODataObjectConversionWithPathTestCases {
    public SDODataObjectGetIntegerConversionWithPathTest(String name) {
        super(name);
    }
    
     public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetIntegerConversionWithPathTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getBigInteger with boolean property
    public void testGetIntegerFromBoolean() {        
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BOOLEAN);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        dataObject_c.set(property_c, true);

        try {
            BigInteger value = dataObject_a.getBigInteger(propertyPath_a_b_c);
            assertEquals(null, value);
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getBigInteger with byte property
    public void testGetIntegerFromByte() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        byte theByte = 6;
        dataObject_c.set(property_c, theByte);

        try {
            BigInteger value = dataObject_a.getBigInteger(propertyPath_a_b_c);
            BigInteger controlValue = new BigInteger("6");
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //3. purpose: getBigInteger with character property
    public void testGetIntegerFromCharacter() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_CHARACTER);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);       
        dataObject_c.set(property_c, 'e');

        try {
            dataObject_a.getBigInteger(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //5. purpose: getBigInteger with Defined Double Property
    public void testGetIntegerConversionFromDefinedDoubleProperty() {
        // dataObject's type add boolean property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DOUBLE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        double db = 12;
        int il = (int)db;
        BigInteger bd = new BigInteger(String.valueOf(il));
        dataObject_a.setDouble(propertyPath_a_b_c, db);// add it to instance list

        this.assertEquals(bd, dataObject_a.getBigInteger(property));
    }

    //6. purpose: getBigInteger with Undefined Double Property
    public void testGetIntegerConversionFromUnDefinedDoubleProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DOUBLE);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getBigInteger(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //7. purpose: getBigInteger with Defined float Property
    public void testGetIntegerConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_FLOAT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
               
        float fl = 12;
        int il = (int)fl;
        BigInteger bd = new BigInteger(String.valueOf(il));
        dataObject_a.setFloat(propertyPath_a_b_c, fl);// add it to instance list

        this.assertEquals(bd, dataObject_a.getBigInteger(property));
    }

    //8. purpose: getBigInteger with Undefined float Property
    public void testGetIntegerConversionFromUnDefinedFloatProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_FLOAT);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getBigInteger(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //9. purpose: getBigInteger with Defined int Property
    public void testGetIntegerConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);        

        int in = 12;
        BigInteger bd = new BigInteger(String.valueOf(in));
        dataObject_a.setInt(propertyPath_a_b_c, in);// add it to instance list

        this.assertEquals(bd, dataObject_a.getBigInteger(propertyPath_a_b_c));
    }

    //10. purpose: getBigInteger with Undefined int Property
    public void testGetIntegerConversionFromUnDefinedIntProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INT);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getBigInteger(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //11. purpose: getBigInteger with Defined long Property
    public void testGetIntegerConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_LONG);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        long lg = 12;
        BigInteger bd = new BigInteger(String.valueOf(lg));
        dataObject_a.setLong(propertyPath_a_b_c, lg);// add it to instance list

        this.assertEquals(bd, dataObject_a.getBigInteger(propertyPath_a_b_c));
    }

    //12. purpose: getBigInteger with Undefined long Property
    public void testGetgetIntegerConversionFromUnDefinedLongProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_LONG);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getBigInteger(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //13. purpose: getBigInteger with Undefined short Property
    public void testGetIntegerConversionFromUnDefinedShortProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_SHORT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

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
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        String str = "12";
        BigInteger bd = new BigInteger(str);
        dataObject_a.setString(propertyPath_a_b_c, str);// add it to instance list

        this.assertEquals(bd, dataObject_a.getBigInteger(propertyPath_a_b_c));
    }

    //15. purpose: getBigInteger with Undefined string Property
    public void testGetIntegerConversionFromUnDefinedStringProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getBigInteger(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //16. purpose: getBigInteger with Defined Bytes Property !! OX Pro    !!
    public void testGetIntegerConversionFromDefinedBytesProperty() {
        // dataObject's type add boolean property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTES);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        byte[] b = { 12, 13 };
        BigInteger bin = new BigInteger(b);

        dataObject_a.setBytes(propertyPath_a_b_c, b);// add it to instance list

        this.assertEquals(bin, dataObject_a.getBigInteger(propertyPath_a_b_c));
    }

    //17. purpose: getBigInteger with Undefined Bytes Property
    public void testGetIntegerConversionFromUnDefinedBytesProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTES);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getBigInteger(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //18. purpose: getBigInteger with Defined Decimal Property
    public void testGetIntegerConversionFromDefinedDecimalProperty() {
        // dataObject's type add int property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DECIMAL);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        int db = 12;
        BigDecimal bd = new BigDecimal(db);
        BigInteger bd_ = new BigInteger(String.valueOf(bd));
        dataObject_a.setBigDecimal(propertyPath_a_b_c, bd);// add it to instance list

        this.assertEquals(bd_, dataObject_a.getBigInteger(property));
    }

    //19. purpose: getBigInteger with Undefined decimal Property
    public void testGetIntegerConversionFromUnDefinedDecimalProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DECIMAL);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getBigInteger(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //20. purpose: getBigInteger with Defined integer Property
    public void testGetIntegerConversionFromDefinedIntegerProperty() {
        // dataObject's type add int property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INTEGER);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        BigInteger bi = new BigInteger("12");
        dataObject_a.setBigInteger(propertyPath_a_b_c, bi);// add it to instance list

        this.assertEquals(bi, dataObject_a.getBigInteger(propertyPath_a_b_c));
    }

    //21. purpose: getBigInteger with Undefined Integer Property
    public void testGetIntegerConversionFromUnDefinedIntegerProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INTEGER);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getBigInteger(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //22. purpose: getBigInteger with date property
    public void testGetIntegerFromDate() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DATE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        dataObject_c.set(property_c, Calendar.getInstance().getTime());
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
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }
}
