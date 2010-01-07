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
/*
   DESCRIPTION
    JUnit testing of the SDO ListWrapper class

   PRIVATE CLASSES

   NOTES

    mfobrien    05/26/06 - SDO-55: (Creation) Enhancements to wrapper functionality
    mfobrien    05/30/06 - SDO-55: JUnit test case adjustments
 */

package org.eclipse.persistence.testing.sdo.model.dataobject;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.helper.ListWrapper;
import org.eclipse.persistence.testing.sdo.SDOTestCase;
import org.eclipse.persistence.exceptions.SDOException;

public class SDODataObjectListWrapperTest extends SDOTestCase {
    protected static final String URINAME = "dataObject";
    protected static final String TYPENAME = "commonj.sdo";
    protected static final String ROOT_PROPERTY_NAME = "root";
    protected static final String CHILD1_PROPERTY_NAME = "items";
    protected static final String CHILD2_PROPERTY_NAME = "item";
    protected static final String CHILD1ITEM_PROPERTY_NAME = "item";
    protected SDODataObject aRoot;
    protected SDODataObject aRoot5;
    protected SDODataObject anEmptyListRoot;

    public SDODataObjectListWrapperTest(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        String xsdString = getXSDString("org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderDeep.xsd");

        // Generate a list of sdo types based on the purchaseOrder.xsd and print
        // out info about them
        List types = xsdHelper.define(xsdString);

        // create a Purchase Order dataObject and write it to XML called
        try {
            FileInputStream inStream = new FileInputStream("org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderNSDeep.xml");
            XMLDocument document = xmlHelper.load(inStream);
            aRoot = (SDODataObject)document.getRootObject();
            inStream.close();
        } catch (IOException e) {
            fail("An IOException occurred during setup.");
        }

        // create a List of new items
        List aNewList = new ArrayList();

        // create item from existing
        SDODataObject anItem = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem.set(anItem.getInstanceProperty("partNum"), "926-ZA");

        SDODataObject anItem2 = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem2.set(anItem.getInstanceProperty("partNum"), "926-ZB");

        SDODataObject anItem3 = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem3.set(anItem.getInstanceProperty("partNum"), "926-ZC");

        // add items to list (to be added to the ListWrapper)
        aNewList.add(anItem);
        aNewList.add(anItem2);
        aNewList.add(anItem3);

        // copy root
        aRoot5 = (SDODataObject)copyHelper.copy(aRoot);
        // get containment node
        ListWrapper aList = (ListWrapper)aRoot5.getList("items/item");

        aList.addAll(1, aNewList);

        // create empty ListWrapper
        anEmptyListRoot = (SDODataObject)copyHelper.copy(aRoot);
        // get containment node
        aList = (ListWrapper)anEmptyListRoot.getList("items/item");
        aList.clear();

    }

    public void testLitWrapperAddMaintainsContainment() {
        // Setup step:
        //log("SDODataObjectListWrapperTest.testListWrapperAddIndexEnd()");
        // create item from existing
        SDODataObject anItem = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem.set(anItem.getInstanceProperty("partNum"), "926-AB");
        // get containment node
        ListWrapper aList = (ListWrapper)aRoot.getList("items/item");

        // Test step:
        //add at position start = length
        aList.add(aList.size(), anItem);
        // verify that container does not reference itself
        assertNotSame(anItem, anItem.getContainer());

        // Validate step:
        // get new dataobject and compare to original
        assertTrue(aList.size() == 3);
        SDODataObject additionalDO = (SDODataObject)aRoot.get("items/item[" + aList.size() + "]");
        assertNotNull(additionalDO);
        assertTrue(additionalDO.equals(anItem));
    }

    // BASE test case scenarios
    // boolean add(Object item)
    public void testListWrapperAdd() {
        // Setup step:        
        //log("SDODataObjectListWrapperTest.testListWrapperAdd()");
        // create item from existing
        SDODataObject anItem = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));///levels2/level2[2]");    	

        // modify object
        anItem.set(anItem.getInstanceProperty("partNum"), "926-AB");
        // get containment node
        ListWrapper aList = (ListWrapper)aRoot.getList("items/item");

        // Test step:
        aList.add(anItem);

        // Validate step:
        // get new dataobject and compare to
        assertTrue(aList.size() == 3);
        SDODataObject additionalDO = (SDODataObject)aRoot.get("items/item[3]");
        assertNotNull(additionalDO);
        assertTrue(additionalDO.equals(anItem));
    }

    // TODO: boolean add(Object item)
    public void testListWrapperAddWithNullProperty() {
        // Setup step:
        //log("SDODataObjectListWrapperTest.testListWrapperAddWithNullProperty()");
        // create item from existing
        SDODataObject anItem = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));///levels2/level2[2]");    	

        // modify object
        anItem.set(anItem.getInstanceProperty("partNum"), "926-AB");
        // get containment node
        ListWrapper aList = (ListWrapper)aRoot.getList("items/item");

        // Test step:
        aList.add(anItem);

        // Validate step:
        // get new dataobject and compare to
        assertTrue(aList.size() == 3);
        SDODataObject additionalDO = (SDODataObject)aRoot.get("items/item[3]");
        assertNotNull(additionalDO);
        assertTrue(additionalDO.equals(anItem));
    }

    // TODO: boolean add(Object item)
    public void testListWrapperAddWithNoContainment() {
        // Setup step:
        //log("SDODataObjectListWrapperTest.testListWrapperAddWithNoContainment()");
        // create item from existing
        SDODataObject anItem = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));///levels2/level2[2]");    	

        // modify object
        anItem.set(anItem.getInstanceProperty("partNum"), "926-AB");
        // get containment node
        ListWrapper aList = (ListWrapper)aRoot.getList("items/item");

        // Test step:
        aList.add(anItem);

        // Validate step:
        // get new dataobject and compare to
        assertTrue(aList.size() == 3);
        SDODataObject additionalDO = (SDODataObject)aRoot.get("items/item[3]");
        assertNotNull(additionalDO);
        assertTrue(additionalDO.equals(anItem));
    }

    // void add(int position, Object item)
    public void testListWrapperAddIndexEnd() {
        // Setup step:
        //log("SDODataObjectListWrapperTest.testListWrapperAddIndexEnd()");
        // create item from existing
        SDODataObject anItem = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem.set(anItem.getInstanceProperty("partNum"), "926-AB");
        // get containment node
        ListWrapper aList = (ListWrapper)aRoot.getList("items/item");

        // Test step:
        //add at position start = length
        aList.add(aList.size(), anItem);

        // Validate step:
        // get new dataobject and compare to original
        assertTrue(aList.size() == 3);
        SDODataObject additionalDO = (SDODataObject)aRoot.get("items/item[" + aList.size() + "]");
        assertNotNull(additionalDO);
        assertTrue(additionalDO.equals(anItem));
    }

    public void testListWrapperAddIndex0() {
        // Setup step:
        //log("SDODataObjectListWrapperTest.testListWrapperAddIndex0()");
        // create item from existing
        SDODataObject anItem = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem.set(anItem.getInstanceProperty("partNum"), "926-AB");
        // get containment node
        ListWrapper aList = (ListWrapper)aRoot.getList("items/item");

        // Test step:
        //add at position start = 0
        aList.add(0, anItem);

        // Validate step:
        // get new dataobject and compare to original
        assertTrue(aList.size() == 3);
        SDODataObject additionalDO = (SDODataObject)aRoot.get("items/item[1]");
        assertNotNull(additionalDO);
        assertTrue(additionalDO.equals(anItem));
    }

    public void testListWrapperAddIndexPastEnd() {
        // Setup step:
        //log("SDODataObjectListWrapperTest.testListWrapperAddIndexPastEnd()");
        // create item from existing
        SDODataObject anItem = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem.set(anItem.getInstanceProperty("partNum"), "926-AB");
        // get containment node
        ListWrapper aList = (ListWrapper)aRoot.getList("items/item");

        // Test step:
        //add at position past the end
        aList.add(aList.size() + 2, anItem);

        // Validate step:
        // verify list unchanged
        assertTrue(aList.size() == 2);
    }

    // boolean addAll(Collection items)
    public void testListWrapperAddAll() {
        // Setup step:
        //log("SDODataObjectListWrapperTest.testListWrapperAddAll()");
        // create a List of new items
        List aNewList = new ArrayList();

        // create item from existing
        SDODataObject anItem1 = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem1.set(anItem1.getInstanceProperty("partNum"), "926-ZA");
        SDODataObject anItem2 = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem2.set(anItem1.getInstanceProperty("partNum"), "926-ZB");
        SDODataObject anItem3 = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem3.set(anItem1.getInstanceProperty("partNum"), "926-ZC");

        // add items to list (to be added to the ListWrapper)
        aNewList.add(anItem1);
        aNewList.add(anItem2);
        aNewList.add(anItem3);

        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot.getList("items/item");

        // Test step:
        assertTrue(aList.addAll(aNewList));

        // Validate step: (the 3 existing objects should have been clear()ed with the new ones added)
        assertTrue(aList.size() == (aNewList.size() + 2));
        SDODataObject additionalDO = (SDODataObject)aRoot.get("items/item[5]");
        assertNotNull(additionalDO);
        //assertTrue(additionalDO.getProperty("partNum").getName().equals("926-ZC"));
        assertTrue(additionalDO.equals(anItem3));
    }

    // boolean addAll(Collection items)
    public void testListWrapperAddAllWithEmptyCollection() {
        // Setup step:
        //log("SDODataObjectListWrapperTest.testListWrapperAddAllWithEmptyCollection()");
        // create a List of new items
        List aNewList = new ArrayList();

        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot.getList("items/item");

        // Test step:
        int originalSize = aList.size();
        assertFalse(aList.addAll(aNewList));

        // Validate step:
        assertTrue(aList.size() == originalSize);
    }

    // boolean addAll(int position, Collection items)
    public void testListWrapperAddAllIndex0() {
        // Setup step:
        //log("SDODataObjectListWrapperTest.testListWrapperAddAllIndex0()");
        // create a List of new items
        List aNewList = new ArrayList();

        // create item from existing
        SDODataObject anItem1 = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem1.set(anItem1.getInstanceProperty("partNum"), "926-ZA");
        SDODataObject anItem2 = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem2.set(anItem1.getInstanceProperty("partNum"), "926-ZB");
        SDODataObject anItem3 = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem3.set(anItem1.getInstanceProperty("partNum"), "926-ZC");
        // add items to list (to be added to the ListWrapper)
        aNewList.add(anItem1);
        aNewList.add(anItem2);
        aNewList.add(anItem3);

        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot.getList("items/item");

        // Test step: (add at the start of the current list)
        int originalSize = aList.size();
        assertTrue(aList.addAll(0, aNewList));
        // Validate step:
        assertTrue(aList.size() == (originalSize + aNewList.size()));
        SDODataObject additionalDO = (SDODataObject)aRoot.get("items/item[1]");
        assertNotNull(additionalDO);
        //assertTrue(additionalDO.getProperty("partNum").getName().equals("926-ZC"));
        assertTrue(additionalDO.equals(anItem1));
    }

    // boolean addAll(int position, Collection items)
    public void testListWrapperAddAllIndexInternal() {
        // Setup step:
        //log("SDODataObjectListWrapperTest.testListWrapperAddAllIndexInternal()");
        // create a List of new items
        List aNewList = new ArrayList();

        // create item from existing
        SDODataObject anItem1 = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem1.set(anItem1.getInstanceProperty("partNum"), "926-ZA");
        SDODataObject anItem2 = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem2.set(anItem1.getInstanceProperty("partNum"), "926-ZB");
        SDODataObject anItem3 = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem3.set(anItem1.getInstanceProperty("partNum"), "926-ZC");

        // add items to list (to be added to the ListWrapper)
        aNewList.add(anItem1);
        aNewList.add(anItem2);
        aNewList.add(anItem3);

        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot.getList("items/item");

        // Test step: (add in the middle of the current list)
        int originalSize = aList.size();
        assertTrue(aList.addAll(originalSize, aNewList));

        // Validate step:
        assertTrue(aList.size() == (originalSize + aNewList.size()));
        SDODataObject additionalDO = (SDODataObject)aRoot.get("items/item[" + (originalSize + aNewList.size()) + "]");
        assertNotNull(additionalDO);
        //assertTrue(additionalDO.getProperty("partNum").getName().equals("926-ZC"));
        assertTrue(additionalDO.equals(anItem3));
    }

    // boolean addAll(int position, Collection items)
    public void testListWrapperAddAllIndexEnd() {
        // Setup step:
        //log("SDODataObjectListWrapperTest.testListWrapperAddAllIndexEnd()");
        // create a List of new items
        List aNewList = new ArrayList();

        // create item from existing
        SDODataObject anItem = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem.set(anItem.getInstanceProperty("partNum"), "926-ZA");
        SDODataObject anItem2 = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem2.set(anItem.getInstanceProperty("partNum"), "926-ZB");
        SDODataObject anItem3 = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem3.set(anItem.getInstanceProperty("partNum"), "926-ZC");

        // add items to list (to be added to the ListWrapper)
        aNewList.add(anItem);
        aNewList.add(anItem2);
        aNewList.add(anItem3);

        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot.getList("items/item");

        // Test step: (add at the end of the current list)
        int originalSize = aList.size();
        assertTrue(aList.addAll(1, aNewList));
        // Validate step:
        assertTrue(aList.size() == (originalSize + aNewList.size()));
        SDODataObject additionalDO = (SDODataObject)aRoot.get("items/item[4]");
        assertNotNull(additionalDO);
        //assertTrue(additionalDO.getProperty("partNum").getName().equals("926-ZC"));
        assertTrue(additionalDO.equals(anItem3));
    }

    // boolean addAll(Collection items)
    public void testListWrapperAddAllIndexWrongWithEmptyCollection() {
        // implementation may check for empty collection or wrong list in any order
        // Setup step:
        //log("SDODataObjectListWrapperTest.testListWrapperAddAllIndexWrongWithEmptyCollection()");
        // create a List of new items
        List aNewList = new ArrayList();

        // create item from existing
        SDODataObject anItem = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem.set(anItem.getInstanceProperty("partNum"), "926-ZA");
        SDODataObject anItem2 = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem2.set(anItem.getInstanceProperty("partNum"), "926-ZB");
        SDODataObject anItem3 = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem3.set(anItem.getInstanceProperty("partNum"), "926-ZC");

        // add items to list (to be added to the ListWrapper)
        aNewList.add(anItem);
        aNewList.add(anItem2);
        aNewList.add(anItem3);

        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot.getList("items/item");

        // Test step:
        int originalSize = aList.size();
        assertFalse(aList.addAll(-1, aNewList));

        // Validate step:
        assertTrue(aList.size() == originalSize);
    }

    // boolean addAll(index, Collection items)
    public void testListWrapperAddAllIndexWrong() {
        // implementation may check for empty collection or wrong list in any order
        // Setup step:
        //log("SDODataObjectListWrapperTest.testListWrapperAddAllIndexWrong()");
        // create a List of new items
        List aNewList = new ArrayList();

        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot.getList("items/item");

        // Test step:
        int originalSize = aList.size();
        assertFalse(aList.addAll(-1, aNewList));

        // Validate step:
        assertTrue(aList.size() == originalSize);
    }

    // boolean addAll(index, Collection items)
    public void testListWrapperAddAllIndexEmptyCollection() {
        // implementation may check for empty collection or wrong list in any order
        // Setup step:
        //log("SDODataObjectListWrapperTest.testListWrapperAddAllIndexEmptyCollection()");
        // create a List of new items
        List aNewList = new ArrayList();

        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot.getList("items/item");

        // Test step:
        int originalSize = aList.size();
        assertFalse(aList.addAll(0, aNewList));

        // Validate step:
        assertTrue(aList.size() == originalSize);
    }

    // void clear()
    public void testListWrapperClear() {
        // setup
        //log("SDODataObjectListWrapperTest.testListWrapperClear()");
        // get containment node
        ListWrapper aList = (ListWrapper)aRoot.getList("items/item");

        // Test step:
        assertTrue(aList.size() > 0);
        aList.clear();
        assertTrue(aList.size() == 0);
    }

    // boolean equals(Object aList)
    public void testListWrapperEquals() {
        //log("SDODataObjectListWrapperTest.testListWrapperAddAllIndex()");
        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot5.getList("items/item");

        // get an item and its type
        SDODataObject anItem = (SDODataObject)aRoot5.get("items/item[1]");

        // Test step:
        // Validation step:
        // verify an entry
        assertTrue(aList.get(0).equals(anItem));

    }

    // int indexOf(Object item)
    public void testListWrapperIndexOf() {
        //log("SDODataObjectListWrapperTest.testListWrapperIndexOf()");
    }

    // boolean isEmpty()
    public void testListWrapperIsEmptyFilled() {
        //log("SDODataObjectListWrapperTest.testListWrapperIsEmptyFilled()");
        // get containment node
        ListWrapper aList = (ListWrapper)aRoot.getList("items/item");

        // Test step:
        assertTrue(aList.size() > 0);
        assertTrue(!aList.isEmpty());
        aList.clear();
        assertTrue(aList.size() == 0);
        assertTrue(aList.isEmpty());
    }

    public void testListWrapperIsEmptyEmpty() {
        //log("SDODataObjectListWrapperTest.testListWrapperIsEmptyEmpty()");
        // get containment node
        ListWrapper aList = (ListWrapper)anEmptyListRoot.getList("items/item");

        // Test step:
        assertTrue(aList.size() == 0);
        assertTrue(aList.isEmpty());
    }

    // Object get(int position)
    public void testListWrapperGetIndex0() {
        //log("SDODataObjectListWrapperTest.testListWrapperGetIndex0()");
        SDODataObject anItem = (SDODataObject)aRoot5.get("items/item[1]");

        //String anID = anItem.getProperty("partNum").toString();//,"926-ZC"
        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot5.getList("items/item");

        // Test step: (remove at start)
        SDODataObject anObject = (SDODataObject)aList.get(0);
        assertNotNull(anObject);
        assertTrue(anObject.equals(anItem));
    }

    // Object get(int position)
    public void testListWrapperGetIndexEnd() {
        //log("SDODataObjectListWrapperTest.testListWrapperGetEnd()");
        SDODataObject anItem = (SDODataObject)aRoot5.get("items/item[5]");

        //String anID = anItem.getProperty("partNum").toString();//,"926-ZC"
        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot5.getList("items/item");

        // Test step: (remove at start)
        SDODataObject anObject = (SDODataObject)aList.get(4);
        assertNotNull(anObject);
        assertTrue(anObject.equals(anItem));
    }

    // Object get(int position)
    public void testListWrapperGetIndexOutOfBoundsFailure() {
        //log("SDODataObjectListWrapperTest.testListWrapperGetIndexOutOfBoundsFailure()");
        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot5.getList("items/item");

        // Test step:
        SDODataObject anObject = null;
        try {
            anObject = (SDODataObject)aList.get(6);
        } catch (IndexOutOfBoundsException e) {
        } finally {
            // catch passes and failures
            assertNull(anObject);
        }
    }

    // Object get(int position)
    public void testListWrapperGetFromEmptyList() {
        //log("SDODataObjectListWrapperTest.testListWrapperGetFromEmptyList()");
        // get containment node    	
        ListWrapper aList = (ListWrapper)anEmptyListRoot.getList("items/item");

        // Test step:
        SDODataObject anObject = null;
        try {
            anObject = (SDODataObject)aList.get(6);
        } catch (IndexOutOfBoundsException e) {
        } finally {
            // catch passes and failures
            assertNull(anObject);
        }
    }
   
    // Iterator iterator()
    // TODO: 20060906 bidirectional
    public void testListWrapperIterator() {
        //log("SDODataObjectListWrapperTest.testListWrapperIterator()");
        List aList1 = (List)aRoot5.getList("items/item");

        // Test step:
        // start at index 2 in the bidirectional iterator
        Iterator anIterator = aList1.iterator();
        while (anIterator.hasNext()) {
            anIterator.next();
        }
    }

    // int lastIndexOf(Object item)
    public void testListWrapperLastIndexOfInternalNoDupl() {
        //log("SDODataObjectListWrapperTest.testListWrapperLastIndexOfInternalNoDupl()");
        List aList1 = (List)aRoot5.getList("items/item");
        SDODataObject anItem = (SDODataObject)aRoot5.get("items/item[2]");
        int anIndex = aList1.lastIndexOf(anItem);
        assertTrue(anIndex == (2 - 1));
    }

    // int lastIndexOf(Object item)
    public void testListWrapperLastIndexOfNotExisting() {
        //log("SDODataObjectListWrapperTest.testListWrapperLastIndexOfNotExisting()");
        List aList1 = (List)aRoot5.getList("items/item");
        try {
            SDODataObject anItem = (SDODataObject)aRoot.get("items/item[4]");
        } catch (IndexOutOfBoundsException e) {
        	// get() should not throw exception (SDO 2.1 Spec)
            fail("An IndexOutOfBoundsException occured but was expected.");
        }

        //Changed test since out of bounds now throws an exception SDO Jira 81
        // int anIndex = aList1.lastIndexOf(anItem);
        //assertTrue(anIndex == -1);
    }

    // ListIterator listIterator()
    public void testListWrapperListIteratorIndirect() {
        //log("SDODataObjectListWrapperTest.testListWrapperListIteratorIndirect()");
        List aList1 = (List)aRoot5.getList("items/item");
        List aList2 = (List)aRoot5.getList("items/item");

        // testing equality will invoke the ListWrapper.listIterator() function
        assertEquals(aList2, aList1);
    }

    // ListIterator listIterator()
    // TODO: 20060906 bidirectional
    public void testListWrapperListIteratorDirect() {
        //log("SDODataObjectListWrapperTest.testListWrapperListIteratorDirect()");
        List aList1 = (List)aRoot5.getList("items/item");

        // Test step:
        // start at index 0 in the bidirectional iterator
        ListIterator aListIterator = aList1.listIterator();
        while (aListIterator.hasNext()) {
            aListIterator.next();
        }
    }

    // ListIterator listIterator(int position)
    // TODO: 20060906 bidirectional
    public void testListWrapperListIteratorIndex() {
        //log("SDODataObjectListWrapperTest.testListWrapperListIteratorIndex()");
        List aList1 = (List)aRoot5.getList("items/item");

        // Test step:
        // start at index 2 in the bidirectional iterator
        ListIterator aListIterator = aList1.listIterator(2);
        while (aListIterator.hasNext()) {
            aListIterator.next();
        }
    }

    // Object remove(int index)
    public void testListWrapperRemoveIndex0() {
        //log("SDODataObjectListWrapperTest.testListWrapperRemoveIndex0()");
        // Setup step:
        // get original object before removal
        SDODataObject anItem = (SDODataObject)aRoot.get("items/item[1]");

        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot.getList("items/item");

        // Test step: (remove at start)
        int originalSize = aList.size();
        SDODataObject originalDO = (SDODataObject)aList.remove(0);
        assertTrue(aList.size() == (originalSize - 1));
        assertNotNull(originalDO);
        assertTrue(originalDO.equals(anItem));
    }

    // Object remove(int index)
    public void testListWrapperRemoveIndexEnd() {
        //log("SDODataObjectListWrapperTest.testListWrapperRemoveIndexEnd()");
        // Setup step:
        // get original object before removal
        SDODataObject anItem = (SDODataObject)aRoot.get("items/item[2]");

        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot.getList("items/item");

        // Test step: (remove at start)
        int originalSize = aList.size();
        SDODataObject originalDO = (SDODataObject)aList.remove(aList.size() - 1);
        assertTrue(aList.size() == (originalSize - 1));
        assertNotNull(originalDO);
        assertTrue(originalDO.equals(anItem));
    }

    // Object remove(int index)
    public void testListWrapperRemoveIndexPastEnd() {
        //log("SDODataObjectListWrapperTest.testListWrapperRemoveIndexPastEnd()");
        // Setup step:
        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot.getList("items/item");

        // Test step: (remove at start)
        int originalSize = aList.size();
        SDODataObject originalDO = (SDODataObject)aList.remove(originalSize + 2);
        assertTrue(aList.size() == originalSize);
        assertNull(originalDO);
    }

    // Object remove(int index)
    public void testListWrapperRemoveIndexFromEmptyList() {
        //log("SDODataObjectListWrapperTest.testListWrapperRemoveIndexFromEmptyList()");
        // Setup step:
        // remove all list items
        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot.getList("items/item");
        aList.remove(0);
        aList.remove(0);

        // Test step: (remove at start)
        // list is empty first
        assertTrue(aList.size() == 0);
        SDODataObject originalDO = (SDODataObject)aList.remove(0);
        assertNull(originalDO);
    }

    // boolean remove(Object item)
    public void testListWrapperRemoveObject() {
        //log("SDODataObjectListWrapperTest.testListWrapperRemoveObject()");
    }

    // TODO: boolean remove(Object item)
    public void testListWrapperRemoveObjectWithNullProperty() {
        //log("SDODataObjectListWrapperTest.testListWrapperRemoveObjectWithNullProperty()");
    }

    // TODO: boolean remove(Object item)
    public void testListWrapperRemoveObjectWithNoContainment() {
        //log("SDODataObjectListWrapperTest.testListWrapperRemoveObjectWithNoContainment()");
    }

    // boolean removeAll(Collection items)
    public void testListWrapperRemoveAllUsingAllItems() {
        //log("SDODataObjectListWrapperTest.testListWrapperRemoveAllUsingAllItems()");
        // Setup step: (remove 2 items from a list of 5)
        // get an Item DO
        SDODataObject anItemDO1 = (SDODataObject)aRoot5.get("items/item[1]");
        SDODataObject anItemDO2 = (SDODataObject)aRoot5.get("items/item[2]");

        // Setup a list of items to keep
        ArrayList anArrayList = new ArrayList();
        anArrayList.add(anItemDO1);
        anArrayList.add(anItemDO2);
        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot5.getList("items/item");

        // keep an item that should be removed and check for it later
        //SDODataObject aRemovableItem = (SDODataObject)aRoot5.get("items/item[5]");
        // Test: remove all the objects in the list from the ListWrapper
        int originalSize = aList.size();
        boolean isModified = aList.removeAll(anArrayList);
        int modifiedSize = aList.size();

        // Validate step
        assertTrue(isModified);
        assertTrue(modifiedSize == (originalSize - anArrayList.size()));

        // check retained items
        //SDODataObject remainingDO = (SDODataObject)aRoot5.get("items/item[3]");
        // check removed items
        //assertFalse(aList.contains(aRemovableItem));
    }

    // boolean retainAll(Collection itemsToKeep)
    public void testListWrapperRetainAllPartial() {
        //log("SDODataObjectListWrapperTest.testListWrapperRetainAllPartial()");
        // Setup step: (remove 2 items from a list of 5)
        // get an Item DO
        SDODataObject anItemDO1 = (SDODataObject)aRoot5.get("items/item[1]");
        SDODataObject anItemDO2 = (SDODataObject)aRoot5.get("items/item[2]");

        // Setup a list of items to keep
        ArrayList anArrayList = new ArrayList();
        anArrayList.add(anItemDO1);
        anArrayList.add(anItemDO2);
        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot5.getList("items/item");

        // keep an item that should be removed and check for it later
        SDODataObject aRemovableItem = (SDODataObject)aRoot5.get("items/item[5]");

        // Test: remove all the objects in the list from the ListWrapper
        //int originalSize = aList.size();
        boolean isModified = aList.retainAll(anArrayList);
        int modifiedSize = aList.size();

        // Validate step
        assertTrue(isModified);
        assertTrue(anArrayList.size() == modifiedSize);
        // check retained items
        SDODataObject remainingDO = (SDODataObject)aRoot5.get("items/item[1]");
        assertTrue(aList.contains(remainingDO));
        // check removed items
        assertFalse(aList.contains(aRemovableItem));
    }

    // boolean retainAll(Collection itemsToKeep) items don't exist in empty list
    public void testListWrapperRetainAllFromEmpty() {
        //log("SDODataObjectListWrapperTest.testListWrapperRetainAllFromEmpty()");
        // Setup step: (remove 2 items from a list of 5)
        // get an Item DO
        SDODataObject anItemDO1 = (SDODataObject)aRoot5.get("items/item[1]");
        SDODataObject anItemDO2 = (SDODataObject)aRoot5.get("items/item[2]");

        // Setup a list of items to keep
        ArrayList anArrayList = new ArrayList();
        anArrayList.add(anItemDO1);
        anArrayList.add(anItemDO2);
        // get containment node    	
        ListWrapper aList = (ListWrapper)anEmptyListRoot.getList("items/item");
        ;

        // Test: remove all the objects in the list from the ListWrapper
        int originalSize = aList.size();
        boolean isModified = aList.retainAll(anArrayList);
        int modifiedSize = aList.size();

        // Validate step
        assertFalse(isModified);
        assertTrue(modifiedSize == originalSize);
        assertTrue(aList.isEmpty());
    }

    // boolean retainAll(Collection itemsToKeep) collection is empty
    public void testListWrapperRetainAllUsingEmpty() {
        //log("SDODataObjectListWrapperTest.testListWrapperRetainAllUsingEmpty()");
        // Setup step:
        // Setup a list of items to keep
        ArrayList anArrayList = new ArrayList();

        // get containment node    	
        ListWrapper aList = (ListWrapper)anEmptyListRoot.getList("items/item");

        // Test: remove all the objects in the list from the ListWrapper
        boolean isModified = aList.retainAll(anArrayList);

        // Validate step
        assertTrue(isModified);
        assertTrue(aList.isEmpty());
    }

    // boolean retainAll(Collection itemsToKeep) collection is empty
    public void testListWrapperRetainAllUsingNull() {
        //log("SDODataObjectListWrapperTest.testListWrapperRetainAllUsingNull()");
        // Setup step:
        // get containment node    	
        ListWrapper aList = (ListWrapper)anEmptyListRoot.getList("items/item");

        // Test: remove all the objects in the list from the ListWrapper
        int originalSize = aList.size();
        boolean isModified = aList.retainAll(null);
        int modifiedSize = aList.size();

        // Validate step
        assertFalse(isModified);
        assertTrue(modifiedSize == originalSize);
        assertTrue(aList.isEmpty());
    }

    // Object set(int position, Object item) - tests remove and add internally
    public void testListWrapperSet0() {
        //log("SDODataObjectListWrapperTest.testListWrapperSet0()");
        // Setup step:
        // create item from existing
        SDODataObject anItem = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem.set(anItem.getInstanceProperty("partNum"), "926-ZA");

        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot.getList("items/item");

        // Test step: (replace at the start of the current list)
        int originalSize = aList.size();
        SDODataObject aPreviousObject = (SDODataObject)aList.set(0, anItem);

        // check previous object !null
        assertNotNull(aPreviousObject);
        // size is unchanged
        assertTrue(originalSize == aList.size());
        SDODataObject replacedDO = (SDODataObject)aRoot.get("items/item[1]");
        assertNotNull(replacedDO);
        assertTrue(replacedDO.equals(anItem));
    }

    public void testListWrapperSetSquareBrackets() {
        SDODataObject anItem = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem.set(anItem.getInstanceProperty("partNum"), "926-ZA");

        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot.getList("items/item");

        // Test step: (replace at the start of the current list)
        int originalSize = aList.size();
        aRoot.set("items/item[1]", anItem);
        //SDODataObject aPreviousObject = (SDODataObject)aList.set(0, anItem);
        // check previous object !null
        //assertNotNull(aPreviousObject);
        // size is unchanged
        assertTrue(originalSize == aList.size());
        SDODataObject replacedDO = (SDODataObject)aRoot.get("items/item[1]");
        assertNotNull(replacedDO);
        assertTrue(replacedDO.equals(anItem));
    }

    public void testListWrapperSetDot() {
        SDODataObject anItem = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem.set(anItem.getInstanceProperty("partNum"), "926-ZA");

        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot.getList("items/item");

        // Test step: (replace at the start of the current list)
        int originalSize = aList.size();
        aRoot.set("items/item.0", anItem);
        //SDODataObject aPreviousObject = (SDODataObject)aList.set(0, anItem);
        // check previous object !null
        //assertNotNull(aPreviousObject);
        // size is unchanged
        assertTrue(originalSize == aList.size());
        SDODataObject replacedDO = (SDODataObject)aRoot.get("items/item.0");
        assertNotNull(replacedDO);
        assertTrue(replacedDO.equals(anItem));
    }

    // Object set(int position, Object item) - tests remove and add internally
    public void testListWrapperSetEnd() {
        //log("SDODataObjectListWrapperTest.testListWrapperSetEnd()");
        // Setup step:
        // create item from existing
        SDODataObject anItem = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem.set(anItem.getInstanceProperty("partNum"), "926-ZA");

        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot.getList("items/item");

        // Test step: (replace at end of the current list)
        int originalSize = aList.size();
        SDODataObject aPreviousObject = (SDODataObject)aList.set(originalSize - 1, anItem);

        // check previous object !null
        assertNotNull(aPreviousObject);
        // size is unchanged
        assertTrue(originalSize == aList.size());
        SDODataObject replacedDO = (SDODataObject)aRoot.get("items/item[" + originalSize + "]");
        assertNotNull(replacedDO);
        assertTrue(replacedDO.equals(anItem));
    }

    // Object set(int position, Object item) - tests remove and add internally
    public void testListWrapperSetPastEnd() {
        //log("SDODataObjectListWrapperTest.testListWrapperSetPastEnd()");
        // Setup step:
        // create item from existing
        SDODataObject anItem = (SDODataObject)copyHelper.copy(((SDODataObject)aRoot.get("items/item[2]")));

        // modify object
        anItem.set(anItem.getInstanceProperty("partNum"), "926-ZA");

        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot.getList("items/item");

        // Test step: (reset past the end of the current list)
        int originalSize = aList.size();

        try {
        	SDODataObject aPreviousObject = (SDODataObject)aList.set(originalSize + 2, anItem);
        } catch (IndexOutOfBoundsException ioobe) {
        	// Expected, continue
        } catch (Exception e) {
        	// Unexpected, fail
        	fail("An unexpected exception occurred.");
        }

        // size is unchanged
        assertTrue(originalSize == aList.size());
    }

    // int size()
    public void testListWrapperSize5() {
        //log("SDODataObjectListWrapperTest.testListWrapperSize5()");
        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot5.getList("items/item");
        assertTrue(aList.size() == 5);
    }

    // int size()
    public void testListWrapperSize0() {
        //log("SDODataObjectListWrapperTest.testListWrapperSize0()");
        // get containment node    	
        ListWrapper aList = (ListWrapper)anEmptyListRoot.getList("items/item");
        assertTrue(aList.size() == 0);
    }

    // List subList(int startPosition, int endPosition)
    public void testListWrapperSubList() {
        //log("SDODataObjectListWrapperTest.testListWrapperSubList()");
    }

    // Object subList(start, end)
    public void testListWrapperSubListIndexOutOfBoundsAfterFailure() {
        //log("SDODataObjectListWrapperTest.testListWrapperSubListIndexOutOfBoundsAfterFailure()");
        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot5.getList("items/item");

        // Test step:
        List aSubList = null;
        try {
            aSubList = aList.subList(3, 9);
        } catch (IndexOutOfBoundsException e) {
        } finally {
            // catch passes and failures
            assertNull(aSubList);
        }
    }

    // Object subList(start, end)
    public void testListWrapperSubListIndexOutOfBoundsBeforeFailure() {
        //log("SDODataObjectListWrapperTest.testListWrapperSubListIndexOutOfBoundsBeforeFailure()");
        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot5.getList("items/item");

        // Test step:
        List aSubList = null;
        try {
            aSubList = aList.subList(-2, 2);
        } catch (IndexOutOfBoundsException e) {
        } finally {
            // catch passes and failures
            assertNull(aSubList);
        }
    }

    // Object subList(start, end)
    public void testListWrapperSubListReversedIndexes() {
        //log("SDODataObjectListWrapperTest.testListWrapperSubListReversedIndexes()");
        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot5.getList("items/item");

        // Test step:
        List aSubList = null;
        try {
            aSubList = aList.subList(2, 1);
        } catch (IllegalArgumentException e) {
        } finally {
            // catch passes and failures
            assertNull(aSubList);
        }
    }

    // Object subList(start, end)
    public void testListWrapperSubListInternal() {
        //log("SDODataObjectListWrapperTest.testListWrapperSubListInternal()");
        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot5.getList("items/item");

        // Test step:
        List aSubList = null;
        int start = 1;
        int end = 3;
        aSubList = aList.subList(start, end);
        assertNotNull(aSubList);
        assertTrue(aSubList.size() == (end - start));
    }

    // Object[] toArray()
    public void testListWrapperToArray() {
        //log("SDODataObjectListWrapperTest.testListWrapperToArray()");
        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot5.getList("items/item");

        // get an item and its type
        SDODataObject anItem = (SDODataObject)aRoot5.get("items/item[4]");

        // get items
        Object[] anArray = aList.toArray();

        // Test step:
        assertNotNull(anArray);
        // verify size
        assertTrue(anArray.length == 5);
        // verify an entry
        assertTrue(anArray[3].equals(anItem));
    }

    // Object[] toArray(Object[] items)
    public void testListWrapperToArrayObjectSizeTooSmall() {
        //log("SDODataObjectListWrapperTest.testListWrapperToArrayObjectSizeTooSmall()");
        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot5.getList("items/item");

        // get an item and its type
        SDODataObject anItem = (SDODataObject)aRoot5.get("items/item[5]");

        // Test step:
        Object[] anArray = aList.toArray(new SDODataObject[2]);

        // Validation step:
        assertNotNull(anArray);
        // verify return type is the same as input parameter
        assertTrue(anArray instanceof SDODataObject[]);
        // verify size of array has increased from input
        assertTrue(anArray.length == 5);
        // verify an entry
        assertTrue(anArray[4].equals(anItem));
    }

    // Object[] toArray(Object[] items)
    public void testListWrapperToArrayObjectSameSize() {
        //log("SDODataObjectListWrapperTest.testListWrapperToArrayObjectSameSize()");
        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot5.getList("items/item");

        // get an item and its type
        SDODataObject anItem = (SDODataObject)aRoot5.get("items/item[1]");

        // Test step:
        Object[] anArray = aList.toArray(new SDODataObject[5]);

        // Validation step:
        assertNotNull(anArray);
        // verify return type is the same as input parameter
        assertTrue(anArray instanceof SDODataObject[]);
        // verify size of array has increased from input
        assertTrue(anArray.length == 5);
        // verify an entry
        assertTrue(anArray[0].equals(anItem));
    }

    // Object[] toArray(Object[] items)
    public void testListWrapperToArrayObjectLargerSize() {
        //log("SDODataObjectListWrapperTest.testListWrapperToArrayObjectLargerSize()");
        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot5.getList("items/item");

        // get an item and its type
        //SDODataObject anItem = (SDODataObject)aRoot5.get("items/item[1]");
        // Test step:
        Object[] anArray = aList.toArray(new SDODataObject[8]);

        // Validation step:
        assertNotNull(anArray);
        // verify return type is the same as input parameter
        assertTrue(anArray instanceof SDODataObject[]);
        // verify size of array has increased from input
        assertTrue(anArray.length == 8);
        // verify remaining 3 entries are null
        assertTrue(anArray[7] == null);
    }

    // Object[] toArray(Object[] items)
    public void testListWrapperToArrayObjectNullFailure() {
        //log("SDODataObjectListWrapperTest.testListWrapperToArrayObjectNullFailure()");
        // get containment node    	
        ListWrapper aList = (ListWrapper)aRoot5.getList("items/item");

        // get items
        Object[] anArray = null;
        try {
            anArray = aList.toArray(null);
        } catch (NullPointerException npe) {
            assertNull(anArray);
        }
    }
    
    public void testGetListNullProperty()
    {
      Property p = null;
      try{
        ListWrapper aList = (ListWrapper)aRoot.getList(p);      
      }catch(SDOException e)
      {
        assertEquals(SDOException.CANNOT_PERFORM_OPERATION_ON_NULL_ARGUMENT, e.getErrorCode());
        return;
      }
      fail("A CANNOT_PERFORM_OPERATION_ON_NULL_ARGUMENT exception should have occurred");
    }
    
    public void testMoveItemBetweenLists(){
     ListWrapper aList = (ListWrapper)aRoot5.getList("items/item");
     DataObject newItemsDO = dataFactory.create("http://www.example.org", "Items");
     
     newItemsDO.set("item", aList);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectListWrapperTest" };
        TestRunner.main(arguments);
    }

    private String getXSDString(String filename) {
        try {
            FileInputStream inStream = new FileInputStream(filename);
            byte[] bytes = new byte[inStream.available()];
            inStream.read(bytes);
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Write an object representation of the SDODataObject to the stream
     * @param anObject
     * @return
     * String
     *
     */
    private String dataObjectToString(SDODataObject anObject) {
        if (anObject == null) {
            return SDOConstants.EMPTY_STRING;
        }
        StringBuffer aBuffer = new StringBuffer();
        aBuffer.append(anObject.toString());
        aBuffer.append("\n\t root: ");
        aBuffer.append(anObject.getRootObject());
        aBuffer.append("\n\t type: ");
        aBuffer.append(anObject.getType());
        aBuffer.append(" name: ");
        aBuffer.append(anObject.getType().getName());
        aBuffer.append("\n\t properties: (");
        // iterate any properties
        List properties = anObject.getInstanceProperties();
        if (!properties.isEmpty()) {
            List keys = anObject.getInstanceProperties();
            Iterator anIterator = keys.iterator();
            while (anIterator.hasNext()) {
                Property aKey = (Property)anIterator.next();
                aBuffer.append(aKey.getName());
                aBuffer.append(":");
                aBuffer.append(anObject.get(aKey));
                aBuffer.append(",\n\t\t");
            }
        }
        aBuffer.append(")");
        return aBuffer.toString();
    }
}
