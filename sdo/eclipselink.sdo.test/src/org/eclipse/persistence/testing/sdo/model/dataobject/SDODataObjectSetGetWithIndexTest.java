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
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.exceptions.SDOException;

public class SDODataObjectSetGetWithIndexTest extends SDODataObjectTestCases {
    public SDODataObjectSetGetWithIndexTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectSetGetWithIndexTest" };
        TestRunner.main(arguments);
    }


    //purpose:  a property index is in instance property boundry
    public void testInstancePropertiesWithDefinedProperty() {
        // check it is in the list
        this.assertTrue(DEFINED_PROPERTY_INDEX <= (dataObject.getInstanceProperties().size() - 1));
    }

    //required conditions: none
    //purpose: Set value for property with valid index and retrieve it.
    public void testSetGetWithDefinedProperty() {
        dataObject.set(DEFINED_PROPERTY_INDEX, CONTROL_STRING_1);// set defined Property value

        String testValue = (String)dataObject.get(DEFINED_PROPERTY_INDEX);
        this.assertEquals(CONTROL_STRING_1, testValue);// check value is set as we want
    }

    //purpose:  Confirm that index id out of boundry of instance properties list.
    public void testInstancePropertiesWithUndefinedProperty() {
        // check it is not in the list
        this.assertFalse(UNDEFINED_PROPERTY_INDEX <= (dataObject.getInstanceProperties().size() - 1));
    }

    //required conditions: 1) DataObject is open
    //purpose: when property is undefined, getting it should throw exception
    // !!  inside !!
    public void testGetWithUnDefinedProperty_openDataObject() {
        try {
            dataObject.get(UNDEFINED_PROPERTY_INDEX);// get undefined Property value               
        } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");
    }

    // !! !!
    //required conditions: 1) DataObject is open
    //purpose: when property is undefined, set still does 'set', get still 'get', instance property list is still ok.
    // test undefined is not in instance property list
    // tset after set, it is in the list, and value is what we want
    public void testSetGetWithUnDefinedProperty_openDataObject() {
        try {
            dataObject.set(UNDEFINED_PROPERTY_INDEX, CONTROL_STRING_1);// set undefined Property value       
         } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");
    }

    //purpose: check dataObject is not open
    public void testDataObject_Not_Open() {
        this.assertFalse(dataObject_Not_Open.getType().isOpen());// check if DataObject is not Opened
    }

    //purpose: check property index is out of bountry of instance property list
    public void testDataObject_Not_In_List() {
        // check if it is undefined
        this.assertFalse(UNDEFINED_PROPERTY_INDEX < (dataObject_Not_Open.getInstanceProperties().size() - 1));
    }

    //!! !!
    //required conditions: 1) DataObject is not open
    //purpose: IllegalArgumentException can be thrown for undefined Property when set.
    public void testSetPropertyInvalidIndex_WithDataObject_Not_Open() {
        try {
            dataObject_Not_Open.set(UNDEFINED_PROPERTY_INDEX, CONTROL_STRING_1);// set undefined Property value   
         } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");
    }

    //required conditions: 1) DataObject is not open
    //purpose: IllegalArgumentException can be thrown for undefined Property when get.
    public void testGetInvalidPropertyIndex_WithDataObject_Not_Open() {
        try {
            dataObject_Not_Open.get(UNDEFINED_PROPERTY_INDEX);// get undefined Property value                   
        } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");
    }

    //required conditions: 1) DataObject is not open
    //purpose: IllegalArgumentException can be thrown for valid Property index when set.
    public void testSetValidPropertyINdex_WithDataObject_Not_Open() {
        try {
            dataObject_Not_Open.set(DEFINED_PROPERTY_INDEX, CONTROL_STRING_1);// set defined Property value   
        } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");
    }

    //required conditions: 1) DataObject is open 2) Property is read only
    //purpose: UnsupportedOperationException can be thrown for read only Property when set.
    public void testSetinvalidPropertyIndex_WithProperty_ReadOnly() {
        // check if it is defined
        this.assertTrue(DEFINED_PROPERTY_READONLY_INDEX <= (dataObject_WithReadOnlyProperty.getInstanceProperties().size() - 1));

        // check property is read only
        this.assertTrue(dataObject_WithReadOnlyProperty.getInstanceProperty(DEFINED_PROPERTY_READONLY_NAME).isReadOnly());

        try {
            dataObject_WithReadOnlyProperty.set(DEFINED_PROPERTY_READONLY_INDEX, CONTROL_STRING_1);// set undefined Property value  
            fail("An UnsupportedOperationException should have been thrown.");
        } catch (UnsupportedOperationException e) {
        }
    }

    //required condition: none
    //purpose: defined property not being set value, should return default value
    public void testGetDefinedProperty_ForDefaultValue() {
        String testValue = (String)dataObject.get(DEFINED_PROPERTY_INDEX);

        // TODO: Verify ValueStore refactor does not change behavior
        assertEquals(DEFAULT_VALUE, testValue);
    }

    //required condition: none
    //purpose: test get with index -1
    public void testGetWith_MINUS_ONE() {
        try {
            dataObject_Not_Open.get(MINUS_ONE);// get undefined Property value       
            fail("An IllegalArgumentException should have been thrown.");
        } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");
    }

    //required condition: none
    //purpose: test set with -1
    public void testSetWith_MINUS_ONE() {
        try {
            SDOProperty NUll = null;
            dataObject_Not_Open.set(MINUS_ONE, CONTROL_STRING_1);// set undefined Property value  
        } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");
    }
}
