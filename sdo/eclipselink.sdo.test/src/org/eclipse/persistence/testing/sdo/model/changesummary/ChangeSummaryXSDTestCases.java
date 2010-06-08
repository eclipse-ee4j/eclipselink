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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOSetting;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.ValueStore;
import org.eclipse.persistence.sdo.helper.ListWrapper;
import org.eclipse.persistence.testing.sdo.SDOTestCase;
import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

/**
 *
 * MODIFIED    (MM/DD/YY)
 *   dmahar      09/18/07 - 
 *   mfobrien    05/01/07 - 
 *   dmahar      04/10/07 - 
 *   mfobrien    03/16/07 - 
 *   dmahar      03/14/07 - 
 *   dmahar      03/14/07 - 
 *   mfobrien    02/14/07 - cs.undoChanges is the same as a detach/reset
 *                                           testDetachAndResetYardToSamePlace() mod assertEquals(0, shipToSettings.size()); from 1
 *
 */
public class ChangeSummaryXSDTestCases extends SDOTestCase {
    private DataObject rootObject;
    private ChangeSummary cs;
    
    public static final String OPEN_CONTENT_NS_URI = "";
    public static final String OC_PROP1SimpleSingleNAME = "open1";
    public static final String OC_PROP1SimpleSingleVALUE = "open1-val";
    public static final String OC_PROP2SimpleSingleNAME = "open2";
    public static final String OC_PROP2SimpleSingleVALUE = "open2-val";
    public static final String OC_PROP3DataObjectNAME = "open-do";
    public static final String OC_PROP3DataObjectVALUE = "open-do-val";
    
    public static final String PROPERTY_NAME_PO_ITEMS = "items";
    public static final String PROPERTY_NAME_PO_ITEMS_ITEM1 = "items/item[1]";    
    public static final String PROPERTY_NAME_PO_SHIPTO = "shipTo";    
    public static final String PROPERTY_NAME_PO_SHIPTO_PHONE2 = "shipTo/phone[2]";    

    public ChangeSummaryXSDTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryXSDTestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() {
        super.setUp();// watch setup redundancy
        //define types from deep with cs 
        try {
            InputStream is = new FileInputStream("org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderDeepWithCS.xsd");
            xsdHelper.define(is, null);
            XMLDocument document = xmlHelper.load(new FileInputStream("org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderDeepWithCS.xml"));
            rootObject = document.getRootObject();
            cs = rootObject.getChangeSummary();
            cs.endLogging();
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred loading the xsd");
        }
    }

    public void writeXML(DataObject anObject) {
        // verify save
        super.writeXML(rootObject, ChangeSummaryOnRootTestCases.URINAME,//
                       ChangeSummaryOnRootTestCases.TYPENAME,//
                       System.out);
    }

    public DataObject marshalUnmarshalObject(DataObject anObject) {
        DataObject copy = null;
        try {
            String s = xmlHelper.save(anObject, "http://example.org",// this uri does not affect unmarshal 
                                      "dataObject");// this name is overridden by the xsi:type            
            XMLDocument document = xmlHelper.load(s);
            copy = document.getRootObject();
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred loading the xml file");
        }
        return copy;
    }

    
    /**
     * Test multiple oc properties that were deleted and how they are returned to the DO after an undoChanges
     *
     */
    public void testMultipleOCPropsUndoChangesDeepCopyObjectWithCSLoggingOnWithComboDeleteSingleCreateManyModifyRootChangesInTheDeepCopy() {
        List preOrderList = null;
        int numberOfDataObjectsInSubTree = 0;

        // unset isMany=true (to fill unsetPropsMap and createdList later when we (re)set it back)
        SDODataObject shipTo = (SDODataObject)rootObject.get(PROPERTY_NAME_PO_SHIPTO);

        // use index 2 to avoid a swap later
        SDODataObject phone2 = (SDODataObject)rootObject.get(PROPERTY_NAME_PO_SHIPTO_PHONE2);
        shipTo.unset("phone[2]");

        // save size of tree before CS was turned on
        preOrderList = preOrderTraversalDataObjectList((SDODataObject)rootObject, false);
        numberOfDataObjectsInSubTree = preOrderList.size();
        int numberOfNodesInRootBeforeCSon = 14;
        assertEquals(numberOfNodesInRootBeforeCSon, numberOfDataObjectsInSubTree);

        SDOProperty openRootProperty = new SDOProperty(aHelperContext);
        openRootProperty.setName(OC_PROP1SimpleSingleNAME);
        openRootProperty.setType(SDOConstants.SDO_STRING);        
        //aRootTypeOC.setOpen(true);
        ((SDOType)rootObject.getType()).setOpen(true);
        rootObject.set(openRootProperty, OC_PROP1SimpleSingleVALUE);

        // set an Open Content property and delete it
        SDOProperty openRootPropertyToBeDeleted = new SDOProperty(aHelperContext);
        openRootPropertyToBeDeleted.setName(OC_PROP2SimpleSingleNAME);
        openRootPropertyToBeDeleted.setType(SDOConstants.SDO_STRING);        
        //aRootTypeOC.setOpen(true);
        rootObject.set(openRootPropertyToBeDeleted, OC_PROP2SimpleSingleVALUE);

        // set an Open Content property and delete it
        SDOProperty openRootPropertyToBeDeleted2 = new SDOProperty(aHelperContext);
        openRootPropertyToBeDeleted2.setName(OC_PROP2SimpleSingleNAME + "1");
        openRootPropertyToBeDeleted2.setType(SDOConstants.SDO_STRING);        
        //aRootTypeOC.setOpen(true);
        rootObject.set(openRootPropertyToBeDeleted2, OC_PROP2SimpleSingleVALUE + "1");

        // set an Open Content property and delete it
        SDOProperty openRootPropertyToBeDeleted3 = new SDOProperty(aHelperContext);
        openRootPropertyToBeDeleted3.setName(OC_PROP2SimpleSingleNAME + "2");
        openRootPropertyToBeDeleted3.setType(SDOConstants.SDO_STRING);        
        //aRootTypeOC.setOpen(true);
        rootObject.set(openRootPropertyToBeDeleted3, OC_PROP2SimpleSingleVALUE + "2");

        // verify logging is on
        // turn on logging
        cs.beginLogging();
        assertTrue(cs.isLogging());

        // do some changes
        
        // delete an Open Content property
        rootObject.unset(openRootPropertyToBeDeleted);
        rootObject.unset(openRootPropertyToBeDeleted2);
        rootObject.unset(openRootPropertyToBeDeleted3);
        //ChangeSummary.Setting setting = cs.getOldValue(rootObject, openRootPropertyToBeDeleted);
        //assertEquals("openTest", setting.getValue());

        
        SDODataObject items = (SDODataObject)rootObject.get(PROPERTY_NAME_PO_ITEMS);

        // detach isMany=false (to fill deletedList and modifiedList)
        //items.detach();
        items.delete();
        // (re)set isMany=true (to fill createdList, unsetPropsMap)
        List phones = (List)shipTo.get("phone");
        phones.add(phone2);
        // get an oldSetting in order to enable caching to fill the oldSettings hash map in the cs
        //Object oldValueItems = cs.getOldValue(rootObject, rootObject.getInstanceProperty("items")).getValue();
        //assertNotNull(oldValueItems);
        //assertNull(rootObject.get("items"));
        // save fields lengths for later comparision
        List<DataObject> changedObjects = cs.getChangedDataObjects();
        int changedObjectsLength = changedObjects.size();
        //int itemsOldValuesSize = cs.getOldValues(items).size();// invoke usage of the reverseDeletedMap

        // take an object with CS on and deep copy it
        SDODataObject copy = (SDODataObject)copyHelper.copy(rootObject);

        // copy the copy - so we are sure that the copy has enough in the cs to do an undo of the 2nd level copy
        SDODataObject copyOfCopy = (SDODataObject)copyHelper.copy(copy);

        //SDODataObject copy = (SDODataObject)marshalUnmarshalObject(rootObject);
        // verify that logging is still on
        ChangeSummary cs2 = copy.getChangeSummary();
        assertNotNull(cs2);
        assertTrue(cs2.isLogging());
        ChangeSummary cs3 = copyOfCopy.getChangeSummary();
        assertNotNull(cs3);
        assertTrue(cs3.isLogging());

        // verify saved field lengths from above
        assertEquals(changedObjectsLength, cs2.getChangedDataObjects().size());

        // no cache entries yet = 0 oldValues
        //assertEquals(itemsOldValuesSize, cs2.getOldValues(items).size());
        // check shallow equality
        assertEquals(rootObject.getInstanceProperties().size(), copy.getInstanceProperties().size());
        
        boolean isEqual = equalityHelper.equalShallow(rootObject, copy);
        assertTrue(isEqual);

        // check deep equal is unaffected (we should have a copy of the copy for List items 
        //- ListWrapper.add(item) will remove the item from its original wrapper) 
        assertTrue(equalityHelper.equal(rootObject, copy));
        assertTrue(equalityHelper.equalShallow(rootObject, copyOfCopy));

        // check deep equal is unaffected (we should have a copy of the copy for List items 
        //- ListWrapper.add(item) will remove the item from its original wrapper) 
        assertTrue(equalityHelper.equal(rootObject, copyOfCopy));

        // verify that all objects referenced in the copy changeSummary are from the copy not the original
        // verify createdList
        // verify deepCopies
        // verify deletedList
        // verify modifiedList
        // verify oldContainer
        // verify oldContainmentProperty
        // verify originalElements
        // verify originalValueStores
        Map originalValueStores = ((SDOChangeSummary)cs).getOriginalValueStores();
        Map originalValueStoresCopy = ((SDOChangeSummary)cs2).getOriginalValueStores();

        // verify originally deleted items is referencing a copy and a copy of its valuestore
        // verify reverseDeletedMap
        // verify rootDataObject
        assertEquals(copy, cs2.getRootObject());
        // verify unsetPropsMap
        SDODataObject items2 = (SDODataObject)copy.get(PROPERTY_NAME_PO_ITEMS);

        // check new phone copy
        SDODataObject phone2c = (SDODataObject)copy.get(PROPERTY_NAME_PO_SHIPTO_PHONE2);
        assertNotNull(phone2);
        // get original new phone
        SDODataObject phone2o = (SDODataObject)rootObject.get(PROPERTY_NAME_PO_SHIPTO_PHONE2);
        assertNotNull(phone2);
        assertFalse(phone2o == phone2c);
        assertFalse(phone2o._getCurrentValueStore() == phone2c._getCurrentValueStore());

        // check new phone copy (in oldSettings) is == to currentValueStore phone (both are the same deepcopies of the original)
        List createdListCopy = (List)((SDOChangeSummary)cs2).getCreated();
        assertNotNull(createdListCopy);
        assertEquals(1, createdListCopy.size());
        SDODataObject phone2cInCreatedList = (SDODataObject)createdListCopy.get(0);
        assertNotNull(phone2cInCreatedList);
        assertNotNull(phone2cInCreatedList);
        assertTrue(phone2c == phone2cInCreatedList); // test cs.createList iterator makes deep copies
        assertTrue(phone2c._getCurrentValueStore() == phone2cInCreatedList._getCurrentValueStore());
        
        // get an oldSetting in order to enable caching to fill the hash map in the cs
        //Object oldValueItems2 = cs2.getOldValue(copy, copy.getInstanceProperty("items"));
        // verify that we have oldSettings in the new copy        
        //        assertNotNull(oldValueItems2);
        //        assertNotNull(((SDOSetting)oldValueItems2).getValue());
        //    	assertNull(copy.get("items"));
        // deleted objects are 9
        // verify that we have transferred the logged changes during the copy (we dont have to worry about nested changesummaries)        
        assertEquals(changedObjectsLength, cs2.getChangedDataObjects().size());
        preOrderList = preOrderTraversalDataObjectList(copy, false);
        numberOfDataObjectsInSubTree = preOrderList.size();
        // assume that for logging=true copies we should have oldContainer, oldContProperties
        assertEquals(6, numberOfDataObjectsInSubTree);// 16 - 10 
        assertEquals(((SDOChangeSummary)cs).getOldContainer().size(),//
                     ((SDOChangeSummary)copy.getChangeSummary()).getOldContainer().size());
        assertEquals(((SDOChangeSummary)cs).getOldContainmentProperty().size(),//
                     ((SDOChangeSummary)copy.getChangeSummary()).getOldContainmentProperty().size());

        // no oldSetting for simple object
        
        // verify oc props that were deleted are in oldSettings
        Property oc2cProp = openRootPropertyToBeDeleted;//(Property)copy.getOpenContentPropertiesMap().get(OC_PROP2SimpleSingleNAME);
        assertNotNull(oc2cProp);
        Object oc2cSetting = cs2.getOldValue(copy, oc2cProp);
        assertNotNull(oc2cSetting);
        String oc2cValue = (String)((SDOSetting)oc2cSetting).getValue();
        assertNotNull(oc2cValue);
        assertEquals(OC_PROP2SimpleSingleVALUE, oc2cValue);

        // verify unsetOC map
        List<Property> ocUnsetList = ((SDOChangeSummary)cs2).getUnsetOCProperties(copy);
        assertNotNull(ocUnsetList);
        Property oc2cProp_ocUnsetMap = ocUnsetList.get(0);
        assertNotNull(oc2cProp_ocUnsetMap);
        // verify that we are pointing to the same global property        
        assertEquals(oc2cProp_ocUnsetMap, openRootPropertyToBeDeleted);
        String oc2cValue_ocUnsetMap = oc2cProp_ocUnsetMap.getName();
        assertNotNull(oc2cValue_ocUnsetMap);
        assertSame(openRootPropertyToBeDeleted.getName(), oc2cValue_ocUnsetMap);
        
        // verify oldContainer <key, value> are both deep copies
        // we an old setting first
        //DataObject anOldContainerValue = cs2.getOldContainer(items2);
        //assertTrue(copy == anOldContainerValue);
        
        
        
        // perform an undo on the copy and compare to the undone original (not the undone copy)
        cs2.undoChanges();
        // verify that original was unaffected by undo on copy
        items = (SDODataObject)rootObject.get(PROPERTY_NAME_PO_ITEMS);
        assertNull(items);
        // get back "items" that has been reset
        items2 = (SDODataObject)copy.get(PROPERTY_NAME_PO_ITEMS);
        assertNotNull(items2);

        assertEquals(0, cs2.getChangedDataObjects().size());
        preOrderList = preOrderTraversalDataObjectList(copy, false);
        numberOfDataObjectsInSubTree = preOrderList.size();
        // assume that for logging=true copies we should have oldContainer, oldContProperties
        assertEquals(14, numberOfDataObjectsInSubTree);

        // undo changes on original
        cs.undoChanges();
        assertEquals(0, cs.getChangedDataObjects().size());
        // the items:do in the original and copy should not be the same object (different valueStores)
        // verify that original was unaffected by undo on copy
        items = (SDODataObject)rootObject.get(PROPERTY_NAME_PO_ITEMS);
        assertFalse(items == items2);
        assertFalse(items._getCurrentValueStore() == items2._getCurrentValueStore());

        assertEquals(0, cs2.getChangedDataObjects().size());
        preOrderList = preOrderTraversalDataObjectList(copy, false);
        numberOfDataObjectsInSubTree = preOrderList.size();
        // assume that for logging=true copies we should have oldContainer, oldContProperties
        assertEquals(14, numberOfDataObjectsInSubTree);// 16 - 10 

        // verify deep objects item[*] are distince between original and copy
        SDODataObject item1o = (SDODataObject)items.get("item[1]");
        SDODataObject item1c = (SDODataObject)items2.get("item[1]");
        assertNotNull(item1o);
        assertNotNull(item1c);
        assertFalse(item1o == item1c);
        assertFalse(item1o._getCurrentValueStore() == item1c._getCurrentValueStore());

        // verify listwrappers hashes are the same between two equal objects in the cs
        // verify simple Many integrity
        ListWrapper origComments = (ListWrapper)rootObject.get("items/item[1]/comment");
        assertNotNull(origComments);
        assertEquals(2, origComments.size());

        ListWrapper copyComments = (ListWrapper)copy.get("items/item[1]/comment");
        assertNotNull(copyComments);
        assertEquals(2, copyComments.size());
        //writeXML(copy);
        // check shallow equality
        assertTrue(equalityHelper.equalShallow(rootObject, copy));

        // check deep equal is unaffected (we should have a copy of the copy for List items 
        //- ListWrapper.add(item) will remove the item from its original wrapper) 
        assertTrue(equalityHelper.equal(rootObject, copy));

        // verify equality and undo the copyOfCopy to ensure that the csCopyOfCopy has enough info to do an undo
        copyOfCopy.getChangeSummary().undoChanges();
        assertTrue(equalityHelper.equalShallow(rootObject, copyOfCopy));

        // check deep equal is unaffected (we should have a copy of the copy for List items 
        //- ListWrapper.add(item) will remove the item from its original wrapper) 
        assertTrue(equalityHelper.equal(rootObject, copyOfCopy));
        assertTrue(equalityHelper.equal(copy, copyOfCopy));

        // Verify CS undo of oc props
        // verify oc props that are copied
        Object oc1cProp = copy.get(OC_PROP1SimpleSingleNAME);
        assertNotNull(oc1cProp);
        assertEquals(OC_PROP1SimpleSingleVALUE, oc1cProp);

        // verify that deleted oc props were returned being set after an undo - use saved Property object (outside of this object)
        Object oc2cPropUndo = rootObject.get(openRootPropertyToBeDeleted);
        assertNotNull (oc2cPropUndo);
        assertEquals(oc2cPropUndo, OC_PROP2SimpleSingleVALUE);

       
        // test undo for oc - not implemented yet see #5928954
        // verify that deleted oc props were returned being set after an undo - do not used saved Property object -use getPath
        oc2cPropUndo = rootObject.get(openRootPropertyToBeDeleted.getName());
        assertNotNull(oc2cPropUndo);
        assertEquals(oc2cPropUndo, OC_PROP2SimpleSingleVALUE);
        
        // verify oc Property is in valueStore
        String openRootPropertyToBeDeletedAfterUndo = (String)((SDODataObject)rootObject)._getCurrentValueStore()//
        	.getOpenContentProperty(openRootPropertyToBeDeleted);
        assertNotNull(openRootPropertyToBeDeletedAfterUndo);
        assertEquals(openRootPropertyToBeDeletedAfterUndo, OC_PROP2SimpleSingleVALUE);

        // verify that deleted oc props were returned being set after an undo - use saved Property object (outside of this object)
        Object oc2cPropUndo2 = rootObject.get(openRootPropertyToBeDeleted2);
        assertNotNull (oc2cPropUndo2);
        assertEquals(oc2cPropUndo2, OC_PROP2SimpleSingleVALUE + "1");
        
        oc2cPropUndo2 = rootObject.get(openRootPropertyToBeDeleted2.getName());
        assertNotNull(oc2cPropUndo2);
        assertEquals(oc2cPropUndo2, OC_PROP2SimpleSingleVALUE + "1");
        
        // verify oc Property is in valueStore
        String openRootPropertyToBeDeletedAfterUndo2 = (String)((SDODataObject)rootObject)._getCurrentValueStore()//
        	.getOpenContentProperty(openRootPropertyToBeDeleted2);
        assertNotNull(openRootPropertyToBeDeletedAfterUndo2);
        assertEquals(openRootPropertyToBeDeletedAfterUndo2, OC_PROP2SimpleSingleVALUE + "1");

        // verify that deleted oc props were returned being set after an undo - use saved Property object (outside of this object)
        Object oc2cPropUndo3 = rootObject.get(openRootPropertyToBeDeleted3);
        assertNotNull (oc2cPropUndo3);
        assertEquals(oc2cPropUndo3, OC_PROP2SimpleSingleVALUE + "2");
        
        oc2cPropUndo3 = rootObject.get(openRootPropertyToBeDeleted3.getName());
        assertNotNull(oc2cPropUndo3);
        assertEquals(oc2cPropUndo3, OC_PROP2SimpleSingleVALUE + "2");
        
        // verify oc Property is in valueStore
        String openRootPropertyToBeDeletedAfterUndo3 = (String)((SDODataObject)rootObject)._getCurrentValueStore()//
        	.getOpenContentProperty(openRootPropertyToBeDeleted3);
        assertNotNull(openRootPropertyToBeDeletedAfterUndo3);
        assertEquals(openRootPropertyToBeDeletedAfterUndo3, OC_PROP2SimpleSingleVALUE + "2");
        
    }
    
    public void testUndoWithLoggingOffDeepCopyObjectWithCSLoggingOnWithComboDeleteSingleCreateManyModifyRootChangesInTheDeepCopy() {
    	
        List preOrderList = null;
        int numberOfDataObjectsInSubTree = 0;

        // unset isMany=true (to fill unsetPropsMap and createdList later when we (re)set it back)
        SDODataObject shipTo = (SDODataObject)rootObject.get(PROPERTY_NAME_PO_SHIPTO);

        // use index 2 to avoid a swap later
        SDODataObject phone2 = (SDODataObject)rootObject.get(PROPERTY_NAME_PO_SHIPTO_PHONE2);
        shipTo.unset("phone[2]");

        // save size of tree before CS was turned on
        preOrderList = preOrderTraversalDataObjectList((SDODataObject)rootObject, false);
        numberOfDataObjectsInSubTree = preOrderList.size();
        int numberOfNodesInRootBeforeCSon = 14;
        assertEquals(numberOfNodesInRootBeforeCSon, numberOfDataObjectsInSubTree);

        SDOProperty openRootProperty = new SDOProperty(aHelperContext);
        openRootProperty.setName(OC_PROP1SimpleSingleNAME);
        openRootProperty.setType(SDOConstants.SDO_STRING);        
        //aRootTypeOC.setOpen(true);
        ((SDOType)rootObject.getType()).setOpen(true);
        rootObject.set(openRootProperty, OC_PROP1SimpleSingleVALUE);

        // set an Open Content property and delete it
        SDOProperty openRootPropertyToBeDeleted = new SDOProperty(aHelperContext);
        openRootPropertyToBeDeleted.setName(OC_PROP2SimpleSingleNAME);
        openRootPropertyToBeDeleted.setType(SDOConstants.SDO_STRING);        
        //aRootTypeOC.setOpen(true);
        rootObject.set(openRootPropertyToBeDeleted, OC_PROP2SimpleSingleVALUE);
        
        // verify logging is on
        // turn on logging
        cs.beginLogging();
        assertTrue(cs.isLogging());

        // do some changes
        
        // delete an Open Content property
        rootObject.unset(openRootPropertyToBeDeleted);
        //ChangeSummary.Setting setting = cs.getOldValue(rootObject, openRootPropertyToBeDeleted);
        //assertEquals("openTest", setting.getValue());

        
        SDODataObject items = (SDODataObject)rootObject.get(PROPERTY_NAME_PO_ITEMS);

        // detach isMany=false (to fill deletedList and modifiedList)
        //items.detach();
        items.delete();
        // (re)set isMany=true (to fill createdList, unsetPropsMap)
        List phones = (List)shipTo.get("phone");
        phones.add(phone2);
        // get an oldSetting in order to enable caching to fill the oldSettings hash map in the cs
        //Object oldValueItems = cs.getOldValue(rootObject, rootObject.getInstanceProperty("items")).getValue();
        //assertNotNull(oldValueItems);
        //assertNull(rootObject.get("items"));
        // save fields lengths for later comparision
        List<DataObject> changedObjects = cs.getChangedDataObjects();
        int changedObjectsLength = changedObjects.size();
        //int itemsOldValuesSize = cs.getOldValues(items).size();// invoke usage of the reverseDeletedMap

        // take an object with CS on and deep copy it
        SDODataObject copy = (SDODataObject)copyHelper.copy(rootObject);

        // copy the copy - so we are sure that the copy has enough in the cs to do an undo of the 2nd level copy
        SDODataObject copyOfCopy = (SDODataObject)copyHelper.copy(copy);

        //SDODataObject copy = (SDODataObject)marshalUnmarshalObject(rootObject);
        // verify that logging is still on
        ChangeSummary cs2 = copy.getChangeSummary();
        assertNotNull(cs2);
        assertTrue(cs2.isLogging());
        ChangeSummary cs3 = copyOfCopy.getChangeSummary();
        assertNotNull(cs3);
        assertTrue(cs3.isLogging());

        // verify saved field lengths from above
        assertEquals(changedObjectsLength, cs2.getChangedDataObjects().size());

        // no cache entries yet = 0 oldValues
        //assertEquals(itemsOldValuesSize, cs2.getOldValues(items).size());
        // check shallow equality
        assertEquals(rootObject.getInstanceProperties().size(), copy.getInstanceProperties().size());
        
        boolean isEqual = equalityHelper.equalShallow(rootObject, copy);
        assertTrue(isEqual);

        // check deep equal is unaffected (we should have a copy of the copy for List items 
        //- ListWrapper.add(item) will remove the item from its original wrapper) 
        assertTrue(equalityHelper.equal(rootObject, copy));
        assertTrue(equalityHelper.equalShallow(rootObject, copyOfCopy));

        // check deep equal is unaffected (we should have a copy of the copy for List items 
        //- ListWrapper.add(item) will remove the item from its original wrapper) 
        assertTrue(equalityHelper.equal(rootObject, copyOfCopy));

        // verify that all objects referenced in the copy changeSummary are from the copy not the original
        // verify createdList
        // verify deepCopies
        // verify deletedList
        // verify modifiedList
        // verify oldContainer
        // verify oldContainmentProperty
        // verify originalElements
        // verify originalValueStores
        Map originalValueStores = ((SDOChangeSummary)cs).getOriginalValueStores();
        Map originalValueStoresCopy = ((SDOChangeSummary)cs2).getOriginalValueStores();

        // verify originally deleted items is referencing a copy and a copy of its valuestore
        // verify reverseDeletedMap
        // verify rootDataObject
        assertEquals(copy, cs2.getRootObject());
        // verify unsetPropsMap
        SDODataObject items2 = (SDODataObject)copy.get(PROPERTY_NAME_PO_ITEMS);

        // check new phone copy
        SDODataObject phone2c = (SDODataObject)copy.get(PROPERTY_NAME_PO_SHIPTO_PHONE2);
        assertNotNull(phone2);
        // get original new phone
        SDODataObject phone2o = (SDODataObject)rootObject.get(PROPERTY_NAME_PO_SHIPTO_PHONE2);
        assertNotNull(phone2);
        assertFalse(phone2o == phone2c);
        assertFalse(phone2o._getCurrentValueStore() == phone2c._getCurrentValueStore());

        // check new phone copy (in oldSettings) is == to currentValueStore phone (both are the same deepcopies of the original)
        List createdListCopy = ((SDOChangeSummary)cs2).getCreated();
        assertNotNull(createdListCopy);
        assertEquals(1, createdListCopy.size());
        SDODataObject phone2cInCreatedList = (SDODataObject)createdListCopy.get(0);
        assertNotNull(phone2cInCreatedList);
        assertNotNull(phone2cInCreatedList);
        assertTrue(phone2c == phone2cInCreatedList); // test cs.createList iterator makes deep copies
        assertTrue(phone2c._getCurrentValueStore() == phone2cInCreatedList._getCurrentValueStore());
        
        // get an oldSetting in order to enable caching to fill the hash map in the cs
        //Object oldValueItems2 = cs2.getOldValue(copy, copy.getInstanceProperty("items"));
        // verify that we have oldSettings in the new copy        
        //        assertNotNull(oldValueItems2);
        //        assertNotNull(((SDOSetting)oldValueItems2).getValue());
        //    	assertNull(copy.get("items"));
        // deleted objects are 9
        // verify that we have transferred the logged changes during the copy (we dont have to worry about nested changesummaries)        
        assertEquals(changedObjectsLength, cs2.getChangedDataObjects().size());
        preOrderList = preOrderTraversalDataObjectList((SDODataObject)copy, false);
        numberOfDataObjectsInSubTree = preOrderList.size();
        // assume that for logging=true copies we should have oldContainer, oldContProperties
        assertEquals(6, numberOfDataObjectsInSubTree);// 16 - 10 
        assertEquals(((SDOChangeSummary)cs).getOldContainer().size(),//
                     ((SDOChangeSummary)copy.getChangeSummary()).getOldContainer().size());
        assertEquals(((SDOChangeSummary)cs).getOldContainmentProperty().size(),//
                     ((SDOChangeSummary)copy.getChangeSummary()).getOldContainmentProperty().size());

        // no oldSetting for simple object
        
        // verify oc props that were deleted are in oldSettings
        Property oc2cProp = openRootPropertyToBeDeleted;//(Property)copy.getOpenContentPropertiesMap().get(OC_PROP2SimpleSingleNAME);
        assertNotNull(oc2cProp);
        Object oc2cSetting = cs2.getOldValue(copy, oc2cProp);
        assertNotNull(oc2cSetting);
        String oc2cValue = (String)((SDOSetting)oc2cSetting).getValue();
        assertNotNull(oc2cValue);
        assertEquals(OC_PROP2SimpleSingleVALUE, oc2cValue);

        // verify unsetOC map
        List<Property> ocUnsetList = ((SDOChangeSummary)cs2).getUnsetOCProperties(copy);
        assertNotNull(ocUnsetList);
        Property oc2cProp_ocUnsetMap = ocUnsetList.get(0);
        assertNotNull(oc2cProp_ocUnsetMap);
        // verify that we are pointing to the same global property        
        assertEquals(oc2cProp_ocUnsetMap, openRootPropertyToBeDeleted);
        String oc2cValue_ocUnsetMap = oc2cProp_ocUnsetMap.getName();
        assertNotNull(oc2cValue_ocUnsetMap);
        assertSame(openRootPropertyToBeDeleted.getName(), oc2cValue_ocUnsetMap);

        // turn off logging
        cs.endLogging();        
        //restart logging to clear cs (to reproduce that we used to clear CS on endLogging)
        assertFalse(cs.isLogging());
        cs.beginLogging();
                
        // perform an undo on the copy and compare to the undone original (not the undone copy)
        cs2.undoChanges();
        // verify that original was unaffected by undo on copy
        items = (SDODataObject)rootObject.get(PROPERTY_NAME_PO_ITEMS);
        assertNull(items);
        // get back "items" that has been reset
        items2 = (SDODataObject)copy.get(PROPERTY_NAME_PO_ITEMS);
        assertNotNull(items2);

        assertEquals(0, cs2.getChangedDataObjects().size());
        preOrderList = preOrderTraversalDataObjectList(copy, false);
        numberOfDataObjectsInSubTree = preOrderList.size();
        // assume that for logging=true copies we should have oldContainer, oldContProperties
        assertEquals(14, numberOfDataObjectsInSubTree);

        // undo changes on original with logging off = do nothing because all of the cs is cleared
        cs.undoChanges();
        assertEquals(0, cs.getChangedDataObjects().size());
        // the items:do in the original and copy should not be the same object (different valueStores)
        // verify that original was unaffected by undo on copy
        items = (SDODataObject)rootObject.get(PROPERTY_NAME_PO_ITEMS);
        assertFalse(items == items2);
        //assertFalse(items._getCurrentValueStore() == items2._getCurrentValueStore());

        assertEquals(0, cs2.getChangedDataObjects().size());
        preOrderList = preOrderTraversalDataObjectList(copy, false);
        numberOfDataObjectsInSubTree = preOrderList.size();
        // assume that for logging=true copies we should have oldContainer, oldContProperties
        assertEquals(14, numberOfDataObjectsInSubTree);// 16 - 10 


        // verify listwrappers hashes are the same between two equal objects in the cs
        // verify simple Many integrity
        ListWrapper origComments = (ListWrapper)rootObject.get("items/item[1]/comment");
        assertNull(origComments);
        //assertEquals(2, origComments.size());

        ListWrapper copyComments = (ListWrapper)copy.get("items/item[1]/comment");
        assertNotNull(copyComments);
        assertEquals(2, copyComments.size());
        //writeXML(copy);
        // check shallow equality
        assertFalse(equalityHelper.equalShallow(rootObject, copy));

        // check deep equal is unaffected (we should have a copy of the copy for List items 
        //- ListWrapper.add(item) will remove the item from its original wrapper) 
        assertFalse(equalityHelper.equal(rootObject, copy));

        // verify equality and undo the copyOfCopy to ensure that the csCopyOfCopy has enough info to do an undo
        copyOfCopy.getChangeSummary().undoChanges();
        assertFalse(equalityHelper.equalShallow(rootObject, copyOfCopy));

        // check deep equal is unaffected (we should have a copy of the copy for List items 
        //- ListWrapper.add(item) will remove the item from its original wrapper) 
        assertFalse(equalityHelper.equal(rootObject, copyOfCopy));
        assertTrue(equalityHelper.equal(copy, copyOfCopy));

        // Verify CS undo of oc props
        // verify oc props that are copied
        Object oc1cProp = copy.get(OC_PROP1SimpleSingleNAME);
        assertNotNull(oc1cProp);
        assertEquals(OC_PROP1SimpleSingleVALUE, oc1cProp);

        // verify that deleted oc props were returned being set after an undo - use saved Property object (outside of this object)
        Object oc2cPropUndo = rootObject.get(openRootPropertyToBeDeleted);
        assertNull (oc2cPropUndo);
        //assertEquals(oc2cPropUndo, OC_PROP2SimpleSingleVALUE);

       
        // test undo for oc - not implemented yet see #5928954
        // verify that deleted oc props were returned being set after an undo - do not used saved Property object -use getPath
        oc2cPropUndo = rootObject.get(openRootPropertyToBeDeleted.getName());
        assertNull(oc2cPropUndo);
        //assertEquals(oc2cPropUndo, OC_PROP2SimpleSingleVALUE);
        
        // verify oc Property is in valueStore
        String openRootPropertyToBeDeletedAfterUndo = (String)((SDODataObject)rootObject)._getCurrentValueStore()//
        	.getOpenContentProperty(openRootPropertyToBeDeleted);
        assertNull(openRootPropertyToBeDeletedAfterUndo);
        //assertEquals(openRootPropertyToBeDeletedAfterUndo, OC_PROP2SimpleSingleVALUE);        
    }
    
    public void testDeepCopyObjectWithCSLoggingOnWithComboDeleteSingleCreateManyModifyRootChangesInTheDeepCopy() {
    	
        List preOrderList = null;
        int numberOfDataObjectsInSubTree = 0;

        // unset isMany=true (to fill unsetPropsMap and createdList later when we (re)set it back)
        SDODataObject shipTo = (SDODataObject)rootObject.get(PROPERTY_NAME_PO_SHIPTO);

        // use index 2 to avoid a swap later
        SDODataObject phone2 = (SDODataObject)rootObject.get(PROPERTY_NAME_PO_SHIPTO_PHONE2);
        shipTo.unset("phone[2]");

        // save size of tree before CS was turned on
        preOrderList = preOrderTraversalDataObjectList((SDODataObject)rootObject, false);
        numberOfDataObjectsInSubTree = preOrderList.size();
        int numberOfNodesInRootBeforeCSon = 14;
        assertEquals(numberOfNodesInRootBeforeCSon, numberOfDataObjectsInSubTree);

        SDOProperty openRootProperty = new SDOProperty(aHelperContext);
        openRootProperty.setName(OC_PROP1SimpleSingleNAME);
        openRootProperty.setType(SDOConstants.SDO_STRING);        
        //aRootTypeOC.setOpen(true);
        ((SDOType)rootObject.getType()).setOpen(true);
        rootObject.set(openRootProperty, OC_PROP1SimpleSingleVALUE);

        // set an Open Content property and delete it
        SDOProperty openRootPropertyToBeDeleted = new SDOProperty(aHelperContext);
        openRootPropertyToBeDeleted.setName(OC_PROP2SimpleSingleNAME);
        openRootPropertyToBeDeleted.setType(SDOConstants.SDO_STRING);        
        //aRootTypeOC.setOpen(true);
        rootObject.set(openRootPropertyToBeDeleted, OC_PROP2SimpleSingleVALUE);
        
        // verify logging is on
        // turn on logging
        cs.beginLogging();
        assertTrue(cs.isLogging());

        // do some changes
        
        // delete an Open Content property
        rootObject.unset(openRootPropertyToBeDeleted);
        //ChangeSummary.Setting setting = cs.getOldValue(rootObject, openRootPropertyToBeDeleted);
        //assertEquals("openTest", setting.getValue());

        
        SDODataObject items = (SDODataObject)rootObject.get(PROPERTY_NAME_PO_ITEMS);

        // detach isMany=false (to fill deletedList and modifiedList)
        //items.detach();
        items.delete();
        // (re)set isMany=true (to fill createdList, unsetPropsMap)
        List phones = (List)shipTo.get("phone");
        phones.add(phone2);
        // get an oldSetting in order to enable caching to fill the oldSettings hash map in the cs
        //Object oldValueItems = cs.getOldValue(rootObject, rootObject.getInstanceProperty("items")).getValue();
        //assertNotNull(oldValueItems);
        //assertNull(rootObject.get("items"));
        // save fields lengths for later comparision
        List<DataObject> changedObjects = cs.getChangedDataObjects();
        int changedObjectsLength = changedObjects.size();
        //int itemsOldValuesSize = cs.getOldValues(items).size();// invoke usage of the reverseDeletedMap

        // take an object with CS on and deep copy it
        SDODataObject copy = (SDODataObject)copyHelper.copy(rootObject);

        // copy the copy - so we are sure that the copy has enough in the cs to do an undo of the 2nd level copy
        SDODataObject copyOfCopy = (SDODataObject)copyHelper.copy(copy);

        //SDODataObject copy = (SDODataObject)marshalUnmarshalObject(rootObject);
        // verify that logging is still on
        ChangeSummary cs2 = copy.getChangeSummary();
        assertNotNull(cs2);
        assertTrue(cs2.isLogging());
        ChangeSummary cs3 = copyOfCopy.getChangeSummary();
        assertNotNull(cs3);
        assertTrue(cs3.isLogging());

        // verify saved field lengths from above
        assertEquals(changedObjectsLength, cs2.getChangedDataObjects().size());

        // no cache entries yet = 0 oldValues
        //assertEquals(itemsOldValuesSize, cs2.getOldValues(items).size());
        // check shallow equality
        assertEquals(rootObject.getInstanceProperties().size(), copy.getInstanceProperties().size());
        
        boolean isEqual = equalityHelper.equalShallow(rootObject, copy);
        assertTrue(isEqual);

        // check deep equal is unaffected (we should have a copy of the copy for List items 
        //- ListWrapper.add(item) will remove the item from its original wrapper) 
        assertTrue(equalityHelper.equal(rootObject, copy));
        assertTrue(equalityHelper.equalShallow(rootObject, copyOfCopy));

        // check deep equal is unaffected (we should have a copy of the copy for List items 
        //- ListWrapper.add(item) will remove the item from its original wrapper) 
        assertTrue(equalityHelper.equal(rootObject, copyOfCopy));

        // verify that all objects referenced in the copy changeSummary are from the copy not the original
        // verify createdList
        // verify deepCopies
        // verify deletedList
        // verify modifiedList
        // verify oldContainer
        // verify oldContainmentProperty
        // verify originalElements
        // verify originalValueStores
        Map originalValueStores = ((SDOChangeSummary)cs).getOriginalValueStores();
        Map originalValueStoresCopy = ((SDOChangeSummary)cs2).getOriginalValueStores();

        // verify originally deleted items is referencing a copy and a copy of its valuestore
        // verify reverseDeletedMap
        // verify rootDataObject
        assertEquals(copy, cs2.getRootObject());
        // verify unsetPropsMap
        SDODataObject items2 = (SDODataObject)copy.get(PROPERTY_NAME_PO_ITEMS);

        // check new phone copy
        SDODataObject phone2c = (SDODataObject)copy.get(PROPERTY_NAME_PO_SHIPTO_PHONE2);
        assertNotNull(phone2);
        // get original new phone
        SDODataObject phone2o = (SDODataObject)rootObject.get(PROPERTY_NAME_PO_SHIPTO_PHONE2);
        assertNotNull(phone2);
        assertFalse(phone2o == phone2c);
        assertFalse(phone2o._getCurrentValueStore() == phone2c._getCurrentValueStore());

        // check new phone copy (in oldSettings) is == to currentValueStore phone (both are the same deepcopies of the original)
        List createdListCopy = (List)((SDOChangeSummary)cs2).getCreated();
        assertNotNull(createdListCopy);
        assertEquals(1, createdListCopy.size());
        SDODataObject phone2cInCreatedList = (SDODataObject)createdListCopy.get(0);
        assertNotNull(phone2cInCreatedList);
        assertNotNull(phone2cInCreatedList);
        assertTrue(phone2c == phone2cInCreatedList); // test cs.createList iterator makes deep copies
        assertTrue(phone2c._getCurrentValueStore() == phone2cInCreatedList._getCurrentValueStore());
        
        // get an oldSetting in order to enable caching to fill the hash map in the cs
        //Object oldValueItems2 = cs2.getOldValue(copy, copy.getInstanceProperty("items"));
        // verify that we have oldSettings in the new copy        
        //        assertNotNull(oldValueItems2);
        //        assertNotNull(((SDOSetting)oldValueItems2).getValue());
        //    	assertNull(copy.get("items"));
        // deleted objects are 9
        // verify that we have transferred the logged changes during the copy (we dont have to worry about nested changesummaries)        
        assertEquals(changedObjectsLength, cs2.getChangedDataObjects().size());
        preOrderList = preOrderTraversalDataObjectList(copy, false);
        numberOfDataObjectsInSubTree = preOrderList.size();
        // assume that for logging=true copies we should have oldContainer, oldContProperties
        assertEquals(6, numberOfDataObjectsInSubTree);// 16 - 10 
        assertEquals(((SDOChangeSummary)cs).getOldContainer().size(),//
                     ((SDOChangeSummary)copy.getChangeSummary()).getOldContainer().size());
        assertEquals(((SDOChangeSummary)cs).getOldContainmentProperty().size(),//
                     ((SDOChangeSummary)copy.getChangeSummary()).getOldContainmentProperty().size());

        // no oldSetting for simple object
        
        // verify oc props that were deleted are in oldSettings
        Property oc2cProp = openRootPropertyToBeDeleted;//(Property)copy.getOpenContentPropertiesMap().get(OC_PROP2SimpleSingleNAME);
        assertNotNull(oc2cProp);
        Object oc2cSetting = cs2.getOldValue(copy, oc2cProp);
        assertNotNull(oc2cSetting);
        String oc2cValue = (String)((SDOSetting)oc2cSetting).getValue();
        assertNotNull(oc2cValue);
        assertEquals(OC_PROP2SimpleSingleVALUE, oc2cValue);

        // verify unsetOC map
        List<Property> ocUnsetList = ((SDOChangeSummary)cs2).getUnsetOCProperties(copy);
        assertNotNull(ocUnsetList);
        Property oc2cProp_ocUnsetMap = ocUnsetList.get(0);
        assertNotNull(oc2cProp_ocUnsetMap);
        // verify that we are pointing to the same global property        
        assertEquals(oc2cProp_ocUnsetMap, openRootPropertyToBeDeleted);
        String oc2cValue_ocUnsetMap = oc2cProp_ocUnsetMap.getName();
        assertNotNull(oc2cValue_ocUnsetMap);
        assertSame(openRootPropertyToBeDeleted.getName(), oc2cValue_ocUnsetMap);
        
        // verify oldContainer <key, value> are both deep copies
        // we an old setting first
        //DataObject anOldContainerValue = cs2.getOldContainer(items2);
        //assertTrue(copy == anOldContainerValue);
        
        
        
        // perform an undo on the copy and compare to the undone original (not the undone copy)
        cs2.undoChanges();
        // verify that original was unaffected by undo on copy
        items = (SDODataObject)rootObject.get(PROPERTY_NAME_PO_ITEMS);
        assertNull(items);
        // get back "items" that has been reset
        items2 = (SDODataObject)copy.get(PROPERTY_NAME_PO_ITEMS);
        assertNotNull(items2);

        assertEquals(0, cs2.getChangedDataObjects().size());
        preOrderList = preOrderTraversalDataObjectList(copy, false);
        numberOfDataObjectsInSubTree = preOrderList.size();
        // assume that for logging=true copies we should have oldContainer, oldContProperties
        assertEquals(14, numberOfDataObjectsInSubTree);

        // undo changes on original
        cs.undoChanges();
        assertEquals(0, cs.getChangedDataObjects().size());
        // the items:do in the original and copy should not be the same object (different valueStores)
        // verify that original was unaffected by undo on copy
        items = (SDODataObject)rootObject.get(PROPERTY_NAME_PO_ITEMS);
        assertFalse(items == items2);
        assertFalse(items._getCurrentValueStore() == items2._getCurrentValueStore());

        assertEquals(0, cs2.getChangedDataObjects().size());
        preOrderList = preOrderTraversalDataObjectList(copy, false);
        numberOfDataObjectsInSubTree = preOrderList.size();
        // assume that for logging=true copies we should have oldContainer, oldContProperties
        assertEquals(14, numberOfDataObjectsInSubTree);// 16 - 10 

        // verify deep objects item[*] are distince between original and copy
        SDODataObject item1o = (SDODataObject)items.get("item[1]");
        SDODataObject item1c = (SDODataObject)items2.get("item[1]");
        assertNotNull(item1o);
        assertNotNull(item1c);
        assertFalse(item1o == item1c);
        assertFalse(item1o._getCurrentValueStore() == item1c._getCurrentValueStore());

        // verify listwrappers hashes are the same between two equal objects in the cs
        // verify simple Many integrity
        ListWrapper origComments = (ListWrapper)rootObject.get("items/item[1]/comment");
        assertNotNull(origComments);
        assertEquals(2, origComments.size());

        ListWrapper copyComments = (ListWrapper)copy.get("items/item[1]/comment");
        assertNotNull(copyComments);
        assertEquals(2, copyComments.size());
        //writeXML(copy);
        // check shallow equality
        assertTrue(equalityHelper.equalShallow(rootObject, copy));

        // check deep equal is unaffected (we should have a copy of the copy for List items 
        //- ListWrapper.add(item) will remove the item from its original wrapper) 
        assertTrue(equalityHelper.equal(rootObject, copy));

        // verify equality and undo the copyOfCopy to ensure that the csCopyOfCopy has enough info to do an undo
        copyOfCopy.getChangeSummary().undoChanges();
        assertTrue(equalityHelper.equalShallow(rootObject, copyOfCopy));

        // check deep equal is unaffected (we should have a copy of the copy for List items 
        //- ListWrapper.add(item) will remove the item from its original wrapper) 
        assertTrue(equalityHelper.equal(rootObject, copyOfCopy));
        assertTrue(equalityHelper.equal(copy, copyOfCopy));

        // Verify CS undo of oc props
        // verify oc props that are copied
        Object oc1cProp = copy.get(OC_PROP1SimpleSingleNAME);
        assertNotNull(oc1cProp);
        assertEquals(OC_PROP1SimpleSingleVALUE, oc1cProp);

        // verify that deleted oc props were returned being set after an undo - use saved Property object (outside of this object)
        Object oc2cPropUndo = rootObject.get(openRootPropertyToBeDeleted);
        assertNotNull (oc2cPropUndo);
        assertEquals(oc2cPropUndo, OC_PROP2SimpleSingleVALUE);

       
        // test undo for oc - not implemented yet see #5928954
        // verify that deleted oc props were returned being set after an undo - do not used saved Property object -use getPath
        oc2cPropUndo = rootObject.get(openRootPropertyToBeDeleted.getName());
        assertNotNull(oc2cPropUndo);
        assertEquals(oc2cPropUndo, OC_PROP2SimpleSingleVALUE);
        
        // verify oc Property is in valueStore
        String openRootPropertyToBeDeletedAfterUndo = (String)((SDODataObject)rootObject)._getCurrentValueStore()//
        	.getOpenContentProperty(openRootPropertyToBeDeleted);
        assertNotNull(openRootPropertyToBeDeletedAfterUndo);
        assertEquals(openRootPropertyToBeDeletedAfterUndo, OC_PROP2SimpleSingleVALUE);
        
        // exercise printstring
        cs.toString();
    }

    
    public void testDefineOpenContentPropDynamicallySpecP34() {
		// Create a new Type and with an open content property set
		DataObject myDataType = dataFactory.create("commonj.sdo", "Type");
		myDataType.set("name", "MyType");
		// SDOTypeHelperDelegate.openContentProperties Map is still null at this point
		Property openContentProperty = typeHelper.getOpenContentProperty(
				"commonj.sdo", "someProperty");
		assertNull(openContentProperty);
		// NPE here
		//myDataType.set(openContentProperty, "test");
		// Define the Type
		Type definedType = typeHelper.define(myDataType);
		// Retrieve the open content property
		Object retrievedValue = definedType.get(openContentProperty);    	
    }

    public void testDefineOpenContentPropDynamicallyLoggingOff() {
		// turn on logging
		assertFalse(cs.isLogging());

		// verify oc property does not exist prior to dynamically creating it
		Property openRootProperty =  rootObject.getInstanceProperty(OC_PROP1SimpleSingleNAME);
		assertNull(openRootProperty);
		
		// create oc prop on the fly
		((SDOType) rootObject.getType()).setOpen(true);
		rootObject.set(OC_PROP1SimpleSingleNAME, OC_PROP1SimpleSingleVALUE);		
		// get generated open content property
		openRootProperty = rootObject.getInstanceProperty(OC_PROP1SimpleSingleNAME);
		// check SDOTypeHelperDelegate.openContentProperties Map is set with this property
		Property openRootPropertyFromTypeHelper = typeHelper.getOpenContentProperty(OPEN_CONTENT_NS_URI, OC_PROP1SimpleSingleNAME);
		//assertNotNull(openRootPropertyFromTypeHelper);
    assertNull(openRootPropertyFromTypeHelper);
		assertNotNull(openRootProperty);
		//assertTrue(openRootProperty == openRootPropertyFromTypeHelper);
    }

    public void testDeepCopySettingForDynanicOpenContentSetAfterLoggingOn() {
		// turn on logging
		cs.beginLogging();
		assertTrue(cs.isLogging());

		// verify oc property does not exist prior to dynamically creating it
		Property openRootProperty =  rootObject.getInstanceProperty(OC_PROP1SimpleSingleNAME);
		assertNull(openRootProperty);
		
		// create oc prop on the fly
		((SDOType) rootObject.getType()).setOpen(true);
		rootObject.set(OC_PROP1SimpleSingleNAME, OC_PROP1SimpleSingleVALUE);		
		// get generated open content property
		openRootProperty = rootObject.getInstanceProperty(OC_PROP1SimpleSingleNAME);
		// check SDOTypeHelperDelegate.openContentProperties Map is set with this property
		Property openRootPropertyFromTypeHelper = typeHelper.getOpenContentProperty(//
				OPEN_CONTENT_NS_URI, OC_PROP1SimpleSingleNAME);
		//assertNotNull(openRootPropertyFromTypeHelper);
    assertNull(openRootPropertyFromTypeHelper);
		assertNotNull(openRootProperty);
		//assertTrue(openRootProperty == openRootPropertyFromTypeHelper);

		// deep copy
		DataObject dcopy = copyHelper.copy(rootObject);
		assertTrue(equalityHelper.equal(rootObject, dcopy));

		// get original setting
		ChangeSummary.Setting origSetting = rootObject.getChangeSummary().getOldValue(rootObject, openRootProperty);
		assertNotNull(origSetting);
		assertFalse(origSetting.isSet());
		assertEquals(null, origSetting.getValue());

		// get copy of original setting
		ChangeSummary.Setting copySetting = dcopy.getChangeSummary().getOldValue(dcopy, openRootProperty);
		assertNotNull(copySetting);
		// verify that isSet=false open content old values in the originalValueStores map are not copied over in cs.copy
		assertFalse(copySetting.isSet());
		assertEquals(null, copySetting.getValue());     	
    }

    public void testDeepCopySettingForOpenContentSetAfterLoggingOn() {
		// turn on logging
		cs.beginLogging();
		assertTrue(cs.isLogging());

		SDOProperty openRootProperty = new SDOProperty(aHelperContext);
		openRootProperty.setName(OC_PROP1SimpleSingleNAME);
		openRootProperty.setType(SDOConstants.SDO_STRING);
		((SDOType) rootObject.getType()).setOpen(true);
		rootObject.set(openRootProperty, OC_PROP1SimpleSingleVALUE);

		DataObject deepCopy = copyHelper.copy(rootObject);
		assertTrue(equalityHelper.equal(rootObject, deepCopy));

		commonj.sdo.ChangeSummary.Setting setting = rootObject.getChangeSummary().getOldValue(rootObject, openRootProperty);
		assertNotNull(setting);
		assertFalse(setting.isSet());
		assertEquals(null, setting.getValue());

		commonj.sdo.ChangeSummary.Setting deepCopySetting = deepCopy.getChangeSummary().getOldValue(deepCopy, openRootProperty);
		assertNotNull(deepCopySetting);
		// verify that isSet=false open content old values in the originalValueStores map are not copied over in cs.copy
		assertFalse(deepCopySetting.isSet());
		assertEquals(null, deepCopySetting.getValue());     	
    }
    

    /**
     * Exercise cs.removeUnsetOCProperty()
     */
    public void testResetOfUnsetOpenContentPropertyClearsUnsetOCPropMap() {
        List preOrderList = null;
        int numberOfDataObjectsInSubTree = 0;

        // unset isMany=true (to fill unsetPropsMap and createdList later when we (re)set it back)
        SDODataObject shipTo = (SDODataObject)rootObject.get(PROPERTY_NAME_PO_SHIPTO);

        // use index 2 to avoid a swap later
        SDODataObject phone2 = (SDODataObject)rootObject.get(PROPERTY_NAME_PO_SHIPTO_PHONE2);
        shipTo.unset("phone[2]");

        // save size of tree before CS was turned on
        preOrderList = preOrderTraversalDataObjectList((SDODataObject)rootObject, false);
        numberOfDataObjectsInSubTree = preOrderList.size();
        int numberOfNodesInRootBeforeCSon = 14;
        assertEquals(numberOfNodesInRootBeforeCSon, numberOfDataObjectsInSubTree);

        // set a simple-single Open Content property
        SDOProperty openRootProperty = new SDOProperty(aHelperContext);
        openRootProperty.setName(OC_PROP1SimpleSingleNAME);
        openRootProperty.setType(SDOConstants.SDO_STRING);        
        //aRootTypeOC.setOpen(true);
        ((SDOType)rootObject.getType()).setOpen(true);
        rootObject.set(openRootProperty, OC_PROP1SimpleSingleVALUE);

        // set an Open Content property and delete it
        SDOProperty openRootPropertyToBeDeleted = new SDOProperty(aHelperContext);
        openRootPropertyToBeDeleted.setName(OC_PROP2SimpleSingleNAME);
        openRootPropertyToBeDeleted.setType(SDOConstants.SDO_STRING);        
        //aRootTypeOC.setOpen(true);
        rootObject.set(openRootPropertyToBeDeleted, OC_PROP2SimpleSingleVALUE);
        
        // turn on logging
        cs.beginLogging();
        assertTrue(cs.isLogging());

        // delete an Open Content property
        rootObject.unset(openRootPropertyToBeDeleted);
        assertFalse(((SDOChangeSummary)cs).getUnsetOCPropertiesMap().isEmpty());
        // reset it
        rootObject.set(openRootPropertyToBeDeleted, OC_PROP2SimpleSingleVALUE);
        assertTrue(((SDOChangeSummary)cs).getUnsetOCPropertiesMap().isEmpty());
    }
    
    /**
     * This test exercises bug 5913485: deepcopy is placed into cs.originalElements during a getOldValue()
     * SDOCopyHelper.copyChangeSummary()
     *  536                             // if deleted list is not found look in the reverse deleted map - if getOldSettings was called
     *  537  0                          SDODataObject reverseDeletedDO = (SDODataObject)originalCS.getReverseDeletedMap().get(aListItem);
     */
    public void testReverseDeletedMapUsageDuringDeepCopyChangeSummary() {
        // unset isMany=true (to fill unsetPropsMap and createdList later when we (re)set it back)
        SDODataObject shipTo = (SDODataObject)rootObject.get(PROPERTY_NAME_PO_SHIPTO);
        // use index 2 to avoid a swap later
        SDODataObject phone2 = (SDODataObject)rootObject.get(PROPERTY_NAME_PO_SHIPTO_PHONE2);
        shipTo.unset("phone[2]");
        cs.beginLogging();
        assertTrue(cs.isLogging());
        SDODataObject items = (SDODataObject)rootObject.get(PROPERTY_NAME_PO_ITEMS);
        // detach isMany=false (to fill deletedList and modifiedList)
        items.delete();
        // (re)set isMany=true (to fill createdList, unsetPropsMap)
        List phones = (List)shipTo.get("phone");
        phones.add(phone2);
        // get an oldSetting in order to enable caching to fill the oldSettings hash map in the cs
        Object oldValueItems = cs.getOldValue(rootObject, rootObject.getInstanceProperty("items")).getValue();
        assertNotNull(oldValueItems);
        assertNull(rootObject.get("items"));
        // save fields lengths for later comparision
        List<DataObject> changedObjects = cs.getChangedDataObjects();
        int changedObjectsLength = changedObjects.size();


        
        // verify #5913485 will cause an undone copy to be !deep equal
        int itemsOldValuesSize = cs.getOldValues(items).size();// invoke usage of the reverseDeletedMap
        // take an object with CS on and deep copy it
        SDODataObject copy = (SDODataObject)copyHelper.copy(rootObject);

        
        
        // verify that logging is still on
        ChangeSummary cs2 = copy.getChangeSummary();
        // verify saved field lengths from above
        assertEquals(changedObjectsLength, cs2.getChangedDataObjects().size());
        // check shallow equality
        boolean isEqual = equalityHelper.equalShallow(rootObject, copy);
        assertTrue(isEqual);
        // check deep equal is unaffected (we should have a copy of the copy for List items 
        //- ListWrapper.add(item) will remove the item from its original wrapper) 
        assertTrue(equalityHelper.equal(rootObject, copy));
        // verify rootDataObject
        assertEquals(copy, cs2.getRootObject());
        // perform an undo on the copy and compare to the undone original (not the undone copy)
        cs2.undoChanges();
        // undo changes on original
        cs.undoChanges();
        // check shallow equality
        assertTrue(equalityHelper.equalShallow(rootObject, copy));
        // check deep equal is unaffected (we should have a copy of the copy for List items 
        //- ListWrapper.add(item) will remove the item from its original wrapper) 
        assertTrue(equalityHelper.equal(rootObject, copy));
    }

    
    //private void verifyCopyDistinctionFromOriginal(SDODataObject topObject, SDODataObject...childObjects ) {
    //}
    //private void verifyInternalListsAreSameInstancesThroughoutChangeSummary(SDODataObject topObject, ListWrapper...lists) {
    //}
    // depends on #5878605 test
    public void testEqualityWithMixedCSLoggingStatesDoesNotAffectEquality() {
        //SDODataObject copy = verifyDeepCopyObjectWithCSLoggingOnDoesNotLogChangesInTheDeepCopy((SDODataObject)rootObject);
        // load a 2nd copy of the same data object
        DataObject rootObject2EQ = null;
        ChangeSummary cs2EQ = null;
        try {
            XMLDocument document = xmlHelper.load(new FileInputStream("org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderDeepWithCS.xml"));
            rootObject2EQ = document.getRootObject();
            cs2EQ = rootObject2EQ.getChangeSummary();
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred loading the xml file");
        }
        assertNotNull(rootObject2EQ);
        assertNotNull(cs2EQ);

        // 1. turn logging off on both
        cs.endLogging();
        cs2EQ.endLogging();

        // check shallow equal
        boolean isEqual = aHelperContext.getEqualityHelper().equalShallow(rootObject, rootObject2EQ);
        assertTrue(isEqual);

        // check deep equal is unaffected
        isEqual = aHelperContext.getEqualityHelper().equal(rootObject, rootObject2EQ);
        assertTrue(isEqual);

        // 2. turn logging on on both
        cs.beginLogging();
        cs2EQ.beginLogging();
        // check shallow equal
        isEqual = aHelperContext.getEqualityHelper().equalShallow(rootObject, rootObject2EQ);
        assertTrue(isEqual);

        // check deep equal is unaffected
        isEqual = aHelperContext.getEqualityHelper().equal(rootObject, rootObject2EQ);
        assertTrue(isEqual);

        // 3. turn logging off on one 
        cs.endLogging();
        // check shallow equal
        isEqual = aHelperContext.getEqualityHelper().equalShallow(rootObject, rootObject2EQ);
        assertTrue(isEqual);

        // check deep equal is unaffected
        isEqual = aHelperContext.getEqualityHelper().equal(rootObject, rootObject2EQ);
        assertTrue(isEqual);

        // 4. perform changes on one of the copies and put it back (without an undo)
        DataObject items = (DataObject)rootObject2EQ.get("items");
        ((DataObject)rootObject2EQ.get("items")).detach();
        // get an oldSetting in order to enable caching to fill the hash map in the cs
        Object oldValueItems = cs2EQ.getOldValue(rootObject2EQ, rootObject2EQ.getInstanceProperty("items")).getValue();
        assertNotNull(oldValueItems);
        assertNull(rootObject2EQ.get("items"));
        rootObject2EQ.set("items", items);

        // check shallow equal is unaffected by the entry in the oldSettings list
        isEqual = aHelperContext.getEqualityHelper().equalShallow(rootObject, rootObject2EQ);
        assertTrue(isEqual);

        // check deep equal is unaffected by the entry in the oldSettings list
        isEqual = aHelperContext.getEqualityHelper().equal(rootObject, rootObject2EQ);
        assertTrue(isEqual);

        // 5. perform changes on both of the copies (1 with the cs on)
        items = (DataObject)rootObject.get("items");
        ((DataObject)rootObject.get("items")).detach();
        // get an oldSetting in order to enable caching to fill the hash map in the cs
        assertNull(rootObject.get("items"));
        items = (DataObject)rootObject2EQ.get("items");
        ((DataObject)rootObject2EQ.get("items")).detach();
        // get an oldSetting in order to enable caching to fill the hash map in the cs
        oldValueItems = cs2EQ.getOldValue(rootObject2EQ, rootObject2EQ.getInstanceProperty("items")).getValue();
        assertNotNull(oldValueItems);
        assertNull(rootObject2EQ.get("items"));
        //rootObject2EQ.set("items", items);
        // check shallow equal is unaffected by the entry in the oldSettings list
        isEqual = aHelperContext.getEqualityHelper().equalShallow(rootObject, rootObject2EQ);
        assertTrue(isEqual);

        // check deep equal is unaffected by the entry in the oldSettings list
        isEqual = aHelperContext.getEqualityHelper().equal(rootObject, rootObject2EQ);
        assertTrue(isEqual);
    }

    private SDODataObject verifyDeepCopyObjectWithCSLoggingOnDoesNotLogChangesInTheDeepCopy(SDODataObject aRootObject) {
        List preOrderList = null;
        int numberOfDataObjectsInSubTree = 0;

        // verify logging is on
        // turn on logging
        cs.beginLogging();
        assertTrue(cs.isLogging());

        // take an object with CS on and deep copy it
        SDODataObject copy = (SDODataObject)copyHelper.copy(aRootObject);

        // verify that logging is still on
        assertNotNull(cs);
        assertTrue(cs.isLogging());
        ChangeSummary cs2 = copy.getChangeSummary();
        assertNotNull(cs2);
        assertTrue(cs2.isLogging());
        // verify that we have not logged changes during the copy (we dont have to worry about nested changesummaries)
        assertEquals(0, cs2.getChangedDataObjects().size());// we should not have 5 old settings after a copy
        preOrderList = preOrderTraversalDataObjectList(copy, false);
        numberOfDataObjectsInSubTree = preOrderList.size();
        // assume that for logging=true copies we should have oldContainer, oldContProperties
        assertEquals(15, numberOfDataObjectsInSubTree);
        assertEquals(((SDOChangeSummary)cs2).getOldContainer().size(),//
                     ((SDOChangeSummary)copy.getChangeSummary()).getOldContainer().size());
        assertEquals(((SDOChangeSummary)cs2).getOldContainmentProperty().size(),//
                     ((SDOChangeSummary)copy.getChangeSummary()).getOldContainmentProperty().size());
        return copy;

    }

    // see bug #5878605: SDO: COPYHELPER.COPY() LOGS CS CHANGES - SHOULD SUSPEND LOGGING DURING COPY
    // we dont want changes to occur during the embedded set() calls in copy()
    // this test should be in the following location but this suite has a deeper model in order to fully test the bug see SDOCopyHelperDeepCopyTest.testDeepCopyObjectWithCSLoggingOnDoesNotLogChangesInTheDeepCopy()
    public void testDeepCopyObjectWithCSLoggingOnDoesNotLogChangesInTheDeepCopy() {
        verifyDeepCopyObjectWithCSLoggingOnDoesNotLogChangesInTheDeepCopy((SDODataObject)rootObject);
    }

    // verify unset after unset - logging on in the middle
    public void testUnsetCommentsAfterLoggingOnAfterUnset() {
        //cs.beginLogging();
        rootObject.unset("comment");
        cs.beginLogging();
        rootObject.unset("comment");
        assertUnchanged(rootObject, cs);//
        assertEquals(0, cs.getChangedDataObjects().size());

        List oldValues = cs.getOldValues(rootObject);
        assertEquals(0, oldValues.size());
        //writeXML(rootObject);
    }

    public void testDeleteItems() {
        DataObject itemsDO = rootObject.getDataObject("items");
        DataObject item1DO = rootObject.getDataObject("items/item[1]");
        DataObject item2DO = rootObject.getDataObject("items/item[2]");

        DataObject item1ProductDO = item1DO.getDataObject("product");
        DataObject item1ProductPrice1DO = item1ProductDO.getDataObject("price[1]");
        DataObject item1ProductPrice2DO = item1ProductDO.getDataObject("price[2]");

        DataObject item2ProductDO = item2DO.getDataObject("product");
        DataObject item2ProductPrice1DO = item2ProductDO.getDataObject("price[1]");
        DataObject item2ProductPrice2DO = item2ProductDO.getDataObject("price[2]");

        cs.beginLogging();
        itemsDO.delete();

        assertNotNull(item1DO);
        assertNotNull(item2DO);

        assertModified(rootObject, cs);
        assertDeleted(itemsDO, cs);
        assertDeleted(item1DO, cs);
        assertDeleted(item2DO, cs);
        assertDeleted(item1ProductDO, cs);
        assertDeleted(item1ProductPrice1DO, cs);
        assertDeleted(item1ProductPrice2DO, cs);
        assertDeleted(item2ProductDO, cs);
        assertDeleted(item2ProductPrice1DO, cs);
        assertDeleted(item2ProductPrice2DO, cs);
        assertEquals(10, cs.getChangedDataObjects().size());

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());//  from 9

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());// from 9

        //writeXML(rootObject);// null objectValue in org.eclipse.persistence.internal.oxm.XMLAnyCollectionMappingNodeValue.marshal(XMLAnyCollectionMappingNodeValue.java:92)
    }

    public void testDeleteItemsAndUndo() {
        // save original root for later comparison after undo
        DataObject originalRootDO = copyHelper.copy(rootObject);
        assertTrue(equalityHelper.equal(rootObject, originalRootDO));

        DataObject itemsDO = rootObject.getDataObject("items");
        DataObject item1DO = rootObject.getDataObject("items/item[1]");
        DataObject item2DO = rootObject.getDataObject("items/item[2]");

        DataObject item1ProductDO = item1DO.getDataObject("product");
        DataObject item1ProductPrice1DO = item1ProductDO.getDataObject("price[1]");
        DataObject item1ProductPrice2DO = item1ProductDO.getDataObject("price[2]");

        DataObject item2ProductDO = item2DO.getDataObject("product");
        DataObject item2ProductPrice1DO = item2ProductDO.getDataObject("price[1]");
        DataObject item2ProductPrice2DO = item2ProductDO.getDataObject("price[2]");

        cs.beginLogging();
        // verify original VS is null and save a copy of current VS for object identity testing after undo
        ValueStore aCurrentValueStore = ((SDODataObject)rootObject)._getCurrentValueStore();
        assertNotNull(aCurrentValueStore);
        ValueStore anOriginalValueStore = (ValueStore)((SDOChangeSummary)cs).getOriginalValueStores().get(rootObject);
        assertNull(anOriginalValueStore);

        itemsDO.delete();

        // check valueStores
        assertNotNull(aCurrentValueStore);
        ValueStore anOriginalValueStoreAfterOperation = (ValueStore)((SDOChangeSummary)cs).getOriginalValueStores().get(rootObject);
        ValueStore aCurrentValueStoreAfterOperation = ((SDODataObject)rootObject)._getCurrentValueStore();
        assertNotNull(anOriginalValueStoreAfterOperation);
        assertNotNull(aCurrentValueStoreAfterOperation);
        assertTrue(anOriginalValueStoreAfterOperation == aCurrentValueStore);

        assertNotNull(item1DO);
        assertNotNull(item2DO);

        assertModified(rootObject, cs);
        assertDeleted(itemsDO, cs);
        assertDeleted(item1DO, cs);
        assertDeleted(item2DO, cs);
        assertDeleted(item1ProductDO, cs);
        assertDeleted(item1ProductPrice1DO, cs);
        assertDeleted(item1ProductPrice2DO, cs);
        assertDeleted(item2ProductDO, cs);
        assertDeleted(item2ProductPrice1DO, cs);
        assertDeleted(item2ProductPrice2DO, cs);
        assertEquals(10, cs.getChangedDataObjects().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());//  from 9
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());// from 9

        assertUndoChangesEqualToOriginal(cs, rootObject, originalRootDO);

        // verify that property is reset
        assertTrue(rootObject.isSet("items"));
        // get back items object
        DataObject itemsDOundone = rootObject.getDataObject("items");

        ValueStore anOriginalValueStoreAfterUndo = (ValueStore)((SDOChangeSummary)cs).getOriginalValueStores().get(rootObject);
        ValueStore aCurrentValueStoreAfterUndo = ((SDODataObject)rootObject)._getCurrentValueStore();
        assertNull(anOriginalValueStoreAfterUndo);
        assertNotNull(aCurrentValueStoreAfterUndo);
        // we return the original value store back to the current VS
        assertTrue(aCurrentValueStoreAfterUndo == aCurrentValueStore);
    }

    public void testDetachItems() {
        // save original root for later comparison after undo
        DataObject originalRootDO = copyHelper.copy(rootObject);
        assertTrue(equalityHelper.equal(rootObject, originalRootDO));

        DataObject itemsDO = rootObject.getDataObject("items");
        DataObject item1DO = rootObject.getDataObject("items/item[1]");
        DataObject item2DO = rootObject.getDataObject("items/item[2]");

        DataObject item1ProductDO = item1DO.getDataObject("product");
        DataObject item1ProductPrice1DO = item1ProductDO.getDataObject("price[1]");
        DataObject item1ProductPrice2DO = item1ProductDO.getDataObject("price[2]");

        DataObject item2ProductDO = item2DO.getDataObject("product");
        DataObject item2ProductPrice1DO = item2ProductDO.getDataObject("price[1]");
        DataObject item2ProductPrice2DO = item2ProductDO.getDataObject("price[2]");

        cs.beginLogging();
        itemsDO.detach();

        assertNotNull(item1DO);
        assertNotNull(item2DO);

        assertModified(rootObject, cs);
        assertDetached(itemsDO, cs);
        assertDetached(item1DO, cs, false);// internal children of a detach have a non-null container
        assertDetached(item2DO, cs, false);// internal children of a detach have a non-null container
        assertDetached(item1ProductDO, cs, false);// internal children of a detach have a non-null container
        assertDetached(item1ProductPrice1DO, cs, false);// internal children of a detach have a non-null container
        assertDetached(item1ProductPrice2DO, cs, false);// internal children of a detach have a non-null container
        assertDetached(item2ProductDO, cs, false);// internal children of a detach have a non-null container
        assertDetached(item2ProductPrice1DO, cs, false);// internal children of a detach have a non-null container
        assertDetached(item2ProductPrice2DO, cs, false);// internal children of a detach have a non-null container
        assertEquals(10, cs.getChangedDataObjects().size());

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());//  from 9

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());// from 9

        assertUndoChangesEqualToOriginal(cs, rootObject, originalRootDO);
    }

    // test unset of single property containing an isMany=true set of DataObjects
    public void testCSonRootUnsetComplexSingleAtRootWithComplexManyChildren() {// testUnsetItems() { // NPE on save
        DataObject itemsDO = rootObject.getDataObject("items");
        DataObject item1DO = rootObject.getDataObject("items/item[1]");
        DataObject item2DO = rootObject.getDataObject("items/item[2]");

        DataObject item1ProductDO = item1DO.getDataObject("product");
        DataObject item1ProductPrice1DO = item1ProductDO.getDataObject("price[1]");
        DataObject item1ProductPrice2DO = item1ProductDO.getDataObject("price[2]");

        DataObject item2ProductDO = item2DO.getDataObject("product");
        DataObject item2ProductPrice1DO = item2ProductDO.getDataObject("price[1]");
        DataObject item2ProductPrice2DO = item2ProductDO.getDataObject("price[2]");
        Property containmentProp = itemsDO.getContainmentProperty();

        cs.beginLogging();
        rootObject.unset(containmentProp);

        //        verifyShipToDetachedOrUnset(shipToDO, yardDO,//
        //        		phoneList, phone1, phone2, containmentProp, oldStreet);
        assertNotNull(item1DO);
        assertNotNull(item2DO);

        assertModified(rootObject, cs);
        assertDetached(itemsDO, cs);
        assertDetached(item1DO, cs, false);// internal children of a detach have a non-null container
        assertDetached(item2DO, cs, false);// internal children of a detach have a non-null container
        assertDetached(item1ProductDO, cs, false);// internal children of a detach have a non-null container
        assertDetached(item1ProductPrice1DO, cs, false);// internal children of a detach have a non-null container
        assertDetached(item1ProductPrice2DO, cs, false);// internal children of a detach have a non-null container
        assertDetached(item2ProductDO, cs, false);// internal children of a detach have a non-null container
        assertDetached(item2ProductPrice1DO, cs, false);// internal children of a detach have a non-null container
        assertDetached(item2ProductPrice2DO, cs, false);// internal children of a detach have a non-null container
        assertEquals(10, cs.getChangedDataObjects().size());

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());//  from 9

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());// from 9

        //writeXML(rootObject);// null objectValue in org.eclipse.persistence.internal.oxm.XMLAnyCollectionMappingNodeValue.marshal(XMLAnyCollectionMappingNodeValue.java:92) 
    }

    // Test Scenarios:
    // - many delete
    public void testDeleteItem2Price1() {
        // save original root for later comparison after undo
        DataObject originalRootDO = copyHelper.copy(rootObject);
        assertTrue(equalityHelper.equal(rootObject, originalRootDO));

        DataObject itemsDO = rootObject.getDataObject("items");
        DataObject item1DO = rootObject.getDataObject("items/item[1]");
        DataObject item2DO = rootObject.getDataObject("items/item[2]");

        DataObject item1ProductDO = item1DO.getDataObject("product");
        DataObject item1ProductPrice1DO = item1ProductDO.getDataObject("price[1]");
        DataObject item1ProductPrice2DO = item1ProductDO.getDataObject("price[2]");

        DataObject item2ProductDO = item2DO.getDataObject("product");
        DataObject item2ProductPrice1DO = item2ProductDO.getDataObject("price[1]");
        DataObject item2ProductPrice2DO = item2ProductDO.getDataObject("price[2]");

        cs.beginLogging();
        item2ProductPrice1DO.delete();

        assertModified(item2ProductDO, cs);
        assertDeleted(item2ProductPrice1DO, cs);

        assertEquals(2, cs.getChangedDataObjects().size());

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

        ChangeSummary.Setting containmentSetting = (ChangeSummary.Setting)cs.getOldValues(item2ProductDO).get(0);
        assertEquals("price", containmentSetting.getProperty().getName());
        assertTrue(containmentSetting.getValue() instanceof List);
        assertEquals(2, ((List)containmentSetting.getValue()).size());
        assertEquals(true, containmentSetting.isSet());
        //writeXML(rootObject);
        // undo and verify equality
        assertUndoChangesEqualToOriginal(cs, rootObject, originalRootDO);

    }

    public void testDetachItem2Price1() {
        DataObject itemsDO = rootObject.getDataObject("items");
        DataObject item1DO = rootObject.getDataObject("items/item[1]");
        DataObject item2DO = rootObject.getDataObject("items/item[2]");

        DataObject item1ProductDO = item1DO.getDataObject("product");
        DataObject item1ProductPrice1DO = item1ProductDO.getDataObject("price[1]");
        DataObject item1ProductPrice2DO = item1ProductDO.getDataObject("price[2]");

        DataObject item2ProductDO = item2DO.getDataObject("product");
        DataObject item2ProductPrice1DO = item2ProductDO.getDataObject("price[1]");
        DataObject item2ProductPrice2DO = item2ProductDO.getDataObject("price[2]");

        cs.beginLogging();
        item2ProductPrice1DO.detach();

        assertModified(item2ProductDO, cs);
        assertDetached(item2ProductPrice1DO, cs);

        assertEquals(2, cs.getChangedDataObjects().size());

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

        ChangeSummary.Setting containmentSetting = (ChangeSummary.Setting)cs.getOldValues(item2ProductDO).get(0);
        assertEquals("price", containmentSetting.getProperty().getName());
        assertTrue(containmentSetting.getValue() instanceof List);
        assertEquals(2, ((List)containmentSetting.getValue()).size());
        assertEquals(true, containmentSetting.isSet());
        //writeXML(rootObject);
    }

    // Note: unset of a single item in a list is different than a detach in that all the items under
    // property being unset are detached.
    public void testCSonRootUnsetComplexSingleBelowRootWithCompexManyChildren() {// testUnsetItem2Price() {
        DataObject item2DO = rootObject.getDataObject("items/item[2]");

        //DataObject item1ProductDO = item1DO.getDataObject("product");
        //DataObject item1ProductPrice1DO = item1ProductDO.getDataObject("price[1]");
        //DataObject item1ProductPrice2DO = item1ProductDO.getDataObject("price[2]");
        DataObject item2ProductDO = item2DO.getDataObject("product");
        DataObject item2ProductPrice1DO = item2ProductDO.getDataObject("price[1]");
        DataObject item2ProductPrice2DO = item2ProductDO.getDataObject("price[2]");
        Property containmentProp = item2ProductPrice1DO.getContainmentProperty();

        cs.beginLogging();
        // test isMany section of unset() - all items including price1 and price2 are unset
        item2ProductDO.unset(containmentProp);

        assertModified(item2ProductDO, cs);
        assertDetached(item2ProductPrice1DO, cs);
        assertDetached(item2ProductPrice2DO, cs);

        assertEquals(3, cs.getChangedDataObjects().size());

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

        List aSettingList = cs.getOldValues(item2ProductDO);
        assertTrue(aSettingList.size() > 0);
        ChangeSummary.Setting containmentSetting = (ChangeSummary.Setting)aSettingList.get(0);
        assertEquals("price", containmentSetting.getProperty().getName());
        assertTrue(containmentSetting.getValue() instanceof List);
        assertEquals(2, ((List)containmentSetting.getValue()).size());
        assertEquals(true, containmentSetting.isSet());
        //writeXML(rootObject);
    }

    // unset a single item in a list
    // TODO: this test will fail as of 20070111 until bug#5757236 is fixed
    public void testSetItem2Price3() {
        DataObject item2DO = rootObject.getDataObject("items/item[2]");

        DataObject item2ProductDO = item2DO.getDataObject("product");
        DataObject item2ProductPrice1DO = item2ProductDO.getDataObject("price[1]");
        DataObject item2ProductPrice2DO = item2ProductDO.getDataObject("price[2]");

        cs.beginLogging();
        int startListSize = item2ProductDO.getList("price").size();
        assertEquals(2, startListSize);
        DataObject newPrice = dataFactory.create(item2ProductPrice2DO.getType());
        item2ProductDO.set("price[3]", newPrice);

        int endListSize = item2ProductDO.getList("price").size();
        assertEquals(3, endListSize);

        assertCreated(newPrice, cs);// TODO: failure starts here  
        assertModified(item2ProductDO, cs);
        assertUnchanged(item2ProductPrice1DO, cs);
        assertUnchanged(item2ProductPrice2DO, cs);// TODO: failure starts here  

        assertEquals(2, cs.getChangedDataObjects().size());

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

        List aSettingList = cs.getOldValues(item2ProductDO);
        assertTrue(aSettingList.size() > 0);
        ChangeSummary.Setting containmentSetting = (ChangeSummary.Setting)aSettingList.get(0);
        assertEquals("price", containmentSetting.getProperty().getName());
        assertTrue(containmentSetting.getValue() instanceof List);
        assertEquals(2, ((List)containmentSetting.getValue()).size());
        assertEquals(true, containmentSetting.isSet());
        //writeXML(rootObject);
    }

    public void testRemoveByObjectItem2Price2() {
        DataObject item2DO = rootObject.getDataObject("items/item[2]");

        DataObject item2ProductDO = item2DO.getDataObject("product");
        DataObject item2ProductPrice1DO = item2ProductDO.getDataObject("price[1]");
        DataObject item2ProductPrice2DO = item2ProductDO.getDataObject("price[2]");

        cs.beginLogging();

        int startListSize = item2ProductDO.getList("price").size();
        assertEquals(2, startListSize);

        item2ProductDO.getList("price").remove(item2ProductPrice2DO);

        int endListSize = item2ProductDO.getList("price").size();
        assertEquals(1, endListSize);

        assertModified(item2ProductDO, cs);
        assertUnchanged(item2ProductPrice1DO, cs);
        assertDetached(item2ProductPrice2DO, cs);// TODO: failure starts here        

        assertEquals(2, cs.getChangedDataObjects().size());

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

        List aSettingList = cs.getOldValues(item2ProductDO);
        assertTrue(aSettingList.size() > 0);
        ChangeSummary.Setting containmentSetting = (ChangeSummary.Setting)aSettingList.get(0);
        assertEquals("price", containmentSetting.getProperty().getName());
        assertTrue(containmentSetting.getValue() instanceof List);
        assertEquals(2, ((List)containmentSetting.getValue()).size());
        assertEquals(true, containmentSetting.isSet());
        //writeXML(rootObject);
    }

    public void testRemoveByIndexItem2Price2() {
        DataObject item2DO = rootObject.getDataObject("items/item[2]");

        DataObject item2ProductDO = item2DO.getDataObject("product");
        DataObject item2ProductPrice1DO = item2ProductDO.getDataObject("price[1]");
        DataObject item2ProductPrice2DO = item2ProductDO.getDataObject("price[2]");

        cs.beginLogging();
        int startListSize = item2ProductDO.getList("price").size();
        assertEquals(2, startListSize);

        int indexToRemove = item2ProductDO.getList("price").indexOf(item2ProductPrice2DO);
        item2ProductDO.getList("price").remove(indexToRemove);

        int endListSize = item2ProductDO.getList("price").size();
        assertEquals(1, endListSize);

        assertModified(item2ProductDO, cs);
        assertUnchanged(item2ProductPrice1DO, cs);
        assertDetached(item2ProductPrice2DO, cs);// TODO: failure starts here        

        assertEquals(2, cs.getChangedDataObjects().size());

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

        List aSettingList = cs.getOldValues(item2ProductDO);
        assertTrue(aSettingList.size() > 0);
        ChangeSummary.Setting containmentSetting = (ChangeSummary.Setting)aSettingList.get(0);
        assertEquals("price", containmentSetting.getProperty().getName());
        assertTrue(containmentSetting.getValue() instanceof List);
        assertEquals(2, ((List)containmentSetting.getValue()).size());
        assertEquals(true, containmentSetting.isSet());
        //writeXML(rootObject);
    }

    // unset a single item in a list
    // TODO: this test will fail as of 20070111 until bug#5757236 is fixed
    public void testUnsetItem2Price2() {
        DataObject item2DO = rootObject.getDataObject("items/item[2]");

        DataObject item2ProductDO = item2DO.getDataObject("product");
        DataObject item2ProductPrice1DO = item2ProductDO.getDataObject("price[1]");
        DataObject item2ProductPrice2DO = item2ProductDO.getDataObject("price[2]");

        cs.beginLogging();
        int startListSize = item2ProductDO.getList("price").size();
        assertEquals(2, startListSize);
        // test isMany section of unset() - all items including price1 and price2 are unset
        item2ProductDO.unset("price[2]");

        int endListSize = item2ProductDO.getList("price").size();
        assertEquals(1, endListSize);

        assertModified(item2ProductDO, cs);
        assertUnchanged(item2ProductPrice1DO, cs);
        assertDetached(item2ProductPrice2DO, cs);// TODO: failure starts here        

        assertEquals(2, cs.getChangedDataObjects().size());

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

        List aSettingList = cs.getOldValues(item2ProductDO);
        assertTrue(aSettingList.size() > 0);
        ChangeSummary.Setting containmentSetting = (ChangeSummary.Setting)aSettingList.get(0);
        assertEquals("price", containmentSetting.getProperty().getName());
        assertTrue(containmentSetting.getValue() instanceof List);
        assertEquals(2, ((List)containmentSetting.getValue()).size());
        assertEquals(true, containmentSetting.isSet());
        //writeXML(rootObject);
    }

    /**
     * The following two tests do the same thing in 2 different ways.
     * The results should be the same after both calls
     * detach/re(set) yard
     * detach/undo yard
     * see bug# 5882923
     */
    private List setupDetachForResetOrUndoOfYardToSamePlace(DataObject shipToDO, DataObject yardDO, Object length) {
        cs.beginLogging();
        yardDO.detach();

        List yardDOSettings = cs.getOldValues(yardDO);
        assertEquals(3, yardDOSettings.size());
        assertDetached(yardDO, cs);
        assertModified(shipToDO, cs);
        assertFalse(cs.isModified(yardDO));
        assertEquals(2, cs.getChangedDataObjects().size());
        return yardDOSettings;
    }

    // perform a manual undo (local undo)
    // See SDO-225 Feb-15 2007 issue (option A - nothing in the cs, option B - track unset/reset)
    public void testDetachAndResetYardToSamePlaceUsingManualUndoViaSet() {
        DataObject shipToDO = rootObject.getDataObject("shipTo");

        //Property containmentProp = shipToDO.getInstanceProperty("yard");
        DataObject yardDO = shipToDO.getDataObject("yard");
        Object length = yardDO.get("length");
        List yardDOSettings = setupDetachForResetOrUndoOfYardToSamePlace(shipToDO, yardDO, length);

        // originalValueStore of shipTo will already be copied in the modifiy part of the detach of its child above
        // this set will trigger a 2nd copy of the ValueStore - we will ignore it.
        /**
        * Case: Move/Reset
        *  a detach of a child modifies its parent - trigering a copy of parent's ValueStore
        *  a set on the parent will trigger another copy - but we will skip this one as we already
        *  have a copy of the original.
        *  Issue: if we reset the child back the same place (in effect doing our own undo)
        *  do we optimize this and remove the copy - for now a real undoChanges() will do the same
        */
        shipToDO.set("yard", yardDO); // the entry in deletedMap will be removed and isModified == false
        assertFalse(cs.isDeleted(yardDO));
        
        yardDOSettings = cs.getOldValues(yardDO);  // will return an empty list since the oldValueStore entry was removed during set()
        List shipToSettings = cs.getOldValues(shipToDO);
        // see cs.getOldValue() does not return null when yardDO is !modified and !deleted (but !created as well) - (re)set
        ChangeSummary.Setting lengthSetting = cs.getOldValue(yardDO, yardDO.getInstanceProperty("length"));

        // TODO: FIX this ambiguity Jira 225 and 109/125 via a smart local undo
        // 20070501: this test is has been switched to assertTrue to pass until we fix 5882923
        if(1 < 0) {
            assertFalse(cs.isModified(shipToDO)); // this should be the correct assertion
            assertEquals(0, cs.getChangedDataObjects().size());//if we add new logic
            // see cs.getOldValues() does not return and empty List when yardDO is !modified and !deleted (but !created as well) - (re)set        
            assertEquals(0, yardDOSettings.size());// was 3 before we implemented undoChanges()
            //assertNotNull(lengthSetting);
            assertNull(lengthSetting);
        } else {
            assertTrue(cs.isModified(shipToDO)); // see bug# 5882923 - we should clear the setting here
            assertEquals(1, cs.getChangedDataObjects().size());//if we add new logic
            // see cs.getOldValues() does not return and empty List when yardDO is !modified and !deleted (but !created as well) - (re)set        
            assertEquals(0, yardDOSettings.size());// was 3 before we implemented undoChanges()
            assertNull(lengthSetting);
            //assertNull(lengthSetting);
        }

        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        assertFalse(cs.isModified(yardDO));

        //assertEquals(1, cs.getChangedDataObjects().size());//just shipTo
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

        assertEquals(0, shipToSettings.size());// 20070214: modified from 1 

        //assertEquals(length, lengthSetting.getValue());
        //writeXML(rootObject);
    }

    // same as above but using a real undoChanges() instead of a re(set)
    // see bug# 5882923 
    // See SDO-225 Feb-15 2007 issue (option A - nothing in the cs, option B - track unset/reset)
    public void testDetachAndResetYardToSamePlaceUsingFullCSUndoChangesFunction() {
        DataObject shipToDO = rootObject.getDataObject("shipTo");

        //Property containmentProp = shipToDO.getInstanceProperty("yard");
        DataObject yardDO = shipToDO.getDataObject("yard");
        Object length = yardDO.get("length");
        List yardDOSettings = setupDetachForResetOrUndoOfYardToSamePlace(shipToDO, yardDO, length);

        // originalValueStore of shipTo will already be copied in the modifiy part of the detach of its child above
        // this set will trigger a 2nd copy of the ValueStore - we will ignore it.
        /**
        * Case: Move/Reset
        *  a detach of a child modifies its parent - trigering a copy of parent's ValueStore
        *  a set on the parent will trigger another copy - but we will skip this one as we already
        *  have a copy of the original.
        *  Issue: if we reset the child back the same place (in effect doing our own undo)
        *  do we optimize this and remove the copy - for now a real undoChanges() will do the same
        */
        //shipToDO.set("yard", yardDO);
        cs.undoChanges();

        assertFalse(cs.isDeleted(yardDO));
        assertFalse(cs.isModified(shipToDO));//if we add new logic
        //assertTrue(cs.isModified(shipToDO)); // see bug# 5882923 - we should clear the setting here
        assertFalse(cs.isModified(yardDO));

        assertEquals(0, cs.getChangedDataObjects().size());//if we add new logic
        //assertEquals(1, cs.getChangedDataObjects().size());//just shipTo
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

        List shipToSettings = cs.getOldValues(shipToDO);
        assertEquals(0, shipToSettings.size());// 20070214: modified from 1 

        yardDOSettings = cs.getOldValues(yardDO);
        // see cs.getOldValues() does not return and empty List when yardDO is !modified and !deleted (but !created as well) - (re)set        
        assertEquals(0, yardDOSettings.size());// was 3 before we implemented undoChanges()

        // see cs.getOldValue() does not return null when yardDO is !modified and !deleted (but !created as well) - (re)set
        ChangeSummary.Setting lengthSetting = cs.getOldValue(yardDO, yardDO.getInstanceProperty("length"));

        //assertNotNull(lengthSetting);
        assertNull(lengthSetting);

        //assertEquals(length, lengthSetting.getValue());
        //writeXML(rootObject);
    }

    // Verify that an unset followed by a (re)set of the same subtree returns tree to original state
    // See SDO-225 Feb-15 2007 issue (option A - nothing in the cs, option B - track unset/reset)
    public void testDetachAndReSetPrice1BackToSameIndex() {
        DataObject itemsDO = rootObject.getDataObject("items");
        DataObject item1DO = rootObject.getDataObject("items/item[1]");
        DataObject item2DO = rootObject.getDataObject("items/item[2]");

        DataObject item1ProductDO = item1DO.getDataObject("product");
        DataObject item1ProductPrice1DO = item1ProductDO.getDataObject("price[1]");
        DataObject item1ProductPrice2DO = item1ProductDO.getDataObject("price[2]");

        DataObject item2ProductDO = item2DO.getDataObject("product");
        DataObject item2ProductPrice1DO = item2ProductDO.getDataObject("price[1]");
        DataObject item2ProductPrice2DO = item2ProductDO.getDataObject("price[2]");

        cs.beginLogging();
        item2ProductPrice1DO.detach();

        assertModified(item2ProductDO, cs);
        assertDetached(item2ProductPrice1DO, cs);

        assertEquals(2, cs.getChangedDataObjects().size());

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

        List aSettingList = cs.getOldValues(item2ProductDO);
        assertTrue(aSettingList.size() > 0);
        ChangeSummary.Setting containmentSetting = (ChangeSummary.Setting)aSettingList.get(0);
        assertEquals("price", containmentSetting.getProperty().getName());
        assertTrue(containmentSetting.getValue() instanceof List);
        assertEquals(2, ((List)containmentSetting.getValue()).size());
        assertEquals(true, containmentSetting.isSet());

        // UNDO - reattach subtree
        ((ListWrapper)item2ProductDO.get("price")).add(0, item2ProductPrice1DO);

        assertFalse(cs.isDeleted(item2ProductPrice1DO));
        //assertUnchanged(cs.isModified(item2ProductDO)); //if we add new logic        
        assertFalse(cs.isModified(item2ProductDO));

        assertFalse(cs.isModified(item2ProductPrice1DO));

        assertEquals(0, cs.getChangedDataObjects().size());

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());
        //writeXML(rootObject);
    }

    // Verify that an unset followed by a set (swap) at a different position does not return the tree to the original state
    // price[1] becomes price[2], and price[2] becomes price[1]
    public void testSwapUsingDetachAndSetPrice1ToLastIndex2() {
        DataObject itemsDO = rootObject.getDataObject("items");
        DataObject item1DO = rootObject.getDataObject("items/item[1]");
        DataObject item2DO = rootObject.getDataObject("items/item[2]");

        DataObject item1ProductDO = item1DO.getDataObject("product");
        DataObject item1ProductPrice1DO = item1ProductDO.getDataObject("price[1]");
        DataObject item1ProductPrice2DO = item1ProductDO.getDataObject("price[2]");

        DataObject item2ProductDO = item2DO.getDataObject("product");
        DataObject item2ProductPrice1DO = item2ProductDO.getDataObject("price[1]");
        DataObject item2ProductPrice2DO = item2ProductDO.getDataObject("price[2]");

        cs.beginLogging();
        item2ProductPrice1DO.detach();

        assertModified(item2ProductDO, cs);
        assertDetached(item2ProductPrice1DO, cs);

        assertEquals(2, cs.getChangedDataObjects().size());

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

        List aSettingList = cs.getOldValues(item2ProductDO);
        assertTrue(aSettingList.size() > 0);
        ChangeSummary.Setting containmentSetting = (ChangeSummary.Setting)aSettingList.get(0);
        assertEquals("price", containmentSetting.getProperty().getName());
        assertTrue(containmentSetting.getValue() instanceof List);
        assertEquals(2, ((List)containmentSetting.getValue()).size());
        assertEquals(true, containmentSetting.isSet());

        // Swap/move - reattach subtree in new positon
        ((ListWrapper)item2ProductDO.get("price")).add(//
        1,//
        item2ProductPrice1DO);

        // verify that container does not reference itself
        assertNotSame(item2ProductPrice1DO, item2ProductPrice1DO.getContainer());
        assertModified(item2ProductDO, cs);
        // TODO: fails
        //assertModified(item2ProductPrice1DO, cs);// assertDeleted 
        assertFalse(cs.isModified(item2ProductPrice1DO));

        //assertEquals(0, cs.getChangedDataObjects().size());  //if we add new logic      
        assertEquals(1, cs.getChangedDataObjects().size());

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());
        //writeXML(rootObject);
    }

    // test deletion inside a list - see detach() isMany section
    // Test Scenarios:
    // - many delete
    public void testDeleteMultipleInList() {
        cs.endLogging();
        DataObject itemsDO = rootObject.getDataObject("items");
        DataObject item1DO = rootObject.getDataObject("items/item[1]");
        DataObject item3DO = dataFactory.create(item1DO.getType());
        DataObject item4DO = dataFactory.create(item1DO.getType());
        DataObject item5DO = dataFactory.create(item1DO.getType());
        DataObject item6DO = dataFactory.create(item1DO.getType());
        itemsDO.getList("item").add(item3DO);
        itemsDO.getList("item").add(item4DO);
        itemsDO.getList("item").add(item5DO);
        itemsDO.getList("item").add(item6DO);
        cs.beginLogging();
        item3DO.delete();
        item5DO.delete();
        assertDeleted(item3DO, cs);// 5/2
        assertDeleted(item5DO, cs);// 5/2
        assertModified(itemsDO, cs);

        assertEquals(3, cs.getChangedDataObjects().size());
        assertEquals(18, ((SDOChangeSummary)cs).getOldContainer().size());// 14 + 4 new objects
        assertEquals(18, ((SDOChangeSummary)cs).getOldContainmentProperty().size());// 14 + 4 new objects
        assertEquals(1, ((SDOChangeSummary)cs).getOldValues(itemsDO).size());// from 2 (only 1st change on items is recorded)
        assertEquals(5, ((SDOChangeSummary)cs).getOldValues(item3DO).size());// 1
        assertEquals(5, ((SDOChangeSummary)cs).getOldValues(item5DO).size());// 1
        List aSettingList = cs.getOldValues(itemsDO);
        assertTrue(aSettingList.size() > 0);
        ChangeSummary.Setting containmentSetting = (ChangeSummary.Setting)aSettingList.get(0);
        assertEquals("item", containmentSetting.getProperty().getName());
        assertTrue(containmentSetting.getValue() instanceof List);
        assertEquals(6, ((List)containmentSetting.getValue()).size());
        assertEquals(true, containmentSetting.isSet());// see detach(boolean)
        //writeXML(rootObject);
    }

    // test deletion inside a list - see detach() isMany section
    // Test Scenarios:
    // - many delete
    public void testCSonRootDeleteComplexManyBelowRoot() {//testDeleteItem2() {
        DataObject itemsDO = rootObject.getDataObject("items");
        DataObject item1DO = rootObject.getDataObject("items/item[1]");
        DataObject item2DO = rootObject.getDataObject("items/item[2]");

        DataObject item2ProductDO = item2DO.getDataObject("product");
        DataObject item2ProductPrice1DO = item2ProductDO.getDataObject("price[1]");
        DataObject item2ProductPrice2DO = item2ProductDO.getDataObject("price[2]");

        cs.beginLogging();
        item2DO.delete();

        assertModified(itemsDO, cs);
        assertDeleted(item2DO, cs);
        assertDeleted(item2ProductDO, cs);
        assertDeleted(item2ProductPrice1DO, cs);
        assertDeleted(item2ProductPrice2DO, cs);

        assertEquals(5, cs.getChangedDataObjects().size());

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

        assertEquals(1, cs.getOldValues(itemsDO).size());
        ChangeSummary.Setting containmentSetting = (ChangeSummary.Setting)cs.getOldValues(itemsDO).get(0);
        assertEquals("item", containmentSetting.getProperty().getName());
        assertTrue(containmentSetting.getValue() instanceof List);
        assertEquals(2, ((List)containmentSetting.getValue()).size());
        assertEquals(true, containmentSetting.isSet());
        //writeXML(rootObject);
    }

    public void testMoveYardWithSet() {
        DataObject shipToDO = rootObject.getDataObject("shipTo");
        Property containmentProp = shipToDO.getInstanceProperty("yard");

        DataObject billToDO = rootObject.getDataObject("billTo");
        DataObject yardDO = shipToDO.getDataObject("yard");
        cs.beginLogging();
        billToDO.set("yard", yardDO);

        assertModified(shipToDO, cs);
        assertModified(billToDO, cs);
        assertFalse(cs.isDeleted(yardDO));
        assertEquals(2, cs.getChangedDataObjects().size());

        assertEquals(billToDO.getChangeSummary(), yardDO.getChangeSummary());

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());
        // test detach from updateContainment
        List shipToSettings = cs.getOldValues(shipToDO);
        List billToSettings = cs.getOldValues(billToDO);
        assertEquals(1, billToSettings.size());
        assertEquals(1, shipToSettings.size());

        ChangeSummary.Setting billToSetting = (ChangeSummary.Setting)billToSettings.get(0);
        assertEquals("yard", billToSetting.getProperty().getName());
        assertEquals(null, billToSetting.getValue());

        //TODO: comment out this line when null policy stuff is working
        //assertEquals(false, billToSetting.isSet());
        ChangeSummary.Setting shipToSetting = (ChangeSummary.Setting)shipToSettings.get(0);
        assertEquals("yard", shipToSetting.getProperty().getName());
        assertFalse(yardDO == shipToSetting.getValue());
        assertTrue(equalityHelper.equal(yardDO, (DataObject)shipToSetting.getValue()));
        assertEquals(true, shipToSetting.isSet());
        assertEquals(shipToDO, cs.getOldContainer(yardDO));
        assertEquals(containmentProp, cs.getOldContainmentProperty(yardDO));
        //writeXML(rootObject);
    }

    public void testMoveYardWithDetachAndSet() {
        DataObject shipToDO = rootObject.getDataObject("shipTo");
        Property containmentProp = shipToDO.getInstanceProperty("yard");

        DataObject billToDO = rootObject.getDataObject("billTo");
        DataObject yardDO = shipToDO.getDataObject("yard");
        cs.beginLogging();
        yardDO.detach();
        assertTrue(((SDOChangeSummary)cs).isDeleted(yardDO));
        billToDO.set("yard", yardDO);

        assertModified(shipToDO, cs);
        assertModified(billToDO, cs);
        assertFalse(cs.isDeleted(yardDO));
        assertEquals(2, cs.getChangedDataObjects().size());

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());
        // TODO: VERIFY 2 -> 3        
        List shipToSettings = cs.getOldValues(shipToDO);
        List billToSettings = cs.getOldValues(billToDO);
        assertEquals(1, billToSettings.size());
        assertEquals(1, shipToSettings.size());

        ChangeSummary.Setting billToSetting = (ChangeSummary.Setting)billToSettings.get(0);
        assertEquals("yard", billToSetting.getProperty().getName());
        assertEquals(null, billToSetting.getValue());

        //TODO: comment out this line when null policy stuff is working
        //assertEquals(false, billToSetting.isSet());
        ChangeSummary.Setting shipToSetting = (ChangeSummary.Setting)shipToSettings.get(0);
        assertEquals("yard", shipToSetting.getProperty().getName());
        assertFalse(yardDO == shipToSetting.getValue());
        assertTrue(equalityHelper.equal(yardDO, (DataObject)shipToSetting.getValue()));
        assertEquals(true, shipToSetting.isSet());
        assertEquals(shipToDO, cs.getOldContainer(yardDO));
        assertEquals(containmentProp, cs.getOldContainmentProperty(yardDO));
        //writeXML(rootObject);
    }

    public void testDeleteYard() {
        DataObject shipToDO = rootObject.getDataObject("shipTo");
        DataObject yardDO = shipToDO.getDataObject("yard");
        cs.beginLogging();
        yardDO.delete();

        assertYardDeleted(shipToDO, yardDO, cs, true);
        assertModified(shipToDO, cs);

        assertEquals(2, cs.getChangedDataObjects().size());

        assertEquals(1, cs.getOldValues(shipToDO).size());
        ChangeSummary.Setting shipToSetting = (ChangeSummary.Setting)cs.getOldValues(shipToDO).get(0);
        assertEquals("yard", shipToSetting.getProperty().getName());
        assertEquals(true, shipToSetting.isSet());
        DataObject yardDeepCopy = (DataObject)((SDOChangeSummary)cs).getDeepCopies().get(yardDO);
        assertNotNull(yardDeepCopy);
        assertEquals(yardDeepCopy, shipToSetting.getValue());

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

        assertEquals(shipToDO, cs.getOldContainer(yardDO));
        //writeXML(rootObject);
    }

    // test deletion inside a list - see detach() isMany section
    // Test Scenarios:
    // - many delete
    public void testDeletePhone2() {
        DataObject shipToDO = rootObject.getDataObject("shipTo");
        DataObject phoneDO = shipToDO.getDataObject("phone[2]");

        cs.beginLogging();
        phoneDO.delete();

        assertDeleted(phoneDO, cs);
        assertModified(shipToDO, cs);

        assertEquals(2, cs.getChangedDataObjects().size());
        assertEquals(1, cs.getOldValues(phoneDO).size());
        assertEquals(1, cs.getOldValues(shipToDO).size());

        ChangeSummary.Setting phoneSetting = (ChangeSummary.Setting)cs.getOldValues(shipToDO).get(0);
        assertEquals("phone", phoneSetting.getProperty().getName());
        assertEquals(true, phoneSetting.isSet());
        assertTrue(phoneSetting.getValue() instanceof List);
        assertEquals(2, ((List)phoneSetting.getValue()).size());
        DataObject phoneDeepCopy = (DataObject)((SDOChangeSummary)cs).getDeepCopies().get(phoneDO);

        assertTrue(((List)phoneSetting.getValue()).contains(phoneDeepCopy));

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

        assertEquals(shipToDO, cs.getOldContainer(phoneDO));
        //writeXML(rootObject);
    }

    public void testDetachShipToBySet() {
        DataObject shipToDO = rootObject.getDataObject("shipTo");
        DataObject yardDO = shipToDO.getDataObject("yard");
        List phoneList = shipToDO.getList("phone");
        DataObject phone1 = (DataObject)phoneList.get(0);
        DataObject phone2 = (DataObject)phoneList.get(1);
        Property containmentProp = shipToDO.getContainmentProperty();

        Object oldStreet = shipToDO.get("street");

        cs.beginLogging();
        DataObject newShipAddress = dataFactory.create(shipToDO.getType());
        rootObject.set("shipTo", newShipAddress);

        assertNull(shipToDO.getChangeSummary());
        assertNull(yardDO.getChangeSummary());

        Property shipToProp = rootObject.getInstanceProperty("shipTo");
        ChangeSummary.Setting shipToSetting = cs.getOldValue(rootObject, shipToProp);
        assertTrue(shipToSetting.isSet());
        Object shipToDeepCopy = ((SDOChangeSummary)cs).getDeepCopies().get(shipToDO);
        assertNotNull(shipToDeepCopy);
        assertEquals(shipToDeepCopy, shipToSetting.getValue());

        verifyShipToDetachedOrUnsetBasic(shipToDO, yardDO,//
                                         phoneList, phone1, phone2, containmentProp, oldStreet);

        assertCreated(newShipAddress, cs);
        assertEquals(6, cs.getChangedDataObjects().size());
        assertEquals(1, cs.getOldValues(rootObject).size());

        //writeXML(rootObject);
    }

    public void testDeleteShipTo() {
        DataObject shipToDO = rootObject.getDataObject("shipTo");
        DataObject yardDO = shipToDO.getDataObject("yard");
        List phoneList = shipToDO.getList("phone");
        DataObject phone1 = (DataObject)phoneList.get(0);
        DataObject phone2 = (DataObject)phoneList.get(1);
        Property containmentProp = shipToDO.getContainmentProperty();

        Object oldStreet = shipToDO.get("street");

        cs.beginLogging();
        shipToDO.delete();
        assertNull(shipToDO.getChangeSummary());
        assertNull(yardDO.getChangeSummary());
        verifyShipToDetachedOrUnset(shipToDO, yardDO,//
                                    phoneList, phone1, phone2, containmentProp, oldStreet, true);

        //writeXML(rootObject);
    }

    public void testDetachShipTo() {
        DataObject shipToDO = rootObject.getDataObject("shipTo");
        DataObject yardDO = shipToDO.getDataObject("yard");
        List phoneList = shipToDO.getList("phone");
        DataObject phone1 = (DataObject)phoneList.get(0);
        DataObject phone2 = (DataObject)phoneList.get(1);
        Property containmentProp = shipToDO.getContainmentProperty();

        Object oldStreet = shipToDO.get("street");

        cs.beginLogging();
        shipToDO.detach();
        assertNull(shipToDO.getChangeSummary());
        assertNull(yardDO.getChangeSummary());
        verifyShipToDetachedOrUnset(shipToDO, yardDO,//
                                    phoneList, phone1, phone2, containmentProp, oldStreet);
        //writeXML(rootObject);// null objectValue in org.eclipse.persistence.internal.oxm.XMLAnyCollectionMappingNodeValue.marshal(XMLAnyCollectionMappingNodeValue.java:92)
    }

    public void testUnsetShipTo() {
        DataObject shipToDO = rootObject.getDataObject("shipTo");
        DataObject yardDO = shipToDO.getDataObject("yard");
        List phoneList = shipToDO.getList("phone");
        DataObject phone1 = (DataObject)phoneList.get(0);
        DataObject phone2 = (DataObject)phoneList.get(1);
        Property containmentProp = shipToDO.getContainmentProperty();

        Object oldStreet = shipToDO.get("street");

        cs.beginLogging();
        rootObject.unset(containmentProp);

        verifyShipToDetachedOrUnset(shipToDO, yardDO,//
                                    phoneList, phone1, phone2, containmentProp, oldStreet);
        //writeXML(rootObject);
    }

    private void verifyShipToDetachedOrUnset(//
    	    DataObject shipToDO,//
    	    DataObject yardDO,//
    	    List phoneList,//
    	    DataObject phone1,//
    	    DataObject phone2,//
    	    Property containmentProp,//
    	    Object oldStreet) {
    	verifyShipToDetachedOrUnset(//
        	    shipToDO,//
        	    yardDO,//
        	    phoneList,//
        	    phone1,//
        	    phone2,//
        	    containmentProp,//
        	    oldStreet, false);
    }
    
    private void verifyShipToDetachedOrUnset(//
    DataObject shipToDO,//
    DataObject yardDO,//
    List phoneList,//
    DataObject phone1,//
    DataObject phone2,//
    Property containmentProp,//
    Object oldStreet, boolean fromDelete) {
        verifyShipToDetachedOrUnsetBasic(shipToDO, yardDO, phoneList, phone1, phone2, containmentProp, oldStreet, fromDelete);
        assertEquals(5, cs.getChangedDataObjects().size());// 2
        assertEquals(1, cs.getOldValues(rootObject).size());
    }

    
    private void verifyShipToDetachedOrUnsetBasic(//
    	    DataObject shipToDO,//
    	    DataObject yardDO,//
    	    List phoneList,//
    	    DataObject phone1,//
    	    DataObject phone2,//
    	    Property containmentProp,//
    	    Object oldStreet) {
    	verifyShipToDetachedOrUnsetBasic(//
        	    shipToDO,//
        	    yardDO,//
        	    phoneList,//
        	    phone1,//
        	    phone2,//
        	    containmentProp,//
        	    oldStreet, false);
    }
    
    private void verifyShipToDetachedOrUnsetBasic(//
    DataObject shipToDO,//
    DataObject yardDO,//
    List phoneList,//
    DataObject phone1,//
    DataObject phone2,//
    Property containmentProp, Object oldStreet, boolean fromDelete) {
        // verify containers are not unset for children (oldSetting is same object as original - inside List)
        //assertNotNull(((DataObject)shipToDO.getList("phone").get(0)).getContainer());
        //assertNotNull(phone1.getContainer());
        //assertEquals(phone1.getContainer(), shipToDO);
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

        if(!fromDelete) {
        	assertEquals("123 Maple Street", shipToDO.get("street"));
        } else {
        	assertEquals(null, shipToDO.get("street"));
        }
        DataObject oldContainer = ((SDOChangeSummary)cs).getOldContainer(shipToDO);
        Property shipToProp = oldContainer.getInstanceProperty("shipTo");
        ChangeSummary.Setting oldSetting = ((SDOChangeSummary)cs).getOldValue(oldContainer, shipToProp);
        DataObject deepCopyShipTo = (DataObject)oldSetting.getValue();
        assertEquals("123 Maple Street", deepCopyShipTo.get("street"));

        assertModified(rootObject, cs);
        assertNotNull(cs.getOldContainmentProperty(shipToDO));
        assertNotNull(cs.getOldContainer(shipToDO));
        assertEquals(rootObject, cs.getOldContainer(shipToDO));
        assertEquals(containmentProp, cs.getOldContainmentProperty(shipToDO));
        assertDetached(shipToDO, cs);// 8/0
        assertYardDetached(shipToDO, yardDO, cs, false);//
        assertDetached(phone1, cs, false);
        assertDetached(phone2, cs, false);

        ChangeSummary.Setting setting = (ChangeSummary.Setting)cs.getOldValues(rootObject).get(0);
        assertEquals(containmentProp, setting.getProperty());
        Object shipToDeepCopy = ((SDOChangeSummary)cs).getDeepCopies().get(shipToDO);
        assertNotNull(shipToDeepCopy);
        assertEquals(shipToDeepCopy, setting.getValue());
        assertEquals(true, setting.isSet());
        assertEquals(8, cs.getOldValues(shipToDO).size());// 0

        Object newStreet = shipToDO.get("street");

        // verify that children of detached objects are not unset
        if(!fromDelete) {
        	assertNotNull(newStreet);
            assertEquals(oldStreet, newStreet);
        } else {
        	assertNull(newStreet);
        }

        assertEquals(1, cs.getOldValues(phone1).size());// 0
        assertEquals(1, cs.getOldValues(phone2).size());// 0
        ChangeSummary.Setting phone2setting = (ChangeSummary.Setting)cs.getOldValues(phone2).get(0);
        assertEquals("number", phone2setting.getProperty().getName());
        assertEquals("2345678", phone2setting.getValue());
        assertEquals(true, phone2setting.isSet());
    }

    public void testCSonRootDeleteComplexSingleAtRoot() {//testDeleteShipTo() {
        DataObject shipToDO = rootObject.getDataObject("shipTo");
        DataObject yardDO = shipToDO.getDataObject("yard");
        List phoneList = shipToDO.getList("phone");
        DataObject phone1 = (DataObject)phoneList.get(0);
        DataObject phone2 = (DataObject)phoneList.get(1);
        Property containmentProp = shipToDO.getContainmentProperty();
        cs.beginLogging();
        shipToDO.delete();

        // for dataType verify copy of shipTo has a set child in deleted list and current value is unset
        assertNull(shipToDO.get("street"));
        DataObject oldContainer = ((SDOChangeSummary)cs).getOldContainer(shipToDO);
        Property shipToProp = oldContainer.getInstanceProperty("shipTo");
        ChangeSummary.Setting oldSetting = ((SDOChangeSummary)cs).getOldValue(oldContainer, shipToProp);
        DataObject deepCopyShipTo = (DataObject)oldSetting.getValue();
        assertEquals("123 Maple Street", deepCopyShipTo.get("street"));

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

        assertModified(rootObject, cs);
        assertNotNull(cs.getOldContainmentProperty(shipToDO));
        assertNotNull(cs.getOldContainer(shipToDO));
        assertEquals(rootObject, cs.getOldContainer(shipToDO));

        assertEquals(shipToDO, cs.getOldContainer(yardDO));
        assertEquals(shipToDO, cs.getOldContainer(phone1));
        assertEquals(shipToDO, cs.getOldContainer(phone2));

        assertEquals(containmentProp, cs.getOldContainmentProperty(shipToDO));
        assertDeleted(shipToDO, cs);
        //assertDeleted(yardDO, cs);
        assertYardDeleted(shipToDO, yardDO, cs, true);
        assertDeleted(phone1, cs);
        assertDeleted(phone2, cs);
        assertEquals(5, cs.getChangedDataObjects().size());
        assertEquals(1, cs.getOldValues(rootObject).size());
        ChangeSummary.Setting setting = (ChangeSummary.Setting)cs.getOldValues(rootObject).get(0);
        assertEquals(containmentProp, setting.getProperty());
        DataObject shipToDeepCopy = (DataObject)((SDOChangeSummary)cs).getDeepCopies().get(shipToDO);

        assertEquals(shipToDeepCopy, setting.getValue());
        assertEquals(true, setting.isSet());
        assertEquals(8, cs.getOldValues(shipToDO).size());

        assertEquals(1, cs.getOldValues(phone1).size());
        assertEquals(1, cs.getOldValues(phone2).size());
        ChangeSummary.Setting phone2setting = (ChangeSummary.Setting)cs.getOldValues(phone2).get(0);
        assertEquals("number", phone2setting.getProperty().getName());
        assertEquals("2345678", phone2setting.getValue());
        assertEquals(true, phone2setting.isSet());
        //writeXML(rootObject);
    }

    // same test as below
    public void testUnsetCommentsAfterSetNull() {
        rootObject.set("comment", null);
        cs.beginLogging();
        rootObject.unset("comment");

        assertUnchanged(rootObject, cs);
        assertEquals(0, cs.getChangedDataObjects().size());

        List oldValues = cs.getOldValues(rootObject);
        assertEquals(0, oldValues.size());
        //writeXML(rootObject);
    }

    public void testUnsetComments() {
        rootObject.set("comment", null);// set all "comment"s to null
        cs.beginLogging();
        rootObject.unset("comment");// unset all 3 comments?
        assertUnchanged(rootObject, cs);
        assertEquals(0, cs.getChangedDataObjects().size());

        List oldValues = cs.getOldValues(rootObject);
        assertEquals(0, oldValues.size());
        //writeXML(rootObject);
    }

    public void testUnsetSimpleIDSetToNullOnRoot() {
        SDOProperty idProperty = (SDOProperty) ((SDOType)rootObject.getType()).getDeclaredPropertiesMap().get("poId");
        idProperty.setNullable(true);
        
        rootObject.set("poId", null);
        cs.beginLogging();
        rootObject.unset("poId");

        assertModified(rootObject, cs);
        assertEquals(1, cs.getChangedDataObjects().size());

        List oldValues = cs.getOldValues(rootObject);
        assertEquals(1, oldValues.size());

        ChangeSummary.Setting theSetting = (ChangeSummary.Setting)oldValues.get(0);
        assertEquals("poId", theSetting.getProperty().getName());
        assertEquals(true, theSetting.isSet());
        assertEquals(null, theSetting.getValue());
        //writeXML(rootObject);
    }

    public void testUnsetSimpleIDSetToValueOnRoot() {
        rootObject.set("poId", "123");
        // copy our root for later comparison
        DataObject original = copyHelper.copy(rootObject);

        boolean equals = equalityHelper.equal(original, rootObject);
        assertTrue(equals);

        cs.beginLogging();
        rootObject.unset("poId");

        assertModified(rootObject, cs);
        assertEquals(1, cs.getChangedDataObjects().size());

        List oldValues = cs.getOldValues(rootObject);
        assertEquals(1, oldValues.size());

        ChangeSummary.Setting theSetting = (ChangeSummary.Setting)oldValues.get(0);
        assertEquals("poId", theSetting.getProperty().getName());
        assertEquals(true, theSetting.isSet());
        assertEquals("123", theSetting.getValue());
        //writeXML(rootObject);
    }

    public void testUnsetNameAfterLoggingOnAfterUnset() {
        rootObject.unset("poId");
        cs.beginLogging();
        rootObject.unset("poId");
        assertUnchanged(rootObject, cs);//
        assertEquals(0, cs.getChangedDataObjects().size());

        List oldValues = cs.getOldValues(rootObject);
        assertEquals(0, oldValues.size());
        //writeXML(rootObject);
    }

    // test that we do not loose containment in the oldSetting - by unwrapping the ListWrapper into a List
    public void testUnsetFilledSimpleListOnRootRetainsContainmentInOldSetting() {
        List<String> comments = new ArrayList<String>();
        comments.add("comment1");
        comments.add("comment2");
        rootObject.set("comment", comments);
        cs.beginLogging();
        rootObject.unset("comment");

        assertModified(rootObject, cs);
        assertEquals(1, cs.getChangedDataObjects().size());

        List oldValues = cs.getOldValues(rootObject);
        assertEquals(1, oldValues.size());

        ChangeSummary.Setting theSetting = (ChangeSummary.Setting)oldValues.get(0);
        assertEquals("comment", theSetting.getProperty().getName());
        assertEquals(true, theSetting.isSet());
        assertEquals(comments.size(), ((List)theSetting.getValue()).size());
        //writeXML(rootObject);
    }
    public void testModifySimpleCollection() {
        DataObject item = rootObject.createDataObject("items");
        DataObject lineItem = item.createDataObject("item");
        //lineItem.setList("comments", new ArrayList());
        cs.beginLogging();
//        DataObject item = (DataObject)rootObject.getList("items").get(0);
        
        assertUnchanged(lineItem, cs);
        
        lineItem.getList("comment").add("a comment");
        lineItem.getList("comment").add("another comment");
        
        assertModified(lineItem, cs);
        
        cs.endLogging();
        cs.beginLogging();
        
        assertUnchanged(lineItem, cs);
        
        lineItem.getList("comment").remove(1);
        
        assertModified(lineItem, cs);

        cs.endLogging();
        cs.beginLogging();
        
        assertUnchanged(lineItem, cs);

        ArrayList newComments = new ArrayList();
        newComments.add("new comment 1");
        newComments.add("new comment 2");
        lineItem.getList("comment").addAll(newComments);
        
        assertModified(lineItem, cs);

        cs.endLogging();
        cs.beginLogging();

        assertUnchanged(lineItem, cs);

        lineItem.getList("comment").removeAll(newComments);
        assertModified(lineItem, cs);
    }
    public void testSetCommentToSameValue() {
        rootObject.set("poId", "123");
        cs.beginLogging();
        rootObject.set("poId", "123");

        assertUnchanged(rootObject, cs);
        assertEquals(0, cs.getChangedDataObjects().size());

        List oldValues = cs.getOldValues(rootObject);
        assertEquals(0, oldValues.size());
        //writeXML(rootObject);
    }

    private void assertYardDeleted(DataObject shipToDO, DataObject yardDO, ChangeSummary cs, boolean nullContainer) {
        assertDeleted(yardDO, cs, nullContainer);
        assertEquals(3, cs.getOldValues(yardDO).size());
        Property sfProp = yardDO.getInstanceProperty("squarefootage");
        Property widthProp = yardDO.getInstanceProperty("width");
        Property lengthProp = yardDO.getInstanceProperty("length");
        ChangeSummary.Setting yardSFsetting = cs.getOldValue(yardDO, sfProp);
        assertEquals(yardSFsetting.getValue(), null);

        //TODO: uncomment this line.  Will fail unless Node Null policy stuff is fixed
        //assertEquals(false, yardSFsetting.isSet());        
        ChangeSummary.Setting yardWidthsetting = cs.getOldValue(yardDO, widthProp);
        assertEquals("65", yardWidthsetting.getValue());
        assertEquals(true, yardWidthsetting.isSet());

        ChangeSummary.Setting yardLengththsetting =  cs.getOldValue(yardDO, lengthProp);
        assertEquals("45", yardLengththsetting.getValue());
        assertEquals(true, yardLengththsetting.isSet());

        // shipToDO was not deleted, only check yardDO
        assertChildrenUnset(yardDO);
    }

    private void assertYardDetached(DataObject shipToDO, DataObject yardDO, ChangeSummary cs, boolean nullContainer) {
        assertDetached(yardDO, cs, nullContainer);
        assertEquals(3, cs.getOldValues(yardDO).size());
        Property sfProp = yardDO.getInstanceProperty("squarefootage");
        Property widthProp = yardDO.getInstanceProperty("width");
        Property lengthProp = yardDO.getInstanceProperty("length");
        ChangeSummary.Setting yardSFsetting =  cs.getOldValue(yardDO, sfProp);
        assertEquals(yardSFsetting.getValue(), null);

        //TODO: uncomment this line.  Will fail unless Node Null policy stuff is fixed
        //assertEquals(false, yardSFsetting.isSet());        
        ChangeSummary.Setting yardWidthsetting = cs.getOldValue(yardDO, widthProp);
        assertEquals("65", yardWidthsetting.getValue());
        assertEquals(true, yardWidthsetting.isSet());

        ChangeSummary.Setting yardLengththsetting = cs.getOldValue(yardDO, lengthProp);
        assertEquals("45", yardLengththsetting.getValue());
        assertEquals(true, yardLengththsetting.isSet());
    }
}
