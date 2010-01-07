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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;

public class SDODataObjectGetBytesByPositionalPathTest extends SDODataObjectGetByPositionalPathTestCases {
    public SDODataObjectGetBytesByPositionalPathTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetBytesByPositionalPathTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getBytes with Boolean property
    public void testGetBytesFromBoolean() {    
        property_c = (SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C);       
        property_c.setType(SDOConstants.SDO_BOOLEAN);
        dataObject_c.set(property_c, true);
        try {            
            dataObject_a.getBytes(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //2. purpose: getBytes with Byte property
    public void testGetBytesFromByte() {
        property_c = (SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C);       
        property_c.setType(SDOConstants.SDO_BYTE);
        dataObject_c.set(property_c, new String("aaa").getBytes()[0]);
        try {
            dataObject_a.getBytes(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //3. purpose: getBytes with character property
    public void testGetBytesFromCharacter() {
        property_c = (SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C);       
        property_c.setType(SDOConstants.SDO_CHARACTER);
        dataObject_c.set(property_c, 'c');
        try {
            dataObject_a.getBytes(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //4. purpose: getBytes with Double Property
    public void testGetBytesFromDouble() {
        property_c = (SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C);       
        property_c.setType(SDOConstants.SDO_DOUBLE);
        double doubleValue = 2;
        dataObject_c.set(property_c, doubleValue);
        try {
            dataObject_a.getBytes(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //5. purpose: getBytes with float Property
    public void testGetBytesFromFloat() {
        property_c = (SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C);       
        property_c.setType(SDOConstants.SDO_FLOAT);
        float floatValue = 2;
        dataObject_c.set(property_c, floatValue);
        try {
            dataObject_a.getBytes(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //6. purpose: getBytes with int Property
    public void testGetBytesFromInt() {
        property_c = (SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C);       
        property_c.setType(SDOConstants.SDO_INT);
        int intValue = 2;
        dataObject_c.set(property_c, intValue);
        try {
            dataObject_a.getBytes(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //7. purpose: getBytes with long Property
    public void testGetBytesFromLong() {
        property_c = (SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C);       
        property_c.setType(SDOConstants.SDO_LONG);
        long longValue = 2;
        dataObject_c.set(property_c, longValue);
      
        
        try {
            dataObject_a.getBytes(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //8. purpose: getBytes with short Property
    public void testGetBytesFromShort() {
        property_c = (SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C);       
        property_c.setType(SDOConstants.SDO_SHORT);
        
        short shortValue = 2;
        dataObject_c.set(property_c, shortValue);
      
        try {
            dataObject_a.getBytes(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //9. purpose: getBytes with string Property
    public void testGetBytesFromString() {
        property_c = (SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C);       
        property_c.setType(SDOConstants.SDO_STRING);
        String test = "test";
        dataObject_c.set(property_c, test);
           
        try {
            dataObject_a.getBytes(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //10. purpose: getBytes with Defined Bytes Property
    public void testGetBytesConversionFromDefinedBytesProperty() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_BYTES);

        byte[] b = { 12, 13 };

        dataObject_a.setBytes(propertyPath_a_b_c, b);// add it to instance list

        this.assertTrue(Arrays.equals(b, dataObject_a.getBytes(propertyPath_a_b_c)));
    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyBracketPositionalSet() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_BYTES);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        byte[] bb = { 12, 13 };
        List b = new ArrayList();

        //b.add(bb);
        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setBytes(property3, bb);

        this.assertTrue(Arrays.equals(bb, dataObject_a.getBytes(property3)));

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyDotPositionalSet() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_BYTES);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        byte[] bb = { 12, 13 };
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setBytes(property + ".0", bb);

        this.assertTrue(Arrays.equals(bb, dataObject_a.getBytes(property + ".0")));

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyBracketInPathMiddle() {
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_BYTES);

        byte[] bb = { 12, 13 };

        dataObject_a.setBytes(property1, bb);// c dataobject's a property has value boolean 'true'

        this.assertTrue(Arrays.equals(bb, dataObject_a.getBytes(property1)));
    }

    /*   public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyEqualSignBracketInPathDotSet() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTES);
        property_c.setMany(true);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        byte[] bb = { 12, 13 };
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setBytes(property2+"[number=1]", bb);

        this.assertTrue(Arrays.equals(bb, dataObject_a.getBytes(property2+"[number=1]")));

    }*/

    //12. purpose: getBytes with decimal property
    public void testGetBytesFromDecimal() {
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DECIMAL);
        dataObject_c.set(property_c, new BigDecimal(2));
      
        try {
            dataObject_a.getBytes(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    /*    //13. purpose: getBytes with Defined BigInteger Property   !!   OX PRO     !!
    public void testGetBytesConversionFromDefinedIntegerProperty() {
        // dataObject's type add boolean property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTES);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        BigInteger bin = new BigInteger("12");
        byte[] b = bin.toByteArray();

        dataObject_a.setBigInteger(propertyPath_a_b_c, bin);// add it to instance list
        byte[] b1 = dataObject_a.getBytes(propertyPath_a_b_c);
        this.assertTrue(Arrays.equals(b, b1));
    }
    */

    //22. purpose: getBytes with date property
    public void testGetBytesFromDate() {
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DATE);
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
