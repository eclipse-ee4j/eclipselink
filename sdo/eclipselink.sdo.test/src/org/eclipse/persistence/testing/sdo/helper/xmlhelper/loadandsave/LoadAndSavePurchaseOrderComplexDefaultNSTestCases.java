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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;

public class LoadAndSavePurchaseOrderComplexDefaultNSTestCases extends LoadAndSaveTestCases {
    public LoadAndSavePurchaseOrderComplexDefaultNSTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSavePurchaseOrderComplexDefaultNSTestCases" };
        TestRunner.main(arguments);
    }
    
    protected void verifyAfterLoad(XMLDocument document){
      super.verifyAfterLoad(document);
      DataObject rootDO = document.getRootObject();
      DataObject shipToDO = rootDO.getDataObject("shipTo");      
      String base64TestValueString = shipToDO.getString("base64Test");
      assertEquals("eHdmb3Rh", base64TestValueString);      
    }
    
   //override here to use a different verify after load method in this case
   public void testNoSchemaLoadFromInputStreamSaveDataObjectToString() throws Exception {
        registerTypes();
        FileInputStream inputStream = new FileInputStream(getNoSchemaControlFileName());
        XMLDocument document = xmlHelper.load(inputStream, null, getOptions());
        verifyAfterLoadNoSchema(document);
        StringWriter writer = new StringWriter();
        xmlHelper.save(document, writer, null);
        // Nodes will not be the same but XML output is
        compareXML(getNoSchemaControlWriteFileName(), writer.toString());//, false);
    }
    
    protected void verifyAfterLoadNoSchema(XMLDocument document){
      super.verifyAfterLoad(document);      
    }

    protected String getNoSchemaControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/schemas/purchaseOrderComplexNSNoSchema.xml";
    }

    protected String getControlDataObjectFileName() {
        return "./org/eclipse/persistence/testing/sdo/schemas/purchaseOrderComplexNSDataObject.xml";
    }

    protected String getControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/schemas/purchaseOrderComplexNS.xml";
    }

    protected String getSchemaName() {
        return "org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderComplexDefaultNamespace.xsd";
    }

    protected String getControlRootURI() {
        return NON_DEFAULT_URI;
    }

    protected String getControlRootName() {
        return "purchaseOrder";
    }

    protected String getRootInterfaceName() {
        return "PurchaseOrderType";
    }

    // Override package generation based on the JAXB 2.0 algorithm in SDOUtil.java
    protected List<String> getPackages() {
        List<String> packages = new ArrayList<String>();       
        packages.add(NON_DEFAULT_JAVA_PACKAGE_DIR);
        return packages;
    }
    
    protected void registerTypes() {
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);
        Type intType = typeHelper.getType("commonj.sdo", "Int");
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type dateType = typeHelper.getType("commonj.sdo", "YearMonthDay");
        Type addressType = registerAddressType();
        Type itemsType = registerItemsType();
        registerUSAddressType();
        registerCdnAddressType();
        registerCdnMailingAddressType();
        Type customerType = registerCustomerType();

        DataObject purchaseOrderTypeType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)purchaseOrderTypeType.getType().getProperty("uri");
        purchaseOrderTypeType.set(prop, getControlRootURI());
        prop = (SDOProperty)purchaseOrderTypeType.getType().getProperty("name");
        purchaseOrderTypeType.set(prop, "purchaseOrder");
        addProperty(purchaseOrderTypeType, "orderDate", dateType);
        DataObject shipToProp = addProperty(purchaseOrderTypeType, "shipTo", addressType, true, false, true);
        DataObject billToProp = addProperty(purchaseOrderTypeType, "billTo", addressType, true, false, true);
        addProperty(purchaseOrderTypeType, "comment", stringType);
        DataObject itemsProp = addProperty(purchaseOrderTypeType, "items", itemsType, true, false, true);
        DataObject customerProp = addProperty(purchaseOrderTypeType, "customer", customerType, true, false, true);
        addProperty(purchaseOrderTypeType, "poId", stringType);
        Type POType = typeHelper.define(purchaseOrderTypeType);

        DataObject propDO = dataFactory.create(propertyType);
        propDO.set("name", getControlRootName());
        propDO.set("type", POType);
        typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);
    }

    private Type registerAddressType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        DataObject addressType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)addressType.getType().getProperty("uri");
        addressType.set(prop, getControlRootURI());
        prop = (SDOProperty)addressType.getType().getProperty("name");
        addressType.set(prop, "AddressType");
        addProperty(addressType, "name", stringType);
        addProperty(addressType, "street", stringType);
        addProperty(addressType, "city", stringType);
        DataObject newProperty = addProperty(addressType, "country", stringType);
        prop = (SDOProperty)newProperty.getType().getProperty("default");
        newProperty.set(prop, "US");
        return typeHelper.define(addressType);
    }

    private Type registerCdnAddressType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        DataObject addressType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)addressType.getType().getProperty("uri");
        addressType.set(prop, getControlRootURI());
        prop = (SDOProperty)addressType.getType().getProperty("name");
        addressType.set(prop, "cdnAddressType");
        prop = (SDOProperty)addressType.getType().getProperty("baseType");
        List baseTypes = new ArrayList();
        baseTypes.add(typeHelper.getType(getControlRootURI(), "AddressType"));
        addressType.set(prop, baseTypes);
        addProperty(addressType, "province", stringType);
        addProperty(addressType, "territory", stringType);
        addProperty(addressType, "postalcode", stringType);
        return typeHelper.define(addressType);
    }

    private Type registerCdnMailingAddressType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        DataObject addressType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)addressType.getType().getProperty("uri");
        addressType.set(prop, getControlRootURI());
        prop = (SDOProperty)addressType.getType().getProperty("name");
        addressType.set(prop, "cdnAddressMailingType");
        prop = (SDOProperty)addressType.getType().getProperty("baseType");
        List baseTypes = new ArrayList();
        baseTypes.add(typeHelper.getType(getControlRootURI(), "cdnAddressType"));
        addressType.set(prop, baseTypes);
        addProperty(addressType, "deliveryInfo", stringType);
        return typeHelper.define(addressType);
    }

    private Type registerUSAddressType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type intType = typeHelper.getType("commonj.sdo", "Int");
        DataObject addressType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)addressType.getType().getProperty("uri");
        addressType.set(prop, getControlRootURI());
        prop = (SDOProperty)addressType.getType().getProperty("name");
        addressType.set(prop, "usAddressType");
        prop = (SDOProperty)addressType.getType().getProperty("baseType");
        List baseTypes = new ArrayList();
        baseTypes.add(typeHelper.getType(getControlRootURI(), "AddressType"));
        addressType.set(prop, baseTypes);
        addProperty(addressType, "state", stringType);
        addProperty(addressType, "zip", intType);
        return typeHelper.define(addressType);
    }

    private Type registerItemType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type intType = typeHelper.getType("commonj.sdo", "Int");
        Type floatType = typeHelper.getType("commonj.sdo", "Float");
        Type dateType = typeHelper.getType("commonj.sdo", "YearMonthDay");
        Type SKUType = registerSKUType();
        Type quantityType = registerQuantityType();
        DataObject itemType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)itemType.getType().getProperty("uri");
        itemType.set(prop, getControlRootURI());
        prop = (SDOProperty)itemType.getType().getProperty("name");
        itemType.set(prop, "LineItemType");
        addProperty(itemType, "partNum", SKUType);
        addProperty(itemType, "productName", stringType);
        addProperty(itemType, "quantity", quantityType);
        addProperty(itemType, "USPrice", floatType);
        addProperty(itemType, "CDNPrice", floatType);
        addProperty(itemType, "exchangeRate", floatType);
        addProperty(itemType, "comment", stringType);
        addProperty(itemType, "shipDate", dateType);
        return typeHelper.define(itemType);
    }

    private Type registerSKUType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        DataObject skuType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)skuType.getType().getProperty("uri");
        skuType.set(prop, getControlRootURI());
        prop = (SDOProperty)skuType.getType().getProperty("name");
        skuType.set(prop, "SKU");
        prop = (SDOProperty)skuType.getType().getProperty("dataType");
        skuType.set(prop, "true");
        prop = (SDOProperty)skuType.getType().getProperty("baseType");
        List baseTypes = new ArrayList();
        baseTypes.add(stringType);
        skuType.set(prop, baseTypes);
        return typeHelper.define(skuType);
    }

    private Type registerNamePrefixType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        DataObject namePrefixType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)namePrefixType.getType().getProperty("uri");
        namePrefixType.set(prop, getControlRootURI());
        prop = (SDOProperty)namePrefixType.getType().getProperty("name");
        namePrefixType.set(prop, "namePrefix");
        prop = (SDOProperty)namePrefixType.getType().getProperty("dataType");
        namePrefixType.set(prop, "true");
        prop = (SDOProperty)namePrefixType.getType().getProperty("baseType");
        List baseTypes = new ArrayList();
        baseTypes.add(stringType);
        namePrefixType.set(prop, baseTypes);
        return typeHelper.define(namePrefixType);
    }

    private Type registerPhoneNumberType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        DataObject namePrefixType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)namePrefixType.getType().getProperty("uri");
        namePrefixType.set(prop, getControlRootURI());
        prop = (SDOProperty)namePrefixType.getType().getProperty("name");
        namePrefixType.set(prop, "phoneNumber");
        prop = (SDOProperty)namePrefixType.getType().getProperty("dataType");
        namePrefixType.set(prop, "true");
        prop = (SDOProperty)namePrefixType.getType().getProperty("baseType");
        List baseTypes = new ArrayList();
        baseTypes.add(stringType);
        namePrefixType.set(prop, baseTypes);
        return typeHelper.define(namePrefixType);
    }

    private Type registerCustomerType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        DataObject customerType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)customerType.getType().getProperty("uri");
        customerType.set(prop, getControlRootURI());
        prop = (SDOProperty)customerType.getType().getProperty("name");
        customerType.set(prop, "CustomerType");
        addProperty(customerType, "name", stringType);
        addProperty(customerType, "gender", registerGenderType());
        addProperty(customerType, "phoneNumber", registerPhoneNumberType());
        addProperty(customerType, "namePrefix", registerNamePrefixType());
        return typeHelper.define(customerType);
    }

    private Type registerGenderType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        DataObject genderType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)genderType.getType().getProperty("uri");
        genderType.set(prop, getControlRootURI());
        prop = (SDOProperty)genderType.getType().getProperty("name");
        genderType.set(prop, "gender");
        prop = (SDOProperty)genderType.getType().getProperty("dataType");
        genderType.set(prop, "true");
        prop = (SDOProperty)genderType.getType().getProperty("baseType");
        List baseTypes = new ArrayList();
        baseTypes.add(stringType);
        genderType.set(prop, baseTypes);
        return typeHelper.define(genderType);
    }

    private Type registerQuantityType() {
        Type intType = typeHelper.getType("commonj.sdo", "Int");
        DataObject quantityType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)quantityType.getType().getProperty("uri");
        quantityType.set(prop, getControlRootURI());
        prop = (SDOProperty)quantityType.getType().getProperty("name");
        quantityType.set(prop, "quantityType");
        prop = (SDOProperty)quantityType.getType().getProperty("dataType");
        quantityType.set(prop, "true");
        prop = (SDOProperty)quantityType.getType().getProperty("baseType");
        List baseTypes = new ArrayList();
        baseTypes.add(intType);
        quantityType.set(prop, baseTypes);
        return typeHelper.define(quantityType);
    }

    private Type registerItemsType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type itemType = registerItemType();
        DataObject itemsType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)itemsType.getType().getProperty("uri");
        itemsType.set(prop, getControlRootURI());
        prop = (SDOProperty)itemsType.getType().getProperty("name");
        itemsType.set(prop, "Items");
        DataObject itemProp = addProperty(itemsType, "item", itemType, true, true, true);
        return typeHelper.define(itemsType);
    }
}
