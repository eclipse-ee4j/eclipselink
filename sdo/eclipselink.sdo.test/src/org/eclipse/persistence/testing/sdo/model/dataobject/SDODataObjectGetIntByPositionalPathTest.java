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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;

public class SDODataObjectGetIntByPositionalPathTest extends SDODataObjectGetByPositionalPathTestCases {
    public SDODataObjectGetIntByPositionalPathTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetIntByPositionalPathTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getInt with boolean property
    public void testGetIntFromBoolean() {
        SDOProperty prop = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_BOOLEAN);
        dataObject_c.set(prop, true);
        try {
            int value = dataObject_a.getInt(propertyPath_a_b_c);
            assertEquals(1, value);
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getInt with Defined Byte Property
    public void testGetIntConversionFromDefinedByteProperty() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_BYTE);

        byte by = 12;

        dataObject_a.setByte(propertyPath_a_b_c, by);// add it to instance list

        this.assertEquals((int)by, dataObject_a.getInt(propertyPath_a_b_c));
    }

    //3. purpose: getInt with Undefined Byte Property
    public void testGetIntConversionFromUnDefinedProperty() {
        try {
            dataObject_a.getInt(UNDEFINED_PATH);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //4. purpose: getInt with character property
    public void testGetIntFromCharacter() {
       
        SDOProperty prop = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_CHARACTER);
        dataObject_c.set(prop, 'e');       
        try {
            dataObject_a.getInt(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //5. purpose: getInt with Defined Double Property
    public void testGetIntConversionFromDefinedDoubleProperty() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DOUBLE);

        double db = 12;
        dataObject_a.setDouble(propertyPath_a_b_c, db);// add it to instance list

        this.assertEquals((int)db, dataObject_a.getInt(propertyPath_a_b_c));
    }

  
    //7. purpose: getInt with Defined float Property
    public void testGetIntConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_FLOAT);

        float fl = 12;
        dataObject_a.setFloat(propertyPath_a_b_c, fl);// add it to instance list

        this.assertEquals((int)fl, dataObject_a.getInt(propertyPath_a_b_c));
    }


    //9. purpose: getInt with Defined int Property
    public void testGetIntConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_INT);

        int in = 12;
        dataObject_a.setInt(propertyPath_a_b_c, in);// add it to instance list

        this.assertEquals((int)in, dataObject_a.getInt(propertyPath_a_b_c));
    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyBracketPositionalSet() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_INT);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        int in = 12;
        List b = new ArrayList();

        //b.add(bb);
        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setInt(property3, in);

        this.assertEquals(in, dataObject_a.getInt(property3));
    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyDotPositionalSet() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_INT);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        int in = 12;
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setInt(property + ".0", in);

        this.assertEquals(in, dataObject_a.getInt(property + ".0"));
    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyBracketInPathMiddle() {
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_INT);

        int in = 12;

        dataObject_a.setInt(property1, in);// c dataobject's a property has value boolean 'true'

        this.assertEquals(in, dataObject_a.getInt(property1));
    }

    /* public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyEqualSignBracketInPathDotSet() {
        property_c = new SDOProperty();
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INT);
        property_c.setMany(true);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        int in = 12;
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setInt(property2+"[number=1]", in);

        this.assertEquals(in, dataObject_a.getInt(property2+"[number=1]"));

    }*/

    
    //11. purpose: getInt with Defined long Property
    public void testGetIntConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_LONG);

        long lg = 12;
        dataObject_a.setLong(propertyPath_a_b_c, lg);// add it to instance list

        this.assertEquals((int)lg, dataObject_a.getInt(propertyPath_a_b_c));
    }

   
    //13. purpose: getInt with Defined short Property
    public void testGetIntConversionFromDefinedShortProperty() {
        // dataObject's type add short property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_SHORT);

        short shr = 12;
        dataObject_a.setShort(propertyPath_a_b_c, shr);// add it to instance list

        this.assertEquals((int)shr, dataObject_a.getInt(propertyPath_a_b_c));
    }

   
    //15. purpose: getInt with Defined String Property
    public void testGetIntConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_STRING);

        String str = "12";
        Integer s_d = new Integer(str);
        dataObject_a.setString(propertyPath_a_b_c, str);// add it to instance list

        this.assertEquals(s_d.intValue(), dataObject_a.getInt(propertyPath_a_b_c));
    }

   

    //17. purpose: getInt with bytes property
    public void testGetIntFromBytes() {
        SDOProperty prop =((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_BYTES);
        dataObject_c.set(prop, new String("eee").getBytes());
        try {
            dataObject_a.getInt(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //18. purpose: getInt with Defined Decimal Property
    public void testGetIntConversionFromDefinedDecimalProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DECIMAL);

        int db = 12;
        BigDecimal bd = new BigDecimal(db);
        dataObject_a.setBigDecimal(propertyPath_a_b_c, bd);// add it to instance list

        this.assertEquals(bd.intValue(), dataObject_a.getInt(property));
    }


    //20. purpose: getInt with Defined integer Property
    public void testGetIntConversionFromDefinedIntegerProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_INTEGER);

        BigInteger bi = new BigInteger("12");
        dataObject_a.setBigInteger(propertyPath_a_b_c, bi);// add it to instance list

        this.assertEquals(bi.intValue(), dataObject_a.getInt(propertyPath_a_b_c));
    }

  
    //22. purpose: getInt with date property
    public void testGetIntFromDate() {
        SDOProperty prop =((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_DATE);
        dataObject_c.set(prop, Calendar.getInstance().getTime());
        try {
            dataObject_a.getInt(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //purpose: getInt with null value
    public void testGetIntWithNullArgument() {
        try {
            String p = null;
            dataObject_a.getInt(p);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }
}
