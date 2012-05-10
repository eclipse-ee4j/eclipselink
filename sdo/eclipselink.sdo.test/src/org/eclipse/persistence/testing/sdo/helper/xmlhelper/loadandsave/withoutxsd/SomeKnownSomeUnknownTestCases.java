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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.withoutxsd;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.oxm.sequenced.Setting;

public class SomeKnownSomeUnknownTestCases extends LoadAndSaveUnknownTestCases {
    public SomeKnownSomeUnknownTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.withoutxsd.SomeKnownSomeUnknownTestCases" };
        TestRunner.main(arguments);
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/withoutxsd/purchaseOrder.xml");
    }

    protected String getControlWriteFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/withoutxsd/purchaseOrder_write.xml");
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/withoutxsd/PurchaseOrder.xsd";
    }
  
    protected void verifyAfterLoad(XMLDocument document) {
        assertNull(document.getRootObject().getContainer());
        DataObject po = document.getRootObject();
        assertNotNull(po);
        DataObject shipTo = po.getDataObject("shipTo");
        assertNotNull(shipTo);
        assertNotNull(shipTo.get("street"));

        assertEquals("123 Maple Street", shipTo.get("street"));
        assertEquals(po, shipTo.getContainer());
        DataObject billTo = po.getDataObject("billTo");

        Property extraProp = billTo.getInstanceProperty("testExtra");
        assertNotNull(extraProp);

        List extraContentList = billTo.getList(extraProp);
        assertEquals(1, extraContentList.size());
        assertEquals("extraContext", extraContentList.get(0));

        Setting setting = ((SDODataObject)billTo).getSettings().get(4);
        assertNotNull(setting);
        assertNotNull(billTo);
        assertEquals(po, billTo.getContainer());
        DataObject items = po.getDataObject("items");
        assertNotNull(items);
        assertEquals(po, items.getContainer());
        DataObject item1 = (DataObject)items.getList("item").get(0);
        assertNotNull(item1);
        assertEquals(items, item1.getContainer());
        DataObject item2 = (DataObject)items.getList("item").get(1);
        assertNotNull(item2);
        assertEquals(items, item2.getContainer());
        //unmapped content

        List phones = po.getList("phone");
        assertEquals(2, phones.size());
        DataObject phone = (DataObject)phones.get(0);
        assertNotNull(phone);
        assertEquals(po, phone.getContainer());
        List addrs = phone.getList("addr");
        assertEquals(1, addrs.size());
        DataObject addr = (DataObject)addrs.get(0);
        assertNotNull(addr);
        assertEquals(phone, addr.getContainer());

		List itemList = addr.getList("item");
        assertEquals(1, itemList.size());
        DataObject item = (DataObject)itemList.get(0);
        assertNotNull(item);
        assertEquals(addr, item.getContainer());
        
		List dwellings = addr.getList("dwelling");
        assertEquals(1, dwellings.size());
        DataObject dwelling = (DataObject)dwellings.get(0);
        assertNotNull(dwelling);
        assertEquals(addr, dwelling.getContainer());

        DataObject phone2 = (DataObject)phones.get(1);
        assertNotNull(phone2);
        List numList = phone2.getList("number");
        assertEquals(1, numList.size());
        assertEquals("12345678", numList.get(0));
        List extList = phone2.getList("ext");

        assertEquals(2, extList.size());
        assertEquals("234", extList.get(0));
        assertEquals("456", extList.get(1));

        List companyNames = po.getList("companyName");
        assertNotNull(companyNames);

        String companyName = (String)companyNames.get(0);

        Property attrProp = xsdHelper.getGlobalProperty("http://www.example.org", "globalTest", true);
        assertNotNull(attrProp);
        Property elemProp = xsdHelper.getGlobalProperty("http://www.example.org", "globalTest", false);
        assertNotNull(elemProp);

        String attrValue = phone.getString(attrProp);

        // assertEquals("globalAttributeTest", attrValue);
        String elemValue = phone.getString(elemProp);

        //assertEquals("globalElementTest", elemValue);
    }
}
