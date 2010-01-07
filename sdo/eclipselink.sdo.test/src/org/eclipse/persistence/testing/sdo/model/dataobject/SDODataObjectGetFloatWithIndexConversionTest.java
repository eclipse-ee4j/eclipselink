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
import java.util.Calendar;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.exceptions.SDOException;

public class SDODataObjectGetFloatWithIndexConversionTest extends SDODataObjectConversionTestCases {
    public SDODataObjectGetFloatWithIndexConversionTest(String name) {
        super(name);
    }
            
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetFloatWithIndexConversionTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getFloat with boolean property
    public void testGetFloatFromBoolean() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BOOLEAN);
        dataObject.set(property, true);
        try {
            float floatValue =dataObject.getFloat(PROPERTY_INDEX);
            assertEquals(1, floatValue);
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getFloat with Defined Byte Property
    public void testGetFloatConversionFromDefinedByteProperty() {
        // dataObject's type add boolean property
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTE);
        type.addDeclaredProperty(property);

        byte by = 12;
        float delta = 0;

        dataObject.setByte(PROPERTY_INDEX, by);// add it to instance list

        this.assertEquals((float)by, dataObject.getFloat(PROPERTY_INDEX), delta);
    }

    //3. purpose: getFloat with Undefined Byte Property
    public void testGetFloatConversionFromUnDefinedProperty() {
        try {
            dataObject.getFloat(1);
         } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");    
    }

    //4. purpose: getFloat with character property
    public void testGetFloatFromCharacter() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_CHARACTER);
        dataObject.set(property, 'c');
        try {
            dataObject.getFloat(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //5. purpose: getFloat with Defined Double Property
    public void testGetFloatConversionFromDefinedDoubleProperty() {
        // dataObject's type add boolean property
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DOUBLE);
        type.addDeclaredProperty(property);

        double db = 12;
        float delta = 0;
        dataObject.setDouble(PROPERTY_INDEX, db);// add it to instance list

        this.assertEquals((float)db, dataObject.getFloat(PROPERTY_INDEX), delta);
    }

    //7. purpose: getFloat with Defined float Property
    public void testGetFloatConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_FLOAT);
        type.addDeclaredProperty(property);

        float fl = 12;
        float delta = 0;
        dataObject.setFloat(PROPERTY_INDEX, fl);// add it to instance list

        this.assertEquals((float)fl, dataObject.getFloat(PROPERTY_INDEX), delta);
    }

    //9. purpose: getFloat with Defined int Property
    public void testGetFloatConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_INT);
        type.addDeclaredProperty(property);

        int in = 12;
        float delta = 0;
        dataObject.setInt(PROPERTY_INDEX, in);// add it to instance list

        this.assertEquals((float)in, dataObject.getFloat(PROPERTY_INDEX), delta);
    }

    //11. purpose: getFloat with Defined long Property
    public void testGetFloatConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_LONG);
        type.addDeclaredProperty(property);

        long lg = 12;
        float delta = 0;
        dataObject.setLong(PROPERTY_INDEX, lg);// add it to instance list

        this.assertEquals((float)lg, dataObject.getFloat(PROPERTY_INDEX), delta);
    }

    //13. purpose: getFloat with Defined short Property
    public void testGetFloatConversionFromDefinedShortProperty() {
        // dataObject's type add short property
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_SHORT);
        type.addDeclaredProperty(property);

        short shr = 12;
        float delta = 0;
        dataObject.setShort(PROPERTY_INDEX, shr);// add it to instance list

        this.assertEquals((float)shr, dataObject.getFloat(PROPERTY_INDEX), delta);
    }

    //15. purpose: getFloat with Defined String Property
    public void testGetFloatConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_STRING);
        type.addDeclaredProperty(property);

        String str = "12";
        Float s_d = new Float(str);
        float delta = 0;
        dataObject.setString(PROPERTY_INDEX, str);// add it to instance list

        this.assertEquals(s_d.doubleValue(), dataObject.getFloat(PROPERTY_INDEX), delta);
    }

    //17. purpose: getFloat with bytes property
    public void testGetFloatFromBytes() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTES);
        dataObject.set(property, new String("eee").getBytes());
        try {
            dataObject.getFloat(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //18. purpose: getFloat with Defined Decimal Property
    public void testGetFloatConversionFromDefinedDecimalProperty() {
        // dataObject's type add int property
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DECIMAL);
        type.addDeclaredProperty(property);

        float db = 12;
        BigDecimal bd = new BigDecimal(db);
        float delta = 0;
        dataObject.setBigDecimal(PROPERTY_INDEX, bd);// add it to instance list

        this.assertEquals(bd.floatValue(), dataObject.getFloat(PROPERTY_INDEX), delta);
    }

    //20. purpose: getFloat with Defined integer Property
    public void testGetFloatConversionFromDefinedIntegerProperty() {
        // dataObject's type add int property
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_INTEGER);
        type.addDeclaredProperty(property);

        BigInteger bi = new BigInteger("12");
        float delta = 0;
        dataObject.setBigInteger(PROPERTY_INDEX, bi);// add it to instance list

        this.assertEquals(bi.floatValue(), dataObject.getFloat(PROPERTY_INDEX), delta);
    }

    //22. purpose: getFloat with date property
    public void testGetFloatFromDate() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DATE);
        dataObject.set(property, Calendar.getInstance().getTime());
        try {
            dataObject.getFloat(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //purpose: getFloat with nul value
    public void testGetFloatWithNullArgument() {
        try {
            int p = -1;
            dataObject.getFloat(p);
        } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");    
    }
}
