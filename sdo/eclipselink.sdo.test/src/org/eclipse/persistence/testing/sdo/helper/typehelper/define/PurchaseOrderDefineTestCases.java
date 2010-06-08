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
package org.eclipse.persistence.testing.sdo.helper.typehelper.define;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class PurchaseOrderDefineTestCases extends SDOTestCase {
    public PurchaseOrderDefineTestCases(String name) {
        super(name);
    }

    public void testDefineTypes() throws Exception {
        List dataObjects = new ArrayList();
        dataObjects.add(getDataObject());
        List definedTypes = typeHelper.define(dataObjects);
        assertNotNull(definedTypes);
        assertEquals(1, definedTypes.size());
        verify((Type)definedTypes.get(0));
    }

    public void testDefineType() throws Exception {
        Type definedType = typeHelper.define(getDataObject());
        verify(definedType);

    }

    protected DataObject getDataObject() {
        Type intType = typeHelper.getType("commonj.sdo", "Int");
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type dateType = typeHelper.getType("commonj.sdo", "Date");
        Type addressType = registerAddressType();
        Type itemsType = registerItemsType();

        // create a new Type for Customers
        DataObject purchaseOrderTypeType = dataFactory.create("commonj.sdo", "Type");

        SDOProperty prop = (SDOProperty)purchaseOrderTypeType.getType().getProperty("uri");
        purchaseOrderTypeType.set(prop, "http://example.com/");

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

        return purchaseOrderTypeType;
    }

    private Type registerAddressType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");

        // create a new Type for addressType
        DataObject addressType = dataFactory.create("commonj.sdo", "Type");

        SDOProperty prop = (SDOProperty)addressType.getType().getProperty("uri");
        addressType.set(prop, "http://example.com/");

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
        itemType.set(prop, "http://example.com/");

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
        itemsType.set(prop, "http://example.com/");

        prop = (SDOProperty)itemsType.getType().getProperty("name");
        itemsType.set(prop, "Items");

        DataObject itemProp = addProperty(itemsType, "item", itemType);
        itemProp.setBoolean("containment", true);
        itemProp.setBoolean("many", true);

        return typeHelper.define(itemsType);

    }

    private void verify(Type definedType) {
        assertEquals(definedType.getName(), "PurchaseOrderType");

        assertEquals(definedType.getURI(), "http://example.com/");

        assertEquals(5, definedType.getDeclaredProperties().size());
        Type dateType = typeHelper.getType("commonj.sdo", "Date");
        Type stringType = typeHelper.getType("commonj.sdo", "String");

        Property prop = definedType.getProperty("orderDate");
        assertNotNull(prop);
        assertEquals(dateType, prop.getType());

        prop = definedType.getProperty("shipTo");
        assertNotNull(prop);
        Type addressType = typeHelper.getType("http://example.com/", "AddressType");
        assertEquals(addressType, prop.getType());

        prop = definedType.getProperty("billTo");
        assertNotNull(prop);
        assertEquals(addressType, prop.getType());
    }
}
