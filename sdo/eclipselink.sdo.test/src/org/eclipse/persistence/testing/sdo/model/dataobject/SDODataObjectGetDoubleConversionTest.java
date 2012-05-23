/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

public class SDODataObjectGetDoubleConversionTest extends SDODataObjectConversionTestCases {
    public SDODataObjectGetDoubleConversionTest(String name) {
        super(name);
    }
    
      public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetDoubleConversionTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getDouble with boolean property
    public void testGetDoubleFromBoolean() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BOOLEAN);
        dataObject.set(property, true);
        try {
            double value = dataObject.getDouble(property);             
            double controlValue = 1;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getDouble with Defined Byte Property
    public void testGetDoubleConversionFromDefinedByteProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTE);

        byte by = 12;
        double delta = 0.0;

        dataObject.setByte(property, by);// add it to instance list

        this.assertEquals((double)by, dataObject.getDouble(property), delta);
    }

    //3. purpose: getDouble with Undefined Byte Property
    public void testGetDoubleConversionFromUnDefinedByteProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTE);

        try {
            dataObject.getDouble(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //4. purpose: getDouble with character property
    public void testGetDoubleFromCharacter() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_CHARACTER);
        dataObject.set(property, 'e');
        try {
            dataObject.getDouble(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //5. purpose: getDouble with Defined Double Property
    public void testGetDoubleConversionFromDefinedDoubleProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DOUBLE);

        double db = 12;
        double delta = 0.0;
        dataObject.setDouble(property, db);// add it to instance list

        this.assertEquals((double)db, dataObject.getDouble(property), delta);
    }

    //6. purpose: getDouble with Undefined Double Property
    public void testGetDoubleConversionFromUnDefinedDoubleProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTE);

        try {
            dataObject.getDouble(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //7. purpose: getDouble with Defined float Property
    public void testGetDoubleConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_FLOAT);

        float fl = 12;
        double delta = 0.0;
        dataObject.setFloat(property, fl);// add it to instance list

        this.assertEquals((double)fl, dataObject.getDouble(property), delta);
    }

    //8. purpose: getDouble with Undefined float Property
    public void testGetDoubleConversionFromUnDefinedFloatProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_FLOAT);

        try {
            dataObject.getDouble(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //9. purpose: getDouble with Defined int Property
    public void testGetDoubleConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INT);

        int in = 12;
        double delta = 0.0;
        dataObject.setInt(property, in);// add it to instance list

        this.assertEquals((double)in, dataObject.getDouble(property), delta);
    }

    //10. purpose: getDouble with Undefined int Property
    public void testGetDoubleConversionFromUnDefinedIntProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_INT);

        try {
            dataObject.getDouble(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //11. purpose: getDouble with Defined long Property
    public void testGetDoubleConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_LONG);

        long lg = 12;
        double delta = 0.0;
        dataObject.setLong(property, lg);// add it to instance list

        this.assertEquals((double)lg, dataObject.getDouble(property), delta);
    }

    //12. purpose: getDouble with Undefined long Property
    public void testGetDoubleConversionFromUnDefinedLongProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_LONG);

        try {
            dataObject.getDouble(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //13. purpose: getDouble with Defined short Property
    public void testGetDoubleConversionFromDefinedShortProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_SHORT);

        short shr = 12;
        double delta = 0.0;
        dataObject.setShort(property, shr);// add it to instance list

        this.assertEquals((double)shr, dataObject.getDouble(property), delta);
    }

    //14. purpose: getDouble with Undefined short Property
    public void testGetDoubleConversionFromUnDefinedShortProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_SHORT);

        try {
            dataObject.getDouble(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //15. purpose: getDouble with Defined String Property
    public void testGetDoubleConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_STRING);

        String str = "12";
        Double s_d = new Double(str);
        double delta = 0.0;
        dataObject.setString(property, str);// add it to instance list

        this.assertEquals(s_d.doubleValue(), dataObject.getDouble(property), delta);
    }

    //16. purpose: getDouble with Undefined string Property
    public void testGetDoubleConversionFromUnDefinedStringProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_STRING);

        try {
            dataObject.getDouble(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //17. purpose: getDouble with bytes property
    public void testGetDoubleFromBytes() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTES);
        byte[] bytes = new byte[]{10,100};
        dataObject.set(property, bytes);
        try {
            dataObject.getDouble(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //18. purpose: getDouble with Defined Decimal Property
    public void testGetDoubleConversionFromDefinedDecimalProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DECIMAL);

        double db = 12;
        BigDecimal bd = new BigDecimal(db);
        double delta = 0.0;
        dataObject.setBigDecimal(property, bd);// add it to instance list

        this.assertEquals(bd.doubleValue(), dataObject.getDouble(property), delta);
    }

    //19. purpose: getDouble with Undefined decimal Property
    public void testGetDoubleConversionFromUnDefinedDecimalProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DECIMAL);

        try {
            dataObject.getDouble(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //20. purpose: getDouble with Defined integer Property
    public void testGetDoubleConversionFromDefinedIntegerProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INTEGER);

        BigInteger bi = new BigInteger("12");
        double delta = 0.0;
        dataObject.setBigInteger(property, bi);// add it to instance list

        this.assertEquals(bi.doubleValue(), dataObject.getDouble(property), delta);
    }

    //21. purpose: getDouble with Undefined Integer Property
    public void testGetDoubleConversionFromUnDefinedIntegerProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_INTEGER);

        try {
            dataObject.getDouble(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //22. purpose: getDouble with date property
    public void testGetDoubleFromDate() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DATE);
        dataObject.set(property, Calendar.getInstance().getTime());
        try {
            dataObject.getDouble(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //purpose: getDouble with nul value
    public void testGetDoubleWithNullArgument() {
        try {
            Property p = null;
            dataObject.getDouble(p);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testGetDoubleFromBooleanObject() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BOOLEANOBJECT);
        dataObject.set(property, false);
        try {
            dataObject.getDouble(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getDouble with Defined Byte Property
    public void testGetDoubleConversionFromDefinedBytePObject() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTEOBJECT);

        byte by = 12;
        double delta = 0.0;

        dataObject.setByte(property, by);// add it to instance list

        this.assertEquals((double)by, dataObject.getDouble(property), delta);
    }

    public void testGetDoubleFromCharacterObject() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_CHARACTEROBJECT);
        dataObject.set(property, 'e');
        try {
            dataObject.getDouble(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //5. purpose: getDouble with Defined Double Property
    public void testGetDoubleConversionFromDefinedDoubleObject() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DOUBLEOBJECT);

        double db = 12;
        double delta = 0.0;
        dataObject.setDouble(property, db);// add it to instance list

        this.assertEquals((double)db, dataObject.getDouble(property), delta);
    }

    //7. purpose: getDouble with Defined float Property
    public void testGetDoubleConversionFromDefinedFloatObject() {
        // dataObject's type add float property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_FLOATOBJECT);

        float fl = 12;
        double delta = 0.0;
        dataObject.setFloat(property, fl);// add it to instance list

        this.assertEquals((double)fl, dataObject.getDouble(property), delta);
    }

    //9. purpose: getDouble with Defined int Property
    public void testGetDoubleConversionFromDefinedIntObject() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INTOBJECT);

        int in = 12;
        double delta = 0.0;
        dataObject.setInt(property, in);// add it to instance list

        this.assertEquals((double)in, dataObject.getDouble(property), delta);
    }

    //11. purpose: getDouble with Defined long Property
    public void testGetDoubleConversionFromDefinedLongObject() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_LONGOBJECT);

        long lg = 12;
        double delta = 0.0;
        dataObject.setLong(property, lg);// add it to instance list

        this.assertEquals((double)lg, dataObject.getDouble(property), delta);
    }

    //13. purpose: getDouble with Defined short Property
    public void testGetDoubleConversionFromDefinedShortObject() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_SHORTOBJECT);

        short shr = 12;
        double delta = 0.0;
        dataObject.setShort(property, shr);// add it to instance list

        this.assertEquals((double)shr, dataObject.getDouble(property), delta);
    }
}
