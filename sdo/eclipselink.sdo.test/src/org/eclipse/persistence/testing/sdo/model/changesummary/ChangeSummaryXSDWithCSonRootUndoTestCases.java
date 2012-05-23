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
package org.eclipse.persistence.testing.sdo.model.changesummary;

import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.ValueStore;
import commonj.sdo.DataObject;
import commonj.sdo.Property;

public class ChangeSummaryXSDWithCSonRootUndoTestCases extends ChangeSummaryXSDWithCSonRootProject {

    /**
     * TestCases:
     *  delete, detach, set(attach), set(createDataObject), move, swap, reset, unset,
     */
    public ChangeSummaryXSDWithCSonRootUndoTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryXSDWithCSonRootUndoTestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() {
        super.setUp();// watch setup redundancy
    }

    public void prepareSetItemsAfterDetachUnsetOrDeleteAndUndo_MultiOperation_SetComplexSingleWithManyChildBelowRootAndUndo(DataObject itemsDO,//
                                                                                                                            DataObject item1DO,//
                                                                                                                            DataObject item2DO,//
                                                                                                                            DataObject item1ProductDO,//
                                                                                                                            DataObject item1ProductPrice1DO,//
                                                                                                                            DataObject item1ProductPrice2DO,//
                                                                                                                            DataObject item2ProductDO,//
                                                                                                                            DataObject item2ProductPrice1DO,//
                                                                                                                            DataObject item2ProductPrice2DO,//
                                                                                                                            Property itemsProperty,//
                                                                                                                            DataObject originalRootDO,//        	
                                                                                                                            DataObject itemsDOtoSet,//
                                                                                                                            ValueStore aCurrentValueStoreAfterLoggingFirstOn) {
        // get items property - to speed debugging
        itemsProperty = rootObject.getInstanceProperty("items");
        // take the full DataObject and remove the complex single that we would like to set    	
        itemsDOtoSet = rootObject.getDataObject(itemsProperty);
        // verify logging is off
        assertFalse(cs.isLogging());
        //rootObject.unset(itemsProperty); //unset is not clearing the cs when logging is off
        itemsDOtoSet.detach();

        // verify CS is null on removed trees
        assertChangeSummaryStatusIfClearedIfCSIsAncestor(itemsDOtoSet, true);

        // verify that items is not set
        assertNull(rootObject.getDataObject(itemsProperty));
        assertFalse(rootObject.isSet(itemsProperty));

        // save original root for later comparison after undo
        originalRootDO = copyHelper.copy(rootObject);
        assertTrue(equalityHelper.equal(rootObject, originalRootDO));

        assertNull(rootObject.getDataObject(itemsProperty));
        assertNull(rootObject.getDataObject("items/item[1]"));
        assertNull(rootObject.getDataObject("items/item[2]"));

        cs.beginLogging();
        // verify original VS is null and save a copy of current VS for object identity testing after undo
        assertValueStoresInitializedAfterLoggingOn(rootObject);
        // save a copy of current VS for object identity testing after undo
        aCurrentValueStoreAfterLoggingFirstOn = ((SDODataObject)rootObject)._getCurrentValueStore();

        // set the items back  (in effect doing an undo)
        rootObject.set(itemsProperty, itemsDOtoSet);
        // check valueStores
        assertValueStoresCopiedAndSwappedAfterFirstModifyOperation(rootObject, aCurrentValueStoreAfterLoggingFirstOn);

        itemsDO = rootObject.getDataObject(itemsProperty);
        item1DO = rootObject.getDataObject("items/item[1]");
        item2DO = rootObject.getDataObject("items/item[2]");

        item1ProductDO = item1DO.getDataObject("product");
        item1ProductPrice1DO = item1ProductDO.getDataObject("price[1]");
        item1ProductPrice2DO = item1ProductDO.getDataObject("price[2]");

        item2ProductDO = item2DO.getDataObject("product");
        item2ProductPrice1DO = item2ProductDO.getDataObject("price[1]");
        item2ProductPrice2DO = item2ProductDO.getDataObject("price[2]");

        assertNotNull(item1DO);
        assertNotNull(item2DO);
        assertNotNull(itemsDO);
        assertNotNull(item1ProductDO);
        assertNotNull(item1ProductPrice1DO);
        assertNotNull(item1ProductPrice2DO);
        assertNotNull(item2ProductDO);
        assertNotNull(item2ProductPrice2DO);
        assertNotNull(item2ProductPrice2DO);

        assertModified(rootObject, cs);
        assertCreated(itemsDO, cs);
        assertUnchanged(item1DO, cs);
        assertUnchanged(item2DO, cs);
        assertUnchanged(item1ProductDO, cs);
        assertUnchanged(item1ProductPrice1DO, cs);
        assertUnchanged(item1ProductPrice2DO, cs);
        assertUnchanged(item2ProductDO, cs);
        assertUnchanged(item2ProductPrice1DO, cs);
        assertUnchanged(item2ProductPrice2DO, cs);
        assertEquals(2, cs.getChangedDataObjects().size());// 10 when using delete
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

        assertUndoChangesEqualToOriginal(cs, rootObject, originalRootDO);

        // verify that property is reset
        assertFalse(rootObject.isSet(itemsProperty));
        // get back items object
        DataObject itemsDOundone = rootObject.getDataObject(itemsProperty);
        assertNull(itemsDOundone);

        // compare with original
        //assertTrue(equalityHelper.equal(itemsDOundone, itemsDO));
        assertValueStoresReturnedToStartStateAfterUndoChanges(rootObject, aCurrentValueStoreAfterLoggingFirstOn);
    }

    public void verifySetItemsAfterDetachUnsetOrDeleteAndUndo_MultiOperation_SetComplexSingleWithManyChildBelowRootAndUndo(DataObject itemsDO,//
                                                                                                                           DataObject item1DO,//
                                                                                                                           DataObject item2DO,//
                                                                                                                           DataObject item1ProductDO,//
                                                                                                                           DataObject item1ProductPrice1DO,//
                                                                                                                           DataObject item1ProductPrice2DO,//
                                                                                                                           DataObject item2ProductDO,//
                                                                                                                           DataObject item2ProductPrice1DO,//
                                                                                                                           DataObject item2ProductPrice2DO,//
                                                                                                                           Property itemsProperty,//
                                                                                                                           DataObject originalRootDO,//        	
                                                                                                                           DataObject itemsDOtoSet,//
                                                                                                                           ValueStore aCurrentValueStoreAfterLoggingFirstOn,//
                                                                                                                           boolean isDeleted) {
        assertNotNull(item1DO);
        assertNotNull(item2DO);
        assertNotNull(itemsDO);
        assertNotNull(item1ProductDO);
        assertNotNull(item1ProductPrice1DO);
        assertNotNull(item1ProductPrice2DO);
        assertNotNull(item2ProductDO);
        assertNotNull(item2ProductPrice2DO);
        assertNotNull(item2ProductPrice2DO);

        assertModified(rootObject, cs);
        assertCreated(itemsDO, cs);
        assertUnchanged(item1DO, cs);
        assertUnchanged(item2DO, cs);
        assertUnchanged(item1ProductDO, cs);
        assertUnchanged(item1ProductPrice1DO, cs);
        assertUnchanged(item1ProductPrice2DO, cs);
        assertUnchanged(item2ProductDO, cs);
        assertUnchanged(item2ProductPrice1DO, cs);
        assertUnchanged(item2ProductPrice2DO, cs);
        assertEquals(2, cs.getChangedDataObjects().size());// 10 when using delete
        assertEquals(5, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(5, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

        assertUndoChangesEqualToOriginal(cs, rootObject, originalRootDO);

        // verify that property is reset
        assertFalse(rootObject.isSet(itemsProperty));
        // get back items object
        DataObject itemsDOundone = rootObject.getDataObject(itemsProperty);
        assertNull(itemsDOundone);

        // compare with original
        //assertTrue(equalityHelper.equal(itemsDOundone, itemsDO));
        assertValueStoresReturnedToStartStateAfterUndoChanges(rootObject, aCurrentValueStoreAfterLoggingFirstOn);
    }

    public void testSetItemsAfterDetachAndUndo_MultiOperation_SetComplexSingleWithManyChildBelowRootAndUndo() {
        // get items property - to speed debugging
        Property itemsProperty = rootObject.getInstanceProperty("items");

        // take the full DataObject and remove the complex single that we would like to set    	
        DataObject itemsDOtoSet = rootObject.getDataObject(itemsProperty);

        // verify logging is off
        assertFalse(cs.isLogging());
        //rootObject.unset(itemsProperty); //unset is not clearing the cs when logging is off
        itemsDOtoSet.detach();

        // verify CS is null on removed trees
        assertChangeSummaryStatusIfClearedIfCSIsAncestor(itemsDOtoSet, true);

        // verify that items is not set
        assertNull(rootObject.getDataObject(itemsProperty));
        assertFalse(rootObject.isSet(itemsProperty));

        // save original root for later comparison after undo
        DataObject originalRootDO = copyHelper.copy(rootObject);
        assertTrue(equalityHelper.equal(rootObject, originalRootDO));

        assertNull(rootObject.getDataObject(itemsProperty));
        assertNull(rootObject.getDataObject("items/item[1]"));
        assertNull(rootObject.getDataObject("items/item[2]"));

        cs.beginLogging();
        // verify original VS is null and save a copy of current VS for object identity testing after undo
        assertValueStoresInitializedAfterLoggingOn(rootObject);
        // save a copy of current VS for object identity testing after undo
        ValueStore aCurrentValueStoreAfterLoggingFirstOn = ((SDODataObject)rootObject)._getCurrentValueStore();

        // set the items back  (in effect doing an undo)
        rootObject.set(itemsProperty, itemsDOtoSet);
        // check valueStores
        assertValueStoresCopiedAndSwappedAfterFirstModifyOperation(rootObject, aCurrentValueStoreAfterLoggingFirstOn);

        verifySetItemsAfterDetachUnsetOrDeleteAndUndo_MultiOperation_SetComplexSingleWithManyChildBelowRootAndUndo(rootObject.getDataObject(itemsProperty),//
                                                                                                                   rootObject.getDataObject("items/item[1]"),//
                                                                                                                   rootObject.getDataObject("items/item[2]"),//
                                                                                                                   rootObject.getDataObject("items/item[1]/product"),//
                                                                                                                   rootObject.getDataObject("items/item[1]/product/price[1]"),//
                                                                                                                   rootObject.getDataObject("items/item[1]/product/price[2]"),//
                                                                                                                   rootObject.getDataObject("items/item[2]/product"),//
                                                                                                                   rootObject.getDataObject("items/item[2]/product/price[1]"),//
                                                                                                                   rootObject.getDataObject("items/item[2]/product/price[2]"),//
                                                                                                                   itemsProperty,//
                                                                                                                   originalRootDO,//        	
                                                                                                                   itemsDOtoSet,//
                                                                                                                   aCurrentValueStoreAfterLoggingFirstOn, false);
    }

    public void testSetItemsAfterUnsetAndUndo_MultiOperation_SetComplexSingleWithManyChildBelowRootAndUndo() {
        // get items property - to speed debugging
        Property itemsProperty = rootObject.getInstanceProperty("items");

        // take the full DataObject and remove the complex single that we would like to set    	
        DataObject itemsDOtoSet = rootObject.getDataObject(itemsProperty);

        // verify logging is off
        assertFalse(cs.isLogging());
        rootObject.unset(itemsProperty);//unset is not clearing the cs when logging is off
        // verify CS is null on removed trees
        assertChangeSummaryStatusIfClearedIfCSIsAncestor(itemsDOtoSet, true);

        // verify that items is not set
        assertNull(rootObject.getDataObject(itemsProperty));
        assertFalse(rootObject.isSet(itemsProperty));

        // save original root for later comparison after undo
        DataObject originalRootDO = copyHelper.copy(rootObject);
        assertTrue(equalityHelper.equal(rootObject, originalRootDO));

        assertNull(rootObject.getDataObject(itemsProperty));
        assertNull(rootObject.getDataObject("items/item[1]"));
        assertNull(rootObject.getDataObject("items/item[2]"));

        cs.beginLogging();
        // verify original VS is null and save a copy of current VS for object identity testing after undo
        assertValueStoresInitializedAfterLoggingOn(rootObject);
        // save a copy of current VS for object identity testing after undo
        ValueStore aCurrentValueStoreAfterLoggingFirstOn = ((SDODataObject)rootObject)._getCurrentValueStore();

        // set the items back  (in effect doing an undo)
        rootObject.set(itemsProperty, itemsDOtoSet);
        // check valueStores
        assertValueStoresCopiedAndSwappedAfterFirstModifyOperation(rootObject, aCurrentValueStoreAfterLoggingFirstOn);
        verifySetItemsAfterDetachUnsetOrDeleteAndUndo_MultiOperation_SetComplexSingleWithManyChildBelowRootAndUndo(rootObject.getDataObject(itemsProperty),//
                                                                                                                   rootObject.getDataObject("items/item[1]"),//
                                                                                                                   rootObject.getDataObject("items/item[2]"),//
                                                                                                                   rootObject.getDataObject("items/item[1]/product"),//
                                                                                                                   rootObject.getDataObject("items/item[1]/product/price[1]"),//
                                                                                                                   rootObject.getDataObject("items/item[1]/product/price[2]"),//
                                                                                                                   rootObject.getDataObject("items/item[2]/product"),//
                                                                                                                   rootObject.getDataObject("items/item[2]/product/price[1]"),//
                                                                                                                   rootObject.getDataObject("items/item[2]/product/price[2]"),//
                                                                                                                   itemsProperty,//
                                                                                                                   originalRootDO,//        	
                                                                                                                   itemsDOtoSet,//
                                                                                                                   aCurrentValueStoreAfterLoggingFirstOn, false);
    }

    public void testSetItemsAfterDeleteAndUndo_MultiOperation_SetComplexSingleWithManyChildBelowRootAndUndo() {
        // get items property - to speed debugging
        Property itemsProperty = rootObject.getInstanceProperty("items");

        // save objects before deletion
        DataObject itemsDO = rootObject.getDataObject("items");
        DataObject item1DO = rootObject.getDataObject("items/item[1]");
        DataObject item2DO = rootObject.getDataObject("items/item[2]");

        DataObject item1ProductDO = item1DO.getDataObject("product");
        DataObject item1ProductPrice1DO = item1ProductDO.getDataObject("price[1]");
        DataObject item1ProductPrice2DO = item1ProductDO.getDataObject("price[2]");

        DataObject item2ProductDO = item2DO.getDataObject("product");
        DataObject item2ProductPrice1DO = item2ProductDO.getDataObject("price[1]");
        DataObject item2ProductPrice2DO = item2ProductDO.getDataObject("price[2]");

        // take the full DataObject and remove the complex single that we would like to set    	
        DataObject itemsDOtoSet = rootObject.getDataObject(itemsProperty);

        // verify logging is off
        assertFalse(cs.isLogging());
        //rootObject.unset(itemsProperty); //unset is not clearing the cs when logging is off
        itemsDOtoSet.delete();

        // verify CS is null on removed trees
        assertChangeSummaryStatusIfClearedIfCSIsAncestor(itemsDOtoSet, true);

        // verify that items is not set
        assertNull(rootObject.getDataObject(itemsProperty));
        assertFalse(rootObject.isSet(itemsProperty));

        // save original root for later comparison after undo
        DataObject originalRootDO = copyHelper.copy(rootObject);
        assertTrue(equalityHelper.equal(rootObject, originalRootDO));

        assertNull(rootObject.getDataObject(itemsProperty));
        assertNull(rootObject.getDataObject("items/item[1]"));
        assertNull(rootObject.getDataObject("items/item[2]"));

        cs.beginLogging();
        // verify original VS is null and save a copy of current VS for object identity testing after undo
        assertValueStoresInitializedAfterLoggingOn(rootObject);
        // save a copy of current VS for object identity testing after undo
        ValueStore aCurrentValueStoreAfterLoggingFirstOn = ((SDODataObject)rootObject)._getCurrentValueStore();

        // set the items back  (in effect doing an undo)
        rootObject.set(itemsProperty, itemsDOtoSet);
        // check valueStores
        assertValueStoresCopiedAndSwappedAfterFirstModifyOperation(rootObject, aCurrentValueStoreAfterLoggingFirstOn);

        verifySetItemsAfterDetachUnsetOrDeleteAndUndo_MultiOperation_SetComplexSingleWithManyChildBelowRootAndUndo(rootObject.getDataObject(itemsProperty),//
                                                                                                                   item1DO,//
                                                                                                                   item2DO,//
                                                                                                                   item1ProductDO,//
                                                                                                                   item1ProductPrice1DO,//
                                                                                                                   item1ProductPrice2DO,//
                                                                                                                   item2ProductDO,//
                                                                                                                   item2ProductPrice1DO,//
                                                                                                                   item2ProductPrice2DO,//
                                                                                                                   itemsProperty,//
                                                                                                                   originalRootDO,//        	
                                                                                                                   itemsDOtoSet,//
                                                                                                                   aCurrentValueStoreAfterLoggingFirstOn, true);
    }

    public void testDeleteChainBottomToRoot_ItemsItem2ProductPrice2_MultiOperation_DeleteComplexManyOfComplexSingleOfComplexManyOfComplexSingleInSequence() {
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
        assertValueStoresInitializedAfterLoggingOn(rootObject);
        // save a copy of current VS for object identity testing after undo
        ValueStore aCurrentValueStoreAfterLoggingFirstOn = ((SDODataObject)rootObject)._getCurrentValueStore();

        assertNotNull(itemsDO.getChangeSummary());

        // start deleting at the bottom of the tree and move up
        item2ProductPrice2DO.delete();
        item2ProductDO.delete();// ListWrapper.clear() will call copyElements() twice for this parent of many
        item2DO.delete();
        itemsDO.delete();// ListWrapper.clear() will call copyElements() twice for this parent of many
        boolean wasDeleted = true;

        // verify CS is null on removed trees
        assertChangeSummaryStatusIfClearedIfCSIsAncestor(itemsDO, true);

        // check valueStores
        assertValueStoresCopiedAndSwappedAfterFirstModifyOperation(rootObject, aCurrentValueStoreAfterLoggingFirstOn);

        assertNotNull(item1DO);
        assertNotNull(item2DO);

        assertModified(rootObject, cs);
        assertDeleted(itemsDO, cs, wasDeleted);
        assertDeleted(item1DO, cs, wasDeleted);
        assertDeleted(item2DO, cs, wasDeleted);
        assertDeleted(item1ProductDO, cs, wasDeleted);
        assertDeleted(item1ProductPrice1DO, cs, wasDeleted);
        assertDeleted(item1ProductPrice2DO, cs, wasDeleted);
        assertDeleted(item2ProductDO, cs, wasDeleted);
        assertDeleted(item2ProductPrice1DO, cs, wasDeleted);
        assertDeleted(item2ProductPrice2DO, cs, wasDeleted);
        assertEquals(10, cs.getChangedDataObjects().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());//  from 9
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());// from 9

        assertUndoChangesEqualToOriginal(cs, rootObject, originalRootDO);

        // verify that property is reset
        assertTrue(rootObject.isSet("items"));
        // get back items object
        DataObject itemsDOundone = rootObject.getDataObject("items");

        // compare with original
        assertTrue(equalityHelper.equal(itemsDOundone, itemsDO));

        assertValueStoresReturnedToStartStateAfterUndoChanges(rootObject, aCurrentValueStoreAfterLoggingFirstOn);

    }

    public void testDeleteItemsResetAndUndo_SingleOperation_DeleteComplexSingleWithManyChildBelowRootResetAndUndo() {
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
        assertValueStoresInitializedAfterLoggingOn(rootObject);
        // save a copy of current VS for object identity testing after undo
        ValueStore aCurrentValueStoreAfterLoggingFirstOn = ((SDODataObject)rootObject)._getCurrentValueStore();

        assertNotNull(itemsDO.getChangeSummary());

        itemsDO.delete();
        boolean wasDeleted = true;

        // verify CS is null on removed trees
        assertChangeSummaryStatusIfClearedIfCSIsAncestor(itemsDO, true);

        // check valueStores
        assertValueStoresCopiedAndSwappedAfterFirstModifyOperation(rootObject, aCurrentValueStoreAfterLoggingFirstOn);

        assertNotNull(item1DO);
        assertNotNull(item2DO);

        assertModified(rootObject, cs);
        assertDeleted(itemsDO, cs, wasDeleted);
        assertDeleted(item1DO, cs, wasDeleted);
        assertDeleted(item2DO, cs, wasDeleted);
        assertDeleted(item1ProductDO, cs, wasDeleted);
        assertDeleted(item1ProductPrice1DO, cs, wasDeleted);
        assertDeleted(item1ProductPrice2DO, cs, wasDeleted);
        assertDeleted(item2ProductDO, cs, wasDeleted);
        assertDeleted(item2ProductPrice1DO, cs, wasDeleted);
        assertDeleted(item2ProductPrice2DO, cs, wasDeleted);
        assertEquals(10, cs.getChangedDataObjects().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());//  from 9
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());// from 9

        assertUndoChangesEqualToOriginal(cs, rootObject, originalRootDO);

        // verify that property is reset
        assertTrue(rootObject.isSet("items"));
        // get back items object
        DataObject itemsDOundone = rootObject.getDataObject("items");

        // compare with original
        assertTrue(equalityHelper.equal(itemsDOundone, itemsDO));

        assertValueStoresReturnedToStartStateAfterUndoChanges(rootObject, aCurrentValueStoreAfterLoggingFirstOn);
    }

    public void testDeleteItemsResetAndUndo_MultiOperation_DeleteComplexSingleWithManyChildBelowRootResetAndUndo() {
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
        assertValueStoresInitializedAfterLoggingOn(rootObject);
        // save a copy of current VS for object identity testing after undo
        ValueStore aCurrentValueStoreAfterLoggingFirstOn = ((SDODataObject)rootObject)._getCurrentValueStore();

        assertNotNull(itemsDO.getChangeSummary());

        itemsDO.delete();
        boolean wasDeleted = true;

        // verify CS is null on removed trees
        assertChangeSummaryStatusIfClearedIfCSIsAncestor(itemsDO, true);

        // check valueStores
        assertValueStoresCopiedAndSwappedAfterFirstModifyOperation(rootObject, aCurrentValueStoreAfterLoggingFirstOn);

        assertNotNull(item1DO);
        assertNotNull(item2DO);

        assertModified(rootObject, cs);
        assertDeleted(itemsDO, cs, wasDeleted);
        assertDeleted(item1DO, cs, wasDeleted);
        assertDeleted(item2DO, cs, wasDeleted);
        assertDeleted(item1ProductDO, cs, wasDeleted);
        assertDeleted(item1ProductPrice1DO, cs, wasDeleted);
        assertDeleted(item1ProductPrice2DO, cs, wasDeleted);
        assertDeleted(item2ProductDO, cs, wasDeleted);
        assertDeleted(item2ProductPrice1DO, cs, wasDeleted);
        assertDeleted(item2ProductPrice2DO, cs, wasDeleted);
        assertEquals(10, cs.getChangedDataObjects().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());//  from 9
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());// from 9

        // set the items back  (in effect doing an undo)
        rootObject.set("items", itemsDO);

        assertUndoChangesEqualToOriginal(cs, rootObject, originalRootDO);

        // verify that property is reset
        assertTrue(rootObject.isSet("items"));
        // get back items object
        DataObject itemsDOundone = rootObject.getDataObject("items");

        // compare with original
        assertTrue(equalityHelper.equal(itemsDOundone, itemsDO));

        assertValueStoresReturnedToStartStateAfterUndoChanges(rootObject, aCurrentValueStoreAfterLoggingFirstOn);
    }

    public void testUnsetItemsResetAndUndo_MultiOperation_UnsetComplexSingleWithManyChildBelowRootResetAndUndo() {
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
        assertValueStoresInitializedAfterLoggingOn(rootObject);
        // save a copy of current VS for object identity testing after undo
        ValueStore aCurrentValueStoreAfterLoggingFirstOn = ((SDODataObject)rootObject)._getCurrentValueStore();

        assertNotNull(itemsDO.getChangeSummary());

        rootObject.unset("items");
        boolean wasDeleted = false;

        // verify CS is null on removed trees
        assertChangeSummaryStatusIfClearedIfCSIsAncestor(itemsDO, true);

        // check valueStores
        assertValueStoresCopiedAndSwappedAfterFirstModifyOperation(rootObject, aCurrentValueStoreAfterLoggingFirstOn);

        assertNotNull(item1DO);
        assertNotNull(item2DO);

        assertModified(rootObject, cs);
        assertDetached(itemsDO, cs);// 20070206 CS is not being deleted
        assertDetached(item1DO, cs, wasDeleted);
        assertDetached(item2DO, cs, wasDeleted);
        assertDetached(item1ProductDO, cs, wasDeleted);
        assertDetached(item1ProductPrice1DO, cs, wasDeleted);
        assertDetached(item1ProductPrice2DO, cs, wasDeleted);
        assertDetached(item2ProductDO, cs, wasDeleted);
        assertDetached(item2ProductPrice1DO, cs, wasDeleted);
        assertDetached(item2ProductPrice2DO, cs, wasDeleted);
        assertEquals(10, cs.getChangedDataObjects().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());//  from 9
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());// from 9

        // set the items back  (in effect doing an undo)
        rootObject.set("items", itemsDO);

        assertUndoChangesEqualToOriginal(cs, rootObject, originalRootDO);

        // verify that property is reset
        assertTrue(rootObject.isSet("items"));
        // get back items object
        DataObject itemsDOundone = rootObject.getDataObject("items");

        // compare with original
        assertTrue(equalityHelper.equal(itemsDOundone, itemsDO));

        assertValueStoresReturnedToStartStateAfterUndoChanges(rootObject, aCurrentValueStoreAfterLoggingFirstOn);
    }
}
