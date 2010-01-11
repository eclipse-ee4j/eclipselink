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
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import junit.textui.TestRunner;

public class SDODataObjectGetBooleanConversionTest extends SDODataObjectConversionTestCases {
    public SDODataObjectGetBooleanConversionTest(String name) {
        super(name);
    }
    
      public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetBooleanConversionTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionFromDefinedBooleanProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_BOOLEAN);                      
      
        boolean b = true;

        dataObject.setBoolean(property, b);// add it to instance list

        assertEquals(b, dataObject.getBoolean(property));
    }

    //2. purpose: getBoolean with Undefined Boolean Property
    public void testGetBooleanConversionFromUnDefinedBooleanProperty() {
        SDOProperty property = new SDOProperty(aHelperContext); 
          property.setName(PROPERTY_NAME);    
        property.setType(SDOConstants.SDO_BOOLEAN);    

        try {
            dataObject.getBoolean(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //3. purpose: getBoolean with Byte property
    public void testGetBooleanFromByte() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTE);
        byte theValue = 0;
        dataObject.set(property, theValue);
        try {            
            boolean value = dataObject.getBoolean(property);
            boolean controlValue = false;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //4. purpose: getBoolean with character property
    public void testGetBooleanFromCharacter() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_CHARACTER);    
        
        Character b = new Character('1');

        dataObject.setChar(property, b.charValue());// add it to instance list

        assertEquals(true, dataObject.getBoolean(property));

        /*SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_CHARACTER);
        type.addDeclaredProperty(property);
        try {
            dataObject.getBoolean(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }*/
    }

    //5. purpose: getBoolean with Double Property
    public void testGetBooleanFromDouble() {
        SDOProperty property = ((SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DOUBLE);    

        Double b = new Double(0);

        dataObject.setDouble(property, b.doubleValue());// add it to instance list

        assertEquals(false, dataObject.getBoolean(property));

        /*SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DOUBLE);
        type.addDeclaredProperty(property);
        try {
            dataObject.getBoolean(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }*/
    }

    //6. purpose: getBoolean with float Property
    public void testGetBooleanFromFloat() {
        SDOProperty property = ((SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME));        
        property.setType(SDOConstants.SDO_FLOAT);        

        Float b = new Float(1.20);

        dataObject.setFloat(property, b.floatValue());// add it to instance list

        assertEquals(true, dataObject.getBoolean(property));

        /*SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_FLOAT);
        type.addDeclaredProperty(property);
        try {
            dataObject.getBoolean(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }*/
    }

    //7. purpose: getBooleab with int Property
    public void testGetBooleanFromInt() {
        SDOProperty property = ((SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME));                
        property.setType(SDOConstants.SDO_INT);
        
        Integer b = new Integer(0);

        dataObject.setInt(property, b.intValue());// add it to instance list

        assertEquals(false, dataObject.getBoolean(property));

        /*SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_FLOAT);
        type.addDeclaredProperty(property);
        try {
            dataObject.getBoolean(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }*/
    }

    //8. purpose: getBoolea with long Property
    public void testGetBooleanFromLong() {
        SDOProperty property = ((SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME));                
        property.setType(SDOConstants.SDO_LONG);
        
        Double b = new Double(0);

        dataObject.setDouble(property, b.doubleValue());// add it to instance list

        assertEquals(false, dataObject.getBoolean(property));

        /*SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_LONG);
        type.addDeclaredProperty(property);
        try {
            dataObject.getBoolean(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }*/
    }

    //9. purpose: getBytes with short Property
    public void testGetBooleanFromShort() {
        SDOProperty property = ((SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME));                
        property.setType(SDOConstants.SDO_SHORT);
        
        short s = 12;
        Short b = new Short(s);

        dataObject.setShort(property, b.shortValue());// add it to instance list

        assertEquals(true, dataObject.getBoolean(property));

        /*SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_SHORT);
        type.addDeclaredProperty(property);
        try {
            dataObject.getBoolean(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }*/
    }

    //10. purpose: getDouble with Defined String Property
    public void testGetBooleanConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        SDOProperty property = ((SDOProperty)type.getProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_STRING);

        String str = "true";
        Boolean B_STR = new Boolean(str);
        dataObject.setString(property, str);// add it to instance list

        assertEquals(B_STR.booleanValue(), dataObject.getBoolean(property));
    }

    //11. purpose: getDouble with Undefined string Property
    public void testGetBooleanConversionFromUnDefinedStringProperty() {
        SDOProperty property = new SDOProperty(aHelperContext); 
        property.setName(PROPERTY_NAME);    
        property.setType(SDOConstants.SDO_STRING);

        try {
            dataObject.getBoolean(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //12. purpose: getBoolean with bytes property
    public void testGetBooleanFromBytes() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_BYTES);        
        dataObject.set(property, new byte[]{10, 100});
        try {
            dataObject.getBoolean(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //13. purpose: getBoolean with decimal property
    public void testGetBooleanFromDecimal() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DECIMAL);
        BigDecimal theValue = new BigDecimal(10);
        dataObject.set(property, theValue);
        try {
            boolean value = dataObject.getBoolean(property);
            boolean controlValue = true;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //14. purpose: getBoolean with integer property
    public void testGetBooleanFromInteger() {
      SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_INTEGER);
        
        BigInteger theValue = new BigInteger("0");
        dataObject.set(property, theValue);
        try {            
            boolean value = dataObject.getBoolean(property);
            boolean controlValue = false;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //22. purpose: getBoolean with date property
    public void testGetBooleanFromDate() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DATE);        
        dataObject.set(property, Calendar.getInstance().getTime());
        try {
            dataObject.getBoolean(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //purpose: getDouble with nul value
    public void testGetBooleanWithNullArgument() {
        try {
            Property p = null;
            dataObject.getDouble(p);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionFromDefinedBooleanObjectProperty() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME));                
        property.setType(SDOConstants.SDO_BOOLEANOBJECT);
        
        boolean b = true;

        dataObject.setBoolean(property, b);// add it to instance list

        this.assertEquals(b, dataObject.getBoolean(property));
    }

    public void testGetBooleanFromByteObject() {
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);        
        property.setType(SDOConstants.SDO_BYTEOBJECT);        
        Byte theValue = new Byte("10");
        dataObject.set(property, theValue);
        try {
            boolean value = dataObject.getBoolean(property);
            boolean controlValue = true;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //4. purpose: getBoolean with character property
    public void testGetBooleanFromCharacterObject() {
        // dataObject's type add boolean property        
        SDOProperty property = (SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME);        
        property.setType(SDOConstants.SDO_CHARACTEROBJECT);
                
        Character b = new Character('1');

        dataObject.setChar(property, b.charValue());// add it to instance list

        assertEquals(true, dataObject.getBoolean(property));

        /*SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_CHARACTER);
        type.addDeclaredProperty(property);
        try {
        dataObject.getBoolean(property);
        fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }*/
    }

    //5. purpose: getBoolean with Double Property
    public void testGetBooleanFromDoubleObject() {
        SDOProperty property = ((SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME));        
        property.setType(SDOConstants.SDO_DOUBLEOBJECT);
        
        Double b = new Double(0);

        dataObject.setDouble(property, b.doubleValue());// add it to instance list

        assertEquals(false, dataObject.getBoolean(property));

        /*SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DOUBLE);
        type.addDeclaredProperty(property);
        try {
        dataObject.getBoolean(property);
        fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }*/
    }

    public void testGetBooleanFromFloatObject() {
        SDOProperty property = ((SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME));                
        property.setType(SDOConstants.SDO_FLOATOBJECT);
        
        Float b = new Float(1.20);

        dataObject.setFloat(property, b.floatValue());// add it to instance list

        assertEquals(true, dataObject.getBoolean(property));

        /*SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_FLOAT);
        type.addDeclaredProperty(property);
        try {
        dataObject.getBoolean(property);
        fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }*/
    }

    //7. purpose: getBooleab with int Property
    public void testGetBooleanFromIntObject() {
        SDOProperty property = ((SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME));                
        property.setType(SDOConstants.SDO_INTOBJECT);

        Integer b = new Integer(0);

        dataObject.setInt(property, b.intValue());// add it to instance list

        assertEquals(false, dataObject.getBoolean(property));

        /*SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_FLOAT);
        type.addDeclaredProperty(property);
        try {
        dataObject.getBoolean(property);
        fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }*/
    }

    public void testGetBooleanFromShortObject() {
        SDOProperty property = ((SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME));                
        property.setType(SDOConstants.SDO_SHORTOBJECT);
        
        short s = 12;
        Short b = new Short(s);

        dataObject.setShort(property, b.shortValue());// add it to instance list

        assertEquals(true, dataObject.getBoolean(property));

        /*SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_SHORT);
        type.addDeclaredProperty(property);
        try {
        dataObject.getBoolean(property);
        fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }*/
    }

    //purpose: test throw conversion exception caused by 't'
    public void testGetBooleanFromCharacterT() {
        SDOProperty property = ((SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME));        
        property.setType(SDOConstants.SDO_CHARACTEROBJECT);
        
        Character b = new Character('t');

        dataObject.setChar(property, b.charValue());// add it to instance list

        try {
            //this.assertEquals(true, dataObject_a.getBoolean(property));
            boolean value = dataObject.getBoolean(property);
            boolean controlValue = true;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //purpose: test throw conversion exception caused by 'F'
    public void testGetBooleanFromCharacterF() {
        SDOProperty property = ((SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME));        
        property.setType(SDOConstants.SDO_CHARACTEROBJECT);
        
        Character b = new Character('f');

        dataObject.setChar(property, b.charValue());// add it to instance list

        try {
            //this.assertEquals(true, dataObject_a.getBoolean(property));
            boolean value = dataObject.getBoolean(property);
            boolean controlValue = false;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }
}
