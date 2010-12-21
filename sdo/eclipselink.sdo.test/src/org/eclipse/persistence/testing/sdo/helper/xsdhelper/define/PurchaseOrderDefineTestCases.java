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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define;

import commonj.sdo.Type;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.internal.helper.ClassConstants;

public class PurchaseOrderDefineTestCases extends XSDHelperDefineTestCases {
    public PurchaseOrderDefineTestCases(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(PurchaseOrderDefineTestCases.class);
    }

    public String getSchemaToDefine() {
        return "org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/PurchaseOrder.xsd";
    }

    public List getControlTypes() {
        List types = new ArrayList();
        String uri = null;
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type intType = typeHelper.getType("commonj.sdo", "Int");
        Type yearMonthDayType = typeHelper.getType("commonj.sdo", "YearMonthDay");
        Type decimalType = typeHelper.getType("commonj.sdo", "Decimal");

        /****ADDRESS TYPE*****/
        String javaPackage = "noNamespace";

        //ADDRESS TYPE
        SDOType USaddrType = new SDOType(uri, "USAddress");
        USaddrType.setXsd(true);
        USaddrType.setXsdLocalName("USAddress");
        USaddrType.setDataType(false);
        USaddrType.setInstanceClassName(javaPackage + "." + "USAddress");
        USaddrType.setXsdLocalName("USAddress");
        SDOProperty addrNameProp = new SDOProperty(aHelperContext);
        
        addrNameProp.setName("name");
        addrNameProp.setXsdLocalName("name");
        addrNameProp.setXsd(true);
        addrNameProp.setContainingType(USaddrType);
        addrNameProp.setType(stringType);
        USaddrType.getDeclaredProperties().add(addrNameProp);

        SDOProperty streetProp = new SDOProperty(aHelperContext);
        streetProp.setName("street");
        streetProp.setXsdLocalName("street");
        streetProp.setXsd(true);
        streetProp.setContainingType(USaddrType);
        streetProp.setType(stringType);
        // streetProp.setAttribute(true);
        USaddrType.getDeclaredProperties().add(streetProp);

        SDOProperty cityProp = new SDOProperty(aHelperContext);
        cityProp.setName("city");
        cityProp.setType(stringType);
        // cityProp.setAttribute(true);
        cityProp.setXsdLocalName("city");
        cityProp.setXsd(true);
        cityProp.setContainingType(USaddrType);
        USaddrType.getDeclaredProperties().add(cityProp);

        SDOProperty stateProp = new SDOProperty(aHelperContext);
        stateProp.setName("state");
        stateProp.setXsdLocalName("state");
        stateProp.setXsd(true);
        stateProp.setContainingType(USaddrType);
        stateProp.setType(stringType);
        // stateProp.setAttribute(true);
        USaddrType.getDeclaredProperties().add(stateProp);

        SDOProperty zipProp = new SDOProperty(aHelperContext);
        zipProp.setName("zip");
        zipProp.setXsdLocalName("zip");
        zipProp.setXsd(true);
        zipProp.setContainingType(USaddrType);
        zipProp.setType(decimalType);
        //  zipProp.setAttribute(true);
        USaddrType.getDeclaredProperties().add(zipProp);

        SDOProperty countryProp = new SDOProperty(aHelperContext);
        countryProp.setName("country");
        countryProp.setXsdLocalName("country");
        countryProp.setXsd(true);
        countryProp.setContainingType(USaddrType);
        countryProp.setType(stringType);
        //countryProp.setAttribute(true);
        countryProp.setDefault("US");
        USaddrType.getDeclaredProperties().add(countryProp);

        /****QUANTITY TYPE*****/
        SDOType quantityType = new SDOType(uri, "quantityType");        
        quantityType.setXsd(true);
        quantityType.setXsdLocalName("quantityType");
        quantityType.setDataType(true);
        quantityType.getBaseTypes().add(intType);
        //quantityType.setInstanceClassName("java.lang.Integer");
        quantityType.setInstanceClassName(ClassConstants.PINT.getName());

        /****SKU TYPE*****/
        SDOType SKUType = new SDOType(uri, "SKU");
        SKUType.setXsd(true);
        SKUType.setXsdLocalName("SKU");
        SKUType.setDataType(true);
        SKUType.getBaseTypes().add(stringType);
        SKUType.setInstanceClassName("java.lang.String");

        /****ITEM TYPE*****/
        SDOType itemType = new SDOType(uri, "Item");
        itemType.setXsd(true);
        itemType.setXsdLocalName("Item");
        itemType.setDataType(false);
        itemType.setInstanceClassName(javaPackage + "." + "Item");

        SDOProperty productNameProp = new SDOProperty(aHelperContext);
        productNameProp.setName("productName");
        productNameProp.setXsdLocalName("productName");
        productNameProp.setXsd(true);
        productNameProp.setContainingType(itemType);
        productNameProp.setType(stringType);
        // productNameProp.setAttribute(true);
        itemType.getDeclaredProperties().add(productNameProp);

        SDOProperty quantityProp = new SDOProperty(aHelperContext);
        quantityProp.setName("quantity");
        quantityProp.setType(quantityType);
        //quantityProp.setAttribute(true);
        quantityProp.setXsdLocalName("quantity");
        quantityProp.setXsd(true);
        quantityProp.setContainingType(itemType);
        itemType.getDeclaredProperties().add(quantityProp);

        SDOProperty partNumProp = new SDOProperty(aHelperContext);
        partNumProp.setName("partNum");
        partNumProp.setType(SKUType);
        //partNumProp.setAttribute(true);
        partNumProp.setXsdLocalName("partNum");
        partNumProp.setXsd(true);
        partNumProp.setContainingType(itemType);
        itemType.getDeclaredProperties().add(partNumProp);

        SDOProperty USPriceProp = new SDOProperty(aHelperContext);
        USPriceProp.setName("USPrice");
        USPriceProp.setType(decimalType);
        USPriceProp.setXsdLocalName("USPrice");
        USPriceProp.setXsd(true);
        USPriceProp.setContainingType(itemType);
        //USPriceProp.setAttribute(true);
        itemType.getDeclaredProperties().add(USPriceProp);

        SDOProperty itemCommentProp = new SDOProperty(aHelperContext);
        itemCommentProp.setName("comment");
        itemCommentProp.setType(stringType);
        itemCommentProp.setContainment(false);
        //itemCommentProp.setAttribute(true);
        itemCommentProp.setXsdLocalName("comment");
        itemCommentProp.setXsd(true);
        itemCommentProp.setContainingType(itemType);
        itemType.getDeclaredProperties().add(itemCommentProp);

        SDOProperty shipDateProp = new SDOProperty(aHelperContext);
        shipDateProp.setName("shipDate");
        shipDateProp.setType(yearMonthDayType);
        shipDateProp.setContainment(false);
        //shipDateProp.setAttribute(true);
        shipDateProp.setXsdLocalName("shipDate");
        shipDateProp.setXsd(true);
        shipDateProp.setContainingType(itemType);
        itemType.getDeclaredProperties().add(shipDateProp);

        /****ITEMS TYPE*****/
        SDOType itemsType = new SDOType(uri, "Items");
        itemsType.setXsd(true);
        itemsType.setXsdLocalName("Items");
        itemsType.setDataType(false);
        itemsType.setInstanceClassName(javaPackage + "." + "Items");

        SDOProperty itemProp = new SDOProperty(aHelperContext);
        itemProp.setName("item");
        itemProp.setContainment(true);
        itemProp.setMany(true);
        //itemProp.setElement(true);
        itemProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        itemProp.setXsdLocalName("item");
        itemProp.setXsd(true);
        itemProp.setContainingType(itemsType);
        //itemProp.setContainingType(itemType);
        itemProp.setType(itemType);
        itemsType.getDeclaredProperties().add(itemProp);

        /****PURCHASEORDER TYPE*****/
        SDOType POtype = new SDOType(uri, "PurchaseOrder");
        POtype.setXsd(true);
        POtype.setXsdLocalName("PurchaseOrder");
        POtype.setDataType(false);
        POtype.setInstanceClassName(javaPackage + "." + "PurchaseOrder");

        SDOProperty shipToProp = new SDOProperty(aHelperContext);
        shipToProp.setName("shipTo");
        shipToProp.setContainment(true);
        //shipToProp.setElement(true);
        shipToProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        shipToProp.setType(USaddrType);
        shipToProp.setXsdLocalName("shipTo");
        shipToProp.setXsd(true);
        shipToProp.setContainingType(POtype);

        SDOProperty billToProp = new SDOProperty(aHelperContext);
        billToProp.setName("billTo");
        billToProp.setContainment(true);
        //billToProp.setElement(true);
        billToProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        billToProp.setType(USaddrType);
        billToProp.setXsdLocalName("billTo");
        billToProp.setXsd(true);
        billToProp.setContainingType(POtype);

        SDOProperty itemsProp = new SDOProperty(aHelperContext);
        itemsProp.setName("items");
        itemsProp.setContainment(true);
        //itemsProp.setElement(true);
        itemsProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        itemsProp.setType(itemsType);
        itemsProp.setXsdLocalName("items");
        itemsProp.setXsd(true);
        itemsProp.setContainingType(POtype);

        SDOProperty commentProp = new SDOProperty(aHelperContext);
        commentProp.setName("comment");
        commentProp.setType(stringType);
        //commentProp.setAttribute(true);
        commentProp.setContainment(false);
        commentProp.setXsdLocalName("comment");
        commentProp.setXsd(true);
        commentProp.setContainingType(POtype);

        SDOProperty orderDateProp = new SDOProperty(aHelperContext);
        orderDateProp.setName("orderDate");
        //orderDateProp.setAttribute(true);
        orderDateProp.setType(yearMonthDayType);
        orderDateProp.setContainment(false);
        orderDateProp.setXsdLocalName("orderDate");
        orderDateProp.setXsd(true);
        orderDateProp.setContainingType(POtype);

        POtype.getDeclaredProperties().add(shipToProp);
        POtype.getDeclaredProperties().add(billToProp);
        POtype.getDeclaredProperties().add(itemsProp);
        POtype.getDeclaredProperties().add(commentProp);
        POtype.getDeclaredProperties().add(orderDateProp);

        types.add(itemsType);
        types.add(USaddrType);
        types.add(SKUType);
        types.add(itemType);
        types.add(POtype);
        types.add(quantityType);

        return types;
    }
}
