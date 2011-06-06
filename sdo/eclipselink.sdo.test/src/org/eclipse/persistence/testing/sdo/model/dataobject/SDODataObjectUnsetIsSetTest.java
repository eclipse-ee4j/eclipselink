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
import java.lang.System;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.exceptions.SDOException;

public class SDODataObjectUnsetIsSetTest extends SDODataObjectTestCases {
    public SDODataObjectUnsetIsSetTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectUnsetIsSetTest" };
        TestRunner.main(arguments);
    }

    //purpose: if dataObject just been new and property has not been set, iset() return false
    public void testIsSetWithNotYetSetDefinedProperty_SingleValue() {
        Property test = dataObject.getInstanceProperty(DEFINED_PROPERTY_NAME);
        this.assertFalse(dataObject.isSet(test));
    }

    //purpose: if dataObject just been new and property has not been set, iset() return false
    public void testIsSetWithNotYetSetDefinedProperty_SingleValue_Path() {
        this.assertFalse(dataObject.isSet(DEFINED_PROPERTY_NAME));
    }

    //purpose: if dataObject just been new and property has not been set, iset() return false
    public void testIsSetWithNotYetSetDefinedProperty_SingleValue_Index() {
        this.assertFalse(dataObject.isSet(0));
    }

    //purpose: if dataObject just been new and property has not been set, iset() return false
    public void testIsSetWithNotYetSetDefinedProperty_SingleValue_Path_a_b() {
        this.assertFalse(dataObject_Path_a_b.isSet(DEFINED_PROPERTY_NAME_A_B));
    }

    //purpose: if property has not been unSet, iset() return false
    public void testIsSetWithUnSetDefinedProperty_SingleValue() {
        Property test = dataObject.getInstanceProperty(DEFINED_PROPERTY_NAME);
        dataObject.unset(test);
        this.assertFalse(dataObject.isSet(test));
    }

    //purpose: if property has been set and then unSet, isSet() return true first and false later
    public void testIsSetWithSetThenUnSetDefinedProperty_SingleValue() {
        Property test = dataObject.getInstanceProperty(DEFINED_PROPERTY_NAME);
        dataObject.set(test, CONTROL_STRING_1);
        this.assertTrue(dataObject.isSet(test));
        dataObject.unset(test);
        this.assertFalse(dataObject.isSet(test));
    }

    //purpose: if current value isn't default value, iset() return false
    public void testIsSetWithCurrentAsDefinedPropertyDefault_SingleValue() {
        Property test = dataObject.getInstanceProperty(DEFINED_PROPERTY_NAME);
        dataObject.set(test, CONTROL_STRING_2);
        this.assertFalse(((String)dataObject.get(test)).equals(DEFAULT_VALUE));
        this.assertTrue(dataObject.isSet(test));
    }

    //purpose: if property has been set to default value, iset() return true. 
    //SDO Specification 2.1: 
    //"Any call to set() without a call to unset() will cause isSet() to return true, regardless of the value being set. "(Page 16)
    public void testIsSetWithSetDefinedPropertyAsDefault_SingleValue() {
        Property test = dataObject.getInstanceProperty(DEFINED_PROPERTY_NAME);
        dataObject.set(test, test.getDefault());
        assertTrue(((String)dataObject.get(test)).equals(DEFAULT_VALUE));
        assertTrue(dataObject.isSet(test));
    }

    //purpose: if property has been set, iset() return true
    public void testIsSetWithDefinedPropertyJustSet_SingleValue() {
        Property test = dataObject.getInstanceProperty(DEFINED_PROPERTY_NAME);

        dataObject.set(test, CONTROL_STRING_1);

        this.assertTrue(dataObject.isSet(test));
    }

    //purpose: set a property's value, then unset it, get its value should now return defaule value.
    public void testUnSetWithDefinedProperty_SingleValue() {
        Property test = dataObject.getInstanceProperty(DEFINED_PROPERTY_NAME);

        dataObject.set(test, CONTROL_STRING_1);

        dataObject.unset(test);
        // TODO: Verify ValueStore refactor does not change behavior
        assertEquals(DEFAULT_VALUE, (String)dataObject.get(test));
    }

    //purpose: set a property's value, then unset it, get its value should now return defaule value.
    public void testUnSetWithDefinedProperty_SingleValue_Path() {
        dataObject.set(DEFINED_PROPERTY_NAME, CONTROL_STRING_1);

        dataObject.unset(DEFINED_PROPERTY_NAME);
        // TODO: Verify ValueStore refactor does not change behavior
        assertEquals(DEFAULT_VALUE, (String)dataObject.get(DEFINED_PROPERTY_NAME));
    }

    //purpose: set a property's value, then unset it, get its value should now return defaule value.
    public void testUnSetWithDefinedProperty_SingleValue_Index() {
        dataObject.set(DEFINED_PROPERTY_NAME, CONTROL_STRING_1);

        dataObject.unset(0);

        this.assertEquals(DEFAULT_VALUE, (String)dataObject.get(DEFINED_PROPERTY_NAME));
    }

    //purpose: set a property's value, then unset it, get its value should now return defaule value.
    public void testUnSetWithDefinedProperty_SingleValue_Path_a_b() {
        dataObject_Path_a_b.unset(DEFINED_PROPERTY_NAME_A_B);

        this.assertEquals(DEFAULT_VALUE, (String)dataObject_Path_a_b.get(DEFINED_PROPERTY_NAME_A_B));
    }

    //purpose: unset  undefined property should cause exception
    public void testUnSetWithUndefinedProperty_SingleValue() {
        Property test = dataObject.getInstanceProperty(UNDEFINED_PROPERTY_NAME);

        try {
            dataObject.unset(test);
        } catch (SDOException e) {
            assertEquals(SDOException.CANNOT_PERFORM_OPERATION_ON_NULL_ARGUMENT ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");    
    }

    //purpose: set undefined property should cause exception
    public void testSetWithUndefinedProperty_SingleValue() {
        Property test = dataObject.getInstanceProperty(UNDEFINED_PROPERTY_NAME);

        try {
            dataObject.isSet(test);
        } catch (SDOException e) {
          assertEquals(SDOException.CANNOT_PERFORM_OPERATION_ON_NULL_ARGUMENT ,e.getErrorCode());
          return;
        }
        fail("An SDOException should have occurred.");
    }

    //bug 5770518
    public void testUnsetUndefinedByProp() {
        SDOProperty prop = new SDOProperty(getHelperContext());
        prop.setName("nonExistant");
        prop.setType(SDOConstants.SDO_STRING);
        dataObject.unset(prop);
    }

    //bug 5770518
    public void testUnsetUndefinedByPath() {        
        dataObject.unset("nonExistant");  
        //nothing should happen ie: no exception
    }

    //bug 5770518
    public void testUnsetUndefinedByIndex() {
        int totalSize = dataObject.getInstanceProperties().size();
        try {
            dataObject.unset(totalSize + 1);
        } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX, e.getErrorCode());
            return;
        }
        fail("An SDOException should have occurred.");
    }

    //purpose: set a non-nullable property's value to null - this should result in an unset op
    public void testSetNonNullablePropertyToNull() {
        Property test = dataObject.getInstanceProperty(DEFINED_PROPERTY_NAME);
        dataObject.set(test, null);
        // TODO: Verify ValueStore refactor does not change behavior
        assertTrue("Set non-nullable property to null didn't result in an unset operation being performed as expected", dataObject.isSet(test) == false);
    }

    //purpose: set a non-nullable many property's value to null - this should result in an unset op
    public void testSetNonNullableManyPropertyToNull() {
        Property test = dataObject.getInstanceProperty(DEFINED_MANY_PROPERTY_NAME);
        dataObject.set(test, null);
        // TODO: Verify ValueStore refactor does not change behavior
        assertTrue("Set non-nullable many property to null didn't result in an unset operation being performed as expected", dataObject.isSet(test) == false);
    }
}
