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
package org.eclipse.persistence.testing.sdo.model.changesummary;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDOSetting;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sdo.helper.delegates.SDOTypeHelperDelegate;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.ChangeSummaryRootLoadAndSaveTestCases;
import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;

public abstract class ChangeSummaryOnChildTestCases extends ChangeSummaryRootLoadAndSaveTestCases { // we override everything on root - don't need child version of parent
    protected DataObject rootObject;
    protected DataObject rootObject2;
    protected DataObject rootObjectOriginalCopy;
    protected ChangeSummary salesPO1CS;
    protected ChangeSummary salesPO2CS;
    protected ChangeSummary developmentPO1CS; // n/a on startup
    protected ChangeSummary developmentPO2CS; // n/a on startup
    protected ChangeSummary stock1CS;
    protected ChangeSummary stock2CS;
    protected ChangeSummary stock3CS;
    protected ChangeSummary salesPO1CS2;
    protected ChangeSummary salesPO2CS2;
    protected ChangeSummary developmentPO1CS2; // n/a on startup
    protected ChangeSummary developmentPO2CS2; // n/a on startup
    protected ChangeSummary stock1CS2;
    protected ChangeSummary stock2CS2;
    protected ChangeSummary stock3CS2;
    public static final String URINAME = "http://www.example.org";
    public static final String TYPENAME = "corporation";
    protected String getControlRootURI() {
        return URINAME;
    }

    protected String getControlRootName() {
        return TYPENAME;
    }
    
    protected String getRootInterfaceName() {
        return "CorporationType";
    }

    protected String getControlFileName() {
        // implemented by subclass
        return getControlFileName2();
    }

    protected abstract String getControlFileName2();

    //protected String getNoSchemaControlFileName() {
    //    return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderDeepWithCS.xml");
    //}
    
    public void testNoSchemaLoadFromInputStreamSaveDataObjectToString() throws Exception {
        //do nothing
        //TODO: need to make this test run
    }
    
    protected String getModelFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderDeepWithCSonChild.xml");
    }

    public ChangeSummaryOnChildTestCases(String name) {
        super(name);
    }

    public String getSchemaName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderDeepWithCSonChild.xsd");
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryOnChildTestCases" };
        TestRunner.main(arguments);
    }
    
    protected List defineTypes() {
        
        List types =  xsdHelper.define(getSchema(getSchemaName()));
        try{
        XMLDocument document = xmlHelper.load(new FileInputStream(getModelFileName()));
            rootObject = document.getRootObject();
            salesPO1CS = rootObject.getDataObject("sales/purchaseOrder[1]").getChangeSummary();
            salesPO2CS = rootObject.getDataObject("sales/purchaseOrder[2]").getChangeSummary();
            developmentPO1CS = rootObject.getDataObject("development/purchaseOrder[1]").getChangeSummary();
            developmentPO2CS = null;//rootObject.getDataObject("development/purchaseOrder[2]").getChangeSummary();
            stock1CS = rootObject.getDataObject("stock[1]").getChangeSummary();
            stock2CS = rootObject.getDataObject("stock[2]").getChangeSummary();
            stock3CS = rootObject.getDataObject("stock[3]").getChangeSummary();

            //rootObjectOriginalCopy = copyHelper.copy(rootObject);
            // see bug #5878605: SDO: COPYHELPER.COPY() LOGS CS CHANGES - SHOULD SUSPEND LOGGING DURING COPY
            // turn off logging before deep copy as a workaround
            salesPO1CS.endLogging();
            salesPO2CS.endLogging();            
            developmentPO1CS.endLogging();
            //developmentPO2CS.endLogging();
            stock1CS.endLogging();
            stock2CS.endLogging();
            stock3CS.endLogging();            
            
            rootObject2 = copyHelper.copy(rootObject);
            
            salesPO1CS2 = rootObject2.getDataObject("sales/purchaseOrder[1]").getChangeSummary();
            salesPO2CS2 = rootObject2.getDataObject("sales/purchaseOrder[2]").getChangeSummary();
            developmentPO1CS2 = rootObject.getDataObject("development/purchaseOrder[1]").getChangeSummary();
            developmentPO2CS2 = null;//rootObject.getDataObject("development/purchaseOrder[2]").getChangeSummary();
            stock1CS2 = rootObject2.getDataObject("stock[1]").getChangeSummary();
            stock2CS2 = rootObject2.getDataObject("stock[2]").getChangeSummary();
            stock3CS2 = rootObject2.getDataObject("stock[3]").getChangeSummary();

            // make sure all logs are off
            salesPO1CS.endLogging();
            salesPO2CS.endLogging();            
            developmentPO1CS.endLogging();
            //developmentPO2CS.endLogging();
            stock1CS.endLogging();
            stock2CS.endLogging();
            stock3CS.endLogging();
            
            salesPO1CS2.endLogging();
            salesPO2CS2.endLogging();            
            developmentPO1CS2.endLogging();
            //developmentPO2CS2.endLogging();
            stock1CS2.endLogging();
            stock2CS2.endLogging();
            stock3CS2.endLogging();
        }catch(Exception e)
        {
          e.printStackTrace();
        }
        return types;
    }

    protected String getControlString(String fileName) {
        try {
            FileInputStream inputStream = new FileInputStream(fileName);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            return new String(bytes);
        } catch (Exception e) {
            fail("An error occurred loading the control document");
            e.printStackTrace();
            return null;
        }
    }

    protected void verifyAfterLoad(XMLDocument document) {
        assertNull(document.getRootObject().getContainer());
    }
/*
    protected List defineTypes() {
        // do not define types twice - so we do not set dirty=true
        //return xsdHelper.define(getSchema(getSchemaName()));
        return (List)new ArrayList(((SDOTypeHelper)typeHelper).getTypesHashMap().values());
    }
*/
    //protected String getSchemaLocation() {
    //    return "";
    //}
    public DataObject loadObjectFromInputStream() throws Exception {
        List types = defineTypes();
        FileInputStream inputStream = new FileInputStream(getControlFileName());
        XMLDocument document = xmlHelper.load(inputStream);

        // reset global variables for this xml based test
        rootObject = document.getRootObject();
        salesPO1CS = rootObject.getDataObject("sales/purchaseOrder[1]").getChangeSummary();
        salesPO2CS = rootObject.getDataObject("sales/purchaseOrder[2]").getChangeSummary();
        developmentPO1CS = rootObject.getDataObject("development/purchaseOrder[1]").getChangeSummary();
        developmentPO2CS = null;//rootObject.getDataObject("development/purchaseOrder[2]").getChangeSummary();
        stock1CS = rootObject.getDataObject("stock[1]").getChangeSummary();
        stock2CS = rootObject.getDataObject("stock[2]").getChangeSummary();
        stock3CS = rootObject.getDataObject("stock[3]").getChangeSummary();

        verifyAfterLoad(document);
        return document.getRootObject();

        //compareXML(getControlString(getLoadControlFileName()), outputStream.toString());
        //ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //xmlHelper.save(document, outputStream, null);
        //compareXML(getControlString(getControlFileName()), outputStream.toString());
    }

    public void saveObjectToOutputStream() throws Exception {
        //List types = defineTypes();
        //FileInputStream inputStream = new FileInputStream(getControlFileName());
        //XMLDocument document = xmlHelper.load(inputStream);
        //verifyAfterLoad(document);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            xmlHelper.save(rootObject, URINAME,//
                           TYPENAME,//
                           //System.out);//outputStream);
            outputStream);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
        compareXML(getControlString(getControlFileName()), outputStream.toString());
    }

    public void assertDeleteDetachUnsetComplexSingleBelowRoot(boolean testLoadSave, //
    		boolean isDeleted, //
    		DataObject itemsDO,//
    		DataObject item1DO,//
    		DataObject item1ProductDO,//
    		DataObject item1ProductPrice1DO,//
    		DataObject item1ProductPrice2DO//
    		) {
        assertEquals(5, salesPO1CS.getChangedDataObjects().size());
        assertEquals(1, salesPO1CS.getOldValues(itemsDO).size());
        if (!testLoadSave) {
            assertModified(itemsDO, salesPO1CS);            
            if (isDeleted) {
                assertDeleted(item1DO, salesPO1CS);
                assertFalse(item1DO == salesPO1CS.getOldValue(itemsDO, itemsDO.getInstanceProperty("item"))); // verify that oldValue of original deleted is deep copy
                assertDeleted(item1ProductDO, salesPO1CS);
                assertFalse(item1ProductDO == salesPO1CS.getOldValue(item1DO, item1DO.getInstanceProperty("product"))); // verify that oldValue of original deleted is deep copy                
                assertDeleted(item1ProductPrice1DO, salesPO1CS);
                Object  oldValue = salesPO1CS.getOldValue(item1ProductDO, item1ProductDO.getInstanceProperty("price")).getValue();
                // Bug# 5895047 - verify we are not getting an oldSetting that is an empty ArrayList
                assertTrue(oldValue instanceof ArrayList);
                assertEquals(2, ((ArrayList)oldValue).size());
                List aPriceList = ((ArrayList)oldValue);
                DataObject oldItem1ProductPrice1DO = (DataObject) aPriceList.get(0); 
                assertNotNull(oldItem1ProductPrice1DO);
                assertFalse(item1ProductPrice1DO == oldItem1ProductPrice1DO); // verify that oldValue of original deleted is deep copy                
                assertDeleted(item1ProductPrice2DO, salesPO1CS);
                DataObject oldItem2ProductPrice1DO = (DataObject) aPriceList.get(1); 
                assertNotNull(oldItem2ProductPrice1DO);
                assertFalse(item1ProductPrice2DO == oldItem2ProductPrice1DO); // verify that oldValue of original deleted is deep copy                
            } else {
                assertDetached(item1DO, salesPO1CS);    
                assertDetached(item1ProductDO, salesPO1CS, false);
                assertDetached(item1ProductPrice1DO, salesPO1CS, false);
                assertDetached(item1ProductPrice2DO, salesPO1CS, false);
            }
        }

        assertEquals(5, salesPO1CS.getChangedDataObjects().size());

        if (!testLoadSave) {
            assertEquals(1, salesPO1CS.getOldValues(itemsDO).size());
            ChangeSummary.Setting containmentSetting = (ChangeSummary.Setting)salesPO1CS.getOldValues(itemsDO).get(0);
            assertEquals("item", containmentSetting.getProperty().getName());
            assertTrue(containmentSetting.getValue() instanceof List);
            assertEquals(2, ((List)containmentSetting.getValue()).size());
            assertEquals(true, containmentSetting.isSet());
        }

        assertEquals(16, ((SDOChangeSummary)salesPO1CS).getOldContainer().size());
        assertEquals(16, ((SDOChangeSummary)salesPO1CS).getOldContainmentProperty().size());
    }    

    public void assertDeleteDetachUnsetComplexSingleAtRoot(boolean testLoadSave, //
    		boolean isDeleted, //
    		DataObject po1DO,//
    		DataObject itemsDO,//
    		DataObject item1DO,//
    		DataObject item2DO,//
    		DataObject item1ProductDO,//
    		DataObject item1ProductPrice1DO,//
    		DataObject item1ProductPrice2DO//
    		) {
        assertEquals(10, salesPO1CS.getChangedDataObjects().size());
        assertEquals(1, salesPO1CS.getOldValues(po1DO).size());
        if (!testLoadSave) {
            assertModified(po1DO, salesPO1CS);            
            if (isDeleted) {
                assertDeleted(itemsDO, salesPO1CS);
                assertFalse(itemsDO == salesPO1CS.getOldValue(po1DO, po1DO.getInstanceProperty("items"))); // verify that oldValue of original deleted is deep copy
                Object  oldValueItem = salesPO1CS.getOldValue(itemsDO, itemsDO.getInstanceProperty("item")).getValue();

                // Bug# 5895047 - verify we are not getting an oldSetting that is an empty ArrayList
                assertTrue(oldValueItem instanceof ArrayList);
                assertEquals(2, ((ArrayList)oldValueItem).size());
                List anItemList = ((ArrayList)oldValueItem);
                DataObject oldItem1DO = (DataObject) anItemList.get(0); 
                assertNotNull(oldItem1DO);
                assertFalse(item1ProductPrice1DO == oldItem1DO); // verify that oldValue of original deleted is deep copy                
                assertDeleted(item1DO, salesPO1CS);
                DataObject oldItem2DO = (DataObject) anItemList.get(1); 
                assertNotNull(oldItem2DO);
                assertFalse(item1ProductPrice2DO == oldItem2DO); // verify that oldValue of original deleted is deep copy                
                assertDeleted(item2DO, salesPO1CS);                

                assertDeleted(item1ProductDO, salesPO1CS);
                assertFalse(item1ProductDO == salesPO1CS.getOldValue(item1DO, item1DO.getInstanceProperty("product"))); // verify that oldValue of original deleted is deep copy                
                assertDeleted(item1ProductPrice1DO, salesPO1CS);
                Object  oldValuePrice = salesPO1CS.getOldValue(item1ProductDO, item1ProductDO.getInstanceProperty("price")).getValue();
                // Bug# 5895047 - verify we are not getting an oldSetting that is an empty ArrayList
                assertTrue(oldValuePrice instanceof ArrayList);
                assertEquals(2, ((ArrayList)oldValuePrice).size());
                List aPriceList = ((ArrayList)oldValuePrice);
                DataObject oldItem1ProductPrice1DO = (DataObject) aPriceList.get(0); 
                assertNotNull(oldItem1ProductPrice1DO);
                assertFalse(item1ProductPrice1DO == oldItem1ProductPrice1DO); // verify that oldValue of original deleted is deep copy                
                assertDeleted(item1ProductPrice2DO, salesPO1CS);
                DataObject oldItem2ProductPrice1DO = (DataObject) aPriceList.get(1); 
                assertNotNull(oldItem2ProductPrice1DO);
                assertFalse(item1ProductPrice2DO == oldItem2ProductPrice1DO); // verify that oldValue of original deleted is deep copy                
            } else {
                assertDetached(item1DO, salesPO1CS);    
                assertDetached(item1ProductDO, salesPO1CS, false);
                assertDetached(item1ProductPrice1DO, salesPO1CS, false);
                assertDetached(item1ProductPrice2DO, salesPO1CS, false);
            }
        }

        assertEquals(10, salesPO1CS.getChangedDataObjects().size());

        if (!testLoadSave) {
            assertEquals(1, salesPO1CS.getOldValues(itemsDO).size());
            ChangeSummary.Setting containmentSetting = (ChangeSummary.Setting)salesPO1CS.getOldValues(itemsDO).get(0);
            assertEquals("item", containmentSetting.getProperty().getName());
            assertTrue(containmentSetting.getValue() instanceof List);
            assertEquals(2, ((List)containmentSetting.getValue()).size());
            assertEquals(true, containmentSetting.isSet());
        }

        assertEquals(16, ((SDOChangeSummary)salesPO1CS).getOldContainer().size());
        assertEquals(16, ((SDOChangeSummary)salesPO1CS).getOldContainmentProperty().size());
    }    
}
