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
package org.eclipse.persistence.testing.sdo.model.sequence;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOSequence;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.DefaultSchemaLocationResolver;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.oxm.XMLConstants;

public class SDOSequenceTest extends SDOSequenceTestCases {

    public static final String URINAME = "http://www.example.org";
    public static final String COMPANY_TYPENAME = "Company";
    public static final String PO_TYPENAME = "PurchaseOrder";
    public static final String PO_SEQUENCE_PATH = "porder[1]";
    public static final String PO_PATH = "porder";
    public static final String CUSTOMER_TYPENAME = "Customer";
    public static final String ADDRESS_TYPENAME = "USAddress";
    public static final String ITEM_TYPENAME = "Item";
    
    public static final String XML_PATH = "org/eclipse/persistence/testing/sdo/model/sequence/CompanyWithSequence.xml";
    public static final String XSD_PATH = "org/eclipse/persistence/testing/sdo/schemas/CompanyWithSequence.xsd";    

    protected DataObject root;
    protected ChangeSummary cs;
    protected Type stringType;
    protected Type dateType;
    protected Type yearMonthDayType;
    protected Type decimalType;
    protected Type idType;

    public SDOSequenceTest(String name) {
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
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.sequence.SDOSequenceTest" };
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
    public DataObject createRootObject(boolean viaXML, List types) {
    	DataObject aRoot = null;
    	if(viaXML) {
    		aRoot = loadXML(XML_PATH, true);
    	} else {
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
    		itemsRef.add(item3);//7);
    		po1.set("item", itemsRef); // unwrapping exception
    		
            aRoot = dataFactory.create(typeHelper.getType(URINAME, COMPANY_TYPENAME));
            List<DataObject> customers = new ArrayList<DataObject>();
            customers.add(cust1);
            aRoot.set("cust", customers);
            // add purchaseOrders
    		List pos = new ArrayList();
    		pos.add(po1);
            aRoot.set(PO_PATH, pos);

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

    private Type defineAndPostProcessBidirectional(DataObject typeToDefine, String idPropertyName) {
        Type aType = typeHelper.define(typeToDefine);
        //SDOProperty itemProperty = (SDOProperty)aType.getProperty("item");
        //itemProperty.setXsd(true);
        //((SDOType)aType).setIDProp(aType.getProperty(idPropertyName));
        return aType;
    }

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
        Type idType = typeHelper.getType(XMLConstants.SCHEMA_URL, "ID");
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
        DataObject billToProp = addProperty(purchaseOrderTypeDO, "billTo", addressType, true);

        // unidirectional IDREFS
        DataObject itemProp = addProperty(purchaseOrderTypeDO, "item", itemType, false, true);

        DataObject poidProperty = addProperty(purchaseOrderTypeDO, idPropName, stringType, false);
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
        //name.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        SDOProperty nameProp = (SDOProperty)companyType.getType().getProperty(SDOConstants.XMLELEMENT_PROPERTY_NAME);
        //name.set(SDOConstants.XMLELEMENT_PROPERTY_NAME, false);
        //nameProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.FALSE);        
        DataObject custProp =  addProperty(companyType, "cust", customerType, true, true);
        DataObject poProp = addProperty(companyType, PO_PATH, purchaseOrderType, true, true);
        DataObject itemProp = addProperty(companyType, "item", itemType, true, true);

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
        DataObject poProp = addProperty(customerType, "purchaseOrder", purchaseOrderType, false, false);
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
        boolean createTogether = false;
        Type poType = null;
        Type customerType = null;
        	poType = registerPurchaseOrderType(addressType, itemType, customerType);        	
        	customerType = registerCustomerType(poType);
        Type companyType = registerCompanyType(poType, customerType, itemType);        
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

    // use programmatic define(DataObject) and programmatic DataFactory to load
    public void getRootViaDefineViaDataObjectandLoadViaDataFactory() {
    	DefaultSchemaLocationResolver resolver = new DefaultSchemaLocationResolver(getMap());
    	registerTypes();
    	List<Type> types = getTypesToGenerateFrom();
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(types, resolver);
        log(generatedSchema);
        root = createRootObject(false, types);
    	int aRootsize = preOrderTraversalDataObjectList((SDODataObject)root).size();
    	assertEquals(10, aRootsize);
    }

/*    
    private SDOSequence getSequence() {
    	// get sequence po
    	SDODataObject po = (SDODataObject)root.get(PO_SEQUENCE_PATH);
    	assertNotNull(po);
        assertTrue(po.getType().isSequenced());
        SDOSequence aSequence = (SDOSequence)po.getSequence();
        
        // create our own setting until Helpers are ready
        aSequence = new SDOSequence(po);
        for(Iterator i = po.getInstanceProperties().iterator(); i.hasNext();) {
        	Property aProperty = (Property)i.next();
        	aSequence.add(aProperty, po.get(aProperty));
        }
        assertNotNull(aSequence);
        assertEquals(5, aSequence.size());
        return aSequence;    	
    }
*/    
    
    /**
     * SDOSequence specific unit tests
     */

    /**
     * Returns the <code>Sequence</code> for this DataObject.
     * When getType().isSequencedType() == true,
     * the Sequence of a DataObject corresponds to the
     * XML elements representing the values of its Properties.
     * Updates through DataObject and the Lists or Sequences returned
     * from DataObject operate on the same data.
     * When getType().isSequencedType() == false, null is returned.
     * @return the <code>Sequence</code> or null.
     */
    public void testSequenceReturnFrom_SDODataObject_getSequence() {
    	getRootViaDefineViaDataObjectandLoadViaDataFactory();
    	// get sequence po
    	SDODataObject po = (SDODataObject)root.get(PO_SEQUENCE_PATH);
    	assertNotNull(po);
        assertTrue(po.getType().isSequenced());
        SDOSequence aSequence = (SDOSequence)po.getSequence();
    	assertNotNull(aSequence);
//    	assertEquals(4, aSequence.size());    	
    }
    

    private boolean verifySequenceIntegrity(SDOSequence aSequence, Property aProperty) {
//    	assertNotNull(aSequence);
    	// verify that the # element properties (not attributes) are the same as in aProperty
    	
    	List<Property> sequencedProperties = new ArrayList<Property>();//InProperty;//aProperty.getInstanceProperties()
    	for(Iterator i = aProperty.getInstanceProperties().iterator(); i.hasNext();) {
    		Property currentProperty = (Property)i.next();
    		if(currentProperty.getType().isSequenced()) {
    			sequencedProperties.add(currentProperty);
    		}
    		
    	}
    	assertEquals(4, aSequence.size());
    	
    	return true;
    }

    
    public void testMoveSameIndex() {
    	getRootViaDefineViaDataObjectandLoadViaDataFactory();
    	//SDOSequence aSequence = getSequence();
    	SDODataObject po = (SDODataObject)root.get(PO_SEQUENCE_PATH);
    	assertNotNull(po);
        assertTrue(po.getType().isSequenced());
        SDOSequence aSequence = (SDOSequence)po.getSequence();
    	

        // move 2 sequenced objects
        Property beforeMoveProp1 = aSequence.getProperty(1);
        assertNotNull(beforeMoveProp1);
        
        aSequence.move(1, 1);
        
        Property afterMoveProp1 = aSequence.getProperty(1);
        assertTrue(beforeMoveProp1 == afterMoveProp1);
    }

    public void testMoveIndex0toEnd() {
    	getRootViaDefineViaDataObjectandLoadViaDataFactory();
    	//SDOSequence aSequence = getSequence();
    	SDODataObject po = (SDODataObject)root.get(PO_SEQUENCE_PATH);
    	assertNotNull(po);
        assertTrue(po.getType().isSequenced());
        SDOSequence aSequence = (SDOSequence)po.getSequence();

        // move 2 sequenced objects
        Property beforeMoveProp0 = aSequence.getProperty(0);
        Property beforeMoveProp1 = aSequence.getProperty(1);
        Property beforeMoveProp3 = aSequence.getProperty(3);
        assertNotNull(beforeMoveProp0);
        assertNotNull(beforeMoveProp1);
        assertNotNull(beforeMoveProp3);
        
        // 0,1,2,3 -> 1,2,3,0
        aSequence.move(3, 0);
        
        Property afterMoveProp0 = aSequence.getProperty(0);
        //Property afterMoveProp0 = aSequence.getProperty(0);
        Property afterMoveProp3 = aSequence.getProperty(3);
        assertTrue(beforeMoveProp1 == afterMoveProp0);
        assertTrue(beforeMoveProp0 == afterMoveProp3);
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
    	assertEquals(9, aRootsize);
        assertTrue(writeXML(root, URINAME, COMPANY_TYPENAME, System.out));
    }
*/
/*    
    // use programmatic define(DataObject) to define and XML to load - not working
    public void testDefineViaDataObjectandLoadViaXML() throws Exception {
    	DefaultSchemaLocationResolver resolver = new DefaultSchemaLocationResolver(getMap());
    	registerTypes();
    	List<Type> types = getTypesToGenerateFrom();
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(types, resolver);
        log(generatedSchema);
        root = createRootObject(true, types);
    	int aRootsize = preOrderTraversalDataObjectList((SDODataObject)root).size();
        assertTrue(writeXML(root, URINAME, COMPANY_TYPENAME, System.out));
    }
*/
/*    
    // use programmatic define(DataObject) and programmatic DataFactory to load
    public void testDefineViaDataObjectandLoadViaDataFactory() throws Exception {
    	DefaultSchemaLocationResolver resolver = new DefaultSchemaLocationResolver(getMap());
    	registerTypes();
    	List<Type> types = getTypesToGenerateFrom();
       
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(types, resolver);
        log(generatedSchema);
        root = createRootObject(false, types);
    	int aRootsize = preOrderTraversalDataObjectList((SDODataObject)root).size();
    	assertEquals(9, aRootsize);
        assertTrue(writeXML(root, URINAME, COMPANY_TYPENAME, System.out));
    }
*/
}
