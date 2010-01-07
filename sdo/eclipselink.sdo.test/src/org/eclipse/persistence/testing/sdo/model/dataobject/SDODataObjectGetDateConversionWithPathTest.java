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

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;

public class SDODataObjectGetDateConversionWithPathTest extends SDODataObjectConversionWithPathTestCases {
    public SDODataObjectGetDateConversionWithPathTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetDateConversionWithPathTest" };
        TestRunner.main(arguments);
    }


    //1. purpose: getDate with Boolean property
    public void testGetDateFromBoolean() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BOOLEAN);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        dataObject_c.set(property_c, true);
        try {
            dataObject_a.getDate(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //2. purpose: getDate with Byte property
    public void testGetDateFromByte() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        dataObject_c.set(property_c, new String("aa").getBytes()[0]);
        try {
            dataObject_a.getDate(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //3. purpose: getDate with character property
    public void testGetDateFromCharacter() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_CHARACTER);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        dataObject_c.set(property_c, 'p');
        try {
            dataObject_a.getDate(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //4. purpose: getDate with Double Property
    public void testGetDateFromDouble() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DOUBLE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        double value = 3;
        dataObject_c.set(property_c, value);
        try {
            dataObject_a.getDate(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //5. purpose: getDate with float Property
    public void testGetDateFromFloat() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_FLOAT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        float value = 3;
        dataObject_c.set(property_c, value);
        try {
            dataObject_a.getDate(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //7. purpose: getDate with int Property
    public void testGetDateFromInt() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        int value = 3;
        dataObject_c.set(property_c, value);    
        try {
            dataObject_a.getDate(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //8. purpose: getDate with Defined long Property
    public void testGetDateConversionFromDefinedLongProperty() {
        // dataObject's type add boolean property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_LONG);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        long l = 12;
        Date d = new Date(l);

        dataObject_c.setLong(property_c, l);// add it to instance list

        this.assertEquals(d, dataObject_a.getDate(propertyPath_a_b_c));
    }

    //9. purpose: getDate with Undefined Boolean Property
    public void testGetDateConversionFromUnDefinedLongProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_LONG);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getDate(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //10. purpose: getDate with short Property
    public void testGetDateFromShort() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_SHORT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        short value = 3;
        dataObject_c.set(property_c, value);
        try {
            dataObject_a.getDate(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //11. purpose: getDate with Defined String Property
    public void testGetDateConversionFromDefinedStringProperty() {
        // dataObject's type add int property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2001);
        controlCalendar.set(Calendar.MONTH, 0);
        controlCalendar.set(Calendar.DATE, 1);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date d = controlCalendar.getTime();

        dataObject_a.setString(propertyPath_a_b_c, "2001-01-01");// add it to instance list

        this.assertEquals(d, dataObject_a.getDate(propertyPath_a_b_c));
    }

    //12. purpose: getDate with Undefined string Property
    public void testGetDateConversionFromUnDefinedStringProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getDate(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //13. purpose: getDate with bytes property
    public void testGetDateFromBytes() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BYTES);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        dataObject_c.set(property_c, new String("eee").getBytes());
        try {
            dataObject_a.getDate(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //14. purpose: getBoolean with decimal property
    public void testGetDateFromDecimal() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DECIMAL);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        dataObject_c.set(property_c, new BigDecimal(3));
        try {
            dataObject_a.getDate(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //15. purpose: getDate with integer property
    public void testGetDateFromInteger() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_INTEGER);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        Integer value = new Integer(3);
        dataObject_c.set(property_c, value);
        try {
            dataObject_a.getDate(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //16. purpose: getDate with Defined Date Property
    public void testGetDateConversionFromDefinedDateProperty() {
        // dataObject's type add int property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DATE);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        long l = 12000;
        Date d = new Date(l);
        dataObject_a.setDate(propertyPath_a_b_c, d);// add it to instance list

        this.assertEquals(d, dataObject_a.getDate(propertyPath_a_b_c));
    }

    //12. purpose: getDate with Undefined string Property
    public void testGetDateConversionFromUnDefinedDateProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DATE);
        //type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        try {
            dataObject_a.getDate(propertyPath_a_b_c);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //purpose: getDate with nul value
    public void testGetDateWithNullArgument() {
        try {
            String p = null;
            dataObject_a.getDate(p);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }
}
