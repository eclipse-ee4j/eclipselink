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

package org.eclipse.persistence.testing.sdo.model.datagraph;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataGraph;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

public class SDODataGraphDataObjectTestCases extends SDOTestCase {
    protected DataGraph dataGraph;
    
    public static final String URINAME = "http://www.example.org";
    public static final String COMPANY_TYPENAME = "Company";
    public static final String PO_TYPENAME = "PurchaseOrder";
    public static final String PO_SEQUENCE_PATH = "porder[1]";
    public static final String PO_PATH = "porder";
    public static final String CUSTOMER_TYPENAME = "Customer";
    public static final String ADDRESS_TYPENAME = "USAddress";
    public static final String ITEM_TYPENAME = "Item";

    protected Type stringType;
    protected Type dateType;
    protected Type yearMonthDayType;
    protected Type decimalType;
    protected Type idType;

    protected SDOType type;

    public SDODataGraphDataObjectTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        dataGraph = new SDODataGraph(aHelperContext);
        stringType = typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.STRING);
        dateType = typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATE);
        yearMonthDayType = typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.YEARMONTHDAY);
        decimalType = SDOConstants.SDO_DECIMAL;
        idType = typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.ID);     
    }
   
    /**
     * This function will define control types programmatically to compare to XSD definition
     * by using the standard spec SDODataObject generation method on page
     * 
     * The existing getControlTypes() uses non-public Property constructors
     * @throws Exception
     */
    public DataObject createRootObject(List<Type> types) {
    	DataObject aRoot = null;
    		DataObject cust1 = dataFactory.create(typeHelper.getType(URINAME, CUSTOMER_TYPENAME));
    		cust1.set("custID", 5);
    		
    		DataObject item1 = dataFactory.create(typeHelper.getType(URINAME, ITEM_TYPENAME));
    		item1.set("itemID", 3);
    		item1.set("name", "item1-DF");    		
    		DataObject item2 = dataFactory.create(typeHelper.getType(URINAME, ITEM_TYPENAME));
    		item2.set("itemID", 6);
    		item2.set("name", "item2-DF");    		
    		DataObject item3 = dataFactory.create(typeHelper.getType(URINAME, ITEM_TYPENAME));
    		item3.set("itemID", 7);
    		item3.set("name", "item3-DF");    		
    		
    		DataObject billTo = dataFactory.create(typeHelper.getType(URINAME, ADDRESS_TYPENAME));
    		billTo.set("name", "bill");
    		DataObject shipTo = dataFactory.create(typeHelper.getType(URINAME, ADDRESS_TYPENAME));
    		shipTo.set("name", "ship");

    		DataObject po1 = dataFactory.create(typeHelper.getType(URINAME, PO_TYPENAME));
    		po1.set("poID", 10);
    		po1.set("shipTo", shipTo);
    		po1.set("billTo", billTo);
    		po1.set("orderDate", "1999-10-20");
    		
    		// references to company/item
    		List<DataObject> itemsRef = new ArrayList<DataObject>();
    		itemsRef.add(item2);//6);
    		itemsRef.add(item3);//7);
    		po1.set("item", itemsRef); // unwrapping exception
    		
            aRoot = dataFactory.create(typeHelper.getType(URINAME, COMPANY_TYPENAME));
            List<DataObject> customers = new ArrayList<DataObject>();
            customers.add(cust1);
            aRoot.set("cust", customers);
            // add purchaseOrders
    		List<DataObject> pos = new ArrayList<DataObject>();
    		pos.add(po1);
            aRoot.set(PO_PATH, pos);

            // add items (containment)
    		List<DataObject> items = new ArrayList<DataObject>();
    		items.add(item1);
    		items.add(item2);
    		items.add(item3);
    		aRoot.set("item", items);
    		
            aRoot.set("name", "mycomp-DF");    		
    	
    	return aRoot;
    }

    public DataObject populateRootDataGraphDataObject(DataObject aRoot, List<Type> types) {
    		DataObject cust1 = dataFactory.create(typeHelper.getType(URINAME, CUSTOMER_TYPENAME));
    		cust1.set("custID", 5);
    		
    		DataObject item1 = dataFactory.create(typeHelper.getType(URINAME, ITEM_TYPENAME));
    		item1.set("itemID", 3);
    		item1.set("name", "item1-DF");    		
    		DataObject item2 = dataFactory.create(typeHelper.getType(URINAME, ITEM_TYPENAME));
    		item2.set("itemID", 6);
    		item2.set("name", "item2-DF");    		
    		DataObject item3 = dataFactory.create(typeHelper.getType(URINAME, ITEM_TYPENAME));
    		item3.set("itemID", 7);
    		item3.set("name", "item3-DF");    		
    		
    		DataObject billTo = dataFactory.create(typeHelper.getType(URINAME, ADDRESS_TYPENAME));
    		billTo.set("name", "bill");
    		DataObject shipTo = dataFactory.create(typeHelper.getType(URINAME, ADDRESS_TYPENAME));
    		shipTo.set("name", "ship");

    		DataObject po1 = dataFactory.create(typeHelper.getType(URINAME, PO_TYPENAME));
    		po1.set("poID", 10);
    		po1.set("shipTo", shipTo);
    		po1.set("billTo", billTo);
    		po1.set("orderDate", "1999-10-20");
    		
    		// references to company/item
    		List<DataObject> itemsRef = new ArrayList<DataObject>();
    		itemsRef.add(item2);//6);
    		itemsRef.add(item3);//7);
    		po1.set("item", itemsRef); // unwrapping exception
    		
            //aRoot = dataFactory.create(typeHelper.getType(URINAME, COMPANY_TYPENAME));
            List<DataObject> customers = new ArrayList<DataObject>();
            customers.add(cust1);
            aRoot.set("cust", customers);
            // add purchaseOrders
    		List<DataObject> pos = new ArrayList<DataObject>();
    		pos.add(po1);
            aRoot.set(PO_PATH, pos);

            // add items (containment)
    		List<DataObject> items = new ArrayList<DataObject>();
    		items.add(item1);
    		items.add(item2);
    		items.add(item3);
    		aRoot.set("item", items);
    		
            aRoot.set("name", "mycomp-DF");    		
    	return aRoot;
    }
    
    public List<Type> getControlTypes() {
        List<Type> types = new ArrayList<Type>();
        if(typeHelper.getType(URINAME, COMPANY_TYPENAME) != null) {
        	types.add(typeHelper.getType(URINAME, COMPANY_TYPENAME));
        }
        if(typeHelper.getType(URINAME, PO_TYPENAME) != null) {
        	types.add(typeHelper.getType(URINAME, PO_TYPENAME));
        }
        if(typeHelper.getType(URINAME, CUSTOMER_TYPENAME) != null) {
        	types.add(typeHelper.getType(URINAME, CUSTOMER_TYPENAME));
        }
        if(typeHelper.getType(URINAME, ADDRESS_TYPENAME) != null) {
        	types.add(typeHelper.getType(URINAME, ADDRESS_TYPENAME));
        }
        if(typeHelper.getType(URINAME, ITEM_TYPENAME) != null) {
        	types.add(typeHelper.getType(URINAME, ITEM_TYPENAME));
        }
    	return types;
    }

    protected List<Type> getTypesToGenerateFrom() {
        return getControlTypes();
    }

    protected String getControlRootURI() {
        return URINAME;
    }
    
    protected String getControlRootName() {
        return "company";
    }


    private Type defineAndPostProcessUnidirectional(String containingPropertyLocalName, DataObject typeToDefine, //
    		String idPropertyName, String containingPropertyName) {
    	// define the current type
        Type aType = typeHelper.define(typeToDefine);
        // get the 
        Property containingProperty = aType.getProperty(containingPropertyName);
        ((SDOProperty)containingProperty).setXsd(true);
        ((SDOProperty)containingProperty).setXsdLocalName(containingProperty.getContainingType().getName());//containingPropertyLocalName);
        //((SDOType)aType).setIDProp(aType.getProperty(idPropertyName));
        return aType;
    }
/*
    private Type defineAndPostProcessBidirectional(DataObject typeToDefine, String idPropertyName) {
        Type aType = typeHelper.define(typeToDefine);
        //SDOProperty itemProperty = (SDOProperty)aType.getProperty("item");
        //itemProperty.setXsd(true);
        //((SDOType)aType).setIDProp(aType.getProperty(idPropertyName));
        return aType;
    }
*/    
    private Type registerAddressType() {
        DataObject addressType = dataFactory.create(SDOConstants.SDO_URL, SDOConstants.TYPE);
        SDOProperty prop = (SDOProperty)addressType.getType().getProperty("uri");
        addressType.set(prop, getControlRootURI());
        prop = (SDOProperty)addressType.getType().getProperty(SDOConstants.SDOXML_NAME);
        addressType.set(prop,  ADDRESS_TYPENAME);
        
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

    private Type registerItemType() {
        DataObject itemType = dataFactory.create(SDOConstants.SDO_URL, SDOConstants.TYPE);
        SDOProperty prop = (SDOProperty)itemType.getType().getProperty("uri");
        itemType.set(prop, getControlRootURI());
        prop = (SDOProperty)itemType.getType().getProperty(SDOConstants.SDOXML_NAME);
        String typeName = ITEM_TYPENAME;
        String idPropName = "itemID";
        // set unidirection reference id
        setIDPropForReferenceProperties(itemType, "itemID");

        itemType.set(prop, typeName);

        DataObject newProperty = itemType.createDataObject("property");
        SDOProperty aProp = (SDOProperty)newProperty.getType().getProperty(SDOConstants.SDOXML_NAME);
        newProperty.set(aProp, idPropName);
        aProp = (SDOProperty)newProperty.getType().getProperty("type");
        //Type idTypeLocal = typeHelper.getType(XMLConstants.SCHEMA_URL, "ID");
        //assertNotNull(idTypeLocal);
        newProperty.set(aProp, stringType);     // no ID type to set - use string type
        aProp.setXsd(true);

        addProperty(itemType, "name", stringType);

        Type aType = typeHelper.define(itemType);
        //itemProperty.setXsd(true);
//        ((SDOType)aType).setIDProp(aType.getProperty(idPropName));
        return aType;
    }

    // sequenced
    private Type registerPurchaseOrderType(Type addressType, Type itemType, Type customerType) {
    	/**
    	 * instance properties available
    	 * aliasName, name, many, containment, default, readOnly, type, opposite, nullable
    	 */
        DataObject purchaseOrderTypeDO = dataFactory.create(SDOConstants.SDO_URL, SDOConstants.TYPE);
        SDOProperty prop = (SDOProperty)purchaseOrderTypeDO.getType().getProperty("uri");
        purchaseOrderTypeDO.set(prop, getControlRootURI());
        purchaseOrderTypeDO.set("sequenced", true); // schema shows "choice" in true case from "sequenced"
        prop = (SDOProperty)purchaseOrderTypeDO.getType().getProperty(SDOConstants.SDOXML_NAME);
        String typeName = PO_TYPENAME;
        String idPropName = "poID";
        purchaseOrderTypeDO.set(prop, typeName);

        // set unidirection reference id
        setIDPropForReferenceProperties(purchaseOrderTypeDO, "poID");
        
        // add properties in sequence
        DataObject shipToProp = addProperty(purchaseOrderTypeDO, "shipTo", addressType, true);
        assertNotNull(shipToProp);
        DataObject billToProp = addProperty(purchaseOrderTypeDO, "billTo", addressType, true);
        assertNotNull(billToProp);
        // unidirectional IDREFS
        DataObject itemProp = addProperty(purchaseOrderTypeDO, "item", itemType, false, true);
        assertNotNull(itemProp);
        DataObject poidProperty = addProperty(purchaseOrderTypeDO, idPropName, stringType, false);
        assertNotNull(poidProperty);
        addProperty(purchaseOrderTypeDO, "orderDate", yearMonthDayType);

        String containingPropertyName = "item";
        Type aType = defineAndPostProcessUnidirectional(typeName, purchaseOrderTypeDO, idPropName, containingPropertyName);
        
        return aType;
    }

    // not sequenced
    private Type registerCompanyType(Type purchaseOrderType, Type customerType, Type itemType) {
        DataObject companyType = dataFactory.create(SDOConstants.SDO_URL, SDOConstants.TYPE);
        SDOProperty prop = (SDOProperty)companyType.getType().getProperty("uri");
        companyType.set(prop, getControlRootURI());
        companyType.set("sequenced", false); // sequenced is false by default
        prop = (SDOProperty)companyType.getType().getProperty(SDOConstants.SDOXML_NAME);
        companyType.set(prop,  COMPANY_TYPENAME);

        DataObject name = addProperty(companyType, "name", stringType);
        assertNotNull(name);
        //name.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
//        SDOProperty nameProp = (SDOProperty)companyType.getType().getProperty(SDOConstants.XMLELEMENT_PROPERTY_NAME);
//        assertNotNull(nameProp);
        //name.set(SDOConstants.XMLELEMENT_PROPERTY_NAME, false);
        //nameProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.FALSE);        
        DataObject custProp =  addProperty(companyType, "cust", customerType, true, true);
        assertNotNull(custProp);
        DataObject poProp = addProperty(companyType, PO_PATH, purchaseOrderType, true, true);
        assertNotNull(poProp);
        DataObject itemProp = addProperty(companyType, "item", itemType, true, true);
        assertNotNull(itemProp);

        Type aType = typeHelper.define(companyType);
        return aType;
    }

    private Type registerCustomerType(Type purchaseOrderType) {
        DataObject customerType = dataFactory.create(SDOConstants.SDO_URL, SDOConstants.TYPE);
        SDOProperty prop = (SDOProperty)customerType.getType().getProperty("uri");
        customerType.set(prop, getControlRootURI());
        prop = (SDOProperty)customerType.getType().getProperty(SDOConstants.SDOXML_NAME);
        String typeName = CUSTOMER_TYPENAME;
        String idPropName = "custID";
        customerType.set(prop, typeName);
        setIDPropForReferenceProperties(customerType, "custID");
        
        DataObject custidProperty = addProperty(customerType, idPropName, stringType);
        assertNotNull(custidProperty);
        DataObject poProp = addProperty(customerType, "purchaseOrder", purchaseOrderType, false, false);
        assertNotNull(poProp);
        // post define processing
        String containingPropertyName = "purchaseOrder";
        Type aType = defineAndPostProcessUnidirectional(typeName, customerType, idPropName, containingPropertyName);
        return aType;
    }
    
    
    /**
     * See section 3.8.4 p.41
     * We would like to create at least one test framework that uses the standard createDataObject spec
     * instead of creating Property objects - which uses a non-public public api
     */
    public void registerTypes() {
        Type addressType = registerAddressType();
        Type itemType = registerItemType();
        //boolean createTogether = false;
        Type poType = null;
        Type customerType = null;
        	poType = registerPurchaseOrderType(addressType, itemType, customerType);        	
        	customerType = registerCustomerType(poType);
        Type companyType = registerCompanyType(poType, customerType, itemType);
        assertNotNull(companyType);
    }    
}
