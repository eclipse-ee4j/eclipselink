/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.withoutxsd;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;
import java.util.List;
import junit.textui.TestRunner;

public class RootUnknownTestCases extends LoadAndSaveUnknownTestCases {
    public RootUnknownTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.withoutxsd.RootUnknownTestCases" };
        TestRunner.main(arguments);
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/withoutxsd/purchaseOrderWrappedInUnknown.xml");
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/withoutxsd/PurchaseOrder.xsd";
    }
    protected void verifyAfterLoad(XMLDocument document) {
        DataObject root = document.getRootObject();
        assertNull(root.getContainer());
        DataObject po = root.getDataObject("purchaseOrder");
        assertNotNull(po);
        DataObject shipTo = po.getDataObject("shipTo");
        assertNotNull(shipTo);
        assertEquals(po, shipTo.getContainer());
        DataObject billTo = po.getDataObject("billTo");
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
        DataObject phone = (DataObject)phones.get(0);
        assertNotNull(phone);
        assertEquals(po, phone.getContainer());

        List addrs = phone.getList("addr");
        DataObject addr = (DataObject)addrs.get(0);

        assertNotNull(addr);
        assertEquals(phone, addr.getContainer());
        List itemList = addr.getList("item");

        assertNotNull(itemList);
        DataObject item = (DataObject)itemList.get(0);
        assertNotNull(item);
        assertEquals(addr, item.getContainer());

        List dwellings = addr.getList("dwelling");
        DataObject dwelling = (DataObject)dwellings.get(0);
        assertNotNull(dwelling);
        assertEquals(addr, dwelling.getContainer());
    }
}
