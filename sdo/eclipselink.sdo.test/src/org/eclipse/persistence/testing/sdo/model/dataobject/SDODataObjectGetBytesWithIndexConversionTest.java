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
import java.util.Arrays;
import java.util.Calendar;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.exceptions.SDOException;

public class SDODataObjectGetBytesWithIndexConversionTest extends SDODataObjectConversionTestCases {
    public SDODataObjectGetBytesWithIndexConversionTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetBytesWithIndexConversionTest" };
        TestRunner.main(arguments);
    }


    //1. purpose: getBytes with Boolean property
    public void testGetBytesFromBoolean() {
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BOOLEAN);
        dataObject.set(property, true);
        try {
            dataObject.getBytes(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getBytes with Byte property
    public void testGetBytesFromByte() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);        
        property.setType(SDOConstants.SDO_BYTE);        
        dataObject.set(property, new String("ddd").getBytes()[0]);
        try {
            dataObject.getBytes(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //3. purpose: getBytes with character property
    public void testGetBytesFromCharacter() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);        
        property.setType(SDOConstants.SDO_CHARACTER);
        dataObject.set(property, 'd');
        
        try {
            dataObject.getBytes(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //4. purpose: getBytes with Double Property
    public void testGetBytesFromDouble() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);        
        property.setType(SDOConstants.SDO_DOUBLE);        
        double doubleValue = 3;
        dataObject.set(property, doubleValue);

        try {
            dataObject.getBytes(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //5. purpose: getBytes with float Property
    public void testGetBytesFromFloat() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);        
        property.setType(SDOConstants.SDO_FLOAT);
        float floatValue = 3;
        dataObject.set(property, floatValue);


        try {
            dataObject.getBytes(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //6. purpose: getBytes with int Property
    public void testGetBytesFromInt() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);        
         property.setType(SDOConstants.SDO_FLOAT);
        int intValue = 3;
        dataObject.set(property, intValue);

        try {
            dataObject.getBytes(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //7. purpose: getBytes with long Property
    public void testGetBytesFromLong() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);        
        property.setType(SDOConstants.SDO_LONG);
        long longValue = 3;
        dataObject.set(property, longValue);


        try {
            dataObject.getBytes(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //8. purpose: getBytes with short Property
    public void testGetBytesFromShort() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);        
        property.setType(SDOConstants.SDO_SHORT);
   
        short shortValue = 3;
        dataObject.set(property, shortValue);

   
        try {
            dataObject.getBytes(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //9. purpose: getBytes with string Property
    public void testGetBytesFromString() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);        
        property.setType(SDOConstants.SDO_STRING);
        
        dataObject.set(property, new String("0A64"));
        
        try {
            byte[] value = dataObject.getBytes(PROPERTY_INDEX);
            byte[] controlBytes = new byte[]{10, 100};
            assertEqualsBytes(controlBytes, value);                      
        } catch (ClassCastException e) {
        }
    }

    //10. purpose: getBytes with Defined Bytes Property
    public void testGetBytesConversionFromDefinedBytesProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BYTES);

        byte[] b = { 12, 13 };

        dataObject.setBytes(PROPERTY_INDEX, b);// add it to instance list

        this.assertTrue(Arrays.equals(b, dataObject.getBytes(PROPERTY_INDEX)));
    }

    //11. purpose: getBytes with Undefined  Property
    public void testGetBytesConversionFromUnDefinedProperty() {
        try {
            dataObject.getBytes(1);
        } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");        }

    //12. purpose: getBytes with decimal property
    public void testGetBytesFromDecimal() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);        
        property.setType(SDOConstants.SDO_DECIMAL);
        dataObject.set(property, new BigDecimal(2));

        try {
            dataObject.getBytes(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //13. purpose: getBytes with Defined Bytes Property   !!  OX PRO !!
    public void testGetBytesConversionFromDefinedIntegerProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_INTEGER);

        BigInteger bin = new BigInteger("12");
        byte[] b = bin.toByteArray();

        dataObject.setBigInteger(PROPERTY_INDEX, bin);// add it to instance list

        this.assertTrue(Arrays.equals(b, dataObject.getBytes(PROPERTY_INDEX)));
    }

    //22. purpose: getBytes with date property
    public void testGetBytesFromDate() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);        
        property.setType(SDOConstants.SDO_DATE);
        
        dataObject.set(property, Calendar.getInstance().getTime());
        try {
            dataObject.getBytes(PROPERTY_INDEX);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //purpose: getBytes with nul value
    public void testGetBytesWithNullArgument() {
        try {
            int p = -1;
            dataObject.getBytes(p);
          } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");    
    }
}
