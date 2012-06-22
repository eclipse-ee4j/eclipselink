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

import commonj.sdo.Type;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.internal.helper.ClassConstants;

public class PurchaseOrderComplexDefineTestCases extends XSDHelperDefineTestCases {
    public PurchaseOrderComplexDefineTestCases(String name) {
        super(name);
    }

    public String getSchemaToDefine() {
        return "org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderComplex.xsd";
    }

    public static void main(String[] args) {
        TestRunner.run(PurchaseOrderComplexDefineTestCases.class);
    }

    public List getControlTypes() {
        java.util.List types = new ArrayList();
        String uri = NON_DEFAULT_URI;
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type intType = typeHelper.getType("commonj.sdo", "Int");
        Type yearMonthDayType = typeHelper.getType("commonj.sdo", "YearMonthDay");
        Type floatType = typeHelper.getType("commonj.sdo", "Float");
        String javaPackage = NON_DEFAULT_JAVA_PACKAGE_NAME;

        /****ADDRESS TYPE*****/

        //ADDRESS TYPE
        SDOType addrType = new SDOType(uri, "AddressType");
        addrType.setDataType(false);
        addrType.setInstanceClassName(javaPackage + "." + "AddressType");
        addrType.setXsd(true);
        addrType.setXsdLocalName("AddressType");

        SDOProperty addrNameProp = new SDOProperty(aHelperContext);
        addrNameProp.setName("name");
        addrNameProp.setType(stringType);
        addrNameProp.setXsd(true);
        addrNameProp.setXsdLocalName("name");
        //addrNameProp.setElement(true);
        addrNameProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        addrNameProp.setContainment(true);
        addrNameProp.setContainingType(addrType);
        addrType.getDeclaredProperties().add(addrNameProp);

        SDOProperty streetProp = new SDOProperty(aHelperContext);
        streetProp.setName("street");
        streetProp.setType(stringType);
        streetProp.setXsd(true);
        streetProp.setXsdLocalName("street");
        //streetProp.setElement(true);
        streetProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        streetProp.setContainment(true);
        streetProp.setContainingType(addrType);
        addrType.getDeclaredProperties().add(streetProp);

        SDOProperty cityProp = new SDOProperty(aHelperContext);
        cityProp.setName("city");
        cityProp.setType(stringType);
        cityProp.setXsd(true);
        cityProp.setXsdLocalName("city");
        //cityProp.setElement(true);
        cityProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        cityProp.setContainment(true);
        cityProp.setContainingType(addrType);
        addrType.getDeclaredProperties().add(cityProp);
        
        
        SDOProperty base64Prop = new SDOProperty(aHelperContext);
        base64Prop.setName("base64Test");
        base64Prop.setType(SDOConstants.SDO_BYTES);
        base64Prop.setXsd(true);
        base64Prop.setXsdLocalName("base64Test");
        //cityProp.setElement(true);
        base64Prop.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        base64Prop.setContainment(true);
        base64Prop.setContainingType(addrType);
        addrType.getDeclaredProperties().add(base64Prop);

        SDOProperty countryProp = new SDOProperty(aHelperContext);
        countryProp.setName("country");
        countryProp.setType(stringType);
        countryProp.setXsdLocalName("country");
        countryProp.setXsd(true);        
        countryProp.setDefault("US");        
        countryProp.setContainingType(addrType);
        addrType.getDeclaredProperties().add(countryProp);

        /****US ADDRESS TYPE*****/
        SDOType usAddressType = new SDOType(uri, "usAddressType");
        usAddressType.setXsd(true);
        usAddressType.setDataType(false);
        usAddressType.setInstanceClassName(javaPackage + "." + "UsAddressType");
        usAddressType.setXsdLocalName("usAddressType");
        ArrayList baseTypes = new ArrayList();
        baseTypes.add(addrType);
        usAddressType.setBaseTypes(baseTypes);

        SDOProperty stateProp = new SDOProperty(aHelperContext);
        stateProp.setName("state");
        stateProp.setType(stringType);
        stateProp.setXsd(true);
        stateProp.setXsdLocalName("state");
        //stateProp.setElement(true);
        stateProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        stateProp.setContainment(true);
        stateProp.setContainingType(usAddressType);
        usAddressType.getDeclaredProperties().add(stateProp);

        SDOProperty zipProp = new SDOProperty(aHelperContext);
        zipProp.setName("zip");
        zipProp.setXsdLocalName("zip");
        zipProp.setXsd(true);
        zipProp.setType(intType);
        
        zipProp.setContainingType(usAddressType);
        usAddressType.getDeclaredProperties().add(zipProp);

        /****CDN ADDRESS TYPE*****/
        SDOType cdnAddressType = new SDOType(uri, "cdnAddressType");
        cdnAddressType.setXsd(true);
        cdnAddressType.setDataType(false);
        cdnAddressType.setInstanceClassName(javaPackage + "." + "CdnAddressType");
        cdnAddressType.setXsdLocalName("cdnAddressType");
        ArrayList cdnbaseTypes = new ArrayList();
        cdnbaseTypes.add(addrType);
        cdnAddressType.setBaseTypes(cdnbaseTypes);

        SDOProperty postalCodeProp = new SDOProperty(aHelperContext);
        postalCodeProp.setName("postalcode");
        postalCodeProp.setType(stringType);
        postalCodeProp.setXsd(true);
        postalCodeProp.setXsdLocalName("postalcode");
        //postalCodeProp.setElement(true);
        postalCodeProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        postalCodeProp.setContainment(true);
        postalCodeProp.setContainingType(cdnAddressType);
        cdnAddressType.getDeclaredProperties().add(postalCodeProp);

        SDOProperty provinceProp = new SDOProperty(aHelperContext);
        provinceProp.setName("province");
        provinceProp.setType(stringType);
        provinceProp.setXsd(true);
        provinceProp.setXsdLocalName("province");
        //provinceProp.setElement(true);
        provinceProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        provinceProp.setContainment(true);
        provinceProp.setContainingType(cdnAddressType);
        cdnAddressType.getDeclaredProperties().add(provinceProp);

        SDOProperty territoryProp = new SDOProperty(aHelperContext);
        territoryProp.setName("territory");
        territoryProp.setXsdLocalName("territory");
        territoryProp.setType(stringType);
        territoryProp.setXsd(true);
        territoryProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        //territoryProp.setElement(true);
        territoryProp.setContainment(true);
        territoryProp.setContainingType(cdnAddressType);
        cdnAddressType.getDeclaredProperties().add(territoryProp);

        /****CDN MAILING ADDRESS TYPE*****/
        SDOType cdnMailingAddressType = new SDOType(uri, "cdnAddressMailingType");
        cdnMailingAddressType.setXsd(true);
        cdnMailingAddressType.setDataType(false);
        cdnMailingAddressType.setInstanceClassName(javaPackage + "." + "CdnAddressMailingType");
        cdnMailingAddressType.setXsdLocalName("cdnAddressMailingType");
        ArrayList cdnMailingbaseTypes = new ArrayList();
        cdnMailingbaseTypes.add(cdnAddressType);
        cdnMailingAddressType.setBaseTypes(cdnbaseTypes);
        
        SDOProperty deliveryProp = new SDOProperty(aHelperContext);
        deliveryProp.setName("deliveryInfo");
        deliveryProp.setXsdLocalName("deliveryInfo");
        deliveryProp.setType(stringType);
        deliveryProp.setXsd(true);        
        deliveryProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        deliveryProp.setContainment(true);
        deliveryProp.setContainingType(cdnMailingAddressType);
        cdnMailingAddressType.getDeclaredProperties().add(deliveryProp);


        /****QUANTITY TYPE*****/
        SDOType quantityType = new SDOType(uri, "quantityType");
        quantityType.setXsd(true);
        quantityType.setDataType(true);
        quantityType.getBaseTypes().add(intType);
        //quantityType.setInstanceClassName("java.lang.Integer");
        quantityType.setInstanceClassName(ClassConstants.PINT.getName());
        quantityType.setXsdLocalName("quantityType");

        /****SKU TYPE*****/
        SDOType SKUType = new SDOType(uri, "SKU");
        SKUType.setXsd(true);
        SKUType.setDataType(true);
        SKUType.getBaseTypes().add(stringType);
        SKUType.setInstanceClassName("java.lang.String");
        SKUType.setXsdLocalName("SKU");

        /****PHONE TYPE*****/
        SDOType phoneType = new SDOType(uri, "phoneNumber");
        phoneType.setXsd(true);
        phoneType.setDataType(true);
        phoneType.getBaseTypes().add(stringType);
        phoneType.setInstanceClassName("java.lang.String");
        phoneType.setXsdLocalName("phoneNumber");

        /****NAME PREFIX TYPE*****/
        SDOType namePrefixType = new SDOType(uri, "namePrefix");
        namePrefixType.setXsd(true);

        //namePrefixType.setDataType(true);
        namePrefixType.setDataType(false);
        namePrefixType.getBaseTypes().add(stringType);
        namePrefixType.setInstanceClassName("java.lang.String");
        namePrefixType.setXsdLocalName("namePrefix");

        /****GENDER TYPE*****/
        SDOType genderType = new SDOType(uri, "gender");
        genderType.setXsd(true);
        genderType.setDataType(true);
        genderType.getBaseTypes().add(stringType);
        genderType.setInstanceClassName("java.lang.String");
        genderType.setXsdLocalName("gender");

        /****ITEM TYPE*****/
        SDOType itemType = new SDOType(uri, "LineItemType");
        itemType.setXsd(true);
        itemType.setDataType(false);
        itemType.setInstanceClassName(javaPackage + "." + "LineItemType");
        itemType.setXsdLocalName("LineItemType");

        SDOProperty productNameProp = new SDOProperty(aHelperContext);
        productNameProp.setName("productName");
        productNameProp.setXsdLocalName("productName");
        productNameProp.setType(stringType);
        productNameProp.setXsd(true);
        //productNameProp.setElement(true);
        productNameProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);

        productNameProp.setContainment(true);
        productNameProp.setContainingType(itemType);
        itemType.getDeclaredProperties().add(productNameProp);

        SDOProperty partNumProp = new SDOProperty(aHelperContext);
        partNumProp.setName("partNum");
        partNumProp.setXsdLocalName("partNum");
        partNumProp.setType(SKUType);
        partNumProp.setXsd(true);        
        partNumProp.setContainingType(itemType);
        itemType.getDeclaredProperties().add(partNumProp);

        SDOProperty quantityProp = new SDOProperty(aHelperContext);
        quantityProp.setName("quantity");
        quantityProp.setType(quantityType);
        quantityProp.setXsd(true);
        quantityProp.setXsdLocalName("quantity");              
        quantityProp.setContainingType(itemType);
        itemType.getDeclaredProperties().add(quantityProp);

        SDOProperty USPriceProp = new SDOProperty(aHelperContext);
        USPriceProp.setName("USPrice");
        USPriceProp.setXsdLocalName("USPrice");
        USPriceProp.setType(floatType);
        USPriceProp.setXsd(true);
        USPriceProp.setContainment(true);
        //USPriceProp.setElement(true);
        USPriceProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        USPriceProp.setContainingType(itemType);
        itemType.getDeclaredProperties().add(USPriceProp);

        SDOProperty CDNPriceProp = new SDOProperty(aHelperContext);
        CDNPriceProp.setName("CDNPrice");
        CDNPriceProp.setXsdLocalName("CDNPrice");
        CDNPriceProp.setType(floatType);
        CDNPriceProp.setXsd(true);
        //CDNPriceProp.setElement(true);
        CDNPriceProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        CDNPriceProp.setContainment(true);
        CDNPriceProp.setContainingType(itemType);
        itemType.getDeclaredProperties().add(CDNPriceProp);

        SDOProperty exchangeProp = new SDOProperty(aHelperContext);
        exchangeProp.setName("exchangeRate");
        exchangeProp.setXsdLocalName("exchangeRate");
        exchangeProp.setType(floatType);
        exchangeProp.setXsd(true);
        //exchangeProp.setElement(true);
        exchangeProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        exchangeProp.setContainment(true);
        exchangeProp.setContainingType(itemType);
        itemType.getDeclaredProperties().add(exchangeProp);

        SDOProperty shipDateProp = new SDOProperty(aHelperContext);
        shipDateProp.setName("shipDate");
        shipDateProp.setXsdLocalName("shipDate");
        shipDateProp.setType(yearMonthDayType);
        shipDateProp.setContainment(true);
        //shipDateProp.setElement(true);
        shipDateProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        shipDateProp.setContainingType(itemType);
        shipDateProp.setXsd(true);

        SDOProperty itemCommentProp = new SDOProperty(aHelperContext);
        itemCommentProp.setName("comment");
        itemCommentProp.setXsdLocalName("comment");
        itemCommentProp.setType(stringType);
        itemCommentProp.setContainment(true);
        //itemCommentProp.setElement(true);
        itemCommentProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        itemCommentProp.setXsd(true);
        itemCommentProp.setContainment(true);
        itemCommentProp.setContainingType(itemType);
        itemType.getDeclaredProperties().add(itemCommentProp);

        itemType.getDeclaredProperties().add(shipDateProp);

        /****ITEMS TYPE*****/
        SDOType itemsType = new SDOType(uri, "Items");
        itemsType.setXsd(true);
        itemsType.setDataType(false);
        itemsType.setInstanceClassName(javaPackage + "." + "Items");
        itemsType.setXsdLocalName("Items");
        SDOProperty itemProp = new SDOProperty(aHelperContext);
        itemProp.setName("item");
        itemProp.setXsdLocalName("item");
        itemProp.setContainment(true);
        itemProp.setXsd(true);
        //itemProp.setElement(true);
        itemProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        itemProp.setMany(true);
        itemProp.setContainingType(itemsType);
        itemProp.setType(itemType);
        itemsType.getDeclaredProperties().add(itemProp);

        /****CUSTOMER TYPE*****/
        SDOType customerType = new SDOType(uri, "CustomerType");
        customerType.setXsd(true);
        customerType.setDataType(false);
        customerType.setInstanceClassName(javaPackage + "." + "CustomerType");
        customerType.setXsdLocalName("CustomerType");

        SDOProperty nameProp = new SDOProperty(aHelperContext);
        nameProp.setName("name");
        nameProp.setXsdLocalName("name");
        nameProp.setType(stringType);
        nameProp.setXsd(true);
        //nameProp.setElement(true);
        nameProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        nameProp.setContainingType(customerType);
        customerType.getDeclaredProperties().add(nameProp);

        SDOProperty genderProp = new SDOProperty(aHelperContext);
        genderProp.setName("gender");
        genderProp.setXsdLocalName("gender");
        genderProp.setType(genderType);
        //genderProp.setType(stringType);             
        genderProp.setXsd(true);
        //genderProp.setElement(true);
        genderProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        genderProp.setContainingType(customerType);
        genderProp.setContainment(true);
        customerType.getDeclaredProperties().add(genderProp);

        SDOProperty namePrefixProp = new SDOProperty(aHelperContext);
        namePrefixProp.setName("namePrefix");
        //TODO or string type??
        namePrefixProp.setXsdLocalName("namePrefix");
        namePrefixProp.setType(namePrefixType);
        namePrefixProp.setXsd(true);        
        namePrefixProp.setContainingType(customerType);
        customerType.getDeclaredProperties().add(namePrefixProp);

        SDOProperty phoneProp = new SDOProperty(aHelperContext);
        phoneProp.setName("phoneNumber");
        //TODO or intType type??
        phoneProp.setXsdLocalName("phoneNumber");
        phoneProp.setType(phoneType);
        phoneProp.setXsd(true);
        //phoneProp.setElement(true);
        phoneProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        phoneProp.setContainingType(customerType);
        phoneProp.setContainment(true);
        customerType.getDeclaredProperties().add(phoneProp);

        /****PURCHASEORDER TYPE*****/
        SDOType POtype = new SDOType(uri, "PurchaseOrderType");
        POtype.setXsd(true);
        POtype.setInstanceClassName(javaPackage + "." + "PurchaseOrderType");
        POtype.setDataType(false);
        POtype.setXsdLocalName("PurchaseOrderType");

        SDOProperty shipToProp = new SDOProperty(aHelperContext);
        shipToProp.setName("shipTo");
        shipToProp.setXsdLocalName("shipTo");
        shipToProp.setContainment(true);
        //shipToProp.setElement(true);
        shipToProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        shipToProp.setXsd(true);
        shipToProp.setContainingType(POtype);
        shipToProp.setType(addrType);

        SDOProperty billToProp = new SDOProperty(aHelperContext);
        billToProp.setName("billTo");
        billToProp.setXsdLocalName("billTo");
        billToProp.setContainment(true);
        //billToProp.setElement(true);
        billToProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        billToProp.setXsd(true);
        billToProp.setContainingType(POtype);
        billToProp.setType(addrType);

        SDOProperty commentProp = new SDOProperty(aHelperContext);
        commentProp.setName("comment");
        commentProp.setXsdLocalName("comment");
        commentProp.setType(stringType);
        //commentProp.setElement(true);
        commentProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        commentProp.setXsd(true);
        commentProp.setContainingType(POtype);
        commentProp.setContainment(true);

        SDOProperty itemsProp = new SDOProperty(aHelperContext);
        itemsProp.setName("items");
        itemsProp.setXsdLocalName("items");
        itemsProp.setContainment(true);
        //itemsProp.setElement(true);      
        itemsProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        itemsProp.setXsd(true);
        itemsProp.setContainingType(POtype);
        itemsProp.setType(itemsType);

        SDOProperty customerProp = new SDOProperty(aHelperContext);
        customerProp.setName("customer");
        customerProp.setXsdLocalName("customer");
        customerProp.setType(customerType);
        customerProp.setXsd(true);
        customerProp.setContainingType(POtype);
        customerProp.setContainment(true);
        //customerProp.setElement(true);      
        customerProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);

        SDOProperty poIdProp = new SDOProperty(aHelperContext);
        poIdProp.setName("poId");
        poIdProp.setXsdLocalName("poId");
        poIdProp.setType(stringType);
        poIdProp.setXsd(true);
        poIdProp.setContainingType(POtype);        

        SDOProperty orderDateProp = new SDOProperty(aHelperContext);
        orderDateProp.setName("orderDate");
        orderDateProp.setXsdLocalName("orderDate");
        orderDateProp.setType(yearMonthDayType);
        orderDateProp.setXsd(true);
        orderDateProp.setContainingType(POtype);        

        POtype.getDeclaredProperties().add(shipToProp);
        POtype.getDeclaredProperties().add(billToProp);
        POtype.getDeclaredProperties().add(commentProp);
        POtype.getDeclaredProperties().add(itemsProp);
        POtype.getDeclaredProperties().add(poIdProp);
        POtype.getDeclaredProperties().add(orderDateProp);
        POtype.getDeclaredProperties().add(customerProp);


        types.add(SKUType);
        types.add(cdnAddressType);
        types.add(POtype);
        types.add(itemsType);        
        types.add(cdnMailingAddressType);
        types.add(genderType);
        types.add(namePrefixType);        
        types.add(quantityType);
        types.add(phoneType);
        types.add(usAddressType);        
        types.add(customerType);        
        types.add(itemType);
        types.add(addrType);
        
        return types;

    }
}
