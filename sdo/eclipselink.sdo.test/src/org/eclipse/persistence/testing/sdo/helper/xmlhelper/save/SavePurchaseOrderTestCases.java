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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.save;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import org.eclipse.persistence.sdo.SDOProperty;

public class SavePurchaseOrderTestCases extends SDOXMLHelperSaveTestCases {
    public SavePurchaseOrderTestCases(String name) {
        super(name);
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/purchaseOrderNS.xml");
    }

    protected String getNoSchemaControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/purchaseOrderNSNoSchema.xml");
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrder.xsd";
    }

    protected String getControlRootName() {
        return "purchaseOrder";
    }

    protected XMLDocument getXMLDocumentToSave() {
        DataObject purchaseOrder = dataFactory.create(getControlRootURI(), "PurchaseOrderType");

        purchaseOrder.set(purchaseOrder.getInstanceProperty("orderDate"), "1999-10-20");

        DataObject shipTo = purchaseOrder.createDataObject("shipTo");
        shipTo.set(shipTo.getInstanceProperty("country"), "US");
        shipTo.set(shipTo.getInstanceProperty("name"), "Alice Smith");
        shipTo.set(shipTo.getInstanceProperty("street"), "123 Maple Street");
        shipTo.set(shipTo.getInstanceProperty("city"), "Mill Valley");
        shipTo.set(shipTo.getInstanceProperty("state"), "CA");
        shipTo.set(shipTo.getInstanceProperty("zip"), "90952");

        DataObject billTo = purchaseOrder.createDataObject("billTo");
        billTo.set(billTo.getInstanceProperty("country"), "US");
        billTo.set(billTo.getInstanceProperty("name"), "Robert Smith");
        billTo.set(billTo.getInstanceProperty("street"), "8 Oak Avenue");
        billTo.set(billTo.getInstanceProperty("city"), "Mill Valley");
        billTo.set(billTo.getInstanceProperty("state"), "PA");
        billTo.set(billTo.getInstanceProperty("zip"), "95819");

        purchaseOrder.set(purchaseOrder.getInstanceProperty("comment"), "Hurry, my lawn is going wild!");

        DataObject items = purchaseOrder.createDataObject("items");
        DataObject item1 = items.createDataObject("item");

        item1.set(item1.getInstanceProperty("partNum"), "872-AA");
        item1.set(item1.getInstanceProperty("productName"), "Lawnmower");
        item1.set(item1.getInstanceProperty("quantity"), "1");
        item1.set(item1.getInstanceProperty("USPrice"), "148.95");
        item1.set(item1.getInstanceProperty("comment"), "Confirm this is electric");

        /*
        item1.set("partNum", "872-AA");
        item1.set("productName", "Lawnmower");
        item1.setInt("quantity", 1);
        item1.setString("USPrice", "148.95");
        item1.set("comment", "Confirm this is electric");
        */
        DataObject item2 = items.createDataObject("item");

        /*item2.set("partNum", "926-AA");
        item2.set("productName", "Baby Monitor");
        item1.setInt("quantity", 1);
        item2.setString("USPrice", "39.98");
                */
        item2.set(item2.getInstanceProperty("partNum"), "926-AA");
        item2.set(item2.getInstanceProperty("productName"), "Baby Monitor");
        item2.set(item2.getInstanceProperty("quantity"), "1");
        item2.set(item2.getInstanceProperty("USPrice"), "39.98");
        //item2.setString(item2.getProperty("shipDate"), "1999-05-21");
        item2.set(item2.getInstanceProperty("shipDate"), "1999-05-21");

        XMLDocument doc = xmlHelper.createDocument(purchaseOrder, getControlRootURI(), getControlRootName());
        return doc;
    }

    protected void registerTypes() {
        Type intType = typeHelper.getType("commonj.sdo", "Int");
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type dateType = typeHelper.getType("commonj.sdo", "Date");
        Type addressType = registerAddressType();
        Type itemsType = registerItemsType();

        // create a new Type for Customers        
        DataObject purchaseOrderTypeType = dataFactory.create("commonj.sdo", "Type");

        SDOProperty prop = (SDOProperty)purchaseOrderTypeType.getType().getProperty("uri");
        purchaseOrderTypeType.set(prop, CONTROL_ROOT_URI);

        prop = (SDOProperty)purchaseOrderTypeType.getType().getProperty("name");
        purchaseOrderTypeType.set(prop, "PurchaseOrderType");

        // create a orderDateProperty 
        addProperty(purchaseOrderTypeType, "orderDate", dateType);

        DataObject shipToProp = addProperty(purchaseOrderTypeType, "shipTo", addressType);
        shipToProp.setBoolean("containment", true);

        DataObject billToProp = addProperty(purchaseOrderTypeType, "billTo", addressType);
        billToProp.setBoolean("containment", true);

        addProperty(purchaseOrderTypeType, "comment", stringType);

        DataObject itemsProp = addProperty(purchaseOrderTypeType, "items", itemsType);
        itemsProp.setBoolean("containment", true);

        typeHelper.define(purchaseOrderTypeType);
    }

    private Type registerAddressType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");

        // create a new Type for addressType        
        DataObject addressType = dataFactory.create("commonj.sdo", "Type");

        SDOProperty prop = (SDOProperty)addressType.getType().getProperty("uri");
        addressType.set(prop, CONTROL_ROOT_URI);

        prop = (SDOProperty)addressType.getType().getProperty("name");
        addressType.set(prop, "AddressType");

        addProperty(addressType, "country", stringType);
        addProperty(addressType, "name", stringType);
        addProperty(addressType, "street", stringType);
        addProperty(addressType, "city", stringType);
        addProperty(addressType, "state", stringType);
        addProperty(addressType, "zip", stringType);

        return typeHelper.define(addressType);
    }

    private Type registerItemType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type intType = typeHelper.getType("commonj.sdo", "Int");
        Type floatType = typeHelper.getType("commonj.sdo", "Float");
        Type dateType = typeHelper.getType("commonj.sdo", "Date");

        // create a new Type for addressType        
        DataObject itemType = dataFactory.create("commonj.sdo", "Type");

        SDOProperty prop = (SDOProperty)itemType.getType().getProperty("uri");
        itemType.set(prop, CONTROL_ROOT_URI);

        prop = (SDOProperty)itemType.getType().getProperty("name");
        itemType.set(prop, "Item");

        addProperty(itemType, "partNum", stringType);
        addProperty(itemType, "productName", stringType);
        addProperty(itemType, "quantity", intType);
        addProperty(itemType, "USPrice", floatType);
        addProperty(itemType, "comment", stringType);
        addProperty(itemType, "shipDate", dateType);

        return typeHelper.define(itemType);

    }

    private Type registerItemsType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type itemType = registerItemType();

        // create a new Type for itemsType        
        DataObject itemsType = dataFactory.create("commonj.sdo", "Type");

        SDOProperty prop = (SDOProperty)itemsType.getType().getProperty("uri");
        itemsType.set(prop, CONTROL_ROOT_URI);

        prop = (SDOProperty)itemsType.getType().getProperty("name");
        itemsType.set(prop, "Items");

        DataObject itemProp = addProperty(itemsType, "item", itemType);
        itemProp.setBoolean("containment", true);
        itemProp.setBoolean("many", true);

        return typeHelper.define(itemsType);

    }
}
