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

public class SDODataObjectGetFloatByPositionalPathTest extends SDODataObjectGetByPositionalPathTestCases {
    public SDODataObjectGetFloatByPositionalPathTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetFloatByPositionalPathTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getFloat with boolean property
    public void testGetFloatFromBoolean() {
        SDOProperty prop= ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_BOOLEAN);
        dataObject_c.set(prop, true);
        try {
            float value = dataObject_a.getFloat(propertyPath_a_b_c);
            assertEquals(0.0f, value);
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getFloat with Defined Byte Property
    public void testGetFloatConversionFromDefinedByteProperty() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_BYTE);

        byte by = 12;
        float delta = 0;

        dataObject_a.setByte(propertyPath_a_b_c, by);// add it to instance list

        this.assertEquals((float)by, dataObject_a.getFloat(propertyPath_a_b_c), delta);
    }

    //3. purpose: getFloat with Undefined Byte Property
    public void testGetFloatConversionFromUnDefinedProperty() {
        
        try {
            dataObject_a.getFloat(UNDEFINED_PATH);
        } catch (ClassCastException e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //4. purpose: getFloat with character property
    public void testGetFloatFromCharacter() {
        SDOProperty prop = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_CHARACTER);
        dataObject_c.set(prop, 'e');
        try {
            dataObject_a.getFloat(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //5. purpose: getFloat with Defined Double Property
    public void testGetFloatConversionFromDefinedDoubleProperty() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DOUBLE);

        double db = 12;
        float delta = 0;
        dataObject_a.setDouble(propertyPath_a_b_c, db);// add it to instance list

        this.assertEquals((float)db, dataObject_a.getFloat(propertyPath_a_b_c), delta);
    }

    
    //7. purpose: getFloat with Defined float Property
    public void testGetFloatConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_FLOAT);

        float fl = 12;
        float delta = 0;
        dataObject_a.setFloat(propertyPath_a_b_c, fl);// add it to instance list

        this.assertEquals((float)fl, dataObject_a.getFloat(propertyPath_a_b_c), delta);
    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyBracketPositionalSet() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_FLOAT);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        float fl = 12;
        float delta = 0;
        List b = new ArrayList();

        //b.add(bb);
        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setFloat(property3, fl);

        this.assertEquals(fl, dataObject_a.getFloat(property3), delta);

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyDotPositionalSet() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_FLOAT);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        float fl = 12;
        float delta = 0;
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setFloat(property + ".0", fl);

        this.assertEquals(fl, dataObject_a.getFloat(property + ".0"), delta);

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyBracketInPathMiddle() {
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_FLOAT);
        

        float fl = 12;
        float delta = 0;

        dataObject_a.setFloat(property1, fl);// c dataobject's a property has value boolean 'true'

        this.assertEquals(fl, dataObject_a.getFloat(property1), delta);
    }

    /* public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyEqualSignBracketInPathDotSet() {
        property_c = new SDOProperty();
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_FLOAT);
        property_c.setMany(true);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        float fl = 12;
        float delta = 0;
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setFloat(property2+"[number=1]", fl);

        this.assertEquals(fl, dataObject_a.getFloat(property2+"[number=1]"), delta);

    }*/

    
    //9. purpose: getFloat with Defined int Property
    public void testGetFloatConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_INT);

        int in = 12;
        float delta = 0;
        dataObject_a.setInt(propertyPath_a_b_c, in);// add it to instance list

        this.assertEquals((float)in, dataObject_a.getFloat(propertyPath_a_b_c), delta);
    }

  
    //11. purpose: getFloat with Defined long Property
    public void testGetFloatConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_LONG);

        long lg = 12;
        float delta = 0;
        dataObject_a.setLong(propertyPath_a_b_c, lg);// add it to instance list

        this.assertEquals((float)lg, dataObject_a.getFloat(propertyPath_a_b_c), delta);
    }

  
    //13. purpose: getFloat with Defined short Property
    public void testGetFloatConversionFromDefinedShortProperty() {
        // dataObject's type add short property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_SHORT);

        short shr = 12;
        float delta = 0;
        dataObject_a.setShort(propertyPath_a_b_c, shr);// add it to instance list

        this.assertEquals((float)shr, dataObject_a.getFloat(propertyPath_a_b_c), delta);
    }

   

    //15. purpose: getFloat with Defined String Property
    public void testGetFloatConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_STRING);

        String str = "12";
        Float s_d = new Float(str);
        float delta = 0;
        dataObject_a.setString(propertyPath_a_b_c, str);// add it to instance list

        this.assertEquals(s_d.doubleValue(), dataObject_a.getFloat(propertyPath_a_b_c), delta);
    }

    

    //17. purpose: getFloat with bytes property
    public void testGetFloatFromBytes() {
        SDOProperty prop = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_BYTES);
        dataObject_c.set(prop, new String("eee").getBytes());

        try {
            dataObject_a.getFloat(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //18. purpose: getFloat with Defined Decimal Property
    public void testGetFloatConversionFromDefinedDecimalProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DECIMAL);

        float db = 12;
        BigDecimal bd = new BigDecimal(db);
        float delta = 0;
        dataObject_a.setBigDecimal(propertyPath_a_b_c, bd);// add it to instance list

        this.assertEquals(bd.floatValue(), dataObject_a.getFloat(propertyPath_a_b_c), delta);
    }

 
    //20. purpose: getFloat with Defined integer Property
    public void testGetFloatConversionFromDefinedIntegerProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_INTEGER);

        BigInteger bi = new BigInteger("12");
        float delta = 0;
        dataObject_a.setBigInteger(propertyPath_a_b_c, bi);// add it to instance list

        this.assertEquals(bi.floatValue(), dataObject_a.getFloat(propertyPath_a_b_c), delta);
    }

   

    //22. purpose: getFloat with date property
    public void testGetFloatFromDate() {        
        SDOProperty prop = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_DATE);
        dataObject_c.set(prop, Calendar.getInstance().getTime());
        try {
            dataObject_a.getFloat(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //purpose: getFloat with nul value
    public void testGetFloatWithNullArgument() {
        try {
            String p = null;
            dataObject_a.getFloat(p);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }
}
