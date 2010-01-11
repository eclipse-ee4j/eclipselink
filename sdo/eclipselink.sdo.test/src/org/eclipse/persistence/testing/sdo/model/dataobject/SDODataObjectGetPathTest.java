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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.helper.ListWrapper;
import org.eclipse.persistence.sdo.helper.SDOMarshalListener;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;
import org.eclipse.persistence.testing.sdo.SDOTestCase;
import commonj.sdo.helper.XMLDocument;

public class SDODataObjectGetPathTest extends SDOTestCase {
    protected static final String URINAME = "dataObject";
    protected static final String TYPENAME = "commonj.sdo";
    protected static final String ROOT_PROPERTY_NAME = "root";
    protected static final String CHILD1_PROPERTY_NAME = "items";
    protected static final String CHILD2_PROPERTY_NAME = "item";
    protected static final String CHILD1ITEM_PROPERTY_NAME = "item";
    protected SDODataObject aRoot;
    protected SDODataObject aRoot5;
    protected SDODataObject anEmptyListRoot;

    // default to XPath format ie: ns0:item not just item
    protected boolean useXPathFormat = true;

    public SDODataObjectGetPathTest(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        try {
            String xsdString = getXSDString("org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderNSDeepCSPath.xsd");            
            // Generate a list of sdo types based on the purchaseOrder.xsd and print
            // out info about them
            xsdHelper.define(xsdString);

            // create a Purchase Order dataObject and write it to XML called
            FileInputStream inStream = new FileInputStream("org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderNSDeepCSPath.xml");
            XMLDocument document = xmlHelper.load(inStream);
            aRoot = (SDODataObject)document.getRootObject();
            // reset changes if changeSummary=true
            SDOChangeSummary aCS = (SDOChangeSummary)aRoot.getChangeSummary();
            if (aCS != null) {
                //aCS.resetChanges();
                aCS.endLogging();
            }
            inStream.close();

            //aRoot.getChangeSummary().beginLogging();
            // create a List of new items
            List<SDODataObject> aNewList = new ArrayList<SDODataObject>();

            // create item from existing
            SDODataObject anItem = (SDODataObject)aHelperContext.getCopyHelper().copy(((SDODataObject)aRoot.get("items/item[2]")));

            // modify object
            anItem.set(anItem.getInstanceProperty("partNum"), "926-ZA");

            SDODataObject anItem2 = (SDODataObject)aHelperContext.getCopyHelper().copy(((SDODataObject)aRoot.get("items/item[2]")));

            // modify object
            anItem2.set(anItem.getInstanceProperty("partNum"), "926-ZB");

            SDODataObject anItem3 = (SDODataObject)aHelperContext.getCopyHelper().copy(((SDODataObject)aRoot.get("items/item[2]")));

            // modify object
            anItem3.set(anItem.getInstanceProperty("partNum"), "926-ZC");

            // add items to list (to be added to the ListWrapper)
            aNewList.add(anItem);
            aNewList.add(anItem2);
            aNewList.add(anItem3);

            // copy root
            aRoot5 = (SDODataObject)aHelperContext.getCopyHelper().copy(aRoot);
            // get containment node
            ListWrapper aList = (ListWrapper)aRoot5.getList("items/item");

            // TODO: changeSummary is not copied
            //aRoot5.setChangeSummary(aRoot.getChangeSummary());
            setChangeSummary(aRoot5);
            aList.addAll(1, aNewList);

            // create empty ListWrapper
            anEmptyListRoot = (SDODataObject)aHelperContext.getCopyHelper().copy(aRoot);
            // get containment node
            aList = (ListWrapper)anEmptyListRoot.getList("items/item");
            aList.clear();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private SDOChangeSummary setChangeSummary(SDODataObject aCSRoot) {
        // get and set change summary (temp workaround does not propagate to children)
        SDOChangeSummary aCS = new SDOChangeSummary(aCSRoot, aHelperContext);
        aCSRoot._setChangeSummary(aCS);
        aCS.endLogging();
        aCS.beginLogging();
        aCS.endLogging();
        return aCS;
    }

    // NS tests required
    public void testGetPathForRootObject() {
        String aPath = aRoot5._getPath();
        assertNotNull(aPath);
        assertEquals(SDOConstants.SDO_XPATH_TO_ROOT, aPath);
    }

    public void testGetPathForInternalNonIsManyObject() {
        SDODataObject anItem = (SDODataObject)aRoot5.get("shipTo");
        String aPath = anItem._getPath();
        assertNotNull(aPath);
        assertEquals("shipTo", aPath);
    }

    public void testGetPathForInternalIsManyObject() {
        SDODataObject anItem = (SDODataObject)aRoot5.get("items/item[2]");
        String aPath = anItem._getPath();
        assertNotNull(aPath);
        assertEquals("items/item[2]", aPath);
    }

    public void testGetPathFromAncestorFromContainedToParent() {
        SDODataObject target = (SDODataObject)aRoot5.get("items");
        SDODataObject aSibling = (SDODataObject)aRoot5.get("items/item[2]");        
        String aPath =   ((SDOMarshalListener)((SDOXMLHelper)xmlHelper).getXmlMarshaller().getMarshalListener()).getPathFromAncestor(aSibling, target,(SDOChangeSummary)aSibling.getChangeSummary());
        assertNotNull(aPath);
        assertEquals("ns0:item[2]", aPath);
    }

    public void testGetPathFromAncestorFromChildToRoot() {
        SDODataObject anItem = (SDODataObject)aRoot5.get("items/item[2]");
        String aPath =  ((SDOMarshalListener)((SDOXMLHelper)xmlHelper).getXmlMarshaller().getMarshalListener()).getPathFromAncestor(anItem,aRoot5,(SDOChangeSummary)anItem.getChangeSummary());
        assertNotNull(aPath);
        assertEquals("ns0:items/ns0:item[2]", aPath);
    }

    public void testGetPathFromAncestorFromChildToCurrentObject() {
        SDODataObject anItem = (SDODataObject)aRoot5.get("items/item[2]");
        String aPath =  ((SDOMarshalListener)((SDOXMLHelper)xmlHelper).getXmlMarshaller().getMarshalListener()).getPathFromAncestor(anItem,anItem,(SDOChangeSummary)anItem.getChangeSummary());
        assertNotNull(aPath);
        assertEquals(SDOConstants.EMPTY_STRING, aPath);
    }

    public void testGetPathFromAncestorFromRootToCurrentObject() {      
        String aPath =  ((SDOMarshalListener)((SDOXMLHelper)xmlHelper).getXmlMarshaller().getMarshalListener()).getPathFromAncestor(aRoot5,aRoot5,(SDOChangeSummary)aRoot5.getChangeSummary());
        assertNotNull(aPath);
        assertEquals(SDOConstants.EMPTY_STRING, aPath);
    }

    public void testGetPathFromAncestorFromRootToSibling() {
        SDODataObject anItem = (SDODataObject)aRoot5.get("items/item[2]");        
        String aPath =  ((SDOMarshalListener)((SDOXMLHelper)xmlHelper).getXmlMarshaller().getMarshalListener()).getPathFromAncestor(aRoot5,anItem,(SDOChangeSummary)aRoot5.getChangeSummary());
        assertEquals(SDOConstants.SDO_XPATH_INVALID_PATH, aPath);
    }

    // path would need to go up to common ancestor and back down to sibling - null for now
    public void testGetPathFromAncestorFromChildToSibling() {
        SDODataObject anItem = (SDODataObject)aRoot5.get("items/item[2]");
        SDODataObject aSibling = (SDODataObject)aRoot5.get("shipTo");        
        String aPath =  ((SDOMarshalListener)((SDOXMLHelper)xmlHelper).getXmlMarshaller().getMarshalListener()).getPathFromAncestor(anItem,aSibling,(SDOChangeSummary)anItem.getChangeSummary());
        assertEquals(SDOConstants.SDO_XPATH_INVALID_PATH, aPath);
    }

    public void testGetPathFromAncestorDeletedFromContainedToParentLoggingOff() {
        SDODataObject target = (SDODataObject)aRoot5.get("items");
        SDODataObject aSibling = (SDODataObject)aRoot5.get("items/item[2]");

        // get changeSummary
        SDOChangeSummary aCS = (SDOChangeSummary)aRoot5.getChangeSummary();
        assertFalse(aCS.isLogging());

        // delete source object
        aSibling.delete();        
        String aPath =  ((SDOMarshalListener)((SDOXMLHelper)xmlHelper).getXmlMarshaller().getMarshalListener()).getPathFromAncestor(aSibling,target,(SDOChangeSummary)target.getChangeSummary());
        assertEquals(SDOConstants.SDO_XPATH_INVALID_PATH, aPath);
    }

    // if logging is off we get null path because we need a changeSummary to compute containers
    public void testGetPathFromAncestorDeletedFromChildToRootLoggingOff() {
        SDODataObject anItem = (SDODataObject)aRoot5.get("items/item[2]");

        // get changeSummary
        SDOChangeSummary aCS = (SDOChangeSummary)aRoot5.getChangeSummary();
        assertFalse(aCS.isLogging());

        // delete source object, useXPathFormat
        anItem.delete();        
        String aPath =  ((SDOMarshalListener)((SDOXMLHelper)xmlHelper).getXmlMarshaller().getMarshalListener()).getPathFromAncestor(anItem,aRoot5,(SDOChangeSummary)aRoot5.getChangeSummary());
        assertNull(aPath);

        // get path to root
        //String aPathAcrossCS = anItem.getPathFromAncestor(aCS, aRoot5, false);
        //assertNotNull(aPathAcrossCS);
        //assertEquals(aPathAcrossCS, "items/item[2]");
    }

    public void testGetPathFromAncestorDeletedFromChildToCurrentObjectLoggingOff() {
        SDODataObject anItem = (SDODataObject)aRoot5.get("items/item[2]");

        // get changeSummary
        SDOChangeSummary aCS = (SDOChangeSummary)aRoot5.getChangeSummary();
        assertFalse(aCS.isLogging());

        // delete source object
        anItem.delete();        
        String aPath =  ((SDOMarshalListener)((SDOXMLHelper)xmlHelper).getXmlMarshaller().getMarshalListener()).getPathFromAncestor(anItem,anItem,(SDOChangeSummary)anItem.getChangeSummary());
        assertNotNull(aPath);
        assertEquals(SDOConstants.EMPTY_STRING, aPath);
    }

    public void testGetPathFromAncestorDeletedFromRootToCurrentObjectLoggingOff() {
        String aPath =  ((SDOMarshalListener)((SDOXMLHelper)xmlHelper).getXmlMarshaller().getMarshalListener()).getPathFromAncestor(aRoot5,aRoot5,(SDOChangeSummary)aRoot5.getChangeSummary());    

        // get changeSummary
        SDOChangeSummary aCS = (SDOChangeSummary)aRoot5.getChangeSummary();
        assertFalse(aCS.isLogging());

        // delete source object
        aRoot5.delete();
        assertNotNull(aPath);
        assertEquals(SDOConstants.EMPTY_STRING, aPath);
    }

    public void testGetPathFromAncestorDeletedFromRootToSiblingLoggingOff() {
        SDODataObject anItem = (SDODataObject)aRoot5.get("items/item[2]");       
       String aPath =  ((SDOMarshalListener)((SDOXMLHelper)xmlHelper).getXmlMarshaller().getMarshalListener()).getPathFromAncestor(aRoot5,anItem,(SDOChangeSummary)aRoot5.getChangeSummary());    

        // get changeSummary
        SDOChangeSummary aCS = (SDOChangeSummary)aRoot5.getChangeSummary();
        assertFalse(aCS.isLogging());
        // delete source object
        aRoot5.delete();
        assertEquals(SDOConstants.SDO_XPATH_INVALID_PATH, aPath);
    }

    // these deleted tests are not supported yet for XPath
    // TODO: we must store the index (2) position when deleting inside a ListWrapper
    public void testGetPathFromAncestorDeletedFromContainedToParentLoggingOn() {
        SDODataObject target = (SDODataObject)aRoot5.get("items");
        SDODataObject aSibling = (SDODataObject)aRoot5.get("items/item[2]");

        // start logging
        SDOChangeSummary aCS = (SDOChangeSummary)aRoot5.getChangeSummary();
        aCS.beginLogging();
        // delete source object
        aSibling.delete();
        String aPath =  ((SDOMarshalListener)((SDOXMLHelper)xmlHelper).getXmlMarshaller().getMarshalListener()).getPathFromAncestor(aSibling,target,(SDOChangeSummary)target.getChangeSummary());    
        assertNotNull(aPath);
        // TODO: no storage of deleted indexed postition - defaults to size() = start of list for now
        // see SDODataObject: index = ((SDODataObject)parent).getList(aChild).size();
        assertEquals("ns0:item[2]", aPath);
    }

    public void testGetPathFromAncestorModifiedFromChildToRootLoggingOnWithoutPassingChangeSummary() {
        SDODataObject anItem = (SDODataObject)aRoot5.get("billTo");

        // start logging
        SDOChangeSummary aCS = (SDOChangeSummary)aRoot5.getChangeSummary();
        aCS.beginLogging();
        assertTrue(aCS.isLogging());
        // modifydelete source object
        ((SDODataObject)aRoot5.get("billTo")).set("name", "new name");

        String aPath =  ((SDOMarshalListener)((SDOXMLHelper)xmlHelper).getXmlMarshaller().getMarshalListener()).getPathFromAncestor(anItem,aRoot5,(SDOChangeSummary)anItem.getChangeSummary());    
        assertNotNull(aPath);
        assertEquals("ns0:billTo", aPath);
    }

    public void testGetPathFromAncestorDeletedFromChildToRootLoggingOn() {
        SDODataObject anItem = (SDODataObject)aRoot5.get("billTo");

        // start logging
        SDOChangeSummary aCS = (SDOChangeSummary)aRoot5.getChangeSummary();
        aCS.beginLogging();
        assertTrue(aCS.isLogging());
        // delete source object
        ((SDODataObject)aRoot5.get("billTo")).detach();

        String aPath =  ((SDOMarshalListener)((SDOXMLHelper)xmlHelper).getXmlMarshaller().getMarshalListener()).getPathFromAncestor(anItem,aRoot5,(SDOChangeSummary)aRoot5.getChangeSummary());    
        assertNotNull(aPath);
        assertEquals("ns0:billTo", aPath);
    }
    
    // verify that we do not get an infinite loop if we pass in the target's changeSummary
    public void testGetPathFromAncestorDeletedFromChildToAncestorListWrapperLoggingOn() {
        SDODataObject anItem = (SDODataObject)aRoot5.get("items/item[2]");

        // start logging
        SDOChangeSummary aCS = (SDOChangeSummary)aRoot5.getChangeSummary();
        aCS.beginLogging();
        assertTrue(aCS.isLogging());
        // delete source object
        SDODataObject deletedObject = ((SDODataObject)aRoot5.get("items"));
        deletedObject.delete();
        
        String aPath =  ((SDOMarshalListener)((SDOXMLHelper)xmlHelper).getXmlMarshaller().getMarshalListener()).getPathFromAncestor(anItem,aRoot5,(SDOChangeSummary)aRoot5.getChangeSummary());    

        assertNotNull(aPath);
        // TODO: no storage of deleted indexed postition - defaults to size() = start of list for now
        // see SDODataObject: index = ((SDODataObject)parent).getList(aChild).size();
        assertEquals("ns0:items/ns0:item[2]", aPath);
        //assertEquals(anItem, anItemFromPath);
    }

    public void testGetXPathFromAncestorDeletedFromChildToRootInsideListWrapperLoggingOn() {
        SDODataObject anItem = (SDODataObject)aRoot5.get("items/item[2]");

        // start logging
        SDOChangeSummary aCS = (SDOChangeSummary)aRoot5.getChangeSummary();
        aCS.beginLogging();
        assertTrue(aCS.isLogging());
        // delete source object
        //SDODataObject deletedObject = ((SDODataObject)aRoot5.get("items"));
        anItem.delete();//detach();

        String aPath =  ((SDOMarshalListener)((SDOXMLHelper)xmlHelper).getXmlMarshaller().getMarshalListener()).getPathFromAncestor(anItem,aRoot5,(SDOChangeSummary)aRoot5.getChangeSummary());    

        //SDODataObject anItemFromPath = (SDODataObject)aRoot5.get(aPath);
        //System.out.println("testGetPathFromAncestorDeletedFromChildToRootLoggingOn: " + aPath);
        assertNotNull(aPath);
        // TODO: no storage of deleted indexed postition - defaults to size() = start of list for now
        // see SDODataObject: index = ((SDODataObject)parent).getList(aChild).size();
        assertEquals("ns0:items/ns0:item[2]", aPath);
    }

    public void testGetXPathFromAncestorDeletedFromChildToAncestorInsideListWrapperLoggingOn() {
        SDODataObject anItem = (SDODataObject)aRoot5.get("billTo/phone[1]");

        // start logging
        SDOChangeSummary aCS = (SDOChangeSummary)aRoot5.getChangeSummary();
        aCS.beginLogging();
        assertTrue(aCS.isLogging());
        // delete source object
        SDODataObject deletedObjectParent = ((SDODataObject)aRoot5.get("billTo"));
        deletedObjectParent.delete();//detach();

        String aPath =  ((SDOMarshalListener)((SDOXMLHelper)xmlHelper).getXmlMarshaller().getMarshalListener()).getPathFromAncestor(anItem,aRoot5,(SDOChangeSummary)aRoot5.getChangeSummary());    

        //SDODataObject anItemFromPath = (SDODataObject)aRoot5.get(aPath);
        //System.out.println("testGetPathFromAncestorDeletedFromChildToRootLoggingOn: " + aPath);
        assertNotNull(aPath);
        // TODO: no storage of deleted indexed postition - defaults to size() = start of list for now
        // see SDODataObject: index = ((SDODataObject)parent).getList(aChild).size();
        assertEquals("ns0:billTo/ns0:phone[1]", aPath);
    }

    // test index=0 from item inside deleted listWrapper
    public void testGetPathFromAncestorDeletedFromChildToAncestorInsideListWrapperLoggingOn() {
        SDODataObject anItem = (SDODataObject)aRoot5.get("items/item[2]");

        // start logging
        SDOChangeSummary aCS = (SDOChangeSummary)aRoot5.getChangeSummary();
        aCS.beginLogging();
        assertTrue(aCS.isLogging());
        // delete source object
        //SDODataObject deletedObject = ((SDODataObject)aRoot5.get("items"));
        anItem.delete();//detach();

        String aPath =  ((SDOMarshalListener)((SDOXMLHelper)xmlHelper).getXmlMarshaller().getMarshalListener()).getPathFromAncestor(anItem,aRoot5,(SDOChangeSummary)aRoot5.getChangeSummary());    

        assertNotNull(aPath);
        // TODO: no storage of deleted indexed postition - defaults to size() = start of list for now
        // see SDODataObject: index = ((SDODataObject)parent).getList(aChild).size();
        //assertEquals("items/item[2]", aPath);
        assertEquals("ns0:items/ns0:item[2]", aPath);
    }

    // TODO: This case fails to find a path because the oldSettings map is not populated during detach()
    public void testGetPathFromAncestorDetachedFromChildToAncestorInsideListWrapperLoggingOn() {
        SDODataObject anItem = (SDODataObject)aRoot5.get("items/item[2]");

        // start logging
        SDOChangeSummary aCS = (SDOChangeSummary)aRoot5.getChangeSummary();
        aCS.beginLogging();
        assertTrue(aCS.isLogging());
        // delete source object
        //SDODataObject deletedObject = ((SDODataObject)aRoot5.get("items"));
        anItem.detach();

        //String aPath = anItem.getPathFromAncestor(aCS, aRoot5, useXPathFormat);
        String aPath =  ((SDOMarshalListener)((SDOXMLHelper)xmlHelper).getXmlMarshaller().getMarshalListener()).getPathFromAncestor(anItem,aRoot5,(SDOChangeSummary)aRoot5.getChangeSummary());    

        //SDODataObject anItemFromPath = (SDODataObject)aRoot5.get(aPath);
        assertNotNull(aPath);
        // TODO: no storage of deleted indexed postition - defaults to size() = start of list for now
        // see SDODataObject: index = ((SDODataObject)parent).getList(aChild).size();
        assertEquals("ns0:items/ns0:item[2]", aPath);
    }

    public void testGetPathFromAncestorDeletedFromChildToCurrentObjectLoggingOn() {
        SDODataObject anItem = (SDODataObject)aRoot5.get("items/item[2]");

        // start logging
        SDOChangeSummary aCS = (SDOChangeSummary)aRoot5.getChangeSummary();
        aCS.beginLogging();
        assertTrue(aCS.isLogging());
        // delete source object
        anItem.delete();

        String aPath =  ((SDOMarshalListener)((SDOXMLHelper)xmlHelper).getXmlMarshaller().getMarshalListener()).getPathFromAncestor(anItem,anItem,(SDOChangeSummary)anItem.getChangeSummary());    

        assertNotNull(aPath);
        assertEquals(SDOConstants.EMPTY_STRING, aPath);
    }

    public void testGetXPathFromAncestorDeletedFromRootToCurrentObjectLoggingOn() {        
        String aPath =  ((SDOMarshalListener)((SDOXMLHelper)xmlHelper).getXmlMarshaller().getMarshalListener()).getPathFromAncestor(aRoot5,aRoot5,(SDOChangeSummary)aRoot5.getChangeSummary());    

        // start logging
        SDOChangeSummary aCS = (SDOChangeSummary)aRoot5.getChangeSummary();
        aCS.beginLogging();
        assertTrue(aCS.isLogging());
        // delete source object
        aRoot5.delete();
        assertNotNull(aPath);
        assertEquals(SDOConstants.EMPTY_STRING, aPath);
    }

    public void testGetPathFromAncestorDeletedFromRootToSiblingLoggingOn() {
        SDODataObject anItem = (SDODataObject)aRoot5.get("items/item[2]");        
        String aPath =  ((SDOMarshalListener)((SDOXMLHelper)xmlHelper).getXmlMarshaller().getMarshalListener()).getPathFromAncestor(aRoot5,anItem,(SDOChangeSummary)aRoot5.getChangeSummary());    

        // start logging
        SDOChangeSummary aCS = (SDOChangeSummary)aRoot5.getChangeSummary();
        aCS.beginLogging();
        assertTrue(aCS.isLogging());
        // delete source object
        aRoot5.delete();
        assertEquals(SDOConstants.SDO_XPATH_INVALID_PATH, aPath);
    }

    public void testGetXPathFromAncestorFromContainedToParent() {
        SDODataObject target = (SDODataObject)aRoot5.get("items");
        SDODataObject aSibling = (SDODataObject)aRoot5.get("items/item[2]");        
        String aPath =  ((SDOMarshalListener)((SDOXMLHelper)xmlHelper).getXmlMarshaller().getMarshalListener()).getPathFromAncestor(aSibling,target,(SDOChangeSummary)aSibling.getChangeSummary());    
        assertNotNull(aPath);
        assertEquals("ns0:item[2]", aPath);
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

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All SDODataObject.SDODataObjectGetPathTest Tests");

        suite.addTest(new TestSuite(SDODataObjectGetPathTest.class));
        return suite;
    }
}
