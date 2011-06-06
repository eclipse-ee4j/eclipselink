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

public class SDODataObjectGetShortConversionTest extends SDODataObjectConversionTestCases {
    public SDODataObjectGetShortConversionTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetShortConversionTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getShort with boolean property
    public void testGetShortFromBoolean() {        
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BOOLEAN);
        dataObject.set(property, true);
        try {
            short shortValue = dataObject.getShort(property);
            //fail("ClassCastException should be thrown.");
            assertEquals(1, shortValue);
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getShort with Defined Byte Property
    public void testGetShortConversionFromDefinedByteProperty() {
        // dataObject's type add boolean property      
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTE);

        byte by = 12;

        dataObject.setByte(property, by);// add it to instance list

        this.assertEquals((short)by, dataObject.getShort(property));
    }

    //3. purpose: getShort with Undefined Byte Property
    public void testGetShortConversionFromUnDefinedByteProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTE);

        try {
            dataObject.getShort(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //4. purpose: getShort with character property
    public void testGetShortFromCharacter() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_CHARACTER);
        dataObject.set(property,'a');
        try {
            dataObject.getShort(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //5. purpose: getShort with Defined Double Property
    public void testGetShortConversionFromDefinedDoubleProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DOUBLE);

        double db = 12;
        dataObject.setDouble(property, db);// add it to instance list

        this.assertEquals((short)db, dataObject.getShort(property));
    }

    //6. purpose: getShort with Undefined Double Property
    public void testGetShortConversionFromUnDefinedDoubleProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTE);

        try {
            dataObject.getShort(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //7. purpose: getShort with Defined float Property
    public void testGetShortConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_FLOAT);

        float fl = 12;
        dataObject.setFloat(property, fl);// add it to instance list

        this.assertEquals((short)fl, dataObject.getShort(property));
    }

    //8. purpose: getShort with Undefined float Property
    public void testGetShortConversionFromUnDefinedFloatProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_FLOAT);

        try {
            dataObject.getShort(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //9. purpose: getShort with Defined int Property
    public void testGetShortConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INT);

        int in = 12;
        dataObject.setInt(property, in);// add it to instance list

        this.assertEquals((short)in, dataObject.getShort(property));
    }

    //10. purpose: getShort with Undefined int Property
    public void testGetgetShortConversionFromUnDefinedIntProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_INT);

        try {
            dataObject.getShort(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //11. purpose: getShort with Defined long Property
    public void testGetShortConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_LONG);

        long lg = 12;
        dataObject.setLong(property, lg);// add it to instance list

        this.assertEquals((short)lg, dataObject.getShort(property));
    }

    //12. purpose: getShort with Undefined long Property
    public void testGetShortConversionFromUnDefinedLongProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_LONG);

        try {
            dataObject.getShort(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //13. purpose: getShort with Defined short Property
    public void testGetShortConversionFromDefinedShortProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_SHORT);

        short shr = 12;
        dataObject.setShort(property, shr);// add it to instance list

        this.assertEquals(shr, dataObject.getShort(property));
    }

    //14. purpose: getShort with Undefined short Property
    public void testGetShortConversionFromUnDefinedShortProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_SHORT);

        try {
            dataObject.getShort(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //15. purpose: getShort with Defined String Property
    public void testGetShortConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_STRING);

        String str = "12";
        Short s_d = new Short(str);
        dataObject.setString(property, str);// add it to instance list

        this.assertEquals(s_d.shortValue(), dataObject.getShort(property));
    }

    //16. purpose: getShort with Undefined string Property
    public void testGetShortConversionFromUnDefinedStringProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_STRING);

        try {
            dataObject.getShort(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //17. purpose: getShort with bytes property
    public void testGetShortFromBytes() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTES);
        byte[] bytes = new String("abc").getBytes();
        dataObject.set(property, bytes);
        try {
            dataObject.getShort(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //18. purpose: getShort with decimal property
    public void testGetShortFromDecimal() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DECIMAL);
        BigDecimal bd = new BigDecimal(2);
        dataObject.set(property, bd);
        try {
            short value = dataObject.getShort(property);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
            assertEquals(2, value);
        } catch (ClassCastException e) {
        }
    }

    //19. purpose: getShort with integer property
    public void testGetShortFromInteger() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_INTEGER);
        dataObject.set(property, new Integer("123"));
        try {
            short value = dataObject.getShort(property);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
            assertEquals(123, value);
        } catch (ClassCastException e) {
        }
    }

    //22. purpose: getShort with date property
    public void testGetShortFromDate() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DATE);
        dataObject.set(property, Calendar.getInstance().getTime());
        try {
            dataObject.getShort(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //purpose: getShort with nul value
    public void testGetShortWithNullArgument() {
        try {
            Property p = null;
            dataObject.getShort(p);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testGetShortFromBooleanObject() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BOOLEANOBJECT);
        dataObject.set(property, Boolean.FALSE);
        try {
            short shortValue = dataObject.getShort(property);
            assertEquals(0, shortValue);
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getShort with Defined Byte Property
    public void testGetShortConversionFromDefinedBytePbject() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTEOBJECT);

        byte by = 12;

        dataObject.setByte(property, by);// add it to instance list

        this.assertEquals((short)by, dataObject.getShort(property));
    }

    //4. purpose: getShort with character property
    public void testGetShortFromCharacterObject() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_CHARACTEROBJECT);
        dataObject.set(property,new Character('a'));
        try {
            dataObject.getShort(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //5. purpose: getShort with Defined Double Property
    public void testGetShortConversionFromDefinedDoubleObject() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DOUBLEOBJECT);

        double db = 12;
        dataObject.setDouble(property, db);// add it to instance list

        this.assertEquals((short)db, dataObject.getShort(property));
    }

    //7. purpose: getShort with Defined float Property
    public void testGetShortConversionFromDefinedFloatObject() {
        // dataObject's type add float property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_FLOATOBJECT);

        float fl = 12;
        dataObject.setFloat(property, fl);// add it to instance list

        this.assertEquals((short)fl, dataObject.getShort(property));
    }

    //9. purpose: getShort with Defined int Property
    public void testGetShortConversionFromDefinedIntObject() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INTOBJECT);

        int in = 12;
        dataObject.setInt(property, in);// add it to instance list

        this.assertEquals((short)in, dataObject.getShort(property));
    }

    //11. purpose: getShort with Defined long Property
    public void testGetShortConversionFromDefinedLongObject() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_LONGOBJECT);

        long lg = 12;
        dataObject.setLong(property, lg);// add it to instance list

        this.assertEquals((short)lg, dataObject.getShort(property));
    }

    //13. purpose: getShort with Defined short Property
    public void testGetShortConversionFromDefinedShortObject() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_SHORTOBJECT);

        short shr = 12;
        dataObject.setShort(property, shr);// add it to instance list

        this.assertEquals(shr, dataObject.getShort(property));
    }
}
