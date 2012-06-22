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

public class SDODataObjectGetDoubleWithIndexConversionTest extends SDODataObjectConversionTestCases {
    public SDODataObjectGetDoubleWithIndexConversionTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetDoubleWithIndexConversionTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getDouble with boolean property
    public void testGetDoubleFromBoolean() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BOOLEAN);
        dataObject.set(property, true);
        try {
            dataObject.getDouble(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
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

        dataObject.setByte(PROPERTY_INDEX, by);// add it to instance list

        this.assertEquals((double)by, dataObject.getDouble(PROPERTY_INDEX), delta);
    }

    //3. purpose: getDouble with Undefined Byte Property
    public void testGetDoubleConversionFromUnDefinedProperty() {
        try {
            dataObject.getDouble(1);
            fail("IllegalArgumentException should be thrown.");
        } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX, e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");
    }

    //4. purpose: getDouble with character property
    public void testGetDoubleFromCharacter() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_CHARACTER);
        dataObject.set(property, 'e');
        try {
            dataObject.getDouble(PROPERTY_INDEX);
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
        dataObject.setDouble(PROPERTY_INDEX, db);// add it to instance list

        this.assertEquals((double)db, dataObject.getDouble(PROPERTY_INDEX), delta);
    }

    //7. purpose: getDouble with Defined float Property
    public void testGetDoubleConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_FLOAT);

        float fl = 12;
        double delta = 0.0;
        dataObject.setFloat(PROPERTY_INDEX, fl);// add it to instance list

        this.assertEquals((double)fl, dataObject.getDouble(PROPERTY_INDEX), delta);
    }

    //9. purpose: getDouble with Defined int Property
    public void testGetDoubleConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INT);

        int in = 12;
        double delta = 0.0;
        dataObject.setInt(PROPERTY_INDEX, in);// add it to instance list

        this.assertEquals((double)in, dataObject.getDouble(PROPERTY_INDEX), delta);
    }

    //11. purpose: getDouble with Defined long Property
    public void testGetDoubleConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_LONG);

        long lg = 12;
        double delta = 0.0;
        dataObject.setLong(PROPERTY_INDEX, lg);// add it to instance list

        this.assertEquals((double)lg, dataObject.getDouble(PROPERTY_INDEX), delta);
    }

    //13. purpose: getDouble with Defined short Property
    public void testGetDoubleConversionFromDefinedShortProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_SHORT);

        short shr = 12;
        double delta = 0.0;
        dataObject.setShort(PROPERTY_INDEX, shr);// add it to instance list

        this.assertEquals((double)shr, dataObject.getDouble(PROPERTY_INDEX), delta);
    }

    //15. purpose: getDouble with Defined String Property
    public void testGetDoubleConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_STRING);

        String str = "12";
        Double s_d = new Double(str);
        double delta = 0.0;
        dataObject.setString(PROPERTY_INDEX, str);// add it to instance list

        this.assertEquals(s_d.doubleValue(), dataObject.getDouble(PROPERTY_INDEX), delta);
    }

    //17. purpose: getDouble with bytes property
    public void testGetDoubleFromBytes() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTES);
        dataObject.set(property, new byte[] { 10, 100 });
        try {
            dataObject.getDouble(PROPERTY_INDEX);
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
        dataObject.setBigDecimal(PROPERTY_INDEX, bd);// add it to instance list

        this.assertEquals(bd.doubleValue(), dataObject.getDouble(PROPERTY_INDEX), delta);
    }

    //20. purpose: getDouble with Defined integer Property
    public void testGetDoubleConversionFromDefinedIntegerProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INTEGER);

        BigInteger bi = new BigInteger("12");
        double delta = 0.0;
        dataObject.setBigInteger(PROPERTY_INDEX, bi);// add it to instance list

        this.assertEquals(bi.doubleValue(), dataObject.getDouble(PROPERTY_INDEX), delta);
    }

    //22. purpose: getDouble with date property
    public void testGetDoubleFromDate() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DATE);
        dataObject.set(property, Calendar.getInstance().getTime());
        try {
            dataObject.getDouble(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //purpose: getDouble with nul value
    public void testGetDoubleWithNullArgument() {
        try {
            int p = -1;
            dataObject.getDouble(p);
        } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX, e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");
    }
}
