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
import java.util.Date;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOConstants;

public class SDODataObjectGetLongConversionTest extends SDODataObjectConversionTestCases {
    public SDODataObjectGetLongConversionTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetLongConversionTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getLong with boolean property
    public void testGetLongFromBoolean() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BOOLEAN);
        dataObject.set(property, true);
        try {
            long longValue = dataObject.getLong(property);
            assertEquals(1, longValue);
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getLong with Defined Byte Property
    public void testGetLongConversionFromDefinedByteProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTE);

        byte by = 12;

        dataObject.setByte(property, by);// add it to instance list

        this.assertEquals((long)by, dataObject.getLong(property));
    }

    //3. purpose: getLong with Undefined Byte Property
    public void testGetLongConversionFromUnDefinedByteProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTE);

        try {
            dataObject.getLong(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //4. purpose: getLong with character property
    public void testGetLongFromCharacter() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_CHARACTER);
        dataObject.set(property, 'c');
        try {
            dataObject.getLong(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //5. purpose: getLong with Defined Double Property
    public void testGetLongConversionFromDefinedDoubleProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DOUBLE);

        double db = 12;
        dataObject.setDouble(property, db);// add it to instance list

        this.assertEquals((long)db, dataObject.getLong(property));
    }

    //6. purpose: getLong with Undefined Double Property
    public void testGetLongConversionFromUnDefinedDoubleProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTE);

        try {
            dataObject.getLong(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //7. purpose: getLong with Defined float Property
    public void testGetLongConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_FLOAT);

        float fl = 12;
        dataObject.setFloat(property, fl);// add it to instance list

        this.assertEquals((long)fl, dataObject.getLong(property));
    }

    //8. purpose: getLong with Undefined float Property
    public void testGetLongConversionFromUnDefinedFloatProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_FLOAT);

        try {
            dataObject.getLong(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //9. purpose: getLong with Defined int Property
    public void testGetLongConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INT);

        int in = 12;
        dataObject.setInt(property, in);// add it to instance list

        this.assertEquals((long)in, dataObject.getLong(property));
    }

    //10. purpose: getLong with Undefined int Property
    public void testGetLongConversionFromUnDefinedIntProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_INT);

        try {
            dataObject.getLong(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //11. purpose: getLong with Defined long Property
    public void testGetLongConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_LONG);

        long lg = 12;
        dataObject.setLong(property, lg);// add it to instance list

        this.assertEquals(lg, dataObject.getLong(property));
    }

    //12. purpose: getLong with Undefined long Property
    public void testGetLongConversionFromUnDefinedLongProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_LONG);

        try {
            dataObject.getLong(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //13. purpose: getLong with Defined short Property
    public void testGetLongConversionFromDefinedShortProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_SHORT);

        short shr = 12;
        dataObject.setShort(property, shr);// add it to instance list

        this.assertEquals((long)shr, dataObject.getLong(property));
    }

    //14. purpose: getLong with Undefined short Property
    public void testGetLongConversionFromUnDefinedShortProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_SHORT);

        try {
            dataObject.getLong(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //15. purpose: getLong with Defined String Property
    public void testGetLongConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_STRING);

        String str = "12";
        Long s_d = new Long(str);
        dataObject.setString(property, str);// add it to instance list

        this.assertEquals(s_d.longValue(), dataObject.getLong(property));
    }

    //16. purpose: getLong with Undefined string Property
    public void testGetLongConversionFromUnDefinedStringProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_STRING);

        try {
            dataObject.getLong(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //17. purpose: getLong with bytes property
    public void testGetLongFromBytes() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTES);
        dataObject.set(property, new String("cde").getBytes());
        try {
            dataObject.getLong(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //18. purpose: getLong with Defined Decimal Property
    public void testGetLongConversionFromDefinedDecimalProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DECIMAL);

        long db = 12;
        BigDecimal bd = new BigDecimal(db);
        dataObject.setBigDecimal(property, bd);// add it to instance list

        this.assertEquals(bd.longValue(), dataObject.getLong(property));
    }

    //19. purpose: getLong with Undefined decimal Property
    public void testGetLongConversionFromUnDefinedDecimalProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DECIMAL);

        try {
            dataObject.getLong(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //20. purpose: getLong with Defined integer Property
    public void testGetLongConversionFromDefinedIntegerProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INTEGER);

        BigInteger bi = new BigInteger("12");
        dataObject.setBigInteger(property, bi);// add it to instance list

        this.assertEquals(bi.longValue(), dataObject.getLong(property));
    }

    //21. purpose: getLong with Undefined Integer Property
    public void testGetLongConversionFromUnDefinedIntegerProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_INTEGER);

        try {
            dataObject.getLong(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //22. purpose: getLong with Defined date Property
    public void testGetLongConversionFromDefinedDateProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DATE);

        long d = 120000;
        Date bi = new Date(d);
        dataObject.setDate(property, bi);// add it to instance list

        this.assertEquals(bi.getTime(), dataObject.getLong(property));
    }

    //23. purpose: getLong with Undefined date Property
    public void testGetLongConversionFromUnDefinedDateProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DATE);

        try {
            dataObject.getLong(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //purpose: getLong with nul value
    public void testGetIntWithNullArgument() {
        try {
            Property p = null;
            dataObject.getLong(p);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testGetLongFromBooleanObject() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BOOLEANOBJECT);
        dataObject.set(property, Boolean.FALSE);
        try {
            long longValue = dataObject.getLong(property);
            assertEquals(0, longValue);
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getLong with Defined Byte Property
    public void testGetLongConversionFromDefinedByteObject() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTEOBJECT);

        byte by = 12;

        dataObject.setByte(property, by);// add it to instance list

        this.assertEquals((long)by, dataObject.getLong(property));
    }

    //4. purpose: getLong with character property
    public void testGetLongFromCharacterObject() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_CHARACTEROBJECT);
        dataObject.set(property, new Character('v'));
        try {
            dataObject.getLong(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //5. purpose: getLong with Defined Double Property
    public void testGetLongConversionFromDefinedDoubleObject() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DOUBLEOBJECT);

        double db = 12;
        dataObject.setDouble(property, db);// add it to instance list

        this.assertEquals((long)db, dataObject.getLong(property));
    }

    //7. purpose: getLong with Defined float Property
    public void testGetLongConversionFromDefinedFloatObject() {
        // dataObject's type add float property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_FLOATOBJECT);

        float fl = 12;
        dataObject.setFloat(property, fl);// add it to instance list

        this.assertEquals((long)fl, dataObject.getLong(property));
    }

    //9. purpose: getLong with Defined int Property
    public void testGetLongConversionFromDefinedIntObject() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INTOBJECT);

        int in = 12;
        dataObject.setInt(property, in);// add it to instance list

        this.assertEquals((long)in, dataObject.getLong(property));
    }

    //11. purpose: getLong with Defined long Property
    public void testGetLongConversionFromDefinedLongObject() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_LONGOBJECT);

        long lg = 12;
        dataObject.setLong(property, lg);// add it to instance list

        this.assertEquals(lg, dataObject.getLong(property));
    }

    //13. purpose: getLong with Defined short Property
    public void testGetLongConversionFromDefinedShortObject() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_SHORTOBJECT);

        short shr = 12;
        dataObject.setShort(property, shr);// add it to instance list

        this.assertEquals((long)shr, dataObject.getLong(property));
    }
}
