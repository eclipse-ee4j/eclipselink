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
import java.util.Date;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.exceptions.SDOException;

public class SDODataObjectGetLongWithIndexConversionTest extends SDODataObjectConversionTestCases {
    public SDODataObjectGetLongWithIndexConversionTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetLongWithIndexConversionTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getLong with boolean property
    public void testGetLongFromBoolean() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BOOLEAN);
        dataObject.set(property, true);
        try {
            long longValue = dataObject.getLong(PROPERTY_INDEX);
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

        dataObject.setByte(PROPERTY_INDEX, by);// add it to instance list

        this.assertEquals((long)by, dataObject.getLong(PROPERTY_INDEX));
    }

    //3. purpose: getLong with Undefined Byte Property
    public void testGetLongConversionFromUnDefinedProperty() {        

        try {
            dataObject.getLong(1);
        } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");    
    }

    //4. purpose: getLong with character property
    public void testGetLongFromCharacter() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_CHARACTER);
        dataObject.set(property, 'l');
        try {
            dataObject.getLong(PROPERTY_INDEX);
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
        dataObject.setDouble(PROPERTY_INDEX, db);// add it to instance list

        this.assertEquals((long)db, dataObject.getLong(PROPERTY_INDEX));
    }

    //7. purpose: getLong with Defined float Property
    public void testGetLongConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_FLOAT);

        float fl = 12;
        dataObject.setFloat(PROPERTY_INDEX, fl);// add it to instance list

        this.assertEquals((long)fl, dataObject.getLong(PROPERTY_INDEX));
    }


    //9. purpose: getLong with Defined int Property
    public void testGetLongConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INT);

        int in = 12;
        dataObject.setInt(PROPERTY_INDEX, in);// add it to instance list

        this.assertEquals((long)in, dataObject.getLong(PROPERTY_INDEX));
    }
    

    //11. purpose: getLong with Defined long Property
    public void testGetLongConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_LONG);

        long lg = 12;
        dataObject.setLong(PROPERTY_INDEX, lg);// add it to instance list

        this.assertEquals(lg, dataObject.getLong(PROPERTY_INDEX));
    }


    //13. purpose: getLong with Defined short Property
    public void testGetLongConversionFromDefinedShortProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_SHORT);

        short shr = 12;
        dataObject.setShort(PROPERTY_INDEX, shr);// add it to instance list

        this.assertEquals((long)shr, dataObject.getLong(PROPERTY_INDEX));
    }


    //15. purpose: getLong with Defined String Property
    public void testGetLongConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_STRING);

        String str = "12";
        Long s_d = new Long(str);
        dataObject.setString(PROPERTY_INDEX, str);// add it to instance list

        this.assertEquals(s_d.longValue(), dataObject.getLong(PROPERTY_INDEX));
    }

    //17. purpose: getLong with bytes property
    public void testGetLongFromBytes() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTES);
        dataObject.set(property, new String("eee").getBytes());
        try {
            dataObject.getLong(PROPERTY_INDEX);
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
        dataObject.setBigDecimal(PROPERTY_INDEX, bd);// add it to instance list

        this.assertEquals(bd.longValue(), dataObject.getLong(PROPERTY_INDEX));
    }

      //20. purpose: getLong with Defined integer Property
    public void testGetLongConversionFromDefinedIntegerProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INTEGER);

        BigInteger bi = new BigInteger("12");
        dataObject.setBigInteger(PROPERTY_INDEX, bi);// add it to instance list

        this.assertEquals(bi.longValue(), dataObject.getLong(PROPERTY_INDEX));
    }


    //22. purpose: getLong with Defined date Property
    public void testGetLongConversionFromDefinedDateProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DATE);

        long d = 120000;
        Date bi = new Date(d);
        dataObject.setDate(PROPERTY_INDEX, bi);// add it to instance list

        this.assertEquals(bi.getTime(), dataObject.getLong(PROPERTY_INDEX));
    }

    //purpose: getLong with nul value
    public void testGetIntWithNullArgument() {
        try {
            int p = -1;
            dataObject.getLong(p);
        } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");
    }
}
