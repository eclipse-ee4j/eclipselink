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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;

public class LoadAndSavePurchaseOrderWithAnnotations extends LoadAndSaveTestCases {
	/** 
	 * Since there is no targetNamespace defined in the schema and we use the sdoJava:package annotations on the XSD, 
	 * We must also set the URI on any programmatically defined types in registerTypes() for no-schema-load test cases 
	 */
	private static final String CUSTOM_JAVA_PACKAGE_DIR = "com/example/myPackage";
	/** This URI must match what is set for sdoJava:package in the XSD */
	private static final String CUSTOM_JAVA_PACKAGE_URI = "com.example.myPackage"; 
	
    public LoadAndSavePurchaseOrderWithAnnotations(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSavePurchaseOrderWithAnnotations" };
        TestRunner.main(arguments);
    }

    protected String getNoSchemaControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/schemas/purchaseOrderWithInstanceClassNoSchema.xml";
    }

    protected String getControlDataObjectFileName() {
        return "./org/eclipse/persistence/testing/sdo/schemas/purchaseOrderWithInstanceClassWrite.xml";
    }

    protected String getControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/schemas/purchaseOrderWithInstanceClass.xml";
    }
    
    protected String getControlWriteFileName() {
        return "./org/eclipse/persistence/testing/sdo/schemas/purchaseOrderWithInstanceClassWrite.xml";
    }        

    protected String getSchemaName() {
        return "org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderWithInstanceClass.xsd";
    }

    protected String getControlRootURI() {
        return null;
    }

    protected String getControlRootName() {
        return "purchaseOrder";
    }

    protected String getRootInterfaceName() {
        return "PurchaseOrder";
    }

    protected List<String> getPackages() {
        List<String> packages = new ArrayList<String>();
        packages.add(CUSTOM_JAVA_PACKAGE_DIR);
        return packages;
    }
    
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

    protected void verifyAfterLoad(XMLDocument doc) {
        super.verifyAfterLoad(doc);
        DataObject rootObject = doc.getRootObject();
        Object value = rootObject.get("orderDate");

        assertNotNull(value);
        assertTrue(value instanceof Timestamp);
    }
    
     protected void verifyAfterLoadNoSchema(XMLDocument doc) {
        super.verifyAfterLoad(doc);
        DataObject rootObject = doc.getRootObject();
        Object value = rootObject.get("orderDate");

        assertNotNull(value);
        assertTrue(value instanceof String);
    }


    protected List defineTypes() {
        List types = super.defineTypes();
        SDOType itemType = (SDOType)typeHelper.getType(null, "ItemSDO");
        SDOType binaryType = (SDOType)typeHelper.getType(null, "myBinaryHandlerType");
        SDOProperty binaryDataProp = (SDOProperty)itemType.getDeclaredPropertiesMap().get("binaryData");
        SDOProperty binaryDataHandlerProp = (SDOProperty)itemType.getDeclaredPropertiesMap().get("binaryDataHandler");

        SDOProperty binaryDataManyProp = (SDOProperty)itemType.getDeclaredPropertiesMap().get("binaryDataMany");

        SDOProperty binaryDataHandlerManyProp = (SDOProperty)itemType.getDeclaredPropertiesMap().get("binaryDataHandlerMany");

        SDOProperty binaryDataHandlerNoMimeProp = (SDOProperty)itemType.getDeclaredPropertiesMap().get("binaryDataHandlerNoMime");
        SDOProperty binaryDataHandlerManyNoMimeProp = (SDOProperty)itemType.getDeclaredPropertiesMap().get("binaryDataHandlerManyNoMime");

        assertTrue(binaryDataProp.getType().equals(SDOConstants.SDO_BYTES));
        assertTrue(binaryDataHandlerProp.getType().equals(binaryType));
        assertTrue(binaryDataManyProp.isMany());
        assertTrue(binaryDataHandlerManyProp.isMany());
        assertTrue(binaryDataHandlerManyNoMimeProp.isMany());

        assertTrue(binaryDataProp.getXmlMapping() instanceof XMLBinaryDataMapping);
        assertTrue(binaryDataHandlerProp.getXmlMapping() instanceof XMLBinaryDataMapping);
        assertTrue(binaryDataManyProp.getXmlMapping() instanceof XMLBinaryDataCollectionMapping);
        assertTrue(binaryDataHandlerManyProp.getXmlMapping() instanceof XMLBinaryDataCollectionMapping);
        assertTrue(binaryDataHandlerNoMimeProp.getXmlMapping() instanceof XMLBinaryDataMapping);
        assertTrue(binaryDataHandlerManyNoMimeProp.getXmlMapping() instanceof XMLBinaryDataCollectionMapping);

        assertTrue(((XMLBinaryDataMapping)binaryDataProp.getXmlMapping()).getConverter() == null);
        assertTrue(((XMLBinaryDataMapping)binaryDataHandlerProp.getXmlMapping()).getConverter() == null);
        assertTrue(((XMLBinaryDataMapping)binaryDataHandlerNoMimeProp.getXmlMapping()).getConverter() == null);
        assertTrue(((XMLBinaryDataCollectionMapping)binaryDataManyProp.getXmlMapping()).getValueConverter() instanceof TypeConversionConverter);
        assertTrue(((XMLBinaryDataCollectionMapping)binaryDataHandlerManyProp.getXmlMapping()).getValueConverter() == null);
        assertTrue(((XMLBinaryDataCollectionMapping)binaryDataHandlerManyNoMimeProp.getXmlMapping()).getValueConverter() == null);

        return types;
    }

    protected void registerTypes() {
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type itemsType = registerItemsType();
        Type myTimeStampType = registerMyTimestampType();

        Type customerType = registerCustomerType();

        DataObject purchaseOrderTypeType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)purchaseOrderTypeType.getType().getProperty("uri");
        purchaseOrderTypeType.set(prop, getControlRootURI());
        prop = (SDOProperty)purchaseOrderTypeType.getType().getProperty("name");
        purchaseOrderTypeType.set(prop, "purchaseOrder");
        addProperty(purchaseOrderTypeType, "comment", stringType, false, false, false);
        addProperty(purchaseOrderTypeType, "orderDate", myTimeStampType, false, false, false);

        DataObject itemsProp = addProperty(purchaseOrderTypeType, "items", itemsType, true, false, true);
        DataObject customerProp = addProperty(purchaseOrderTypeType, "customer", customerType, true, false, true);
        addProperty(purchaseOrderTypeType, "poId", stringType);

        Type poType = typeHelper.define(purchaseOrderTypeType);

        DataObject propDO = dataFactory.create(propertyType);
        propDO.set("name", getControlRootName());
        propDO.set("type", poType);
        typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);
    }

    private Type registerItemType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type intType = typeHelper.getType("commonj.sdo", "Int");
        Type floatType = typeHelper.getType("commonj.sdo", "Float");
        Type dateType = typeHelper.getType("commonj.sdo", "YearMonthDay");
        Type SKUType = registerSKUType();
        Type myBinaryType = registerMyBinaryTypeType();
        Type quantityType = registerQuantityType();
        DataObject itemType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)itemType.getType().getProperty("uri");
        itemType.set(prop, getControlRootURI());

        itemType.set("name", "LineItemType");

        addProperty(itemType, "theMimeType", stringType, false, false, false);
        addProperty(itemType, "productName", stringType, false, false, false);
        addProperty(itemType, "quantity", quantityType, false, false, false);
        addProperty(itemType, "partNum", SKUType, false, false, false);
        addProperty(itemType, "porder", stringType, false, false, true);
        addProperty(itemType, "skusTest", SKUType, false, true, true);
        addProperty(itemType, "comment", stringType, false, false, true);
        addProperty(itemType, "shipDate", dateType, false, false, true);

        DataObject binaryDataProp = addProperty(itemType, "binaryData", SDOConstants.SDO_BYTES, false, false, true);
        binaryDataProp.set(SDOConstants.MIME_TYPE_PROPERTY_PROPERTY, "theMimeType");

        DataObject binaryDataHandlerProp = addProperty(itemType, "binaryDataHandler", myBinaryType, false, false, true);
        binaryDataHandlerProp.set(SDOConstants.MIME_TYPE_PROPERTY_PROPERTY, "theMimeType");

        DataObject binaryDataManyProp = addProperty(itemType, "binaryDataMany", SDOConstants.SDO_BYTES, false, true, true);
        binaryDataManyProp.set(SDOConstants.MIME_TYPE_PROPERTY_PROPERTY, "theMimeType");

        DataObject binaryDataHandlerManyProp = addProperty(itemType, "binaryDataHandlerMany", myBinaryType, false, true, true);
        binaryDataHandlerManyProp.set(SDOConstants.MIME_TYPE_PROPERTY_PROPERTY, "theMimeType");

        addProperty(itemType, "binaryDataHandlerNoMime", myBinaryType, false, false, true);
        addProperty(itemType, "binaryDataHandlerManyNoMime", myBinaryType, false, true, true);

        Type theType = typeHelper.define(itemType);

        return theType;
    }

    private Type registerMyBinaryTypeType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        DataObject myBinaryTypeType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)myBinaryTypeType.getType().getProperty("uri");
        myBinaryTypeType.set(prop, getControlRootURI());
        myBinaryTypeType.set("name", "myBinaryHandlerType");
        myBinaryTypeType.set("dataType", true);

        List baseTypes = new ArrayList();
        baseTypes.add(SDOConstants.SDO_BYTES);
        myBinaryTypeType.set("baseType", baseTypes);
        myBinaryTypeType.set(SDOConstants.JAVA_CLASS_PROPERTY, "javax.activation.DataHandler");
        Type theType = typeHelper.define(myBinaryTypeType);

        return theType;
    }

    private Type registerMyTimestampType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        DataObject myTimestampType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)myTimestampType.getType().getProperty("uri");
        myTimestampType.set(prop, getControlRootURI());
        myTimestampType.set("dataType", true);
        myTimestampType.set("name", "MyTimeStamp");
        myTimestampType.set(SDOConstants.JAVA_CLASS_PROPERTY, "java.sql.Timestamp");
        return typeHelper.define(myTimestampType);
    }

    private Type registerSKUType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        DataObject skuType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)skuType.getType().getProperty("uri");
        skuType.set(prop, getControlRootURI());
        skuType.set("name", "SKU");
        skuType.set("dataType", true);
        List baseTypes = new ArrayList();
        baseTypes.add(stringType);
        skuType.set("baseType", baseTypes);
        skuType.set(SDOConstants.JAVA_CLASS_PROPERTY, "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.MySKU");
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
