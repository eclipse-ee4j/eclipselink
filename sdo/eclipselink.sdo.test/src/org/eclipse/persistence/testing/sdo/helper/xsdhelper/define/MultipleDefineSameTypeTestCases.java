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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define;

import commonj.sdo.Type;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.internal.helper.ClassConstants;

public class MultipleDefineSameTypeTestCases extends XSDHelperDefineTestCases {
    public MultipleDefineSameTypeTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xsdhelper.define.MultipleDefineSameTypeTestCases" };
        TestRunner.main(arguments);
    }

    public String getSchemaToDefine() {
        return "org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrder.xsd";
    }

    public void testDefineSameSchemaTwice() {
        InputStream is = getSchemaInputStream(getSchemaToDefine());
        List types = xsdHelper.define(is, null);
        int declPropsSize = typeHelper.getType("http://www.example.org", "PurchaseOrderType").getDeclaredProperties().size();
        List controlTypes = getControlTypes();
        compare(controlTypes, types);

        InputStream is2 = getSchemaInputStream(getSchemaToDefine());
        List types2 = xsdHelper.define(is2, null);

        int newSize = typeHelper.getType("http://www.example.org", "PurchaseOrderType").getDeclaredProperties().size();
        assertEquals(declPropsSize, newSize);
        assertEquals(0, types2.size());
        //TODO: update when SDO113 is resolved

        /*
        assertEquals(controlTypes.size(), types2.size());
        compare(getControlTypes(), types2);

        for (int i = 0; i < types.size(); i++) {
            Type type1 = (Type)types.get(i);
            Type type2 = (Type)types2.get(i);
            assertEquals(type1, type2);
        }*/
    }

    public void testDefineDifferentSchemaTwice() {
        InputStream is = getSchemaInputStream(getSchemaToDefine());
        List types = xsdHelper.define(is, null);
        compare(getControlTypes(), types);

        int declPropsSize = typeHelper.getType("http://www.example.org", "PurchaseOrderType").getDeclaredProperties().size();

        //this schema looks the same as the original except has 1 extra change summary property on PurchaseOrder
        InputStream is2 = getSchemaInputStream("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderWChangeSummary.xsd");
        List types2 = xsdHelper.define(is2, null);
        int newSize = typeHelper.getType("http://www.example.org", "PurchaseOrderType").getDeclaredProperties().size();
        assertEquals(declPropsSize, newSize);
        assertEquals(0, types2.size());

        //else SDO113      

        /*
        assertEquals(controlTypes.size(), types2.size());
        compare(getControlTypes(), types2);

        for (int i = 0; i < types.size(); i++) {
            Type type1 = (Type)types.get(i);
            Type type2 = (Type)types2.get(i);
            assertEquals(type1, type2);
        }*/
    }

    // THIS TEST CAN BE UNCOMMENTED AFTER A FIX FOR BUG# 245444 IS SUBMITTED.  CHANGES WILL BE REQUIRED...
    
    /*
    public void testDefineThreeDifferenceSchemas() {
        InputStream is = getSchemaInputStream(getSchemaToDefine());
        List types = xsdHelper.define(is, null);
        compare(getControlTypes(), types);
        int declPropsSize = typeHelper.getType("http://www.example.org", "PurchaseOrderType").getDeclaredProperties().size();

        InputStream is2 = getSchemaInputStream("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderWChangeSummary.xsd");
        List types2 = xsdHelper.define(is2, null);
        int newSize = typeHelper.getType("http://www.example.org", "PurchaseOrderType").getDeclaredProperties().size();
        assertEquals(declPropsSize, newSize);
        assertEquals(0, types2.size());

        //else SDO113      

        
        //assertEquals(controlTypes.size(), types2.size());
        //compare(getControlTypes(), types2);

        //for (int i = 0; i < types.size(); i++) {
        //    Type type1 = (Type)types.get(i);
        //    Type type2 = (Type)types2.get(i);
        //    assertEquals(type1, type2);
        //}

        //this schema is more complex and some new types will be defined but some old ones will be found
        InputStream is3 = getSchemaInputStream("org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderComplex.xsd");
        List types3 = xsdHelper.define(is3, null);
        assertEquals(declPropsSize, newSize);
        assertEquals(9, types3.size());
        compare(getComplexControlTypes(), types3);

        
        //assertEquals(types.size(), types2.size());
        //for (int i = 0; i < types.size(); i++) {
        //    Type type1 = (Type)types.get(i);
        //    Type type2 = (Type)types2.get(i);
        //    assertEquals(type1, type2);
        //}
    }*/

    public List getControlTypes() {
        List types = new ArrayList();
        String uri = "http://www.example.org";
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type intType = typeHelper.getType("commonj.sdo", "Int");
        Type yearMonthDayType = typeHelper.getType("commonj.sdo", "YearMonthDay");
        Type floatType = typeHelper.getType("commonj.sdo", "Float");

        /****ADDRESS TYPE*****/
        String javaPackage = "org.example";

        //ADDRESS TYPE
        SDOType addrType = new SDOType(uri, "AddressType");
        addrType.setDataType(false);
        addrType.setInstanceClassName(javaPackage + "." + "AddressType");
        addrType.setXsd(true);
        addrType.setXsdLocalName("AddressType");
        SDOProperty addrNameProp = new SDOProperty(aHelperContext);

        //addrNameProp.setAttribute(false);
        addrNameProp.setName("name");
        addrNameProp.setXsdLocalName("name");
        addrNameProp.setXsd(true);
        addrNameProp.setContainingType(addrType);
        addrNameProp.setType(stringType);
        addrNameProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        addrType.addDeclaredProperty(addrNameProp);

        SDOProperty streetProp = new SDOProperty(aHelperContext);
        streetProp.setName("street");
        streetProp.setXsdLocalName("street");
        streetProp.setXsd(true);
        streetProp.setContainingType(addrType);
        streetProp.setType(stringType);
        streetProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        //streetProp.setAttribute(false);
        addrType.addDeclaredProperty(streetProp);

        SDOProperty cityProp = new SDOProperty(aHelperContext);
        cityProp.setName("city");
        cityProp.setType(stringType);
        //cityProp.setAttribute(false);
        cityProp.setXsdLocalName("city");
        cityProp.setXsd(true);
        cityProp.setContainingType(addrType);
        cityProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        addrType.addDeclaredProperty(cityProp);

        SDOProperty stateProp = new SDOProperty(aHelperContext);
        stateProp.setName("state");
        stateProp.setXsdLocalName("state");
        stateProp.setXsd(true);
        stateProp.setContainingType(addrType);
        stateProp.setType(stringType);
        stateProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        //stateProp.setAttribute(false);
        addrType.addDeclaredProperty(stateProp);

        SDOProperty zipProp = new SDOProperty(aHelperContext);
        zipProp.setName("zip");
        zipProp.setXsdLocalName("zip");
        zipProp.setXsd(true);
        zipProp.setContainingType(addrType);
        zipProp.setType(stringType);
        zipProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        //zipProp.setAttribute(false);
        addrType.addDeclaredProperty(zipProp);

        SDOProperty countryProp = new SDOProperty(aHelperContext);
        countryProp.setName("country");
        countryProp.setXsdLocalName("country");
        countryProp.setXsd(true);
        countryProp.setContainingType(addrType);
        countryProp.setType(stringType);
        //countryProp.setAttribute(true);
        countryProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.FALSE);
        addrType.addDeclaredProperty(countryProp);

        /****ITEM TYPE*****/
        SDOType itemType = new SDOType(uri, "LineItemType");
        itemType.setDataType(false);
        itemType.setInstanceClassName(javaPackage + "." + "LineItemType");
        itemType.setXsd(true);
        itemType.setXsdLocalName("LineItemType");

        SDOProperty productNameProp = new SDOProperty(aHelperContext);
        productNameProp.setName("productName");
        productNameProp.setXsdLocalName("productName");
        productNameProp.setXsd(true);
        productNameProp.setContainingType(itemType);
        productNameProp.setType(stringType);
        //productNameProp.setAttribute(false);
        //productNameProp.setElement(true);
        productNameProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        itemType.addDeclaredProperty(productNameProp);

        SDOProperty quantityProp = new SDOProperty(aHelperContext);
        quantityProp.setName("quantity");
        quantityProp.setXsdLocalName("quantity");
        quantityProp.setXsd(true);
        quantityProp.setContainingType(itemType);
        quantityProp.setType(intType);
        //quantityProp.setAttribute(false);
        //quantityProp.setElement(true);
        quantityProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        itemType.addDeclaredProperty(quantityProp);

        SDOProperty USPriceProp = new SDOProperty(aHelperContext);
        USPriceProp.setName("USPrice");
        USPriceProp.setType(floatType);
        USPriceProp.setXsdLocalName("USPrice");
        USPriceProp.setXsd(true);
        USPriceProp.setContainingType(itemType);
        //USPriceProp.setAttribute(false);
        //USPriceProp.setElement(true);
        USPriceProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        itemType.addDeclaredProperty(USPriceProp);

        SDOProperty shipDateProp = new SDOProperty(aHelperContext);
        shipDateProp.setName("shipDate");
        shipDateProp.setType(yearMonthDayType);
        shipDateProp.setContainment(false);
        //shipDateProp.setAttribute(false);
        //shipDateProp.setElement(true);
        shipDateProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        shipDateProp.setXsdLocalName("shipDate");
        shipDateProp.setXsd(true);
        shipDateProp.setContainingType(itemType);
        itemType.addDeclaredProperty(shipDateProp);

        SDOProperty itemCommentProp = new SDOProperty(aHelperContext);
        itemCommentProp.setName("comment");
        itemCommentProp.setType(stringType);
        itemCommentProp.setContainment(false);
        //itemCommentProp.setAttribute(false);
        //itemCommentProp.setElement(true);
        itemCommentProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);

        itemCommentProp.setXsdLocalName("comment");
        itemCommentProp.setXsd(true);
        itemCommentProp.setContainingType(itemType);
        itemType.addDeclaredProperty(itemCommentProp);

        SDOProperty partNumProp = new SDOProperty(aHelperContext);
        partNumProp.setName("partNum");
        partNumProp.setType(stringType);
        //partNumProp.setAttribute(true);
        partNumProp.setXsdLocalName("partNum");
        partNumProp.setXsd(true);
        partNumProp.setContainingType(itemType);
        itemType.addDeclaredProperty(partNumProp);

        /****ITEMS TYPE*****/
        SDOType itemsType = new SDOType(uri, "Items");
        itemsType.setDataType(false);
        itemsType.setInstanceClassName(javaPackage + "." + "Items");
        itemsType.setXsd(true);
        itemsType.setXsdLocalName("Items");

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
        itemsType.addDeclaredProperty(itemProp);

        /****PURCHASEORDER TYPE*****/
        SDOType POtype = new SDOType(uri, "PurchaseOrderType");
        POtype.setDataType(false);
        POtype.setInstanceClassName(javaPackage + "." + "PurchaseOrderType");
        POtype.setXsd(true);
        POtype.setXsdLocalName("PurchaseOrderType");

        SDOProperty idProp = new SDOProperty(aHelperContext);
        idProp.setName("poId");
        idProp.setXsdLocalName("poId");
        idProp.setXsd(true);
        idProp.setContainingType(POtype);
        idProp.setType(stringType);
        //idProp.setAttribute(false);
        //idProp.setElement(true);
        idProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        POtype.addDeclaredProperty(idProp);

        SDOProperty shipToProp = new SDOProperty(aHelperContext);
        shipToProp.setName("shipTo");
        shipToProp.setContainment(true);
        //shipToProp.setElement(true);
        shipToProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        shipToProp.setType(addrType);
        shipToProp.setXsdLocalName("shipTo");
        shipToProp.setXsd(true);
        shipToProp.setContainingType(POtype);

        SDOProperty billToProp = new SDOProperty(aHelperContext);
        billToProp.setName("billTo");
        billToProp.setContainment(true);
        //billToProp.setElement(true);
        billToProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        billToProp.setType(addrType);
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
        //commentProp.setAttribute(false);
        //commentProp.setElement(true);
        commentProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
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

        POtype.addDeclaredProperty(shipToProp);
        POtype.addDeclaredProperty(billToProp);
        POtype.addDeclaredProperty(itemsProp);
        POtype.addDeclaredProperty(commentProp);
        POtype.addDeclaredProperty(orderDateProp);

        types.add(itemsType);
        types.add(POtype);
        types.add(itemType);
        types.add(addrType);

        return types;
    }

    public List getComplexControlTypes() {
        java.util.List types = new ArrayList();
        String uri = "http://www.example.org";
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type intType = typeHelper.getType("commonj.sdo", "Int");
        Type yearMonthDayType = typeHelper.getType("commonj.sdo", "YearMonthDay");
        Type floatType = typeHelper.getType("commonj.sdo", "Float");
        String javaPackage = "org.example";

        /****ADDRESS TYPE*****/

        //ADDRESS TYPE

        /* SDOType addrType = new SDOType(uri, "AddressType");
         addrType.setDataType(false);
         addrType.setInstanceClassName(javaPackage+"."+"AddressType");

         SDOProperty addrNameProp = new SDOProperty(aHelperContext);
         addrNameProp.setName("name");
         addrNameProp.setType(stringType);
         addrNameProp.setXsd(true);
         addrNameProp.setXsdLocalName("name");
         addrNameProp.setElement(true);
         addrNameProp.setContainment(true);
         addrNameProp.setContainingType(addrType);
         addrType.getDeclaredProperties().add(addrNameProp);

         SDOProperty streetProp = new SDOProperty(aHelperContext);
         streetProp.setName("street");
         streetProp.setType(stringType);
         streetProp.setXsd(true);
         streetProp.setXsdLocalName("street");
         streetProp.setElement(true);
         streetProp.setContainment(true);
         streetProp.setContainingType(addrType);
         addrType.getDeclaredProperties().add(streetProp);

         SDOProperty cityProp = new SDOProperty(aHelperContext);
         cityProp.setName("city");
         cityProp.setType(stringType);
         cityProp.setXsd(true);
         cityProp.setXsdLocalName("city");
         cityProp.setElement(true);
         cityProp.setContainment(true);
         cityProp.setContainingType(addrType);
         addrType.getDeclaredProperties().add(cityProp);

         SDOProperty countryProp = new SDOProperty(aHelperContext);
         countryProp.setName("country");
         countryProp.setType(stringType);
         countryProp.setXsdLocalName("country");
         countryProp.setXsd(true);
         countryProp.setAttribute(true);
         countryProp.setDefault("US");
         countryProp.setContainment(true);
         countryProp.setContainingType(addrType);
         addrType.getDeclaredProperties().add(countryProp);
        */
        Type addrType = typeHelper.getType(uri, "AddressType");

        /****US ADDRESS TYPE*****/
        SDOType usAddressType = new SDOType(uri, "usAddressType");
        usAddressType.setDataType(false);
        usAddressType.setInstanceClassName(javaPackage + "." + "UsAddressType");
        usAddressType.setXsd(true);
        usAddressType.setXsdLocalName("usAddressType");
        ArrayList baseTypes = new ArrayList();
        baseTypes.add(addrType);
        usAddressType.setBaseTypes(baseTypes);

        /****CDN ADDRESS TYPE*****/
        SDOType cdnAddressType = new SDOType(uri, "cdnAddressType");
        cdnAddressType.setDataType(false);
        cdnAddressType.setInstanceClassName(javaPackage + "." + "CdnAddressType");
        cdnAddressType.setXsd(true);
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
        cdnAddressType.addDeclaredProperty(postalCodeProp);

        SDOProperty provinceProp = new SDOProperty(aHelperContext);
        provinceProp.setName("province");
        provinceProp.setType(stringType);
        provinceProp.setXsd(true);
        provinceProp.setXsdLocalName("province");
        //provinceProp.setElement(true);
        provinceProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        provinceProp.setContainment(true);
        provinceProp.setContainingType(cdnAddressType);
        cdnAddressType.addDeclaredProperty(provinceProp);

        SDOProperty territoryProp = new SDOProperty(aHelperContext);
        territoryProp.setName("territory");
        territoryProp.setXsdLocalName("territory");
        territoryProp.setType(stringType);
        territoryProp.setXsd(true);
        //territoryProp.setElement(true);
        territoryProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        territoryProp.setContainment(true);
        territoryProp.setContainingType(cdnAddressType);
        cdnAddressType.addDeclaredProperty(territoryProp);

        /****QUANTITY TYPE*****/
        SDOType quantityType = new SDOType(uri, "quantityType");
        quantityType.setDataType(true);
        quantityType.getBaseTypes().add(intType);
        quantityType.setXsd(true);
        quantityType.setXsdLocalName("quantityType");
        //quantityType.setInstanceClassName("java.lang.Integer");
        quantityType.setInstanceClassName(ClassConstants.PINT.getName());

        /****SKU TYPE*****/
        SDOType SKUType = new SDOType(uri, "SKU");
        SKUType.setDataType(true);
        SKUType.setXsd(true);
        SKUType.setXsdLocalName("SKU");
        SKUType.getBaseTypes().add(stringType);
        SKUType.setInstanceClassName("java.lang.String");

        /****PHONE TYPE*****/
        SDOType phoneType = new SDOType(uri, "phoneNumber");
        phoneType.setXsd(true);
        phoneType.setXsdLocalName("phoneNumber");

        phoneType.setDataType(true);
        phoneType.getBaseTypes().add(stringType);
        phoneType.setInstanceClassName("java.lang.String");

        /****NAME PREFIX TYPE*****/
        SDOType namePrefixType = new SDOType(uri, "namePrefix");

        //namePrefixType.setDataType(true);
        namePrefixType.setDataType(false);
        namePrefixType.setXsd(true);
        namePrefixType.setXsdLocalName("namePrefix");

        namePrefixType.getBaseTypes().add(stringType);
        namePrefixType.setInstanceClassName("java.lang.String");

        /****GENDER TYPE*****/
        SDOType genderType = new SDOType(uri, "gender");
        genderType.setDataType(true);
        genderType.setXsd(true);
        genderType.setXsdLocalName("gender");
        genderType.getBaseTypes().add(stringType);
        genderType.setInstanceClassName("java.lang.String");

        /****ITEM TYPE*****/

        /*
        SDOType itemType = new SDOType(uri, "LineItemType");
        itemType.setDataType(false);
        itemType.setInstanceClassName(javaPackage+"."+"LineItemType");

        SDOProperty productNameProp = new SDOProperty(aHelperContext);
        productNameProp.setName("productName");
        productNameProp.setXsdLocalName("productName");
        productNameProp.setType(stringType);
        productNameProp.setXsd(true);
        productNameProp.setElement(true);
        productNameProp.setContainment(true);
        productNameProp.setContainingType(itemType);
        itemType.getDeclaredProperties().add(productNameProp);

        SDOProperty partNumProp = new SDOProperty(aHelperContext);
        partNumProp.setName("partNum");
        partNumProp.setXsdLocalName("partNum");
        partNumProp.setType(SKUType);
        partNumProp.setXsd(true);
        partNumProp.setAttribute(true);
        partNumProp.setContainingType(itemType);
        itemType.getDeclaredProperties().add(partNumProp);

        SDOProperty quantityProp = new SDOProperty(aHelperContext);
        quantityProp.setName("quantity");
        quantityProp.setType(quantityType);
        quantityProp.setXsd(true);
        quantityProp.setXsdLocalName("quantity");
        quantityProp.setAttribute(true);
        quantityProp.setContainment(true);
        quantityProp.setContainingType(itemType);
        itemType.getDeclaredProperties().add(quantityProp);

        SDOProperty USPriceProp = new SDOProperty(aHelperContext);
        USPriceProp.setName("USPrice");
        USPriceProp.setXsdLocalName("USPrice");
        USPriceProp.setType(floatType);
        USPriceProp.setXsd(true);
        USPriceProp.setContainment(true);
        USPriceProp.setElement(true);
        USPriceProp.setContainingType(itemType);
        itemType.getDeclaredProperties().add(USPriceProp);

        SDOProperty CDNPriceProp = new SDOProperty(aHelperContext);
        CDNPriceProp.setName("CDNPrice");
        CDNPriceProp.setXsdLocalName("CDNPrice");
        CDNPriceProp.setType(floatType);
        CDNPriceProp.setXsd(true);
        CDNPriceProp.setElement(true);
        CDNPriceProp.setContainment(true);
        CDNPriceProp.setContainingType(itemType);
        itemType.getDeclaredProperties().add(CDNPriceProp);

        SDOProperty exchangeProp = new SDOProperty(aHelperContext);
        exchangeProp.setName("exchangeRate");
        exchangeProp.setXsdLocalName("exchangeRate");
        exchangeProp.setType(floatType);
        exchangeProp.setXsd(true);
        exchangeProp.setElement(true);
        exchangeProp.setContainment(true);
        exchangeProp.setContainingType(itemType);
        itemType.getDeclaredProperties().add(exchangeProp);

        SDOProperty shipDateProp = new SDOProperty(aHelperContext);
        shipDateProp.setName("shipDate");
        shipDateProp.setXsdLocalName("shipDate");
        shipDateProp.setType(yearMonthDayType);
        shipDateProp.setContainment(true);
        shipDateProp.setElement(true);
        shipDateProp.setContainingType(itemType);
        shipDateProp.setXsd(true);

        SDOProperty itemCommentProp = new SDOProperty(aHelperContext);
        itemCommentProp.setName("comment");
        itemCommentProp.setXsdLocalName("comment");
        itemCommentProp.setType(stringType);
        itemCommentProp.setContainment(true);
        itemCommentProp.setElement(true);
        itemCommentProp.setXsd(true);
        itemCommentProp.setContainment(true);
        itemCommentProp.setContainingType(itemType);
        itemType.getDeclaredProperties().add(itemCommentProp);

        itemType.getDeclaredProperties().add(shipDateProp);
        */

        //Type itemType = typeHelper.getType(uri, "LineItemType");

        /****ITEMS TYPE*****/

        /*  SDOType itemsType = new SDOType(uri, "Items");
          itemsType.setDataType(false);
          itemsType.setInstanceClassName(javaPackage+"."+"Items");
          SDOProperty itemProp = new SDOProperty(aHelperContext);
          itemProp.setName("item");
          itemProp.setXsdLocalName("item");
          itemProp.setContainment(true);
          itemProp.setXsd(true);
          itemProp.setElement(true);
          itemProp.setMany(true);
          itemProp.setContainingType(itemsType);
          itemProp.setType(itemType);
          itemsType.getDeclaredProperties().add(itemProp);
          */

        // Type itemsType = typeHelper.getType(uri, "Items");

        /****CUSTOMER TYPE*****/
        SDOType customerType = new SDOType(uri, "CustomerType");
        customerType.setDataType(false);
        customerType.setXsd(true);
        customerType.setXsdLocalName("CustomerType");
        customerType.setInstanceClassName(javaPackage + "." + "CustomerType");

        SDOProperty nameProp = new SDOProperty(aHelperContext);
        nameProp.setName("name");
        nameProp.setXsdLocalName("name");
        nameProp.setType(stringType);
        nameProp.setXsd(true);
        //nameProp.setElement(true);
        nameProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        nameProp.setContainingType(customerType);
        customerType.addDeclaredProperty(nameProp);

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
        customerType.addDeclaredProperty(genderProp);

        SDOProperty namePrefixProp = new SDOProperty(aHelperContext);
        namePrefixProp.setName("namePrefix");
        //TODO or string type??
        namePrefixProp.setXsdLocalName("namePrefix");
        namePrefixProp.setType(namePrefixType);
        namePrefixProp.setXsd(true);
        //namePrefixProp.setAttribute(true);        
        namePrefixProp.setContainingType(customerType);
        customerType.addDeclaredProperty(namePrefixProp);

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
        customerType.addDeclaredProperty(phoneProp);

        /****PURCHASEORDER TYPE*****/
        /*
        SDOType POtype = new SDOType(uri, "PurchaseOrderType");
        POtype.setInstanceClassName(javaPackage+"."+"PurchaseOrderType");
        POtype.setDataType(false);

        SDOProperty shipToProp = new SDOProperty(aHelperContext);
        shipToProp.setName("shipTo");
        shipToProp.setXsdLocalName("shipTo");
        shipToProp.setContainment(true);
        shipToProp.setElement(true);
        shipToProp.setXsd(true);
        shipToProp.setContainingType(POtype);
        shipToProp.setType(addrType);

        SDOProperty billToProp = new SDOProperty(aHelperContext);
        billToProp.setName("billTo");
        billToProp.setXsdLocalName("billTo");
        billToProp.setContainment(true);
        billToProp.setElement(true);
        billToProp.setXsd(true);
        billToProp.setContainingType(POtype);
        billToProp.setType(addrType);

        SDOProperty commentProp = new SDOProperty(aHelperContext);
        commentProp.setName("comment");
        commentProp.setXsdLocalName("comment");
        commentProp.setType(stringType);
        commentProp.setElement(true);
        commentProp.setXsd(true);
        commentProp.setContainingType(POtype);
        commentProp.setContainment(true);

        SDOProperty itemsProp = new SDOProperty(aHelperContext);
        itemsProp.setName("items");
        itemsProp.setXsdLocalName("items");
        itemsProp.setContainment(true);
        itemsProp.setElement(true);
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
        customerProp.setElement(true);

        SDOProperty poIdProp = new SDOProperty(aHelperContext);
        poIdProp.setName("poId");
        poIdProp.setXsdLocalName("poId");
        poIdProp.setType(stringType);
        poIdProp.setXsd(true);
        poIdProp.setAttribute(true);
        poIdProp.setContainingType(POtype);
        poIdProp.setContainment(true);

        SDOProperty orderDateProp = new SDOProperty(aHelperContext);
        orderDateProp.setName("orderDate");
        orderDateProp.setXsdLocalName("orderDate");
        orderDateProp.setType(yearMonthDayType);
        orderDateProp.setXsd(true);
        orderDateProp.setContainingType(POtype);
        orderDateProp.setAttribute(true);
        orderDateProp.setContainment(true);


        POtype.getDeclaredProperties().add(shipToProp);
        POtype.getDeclaredProperties().add(billToProp);
        POtype.getDeclaredProperties().add(commentProp);
        POtype.getDeclaredProperties().add(itemsProp);
        POtype.getDeclaredProperties().add(poIdProp);
        POtype.getDeclaredProperties().add(orderDateProp);
        POtype.getDeclaredProperties().add(customerProp);
        */
        
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
        cdnMailingAddressType.addDeclaredProperty(deliveryProp);
        
        types.add(quantityType);
        types.add(phoneType);
        types.add(cdnAddressType);        
        types.add(SKUType);
        types.add(usAddressType);
        //types.add(itemsType);
        //types.add(POtype);
        types.add(cdnMailingAddressType);
        types.add(customerType);
        types.add(genderType);
        //types.add(itemType);
        //types.add(addrType);
        types.add(namePrefixType);        

        return types;

    }
}
