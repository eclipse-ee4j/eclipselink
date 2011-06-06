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

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;

public class SDODataObjectGetBooleanConversionWithPathTest extends SDODataObjectConversionWithPathTestCases {
    public SDODataObjectGetBooleanConversionWithPathTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetBooleanConversionWithPathTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanProperty() {
        // dataObject's type add boolean property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BOOLEAN);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        boolean c = true;

        dataObject_a.setBoolean(property, c);// c dataobject's a property has value boolean 'true'

        this.assertEquals(c, dataObject_a.getBoolean(property));

    }

    //2. purpose: getBoolean with Undefined Boolean Property
    public void testGetBooleanConversionFromUnDefinedBooleanProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BOOLEAN);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getBoolean(property);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //3. purpose: getBoolean with Byte property
    public void testGetBooleanFromByte() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        byte theByte = 0;
        dataObject_c.set(property_c, theByte);
        try {
            boolean value = dataObject_a.getBoolean(property);
            boolean controlValue = false;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //4. purpose: getBoolean with character property with value 't'
    public void testGetBooleanFromCharacterT() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_CHARACTER);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        char s = 't';

        //Short c = new Short(s);
        dataObject_a.setChar(property, s);// c dataobject's a property has value boolean 'true'

        try {
            //this.assertEquals(true, dataObject_a.getBoolean(property));
            boolean value = dataObject_a.getBoolean(property);
            boolean controlValue = true;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }

        /*property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_CHARACTER);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        try {
            dataObject_a.getBoolean(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }*/
    }

    public void testGetBooleanFromCharacterF() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_CHARACTER);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        char s = 'f';

        //Short c = new Short(s);
        dataObject_a.setChar(property, s);// c dataobject's a property has value boolean 'true'

        try {
            //this.assertEquals(true, dataObject_a.getBoolean(property));
            boolean value = dataObject_a.getBoolean(property);
            boolean controlValue = false;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //5. purpose: getBoolean with Double Property
    public void testGetBooleanFromDouble() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DOUBLE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        double s = 0;

        //Short c = new Short(s);
        dataObject_a.setDouble(property, s);// c dataobject's a property has value boolean 'true'

        this.assertEquals(false, dataObject_a.getBoolean(property));

        /*property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DOUBLE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        try {
            dataObject_a.getBoolean(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }*/
    }

    //6. purpose: getBoolean with float Property
    public void testGetBooleanFromFloat() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_FLOAT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        Float s = new Float(0);

        //Short c = new Short(s);
        dataObject_a.setFloat(property, s.floatValue());// c dataobject's a property has value boolean 'true'

        this.assertEquals(false, dataObject_a.getBoolean(property));

        /*property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_FLOAT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        try {
            dataObject_a.getBoolean(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }*/
    }

    //7. purpose: getBooleab with int Property
    public void testGetBooleanFromInt() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        int s = 1;

        //Short c = new Short(s);
        dataObject_a.setInt(property, s);// c dataobject's a property has value boolean 'true'

        this.assertEquals(true, dataObject_a.getBoolean(property));

        /*property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        try {
            dataObject_a.getBoolean(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }*/
    }

    //8. purpose: getBoolea with long Property
    public void testGetBooleanFromLong() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_LONG);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        long s = 0;

        //Short c = new Short(s);
        dataObject_a.setLong(property, s);// c dataobject's a property has value boolean 'true'

        this.assertEquals(false, dataObject_a.getBoolean(property));

        /*property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_LONG);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        try {
            dataObject_a.getBoolean(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }*/
    }

    //9. purpose: getBytes with short Property
    public void testGetBooleanFromShort() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_SHORT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        short s = 0;

        //Short c = new Short(s);
        dataObject_a.setShort(property, s);// c dataobject's a property has value boolean 'true'

        this.assertEquals(false, dataObject_a.getBoolean(property));

        /*property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_SHORT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        try {
            dataObject_a.getBoolean(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }*/
    }

    //10. purpose: getBoolean with Defined String Property
    public void testGetBooleanConversionFromDefinedStringProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        String str = "true";
        Boolean B_STR = new Boolean(str);
        dataObject_a.setString(property, str);// add it to instance list

        this.assertEquals(B_STR.booleanValue(), dataObject_a.getBoolean("PName-a/PName-b/PName-c"));
    }

    //11. purpose: getDouble with Undefined string Property
    public void testGetBooleanConversionFromUnDefinedStringProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getBoolean(property);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //12. purpose: getBoolean with bytes property
    public void testGetBooleanFromBytes() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTES);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        dataObject_c.set(property_c, new String("eee").getBytes());
        try {
            dataObject_a.getBoolean(property);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //13. purpose: getBoolean with decimal property
    public void testGetBooleanFromDecimal() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DECIMAL);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        dataObject_c.set(property_c, new BigDecimal(3));
        try {
            boolean value =  dataObject_a.getBoolean(property);
            boolean controlValue = true;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //14. purpose: getBoolean with integer property
    public void testGetBooleanFromInteger() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INTEGER);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        dataObject_c.set(property_c, new BigInteger("0"));
        try {
            boolean value = dataObject_a.getBoolean(property);
            boolean controlValue = false;
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //22. purpose: getBoolean with date property
    public void testGetBooleanFromDate() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DATE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        dataObject_c.set(property_c, Calendar.getInstance().getTime());
        try {
            dataObject_a.getBoolean(property);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //purpose: getDouble with nul value
    public void testGetBooleanWithNullArgument() {
        try {
            String path = null;
            dataObject_a.getBoolean(path);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }
    
          //22. purpose: getDouble from null
    public void testGetBooleanFromNull() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BOOLEAN);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        dataObject_c.set(property_c, null);
        
        boolean value = dataObject_a.getBoolean(propertyPath_a_b_c);
        boolean booleanValue = false;
        assertEquals(booleanValue, value);            
        DataObject doNext = dataObject_a.getDataObject("PName-a");
        doNext = doNext.getDataObject("PName-b");        
        Property prop  = doNext.getInstanceProperty("PName-c");
        boolean value2 =  doNext.getBoolean(prop);        
        assertEquals(booleanValue, value2);       
    }
}
