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
package org.eclipse.persistence.testing.sdo.model.dataobject.xpathquery;

import commonj.sdo.Property;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;

public class SDODataObjectUnsetIsSetByXPathQueryTest extends SDODataObjectGetByXPathQueryTestCases {
    public SDODataObjectUnsetIsSetByXPathQueryTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.xpathquery.SDODataObjectUnsetIsSetByXPathQueryTest" };
        TestRunner.main(arguments);
    }

    //purpose: if dataObject just been new and property has not been set, iset() return false
    public void testIsSetWithNotYetSetDefinedProperty_SingleValue() {
        //Property test = dataObject.getProperty(DEFINED_PROPERTY_NAME);
        //a/b[number=1]/c
        this.assertFalse(dataObject_a.isSet("PName-a0/PName-b0[number='1']/" + "PName-c0"));
    }

    //purpose: if dataObject just been new and property has not been set, iset() return false
    public void testIsSetWithNotYetSetDefinedProperty_SingleValue_Path() {
        //a/b.0/c
        this.assertFalse(dataObject_a.isSet("PName-a0/PName-b0.1/PName-c0"));
    }

    //purpose: if dataObject just been new and property has not been set, iset() return false
    public void testIsSetWithNotYetSetDefinedProperty_SingleValue_Index() {
        this.assertTrue(dataObject_a.isSet(propertyTest + "PName-c0"));
    }

    //purpose: if dataObject just been new and property has not been set, iset() return false
    public void testIsSetWithNotYetSetDefinedProperty_SingleValue_Path_a_b() {
        this.assertTrue(dataObject_a.isSet("PName-a0/PName-b0.0/PName-c0"));
    }

    //purpose: if property has been set and then unSet, isSet() return true first and false later
    public void testIsSetWithSetThenUnSetDefinedProperty_SingleValue1() {
        this.assertTrue(dataObject_a.isSet(propertyTest + "PName-c0"));
        dataObject_a.unset(propertyTest + "PName-c0");
        this.assertFalse(dataObject_a.isSet(propertyTest + "PName-c0"));
    }

    //purpose: if property has been set and then unSet, isSet() return true first and false later
    public void testIsSetWithSetThenUnSetDefinedProperty_SingleValue() {
        this.assertTrue(dataObject_a.isSet("PName-a0/PName-b0.0/PName-c0"));
        dataObject_a.unset("PName-a0/PName-b0.0/PName-c0");
        this.assertFalse(dataObject_a.isSet("PName-a0/PName-b0.0/PName-c0"));
    }

    //purpose: set a property's value, then unset it, get its value should now return defaule value.
    public void testUnSetWithDefinedProperty_SingleValue_Path_a_b1() {
        List deflt = new ArrayList();
        property_c0.setDefault(deflt);
        dataObject_a.unset(propertyTest + "PName-c0");

        this.assertEquals(deflt, (List)dataObject_a.get(propertyTest + "PName-c0"));
    }

    //purpose: set a property's value, then unset it, get its value should now return defaule value.
    public void testUnSetWithDefinedProperty_SingleValue_Path_a_b() {
        List deflt = new ArrayList();
        property_c0.setDefault(deflt);
        dataObject_a.unset("PName-a0/PName-b0.0/PName-c0");

        this.assertEquals(deflt, (List)dataObject_a.get("PName-a0/PName-b0.0/PName-c0"));
    }

    //case:a/b.0/c[number="123"] where number is a many type property, and no objects meets requirement
    public void testMultipleCaseOueryD1AliasName() {
        //List aliasNames = new ArrayList();
        //aliasNames.add("alias1");
        //aliasNames.add("alias2");
        //property_d_number.setAliasNames(aliasNames);
        dataObject_a.unset("schema:PName-a/PName-b.0/PName-c[alias1=\"1\"]");
        this.assertFalse(dataObject_a.isSet("schema:PName-a/PName-b.0/PName-c[alias1=\"1\"]"));
    }

    public void testunSetOpenContentPropertyAliasName() {
        SDOProperty property_open = new SDOProperty(aHelperContext);
        property_open.setName("openProperty");
        property_open.setType(SDOConstants.SDO_STRING);
        property_open.setMany(false);
        List aliasNames_ = new ArrayList();
        aliasNames_.add("alias1");
        aliasNames_.add("alias2");
        property_open.setAliasNames(aliasNames_);

        dataObject_c.set(property_open, "test");
        dataObject_a.unset("schema:PName-a/PName-b.0/alias2");

        this.assertFalse(dataObject_a.isSet("schema:PName-a/PName-b.0/alias1"));

    }

    //case:a/b.0/c[number="123"] where number is a many type property, and no objects meets requirement
    public void testMultipleCaseOueryD1AliasName1() {
        dataObject_a.unset("schema:PName-a/@alias2.0/PName-c[number=\"1\"]");
        this.assertFalse(dataObject_a.isSet("schema:PName-a/@alias2.0/PName-c[number=\"1\"]"));
    }

    /*//purpose: unset  undefined property should cause exception
    public void testUnSetWithUndefinedProperty_SingleValue() {
        Property test = dataObject.getProperty(UNDEFINED_PROPERTY_NAME);

        try {
            dataObject.unset(propertyTest+"undefined");
            fail("Should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
        }
    }*/

    //purpose: set undefined property should cause exception
    public void testSetWithUndefinedProperty_SingleValue1() {
        // Property test = dataObject.getProperty(UNDEFINED_PROPERTY_NAME);
        //try {
        this.assertFalse(dataObject_a.isSet(propertyTest + "undefined"));

        //fail("Should throw IllegalArgumentException.");
        //} catch (IllegalArgumentException e) {
        //}
    }

    //purpose: set undefined property should cause exception
    public void testSetWithUndefinedProperty_SingleValue() {
        // Property test = dataObject.getProperty(UNDEFINED_PROPERTY_NAME);
        //try {
        this.assertFalse(dataObject_a.isSet("PName-a0/PName-b0.0/undefined"));

        //fail("Should throw IllegalArgumentException.");
        //} catch (IllegalArgumentException e) {
        //}
    }
    
    //purpose: if dataObject just been new and property has not been set, iset() return false
    public void testUnSetEntireList() {
        SDODataObject dataObjectC1 = (SDODataObject)dataFactory.create(type_c);        
        SDODataObject dataObjectC2 = (SDODataObject)dataFactory.create(type_c);        
        SDODataObject dataObjectC3 = (SDODataObject)dataFactory.create(type_c);      
        SDODataObject dataObjectC4 = (SDODataObject)dataFactory.create(type_c);
            
        List theList = new ArrayList();
        theList.add(dataObjectC1);
        theList.add(dataObjectC2);
        theList.add(dataObjectC3);
        theList.add(dataObjectC4);
        
        dataObject_a.set("PName-a0/PName-b0", theList);
        List dataObjectList = dataObject_a.getList("PName-a0/PName-b0");
        assertEquals(4, dataObjectList.size());
        
        
        dataObject_a.set("PName-a0/PName-b0", theList);
        
        dataObject_a.unset("PName-a0/PName-b0");
        
        dataObjectList = dataObject_a.getList("PName-a0/PName-b0");
        assertEquals(0, dataObjectList.size() );
              
    }
    
    public void testUnSetItemInList() {
        SDODataObject dataObjectC1 = (SDODataObject)dataFactory.create(type_c);        
        SDODataObject dataObjectC2 = (SDODataObject)dataFactory.create(type_c);        
        SDODataObject dataObjectC3 = (SDODataObject)dataFactory.create(type_c);        
        SDODataObject dataObjectC4 = (SDODataObject)dataFactory.create(type_c);        
        
        List theList = new ArrayList();
        theList.add(dataObjectC1);
        theList.add(dataObjectC2);
        theList.add(dataObjectC3);
        theList.add(dataObjectC4);
        
        dataObject_a.set("PName-a0/PName-b0", theList);
        List dataObjectList = dataObject_a.getList("PName-a0/PName-b0");
        assertEquals(4, dataObjectList.size() );
        
        dataObject_a.unset("PName-a0/PName-b0.1");
        
        dataObjectList = dataObject_a.getList("PName-a0/PName-b0");
        assertEquals(3, dataObjectList.size() );
              
    }

    public void testPathDoesNotExistJira8() {
        SDODataObject dataObjectC1 = (SDODataObject)dataFactory.create(type_c);
        boolean isSet = dataObjectC1.isSet("something/somethingelse");
        assertFalse(isSet);
    }
}
