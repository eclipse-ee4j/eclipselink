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
import junit.textui.TestRunner;
import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOConstants;

public class SDODataObjectGetIntConversionTest extends SDODataObjectConversionTestCases {
    public SDODataObjectGetIntConversionTest(String name) {
        super(name);
    }

   
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetIntConversionTest" };
        TestRunner.main(arguments);
    }


    //1. purpose: getInt with boolean property
    public void testGetIntFromBoolean() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BOOLEAN);
        dataObject.set(property, true);
        
        int value = dataObject.getInt(property);
        assertEquals(1, value);        
    }

    //2. purpose: getInt with Defined Byte Property
    public void testGetIntConversionFromDefinedByteProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTE);

        byte by = 12;

        dataObject.setByte(property, by);// add it to instance list

        this.assertEquals((int)by, dataObject.getInt(property));
    }

    //3. purpose: getInt with Undefined Byte Property
    public void testGetIntConversionFromUnDefinedByteProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTE);

        try {
            dataObject.getInt(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //4. purpose: getInt with character property
    public void testGetIntFromCharacter() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_CHARACTER);
        dataObject.set(property, 'c');        
        try {
            dataObject.getInt(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }
    
    //4. purpose: getInt with character property
    public void testGetIntFromNullCharacter() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_CHARACTER);        
        try {
            int value = dataObject.getInt(property);
            assertEquals(0, value);
        } catch (ClassCastException e) {
        }
    }

    //5. purpose: getInt with Defined Double Property
    public void testGetIntConversionFromDefinedDoubleProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DOUBLE);

        double db = 12;
        dataObject.setDouble(property, db);// add it to instance list

        this.assertEquals((int)db, dataObject.getInt(property));
    }

    //6. purpose: getInt with Undefined Double Property
    public void testGetIntConversionFromUnDefinedDoubleProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTE);

        try {
            dataObject.getInt(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //7. purpose: getInt with Defined float Property
    public void testGetIntConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_FLOAT);

        float fl = 12;
        dataObject.setFloat(property, fl);// add it to instance list

        this.assertEquals((int)fl, dataObject.getInt(property));
    }

    //8. purpose: getInt with Undefined float Property
    public void testGetIntConversionFromUnDefinedFloatProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_FLOAT);

        try {
            dataObject.getInt(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //9. purpose: getInt with Defined int Property
    public void testGetIntConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INT);

        int in = 12;
        dataObject.setInt(property, in);// add it to instance list

        this.assertEquals((int)in, dataObject.getInt(property));
    }

    //10. purpose: getInt with Undefined int Property
    public void testGetIntConversionFromUnDefinedIntProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_INT);

        try {
            dataObject.getInt(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //11. purpose: getInt with Defined long Property
    public void testGetIntConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_LONG);

        long lg = 12;
        dataObject.setLong(property, lg);// add it to instance list

        this.assertEquals((int)lg, dataObject.getInt(property));
    }

    //12. purpose: getInt with Undefined long Property
    public void testGetIntConversionFromUnDefinedLongProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_LONG);

        try {
            dataObject.getInt(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //13. purpose: getInt with Defined short Property
    public void testGetIntConversionFromDefinedShortProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_SHORT);

        short shr = 12;
        dataObject.setShort(property, shr);// add it to instance list

        this.assertEquals((int)shr, dataObject.getInt(property));
    }

    //14. purpose: getInt with Undefined short Property
    public void testGetIntConversionFromUnDefinedShortProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_SHORT);

        try {
            dataObject.getInt(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //15. purpose: getInt with Defined String Property
    public void testGetIntConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_STRING);

        String str = "12";
        Integer s_d = new Integer(str);
        dataObject.setString(property, str);// add it to instance list

        this.assertEquals(s_d.intValue(), dataObject.getInt(property));
    }

    //16. purpose: getInt with Undefined string Property
    public void testGetIntConversionFromUnDefinedStringProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_STRING);

        try {
            dataObject.getInt(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //17. purpose: getInt with bytes property
    public void testGetIntFromBytes() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTES);
        String testString = "abc";
        byte[] bytes = testString.getBytes();
        dataObject.set(property, bytes);
        
        try {
            dataObject.getInt(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //18. purpose: getInt with Defined Decimal Property
    public void testGetIntConversionFromDefinedDecimalProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DECIMAL);

        int db = 12;
        BigDecimal bd = new BigDecimal(db);
        dataObject.setBigDecimal(property, bd);// add it to instance list

        this.assertEquals(bd.intValue(), dataObject.getInt(property));
    }

    //19. purpose: getInt with Undefined decimal Property
    public void testGetIntConversionFromUnDefinedDecimalProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DECIMAL);

        try {
            dataObject.getInt(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //20. purpose: getInt with Defined integer Property
    public void testGetIntConversionFromDefinedIntegerProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INTEGER);

        BigInteger bi = new BigInteger("12");
        dataObject.setBigInteger(property, bi);// add it to instance list

        this.assertEquals(bi.intValue(), dataObject.getInt(property));
    }

    //21. purpose: getInt with Undefined Integer Property
    public void testGetIntConversionFromUnDefinedIntegerProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_INTEGER);

        try {
            dataObject.getInt(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //22. purpose: getInt with date property
    public void testGetIntFromDate() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DATE);
        Date theDate = Calendar.getInstance().getTime();
        dataObject.set(property, theDate);
        try {
            dataObject.getInt(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //purpose: getInt with nul value
    public void testGetIntWithNullArgument() {
        try {
            Property p = null;
            dataObject.getInt(p);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //1. purpose: getInt with boolean property
    public void testGetIntFromBooleanObject() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BOOLEANOBJECT);
        dataObject.set(property, true);
        
        int value = dataObject.getInt(property);
        assertEquals(1, value);
        
    }

    //2. purpose: getInt with Defined Byte Property
    public void testGetIntConversionFromDefinedByteObject() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTEOBJECT);

        byte by = 12;

        dataObject.setByte(property, by);// add it to instance list

        this.assertEquals((int)by, dataObject.getInt(property));
    }

    //4. purpose: getInt with character property
    public void testGetIntFromCharacterObject() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_CHARACTEROBJECT);
        dataObject.set(property, "t");
        try {
            dataObject.getInt(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //5. purpose: getInt with Defined Double Property
    public void testGetIntConversionFromDefinedDoubleObject() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DOUBLEOBJECT);

        double db = 12;
        dataObject.setDouble(property, db);// add it to instance list

        this.assertEquals((int)db, dataObject.getInt(property));
    }

    //7. purpose: getInt with Defined float Property
    public void testGetIntConversionFromDefinedFloatObject() {
        // dataObject's type add float property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_FLOATOBJECT);

        float fl = 12;
        dataObject.setFloat(property, fl);// add it to instance list

        this.assertEquals((int)fl, dataObject.getInt(property));
    }

    //9. purpose: getInt with Defined int Property
    public void testGetIntConversionFromDefinedIntObject() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INTOBJECT);

        int in = 12;
        dataObject.setInt(property, in);// add it to instance list

        this.assertEquals((int)in, dataObject.getInt(property));
    }
    
     //9. purpose: getInt with Defined int Property
    public void testGetIntConversionFromNullDefinedIntObject() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INTOBJECT);        
        int value = dataObject.getInt(property);
        
        this.assertEquals(0, value);        
    }

    //11. purpose: getInt with Defined long Property
    public void testGetIntConversionFromDefinedLongObject() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_LONGOBJECT);

        long lg = 12;
        dataObject.setLong(property, lg);// add it to instance list

        this.assertEquals((int)lg, dataObject.getInt(property));
    }

    //13. purpose: getInt with Defined short Property
    public void testGetIntConversionFromDefinedShortObject() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_SHORTOBJECT);

        short shr = 12;
        dataObject.setShort(property, shr);// add it to instance list

        this.assertEquals((int)shr, dataObject.getInt(property));
    }

    // purpose: setString with incorrect Integer as String Property
    public void testInvalidIntFromDefinedStringProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty) type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INT);

        String str = "12&";
        try {
            dataObject.setString(property, str);
        } catch (SDOException sdo) {
            this.assertEquals(sdo.getErrorCode(), SDOException.INVALID_PROPERTY_VALUE);
        }
    }
}
