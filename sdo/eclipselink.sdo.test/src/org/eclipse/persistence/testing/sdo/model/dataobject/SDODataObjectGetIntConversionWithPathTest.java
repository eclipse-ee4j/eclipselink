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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;

public class SDODataObjectGetIntConversionWithPathTest extends SDODataObjectConversionWithPathTestCases {
    public SDODataObjectGetIntConversionWithPathTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetIntConversionWithPathTest" };
        TestRunner.main(arguments);
    }

    //1. purpose: getInt with boolean property
    public void testGetIntFromBoolean() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BOOLEAN);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        dataObject_c.set(property_c, true);

        try {
            int value = dataObject_a.getInt(propertyPath_a_b_c);
            assertEquals(1, value);
        } catch (ClassCastException e) {
        }
    }

    //2. purpose: getInt with Defined Byte Property
    public void testGetIntConversionFromDefinedByteProperty() {
        // dataObject's type add boolean property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        byte by = 12;

        dataObject_a.setByte(propertyPath_a_b_c, by);// add it to instance list

        this.assertEquals((int)by, dataObject_a.getInt(propertyPath_a_b_c));
    }

    //3. purpose: getInt with Undefined Byte Property
    public void testGetIntConversionFromUnDefinedByteProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTE);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getInt(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //4. purpose: getInt with character property
    public void testGetIntFromCharacter() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_CHARACTER);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        dataObject_c.set(property_c, 'e');
        try {
            dataObject_a.getInt(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //5. purpose: getInt with Defined Double Property
    public void testGetIntConversionFromDefinedDoubleProperty() {
        // dataObject's type add boolean property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DOUBLE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        double db = 12;
        dataObject_a.setDouble(propertyPath_a_b_c, db);// add it to instance list

        this.assertEquals((int)db, dataObject_a.getInt(propertyPath_a_b_c));
    }

    //6. purpose: getInt with Undefined Double Property
    public void testGetIntConversionFromUnDefinedDoubleProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DOUBLE);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getInt(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //7. purpose: getInt with Defined float Property
    public void testGetIntConversionFromDefinedFloatProperty() {
        // dataObject's type add float property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_FLOAT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        float fl = 12;
        dataObject_a.setFloat(propertyPath_a_b_c, fl);// add it to instance list

        this.assertEquals((int)fl, dataObject_a.getInt(propertyPath_a_b_c));
    }

    //8. purpose: getInt with Undefined float Property
    public void testGetIntConversionFromUnDefinedFloatProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_FLOAT);
        // type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getInt(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //9. purpose: getInt with Defined int Property
    public void testGetIntConversionFromDefinedIntProperty() {
        // dataObject's type add int property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        int in = 12;
        dataObject_a.setInt(propertyPath_a_b_c, in);// add it to instance list

        this.assertEquals((int)in, dataObject_a.getInt(propertyPath_a_b_c));
    }

    //10. purpose: getInt with Undefined int Property
    public void testGetIntConversionFromUnDefinedIntProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INT);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getInt(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //11. purpose: getInt with Defined long Property
    public void testGetIntConversionFromDefinedLongProperty() {
        // dataObject's type add short property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_LONG);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        long lg = 12;
        dataObject_a.setLong(propertyPath_a_b_c, lg);// add it to instance list

        this.assertEquals((int)lg, dataObject_a.getInt(propertyPath_a_b_c));
    }

    //12. purpose: getInt with Undefined long Property
    public void testGetIntConversionFromUnDefinedLongProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_LONG);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getInt(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //13. purpose: getInt with Defined short Property
    public void testGetIntConversionFromDefinedShortProperty() {
        // dataObject's type add short property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_SHORT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        short shr = 12;
        dataObject_a.setShort(propertyPath_a_b_c, shr);// add it to instance list

        this.assertEquals((int)shr, dataObject_a.getInt(propertyPath_a_b_c));
    }

    //14. purpose: getInt with Undefined short Property
    public void testGetIntConversionFromUnDefinedShortProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_SHORT);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getInt(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //15. purpose: getInt with Defined String Property
    public void testGetIntConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        String str = "12";
        Integer s_d = new Integer(str);
        dataObject_a.setString(propertyPath_a_b_c, str);// add it to instance list

        this.assertEquals(s_d.intValue(), dataObject_a.getInt(propertyPath_a_b_c));
    }

    //16. purpose: getInt with Undefined string Property
    public void testGetIntConversionFromUnDefinedStringProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getInt(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //17. purpose: getInt with bytes property
    public void testGetIntFromBytes() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTES);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        dataObject_c.set(property_c, new String("eee").getBytes());
        try {
            dataObject_a.getInt(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //18. purpose: getInt with Defined Decimal Property
    public void testGetIntConversionFromDefinedDecimalProperty() {
        // dataObject's type add int property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DECIMAL);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        int db = 12;
        BigDecimal bd = new BigDecimal(db);
        dataObject_a.setBigDecimal(propertyPath_a_b_c, bd);// add it to instance list

        this.assertEquals(bd.intValue(), dataObject_a.getInt(property));
    }

    //19. purpose: getInt with Undefined decimal Property
    public void testGetIntConversionFromUnDefinedDecimalProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DECIMAL);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getInt(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //20. purpose: getInt with Defined integer Property
    public void testGetIntConversionFromDefinedIntegerProperty() {
        // dataObject's type add int property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INTEGER);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        BigInteger bi = new BigInteger("12");
        dataObject_a.setBigInteger(propertyPath_a_b_c, bi);// add it to instance list

        this.assertEquals(bi.intValue(), dataObject_a.getInt(propertyPath_a_b_c));
    }

    //21. purpose: getInt with Undefined Integer Property
    public void testGetIntConversionFromUnDefinedIntegerProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INTEGER);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getInt(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //22. purpose: getInt with date property
    public void testGetIntFromDate() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DATE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        dataObject_c.set(property_c, Calendar.getInstance().getTime());
        try {
            dataObject_a.getInt(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //purpose: getInt with nul value
    public void testGetIntWithNullArgument() {
        try {
            String p = null;
            dataObject_a.getInt(p);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }
}
