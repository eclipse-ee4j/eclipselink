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
import org.eclipse.persistence.exceptions.SDOException;

public class SDODataObjectGetShortWithIndexConversionTest extends SDODataObjectConversionTestCases {
    public SDODataObjectGetShortWithIndexConversionTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetShortWithIndexConversionTest" };
        TestRunner.main(arguments);
    }


    //1. purpose: getShort with boolean property
    public void testGetShortFromBoolean() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BOOLEAN);
        dataObject.set(property, true);
        
        short shortValue = dataObject.getShort(PROPERTY_INDEX);
        assertEquals(1, shortValue);        
    }

    //2. purpose: getShort with Defined Byte Property
    public void testGetShortConversionFromDefinedByteProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTE);

        byte by = 12;

        dataObject.setByte(PROPERTY_INDEX, by);// add it to instance list

        this.assertEquals((short)by, dataObject.getShort(PROPERTY_INDEX));
    }

    //3. purpose: getShort with Undefined Byte Property
    public void testGetShortConversionFromUnDefinedProperty() {        
        try {
            dataObject.getShort(1);
          } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");
    }

    //4. purpose: getShort with character property
    public void testGetShortFromCharacter() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_CHARACTER);
        dataObject.set(property, 'k');
        try {
            dataObject.getShort(PROPERTY_INDEX);
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
        dataObject.setDouble(PROPERTY_INDEX, db);// add it to instance list

        this.assertEquals((short)db, dataObject.getShort(PROPERTY_INDEX));
    }

    //7. purpose: getShort with Defined float Property
    public void testGetShortConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_FLOAT);

        float fl = 12;
        dataObject.setFloat(PROPERTY_INDEX, fl);// add it to instance list

        this.assertEquals((short)fl, dataObject.getShort(PROPERTY_INDEX));
    }

    //9. purpose: getShort with Defined int Property
    public void testGetShortConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INT);

        int in = 12;
        dataObject.setInt(PROPERTY_INDEX, in);// add it to instance list

        this.assertEquals((short)in, dataObject.getShort(PROPERTY_INDEX));
    }
    

    //11. purpose: getShort with Defined long Property
    public void testGetShortConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_LONG);

        long lg = 12;
        dataObject.setLong(PROPERTY_INDEX, lg);// add it to instance list

        this.assertEquals((short)lg, dataObject.getShort(PROPERTY_INDEX));
    }

    //13. purpose: getShort with Defined short Property
    public void testGetShortConversionFromDefinedShortProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_SHORT);

        short shr = 12;
        dataObject.setShort(PROPERTY_INDEX, shr);// add it to instance list

        this.assertEquals(shr, dataObject.getShort(PROPERTY_INDEX));
    }

    //15. purpose: getShort with Defined String Property
    public void testGetShortConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_STRING);

        String str = "12";
        Short s_d = new Short(str);
        dataObject.setString(PROPERTY_INDEX, str);// add it to instance list

        this.assertEquals(s_d.shortValue(), dataObject.getShort(PROPERTY_INDEX));
    }


    //17. purpose: getShort with bytes property
    public void testGetShortFromBytes() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTES);
        dataObject.set(property, new String("rrr").getBytes());
        try {
            dataObject.getShort(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //18. purpose: getShort with decimal property
    public void testGetShortFromDecimal() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DECIMAL);
        dataObject.set(property, new BigDecimal("2"));
        try {
            short value = dataObject.getShort(PROPERTY_INDEX);
            short controlValue = 2;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //19. purpose: getShort with integer property
    public void testGetShortFromInteger() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INTEGER);
        dataObject.set(property, new BigInteger("2"));
        try {
            short value = dataObject.getShort(PROPERTY_INDEX);
            short controlValue = 2;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //22. purpose: getShort with date property
    public void testGetShortFromDate() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DATE);
        dataObject.set(property, Calendar.getInstance().getTime());
        try {
            dataObject.getShort(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //purpose: getShort with nul value
    public void testGetShortWithNullArgument() {
        try {
            int p = -1;
            dataObject.getShort(p);            
        } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");
    }
}
