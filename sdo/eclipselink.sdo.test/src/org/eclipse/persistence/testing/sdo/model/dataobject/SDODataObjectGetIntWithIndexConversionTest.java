/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

public class SDODataObjectGetIntWithIndexConversionTest extends SDODataObjectConversionTestCases {
    public SDODataObjectGetIntWithIndexConversionTest(String name) {
        super(name);
    }
    
     public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetIntWithIndexConversionTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getInt with boolean property
    public void testGetIntFromBoolean() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BOOLEAN);
        dataObject.set(property, true);
        
        int intvalue = dataObject.getInt(PROPERTY_INDEX);
        assertEquals(1, intvalue);
        
    }

    //2. purpose: getInt with Defined Byte Property
    public void testGetIntConversionFromDefinedByteProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTE);

        byte by = 12;

        dataObject.setByte(PROPERTY_INDEX, by);// add it to instance list

        this.assertEquals((int)by, dataObject.getInt(PROPERTY_INDEX));
    }

    //3. purpose: getInt with Undefined Byte Property
    public void testGetIntConversionFromUnDefinedProperty() {
        
        try {
            dataObject.getInt(1);
        } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");
    }

    //4. purpose: getInt with character property
    public void testGetIntFromCharacter() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_CHARACTER);
        dataObject.set(property, 'e');
        try {
            dataObject.getInt(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //5. purpose: getInt with Defined Double Property
    public void testGetIntConversionFromDefinedDoubleProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DOUBLE);

        double db = 12;
        dataObject.setDouble(PROPERTY_INDEX, db);// add it to instance list

        this.assertEquals((int)db, dataObject.getInt(PROPERTY_INDEX));
    }



    //7. purpose: getInt with Defined float Property
    public void testGetIntConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_FLOAT);

        float fl = 12;
        dataObject.setFloat(PROPERTY_INDEX, fl);// add it to instance list

        this.assertEquals((int)fl, dataObject.getInt(PROPERTY_INDEX));
    }


    //9. purpose: getInt with Defined int Property
    public void testGetIntConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INT);

        int in = 12;
        dataObject.setInt(PROPERTY_INDEX, in);// add it to instance list

        this.assertEquals((int)in, dataObject.getInt(PROPERTY_INDEX));
    }


    //11. purpose: getInt with Defined long Property
    public void testGetIntConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_LONG);

        long lg = 12;
        dataObject.setLong(PROPERTY_INDEX, lg);// add it to instance list

        this.assertEquals((int)lg, dataObject.getInt(PROPERTY_INDEX));
    }


    //13. purpose: getInt with Defined short Property
    public void testGetIntConversionFromDefinedShortProperty() {
        // dataObject's type add short property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_SHORT);

        short shr = 12;
        dataObject.setShort(PROPERTY_INDEX, shr);// add it to instance list

        this.assertEquals((int)shr, dataObject.getInt(PROPERTY_INDEX));
    }


    //15. purpose: getInt with Defined String Property
    public void testGetIntConversionFromDefinedStringProperty() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_STRING);

        String str = "12";
        Integer s_d = new Integer(str);
        dataObject.setString(PROPERTY_INDEX, str);// add it to instance list

        this.assertEquals(s_d.intValue(), dataObject.getInt(PROPERTY_INDEX));
    }

    //17. purpose: getInt with bytes property
    public void testGetIntFromBytes() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTES);
        dataObject.set(property, new String("rrr").getBytes());
        try {
            dataObject.getInt(PROPERTY_INDEX);
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
        dataObject.setBigDecimal(PROPERTY_INDEX, bd);// add it to instance list

        this.assertEquals(bd.intValue(), dataObject.getInt(PROPERTY_INDEX));
    }


    //20. purpose: getInt with Defined integer Property
    public void testGetIntConversionFromDefinedIntegerProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INTEGER);
        

        BigInteger bi = new BigInteger("12");
        dataObject.setBigInteger(PROPERTY_INDEX, bi);// add it to instance list

        this.assertEquals(bi.intValue(), dataObject.getInt(PROPERTY_INDEX));
    }


    //22. purpose: getInt with date property
    public void testGetIntFromDate() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DATE);
        dataObject.set(property, Calendar.getInstance().getTime());
        try {
            dataObject.getInt(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //purpose: getInt with nul value
    public void testGetIntWithNullArgument() {
        try {            
            dataObject.getInt(-1);
        } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");
    }
}
