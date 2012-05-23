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

public class SDODataObjectGetDoubleByPositionalPathTest extends SDODataObjectGetByPositionalPathTestCases {
    public SDODataObjectGetDoubleByPositionalPathTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetDoubleByPositionalPathTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getDouble with boolean property
    public void testGetDoubleFromBoolean() {
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_BOOLEAN);
        dataObject_c.set(property, true);

        try {
            dataObject_a.getDouble(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //2. purpose: getDouble with Defined Byte Property
    public void testGetDoubleConversionFromDefinedByteProperty() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_BYTE);
      

        byte by = 12;
        double delta = 0.0;

        dataObject_a.setByte(propertyPath_a_b_c, by);// add it to instance list

        this.assertEquals((double)by, dataObject_a.getDouble(propertyPath_a_b_c), delta);
    }

    //3. purpose: getDouble with Undefined Byte Property
    public void testGetDoubleConversionFromUnDefinedProperty() {        
        try {
            dataObject_a.getDouble(UNDEFINED_PATH);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //4. purpose: getDouble with character property
    public void testGetDoubleFromCharacter() {
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_CHARACTER);
        dataObject_c.set(property, 'e');

        try {
            dataObject_a.getDouble(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //5. purpose: getDouble with Defined Double Property
    public void testGetDoubleConversionFromDefinedDoubleProperty() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DOUBLE);

        double db = 12;
        double delta = 0.0;
        dataObject_a.setDouble(propertyPath_a_b_c, db);// add it to instance list

        this.assertEquals((double)db, dataObject_a.getDouble(propertyPath_a_b_c), delta);
    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyBracketPositionalSet() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DOUBLE);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        double db = 12;
        double delta = 0.0;
        List b = new ArrayList();

        //b.add(bb);
        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setDouble(property3, db);

        this.assertEquals(db, dataObject_a.getDouble(property3), delta);

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyDotPositionalSet() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DOUBLE);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        double db = 12;
        double delta = 0.0;
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setDouble(property + ".0", db);

        this.assertEquals(db, dataObject_a.getDouble(property + ".0"), delta);

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyBracketInPathMiddle() {
((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DOUBLE);
        double db = 12;
        double delta = 0.0;

        dataObject_a.setDouble(property1, db);// c dataobject's a property has value boolean 'true'

        this.assertEquals(db, dataObject_a.getDouble(property1), delta);
    }

    /*public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyEqualSignBracketInPathDotSet() {
        property_c = new SDOProperty();
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DOUBLE);
        property_c.setMany(true);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        double db = 12;
        double delta = 0.0;
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setDouble(property2+"[number=1]", db);

        this.assertEquals(db, dataObject_a.getDouble(property2+"[number=1]"), delta);

    } */


    //7. purpose: getDouble with Defined float Property
    public void testGetDoubleConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_FLOAT);

        float fl = 12;
        double delta = 0.0;
        dataObject_a.setFloat(propertyPath_a_b_c, fl);// add it to instance list

        this.assertEquals((double)fl, dataObject_a.getDouble(propertyPath_a_b_c), delta);
    }


    //9. purpose: getDouble with Defined int Property
    public void testGetDoubleConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_INT);

        int in = 12;
        double delta = 0.0;
        dataObject_a.setInt(propertyPath_a_b_c, in);// add it to instance list

        this.assertEquals((double)in, dataObject_a.getDouble(propertyPath_a_b_c), delta);
    }


    //11. purpose: getDouble with Defined long Property
    public void testGetDoubleConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_LONG);

        long lg = 12;
        double delta = 0.0;
        dataObject_a.setLong(propertyPath_a_b_c, lg);// add it to instance list

        this.assertEquals((double)lg, dataObject_a.getDouble(propertyPath_a_b_c), delta);
    }

   
    //13. purpose: getDouble with Defined short Property
    public void testGetDoubleConversionFromDefinedShortProperty() {
        // dataObject's type add short property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_SHORT);

        short shr = 12;
        double delta = 0.0;
        dataObject_a.setShort(propertyPath_a_b_c, shr);// add it to instance list

        this.assertEquals((double)shr, dataObject_a.getDouble(propertyPath_a_b_c), delta);
    }



    //15. purpose: getDouble with Defined String Property
    public void testGetDoubleConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_STRING);
        String str = "12";
        Double s_d = new Double(str);
        double delta = 0.0;
        dataObject_a.setString(propertyPath_a_b_c, str);// add it to instance list

        this.assertEquals(s_d.doubleValue(), dataObject_a.getDouble(propertyPath_a_b_c), delta);
    }
   

    //17. purpose: getDouble with bytes property
    public void testGetDoubleFromBytes() {
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_BYTES);
        dataObject_c.set(property, new byte[]{10, 100});

        try {
            dataObject_a.getDouble(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //18. purpose: getDouble with Defined Decimal Property
    public void testGetDoubleConversionFromDefinedDecimalProperty() {
        // dataObject's type add int property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DECIMAL);

        double db = 12;
        BigDecimal bd = new BigDecimal(db);
        double delta = 0.0;
        dataObject_a.setBigDecimal(propertyPath_a_b_c, bd);// add it to instance list

        this.assertEquals(bd.doubleValue(), dataObject_a.getDouble(propertyPath_a_b_c), delta);
    }

   
    //20. purpose: getDouble with Defined integer Property
    public void testGetDoubleConversionFromDefinedIntegerProperty() {
        // dataObject's type add int property
        
        // !!    OX PRO BIGINTEGER TO DOUBLE !!
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_INTEGER);

        BigInteger bi = new BigInteger("12");
        double delta = 0.0;
        dataObject_a.setBigInteger(propertyPath_a_b_c, bi);// add it to instance list

        this.assertEquals(bi.doubleValue(), dataObject_a.getDouble(propertyPath_a_b_c), delta);
    }

    //22. purpose: getDouble with date property
    public void testGetDoubleFromDate() {
        SDOProperty property = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        property.setType(SDOConstants.SDO_DATE);
        dataObject_c.set(property, Calendar.getInstance().getTime());

        try {
            dataObject_a.getDouble(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //purpose: getDouble with nul value
    public void testGetDoubleWithNullArgument() {
        try {
            String p = null;
            dataObject_a.getDouble(p);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }
}
