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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;

public class DefineWithBuiltInSchemaLocationTestCases extends XSDHelperDefineTestCases {
    public DefineWithBuiltInSchemaLocationTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(DefineWithBuiltInSchemaLocationTestCases.class);
    }

    public String getSchemaToDefine() {
        return "org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderWithBuiltInSchemaLocations.xsd";
    }

    protected String getSchemaLocation() {
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/";
    }
    
    public List<Type> getControlTypes() {
        SDOType intType = (SDOType) typeHelper.getType("commonj.sdo", "Int");
        SDOType stringType = (SDOType) typeHelper.getType("commonj.sdo", "String");
        SDOType decimalType = (SDOType) typeHelper.getType("commonj.sdo", "Decimal");

        // create a new Type for USAddress
        DataObject USaddrDO = dataFactory.create("commonj.sdo", "Type");
        USaddrDO.set("uri", "my.uri");
        USaddrDO.set("name", "USAddress");
        DataObject streetProperty = USaddrDO.createDataObject("property");
        streetProperty.set("name", "street");
        DataObject cityProperty = USaddrDO.createDataObject("property");
        cityProperty.set("name", "city");
        DataObject stateProperty = USaddrDO.createDataObject("property");
        stateProperty.set("name", "state");
        DataObject zipProperty = USaddrDO.createDataObject("property");
        zipProperty.set("name", "zip");
        zipProperty.set("type", decimalType);
        DataObject countryProperty = USaddrDO.createDataObject("property");
        countryProperty.set("name", "country");
        
        SDOType usAddrType = (SDOType) typeHelper.define(USaddrDO);
        usAddrType.setInstanceClassName("com.example.myPackage.USAddress");
        
        // create a new Type for Item
        DataObject ItemDO = dataFactory.create("commonj.sdo", "Type");
        ItemDO.set("uri", "my.uri");
        ItemDO.set("name", "ItemSDO");
        DataObject prodProperty = ItemDO.createDataObject("property");
        prodProperty.set("name", "productName");
        DataObject usPriceProperty = ItemDO.createDataObject("property");
        usPriceProperty.set("name", "USPrice");
        usPriceProperty.set("type", decimalType);
        DataObject commentProperty = ItemDO.createDataObject("property");
        commentProperty.set("name", "comment");
        commentProperty.set("type", decimalType);
        List aliasNames = new ArrayList();
        aliasNames.add("itemComment");
        commentProperty.set("AliasNames", aliasNames);
        DataObject shipDateProperty = ItemDO.createDataObject("property");
        shipDateProperty.set("name", "shipDate");
        
        SDOType itemType = (SDOType) typeHelper.define(ItemDO);
        itemType.setInstanceClassName("com.example.myPackage.ItemSDO");

        // create a new Type for Items
        DataObject ItemsDO = dataFactory.create("commonj.sdo", "Type");
        ItemsDO.set("uri", "my.uri");
        ItemsDO.set("name", "ItemSDO");
        DataObject itemProperty = ItemsDO.createDataObject("property");
        itemProperty.set("name", "item");
        itemProperty.set("type", itemType);

        SDOType itemsType = (SDOType) typeHelper.define(ItemsDO);
        itemsType.setInstanceClassName("com.example.myPackage.Items");
        
        // create a new Type for PurchaseOrder
        DataObject PurchaseOrderDO = dataFactory.create("commonj.sdo", "Type");
        PurchaseOrderDO.set("uri", "my.uri");
        PurchaseOrderDO.set("name", "PurchaseOrder");
        DataObject shipToProperty = PurchaseOrderDO.createDataObject("property");
        shipToProperty.set("name", "shipTo");
        shipToProperty.set("type", usAddrType);
        DataObject billToProperty = PurchaseOrderDO.createDataObject("property");
        billToProperty.set("name", "billTo");
        billToProperty.set("type", usAddrType);
        DataObject itemsProperty = PurchaseOrderDO.createDataObject("property");
        itemsProperty.set("name", "items");
        itemsProperty.set("type", itemsType);
        DataObject partNumberProperty = PurchaseOrderDO.createDataObject("property");
        partNumberProperty.set("name", "comment");
        DataObject orderDateProperty = PurchaseOrderDO.createDataObject("property");
        orderDateProperty.set("name", "orderDate");

        SDOType purchaseOrderType = (SDOType) typeHelper.define(PurchaseOrderDO);
        purchaseOrderType.setInstanceClassName("com.example.myPackage.PurchaseOrder");

        // add the types to a List to return
        List<Type> types = new ArrayList<Type>();
        types.add(itemType);
        types.add(purchaseOrderType);
        types.add(itemsType);
        types.add(usAddrType);
        return types;
    }
}
