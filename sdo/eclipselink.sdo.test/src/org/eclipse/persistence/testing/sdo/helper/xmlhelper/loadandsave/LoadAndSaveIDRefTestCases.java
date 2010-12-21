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

import java.util.ArrayList;
import java.util.List;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;

public class LoadAndSaveIDRefTestCases extends LoadAndSaveTestCases {
    public LoadAndSaveIDRefTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveIDRefTestCases" };
        TestRunner.main(arguments);
    }

    protected String getNoSchemaControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderWithIDREF.xml";
    }

    protected String getControlDataObjectFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderWithIDREF.xml";
    }

    protected String getControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderWithIDREF.xml";
    }

    protected String getSchemaName() {
        return "org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderWithIDREF.xsd";
    }

    protected String getControlRootURI() {
        return NON_DEFAULT_URI;
    }

    protected String getControlRootName() {
        return "company";
    }
    
     protected String getRootInterfaceName() {
        return "Company";
    }

     // Override package generation based on the JAXB 2.0 algorithm in SDOUtil.java
     protected List<String> getPackages() {
         List<String> packages = new ArrayList<String>();       
         packages.add(NON_DEFAULT_JAVA_PACKAGE_DIR);
         return packages;
     }
     
    private Type registerAddressType() {

        /****ADDRESS TYPE*****/
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type decimalType = SDOConstants.SDO_DECIMAL;
        DataObject addressType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)addressType.getType().getProperty("uri");
        addressType.set(prop, getControlRootURI());
        prop = (SDOProperty)addressType.getType().getProperty("name");
        addressType.set(prop, "USAddress");
        addProperty(addressType, "name", stringType);
        addProperty(addressType, "street", stringType);
        addProperty(addressType, "city", stringType);
        addProperty(addressType, "state", stringType);
        addProperty(addressType, "zip", decimalType);

        DataObject newProperty = addProperty(addressType, "country", stringType);
        prop = (SDOProperty)newProperty.getType().getProperty("default");
        newProperty.set(prop, "US");
        return typeHelper.define(addressType);
    }

    private Type defineAndPostProcessUnidirectional(String containingPropertyLocalName, DataObject typeToDefine, //
    		String idPropertyName, String containingPropertyName) {
        setIDPropForReferenceProperties(typeToDefine, idPropertyName);
        // define the current type
    	Type aType = null;
    	try {
    		aType = typeHelper.define(typeToDefine);
    	} catch (Exception e) {
    		fail(e.getMessage());
    	}
        return aType;
    }
    
    private Type registerItemType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");

        DataObject itemType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)itemType.getType().getProperty("uri");
        itemType.set(prop, getControlRootURI());
        prop = (SDOProperty)itemType.getType().getProperty("name");
        itemType.set(prop, "Item");
        //TODO: anyuri type?
        addProperty(itemType, "itemID", stringType);
        
        addProperty(itemType, "name", stringType);
        String containingPropertyName = "purchaseOrder";        
        // set unidirectional reference id
        Type aType = defineAndPostProcessUnidirectional("Item", itemType, "itemID", containingPropertyName);

        return aType;
    }

    private Type registerCompanyType(Type purchaseOrderType, Type customerType) {
        Type stringType = typeHelper.getType("commonj.sdo", "String");

        DataObject companyType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)companyType.getType().getProperty("uri");
        companyType.set(prop, getControlRootURI());
        prop = (SDOProperty)companyType.getType().getProperty("name");
        companyType.set(prop, "Company");

        //TODO: anyuri type?
        addProperty(companyType, "name", stringType, false, false, false);
        addProperty(companyType, "cust", customerType, true, true, true);
        addProperty(companyType, "porder", purchaseOrderType,true,true,true);
        addProperty(companyType, "item", registerItemType(),true, true, true);

        return typeHelper.define(companyType);
    }

    private DataObject getCustomerTypeDO(DataObject purchaseOrderTypeType) {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        DataObject customerType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)customerType.getType().getProperty("uri");
        customerType.set(prop, getControlRootURI());
        prop = (SDOProperty)customerType.getType().getProperty("name");
        customerType.set(prop, "Customer");
        addProperty(customerType, "custName", stringType,false,false,true);
        addProperty(customerType, "custID", stringType);
        // set unidirectional reference id
        setIDPropForReferenceProperties(customerType, "custID");
        
        //Property poProp = addProperty(customerType, "purchaseOrder", purchaseOrderTypeType, false, false, true);
        //poProp.set("opposite",oppositeProp);
        
        //addProperty(customerType, "purchaseOrder", purchaseOrderTypeType);      
        return customerType;
        //return typeHelper.define(customerType);
    }

    public void registerTypes() {
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);
        SDOType stringType = (SDOType) typeHelper.getType("commonj.sdo", "String");
        SDOType dateType = (SDOType) typeHelper.getType("commonj.sdo", "YearMonthDay");
        SDOType addressType = (SDOType) registerAddressType();
        SDOType itemType = (SDOType) registerItemType();

        DataObject purchaseOrderTypeType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)purchaseOrderTypeType.getType().getProperty("uri");
        purchaseOrderTypeType.set(prop, getControlRootURI());
        prop = (SDOProperty)purchaseOrderTypeType.getType().getProperty("name");
        purchaseOrderTypeType.set(prop, "PurchaseOrder");
        addProperty(purchaseOrderTypeType, "poID", stringType,false, false,true);
        DataObject shipToProp = addProperty(purchaseOrderTypeType, "shipTo", addressType,true, false, true);        
        DataObject billToProp = addProperty(purchaseOrderTypeType, "billTo", addressType,true, false, true);        
        DataObject itemProp = addProperty(purchaseOrderTypeType, "item", itemType,false,true,true);    
        
        addProperty(purchaseOrderTypeType, "comment", stringType);
        addProperty(purchaseOrderTypeType, "orderDate", dateType);
        // set unidirectional reference id
        setIDPropForReferenceProperties(purchaseOrderTypeType, "poID");
        DataObject customerTypeDO = getCustomerTypeDO(purchaseOrderTypeType);
        
        
        DataObject customerProp = addProperty(purchaseOrderTypeType, "customer", customerTypeDO,false, false, false);
        DataObject poProp = addProperty(customerTypeDO, "purchaseOrder", purchaseOrderTypeType,false, false, true);
        customerProp.set("opposite", poProp);
        poProp.set("opposite", customerProp);
        
        Type POType = typeHelper.define(purchaseOrderTypeType);
        Type custType = typeHelper.define(customerTypeDO);
        Type companyType = registerCompanyType(POType, custType);        

        DataObject propDO = dataFactory.create(propertyType);
        propDO.set("name", getControlRootName());
       // propDO.set("type", POType);
          propDO.set("type", companyType);
          propDO.set("containment", true);
           propDO.set(SDOConstants.XMLELEMENT_PROPERTY, true);
        typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);
    }
    
    public void verifyAfterLoad(XMLDocument doc)
    {
      super.verifyAfterLoad(doc);
      
      Type customerType = typeHelper.getType(getControlRootURI(),"Customer");
      Type poType = typeHelper.getType(getControlRootURI(),"PurchaseOrder");
      assertNotNull(customerType);
      assertNotNull(poType);
      
      Property custProp = poType.getProperty("customer");
      Property poProp = customerType.getProperty("purchaseOrder");
      assertNotNull(custProp);
      assertNotNull(poProp);
      /*assertNotNull(custProp.getOpposite());
      assertNotNull(poProp.getOpposite());      
      assertTrue(custProp == poProp.getOpposite());
      assertTrue(poProp == custProp.getOpposite());*/
    }
}
