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
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;

public class SDODataObjectGetByteConversionTest extends SDODataObjectConversionTestCases {
    public SDODataObjectGetByteConversionTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetByteConversionTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getByte with boolean property
    public void testGetByteFromBoolean() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BOOLEAN);
        dataObject.getType().getDeclaredProperties().add(property);
        dataObject.set(property, true);
        try {
            dataObject.getByte(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getByte with Defined Byte Property
    public void testGetByteConversionFromDefinedByteProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTE);

        byte by = 12;

        dataObject.setByte(property, by);// add it to instance list

        this.assertEquals(by, dataObject.getByte(property));
    }

    //3. purpose: getByte with Undefined Byte Property
    public void testGetByteConversionFromUnDefinedByteProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTE);

        try {
            dataObject.getByte(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //4. purpose: getByte with character property
    public void testGetByteFromCharacter() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_CHARACTER);
        char theValue = 'e';
        dataObject.set(property, theValue);
        try {
            dataObject.getByte(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //5. purpose: getByte with Defined Double Property
    public void testGetByteConversionFromDefinedDoubleProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DOUBLE);

        double db = 12;
        dataObject.setDouble(property, db);// add it to instance list

        this.assertEquals((byte)db, dataObject.getByte(property));
    }

    //6. purpose: getByte with Undefined Double Property
    public void testGetByteConversionFromUnDefinedDoubleProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTE);

        try {
            dataObject.getByte(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //7. purpose: getByte with Defined float Property
    public void testGetByteConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_FLOAT);

        float fl = 12;
        dataObject.setFloat(property, fl);// add it to instance list

        this.assertEquals((byte)fl, dataObject.getByte(property));
    }

    //8. purpose: getByte with Undefined float Property
    public void testGetByteConversionFromUnDefinedFloatProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_FLOAT);

        try {
            dataObject.getByte(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //9. purpose: getByte with Defined int Property
    public void testGetByteConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INT);
        int in = 12;
        dataObject.setInt(property, in);// add it to instance list

        this.assertEquals((byte)in, dataObject.getByte(property));
    }

    //10. purpose: getByte with Undefined int Property
    public void testGetByteConversionFromUnDefinedIntProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_INT);

        try {
            dataObject.getByte(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //11. purpose: getByte with Defined long Property
    public void testGetByteConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_LONG);

        long lg = 12;
        dataObject.setLong(property, lg);// add it to instance list

        this.assertEquals((byte)lg, dataObject.getByte(property));
    }

    //12. purpose: getByte with Undefined long Property
    public void testGetByteConversionFromUnDefinedLongProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_LONG);

        try {
            dataObject.getByte(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //13. purpose: getByte with Defined short Property
    public void testGetByteConversionFromDefinedShortProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_SHORT);

        short shr = 12;
        dataObject.setShort(property, shr);// add it to instance list

        this.assertEquals((byte)shr, dataObject.getByte(property));
    }

    //14. purpose: getByte with Undefined short Property
    public void testGetDoubleConversionFromUnDefinedShortProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_SHORT);

        try {
            dataObject.getByte(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //15. purpose: getByte with Defined String Property
    public void testGetByteConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_STRING);

        String str = "12";
        Byte s_d = new Byte(str);
        dataObject.setString(property, str);// add it to instance list

        this.assertEquals(s_d.byteValue(), dataObject.getByte(property));
    }

    //16. purpose: getDouble with Undefined string Property
    public void testGetByteConversionFromUnDefinedStringProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_STRING);

        try {
            dataObject.getByte(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //17. purpose: getByte with bytes property
    public void testGetByteFromBytes() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTES);
        byte[] theValue = new byte[]{10,100};
        dataObject.set(property, theValue);
        try {
            dataObject.getByte(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //18. purpose: getByte with decimal property
    public void testGetByteFromDecimal() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DECIMAL);        
        BigDecimal theValue = new BigDecimal(12);
        dataObject.set(property, theValue);
        try {
            byte value = dataObject.getByte(property);            
            byte controlValue = theValue.byteValue();
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //19. purpose: getByte with decimal property
    public void testGetByteFromInteger() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_INTEGER);
        BigInteger theValue = new BigInteger("12");
        dataObject.set(property, theValue);
        try {
            byte value = dataObject.getByte(property);            
            byte controlValue = theValue.byteValue();
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //20. purpose: getByte with date property
    public void testGetByteFromDate() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DATE);
        dataObject.set(property, Calendar.getInstance().getTime());
        try {
            dataObject.getByte(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //purpose: getByte with nul value
    public void testGetByteWithNullArgument() {
        try {
            Property p = null;
            dataObject.getByte(p);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testGetByteFromBooleanObject() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BOOLEANOBJECT);        
        Boolean theValue = Boolean.TRUE;
        dataObject.set(property, theValue);
        try {
            dataObject.getByte(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    public void testGetByteConversionFromDefinedByteObjectProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTEOBJECT);

        byte by = 12;

        dataObject.setByte(property, by);// add it to instance list

        this.assertEquals(by, dataObject.getByte(property));
    }

    public void testGetByteFromCharacterObject() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_CHARACTEROBJECT);
        Character theValue = Character.valueOf('e');
        dataObject.set(property, theValue);
        try {
            dataObject.getByte(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    public void testGetByteConversionFromDefinedDoubleObjectProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DOUBLEOBJECT);

        double db = 12;
        dataObject.setDouble(property, db);// add it to instance list

        this.assertEquals((byte)db, dataObject.getByte(property));
    }

    public void testGetByteConversionFromDefinedFloatObjectProperty() {
        // dataObject's type add float property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_FLOATOBJECT);

        float fl = 12;
        dataObject.setFloat(property, fl);// add it to instance list

        this.assertEquals((byte)fl, dataObject.getByte(property));
    }

    public void testGetByteConversionFromDefinedIntObjectProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INTOBJECT);
        int in = 12;
        dataObject.setInt(property, in);// add it to instance list

        this.assertEquals((byte)in, dataObject.getByte(property));
    }

    public void testGetByteConversionFromDefinedLongObjectProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_LONGOBJECT);

        long lg = 12;
        dataObject.setLong(property, lg);// add it to instance list

        this.assertEquals((byte)lg, dataObject.getByte(property));
    }

    public void testGetByteConversionFromDefinedShortObjectProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_SHORTOBJECT);

        short shr = 12;
        dataObject.setShort(property, shr);// add it to instance list

        this.assertEquals((byte)shr, dataObject.getByte(property));
    }
}
