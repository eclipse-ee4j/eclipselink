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
import java.util.Calendar;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;

public class SDODataObjectGetIntegerConversionTest extends SDODataObjectConversionTestCases {
    public SDODataObjectGetIntegerConversionTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetIntegerConversionTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getBigInteger with boolean property
    public void testGetIntegerFromBoolean() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BOOLEAN);        
        dataObject.set(property, true);
        try {
            BigInteger bigIntegerValue = dataObject.getBigInteger(property);
            assertEquals(1, bigIntegerValue);
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getBigInteger with byte property
    public void testGetIntegerFromByte() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTE);        
        
        byte theByte = 10;
        dataObject.set(property, theByte);
        try {
            BigInteger value = dataObject.getBigInteger(property);
            BigInteger control = new BigInteger("10");
            assertEquals(control, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //3. purpose: getBigInteger with character property
    public void testGetIntegerFromCharacter() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_CHARACTER);        
        dataObject.set(property, 'd');
        try {
            dataObject.getBigInteger(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //5. purpose: getBigInteger with Defined Double Property
    public void testGetIntegerConversionFromDefinedDoubleProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DOUBLE);

        double db = 12;
        int il = (int)db;
        BigInteger bd = new BigInteger(String.valueOf(il));
        dataObject.setDouble(property, db);// add it to instance list

        this.assertEquals(bd, dataObject.getBigInteger(property));
    }

    //6. purpose: getBigInteger with Undefined Double Property
    public void testGetIntegerConversionFromUnDefinedDoubleProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DOUBLE);

        try {
            dataObject.getBigInteger(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //7. purpose: getBigInteger with Defined float Property
    public void testGetIntegerConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_FLOAT);

        float fl = 12;
        int il = (int)fl;
        BigInteger bd = new BigInteger(String.valueOf(il));
        dataObject.setFloat(property, fl);// add it to instance list

        this.assertEquals(bd, dataObject.getBigInteger(property));
    }

    //8. purpose: getBigInteger with Undefined float Property
    public void testGetIntegerConversionFromUnDefinedFloatProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_FLOAT);

        try {
            dataObject.getBigInteger(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //9. purpose: getBigInteger with Defined int Property
    public void testGetIntegerConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INT);

        int in = 12;
        BigInteger bd = new BigInteger(String.valueOf(in));
        dataObject.setInt(property, in);// add it to instance list

        this.assertEquals(bd, dataObject.getBigInteger(property));
    }

    //10. purpose: getBigInteger with Undefined int Property
    public void testGetIntegerConversionFromUnDefinedIntProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_INT);

        try {
            dataObject.getBigInteger(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //11. purpose: getBigInteger with Defined long Property
    public void testGetIntegerConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_LONG);

        long lg = 12;
        BigInteger bd = new BigInteger(String.valueOf(lg));
        dataObject.setLong(property, lg);// add it to instance list

        this.assertEquals(bd, dataObject.getBigInteger(property));
    }

    //12. purpose: getBigInteger with Undefined long Property
    public void testGetgetIntegerConversionFromUnDefinedLongProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_LONG);

        try {
            dataObject.getBigInteger(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //13. purpose: getBigInteger with Undefined short Property
    public void testGetIntegerConversionFromUnDefinedShortProperty() {
        ((SDOType)dataObject.getType()).setOpen(true);
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_SHORT);
        short shortValue = 2;
        dataObject.set(property, shortValue);
        try {
            BigInteger value = dataObject.getBigInteger(property);
            BigInteger controlValue = new BigInteger("2");
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
            } catch (ClassCastException e) {
        }
    }

    //14. purpose: getBigInteger with Defined String Property
    public void testGetgetIntegerConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_STRING);

        String str = "12";
        BigInteger bd = new BigInteger(str);
        dataObject.setString(property, str);// add it to instance list

        this.assertEquals(bd, dataObject.getBigInteger(property));
    }

    //15. purpose: getBigInteger with Undefined string Property
    public void testGetIntegerConversionFromUnDefinedStringProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_STRING);

        try {
            dataObject.getBigInteger(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //16. purpose: getBigInteger with Defined Bytes Property !! OX Pro    !!
    public void testGetIntegerConversionFromDefinedBytesProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTES);

        byte[] b = { 12, 13 };
        BigInteger bin = new BigInteger(b);

        dataObject.setBytes(property, b);// add it to instance list

        this.assertEquals(bin, dataObject.getBigInteger(property));
    }

    //17. purpose: getBigInteger with Undefined Bytes Property
    public void testGetIntegerConversionFromUnDefinedBytesProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTES);

        try {
            dataObject.getBigInteger(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //17. purpose: getBigDecimal with bytes property
    public void testGetDecimalFromBytes() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTES);        
        dataObject.set(property, new String("abcd").getBytes());
        try {
            dataObject.getBigDecimal(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //18. purpose: getBigInteger with Defined Decimal Property
    public void testGetIntegerConversionFromDefinedDecimalProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DECIMAL);

        int db = 12;
        BigDecimal bd = new BigDecimal(db);
        BigInteger bd_ = new BigInteger(String.valueOf(bd));
        dataObject.setBigDecimal(property, bd);// add it to instance list

        this.assertEquals(bd_, dataObject.getBigInteger(property));
    }

    //19. purpose: getBigInteger with Undefined decimal Property
    public void testGetIntegerConversionFromUnDefinedDecimalProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DECIMAL);

        try {
            dataObject.getBigInteger(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //20. purpose: getBigInteger with Defined integer Property
    public void testGetIntegerConversionFromDefinedIntegerProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INTEGER);

        BigInteger bi = new BigInteger("12");
        dataObject.setBigInteger(property, bi);// add it to instance list

        this.assertEquals(bi, dataObject.getBigInteger(property));
    }

    //21. purpose: getBigInteger with Undefined Integer Property
    public void testGetIntegerConversionFromUnDefinedIntegerProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_INTEGER);

        try {
            dataObject.getBigInteger(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //22. purpose: getBigInteger with date property
    public void testGetIntegerFromDate() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DATE);

        dataObject.set(property, Calendar.getInstance().getTime());
        try {
            dataObject.getBigInteger(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //purpose: getBigInteger with nul value
    public void testGetIntegerWithNullArgument() {
        try {
            Property p = null;
            dataObject.getBigInteger(p);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }
}
