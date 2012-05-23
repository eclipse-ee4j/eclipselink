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

public class SDODataObjectGetFloatConversionTest extends SDODataObjectConversionTestCases {
    public SDODataObjectGetFloatConversionTest(String name) {
        super(name);
    }

  public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetFloatConversionTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getFloat with boolean property
    public void testGetFloatFromBoolean() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BOOLEAN);        
        dataObject.set(property, true);
        try {
            float floatValue =dataObject.getFloat(property);
            assertEquals(1, floatValue);
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getFloat with Defined Byte Property
    public void testGetFloatConversionFromDefinedByteProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTE);

        byte by = 12;
        float delta = 0;

        dataObject.setByte(property, by);// add it to instance list

        this.assertEquals((float)by, dataObject.getFloat(property), delta);
    }

    //3. purpose: getFloat with Undefined Byte Property
    public void testGetFloatConversionFromUnDefinedByteProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTE);

        try {
            dataObject.getFloat(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //4. purpose: getFloat with character property
    public void testGetFloatFromCharacter() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_CHARACTER);
        dataObject.set(property, 't');
        try {
            dataObject.getFloat(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //5. purpose: getFloat with Defined Double Property
    public void testGetFloatConversionFromDefinedDoubleProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DOUBLE);

        double db = 12;
        float delta = 0;
        dataObject.setDouble(property, db);// add it to instance list

        this.assertEquals((float)db, dataObject.getFloat(property), delta);
    }

    //6. purpose: getFloat with Undefined Double Property
    public void testGetFloatConversionFromUnDefinedDoubleProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTE);

        try {
            dataObject.getFloat(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //7. purpose: getFloat with Defined float Property
    public void testGetFloatConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_FLOAT);

        float fl = 12;
        float delta = 0;
        dataObject.setFloat(property, fl);// add it to instance list

        this.assertEquals((float)fl, dataObject.getFloat(property), delta);
    }

    //8. purpose: getFloat with Undefined float Property
    public void testGetFloatConversionFromUnDefinedFloatProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_FLOAT);

        try {
            dataObject.getFloat(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //9. purpose: getFloat with Defined int Property
    public void testGetFloatConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INT);

        int in = 12;
        float delta = 0;
        dataObject.setInt(property, in);// add it to instance list

        this.assertEquals((float)in, dataObject.getFloat(property), delta);
    }

    //10. purpose: getFloat with Undefined int Property
    public void testGetFloatConversionFromUnDefinedIntProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_INT);

        try {
            dataObject.getFloat(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //11. purpose: getFloat with Defined long Property
    public void testGetFloatConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_LONG);

        long lg = 12;
        float delta = 0;
        dataObject.setLong(property, lg);// add it to instance list

        this.assertEquals((float)lg, dataObject.getFloat(property), delta);
    }

    //12. purpose: getFloat with Undefined long Property
    public void testGetFloatConversionFromUnDefinedLongProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_LONG);

        try {
            dataObject.getFloat(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //13. purpose: getFloat with Defined short Property
    public void testGetFloatConversionFromDefinedShortProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_SHORT);

        short shr = 12;
        float delta = 0;
        dataObject.setShort(property, shr);// add it to instance list

        this.assertEquals((float)shr, dataObject.getFloat(property), delta);
    }

    //14. purpose: getFloat with Undefined short Property
    public void testGetFloatConversionFromUnDefinedShortProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_SHORT);

        try {
            dataObject.getFloat(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //15. purpose: getFloat with Defined String Property
    public void testGetFloatConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_STRING);

        String str = "12";
        Float s_d = new Float(str);
        float delta = 0;
        dataObject.setString(property, str);// add it to instance list

        this.assertEquals(s_d.doubleValue(), dataObject.getFloat(property), delta);
    }

    //16. purpose: getFloat with Undefined string Property
    public void testGetFloatConversionFromUnDefinedStringProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_STRING);

        try {
            dataObject.getFloat(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //17. purpose: getFloat with bytes property
    public void testGetFloatFromBytes() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTES);
        dataObject.set(property, new String("123").getBytes());
        try {
            dataObject.getFloat(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //18. purpose: getFloat with Defined Decimal Property
    public void testGetFloatConversionFromDefinedDecimalProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DECIMAL);

        float db = 12;
        BigDecimal bd = new BigDecimal(db);
        float delta = 0;
        dataObject.setBigDecimal(property, bd);// add it to instance list

        this.assertEquals(bd.floatValue(), dataObject.getFloat(property), delta);
    }

    //19. purpose: getFloat with Undefined decimal Property
    public void testGetFloatConversionFromUnDefinedDecimalProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DECIMAL);

        try {
            dataObject.getFloat(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //20. purpose: getFloat with Defined integer Property
    public void testGetFloatConversionFromDefinedIntegerProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INTEGER);

        BigInteger bi = new BigInteger("12");
        float delta = 0;
        dataObject.setBigInteger(property, bi);// add it to instance list

        this.assertEquals(bi.floatValue(), dataObject.getFloat(property), delta);
    }

    //21. purpose: getFloat with Undefined Integer Property
    public void testGetFloatConversionFromUnDefinedIntegerProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_INTEGER);

        try {
            dataObject.getFloat(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //22. purpose: getFloat with date property
    public void testGetFloatFromDate() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DATE);
        dataObject.set(property, Calendar.getInstance().getTime());
        try {
            dataObject.getFloat(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //purpose: getFloat with nul value
    public void testGetFloatWithNullArgument() {
        try {
            Property p = null;
            dataObject.getFloat(p);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testGetFloatFromBooleanObject() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BOOLEAN);
        dataObject.set(property, Boolean.TRUE);
        try {
            float floatValue =dataObject.getFloat(property);
            assertEquals(1, floatValue);
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getFloat with Defined Byte Property
    public void testGetFloatConversionFromDefinedByteObject() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTEOBJECT);

        byte by = 12;
        float delta = 0;

        dataObject.setByte(property, by);// add it to instance list

        this.assertEquals((float)by, dataObject.getFloat(property), delta);
    }

    //4. purpose: getFloat with character property
    public void testGetFloatFromCharacterObject() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_CHARACTEROBJECT);
        dataObject.set(property, new Character('y'));
        try {
            dataObject.getFloat(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //5. purpose: getFloat with Defined Double Property
    public void testGetFloatConversionFromDefinedDoubleObject() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DOUBLEOBJECT);

        double db = 12;
        float delta = 0;
        dataObject.setDouble(property, db);// add it to instance list

        this.assertEquals((float)db, dataObject.getFloat(property), delta);
    }

    //7. purpose: getFloat with Defined float Property
    public void testGetFloatConversionFromDefinedFloatObject() {
        // dataObject's type add float property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_FLOATOBJECT);

        float fl = 12;
        float delta = 0;
        dataObject.setFloat(property, fl);// add it to instance list

        this.assertEquals((float)fl, dataObject.getFloat(property), delta);
    }

    //9. purpose: getFloat with Defined int Property
    public void testGetFloatConversionFromDefinedIntObject() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INTOBJECT);
        int in = 12;
        float delta = 0;
        dataObject.setInt(property, in);// add it to instance list

        this.assertEquals((float)in, dataObject.getFloat(property), delta);
    }

    //11. purpose: getFloat with Defined long Property
    public void testGetFloatConversionFromDefinedLongObject() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_LONGOBJECT);

        long lg = 12;
        float delta = 0;
        dataObject.setLong(property, lg);// add it to instance list

        this.assertEquals((float)lg, dataObject.getFloat(property), delta);
    }

    //13. purpose: getFloat with Defined short Property
    public void testGetFloatConversionFromDefinedShortObject() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_SHORTOBJECT);

        short shr = 12;
        float delta = 0;
        dataObject.setShort(property, shr);// add it to instance list

        this.assertEquals((float)shr, dataObject.getFloat(property), delta);
    }
}
