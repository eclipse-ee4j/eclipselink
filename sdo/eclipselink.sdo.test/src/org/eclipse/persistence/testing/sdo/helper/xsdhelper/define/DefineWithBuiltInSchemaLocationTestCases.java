/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

    public List getControlTypes() {
        List types = new ArrayList();

        String uri = "my.uri";
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type intType = typeHelper.getType("commonj.sdo", "Int");

        Type decimalType = typeHelper.getType("commonj.sdo", "Decimal");

        /****ADDRESS TYPE*****/

        //ADDRESS TYPE
        SDOType USaddrType = new SDOType(uri, "USAddress");
        USaddrType.setXsd(true);
        USaddrType.setXsdLocalName("USAddress");
        USaddrType.setDataType(false);
        USaddrType.setInstanceClassName("com.example.myPackage.USAddress");

        SDOProperty addrNameProp = new SDOProperty(aHelperContext);
        addrNameProp.setName("name");
        addrNameProp.setXsd(true);
        addrNameProp.setXsdLocalName("name");
        addrNameProp.setType(stringType);

        USaddrType.addDeclaredProperty(addrNameProp);

        SDOProperty streetProp = new SDOProperty(aHelperContext);
        streetProp.setName("street");
        streetProp.setXsd(true);
        streetProp.setXsdLocalName("street");
        streetProp.setType(stringType);
        USaddrType.addDeclaredProperty(streetProp);

        SDOProperty cityProp = new SDOProperty(aHelperContext);
        cityProp.setName("city");
        cityProp.setXsd(true);
        cityProp.setXsdLocalName("city");
        cityProp.setType(stringType);
        USaddrType.addDeclaredProperty(cityProp);

        SDOProperty stateProp = new SDOProperty(aHelperContext);
        stateProp.setName("state");
        stateProp.setXsd(true);
        stateProp.setXsdLocalName("state");
        stateProp.setType(stringType);
        USaddrType.addDeclaredProperty(stateProp);

        SDOProperty zipProp = new SDOProperty(aHelperContext);
        zipProp.setName("zip");
        zipProp.setXsd(true);
        zipProp.setXsdLocalName("zip");
        zipProp.setType(decimalType);
        USaddrType.addDeclaredProperty(zipProp);

        SDOProperty countryProp = new SDOProperty(aHelperContext);
        countryProp.setName("country");
        countryProp.setXsd(true);
        countryProp.setXsdLocalName("country");
        countryProp.setType(stringType);
        countryProp.setDefault("US");
        USaddrType.addDeclaredProperty(countryProp);

        /****ITEM TYPE*****/
        SDOType itemType = new SDOType(uri, "ItemSDO");
        itemType.setXsd(true);
        itemType.setXsdLocalName("ItemSDO");
        itemType.setInstanceClassName("com.example.myPackage.ItemSDO");
        itemType.setDataType(false);

        SDOProperty productNameProp = new SDOProperty(aHelperContext);
        productNameProp.setName("productName");
        productNameProp.setXsd(true);
        productNameProp.setXsdLocalName("productName");
        productNameProp.setType(stringType);
        productNameProp.setReadOnly(true);
        itemType.addDeclaredProperty(productNameProp);

        SDOProperty USPriceProp = new SDOProperty(aHelperContext);
        USPriceProp.setName("USPrice");
        USPriceProp.setXsd(true);
        USPriceProp.setXsdLocalName("USPrice");
        USPriceProp.setType(decimalType);
        itemType.addDeclaredProperty(USPriceProp);

        SDOProperty itemCommentProp = new SDOProperty(aHelperContext);
        itemCommentProp.setName("comment");
        itemCommentProp.setXsd(true);
        itemCommentProp.setXsdLocalName("comment");
        itemCommentProp.setType(stringType);
        itemCommentProp.setContainment(false);
        List aliasNames = new ArrayList();
        aliasNames.add("itemComment");
        itemCommentProp.setAliasNames(aliasNames);
        itemType.addDeclaredProperty(itemCommentProp);

        SDOProperty shipDateProp = new SDOProperty(aHelperContext);
        shipDateProp.setName("shipDate");
        shipDateProp.setXsd(true);
        shipDateProp.setXsdLocalName("shipDate");
        shipDateProp.setType(stringType);
        shipDateProp.setContainment(false);
        itemType.addDeclaredProperty(shipDateProp);

        /****ITEMS TYPE*****/
        SDOType itemsType = new SDOType(uri, "Items");
        itemsType.setXsd(true);
        itemsType.setXsdLocalName("Items");
        itemsType.setDataType(false);
        itemsType.setInstanceClassName("com.example.myPackage.Items");

        SDOProperty itemProp = new SDOProperty(aHelperContext);
        itemProp.setName("item");
        itemProp.setContainment(true);
        itemProp.setMany(true);
        itemProp.setXsd(true);
        itemProp.setXsdLocalName("item");
        itemProp.setType(itemType);
        itemsType.addDeclaredProperty(itemProp);

        /****PURCHASEORDER TYPE*****/
        SDOProperty shipToProp = new SDOProperty(aHelperContext);
        shipToProp.setName("shipTo");
        shipToProp.setXsd(true);
        shipToProp.setXsdLocalName("shipTo");
        shipToProp.setContainment(true);
        shipToProp.setMany(true);
        shipToProp.setType(USaddrType);

        SDOProperty billToProp = new SDOProperty(aHelperContext);
        billToProp.setName("billTo");
        billToProp.setXsd(true);
        billToProp.setXsdLocalName("billTo");
        billToProp.setContainment(true);
        billToProp.setReadOnly(true);
        billToProp.setMany(false);
        billToProp.setType(USaddrType);

        SDOProperty itemsProp = new SDOProperty(aHelperContext);
        itemsProp.setName("items");
        itemsProp.setXsd(true);
        itemsProp.setXsdLocalName("items");
        itemsProp.setMany(false);
        itemsProp.setContainment(true);
        itemsProp.setType(itemsType);

        SDOProperty commentProp = new SDOProperty(aHelperContext);
        commentProp.setName("comment");
        commentProp.setXsd(true);
        commentProp.setXsdLocalName("comment");
        commentProp.setType(stringType);
        commentProp.setContainment(false);

        SDOProperty orderDateProp = new SDOProperty(aHelperContext);
        orderDateProp.setName("orderDate");
        orderDateProp.setXsd(true);
        orderDateProp.setXsdLocalName("orderDate");
        orderDateProp.setType(stringType);
        orderDateProp.setContainment(false);

        SDOType POtype = new SDOType(uri, "PurchaseOrder");
        POtype.setXsd(true);
        POtype.setXsdLocalName("PurchaseOrder");
        POtype.setSequenced(true);
        POtype.setInstanceClassName("com.example.myPackage.PurchaseOrder");
        POtype.setDataType(false);
        POtype.addDeclaredProperty(shipToProp);
        POtype.addDeclaredProperty(billToProp);
        POtype.addDeclaredProperty(itemsProp);
        POtype.addDeclaredProperty(commentProp);
        POtype.addDeclaredProperty(orderDateProp);

        types.add(itemType);
        types.add(POtype);
        types.add(itemsType);
        types.add(USaddrType);

        return types;
    }
}