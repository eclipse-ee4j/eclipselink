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
import java.nio.ByteBuffer;
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
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetStringByPositionalPathTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getString with Defined boolean Property
    public void testGetStringConversionFromDefinedBoolean() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_BOOLEAN);

        boolean str = true;
        Boolean B_STR = new Boolean(str);
        dataObject_a.setBoolean(propertyPath_a_b_c, str);// add it to instance list

        this.assertEquals(B_STR.toString(), dataObject_a.getString(propertyPath_a_b_c));
    }

    //2. purpose: getString with Undefined boolean Property
    public void testGetStringConversionFromUnDefinedProperty() {
        
        try {
            dataObject_a.getString(UNDEFINED_PATH);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //3. purpose: getString with Defined Byte Property
    public void testGetStringConversionFromDefinedByteProperty() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_BYTE);

        byte by = 12;
        String by_ = String.valueOf(by);

        dataObject_a.setByte(propertyPath_a_b_c, by);// add it to instance list

        this.assertEquals(by_, dataObject_a.getString(propertyPath_a_b_c));
    }

  

    //4. purpose: getString with Defined character Property
    public void testGetStringConversionFromDefinedCharacterProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_CHARACTER);

        char str = 'c';
        String str_ = String.valueOf(str);
        dataObject_a.setChar(propertyPath_a_b_c, str);// add it to instance list

        this.assertEquals(str_, dataObject_a.getString(propertyPath_a_b_c));
    }

   

    //6. purpose: getString with Defined Double Property
    public void testGetStringConversionFromDefinedDoubleProperty() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DOUBLE);

        double db = 12;
        String db_ = "12.0";// String.valueOf(12);
        dataObject_a.setDouble(propertyPath_a_b_c, db);// add it to instance list

        this.assertEquals(db_, dataObject_a.getString(propertyPath_a_b_c));
    }

    //8. purpose: getString with Defined float Property
    public void testGetIntConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_FLOAT);

        float fl = 12;
        dataObject_a.setFloat(propertyPath_a_b_c, fl);// add it to instance list
        String fl_ = String.valueOf(fl);

        this.assertEquals(fl_, dataObject_a.getString(propertyPath_a_b_c));
    }

   
    //10. purpose: getString with Defined int Property
    public void testGetStringConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_INT);

        int in = 12;
        dataObject_a.setInt(propertyPath_a_b_c, in);// add it to instance list
        String in_ = String.valueOf(in);

        this.assertEquals(in_, dataObject_a.getString(propertyPath_a_b_c));
    }

  
    //12. purpose: getString with Defined long Property
    public void testGetStringConversionFromDefinedLongProperty() {
        // dataObject's type add short property
  ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_LONG);
        long lg = 12;
        dataObject_a.setLong(propertyPath_a_b_c, lg);// add it to instance list
        String lg_ = String.valueOf(lg);

        this.assertEquals(lg_, dataObject_a.getString(propertyPath_a_b_c));
    }

   

    //14. purpose: getString with Defined short Property
    public void testGetStringConversionFromDefinedShortProperty() {
        // dataObject's type add short property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_SHORT);

        short shr = 12;
        dataObject_a.setShort(propertyPath_a_b_c, shr);// add it to instance list
        String shr_ = String.valueOf(shr);

        this.assertEquals(shr_, dataObject_a.getString(propertyPath_a_b_c));
    }

   

    //16. purpose: getString with Defined String Property
    public void testGetIntConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_STRING);

        String str = "12";
        dataObject_a.setString(propertyPath_a_b_c, str);// add it to instance list

        this.assertEquals(str, dataObject_a.getString(propertyPath_a_b_c));
    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyBracketPositionalSet() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_STRING);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

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
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_STRING);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        String str = "12";
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setString(property + ".0", str);

        this.assertEquals(str, dataObject_a.getString(property + ".0"));

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyBracketInPathMiddle() {
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_STRING);
        
        String str = "12";

        dataObject_a.setString(property1, str);// c dataobject's a property has value boolean 'true'

        this.assertEquals(str, dataObject_a.getString(property1));
    }

    /*public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyEqualSignBracketInPathDotSet() {
        property_c = new SDOProperty();
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

   
    //18. purpose: getString with bytes property
    public void testGetStringFromBytes() {
        SDOProperty prop = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_BYTES);        
        
        byte[] bytes = new byte[]{10, 100};
              
        dataObject_c.set(prop, bytes);
        try {
            String value = dataObject_a.getString(propertyPath_a_b_c);
            assertEquals("0A64", value);
        } catch (ClassCastException e) {
        }
    }

    //19. purpose: getString with Defined Decimal Property
    public void testGetStringConversionFromDefinedDecimalProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DECIMAL);

        int db = 12;
        BigDecimal bd = new BigDecimal(db);
        dataObject_a.setBigDecimal(propertyPath_a_b_c, bd);// add it to instance list

        this.assertEquals(bd.toString(), dataObject_a.getString(propertyPath_a_b_c));
    }

  
    //21. purpose: getString with Defined integer Property
    public void testGetStringConversionFromDefinedIntegerProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_INTEGER);

        BigInteger bi = new BigInteger("12");
        dataObject_a.setBigInteger(propertyPath_a_b_c, bi);// add it to instance list

        this.assertEquals(bi.toString(), dataObject_a.getString(propertyPath_a_b_c));
    }

  

    //23. purpose: getString with Defined date Property
    public void testGetStringConversionFromDefinedDateProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DATE);

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
