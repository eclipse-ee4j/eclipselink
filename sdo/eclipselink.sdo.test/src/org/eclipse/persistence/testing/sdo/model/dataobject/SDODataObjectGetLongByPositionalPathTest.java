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
import java.util.Date;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;

public class SDODataObjectGetLongByPositionalPathTest extends SDODataObjectGetByPositionalPathTestCases {
    public SDODataObjectGetLongByPositionalPathTest(String name) {
        super(name);
    }

   public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetLongByPositionalPathTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getLong with boolean property
    public void testGetLongFromBoolean() {        
        SDOProperty prop = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_BOOLEAN);
        dataObject_c.set(prop, true);

        try {
            long value = dataObject_a.getLong(propertyPath_a_b_c);
            assertEquals(1, value);
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getLong with Defined Byte Property
    public void testGetLongConversionFromDefinedByteProperty() {
        // dataObject's type add boolean property
               ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_BYTE);

        byte by = 12;

        dataObject_a.setByte(propertyPath_a_b_c, by);// add it to instance list

        this.assertEquals((long)by, dataObject_a.getLong(propertyPath_a_b_c));
    }

  
    //4. purpose: getLong with character property
    public void testGetLongFromCharacter() {
  
        SDOProperty prop =((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_CHARACTER);
        dataObject_c.set(prop, 't');
        
        try {
            dataObject_a.getLong(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //5. purpose: getLong with Defined Double Property
    public void testGetLongConversionFromDefinedDoubleProperty() {
        // dataObject's type add boolean property        
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DOUBLE);

        double db = 12;
        dataObject_a.setDouble(propertyPath_a_b_c, db);// add it to instance list

        this.assertEquals((long)db, dataObject_a.getLong(propertyPath_a_b_c));
    }


    //7. purpose: getLong with Defined float Property
    public void testGetLongConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
      
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_FLOAT);

        float fl = 12;
        dataObject_a.setFloat(propertyPath_a_b_c, fl);// add it to instance list

        this.assertEquals((long)fl, dataObject_a.getLong(propertyPath_a_b_c));
    }


    //9. purpose: getLong with Defined int Property
    public void testGetLongConversionFromDefinedIntProperty() {
        // dataObject's type add int property
                ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_INT);

        int in = 12;
        dataObject_a.setInt(propertyPath_a_b_c, in);// add it to instance list

        this.assertEquals((long)in, dataObject_a.getLong(property));
    }

    //11. purpose: getLong with Defined long Property
    public void testGetLongConversionFromDefinedLongProperty() {
        // dataObject's type add short property
                ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_LONG);

        long lg = 12;
        dataObject_a.setLong(propertyPath_a_b_c, lg);// add it to instance list

        this.assertEquals(lg, dataObject_a.getLong(propertyPath_a_b_c));
    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyBracketPositionalSet() {
        // dataObject's type add boolean property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_LONG);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);

        long lg = 12;
        List b = new ArrayList();

        //b.add(bb);
        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setLong(property3, lg);
        long value = dataObject_a.getLong(property3);
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(false);
        this.assertEquals(lg, value);

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyDotPositionalSet() {
        // dataObject's type add boolean property
       ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_LONG);
       ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(true);
        long lg = 12;
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setLong(property + ".0", lg);
        long value = dataObject_a.getLong(property + ".0");
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setMany(false);

        this.assertEquals(lg,value );

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyBracketInPathMiddle() {
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_LONG);

        long lg = 12;

        dataObject_a.setLong(property1, lg);// c dataobject's a property has value boolean 'true'

        this.assertEquals(lg, dataObject_a.getLong(property1));
    }

    /*public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyEqualSignBracketInPathDotSet() {
        property_c = new SDOProperty();
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_LONG);
        property_c.setMany(true);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        long lg = 12;
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setLong(property2+"[number=1]", lg);

        this.assertEquals(lg, dataObject_a.getLong(property2+"[number=1]"));

    }*/

    //13. purpose: getLong with Defined short Property
    public void testGetLongConversionFromDefinedShortProperty() {
        // dataObject's type add short property
        ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_SHORT);

        short shr = 12;
        dataObject_a.setShort(propertyPath_a_b_c, shr);// add it to instance list

        this.assertEquals((long)shr, dataObject_a.getLong(propertyPath_a_b_c));
    }

    //15. purpose: getLong with Defined String Property
    public void testGetLongConversionFromDefinedStringProperty() {
        // dataObject's type add int property
                ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_STRING);

        String str = "12";
        Long s_d = new Long(str);
        dataObject_a.setString(propertyPath_a_b_c, str);// add it to instance list

        this.assertEquals(s_d.longValue(), dataObject_a.getLong(propertyPath_a_b_c));
    }

    //17. purpose: getLong with bytes property
    public void testGetLongFromBytes() {
        SDOProperty prop = ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C));
        prop.setType(SDOConstants.SDO_BYTES);
        dataObject_c.set(prop, new String("eee").getBytes());
        try {
            dataObject_a.getLong(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //18. purpose: getLong with Defined Decimal Property
    public void testGetLongConversionFromDefinedDecimalProperty() {
        // dataObject's type add int property
                ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DECIMAL);

        long db = 12;
        BigDecimal bd = new BigDecimal(db);
        dataObject_a.setBigDecimal(propertyPath_a_b_c, bd);// add it to instance list

        this.assertEquals(bd.longValue(), dataObject_a.getLong(propertyPath_a_b_c));
    }



    //20. purpose: getLong with Defined integer Property
    public void testGetLongConversionFromDefinedIntegerProperty() {
        // dataObject's type add int property
                ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_INTEGER);

        BigInteger bi = new BigInteger("12");
        dataObject_a.setBigInteger(propertyPath_a_b_c, bi);// add it to instance list

        this.assertEquals(bi.longValue(), dataObject_a.getLong(propertyPath_a_b_c));
    }

     //22. purpose: getLong with Defined date Property
    public void testGetLongConversionFromDefinedDateProperty() {
        // dataObject's type add int property
                ((SDOProperty)dataObject_c.getInstanceProperty(PROPERTY_NAME_C)).setType(SDOConstants.SDO_DATE);

        long d = 120000;
        Date bi = new Date(d);
        dataObject_a.setDate(propertyPath_a_b_c, bi);// add it to instance list

        this.assertEquals(bi.getTime(), dataObject_a.getLong(propertyPath_a_b_c));
    }

    //23. purpose: getLong with Undefined date Property
    public void testGetLongConversionFromUnDefinedProperty() {        

        try {
            dataObject_a.getLong(UNDEFINED_PATH);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //purpose: getLong with nul value
    public void testGetIntWithNullArgument() {
        try {
            String p = null;
            dataObject_a.getLong(p);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }
}
