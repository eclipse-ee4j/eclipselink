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
package org.eclipse.persistence.testing.sdo.model.changesummary;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;

public class ChangeSummaryGetOldValueTest extends ChangeSummaryTestCases {
    public ChangeSummaryGetOldValueTest(String name) {
        super(name);
    }
    
    //Bug 5918326
    public void testGetOldValueNullProperty() {
      changeSummary.beginLogging();
      //make root dirty
      root.set(rootProperty1, "test2");
      ChangeSummary.Setting s = changeSummary.getOldValue(root, null);
      assertNull(s);
    }
      

    // purpose: modified DataObject and nonmodified property, get(DataObject, Property)
    public void testGetOldValueReturnSettingWithUnmodifiedProperty() {
        changeSummary.beginLogging();
        List oldValues = changeSummary.getOldValues(root);
        assertNotNull(oldValues);
        assertEquals(0, oldValues.size());
        root.set(rootProperty1, "test");
        this.assertNull(changeSummary.getOldValue(root, rootProperty));// no old value for this unmodified property
        this.assertNull(changeSummary.getOldValue(root, rootProperty1).getValue());// old value has value null for this modified property
    }

    // purpose: modified DataObject and nonmodified property, get(DataObject, Property)
    public void testGetOldValueAfterTwoModifications() {
        changeSummary.beginLogging();
        // this should be flagged as a modification
        root.set(rootProperty1, "test");
        root.set(rootProperty1, "test2");
        // no old value for this unmodified property        
        assertNull(changeSummary.getOldValue(root, rootProperty));
        // old value has value null for this modified property        
        assertNull(changeSummary.getOldValue(root, rootProperty1).getValue());
        assertFalse(changeSummary.getOldValue(root, rootProperty1).isSet());
    }

    public void testUC1GetOldValueAfter1stSetAfterLoggingOn() {
        changeSummary.beginLogging();
        root.set(rootProperty1, "test");
        // no old value for this unmodified property
        assertTrue(changeSummary.isModified(root));
        assertNull(changeSummary.getOldValue(root, rootProperty1).getValue());
        assertFalse(changeSummary.getOldValue(root, rootProperty1).isSet());
    }

    public void testUC2GetOldValueAfter2ndSetAfterLoggingOnAfter1stSet() {
        root.set(rootProperty1, null);
        changeSummary.beginLogging();
        root.set(rootProperty1, "test");
        // no old value for this unmodified property
        assertTrue(changeSummary.isModified(root));
        assertNull(changeSummary.getOldValue(root, rootProperty1).getValue());
        assertTrue(changeSummary.getOldValue(root, rootProperty1).isSet());
    }

    public void testUC3GetOldValueAfter2ndSetAfterLoggingOnAfter1stSet() {
        root.set(rootProperty1, "test");
        changeSummary.beginLogging();
        root.set(rootProperty1, "test2");
        // no old value for this unmodified property
        assertTrue(changeSummary.isModified(root));
        assertEquals("test", changeSummary.getOldValue(root, rootProperty1).getValue());
        assertTrue(changeSummary.getOldValue(root, rootProperty1).isSet());
    }

    public void testUC4GetOldValueAfter2ndUnSetAfterLoggingOnAfter1stSet() {
        root.set(rootProperty1, null);
        changeSummary.beginLogging();
        root.unset(rootProperty1);
        // no old value for this unmodified property
        assertTrue(changeSummary.isModified(root));
        assertNull(changeSummary.getOldValue(root, rootProperty1).getValue());
        assertTrue(changeSummary.getOldValue(root, rootProperty1).isSet());
    }

    // TODO: Is there a difference between default->null and null->null
    public void testUC5GetOldValueAfter2ndSetAfterLoggingOnAfter1stUnSet() {
        root.unset(rootProperty1);
        changeSummary.beginLogging();
        root.set(rootProperty1, null);
        // no old value for this unmodified property
        assertTrue(changeSummary.isModified(root));
        assertNull(changeSummary.getOldValue(root, rootProperty1).getValue());
        assertFalse(changeSummary.getOldValue(root, rootProperty1).isSet());
    }

    public void testUC6GetOldValueAfter2ndSetAfterLoggingToSameValue() {
        root.set(rootProperty1, null);
        changeSummary.beginLogging();
        root.set(rootProperty1, null);
        // no old value for this unmodified property
        assertFalse(changeSummary.isModified(root));
        assertNull(changeSummary.getOldValue(root, rootProperty1));
    }

    public void testUC7GetOldValueAfter2ndSetAfterLoggingToSameValue() {
        root.set(rootProperty1, "test");
        changeSummary.beginLogging();
        root.set(rootProperty1, "test");
        // no old value for this unmodified property
        assertFalse(changeSummary.isModified(root));
        assertNull(changeSummary.getOldValue(root, rootProperty1));
    }

    // TODO: Is there a difference between default->null and null->null
    public void testUC8GetOldValueAfterUnSetAfterLoggingOn() {
        changeSummary.beginLogging();
        root.unset(rootProperty1);
        // no old value for this unmodified property
        assertFalse(changeSummary.isModified(root));
        assertNull(changeSummary.getOldValue(root, rootProperty1));
    }

    public void testUC9GetOldValueAfterUnSetAfterLoggingOn() {
        root.unset(rootProperty1);
        changeSummary.beginLogging();
        root.unset(rootProperty1);
        // no old value for this unmodified property
        assertFalse(changeSummary.isModified(root));
        assertNull(changeSummary.getOldValue(root, rootProperty1));
    }

    // purpose: verify property.unset() retains oldValue and set(property, null) has no differences
    public void testGetOldValueAfterUnSet() {
        root.set(rootProperty1, "test");
        changeSummary.beginLogging();
        root.set(rootProperty1, null);
        // no old value for this unmodified property        
        assertEquals("test", changeSummary.getOldValue(root, rootProperty1).getValue());
    }

    // purpose: verify set(property, null) retains oldValue
    public void testGetOldValueAfterSettingToNull() {
        root.set(rootProperty1, "test");
        changeSummary.beginLogging();
        root.unset(rootProperty1);
        assertEquals("test", changeSummary.getOldValue(root, rootProperty1).getValue());// no old value for this unmodified property
    }

    // purpose: modified DataObject and nonmodified property, get(DataObject, Property)
    public void testGetOldValueAfterMultipleModifications() {
        root.set(rootProperty1, "test");
        changeSummary.beginLogging();
        root.set(rootProperty1, "test2");
        root.set(rootProperty1, "test3");
        this.assertNull(changeSummary.getOldValue(root, rootProperty));// no old value for this unmodified property
        ChangeSummary.Setting setting = changeSummary.getOldValue(root, rootProperty1);
        this.assertEquals("test", setting.getValue());// specific original old value for this modified property
    }

    // purpose: unmodified DataObject
    public void testGetOldValueReturnSettingWithUnmodifiedDataObject() {
        changeSummary.beginLogging();
        this.assertNull(changeSummary.getOldValue(root, rootProperty));// no old setting for this unmodified dataobject
    }

    // purpose: modified DataObject's modified property
    public void testGetOldValueReturnSettingWithModifiedDataObject() {
        changeSummary.beginLogging();

        DataObject p = dataFactory.create(rootProperty.getType());
        root.set(rootProperty, p);
        Object o = changeSummary.getOldValue(root, rootProperty).getValue();
        Object deepCopy = changeSummary.getDeepCopies().get(containedDataObject);

        this.assertEquals(deepCopy, o);
    }

    // purpose: unmodified DataObject
    public void testGetOldValueReturnListWithUnmodifiedProperty() {
        changeSummary.beginLogging();
        this.assertTrue(0 == changeSummary.getOldValues(root).size());
    }

    // purpose: modified DataObject
    public void testGetOldValueReturnListWithUnmodifiedDataObject() {
        changeSummary.beginLogging();

        DataObject p = dataFactory.create(rootProperty.getType());
        root.set(rootProperty, p);
        Object o = changeSummary.getOldValue(root, rootProperty).getValue();
        List l = changeSummary.getOldValues(root);
        this.assertTrue(1 == l.size());
        Object deepCopy = changeSummary.getDeepCopies().get(containedDataObject);
        this.assertEquals(deepCopy, (SDODataObject)((ChangeSummary.Setting)l.get(0)).getValue());
    }

    // purpose: test an open content property's value can be recorded in old setting list
    public void testGetOldValueFromAnOpenContentProperty() {
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

        rootType.setOpen(true);
        
        DataObject openContentDO = dataFactory.create(propertyType);
        openContentDO.set("name", "openContent");
        openContentDO.set("type", SDOConstants.SDO_STRING);        

        Property openRootProperty = typeHelper.defineOpenContentProperty("someURI", openContentDO);

        root.set(openRootProperty, "openTest");
        changeSummary.beginLogging();
        root.set(openRootProperty, "openTest1");
        ChangeSummary.Setting setting = changeSummary.getOldValue(root, openRootProperty);
        this.assertEquals("openTest", setting.getValue());
    }

    // purpose: after a dataobject detach itself, check the changed dataobject lists
    public void testGetOldValueAfterPerformingDetach() {
        containedDataObject.set("containedProperty", "aaa");
        changeSummary.beginLogging();
        containedDataObject.detach();
        this.assertTrue(changeSummary.isModified(root));
        this.assertTrue(changeSummary.isDeleted(containedDataObject));

        //TODO: is this the right check?
        //List rootOldValues = changeSummary.getOldValues(root);
        //List containedOldValues = changeSummary.getOldValues(containedDataObject);
        //this.assertEquals(1, rootOldValues.size());
        //this.assertEquals(1, containedOldValues.size());
    }

    // purpose: after a dataobject detach itself, check the changed dataobject lists
    public void testGetOldValueAfterPerformingDelete() {
        containedDataObject.set("containedProperty", "aaa");
        changeSummary.beginLogging();
        containedDataObject.delete();
        this.assertTrue(changeSummary.isModified(root));
        this.assertTrue(changeSummary.isDeleted(containedDataObject));
        List rootOldValues = changeSummary.getOldValues(root);
        List containedOldValues = changeSummary.getOldValues(containedDataObject);
        this.assertEquals(1, rootOldValues.size());
        this.assertEquals(1, containedOldValues.size());
    }

    // test open content detach
    public void testGetOldValueAfterModifiedManyTypeProperty() {
        SDOProperty openRootProperty = new SDOProperty(aHelperContext);
        openRootProperty.setName("openContent");
        openRootProperty.setContainment(true);
        openRootProperty.setMany(true);
        DataObject typeDO = defineType("openPropertyTypeUri", "openPropertyType");
        SDOType type = (SDOType)typeHelper.define(typeDO);

        openRootProperty.setType(type);
        List objects = new ArrayList();
        DataObject obj1TypeDO = defineType("obj1Uri", "obj1");
        SDOType obj1Type = (SDOType)typeHelper.define(obj1TypeDO);

        DataObject obj1 = dataFactory.create(obj1Type);
        objects.add(obj1);
        rootType.setOpen(true);
        root.set(openRootProperty, objects);
        changeSummary.beginLogging();
        obj1.detach();
        List changedDataObjects = changeSummary.getChangedDataObjects();
        assertFalse(changedDataObjects.isEmpty());
        assertEquals(changedDataObjects.get(0), root);//expected:<defaultPackage.Obj1Impl@1506dc4> but was:<defaultPackage.RootTypeNameImpl@15663a2>
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryGetOldValueTest" };
        TestRunner.main(arguments);
    }
}
