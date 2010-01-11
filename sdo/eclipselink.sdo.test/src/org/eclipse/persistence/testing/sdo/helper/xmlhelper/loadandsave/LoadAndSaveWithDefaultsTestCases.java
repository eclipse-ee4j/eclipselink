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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;

/**
 * Created for bug 60307617
 */
public class LoadAndSaveWithDefaultsTestCases extends LoadAndSavePurchaseOrderComplexTestCases {
    public LoadAndSaveWithDefaultsTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveWithDefaultsTestCases" };
        TestRunner.main(arguments);
    }

    protected String getNoSchemaControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/defaultvalue/purchaseOrderComplexDefaultValueNSNoSchema.xml";
    }

    protected String getControlDataObjectFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/defaultvalue/purchaseOrderComplexDefaultValueNSDataObject.xml";
    }

    protected String getControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/defaultvalue/purchaseOrderComplexDefaultValueNS.xml";
    }

    protected String getSchemaName() {
        return "org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderComplexDefaultValue.xsd";
    }

    protected void verifyAfterLoad(XMLDocument doc) {
    	// TODO: the noSchema test will continue to fail until attributes are working with IsSetOptionalNodeNullPolicy
        super.verifyAfterLoad(doc);
        DataObject rootObject = doc.getRootObject();
        DataObject address = rootObject.getDataObject("shipTo");
        assertNotNull(address);

        //city is not set to nillable and is not present in the doc this means we will call set(null)                
        //IsSetOptionalNodeNull Policy will return the default
        assertEquals("Ottawa", address.get("city"));
        assertFalse(address.isSet("city"));

        Property prop = address.getInstanceProperty("street");

        boolean isAttribute = xsdHelper.isAttribute(prop);

        //street is set to nillable and is not present in the doc
        //street isSet false get "Main Street"
        if (isAttribute) {
            assertEquals(null, address.get("street"));
            assertTrue(address.isSet("street"));
        } else {
            assertFalse(address.isSet("street"));
            assertEquals("Main Street", address.get("street"));
        }
        
        
        //name is set to nillable and has xsi:nil=true in the doc                
        assertTrue(address.isSet("name"));
        assertEquals(null, address.get("name"));

        //country is not set to nillable is not present in the doc
        assertFalse(address.isSet("country"));
        assertEquals("US", address.get("country"));

        List items = rootObject.getDataObject("items").getList("item");
        DataObject item1 = (DataObject)items.get(0);
        DataObject item2 = (DataObject)items.get(0);
        assertNotNull(item1);
        assertNotNull(item2);

        //USPrice is not set to nillable and is not present in the doc this means we will call set(null)
        assertFalse(item1.isSet("USPrice"));
        assertEquals(10, ((Float)item1.get("USPrice")).intValue());

        isAttribute = xsdHelper.isAttribute(item2.getInstanceProperty("CDNPrice"));
        //CDNPrice is set to nillable and is not present in the doc
        if (isAttribute) {
            assertTrue(item2.isSet("CDNPrice"));
            assertEquals(null, item2.get("CDNPrice"));
        } else {
            assertFalse(item2.isSet("CDNPrice"));
            assertEquals(15.0f, item2.get("CDNPrice"));
        }
        address.unset("country");              
    }

    protected Type registerAddressType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        DataObject addressType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)addressType.getType().getProperty("uri");
        addressType.set(prop, getControlRootURI());
        prop = (SDOProperty)addressType.getType().getProperty("name");
        addressType.set(prop, "AddressType");
        DataObject nameProp = addProperty(addressType, "name", stringType, false, false, true);
        nameProp.set("nullable", true);  
        nameProp.set("default", "MyAddress");  
        
        DataObject streetProp = addProperty(addressType, "street", stringType, false, false, true);
        //streetProp.set("nullable", true);  //dont set since this is an attribute
        streetProp.set("default", "Main Street");
        DataObject cityProp = addProperty(addressType, "city", stringType, false, false, true);
        cityProp.set("default", "Ottawa");
        DataObject newProperty = addProperty(addressType, "country", stringType, false, false, true);
        prop = (SDOProperty)newProperty.getType().getProperty("default");
        newProperty.set(prop, "US");
        return typeHelper.define(addressType);
    }

    protected Type registerItemType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        //Type intType = typeHelper.getType("commonj.sdo", "Int");
        Type floatType = typeHelper.getType("commonj.sdo", "Float");
        Type dateType = typeHelper.getType("commonj.sdo", "YearMonthDay");
        Type SKUType = registerSKUType();
        Type quantityType = registerQuantityType();
        DataObject itemType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)itemType.getType().getProperty("uri");
        itemType.set(prop, getControlRootURI());
        prop = (SDOProperty)itemType.getType().getProperty("name");
        itemType.set(prop, "LineItemType");
        addProperty(itemType, "partNum", SKUType, false, false, false);
        addProperty(itemType, "productName", stringType, false, false, true);
        addProperty(itemType, "quantity", quantityType, false, false, false);
        // attributes are not supported by IsSetOptionalNodeNullPolicy yet
        DataObject usPriceProp = addProperty(itemType, "USPrice", floatType, false, false, true);
        usPriceProp.set("default", 10f);
        DataObject cdnPriceProp = addProperty(itemType, "CDNPrice", floatType, false, false, true);
        //cdnPriceProp.set("nullable", true); //dont set since this is an attribute
        cdnPriceProp.set("default", 15f);
        addProperty(itemType, "exchangeRate", floatType, false, false, true);
        addProperty(itemType, "comment", stringType, false, false, true);
        addProperty(itemType, "shipDate", dateType, false, false, true);
        return typeHelper.define(itemType);
    }
    
    protected Type registerCustomerType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        DataObject customerType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)customerType.getType().getProperty("uri");
        customerType.set(prop, getControlRootURI());
        prop = (SDOProperty)customerType.getType().getProperty("name");
        customerType.set(prop, "CustomerType");
        addProperty(customerType, "name", stringType, false, false, true);
        addProperty(customerType, "gender", registerGenderType(),false, false, true);
        addProperty(customerType, "phoneNumber", registerPhoneNumberType(),false, false, true);
        addProperty(customerType, "namePrefix", registerNamePrefixType(),false, false, false);
        return typeHelper.define(customerType);
    }
    
     protected Type registerCdnAddressType() {
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
        addProperty(addressType, "province", stringType, false, false, true);
        addProperty(addressType, "territory", stringType, false, false, true);
        addProperty(addressType, "postalcode", stringType, false, false, true);
        return typeHelper.define(addressType);
    }
    
     protected Type registerUSAddressType() {
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
        addProperty(addressType, "state", stringType, false, false, true);
        addProperty(addressType, "zip", intType, false, false, false);
        return typeHelper.define(addressType);
    }
    
     protected Type registerCdnMailingAddressType() {
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
        addProperty(addressType, "deliveryInfo", stringType, false, false, true);
        return typeHelper.define(addressType);
    }
    
     protected void registerTypes() {
        Type intType = typeHelper.getType("commonj.sdo", "Int");
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type dateType = typeHelper.getType("commonj.sdo", "YearMonthDay");
        Type addressType = registerAddressType();
        Type itemsType = registerItemsType();
        registerUSAddressType();
        registerCdnAddressType();
        registerCdnMailingAddressType();
        Type customerType = registerCustomerType();
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

        DataObject purchaseOrderTypeType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)purchaseOrderTypeType.getType().getProperty("uri");
        purchaseOrderTypeType.set(prop, getControlRootURI());
        prop = (SDOProperty)purchaseOrderTypeType.getType().getProperty("name");
        purchaseOrderTypeType.set(prop, "purchaseOrder");
        addProperty(purchaseOrderTypeType, "orderDate", dateType);
        DataObject shipToProp = addProperty(purchaseOrderTypeType, "shipTo", addressType, true, false, true);
        DataObject billToProp = addProperty(purchaseOrderTypeType, "billTo", addressType, true, false, true);
        addProperty(purchaseOrderTypeType, "comment", stringType, false, false, true);
        DataObject itemsProp = addProperty(purchaseOrderTypeType, "items", itemsType, true, false, true);
        DataObject customerProp = addProperty(purchaseOrderTypeType, "customer", customerType, true, false, true);

        addProperty(purchaseOrderTypeType, "poId", stringType);
        Type POType = typeHelper.define(purchaseOrderTypeType);

        DataObject propDO = dataFactory.create(propertyType);
        propDO.set("name", getControlRootName());
        propDO.set("type", POType);
        typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);
    }

}
