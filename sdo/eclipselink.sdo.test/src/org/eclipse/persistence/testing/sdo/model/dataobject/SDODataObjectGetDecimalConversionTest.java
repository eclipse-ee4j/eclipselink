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

import commonj.sdo.Property;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;

public class SDODataObjectGetDecimalConversionTest extends SDODataObjectConversionTestCases {
    public SDODataObjectGetDecimalConversionTest(String name) {
        super(name);
    }
 
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetDecimalConversionTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getBigDecimal with boolean property
    public void testGetDecimalFromBoolean() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BOOLEAN);
        dataObject.set(property, true);
        try {
            BigDecimal bd = dataObject.getBigDecimal(property);
            assertEquals(1, bd);
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getBigDecimal with byte property
    public void testGetDecimalFromByte() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTE);
        byte theByte = 10;
        dataObject.set(property, theByte);
        try {
            BigDecimal value = dataObject.getBigDecimal(property);
            BigDecimal controlValue = new BigDecimal(10);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
            assertEquals(controlValue, value);
        } catch (ClassCastException e) {
        }
    }

    //3. purpose: getBigDecimal with character property
    public void testGetDecimalFromCharacter() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_CHARACTER);
        dataObject.set(property, new Character('y'));
        try {
            dataObject.getBigDecimal(property);
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
        dataObject.setDouble(property, db);// add it to instance list

        this.assertEquals(bd, dataObject.getBigDecimal(property));
    }

    //6. purpose: getBigDecimal with Undefined Double Property
    public void testGetDecimalConversionFromUnDefinedDoubleProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DOUBLE);

        try {
            dataObject.getBigDecimal(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //7. purpose: getBigDecimal with Defined float Property
    public void testGetIntConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_FLOAT);

        float fl = 12;
        BigDecimal bd = new BigDecimal(fl);
        dataObject.setFloat(property, fl);// add it to instance list

        this.assertEquals(bd, dataObject.getBigDecimal(property));
    }

    //8. purpose: getBigDecimal with Undefined float Property
    public void testGetDecimalConversionFromUnDefinedFloatProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_FLOAT);

        try {
            dataObject.getBigDecimal(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //9. purpose: getBigDecimal with Defined int Property
    public void testGetDecimalConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INT);

        int in = 12;
        BigDecimal bd = new BigDecimal(in);
        dataObject.setInt(property, in);// add it to instance list

        this.assertEquals(bd, dataObject.getBigDecimal(property));
    }

    //10. purpose: getBigDecimal with Undefined int Property
    public void testGetDecimalConversionFromUnDefinedIntProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_INT);

        try {
            dataObject.getBigDecimal(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //11. purpose: getBigDecimal with Defined long Property
    public void testGetDecimalConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_LONG);

        long lg = 12;
        BigDecimal bd = new BigDecimal(lg);
        dataObject.setLong(property, lg);// add it to instance list

        this.assertEquals(bd, dataObject.getBigDecimal(property));
    }

    //12. purpose: getBigDecimal with Undefined long Property
    public void testGetgetDecimalConversionFromUnDefinedLongProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_LONG);

        try {
            dataObject.getBigDecimal(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //13. purpose: getBigDecimal with Undefined short Property
    public void testGetDecimalConversionFromUnDefinedShortProperty() {
        ((SDOType)dataObject.getType()).setOpen(true);
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_SHORT);
        short shortValue = 2;
        dataObject.set(property, shortValue);
        try {
            BigDecimal value = dataObject.getBigDecimal(property);
            BigDecimal controlValue = new BigDecimal(2);
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //14. purpose: getBigDecimal with Defined String Property
    public void testGetDecimalConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_STRING);

        String str = "12";
        BigDecimal bd = new BigDecimal(str);
        dataObject.setString(property, str);// add it to instance list

        this.assertEquals(bd, dataObject.getBigDecimal(property));
    }

    //15. purpose: getBigDecimal with Undefined string Property
    public void testGetDecimalConversionFromUnDefinedStringProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_STRING);

        try {
            dataObject.getBigDecimal(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //17. purpose: getBigDecimal with bytes property
    public void testGetDecimalFromBytes() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTES);
        dataObject.set(property, new String("rty").getBytes());
        try {
            dataObject.getBigDecimal(property);
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
        dataObject.setBigDecimal(property, bd);// add it to instance list

        this.assertEquals(bd, dataObject.getBigDecimal(property));
    }

    //19. purpose: getBigDecimal with Undefined decimal Property
    public void testGetDecimalConversionFromUnDefinedDecimalProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DECIMAL);

        try {
            dataObject.getBigDecimal(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //20. purpose: getDecimal with Defined integer Property
    public void testGetDecimalConversionFromDefinedIntegerProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INTEGER);

        BigInteger bi = new BigInteger("12");
        BigDecimal bd = new BigDecimal(bi);
        dataObject.setBigInteger(property, bi);// add it to instance list

        this.assertEquals(bd, dataObject.getBigDecimal(property));
    }

    //21. purpose: getBigDecimal with Undefined Integer Property
    public void testGetDecimalConversionFromUnDefinedIntegerProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_INTEGER);

        try {
            dataObject.getBigDecimal(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //22. purpose: getBigDecimal with date property
    public void testGetDecimalFromDate() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DATE);
        dataObject.set(property, Calendar.getInstance().getTime());
        try {
            dataObject.getBigDecimal(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //purpose: getBigDecimal with nul value
    public void testGetDecimaltWithNullArgument() {
        try {
            Property p = null;
            dataObject.getBigDecimal(p);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }
}
