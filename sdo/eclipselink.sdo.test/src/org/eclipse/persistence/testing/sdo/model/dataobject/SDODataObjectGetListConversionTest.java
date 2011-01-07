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

import commonj.sdo.Property;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.exceptions.SDOException;

public class SDODataObjectGetListConversionTest extends SDODataObjectConversionWithPathTestCases {
    public SDODataObjectGetListConversionTest(String name) {
        super(name);
    }    
      
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetListConversionTest" };
        TestRunner.main(arguments);
    }

    //6. purpose: getList(Path) with path length 1: "a"
    public void testGetListConversionWithPath() {
        property_a_pathLength_1.setType(SDOConstants.SDO_STRINGS);
        property_a_pathLength_1.setMany(true);
        List b = new ArrayList();

        dataObject_a_pathLength_1.setList("PName-a-length-1", b);

        this.assertEquals(b, dataObject_a_pathLength_1.getList("PName-a-length-1"));
    }

    //6. purpose: getList(Path) with path length 1: "a"
    public void testGetListConversionWithWrongPath() {
        SDOProperty property_undefined = new SDOProperty(aHelperContext);
        property_undefined.setName("PName-Undefined");
        property_undefined.setType(SDOConstants.SDO_STRINGS);
        property_undefined.setMany(true);

        try {
            dataObject_a_pathLength_1.getList("PName-Undefined");
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    //6. purpose: getList(Path) with defined property
    public void testGetListConversionWithProperty() {
        property_a_pathLength_1.setType(SDOConstants.SDO_STRINGS);
        property_a_pathLength_1.setMany(true);
        List b = new ArrayList();

        dataObject_a_pathLength_1.setList(property_a_pathLength_1, b);

        this.assertEquals(b, dataObject_a_pathLength_1.getList(property_a_pathLength_1));
    }

    //6. purpose: getList(Path) with defined property
    public void testGetListConversionWithPropertyPositionalSetting() {
        type_a_pathLength_1.setOpen(true);
        property_a_pathLength_1.setType(SDOConstants.SDO_STRINGS);
        property_a_pathLength_1.setMany(true);
        List b = new ArrayList();

        dataObject_a_pathLength_1.setList(property_a_pathLength_1, b);
        dataObject_a_pathLength_1.set(PROPERTY_NAME_A_LENGTH_1 + ".0", "test");

        this.assertEquals(1, dataObject_a_pathLength_1.getList(property_a_pathLength_1).size());
    }

    //6. purpose: getList(Path) with defined property
    public void testGetListConversionWithPropertyPositionalSettingBracket() {
        type_a_pathLength_1.setOpen(true);
        property_a_pathLength_1.setType(SDOConstants.SDO_STRINGS);
        property_a_pathLength_1.setMany(true);
        List b = new ArrayList();

        dataObject_a_pathLength_1.setList(property_a_pathLength_1, b);
        dataObject_a_pathLength_1.set(PROPERTY_NAME_A_LENGTH_1 + "[1]", "test");

        this.assertEquals(1, dataObject_a_pathLength_1.getList(property_a_pathLength_1).size());
        this.assertEquals("test", dataObject_a_pathLength_1.getList(property_a_pathLength_1).get(0));
    }

    //6. purpose: getList(Path) with defined property value null
    public void testGetListConversionWithPropertyValueNull() {
        property_a_pathLength_1.setType(SDOConstants.SDO_STRINGS);
        property_a_pathLength_1.setMany(true);
        List b = null;

        dataObject_a_pathLength_1.setList(property_a_pathLength_1, b);

        this.assertNotNull(dataObject_a_pathLength_1.getList(property_a_pathLength_1));
    }

    //6. purpose: getList(Path) with path length 1: "a"
    public void testGetListConversionWithUndefinedProperty() {
        SDOProperty property_undefined = new SDOProperty(aHelperContext);
        property_undefined.setName("PName-Undefined");
        property_undefined.setType(SDOConstants.SDO_STRINGS);
        property_undefined.setMany(true);

        try {
            dataObject_a_pathLength_1.getList(property_undefined);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //6. purpose: getBoolean with Defined Boolean Property
    public void testGetListConversionWithIndex() {
        // dataObject's type add boolean property
        property_a_pathLength_1.setType(SDOConstants.SDO_STRINGS);
        property_a_pathLength_1.setMany(true);
        List b = new ArrayList();

        dataObject_a_pathLength_1.setList(0, b);

        this.assertEquals(b, dataObject_a_pathLength_1.getList(0));
    }

    //6. purpose: getBoolean with Undefined Boolean Property
    public void testGetListConversionWithUndefinedIndex() {
        // dataObject's type add boolean property        
        SDOType type_undefined = new SDOType(URINAME, TYPENAME_A);
        SDODataObject dataObject_undeinfed = (SDODataObject)dataFactory.create(type_undefined);
        
        SDOProperty property_undefined = new SDOProperty(aHelperContext);
        List b = new ArrayList();

        try {
            dataObject_undeinfed.getList(0);
            
        } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");                
    }

    public void testGetListConversionWithNonManyProperty() {
        property_a_pathLength_1.setType(SDOConstants.SDO_STRINGS);
        property_a_pathLength_1.setMany(false);        
        try {
            dataObject_a_pathLength_1.getList("PName-a-length-1");            
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());            
        }
    }

    public void testGetListConversionWithNullValue() {
        property_a_pathLength_1.setType(SDOConstants.SDO_STRINGS);
        property_a_pathLength_1.setMany(true);

        List value = dataObject_a_pathLength_1.getList("PName-a-length-1");
        this.assertEquals(new ArrayList(), value);
    }

    public void testGetListConversionWithNullValueByProperty() {
        property_a_pathLength_1.setType(SDOConstants.SDO_STRINGS);
        property_a_pathLength_1.setMany(true);

        List value = dataObject_a_pathLength_1.getList(PROPERTY_NAME_A_LENGTH_1);
        this.assertEquals(new ArrayList(), value);
    }

    public void testSameListObject() {
        property_a_pathLength_1.setType(SDOConstants.SDO_STRING);
        property_a_pathLength_1.setMany(true);

        Object value = dataObject_a_pathLength_1.get(PROPERTY_NAME_A_LENGTH_1);
        Object value2 = dataObject_a_pathLength_1.getList(PROPERTY_NAME_A_LENGTH_1);

        boolean equals = value == value2;
        assertTrue(equals);

        dataObject_a_pathLength_1.unset(PROPERTY_NAME_A_LENGTH_1);
        Object value3 = dataObject_a_pathLength_1.get(PROPERTY_NAME_A_LENGTH_1);
        Object value4 = dataObject_a_pathLength_1.getList(PROPERTY_NAME_A_LENGTH_1);

        equals = value3 == value4;
        assertTrue(equals);

        equals = value2 == value3;
        assertTrue(equals);

        List myList = new ArrayList();
        myList.add("test");
        dataObject_a_pathLength_1.set(PROPERTY_NAME_A_LENGTH_1, myList);
        Object value5 = dataObject_a_pathLength_1.get(PROPERTY_NAME_A_LENGTH_1);
        Object value6 = dataObject_a_pathLength_1.getList(PROPERTY_NAME_A_LENGTH_1);

        equals = value5 == value6;
        assertTrue(equals);

        equals = value4 == value5;
        assertTrue(equals);

        List myList2 = new ArrayList();
        myList2.add("test2");
        dataObject_a_pathLength_1.set(PROPERTY_NAME_A_LENGTH_1, myList2);
        Object value7 = dataObject_a_pathLength_1.get(PROPERTY_NAME_A_LENGTH_1);
        Object value8 = dataObject_a_pathLength_1.getList(PROPERTY_NAME_A_LENGTH_1);

        equals = value7 == value8;
        assertTrue(equals);

        equals = value6 == value7;
        assertTrue(equals);
    }
}
