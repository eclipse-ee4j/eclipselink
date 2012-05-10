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
package org.eclipse.persistence.testing.sdo.model.changesummary.sequence;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOSequence;
import org.eclipse.persistence.testing.sdo.SDOTestCase;
import org.eclipse.persistence.exceptions.SDOException;

public class ChangeSummaryGetOldSequenceTestCases extends SDOTestCase {
    DataObject companyObject;
    DataObject purchaseOrderObject;
    DataObject address1Object;
    DataObject itemsObject;
    ChangeSummary cs;

    public ChangeSummaryGetOldSequenceTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.sequence.ChangeSummaryGetOldSequenceTestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() {
        super.setUp();

        try {
            InputStream is = new FileInputStream(getSchemaToDefine());
            List types = xsdHelper.define(is, null);

            //     XMLDocument doc = xmlHelper.load(new FileInputStream(getControlFileName()));
            //companyObject= doc.getRootObject();
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred during setup.");
        }
        companyObject = buildDataObjects();
        purchaseOrderObject = companyObject.getDataObject("order");
        assertNotNull(purchaseOrderObject);
        address1Object = (DataObject)(purchaseOrderObject.getList("address")).get(0);
        assertNotNull(address1Object);
        assertTrue(address1Object.getType().isSequenced());
        assertNotNull(address1Object.getSequence());
        assertNotNull(purchaseOrderObject.getChangeSummary());
        itemsObject = purchaseOrderObject.getDataObject("items");
        assertNotNull(itemsObject);
        assertNotNull(itemsObject.getSequence());
        cs = purchaseOrderObject.getChangeSummary();

    }

    public String getSchemaToDefine() {
        return "org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderComplexSequenced.xsd";
    }

    protected String getControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/sequence/purchaseOrderComplexSequenced.xml";
    }

    public void testGetOldSequenceNotLogging() {
        cs.endLogging();
        address1Object.set("city", "aaa");
        List oldValues = cs.getOldValues(address1Object);
        assertEquals(0, oldValues.size());
        address1Object.unset(address1Object.getInstanceProperty("city"));
        Sequence oldAddressSeq = cs.getOldSequence(address1Object);

        assertNotNull(oldAddressSeq);
    }

    public void testGetOldSequenceNotLoggingNull() {
        purchaseOrderObject.unset("address");
        cs.endLogging();
        address1Object.unset(address1Object.getInstanceProperty("city"));
        Sequence oldAddressSeq = cs.getOldSequence(address1Object);

        assertNotNull(oldAddressSeq);
    }

    public void testGetOldSequenceNotSequenced() {
        cs.beginLogging();
        DataObject lineItem1 = (DataObject)itemsObject.getList("item").get(0);
        assertNotNull(lineItem1);
        lineItem1.unset(0);
        Sequence oldLineItemSeq = cs.getOldSequence(lineItem1);

        //old seq should be null because line item is not sequenced
        assertNull(oldLineItemSeq);
    }

    public void testGetOldSequenceNull() {
        cs.beginLogging();

        Sequence oldSeq = cs.getOldSequence(null);
        assertNull(oldSeq);
    }

    public void testGetOldSequenceTwice() {
        Sequence originalSeq = address1Object.getSequence();
        String originalValue = (String)originalSeq.getValue(0);
        assertEquals("Alice Smith", originalValue);
        cs.beginLogging();
        address1Object.set("name", "newName");

        Sequence oldSeq1 = cs.getOldSequence(address1Object);
        assertNotNull(oldSeq1);
        assertNotSame(oldSeq1, originalSeq);

        Sequence oldSeq2 = cs.getOldSequence(address1Object);
        assertNotNull(oldSeq2);
        assertEquals(oldSeq1, oldSeq2);

        address1Object.set("name", "newName2");

        Sequence oldSeq3 = cs.getOldSequence(address1Object);
        assertNotNull(oldSeq3);
        assertEquals(oldSeq2, oldSeq3);		   
        
        String oldSeqValue = (String)oldSeq3.getValue(0);
        assertEquals("Alice Smith", oldSeqValue);

        String currentValue = (String)address1Object.getSequence().getValue(0);
        assertEquals("newName2", currentValue);
    }

    public void testGetOldSequenceAndOldValueAreEqual() {
        cs.beginLogging();

        DataObject lineItem1 = (DataObject)itemsObject.getList("item").get(0);
        assertNotNull(lineItem1);
        lineItem1.delete();

        Sequence oldSeq1 = cs.getOldSequence(itemsObject);
        assertNotNull(oldSeq1);
        Object seqFirstOldValue = oldSeq1.getValue(0);

        ChangeSummary.Setting itemOldSetting = cs.getOldValue(itemsObject, itemsObject.getInstanceProperty("item"));
        assertNotNull(itemOldSetting);
        Object firstValue = ((List)itemOldSetting.getValue()).get(0);
        assertEquals(seqFirstOldValue, firstValue);
    }

    public void testGetOldSequenceCreated() {
        cs.beginLogging();

        DataObject newAddress = dataFactory.create("http://www.example.org", "AddressType");
        assertNotNull(newAddress);
        newAddress.set("city", "newCity");

        purchaseOrderObject.getList("address").add(newAddress);

        assertCreated(newAddress, cs);
        assertNull(cs.getOldSequence(newAddress));
    }

    public void testGetOldSequenceUnchanged() {
        cs.beginLogging();
        //if the sequence has not changed return the current sequence
        Sequence oldSeq = cs.getOldSequence(address1Object);
        assertNotNull(oldSeq);
        assertEquals(4, ((SDOSequence)oldSeq).getSettings().size());

        assertEquals("name", oldSeq.getProperty(0).getName());
        assertEquals("street", oldSeq.getProperty(1).getName());
        assertEquals("city", oldSeq.getProperty(2).getName());
        assertEquals("state", oldSeq.getProperty(3).getName());

        assertEquals("Alice Smith", oldSeq.getValue(0));
        assertEquals("123 Maple Street", oldSeq.getValue(1));
        assertEquals("Mill Valley", oldSeq.getValue(2));
        assertEquals("CA", oldSeq.getValue(3));
    }

    public void testGetOldSequenceDetachedAddress() {
        cs.beginLogging();

        address1Object.detach();
        Sequence oldSeq = cs.getOldSequence(address1Object);
        assertNotNull(oldSeq);
        assertEquals(4, ((SDOSequence)oldSeq).getSettings().size());

        assertEquals("name", oldSeq.getProperty(0).getName());
        assertEquals("street", oldSeq.getProperty(1).getName());
        assertEquals("city", oldSeq.getProperty(2).getName());
        assertEquals("state", oldSeq.getProperty(3).getName());

        assertEquals("Alice Smith", oldSeq.getValue(0));
        assertEquals("123 Maple Street", oldSeq.getValue(1));
        assertEquals("Mill Valley", oldSeq.getValue(2));
        assertEquals("CA", oldSeq.getValue(3));

    }

    public void testGetOldSequenceDetachedItem() {
        DataObject lineItem1 = (DataObject)itemsObject.getList("item").get(0);
        DataObject lineItem2 = (DataObject)itemsObject.getList("item").get(1);
        Sequence currentSeq = itemsObject.getSequence();
        assertEquals(2, currentSeq.size());

        cs.beginLogging();
        lineItem1.detach();
        assertEquals(1, currentSeq.size());
        Sequence oldSeq = cs.getOldSequence(itemsObject);
        assertNotNull(oldSeq);
        assertEquals(2, ((SDOSequence)oldSeq).getSettings().size());

        DataObject oldLineItem1 = (DataObject)oldSeq.getValue(0);
        assertEquals("Lawnmower", oldLineItem1.get("productName"));

        DataObject oldLineItem2 = (DataObject)oldSeq.getValue(1);
        assertEquals("Baby Monitor", oldLineItem2.get("productName"));
        //should be a deep copy not the same object
        assertNotSame(lineItem1, oldLineItem1);
        assertNotSame(lineItem2, oldLineItem2);
        assertFalse(lineItem1.equals(oldLineItem1));
        assertFalse(lineItem2.equals(oldLineItem2));

    }

    public void testGetOldSequenceDeletedAddress() {
        cs.beginLogging();
        Sequence currentSeq = address1Object.getSequence();
        assertEquals(4, currentSeq.size());
        address1Object.delete();
        assertEquals(0, currentSeq.size());
        Sequence oldSeq = cs.getOldSequence(address1Object);
        assertNotNull(oldSeq);
        assertEquals(4, ((SDOSequence)oldSeq).getSettings().size());
    }

    public void testGetOldSequenceDeletedItem() {
        cs.beginLogging();
        DataObject lineItem1 = (DataObject)itemsObject.getList("item").get(0);
        DataObject lineItem2 = (DataObject)itemsObject.getList("item").get(1);
        Sequence currentSeq = itemsObject.getSequence();
        assertEquals(2, currentSeq.size());

        cs.beginLogging();
        lineItem1.delete();
        assertEquals(1, currentSeq.size());
        Sequence oldSeq = cs.getOldSequence(itemsObject);
        assertNotNull(oldSeq);
        assertEquals(2, ((SDOSequence)oldSeq).getSettings().size());

        DataObject oldLineItem1 = (DataObject)oldSeq.getValue(0);
        assertEquals("Lawnmower", oldLineItem1.get("productName"));

        DataObject oldLineItem2 = (DataObject)oldSeq.getValue(1);
        assertEquals("Baby Monitor", oldLineItem2.get("productName"));
        //should be a deep copy not the same object
        assertNotSame(lineItem1, oldLineItem1);
        assertNotSame(lineItem2, oldLineItem2);
        assertFalse(lineItem1.equals(oldLineItem1));
        assertFalse(lineItem2.equals(oldLineItem2));
    }

    public void testGetOldSequenceReattachedItem() {
        DataObject lineItem1 = (DataObject)itemsObject.getList("item").get(0);
        cs.beginLogging();
        lineItem1.detach();
        assertEquals(1, itemsObject.getSequence().size());
        DataObject firstObject = (DataObject)itemsObject.getSequence().getValue(0);
        assertEquals("Baby Monitor", firstObject.get("productName"));

        itemsObject.getList("item").add(lineItem1);
        assertEquals(2, itemsObject.getSequence().size());

        DataObject firstObjectAgain = (DataObject)itemsObject.getSequence().getValue(0);
        assertEquals("Baby Monitor", firstObjectAgain.get("productName"));

        Sequence oldSeq = cs.getOldSequence(itemsObject);
        assertNotNull(oldSeq);
        assertEquals(2, oldSeq.size());

        DataObject firstObjectOld = (DataObject)oldSeq.getValue(0);
        assertEquals("Lawnmower", firstObjectOld.get("productName"));

    }

    public void testGetOldSequenceReattached() {
        cs.beginLogging();
        address1Object.detach();
        purchaseOrderObject.getList("address").add(address1Object);
        Sequence oldSeq = cs.getOldSequence(address1Object);
        assertNotNull(oldSeq);
    }

    public void testGetOldSequenceAfterUnsetAllOnAddress() {
        cs.beginLogging();

        address1Object.unset(3);
        address1Object.unset(2);
        address1Object.unset(1);
        address1Object.unset(0);

        assertEquals(0, ((SDOSequence)address1Object.getSequence()).getSettings().size());
        boolean exception = false;
        try {
            address1Object.getSequence().getProperty(0);
        } catch (SDOException e) {
            //do nothing
            assertEquals(SDOException.INVALID_INDEX ,e.getErrorCode());
            exception = true;
        } finally {
            assertTrue(exception);
            exception = false;
        }
        try {
            address1Object.getSequence().getValue(0);
        } catch (SDOException e) {
            //do nothing
            assertEquals(SDOException.INVALID_INDEX ,e.getErrorCode());
            exception = true;
        } finally {
            assertTrue(exception);
            exception = false;
        }

        Property cityProp = address1Object.getInstanceProperty("city");
        ChangeSummary.Setting cityOldSetting = cs.getOldValue(address1Object, cityProp);
        assertNotNull(cityOldSetting);
        Object oldCityValue = cityOldSetting.getValue();
        assertNotNull(oldCityValue);

        Sequence address1OldSeq = cs.getOldSequence(address1Object);
        assertNotNull(address1OldSeq);

        assertEquals(4, address1OldSeq.size());
    }

    public void testGetOldSequenceAfterUnsetAllOnItems() {
        cs.beginLogging();

        Sequence seq1 = itemsObject.getSequence();

        itemsObject.unset(0);//unset item property

        Sequence seq2 = itemsObject.getSequence();
        assertEquals(seq1, seq2);
        Property itemProp = itemsObject.getInstanceProperty("item");
        ChangeSummary.Setting itemOldSetting = cs.getOldValue(itemsObject, itemProp);
        assertNotNull(itemOldSetting);
        Object oldItemValue = itemOldSetting.getValue();
        assertNotNull(oldItemValue);
        assertTrue(oldItemValue instanceof List);
        assertEquals(2, ((List)oldItemValue).size());

        Sequence itemsOldSeq = cs.getOldSequence(itemsObject);
        assertNotNull(itemsOldSeq);
        assertEquals(itemProp, itemsOldSeq.getProperty(0));
        Object itemOldSeqValue = itemsOldSeq.getValue(0);
        assertNotNull(itemOldSeqValue);
        assertTrue(itemOldSeqValue instanceof DataObject);
        // assertEquals(2, ((DataObject)itemOldSeqValue).size());
        assertEquals(((List)oldItemValue).get(0), itemOldSeqValue);

        /*  assertEquals(((List)oldItemValue).get(0), ((DataObject)itemOldSeqValue).get(0));
          assertEquals(((List)oldItemValue).get(1), ((DataObject)itemOldSeqValue).get(1));
          assertEquals(((List)oldItemValue), ((List)itemOldSeqValue));
        */
        Sequence itemsOldSeq2 = cs.getOldSequence(itemsObject);
        assertEquals(itemsOldSeq, itemsOldSeq2);

    }

    public void testGetOldSequenceWithText() {
        assertEquals(4, address1Object.getSequence().size());
        address1Object.getSequence().addText("abc");
        assertEquals(5, address1Object.getSequence().size());
        cs.beginLogging();

        address1Object.getSequence().addText("def");

        assertEquals(6, address1Object.getSequence().size());
        assertEquals("def", (String)address1Object.getSequence().getValue(5));

        Sequence oldSeq = cs.getOldSequence(address1Object);
        assertNotNull(oldSeq);
        assertEquals(5, oldSeq.size());
        assertEquals("abc", (String)oldSeq.getValue(4));
    }

    public void testGetOldSequenceAfterRemoveText() {
        assertEquals(4, address1Object.getSequence().size());
        address1Object.getSequence().addText("abc");
        address1Object.getSequence().addText("def");
        assertEquals(6, address1Object.getSequence().size());
        cs.beginLogging();
        address1Object.getSequence().remove(4);
        assertEquals(5, address1Object.getSequence().size());
        assertEquals("def", (String)address1Object.getSequence().getValue(4));

        Sequence oldSeq = cs.getOldSequence(address1Object);
        assertNotNull(oldSeq);
        assertEquals(6, oldSeq.size());
        assertEquals("abc", (String)oldSeq.getValue(4));
        assertEquals("def", (String)oldSeq.getValue(5));
    }

    public void testGetOldSequenceAfterMoveText() {
        assertEquals(4, address1Object.getSequence().size());
        address1Object.getSequence().addText("abc");
        address1Object.getSequence().addText("def");
        assertEquals(6, address1Object.getSequence().size());
        cs.beginLogging();
        assertEquals("abc", (String)address1Object.getSequence().getValue(4));
        assertEquals("def", (String)address1Object.getSequence().getValue(5));
        address1Object.getSequence().move(4, 5);
        assertEquals(6, address1Object.getSequence().size());
        assertEquals("def", (String)address1Object.getSequence().getValue(4));
        assertEquals("abc", (String)address1Object.getSequence().getValue(5));

        Sequence oldSeq = cs.getOldSequence(address1Object);
        assertNotNull(oldSeq);
        assertEquals(6, oldSeq.size());
        assertEquals("abc", (String)oldSeq.getValue(4));
        assertEquals("def", (String)oldSeq.getValue(5));
    }

    private DataObject buildDataObjects() {
        DataObject usAddress = dataFactory.create("http://www.example.org", "usAddressType");
        usAddress.set("name", "Alice Smith");
        List usStreets = new ArrayList();
        usStreets.add("123 Maple Street");
        usAddress.set("street", usStreets);
        usAddress.set("city", "Mill Valley");
        usAddress.set("state", "CA");

        DataObject cdnAddress = dataFactory.create("http://www.example.org", "cdnAddressMailingType");
        cdnAddress.set("name", "Robert Smith");
        List cdnStreets = new ArrayList();
        cdnStreets.add("8 Oak Avenue");

        cdnAddress.set("street", cdnStreets);
        cdnAddress.set("city", "Mill Valley");
        cdnAddress.set("province", "Ontario");
        cdnAddress.set("postalcode", "B3L 4J3");
        cdnAddress.set("deliveryInfo", "usual");

        DataObject items = dataFactory.create("http://www.example.org", "Items");

        DataObject lineItem1 = dataFactory.create("http://www.example.org", "LineItemType");
        lineItem1.set("productName", "Lawnmower");
        lineItem1.set("price", "148.95");
        lineItem1.set("comment", "Confirm this is electric");

        DataObject lineItem2 = dataFactory.create("http://www.example.org", "LineItemType");
        lineItem2.set("productName", "Baby Monitor");
        lineItem2.set("price", "39.98");

        List theItems = new ArrayList();
        theItems.add(lineItem1);
        theItems.add(lineItem2);
        items.set("item", theItems);

        DataObject po = dataFactory.create("http://www.example.org", "PurchaseOrderType");
        List addresses = new ArrayList();
        addresses.add(usAddress);
        addresses.add(cdnAddress);
        po.set("address", addresses);
        po.set("comment", "Hurry, my lawn is going wild");
        po.set("items", items);

        companyObject = dataFactory.create("http://www.example.org", "CompanyType");
        companyObject.set("order", po);

        return companyObject;
    }

    public void testGetOldSequenceSameSettings() {
        org.eclipse.persistence.oxm.sequenced.Setting firstSetting = ((SDOSequence)address1Object.getSequence()).getSettings().get(0);
        cs.beginLogging();
        //modify addressObject        
        address1Object.set("city", "aaa");
        org.eclipse.persistence.oxm.sequenced.Setting secondSetting = ((SDOSequence)address1Object.getSequence()).getSettings().get(0);
        assertTrue(firstSetting == secondSetting);
    }
}
