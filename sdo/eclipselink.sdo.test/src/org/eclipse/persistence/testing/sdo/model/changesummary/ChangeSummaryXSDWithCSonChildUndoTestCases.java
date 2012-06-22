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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.ValueStore;
import org.eclipse.persistence.sdo.helper.ListWrapper;
import org.eclipse.persistence.testing.sdo.SDOTestCase;
import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;

/**
 * This test suite will exercise mulit-operations on changeSummaries that are below the root as well as inter-changeSummary ops.
 *
 * metamodel (DataObjects only shown)
 * corp1 (s(16 + 15) d(15) + stock(12) = 58 children)
 *    -> sales
 *       -> po[1]
 *          -> CS (15 children)
 *          -> buyer(employee)
 *          -> billTo (address)
 *          -> shipTo (address)
 *             -> yard
 *             -> phone[1]
 *             -> phone[2]
 *          -> items
 *             -> item[1]
 *                -> product
 *                   -> price[1]
 *                   -> price[2]
 *             -> item[2]
 *                -> product
 *                   -> price[1]
 *                   -> price[2]
 *       -> po[2]
 *          -> CS (14 children)
 *          -> billTo (address)
 *          -> shipTo (address)
 *             -> yard
 *             -> phone[1]
 *             -> phone[2]
 *          -> items
 *             -> item[1]
 *                -> product
 *                   -> price[1]
 *                   -> price[2]
 *             -> item[2]
 *                -> product
 *                   -> price[1]
 *                   -> price[2]
 *    -> development
 *       -> employee[1]
 *       -> po[1]
 *          -> CS (14 children)
 *          -> billTo (address)
 *          -> shipTo (address)
 *             -> yard
 *             -> phone[1]
 *             -> phone[2]
 *          -> items
 *             -> item[1]
 *                -> product
 *                   -> price[1]
 *                   -> price[2]
 *             -> item[2]
 *                -> product
 *                   -> price[1]
 *                   -> price[2]
 *    -> stock[1]
 *       -> CS (3 children)
 *       -> results
 *          -> dividend
 *             -> cash
 *    -> stock[2]
 *       -> CS (3 children)
 *       -> results
 *          -> dividend
 *             -> cash
 *    -> stock[3]
 *       -> CS (3 children)
 *       -> results
 *          -> dividend
 *             -> cash
 */
public class ChangeSummaryXSDWithCSonChildUndoTestCases extends ChangeSummaryXSDWithCSonChildProject {

    /**
     * Single Operations:
     *    move (set or detach/set)
     *    swap (detach from index 1 of a filled 2 index list - set to index 2 (previous index 2 has moved to index 1 after the detach)
     *    reset (detach/unset an object, then set it back, or set(again))
     *    add (set)
     *
     * Multi-Operations (4 types):
     *     moving between...
     *        2 cs in same tree
     *           corp1.cs1 and corp1.cs2 (same tree)
     *           corp1.cs1 and outside cs1 (same tree)
     *
     *        2 cs in different trees
     *           corp1.cs1 and corp2.cs1 (different tree)
     *           corp1.cs1 and corp2 outside cs1 (different tree)
     *
     *
     * UC 10.1             move corp1.sales.po1.buyer outside/above cs1 to corp1.sales.emp[1] (same tree/ out of cs)
     * UC 10.2             move corp1.sales.emp[1] inside/down to cs1 as sales.po1.buyer (same tree/ into cs)
     * UC 10.3            move obj inside corp1.sales.po1 to itself
     * UC 10.3.1            move obj inside corp1.cs1 (same tree/same cs)
     * UC 10.3.2             swap obj inside corp1.cs1 (same tree/same cs)
     * UC 10.3.3             reset obj inside corp1.cs1 (same tree/same cs)
     * UC 10.4             move corp1.sales.po2.emp(buyer) outside/above cs1 to corp2 as sales.emp (diff tree/ out of cs)
     * UC 10.5             move corp2.sales.emp inside/down into corp1.sales.po2.buyer (diff tree/ into cs)
     * UC 10.6            move corp2.sales.po1.obj to corp2.sales.obj (different tree/different cs)
     * UC 10.7            move corp1.sales.po1.obj to corp1.sales.po2.obj (same tree/different cs)
     * UC 10.8            move corp1.sales.obj above cs to corp2.sales.obj (different tree/no cs)
     * UC 10.9            move obj corp1.sales.obj above cs to itself
     * UC 10.9.1            move obj above cs (same tree/ no cs)
     * UC 10.9.2            swap obj above cs (same tree/ no cs)
     * UC 10.9.3            reset obj above cs (same tree/ no cs)
     */
    public ChangeSummaryXSDWithCSonChildUndoTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryXSDWithCSonChildUndoTestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() {
        super.setUp();// watch setup redundancy
    }

    // see bug #5878605: SDO: COPYHELPER.COPY() LOGS CS CHANGES - SHOULD SUSPEND LOGGING DURING COPY
    // we dont want changes to occur during the embedded set() calls in copy()
    // this test should be in the following location but this suite has a deeper model in order to fully test the bug see SDOCopyHelperDeepCopyTest.testDeepCopyObjectWithCSLoggingOnDoesNotLogChangesInTheDeepCopy()
    public void testDeepCopyObjectWithCSLoggingOnDoesNotLogChangesInTheDeepCopy() {
        List preOrderList = null;
        int numberOfDataObjectsInSubTree = 0;
        DataObject salesDO = rootObject.getDataObject("sales");
        DataObject salesPO1DO = salesDO.getDataObject("purchaseOrder[1]");

        // verify logging is on
        //ChangeSummary salesPO1DO_cs = salesPO1DO.getChangeSummary(); 
        //assertNotNull(salesPO1DO_cs);
        // turn on logging
        salesPO1CS.beginLogging();
        assertTrue(salesPO1CS.isLogging());
        salesPO2CS.beginLogging();
        assertTrue(salesPO2CS.isLogging());
        developmentPO1CS.beginLogging();
        assertTrue(developmentPO1CS.isLogging());
        stock1CS.beginLogging();
        assertTrue(stock1CS.isLogging());
        stock2CS.beginLogging();
        assertTrue(stock1CS.isLogging());
        stock3CS.beginLogging();
        assertTrue(stock1CS.isLogging());

        // take an object with CS on and deep copy it
        SDODataObject copy = (SDODataObject)copyHelper.copy(rootObject);

        DataObject salesDO2 = copy.getDataObject("sales");
        DataObject salesPO1DO2 = salesDO2.getDataObject("purchaseOrder[1]");
        ChangeSummary salesPO1DO2_cs2 = salesPO1DO2.getChangeSummary();

        // verify that logging is still on
        assertNotNull(salesPO1DO2_cs2);
        assertTrue(salesPO1DO2_cs2.isLogging());
        // verify that we have not logged changes during the copy (we dont have to worry about nested changesummaries)
        assertEquals(0, salesPO1DO2_cs2.getChangedDataObjects().size());// we should not have 4 old settings after a copy
        preOrderList = preOrderTraversalDataObjectList((SDODataObject)salesPO1DO2, false);
        numberOfDataObjectsInSubTree = preOrderList.size();
        // assume that for logging=true copies we should have oldContainer, oldContProperties
        assertEquals(16, numberOfDataObjectsInSubTree);
        assertEquals(numberOfDataObjectsInSubTree, ((SDOChangeSummary)salesPO1DO2_cs2).getOldContainer().size());
        assertEquals(numberOfDataObjectsInSubTree, ((SDOChangeSummary)salesPO1DO2_cs2).getOldContainmentProperty().size());

        DataObject salesPO2DO2 = salesDO2.getDataObject("purchaseOrder[2]");
        ChangeSummary salesPO2DO2_cs2 = salesPO2DO2.getChangeSummary();

        // verify that logging is still on
        assertNotNull(salesPO2DO2_cs2);
        assertTrue(salesPO2DO2_cs2.isLogging());
        assertEquals(0, salesPO2DO2_cs2.getChangedDataObjects().size());// we should not have 5 old settings after a copy
        preOrderList = preOrderTraversalDataObjectList((SDODataObject)salesPO2DO2, false);
        numberOfDataObjectsInSubTree = preOrderList.size();
        assertEquals(15, numberOfDataObjectsInSubTree);
        // assume that for logging=true copies we should have oldContainer, oldContProperties
        assertEquals(numberOfDataObjectsInSubTree, ((SDOChangeSummary)salesPO2DO2_cs2).getOldContainer().size());
        assertEquals(numberOfDataObjectsInSubTree, ((SDOChangeSummary)salesPO2DO2_cs2).getOldContainmentProperty().size());

        DataObject devDO2 = copy.getDataObject("development");
        DataObject devPO1DO2 = devDO2.getDataObject("purchaseOrder[1]");
        ChangeSummary devPO1DO2_cs2 = devPO1DO2.getChangeSummary();

        // verify that logging is still on
        assertNotNull(devPO1DO2_cs2);
        assertTrue(devPO1DO2_cs2.isLogging());
        assertEquals(0, devPO1DO2_cs2.getChangedDataObjects().size());// we should not have 15 old settings after a copy
        // verify that the number of DataObjects is equal to the number of oldContainers for them
        preOrderList = preOrderTraversalDataObjectList((SDODataObject)devPO1DO2, false);
        numberOfDataObjectsInSubTree = preOrderList.size();
        assertEquals(15, numberOfDataObjectsInSubTree);
        // assume that for logging=true copies we should have oldContainer, oldContProperties
        assertEquals(numberOfDataObjectsInSubTree, ((SDOChangeSummary)devPO1DO2_cs2).getOldContainer().size());
        assertEquals(numberOfDataObjectsInSubTree, ((SDOChangeSummary)devPO1DO2_cs2).getOldContainmentProperty().size());
        assertEquals(((SDOChangeSummary)developmentPO1CS).getOldContainer().size(), ((SDOChangeSummary)devPO1DO2_cs2).getOldContainer().size());

        DataObject stock1DO2 = copy.getDataObject("stock[1]");
        ChangeSummary stock1DO2_cs2 = stock1DO2.getChangeSummary();

        // verify that logging is still on
        assertNotNull(stock1DO2_cs2);
        assertTrue(stock1DO2_cs2.isLogging());
        assertEquals(0, stock1DO2_cs2.getChangedDataObjects().size());// we should not have 4 old settings after a copy
        // verify that the number of DataObjects is equal to the number of oldContainers for them
        preOrderList = preOrderTraversalDataObjectList((SDODataObject)stock1DO2, false);
        numberOfDataObjectsInSubTree = preOrderList.size();
        assertEquals(4, numberOfDataObjectsInSubTree);
        // assume that for logging=true copies we should have oldContainer, oldContProperties
        assertEquals(numberOfDataObjectsInSubTree, ((SDOChangeSummary)stock1DO2_cs2).getOldContainer().size());
        assertEquals(numberOfDataObjectsInSubTree, ((SDOChangeSummary)stock1DO2_cs2).getOldContainmentProperty().size());

        DataObject stock2DO2 = copy.getDataObject("stock[2]");
        ChangeSummary stock2DO2_cs2 = stock2DO2.getChangeSummary();

        // verify that logging is still on
        assertNotNull(stock2DO2_cs2);
        assertTrue(stock2DO2_cs2.isLogging());
        assertEquals(0, stock2DO2_cs2.getChangedDataObjects().size());// we should not have 4 old settings after a copy
        // verify that the number of DataObjects is equal to the number of oldContainers for them
        preOrderList = preOrderTraversalDataObjectList((SDODataObject)stock2DO2, false);
        numberOfDataObjectsInSubTree = preOrderList.size();
        assertEquals(4, numberOfDataObjectsInSubTree);
        // assume that for logging=true copies we should have oldContainer, oldContProperties
        assertEquals(numberOfDataObjectsInSubTree, ((SDOChangeSummary)stock2DO2_cs2).getOldContainer().size());
        assertEquals(numberOfDataObjectsInSubTree, ((SDOChangeSummary)stock2DO2_cs2).getOldContainmentProperty().size());

        DataObject stock3DO2 = copy.getDataObject("stock[3]");
        ChangeSummary stock3DO2_cs2 = stock3DO2.getChangeSummary();

        // verify that logging is still on
        assertNotNull(stock3DO2_cs2);
        assertTrue(stock3DO2_cs2.isLogging());
        assertEquals(0, stock3DO2_cs2.getChangedDataObjects().size());// we should not have 4 old settings after a copy
        // verify that the number of DataObjects is equal to the number of oldContainers for them
        preOrderList = preOrderTraversalDataObjectList((SDODataObject)stock3DO2, false);
        numberOfDataObjectsInSubTree = preOrderList.size();
        assertEquals(4, numberOfDataObjectsInSubTree);
        // assume that for logging=true copies we should have oldContainer, oldContProperties
        assertEquals(numberOfDataObjectsInSubTree, ((SDOChangeSummary)stock3DO2_cs2).getOldContainer().size());
        assertEquals(numberOfDataObjectsInSubTree, ((SDOChangeSummary)stock3DO2_cs2).getOldContainmentProperty().size());

    }

    //*  UC 10.3			move obj inside corp1.sales.po1 to itself
    //* UC 10.3.1			move obj inside corp1.cs1 (same tree/same cs)
    //* UC 10.3.2 			swap obj inside corp1.cs1 (same tree/same cs)
    //* UC 10.3.3 			reset obj inside corp1.cs1 (same tree/same cs)
    //* UC 10.6	.1		move corp2.sales.po1.obj to corp2.sales.obj (different tree/different cs) using single op set
    //* UC 10.6.2		move corp2.sales.po1.obj to corp2.sales.obj (different tree/different cs) using dual op detach/set    
    public void testMoveUsingSetBetweenDifferentTreesInDifferentChangeSummarysAndUndo_MoveItemsInTree1CS1toTree2CS2() {
        // save original root for later comparison after undo
        DataObject originalRootDO = copyHelper.copy(rootObject);

        assertTrue(equalityHelper.equal(rootObject, originalRootDO));

        DataObject salesDO = rootObject.getDataObject("sales");
        DataObject salesDO2 = rootObject2.getDataObject("sales");
        DataObject salesPO1DO = salesDO.getDataObject("purchaseOrder[1]");
        DataObject originalSalesPO1DO = copyHelper.copy(salesPO1DO);
        assertTrue(equalityHelper.equal(salesPO1DO, originalSalesPO1DO));

        DataObject salesPO2DO = salesDO.getDataObject("purchaseOrder[2]");
        DataObject salesPO1DO2 = salesDO2.getDataObject("purchaseOrder[1]");

        DataObject salesPO2DO2 = salesDO2.getDataObject("purchaseOrder[2]");

        DataObject salesPO1BuyerDO = salesPO1DO.getDataObject("buyer");
        DataObject salesPO2BuyerDO = salesPO2DO.getDataObject("buyer");
        DataObject salesPO1BuyerDO2 = salesPO1DO2.getDataObject("buyer");
        DataObject salesPO2BuyerDO2 = salesPO2DO2.getDataObject("buyer");

        DataObject salesPO1ItemsDO = salesPO1DO.getDataObject("items");
        DataObject salesPO2ItemsDO = salesPO2DO.getDataObject("items");
        DataObject salesPO1ItemsDO2 = salesPO1DO2.getDataObject("items");
        DataObject salesPO2ItemsDO2 = salesPO2DO2.getDataObject("items");

        DataObject salesPO1Item1DO = salesPO1ItemsDO.getDataObject("item[1]");
        DataObject salesPO1Item2DO = salesPO1ItemsDO.getDataObject("item[2]");
        DataObject salesPO2Item1DO = salesPO2ItemsDO.getDataObject("item[1]");
        DataObject salesPO2Item2DO = salesPO2ItemsDO.getDataObject("item[2]");

        DataObject salesPO1Item1ProductDO = salesPO1Item1DO.getDataObject("product");
        DataObject salesPO1Item2ProductDO = salesPO1Item2DO.getDataObject("product");
        DataObject salesPO2Item1ProductDO = salesPO2Item1DO.getDataObject("product");
        DataObject salesPO2Item2ProductDO = salesPO2Item2DO.getDataObject("product");

        DataObject salesPO1Item1ProductPrice1DO = salesPO1Item1ProductDO.getDataObject("price[1]");
        DataObject salesPO1Item1ProductPrice2DO = salesPO1Item1ProductDO.getDataObject("price[2]");
        DataObject salesPO1Item2ProductPrice1DO = salesPO1Item2ProductDO.getDataObject("price[1]");
        DataObject salesPO1Item2ProductPrice2DO = salesPO1Item2ProductDO.getDataObject("price[2]");
        DataObject salesPO2Item1ProductPrice1DO = salesPO2Item1ProductDO.getDataObject("price[1]");
        DataObject salesPO2Item1ProductPrice2DO = salesPO2Item1ProductDO.getDataObject("price[2]");
        DataObject salesPO2Item2ProductPrice1DO = salesPO2Item2ProductDO.getDataObject("price[1]");
        DataObject salesPO2Item2ProductPrice2DO = salesPO2Item2ProductDO.getDataObject("price[2]");

        //DataObject developmentDO = rootObject.getDataObject("development");
        DataObject stock1DO = rootObject.getDataObject("stock[1]");
        DataObject stock2DO = rootObject.getDataObject("stock[2]");
        DataObject stock3DO = rootObject.getDataObject("stock[3]");

        assertNull(salesDO.getChangeSummary());
        assertNotNull(salesPO1DO.getChangeSummary());
        assertNotNull(salesPO2DO.getChangeSummary());
        assertNotNull(salesPO1ItemsDO.getChangeSummary());
        assertNotNull(salesPO2ItemsDO.getChangeSummary());

        assertNull(salesDO2.getChangeSummary());
        assertNotNull(salesPO1DO2.getChangeSummary());
        assertNotNull(salesPO2DO2.getChangeSummary());
        assertNotNull(salesPO1ItemsDO2.getChangeSummary());
        assertNotNull(salesPO2ItemsDO2.getChangeSummary());

        // preoperation setup
        // delete the original items on the 2nd tree
        salesPO1ItemsDO2.detach();
        DataObject originalSalesPO1DO2 = copyHelper.copy(salesPO1DO2);
        assertTrue(equalityHelper.equal(salesPO1DO2, originalSalesPO1DO2));

        salesPO1CS.beginLogging();
        salesPO2CS.beginLogging();
        stock1CS.beginLogging();
        stock2CS.beginLogging();
        stock3CS.beginLogging();

        salesPO1CS2.beginLogging();
        salesPO2CS2.beginLogging();
        stock1CS2.beginLogging();
        stock2CS2.beginLogging();
        stock3CS2.beginLogging();

        // verify original VS is null and save a copy of current VS for object identity testing after undo
        assertValueStoresInitializedAfterLoggingOn(salesPO1DO);
        assertValueStoresInitializedAfterLoggingOn(salesPO2DO);
        assertValueStoresInitializedAfterLoggingOn(stock1DO);
        assertValueStoresInitializedAfterLoggingOn(stock2DO);
        assertValueStoresInitializedAfterLoggingOn(stock3DO);
        // save a copy of current VS for object identity testing after undo
        ValueStore aCurrentValueStoreAfterLoggingFirstOn = ((SDODataObject)salesPO1DO)._getCurrentValueStore();

        // move from buyer to emp[1]
        salesPO1DO2.set("items", salesPO1ItemsDO);
        //salesDO.set("employee[2]", null); NPE
        boolean wasDeleted = false;
        boolean wasReattachedInContextOfSourceChangeSummary = false;
        boolean wasReattachedInContextOfDestinationChangeSummary = true;

        assertModified(salesPO1DO, salesPO1CS);// buyer was set to null
        assertModified(salesPO1DO2, salesPO1CS2);// buyer was set to null
        assertNull(salesPO1DO.get("items"));
        assertNotNull(salesPO1DO2.get("items"));
        // moving an object outside and reattaching in another tree with another cs clears it from the original cs tree
        assertDetached(salesPO1ItemsDO, salesPO1CS, wasDeleted, wasReattachedInContextOfSourceChangeSummary, false);
        assertCreated(salesPO1ItemsDO, salesPO1CS2);
        //assertUnchanged(someChildofBuyerDO, salesPO1CS);        
        assertEquals(10, salesPO1CS.getChangedDataObjects().size());// 1 + 9
        assertEquals(16, ((SDOChangeSummary)salesPO1CS).getOldContainer().size());
        assertEquals(16, ((SDOChangeSummary)salesPO1CS).getOldContainmentProperty().size());

        assertEquals(2, salesPO1CS2.getChangedDataObjects().size());
        // 20070228: all copies of the CS now contain the full # of old* references
        assertEquals(7, ((SDOChangeSummary)salesPO1CS2).getOldContainer().size());
        assertEquals(7, ((SDOChangeSummary)salesPO1CS2).getOldContainmentProperty().size());

        //assertValueStoresReturnedToStartStateAfterUndoChanges(salesPO1DO, aCurrentValueStoreAfterLoggingFirstOn);
        //writeXML(rootObject, URINAME, TYPENAME, System.out);
        // undo first changeSummary
        assertUndoChangesEqualToOriginal(salesPO1CS, salesPO1DO, originalSalesPO1DO);
        //assertUndoChangesEqualToOriginal(salesPO1CS2, salesPO1DO2, originalSalesPO1DO2);        
        // verify that the objects are returned back to their original stes
        assertNotNull(salesPO1DO.get("items"));// undo should bring back the deepcopy not the original
        assertNotNull(salesPO1DO2.get("items"));

        // undo 2nd changeSummary
        //assertUndoChangesEqualToOriginal(salesPO1CS, salesPO1DO, originalSalesPO1DO);
        assertUndoChangesEqualToOriginal(salesPO1CS2, salesPO1DO2, originalSalesPO1DO2);
        // verify that the objects are returned back to their original stes
        assertNotNull(salesPO1DO.get("items"));
        assertNull(salesPO1DO2.get("items"));
        List preOrderList = preOrderTraversalDataObjectList((SDODataObject)rootObject, false);
        assertEquals(62, preOrderList.size());
    }

    //* UC 10.7			move corp1.sales.po1.obj to corp1.sales.po2.obj (same tree/different cs)
    //* UC 10.8			move corp1.sales.obj above cs to corp2.sales.obj (different tree/no cs)
    //* UC 10.9			move obj corp1.sales.obj above cs to itself
    //* UC 10.9.1			move obj above cs (same tree/ no cs)
    //* UC 10.9.2			swap obj above cs (same tree/ no cs)
    //* UC 10.9.3			reset obj above cs (same tree/ no cs)
    //* UC 10.1	 		move corp1.sales.po1.buyer outside/above cs1 to corp1.sales.emp[1] (same tree/ out of cs)
    public void testMoveWithinSameTreeOutOfChangeSummaryAndUndo_MoveBuyerInCS1upasEmployeeOnParentWithoutCS() {
        // save original root for later comparison after undo
        DataObject originalRootDO = copyHelper.copy(rootObject);
        assertTrue(equalityHelper.equal(rootObject, originalRootDO));

        DataObject salesDO = rootObject.getDataObject("sales");
        DataObject salesPO1DO = salesDO.getDataObject("purchaseOrder[1]");
        DataObject originalSalesPO1DO = copyHelper.copy(salesPO1DO);
        assertTrue(equalityHelper.equal(salesPO1DO, originalSalesPO1DO));
        DataObject salesPO2DO = salesDO.getDataObject("purchaseOrder[2]");

        DataObject salesPO1BuyerDO = salesPO1DO.getDataObject("buyer");
        DataObject salesPO2BuyerDO = salesPO2DO.getDataObject("buyer");

        DataObject salesPO1ItemsDO = salesPO1DO.getDataObject("items");
        DataObject salesPO2ItemsDO = salesPO2DO.getDataObject("items");

        DataObject salesPO1Item1DO = salesPO1ItemsDO.getDataObject("item[1]");
        DataObject salesPO1Item2DO = salesPO1ItemsDO.getDataObject("item[2]");
        DataObject salesPO2Item1DO = salesPO2ItemsDO.getDataObject("item[1]");
        DataObject salesPO2Item2DO = salesPO2ItemsDO.getDataObject("item[2]");

        DataObject salesPO1Item1ProductDO = salesPO1Item1DO.getDataObject("product");
        DataObject salesPO1Item2ProductDO = salesPO1Item2DO.getDataObject("product");
        DataObject salesPO2Item1ProductDO = salesPO2Item1DO.getDataObject("product");
        DataObject salesPO2Item2ProductDO = salesPO2Item2DO.getDataObject("product");

        DataObject salesPO1Item1ProductPrice1DO = salesPO1Item1ProductDO.getDataObject("price[1]");
        DataObject salesPO1Item1ProductPrice2DO = salesPO1Item1ProductDO.getDataObject("price[2]");
        DataObject salesPO1Item2ProductPrice1DO = salesPO1Item2ProductDO.getDataObject("price[1]");
        DataObject salesPO1Item2ProductPrice2DO = salesPO1Item2ProductDO.getDataObject("price[2]");
        DataObject salesPO2Item1ProductPrice1DO = salesPO2Item1ProductDO.getDataObject("price[1]");
        DataObject salesPO2Item1ProductPrice2DO = salesPO2Item1ProductDO.getDataObject("price[2]");
        DataObject salesPO2Item2ProductPrice1DO = salesPO2Item2ProductDO.getDataObject("price[1]");
        DataObject salesPO2Item2ProductPrice2DO = salesPO2Item2ProductDO.getDataObject("price[2]");

        //DataObject developmentDO = rootObject.getDataObject("development");
        DataObject stock1DO = rootObject.getDataObject("stock[1]");
        DataObject stock2DO = rootObject.getDataObject("stock[2]");
        DataObject stock3DO = rootObject.getDataObject("stock[3]");

        assertNull(salesDO.getChangeSummary());
        assertNotNull(salesPO1DO.getChangeSummary());
        assertNotNull(salesPO2DO.getChangeSummary());
        assertNotNull(salesPO1ItemsDO.getChangeSummary());
        assertNotNull(salesPO2ItemsDO.getChangeSummary());

        // preoperation setup
        salesPO1CS.beginLogging();
        salesPO2CS.beginLogging();
        stock1CS.beginLogging();
        stock2CS.beginLogging();
        stock3CS.beginLogging();

        // verify original VS is null and save a copy of current VS for object identity testing after undo
        assertValueStoresInitializedAfterLoggingOn(salesPO1DO);
        assertValueStoresInitializedAfterLoggingOn(salesPO2DO);
        assertValueStoresInitializedAfterLoggingOn(stock1DO);
        assertValueStoresInitializedAfterLoggingOn(stock2DO);
        assertValueStoresInitializedAfterLoggingOn(stock3DO);
        // save a copy of current VS for object identity testing after undo
        ValueStore aCurrentValueStoreAfterLoggingFirstOn = ((SDODataObject)salesPO1DO)._getCurrentValueStore();

        // move from buyer to emp[1]
        salesDO.set("employee[1]", salesPO1BuyerDO);
        //salesDO.set("employee[2]", null); NPE
        boolean wasDeleted = false;
        boolean wasReattachedInContextOfAChangeSummary = false;

        assertModified(salesPO1DO, salesPO1CS);// buyer was set to null
        assertNull(salesPO1DO.get("buyer"));
        // moving an object outside and reattaching above clears it from the cs tree
        assertDetached(salesPO1BuyerDO, salesPO1CS, wasDeleted, wasReattachedInContextOfAChangeSummary, false);
        //assertUnchanged(someChildofBuyerDO, salesPO1CS);        
        assertEquals(2, salesPO1CS.getChangedDataObjects().size());
        assertEquals(16, ((SDOChangeSummary)salesPO1CS).getOldContainer().size());//  from 14 (sales has 2 PO's not 1)
        assertEquals(16, ((SDOChangeSummary)salesPO1CS).getOldContainmentProperty().size());

        //assertValueStoresReturnedToStartStateAfterUndoChanges(salesPO1DO, aCurrentValueStoreAfterLoggingFirstOn);
        //writeXML(rootObject, URINAME, TYPENAME, System.out);
        // undo will only work on the cs subtree, objects modified above - stay modified
        assertUndoChangesEqualToOriginal(salesPO1CS, salesPO1DO, originalSalesPO1DO);
        // verify that the object above the tree is still set
        assertNotNull(salesDO.get("employee[1]"));
        assertNotNull(salesPO1DO.get("buyer"));
        List preOrderList = preOrderTraversalDataObjectList((SDODataObject)rootObject, false);
        assertEquals(63, preOrderList.size());
    }

    //* UC 10.2 			move corp1.sales.emp[1] inside/down to cs1 as sales.po1.buyer (same tree/ into cs)
    public void testMoveWithinSameTreeIntoChildChangeSummaryAndUndo_MoveEmployeeDownIntoCS1asBuyerOfCurrentWithoutCS() {
        // save original root for later comparison after undo
        DataObject originalRootDO = copyHelper.copy(rootObject);
        assertTrue(equalityHelper.equal(rootObject, originalRootDO));

        DataObject salesDO = rootObject.getDataObject("sales");
        DataObject salesPO1DO = salesDO.getDataObject("purchaseOrder[1]");
        DataObject salesPO2DO = salesDO.getDataObject("purchaseOrder[2]");

        DataObject salesPO1BuyerDO = salesPO1DO.getDataObject("buyer");
        DataObject salesPO2BuyerDO = salesPO2DO.getDataObject("buyer");

        DataObject salesPO1ItemsDO = salesPO1DO.getDataObject("items");
        DataObject salesPO2ItemsDO = salesPO2DO.getDataObject("items");

        DataObject salesPO1Item1DO = salesPO1ItemsDO.getDataObject("item[1]");
        DataObject salesPO1Item2DO = salesPO1ItemsDO.getDataObject("item[2]");
        DataObject salesPO2Item1DO = salesPO2ItemsDO.getDataObject("item[1]");
        DataObject salesPO2Item2DO = salesPO2ItemsDO.getDataObject("item[2]");

        DataObject salesPO1Item1ProductDO = salesPO1Item1DO.getDataObject("product");
        DataObject salesPO1Item2ProductDO = salesPO1Item2DO.getDataObject("product");
        DataObject salesPO2Item1ProductDO = salesPO2Item1DO.getDataObject("product");
        DataObject salesPO2Item2ProductDO = salesPO2Item2DO.getDataObject("product");

        DataObject salesPO1Item1ProductPrice1DO = salesPO1Item1ProductDO.getDataObject("price[1]");
        DataObject salesPO1Item1ProductPrice2DO = salesPO1Item1ProductDO.getDataObject("price[2]");
        DataObject salesPO1Item2ProductPrice1DO = salesPO1Item2ProductDO.getDataObject("price[1]");
        DataObject salesPO1Item2ProductPrice2DO = salesPO1Item2ProductDO.getDataObject("price[2]");
        DataObject salesPO2Item1ProductPrice1DO = salesPO2Item1ProductDO.getDataObject("price[1]");
        DataObject salesPO2Item1ProductPrice2DO = salesPO2Item1ProductDO.getDataObject("price[2]");
        DataObject salesPO2Item2ProductPrice1DO = salesPO2Item2ProductDO.getDataObject("price[1]");
        DataObject salesPO2Item2ProductPrice2DO = salesPO2Item2ProductDO.getDataObject("price[2]");

        //DataObject developmentDO = rootObject.getDataObject("development");
        DataObject stock1DO = rootObject.getDataObject("stock[1]");
        DataObject stock2DO = rootObject.getDataObject("stock[2]");
        DataObject stock3DO = rootObject.getDataObject("stock[3]");

        assertNull(salesDO.getChangeSummary());
        assertNotNull(salesPO1DO.getChangeSummary());
        assertNotNull(salesPO2DO.getChangeSummary());
        assertNotNull(salesPO1ItemsDO.getChangeSummary());
        assertNotNull(salesPO2ItemsDO.getChangeSummary());

        // preoperation setup
        // move from buyer to emp[1]
        salesDO.set("employee[1]", salesPO1BuyerDO);
        DataObject originalSalesPO1DO = copyHelper.copy(salesPO1DO);
        assertTrue(equalityHelper.equal(salesPO1DO, originalSalesPO1DO));

        DataObject salesEmp1DO = salesDO.getDataObject("employee[1]");

        salesPO1CS.beginLogging();
        salesPO2CS.beginLogging();
        stock1CS.beginLogging();
        stock2CS.beginLogging();
        stock3CS.beginLogging();

        // verify original VS is null and save a copy of current VS for object identity testing after undo
        assertValueStoresInitializedAfterLoggingOn(salesPO1DO);
        assertValueStoresInitializedAfterLoggingOn(salesPO2DO);
        assertValueStoresInitializedAfterLoggingOn(stock1DO);
        assertValueStoresInitializedAfterLoggingOn(stock2DO);
        assertValueStoresInitializedAfterLoggingOn(stock3DO);
        // save a copy of current VS for object identity testing after undo
        ValueStore aCurrentValueStoreAfterLoggingFirstOn = ((SDODataObject)salesPO1DO)._getCurrentValueStore();

        // move from buyer to emp[1]
        salesPO1DO.set("buyer", salesEmp1DO);
        //salesDO.set("employee[2]", null); NPE
        boolean wasDeleted = false;
        boolean wasReattachedInContextOfAChangeSummary = false;

        assertModified(salesPO1DO, salesPO1CS);// buyer was set to null
        assertEquals(0, salesDO.getList("employee").size());
        // moving an object outside and reattaching above clears it from the cs tree
        assertCreated(salesEmp1DO, salesPO1CS);//, wasDeleted, wasReattachedInContextOfAChangeSummary, false); 
        //assertUnchanged(someChildofBuyerDO, salesPO1CS);        
        assertEquals(2, salesPO1CS.getChangedDataObjects().size());
        assertEquals(15, ((SDOChangeSummary)salesPO1CS).getOldContainer().size());//  from 14 (sales has 2 PO's not 1)
        assertEquals(15, ((SDOChangeSummary)salesPO1CS).getOldContainmentProperty().size());

        //assertValueStoresReturnedToStartStateAfterUndoChanges(salesPO1DO, aCurrentValueStoreAfterLoggingFirstOn);
        //writeXML(rootObject, URINAME, TYPENAME, System.out);
        // undo will only work on the cs subtree, objects modified above - stay modified
        assertUndoChangesEqualToOriginal(salesPO1CS, salesPO1DO, originalSalesPO1DO);
        // verify that the object above the tree is still set
        assertEquals(0, salesDO.getList("employee").size());
        assertNull(salesPO1DO.get("buyer"));

    }

    //* UC 10.4 			move corp1.sales.po2.emp(buyer) outside/above cs1 to corp2 as sales.emp (diff tree/ out of cs)
    public void testMoveBetweenDifferentTreesOutOfChangeSummaryAndUndo_MoveBuyerInCS1UpasEmployeeOnParentWithoutCSInDiffTree() {
        // save original root for later comparison after undo
        DataObject originalRootDO = copyHelper.copy(rootObject);

        assertTrue(equalityHelper.equal(rootObject, originalRootDO));

        DataObject salesDO = rootObject.getDataObject("sales");
        DataObject salesDO2 = rootObject2.getDataObject("sales");
        DataObject salesPO1DO = salesDO.getDataObject("purchaseOrder[1]");
        DataObject salesPO2DO = salesDO.getDataObject("purchaseOrder[2]");
        DataObject salesPO1DO2 = salesDO2.getDataObject("purchaseOrder[1]");
        DataObject salesPO2DO2 = salesDO2.getDataObject("purchaseOrder[2]");

        DataObject salesPO1BuyerDO = salesPO1DO.getDataObject("buyer");
        DataObject salesPO2BuyerDO = salesPO2DO.getDataObject("buyer");
        DataObject salesPO1BuyerDO2 = salesPO1DO2.getDataObject("buyer");
        DataObject salesPO2BuyerDO2 = salesPO2DO2.getDataObject("buyer");

        DataObject salesPO1ItemsDO = salesPO1DO.getDataObject("items");
        DataObject salesPO2ItemsDO = salesPO2DO.getDataObject("items");
        DataObject salesPO1ItemsDO2 = salesPO1DO2.getDataObject("items");
        DataObject salesPO2ItemsDO2 = salesPO2DO2.getDataObject("items");

        DataObject salesPO1Item1DO = salesPO1ItemsDO.getDataObject("item[1]");
        DataObject salesPO1Item2DO = salesPO1ItemsDO.getDataObject("item[2]");
        DataObject salesPO2Item1DO = salesPO2ItemsDO.getDataObject("item[1]");
        DataObject salesPO2Item2DO = salesPO2ItemsDO.getDataObject("item[2]");

        DataObject salesPO1Item1ProductDO = salesPO1Item1DO.getDataObject("product");
        DataObject salesPO1Item2ProductDO = salesPO1Item2DO.getDataObject("product");
        DataObject salesPO2Item1ProductDO = salesPO2Item1DO.getDataObject("product");
        DataObject salesPO2Item2ProductDO = salesPO2Item2DO.getDataObject("product");

        DataObject salesPO1Item1ProductPrice1DO = salesPO1Item1ProductDO.getDataObject("price[1]");
        DataObject salesPO1Item1ProductPrice2DO = salesPO1Item1ProductDO.getDataObject("price[2]");
        DataObject salesPO1Item2ProductPrice1DO = salesPO1Item2ProductDO.getDataObject("price[1]");
        DataObject salesPO1Item2ProductPrice2DO = salesPO1Item2ProductDO.getDataObject("price[2]");
        DataObject salesPO2Item1ProductPrice1DO = salesPO2Item1ProductDO.getDataObject("price[1]");
        DataObject salesPO2Item1ProductPrice2DO = salesPO2Item1ProductDO.getDataObject("price[2]");
        DataObject salesPO2Item2ProductPrice1DO = salesPO2Item2ProductDO.getDataObject("price[1]");
        DataObject salesPO2Item2ProductPrice2DO = salesPO2Item2ProductDO.getDataObject("price[2]");

        //DataObject developmentDO = rootObject.getDataObject("development");
        DataObject stock1DO = rootObject.getDataObject("stock[1]");
        DataObject stock2DO = rootObject.getDataObject("stock[2]");
        DataObject stock3DO = rootObject.getDataObject("stock[3]");

        assertNull(salesDO.getChangeSummary());
        assertNotNull(salesPO1DO.getChangeSummary());
        assertNotNull(salesPO2DO.getChangeSummary());
        assertNotNull(salesPO1ItemsDO.getChangeSummary());
        assertNotNull(salesPO2ItemsDO.getChangeSummary());

        assertNull(salesDO2.getChangeSummary());
        assertNotNull(salesPO1DO2.getChangeSummary());
        assertNotNull(salesPO2DO2.getChangeSummary());
        assertNotNull(salesPO1ItemsDO2.getChangeSummary());
        assertNotNull(salesPO2ItemsDO2.getChangeSummary());

        // preoperation setup
        // move from buyer to emp[1] on tree2
        //salesDO2.set("employee[1]", salesPO1BuyerDO2);
        // delete buyer on tree1
        //salesPO1BuyerDO.delete();
        assertEquals(0, salesDO2.getList("employee").size());

        salesPO1CS.beginLogging();
        salesPO2CS.beginLogging();
        stock1CS.beginLogging();
        stock2CS.beginLogging();
        stock3CS.beginLogging();

        salesPO1CS2.beginLogging();
        salesPO2CS2.beginLogging();
        stock1CS2.beginLogging();
        stock2CS2.beginLogging();
        stock3CS2.beginLogging();

        // verify original VS is null and save a copy of current VS for object identity testing after undo
        assertValueStoresInitializedAfterLoggingOn(salesPO1DO);
        assertValueStoresInitializedAfterLoggingOn(salesPO2DO);
        assertValueStoresInitializedAfterLoggingOn(stock1DO);
        assertValueStoresInitializedAfterLoggingOn(stock2DO);
        assertValueStoresInitializedAfterLoggingOn(stock3DO);
        // save a copy of current VS for object identity testing after undo
        ValueStore aCurrentValueStoreAfterLoggingFirstOn = ((SDODataObject)salesPO1DO)._getCurrentValueStore();

        // move from buyer to emp[1]
        //salesPO1DO.set("buyer", salesEmp1DO2);
        salesDO2.set("employee[1]", salesPO1BuyerDO);
        //salesDO.set("employee[2]", null); NPE
        boolean wasDeleted = false;
        boolean wasReattachedInContextOfAChangeSummary = false;

        assertModified(salesPO1DO, salesPO1CS);// buyer was set to null
        assertNull(salesPO1DO.get("buyer"));
        // moving an object outside and reattaching above clears it from the cs tree
        assertDetached(salesPO1BuyerDO, salesPO1CS, wasDeleted, wasReattachedInContextOfAChangeSummary, false);
        //assertUnchanged(someChildofBuyerDO, salesPO1CS);        
        assertEquals(2, salesPO1CS.getChangedDataObjects().size());
        assertEquals(16, ((SDOChangeSummary)salesPO1CS).getOldContainer().size());//  from 14 (sales has 2 PO's not 1)
        assertEquals(16, ((SDOChangeSummary)salesPO1CS).getOldContainmentProperty().size());

        //assertValueStoresReturnedToStartStateAfterUndoChanges(salesPO1DO, aCurrentValueStoreAfterLoggingFirstOn);
        //writeXML(rootObject, URINAME, TYPENAME, System.out);
        // null xpath for create 
    }

    //* UC 10.5 			move corp2.sales.emp inside/down into corp1.sales.po2.buyer (diff tree/ into cs)
    public void testMoveBetweenDifferentTreesIntoChildChangeSummaryAndUndo_MoveEmployeeDownIntoCS1asBuyerOfCurrentWithoutCSInDiffTree() {
        // save original root for later comparison after undo
        DataObject originalRootDO = copyHelper.copy(rootObject);

        assertTrue(equalityHelper.equal(rootObject, originalRootDO));

        DataObject salesDO = rootObject.getDataObject("sales");
        DataObject salesDO2 = rootObject2.getDataObject("sales");
        DataObject salesPO1DO = salesDO.getDataObject("purchaseOrder[1]");
        DataObject salesPO2DO = salesDO.getDataObject("purchaseOrder[2]");
        DataObject salesPO1DO2 = salesDO2.getDataObject("purchaseOrder[1]");
        DataObject salesPO2DO2 = salesDO2.getDataObject("purchaseOrder[2]");

        DataObject salesPO1BuyerDO = salesPO1DO.getDataObject("buyer");
        DataObject salesPO2BuyerDO = salesPO2DO.getDataObject("buyer");
        DataObject salesPO1BuyerDO2 = salesPO1DO2.getDataObject("buyer");
        DataObject salesPO2BuyerDO2 = salesPO2DO2.getDataObject("buyer");

        DataObject salesPO1ItemsDO = salesPO1DO.getDataObject("items");
        DataObject salesPO2ItemsDO = salesPO2DO.getDataObject("items");
        DataObject salesPO1ItemsDO2 = salesPO1DO2.getDataObject("items");
        DataObject salesPO2ItemsDO2 = salesPO2DO2.getDataObject("items");

        DataObject salesPO1Item1DO = salesPO1ItemsDO.getDataObject("item[1]");
        DataObject salesPO1Item2DO = salesPO1ItemsDO.getDataObject("item[2]");
        DataObject salesPO2Item1DO = salesPO2ItemsDO.getDataObject("item[1]");
        DataObject salesPO2Item2DO = salesPO2ItemsDO.getDataObject("item[2]");

        DataObject salesPO1Item1ProductDO = salesPO1Item1DO.getDataObject("product");
        DataObject salesPO1Item2ProductDO = salesPO1Item2DO.getDataObject("product");
        DataObject salesPO2Item1ProductDO = salesPO2Item1DO.getDataObject("product");
        DataObject salesPO2Item2ProductDO = salesPO2Item2DO.getDataObject("product");

        DataObject salesPO1Item1ProductPrice1DO = salesPO1Item1ProductDO.getDataObject("price[1]");
        DataObject salesPO1Item1ProductPrice2DO = salesPO1Item1ProductDO.getDataObject("price[2]");
        DataObject salesPO1Item2ProductPrice1DO = salesPO1Item2ProductDO.getDataObject("price[1]");
        DataObject salesPO1Item2ProductPrice2DO = salesPO1Item2ProductDO.getDataObject("price[2]");
        DataObject salesPO2Item1ProductPrice1DO = salesPO2Item1ProductDO.getDataObject("price[1]");
        DataObject salesPO2Item1ProductPrice2DO = salesPO2Item1ProductDO.getDataObject("price[2]");
        DataObject salesPO2Item2ProductPrice1DO = salesPO2Item2ProductDO.getDataObject("price[1]");
        DataObject salesPO2Item2ProductPrice2DO = salesPO2Item2ProductDO.getDataObject("price[2]");

        //DataObject developmentDO = rootObject.getDataObject("development");
        DataObject stock1DO = rootObject.getDataObject("stock[1]");
        DataObject stock2DO = rootObject.getDataObject("stock[2]");
        DataObject stock3DO = rootObject.getDataObject("stock[3]");

        assertNull(salesDO.getChangeSummary());
        assertNotNull(salesPO1DO.getChangeSummary());
        assertNotNull(salesPO2DO.getChangeSummary());
        assertNotNull(salesPO1ItemsDO.getChangeSummary());
        assertNotNull(salesPO2ItemsDO.getChangeSummary());

        assertNull(salesDO2.getChangeSummary());
        assertNotNull(salesPO1DO2.getChangeSummary());
        assertNotNull(salesPO2DO2.getChangeSummary());
        assertNotNull(salesPO1ItemsDO2.getChangeSummary());
        assertNotNull(salesPO2ItemsDO2.getChangeSummary());

        // preoperation setup
        // move from buyer to emp[1] on tree2
        salesDO2.set("employee[1]", salesPO1BuyerDO2);
        // delete buyer on tree1
        salesPO1BuyerDO.delete();

        DataObject salesEmp1DO2 = salesDO2.getDataObject("employee[1]");

        salesPO1CS.beginLogging();
        salesPO2CS.beginLogging();
        stock1CS.beginLogging();
        stock2CS.beginLogging();
        stock3CS.beginLogging();

        salesPO1CS2.beginLogging();
        salesPO2CS2.beginLogging();
        stock1CS2.beginLogging();
        stock2CS2.beginLogging();
        stock3CS2.beginLogging();

        // verify original VS is null and save a copy of current VS for object identity testing after undo
        assertValueStoresInitializedAfterLoggingOn(salesPO1DO);
        assertValueStoresInitializedAfterLoggingOn(salesPO2DO);
        assertValueStoresInitializedAfterLoggingOn(stock1DO);
        assertValueStoresInitializedAfterLoggingOn(stock2DO);
        assertValueStoresInitializedAfterLoggingOn(stock3DO);
        // save a copy of current VS for object identity testing after undo
        ValueStore aCurrentValueStoreAfterLoggingFirstOn = ((SDODataObject)salesPO1DO)._getCurrentValueStore();

        // move from buyer to emp[1]
        salesPO1DO.set("buyer", salesEmp1DO2);
        //salesDO.set("employee[2]", null); NPE
        boolean wasDeleted = false;
        boolean wasReattachedInContextOfAChangeSummary = false;

        assertModified(salesPO1DO, salesPO1CS);// buyer was set to null
        assertEquals(0, salesDO.getList("employee").size());
        // moving an object outside and reattaching above clears it from the cs tree
        assertCreated(salesEmp1DO2, salesPO1CS);//, wasDeleted, wasReattachedInContextOfAChangeSummary, false); 
        //assertUnchanged(someChildofBuyerDO, salesPO1CS);        
        assertEquals(2, salesPO1CS.getChangedDataObjects().size());
        assertEquals(15, ((SDOChangeSummary)salesPO1CS).getOldContainer().size());//  from 14 (sales has 2 PO's not 1)
        assertEquals(15, ((SDOChangeSummary)salesPO1CS).getOldContainmentProperty().size());

        //assertValueStoresReturnedToStartStateAfterUndoChanges(salesPO1DO, aCurrentValueStoreAfterLoggingFirstOn);
        //writeXML(rootObject, URINAME, TYPENAME, System.out);
        // null xpath for create 
    }

    // move above cs
    // move containing cs
    // move within cs

    /*
    // move between different CS
    public void testTC1MoveMoveSalesPO1ItemsToDifferentCS_MultiOperation_DetachSetComplexSingleWithManyChildBelowRootAndUndo() {
        // sequence (detach from cs1, set on cs2, detach cs2, (re)set on cs1) - or do undo after the first set on cs2
        // save original root for later comparison after undo
        DataObject originalRootDO = copyHelper.copy(rootObject);
        assertTrue(equalityHelper.equal(rootObject, originalRootDO));

        DataObject salesDO = rootObject.getDataObject("sales");
        DataObject salesPO1DO = salesDO.getDataObject("purchaseOrder[1]");
        DataObject salesPO2DO = salesDO.getDataObject("purchaseOrder[2]");

        DataObject salesPO1ItemsDO = salesPO1DO.getDataObject("items");
        DataObject salesPO2ItemsDO = salesPO2DO.getDataObject("items");

        DataObject salesPO1Item1DO = salesPO1ItemsDO.getDataObject("item[1]");
        DataObject salesPO1Item2DO = salesPO1ItemsDO.getDataObject("item[2]");
        //DataObject salesPO2Item1DO = salesPO2ItemsDO.getDataObject("item[1]");
        //DataObject salesPO2Item2DO = salesPO2ItemsDO.getDataObject("item[2]");

        DataObject salesPO1Item1ProductDO = salesPO1Item1DO.getDataObject("product");
        DataObject salesPO1Item2ProductDO = salesPO1Item2DO.getDataObject("product");
        //DataObject salesPO2Item1ProductDO = salesPO2Item1DO.getDataObject("product");
        //DataObject salesPO2Item2ProductDO = salesPO2Item2DO.getDataObject("product");

        DataObject salesPO1Item1ProductPrice1DO = salesPO1Item1ProductDO.getDataObject("price[1]");
        DataObject salesPO1Item1ProductPrice2DO = salesPO1Item1ProductDO.getDataObject("price[2]");
        DataObject salesPO1Item2ProductPrice1DO = salesPO1Item2ProductDO.getDataObject("price[1]");
        DataObject salesPO1Item2ProductPrice2DO = salesPO1Item2ProductDO.getDataObject("price[2]");
        //DataObject salesPO2Item1ProductPrice1DO = salesPO2Item1ProductDO.getDataObject("price[1]");
        //DataObject salesPO2Item1ProductPrice2DO = salesPO2Item1ProductDO.getDataObject("price[2]");
        //DataObject salesPO2Item2ProductPrice1DO = salesPO2Item2ProductDO.getDataObject("price[1]");
        //DataObject salesPO2Item2ProductPrice2DO = salesPO2Item2ProductDO.getDataObject("price[2]");

        //DataObject developmentDO = rootObject.getDataObject("development");
        DataObject stock1DO = rootObject.getDataObject("stock[1]");
        DataObject stock2DO = rootObject.getDataObject("stock[2]");
        DataObject stock3DO = rootObject.getDataObject("stock[3]");

        assertNull(salesDO.getChangeSummary());
        assertNotNull(salesPO1DO.getChangeSummary());
        assertNotNull(salesPO2DO.getChangeSummary());
        assertNotNull(salesPO1ItemsDO.getChangeSummary());
        assertNotNull(salesPO2ItemsDO.getChangeSummary());

        // preoperation setup
        // remove po2.items in prep for move (outside cs)
        salesPO2ItemsDO.detach();

        salesPO1CS.beginLogging();
        salesPO2CS.beginLogging();
        stock1CS.beginLogging();
        stock2CS.beginLogging();
        stock3CS.beginLogging();
    }
    */
    /*
        // detach above the CS and verify that CS is intact (Helena Yan)
        public void testDetachResetComplexSingleParentOfCSRootAndUndo() {
            // sequence (detach from above cs1 and reset)
            // save original root for later comparison after undo
            DataObject originalRootDO = copyHelper.copy(rootObject);
            assertTrue(equalityHelper.equal(rootObject, originalRootDO));

            DataObject salesDO = rootObject.getDataObject("sales");
            DataObject salesPO1DO = salesDO.getDataObject("purchaseOrder[1]");
            DataObject salesPO2DO = salesDO.getDataObject("purchaseOrder[2]");

            DataObject salesPO1ItemsDO = salesPO1DO.getDataObject("items");
            DataObject salesPO2ItemsDO = salesPO2DO.getDataObject("items");

            DataObject salesPO1Item1DO = salesPO1ItemsDO.getDataObject("item[1]");
            DataObject salesPO1Item2DO = salesPO1ItemsDO.getDataObject("item[2]");
            //DataObject salesPO2Item1DO = salesPO2ItemsDO.getDataObject("item[1]");
            //DataObject salesPO2Item2DO = salesPO2ItemsDO.getDataObject("item[2]");

            DataObject salesPO1Item1ProductDO = salesPO1Item1DO.getDataObject("product");
            DataObject salesPO1Item2ProductDO = salesPO1Item2DO.getDataObject("product");
            //DataObject salesPO2Item1ProductDO = salesPO2Item1DO.getDataObject("product");
            //DataObject salesPO2Item2ProductDO = salesPO2Item2DO.getDataObject("product");

            DataObject salesPO1Item1ProductPrice1DO = salesPO1Item1ProductDO.getDataObject("price[1]");
            DataObject salesPO1Item1ProductPrice2DO = salesPO1Item1ProductDO.getDataObject("price[2]");
            DataObject salesPO1Item2ProductPrice1DO = salesPO1Item2ProductDO.getDataObject("price[1]");
            DataObject salesPO1Item2ProductPrice2DO = salesPO1Item2ProductDO.getDataObject("price[2]");
            //DataObject salesPO2Item1ProductPrice1DO = salesPO2Item1ProductDO.getDataObject("price[1]");
            //DataObject salesPO2Item1ProductPrice2DO = salesPO2Item1ProductDO.getDataObject("price[2]");
            //DataObject salesPO2Item2ProductPrice1DO = salesPO2Item2ProductDO.getDataObject("price[1]");
            //DataObject salesPO2Item2ProductPrice2DO = salesPO2Item2ProductDO.getDataObject("price[2]");

            //DataObject developmentDO = rootObject.getDataObject("development");
            DataObject stock1DO = rootObject.getDataObject("stock[1]");
            DataObject stock2DO = rootObject.getDataObject("stock[2]");
            DataObject stock3DO = rootObject.getDataObject("stock[3]");

            assertNull(salesDO.getChangeSummary());
            assertNotNull(salesPO1DO.getChangeSummary());
            assertNotNull(salesPO2DO.getChangeSummary());
            assertNotNull(salesPO1ItemsDO.getChangeSummary());
            assertNotNull(salesPO2ItemsDO.getChangeSummary());

            // preoperation setup
            // remove po2.items in prep for move (outside cs)
            salesPO2ItemsDO.detach();

            salesPO1CS.beginLogging();
            salesPO2CS.beginLogging();
            stock1CS.beginLogging();
            stock2CS.beginLogging();
            stock3CS.beginLogging();

            // verify original VS is null and save a copy of current VS for object identity testing after undo
            assertValueStoresInitializedAfterLoggingOn(salesPO1DO);
            assertValueStoresInitializedAfterLoggingOn(salesPO2DO);
            assertValueStoresInitializedAfterLoggingOn(stock1DO);
            assertValueStoresInitializedAfterLoggingOn(stock2DO);
            assertValueStoresInitializedAfterLoggingOn(stock3DO);
            // save a copy of current VS for object identity testing after undo
            ValueStore aCurrentValueStoreAfterLoggingFirstOn = ((SDODataObject)salesPO1DO).getCurrentValueStore();

            // 2 operations
            salesPO1DO.unset("items");
            salesPO2DO.set("items", salesPO1ItemsDO);
            // or 1 operation
            //salesPO2DO.set("items", salesPO1ItemsDO);
            boolean wasDeleted = false;

            // verify CS is null on removed trees
            assertChangeSummaryStatusIfClearedIfCSIsAncestor(salesPO1ItemsDO, true);

            // check valueStores
            assertValueStoresCopiedAndSwappedAfterFirstModifyOperation(salesPO1DO, aCurrentValueStoreAfterLoggingFirstOn);

            assertNotNull(salesPO1Item1DO);
            assertNotNull(salesPO1Item2DO);

            assertModified(salesPO1DO, salesPO1CS);
            assertDetached(salesPO1ItemsDO, salesPO1CS); // 20070206 CS is not being deleted
            assertDetached(salesPO1Item1DO, salesPO1CS, wasDeleted);
            assertDetached(salesPO1Item2DO, salesPO1CS, wasDeleted);
            assertDetached(salesPO1Item1ProductDO, salesPO1CS, wasDeleted);
            assertDetached(salesPO1Item1ProductPrice1DO, salesPO1CS, wasDeleted);
            assertDetached(salesPO1Item1ProductPrice2DO, salesPO1CS, wasDeleted);
            assertDetached(salesPO1Item2ProductDO, salesPO1CS, wasDeleted);
            assertDetached(salesPO1Item2ProductPrice1DO, salesPO1CS, wasDeleted);
            assertDetached(salesPO1Item2ProductPrice2DO, salesPO1CS, wasDeleted);
            assertEquals(10, salesPO1CS.getChangedDataObjects().size());
            assertEquals(15, ((SDOChangeSummary)salesPO1CS).getOldContainer().size());//  from 14 (sales has 2 PO's not 1)
            assertEquals(15, ((SDOChangeSummary)salesPO1CS).getOldContainmentProperty().size());

            // set the items back  (in effect doing an undo)
            salesPO1DO.set("items", salesPO2ItemsDO);

            assertUndoChangesEqualToOriginal(salesPO1CS, rootObject, originalRootDO);

            // verify that property is reset
            assertTrue(salesPO2DO.isSet("items"));
            // get back items object
            DataObject itemsDOundone = salesPO1DO.getDataObject("items");
            // compare with original
            assertTrue(equalityHelper.equal(itemsDOundone, salesPO1ItemsDO));

            assertValueStoresReturnedToStartStateAfterUndoChanges(salesPO1DO, aCurrentValueStoreAfterLoggingFirstOn);
        }
    */

    // move between different CS
    public void testMoveSalesPO1ItemsToDifferentCS_MultiOperation_DetachSetComplexSingleWithManyChildBelowRootAndUndo() {
        // sequence (detach from cs1, set on cs2, detach cs2, (re)set on cs1) - or do undo after the first set on cs2
        // save original root for later comparison after undo
        DataObject originalRootDO = copyHelper.copy(rootObject);
        assertTrue(equalityHelper.equal(rootObject, originalRootDO));

        DataObject salesDO = rootObject.getDataObject("sales");
        DataObject salesPO1DO = salesDO.getDataObject("purchaseOrder[1]");
        DataObject salesPO2DO = salesDO.getDataObject("purchaseOrder[2]");

        DataObject salesPO1ItemsDO = salesPO1DO.getDataObject("items");
        DataObject salesPO2ItemsDO = salesPO2DO.getDataObject("items");

        DataObject salesPO1Item1DO = salesPO1ItemsDO.getDataObject("item[1]");
        DataObject salesPO1Item2DO = salesPO1ItemsDO.getDataObject("item[2]");
        DataObject salesPO2Item1DO = salesPO2ItemsDO.getDataObject("item[1]");
        DataObject salesPO2Item2DO = salesPO2ItemsDO.getDataObject("item[2]");

        DataObject salesPO1Item1ProductDO = salesPO1Item1DO.getDataObject("product");
        DataObject salesPO1Item2ProductDO = salesPO1Item2DO.getDataObject("product");
        DataObject salesPO2Item1ProductDO = salesPO2Item1DO.getDataObject("product");
        DataObject salesPO2Item2ProductDO = salesPO2Item2DO.getDataObject("product");

        DataObject salesPO1Item1ProductPrice1DO = salesPO1Item1ProductDO.getDataObject("price[1]");
        DataObject salesPO1Item1ProductPrice2DO = salesPO1Item1ProductDO.getDataObject("price[2]");
        DataObject salesPO1Item2ProductPrice1DO = salesPO1Item2ProductDO.getDataObject("price[1]");
        DataObject salesPO1Item2ProductPrice2DO = salesPO1Item2ProductDO.getDataObject("price[2]");
        DataObject salesPO2Item1ProductPrice1DO = salesPO2Item1ProductDO.getDataObject("price[1]");
        DataObject salesPO2Item1ProductPrice2DO = salesPO2Item1ProductDO.getDataObject("price[2]");
        DataObject salesPO2Item2ProductPrice1DO = salesPO2Item2ProductDO.getDataObject("price[1]");
        DataObject salesPO2Item2ProductPrice2DO = salesPO2Item2ProductDO.getDataObject("price[2]");

        //DataObject developmentDO = rootObject.getDataObject("development");
        DataObject stock1DO = rootObject.getDataObject("stock[1]");
        DataObject stock2DO = rootObject.getDataObject("stock[2]");
        DataObject stock3DO = rootObject.getDataObject("stock[3]");

        assertNull(salesDO.getChangeSummary());
        assertNotNull(salesPO1DO.getChangeSummary());
        assertNotNull(salesPO2DO.getChangeSummary());
        assertNotNull(salesPO1ItemsDO.getChangeSummary());
        assertNotNull(salesPO2ItemsDO.getChangeSummary());

        List preOrderList = preOrderTraversalDataObjectList((SDODataObject)rootObject, false);
        assertEquals(62, preOrderList.size());

        // preoperation setup
        // remove po2.items in prep for move (outside cs)
        salesPO2ItemsDO.detach();

        preOrderList = preOrderTraversalDataObjectList((SDODataObject)rootObject, false);
        assertEquals(53, preOrderList.size());// 9 nodes removed

        salesPO1CS.beginLogging();
        salesPO2CS.beginLogging();
        stock1CS.beginLogging();
        stock2CS.beginLogging();
        stock3CS.beginLogging();

        // verify original VS is null and save a copy of current VS for object identity testing after undo
        assertValueStoresInitializedAfterLoggingOn(salesPO1DO);
        assertValueStoresInitializedAfterLoggingOn(salesPO2DO);
        assertValueStoresInitializedAfterLoggingOn(stock1DO);
        assertValueStoresInitializedAfterLoggingOn(stock2DO);
        assertValueStoresInitializedAfterLoggingOn(stock3DO);
        // save a copy of current VS for object identity testing after undo
        ValueStore aCurrentValueStoreAfterLoggingFirstOn = ((SDODataObject)salesPO1DO)._getCurrentValueStore();

        // 2 operations
        salesPO1DO.unset("items");

        //salesPO2DO.set("items", salesPO1ItemsDO);
        // or 1 operation
        //salesPO2DO.set("items", salesPO1ItemsDO);
        boolean wasDeleted = false;

        // verify CS is not null on removed sub trees
        assertChangeSummaryStatusIfClearedIfCSIsAncestor(salesPO1ItemsDO, true);//false);

        // check valueStores
        assertValueStoresCopiedAndSwappedAfterFirstModifyOperation(salesPO1DO, aCurrentValueStoreAfterLoggingFirstOn);

        assertNotNull(salesPO1Item1DO);
        assertNotNull(salesPO1Item2DO);

        // we have oldSettings on the original container
        assertModified(salesPO1DO, salesPO1CS);
        assertDetached(salesPO1ItemsDO, salesPO1CS);
        assertDetached(salesPO1Item1DO, salesPO1CS, wasDeleted);
        assertDetached(salesPO1Item2DO, salesPO1CS, wasDeleted);
        assertDetached(salesPO1Item1ProductDO, salesPO1CS, wasDeleted);
        assertDetached(salesPO1Item1ProductPrice1DO, salesPO1CS, wasDeleted);
        assertDetached(salesPO1Item1ProductPrice2DO, salesPO1CS, wasDeleted);
        assertDetached(salesPO1Item2ProductDO, salesPO1CS, wasDeleted);
        assertDetached(salesPO1Item2ProductPrice1DO, salesPO1CS, wasDeleted);
        assertDetached(salesPO1Item2ProductPrice2DO, salesPO1CS, wasDeleted);
        assertEquals(10, salesPO1CS.getChangedDataObjects().size());
        assertEquals(16, ((SDOChangeSummary)salesPO1CS).getOldContainer().size());//  from 14 (sales has 2 PO's not 1)
        assertEquals(16, ((SDOChangeSummary)salesPO1CS).getOldContainmentProperty().size());

        //writeXML(rootObject, URINAME, TYPENAME, System.out);
        // move the items to the other cs
        salesPO2DO.set("items", salesPO1ItemsDO);

        //assertUndoChangesEqualToOriginal(salesPO1CS, rootObject, originalRootDO);
        // we have oldSettings (null ones) on the new container
        assertModified(salesPO2DO, salesPO2CS);
        assertCreated(salesPO1ItemsDO, salesPO2CS);
        assertUnchanged(salesPO1Item1DO, salesPO2CS);
        assertUnchanged(salesPO1Item2DO, salesPO2CS);
        assertUnchanged(salesPO1Item1ProductDO, salesPO2CS);
        assertUnchanged(salesPO1Item1ProductPrice1DO, salesPO2CS);
        assertUnchanged(salesPO1Item1ProductPrice2DO, salesPO2CS);
        assertUnchanged(salesPO1Item2ProductDO, salesPO2CS);
        assertUnchanged(salesPO1Item2ProductPrice1DO, salesPO2CS);
        assertUnchanged(salesPO1Item2ProductPrice2DO, salesPO2CS);
        assertEquals(2, salesPO2CS.getChangedDataObjects().size());
        assertEquals(6, ((SDOChangeSummary)salesPO2CS).getOldContainer().size());//  from 14 (sales has 2 PO's not 1)
        assertEquals(6, ((SDOChangeSummary)salesPO2CS).getOldContainmentProperty().size());

        // verify that property is set on new container
        assertTrue(salesPO2DO.isSet("items"));
        // get back items object
        DataObject itemsDOundone = salesPO2DO.getDataObject("items");

        // compare with original
        //assertTrue(equalityHelper.equal(itemsDOundone, salesPO2ItemsDO));
        assertFalse(equalityHelper.equal(itemsDOundone, salesPO2ItemsDO));

        //assertValueStoresReturnedToStartStateAfterUndoChanges(salesPO1DO, aCurrentValueStoreAfterLoggingFirstOn);
        //writeXML(rootObject, URINAME, TYPENAME, System.out);
    }

    // reset
    public void testUnsetSalesPO1ItemsResetAndUndo_MultiOperation_UnsetComplexSingleWithManyChildBelowRootResetAndUndo() {
        // save original root for later comparison after undo
        DataObject originalRootDO = copyHelper.copy(rootObject);
        assertTrue(equalityHelper.equal(rootObject, originalRootDO));

        DataObject salesDO = rootObject.getDataObject("sales");
        DataObject salesPO1DO = salesDO.getDataObject("purchaseOrder[1]");
        DataObject salesPO2DO = salesDO.getDataObject("purchaseOrder[2]");

        DataObject salesPO1ItemsDO = salesPO1DO.getDataObject("items");
        DataObject salesPO2ItemsDO = salesPO2DO.getDataObject("items");

        DataObject salesPO1Item1DO = salesPO1ItemsDO.getDataObject("item[1]");
        DataObject salesPO1Item2DO = salesPO1ItemsDO.getDataObject("item[2]");

        //DataObject salesPO2Item1DO = salesPO2ItemsDO.getDataObject("item[1]");
        //DataObject salesPO2Item2DO = salesPO2ItemsDO.getDataObject("item[2]");
        DataObject salesPO1Item1ProductDO = salesPO1Item1DO.getDataObject("product");
        DataObject salesPO1Item2ProductDO = salesPO1Item2DO.getDataObject("product");

        //DataObject salesPO2Item1ProductDO = salesPO2Item1DO.getDataObject("product");
        //DataObject salesPO2Item2ProductDO = salesPO2Item2DO.getDataObject("product");
        DataObject salesPO1Item1ProductPrice1DO = salesPO1Item1ProductDO.getDataObject("price[1]");
        DataObject salesPO1Item1ProductPrice2DO = salesPO1Item1ProductDO.getDataObject("price[2]");
        DataObject salesPO1Item2ProductPrice1DO = salesPO1Item2ProductDO.getDataObject("price[1]");
        DataObject salesPO1Item2ProductPrice2DO = salesPO1Item2ProductDO.getDataObject("price[2]");

        //DataObject salesPO2Item1ProductPrice1DO = salesPO2Item1ProductDO.getDataObject("price[1]");
        //DataObject salesPO2Item1ProductPrice2DO = salesPO2Item1ProductDO.getDataObject("price[2]");
        //DataObject salesPO2Item2ProductPrice1DO = salesPO2Item2ProductDO.getDataObject("price[1]");
        //DataObject salesPO2Item2ProductPrice2DO = salesPO2Item2ProductDO.getDataObject("price[2]");
        //DataObject developmentDO = rootObject.getDataObject("development");
        DataObject stock1DO = rootObject.getDataObject("stock[1]");
        DataObject stock2DO = rootObject.getDataObject("stock[2]");
        DataObject stock3DO = rootObject.getDataObject("stock[3]");

        assertNull(salesDO.getChangeSummary());
        assertNotNull(salesPO1DO.getChangeSummary());
        assertNotNull(salesPO2DO.getChangeSummary());
        assertNotNull(salesPO1ItemsDO.getChangeSummary());
        assertNotNull(salesPO2ItemsDO.getChangeSummary());

        salesPO1CS.beginLogging();
        salesPO2CS.beginLogging();
        stock1CS.beginLogging();
        stock2CS.beginLogging();
        stock3CS.beginLogging();

        // verify original VS is null and save a copy of current VS for object identity testing after undo
        assertValueStoresInitializedAfterLoggingOn(salesPO1DO);
        assertValueStoresInitializedAfterLoggingOn(salesPO2DO);
        assertValueStoresInitializedAfterLoggingOn(stock1DO);
        assertValueStoresInitializedAfterLoggingOn(stock2DO);
        assertValueStoresInitializedAfterLoggingOn(stock3DO);
        // save a copy of current VS for object identity testing after undo
        ValueStore aCurrentValueStoreAfterLoggingFirstOn = ((SDODataObject)salesPO1DO)._getCurrentValueStore();

        salesPO1DO.unset("items");
        boolean wasDeleted = false;

        // verify CS is null on removed trees
        assertChangeSummaryStatusIfClearedIfCSIsAncestor(salesPO1ItemsDO, true);

        // check valueStores
        assertValueStoresCopiedAndSwappedAfterFirstModifyOperation(salesPO1DO, aCurrentValueStoreAfterLoggingFirstOn);

        assertNotNull(salesPO1Item1DO);
        assertNotNull(salesPO1Item2DO);

        assertModified(salesPO1DO, salesPO1CS);
        assertDetached(salesPO1ItemsDO, salesPO1CS);// 20070206 CS is not being deleted
        assertDetached(salesPO1Item1DO, salesPO1CS, wasDeleted);
        assertDetached(salesPO1Item2DO, salesPO1CS, wasDeleted);
        assertDetached(salesPO1Item1ProductDO, salesPO1CS, wasDeleted);
        assertDetached(salesPO1Item1ProductPrice1DO, salesPO1CS, wasDeleted);
        assertDetached(salesPO1Item1ProductPrice2DO, salesPO1CS, wasDeleted);
        assertDetached(salesPO1Item2ProductDO, salesPO1CS, wasDeleted);
        assertDetached(salesPO1Item2ProductPrice1DO, salesPO1CS, wasDeleted);
        assertDetached(salesPO1Item2ProductPrice2DO, salesPO1CS, wasDeleted);
        assertEquals(10, salesPO1CS.getChangedDataObjects().size());
        assertEquals(16, ((SDOChangeSummary)salesPO1CS).getOldContainer().size());//  from 14 (sales has 2 PO's not 1)
        assertEquals(16, ((SDOChangeSummary)salesPO1CS).getOldContainmentProperty().size());

        // set the items back  (in effect doing an undo)
        salesPO1DO.set("items", salesPO1ItemsDO);

        assertUndoChangesEqualToOriginal(salesPO1CS, rootObject, originalRootDO);

        // verify that property is reset
        assertTrue(salesPO1DO.isSet("items"));
        // get back items object
        DataObject itemsDOundone = salesPO1DO.getDataObject("items");

        // compare with original
        assertTrue(equalityHelper.equal(itemsDOundone, salesPO1ItemsDO));

        assertValueStoresReturnedToStartStateAfterUndoChanges(salesPO1DO, aCurrentValueStoreAfterLoggingFirstOn);
    }
}
