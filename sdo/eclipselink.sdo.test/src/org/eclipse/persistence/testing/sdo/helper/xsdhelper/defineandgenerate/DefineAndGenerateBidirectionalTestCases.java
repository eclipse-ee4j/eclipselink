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

package org.eclipse.persistence.testing.sdo.helper.xsdhelper.defineandgenerate;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.helper.DefaultSchemaLocationResolver;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.oxm.XMLConstants;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Type;

public class DefineAndGenerateBidirectionalTestCases extends XSDHelperDefineAndGenerateTestCases {
	
    public static final String URINAME = "http://www.example.org";
    public static final String COMPANY_TYPENAME = "Company";
    public static final String PO_TYPENAME = "PurchaseOrder";
    public static final String CUSTOMER_TYPENAME = "Customer";
    public static final String ADDRESS_TYPENAME = "USAddress";
    public static final String ITEM_TYPENAME = "Item";
    
    //public static final String XML_PATH = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/CompanyWithBidirectionalNS.xml";
    public static final String XML_PATH = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/CompanyWithBidirectional.xml";
    public static final String XSD_PATH = "org/eclipse/persistence/testing/sdo/schemas/CompanyWithBidirectional.xsd";    
	
    protected DataObject root;
    protected ChangeSummary cs;
    protected Type stringType;
    protected Type dateType;
    protected Type yearMonthDayType;
    protected Type decimalType;
    protected Type idType;
    
    
    public DefineAndGenerateBidirectionalTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        stringType = typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.STRING);
        dateType = typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATE);
        yearMonthDayType = typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.YEARMONTHDAY);
        decimalType = SDOConstants.SDO_DECIMAL;
        idType = typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.ID);     
    }
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xsdhelper.defineandgenerate.DefineAndGenerateBidirectionalTestCases" };
        TestRunner.main(arguments);
    }
    
    protected String getControlRootURI() {
        return URINAME;
    }
    
    protected String getControlRootName() {
        return "company";
    }
    
    public String getSchemaToDefine() {
        return XSD_PATH;
    }

    public String getControlGeneratedFileName() {
        return XSD_PATH;
    }

    protected List<Type> getTypesToGenerateFrom() {
        return getControlTypes();
    }

	public static String getXSDString(String filename) {
		try {
			FileInputStream inStream = new FileInputStream(filename);
			byte[] bytes = new byte[inStream.available()];
			inStream.read(bytes);
			return new String(bytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
    
    /**
     * This function will define control types programmatically to compare to XSD definition
     * by using the standard spec SDODataObject generation method on page
     * 
     * The existing getControlTypes() uses non-public Property constructors
     * @throws Exception
     */
    public void testDefineAndGenerateUsingSpecMethod() throws Exception {
    	boolean useXSD = false;
    	List types;
    	DefaultSchemaLocationResolver resolver = new DefaultSchemaLocationResolver(getMap());
    	
        if(useXSD) {
           // InputStream is = getSchemaInputStream(getSchemaToDefine());
            //List types = xsdHelper.define(is, getSchemaLocation());
        	types = xsdHelper.define(getXSDString(XSD_PATH));
        //compareGeneratedTypes(getControlTypesViaSpecMethod(), types);
        } else {
        	registerTypes();
        	types = getTypesToGenerateFrom();
        }
       
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(types, resolver);
        log(generatedSchema);

//        //String controlSchema = getSchema(getControlGeneratedFileName());
//        //log("EXPECTED: \n" + controlSchema);
//        log("ACTUAL: \n" + generatedSchema);

//        StringReader reader = new StringReader(generatedSchema);
//        InputSource inputSource = new InputSource(reader);
//        Document generatedSchemaDoc = parser.parse(inputSource);
//        reader.close();

        try {
        	root = createRootObject(true, types);//false, types);
        } catch (Exception e) {
        	// SDOTestCase will currently throw a fail()
        	fail(e.getMessage());
        } 
        
    	int aRootsize = preOrderTraversalDataObjectList((SDODataObject)root).size();
    	//assertEquals(4, aRootsize);
    	
    	// get opposite properties
    	
    	// check that opposite bidirectional links resolve
    	//DataObject porderDO = (DataObject)root.get("porder[1]");
        assertTrue(writeXML(root, URINAME, COMPANY_TYPENAME, System.out));
        
        //assertXMLIdentical(getDocument(getControlGeneratedFileName()), generatedSchemaDoc);
    }
  
    public DataObject createRootObject(boolean viaXML, List types) {
    	DataObject aRoot = null;
    	if(viaXML) {
    		aRoot = loadXML(XML_PATH, true);
    	} else {
//    		<ns0:company xsi:type="CompanyType" xmlns:sdo="commonj.sdo" xmlns:ns0="http://www.example.org" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
//    		   <ns0:porder orderDate="1999-10-20">
//    		      <ns0:poID>10</ns0:poID>
//    		      <ns0:shipTo name="Alice Smith" country="US"/>
//    		      <ns0:billTo name="Robert Smith" country="US"/>
//    		      <ns0:item>6</ns0:item-->
//    		   </ns0:porder>
//    		</ns0:company>

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
    		List itemsRef = new ArrayList();
    		itemsRef.add(item2);//6);
    		po1.set("item", itemsRef); // unwrapping exception
    		
            aRoot = dataFactory.create(typeHelper.getType(URINAME, COMPANY_TYPENAME));
            List<DataObject> customers = new ArrayList<DataObject>();
            customers.add(cust1);
            aRoot.set("cust", customers);
            // add purchaseOrders
    		List pos = new ArrayList();
    		pos.add(po1);
            aRoot.set("porder", pos);

            // /company/porder/customer is bidirectional with /company/customer/purchaseOrder
            //po1.set("customer", cust1);
            // /company/customer/purchaseOrder is bidirectional with /company/porder/customer
            //cust1.set("purchaseOrder", po1);            
            
            // add items (containment)
    		List items = new ArrayList();
    		items.add(item1);
    		items.add(item2);
    		items.add(item3);
    		aRoot.set("item", items);
    		
            aRoot.set("name", "mycomp-DF");    		
    	}
    	
    	return aRoot;
    }
    
    // this function is overridden above
    public void testDefineAndGenerate() {
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
//        Property containingProperty = aType.getProperty(containingPropertyName);
//        ((SDOProperty)containingProperty).setXsd(true);
//        ((SDOProperty)containingProperty).setXsdLocalName(containingProperty.getContainingType().getName());//containingPropertyLocalName);
//        ((SDOType)aType).setIDProp(aType.getProperty(idPropertyName));
        return aType;
    }
/*
    private Type defineAndPostProcessBidirectional(DataObject typeToDefine, String idPropertyName) {
        Type aType = typeHelper.define(typeToDefine);
        //SDOProperty itemProperty = (SDOProperty)aType.getProperty("item");
        //itemProperty.setXsd(true);
        ((SDOType)aType).setIDProp(aType.getProperty(idPropertyName));
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

        itemType.set(prop, typeName);
        // set unidirectional reference id
        // TODO: BIDIRECTIONAL SUPPORT
        setIDPropForReferenceProperties(itemType, "itemID");

        //TODO: anyuri type?
        //addProperty(itemType, "itemID", stringType);//idType);
        DataObject newProperty = itemType.createDataObject("property");
        SDOProperty aProp = (SDOProperty)newProperty.getType().getProperty(SDOConstants.SDOXML_NAME);
        newProperty.set(aProp, idPropName);
        aProp = (SDOProperty)newProperty.getType().getProperty("type");
        Type idType = typeHelper.getType(XMLConstants.SCHEMA_URL, "ID");
        newProperty.set(aProp, stringType);     // no ID type to set - use string type
        aProp.setXsd(true);

        addProperty(itemType, "name", stringType);

        Type aType = typeHelper.define(itemType);
        //SDOProperty itemProperty = (SDOProperty)aType.getProperty("item");
        //itemProperty.setXsd(true);
        // TODO: BIDIRECTIONAL SUPPORT
///        ((SDOType)aType).setIDProp(aType.getProperty(idPropName));
        return aType;
    }

    private Type registerPurchaseOrderType(Type addressType, Type itemType, Type customerType) {
    	/**
    	 * instance properties available
    	 * aliasName, name, many, containment, default, readOnly, type, opposite, nullable
    	 */
        DataObject purchaseOrderType = dataFactory.create(SDOConstants.SDO_URL, SDOConstants.TYPE);
        SDOProperty prop = (SDOProperty)purchaseOrderType.getType().getProperty("uri");
        purchaseOrderType.set(prop, getControlRootURI());
        prop = (SDOProperty)purchaseOrderType.getType().getProperty(SDOConstants.SDOXML_NAME);
        String typeName = PO_TYPENAME;
        String idPropName = "poID";
        purchaseOrderType.set(prop, typeName);
        // set unidirectional reference id
        //setIDPropForReferenceProperties(purchaseOrderType, "poID");

        DataObject shipToProp = addProperty(purchaseOrderType, "shipTo", addressType);
        shipToProp.setBoolean("containment", true);

        DataObject billToProp = addProperty(purchaseOrderType, "billTo", addressType);
        billToProp.setBoolean("containment", true);

        // unidirectional IDREFS
        DataObject itemProp = addProperty(purchaseOrderType, "item", itemType);
        itemProp.setBoolean("containment", false);
        itemProp.setBoolean("many", true);
        //itemProp.set("opposite", null);
        //itemProp.setBoolean("xsd", true);

        DataObject poidProperty = addProperty(purchaseOrderType, idPropName, stringType);
//      bug: we need to change from attribute to element
        poidProperty.setBoolean("containment", true); // I know containment=false - this is to put poid in the sequence
        //poidProperty.set("xmlElement", true); // not an instance property
        //addProperty(purchaseOrderTypeType, "comment", stringType);
        addProperty(purchaseOrderType, "orderDate", yearMonthDayType);

        // Bidirectional Opposite IDREF (dont set opposite yet)
//        DataObject custProp = addProperty(purchaseOrderType, "customer", customerType);
        //custProp.set("opposite", customerPOProperty);
//        custProp.setBoolean("many", false);
//        custProp.setBoolean("containment", false);

        String containingPropertyName = "item";
        Type aType = defineAndPostProcessUnidirectional(typeName, purchaseOrderType, idPropName, containingPropertyName);
        
        //Type aType = typeHelper.define(purchaseOrderType);
        //SDOProperty itemProperty = (SDOProperty)aType.getProperty(containingPropertyName);
        //itemProperty.setXsd(true);
        //((SDOType)aType).setIDProp(aType.getProperty("poID"));
        
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
        // set unidirectional reference id
        //setIDPropForReferenceProperties(customerType, "custID");

        //addProperty(customerType, "custName", stringType);
        DataObject custidProperty = addProperty(customerType, idPropName, stringType);
        //custidProperty.setBoolean("containment", true); // bug: we need to change from attribute to element
        
        // Bidirectional Opposite IDREF (dont set opposite yet)
        DataObject poProp = addProperty(customerType, "purchaseOrder", purchaseOrderType);
        //poProp.set("opposite", poCustomerProperty);
        poProp.setBoolean("many", false);
        poProp.setBoolean("containment", false);
        
        // post define processing
        String containingPropertyName = "purchaseOrder";
        Type aType = defineAndPostProcessUnidirectional(typeName, customerType, idPropName, containingPropertyName);
        return aType;
    }

    private Type registerCompanyType(Type purchaseOrderType, Type customerType, Type itemType) {
        DataObject companyType = dataFactory.create(SDOConstants.SDO_URL, SDOConstants.TYPE);
        SDOProperty prop = (SDOProperty)companyType.getType().getProperty("uri");
        companyType.set(prop, getControlRootURI());
        prop = (SDOProperty)companyType.getType().getProperty(SDOConstants.SDOXML_NAME);
        companyType.set(prop,  COMPANY_TYPENAME);

        //TODO: anyuri type?
        DataObject name = addProperty(companyType, "name", stringType);
        //name.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        SDOProperty nameProp = (SDOProperty)companyType.getType().getProperty(SDOConstants.XMLELEMENT_PROPERTY_NAME);
        //name.set(SDOConstants.XMLELEMENT_PROPERTY_NAME, false);
        //nameProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.FALSE);        
        DataObject custProp =  addProperty(companyType, "cust", customerType);
        custProp.setBoolean("containment", true);
        custProp.setBoolean("many", true);
        DataObject poProp = addProperty(companyType, "porder", purchaseOrderType);
        poProp.setBoolean("containment", true);
        poProp.setBoolean("many",  true);
        DataObject itemProp = addProperty(companyType, "item", itemType);//registerItemType());
        itemProp.setBoolean("containment", true);
        itemProp.setBoolean("many",  true);

        return typeHelper.define(companyType);
    }
    

    /**
     * See section 3.8.4 p.41
     * We would like to create at least one test framework that uses the standard createDataObject spec
     * instead of creating Property objects - which uses a non-public public api
     */
    public void registerTypes() {
        Type addressType = registerAddressType();
        Type itemType = registerItemType();
        boolean createTogether = true;
        Type poType = null;
        Type customerType = null;
        if(!createTogether) {
        	poType = registerPurchaseOrderType(addressType, itemType, customerType);        	
        	customerType = registerCustomerType(poType);
        } else { 
        	List<Type> types = registerPurchaseOrderAndCustomerTypes(addressType, itemType);    
        	poType = types.get(0);
        	customerType = types.get(1);
    	}
        // move before
        //SDOProperty poProperty = (SDOProperty)poType.getType().getProperty(SDOConstants.SDOXML_NAME);
        //DataObject customerProp = addProperty(purchaseOrderTypeType, "customer", customerType);
        //customerProp.setBoolean("containment", false);
        //((SDOType)aType).setIDProp(aType.getProperty(idPropertyName));
        
        // set Bidirectional Opposite properties after definition (IDProp is set) 
        // /company/porder/customer is bidirectional with /company/customer/purchaseOrder
        SDOProperty poCustomerBidirectionalProperty = (SDOProperty)poType.getProperty("customer");
        // /company/customer/purchaseOrder is bidirectional with /company/porder/customer
        SDOProperty customerPOBidirectionalProperty = (SDOProperty)customerType.getProperty("purchaseOrder");
//        poCustomerBidirectionalProperty.setOpposite(customerPOBidirectionalProperty);
//        customerPOBidirectionalProperty.setOpposite(poCustomerBidirectionalProperty);

        // bidirectional needs poType defined here
        Type companyType = registerCompanyType(poType, customerType, itemType);        
        // make Company a global type
        //((SDOTypeHelper)aHelperContext.getTypeHelper()).s
    }    

    private List<Type> registerPurchaseOrderAndCustomerTypes(Type addressType, Type itemType) {
    	/**
    	 * instance properties available
    	 * aliasName, name, many, containment, default, readOnly, type, opposite, nullable
    	 * 
    	 * Algorithm:
    	 * 
    	 */
    	List<Type> types = new ArrayList<Type>();
        DataObject purchaseOrderTypeDO = dataFactory.create(SDOConstants.SDO_URL, SDOConstants.TYPE);
        SDOProperty prop = (SDOProperty)purchaseOrderTypeDO.getType().getProperty("uri");
        purchaseOrderTypeDO.set(prop, getControlRootURI());
        prop = (SDOProperty)purchaseOrderTypeDO.getType().getProperty(SDOConstants.SDOXML_NAME);
        purchaseOrderTypeDO.set(prop, PO_TYPENAME);

        DataObject shipToProp = addProperty(purchaseOrderTypeDO, "shipTo", addressType, true);
        //shipToProp.setBoolean("containment", true);

        DataObject billToProp = addProperty(purchaseOrderTypeDO, "billTo", addressType, true);
        //billToProp.setBoolean("containment", true);

        // unidirectional IDREFS
        DataObject itemProp = addProperty(purchaseOrderTypeDO, "item", itemType, false, true);
        itemProp.setBoolean("many", true);
        //itemProp.set("opposite", null);

        DataObject poidProperty = addProperty(purchaseOrderTypeDO, "poID", stringType, false);
//      bug: we need to change from attribute to element
        //poidProperty.setBoolean("containment", true); // I know containment=false - this is to put poid in the sequence
        //poidProperty.set("xmlElement", true); // not an instance property
        //addProperty(purchaseOrderTypeType, "comment", stringType);
        addProperty(purchaseOrderTypeDO, "orderDate", yearMonthDayType);

        // Bidirectional Opposite IDREF to customer/po
        // set Bidirectional Opposite properties after definition (IDProp is set) 
        // /company/porder/customer is bidirectional with /company/customer/purchaseOrder
        // /company/customer/purchaseOrder is bidirectional with /company/porder/customer
        Type customerType = null;
        Type purchaseOrderType = typeHelper.define(purchaseOrderTypeDO);
        types.add(purchaseOrderType);

        // define customer
        DataObject customerTypeDO = dataFactory.create(SDOConstants.SDO_URL, SDOConstants.TYPE);
        SDOProperty customerProp = (SDOProperty)customerTypeDO.getType().getProperty("uri");
        customerTypeDO.set(customerProp, getControlRootURI());
        customerProp = (SDOProperty)customerTypeDO.getType().getProperty(SDOConstants.SDOXML_NAME);
        customerTypeDO.set(customerProp, CUSTOMER_TYPENAME);
        DataObject custProp = addProperty(purchaseOrderTypeDO, "customer", customerType);
        custProp.setBoolean("many", false);
        custProp.setBoolean("containment", false);

        //addProperty(customerType, "custName", stringType);
        DataObject custidProperty = addProperty(customerTypeDO, "custID", stringType);

        // Bidirectional Opposite IDREF to po/customer
//        DataObject poProp = addProperty(customerTypeDO, "purchaseOrder", types.get(0));
//        SDOProperty poCustomerProperty =  (SDOProperty)purchaseOrderType.getProperty("customer");
//        poProp.setBoolean("many", false);
//        poProp.setBoolean("containment", false);
        
        types.add(typeHelper.define(customerTypeDO));

        // set opposites after definition
        //poProp.set("opposite", poCustomerProperty);
        //custProp.set("opposite", customerPOProperty);

        // /company/porder/customer is bidirectional with /company/customer/purchaseOrder
//        SDOProperty poCustomerBidirectionalProperty = (SDOProperty)purchaseOrderType.getProperty("customer");
        // /company/customer/purchaseOrder is bidirectional with /company/porder/customer
//        SDOProperty customerPOBidirectionalProperty = (SDOProperty)customerType.getProperty("purchaseOrder");

        return types;
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

    /*
    // use XSD to define and XML to load - working
    public void testDefineViaXSDandLoadViaXML() throws Exception {
    	DefaultSchemaLocationResolver resolver = new DefaultSchemaLocationResolver(getMap());
       	List<Type> types = xsdHelper.define(getXSDString(XSD_PATH));
       
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(types, resolver);
        log(generatedSchema);
        root = createRootObject(true, types);
    	int aRootsize = preOrderTraversalDataObjectList((SDODataObject)root).size();
    	System.out.println("UC1: via XSD/XML: size: " + aRootsize);
    	//assertEquals(4, aRootsize);    	
    	// get opposite properties    	
    	
    	// check that opposite bidirectional links resolve
    	//DataObject porderDO = (DataObject)root.get("porder[1]");
    	System.out.println(root);
        assertTrue(writeXML(root, URINAME, COMPANY_TYPENAME, System.out));
        //assertXMLIdentical(getDocument(getControlGeneratedFileName()), generatedSchemaDoc);    	
    }
 
    // use XSD to define and programmatic DataFactory to load -  not working
    public void testDefineViaXSDandLoadViaDataFactory() throws Exception {
    	DefaultSchemaLocationResolver resolver = new DefaultSchemaLocationResolver(getMap());
       	List<Type> types = xsdHelper.define(getXSDString(XSD_PATH));
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(types, resolver);
        log(generatedSchema);
        root = createRootObject(false, types);
    	int aRootsize = preOrderTraversalDataObjectList((SDODataObject)root).size();
    	System.out.println("UC2: via XSD/DF: size: " + aRootsize);
    	//assertEquals(4, aRootsize);    	
    	// get opposite properties    	
    	
    	// check that opposite bidirectional links resolve
    	//DataObject porderDO = (DataObject)root.get("porder[1]");
    	System.out.println(root);
        assertTrue(writeXML(root, URINAME, COMPANY_TYPENAME, System.out));
        //assertXMLIdentical(getDocument(getControlGeneratedFileName()), generatedSchemaDoc);    	
    }

    // use programmatic define(DataObject) to define and XML to load - not working
    public void testDefineViaDataObjectandLoadViaXML() throws Exception {
    	DefaultSchemaLocationResolver resolver = new DefaultSchemaLocationResolver(getMap());
    	registerTypes();
    	List<Type> types = getTypesToGenerateFrom();
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(types, resolver);
        log(generatedSchema);
        root = createRootObject(true, types);
    	int aRootsize = preOrderTraversalDataObjectList((SDODataObject)root).size();
    	System.out.println("UC3: via DO/XML: size: " + aRootsize);
    	//assertEquals(4, aRootsize);    	
    	// get opposite properties    	
    	
    	// check that opposite bidirectional links resolve
    	//DataObject porderDO = (DataObject)root.get("porder[1]");
    	System.out.println(root);
        assertTrue(writeXML(root, URINAME, COMPANY_TYPENAME, System.out));
        //assertXMLIdentical(getDocument(getControlGeneratedFileName()), generatedSchemaDoc);    	
    }

    // use programmatic define(DataObject) and programmatic DataFactory to load - not working
    public void testDefineViaDataObjectandLoadViaDataFactory() throws Exception {
    	DefaultSchemaLocationResolver resolver = new DefaultSchemaLocationResolver(getMap());
    	registerTypes();
    	List<Type> types = getTypesToGenerateFrom();
       
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(types, resolver);
        log(generatedSchema);
        root = createRootObject(false, types);
    	int aRootsize = preOrderTraversalDataObjectList((SDODataObject)root).size();
    	System.out.println("UC4: via DO/DF: size: " + aRootsize);
    	//assertEquals(4, aRootsize);    	
    	// get opposite properties    	
    	
    	// check that opposite bidirectional links resolve
    	//DataObject porderDO = (DataObject)root.get("porder[1]");
    	System.out.println(root);
        assertTrue(writeXML(root, URINAME, COMPANY_TYPENAME, System.out));
        //assertXMLIdentical(getDocument(getControlGeneratedFileName()), generatedSchemaDoc);    	
    }
  */  

}
