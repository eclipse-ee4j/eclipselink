/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.model.dataobject;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;

public class SDODataObjectGetFloatConversionWithPathTest extends SDODataObjectConversionWithPathTestCases {
    public SDODataObjectGetFloatConversionWithPathTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetFloatConversionWithPathTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getFloat with boolean property
    public void testGetFloatFromBoolean() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BOOLEAN);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        dataObject_c.set(property_c, true);

        try {
            float value = dataObject_a.getFloat(propertyPath_a_b_c);
            assertEquals(0.0f, value);
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getFloat with Defined Byte Property
    public void testGetFloatConversionFromDefinedByteProperty() {
        // dataObject's type add boolean property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        byte by = 12;
        float delta = 0;

        dataObject_a.setByte(propertyPath_a_b_c, by);// add it to instance list

        this.assertEquals((float)by, dataObject_a.getFloat(propertyPath_a_b_c), delta);
    }

    //3. purpose: getFloat with Undefined Byte Property
    public void testGetFloatConversionFromUnDefinedByteProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTE);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getFloat(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //4. purpose: getFloat with character property
    public void testGetFloatFromCharacter() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_CHARACTER);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        dataObject_c.set(property_c, 'e');
        try {
            dataObject_a.getFloat(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //5. purpose: getFloat with Defined Double Property
    public void testGetFloatConversionFromDefinedDoubleProperty() {
        // dataObject's type add boolean property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DOUBLE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        double db = 12;
        float delta = 0;
        dataObject_a.setDouble(propertyPath_a_b_c, db);// add it to instance list

        this.assertEquals((float)db, dataObject_a.getFloat(propertyPath_a_b_c), delta);
    }

    //6. purpose: getFloat with Undefined Double Property
    public void testGetFloatConversionFromUnDefinedDoubleProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DOUBLE);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getFloat(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //7. purpose: getFloat with Defined float Property
    public void testGetFloatConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_FLOAT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        float fl = 12;
        float delta = 0;
        dataObject_a.setFloat(propertyPath_a_b_c, fl);// add it to instance list

        this.assertEquals((float)fl, dataObject_a.getFloat(propertyPath_a_b_c), delta);
    }

    //8. purpose: getFloat with Undefined float Property
    public void testGetFloatConversionFromUnDefinedFloatProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_FLOAT);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getFloat(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //9. purpose: getFloat with Defined int Property
    public void testGetFloatConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        int in = 12;
        float delta = 0;
        dataObject_a.setInt(propertyPath_a_b_c, in);// add it to instance list

        this.assertEquals((float)in, dataObject_a.getFloat(propertyPath_a_b_c), delta);
    }

    //10. purpose: getFloat with Undefined int Property
    public void testGetFloatConversionFromUnDefinedIntProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INT);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getFloat(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //11. purpose: getFloat with Defined long Property
    public void testGetFloatConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_LONG);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        long lg = 12;
        float delta = 0;
        dataObject_a.setLong(propertyPath_a_b_c, lg);// add it to instance list

        this.assertEquals((float)lg, dataObject_a.getFloat(propertyPath_a_b_c), delta);
    }

    //12. purpose: getFloat with Undefined long Property
    public void testGetFloatConversionFromUnDefinedLongProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_LONG);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getFloat(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //13. purpose: getFloat with Defined short Property
    public void testGetFloatConversionFromDefinedShortProperty() {
        // dataObject's type add short property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_SHORT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        short shr = 12;
        float delta = 0;
        dataObject_a.setShort(propertyPath_a_b_c, shr);// add it to instance list

        this.assertEquals((float)shr, dataObject_a.getFloat(propertyPath_a_b_c), delta);
    }

    //14. purpose: getFloat with Undefined short Property
    public void testGetFloatConversionFromUnDefinedShortProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_SHORT);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getFloat(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //15. purpose: getFloat with Defined String Property
    public void testGetFloatConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        String str = "12";
        Float s_d = new Float(str);
        float delta = 0;
        dataObject_a.setString(propertyPath_a_b_c, str);// add it to instance list

        this.assertEquals(s_d.doubleValue(), dataObject_a.getFloat(propertyPath_a_b_c), delta);
    }

    //16. purpose: getFloat with Undefined string Property
    public void testGetFloatConversionFromUnDefinedStringProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getFloat(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //17. purpose: getFloat with bytes property
    public void testGetFloatFromBytes() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTES);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        dataObject_c.set(property_c, new String("eee").getBytes());
        try {
            dataObject_a.getFloat(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //18. purpose: getFloat with Defined Decimal Property
    public void testGetFloatConversionFromDefinedDecimalProperty() {
        // dataObject's type add int property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DECIMAL);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        float db = 12;
        BigDecimal bd = new BigDecimal(db);
        float delta = 0;
        dataObject_a.setBigDecimal(propertyPath_a_b_c, bd);// add it to instance list

        this.assertEquals(bd.floatValue(), dataObject_a.getFloat(propertyPath_a_b_c), delta);
    }

    //19. purpose: getFloat with Undefined decimal Property
    public void testGetFloatConversionFromUnDefinedDecimalProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DECIMAL);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getFloat(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //20. purpose: getFloat with Defined integer Property
    public void testGetFloatConversionFromDefinedIntegerProperty() {
        // dataObject's type add int property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INTEGER);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        BigInteger bi = new BigInteger("12");
        float delta = 0;
        dataObject_a.setBigInteger(propertyPath_a_b_c, bi);// add it to instance list

        this.assertEquals(bi.floatValue(), dataObject_a.getFloat(propertyPath_a_b_c), delta);
    }

    //21. purpose: getFloat with Undefined Integer Property
    public void testGetFloatConversionFromUnDefinedIntegerProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INTEGER);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getFloat(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //22. purpose: getFloat with date property
    public void testGetFloatFromDate() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DATE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        dataObject_c.set(property_c, Calendar.getInstance().getTime());
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

             //22. purpose: getDouble from null
    public void testGetFloatFromNull() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_FLOAT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        dataObject_c.set(property_c, null);

        float value = dataObject_a.getFloat(propertyPath_a_b_c);
        float floatValue = 0;
        assertEquals(floatValue, value);
        DataObject doNext = dataObject_a.getDataObject("PName-a");
        doNext = doNext.getDataObject("PName-b");
        Property prop  = doNext.getInstanceProperty("PName-c");
        float value2 =  doNext.getFloat(prop);
        assertEquals(floatValue, value2);
    }
}
