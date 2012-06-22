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
import java.util.Date;
import java.util.TimeZone;

import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.exceptions.SDOException;

public class SDODataObjectGetStringWithIndexConversionTest extends SDODataObjectConversionTestCases {
    public SDODataObjectGetStringWithIndexConversionTest(String name) {
        super(name);
    }
    
      public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetStringWithIndexConversionTest" };
        TestRunner.main(arguments);
    }


    //1. purpose: getString with Defined boolean Property
    public void testGetStringConversionFromDefinedBoolean() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BOOLEAN);

        boolean str = true;
        Boolean B_STR = new Boolean(str);
        dataObject.setBoolean(PROPERTY_INDEX, str);// add it to instance list

        this.assertEquals(B_STR.toString(), dataObject.getString(PROPERTY_INDEX));
    }

    //2. purpose: getString with Undefined boolean Property
    public void testGetStringConversionFromUnDefinedProperty() {
        try {
            dataObject.getString(1);
        } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");
    }

    //3. purpose: getString with Defined Byte Property
    public void testGetStringConversionFromDefinedByteProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTE);
        byte by = 12;
        String by_ = String.valueOf(by);

        dataObject.setByte(PROPERTY_INDEX, by);// add it to instance list

        this.assertEquals(by_, dataObject.getString(PROPERTY_INDEX));
    }

    //4. purpose: getString with Defined character Property
    public void testGetStringConversionFromDefinedCharacterProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_CHARACTER);

        char str = 'c';
        String str_ = String.valueOf(str);
        dataObject.setChar(PROPERTY_INDEX, str);// add it to instance list

        this.assertEquals(str_, dataObject.getString(PROPERTY_INDEX));
    }

    //6. purpose: getString with Defined Double Property
    public void testGetStringConversionFromDefinedDoubleProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DOUBLE);

        double db = 12;
        String db_ = "12.0";//tring.valueOf(12);
        dataObject.setDouble(PROPERTY_INDEX, db);// add it to instance list

        this.assertEquals(db_, dataObject.getString(PROPERTY_INDEX));
    }

    //8. purpose: getString with Defined float Property
    public void testGetIntConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_FLOAT);
        type.addDeclaredProperty(property);

        float fl = 12;
        dataObject.setFloat(PROPERTY_INDEX, fl);// add it to instance list
        String fl_ = String.valueOf(fl);

        this.assertEquals(fl_, dataObject.getString(PROPERTY_INDEX));
    }

    //10. purpose: getString with Defined int Property
    public void testGetStringConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INT);

        int in = 12;
        dataObject.setInt(PROPERTY_INDEX, in);// add it to instance list
        String in_ = String.valueOf(in);

        this.assertEquals(in_, dataObject.getString(PROPERTY_INDEX));
    }

    //12. purpose: getString with Defined long Property
    public void testGetStringConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_LONG);

        long lg = 12;
        dataObject.setLong(PROPERTY_INDEX, lg);// add it to instance list
        String lg_ = String.valueOf(lg);

        this.assertEquals(lg_, dataObject.getString(PROPERTY_INDEX));
    }

    //14. purpose: getString with Defined short Property
    public void testGetStringConversionFromDefinedShortProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_SHORT);

        short shr = 12;
        dataObject.setShort(PROPERTY_INDEX, shr);// add it to instance list
        String shr_ = String.valueOf(shr);

        this.assertEquals(shr_, dataObject.getString(PROPERTY_INDEX));
    }

    //16. purpose: getString with Defined String Property
    public void testGetIntConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_STRING);
        type.addDeclaredProperty(property);

        String str = "12";
        dataObject.setString(PROPERTY_INDEX, str);// add it to instance list

        this.assertEquals(str, dataObject.getString(PROPERTY_INDEX));
    }

    //18. purpose: getString with bytes property
    public void testGetStringFromBytes() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTES);
        byte [] bytes = new byte[]{10, 100};
        dataObject.set(property, bytes);
        try {
            String value = dataObject.getString(PROPERTY_INDEX);            
            assertEquals("0A64", value);            
        } catch (ClassCastException e) {
        }
    }

    //19. purpose: getString with Defined Decimal Property
    public void testGetStringConversionFromDefinedDecimalProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DECIMAL);

        int db = 12;
        BigDecimal bd = new BigDecimal(db);
        dataObject.setBigDecimal(PROPERTY_INDEX, bd);// add it to instance list

        this.assertEquals(bd.toString(), dataObject.getString(PROPERTY_INDEX));
    }

    //21. purpose: getString with Defined integer Property
    public void testGetStringConversionFromDefinedIntegerProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INTEGER);

        BigInteger bi = new BigInteger("12");
        dataObject.setBigInteger(PROPERTY_INDEX, bi);// add it to instance list

        this.assertEquals(bi.toString(), dataObject.getString(PROPERTY_INDEX));
    }

    //23. purpose: getString with Defined date Property
    public void testGetStringConversionFromDefinedDateProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DATE);

        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2001);
        controlCalendar.set(Calendar.MONTH, 0);
        controlCalendar.set(Calendar.DATE, 1);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date bi = controlCalendar.getTime();

        //dataObject.setString(property, "2001-01-01");// add it to instance list
        dataObject.setDate(property, bi);// add it to instance list

        this.assertEquals("2001-01-01T00:00:00.0Z", dataObject.getString(PROPERTY_INDEX));

    }

    //purpose: getStringt with nul value
    public void testGetStringWithNullArgument() {
        try {
            int p = -1;
            dataObject.getString(p);
        } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");
    }
}
