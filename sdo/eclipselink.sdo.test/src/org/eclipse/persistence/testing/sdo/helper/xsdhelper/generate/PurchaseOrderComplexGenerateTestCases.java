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
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.DefaultSchemaLocationResolver;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class PurchaseOrderComplexGenerateTestCases extends XSDHelperGenerateTestCases {
    public PurchaseOrderComplexGenerateTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate.PurchaseOrderComplexGenerateTestCases" };
        TestRunner.main(arguments);
    }

    public String getControlFileName() {
        return "org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderComplexGenerated.xsd";
    }
    public String getControlFileNameDifferentOrder() {
        return "org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderComplexGeneratedDiffOrder.xsd";
    }

    public void testGenerateSchemaRoundTrip() throws Exception{
        DefaultSchemaLocationResolver resolver = new DefaultSchemaLocationResolver(getMap());
        List types = defineTypesFromSchema();
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(types, resolver);
        String controlSchema = getSchema(getControlFileNameDifferentOrder());
        log("EXPECTED: \n" + controlSchema);
        log("ACTUAL: \n" + generatedSchema);
                
        StringReader reader = new StringReader(generatedSchema);
        InputSource inputSource = new InputSource(reader);
        Document generatedSchemaDoc = parser.parse(inputSource);        
        reader.close();
        
        assertSchemaIdentical(getDocument(getControlFileNameDifferentOrder()), generatedSchemaDoc);       
    }

    public java.util.List defineTypesFromSchema() {
        InputStream is = getSchemaInputStream("org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderComplex.xsd");
        return ((SDOXSDHelper)xsdHelper).define(is, null);
    }

    public java.util.List getTypesToGenerateFrom() {
        java.util.List types = new ArrayList();
        String uri = "http://www.example.org";
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type intType = typeHelper.getType("commonj.sdo", "Int");
        Type dateType = typeHelper.getType("commonj.sdo", "Date");
        Type floatType = typeHelper.getType("commonj.sdo", "Float");

        /****ADDRESS TYPE*****/

        //ADDRESS TYPE
        SDOType addrType = new SDOType(uri, "AddressType");
        addrType.setInstanceClassName("defaultPackage.AddressType");
        addrType.setDataType(false);

        SDOProperty addrNameProp = new SDOProperty(aHelperContext);
        addrNameProp.setName("name");
        addrNameProp.setType(stringType);
        addrNameProp.setXsd(true);
        //addrNameProp.setElement(true);
        addrNameProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        addrNameProp.setContainment(true);
        addrType.getDeclaredProperties().add(addrNameProp);

        SDOProperty streetProp = new SDOProperty(aHelperContext);
        streetProp.setName("street");
        streetProp.setType(stringType);
        streetProp.setXsd(true);
        //streetProp.setElement(true);
        streetProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        streetProp.setContainment(true);
        addrType.getDeclaredProperties().add(streetProp);

        SDOProperty cityProp = new SDOProperty(aHelperContext);
        cityProp.setName("city");
        cityProp.setType(stringType);
        cityProp.setXsd(true);
        //cityProp.setElement(true);
        cityProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        cityProp.setContainment(true);
        addrType.getDeclaredProperties().add(cityProp);

        SDOProperty countryProp = new SDOProperty(aHelperContext);
        countryProp.setName("country");
        countryProp.setType(stringType);
        countryProp.setXsd(true);
        //countryProp.setAttribute(true);
        //countryProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        countryProp.setDefault("US");
        //countryProp.setContainment(true);
        addrType.getDeclaredProperties().add(countryProp);

        /****US ADDRESS TYPE*****/
        SDOType usAddressType = new SDOType(uri, "usAddressType");
        usAddressType.setDataType(false);
        ArrayList baseTypes = new ArrayList();
        baseTypes.add(addrType);
        usAddressType.setBaseTypes(baseTypes);

        SDOProperty stateProp = new SDOProperty(aHelperContext);
        stateProp.setName("state");
        stateProp.setType(stringType);
        stateProp.setXsd(true);
        //stateProp.setElement(true);
        stateProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        stateProp.setContainment(true);
        usAddressType.getDeclaredProperties().add(stateProp);

        SDOProperty zipProp = new SDOProperty(aHelperContext);
        zipProp.setName("zip");
        zipProp.setXsd(true);
        //zipProp.setAttribute(true);
        zipProp.setType(intType);
        //zipProp.setContainment(true);
        usAddressType.getDeclaredProperties().add(zipProp);

        /****CDN ADDRESS TYPE*****/
        SDOType cdnAddressType = new SDOType(uri, "cdnAddressType");
        cdnAddressType.setDataType(false);
        ArrayList cdnbaseTypes = new ArrayList();
        cdnbaseTypes.add(addrType);
        cdnAddressType.setBaseTypes(cdnbaseTypes);

        SDOProperty provinceProp = new SDOProperty(aHelperContext);
        provinceProp.setName("province");
        provinceProp.setType(stringType);
        provinceProp.setXsd(true);
        //provinceProp.setElement(true);
        provinceProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        provinceProp.setContainment(true);
        cdnAddressType.getDeclaredProperties().add(provinceProp);

        SDOProperty territoryProp = new SDOProperty(aHelperContext);
        territoryProp.setName("territory");
        territoryProp.setType(stringType);
        territoryProp.setXsd(true);
        //territoryProp.setElement(true);
        territoryProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        territoryProp.setContainment(true);
        cdnAddressType.getDeclaredProperties().add(territoryProp);

        SDOProperty postalCodeProp = new SDOProperty(aHelperContext);
        postalCodeProp.setName("postalcode");
        postalCodeProp.setType(stringType);
        postalCodeProp.setXsd(true);
        //postalCodeProp.setElement(true);
        postalCodeProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        postalCodeProp.setContainment(true);
        cdnAddressType.getDeclaredProperties().add(postalCodeProp);
        
        /****CDN MAILING ADDRESS TYPE*****/
        SDOType cdnMailingAddressType = new SDOType(uri, "cdnAddressMailingType");
        cdnMailingAddressType.setDataType(false);
        ArrayList cdnMailingbaseTypes = new ArrayList();
        cdnMailingbaseTypes.add(cdnAddressType);
        cdnMailingAddressType.setBaseTypes(cdnMailingbaseTypes);
        
           SDOProperty deliveryProp = new SDOProperty(aHelperContext);
        deliveryProp.setName("deliveryInfo");
        deliveryProp.setType(stringType);
        deliveryProp.setXsd(true);        
        deliveryProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        deliveryProp.setContainment(true);
        cdnMailingAddressType.getDeclaredProperties().add(deliveryProp);

        /****QUANTITY TYPE*****/
        SDOType quantityType = new SDOType(uri, "quantityType");
        quantityType.setDataType(true);
        quantityType.getBaseTypes().add(intType);
        quantityType.setInstanceClassName("java.lang.Integer");

        /****SKU TYPE*****/
        SDOType SKUType = new SDOType(uri, "SKU");
        SKUType.setDataType(true);
        SKUType.getBaseTypes().add(stringType);
        SKUType.setInstanceClassName("java.lang.String");

        /****PHONE TYPE*****/
        SDOType phoneType = new SDOType(uri, "phoneNumber");
        phoneType.setDataType(true);
        phoneType.getBaseTypes().add(stringType);
        phoneType.setInstanceClassName("java.lang.String");

        /****NAME PREFIX TYPE*****/
        SDOType namePrefixType = new SDOType(uri, "namePrefix");
        namePrefixType.setDataType(true);
        namePrefixType.getBaseTypes().add(stringType);
        namePrefixType.setInstanceClassName("java.lang.String");

        /****GENDER TYPE*****/
        SDOType genderType = new SDOType(uri, "gender");
        genderType.setDataType(true);
        genderType.getBaseTypes().add(stringType);
        genderType.setInstanceClassName("java.lang.String");

        /****ITEM TYPE*****/
        SDOType itemType = new SDOType(uri, "LineItemType");
        itemType.setDataType(false);

        SDOProperty productNameProp = new SDOProperty(aHelperContext);
        productNameProp.setName("productName");
        productNameProp.setType(stringType);
        productNameProp.setXsd(true);
        //productNameProp.setElement(true);
        productNameProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        productNameProp.setContainment(true);
        itemType.getDeclaredProperties().add(productNameProp);

        SDOProperty partNumProp = new SDOProperty(aHelperContext);
        partNumProp.setName("partNum");
        partNumProp.setType(SKUType);
        partNumProp.setXsd(true);
        //partNumProp.setAttribute(true);
        itemType.getDeclaredProperties().add(partNumProp);

        SDOProperty quantityProp = new SDOProperty(aHelperContext);
        quantityProp.setName("quantity");
        quantityProp.setType(quantityType);
        quantityProp.setXsd(true);
        //quantityProp.setAttribute(true);
        //quantityProp.setContainment(true);
        itemType.getDeclaredProperties().add(quantityProp);

        SDOProperty USPriceProp = new SDOProperty(aHelperContext);
        USPriceProp.setName("USPrice");
        USPriceProp.setType(floatType);
        USPriceProp.setXsd(true);
        USPriceProp.setContainment(true);
        //USPriceProp.setElement(true);
        USPriceProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        itemType.getDeclaredProperties().add(USPriceProp);

        SDOProperty CDNPriceProp = new SDOProperty(aHelperContext);
        CDNPriceProp.setName("CDNPrice");
        CDNPriceProp.setType(floatType);
        CDNPriceProp.setXsd(true);
        //CDNPriceProp.setElement(true);
        CDNPriceProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        CDNPriceProp.setContainment(true);
        itemType.getDeclaredProperties().add(CDNPriceProp);

        SDOProperty exchangeProp = new SDOProperty(aHelperContext);
        exchangeProp.setName("exchangeRate");
        exchangeProp.setType(floatType);
        exchangeProp.setXsd(true);
        //exchangeProp.setElement(true);
        exchangeProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        exchangeProp.setContainment(true);
        itemType.getDeclaredProperties().add(exchangeProp);

        SDOProperty shipDateProp = new SDOProperty(aHelperContext);
        shipDateProp.setName("shipDate");
        //shipDateProp.setType(dateType);
        shipDateProp.setType(SDOConstants.SDO_YEARMONTHDAY);
        shipDateProp.setContainment(true);
        //shipDateProp.setElement(true);
        shipDateProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        shipDateProp.setXsd(true);
        itemType.getDeclaredProperties().add(shipDateProp);

        SDOProperty itemCommentProp = new SDOProperty(aHelperContext);
        itemCommentProp.setName("comment");
        itemCommentProp.setType(stringType);
        itemCommentProp.setContainment(true);
        //itemCommentProp.setElement(true);
        itemCommentProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        itemCommentProp.setXsd(true);
        itemCommentProp.setContainment(true);
        itemType.getDeclaredProperties().add(itemCommentProp);

        /****ITEMS TYPE*****/
        SDOType itemsType = new SDOType(uri, "Items");
        itemsType.setDataType(false);

        SDOProperty itemProp = new SDOProperty(aHelperContext);
        itemProp.setName("item");
        itemProp.setContainment(true);
        itemProp.setMany(true);
        itemProp.setContainingType(itemsType);
        itemProp.setType(itemType);
        itemsType.getDeclaredProperties().add(itemProp);

        /****CUSTOMER TYPE*****/
        SDOType customerType = new SDOType(uri, "CustomerType");
        customerType.setDataType(false);

        SDOProperty nameProp = new SDOProperty(aHelperContext);
        nameProp.setName("name");
        nameProp.setType(stringType);
        nameProp.setXsd(true);
        //nameProp.setElement(true);
        nameProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        customerType.getDeclaredProperties().add(nameProp);

        SDOProperty genderProp = new SDOProperty(aHelperContext);
        genderProp.setName("gender");
        genderProp.setType(genderType);
        //genderProp.setType(stringType);             
        genderProp.setXsd(true);
        //genderProp.setElement(true);
        genderProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        genderProp.setContainment(true);
        customerType.getDeclaredProperties().add(genderProp);

        SDOProperty namePrefixProp = new SDOProperty(aHelperContext);
        namePrefixProp.setName("namePrefix");
        //TODO or string type??
        namePrefixProp.setType(namePrefixType);
        namePrefixProp.setXsd(true);
        //namePrefixProp.setAttribute(true);
        //namePrefixProp.setContainment(true);
        customerType.getDeclaredProperties().add(namePrefixProp);

        SDOProperty phoneProp = new SDOProperty(aHelperContext);
        phoneProp.setName("phoneNumber");
        //TODO or intType type??
        phoneProp.setType(phoneType);
        phoneProp.setXsd(true);
        //phoneProp.setElement(true);
        phoneProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        phoneProp.setContainment(true);
        customerType.getDeclaredProperties().add(phoneProp);

        /****PURCHASEORDER TYPE*****/
        SDOProperty shipToProp = new SDOProperty(aHelperContext);
        shipToProp.setName("shipTo");
        shipToProp.setContainment(true);
        shipToProp.setType(addrType);

        SDOProperty billToProp = new SDOProperty(aHelperContext);
        billToProp.setName("billTo");
        billToProp.setContainment(true);
        billToProp.setType(addrType);

        SDOProperty commentProp = new SDOProperty(aHelperContext);
        commentProp.setName("comment");
        commentProp.setType(stringType);
        commentProp.setContainment(true);

        SDOProperty itemsProp = new SDOProperty(aHelperContext);
        itemsProp.setName("items");
        itemsProp.setContainment(true);
        itemsProp.setType(itemsType);

        SDOProperty customerProp = new SDOProperty(aHelperContext);
        customerProp.setName("customer");
        customerProp.setType(customerType);
        customerProp.setContainment(true);

        SDOProperty poIdProp = new SDOProperty(aHelperContext);
        poIdProp.setName("poId");
        poIdProp.setType(stringType);
        poIdProp.setXsd(true);
        //poIdProp.setAttribute(true);
        //poIdProp.setContainment(true);

        SDOProperty orderDateProp = new SDOProperty(aHelperContext);
        orderDateProp.setName("orderDate");        
        orderDateProp.setType(SDOConstants.SDO_YEARMONTHDAY);
        orderDateProp.setXsd(true);
        //orderDateProp.setAttribute(true);
        //orderDateProp.setContainment(true);

        SDOType POtype = new SDOType(uri, "PurchaseOrderType");
        POtype.setDataType(false);
        POtype.getDeclaredProperties().add(shipToProp);
        POtype.getDeclaredProperties().add(billToProp);
        POtype.getDeclaredProperties().add(commentProp);
        POtype.getDeclaredProperties().add(itemsProp);
        POtype.getDeclaredProperties().add(poIdProp);
        POtype.getDeclaredProperties().add(orderDateProp);
        POtype.getDeclaredProperties().add(customerProp);

types.add(customerType);
        types.add(quantityType);
        types.add(SKUType);
        types.add(genderType);
        types.add(namePrefixType);
        types.add(phoneType);
        types.add(addrType);
        
        types.add(usAddressType);
        types.add(cdnAddressType);
        types.add(cdnMailingAddressType);                
        types.add(itemType);        
        types.add(POtype);
        types.add(itemsType);

        return types;
    }
}
