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

import junit.textui.TestRunner;
import commonj.sdo.Property;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;

/**
 * Goal: 1) check if defined property is in Instance property list.
 * 2) check if undefined property is added in instance property list.
 * 3) check if set really changes peoperty value.
 * 4) check if DataObject is not open,  get/set undefined property throw exception.
 * 5) check if set read only property will cause exception.
 */
public class SDODataObjectSetGetWithPropertyTest extends SDODataObjectTestCases {
    public SDODataObjectSetGetWithPropertyTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectSetGetWithPropertyTest" };
        TestRunner.main(arguments);
    }
    
    //purpose:  Confirm that property is in instance property.
    public void testInstancePropertiesWithDefinedProperty() {
        Property definedProperty = dataObject.getInstanceProperty(DEFINED_PROPERTY_NAME);

        // check it is in the list
        this.assertTrue(dataObject.getInstanceProperties().contains(definedProperty));
    }

    //required conditions: none
    //purpose: Set value for known property and retrieve it.
    public void testSetGetWithDefinedProperty() {
        Property definedProperty = dataObject.getInstanceProperty(DEFINED_PROPERTY_NAME);

        dataObject.set(definedProperty, CONTROL_STRING_1);// set defined Property value

        String testValue = (String)dataObject.get(definedProperty);
        this.assertEquals(CONTROL_STRING_1, testValue);// check value is set as we want
    }

    //purpose:  Confirm that the undefined property is not in instance properties.
    public void testInstancePropertiesWithUndefinedProperty() {
        SDOProperty undefinedProperty = new SDOProperty(aHelperContext);// make a undefined Property
        undefinedProperty.setName(UNDEFINED_PROPERTY_NAME);

        // check it is not in the list
        this.assertFalse(dataObject.getInstanceProperties().contains(undefinedProperty));
    }

    //required conditions: 1) DataObject is open
    //purpose: when property is undefined, getting it should throw exception
    // !!  inside !!
    //Pretty sure this is not a valid test.

    /*
    public void testGetWithUnDefinedProperty_openDataObject() {
        SDOProperty undefinedProperty = new SDOProperty(aHelperContext);// make a undefined Property
        undefinedProperty.setName(UNDEFINED_PROPERTY_NAME);

        try {
            dataObject.get(undefinedProperty);// get undefined Property value
            fail("An IllegalArgumentException should have been thrown.");// !! not specified in spec. !!
        } catch (IllegalArgumentException e) {
        }
    }
    */

    //required conditions: 1) DataObject is open
    //purpose: when property is undefined, set still does 'set', get still 'get', instance property list is still ok.
    // test undefined is not in instance property list
    // tset after set, it is in the list, and value is what we want
    public void testSetGetWithUnDefinedProperty_openDataObject() {
        SDOProperty undefinedProperty = new SDOProperty(aHelperContext);// make a undefined Property
        undefinedProperty.setName(UNDEFINED_PROPERTY_NAME);
        // 6159746: no null Type allowed on Property Object
        undefinedProperty.setType(SDOConstants.SDO_STRING);

        dataObject.set(undefinedProperty, CONTROL_STRING_1);// set undefined Property value       

        Object testValue = dataObject.get(undefinedProperty);// check it is in
        this.assertEquals(CONTROL_STRING_1, testValue);// also verify its value
    }

    //required conditions: 1) DataObject is open
    //purpose: instance property list is correct.
    public void testSetUndefinedPropertyAddedToInstanceProperties_openDataObject() {
        SDOProperty undefinedProperty = new SDOProperty(aHelperContext);// make a undefined Property
        undefinedProperty.setName(UNDEFINED_PROPERTY_NAME);
        // 6159746: no null Type allowed on Property Object
        undefinedProperty.setType(SDOConstants.SDO_STRING);

        dataObject.set(undefinedProperty, CONTROL_STRING_1);// set undefined Property value       

        // check it is in the list
        this.assertTrue(dataObject.getInstanceProperties().contains(undefinedProperty));
    }

    //required conditions: 1) DataObject is not open
    //purpose: IllegalArgumentException can be thrown for undefined Property when set.
    public void testSetUndefinedProperty_WithDataObject_Not_Open() {
        SDOProperty undefinedProperty = new SDOProperty(aHelperContext);// make a undefined Property
        undefinedProperty.setName(UNDEFINED_PROPERTY_NAME);

        this.assertFalse(dataObject_Not_Open.getType().isOpen());// check if DataObject is not Opened

        // check if it is undefined
        this.assertFalse(dataObject_Not_Open.getInstanceProperties().contains(undefinedProperty));

        try {
            dataObject_Not_Open.set(undefinedProperty, CONTROL_STRING_1);// set undefined Property value   
            fail("An IllegalArgumentException should have been thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //required conditions: 1) DataObject is not open
    //purpose: IllegalArgumentException can be thrown for undefined Property when get.
    public void testGetUndefinedProperty_WithDataObject_Not_Open() {
        SDOProperty undefinedProperty = new SDOProperty(aHelperContext);// make a undefined Property
        undefinedProperty.setName(UNDEFINED_PROPERTY_NAME);

        this.assertFalse(dataObject_Not_Open.getType().isOpen());// check if DataObject is not Opened

        // check if it is undefined
        this.assertFalse(dataObject_Not_Open.getInstanceProperties().contains(undefinedProperty));

        try {
            dataObject_Not_Open.get(undefinedProperty);// get undefined Property value       
            fail("An IllegalArgumentException should have been thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    /*  //required conditions: 1) DataObject is not open
      //purpose: IllegalArgumentException can be thrown for defined Property when set.
      public void testSetdefinedProperty_WithDataObject_Not_Open()
      {
        Property definedProperty = dataObject.getProperty(DEFINED_PROPERTY_NAME);

        this.assertFalse(dataObject_Not_Open.getType().isOpen()); // check if DataObject is not Opened

        try{
          dataObject_Not_Open.set(definedProperty, CONTROL_STRING_1); // set defined Property value
          fail("An IllegalArgumentException should have been thrown.");
        }
        catch(IllegalArgumentException e){}

      }*/

    //required conditions: 1) DataObject is open 2) Property is read only
    //purpose: UnsupportedOperationException can be thrown for read only Property when set.
    public void testSetUndefinedProperty_WithProperty_ReadOnly() {
        Property ReadOnlyProperty = dataObject_WithReadOnlyProperty.getInstanceProperty(DEFINED_PROPERTY_READONLY_NAME);// make a undefined Property

        // check if it is undefined
        this.assertTrue(dataObject_WithReadOnlyProperty.getInstanceProperties().contains(ReadOnlyProperty));

        try {
            dataObject_WithReadOnlyProperty.set(ReadOnlyProperty, CONTROL_STRING_1);// set undefined Property value  
            fail("An UnsupportedOperationException should have been thrown.");
        } catch (UnsupportedOperationException e) {
        }
    }

    //required condition: none
    //purpose: defined property not being set value, should return default value
    public void testGetDefinedProperty_ForDefaultValue() {
        Property propertyWithDefaultV = dataObject.getInstanceProperty(DEFINED_PROPERTY_NAME);
        String testValue = (String)dataObject.get(propertyWithDefaultV);

        // TODO: Verify ValueStore refactor does not change behavior
        assertEquals(DEFAULT_VALUE, testValue);
    }

    //required condition: none
    //purpose: test get with Null value as property
    public void testGetWithNullProperty() {
        try {
            SDOProperty NUll = null;
            dataObject_Not_Open.get(NUll);// get undefined Property value       
            fail("An IllegalArgumentException should have been thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //required condition: none
    //purpose: test set with Null value as property
    public void testSetWithNullProperty() {
        try {
            SDOProperty NUll = null;
            dataObject_Not_Open.set(NUll, CONTROL_STRING_1);// set undefined Property value  
            fail("An IllegalArgumentException should have been thrown.");
        } catch (IllegalArgumentException e) {
        }
    }
}
