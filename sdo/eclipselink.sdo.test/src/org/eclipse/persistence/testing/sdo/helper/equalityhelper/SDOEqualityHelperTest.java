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
package org.eclipse.persistence.testing.sdo.helper.equalityhelper;

import commonj.sdo.DataObject;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;

public class SDOEqualityHelperTest extends SDOEqualityHelperEqualTestCases {
    public SDOEqualityHelperTest(String name) {
        super(name);
    }

    // compare a DataObject with itself by deep equal --- true
    // see SDOEqualityHelperBidirectionalTest.java for a complete bidirectional model    
    public void testDeepEqualWithADataObjectToItself() {
        assertTrue(equalityHelper.equal(root, root));
    }

    // compare a DataObject with its Deep copy by deep equal --- true
    // see SDOEqualityHelperBidirectionalTest.java for a complete bidirectional model    
    public void testDeepEqualWithADataObjectToItsDeepCopy() {
        DataObject deepCopy = copyHelper.copy(root);
        assertTrue(equalityHelper.equal(root, deepCopy));
    }

    // compare a DataObject with another DataObject with the same contents --- true
    public void testDeepEqualWithADataObjectToItsSameContentObject() {
        this.assertTrue(equalityHelper.equal(root, root1));
    }

    // comapre a DataObject with its Shallow copy by deep equal --- false
    public void testDeepEqualWithADataObjectToItsShallowCopy() {
        DataObject shallowCopy = copyHelper.copyShallow(root);
        this.assertFalse(equalityHelper.equal(root, shallowCopy));
    }

    // comapre a DataObject with its Shallow copy by deep equal --- false
    public void testDeepEqualWithADataObjectToItsChild() {
        this.assertFalse(equalityHelper.equal(root, containedDataObject));
    }

    // compare a DataObject with another different DataObject --- false
    public void testDeepEqualWithADataObjectToAnotherDataObject() {
        this.assertFalse(equalityHelper.equal(root, containedDataObject));
    }

    // !! this test case base on the following and Now its meaning is still not sure     !!
    // Note that properties to a containing DataObject are not compared which 
    // means two DataObject trees can be equal even if their containers are not equal.
    public void testDeepEqualWithADataObjectToAnotherDataObjectWithDifferentParents() {
        this.assertTrue(equalityHelper.equal(containedDataObject, containedDataObject_1));
    }

    // purpose: one has open content datatype property while another doesn't
    public void testDeepEqualWithADataObjectToAnotherOpenContentProperties() {
        SDOProperty openP = new SDOProperty(aHelperContext);
        openP.setName("open");
        openP.setContainment(false);
        openP.setMany(false);
        openP.setType(SDOConstants.SDO_STRING);
        containedType.setOpen(true);
        containedDataObject.set(openP, "test");
        this.assertFalse(equalityHelper.equal(root, root1));
    }

    // purpose: one has open content datatype property while another does too
    public void testDeepEqualWithADataObjectToAnotherOpenContentPropertiesBoth() {
        SDOProperty openP = new SDOProperty(aHelperContext);
        openP.setName("open");
        openP.setContainment(false);
        openP.setMany(false);
        openP.setType(SDOConstants.SDO_STRING);
        containedType.setOpen(true);
        containedDataObject.set(openP, "test");
        containedDataObject_1.set(openP, "test");
        this.assertTrue(equalityHelper.equal(root, root1));
        containedDataObject.set(openP, "test");
        containedDataObject_1.set(openP, "test1");
        this.assertFalse(equalityHelper.equal(root, root1));
    }

    // purpose: one has open content datatype property while another doesn't
    public void testDeepEqualWithADataObjectToAnotherOpenContentPropertiesNotDataType() {
        SDOProperty openP = new SDOProperty(aHelperContext);
        openP.setName("open");
        openP.setContainment(true);
        openP.setMany(false);
        openP.setType(new SDOType("", ""));
        containedType.setOpen(true);
        SDOType mockType = new SDOType("uri", "name");
        DataObject d1 = dataFactory.create(mockType);
        containedDataObject.set(openP, d1);
        this.assertFalse(equalityHelper.equal(root, root1));
    }

    // purpose: one has open content datatype property while another doesn't
    public void testDeepEqualWithADataObjectToAnotherOpenContentPropertiesNotDataTypeBoth() {
        SDOProperty openP = new SDOProperty(aHelperContext);
        openP.setName("open");
        openP.setContainment(true);
        openP.setMany(false);
        openP.setType(new SDOType("", ""));
        containedType.setOpen(true);
        SDOType mockType = new SDOType("uri", "name");
        DataObject d1 = dataFactory.create(mockType);
        DataObject d2 = dataFactory.create(mockType);
        containedDataObject.set(openP, d1);
        containedDataObject_1.set(openP, d2);
        this.assertTrue(equalityHelper.equal(root, root1));
    }

    // compare DataObject with properties as many
    public void testDeepEqualWithTwoDataObjectsHavingManyTypeProperties() {
        rootProperty4.setContainment(false);
        rootProperty4.setType(SDOConstants.SDO_STRING);
        objects.add("test");
        objects1.add("test");
        root.set(rootProperty4, objects);
        root1.set(rootProperty4, objects1);
        this.assertTrue(equalityHelper.equal(root, root1));
    }

    // compare DataObject with properties as many
    public void testDeepEqualWithTwoDataObjectsHavingManyTypePropertiesDataObjects() {
        //rootProperty4.setContainment(false);
        //SDOType rootProperty4_type = new SDOType("", "");
        //rootProperty4.setType(SDOConstants.SDO_STRING);
        SDOType mockType = new SDOType("uri", "name");
        DataObject d1 = dataFactory.create(mockType);
        DataObject d2 = dataFactory.create(mockType);
        objects.add(d1);
        objects1.add(d2);
        rootProperty4.setMany(true);
        root.set(rootProperty4, objects);
        root1.set(rootProperty4, objects1);
        this.assertTrue(equalityHelper.equal(root, root1));
    }

    // compare DataObject with properties as many
    public void testDeepEqualWithTwoDataObjectsHavingManyTypePropertiesDataObjectsFail() {
        //rootProperty4.setContainment(false);
        //SDOType rootProperty4_type = new SDOType("", "");
        //rootProperty4.setType(SDOConstants.SDO_STRING);
        SDOType mockType = new SDOType("uri", "name");
        SDOProperty p = new SDOProperty(aHelperContext);
        p.setContainment(false);
        p.setMany(false);
        p.setName("fortest");

        p.setType(SDOConstants.SDO_STRING);
        mockType.addDeclaredProperty(p);
        DataObject d1 = dataFactory.create(mockType);
        DataObject d2 = dataFactory.create(mockType);
        DataObject d3 = dataFactory.create(mockType);
        DataObject d4 = dataFactory.create(mockType);
        objects.add(d1);
        objects.add(d3);
        objects1.add(d4);
        objects1.add(d2);
        d4.set(p, "fail");
        rootProperty4.setMany(true);
        root.set(rootProperty4, objects);
        root1.set(rootProperty4, objects1);
        this.assertFalse(equalityHelper.equal(root, root1));
    }

    // compare DataObject with properties as many
    public void testDeepEqualWithTwoDataObjectsHavingManyTypePropertiesDataObjectsFailListSizeUnequal() {
        //rootProperty4.setContainment(false);
        //SDOType rootProperty4_type = new SDOType("", "");
        //rootProperty4.setType(SDOConstants.SDO_STRING);
        SDOType mockType = new SDOType("uri", "name");
        SDOProperty p = new SDOProperty(aHelperContext);
        p.setContainment(false);
        p.setMany(false);
        p.setName("fortest");

        p.setType(SDOConstants.SDO_STRING);
        mockType.addDeclaredProperty(p);
        DataObject d1 = dataFactory.create(mockType);
        DataObject d2 = dataFactory.create(mockType);
        DataObject d3 = dataFactory.create(mockType);
        DataObject d4 = dataFactory.create(mockType);
        objects.add(d1);
        objects.add(d3);
        //objects1.add(d4);
        objects1.add(d2);
        //d4.set(p, "fail");
        rootProperty4.setMany(true);
        root.set(rootProperty4, objects);
        root1.set(rootProperty4, objects1);
        this.assertFalse(equalityHelper.equal(root, root1));
    }

    // compare DataObject with properties as many
    public void testDeepEqualWithTwoDataObjectsHavingManyTypePropertiesDataObjectsFailOnePropertyNotSet() {
        //rootProperty4.setContainment(false);
        //SDOType rootProperty4_type = new SDOType("", "");
        //rootProperty4.setType(SDOConstants.SDO_STRING);
        SDOType mockType = new SDOType("uri", "name");
        SDOProperty p = new SDOProperty(aHelperContext);
        p.setContainment(false);
        p.setMany(false);
        p.setName("fortest");

        p.setType(SDOConstants.SDO_STRING);
        mockType.addDeclaredProperty(p);
        DataObject d1 = dataFactory.create(mockType);
        DataObject d2 = dataFactory.create(mockType);
        DataObject d3 = dataFactory.create(mockType);
        DataObject d4 = dataFactory.create(mockType);
        objects.add(d1);
        objects.add(d3);
        objects1.add(d4);
        objects1.add(d2);
        //d4.set(p, "fail");
        rootProperty4.setMany(true);
        root.set(rootProperty4, objects);
        //root1.set(rootProperty4, objects1);
        this.assertFalse(equalityHelper.equal(root, root1));
    }

    /**
     * Test whether EqualityHelper.compareProperty() handles nested objects that are null but isSet=true
     * (Where we do not have a bidirectional child property)
     * See SDOCopyHelperDeepTest for the same test with the bidirectional child 
     */
    public void testDeepEqualWithUnsetComplexChild() {
    	// clear complex child
    	root.unset("rootproperty2-notdatatype");	
    	SDODataObject copyOfRoot = (SDODataObject)copyHelper.copy(root);
    	assertFalse(root.isSet("rootproperty2-notdatatype"));
    	
    	assertNotNull(copyOfRoot);
    	assertTrue(equalityHelper.equal(root, copyOfRoot));
    }
    //  TODO: process non-containment side of bidirectionals see #5853175
    public void testDeepEqualWithSetNullComplexChild() {
    	// clear complex child
    	root.set("rootproperty2-notdatatype", null);	
    	SDODataObject copyOfRoot = (SDODataObject)copyHelper.copy(root);
    	assertFalse(root.isSet("rootproperty2-notdatatype"));
    	assertNotNull(copyOfRoot);
    	// this assertion previously failed before fix for #5852525
    	assertTrue(equalityHelper.equal(root, copyOfRoot));
    }
    
    // compare DataObject with properties as many
    public void testDeepEqualWithTwoDataObjectsHavingManyTypeDataTypePropertiesDataObjectsFail() {
        //rootProperty4.setContainment(false);
        //SDOType rootProperty4_type = new SDOType("", "");
        //rootProperty4.setType(SDOConstants.SDO_STRING);
        //SDOType mockType = new SDOType("uri", "name");
        //SDOProperty p = new SDOProperty(aHelperContext);
        //p.setContainment(false);
        //p.setMany(false);
        //p.setName("fortest");
        //p.setType(SDOConstants.SDO_STRING);
        //mockType.addDeclaredProperty(p);
        //DataObject d1 = dataFactory.create(mockType);
        //DataObject d2 = dataFactory.create(mockType);
        //DataObject d3 = dataFactory.create(mockType);
        //DataObject d4 = dataFactory.create(mockType);
        objects.add("test");
        objects.add("test1");
        objects1.add("test");
        objects1.add("test1");
        //d4.set(p, "fail");
        rootProperty4.setMany(true);
        rootProperty4.setContainment(false);
        rootProperty4.setType(SDOConstants.SDO_STRING);
        root.set(rootProperty4, objects);
        root1.set(rootProperty4, objects1);
        this.assertTrue(equalityHelper.equal(root, root1));
    }

    // compare a DataObject with itself by shallow equal --- true
    public void testShallowEqualWithDataObjectToItself() {
        this.assertTrue(equalityHelper.equalShallow(root, root));
    }

    // compare a DataObject with its Deep copy by shallow equal --- true
    public void testShallowEqualWithADataObjectToItsDeepCopy() {
        DataObject deepCopy = copyHelper.copy(root);
        this.assertTrue(equalityHelper.equalShallow(root, deepCopy));
    }

    // compare a DataObject with its Shallow copy by shallow equal --- true
    public void testShallowEqualWithADataObjectToItsShallowCopy() {
        DataObject shallowCopy = copyHelper.copyShallow(root);
        this.assertTrue(equalityHelper.equalShallow(root, shallowCopy));
    }

    // compare a DataObject with another different DataObject  by shallow equal --- false
    public void testShallowEqualWithADataObjectToAnotherDataObject() {
        this.assertFalse(equalityHelper.equalShallow(root, containedDataObject));
    }

    // compare null with null by deep equal --- true
    public void testDeepEqualWithNullToNull() {
        this.assertTrue(equalityHelper.equal(null, null));
    }

    // compare a DataObject with null by deep equal --- false
    public void testDeepEqualWithADataObjectToNull() {
        this.assertFalse(equalityHelper.equal(root, null));
    }

    // compare null with null by shallow equal --- true
    public void testShallowEqualWithNullToNull() {
        this.assertTrue(equalityHelper.equalShallow(null, null));
    }

    // compare a DataObject with shallow null --- false
    public void testShallowEqualWithADataObjectToNull() {
        this.assertFalse(equalityHelper.equalShallow(root, null));
    }

    // compare DataObject with properties as many
    public void testDeepEqualWithTwoDataObjectsHavingManyTypePropertiesDataObjectsFailShallow() {
        SDOProperty rootProperty5 = new SDOProperty(aHelperContext);
        rootProperty5.setContainment(false);
        //SDOType rootProperty4_type = new SDOType("", "");
        rootProperty5.setType(SDOConstants.SDO_STRING);

        objects.add("test");
        objects.add("test1");
        objects1.add("test");
        //objects1.add(d2);
        //d4.set(p, "fail");
        rootProperty5.setMany(true);
        root.set(rootProperty5, objects);
        root1.set(rootProperty5, objects1);
        this.assertFalse(equalityHelper.equalShallow(root, root1));
    }

    // compare DataObject with properties as many
    public void testDeepEqualWithTwoDataObjectsHavingManyTypePropertiesDataObjectsFailShallowOnePropertyNotSet() {
        SDOProperty rootProperty5 = new SDOProperty(aHelperContext);
        rootProperty5.setContainment(false);
        //SDOType rootProperty4_type = new SDOType("", "");
        rootProperty5.setType(SDOConstants.SDO_STRING);

        rootType.addDeclaredProperty(rootProperty5);
        root._setType(rootType);

        objects.add("test");
        objects.add("test1");
        objects1.add("test");
        //objects1.add(d2);
        //d4.set(p, "fail");
        rootProperty5.setMany(true);
        root.set(rootProperty5, objects);
        //root1.set(rootProperty5, objects1);
        this.assertFalse(equalityHelper.equalShallow(root, root1));
    }
}
