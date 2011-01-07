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
import org.eclipse.persistence.exceptions.SDOException;

public class SDODataObjectGetDecimalWithIndexConversionTest extends SDODataObjectConversionTestCases {
    public SDODataObjectGetDecimalWithIndexConversionTest(String name) {
        super(name);
    }

 public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetDecimalWithIndexConversionTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getBigDecimal with boolean property
    public void testGetDecimalFromBoolean() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BOOLEAN);
        dataObject.set(property, true);
        try {
            dataObject.getBigDecimal(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getBigDecimal with byte property
    public void testGetDecimalFromByte() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTE);
        byte theByte = 7;
        dataObject.set(property, theByte);
        try {
            BigDecimal value = dataObject.getBigDecimal(PROPERTY_INDEX);
            BigDecimal controlValue = new BigDecimal(7);
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //3. purpose: getBigDecimal with character property
    public void testGetDecimalFromCharacter() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_CHARACTER);
        dataObject.set(property, 'k');
        try {
            dataObject.getBigDecimal(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //5. purpose: getBigDecimal with Defined Double Property
    public void testGetDecimalConversionFromDefinedDoubleProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DOUBLE);

        double db = 12;
        BigDecimal bd = new BigDecimal(db);
        dataObject.setDouble(PROPERTY_INDEX, db);// add it to instance list

        this.assertEquals(bd, dataObject.getBigDecimal(PROPERTY_INDEX));
    }

    //7. purpose: getBigDecimal with Defined float Property
    public void testGetIntConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_INDEX);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_FLOAT);

        Float fl = new Float(12);
        BigDecimal bd = new BigDecimal(fl);
        dataObject.setFloat(PROPERTY_INDEX, fl);// add it to instance list

        this.assertEquals(bd, dataObject.getBigDecimal(PROPERTY_INDEX));
    }

    //9. purpose: getBigDecimal with Defined int Property
    public void testGetDecimalConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INT);

        int in = 12;
        BigDecimal bd = new BigDecimal(in);
        dataObject.setInt(PROPERTY_INDEX, in);// add it to instance list

        this.assertEquals(bd, dataObject.getBigDecimal(PROPERTY_INDEX));
    }

    //11. purpose: getBigDecimal with Defined long Property
    public void testGetDecimalConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_LONG);

        long lg = 12;
        BigDecimal bd = new BigDecimal(lg);
        dataObject.setLong(PROPERTY_INDEX, lg);// add it to instance list

        this.assertEquals(bd, dataObject.getBigDecimal(PROPERTY_INDEX));
    }

    //14. purpose: getBigDecimal with Defined String Property
    public void testGetDecimalConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_STRING);

        String str = "12";
        BigDecimal bd = new BigDecimal(str);
        dataObject.setString(PROPERTY_INDEX, str);// add it to instance list

        this.assertEquals(bd, dataObject.getBigDecimal(PROPERTY_INDEX));
    }

    //17. purpose: getBigDecimal with bytes property
    public void testGetDecimalFromBytes() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTES);
        dataObject.set(property, new String("eee").getBytes());
        try {
            dataObject.getBigDecimal(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //18. purpose: getBigDecimal with Defined Decimal Property
    public void testGetDecimalConversionFromDefinedDecimalProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DECIMAL);

        int db = 12;
        BigDecimal bd = new BigDecimal(db);
        dataObject.setBigDecimal(PROPERTY_INDEX, bd);// add it to instance list

        this.assertEquals(bd, dataObject.getBigDecimal(PROPERTY_INDEX));
    }

    //19. purpose: getBigDecimal with Undefined decimal Property
    public void testGetDecimalConversionFromUnDefinedProperty() {
        try {
            dataObject.getBigDecimal(1);
            fail("IllegalArgumentException should be thrown.");
        } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");    
    }

    //20. purpose: getDecimal with Defined integer Property
    public void testGetDecimalConversionFromDefinedIntegerProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INTEGER);

        BigInteger bi = new BigInteger("12");
        BigDecimal bd = new BigDecimal(bi);
        dataObject.setBigInteger(PROPERTY_INDEX, bi);// add it to instance list

        this.assertEquals(bd, dataObject.getBigDecimal(PROPERTY_INDEX));
    }

    //22. purpose: getBigDecimal with date property
    public void testGetDecimalFromDate() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DATE);
        dataObject.set(property, Calendar.getInstance().getTime());
        try {
            dataObject.getBigDecimal(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //purpose: getBigDecimal with nul value
    public void testGetDecimaltWithNullArgument() {
        try {
            int p = -1;
            dataObject.getBigDecimal(p);
        } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");        }
}
