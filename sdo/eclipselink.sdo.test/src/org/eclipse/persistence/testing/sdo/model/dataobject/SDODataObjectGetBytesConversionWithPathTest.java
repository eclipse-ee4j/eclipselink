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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;

public class SDODataObjectGetBytesConversionWithPathTest extends SDODataObjectConversionWithPathTestCases {
    public SDODataObjectGetBytesConversionWithPathTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetBytesConversionWithPathTest" };
        TestRunner.main(arguments);
    }


    //1. purpose: getBytes with Boolean property
    public void testGetBytesFromBoolean() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BOOLEAN);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        dataObject_c.set(property_c, true);
        try {
            dataObject_a.getBytes(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //2. purpose: getBytes with Byte property
    public void testGetBytesFromByte() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        dataObject_c.set(property_c, new String("eee").getBytes()[0]);
        try {
            dataObject_a.getBytes(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //3. purpose: getBytes with character property
    public void testGetBytesFromCharacter() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_CHARACTER);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        dataObject_c.set(property_c, 'e');
        try {
            dataObject_a.getBytes(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //4. purpose: getBytes with Double Property
    public void testGetBytesFromDouble() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DOUBLE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        double value = 3;
        dataObject_c.set(property_c, value);
        try {
            dataObject_a.getBytes(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //5. purpose: getBytes with float Property
    public void testGetBytesFromFloat() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_FLOAT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        float value = 3;
        dataObject_c.set(property_c, value);
        try {
            dataObject_a.getBytes(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //6. purpose: getBytes with int Property
    public void testGetBytesFromInt() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        int value = 3;
        dataObject_c.set(property_c, value);
        try {
            dataObject_a.getBytes(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //7. purpose: getBytes with long Property
    public void testGetBytesFromLong() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_LONG);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        long value = 3;
        dataObject_c.set(property_c, value);
        try {
            dataObject_a.getBytes(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //8. purpose: getBytes with short Property
    public void testGetBytesFromShort() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_SHORT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        short value = 3;
        dataObject_c.set(property_c, value);
        try {
            dataObject_a.getBytes(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //9. purpose: getBytes with string Property
    public void testGetBytesFromString() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        dataObject_c.set(property_c, new String("0A64"));
        try {
            byte[] value = dataObject_a.getBytes(propertyPath_a_b_c);
            byte[] controlBytes = new byte[]{10, 100};
            assertEqualsBytes(controlBytes, value);
        } catch (ClassCastException e) {
        }
    }

    //10. purpose: getBytes with Defined Bytes Property
    public void testGetBytesConversionFromDefinedBytesProperty() {
        // dataObject's type add boolean property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTES);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        byte[] b = { 12, 13 };

        dataObject_a.setBytes(propertyPath_a_b_c, b);// add it to instance list

        this.assertTrue(Arrays.equals(b, dataObject_a.getBytes(propertyPath_a_b_c)));
    }

    //11. purpose: getBytes with Undefined Bytes Property
    public void testGetBytesConversionFromUnDefinedBytesProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTES);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getBytes(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //12. purpose: getBytes with decimal property
    public void testGetBytesFromDecimal() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        BigDecimal value = new BigDecimal(3);
        dataObject_c.set(property_c, value);
        try {
            dataObject_a.getBytes(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //13. purpose: getBytes with Defined BigInteger Property   !!   OX PRO     !!
    public void testGetBytesConversionFromDefinedIntegerProperty() {
        // dataObject's type add boolean property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTES);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        BigInteger bin = new BigInteger("12");
        byte[] b = bin.toByteArray();

        dataObject_a.setBigInteger(propertyPath_a_b_c, bin);// add it to instance list
        byte[] b1 = dataObject_a.getBytes(propertyPath_a_b_c);
        this.assertTrue(Arrays.equals(b, b1));
    }

    //11. purpose: getBytes with Undefined Integer Property
    public void testGetBytesConversionFromUnDefinedIntegerProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INTEGER);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getBytes(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //22. purpose: getBytes with date property
    public void testGetBytesFromDate() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DATE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        dataObject_c.set(property_c, Calendar.getInstance().getTime());
        try {
            dataObject_a.getBytes(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //purpose: getBytes with nul value
    public void testGetBytesWithNullArgument() {
        try {
            String p = null;
            dataObject_a.getBytes(p);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }
}
