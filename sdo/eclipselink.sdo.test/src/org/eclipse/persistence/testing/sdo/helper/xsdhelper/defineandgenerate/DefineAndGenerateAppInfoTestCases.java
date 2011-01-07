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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.defineandgenerate;

import commonj.sdo.Property;
import commonj.sdo.Type;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.DefaultSchemaLocationResolver;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class DefineAndGenerateAppInfoTestCases extends XSDHelperDefineAndGenerateTestCases {
    public DefineAndGenerateAppInfoTestCases(String name) {
        super(name);
    }

    public String getControlGeneratedFileName() {
        return "org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderAppInfoGenerated.xsd";
    }

    public String getSchemaToDefine() {
        return "org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderAppInfo.xsd";
    }

    public static void main(String[] args) {
        TestRunner.run(DefineAndGenerateAppInfoTestCases.class);
    }

    public void testDefineAndGenerate() throws Exception {
        InputStream is = getSchemaInputStream(getSchemaToDefine());
        List types = xsdHelper.define(is, getSchemaLocation());

        compareGeneratedTypes(getControlTypes(), types);

        DefaultSchemaLocationResolver resolver = new DefaultSchemaLocationResolver(getMap());
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(types, resolver);

        String controlSchema = getSchema(getControlGeneratedFileName());
        log("EXPECTED: \n" + controlSchema);
        log("ACTUAL: \n" + generatedSchema);

        StringReader reader = new StringReader(generatedSchema);
        InputSource inputSource = new InputSource(reader);
        Document generatedSchemaDoc = parser.parse(inputSource);
        reader.close();

        assertSchemaIdentical(getDocument(getControlGeneratedFileName()), generatedSchemaDoc);
    }

    public void testNullSource() {
        InputStream is = getSchemaInputStream(getSchemaToDefine());
        List types = xsdHelper.define(is, getSchemaLocation());

        compareGeneratedTypes(getControlTypes(), types);

        DefaultSchemaLocationResolver resolver = new DefaultSchemaLocationResolver(getMap());
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(types, resolver);

        Type itemsType = typeHelper.getType(getUri(), "Items");
        Property itemProp = itemsType.getProperty("item");

        String appInfoItemTest = xsdHelper.getAppinfo(itemProp, null);
    }

    protected String getUri() {
        return null;
    }

    public void compareGeneratedTypes(List controlTypes, List generatedTypes) {
        super.compareGeneratedTypes(controlTypes, generatedTypes);
        Type itemsType = typeHelper.getType(getUri(), "Items");
        Type addressType = typeHelper.getType(getUri(), "USAddress");
        Type quantityType = typeHelper.getType(getUri(), "quantityType");
        Property itemProp = itemsType.getProperty("item");
        Property streetProp = addressType.getProperty("street");
        Property zipProp = addressType.getProperty("zip");

        String zipSource = xsdHelper.getAppinfo(zipProp, "zipSource");

        /*System.out.println("--------------");
        System.out.println("zipSource:" + zipSource);
        String zipNullSource = xsdHelper.getAppinfo(zipProp, null);
        System.out.println("--------------");
        System.out.println("zipSource -null:" + zipNullSource);

        System.out.println("--------------");
        System.out.println("zipNotFoundSource:" + zipNotFoundSource);
        */
        String zipNotFoundSource = xsdHelper.getAppinfo(zipProp, "abc");
        assertNull(zipNotFoundSource);

        String appInfoItemTest = xsdHelper.getAppinfo(itemProp, "itemTest");

        String controlString = getTestControlString();

        String controlStringNoWS = removeWhiteSpaceFromString(controlString);
        String appInfoItemTestNoWS = removeWhiteSpaceFromString(appInfoItemTest);
        log("expected:" + controlStringNoWS);
        log("actual  :" + appInfoItemTestNoWS);
        assertEquals(controlStringNoWS, appInfoItemTestNoWS);

        String appInfoItemTest2 = xsdHelper.getAppinfo(itemProp, "itemTest2");
        String quantityTest = xsdHelper.getAppinfo(quantityType, "quantityTest");
        String quantityTest2 = xsdHelper.getAppinfo(quantityType, "quantityTest2");
        String addressTypeTest = xsdHelper.getAppinfo(addressType, "test");
        String streetTest = xsdHelper.getAppinfo(streetProp, "streetTest");
        String streetTest2 = xsdHelper.getAppinfo(streetProp, "streetTest2");
    }

    public List getControlTypes() {
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
        USaddrType.setInstanceClassName("abc.USAddress");

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

        /****QUANTITY TYPE*****/
        SDOType quantityType = new SDOType(uri, "quantityType");
        quantityType.setDataType(true);
        quantityType.setInstanceClass(ClassConstants.PINT);
        quantityType.getBaseTypes().add(intType);

        /****SKU TYPE*****/
        SDOType SKUType = new SDOType(uri, "SKU");
        SKUType.setInstanceClassName("java.lang.String");
        SKUType.setDataType(true);
        SKUType.getBaseTypes().add(stringType);

        /****ITEM TYPE*****/
        SDOType itemType = new SDOType(uri, "Item");
        itemType.setInstanceClassName("abc.Item");
        itemType.setDataType(false);

        SDOProperty productNameProp = new SDOProperty(aHelperContext);
        productNameProp.setName("productName");
        productNameProp.setXsd(true);
        productNameProp.setXsdLocalName("productName");
        productNameProp.setType(stringType);
        itemType.addDeclaredProperty(productNameProp);

        SDOProperty quantityProp = new SDOProperty(aHelperContext);
        quantityProp.setName("quantity");
        quantityProp.setXsd(true);
        quantityProp.setXsdLocalName("quantity");
        quantityProp.setType(quantityType);
        itemType.addDeclaredProperty(quantityProp);

        SDOProperty partNumProp = new SDOProperty(aHelperContext);
        partNumProp.setName("partNum");
        partNumProp.setXsd(true);
        partNumProp.setXsdLocalName("partNum");
        partNumProp.setType(SKUType);
        itemType.addDeclaredProperty(partNumProp);

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
        itemType.addDeclaredProperty(itemCommentProp);

        SDOProperty shipDateProp = new SDOProperty(aHelperContext);
        shipDateProp.setName("shipDate");
        shipDateProp.setXsd(true);
        shipDateProp.setXsdLocalName("shipDate");
        shipDateProp.setType(yearMonthDayType);
        shipDateProp.setContainment(false);
        itemType.addDeclaredProperty(shipDateProp);

        /****ITEMS TYPE*****/
        SDOType itemsType = new SDOType(uri, "Items");
        itemsType.setDataType(false);
        itemsType.setInstanceClassName("abc.Items");

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
        shipToProp.setType(USaddrType);

        SDOProperty billToProp = new SDOProperty(aHelperContext);
        billToProp.setName("billTo");
        billToProp.setXsd(true);
        billToProp.setXsdLocalName("billTo");
        billToProp.setContainment(true);
        billToProp.setType(USaddrType);

        SDOProperty itemsProp = new SDOProperty(aHelperContext);
        itemsProp.setName("items");
        itemsProp.setXsd(true);
        itemsProp.setXsdLocalName("items");
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
        orderDateProp.setType(yearMonthDayType);
        orderDateProp.setContainment(false);

        SDOType POtype = new SDOType(uri, "PurchaseOrder");
        POtype.setInstanceClassName("abc.PurchaseOrder");
        POtype.setDataType(false);
        POtype.addDeclaredProperty(shipToProp);
        POtype.addDeclaredProperty(billToProp);
        POtype.addDeclaredProperty(itemsProp);
        POtype.addDeclaredProperty(commentProp);
        POtype.addDeclaredProperty(orderDateProp);

        types.add(itemsType);
        types.add(USaddrType);
        types.add(SKUType);
        types.add(itemType);
        types.add(POtype);
        types.add(quantityType);

        return types;
    }

    protected String getTestControlString() {        
        String controlString = "<xsd:appinfo source=\"itemTest\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" + "\n" + "   <someTag>blah blah itemTest</someTag>" + "\n" + "</xsd:appinfo>" + "\n" + "<xsd:appinfo source=\"itemTest\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" + "\n" + "   <anotherTag>blah blah itemTest</anotherTag>" + "\n" + "</xsd:appinfo>";        
        return controlString;
    }
}
