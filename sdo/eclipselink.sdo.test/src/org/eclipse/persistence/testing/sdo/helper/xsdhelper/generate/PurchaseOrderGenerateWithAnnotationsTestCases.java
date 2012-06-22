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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate;

import commonj.sdo.Type;
import commonj.sdo.helper.DataFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.sdo.types.SDODataType;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class PurchaseOrderGenerateWithAnnotationsTestCases extends XSDHelperGenerateTestCases {
    public PurchaseOrderGenerateWithAnnotationsTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(PurchaseOrderGenerateWithAnnotationsTestCases.class);
    }

    public String getControlFileName() {
        return "org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderWithAnnotationsGenerated.xsd";
    }
    
    public String getControlFileNameDifferentOrder() {
        return "org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderWithAnnotationsGeneratedRoundTrip.xsd";
    }

    public List getTypesToGenerateFrom() {
        SDOType dataObjectType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);
        SDOProperty xmlDataTypeProperty = (SDOProperty) typeHelper.getOpenContentProperty(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_DATATYPE);

        List types = new ArrayList();
        String uri = getControlUri();
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type intType = typeHelper.getType("commonj.sdo", "Int");
        Type dateType = typeHelper.getType("commonj.sdo", "Date");
        Type decimalType = typeHelper.getType("commonj.sdo", "Decimal");
        String javaPackage = "com.example.myPackage";

        SDOType gregorianDateType = new SDODataType(uri, "MyGregorianDate", (SDOTypeHelper) typeHelper);
        gregorianDateType.setInstanceProperty(SDOConstants.JAVA_CLASS_PROPERTY, "java.sql.Time");
        List aliasNames = new ArrayList();
        aliasNames.add("TheGregorianDate");
        gregorianDateType.setAliasNames(aliasNames);

        /****ADDRESS TYPE*****/

        //ADDRESS TYPE
        SDOType USaddrType = new SDOType(uri, "USAddress");
        USaddrType.setDataType(false);

        SDOProperty addrNameProp = new SDOProperty(aHelperContext);
        addrNameProp.setXsd(true);
        addrNameProp.setXsdLocalName("name");
        //addrNameProp.setAttribute(true);
        addrNameProp.setName("name");
        addrNameProp.setType(stringType);
        addrNameProp.setContainingType(USaddrType);
        USaddrType.getDeclaredProperties().add(addrNameProp);

        SDOProperty streetProp = new SDOProperty(aHelperContext);
        streetProp.setName("street");
        streetProp.setXsd(true);
        streetProp.setXsdLocalName("street");
        streetProp.setType(stringType);
        //streetProp.setAttribute(true);
        streetProp.setContainingType(USaddrType);
        USaddrType.getDeclaredProperties().add(streetProp);

        SDOProperty cityProp = new SDOProperty(aHelperContext);
        cityProp.setName("city");
        cityProp.setXsdLocalName("city");
        cityProp.setXsd(true);
        cityProp.setType(stringType);
        //cityProp.setAttribute(true);
        cityProp.setContainingType(USaddrType);
        USaddrType.getDeclaredProperties().add(cityProp);

        SDOProperty stateProp = new SDOProperty(aHelperContext);
        stateProp.setName("state");
        stateProp.setXsd(true);
        stateProp.setXsdLocalName("state");
        stateProp.setType(stringType);
        //stateProp.setAttribute(true);
        stateProp.setContainingType(USaddrType);
        USaddrType.getDeclaredProperties().add(stateProp);

        SDOProperty zipProp = new SDOProperty(aHelperContext);
        zipProp.setXsd(true);
        zipProp.setName("zip");
        zipProp.setXsdLocalName("zip");
        zipProp.setType(decimalType);
        //zipProp.setAttribute(true);
        zipProp.setContainingType(USaddrType);
        USaddrType.getDeclaredProperties().add(zipProp);

        SDOProperty countryProp = new SDOProperty(aHelperContext);
        countryProp.setXsd(true);
        countryProp.setName("country");
        countryProp.setXsdLocalName("country");
        countryProp.setType(stringType);
        //countryProp.setAttribute(true);
        countryProp.setDefault("US");
        countryProp.setContainingType(USaddrType);
        USaddrType.getDeclaredProperties().add(countryProp);

        /****QUANTITY TYPE*****/
        SDOType quantityType = new SDODataType(uri, "quantityType", (SDOTypeHelper) typeHelper);
        quantityType.setXsdType(SDOConstants.ANY_TYPE_QNAME);
        quantityType.getBaseTypes().add(intType);
        quantityType.setInstanceClassName("java.lang.Integer");

        /****SKU TYPE*****/
        SDOType SKUType = new SDODataType(uri, "SKUSDO", (SDOTypeHelper) typeHelper);
        SKUType.setXsd(true);
        SKUType.setXsdLocalName("SKU");
        SKUType.setInstanceClassName("com.example.myPackage.SKU");
        SKUType.setInstanceProperty(SDOConstants.JAVA_CLASS_PROPERTY, "com.example.myPackage.SKU");

        /****ITEM TYPE*****/
        SDOType itemType = new SDOType(uri, "ItemSDO");
        itemType.setDataType(false);
        itemType.setInstanceClassName(javaPackage + "." + "ItemSDO");
        itemType.setXsdLocalName("Item");

        SDOProperty productNameProp = new SDOProperty(aHelperContext);
        productNameProp.setXsd(true);
        productNameProp.setName("productName");
        productNameProp.setXsdLocalName("productName");
        productNameProp.setType(stringType);
        productNameProp.setReadOnly(true);
        //productNameProp.setAttribute(true);
        productNameProp.setContainingType(itemType);
        itemType.getDeclaredProperties().add(productNameProp);

        SDOProperty pOrderProp = new SDOProperty(aHelperContext);
        pOrderProp.setXsd(true);
        pOrderProp.setName("porder");
        pOrderProp.setXsdLocalName("porder");
        pOrderProp.setType(stringType);
        //pOrderProp.setElement(true);        
        pOrderProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        pOrderProp.setMany(false);
        pOrderProp.setContainment(true);
        pOrderProp.setContainingType(itemType);
        itemType.getDeclaredProperties().add(pOrderProp);

        SDOProperty quantityProp = new SDOProperty(aHelperContext);
        quantityProp.setXsd(true);
        quantityProp.setName("quantity");
        quantityProp.setXsdLocalName("quantity");        
        quantityProp.setContainingType(itemType);          
        quantityProp.setInstanceProperty(xmlDataTypeProperty, SDOConstants.SDO_INTEGER);
        quantityProp.setType(SDOConstants.SDO_INTEGER);
        itemType.getDeclaredProperties().add(quantityProp);

        SDOProperty partNumProp = new SDOProperty(aHelperContext);
        partNumProp.setXsd(true);
        partNumProp.setName("partNumSDO");
        partNumProp.setXsdLocalName("partNum");
        partNumProp.setType(SKUType);
        //partNumProp.setAttribute(true);
        partNumProp.setContainingType(itemType);
        itemType.getDeclaredProperties().add(partNumProp);

        SDOProperty USPriceProp = new SDOProperty(aHelperContext);
        USPriceProp.setXsd(true);
        USPriceProp.setName("USPrice");
        USPriceProp.setXsdLocalName("USPrice");
        USPriceProp.setType(decimalType);
        //USPriceProp.setAttribute(true);
        USPriceProp.setContainingType(itemType);
        itemType.getDeclaredProperties().add(USPriceProp);

        SDOProperty itemCommentProp = new SDOProperty(aHelperContext);
        itemCommentProp.setXsd(true);
        itemCommentProp.setName("comment");
        itemCommentProp.setXsdLocalName("comment");
        itemCommentProp.setType(stringType);
        itemCommentProp.setContainment(false);
        //itemCommentProp.setAttribute(true);
        List names = new ArrayList();
        names.add("itemComment");
        itemCommentProp.setAliasNames(names);
        itemCommentProp.setContainingType(itemType);

        itemCommentProp.setContainment(false);
        itemType.getDeclaredProperties().add(itemCommentProp);

        SDOProperty shipDateProp = new SDOProperty(aHelperContext);
        shipDateProp.setXsd(true);
        shipDateProp.setName("shipDate");
        shipDateProp.setXsdLocalName("shipDate");
        //        shipDateProp.setType(yearMonthDayType);
        shipDateProp.setType(SDOConstants.SDO_YEARMONTHDAY);
        shipDateProp.setContainment(false);
        //shipDateProp.setAttribute(true);
        shipDateProp.setContainingType(itemType);
        itemType.getDeclaredProperties().add(shipDateProp);

        /****ITEMS TYPE*****/
        SDOType itemsType = new SDOType(uri, "Items");
        itemsType.setDataType(false);
        itemsType.setInstanceClassName(javaPackage + "." + "Items");

        SDOProperty itemProp = new SDOProperty(aHelperContext);
        itemProp.setXsd(true);
        itemProp.setName("item");
        itemProp.setXsdLocalName("item");
        itemProp.setContainment(true);
        itemProp.setMany(true);
        //itemProp.setContainingType(itemType);
        //itemProp.setElement(true);
        itemProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        itemProp.setContainingType(itemsType);
        itemProp.setType(itemType);
        itemsType.getDeclaredProperties().add(itemProp);

        /****PURCHASEORDER TYPE*****/
        SDOType POtype = new SDOType(uri, "PurchaseOrder");
        POtype.setDataType(false);
        POtype.setSequenced(true);
        names = new ArrayList();
        names.add("Purchase");
        POtype.setAliasNames(names);
        POtype.setInstanceClassName(javaPackage + "." + "PurchaseOrder");

        SDOProperty shipToProp = new SDOProperty(aHelperContext);
        shipToProp.setXsd(true);
        shipToProp.setName("shipTo");
        shipToProp.setMany(true);
        shipToProp.setXsdLocalName("shipTo");
        shipToProp.setContainment(true);
        names = new ArrayList();
        names.add("mailingAddress");
        shipToProp.setAliasNames(names);
        shipToProp.setMany(true);
        //shipToProp.setElement(true);
        shipToProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        shipToProp.setContainingType(POtype);
        shipToProp.setType(USaddrType);

        SDOProperty billToProp = new SDOProperty(aHelperContext);
        billToProp.setXsd(true);
        billToProp.setName("billToSDO");
        billToProp.setXsdLocalName("billTo");
        billToProp.setReadOnly(true);
        billToProp.setContainment(true);
        //billToProp.setElement(true);
        billToProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        billToProp.setContainingType(POtype);
        billToProp.setType(USaddrType);

        SDOProperty itemsProp = new SDOProperty(aHelperContext);
        itemsProp.setXsd(true);
        itemsProp.setName("items");
        itemsProp.setXsdLocalName("items");
        itemsProp.setContainment(true);
        //temsProp.setElement(true);
        itemsProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        itemsProp.setInstanceProperty(xmlDataTypeProperty, itemsType);
        itemsProp.setType(dataObjectType);
        itemsProp.setContainingType(POtype);

        SDOProperty commentProp = new SDOProperty(aHelperContext);
        commentProp.setXsd(true);
        commentProp.setName("comment");
        commentProp.setXsdLocalName("comment");
        commentProp.setType(stringType);
        //commentProp.setAttribute(true);
        commentProp.setContainingType(POtype);
        commentProp.setContainment(false);

        SDOProperty orderDateProp = new SDOProperty(aHelperContext);
        orderDateProp.setXsd(true);
        orderDateProp.setName("orderDate");
        orderDateProp.setXsdLocalName("orderDate");
        //orderDateProp.setType(yearMonthDayType);
        //orderDateProp.setAttribute(true);
        orderDateProp.setType(SDOConstants.SDO_YEARMONTHDAY);
        orderDateProp.setInstanceProperty(xmlDataTypeProperty, gregorianDateType);
        orderDateProp.setContainingType(POtype);
        orderDateProp.setContainment(false);

        SDOProperty topPriorityItemProp = new SDOProperty(aHelperContext);
        topPriorityItemProp.setXsd(true);
        topPriorityItemProp.setName("topPriorityItems");
        topPriorityItemProp.setXsdLocalName("topPriorityItems");
        topPriorityItemProp.setType(itemType);
        topPriorityItemProp.setMany(true);
        topPriorityItemProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        
        topPriorityItemProp.setContainment(true);
        topPriorityItemProp.setContainingType(POtype);

        POtype.getDeclaredProperties().add(shipToProp);
        POtype.getDeclaredProperties().add(billToProp);
        POtype.getDeclaredProperties().add(itemsProp);
        POtype.getDeclaredProperties().add(commentProp);
        POtype.getDeclaredProperties().add(orderDateProp);
        POtype.getDeclaredProperties().add(topPriorityItemProp);

        types.add(USaddrType);
        types.add(gregorianDateType);
        types.add(POtype);
        types.add(itemsType);
        types.add(itemType);
        types.add(quantityType);
        types.add(SKUType);

        return types;
    }

    public void testGenerateSchemaRoundTrip() throws Exception {
        org.eclipse.persistence.sdo.helper.DefaultSchemaLocationResolver resolver = new org.eclipse.persistence.sdo.helper.DefaultSchemaLocationResolver(getMap());
        resolver.setMap(getMap());
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
        InputStream is = getSchemaInputStream("org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderWithAnnotations.xsd");
        return ((SDOXSDHelper)xsdHelper).define(is, null);
    }

    public String getControlUri() {
        return null;
    }
}
