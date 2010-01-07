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
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOConstants;

public class SDODataObjectGetStringConversion extends SDODataObjectConversionTestCases {
    public SDODataObjectGetStringConversion(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetStringConversion" };
        TestRunner.main(arguments);
    }


    //1. purpose: getString with Defined boolean Property
    public void testGetStringConversionFromDefinedBoolean() {
        // dataObject's type add int property                
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BOOLEAN);

        boolean str = true;
        Boolean B_STR = new Boolean(str);
        dataObject.setBoolean(property, str);// add it to instance list

        this.assertEquals(B_STR.toString(), dataObject.getString(property));
    }

    //2. purpose: getString with Undefined boolean Property
    public void testGetStringConversionFromUnDefinedBooleanProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        //SDOProperty property = (SDOPropert)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BOOLEAN);
        try {
            dataObject.getString(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //3. purpose: getString with Defined Byte Property
    public void testGetStringConversionFromDefinedByteProperty() {
        // dataObject's type add boolean property                
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTE);

        byte by = 12;
        String by_ = String.valueOf(by);

        dataObject.setByte(property, by);// add it to instance list

        this.assertEquals(by_, dataObject.getString(property));
    }

    //3. purpose: getString with Undefined Byte Property
    public void testGetStringConversionFromUnDefinedByteProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTE);

        try {
            dataObject.getString(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //4. purpose: getString with Defined character Property
    public void testGetStringConversionFromDefinedCharacterProperty() {
        // dataObject's type add int property        
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_STRING);

        char str = 'c';
        String str_ = String.valueOf(str);
        dataObject.setChar(property, str);// add it to instance list

        this.assertEquals(str_, dataObject.getString(property));
    }

    //5. purpose: getString with Undefined boolean Property
    public void testGetCStringConversionFromUnDefinedCharacterProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_STRING);

        try {
            dataObject.getString(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //6. purpose: getString with Defined Double Property
    public void testGetStringConversionFromDefinedDoubleProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DOUBLE);

        double db = 12;
        String db_ = "12.0";// String.valueOf(12);
        dataObject.setDouble(property, db);// add it to instance list

        this.assertEquals(db_, dataObject.getString(property));
    }

    //7. purpose: getString with Undefined Double Property
    public void testGetStringConversionFromUnDefinedDoubleProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTE);

        try {
            dataObject.getString(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //8. purpose: getString with Defined float Property
    public void testGetIntConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_FLOAT);

        float fl = 12;
        dataObject.setFloat(property, fl);// add it to instance list
        String fl_ = String.valueOf(fl);

        this.assertEquals(fl_, dataObject.getString(property));
    }

    //9. purpose: getString with Undefined float Property
    public void testGetStringConversionFromUnDefinedFloatProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_FLOAT);

        try {
            dataObject.getString(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //10. purpose: getString with Defined int Property
    public void testGetStringConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INT);

        int in = 12;
        dataObject.setInt(property, in);// add it to instance list
        String in_ = String.valueOf(in);

        this.assertEquals(in_, dataObject.getString(property));
    }

    //11. purpose: getString with Undefined int Property
    public void testGetStringConversionFromUnDefinedIntProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_INT);

        try {
            dataObject.getString(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //12. purpose: getString with Defined long Property
    public void testGetStringConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_LONG);

        long lg = 12;
        dataObject.setLong(property, lg);// add it to instance list
        String lg_ = String.valueOf(lg);

        this.assertEquals(lg_, dataObject.getString(property));
    }

    //13. purpose: getString with Undefined long Property
    public void testGetStringConversionFromUnDefinedLongProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_LONG);

        try {
            dataObject.getString(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //14. purpose: getString with Defined short Property
    public void testGetStringConversionFromDefinedShortProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_SHORT);

        short shr = 12;
        dataObject.setShort(property, shr);// add it to instance list
        String shr_ = String.valueOf(shr);

        this.assertEquals(shr_, dataObject.getString(property));
    }

    //15. purpose: getString with Undefined short Property
    public void testGetStringConversionFromUnDefinedShortProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_SHORT);

        try {
            dataObject.getString(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //16. purpose: getString with Defined String Property
    public void testGetIntConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_STRING);

        String str = "12";
        dataObject.setString(property, str);// add it to instance list

        this.assertEquals(str, dataObject.getString(property));
    }

    //17. purpose: getString with Undefined string Property
    public void testGetStringConversionFromUnDefinedStringProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_STRING);

        try {
            dataObject.getString(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //18. purpose: getString with bytes property
    public void testGetStringFromBytes() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTES);
             
        byte[] bytes = new byte[]{10, 100};
        dataObject.set(property, bytes);
      
        try {
            String value = dataObject.getString(property);           
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
        dataObject.setBigDecimal(property, bd);// add it to instance list

        this.assertEquals(bd.toString(), dataObject.getString(property));
    }

    //20. purpose: getString with Undefined decimal Property
    public void testGetStringConversionFromUnDefinedDecimalProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DECIMAL);
        try {
            dataObject.getString(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //21. purpose: getString with Defined integer Property
    public void testGetStringConversionFromDefinedIntegerProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INTEGER);

        BigInteger bi = new BigInteger("12");
        dataObject.setBigInteger(property, bi);// add it to instance list

        this.assertEquals(bi.toString(), dataObject.getString(property));
    }

    //22. purpose: getString with Undefined Integer Property
    public void testGetStringConversionFromUnDefinedIntegerProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_INTEGER);

        try {
            dataObject.getString(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //23. purpose: getString with Defined date Property
    public void testGetStringConversionFromDefinedDateProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DATE);

        //long d = 120000;
        //Date bi = new Date(d);
        //dataObject.setDate(property, bi);// add it to instance list
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2001);
        controlCalendar.set(Calendar.MONTH, 0);
        controlCalendar.set(Calendar.DATE, 1);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date bi = controlCalendar.getTime();

        //dataObject.setString(property, "2001-01-01");// add it to instance list
        dataObject.setDate(property, bi);// add it to instance list

        this.assertEquals("2001-01-01T00:00:00.0Z", dataObject.getString(property));
    }

    //23. purpose: getString with Undefined date Property
    public void testGetStringConversionFromUnDefinedDateProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DATE);

        try {
            dataObject.getString(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //purpose: getStringt with nul value
    public void testGetStringWithNullArgument() {
        try {
            Property p = null;
            dataObject.getString(p);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }
}
