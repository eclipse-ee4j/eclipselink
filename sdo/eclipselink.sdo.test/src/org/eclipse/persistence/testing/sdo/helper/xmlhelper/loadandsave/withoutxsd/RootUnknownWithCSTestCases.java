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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.withoutxsd;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOChangeSummary;

public class RootUnknownWithCSTestCases extends LoadAndSaveUnknownTestCases {
    public RootUnknownWithCSTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.withoutxsd.RootUnknownWithCSTestCases" };
        TestRunner.main(arguments);
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/withoutxsd/purchaseOrderWrappedInUnknownCS.xml");
    }
    
    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/withoutxsd/PurchaseOrderCS.xsd";
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
        //DataObject phone = po.getDataObject("phone");
        List phones = po.getList("phone");
        DataObject phone = (DataObject)phones.get(0);
        assertNotNull(phone);
        assertEquals(po, phone.getContainer());
        //DataObject addr = phone.getDataObject("addr");
        List addrs = phone.getList("addr");
        DataObject addr = (DataObject)addrs.get(0);
        assertNotNull(addr);
        assertEquals(phone, addr.getContainer());
        //DataObject item = addr.getDataObject("item");
        //assertNotNull(item);
        List itemList = addr.getList("item");
        assertNotNull(itemList);
        DataObject item = (DataObject)itemList.get(0);

        assertNotNull(item);
        assertEquals(addr, item.getContainer());
        //DataObject dwelling = addr.getDataObject("dwelling");
        List dwellings = addr.getList("dwelling");
        DataObject dwelling = (DataObject)dwellings.get(0);
        assertNotNull(dwelling);
        assertEquals(addr, dwelling.getContainer());

        //Changesummary
        ChangeSummary cs = po.getChangeSummary();
        assertNotNull(cs);
        assertNotNull(phone);
        String numvalue = phone.getString("number");
        assertEquals("12345678", numvalue);
        String extvalue = phone.getString("ext");
        assertEquals("123", extvalue);

        List oldSettingsPhone = cs.getOldValues(phone);

        ChangeSummary.Setting oldCommentSetting = cs.getOldValue(po, po.getInstanceProperty("comment"));
        assertEquals("oldComment", oldCommentSetting.getValue());
        assertTrue(oldCommentSetting.isSet());

        ChangeSummary.Setting oldNumberSetting = cs.getOldValue(phone, phone.getInstanceProperty("number"));
        List oldNumberValue = (List)oldNumberSetting.getValue();
        assertEquals(1, oldNumberValue.size());
        assertEquals("33333333", oldNumberValue.get(0));

        ChangeSummary.Setting oldExtSetting = cs.getOldValue(phone, phone.getInstanceProperty("ext"));
        List oldExtValue = (List)oldExtSetting.getValue();
        assertEquals(1, oldExtValue.size());
        assertEquals("444", oldExtValue.get(0));

        //assertEquals(0, ((SDOChangeSummary)cs).getCreated().size());
        // assertEquals(1, ((SDOChangeSummary)cs).getModified().size());
        //assertEquals(1, ((SDOChangeSummary)cs).getDeleted().size());
        assertFalse(po.isSet("blah"));
        assertFalse(po.isSet("foo"));

        List oldSettingsPO = cs.getOldValues(po);
        assertEquals(3, oldSettingsPO.size());

        ChangeSummary.Setting blahSetting = (ChangeSummary.Setting)oldSettingsPO.get(1);
        assertNotNull(blahSetting);
        assertNotNull(blahSetting.getValue());
        assertNotNull(blahSetting.getProperty());
        assertTrue(blahSetting.isSet());

        ChangeSummary.Setting fooSetting = (ChangeSummary.Setting)oldSettingsPO.get(2);
        assertNotNull(fooSetting);

        List fooList = (List)fooSetting.getValue();
        assertNotNull(fooList);
        assertNotNull(fooSetting.getProperty());
        assertEquals(1, fooList.size());
        DataObject fooDO = (DataObject)fooList.get(0);
        assertNotNull(fooDO);
        assertTrue(fooSetting.isSet());

        DataObject deletedFoo = (DataObject)((SDOChangeSummary)cs).getReverseDeletedMap().get(fooDO);
        List oldSettingsFoo = cs.getOldValues(deletedFoo);
        assertEquals(1, oldSettingsFoo.size());

        ChangeSummary.Setting nameSetting = (ChangeSummary.Setting)oldSettingsFoo.get(0);
        assertNotNull(nameSetting);
        assertEquals("name", nameSetting.getProperty().getName());

        List nameList = (List)nameSetting.getValue();
        assertNotNull(nameList);
        assertEquals(1, nameList.size());
        assertEquals("someName", nameList.get(0));
        assertTrue(nameSetting.isSet());

        /*List oldSettingsPO = cs.getOldValues(po);
        assertEquals(1, oldSettingsPO.size());

        ChangeSummary.Setting blahSetting = (ChangeSummary.Setting)oldSettingsPO.get(0);
        assertNotNull(blahSetting);
        assertNotNull(blahSetting.getValue());
        assertNotNull(blahSetting.getProperty());
        assertTrue(blahSetting.isSet());

        //DataObject blahDO  = (DataObject)blahSetting.getValue();
        List blahDO = (List)blahSetting.getValue();

        //List oldSettingsBlah = cs.getOldValues(blahDO);
        //assertEquals(1,oldSettingsBlah.size());
        DataObject deletedBlah = (DataObject)((SDOChangeSummary)cs).getDeleted().get(0);
        assertNotNull(deletedBlah);
        List oldSettingsBlah = cs.getOldValues(deletedBlah);
        assertEquals(1, oldSettingsBlah.size());
        //String theName =(String) deletedBlah.get("name");
        List theNames = deletedBlah.getList("name");
        String theName = (String)theNames.get(0);
        assertEquals("someName", theName);
        */
        /*
        assertEquals(2, ((SDOChangeSummary)cs).getModified().size());
        assertEquals(1, ((SDOChangeSummary)cs).getDeleted().size());*/

        //  assertFalse(po.getChangeSummary().isLogging());
    }
}
