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

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sdo.helper.delegates.SDOTypeHelperDelegate;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.ChangeSummaryRootLoadAndSaveTestCases;
import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public abstract class ChangeSummaryOnRootTestCases extends ChangeSummaryRootLoadAndSaveTestCases {
    protected DataObject rootObject;
    protected ChangeSummary cs;
    public static final String URINAME = "http://www.example.org";
    public static final String TYPENAME = "purchaseOrder";

    protected String getControlRootURI() {
        return URINAME;
    }

    protected String getControlRootName() {
        return TYPENAME;
    }
    
    protected String getRootInterfaceName() {
        return "PurchaseOrderType";
    }

    protected String getControlFileName() {
        // implemented by subclass
        return getControlFileName2();
    }
    
    public void testClassGenerationLoadAndSave() throws Exception {
      //TODO:implement
    }

    protected abstract String getControlFileName2();

    //protected String getNoSchemaControlFileName() {
    //    return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderDeepWithCS.xml");
    //}
    protected String getModelFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderDeepWithCS.xml");
    }
    
    public void testNoSchemaLoadFromInputStreamSaveDataObjectToString() throws Exception {
        //do nothing
        //TODO: need to make this test run
    }
    
    public ChangeSummaryOnRootTestCases(String name) {
        super(name);
    }

    public String getSchemaName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderDeepWithCS.xsd");
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryOnRootTestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() {
        super.setUp();// watch setup redundancy
        //define types from deep with cs 
        try {
            InputStream is = new FileInputStream(getSchemaName());
            List types = xsdHelper.define(is, null);
            XMLDocument document = xmlHelper.load(new FileInputStream(getModelFileName()));
            rootObject = document.getRootObject();
            cs = rootObject.getChangeSummary();
            cs.endLogging();
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred loading the xsd");
        }
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

    protected List defineTypes() {
        // do not define types twice - so we do not set dirty=true
        //return xsdHelper.define(getSchema(getSchemaName()));
        return (List)new ArrayList(((SDOTypeHelper)typeHelper).getTypesHashMap().values());
    }

    protected String getSchemaLocation() {
        return "";
    }

    public DataObject loadObjectFromInputStream() throws Exception {
        List types = defineTypes();
        FileInputStream inputStream = new FileInputStream(getControlFileName());
        XMLDocument document = xmlHelper.load(inputStream);

        // reset global variables for this xml based test
        rootObject = document.getRootObject();
        cs = document.getRootObject().getChangeSummary();

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
        StringReader sreader = new StringReader(outputStream.toString());
        InputSource inputSource = new InputSource(sreader);
        Document testDocument = parser.parse(inputSource);
        sreader.close();
        assertXMLIdentical(getDocument(getControlFileName()), testDocument);
    }

    public void assertDeleteDetachUnsetComplexManyBelowRoot(boolean testLoadSave,//
                                                            boolean isDeleted,//
                                                            DataObject itemsDO,//
                                                            DataObject item2DO,//
                                                            DataObject item2ProductDO,//
                                                            DataObject item2ProductPrice1DO,//
                                                            DataObject item2ProductPrice2DO) {
        if (!testLoadSave) {
            assertModified(itemsDO, cs);
            if (isDeleted) {
                assertDeleted(item2DO, cs);
                assertDeleted(item2ProductDO, cs);
                assertDeleted(item2ProductPrice1DO, cs);
                assertDeleted(item2ProductPrice2DO, cs);
            } else {
                assertDetached(item2DO, cs);
                assertDetached(item2ProductDO, cs, false);
                assertDetached(item2ProductPrice1DO, cs, false);
                assertDetached(item2ProductPrice2DO, cs, false);
            }
        }

        assertEquals(5, cs.getChangedDataObjects().size());

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

        if (!testLoadSave) {
            assertEquals(1, cs.getOldValues(itemsDO).size());
            ChangeSummary.Setting containmentSetting = (ChangeSummary.Setting)cs.getOldValues(itemsDO).get(0);
            assertEquals("item", containmentSetting.getProperty().getName());
            assertTrue(containmentSetting.getValue() instanceof List);
            assertEquals(2, ((List)containmentSetting.getValue()).size());
            assertEquals(true, containmentSetting.isSet());
        }
    }

    public void assertDeleteComplexSingle(DataObject shipToDO,//
                                          DataObject yardDO,//
                                          List phoneList,//
                                          DataObject phone1,//
                                          DataObject phone2,//
                                          Property containmentProp,//
                                          Object oldStreet) {
        // for dataType verify copy of shipTo has a set child in deleted list and current value is unset
        assertNull(shipToDO.get("street"));
        DataObject deepCopyShipTo = (DataObject)((SDOChangeSummary)cs).getDeepCopies().get(shipToDO);
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
        assertEquals(shipToDO, setting.getValue());
        assertEquals(true, setting.isSet());
        assertEquals(8, cs.getOldValues(shipToDO).size());

        assertEquals(1, cs.getOldValues(phone1).size());
        assertEquals(1, cs.getOldValues(phone2).size());
        ChangeSummary.Setting phone2setting = (ChangeSummary.Setting)cs.getOldValues(phone2).get(0);
        assertEquals("number", phone2setting.getProperty().getName());
        assertEquals("2345678", phone2setting.getValue());
        assertEquals(true, phone2setting.isSet());

    }

    public void assertDeleteDetachUnsetComplexSingleBelowRoot//testDeleteYard() {
    (boolean testLoadSave, boolean isDeleted, DataObject shipToDO, DataObject yardDO) {
        //DataObject shipToDO = rootObject.getDataObject("shipTo");
        //DataObject yardDO = shipToDO.getDataObject("yard");
        //cs.beginLogging();
        //yardDO.delete();
        assertEquals(2, cs.getChangedDataObjects().size());
        //assertEquals(1, cs.getOldValues(shipToDO).size());
        if (!testLoadSave) {
            if (isDeleted) {
                assertYardDeleted(shipToDO, yardDO, cs, true);
            } else {
                assertYardDetached(shipToDO, yardDO, cs, false);
            }
            assertModified(shipToDO, cs);
            ChangeSummary.Setting shipToSetting = (ChangeSummary.Setting)cs.getOldValues(shipToDO).get(0);
            assertEquals("yard", shipToSetting.getProperty().getName());
            assertEquals(true, shipToSetting.isSet());
            DataObject deepCopyYard = (DataObject)((SDOChangeSummary)cs).getDeepCopies().get(yardDO);
            assertEquals(deepCopyYard, shipToSetting.getValue());
            assertEquals(shipToDO, cs.getOldContainer(yardDO));
        }

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

    }

    public void assertCSonRootDeleteDetachUnsetComplexManyAtRoot//testDeleteItems() {
    (boolean testLoadSave,//
     DataObject itemsDO,//
     DataObject item1DO,//
     DataObject item1ProductDO,//
     DataObject item1ProductPrice1DO,//
     DataObject item1ProductPrice2DO,//
     DataObject item2DO,//
     DataObject item2ProductDO,//
     DataObject item2ProductPrice1DO,//
     DataObject item2ProductPrice2DO) {
        //DataObject itemsDO = rootObject.getDataObject("items");
        //DataObject item1DO = rootObject.getDataObject("items/item[1]");
        //DataObject item2DO = rootObject.getDataObject("items/item[2]");
        //DataObject item1ProductDO = item1DO.getDataObject("product");
        //DataObject item1ProductPrice1DO = item1ProductDO.getDataObject("price[1]");
        //DataObject item1ProductPrice2DO = item1ProductDO.getDataObject("price[2]");
        //DataObject item2ProductDO = item2DO.getDataObject("product");
        //DataObject item2ProductPrice1DO = item2ProductDO.getDataObject("price[1]");
        //DataObject item2ProductPrice2DO = item2ProductDO.getDataObject("price[2]");
        //cs.beginLogging();
        //itemsDO.delete();
        if (!testLoadSave) {
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
        }
        assertEquals(10, cs.getChangedDataObjects().size());

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());//  from 9

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());// from 9

        // verify keys of oldSettings Map
        //checkOldSettingsSizeTree("0000", changeSummaryB, root, dataObjectB, dataObjectC, dataObjectD);
    }

    public void assertCSonRootDeleteDetachUnsetComplexManyLeafBelowComplexManyBelowRoot// testDetachItem2Price1() {
    (boolean testLoadSave,//
     DataObject itemsDO,//
     DataObject item1DO,//
     DataObject item1ProductDO,//
     DataObject item1ProductPrice1DO,//
     DataObject item1ProductPrice2DO,//
     DataObject item2DO,//
     DataObject item2ProductDO,//
     DataObject item2ProductPrice1DO,//
     DataObject item2ProductPrice2DO) {
        //DataObject itemsDO = rootObject.getDataObject("items");
        //DataObject item1DO = rootObject.getDataObject("items/item[1]");
        //DataObject item2DO = rootObject.getDataObject("items/item[2]");
        //DataObject item1ProductDO = item1DO.getDataObject("product");
        //DataObject item1ProductPrice1DO = item1ProductDO.getDataObject("price[1]");
        //DataObject item1ProductPrice2DO = item1ProductDO.getDataObject("price[2]");
        //DataObject item2ProductDO = item2DO.getDataObject("product");
        //DataObject item2ProductPrice1DO = item2ProductDO.getDataObject("price[1]");
        //DataObject item2ProductPrice2DO = item2ProductDO.getDataObject("price[2]");
        //cs.beginLogging();
        //item2ProductPrice1DO.detach();
        if (!testLoadSave) {
            assertModified(item2ProductDO, cs);
            assertDetached(item2ProductPrice1DO, cs);
            ChangeSummary.Setting containmentSetting = (ChangeSummary.Setting)cs.getOldValues(item2ProductDO).get(0);
            assertEquals("price", containmentSetting.getProperty().getName());
            assertTrue(containmentSetting.getValue() instanceof List);
            assertEquals(2, ((List)containmentSetting.getValue()).size());
            assertEquals(true, containmentSetting.isSet());
        }
        assertEquals(2, cs.getChangedDataObjects().size());

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

    }

    /*
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
        }


        public void testDetachItems() {
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
            assertDetached(item1DO, cs, false); // internal children of a detach have a non-null container
            assertDetached(item2DO, cs, false); // internal children of a detach have a non-null container
            assertDetached(item1ProductDO, cs, false); // internal children of a detach have a non-null container
            assertDetached(item1ProductPrice1DO, cs, false); // internal children of a detach have a non-null container
            assertDetached(item1ProductPrice2DO, cs, false); // internal children of a detach have a non-null container
            assertDetached(item2ProductDO, cs, false); // internal children of a detach have a non-null container
            assertDetached(item2ProductPrice1DO, cs, false); // internal children of a detach have a non-null container
            assertDetached(item2ProductPrice2DO, cs, false); // internal children of a detach have a non-null container
            assertEquals(10, cs.getChangedDataObjects().size());

            assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());//  from 9

            assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());// from 9
            assertEquals(10, ((SDOChangeSummary)cs).getOldSettings().size());

            // verify keys of oldSettings Map
            //checkOldSettingsSizeTree("0000", changeSummaryB, root, dataObjectB, dataObjectC, dataObjectD);
        }

        // test unset of single property containing an isMany=true set of DataObjects
        public void testUnsetItems() {
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
    //                phoneList, phone1, phone2, containmentProp, oldStreet);

            assertNotNull(item1DO);
            assertNotNull(item2DO);

            assertModified(rootObject, cs);
            assertDetached(itemsDO, cs);
            assertDetached(item1DO, cs, false); // internal children of a detach have a non-null container
            assertDetached(item2DO, cs, false); // internal children of a detach have a non-null container
            assertDetached(item1ProductDO, cs, false); // internal children of a detach have a non-null container
            assertDetached(item1ProductPrice1DO, cs, false); // internal children of a detach have a non-null container
            assertDetached(item1ProductPrice2DO, cs, false); // internal children of a detach have a non-null container
            assertDetached(item2ProductDO, cs, false); // internal children of a detach have a non-null container
            assertDetached(item2ProductPrice1DO, cs, false); // internal children of a detach have a non-null container
            assertDetached(item2ProductPrice2DO, cs, false); // internal children of a detach have a non-null container
            assertEquals(10, cs.getChangedDataObjects().size());

            assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());//  from 9

            assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());// from 9
            assertEquals(10, ((SDOChangeSummary)cs).getOldSettings().size());

            // verify keys of oldSettings Map
            //checkOldSettingsSizeTree("0000", changeSummaryB, root, dataObjectB, dataObjectC, dataObjectD);
        }

        // Test Scenarios:
        // - many delete
        public void testDeleteItem2Price1() {
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
            assertEquals(2, ((SDOChangeSummary)cs).getOldSettings().size());

            ChangeSummary.Setting containmentSetting = (ChangeSummary.Setting)cs.getOldValues(item2ProductDO).get(0);
            assertEquals("price", containmentSetting.getProperty().getName());
            assertTrue(containmentSetting.getValue() instanceof List);
            assertEquals(2, ((List)containmentSetting.getValue()).size());
            assertEquals(true, containmentSetting.isSet());
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
            assertEquals(2, ((SDOChangeSummary)cs).getOldSettings().size());

            ChangeSummary.Setting containmentSetting = (ChangeSummary.Setting)cs.getOldValues(item2ProductDO).get(0);
            assertEquals("price", containmentSetting.getProperty().getName());
            assertTrue(containmentSetting.getValue() instanceof List);
            assertEquals(2, ((List)containmentSetting.getValue()).size());
            assertEquals(true, containmentSetting.isSet());
        }

        // Note: unset of a single item in a list is different than a detach in that all the items under
        // property being unset are detached.
        public void testUnsetItem2Price() {
            DataObject itemsDO = rootObject.getDataObject("items");
            DataObject item1DO = rootObject.getDataObject("items/item[1]");
            DataObject item2DO = rootObject.getDataObject("items/item[2]");

            DataObject item1ProductDO = item1DO.getDataObject("product");
            DataObject item1ProductPrice1DO = item1ProductDO.getDataObject("price[1]");
            DataObject item1ProductPrice2DO = item1ProductDO.getDataObject("price[2]");

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
            assertEquals(3, ((SDOChangeSummary)cs).getOldSettings().size());

            ChangeSummary.Setting containmentSetting = (ChangeSummary.Setting)cs.getOldValues(item2ProductDO).get(0);
            assertEquals("price", containmentSetting.getProperty().getName());
            assertTrue(containmentSetting.getValue() instanceof List);
            assertEquals(2, ((List)containmentSetting.getValue()).size());
            assertEquals(true, containmentSetting.isSet());
        }

        // unset a single item in a list
        // TODO: this test will fail as of 20070111 until bug#5757236 is fixed
        public void testUnsetItem2Price2() {
            DataObject item2DO = rootObject.getDataObject("items/item[2]");

            DataObject item2ProductDO = item2DO.getDataObject("product");
            DataObject item2ProductPrice1DO = item2ProductDO.getDataObject("price[1]");
            DataObject item2ProductPrice2DO = item2ProductDO.getDataObject("price[2]");

            cs.beginLogging();
            // test isMany section of unset() - all items including price1 and price2 are unset

            item2ProductDO.unset("price[2]");

            assertModified(item2ProductDO, cs);
            assertDetached(item2ProductPrice1DO, cs);
            assertUnchanged(item2ProductPrice2DO, cs); // TODO: failure starts here

            assertEquals(2, cs.getChangedDataObjects().size());

            assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
            assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());
            assertEquals(2, ((SDOChangeSummary)cs).getOldSettings().size());

            ChangeSummary.Setting containmentSetting = (ChangeSummary.Setting)cs.getOldValues(item2ProductDO).get(0);
            assertEquals("price", containmentSetting.getProperty().getName());
            assertTrue(containmentSetting.getValue() instanceof List);
            assertEquals(2, ((List)containmentSetting.getValue()).size());
            assertEquals(true, containmentSetting.isSet());
        }

        public void testDetachAndResetYardToSamePlace() {
            DataObject shipToDO = rootObject.getDataObject("shipTo");
            Property containmentProp = shipToDO.getInstanceProperty("yard");

            DataObject yardDO = shipToDO.getDataObject("yard");

            Object length = yardDO.get("length");

            cs.beginLogging();
            yardDO.detach();

            List yardDOSettings = cs.getOldValues(yardDO);
            assertEquals(3, yardDOSettings.size());

            assertDetached(yardDO, cs);
            assertModified(shipToDO, cs);
            assertFalse(cs.isModified(yardDO));

            assertEquals(2, cs.getChangedDataObjects().size());

            shipToDO.set("yard", yardDO);

            assertFalse(cs.isDeleted(yardDO));
            //assertFalse(cs.isModified(shipToDO)); //if we add new logic
            assertTrue(cs.isModified(shipToDO));

            assertFalse(cs.isModified(yardDO));

            //assertEquals(0, cs.getChangedDataObjects().size());  //if we add new logic
            assertEquals(1, cs.getChangedDataObjects().size());//just shipTo

            assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
            assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

            //assertEquals(0, ((SDOChangeSummary)cs).getOldSettings().size()); // if we add new logic
            assertEquals(2, ((SDOChangeSummary)cs).getOldSettings().size());//

            List shipToSettings = cs.getOldValues(shipToDO);
            assertEquals(1, shipToSettings.size());

            yardDOSettings = cs.getOldValues(yardDO);
            // see cs.getOldValues() does not return and empty List when yardDO is !modified and !deleted (but !created as well) - (re)set
            assertEquals(3, yardDOSettings.size());

            // see cs.getOldValue() does not return null when yardDO is !modified and !deleted (but !created as well) - (re)set
            ChangeSummary.Setting lengthSetting = cs.getOldValue(yardDO, yardDO.getInstanceProperty("length"));
            assertNotNull(lengthSetting);
            assertEquals(length, lengthSetting.getValue());

        }

        // Verify that an unset followed by a (re)set of the same subtree returns tree to original state
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
            assertEquals(2, ((SDOChangeSummary)cs).getOldSettings().size());

            ChangeSummary.Setting containmentSetting = (ChangeSummary.Setting)cs.getOldValues(item2ProductDO).get(0);
            assertEquals("price", containmentSetting.getProperty().getName());
            assertTrue(containmentSetting.getValue() instanceof List);
            assertEquals(2, ((List)containmentSetting.getValue()).size());
            assertEquals(true, containmentSetting.isSet());

            // UNDO - reattach subtree
            ((ListWrapper)item2ProductDO.get("price")).add(0, item2ProductPrice1DO);

            assertFalse(cs.isDeleted(item2ProductPrice1DO));
            //assertUnchanged(cs.isModified(item2ProductDO)); //if we add new logic
            assertModified(item2ProductDO, cs);

            assertFalse(cs.isModified(item2ProductPrice1DO));

            //assertEquals(0, cs.getChangedDataObjects().size());  //if we add new logic
            assertEquals(1, cs.getChangedDataObjects().size());

            assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
            assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());
            //assertEquals(0, ((SDOChangeSummary)cs).getOldSettings().size()); // if we add new logic
            assertEquals(2, ((SDOChangeSummary)cs).getOldSettings().size());
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
            assertEquals(2, ((SDOChangeSummary)cs).getOldSettings().size());

            ChangeSummary.Setting containmentSetting = (ChangeSummary.Setting)cs.getOldValues(item2ProductDO).get(0);
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
            assertEquals(2, ((SDOChangeSummary)cs).getOldSettings().size());
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
            assertEquals(3, ((SDOChangeSummary)cs).getOldSettings().size());
            assertEquals(1, ((SDOChangeSummary)cs).getOldValues(itemsDO).size());// from 2 (only 1st change on items is recorded)
            assertEquals(5, ((SDOChangeSummary)cs).getOldValues(item3DO).size());// 1
            assertEquals(5, ((SDOChangeSummary)cs).getOldValues(item5DO).size());// 1
            ChangeSummary.Setting containmentSetting = (ChangeSummary.Setting)cs.getOldValues(itemsDO).get(0);
            assertEquals("item", containmentSetting.getProperty().getName());
            assertTrue(containmentSetting.getValue() instanceof List);
            assertEquals(6, ((List)containmentSetting.getValue()).size());
            assertEquals(true, containmentSetting.isSet());// see detach(boolean)
        }

        // test deletion inside a list - see detach() isMany section
        // Test Scenarios:
        // - many delete
        public void testDeleteItem2() {
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
            assertEquals(5, ((SDOChangeSummary)cs).getOldSettings().size());

            assertEquals(1, cs.getOldValues(itemsDO).size());
            ChangeSummary.Setting containmentSetting = (ChangeSummary.Setting)cs.getOldValues(itemsDO).get(0);
            assertEquals("item", containmentSetting.getProperty().getName());
            assertTrue(containmentSetting.getValue() instanceof List);
            assertEquals(2, ((List)containmentSetting.getValue()).size());
            assertEquals(true, containmentSetting.isSet());
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

            assertEquals(billToDO.getChangeSummary(),yardDO.getChangeSummary());

            assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
            assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());
            // test detach from updateContainment
            assertEquals(3, ((SDOChangeSummary)cs).getOldSettings().size());
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
            assertEquals(yardDO, shipToSetting.getValue());
            assertEquals(true, shipToSetting.isSet());

            assertEquals(shipToDO, cs.getOldContainer(yardDO));
            assertEquals(containmentProp, cs.getOldContainmentProperty(yardDO));
        }

        public void testMoveYardWithDetachAndSet() {
            DataObject shipToDO = rootObject.getDataObject("shipTo");
            Property containmentProp = shipToDO.getInstanceProperty("yard");

            DataObject billToDO = rootObject.getDataObject("billTo");
            DataObject yardDO = shipToDO.getDataObject("yard");
            cs.beginLogging();
            yardDO.detach();
            assertTrue(cs.isDeleted(yardDO));
            billToDO.set("yard", yardDO);

            assertModified(shipToDO, cs);
            assertModified(billToDO, cs);
            assertFalse(cs.isDeleted(yardDO));
            assertEquals(2, cs.getChangedDataObjects().size());

            assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
            assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());
            // TODO: VERIFY 2 -> 3
            assertEquals(3, ((SDOChangeSummary)cs).getOldSettings().size());
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
            assertEquals(yardDO, shipToSetting.getValue());
            assertEquals(true, shipToSetting.isSet());

            assertEquals(shipToDO, cs.getOldContainer(yardDO));
            assertEquals(containmentProp, cs.getOldContainmentProperty(yardDO));
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
            assertEquals(yardDO, shipToSetting.getValue());

            assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
            assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

            assertEquals(shipToDO, cs.getOldContainer(yardDO));
        }

        // test deletion inside a list - see detach() isMany section
        // Test Scenarios:
        // - many delete
        public void testDeletePhone2() {
            DataObject shipToDO = rootObject.getDataObject("shipTo");
            DataObject phoneDO = (DataObject)shipToDO.getDataObject("phone[2]");

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
            assertTrue(((List)phoneSetting.getValue()).contains(phoneDO));

            assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
            assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

            assertEquals(shipToDO, cs.getOldContainer(phoneDO));
        }

    */
    public void assertCSonRootUnsetSimpleManyAtRootAfterSetNull(boolean testLoadSave) {//testUnsetComments() {
        assertUnchanged(rootObject, cs);
        assertEquals(0, cs.getChangedDataObjects().size());

        List oldValues = cs.getOldValues(rootObject);
        assertEquals(0, oldValues.size());
    }

    public void assertCSonRootUnsetSimpleManyAtRootAfterUnset(boolean testLoadSave) {//testUnsetComments() {
        assertModified(rootObject, cs);
        assertEquals(1, cs.getChangedDataObjects().size());

        List oldValues = cs.getOldValues(rootObject);
        assertEquals(1, oldValues.size());

        ChangeSummary.Setting theSetting = (ChangeSummary.Setting)oldValues.get(0);
        assertEquals("comment", theSetting.getProperty().getName());
        assertEquals(true, theSetting.isSet());
        assertEquals(null, theSetting.getValue());
    }

    public void assertCSonRootUnsetSimpleSingleAtRootAfterSetNull(boolean testLoadSave) {//testUnsetSimpleIDOnRoot() {
        assertModified(rootObject, cs);
        assertEquals(1, cs.getChangedDataObjects().size());

        List oldValues = cs.getOldValues(rootObject);
        assertEquals(1, oldValues.size());

        ChangeSummary.Setting theSetting = (ChangeSummary.Setting)oldValues.get(0);
        assertEquals("poId", theSetting.getProperty().getName());
        assertEquals(true, theSetting.isSet());
        assertEquals(null, theSetting.getValue());
    }

    public void assertCSonRootUnsetSimpleSingleAtRootAfterUnset(boolean testLoadSave) {//testUnsetNameAfterLoggingOnAfterUnset() {
        assertUnchanged(rootObject, cs);//
        assertEquals(0, cs.getChangedDataObjects().size());

        List oldValues = cs.getOldValues(rootObject);
        assertEquals(0, oldValues.size());
    }

    /*
            // test that we do not loose containment in the oldSetting - by unwrapping the ListWrapper into a List
            public void testUnsetFilledSimpleListOnRootRetainsContainmentInOldSetting() {
                List comments = new ArrayList();
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
            }

            public void testSetCommentToSameValue() {
                rootObject.set("poId", "123");
                cs.beginLogging();
                rootObject.set("poId", "123");

                assertUnchanged(rootObject, cs);
                assertEquals(0, cs.getChangedDataObjects().size());

                List oldValues = cs.getOldValues(rootObject);
                assertEquals(0, oldValues.size());
            }
        */
    public void assertCSonRootDetachComplexSingleAtRoot(boolean testLoadSave, DataObject shipToDO) {// detachShipTo() {
        //DataObject shipToDO = rootObject.getDataObject("shipTo");
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
        verifyCSonRootDetachUnsetComplexSingleAtRoot(testLoadSave, shipToDO, yardDO,//
                                                     phoneList, phone1, phone2, containmentProp, oldStreet);
    }

    public void verifyCSonRootDetachUnsetComplexSingleAtRoot//testDeleteShipTo() 
    (boolean testLoadSave, DataObject shipToDO, Property containmentProp, DataObject yardDO, DataObject phone1, DataObject phone2) {
        //DataObject shipToDO = rootObject.getDataObject("shipTo");
        //DataObject yardDO = shipToDO.getDataObject("yard");
        //List phoneList = shipToDO.getList("phone");
        //DataObject phone1 = (DataObject)phoneList.get(0);
        //DataObject phone2 = (DataObject)phoneList.get(1);
        //Property containmentProp = shipToDO.getContainmentProperty();
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

        if (!testLoadSave) {
            // for dataType verify copy of shipTo has a set child in deleted list and current value is unset - reverse for unset/detach
            assertNotNull(shipToDO.get("street"));
            DataObject oldContainer = ((SDOChangeSummary)cs).getOldContainer(shipToDO);
            Property shipToProp = oldContainer.getInstanceProperty("shipTo");
            ChangeSummary.Setting oldSetting = ((SDOChangeSummary)cs).getOldValue(oldContainer, shipToProp);
            DataObject deepCopyShipTo = (DataObject)oldSetting.getValue();

            assertEquals("123 Maple Street", deepCopyShipTo.get("street"));
            assertModified(rootObject, cs);
            assertNotNull(cs.getOldContainmentProperty(shipToDO));
            assertNotNull(cs.getOldContainer(shipToDO));
            assertEquals(rootObject, cs.getOldContainer(shipToDO));

            assertEquals(shipToDO, cs.getOldContainer(yardDO));
            assertEquals(shipToDO, cs.getOldContainer(phone1));
            assertEquals(shipToDO, cs.getOldContainer(phone2));

            assertEquals(containmentProp, cs.getOldContainmentProperty(shipToDO));
            assertDetached(shipToDO, cs, false);
            //assertDeleted(yardDO, cs);
            assertYardDetached(shipToDO, yardDO, cs, false);
            assertDetached(phone1, cs, false);
            assertDetached(phone2, cs, false);
        }
        assertEquals(5, cs.getChangedDataObjects().size());
        //assertEquals(1, cs.getOldValues(rootObject).size());
        ChangeSummary.Setting setting = (ChangeSummary.Setting)cs.getOldValues(rootObject).get(0);
        if (!testLoadSave) {
            assertEquals(containmentProp, setting.getProperty());
            DataObject deepCopyShipTo = (DataObject)((SDOChangeSummary)cs).getDeepCopies().get(shipToDO);
            assertEquals(deepCopyShipTo, setting.getValue());
            assertEquals(true, setting.isSet());
            assertEquals(8, cs.getOldValues(shipToDO).size());
            assertEquals(1, cs.getOldValues(phone1).size());
            assertEquals(1, cs.getOldValues(phone2).size());
            ChangeSummary.Setting phone2setting = (ChangeSummary.Setting)cs.getOldValues(phone2).get(0);
            assertEquals("number", phone2setting.getProperty().getName());
            assertEquals("2345678", phone2setting.getValue());
            assertEquals(true, phone2setting.isSet());
        }
    }

    public void assertCSonRootUnsetComplexSingleAtRoot(boolean testLoadSave, DataObject shipToDO) {// unsetShipTo() {        
        DataObject yardDO = shipToDO.getDataObject("yard");
        List phoneList = shipToDO.getList("phone");
        DataObject phone1 = (DataObject)phoneList.get(0);
        DataObject phone2 = (DataObject)phoneList.get(1);
        Property containmentProp = shipToDO.getContainmentProperty();

        Object oldStreet = shipToDO.get("street");

        cs.beginLogging();
        rootObject.unset(containmentProp);

        verifyCSonRootDetachUnsetComplexSingleAtRoot(testLoadSave, shipToDO, yardDO,//
                                                     phoneList, phone1, phone2, containmentProp, oldStreet);
    }

    public void verifyCSonRootDetachUnsetComplexSingleAtRoot(//
    boolean testLoadSave,//
    DataObject shipToDO,//
    DataObject yardDO,//
    List phoneList,//
    DataObject phone1,//
    DataObject phone2,//
    Property containmentProp,//
    Object oldStreet) {
        // verify containers are not unset for children (oldSetting is same object as original - inside List)
        //assertNotNull(((DataObject)shipToDO.getList("phone").get(0)).getContainer());
        //assertNotNull(phone1.getContainer());
        //assertEquals(phone1.getContainer(), shipToDO);
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

        assertEquals("123 Maple Street", shipToDO.get("street"));
        DataObject deepCopyShipTo = (DataObject)((SDOChangeSummary)cs).getDeepCopies().get(shipToDO);
        assertEquals("123 Maple Street", deepCopyShipTo.get("street"));

        if (!testLoadSave) {
            assertModified(rootObject, cs);
            assertNotNull(cs.getOldContainmentProperty(shipToDO));
            assertNotNull(cs.getOldContainer(shipToDO));
            assertEquals(rootObject, cs.getOldContainer(shipToDO));
            assertEquals(containmentProp, cs.getOldContainmentProperty(shipToDO));
            assertDetached(shipToDO, cs);// 8/0
            assertYardDetached(shipToDO, yardDO, cs, false);//
            assertDetached(phone1, cs, false);
            assertDetached(phone2, cs, false);
        }

        assertEquals(5, cs.getChangedDataObjects().size());// 2
        assertEquals(1, cs.getOldValues(rootObject).size());
        ChangeSummary.Setting setting = (ChangeSummary.Setting)cs.getOldValues(rootObject).get(0);
        assertEquals(containmentProp, setting.getProperty());
        assertEquals(shipToDO, setting.getValue());
        assertEquals(true, setting.isSet());
        assertEquals(8, cs.getOldValues(shipToDO).size());// 0

        Object newStreet = shipToDO.get("street");

        // verify that children of detached objects are not unset
        assertNotNull(newStreet);
        assertEquals(oldStreet, newStreet);
        assertEquals(1, cs.getOldValues(phone1).size());// 0
        assertEquals(1, cs.getOldValues(phone2).size());// 0
        ChangeSummary.Setting phone2setting = (ChangeSummary.Setting)cs.getOldValues(phone2).get(0);
        assertEquals("number", phone2setting.getProperty().getName());
        assertEquals("2345678", phone2setting.getValue());
        assertEquals(true, phone2setting.isSet());
        //writeXML(rootObject);
    }

    public void assertYardDeleted(DataObject shipToDO, DataObject yardDO, ChangeSummary cs, boolean nullContainer) {
        //assertYardDetached(shipToDO, yardDO, cs, nullContainer);
        assertDeleted(yardDO, cs, nullContainer);
        assertEquals(3, cs.getOldValues(yardDO).size());
        Property sfProp = yardDO.getInstanceProperty("squarefootage");
        Property widthProp = yardDO.getInstanceProperty("width");
        Property lengthProp = yardDO.getInstanceProperty("length");
        ChangeSummary.Setting yardSFsetting = (ChangeSummary.Setting)cs.getOldValue(yardDO, sfProp);
        assertEquals(yardSFsetting.getValue(), null);

        //TODO: uncomment this line.  Will fail unless Node Null policy stuff is fixed
        //assertEquals(false, yardSFsetting.isSet());        
        ChangeSummary.Setting yardWidthsetting = (ChangeSummary.Setting)cs.getOldValue(yardDO, widthProp);
        assertEquals("65", yardWidthsetting.getValue());
        assertEquals(true, yardWidthsetting.isSet());

        ChangeSummary.Setting yardLengththsetting = (ChangeSummary.Setting)cs.getOldValue(yardDO, lengthProp);
        assertEquals("45", yardLengththsetting.getValue());
        assertEquals(true, yardLengththsetting.isSet());
        // shipToDO was not deleted, only check yardDO
        assertChildrenUnset(yardDO);
    }

    public void assertYardDetached(DataObject shipToDO, DataObject yardDO, ChangeSummary cs, boolean nullContainer) {
        assertDetached(yardDO, cs, nullContainer);
        assertEquals(3, cs.getOldValues(yardDO).size());
        Property sfProp = yardDO.getInstanceProperty("squarefootage");
        Property widthProp = yardDO.getInstanceProperty("width");
        Property lengthProp = yardDO.getInstanceProperty("length");
        ChangeSummary.Setting yardSFsetting = (ChangeSummary.Setting)cs.getOldValue(yardDO, sfProp);
        assertEquals(yardSFsetting.getValue(), null);

        //TODO: uncomment this line.  Will fail unless Node Null policy stuff is fixed
        //assertEquals(false, yardSFsetting.isSet());        
        ChangeSummary.Setting yardWidthsetting = (ChangeSummary.Setting)cs.getOldValue(yardDO, widthProp);
        assertEquals("65", yardWidthsetting.getValue());
        assertEquals(true, yardWidthsetting.isSet());

        ChangeSummary.Setting yardLengththsetting = (ChangeSummary.Setting)cs.getOldValue(yardDO, lengthProp);
        assertEquals("45", yardLengththsetting.getValue());
        assertEquals(true, yardLengththsetting.isSet());
    }
}
