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
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.exceptions.SDOException;

public class SDODataObjectGetDateWithIndexConversionTest extends SDODataObjectConversionTestCases {
    public SDODataObjectGetDateWithIndexConversionTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetDateWithIndexConversionTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getDate with Boolean property
    public void testGetDateFromBoolean() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BOOLEAN);
        dataObject.set(property, true);
        try {
            dataObject.getDate(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getDate with Byte property
    public void testGetDateFromByte() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTE);
        dataObject.set(property, new String("eee").getBytes()[0]);
        try {
            dataObject.getDate(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //3. purpose: getDate with character property
    public void testGetDateFromCharacter() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_CHARACTER);
        dataObject.set(property, 'c');
        try {
            dataObject.getDate(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //4. purpose: getDate with Double Property
    public void testGetDateFromDouble() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DOUBLE);
        double doubleValue =3;
        dataObject.set(property, doubleValue);
        try {
            dataObject.getDate(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //5. purpose: getDate with float Property
    public void testGetDateFromFloat() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_FLOAT);
        float floatValue = 3;
        dataObject.set(property, floatValue);
        try {
            dataObject.getDate(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //7. purpose: getDate with int Property
    public void testGetDateFromInt() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INT);
        int intValue = 3;
        dataObject.set(property, intValue);
        try {
            dataObject.getDate(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //8. purpose: getDate with Defined long Property
    public void testGetDateConversionFromDefinedLongProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_LONG);

        long l = 12;
        Date d = new Date(l);

        dataObject.setLong(PROPERTY_INDEX, l);// add it to instance list

        this.assertEquals(d, dataObject.getDate(PROPERTY_INDEX));
    }

    //9. purpose: getDate with Undefined Boolean Property
    public void testGetDateConversionFromUnDefinedBooleanProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BOOLEAN);
        Object obj = dataObject.getDate(PROPERTY_INDEX);
        assertNull(obj);
    }

    //10. purpose: getDate with short Property
    public void testGetDateFromShort() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_SHORT);
        short shortValue = 3;
        dataObject.set(property, shortValue);
        try {
            dataObject.getDate(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //11. purpose: getDate with Defined String Property
    public void testGetDateConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_STRING);

        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2001);
        controlCalendar.set(Calendar.MONTH, 0);
        controlCalendar.set(Calendar.DATE, 1);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date d = controlCalendar.getTime();

        dataObject.setString(property, "2001-01-01");// add it to instance list

        this.assertEquals(d, dataObject.getDate(PROPERTY_INDEX));
    }

    //12. purpose: getDate with Undefined string Property
    public void testGetDateConversionFromUnDefinedStringProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_STRING);
        Object obj = dataObject.getDate(PROPERTY_INDEX);
        assertNull(obj);
    }

    //13. purpose: getDate with bytes property
    public void testGetDateFromBytes() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTES);
        dataObject.set(property, new String("eee").getBytes());
        try {
            dataObject.getDate(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //14. purpose: getBoolean with decimal property
    public void testGetDateFromDecimal() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DECIMAL);
        dataObject.set(property, new BigDecimal(11));
        try {
            dataObject.getDate(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //15. purpose: getDate with integer property
    public void testGetDateFromInteger() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INTEGER);
        dataObject.set(property, new Integer(2));
        try {
            dataObject.getDate(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //16. purpose: getDate with Defined Date Property
    public void testGetDateConversionFromDefinedDateProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DATE);

        long l = 12000;
        Date d = new Date(l);
        dataObject.setDate(PROPERTY_INDEX, d);// add it to instance list

        this.assertEquals(d, dataObject.getDate(PROPERTY_INDEX));
    }

    //12. purpose: getDate with Undefined string Property
    public void testGetDateConversionFromUnDefinedDateProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DATE);
        Object obj = dataObject.getDate(PROPERTY_INDEX);
        assertNull(obj);
    }

    //purpose: getDate with nul value
    public void testGetDateWithNullArgument() {
        try {
            int p = -1;
            dataObject.getDate(p);
        } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");    
    }
}
