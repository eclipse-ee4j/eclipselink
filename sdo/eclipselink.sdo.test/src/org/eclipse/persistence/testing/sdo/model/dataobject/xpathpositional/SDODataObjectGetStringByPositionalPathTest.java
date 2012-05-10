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
package org.eclipse.persistence.testing.sdo.model.dataobject.xpathpositional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;

public class SDODataObjectGetStringByPositionalPathTest extends SDODataObjectGetByPositionalPathTestCases {
    public SDODataObjectGetStringByPositionalPathTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.xpathpositional.SDODataObjectGetStringByPositionalPathTest" };
        TestRunner.main(arguments);
    }


    //1. purpose: getString with Defined boolean Property
    public void testGetStringConversionFromDefinedBoolean() {
        // dataObject's type add int property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BOOLEAN);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        boolean str = true;
        Boolean B_STR = new Boolean(str);
        dataObject_a.setBoolean(propertyPath_a_b_c, str);// add it to instance list

        this.assertEquals(B_STR.toString(), dataObject_a.getString(propertyPath_a_b_c));
    }

    //2. purpose: getString with Undefined boolean Property
    public void testGetStringConversionFromUnDefinedBooleanProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BOOLEAN);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getString(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());                        
        }
    }

    //3. purpose: getString with Defined Byte Property
    public void testGetStringConversionFromDefinedByteProperty() {
        // dataObject's type add boolean property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        byte by = 12;
        String by_ = String.valueOf(by);

        dataObject_a.setByte(propertyPath_a_b_c, by);// add it to instance list

        this.assertEquals(by_, dataObject_a.getString(propertyPath_a_b_c));
    }

    //3. purpose: getString with Undefined Byte Property
    public void testGetStringConversionFromUnDefinedByteProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BOOLEAN);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getString(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());                        
        }
    }

    //4. purpose: getString with Defined character Property
    public void testGetStringConversionFromDefinedCharacterProperty() {
        // dataObject's type add int property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_CHARACTER);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        char str = 'c';
        String str_ = String.valueOf(str);
        dataObject_a.setChar(propertyPath_a_b_c, str);// add it to instance list

        this.assertEquals(str_, dataObject_a.getString(propertyPath_a_b_c));
    }

    //5. purpose: getString with Undefined boolean Property
    public void testGetCStringConversionFromUnDefinedCharacterProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_CHARACTER);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getString(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());                        
        }
    }

    //6. purpose: getString with Defined Double Property
    public void testGetStringConversionFromDefinedDoubleProperty() {
        // dataObject's type add boolean property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DOUBLE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        double db = 12;
        String db_ = "12.0";// String.valueOf(12);
        dataObject_a.setDouble(propertyPath_a_b_c, db);// add it to instance list

        this.assertEquals(db_, dataObject_a.getString(propertyPath_a_b_c));
    }

    //7. purpose: getString with Undefined Double Property
    public void testGetStringConversionFromUnDefinedDoubleProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DOUBLE);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getString(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());                        
        }
    }

    //8. purpose: getString with Defined float Property
    public void testGetIntConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_FLOAT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        float fl = 12;
        dataObject_a.setFloat(propertyPath_a_b_c, fl);// add it to instance list
        String fl_ = String.valueOf(fl);

        this.assertEquals(fl_, dataObject_a.getString(propertyPath_a_b_c));
    }

    //9. purpose: getString with Undefined float Property
    public void testGetStringConversionFromUnDefinedFloatProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_FLOAT);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getString(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());                        
        }
    }

    //10. purpose: getString with Defined int Property
    public void testGetStringConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        int in = 12;
        dataObject_a.setInt(propertyPath_a_b_c, in);// add it to instance list
        String in_ = String.valueOf(in);

        this.assertEquals(in_, dataObject_a.getString(propertyPath_a_b_c));
    }

    //11. purpose: getString with Undefined int Property
    public void testGetStringConversionFromUnDefinedIntProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INT);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getString(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());                        
        }
    }

    //12. purpose: getString with Defined long Property
    public void testGetStringConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_LONG);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        long lg = 12;
        dataObject_a.setLong(propertyPath_a_b_c, lg);// add it to instance list
        String lg_ = String.valueOf(lg);

        this.assertEquals(lg_, dataObject_a.getString(propertyPath_a_b_c));
    }

    //13. purpose: getString with Undefined long Property
    public void testGetStringConversionFromUnDefinedLongProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_LONG);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getString(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());                        
        }
    }

    //14. purpose: getString with Defined short Property
    public void testGetStringConversionFromDefinedShortProperty() {
        // dataObject's type add short property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_SHORT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        short shr = 12;
        dataObject_a.setShort(propertyPath_a_b_c, shr);// add it to instance list
        String shr_ = String.valueOf(shr);

        this.assertEquals(shr_, dataObject_a.getString(propertyPath_a_b_c));
    }

    //15. purpose: getString with Undefined short Property
    public void testGetStringConversionFromUnDefinedShortProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_SHORT);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getString(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());                        
        }
    }

    //16. purpose: getString with Defined String Property
    public void testGetIntConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        String str = "12";
        dataObject_a.setString(propertyPath_a_b_c, str);// add it to instance list

        this.assertEquals(str, dataObject_a.getString(propertyPath_a_b_c));
    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyBracketPositionalSet() {
        // dataObject's type add boolean property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        property_c.setMany(true);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        String str = "12";
        List b = new ArrayList();

        //b.add(bb);
        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setString(property3, str);

        this.assertEquals(str, dataObject_a.getString(property3));

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyDotPositionalSet() {
        // dataObject's type add boolean property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        property_c.setMany(true);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        String str = "12";
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setString(property + ".0", str);

        this.assertEquals(str, dataObject_a.getString(property + ".0"));

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyBracketInPathMiddle() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        String str = "12";

        dataObject_a.setString(property1, str);// c dataobject's a property has value boolean 'true'

        this.assertEquals(str, dataObject_a.getString(property1));
    }

    /*public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyEqualSignBracketInPathDotSet() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        property_c.setMany(true);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        String str = "12";
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setString(property2+"[number=1]", str);

        this.assertEquals(str, dataObject_a.getString(property2+"[number=1]"));

    }*/

    //17. purpose: getString with Undefined string Property
    public void testGetStringConversionFromUnDefinedStringProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getString(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());                        
        }
    }

    //18. purpose: getString with bytes property
    public void testGetStringFromBytes() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTES);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        byte[] bytes = new byte[]{100, 100};
        dataObject_c.set(property_c, bytes);
        try {
            String value = dataObject_a.getString(propertyPath_a_b_c);
            String controlValue = "6464";
            assertEquals(controlValue, value);
            //TODO: conversion not supported by sdo spec but is supported by TopLink
        } catch (ClassCastException e) {
        }
    }

    //19. purpose: getString with Defined Decimal Property
    public void testGetStringConversionFromDefinedDecimalProperty() {
        // dataObject's type add int property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DECIMAL);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        int db = 12;
        BigDecimal bd = new BigDecimal(db);
        dataObject_a.setBigDecimal(propertyPath_a_b_c, bd);// add it to instance list

        this.assertEquals(bd.toString(), dataObject_a.getString(propertyPath_a_b_c));
    }

    //20. purpose: getString with Undefined decimal Property
    public void testGetStringConversionFromUnDefinedDecimalProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DECIMAL);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getString(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());                        
        }
    }

    //21. purpose: getString with Defined integer Property
    public void testGetStringConversionFromDefinedIntegerProperty() {
        // dataObject's type add int property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INTEGER);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        BigInteger bi = new BigInteger("12");
        dataObject_a.setBigInteger(propertyPath_a_b_c, bi);// add it to instance list

        this.assertEquals(bi.toString(), dataObject_a.getString(propertyPath_a_b_c));
    }

    //22. purpose: getString with Undefined Integer Property
    public void testGetStringConversionFromUnDefinedIntegerProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INTEGER);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getString(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());                        
        }
    }

    //23. purpose: getString with Defined date Property
    public void testGetStringConversionFromDefinedDateProperty() {
        // dataObject's type add int property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DATE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        //long d = 120000;
        //Date bi = new Date(d);
        //dataObject.setDate(property, bi);// add it to instance list
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2001);
        controlCalendar.set(Calendar.MONTH, 0);
        controlCalendar.set(Calendar.DATE, 1);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date bi = controlCalendar.getTime();

        //dataObject.setString(property, "2001-01-01");// add it to instance list
        dataObject_a.setDate(propertyPath_a_b_c, bi);// add it to instance list

        this.assertEquals("2001-01-01T00:00:00.0Z", dataObject_a.getString(propertyPath_a_b_c));
    }

    //23. purpose: getString with Undefined date Property
    public void testGetStringConversionFromUnDefinedDateProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DATE);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getString(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());                        
        }
    }

    //purpose: getStringt with nul value
    public void testGetStringWithNullArgument() {
        try {
            String p = null;
            dataObject_a.getString(p);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());                        
        }
    }
}
