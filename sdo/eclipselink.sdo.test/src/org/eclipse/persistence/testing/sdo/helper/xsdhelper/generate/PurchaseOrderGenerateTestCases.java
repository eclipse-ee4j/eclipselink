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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate;

import commonj.sdo.Type;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;

public class PurchaseOrderGenerateTestCases extends XSDHelperGenerateTestCases {
    public PurchaseOrderGenerateTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate.PurchaseOrderGenerateTestCases" };
        TestRunner.main(arguments);
    }

    public String getControlFileName() {
        return "org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/PurchaseOrder.xsd";
    }

    public List getTypesToGenerateFrom() {
        List types = new ArrayList();
        String uri = null;
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type intType = typeHelper.getType("commonj.sdo", "Int");
        Type dateType = typeHelper.getType("commonj.sdo", "Date");
        Type yearMonthDayType = SDOConstants.SDO_YEARMONTHDAY;
        Type decimalType = typeHelper.getType("commonj.sdo", "Decimal");

        /****ADDRESS TYPE*****/

        //ADDRESS TYPE
        SDOType USaddrType = new SDOType(uri, "USAddress");
        USaddrType.setDataType(false);

        SDOProperty addrNameProp = new SDOProperty(aHelperContext);
        addrNameProp.setName("name");
        addrNameProp.setType(stringType);
        USaddrType.getDeclaredProperties().add(addrNameProp);

        SDOProperty streetProp = new SDOProperty(aHelperContext);
        streetProp.setName("street");
        streetProp.setType(stringType);
        USaddrType.getDeclaredProperties().add(streetProp);

        SDOProperty cityProp = new SDOProperty(aHelperContext);
        cityProp.setName("city");
        cityProp.setType(stringType);
        USaddrType.getDeclaredProperties().add(cityProp);

        SDOProperty stateProp = new SDOProperty(aHelperContext);
        stateProp.setName("state");
        stateProp.setType(stringType);
        USaddrType.getDeclaredProperties().add(stateProp);

        SDOProperty zipProp = new SDOProperty(aHelperContext);
        zipProp.setName("zip");
        zipProp.setType(decimalType);
        USaddrType.getDeclaredProperties().add(zipProp);

        SDOProperty countryProp = new SDOProperty(aHelperContext);
        countryProp.setName("country");
        countryProp.setType(stringType);
        countryProp.setDefault("US");
        USaddrType.getDeclaredProperties().add(countryProp);

        /****QUANTITY TYPE*****/
        SDOType quantityType = new SDOType(uri, "quantityType");
        quantityType.setDataType(true);
        quantityType.getBaseTypes().add(intType);

        /****SKU TYPE*****/
        SDOType SKUType = new SDOType(uri, "SKU");
        SKUType.setDataType(true);
        SKUType.getBaseTypes().add(stringType);

        /****ITEM TYPE*****/
        SDOType itemType = new SDOType(uri, "Item");
        itemType.setDataType(false);

        SDOProperty productNameProp = new SDOProperty(aHelperContext);
        productNameProp.setName("productName");
        productNameProp.setType(stringType);
        itemType.getDeclaredProperties().add(productNameProp);

        SDOProperty quantityProp = new SDOProperty(aHelperContext);
        quantityProp.setName("quantity");
        quantityProp.setType(quantityType);
        itemType.getDeclaredProperties().add(quantityProp);

        SDOProperty partNumProp = new SDOProperty(aHelperContext);
        partNumProp.setName("partNum");
        partNumProp.setType(SKUType);
        itemType.getDeclaredProperties().add(partNumProp);

        SDOProperty USPriceProp = new SDOProperty(aHelperContext);
        USPriceProp.setName("USPrice");
        USPriceProp.setType(decimalType);
        itemType.getDeclaredProperties().add(USPriceProp);

        SDOProperty itemCommentProp = new SDOProperty(aHelperContext);
        itemCommentProp.setName("comment");
        itemCommentProp.setType(stringType);
        itemCommentProp.setContainment(false);
        itemType.getDeclaredProperties().add(itemCommentProp);

        SDOProperty shipDateProp = new SDOProperty(aHelperContext);
        shipDateProp.setName("shipDate");
        shipDateProp.setType(yearMonthDayType);
        shipDateProp.setContainment(false);
        itemType.getDeclaredProperties().add(shipDateProp);

        /****ITEMS TYPE*****/
        SDOType itemsType = new SDOType(uri, "Items");
        itemsType.setDataType(false);

        SDOProperty itemProp = new SDOProperty(aHelperContext);
        itemProp.setName("item");
        itemProp.setContainment(true);
        itemProp.setMany(true);
        //itemProp.setContainingType(itemType);
        itemProp.setType(itemType);
        itemsType.getDeclaredProperties().add(itemProp);

        /****PURCHASEORDER TYPE*****/
        SDOProperty shipToProp = new SDOProperty(aHelperContext);
        shipToProp.setName("shipTo");
        shipToProp.setContainment(true);
        shipToProp.setType(USaddrType);

        SDOProperty billToProp = new SDOProperty(aHelperContext);
        billToProp.setName("billTo");
        billToProp.setContainment(true);
        billToProp.setType(USaddrType);

        SDOProperty itemsProp = new SDOProperty(aHelperContext);
        itemsProp.setName("items");
        itemsProp.setContainment(true);
        itemsProp.setType(itemsType);

        SDOProperty commentProp = new SDOProperty(aHelperContext);
        commentProp.setName("comment");
        commentProp.setType(stringType);
        commentProp.setContainment(false);

        SDOProperty orderDateProp = new SDOProperty(aHelperContext);
        orderDateProp.setName("orderDate");
        orderDateProp.setType(yearMonthDayType);
        orderDateProp.setContainment(false);

        SDOType POtype = new SDOType(uri, "PurchaseOrder");
        POtype.setDataType(false);
        POtype.getDeclaredProperties().add(shipToProp);
        POtype.getDeclaredProperties().add(billToProp);
        POtype.getDeclaredProperties().add(itemsProp);
        POtype.getDeclaredProperties().add(commentProp);
        POtype.getDeclaredProperties().add(orderDateProp);

        types.add(USaddrType);
        types.add(POtype);
        types.add(itemsType);
        types.add(itemType);
        types.add(quantityType);
        types.add(SKUType);

        return types;
    }
}
