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
import commonj.sdo.Sequence;
import commonj.sdo.Type;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOSequence;
import org.eclipse.persistence.sdo.SDOSetting;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.DefaultSchemaLocationResolver;
import org.eclipse.persistence.sdo.helper.ListWrapper;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.oxm.XMLConstants;

public class SDOSequenceTestXSD extends SDOSequenceTestCases {

    public static final String URINAME = "http://www.example.org";
    public static final String COMPANY_TYPENAME = "Company";
    public static final String PO_TYPENAME = "PurchaseOrder";
    public static final String PO_SEQUENCE_PATH = "porder[1]";
    public static final String PO_POID_NAME = "poID";
    public static final int PO_SEQUENCE_SIZE = 7;
    public static final int PO_SEQUENCE_TREE_SIZE = 7;
    public static final int PO_COMMENT_LIST_SIZE = 2;
    public static final int PO_TREE_SIZE = 5;
    public static final String PO_PATH = "porder";
    public static final String CUSTOMER_PATH = "cust";
    public static final String ITEM_PATH = "item";
    public static final String CUSTOMER_TYPENAME = "Customer";
    public static final String ADDRESS_TYPENAME = "USAddress";
    public static final String ITEM_TYPENAME = "Item";
    public static final String PO_COMMENT_VALUE1 = "comment 1";
    
    public static final String XML_PATH = "org/eclipse/persistence/testing/sdo/model/sequence/CompanyWithSequenceExt.xml";
    public static final String XSD_PATH = "org/eclipse/persistence/testing/sdo/schemas/CompanyWithSequenceExt.xsd";    
	
	public static final int NO_MATCH_INDEX = -1;
    
    protected DataObject root;
    protected ChangeSummary cs;
    protected Type stringType;
    protected Type dateType;
    protected Type yearMonthDayType;
    protected Type decimalType;
    protected Type idType;
    
    
    public SDOSequenceTestXSD(String name) {
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
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.sequence.SDOSequenceTestXSD" };
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
    public DataObject createRootObject(boolean viaXML, List types, boolean withChangeSummary) {
    	DataObject aRoot = null;
    	if(viaXML) {
    		aRoot = loadXML(XML_PATH, false);
    	} else {
    		DataObject cust1 = dataFactory.create(typeHelper.getType(URINAME, CUSTOMER_TYPENAME));
    		cust1.set("custID", 5);
    		List emails = new ArrayList();
    		emails.add("email1-DF@myCompany.com");
    		emails.add("email2-DF@myCompany.com");
    		cust1.set("email", emails);
    		List phones = new ArrayList();
    		phones.add("6135550001");
    		cust1.set("phone", phones);
    		
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

    		// set comments list
    		List<String> comments = new ArrayList<String>();
    		comments.add(PO_COMMENT_VALUE1);
    		comments.add("comment 2");
            po1.set("comment", comments);
    		
    		po1.set("orderDate", "1999-10-20");
    		
    		// references to company/item
    		List<DataObject> itemsRef = new ArrayList<DataObject>();
    		itemsRef.add(item2);//6);
    		itemsRef.add(item3);//7);
    		po1.set("item", itemsRef); // unwrapping exception
    		
            aRoot = dataFactory.create(typeHelper.getType(URINAME, COMPANY_TYPENAME));
            aRoot.set(CUSTOMER_PATH, cust1);
            //aRoot.set(propertyIndex, value)
            // add purchaseOrders
    		List<DataObject> pos = new ArrayList<DataObject>();
    		pos.add(po1);
            aRoot.set(PO_PATH, pos);

            // add items (containment)
    		List<DataObject> items = new ArrayList<DataObject>();
    		items.add(item1);
    		items.add(item2);
    		items.add(item3);
    		aRoot.set(ITEM_PATH, items);
    		
            aRoot.set("name", "mycomp-DF");    	
            
            if(withChangeSummary) {
            	aRoot.getChangeSummary().endLogging();
            	aRoot.getChangeSummary().beginLogging();
            }
    	}
    	return aRoot;
    }

    public DataObject createRootObject(boolean viaXML, List types) {
    	return createRootObject(viaXML, types, false);
    }
    private Type defineAndPostProcessUnidirectional(String containingPropertyLocalName, DataObject typeToDefine, //
    		String idPropertyName, String containingPropertyName) {
    	// define the current type
    	Type aType = null;
    	try {
    		aType = typeHelper.define(typeToDefine);
    	} catch (Exception e) {
    		fail(e.getMessage());
    	}
        // get the 
        Property containingProperty = aType.getProperty(containingPropertyName);
        ((SDOProperty)containingProperty).setXsd(true);
        ((SDOProperty)containingProperty).setXsdLocalName(containingProperty.getContainingType().getName());//containingPropertyLocalName);
        return aType;
    }

    private Type defineAndPostProcessBidirectional(DataObject typeToDefine, String idPropertyName) {
        Type aType = typeHelper.define(typeToDefine);
        //SDOProperty itemProperty = (SDOProperty)aType.getProperty("item");
        //itemProperty.setXsd(true);
        return aType;
    }
    
    private Type registerAddressType(boolean sequencedFlag) {
        DataObject addressType = dataFactory.create(SDOConstants.SDO_URL, SDOConstants.TYPE);
        SDOProperty prop = (SDOProperty)addressType.getType().getProperty("uri");
        addressType.set(prop, getControlRootURI());        
        addressType.set("name",  ADDRESS_TYPENAME);
        
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

    private Type registerItemType(boolean sequencedFlag) {
        DataObject itemType = dataFactory.create(SDOConstants.SDO_URL, SDOConstants.TYPE);
        SDOProperty prop = (SDOProperty)itemType.getType().getProperty("uri");
        itemType.set(prop, getControlRootURI());                
        itemType.set("name", ITEM_TYPENAME);
        
        Type idType = typeHelper.getType(XMLConstants.SCHEMA_URL, "ID");        
        
        addProperty(itemType, "itemID", stringType, false, false, true);        
        addProperty(itemType, "name", stringType, false, false, true);

        // set unidirection reference id
        setIDPropForReferenceProperties(itemType, "itemID");

        Type aType = typeHelper.define(itemType);
        return aType;
    }

    // sequenced
    private Type registerPurchaseOrderType(boolean sequencedFlag, Type addressType, Type itemType, Type customerType) {
    	 // instance properties available
    	 // aliasName, name, many, containment, default, readOnly, type, opposite, nullable
        DataObject purchaseOrderTypeDO = dataFactory.create(SDOConstants.SDO_URL, SDOConstants.TYPE);
        SDOProperty prop = (SDOProperty)purchaseOrderTypeDO.getType().getProperty("uri");
        purchaseOrderTypeDO.set(prop, getControlRootURI());
        purchaseOrderTypeDO.set("sequenced", true);//sequencedFlag); 
        purchaseOrderTypeDO.set("open", true); 
                
        String typeName = PO_TYPENAME;
        purchaseOrderTypeDO.set("name", typeName);

        // set unidirection reference id
        setIDPropForReferenceProperties(purchaseOrderTypeDO, "poID");

        // add properties in sequence
        DataObject shipToProp = addProperty(purchaseOrderTypeDO, "shipTo", addressType, true);
        DataObject billToProp = addProperty(purchaseOrderTypeDO, "billTo", addressType, true);
        DataObject commentProp = addProperty(purchaseOrderTypeDO, "comment", stringType, false, true);

        // unidirectional IDREFS
        DataObject itemProp = addProperty(purchaseOrderTypeDO, "item", itemType, false, true);

        DataObject poidProperty = addProperty(purchaseOrderTypeDO, "poID", stringType, false);
        // !many, !containment and !nullable - we must set one of these so poID can be an element and exist in the sequence
        poidProperty.set("nullable", true); // this statement goes along with the false containment flag above
        addProperty(purchaseOrderTypeDO, "orderDate", yearMonthDayType);

        String containingPropertyName = ITEM_PATH;
        Type aType = defineAndPostProcessUnidirectional(typeName, purchaseOrderTypeDO, "poID", containingPropertyName);        
        return aType;
    }

    // sequenced
    private Type registerCompanyType(boolean sequencedFlag, Type purchaseOrderType, Type customerType, Type itemType, boolean withChangeSummary) {
        DataObject companyType = dataFactory.create(SDOConstants.SDO_URL, SDOConstants.TYPE);
        SDOProperty prop = (SDOProperty)companyType.getType().getProperty("uri");
        companyType.set(prop, getControlRootURI());
        companyType.set("sequenced", true);//false);//sequencedFlag); // sequenced is false by default
        //companyType.set("sequenced", true);         
        companyType.set("open", true);         
        companyType.set("name",  COMPANY_TYPENAME);

        // changeSummary property
        if(withChangeSummary) {
            SDOType changeSummaryType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.CHANGESUMMARY);
            DataObject changeSumPropertyDO = addProperty(companyType, "csmProp", changeSummaryType);
            changeSumPropertyDO.set("containment", true);
        }

        DataObject name = addProperty(companyType, "name", stringType);
        //name.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        SDOProperty nameProp = (SDOProperty)companyType.getType().getProperty(SDOConstants.XMLELEMENT_PROPERTY_NAME);
        //name.set(SDOConstants.XMLELEMENT_PROPERTY_NAME, false);
        //nameProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.FALSE);        
        DataObject custProp =  addProperty(companyType, CUSTOMER_PATH, customerType, true, false);
        DataObject poProp = addProperty(companyType, PO_PATH, purchaseOrderType, true, true);
        DataObject itemProp = addProperty(companyType, ITEM_PATH, itemType, true, true);

        Type aType = typeHelper.define(companyType);
        return aType;
    }

    private Type registerCustomerType(boolean sequencedFlag, Type purchaseOrderType) {
        DataObject customerType = dataFactory.create(SDOConstants.SDO_URL, SDOConstants.TYPE);
        SDOProperty prop = (SDOProperty)customerType.getType().getProperty("uri");
        customerType.set("sequenced", true);//sequencedFlag); // sequenced is true by default
        customerType.set("open", true); 
        customerType.set(prop, getControlRootURI());
        
        String typeName = CUSTOMER_TYPENAME;
        // set unidirection reference id
        setIDPropForReferenceProperties(customerType, "custID");
        String emailPropName = "email";
        String phonePropName = "phone";
        customerType.set("name", typeName);

        DataObject custEmailProperty = addProperty(customerType, emailPropName, stringType);
        DataObject custPhoneProperty = addProperty(customerType, phonePropName, stringType);
        
        DataObject custidProperty = addProperty(customerType, "custID", stringType);
//        DataObject poProp = addProperty(customerType, "purchaseOrder", purchaseOrderType, false, false);
        // post define processing
        String containingPropertyName = "purchaseOrder";
        //Type aType = defineAndPostProcessUnidirectional(typeName, customerType, idPropName, containingPropertyName);
        Type aType = typeHelper.define(customerType);        
        return aType;
    }
    

     // See section 3.8.4 p.41
     // We would like to create at least one test framework that uses the standard createDataObject spec
     // instead of creating Property objects - which uses a non-public public api
    public void registerTypes(boolean sequencedFlag, boolean withChangeSummary) {
    	// pass the sequenced flag to the typeHelper.define() calls so we can create without a sequence
        Type addressType = registerAddressType(sequencedFlag);
        Type itemType = registerItemType(sequencedFlag);
        Type customerType = null;
        Type poType = registerPurchaseOrderType(sequencedFlag, addressType, itemType, customerType);
        customerType = registerCustomerType(sequencedFlag, poType);
        Type companyType = registerCompanyType(sequencedFlag, poType, customerType, itemType, withChangeSummary);        
    }    

    public void registerTypes(boolean sequencedFlag) {
    	registerTypes(sequencedFlag, false);
    }
    public void registerTypes() {
    	registerTypes(true);
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

    public void setTypeSequenced(String typeName, boolean sequenced) {
    	SDOType aType = (SDOType)typeHelper.getType(URINAME, typeName);
    	aType.setSequenced(sequenced);
    }

    public void defineAndLoadRoot(boolean defineViaXSD, boolean loadViaXML, boolean sequencedFlag, boolean withChangeSummary) {
    	DefaultSchemaLocationResolver resolver = new DefaultSchemaLocationResolver(getMap());
    	List<Type> types = null;
    	if(defineViaXSD) {
    		types = xsdHelper.define(getXSDString(XSD_PATH));
    	} else {
    		registerTypes(sequencedFlag, withChangeSummary);
    		types = getControlTypes();
    	}
       
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(types, resolver);
        //log(generatedSchema);
        root = createRootObject(loadViaXML, types, withChangeSummary);
    	int aRootsize = preOrderTraversalDataObjectList((SDODataObject)root).size();
//    	if(!loadViaXML)  {
//    		assertTrue(writeXML(root, URINAME, COMPANY_TYPENAME, System.out));
//    	}
    	assertEquals(10, aRootsize);
    }

    // use XSD to define and XML to load - sequenced Flag has affect only when defining via typeHelper
    public void defineAndLoadRoot(boolean defineViaXSD, boolean loadViaXML, boolean sequencedFlag) {
    	defineAndLoadRoot(defineViaXSD, loadViaXML, sequencedFlag, false);
    }

    public void defineAndLoadRoot(boolean defineViaXSD, boolean loadViaXML) {
    	defineAndLoadRoot(defineViaXSD, loadViaXML, true);
    }
    public void defineAndLoadRoot() {
    	defineAndLoadRoot(true, true);
    }
    
    
    private SDOSequence getSequence(DataObject aRoot, String sequencePath, int sequenceSize) {
    	// get sequence DO
    	DataObject dataObject = (DataObject)aRoot.get(sequencePath);
    	assertNotNull(dataObject);
        assertTrue(dataObject.getType().isSequenced());
        // get sequence
        SDOSequence aSequence = (SDOSequence)dataObject.getSequence();
        // check sequence
        assertNotNull(aSequence);
        //assertEquals(sequenceSize, aSequence.size());
        return aSequence;    	
    }
    
    public int getNthSequenceIndexFor(SDOSequence aSequence, String propertyName) {
    	return getNthSequenceIndexFor(aSequence, propertyName, 1);    		   
    }
    	 
    public int getNthSequenceIndexFor(SDOSequence aSequence, String propertyName, int matchNumber) {
    	// search for the indexed position in the sequence that corresponds to propertyName
    	int matchIndex = -1;
    	int numberMatches = matchNumber;
    	Property aProperty = null;
    	// TODO: linear performance hit
    	for(int i = 0, size = aSequence.size(); i < size; i++) {
    		aProperty = aSequence.getProperty(i);
    		// TODO: handle null property for unstructured text
    		if(aProperty != null && aProperty.getName().equals(propertyName)) {
    			// breakout of loop with this property
    			matchIndex = i;
    			if(--numberMatches < 1) {
    				i = aSequence.size();
    			}
    		}
    	}
    	return matchIndex;
    }

    private SDOProperty createOpenContentElementProperty(String name, Type type) {
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

        DataObject newProperty = dataFactory.create(propertyType);
        SDOProperty nameProp = (SDOProperty)newProperty.getType().getProperty("name");
        newProperty.set(nameProp, name);
        newProperty.set("type", type);
        SDOProperty openProp = (SDOProperty)typeHelper.defineOpenContentProperty("my.uri", newProperty);
        openProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        assertFalse(xsdHelper.isAttribute(openProp));
        return openProp;
    }
    
    public void testVerifyUnidirectionalReferenceSet() {
    	// make a source object
    	defineAndLoadRoot(false, false);
    	// get the type for PurchaseOrder
    	Property poProperty = root.getInstanceProperty(PO_PATH);
    	assertNotNull(poProperty);
    	SDOType poType = (SDOType)poProperty.getType();
    	assertNotNull(poType);
    	SDOProperty idProp = getIDProp(poType);
    	assertNotNull(idProp);
    }
    
    public void testAddOpenContentPropertyViaDataObject() {
    	// make a source object
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = (SDOSequence)root.getSequence();
        int sequenceSizeBefore = aSequence.size();
        assertNotNull(aSequence);
        SDOProperty openPropString = createOpenContentElementProperty("openContentString", SDOConstants.SDO_STRING);
        
        // perform open content op
        root.set(openPropString, "openContentValue");        
        // sequence should be modified
        assertEquals(aSequence.size(), sequenceSizeBefore + 1);
        
        // get dataObject side
        Object openDOContent = root.get("openContentString");
        assertNotNull(openDOContent);        
        // get sequence side
        Object openSequenceContent = aSequence.getValue(sequenceSizeBefore);
        assertNotNull(openSequenceContent);
        assertEquals(openSequenceContent, openDOContent);
    }

    public void testAddOnTheFlyOpenContentPropertyViaDataObject() {
        // make a source object
        defineAndLoadRoot(false, false);
        SDOSequence aSequence = (SDOSequence)root.getSequence();
        int sequenceSizeBefore = aSequence.size();
        assertNotNull(aSequence);

        // perform open content op
        root.set("openContentString", "openContentValue");
        // sequence should be modified
        assertEquals(aSequence.size(), sequenceSizeBefore + 1);

        // get dataObject side
        Object openDOContent = root.get("openContentString");
        assertNotNull(openDOContent);
    }

    public void testAddOpenContentElementByIndexPropertyViaSequence() {
    	// make a source object
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = (SDOSequence)root.getSequence();
        int sequenceSizeBefore = aSequence.size();
        assertNotNull(aSequence);
        SDOProperty openPropString = createOpenContentElementProperty("openContentString", SDOConstants.SDO_STRING);
        
        // perform open content op
        aSequence.add(2, openPropString, "openContentValue");        
        // sequence should be modified
        assertEquals(aSequence.size(), sequenceSizeBefore + 1);
        
        // get dataObject side
        Object openDOContent = root.get("openContentString");
        assertNotNull(openDOContent);        
        // get sequence side
        Object openSequenceContent = aSequence.getValue(2);
        assertNotNull(openSequenceContent);
        assertEquals(openSequenceContent, openDOContent);
    }
    
    public void testAddOpenContentElementPropertyViaSequence() {
    	// make a source object
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = (SDOSequence)root.getSequence();
        int sequenceSizeBefore = aSequence.size();
        assertNotNull(aSequence);
        SDOProperty openPropString = createOpenContentElementProperty("openContentString", SDOConstants.SDO_STRING);
        
        // perform open content op
        aSequence.add(openPropString, "openContentValue");        
        // sequence should be modified
        assertEquals(aSequence.size(), sequenceSizeBefore + 1);
        
        // get dataObject side
        Object openDOContent = root.get("openContentString");
        assertNotNull(openDOContent);        
        // get sequence side
        Object openSequenceContent = aSequence.getValue(sequenceSizeBefore);
        assertNotNull(openSequenceContent);
        assertEquals(openSequenceContent, openDOContent);
    }

    public void testModifyOpenContentPropertyViaSequence() {
    	
    }

    public void testModifyOpenContentPropertyViaDataObject() {
    }
    
    public void testRemoveOpenContentPropertyByIndexViaSequence() {
    	// make a source object
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = (SDOSequence)root.getSequence();
        int sequenceSizeBefore = aSequence.size();
        assertNotNull(aSequence);
        SDOProperty openPropString = createOpenContentElementProperty("openContentString", SDOConstants.SDO_STRING);
        
        // perform open content op
        aSequence.add(openPropString, "openContentValue");        
        // sequence should be modified
        assertEquals(aSequence.size(), sequenceSizeBefore + 1);
        
        // get dataObject side
        Object openDOContent = root.get("openContentString");
        assertNotNull(openDOContent);        
        // get sequence side
        Object openSequenceContent = aSequence.getValue(sequenceSizeBefore);
        assertNotNull(openSequenceContent);
        assertEquals(openSequenceContent, openDOContent);
        
        // remove
        aSequence.remove(sequenceSizeBefore);
        // sequence should be modified
        assertEquals(aSequence.size(), sequenceSizeBefore);
        
        // get dataObject side
        Object openDOContent2 = root.get("openContentString");
        assertNull(openDOContent2);        
        // get sequence side - look for old property
    }
    
    public void testDeleteOpenContentPropertyViaDataObject() {
    	// make a source object
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = (SDOSequence)root.getSequence();
        int sequenceSizeBefore = aSequence.size();
        assertNotNull(aSequence);
        SDOProperty openPropString = createOpenContentElementProperty("openContentString", SDOConstants.SDO_STRING);
        
        // perform open content op
        aSequence.add(openPropString, "openContentValue");        
        // sequence should be modified
        assertEquals(aSequence.size(), sequenceSizeBefore + 1);
        
        // get dataObject side
        Object openDOContent = root.get("openContentString");
        assertNotNull(openDOContent);        
        // get sequence side
        Object openSequenceContent = aSequence.getValue(sequenceSizeBefore);
        assertNotNull(openSequenceContent);
        assertEquals(openSequenceContent, openDOContent);
        
        // remove
        root.unset(openPropString);
        // sequence should be modified
        assertEquals(aSequence.size(), sequenceSizeBefore);
        
        // get dataObject side
        Object openDOContent2 = root.get("openContentString");
        assertNull(openDOContent2);        
        // get sequence side - look for old property
    }
    
    // undoChanges should have no effect
    public void testVariantUndoChangesWithNoCS() {
    	// make a source object
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        DataObject poDO = (DataObject)root.get(PO_SEQUENCE_PATH);
        assertNotNull(poDO);

        // modify
        poDO.set("poID", 11); // from 10

        // attempt undo via DataObject instead of via ChangeSummary
        
        ((SDODataObject)poDO).undoChanges(false, null, (SDODataObject)root, PO_PATH);
        // no changes should have occurred but since we don't have a cs to verify we just check the property
        assertEquals(11, ((Integer)poDO.get("poID")).intValue());
    }
    
    /**
     * Purpose: See JIRA-242 
     * http://bug.us.oracle.com/pls/bug/webbug_edit.edit_info_top?rptno=6031657
     * http://www.xcalia.com/support/browse/SDO-242
     * We need to clarify move behavior
     * If we do a set(property) on an existing non-many (simple or complex)
     * property what is the expected behavior?
     * 1) We modify the existing sequence (by setValue or creating a new setting in the existing index)
     * 2) We continue to add the new setting at the end of the sequence and remove
     * the previously existing sequence (shifting subsequence settings down)
     * 20070505: we are doing (1) when called from DataObject and (2) when called from Sequence (but without the remove)
     */
    public void testModifySimpleSingleViaDataObjectSet() {
    	// make a source object
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        int sequenceSizeBefore = aSequence.size();
        DataObject poDO = (DataObject)root.get(PO_SEQUENCE_PATH);
        assertNotNull(poDO);
        // get property to modify
        Property aProperty = poDO.getInstanceProperty("poID");
        assertNotNull(aProperty);
        // get property index to compare to later
        int propIndex = getNthSequenceIndexFor(aSequence, "poID");
        
        // modify the object and its sequence setting and observe
        //Object previousValue = poDO.get("poID");
        poDO.set("poID", 11); // from 10
        assertEquals(sequenceSizeBefore, aSequence.size());
        assertEquals(11, poDO.getInt("poID"));
        // verify that original indexed setting was modified
        assertEquals(aProperty, aSequence.getProperty(propIndex));
        assertEquals(11, aSequence.getValue(propIndex));
        // last index should not be the poID property (nothing added at the end)
        assertNotSame(aProperty, aSequence.getProperty(aSequence.size() - 1));
    }
    
    public void testModifySimpleSingleViaDataObjectSetNull() {
    	
    }
    public void testModifySimpleSingleViaSequenceSetValue() {
    	
    }
    public void testModifySimpleSingleViaSequenceSetValueNull() {
    	
    }
    public void testDontModifySimpleSingleViaSequenceAddThrowsException() {
    	// make a source object
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        int sequenceSizeBefore = aSequence.size();
        DataObject poDO = (DataObject)root.get(PO_SEQUENCE_PATH);
        assertNotNull(poDO);
        // get property to modify
        Property aProperty = poDO.getInstanceProperty("poID");
        assertNotNull(aProperty);
        
        boolean threwException = false;
        int errorCode = -1;
        try {
        	// modify the object and its sequence setting and observe
        	//poDO.set("poID", 11); // from 10
        	aSequence.add(aProperty, 11);
        	
        	// size has increased and we now have two instances of the setting - JIRA 242
        	assertEquals(sequenceSizeBefore + 1, aSequence.size());
        } catch (SDOException e) {
        	//SDOExceptionResource { "45018", "Adding a duplicate entry for the complex single setting [{1}] into a sequence at position [{0}] is not supported." }        	
        	threwException = true;
        	errorCode = e.getErrorCode();
        }
        assertTrue(threwException);
        assertEquals(45018, errorCode);
    }
    
    public void testDontModifySimpleSingleViaSequenceAddNull() {
    	
    }
    
    public void testModifyComplexSingleViaDataObjectSet() {
    	// make a source object
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        int sequenceSizeBefore = aSequence.size();
        DataObject poDO = (DataObject)root.get(PO_SEQUENCE_PATH);
        assertNotNull(poDO);
        
        // modify the object and its sequence setting and observe
        int nextPOID = 11;
        poDO.set(PO_POID_NAME, nextPOID); // from 10
        assertEquals(sequenceSizeBefore, aSequence.size());
        // verify the change was done on the dataObject
        int poid = poDO.getInt(PO_POID_NAME);
        assertEquals(nextPOID, poid);
        
        // verify the change was done on the sequence
        SDOSequence aPOSequence = (SDOSequence)poDO.getSequence();
        assertNotNull(aPOSequence);
        // get sequence id
        int sequenceIndex = getNthSequenceIndexFor(aPOSequence, PO_POID_NAME);  
        int poid2 = ((Integer)aPOSequence.getValue(sequenceIndex)).intValue();
        assertEquals(nextPOID, poid2);
        
    }
    
    public void testModifyComplexSingleViaDataObjectSetNull() {
    	
    }
    public void testModifyComplexSingleViaSequenceSetValue() {
    	
    }
    public void testModifyComplexSingleViaSequenceSetValueNull() {
    	
    }
    public void testDontModifyComplexSingleViaSequenceAdd() {
    	
    }
    public void testDontModifyComplexSingleViaSequenceAddNull() {
    	
    }
    
    
    /**
     * Purpose:
     * Test that the sequence on a move is updated on both the source and target objects
     */
    public void testMoveToCopyAlsoRemovesSequenceSettingOnOriginal() {
    	// make a source object
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        DataObject poDO = (DataObject)root.get(PO_SEQUENCE_PATH);
        assertNotNull(poDO);
        DataObject shipToDO = (DataObject)poDO.get("shipTo");
        assertNotNull(shipToDO);

        // make a copy so that we can move between trees
    	DataObject rootCopy = copyHelper.copy(root);    	
    	// check sequence was copied
        SDOSequence aPOSequenceCopy = getSequence(rootCopy, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        assertNotNull(aPOSequenceCopy);
        int sequenceCopySizeBeforeDelete = aPOSequenceCopy.size();
        
        // delete shipTo on target
        DataObject poDOCopy = (DataObject)rootCopy.get(PO_SEQUENCE_PATH);
        assertNotNull(poDO);
        SDODataObject shipToCopy = (SDODataObject)poDOCopy.get("shipTo");
        assertNotNull(shipToCopy);
        shipToCopy.delete();
        
        // verify that the sequence on the copy has been adjusted
        int sequenceCopySizeAfterDelete = aPOSequenceCopy.size();
        assertEquals(sequenceCopySizeBeforeDelete - 1, sequenceCopySizeAfterDelete);
        // verify that the object was deleted on the copy in prep for a move from the root to this copy
        assertNull(rootCopy.get(PO_SEQUENCE_PATH + "/shipTo"));
        
    	// get shipTo sequence
    	int shipToSequenceIndex = getNthSequenceIndexFor(aSequence, "shipTo");
    	assertTrue(shipToSequenceIndex > -1);
    	DataObject shipToObjectFromSetting = (DataObject)aSequence.getValue(shipToSequenceIndex);
    	assertNotNull(shipToObjectFromSetting);
    	assertTrue(shipToObjectFromSetting == shipToDO);
    	
    	// move shipTo from root to copyRoot
        int sequenceSizeBeforeMove = aSequence.size();
        int sequenceCopySizeBeforeMove = aPOSequenceCopy.size();
    	poDOCopy.set("shipTo", shipToDO);

        // verify that the sequence on the copy has been increased and on the original has been decreased
        int sequenceCopySizeAfterMove = aPOSequenceCopy.size();
        int sequenceSizeAfterMove = aSequence.size();
        assertEquals(sequenceCopySizeBeforeMove + 1, sequenceCopySizeAfterMove);
        assertEquals(sequenceSizeBeforeMove - 1, sequenceSizeAfterMove);
        // verify that the object was deleted on the copy in prep for a move from the root to this copy
        assertNull(root.get(PO_SEQUENCE_PATH + "/shipTo"));
    }
    
    public void testRemoveAllEmptyComplexManyReferenceViaListWrapper() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        int sequenceSizeBefore = aSequence.size();
        DataObject itemDO1 = (DataObject)root.get(PO_SEQUENCE_PATH + "/item[1]");
        // after removal the items list indexes on the dataObject will shift down, get item[2] for later comparison
        DataObject itemDO2 = (DataObject)root.get(PO_SEQUENCE_PATH + "/item[2]");
        assertNotNull(itemDO1);
        assertNotNull(itemDO2);
        assertFalse(itemDO1 == itemDO2);
    	
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	ListWrapper existingList = (ListWrapper)po.get("item");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(2, listSizeBefore);
        
    	// get item from sequence
    	int item2SequenceIndex = getNthSequenceIndexFor(aSequence, "item", 2);
    	assertTrue(item2SequenceIndex > -1);
    	DataObject itemObjectFromSetting = (DataObject)aSequence.getValue(item2SequenceIndex);
    	assertNotNull(itemObjectFromSetting);
    	assertTrue(itemObjectFromSetting == itemDO2);
    	
    	// create a list of items to remove
    	List<DataObject> listToRemove = new ArrayList<DataObject>();
    	//for(Iterator<DataObject> i = existingList.iterator(); i.hasNext();) {
    		//listToRemove.add(i.next());	
    	//}
    	
    	int sizeListToRemove = listToRemove.size();

    	// remove entire list
    	existingList.removeAll(listToRemove);
    	assertEquals(sequenceSizeBefore - sizeListToRemove, aSequence.size());
    	assertEquals(listSizeBefore - sizeListToRemove, existingList.size());
    	
    	// are we removed on the DataObject?
    	//DataObject item1ObjectFromAfterRemove = (DataObject)root.get(PO_SEQUENCE_PATH + "/item[1]");
    	// TODO: verify delete or dont delete after a sequence.remove
    	//assertTrue(item1ObjectFromAfterRemove == itemDO1);   
    	//assertFalse(item1ObjectFromAfterRemove == itemDO2);   
    }
    
    public void verifyAddAllAtIndexComplexManyByListWrapperAddPropertyOnExistingList(int addIndex) {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	ListWrapper existingList = (ListWrapper)po.get("item");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(2, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size();
    	// object to add
    	DataObject item3 = (DataObject)root.get("item[1]");
    	assertNotNull(item3);
    	
    	Property itemProperty = po.getInstanceProperty("item");
    	
    	// add list of items to existing list
    	List<DataObject> aListToAdd = new ArrayList<DataObject>();
    	aListToAdd.add(item3);
    	// add to the end of the list
    	existingList.addAll(addIndex,aListToAdd);

    	// verify that the list has increased on the do
    	int listSizeAfter = ((ListWrapper)po.get("item")).size();
    	assertEquals(listSizeBefore + aListToAdd.size(), listSizeAfter);

    	// verify that the sequence size has increased
    	assertEquals(indexToPlaceAtEnd + aListToAdd.size(), aSequence.size());
    	
    	// get back new Setting value
    	SDODataObject item2Value = (SDODataObject)aSequence.getValue(indexToPlaceAtEnd + aListToAdd.size() - 1);
    	assertNotNull(item2Value);
    	assertEquals(item2Value, aListToAdd.get(0));
    	assertNotNull(aSequence.getProperty(indexToPlaceAtEnd));
    	// check increased size of sequence
    	assertEquals(PO_SEQUENCE_TREE_SIZE + aListToAdd.size(), aSequence.size());
    	
    	// verify that DataObject has changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(treeSizeBeforeAdd + aListToAdd.size(), treeSizeAfterAdd);
    }
    
    public void testAddAllAtIndex0ComplexManyByListWrapperAddPropertyOnExistingList() {
    	verifyAddAllAtIndexComplexManyByListWrapperAddPropertyOnExistingList(0);
    }

    public void testAddAllAtIndex1ComplexManyByListWrapperAddPropertyOnExistingList() {
    	verifyAddAllAtIndexComplexManyByListWrapperAddPropertyOnExistingList(1);
    }
    /*
    public void testRemoveSimpleManyBySequence() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	ListWrapper existingList = (ListWrapper)po.get("comment");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(PO_COMMENT_LIST_SIZE, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size();
    	
    	Property aProperty = po.getInstanceProperty("comment");
    	
    	// add list of items to existing list
    	//List<String> aListToAdd = new ArrayList<String>();
    	//aListToAdd.add("comment 2");
    	//aListToAdd.add("comment 3");    	
    	
    	// remove entire list of comments
    	for(int i=0; i<listSizeBefore; i++) {
    		//int indexOfSetting = aSequence.getIndex(aProperty, existingList.get(i));
    		int indexOfSetting = getNthSequenceIndexFor(aSequence, aProperty.getName());
        	// get original setting
        	SDOSetting aSetting = (SDOSetting)aSequence.getSettings().get(indexOfSetting);

        	aSequence.remove(indexOfSetting);
        	
        	assertFalse(aSetting == aSequence.getSettings().get(indexOfSetting));
    	}

    	// verify that the list has decreased on the do
    	int listSizeAfter = ((ListWrapper)po.get("comment")).size();
    	assertEquals(0, listSizeAfter);

    	// verify that the sequence size has decreased
    	assertEquals(indexToPlaceAtEnd - listSizeBefore, aSequence.size());
    	
    	// verify that the sequence hash corresponding to the index to be removed is gone
    	// check decreased size of sequence
    	assertEquals(PO_SEQUENCE_TREE_SIZE - listSizeBefore, aSequence.size());
    	
    	// verify that DataObject has not changed for simple types
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(treeSizeBeforeAdd, treeSizeAfterAdd);
    }
    */
    public void testClearSimpleManyByListWrapper() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	ListWrapper existingList = (ListWrapper)po.get("comment");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(PO_COMMENT_LIST_SIZE, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size();
    	
    	Property itemProperty = po.getInstanceProperty("comment");
    	
    	// add list of items to existing list
    	List<String> aListToAdd = new ArrayList<String>();
    	aListToAdd.add("comment 2");
    	aListToAdd.add("comment 3");
    	
    	existingList.clear();

    	// verify that the list has decreased on the do
    	int listSizeAfter = ((ListWrapper)po.get("comment")).size();
    	assertEquals(0, listSizeAfter);

    	// verify that the sequence size has decreased
    	assertEquals(indexToPlaceAtEnd - listSizeBefore, aSequence.size());
    	
    	// check decreased size of sequence
    	assertEquals(PO_SEQUENCE_TREE_SIZE - listSizeBefore, aSequence.size());
    	
    	// verify that DataObject has not changed for simple types
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(treeSizeBeforeAdd, treeSizeAfterAdd);
    }
    
    public void testClearComplexManyReferenceByListWrapper() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	ListWrapper existingList = (ListWrapper)po.get("item");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(2, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size();
    	// object to add
    	DataObject item3 = (DataObject)root.get("item[1]");
    	assertNotNull(item3);
    	
    	Property itemProperty = po.getInstanceProperty("item");
    	
    	// add list of items to existing list
    	List<DataObject> aListToAdd = new ArrayList<DataObject>();
    	aListToAdd.add(item3);
    	aListToAdd.add(item3);
    	
    	existingList.clear();

    	// verify that the list has decreased on the do
    	int listSizeAfter = ((ListWrapper)po.get("item")).size();
    	assertEquals(listSizeBefore - aListToAdd.size(), listSizeAfter);

    	// verify that the sequence size has decreased
    	assertEquals(indexToPlaceAtEnd - aListToAdd.size(), aSequence.size());
    	
    	// check decreased size of sequence
    	assertEquals(PO_SEQUENCE_TREE_SIZE - aListToAdd.size(), aSequence.size());
    	
    	// verify that DataObject has changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(treeSizeBeforeAdd - aListToAdd.size(), treeSizeAfterAdd);
    }
    
    // SDOSequence and SDODataObject bidirectional testing
    public void testDeleteAlsoRemovesSequenceSetting() {
    	defineAndLoadRoot(false, false);
    	// get sequence po
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	assertNotNull(po);
        assertTrue(po.getType().isSequenced());
        DataObject shipToDO = (DataObject)po.get("shipTo");
        assertNotNull(shipToDO);
        SDOSequence aSequence = (SDOSequence)po.getSequence();
    	assertNotNull(aSequence);
    	// 4 instead of 5 because the one attribute property cannot be sequenced
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());
    	
    	// get shipTo sequence
    	int shipToSequenceIndex = getNthSequenceIndexFor(aSequence, "shipTo");
    	assertTrue(shipToSequenceIndex > -1);
    	DataObject shipToObjectFromSetting = (DataObject)aSequence.getValue(shipToSequenceIndex);
    	assertNotNull(shipToObjectFromSetting);
    	assertTrue(shipToObjectFromSetting == shipToDO);
    	
    	// delete po[1]/shipTo
    	shipToDO.delete();
    	
    	// are we removed on the sequence?
    	int shipToSequenceIndexAfterDelete = getNthSequenceIndexFor(aSequence, "shipTo");
    	assertEquals(NO_MATCH_INDEX, shipToSequenceIndexAfterDelete);
    	//DataObject shipToObjectFromSettingAfterDelete = (DataObject)aSequence.getValue(getSequenceIndexFor(aSequence, "shipTo"));
    	//assertNull(shipToObjectFromSettingAfterDelete);    	
    }

    public void testUnsetAlsoRemovesSequenceSetting() {
    	defineAndLoadRoot(false, false);
    	// get sequence po
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	assertNotNull(po);
        assertTrue(po.getType().isSequenced());
        DataObject shipToDO = (DataObject)po.get("shipTo");
        assertNotNull(shipToDO);
        SDOSequence aSequence = (SDOSequence)po.getSequence();
    	assertNotNull(aSequence);
    	// 4 instead of 5 because the one attribute property cannot be sequenced
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());
    	
    	// get shipTo sequence
    	int shipToSequenceIndex = getNthSequenceIndexFor(aSequence, "shipTo");
    	assertTrue(shipToSequenceIndex > -1);
    	DataObject shipToObjectFromSetting = (DataObject)aSequence.getValue(shipToSequenceIndex);
    	assertNotNull(shipToObjectFromSetting);
    	assertTrue(shipToObjectFromSetting == shipToDO);
    	
    	// delete po[1]/shipTo
    	po.unset("shipTo");
    	
    	// are we removed on the sequence?
    	int shipToSequenceIndexAfterDelete = getNthSequenceIndexFor(aSequence, "shipTo");
    	assertEquals(NO_MATCH_INDEX, shipToSequenceIndexAfterDelete);
    }

    public void testDetachAlsoRemovesSequenceSetting() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        DataObject shipToDO = (DataObject)root.get(PO_SEQUENCE_PATH + "/shipTo");
        assertNotNull(shipToDO);
    	
    	// get shipTo sequence
    	int shipToSequenceIndex = getNthSequenceIndexFor(aSequence, "shipTo");
    	assertTrue(shipToSequenceIndex > -1);
    	DataObject shipToObjectFromSetting = (DataObject)aSequence.getValue(shipToSequenceIndex);
    	assertNotNull(shipToObjectFromSetting);
    	assertTrue(shipToObjectFromSetting == shipToDO);
    	
    	// delete po[1]/shipTo
    	shipToDO.detach();
    	
    	// are we removed on the sequence?
    	int shipToSequenceIndexAfterDelete = getNthSequenceIndexFor(aSequence, "shipTo");
    	assertEquals(NO_MATCH_INDEX, shipToSequenceIndexAfterDelete);
    }

    public void testSetNullAlsoSetsSequenceSetting() {
    	
    }

    // TODO: Issue resolution 20070403-1: is remove local to Sequence - no
    public void testRemoveDoesDeleteDataObjectPropertyValue() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        DataObject shipToDO = (DataObject)root.get(PO_SEQUENCE_PATH + "/shipTo");
        assertNotNull(shipToDO);
    	
    	// get shipTo sequence
    	int shipToSequenceIndex = getNthSequenceIndexFor(aSequence, "shipTo");
    	assertTrue(shipToSequenceIndex > -1);
    	DataObject shipToObjectFromSetting = (DataObject)aSequence.getValue(shipToSequenceIndex);
    	assertNotNull(shipToObjectFromSetting);
    	assertTrue(shipToObjectFromSetting == shipToDO);
    	
    	// delete po[1]/shipTo from setting
    	aSequence.remove(shipToSequenceIndex);
    	
    	// are we removed on the DataObject?
    	DataObject shipToObjectFromAfterRemove = (DataObject)root.get(PO_SEQUENCE_PATH + "/shipTo");
    	assertNull(shipToObjectFromAfterRemove);   
    }
/*
    // TODO: tltest cannot pickup now SDOSequence function defs
    public void testRemoveIndexDoesDeleteDataObjectPropertyValue() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        DataObject shipToDO = (DataObject)root.get(PO_SEQUENCE_PATH + "/shipTo");
        assertNotNull(shipToDO);
    	
    	// get shipTo sequence
    	int shipToSequenceIndex = getNthSequenceIndexFor(aSequence, "shipTo");
    	assertTrue(shipToSequenceIndex > -1);
    	DataObject shipToObjectFromSetting = (DataObject)aSequence.getValue(shipToSequenceIndex);
    	assertNotNull(shipToObjectFromSetting);
    	assertTrue(shipToObjectFromSetting == shipToDO);
    	
    	// delete po[0] from setting
    	aSequence.remove(aSequence.getProperty(0).getName(), aSequence.getProperty(0).getType().getURI(),  true);
    	
    	// are we removed on the DataObject?
    	Object shipToObjectFromAfterRemove = root.get(PO_SEQUENCE_PATH + "/poID");
    	// TODO: verify delete or dont delete after a sequence.remove
    	assertNull(shipToObjectFromAfterRemove);   
    }
    */
    public void testRemoveIndex0ContainingSimpleSingleType() {
    	
    }
    public void testRemoveIndex0ContainingComplexSingleType() {
    	
    }
    public void testRemoveIndex0ContainingComplexManyType() {
    	
    }
    public void testRemoveIndex0ContainingMultipleComplexManyType() {
    	
    }
    public void testRemoveIndex0ContainingSimpleManyType() {
    	
    }
    /**
     * SDOSequence specific unit tests
     */

    public void testSequenceConstructorWithNullDataObjectThrowsException() {
		// create a non-standard sequence - a null DataObject is invalid    	
    	boolean exceptionThrown = false;
    	SDOSequence aSequence = null;
    	int errorCode = -1;
    	try {
    		aSequence = new SDOSequence(null);
    	} catch (SDOException e) {
    		exceptionThrown = true;
    		errorCode = e.getErrorCode();
    	} finally {
        	assertTrue(exceptionThrown);
    		assertNull(aSequence);
    		assertEquals(45021, errorCode);
    	}
    }
    
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
    	defineAndLoadRoot(false, false);
        getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    }
    
    /**
     * Returns the value of a <code>Sequence</code> property identified by the specified path.
     * @param path the path to a valid object and property.
     * @return the <code>Sequence</code> value of the specified property.
     * @see #get(String)
     * @deprecated in 2.1.0.
     */
    public void test_SequenceReturnFrom_SDODataObject_getSequence_StringFails() {
    	defineAndLoadRoot(false, false);
    	assertNotNull(root);
    	boolean exceptionThrown = false;
    	SDOSequence aSequence = null;
        int errorCode = -1;            	
    	try {
    		aSequence = (SDOSequence)root.getSequence(PO_PATH);
    	} catch (SDOException e) {
    		exceptionThrown = true;
        	errorCode = e.getErrorCode();
    	} finally {
        	assertTrue(exceptionThrown);
    		assertNull(aSequence);
            assertEquals(45020, errorCode);    		
    	}
    }
    
    public void test_SequenceReturnFrom_SDODataObject_getSequence_StringPasses() {
    	defineAndLoadRoot(false, false);
    	assertNotNull(root);
        SDOSequence aSequence = (SDOSequence)root.getSequence(PO_SEQUENCE_PATH);
        // check sequence
        assertNotNull(aSequence);
        assertEquals(PO_SEQUENCE_SIZE, aSequence.size());
    }
    
    /**
     * Returns the value of a <code>Sequence</code> property identified by the specified property index.
     * @param propertyIndex the index of the property.
     * @return the <code>Sequence</code> value of the specified property.
     * @see #get(int)
     * @deprecated in 2.1.0.
     */
    public void test_SequenceReturnFrom_SDODataObject_getSequence_index_type_complex_many() {
    	defineAndLoadRoot(false, false);
    	assertNotNull(root);
    	// get property index of porder
    	int propertyIndex = ((SDOProperty)root.getInstanceProperty(PO_PATH)).getIndexInType();
    	// Expect a failure for a complex many property
    	boolean exceptionThrown = false;
    	SDOSequence aSequence = null;
        int errorCode = -1;
    	try {
    		aSequence = (SDOSequence)root.getSequence(propertyIndex);
    	} catch (SDOException e) {
    		exceptionThrown = true;
        	errorCode = e.getErrorCode();
    	} finally {
        	assertTrue(exceptionThrown);
    		assertNull(aSequence);
            assertEquals(45022, errorCode);
    	}
    }

    public void test_SequenceReturnFrom_SDODataObject_getSequence_index_type_simple_many() {
    	defineAndLoadRoot(false, false);
    	assertNotNull(root);
    	// get property index of porder
    	int propertyIndex = ((SDOProperty)root.getInstanceProperty(PO_PATH)).getIndexInType();
    	assertEquals(2, propertyIndex);
    	// Expect a failure for a many property
    	boolean exceptionThrown = false;
    	SDOSequence aSequence = null;
        int errorCode = -1;
    	try {
    		aSequence = (SDOSequence)root.getSequence(propertyIndex);
    	} catch (SDOException e) {
    		exceptionThrown = true;
        	errorCode = e.getErrorCode();
    	} finally {
        	assertTrue(exceptionThrown);
    		assertNull(aSequence);
            assertEquals(45022, errorCode);
    	}
    	//verifySequenceIntegrity(aSequence, root.getInstanceProperty(PO_PATH));
    }

    public void test_SequenceReturnFrom_SDODataObject_getSequence_index_type_simple_single() {
    	defineAndLoadRoot(false, false);
    	assertNotNull(root);
    	// get property index of porder
    	int propertyIndex = ((SDOProperty)root.getInstanceProperty(PO_PATH)).getIndexInType();
    	// Expect a failure for a many property
    	boolean exceptionThrown = false;
    	SDOSequence aSequence = null;
        int errorCode = -1;
    	try {
    		aSequence = (SDOSequence)root.getSequence(propertyIndex);
    	} catch (SDOException e) {
    		exceptionThrown = true;
        	errorCode = e.getErrorCode();
    	} finally {
        	assertTrue(exceptionThrown);
    		assertNull(aSequence);
            assertEquals(45022, errorCode);
    	}
    	//verifySequenceIntegrity(aSequence, root.getInstanceProperty(PO_PATH));
    }

    // TODO: NEED SINGLE COMPLEX WITH COMPLEX CHILDREN
    public void test_SequenceReturnFrom_SDODataObject_getSequence_index_type_complex_single() {
    	defineAndLoadRoot(false, false);
    	assertNotNull(root);
    	// get property index of porder
    	int propertyIndex = ((SDOProperty)root.getInstanceProperty(CUSTOMER_PATH)).getIndexInType();
        SDOSequence aSequence = (SDOSequence)root.getSequence(propertyIndex);
//    	verifySequenceIntegrity(aSequence, root.getInstanceProperty(CUSTOMER_PATH));
    }

    /**
     * Returns the value of the specified <code>Sequence</code> property.
     * @param property the property to get.
     * @return the <code>Sequence</code> value of the specified property.
     * @see #get(Property)
     * @deprecated in 2.1.0.
     */
    public void test_SequenceReturnFrom_SDODataObject_getSequence_Property_type_single_many() { 
    	defineAndLoadRoot(false, false);
    	assertNotNull(root);
    	// get property index of porder
    	Property aPOProperty = root.getInstanceProperty(PO_PATH);
    	// Expect a failure for a single many property
    	SDOSequence aSequence = null;
    	boolean exceptionThrown = false;
        int errorCode = -1;
    	try {
        	aSequence = (SDOSequence)root.getSequence(aPOProperty);
    	} catch (SDOException e) {
    		exceptionThrown = true;
        	errorCode = e.getErrorCode();
    	} finally {
        	assertTrue(exceptionThrown);
    		assertNull(aSequence);
            assertEquals(45022, errorCode);
    	}
    }

    public void test_SequenceReturnFrom_SDODataObject_getSequence_Property_type_complex_many() { 
    	defineAndLoadRoot(false, false);
    	assertNotNull(root);
    	// get property index of porder
    	Property aPOProperty = root.getInstanceProperty(PO_PATH);
    	// Expect a failure for a complex many property
    	SDOSequence aSequence = null;
    	boolean exceptionThrown = false;
        int errorCode = -1;
    	try {
        	aSequence = (SDOSequence)root.getSequence(aPOProperty);
    	} catch (SDOException e) {
    		exceptionThrown = true;
        	errorCode = e.getErrorCode();
    	} finally {
        	assertTrue(exceptionThrown);
    		assertNull(aSequence);
            assertEquals(45022, errorCode);
    	}
    }

    public void test_SequenceReturnFrom_SDODataObject_getSequence_Property_type_simple_single() { 
    	defineAndLoadRoot(false, false);
    	assertNotNull(root);
    	// get property index of porder
    	Property aPOProperty = root.getInstanceProperty(PO_PATH);
    	// Expect a failure for a simple single property
    	SDOSequence aSequence = null;
    	boolean exceptionThrown = false;
        int errorCode = -1;
    	try {
        	aSequence = (SDOSequence)root.getSequence(aPOProperty);
    	} catch (SDOException e) {
    		exceptionThrown = true;
        	errorCode = e.getErrorCode();
    	} finally {
        	assertTrue(exceptionThrown);
    		assertNull(aSequence);
            assertEquals(45022, errorCode);
    	}
    }

    public void test_SequenceReturnFrom_SDODataObject_getSequence_Property_type_complex_single() { 
    	defineAndLoadRoot(false, false);
    	//setTypeSequenced(CUSTOMER_TYPENAME, true);
    	assertNotNull(root);
    	// get property index of porder
    	Property aCustomerProperty = root.getInstanceProperty(CUSTOMER_PATH);
    	// Expect no failure for a complex single property
    	SDOSequence aSequence = null;
    	boolean exceptionThrown = false;
        int errorCode = -1;
    	try {
        	aSequence = (SDOSequence)root.getSequence(aCustomerProperty);
    	} catch (SDOException e) {
    		exceptionThrown = true;
        	errorCode = e.getErrorCode();
    	} finally {
        	assertFalse(exceptionThrown);
    		assertNotNull(aSequence);
            //assertEquals(45022, errorCode);
    	}
   	}
    
    private boolean verifySequenceIntegrity(SDOSequence aSequence, Property aProperty) {
    	assertNotNull(aSequence);
    	// verify that the # element properties (not attributes) are the same as in aProperty
    	
    	List<Property> sequencedProperties = new ArrayList<Property>();//InProperty;//aProperty.getInstanceProperties()
    	for(Iterator i = aProperty.getInstanceProperties().iterator(); i.hasNext();) {
    		Property currentProperty = (Property)i.next();
    		if(currentProperty.getType().isSequenced()) {
    			sequencedProperties.add(currentProperty);
    		}    		
    	}
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());
    	return true;
    }
   
    public void testSDOSequence_constructor() {
    	// DataObject aDataObject) {
    	// get po
    	defineAndLoadRoot(false, false);
    	// get sequence po
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	assertNotNull(po);
        assertTrue(po.getType().isSequenced());
    	
    	SDOSequence aSequence = new SDOSequence((SDODataObject)po);
    	// verify size
    	assertEquals(0, aSequence.size());
    }

    /**
     * Returns the number of entries in the sequence.
     * @return the number of entries.
     */
    public void test_intReturnFrom_size() {
    }

    // create an open content property
    public void test_booleanReturnFrom_add_String_Object_createOpenContentProperty() {
    }
    
    /**
     * Adds a new entry with the specified property name and value
     * to the end of the entries.
     * @param propertyName the name of the entry's property.
     * @param value the value for the entry.
     */
    public void test_booleanReturnFrom_add_String_Object() {
    	// String propertyName, Object value) {
    }
    
    /**
     * Adds a new entry with the specified property index and value
     * to the end of the entries.
     * @param propertyIndex the index of the entry's property.
     * @param value the value for the entry.
     */
    public void test_booleanReturnFrom_add_int_Object() {
    	// int propertyIndex, Object value) {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	int listSizeBefore = ((ListWrapper)po.get("item")).size();
    	assertEquals(2, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size();
    	// object to add
    	DataObject item2 = (DataObject)root.get("item[2]");
    	assertNotNull(item2);
    	
    	// get "item" property index
    	int propIndex = 0;
    	for(Iterator i=po.getInstanceProperties().iterator(); i.hasNext();) {
    		Property aProperty = (Property)i.next();
    		if(aProperty != null && aProperty.getName().equals("item")) {
    			break;
    		} else {
    			propIndex++;
    		}
    	}
    	// add to sequence
    	aSequence.add(propIndex, item2);
    	// get back new Setting value
    	//ListWrapper item2Value = (ListWrapper)aSequence.getValue(indexToPlaceAtEnd);
    	SDODataObject item2Value = (SDODataObject)aSequence.getValue(indexToPlaceAtEnd);    	
    	assertNotNull(item2Value);
    	assertNotNull(aSequence.getProperty(indexToPlaceAtEnd));
    	// check increased size of sequence
    	assertEquals(PO_SEQUENCE_SIZE + 1, aSequence.size());
    	
    	// verify that the list has increased
    	int listSizeAfter = ((ListWrapper)po.get("item")).size();
    	assertEquals(listSizeBefore + 1, listSizeAfter);
    	
    	// verify that DataObject has changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(treeSizeBeforeAdd + 1, treeSizeAfterAdd);
    }

    public void testAddListToSequence() {
    	// a do.set(List) should populate the sequence
    	
    }
    
    /**
     * Adds a new entry with the specified property and value
     * to the end of the entries.
     * @param property the property of the entry.
     * @param value the value for the entry.
     */
    public void test_booleanReturnFrom_add_Property_Object() {
    	// Property property, Object value) {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	int listSizeBefore = ((ListWrapper)po.get("item")).size();
    	assertEquals(2, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size();
    	// object to add
    	DataObject item2 = (DataObject)root.get("item[2]");
    	assertNotNull(item2);
    	
    	Property itemProperty = po.getInstanceProperty("item");
    	
    	// add to sequence
    	aSequence.add(itemProperty, item2);
    	// get back new Setting value
    	//ListWrapper item2Value = (ListWrapper)aSequence.getValue(indexToPlaceAtEnd);
    	SDODataObject item2Value = (SDODataObject)aSequence.getValue(indexToPlaceAtEnd);
    	assertNotNull(item2Value);
    	assertNotNull(aSequence.getProperty(indexToPlaceAtEnd));
    	// check increased size of sequence
    	assertEquals(PO_SEQUENCE_TREE_SIZE + 1, aSequence.size());
    	
    	// verify that the list has increased
    	int listSizeAfter = ((ListWrapper)po.get("item")).size();
    	assertEquals(listSizeBefore + 1, listSizeAfter);
    	
    	// verify that DataObject has changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(treeSizeBeforeAdd + 1, treeSizeAfterAdd);
    }

    /**
     * Test setting a many property using a single item of a list instead of passing a
     * ListWrapper as usual.
     * The existing list will need to be null (not an empty ListWrapper)
     */
    public void test_booleanReturnFrom_add_Property_Object_WhereValuePassedIsSingleItemInEmptyList() {
    	// Property property, Object value) {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	int listSizeBefore = ((ListWrapper)po.get("item")).size();
    	assertEquals(2, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size();
    	// object to add
    	DataObject item2 = (DataObject)root.get("item[2]");
    	assertNotNull(item2);
    	
    	Property itemProperty = po.getInstanceProperty("item");
    	
    	// add to sequence
    	aSequence.add(itemProperty, item2);   	
    	
    	
    	
    	
    	// get back new Setting value
    	//ListWrapper item2Value = (ListWrapper)aSequence.getValue(indexToPlaceAtEnd);
    	SDODataObject item2Value = (SDODataObject)aSequence.getValue(indexToPlaceAtEnd);
    	assertNotNull(item2Value);
    	assertNotNull(aSequence.getProperty(indexToPlaceAtEnd));
    	// check increased size of sequence
    	assertEquals(PO_SEQUENCE_TREE_SIZE + 1, aSequence.size());
    	
    	// verify that the list has increased
    	int listSizeAfter = ((ListWrapper)po.get("item")).size();
    	assertEquals(listSizeBefore + 1, listSizeAfter);
    	
    	// verify that DataObject has changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(treeSizeBeforeAdd + 1, treeSizeAfterAdd);
    }
    
    public void testAddMultipleValuesIntoSequence() {
    }
/*    
    public List<SDOSetting> reflectiveGetList(Object receiver, String fieldName) {
    	List<SDOSetting> instanceField = null;
        try {
            Class cls = receiver.getClass();
            //Field field = cls.getField(fieldName);
            Field field = cls.getDeclaredField(fieldName);            
            // override default security settings for private field access from another class
            field.setAccessible(true); 
            instanceField = (List<SDOSetting>)field.get(receiver);
          } catch (Throwable e) {
             System.err.println(e);
          }
          return instanceField;
    }

    public DataObject reflectiveGetDataObject(Object receiver, String fieldName) {
    	DataObject instanceField = null;
        try {
            Class cls = receiver.getClass();
            //Field field = cls.getField(fieldName);
            Field field = cls.getDeclaredField(fieldName);            
            // override default security settings for private field access from another class
            field.setAccessible(true); 
            instanceField = (DataObject)field.get(receiver);
          } catch (Throwable e) {
             System.err.println(e);
          }
          return instanceField;
    }
*/    
/*    public boolean compareSequences(SDOSequence aSequence, SDOSequence aSequenceCopy, int size, boolean isDeep) {
        // the sequence may be a new object but may be the same if we compare a sequence to itself
        int sequenceSize = aSequence.size();
        assertEquals(size, sequenceSize);
        assertEquals(aSequence.size(), aSequenceCopy.size());
        // the settings inside the sequence must be new objects
        SDOSetting originalSetting = null;
        SDOSetting copySetting = null;
        List<SDOSetting> originalSettingsList = aSequence.getSettings();
        assertNotNull(originalSettingsList);
        assertEquals(sequenceSize, originalSettingsList.size());
        List copySettingsList = aSequenceCopy.getSettings();
        assertNotNull(copySettingsList);
        assertEquals(sequenceSize, copySettingsList.size());

        Property originalProperty = null;
        Property copyProperty = null;

        for(int index = 0, size2 = aSequence.size(); index <  size2; index++) {
        	originalSetting = originalSettingsList.get(index);
        	copySetting = (SDOSetting)copySettingsList.get(index);

            // dataObject back pointer may be distinct
//            DataObject originalDO = reflectiveGetDataObject(aSequence, "dataObject");
//            DataObject copyDO = reflectiveGetDataObject(aSequenceCopy, "dataObject");
        	
        	// each setting may be a new object
        	//assertFalse(originalSetting == copySetting);
            // the property field on the setting must point to the same property instance as the original
        	// check names before instances
            originalProperty = originalSetting.getProperty();
            copyProperty = copySetting.getProperty();
            
            // we must handle null properties that represent unstructured text
            // both null = unstructured
            // one null = invalid state (return not equal)
            // both !null = valid state (check equality)
           	if((null == originalProperty && null != copyProperty) || (null != originalProperty && null == copyProperty)) {
           		assertTrue(false);
            }
           	
           	if(null != originalProperty) {
           		assertEquals(originalProperty.getName(), copyProperty.getName());
           		assertEquals(originalProperty.hashCode(), copyProperty.hashCode());
           		// the value field on the setting must point the the deep copy value instance        	
           		// the property field on the setting must point to the same property instance as the original
           		if(originalProperty != copyProperty) {
           			assertTrue(false);
           		}
           	}
        	// for unstructuredText (null property) and simple dataTypes we check equality directly
/*        	if(null != originalProperty && originalProperty.getType().isDataType()) {        		
        		if(!originalSetting.getValue().equals(copySetting.getValue())) { // we can also use !.equals()
        			assertTrue(false);
        		}
        	} else {
        		// For complex types
            	// we do not need to check deep equality on dataObjects twice here, just check instances
        		// because the dataObject compare will iterate all the properties of each dataObject
        		// only compare DataObjects when in a  deep equal
                if (isDeep) {
                	// there are no lists in settings
                	Object originalValue = originalSetting.getValue();
                	Object copyValue = copySetting.getValue();                	
                	
                	if(null != originalValue && null != copyValue) {
                		if(originalProperty.isMany()) {
                			List aList = (List)originalValue;
                			List aListCopy = (List)copyValue;
                			for(int i = 0, lsize = aList.size(); i< lsize; i++) {                				
                				if(!equal((DataObject)aList.get(i), (DataObject)aListCopy.get(i))) {
                					return false;
                				}
                			}
                		} else {
                			if(!equal((DataObject)originalValue, (DataObject)copyValue)) {
                				return false;
                			}
                		}
                	}
                }
        	}
*//*        }
        return true;
    }
*/    
    public void testCopyDeepWithNoSequence() {
    	
    }
    
    public void testCopyShallowWithNoSequence() {
    }

    public void testCopyShallowWithDiffSequenceSimpleValue() {
    }
    
    public void testCopyDeepComplexManySequence() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	// copy po
    	DataObject rootCopy = (DataObject)copyHelper.copy(root);    	
    	// check sequence was copied
        SDOSequence aSequenceCopy = getSequence(rootCopy, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        assertNotNull(aSequenceCopy);
        assertEquals(aSequence.size(), aSequenceCopy.size());
        //compareSequences(aSequence, aSequenceCopy, PO_SEQUENCE_SIZE, true);
    }

    public void testCopyDeepComplexSingleSequence() {
    	
    }

    public void testCopyDeepSimpleManySequence() {
    	
    }

    public void testCopyDeepSimpleSingleSequence() {
    	
    }

    public void testEqualShallowSimpleSingleSequence() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        //aSequence.setValue(0, 5);        
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	// copy po
    	DataObject poCopy = copyHelper.copyShallow(po);    	
    	// check sequence was copied
        SDOSequence aSequenceCopy = (SDOSequence)poCopy.getSequence();
        assertNotNull(aSequenceCopy);
        //compareSequences(aSequence, aSequenceCopy, PO_SEQUENCE_SIZE, false);
        
        // test equalityHelper
    	//DataObject poCopy = (DataObject)rootCopy.get(PO_SEQUENCE_PATH);
        boolean bEqual = equalityHelper.equalShallow(po, poCopy);
        assertTrue(bEqual);
    }

    public void testEqualShallowSimpleSingleSequenceDifferentValue() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        //aSequence.setValue(0, 5);        
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	// copy po
    	DataObject poCopy = copyHelper.copyShallow(po);    	
    	// check sequence was copied
        SDOSequence aSequenceCopy = (SDOSequence)poCopy.getSequence();
        assertNotNull(aSequenceCopy);
        //compareSequences(aSequence, aSequenceCopy, PO_SEQUENCE_SIZE, false);

        // modify copy sequence
        aSequenceCopy.setValue(0, 5);

        // test equalityHelper
    	//DataObject poCopy = (DataObject)rootCopy.get(PO_SEQUENCE_PATH);
        boolean bEqual = equalityHelper.equalShallow(po, poCopy);
        assertFalse(bEqual);
    }
    
    public void testEqualShallowFailsSimpleSingleSequenceDifferentValueViaXSD() {
    	defineAndLoadRoot(true, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        //aSequence.setValue(0, 5);        
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	// copy po
    	DataObject poCopy = copyHelper.copyShallow(po);    	
    	// check sequence was copied
        SDOSequence aSequenceCopy = (SDOSequence)poCopy.getSequence();
        assertNotNull(aSequenceCopy);
        //compareSequences(aSequence, aSequenceCopy, PO_SEQUENCE_SIZE, false);
        
        // modify copy sequence
        aSequenceCopy.setValue(0, 5);
        // test equalityHelper
    	//DataObject poCopy = (DataObject)rootCopy.get(PO_SEQUENCE_PATH);
        boolean bEqual = equalityHelper.equalShallow(po, poCopy);
        assertFalse(bEqual);
    }

    public void testEqualShallowSimpleSingleSequenceDifferentUnstructuredTextValue() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        aSequence.addText("text1");        
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	// copy po
    	DataObject poCopy = copyHelper.copyShallow(po);    	
    	// check sequence was copied
        SDOSequence aSequenceCopy = (SDOSequence)poCopy.getSequence();
        assertNotNull(aSequenceCopy);
        //compareSequences(aSequence, aSequenceCopy, PO_TREE_SIZE + 1, false);
        
        // modify copy sequence
        aSequenceCopy.setValue(aSequenceCopy.size() -1, "text2");
        // test equalityHelper
    	//DataObject poCopy = (DataObject)rootCopy.get(PO_SEQUENCE_PATH);
        boolean bEqual = equalityHelper.equalShallow(po, poCopy);
        assertFalse(bEqual);
    }

    
    public void testDeepEqualWithNoSequence() {
    	
    }

    public void testEqualShallowWithNoSequence() {
    	
    }
    
    public void testEqualDeepComplexManySequence() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	// copy po
    	DataObject rootCopy = (DataObject)copyHelper.copy(root);    	
    	// check sequence was copied
        SDOSequence aSequenceCopy = getSequence(rootCopy, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        assertNotNull(aSequenceCopy);
        //compareSequences(aSequence, aSequenceCopy, PO_SEQUENCE_SIZE, true);
        
        // test equalityHelper
    	DataObject poCopy = (DataObject)rootCopy.get(PO_SEQUENCE_PATH);
        boolean bEqual = equalityHelper.equal(po, poCopy);
        assertTrue(bEqual);
    }

    public void testEqualDeepComplexManySequenceWithDifferentSequence2() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	// copy po
    	DataObject rootCopy = (DataObject)copyHelper.copy(root);    	
    	// check sequence was copied
        SDOSequence aSequenceCopy = getSequence(rootCopy, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        assertNotNull(aSequenceCopy);
        //compareSequences(aSequence, aSequenceCopy, PO_SEQUENCE_SIZE, true);
        
        // modify copy sequence
        aSequenceCopy.setValue(0, null);
        // test equalityHelper
    	DataObject poCopy = (DataObject)rootCopy.get(PO_SEQUENCE_PATH);
        boolean bEqual = equalityHelper.equal(po, poCopy);
        assertFalse(bEqual);
    }
    
    /*
//  variant test cases that should cause equality failure
    testSequenceOnCopyHasDifferentOrder
    testSequenceOnCopyIsEmpty
    testSequenceOnCopyIsLarger
    testSequenceOnCopyIsSmaller
    testSequenceOnCopyHasDifferentPropertyAtIndex
    testSequenceOnCopyHasDifferentValueAtIndex
    testSequenceOnOriginalHasDifferentOrder
    testSequenceOnOriginalIsEmpty
    testSequenceOnOriginalIsLarger
    testSequenceOnOriginalIsSmaller
    testSequenceOnOriginalHasDifferentPropertyAtIndex
    testSequenceOnOriginalHasDifferentValueAtIndex
*/
    
    public void testEqualDeepTrueAfterIsSequencedSetToFalseAfterDefineViaNonSpecMethod() {
    	defineAndLoadRoot(false, false);
        Sequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	// copy po
    	DataObject rootCopy = (DataObject)copyHelper.copy(root);    	
    	// check sequence was copied
        Sequence aSequenceCopy = getSequence(rootCopy, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        assertNotNull(aSequenceCopy);
        //compareSequences((SDOSequence)aSequence, (SDOSequence)aSequenceCopy, PO_SEQUENCE_SIZE, true);
        
        // test equalityHelper
    	DataObject poCopy = (DataObject)rootCopy.get(PO_SEQUENCE_PATH);
        boolean bEqual = equalityHelper.equal(po, poCopy);
        assertTrue(bEqual);

        // modify copy sequence state - non public method - both po and poCopy will have their state changed because they share an SDOType
        assertTrue(po.getType().isSequenced());
        assertTrue(poCopy.getType().isSequenced());
        // WARNING: Users should not be doing this - however it is supported
        ((SDOType)poCopy.getType()).setSequenced(false);
        assertFalse(po.getType().isSequenced());
        assertFalse(poCopy.getType().isSequenced());        
        bEqual = equalityHelper.equal(po, poCopy);
        // sequences will not be compared
        assertTrue(bEqual);
    }

    public void testSequencesStillNullAfterIsSequencedSetToTrueOutOfSpec() {
    	// create a dataObject with no sequence - the root
    	defineAndLoadRoot(false, false);
        Sequence aSequence = root.getSequence();
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	// copy po
    	DataObject rootCopy = copyHelper.copy(root);
    	// verify we are using the same context
    	//assertEquals(helperContext, ((SDOCopyHelper)copyHelper).getHelperContext())
    	// check sequence was copied
        SDOSequence aSequenceCopy = getSequence(rootCopy, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        assertNotNull(aSequenceCopy);
        //compareSequences(aSequence, aSequenceCopy, PO_SEQUENCE_SIZE, true);
        
        // test equalityHelper
    	DataObject poCopy = (DataObject)rootCopy.get(PO_SEQUENCE_PATH);
        boolean bEqual = equalityHelper.equal(po, poCopy);
        assertTrue(bEqual);

        // modify copy sequence state - non public method - both po and poCopy will have their state changed because they share an SDOType
        assertTrue(po.getType().isSequenced());
        assertTrue(poCopy.getType().isSequenced());
        // WARNING: Users should not be doing this - however it is supported
        ((SDOType)poCopy.getType()).setSequenced(false);
        assertFalse(po.getType().isSequenced());
        assertFalse(poCopy.getType().isSequenced());        
        bEqual = equalityHelper.equal(po, poCopy);
        // sequences will not be compared
        assertTrue(bEqual);
    	
    	// turn on isSequenced - not available via spec interface - do not use
    	// copy
    	
    	// check equality
    	
    }
    
    public void testEqualDeepComplexSingleSequence() {
    	
    }

    public void testEqualDeepSimpleManySequence() {
    	
    }

    public void testEqualDeepSimpleSingleSequence() {
    	
    }

    
//  remove via sequence
    public void testRemoveSimpleSingleByIndex() {
    	
    }
    public void testRemoveSimpleManyByIndex() {
    	
    }
    
    public void testRemoveComplexSingleByIndex() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        DataObject shipToDO = (DataObject)root.get(PO_SEQUENCE_PATH + "/shipTo");
        assertNotNull(shipToDO);
    	
    	// get shipTo sequence
    	int shipToSequenceIndex = getNthSequenceIndexFor(aSequence, "shipTo");
    	assertTrue(shipToSequenceIndex > -1);
    	DataObject shipToObjectFromSetting = (DataObject)aSequence.getValue(shipToSequenceIndex);
    	assertNotNull(shipToObjectFromSetting);
    	assertTrue(shipToObjectFromSetting == shipToDO);
    	
    	// delete po[1]/shipTo from setting
    	aSequence.remove(shipToSequenceIndex);
    	
    	// are we removed on the DataObject?
    	DataObject shipToObjectFromAfterRemove = (DataObject)root.get(PO_SEQUENCE_PATH + "/shipTo");
    	// TODO: verify delete or dont delete after a sequence.remove
    	assertNull(shipToObjectFromAfterRemove);   
    }

    public void testRemoveUnstructuredTextByIndex() {
    	defineAndLoadRoot(false, false, true, true);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        DataObject poDO = (DataObject)root.get(PO_SEQUENCE_PATH);
        assertNotNull(poDO);        
        DataObject shipToDO = (DataObject)root.get(PO_SEQUENCE_PATH + "/shipTo");
        assertNotNull(shipToDO);
    	
    	// get shipTo sequence
    	int shipToSequenceIndex = getNthSequenceIndexFor(aSequence, "shipTo");
    	assertTrue(shipToSequenceIndex > -1);
    	DataObject shipToObjectFromSetting = (DataObject)aSequence.getValue(shipToSequenceIndex);
    	assertNotNull(shipToObjectFromSetting);
    	assertTrue(shipToObjectFromSetting == shipToDO);
    	
    	// add unstructured text setting to sequence before we remove it.
    	ChangeSummary rootCS = root.getChangeSummary();
    	assertNotNull(rootCS);
    	rootCS.endLogging();
    	
    	aSequence.addText("unstructured1");
    	rootCS.beginLogging();
    	
    	int sequenceSize = aSequence.size();
    	
    	// delete po[1]/shipTo from setting
    	//aSequence.remove(shipToSequenceIndex);
    	aSequence.remove(sequenceSize - 1);
    	
    	// are we removed on the DataObject?
    	//DataObject shipToObjectFromAfterRemove = (DataObject)root.get(PO_SEQUENCE_PATH + "/shipTo");
    	assertEquals(sequenceSize - 1, aSequence.size());
    	
    	// TODO: verify delete or dont delete after a sequence.remove
    	//assertNull(shipToObjectFromAfterRemove);
    	// assert dataObject is unchanged
    	assertEquals(1, ((SDOChangeSummary)rootCS).getChangedDataObjects().size());
    	
    	// undo
    	rootCS.undoChanges();
    	
    	SDOSequence undoneSequence = (SDOSequence)poDO.getSequence();
    	// get new sequence object (different from original)
    	assertEquals(sequenceSize, undoneSequence.size());
    	assertEquals("unstructured1", undoneSequence.getValue(undoneSequence.size() - 1));   	
    }

    // purpose verify that the isDirty check on sequence holds so that we do not overwrite the original change    
    public void testAddAndModifyUnstructuredTextByIndex() {
    	defineAndLoadRoot(false, false, true, true);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        DataObject poDO = (DataObject)root.get(PO_SEQUENCE_PATH);
        assertNotNull(poDO);        
        DataObject shipToDO = (DataObject)root.get(PO_SEQUENCE_PATH + "/shipTo");
        assertNotNull(shipToDO);
    	
    	// get shipTo sequence
    	int shipToSequenceIndex = getNthSequenceIndexFor(aSequence, "shipTo");
    	assertTrue(shipToSequenceIndex > -1);
    	DataObject shipToObjectFromSetting = (DataObject)aSequence.getValue(shipToSequenceIndex);
    	assertNotNull(shipToObjectFromSetting);
    	assertTrue(shipToObjectFromSetting == shipToDO);
    	
    	// add unstructured text setting to sequence before we remove it.
    	ChangeSummary rootCS = root.getChangeSummary();
    	assertNotNull(rootCS);
    	rootCS.endLogging();

    	String originalUnstructuredValue = "unstructured1";
    	int sequenceSize = aSequence.size();
    	aSequence.addText(originalUnstructuredValue);    	
    	rootCS.beginLogging();
    	
    	// modify once (into cs)
    	aSequence.setValue(sequenceSize, "unstructured2");
    	
    	// modify twice (ignored)
    	aSequence.setValue(sequenceSize, "unstructured3");
    	
    	// are we added on the Sequence?
    	assertEquals(sequenceSize + 1, aSequence.size());
    	
    	// assert dataObject has changed
    	assertEquals(1, ((SDOChangeSummary)rootCS).getChangedDataObjects().size());
    	
    	assertEquals(sequenceSize + 1,  ((SDOSequence)poDO.getSequence()).size());    	
    	// undo
    	rootCS.undoChanges();
    	
    	SDOSequence undoneSequence = (SDOSequence)poDO.getSequence();
    	// verify that the undo did not decrease the size - only returned the setting to its original value
    	assertEquals(sequenceSize + 1, undoneSequence.size());
    	assertEquals(originalUnstructuredValue, undoneSequence.getValue(undoneSequence.size() - 1));   	
    }
    
    public void testRemoveComplexManyByIndex() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        int sequenceSizeBefore = aSequence.size();
        DataObject itemDO1 = (DataObject)root.get(PO_SEQUENCE_PATH + "/item[1]");
        // after removal the items list indexes on the dataObject will shift down, get item[2] for later comparison
        DataObject itemDO2 = (DataObject)root.get(PO_SEQUENCE_PATH + "/item[2]");
        assertNotNull(itemDO1);
        assertNotNull(itemDO2);
        assertFalse(itemDO1 == itemDO2);
    	
    	// get item from sequence
    	int item2SequenceIndex = getNthSequenceIndexFor(aSequence, "item", 2);
    	assertTrue(item2SequenceIndex > -1);
    	DataObject itemObjectFromSetting = (DataObject)aSequence.getValue(item2SequenceIndex);
    	assertNotNull(itemObjectFromSetting);
    	assertTrue(itemObjectFromSetting == itemDO2);
    	
    	// delete po[1]/item[2] from setting
    	aSequence.remove(item2SequenceIndex);
    	assertEquals(sequenceSizeBefore - 1, aSequence.size());
    	
    	// are we removed on the DataObject?
    	DataObject item1ObjectFromAfterRemove = (DataObject)root.get(PO_SEQUENCE_PATH + "/item[1]");
    	// TODO: verify delete or dont delete after a sequence.remove
    	assertTrue(item1ObjectFromAfterRemove == itemDO1);   
    	assertFalse(item1ObjectFromAfterRemove == itemDO2);   
    }

    public void testRemoveIndexComplexManyReferenceViaListWrapper() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        int sequenceSizeBefore = aSequence.size();
        DataObject itemDO1 = (DataObject)root.get(PO_SEQUENCE_PATH + "/item[1]");
        // after removal the items list indexes on the dataObject will shift down, get item[2] for later comparison
        DataObject itemDO2 = (DataObject)root.get(PO_SEQUENCE_PATH + "/item[2]");
        assertNotNull(itemDO1);
        assertNotNull(itemDO2);
        assertFalse(itemDO1 == itemDO2);
    	
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	ListWrapper existingList = (ListWrapper)po.get("item");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(2, listSizeBefore);
        
    	// get item from sequence
    	int item2SequenceIndex = getNthSequenceIndexFor(aSequence, "item", 2);
    	assertTrue(item2SequenceIndex > -1);
    	DataObject itemObjectFromSetting = (DataObject)aSequence.getValue(item2SequenceIndex);
    	assertNotNull(itemObjectFromSetting);
    	assertTrue(itemObjectFromSetting == itemDO2);
    	
    	// create a list of items to remove
    	List<DataObject> listToRemove = new ArrayList<DataObject>();
    	for(Iterator<DataObject> i = existingList.iterator(); i.hasNext();) {
    		listToRemove.add(i.next());	
    	}
    	
    	int sizeListToRemove = listToRemove.size();

    	// remove entire list
    	for(int i=0; i<listSizeBefore; i++) {
    		existingList.remove(0);
    	}
    	assertEquals(sequenceSizeBefore - sizeListToRemove, aSequence.size());
    	assertEquals(listSizeBefore - sizeListToRemove, existingList.size());
    	
    	// are we removed on the DataObject?
    	//DataObject item1ObjectFromAfterRemove = (DataObject)root.get(PO_SEQUENCE_PATH + "/item[1]");
    	// TODO: verify delete or dont delete after a sequence.remove
    	//assertTrue(item1ObjectFromAfterRemove == itemDO1);   
    	//assertFalse(item1ObjectFromAfterRemove == itemDO2);   
    }

    public void testRemoveIndexFirstDuplicateComplexManyReferenceViaListWrapper() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        int sequenceSizeBefore = aSequence.size();
        DataObject itemDO1 = (DataObject)root.get(PO_SEQUENCE_PATH + "/item[1]");
        // after removal the items list indexes on the dataObject will shift down, get item[2] for later comparison
        DataObject itemDO2 = (DataObject)root.get(PO_SEQUENCE_PATH + "/item[2]");
        assertNotNull(itemDO1);
        assertNotNull(itemDO2);
        assertFalse(itemDO1 == itemDO2);
    	
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	ListWrapper existingList = (ListWrapper)po.get("item");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(2, listSizeBefore);
        
    	// get item from sequence
    	int item2SequenceIndex = getNthSequenceIndexFor(aSequence, "item", 2);
    	assertTrue(item2SequenceIndex > -1);
    	DataObject itemObjectFromSetting = (DataObject)aSequence.getValue(item2SequenceIndex);
    	assertNotNull(itemObjectFromSetting);
    	assertTrue(itemObjectFromSetting == itemDO2);
    	
    	// create a list of items to remove
    	List<DataObject> listToRemove = new ArrayList<DataObject>();
    	for(Iterator<DataObject> i = existingList.iterator(); i.hasNext();) {
    		listToRemove.add(i.next());	
    	}
    	
    	int sizeListToRemove = listToRemove.size();

    	// remove entire list
    	for(int i=0; i<listSizeBefore; i++) {
    		existingList.remove(0);
    	}
    	assertEquals(sequenceSizeBefore - sizeListToRemove, aSequence.size());
    	assertEquals(listSizeBefore - sizeListToRemove, existingList.size());
    	
    	// are we removed on the DataObject?
    	//DataObject item1ObjectFromAfterRemove = (DataObject)root.get(PO_SEQUENCE_PATH + "/item[1]");
    	// TODO: verify delete or dont delete after a sequence.remove
    	//assertTrue(item1ObjectFromAfterRemove == itemDO1);   
    	//assertFalse(item1ObjectFromAfterRemove == itemDO2);   
    }

    /**
     * Unable to test removal of an index of a duplicated list because the updateContainment code now does not allow duplicate
     * containment dataobjects
     */
    public void testRemoveIndexFirstDuplicateComplexManyReferenceViaSequence() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        int sequenceSizeBefore = aSequence.size();
        DataObject itemDO1 = (DataObject)root.get(PO_SEQUENCE_PATH + "/item[1]");
        // after removal the items list indexes on the dataObject will shift down, get item[2] for later comparison
        DataObject itemDO2 = (DataObject)root.get(PO_SEQUENCE_PATH + "/item[2]");
        assertNotNull(itemDO1);
        assertNotNull(itemDO2);
        assertFalse(itemDO1 == itemDO2);
    	
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	ListWrapper existingList = (ListWrapper)po.get("item");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(2, listSizeBefore);
        
    	// get item from sequence
    	int item2SequenceIndex = getNthSequenceIndexFor(aSequence, "item", 2);
    	assertTrue(item2SequenceIndex > -1);
    	DataObject itemObjectFromSetting = (DataObject)aSequence.getValue(item2SequenceIndex);
    	assertNotNull(itemObjectFromSetting);
    	assertTrue(itemObjectFromSetting == itemDO2);
    	
    	// create a list of items to remove
    	List<DataObject> listToRemove = new ArrayList<DataObject>();
    	for(Iterator<DataObject> i = existingList.iterator(); i.hasNext();) {
    		listToRemove.add(i.next());	
    	}
    	
    	int sizeListToRemove = listToRemove.size();

    	// q2: remove(itemDO@123 = index 2) from seq(itemDO@123, shipToDO@234, itemDO@123, itemDO@123) = seq(itemDO@123, shipToDO@234, itemDO@123) - 3rd item removed
    	// invalid state - cannot reproduce
    	
    	// remove entire list
    	for(int i=0; i<listSizeBefore; i++) {
    		aSequence.remove(5);
    	}
    	assertEquals(sequenceSizeBefore - sizeListToRemove, aSequence.size());
    	assertEquals(listSizeBefore - sizeListToRemove, existingList.size());
    	
    	// are we removed on the DataObject?
    	//DataObject item1ObjectFromAfterRemove = (DataObject)root.get(PO_SEQUENCE_PATH + "/item[1]");
    	// TODO: verify delete or dont delete after a sequence.remove
    	//assertTrue(item1ObjectFromAfterRemove == itemDO1);   
    	//assertFalse(item1ObjectFromAfterRemove == itemDO2);   
    }
    
    public void testRemoveAllComplexManyReferenceViaListWrapper() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        int sequenceSizeBefore = aSequence.size();
        DataObject itemDO1 = (DataObject)root.get(PO_SEQUENCE_PATH + "/item[1]");
        // after removal the items list indexes on the dataObject will shift down, get item[2] for later comparison
        DataObject itemDO2 = (DataObject)root.get(PO_SEQUENCE_PATH + "/item[2]");
        assertNotNull(itemDO1);
        assertNotNull(itemDO2);
        assertFalse(itemDO1 == itemDO2);
    	
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	ListWrapper existingList = (ListWrapper)po.get("item");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(2, listSizeBefore);
        
    	// get item from sequence
    	int item2SequenceIndex = getNthSequenceIndexFor(aSequence, "item", 2);
    	assertTrue(item2SequenceIndex > -1);
    	DataObject itemObjectFromSetting = (DataObject)aSequence.getValue(item2SequenceIndex);
    	assertNotNull(itemObjectFromSetting);
    	assertTrue(itemObjectFromSetting == itemDO2);
    	
    	// create a list of items to remove
    	List<DataObject> listToRemove = new ArrayList<DataObject>();
    	for(Iterator<DataObject> i = existingList.iterator(); i.hasNext();) {
    		listToRemove.add(i.next());	
    	}
    	
    	int sizeListToRemove = listToRemove.size();

    	// remove entire list
    	existingList.removeAll(listToRemove);
    	assertEquals(sequenceSizeBefore - sizeListToRemove, aSequence.size());
    	assertEquals(listSizeBefore - sizeListToRemove, existingList.size());
    	
    	// are we removed on the DataObject?
    	//DataObject item1ObjectFromAfterRemove = (DataObject)root.get(PO_SEQUENCE_PATH + "/item[1]");
    	// TODO: verify delete or dont delete after a sequence.remove
    	//assertTrue(item1ObjectFromAfterRemove == itemDO1);   
    	//assertFalse(item1ObjectFromAfterRemove == itemDO2);   
    }

    public void testRemoveAllComplexManyViaListWrapper() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, "/", 5);
        int sequenceSizeBefore = aSequence.size();
        DataObject itemDO1 = (DataObject)root.get("item[1]");
        // after removal the items list indexes on the dataObject will shift down, get item[2] for later comparison
        DataObject itemDO2 = (DataObject)root.get("item[2]");
        assertNotNull(itemDO1);
        assertNotNull(itemDO2);
        assertFalse(itemDO1 == itemDO2);
    	
    	//DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	ListWrapper existingList = (ListWrapper)root.get("item");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(3, listSizeBefore);
        
    	// get item from sequence
    	int item2SequenceIndex = getNthSequenceIndexFor(aSequence, "item", 2);
    	assertTrue(item2SequenceIndex > -1);
    	DataObject itemObjectFromSetting = (DataObject)aSequence.getValue(item2SequenceIndex);
    	assertNotNull(itemObjectFromSetting);
    	assertTrue(itemObjectFromSetting == itemDO2);
    	
    	// create a list of items to remove
    	List<DataObject> listToRemove = new ArrayList<DataObject>();
    	for(Iterator<DataObject> i = existingList.iterator(); i.hasNext();) {
    		listToRemove.add(i.next());	
    	}
    	
    	int sizeListToRemove = listToRemove.size();

    	// remove entire list
    	existingList.removeAll(listToRemove);
    	assertEquals(sequenceSizeBefore - sizeListToRemove, aSequence.size());
    	assertEquals(listSizeBefore - sizeListToRemove, existingList.size());
    	
    	// are we removed on the DataObject?
    	//DataObject item1ObjectFromAfterRemove = (DataObject)root.get(PO_SEQUENCE_PATH + "/item[1]");
    	// TODO: verify delete or dont delete after a sequence.remove
    	//assertTrue(item1ObjectFromAfterRemove == itemDO1);   
    	//assertFalse(item1ObjectFromAfterRemove == itemDO2);
    	assertEquals(0, existingList.size());
    }
    
    public void testRemoveComplexManyReferenceViaListWrapperIndex() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        int sequenceSizeBefore = aSequence.size();
        DataObject itemDO1 = (DataObject)root.get(PO_SEQUENCE_PATH + "/item[1]");
        // after removal the items list indexes on the dataObject will shift down, get item[2] for later comparison
        DataObject itemDO2 = (DataObject)root.get(PO_SEQUENCE_PATH + "/item[2]");
        assertNotNull(itemDO1);
        assertNotNull(itemDO2);
        assertFalse(itemDO1 == itemDO2);
    	
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	ListWrapper existingList = (ListWrapper)po.get("item");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(2, listSizeBefore);
        
    	// get item from sequence
    	int item2SequenceIndex = getNthSequenceIndexFor(aSequence, "item", 2);
    	assertTrue(item2SequenceIndex > -1);
    	DataObject itemObjectFromSetting = (DataObject)aSequence.getValue(item2SequenceIndex);
    	assertNotNull(itemObjectFromSetting);
    	assertTrue(itemObjectFromSetting == itemDO2);
    	
    	// delete po[1]/item[2] from setting
    	existingList.remove(1);
    	assertEquals(sequenceSizeBefore - 1, aSequence.size());
    	
    	// are we removed on the DataObject?
    	DataObject item1ObjectFromAfterRemove = (DataObject)root.get(PO_SEQUENCE_PATH + "/item[1]");
    	// TODO: verify delete or dont delete after a sequence.remove
    	assertTrue(item1ObjectFromAfterRemove == itemDO1);   
    	assertFalse(item1ObjectFromAfterRemove == itemDO2);   
    }

    public void testRemoveComplexManyReferenceViaListWrapperReference() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        int sequenceSizeBefore = aSequence.size();
        DataObject itemDO1 = (DataObject)root.get(PO_SEQUENCE_PATH + "/item[1]");
        // after removal the items list indexes on the dataObject will shift down, get item[2] for later comparison
        DataObject itemDO2 = (DataObject)root.get(PO_SEQUENCE_PATH + "/item[2]");
        assertNotNull(itemDO1);
        assertNotNull(itemDO2);
        assertFalse(itemDO1 == itemDO2);
    	
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	ListWrapper existingList = (ListWrapper)po.get("item");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(2, listSizeBefore);
        
    	// get item from sequence
    	int item2SequenceIndex = getNthSequenceIndexFor(aSequence, "item", 2);
    	assertTrue(item2SequenceIndex > -1);
    	DataObject itemObjectFromSetting = (DataObject)aSequence.getValue(item2SequenceIndex);
    	assertNotNull(itemObjectFromSetting);
    	assertTrue(itemObjectFromSetting == itemDO2);
    	
    	// delete po[1]/item[2] from setting
    	existingList.remove(itemDO2);
    	assertEquals(sequenceSizeBefore - 1, aSequence.size());
    	
    	// are we removed on the DataObject?
    	DataObject item1ObjectFromAfterRemove = (DataObject)root.get(PO_SEQUENCE_PATH + "/item[1]");
    	// TODO: verify delete or dont delete after a sequence.remove
    	assertTrue(item1ObjectFromAfterRemove == itemDO1);   
    	assertFalse(item1ObjectFromAfterRemove == itemDO2);   
    }

    public void testRemoveComplexManyViaListWrapperReference() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, "/", 5);
        int sequenceSizeBefore = aSequence.size();
        DataObject itemDO1 = (DataObject)root.get("item[1]");
        // after removal the items list indexes on the dataObject will shift down, get item[2] for later comparison
        DataObject itemDO2 = (DataObject)root.get("item[2]");
        assertNotNull(itemDO1);
        assertNotNull(itemDO2);
        assertFalse(itemDO1 == itemDO2);
    	
    	ListWrapper existingList = (ListWrapper)root.get("item");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(3, listSizeBefore);
        
    	// get item from sequence
    	int item2SequenceIndex = getNthSequenceIndexFor(aSequence, "item", 2);
    	assertTrue(item2SequenceIndex > -1);
    	DataObject itemObjectFromSetting = (DataObject)aSequence.getValue(item2SequenceIndex);
    	assertNotNull(itemObjectFromSetting);
    	assertTrue(itemObjectFromSetting == itemDO2);
    	
    	// delete po[1]/item[2] from setting
    	existingList.remove(itemDO2);
    	assertEquals(sequenceSizeBefore - 1, aSequence.size());
    	
    	// are we removed on the DataObject?
    	DataObject item1ObjectFromAfterRemove = (DataObject)root.get("item[1]");
    	// TODO: verify delete or dont delete after a sequence.remove
    	assertTrue(item1ObjectFromAfterRemove == itemDO1);   
    	assertFalse(item1ObjectFromAfterRemove == itemDO2);   
    }

    
    /**
     * Test operations involving deep complex types that contain complex children
     *
     */
    public void testRemoveNullComplexManyWithComplexChildrenViaListWrapperReference() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, "/", 5);
        int sequenceSizeBefore = aSequence.size();
    	ListWrapper existingList = (ListWrapper)root.get("porder");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(1, listSizeBefore);
    	existingList.remove(null);
    	assertEquals(sequenceSizeBefore, aSequence.size());
    }

    public void testRemoveComplexManyWithComplexChildreViaListWrapperReference() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, "/", 5);
        int sequenceSizeBefore = aSequence.size();
        DataObject po1 = (DataObject)root.get("porder[1]");        
        assertNotNull(po1);
    	
    	ListWrapper existingList = (ListWrapper)root.get("porder");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(1, listSizeBefore);

    	existingList.remove(po1);
    	assertEquals(sequenceSizeBefore - 1, aSequence.size());
    	
    	// are we removed on the DataObject?
    	ListWrapper existingList2 = (ListWrapper)root.get("porder");  // same instance of existingList
    	int listSizeAfter = existingList2.size();
    	assertEquals(0, listSizeAfter);
    }
    
//     remove via dataobject (same as detach)
    public void testUnsetSimpleSingleByIndex() {
    	
    }
    public void testUnsetSimpleManyByIndex() {
    	
    }
    public void testUnsetComplexSingleByIndex() {
    	
    }
    public void testUnsetComplexManyByIndex() {
    	
    }

    public void testDeleteSimpleSingleByProperty() {
    	
    }
    public void testDeleteSimpleManyByProperty() {
    	
    }
    public void testDeleteComplexSingleByProperty() {
    	
    }
    public void testDeleteComplexManyByProperty() {
    	
    }

    public void testDetachSimpleSingleByProperty() {
    	
    }
    public void testDetachSimpleManyByProperty() {
    	
    }
    public void testDetachComplexSingleByProperty() {
    	
    }
    public void testDetachComplexManyByProperty() {
    	
    }

//     add via sequence

//     add via dataobject
    public void testAddSimpleSingleByDataObjectSetProperty() {
    	
    }
    public void testAddSimpleManyByDataObjectSetPropertyWithListOnEmptyList() {
    	
    }
    public void testAddSimpleManyByDataObjectSetPropertyWithListOnExistingList() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	int listSizeBefore = ((ListWrapper)po.get("comment")).size();
    	assertEquals(PO_COMMENT_LIST_SIZE, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size();
    	
    	Property commentProperty = po.getInstanceProperty("comment");
    	
    	// add list of items to sequence
    	List<String> aListToAdd = new ArrayList<String>();
    	aListToAdd.add("comment 2");
    	aListToAdd.add("comment 3");
    	aSequence.add(indexToPlaceAtEnd, commentProperty, aListToAdd);

    	// get back new Setting value
    	String comment2Value = (String)aSequence.getValue(indexToPlaceAtEnd);
    	assertNotNull(comment2Value);
    	assertEquals(comment2Value, aListToAdd.get(0));
    	assertNotNull(aSequence.getProperty(indexToPlaceAtEnd));
    	// check increased size of sequence for simple type
    	assertEquals(PO_SEQUENCE_TREE_SIZE + aListToAdd.size(), aSequence.size());
    	
    	// verify that the list has increased
    	int listSizeAfter = ((ListWrapper)po.get("comment")).size();
    	assertEquals(listSizeBefore + aListToAdd.size(), listSizeAfter);
    	
    	// verify that DataObject has changed but not the # of dataObjects
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(treeSizeBeforeAdd + 0, treeSizeAfterAdd);
    }

    public void testAddComplexSingleByDataObjectSetProperty() {
    }
    public void testAddComplexManyByDataObjectSetPropertyWithListOnEmptyList() {
    	
    }
    
    public void testAddComplexManyByDataObjectSetPropertyWithListOnExistingList() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	int listSizeBefore = ((ListWrapper)po.get("item")).size();
    	assertEquals(2, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size();
    	// object to add
    	DataObject item3 = (DataObject)root.get("item[1]");
    	assertNotNull(item3);
    	
    	Property itemProperty = po.getInstanceProperty("item");
    	
    	// add list of items to sequence
    	List<DataObject> aListToAdd = new ArrayList<DataObject>();
    	aListToAdd.add(item3);
    	aSequence.add(indexToPlaceAtEnd, itemProperty, aListToAdd);

    	// get back new Setting value
    	SDODataObject item2Value = (SDODataObject)aSequence.getValue(indexToPlaceAtEnd);
    	assertNotNull(item2Value);
    	assertEquals(item2Value, aListToAdd.get(0));
    	assertNotNull(aSequence.getProperty(indexToPlaceAtEnd));
    	// check increased size of sequence
    	assertEquals(PO_SEQUENCE_TREE_SIZE + 1, aSequence.size());
    	
    	// verify that the list has increased
    	int listSizeAfter = ((ListWrapper)po.get("item")).size();
    	assertEquals(listSizeBefore + aListToAdd.size(), listSizeAfter);
    	
    	// verify that DataObject has changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(treeSizeBeforeAdd + 1, treeSizeAfterAdd);
    }
    
    public void testAddComplexManyByDataObjectSetPropertyWithSingleItemOnEmptyList() {
    }
    
    public void testAddComplexManyByDataObjectSetPropertyWithSingleItemOnExistingList() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	int listSizeBefore = ((ListWrapper)po.get("item")).size();
    	assertEquals(2, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size();
    	// object to add
    	DataObject item3 = (DataObject)root.get("item[3]");
    	assertNotNull(item3);
    	
    	Property itemProperty = po.getInstanceProperty("item");
    	
    	// add to sequence
    	aSequence.add(indexToPlaceAtEnd, itemProperty, item3);
    	// get back new Setting value
    	//ListWrapper item2Value = (ListWrapper)aSequence.getValue(indexToPlaceAtEnd);
    	SDODataObject item2Value = (SDODataObject)aSequence.getValue(indexToPlaceAtEnd);
    	assertNotNull(item2Value);
    	assertNotNull(aSequence.getProperty(indexToPlaceAtEnd));
    	// check increased size of sequence
    	assertEquals(PO_SEQUENCE_TREE_SIZE + 1, aSequence.size());
    	
    	// verify that the list has increased
    	int listSizeAfter = ((ListWrapper)po.get("item")).size();
    	assertEquals(listSizeBefore + 1, listSizeAfter);
    	
    	// verify that DataObject has changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(treeSizeBeforeAdd + 1, treeSizeAfterAdd);
    }

//  add via ListWrapper
    public void testAddSimpleSingleByListWrapperAddProperty() {
    	
    }
    public void testAddSimpleManyByListWrapperAddProperty() {
    	
    }
    public void testAddComplexSingleByListWrapperAddProperty() {
    	
    }
    public void testAddComplexManyByListWrapperAddPropertyOnEmptyList() {
    	
    }

    public void testAddSimpleSingleAttributeFailsViaSequenceAdd() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, CUSTOMER_PATH, PO_SEQUENCE_SIZE);
    	DataObject cust = (DataObject)root.get(CUSTOMER_PATH);
    	Property custProperty = cust.getInstanceProperty("custID");
    	int errorCode = -1;
    	try {
    		aSequence.add(custProperty, "0001");    		
    	} catch (SDOException e) {
    		errorCode = e.getErrorCode();    		
    	}
    	assertEquals(SDOException.SEQUENCE_ERROR_PROPERTY_IS_ATTRIBUTE, errorCode);
    }
    
    public void testAddComplexManyByListWrapperAddPropertyOnExistingList() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	ListWrapper existingList = (ListWrapper)po.get("item");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(2, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size();
    	// object to add
    	DataObject item3 = (DataObject)root.get("item[1]");
    	assertNotNull(item3);
    	
    	Property itemProperty = po.getInstanceProperty("item");
    	
    	// add list of items to existing list
    	List<DataObject> aListToAdd = new ArrayList<DataObject>();
    	aListToAdd.add(item3);
    	aListToAdd.add(item3);
    	for(Iterator i = aListToAdd.iterator(); i.hasNext();) {
    		// add to the end of the list
    		existingList.add(i.next());
    	}

    	// verify that the list has increased on the do
    	int listSizeAfter = ((ListWrapper)po.get("item")).size();
    	assertEquals(listSizeBefore + aListToAdd.size(), listSizeAfter);

    	// verify that the sequence size has increased
    	assertEquals(indexToPlaceAtEnd + aListToAdd.size(), aSequence.size());
    	
    	// get back new Setting value
    	SDODataObject item2Value = (SDODataObject)aSequence.getValue(indexToPlaceAtEnd + aListToAdd.size() - 1);
    	assertNotNull(item2Value);
    	assertEquals(item2Value, aListToAdd.get(0));
    	assertNotNull(aSequence.getProperty(indexToPlaceAtEnd));
    	// check increased size of sequence
    	assertEquals(PO_SEQUENCE_TREE_SIZE + aListToAdd.size(), aSequence.size());
    	
    	// verify that DataObject has changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(treeSizeBeforeAdd + aListToAdd.size(), treeSizeAfterAdd);
    }

    public void testAddComplexManyByListWrapperAddAllPropertyOnExistingList() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	ListWrapper existingList = (ListWrapper)po.get("item");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(2, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size();
    	// object to add
    	DataObject item3 = (DataObject)root.get("item[1]");
    	assertNotNull(item3);
    	
    	Property itemProperty = po.getInstanceProperty("item");
    	
    	// add list of items to existing list
    	List<DataObject> aListToAdd = new ArrayList<DataObject>();
    	aListToAdd.add(item3);
    	aListToAdd.add(item3);
    	// add to the end of the list
    	existingList.addAll(aListToAdd);

    	// verify that the list has increased on the do
    	int listSizeAfter = ((ListWrapper)po.get("item")).size();
    	assertEquals(listSizeBefore + aListToAdd.size(), listSizeAfter);

    	// verify that the sequence size has increased
    	assertEquals(indexToPlaceAtEnd + aListToAdd.size(), aSequence.size());
    	
    	// get back new Setting value
    	SDODataObject item2Value = (SDODataObject)aSequence.getValue(indexToPlaceAtEnd + aListToAdd.size() - 1);
    	assertNotNull(item2Value);
    	assertEquals(item2Value, aListToAdd.get(0));
    	assertNotNull(aSequence.getProperty(indexToPlaceAtEnd));
    	// check increased size of sequence
    	assertEquals(PO_SEQUENCE_TREE_SIZE + aListToAdd.size(), aSequence.size());
    	
    	// verify that DataObject has changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(treeSizeBeforeAdd + aListToAdd.size(), treeSizeAfterAdd);
    }
    
    public void testAddAllComplexManyNonContainmentByListWrapperAddAllPropertyOnExistingList() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	ListWrapper existingList = (ListWrapper)po.get("item");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(2, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size();
    	// object to add
    	DataObject item3 = (DataObject)root.get("item[3]");
    	assertNotNull(item3);
    	
    	Property itemProperty = po.getInstanceProperty("item");
    	
    	// add list of items to existing list
    	List<DataObject> aListToAdd = new ArrayList<DataObject>();
    	aListToAdd.add(item3);
    	// add to the end of the list
    	existingList.addAll(aListToAdd);

    	// verify that the list has increased on the do
    	int listSizeAfter = ((ListWrapper)po.get("item")).size();
    	assertEquals(listSizeBefore + aListToAdd.size(), listSizeAfter);

    	// verify that the sequence size has increased
    	assertEquals(indexToPlaceAtEnd + aListToAdd.size(), aSequence.size());
    	
    	// get back new Setting value
    	SDODataObject item2Value = (SDODataObject)aSequence.getValue(indexToPlaceAtEnd + aListToAdd.size() - 1);
    	assertNotNull(item2Value);
    	assertEquals(item2Value, aListToAdd.get(0));
    	assertNotNull(aSequence.getProperty(indexToPlaceAtEnd));
    	// check increased size of sequence
    	assertEquals(PO_SEQUENCE_TREE_SIZE + aListToAdd.size(), aSequence.size());
    	
    	// verify that DataObject has changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(treeSizeBeforeAdd + aListToAdd.size(), treeSizeAfterAdd);
    }
/*
    public void testRemove1StOccurrenceComplexManyNonContainmentByListWrapperAddAllPropertyOnExistingList() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	ListWrapper existingList = (ListWrapper)po.get("item");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(2, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size();
    	// object to add
    	DataObject item3 = (DataObject)root.get("item[3]");
    	assertNotNull(item3);
    	
    	Property itemProperty = po.getInstanceProperty("item");
    	
    	// add list of items to existing list
    	List<DataObject> aListToAdd = new ArrayList<DataObject>();
    	aListToAdd.add(item3);
    	// add to the end of the list
    	existingList.addAll(aListToAdd);

    	// verify that the list has increased on the do
    	int listSizeAfter = ((ListWrapper)po.get("item")).size();
    	assertEquals(listSizeBefore + aListToAdd.size(), listSizeAfter);

    	// verify that the sequence size has increased
    	assertEquals(indexToPlaceAtEnd + aListToAdd.size(), aSequence.size());
    	
    	// get back new Setting value
    	SDODataObject item2Value = (SDODataObject)aSequence.getValue(indexToPlaceAtEnd + aListToAdd.size() - 1);
    	assertNotNull(item2Value);
    	assertEquals(item2Value, aListToAdd.get(0));
    	assertNotNull(aSequence.getProperty(indexToPlaceAtEnd));
    	// check increased size of sequence
    	assertEquals(PO_SEQUENCE_TREE_SIZE + aListToAdd.size(), aSequence.size());
    	
    	// verify that DataObject has changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(treeSizeBeforeAdd + aListToAdd.size(), treeSizeAfterAdd);
    	
    	// get sequence object that will be removed
    	SDOSetting aSetting5 = (SDOSetting)aSequence.getSettings().get(5);
    	SDOSetting aSetting6 = (SDOSetting)aSequence.getSettings().get(6);
    	// remove the first occurrence of the duplicated items in the sequence 
    	existingList.remove(1);
    	
    	// check size
    	assertEquals(indexToPlaceAtEnd, aSequence.size());
    	
    	// verify that the correct index in the sequence was removed based on the listWrapper index
    	//assertEquals(aSetting6, aSequence.getSettings().get(5)); // 6 shifted into 5's place
    	//assertFalse(aSetting5 == aSequence.getSettings().get(5)); // 5 is gone
    	assertTrue(aSetting5 == aSequence.getSettings().get(5)); // 5 is still there    	
    }

    public void testRemove2ndOccurrenceComplexManyNonContainmentByListWrapperAddAllPropertyOnExistingList() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	ListWrapper existingList = (ListWrapper)po.get("item");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(2, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size();
    	// object to add
    	DataObject item3 = (DataObject)root.get("item[3]");
    	assertNotNull(item3);
    	
    	Property itemProperty = po.getInstanceProperty("item");
    	
    	// add list of items to existing list
    	List<DataObject> aListToAdd = new ArrayList<DataObject>();
    	aListToAdd.add(item3);
    	// add to the end of the list
    	existingList.addAll(aListToAdd);

    	// verify that the list has increased on the do
    	int listSizeAfter = ((ListWrapper)po.get("item")).size();
    	assertEquals(listSizeBefore + aListToAdd.size(), listSizeAfter);

    	// verify that the sequence size has increased
    	assertEquals(indexToPlaceAtEnd + aListToAdd.size(), aSequence.size());
    	
    	// get back new Setting value
    	SDODataObject item2Value = (SDODataObject)aSequence.getValue(indexToPlaceAtEnd + aListToAdd.size() - 1);
    	assertNotNull(item2Value);
    	assertEquals(item2Value, aListToAdd.get(0));
    	assertNotNull(aSequence.getProperty(indexToPlaceAtEnd));
    	// check increased size of sequence
    	assertEquals(PO_SEQUENCE_TREE_SIZE + aListToAdd.size(), aSequence.size());
    	
    	// verify that DataObject has changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(treeSizeBeforeAdd + aListToAdd.size(), treeSizeAfterAdd);
    	
    	// get sequence object that will be removed
    	SDOSetting aSetting5 = (SDOSetting)aSequence.getSettings().get(5);
    	SDOSetting aSetting6 = (SDOSetting)aSequence.getSettings().get(6);
    	// remove the first occurrence of the duplicated items in the sequence 
    	existingList.remove(2);
    	
    	// check size
    	assertEquals(indexToPlaceAtEnd, aSequence.size());
    	
    	// verify that the correct index in the sequence was removed based on the listWrapper index
    	assertEquals(aSetting5, aSequence.getSettings().get(5)); // 6 shifted into 5's place
    	assertFalse(aSetting6 == aSequence.getSettings().get(5)); // 5 is gone
    }
*/
    // TODO: Verify that existing duplicates will be removed before the new list is added - addAll same as set
    public void testAddAllDuplicatesComplexManyByListWrapperAddAllPropertyOnExistingList() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, "/", 5);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(root).size();
    	assertEquals(10, treeSizeBeforeAdd);
    	assertEquals(5, aSequence.size());

    	ListWrapper existingList = (ListWrapper)root.get("item");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(3, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size();
    	// object to add
    	DataObject item3 = (DataObject)root.get("item[3]");
    	assertNotNull(item3);
    	
    	Property itemProperty = root.getInstanceProperty("item");
    	
    	// add list of items to existing list
    	List<DataObject> aListToAdd = new ArrayList<DataObject>();
    	aListToAdd.add(item3);
    	aListToAdd.add(item3);
    	aListToAdd.add(item3);
    	// add to the end of the list
    	existingList.addAll(aListToAdd);

    	// verify that the list has increased on the do
    	int listSizeAfter = ((ListWrapper)root.get("item")).size();

    	
    	// TODO: We should not be removing the existing item?
    	int newItems = aListToAdd.size() - 3;
    	//assertEquals(listSizeBefore + aListToAdd.size(), listSizeAfter);
    	assertEquals(listSizeBefore + newItems, listSizeAfter);

    	// verify that the sequence size has increased
    	assertEquals(indexToPlaceAtEnd + newItems, aSequence.size());
    	
    	// get back new Setting value
    	SDODataObject item2Value = (SDODataObject)aSequence.getValue(indexToPlaceAtEnd + newItems - 1);
    	assertNotNull(item2Value);
    	assertEquals(item2Value, aListToAdd.get(0));
    	assertNotNull(aSequence.getProperty(indexToPlaceAtEnd - 1));
    	// check increased size of sequence
    	assertEquals(indexToPlaceAtEnd + newItems, aSequence.size());
    	
    	// verify that DataObject has changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(root).size();
    	assertEquals(treeSizeBeforeAdd + newItems, treeSizeAfterAdd);
  	
    }
/*
    // TODO: Verify that existing duplicates will be removed before the new list is added - addAll same as set
    public void testRemove2ndOccurrenceSimpleManyByListWrapperAddAllPropertyOnExistingList() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	ListWrapper existingList = (ListWrapper)po.get("comment");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(PO_COMMENT_LIST_SIZE, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size();
    	Property commentProperty = po.getInstanceProperty("comment");
    	
    	// add list of items to existing list
    	List<String> aListToAdd = new ArrayList<String>();
    	aListToAdd.add(PO_COMMENT_VALUE1);
    	aListToAdd.add(PO_COMMENT_VALUE1);
    	aListToAdd.add(PO_COMMENT_VALUE1);
    	// add to the end of the list
    	existingList.addAll(aListToAdd);

    	// verify that the list has increased on the do
    	int listSizeAfter = ((ListWrapper)po.get("comment")).size();
    	
    	// TODO: We should not be removing the existing item?
    	int newItems = aListToAdd.size();
    	//assertEquals(listSizeBefore + aListToAdd.size(), listSizeAfter);
    	assertEquals(listSizeBefore + newItems, listSizeAfter);

    	// verify that the sequence size has increased
    	assertEquals(indexToPlaceAtEnd + newItems, aSequence.size());
    	
    	// get back new Setting value
    	String comment2Value = (String)aSequence.getValue(indexToPlaceAtEnd + newItems - 1);
    	assertNotNull(comment2Value);
    	assertEquals(comment2Value, aListToAdd.get(0));
    	assertNotNull(aSequence.getProperty(indexToPlaceAtEnd - 1));
    	// check increased size of sequence
    	assertEquals(indexToPlaceAtEnd + newItems, aSequence.size());
    	
    	// verify that DataObject has changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	// but dataObject node list is the same
    	assertEquals(treeSizeBeforeAdd, treeSizeAfterAdd);
    	
    	// get sequence object that will be removed
    	SDOSetting aSetting7 = (SDOSetting)aSequence.getSettings().get(7);
    	SDOSetting aSetting8 = (SDOSetting)aSequence.getSettings().get(8);
    	SDOSetting aSetting9 = (SDOSetting)aSequence.getSettings().get(9);
    	// remove the first occurrence of the duplicated items in the sequence 
    	int aSequenceSizeBeforeRemove = aSequence.size();
    	existingList.remove(2);
    	
    	// check size
    	assertEquals(aSequenceSizeBeforeRemove - 1, aSequence.size());
    	
    	// verify that the correct index in the sequence was removed based on the listWrapper index
    	assertEquals(aSetting8, aSequence.getSettings().get(7)); // 6 shifted into 5's place
    	assertEquals(aSetting9, aSequence.getSettings().get(8)); // 6 shifted into 5's place
    	assertFalse(aSetting7 == aSequence.getSettings().get(7)); // 5 is gone
    	assertFalse(aSetting7 == aSequence.getSettings().get(8)); // 5 is gone
    	//assertFalse(aSetting7 == aSequence.getSettings().get(9)); // 5 is gone
    }
        */
    // TODO: Verify that existing duplicates will be removed before the new list is added - addAll same as set
    public void testRemoveLastOccurrenceComplexManyByListWrapperAddAllPropertyOnExistingList() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, "/", 5);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(root).size();
    	assertEquals(10, treeSizeBeforeAdd);
    	assertEquals(5, aSequence.size());

    	ListWrapper existingList = (ListWrapper)root.get("item");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(3, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size();
    	// object to add
    	DataObject item3 = (DataObject)root.get("item[3]");
    	assertNotNull(item3);
    	
    	Property itemProperty = root.getInstanceProperty("item");
    	
    	// add list of items to existing list
    	List<DataObject> aListToAdd = new ArrayList<DataObject>();
    	aListToAdd.add(item3);
    	aListToAdd.add(item3);
    	aListToAdd.add(item3);
    	// add to the end of the list
    	existingList.addAll(aListToAdd);

    	// verify that the list has increased on the do
    	int listSizeAfter = ((ListWrapper)root.get("item")).size();

    	
    	// TODO: We should not be removing the existing item
    	int newItems = aListToAdd.size() - 3;
    	//assertEquals(listSizeBefore + aListToAdd.size(), listSizeAfter);
    	assertEquals(listSizeBefore + newItems, listSizeAfter);

    	// verify that the sequence size has increased by 1
    	assertEquals(indexToPlaceAtEnd + newItems, aSequence.size());
    	
    	// get back new Setting value
    	SDODataObject item2Value = (SDODataObject)aSequence.getValue(indexToPlaceAtEnd + newItems - 1);
    	assertNotNull(item2Value);
    	assertEquals(item2Value, aListToAdd.get(0));
    	// the following is no longer valid after we refactored addAll(do list) to not add duplicates
/*    	assertNotNull(aSequence.getProperty(indexToPlaceAtEnd));
    	// check increased size of sequence
    	assertEquals(indexToPlaceAtEnd + newItems, aSequence.size());
    	
    	// verify that DataObject has changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(root).size();
    	assertEquals(treeSizeBeforeAdd + newItems, treeSizeAfterAdd);
    	
    	// get sequence object that will be removed
    	SDOSetting aSetting4 = (SDOSetting)aSequence.getSettings().get(4);
    	SDOSetting aSetting5 = (SDOSetting)aSequence.getSettings().get(5);
    	SDOSetting aSetting6 = (SDOSetting)aSequence.getSettings().get(6);
    	// remove the first occurrence of the duplicated items in the sequence 
    	int aSequenceSizeBeforeRemove = aSequence.size();
    	existingList.remove(existingList.size() - 1);
    	
    	// check size
    	assertEquals(aSequenceSizeBeforeRemove -1, aSequence.size());
    	
    	// verify that the correct index in the sequence was removed based on the listWrapper index
    	assertEquals(aSetting4, aSequence.getSettings().get(4)); 
    	assertEquals(aSetting5, aSequence.getSettings().get(5)); 
    	assertFalse(aSetting6 == aSequence.getSettings().get(5)); 
*/    }
/*
    // Purpose is to verify that a remove(item) for duplicates removes the first occurrence of that item.
    public void testRemove1stOccurrenceSimpleManyByListWrapperAddAllPropertyOnExistingList() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	ListWrapper existingList = (ListWrapper)po.get("comment");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(PO_COMMENT_LIST_SIZE, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size();
    	Property commentProperty = po.getInstanceProperty("comment");
    	
    	// add list of items to existing list
    	List<String> aListToAdd = new ArrayList<String>();
    	aListToAdd.add(PO_COMMENT_VALUE1);
    	aListToAdd.add(PO_COMMENT_VALUE1);
    	aListToAdd.add(PO_COMMENT_VALUE1);
    	// add to the end of the list
    	existingList.addAll(aListToAdd);

    	// verify that the list has increased on the do
    	int listSizeAfter = ((ListWrapper)po.get("comment")).size();
    	
    	// TODO: We should not be removing the existing item?
    	int newItems = aListToAdd.size();
    	//assertEquals(listSizeBefore + aListToAdd.size(), listSizeAfter);
    	assertEquals(listSizeBefore + newItems, listSizeAfter);

    	// verify that the sequence size has increased
    	assertEquals(indexToPlaceAtEnd + newItems, aSequence.size());
    	
    	// get back new Setting value
    	String comment2Value = (String)aSequence.getValue(indexToPlaceAtEnd + newItems - 1);
    	assertNotNull(comment2Value);
    	assertEquals(comment2Value, aListToAdd.get(0));
    	assertNotNull(aSequence.getProperty(indexToPlaceAtEnd - 1));
    	// check increased size of sequence
    	assertEquals(indexToPlaceAtEnd + newItems, aSequence.size());
    	
    	// verify that DataObject has changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	// but dataObject node list is the same
    	assertEquals(treeSizeBeforeAdd, treeSizeAfterAdd);
    	
    	// get sequence object that will be removed
    	SDOSetting aSetting7 = (SDOSetting)aSequence.getSettings().get(7);
    	SDOSetting aSetting8 = (SDOSetting)aSequence.getSettings().get(8);
    	SDOSetting aSetting9 = (SDOSetting)aSequence.getSettings().get(9);
    	// remove the first occurrence of the duplicated items in the sequence 
    	int aSequenceSizeBeforeRemove = aSequence.size();
    	existingList.remove("comment 1");
    	
    	// check size
    	assertEquals(aSequenceSizeBeforeRemove - 1, aSequence.size());
    	
    	// verify that the correct index in the sequence was removed based on the listWrapper index
    	assertEquals(aSetting8, aSequence.getSettings().get(7)); // 6 shifted into 5's place
    	assertEquals(aSetting9, aSequence.getSettings().get(8)); // 6 shifted into 5's place
    	assertFalse(aSetting7 == aSequence.getSettings().get(7)); // 5 is gone
    	assertFalse(aSetting7 == aSequence.getSettings().get(8)); // 5 is gone
    	//assertFalse(aSetting7 == aSequence.getSettings().get(9)); // 5 is gone
    }
    */
    public void testAddAllComplexManyByListWrapperAddAllPropertyOnExistingList() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, "/", 5);
    	//DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(root).size();
    	assertEquals(10, treeSizeBeforeAdd);
    	assertEquals(5, aSequence.size());

    	ListWrapper existingList = (ListWrapper)root.get("item");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(3, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size();
    	// object to add
    	DataObject item3 = (DataObject)root.get("item[1]");
    	assertNotNull(item3);
    	
    	Property itemProperty = root.getInstanceProperty("item");
    	
    	// add list of items to existing list
    	List<DataObject> aListToAdd = new ArrayList<DataObject>();
    	
		DataObject item4 = dataFactory.create(typeHelper.getType(URINAME, ITEM_TYPENAME));
		item4.set("itemID", 4);
		item4.set("name", "item4-DF");    		
		DataObject item5 = dataFactory.create(typeHelper.getType(URINAME, ITEM_TYPENAME));
		item5.set("itemID", 5);
		item5.set("name", "item5-DF");    		
    	aListToAdd.add(item4);
    	aListToAdd.add(item5);
    	// add to the end of the list
    	existingList.addAll(aListToAdd);

    	// verify that the list has increased on the do
    	int listSizeAfter = ((ListWrapper)root.get("item")).size();
    	assertEquals(listSizeBefore + aListToAdd.size(), listSizeAfter);

    	// verify that the sequence size has increased
    	assertEquals(indexToPlaceAtEnd + aListToAdd.size(), aSequence.size());
    	
    	// get back new Setting value
    	for(int i=0; i< aListToAdd.size(); i++) {
    		SDODataObject itemValue = (SDODataObject)aSequence.getValue(indexToPlaceAtEnd + i);    		
    		assertNotNull(itemValue);
    		assertEquals(itemValue, aListToAdd.get(i));
    		assertNotNull(aSequence.getProperty(indexToPlaceAtEnd + i));
    	}
    	// check increased size of sequence
    	assertEquals(5 + aListToAdd.size(), aSequence.size());
    	
    	// verify that DataObject has changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(root).size();
    	assertEquals(treeSizeBeforeAdd + aListToAdd.size(), treeSizeAfterAdd);
    }

    public void testAddAllComplexManyByListWrapperAddPropertyOnExistingList() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, "/", 5);
    	//DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(root).size();
    	assertEquals(10, treeSizeBeforeAdd);
    	assertEquals(5, aSequence.size());

    	ListWrapper existingList = (ListWrapper)root.get("item");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(3, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size();
    	// object to add
    	DataObject item3 = (DataObject)root.get("item[1]");
    	assertNotNull(item3);
    	
    	Property itemProperty = root.getInstanceProperty("item");
    	
    	// add list of items to existing list
    	List<DataObject> aListToAdd = new ArrayList<DataObject>();
    	
		DataObject item4 = dataFactory.create(typeHelper.getType(URINAME, ITEM_TYPENAME));
		item4.set("itemID", 4);
		item4.set("name", "item4-DF");    		
		DataObject item5 = dataFactory.create(typeHelper.getType(URINAME, ITEM_TYPENAME));
		item5.set("itemID", 5);
		item5.set("name", "item5-DF");    		
    	aListToAdd.add(item4);
    	aListToAdd.add(item5);
    	// add each to the end of the list
    	for(Iterator i = aListToAdd.iterator(); i.hasNext();) {    	
    		existingList.add(i.next());
    	}

    	// verify that the list has increased on the do
    	int listSizeAfter = ((ListWrapper)root.get("item")).size();
    	assertEquals(listSizeBefore + aListToAdd.size(), listSizeAfter);

    	// verify that the sequence size has increased
    	assertEquals(indexToPlaceAtEnd + aListToAdd.size(), aSequence.size());
    	
    	// get back new Setting value
    	for(int i=0; i< aListToAdd.size(); i++) {
    		SDODataObject itemValue = (SDODataObject)aSequence.getValue(indexToPlaceAtEnd + i);    		
    		assertNotNull(itemValue);
    		assertEquals(itemValue, aListToAdd.get(i));
    		assertNotNull(aSequence.getProperty(indexToPlaceAtEnd + i));
    	}
    	// check increased size of sequence
    	assertEquals(5 + aListToAdd.size(), aSequence.size());
    	
    	// verify that DataObject has changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(root).size();
    	assertEquals(treeSizeBeforeAdd + aListToAdd.size(), treeSizeAfterAdd);
    }
    
    /**
     * Adds a new entry with the specified property name and value
     * at the specified entry index.
     * @param index the index at which to add the entry.
     * @param propertyName the name of the entry's property.
     * @param value the value for the entry.
     * TODO: single and many versions required
     */
    public void test_voidReturnFrom_add_int_String_Object() {
    	// int index, String propertyName, Object value) {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	int listSizeBefore = ((ListWrapper)po.get("item")).size();
    	assertEquals(2, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size();
    	// object to add
    	DataObject item2 = (DataObject)root.get("item[2]");
    	assertNotNull(item2);
    	// add to sequence
    	aSequence.add(indexToPlaceAtEnd, "item", item2);
    	// get back new Setting value
    	//ListWrapper item2Value = (ListWrapper)aSequence.getValue(indexToPlaceAtEnd);
    	SDODataObject item2Value = (SDODataObject)aSequence.getValue(indexToPlaceAtEnd);    	
    	assertNotNull(item2Value);
    	assertNotNull(aSequence.getProperty(indexToPlaceAtEnd));
    	// check increased size of sequence
    	assertEquals(PO_SEQUENCE_TREE_SIZE + 1, aSequence.size());
    	
    	// verify that the list has increased
    	int listSizeAfter = ((ListWrapper)po.get("item")).size();
    	assertEquals(listSizeBefore + 1, listSizeAfter);
    	
    	// verify that DataObject has changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(treeSizeBeforeAdd + 1, treeSizeAfterAdd);
    }

    /**
     * Adds a new entry with the specified property index and value
     * at the specified entry index.
     * @param index the index at which to add the entry.
     * @param propertyIndex the index of the entry's property.
     * @param value the value for the entry.
     * TODO: single and many versions required
     */
    public void test_voidReturnFrom_add_int_int_Object() {
    	// int index, int propertyIndex, Object value) {
    	// int index, String propertyName, Object value) {
    	defineAndLoadRoot(false, false);
    	// detach shipTo
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	int listSizeBefore = ((ListWrapper)po.get("item")).size();
    	assertEquals(2, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size();
    	// object to add
    	DataObject item2 = (DataObject)root.get("item[2]");
    	assertNotNull(item2);
    	
    	// get "item" property index
    	int propIndex = 0;
    	for(Iterator i=po.getInstanceProperties().iterator(); i.hasNext();) {
    		Property aProperty = (Property)i.next();
    		if(aProperty != null && aProperty.getName().equals("item")) {
    			break;
    		} else {
    			propIndex++;
    		}
    	}
    	// add to sequence
    	aSequence.add(indexToPlaceAtEnd, propIndex, item2);
    	// get back new Setting value
    	//ListWrapper item2Value = (ListWrapper)aSequence.getValue(indexToPlaceAtEnd);
    	SDODataObject item2Value = (SDODataObject)aSequence.getValue(indexToPlaceAtEnd);
    	assertNotNull(item2Value);
    	assertNotNull(aSequence.getProperty(indexToPlaceAtEnd));
    	// check increased size of sequence
    	assertEquals(PO_SEQUENCE_TREE_SIZE + 1, aSequence.size());
    	
    	// verify that the list has increased
    	int listSizeAfter = ((ListWrapper)po.get("item")).size();
    	assertEquals(listSizeBefore + 1, listSizeAfter);
    	
    	// verify that DataObject has changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(treeSizeBeforeAdd + 1, treeSizeAfterAdd);
    }

    /**
     * Adds a new entry with the specified property and value
     * at the specified entry index.
     * @param index the index at which to add the entry.
     * @param property the property of the entry.
     * @param value the value for the entry.
     */
    public void test_voidReturnFrom_add_int_Property_Object() {
    	// int index, Property property, Object value) {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	int listSizeBefore = ((ListWrapper)po.get("item")).size();
    	assertEquals(2, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size();
    	// object to add
    	DataObject item2 = (DataObject)root.get("item[2]");
    	assertNotNull(item2);
    	
    	Property itemProperty = po.getInstanceProperty("item");
    	
    	// add to sequence
    	aSequence.add(indexToPlaceAtEnd, itemProperty, item2);
    	// get back new Setting value
    	//ListWrapper item2Value = (ListWrapper)aSequence.getValue(indexToPlaceAtEnd);
    	SDODataObject item2Value = (SDODataObject)aSequence.getValue(indexToPlaceAtEnd);
    	assertNotNull(item2Value);
    	assertNotNull(aSequence.getProperty(indexToPlaceAtEnd));
    	// check increased size of sequence
    	assertEquals(PO_SEQUENCE_TREE_SIZE + 1, aSequence.size());
    	
    	// verify that the list has increased
    	int listSizeAfter = ((ListWrapper)po.get("item")).size();
    	assertEquals(listSizeBefore + 1, listSizeAfter);
    	
    	// verify that DataObject has changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(treeSizeBeforeAdd + 1, treeSizeAfterAdd);
    }

    public void test_voidReturnFrom_add_int_Property_Object_outofbounds() {
    	// int index, Property property, Object value) {
    	defineAndLoadRoot(false, false);
    	// detach shipTo
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	int listSizeBefore = ((ListWrapper)po.get("item")).size();
    	assertEquals(2, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size();
    	// object to add
    	DataObject item2 = (DataObject)root.get("item[2]");
    	assertNotNull(item2);
    	
    	Property itemProperty = po.getInstanceProperty("item");
    	
    	// add to sequence
    	try {
    		aSequence.add(-1, itemProperty, item2);
    	} catch (Exception e) {
    		
    	}
    	
    	// get back new Setting value
    	ListWrapper item2Value = null;
    	boolean exceptionThrown = false;
    	try {
    		item2Value = (ListWrapper)aSequence.getValue(indexToPlaceAtEnd);
    	} catch (SDOException e) {
        assertEquals(SDOException.INVALID_INDEX ,e.getErrorCode());
    		exceptionThrown = true;
        
    	}
    	assertNull(item2Value);
    	assertTrue(exceptionThrown);
    	
/*    	assertNotNull(aSequence.getProperty(indexToPlaceAtEnd));
    	// check increased size of sequence didnt happen
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());
    	
    	// verify that the list has not increased
    	int listSizeAfter = ((ListWrapper)po.get("item")).size();
    	assertEquals(listSizeBefore, listSizeAfter);
    	
    	// verify that DataObject has not changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(treeSizeBeforeAdd, treeSizeAfterAdd);
*/    }

    public void test_voidReturnFrom_add_int_Property_Object_propertyIsAttribute() {
    	// int index, Property property, Object value) {
    	defineAndLoadRoot(false, false);
    	// detach shipTo
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	int listSizeBefore = ((ListWrapper)po.get("item")).size();
    	assertEquals(2, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size();
    	// object to add
    	DataObject item2 = (DataObject)root.get("item[2]");
    	assertNotNull(item2);
    	
    	Property orderDateProperty = po.getInstanceProperty("orderDate");
    	
    	// add to sequence
    	try {
    		aSequence.add(indexToPlaceAtEnd, orderDateProperty, "1999-10-19");
    	} catch (Exception e) {
    		
    	}
    	
    	// get back new Setting value
    	ListWrapper item2Value = null;
    	boolean exceptionThrown = false;
    	try {
    		item2Value = (ListWrapper)aSequence.getValue(indexToPlaceAtEnd);
    	} catch (SDOException e) {      
        assertEquals(SDOException.INVALID_INDEX, e.getErrorCode());
    		exceptionThrown = true;
    	}
    	assertNull(item2Value);
    	assertTrue(exceptionThrown);
   }
    
    
    /**
     * Removes the entry at the given entry index.
     * @param index the index of the entry.
     */
    public void test_voidReturnFrom_remove_int() {
    	// int index) {
    }

    /**
     * Moves the entry at <code>fromIndex</code> to <code>toIndex</code>.
     * @param toIndex the index of the entry destination.
     * @param fromIndex the index of the entry to move.
     */
    public void test_voidReturnFrom_move_int_int() {
    	// int toIndex, int fromIndex) {
    }

    /**
    * @deprecated replaced by {@link #addText(String)} in 2.1.0
    */
    public void test_voidReturnFrom_add_String() {
    	// String text) {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	int textIndexToPlaceAtEnd = aSequence.size();

    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	
    	aSequence.add("random text");
    	Object addText = aSequence.getValue(textIndexToPlaceAtEnd);
    	assertNotNull(addText);
    	assertNull(aSequence.getProperty(textIndexToPlaceAtEnd));
    	assertEquals(PO_SEQUENCE_TREE_SIZE + 1, aSequence.size());

    	// verify that DataObject has not changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(treeSizeBeforeAdd, treeSizeAfterAdd);
    }

    /**
     * @deprecated replaced by {@link #addText(int, String)} in 2.1.0
     */
    public void test_voidReturnFrom_add_int_String() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	int textIndexToPlaceAtEnd = aSequence.size();
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	
    	aSequence.add(textIndexToPlaceAtEnd, "random text");
    	Object addText = aSequence.getValue(textIndexToPlaceAtEnd);
    	assertNotNull(addText);
    	assertNull(aSequence.getProperty(textIndexToPlaceAtEnd));
    	assertEquals(PO_SEQUENCE_TREE_SIZE + 1, aSequence.size());
    	
    	// verify that DataObject has not changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(treeSizeBeforeAdd, treeSizeAfterAdd);
    }

    /**
     * Adds a new text entry to the end of the Sequence.
     * @param text value of the entry.
     */
    public void test_voidReturnFrom_addText_String() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	int textIndexToPlaceAtEnd = aSequence.size();
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	
    	aSequence.addText("random text");
    	Object addText = aSequence.getValue(textIndexToPlaceAtEnd);
    	assertNotNull(addText);
    	assertNull(aSequence.getProperty(textIndexToPlaceAtEnd));
    	assertEquals(PO_SEQUENCE_TREE_SIZE + 1, aSequence.size());

    	// verify that DataObject has not changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(treeSizeBeforeAdd, treeSizeAfterAdd);
    }

    /**
     * Adds a new text entry at the given index.
     * @param index the index at which to add the entry.
     * @param text value of the entry.
     */
    public void test_voidReturnFromAddText_intEnd_String() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	int textIndexToPlaceAtEnd = aSequence.size();
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	
    	aSequence.addText(textIndexToPlaceAtEnd, "random text");
    	Object addText = aSequence.getValue(textIndexToPlaceAtEnd);
    	assertNotNull(addText);
    	assertNull(aSequence.getProperty(textIndexToPlaceAtEnd));
    	assertEquals(PO_SEQUENCE_TREE_SIZE + 1, aSequence.size());
    	
    	// verify that DataObject has not changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(treeSizeBeforeAdd, treeSizeAfterAdd);
    }

    public void test_voidReturnFromAddText_int0_String_() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	int textIndexToPlaceAtEnd = 0;//aSequence.size();
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	
    	aSequence.addText(textIndexToPlaceAtEnd, "random text");
    	Object addText = aSequence.getValue(textIndexToPlaceAtEnd);
    	assertNotNull(addText);
    	assertNull(aSequence.getProperty(textIndexToPlaceAtEnd));
    	assertEquals(PO_SEQUENCE_TREE_SIZE + 1, aSequence.size());
    	
    	// verify that DataObject has not changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(treeSizeBeforeAdd, treeSizeAfterAdd);
    }
    
    /**
     * Sets the entry at a specified index to the new value.
     * @param index the index of the entry.
     * @param value the new value for the entry.
     */
    public void test_addAll_complexMany() {
    	// int index, Object value) {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	ListWrapper existingList = (ListWrapper)po.get("item");    	
    	int listSizeBefore = existingList.size();
    	assertEquals(2, listSizeBefore);
    	
    	int sequenceSizeBefore = aSequence.size();
    	int indexToPlaceAtEnd = aSequence.size() - 1;
    	// object to replace
    	DataObject item2 = (DataObject)root.get("item[2]");
    	assertNotNull(item2);
    	
    	// object to add
    	DataObject item3 = (DataObject)root.get("item[1]");
    	assertNotNull(item3);
   	
    	// add to sequence
    	
    	// TODO: TEST setting a many property using an item instead of a List of items
    	    	
    	// add the item to the current list
    	// add list of items to existing list
    	List<DataObject> aListToAdd = new ArrayList<DataObject>();
    	aListToAdd.add(item3);
    	// add to the end of the list
    	existingList.addAll(aListToAdd);

    	// verify that the list has increased on the do
    	int listSizeAfter = ((ListWrapper)po.get("item")).size();
    	assertEquals(listSizeBefore + aListToAdd.size(), listSizeAfter);

    	// verify that the sequence size has increased
    	assertEquals(sequenceSizeBefore + aListToAdd.size(), aSequence.size());
    	
    	// get back new Setting value
    	SDODataObject item2Value = (SDODataObject)aSequence.getValue(aSequence.size() -1);
    	assertNotNull(item2Value);
    	assertEquals(item2Value, aListToAdd.get(0));
    	assertNotNull(aSequence.getProperty(indexToPlaceAtEnd));
    	// check increased size of sequence
    	assertEquals(PO_SEQUENCE_TREE_SIZE + 1, aSequence.size());
    	
    	// verify that DataObject has changed
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(treeSizeBeforeAdd + 1, treeSizeAfterAdd);
    }

    public void test_setValue_singleComplex() {
    	
    }
    public void test_setValue_manySimple() {
    	
    }
    public void test_setValue_manyComplex() {
    	
    }

    public void test_setValue_manyComplex_toNull_ThrowsUnsupportedOperationException() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	int indexToPlaceAtEnd = aSequence.size() - 1;
    	boolean exceptionOccurred = false;

    	// null value on non-nillable many property will throw exception
    	try {
    	    aSequence.setValue(indexToPlaceAtEnd, null);
    	} catch (UnsupportedOperationException iex) {
            exceptionOccurred = true;
    	}
    	assertTrue("An UnsupportedOperationException did not occur as expected", exceptionOccurred);
    }

    public void test_setValue_manyComplex_invalidIndexThrowsException() {
    	// int index, String propertyName, Object value) {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	int listSizeBefore = ((ListWrapper)po.get("item")).size();
    	assertEquals(2, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size() - 1;
    	// object to replace
    	DataObject item2 = (DataObject)root.get("item[2]");
    	
    	assertNotNull(item2);
/*   	
    	// add the item to the current list
    	ListWrapper existingList = (ListWrapper)aSequence.getValue(indexToPlaceAtEnd);
    	assertNotNull(existingList);
    	existingList.add(item2);

    	boolean exceptionThrown = false;
    	try {
    		aSequence.setValue(indexToPlaceAtEnd + 1, existingList);
    	} catch (IndexOutOfBoundsException e) {
    		exceptionThrown = true;
    	} finally {
        	assertTrue(exceptionThrown);
    	}
    	
    	// get back modified Setting value
    	ListWrapper item2Value = (ListWrapper)aSequence.getValue(indexToPlaceAtEnd);
    	assertNotNull(item2Value);
    	assertNotNull(aSequence.getProperty(indexToPlaceAtEnd));
    	// check no increase in size of sequence
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());
    	
    	// verify that the list has increased
    	int listSizeAfter = ((ListWrapper)po.get("item")).size();
    	assertEquals(listSizeBefore + 1, listSizeAfter);
    	
    	// verify that DataObject has changed in size
    	int treeSizeAfterAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(treeSizeBeforeAdd + 1, treeSizeAfterAdd);
*/    }
    
    public void test_getValue_whereSettingIsNull() {
    	registerCustomerType(true, null);
		DataObject cust1 = (DataObject)dataFactory.create(typeHelper.getType(URINAME, CUSTOMER_TYPENAME));
		cust1.set("custID", 5);
		List emails = new ArrayList();
		emails.add("email1-DF@myCompany.com");
		emails.add("email2-DF@myCompany.com");
		cust1.set("email", emails);
		List phones = new ArrayList();
		phones.add("6135550001");
		cust1.set("phone", phones);

		// create a non-standard sequence
    	SDOSequence aSequence = new SDOSequence((SDODataObject)cust1);
    	// TODO: there is no way to get a null setting from a sequence
    }

    public void test_getValue_whereIndexIsOutOfBounds() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	int listSizeBefore = ((ListWrapper)po.get("item")).size();
    	assertEquals(2, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size() - 1;
    	// object to replace
    	DataObject item2 = (DataObject)root.get("item[2]");
    	
    	assertNotNull(item2);
/*   	
    	// add the item to the current list
    	ListWrapper existingList = (ListWrapper)aSequence.getValue(indexToPlaceAtEnd);
    	assertNotNull(existingList);
    	existingList.add(item2);

    	boolean exceptionThrown = false;
    	try {
    		aSequence.getValue(-1);
    	} catch (IndexOutOfBoundsException e) {
    		exceptionThrown = true;
    	} finally {
        	assertTrue(exceptionThrown);
    	}
*/    }

    /**
     * Returns the property for the given entry index.
     * Returns <code>null</code> for mixed text entries.
     * @param index the index of the entry.
     * @return the property or <code>null</code> for the given entry index.
     */
    public void test_PropertyReturnFrom_getProperty() {
    	// int index) {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	int listSizeBefore = ((ListWrapper)po.get("item")).size();
    	assertEquals(2, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size() - 1;
    	// object to replace
    	DataObject item2 = (DataObject)root.get("item[2]");
    	
    	assertNotNull(item2);
   	
/*    	// add the item to the current list
    	ListWrapper existingList = (ListWrapper)aSequence.getValue(indexToPlaceAtEnd);
    	assertNotNull(existingList);
    	existingList.add(item2);

    	boolean exceptionThrown = false;
    	Property property1 = po.getInstanceProperty("item"); 
    	Object property1Return = null;
    	try {
    		property1Return = aSequence.getProperty(indexToPlaceAtEnd);
    	} catch (IndexOutOfBoundsException e) {
    		exceptionThrown = true;
    	} finally {
        	assertFalse(exceptionThrown);
    	}
    	
    	// verify property
    	assertNotNull(property1Return);
    	assertEquals(property1, property1Return);
*/    }

    /**
     * Returns the property value for the given entry index.
     * @param index the index of the entry.
     * @return the value for the given entry index.
     */
    public void test_ObjectReturnFrom_getValue() {
    	// int index) {
    }
    
    public void test_getProperty_whereIndexIsOutOfBounds() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	
    	int treeSizeBeforeAdd = preOrderTraversalDataObjectList(po).size();
    	assertEquals(PO_TREE_SIZE, treeSizeBeforeAdd);
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());

    	int listSizeBefore = ((ListWrapper)po.get("item")).size();
    	assertEquals(2, listSizeBefore);
    	
    	int indexToPlaceAtEnd = aSequence.size() - 1;
    	// object to replace
    	DataObject item2 = (DataObject)root.get("item[2]");
    	
    	assertNotNull(item2);
   	
/*    	// add the item to the current list
    	ListWrapper existingList = (ListWrapper)aSequence.getValue(indexToPlaceAtEnd);
    	assertNotNull(existingList);
    	existingList.add(item2);

    	boolean exceptionThrown = false;
    	try {
    		aSequence.getProperty(-1);
    	} catch (IndexOutOfBoundsException e) {
    		exceptionThrown = true;
    	} finally {
        	assertTrue(exceptionThrown);
    	}
*/    }
    
    // get id
    public void test_getSequenceViaPropertyIndexFromSingleSimpleType() {
    	
    }

    // get comment[1]
    public void test_getSequenceViaPropertyIndexFromManySimpleType() {
    	
    }

    // get 
    public void test_getSequenceViaPropertyIndexFromSingleComplexType() {
    	
    }

    // get porder[1]
    public void test_getSequenceViaPropertyIndexFromManyComplexType() {
    	
    }

    public void testMoveSameIndex() {
    	defineAndLoadRoot(false, false);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
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

  
    public void testMoveSameIndexOutOfBoundsHigh() {
    	defineAndLoadRoot(false, false);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	assertNotNull(po);
        assertTrue(po.getType().isSequenced());
        SDOSequence aSequence = (SDOSequence)po.getSequence();

        // move 2 sequenced objects
        Property beforeMoveProp1 = aSequence.getProperty(1);
        assertNotNull(beforeMoveProp1);
    	
        	aSequence.move(99, 99);
        
        Property afterMoveProp1 = aSequence.getProperty(1);
        // verify the move did not take place
        assertTrue(beforeMoveProp1 == afterMoveProp1);
    	
    }

    public void testMoveIndexFromOutOfBoundsHigh() {
    	defineAndLoadRoot(false, false);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	assertNotNull(po);
        assertTrue(po.getType().isSequenced());
        SDOSequence aSequence = (SDOSequence)po.getSequence();

        // move 2 sequenced objects
        Property beforeMoveProp1 = aSequence.getProperty(1);
        assertNotNull(beforeMoveProp1);
    	boolean exceptionThrown = false;
        try {
        	aSequence.move(99, 1);
        } catch (SDOException e) {
        	// catch error
          assertEquals(SDOException.INVALID_INDEX ,e.getErrorCode());
    		exceptionThrown = true;
        }
        
        Property afterMoveProp1 = aSequence.getProperty(1);
        // verify the move did not take place
        assertTrue(beforeMoveProp1 == afterMoveProp1);
    	assertTrue(exceptionThrown);
    }
    
    public void testMoveIndexToOutOfBoundsHigh() {
    	defineAndLoadRoot(false, false);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	assertNotNull(po);
        assertTrue(po.getType().isSequenced());
        SDOSequence aSequence = (SDOSequence)po.getSequence();

        // move 2 sequenced objects
        Property beforeMoveProp1 = aSequence.getProperty(1);
        assertNotNull(beforeMoveProp1);
    	boolean exceptionThrown = false;
        try {
        	aSequence.move(1, 99);
        } catch (SDOException e) {
        assertEquals(SDOException.INVALID_INDEX ,e.getErrorCode());
        	// catch error
    		exceptionThrown = true;
        }
        
        Property afterMoveProp1 = aSequence.getProperty(1);
        // verify the move did not take place
        assertTrue(beforeMoveProp1 == afterMoveProp1);
    	assertTrue(exceptionThrown);
    }

    public void testMoveIndexBothOutOfBoundsHigh() {
    	defineAndLoadRoot(false, false);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	assertNotNull(po);
        assertTrue(po.getType().isSequenced());
        SDOSequence aSequence = (SDOSequence)po.getSequence();

        // move 2 sequenced objects
        Property beforeMoveProp1 = aSequence.getProperty(1);
        assertNotNull(beforeMoveProp1);
    	boolean exceptionThrown = false;
        try {
        	aSequence.move(101, 99);
        } catch (SDOException e) {
        	// catch error
          assertEquals(SDOException.INVALID_INDEX ,e.getErrorCode());
    		exceptionThrown = true;
        }
        
        Property afterMoveProp1 = aSequence.getProperty(1);
        // verify the move did not take place
        assertTrue(beforeMoveProp1 == afterMoveProp1);
    	assertTrue(exceptionThrown);
    }

    public void testMoveSameIndexOutOfBoundsLow() {
    	defineAndLoadRoot(false, false);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	assertNotNull(po);
        assertTrue(po.getType().isSequenced());
        SDOSequence aSequence = (SDOSequence)po.getSequence();

        // move 2 sequenced objects
        Property beforeMoveProp1 = aSequence.getProperty(1);
        assertNotNull(beforeMoveProp1);
    	
      	aSequence.move(-1, -1);
      
        Property afterMoveProp1 = aSequence.getProperty(1);
        // verify the move did not take place
        assertTrue(beforeMoveProp1 == afterMoveProp1);
    	
    }

    public void testMoveIndexFromOutOfBoundsLow() {
    	defineAndLoadRoot(false, false);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	assertNotNull(po);
        assertTrue(po.getType().isSequenced());
        SDOSequence aSequence = (SDOSequence)po.getSequence();

        // move 2 sequenced objects
        Property beforeMoveProp1 = aSequence.getProperty(1);
        assertNotNull(beforeMoveProp1);
    	boolean exceptionThrown = false;
        try {
        	aSequence.move(-1, 1);
        } catch (SDOException e) {
        assertEquals(SDOException.INVALID_INDEX ,e.getErrorCode());
        	// catch error
    		exceptionThrown = true;
        }
        
        Property afterMoveProp1 = aSequence.getProperty(1);
        // verify the move did not take place
        assertTrue(beforeMoveProp1 == afterMoveProp1);
    	assertTrue(exceptionThrown);
    }
    
    public void testMoveIndexToOutOfBoundsLow() {
    	defineAndLoadRoot(false, false);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	assertNotNull(po);
        assertTrue(po.getType().isSequenced());
        SDOSequence aSequence = (SDOSequence)po.getSequence();

        // move 2 sequenced objects
        Property beforeMoveProp1 = aSequence.getProperty(1);
        assertNotNull(beforeMoveProp1);
    	boolean exceptionThrown = false;
        try {
        	aSequence.move(1, -1);
        } catch (SDOException e) {
        	// catch error
          assertEquals(SDOException.INVALID_INDEX ,e.getErrorCode());
    		exceptionThrown = true;
        }
        
        Property afterMoveProp1 = aSequence.getProperty(1);
        // verify the move did not take place
        assertTrue(beforeMoveProp1 == afterMoveProp1);
    	assertTrue(exceptionThrown);
    }

    public void testMoveIndexBothOutOfBoundsLow() {
    	defineAndLoadRoot(false, false);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	assertNotNull(po);
        assertTrue(po.getType().isSequenced());
        SDOSequence aSequence = (SDOSequence)po.getSequence();

        // move 2 sequenced objects
        Property beforeMoveProp1 = aSequence.getProperty(1);
        assertNotNull(beforeMoveProp1);
    	boolean exceptionThrown = false;
        try {
        	aSequence.move(-1, -2);
        } catch (SDOException e) {
        	// catch error
    		  assertEquals(SDOException.INVALID_INDEX ,e.getErrorCode());
          exceptionThrown = true;
        }
        
        Property afterMoveProp1 = aSequence.getProperty(1);
        // verify the move did not take place
        assertTrue(beforeMoveProp1 == afterMoveProp1);
    	assertTrue(exceptionThrown);
    }
    
    // following 3 tests are 3 different ways to do a move via sequence
    public void testMoveIndex0toEnd() {
    	defineAndLoadRoot(false, false);
    	//SDOSequence aSequence = getSequence();
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
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

    // see JIRA 242 bug#6031657
    /*
    public void testDontMoveIndex0toEndViaAddThrowsException() {
    	defineAndLoadRoot(false, false);
    	//SDOSequence aSequence = getSequence();
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	assertNotNull(po);
        assertTrue(po.getType().isSequenced());
        SDOSequence aSequence = (SDOSequence)po.getSequence();

        // save sequence 0
        SDOSetting aSetting0 = (SDOSetting)aSequence.getSettings().get(0);
        assertNotNull(aSetting0);

        // move 2 sequenced objects
        Property beforeMoveProp0 = aSequence.getProperty(0);
        Property beforeMoveProp1 = aSequence.getProperty(1);
        Property beforeMoveProp3 = aSequence.getProperty(3);
        assertNotNull(beforeMoveProp0);
        assertNotNull(beforeMoveProp1);
        assertNotNull(beforeMoveProp3);
        
        boolean threwException = false;
        try {
            // 0,1,2,3 -> 1,2,3,0
           //aSequence.move(3, 0);
        	aSequence.add(aSetting0.getProperty(), aSetting0.getValue());
        
        	Property afterMoveProp0 = aSequence.getProperty(0);
        	//Property afterMoveProp0 = aSequence.getProperty(0);
        	Property afterMovePropEnd = aSequence.getProperty(aSequence.size() - 1);
        	assertFalse(beforeMoveProp1 == afterMoveProp0);
        	assertTrue(beforeMoveProp0 == afterMovePropEnd);
        } catch (Exception e) {
        	threwException = true;
        }
        assertTrue(threwException);
    }

    // see JIRA 242 bug#6031657
    public void testDontMoveIndex0toEndViaAddIndex() {
    	defineAndLoadRoot(false, false);
    	//SDOSequence aSequence = getSequence();
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	assertNotNull(po);
        assertTrue(po.getType().isSequenced());
        SDOSequence aSequence = (SDOSequence)po.getSequence();

        // save sequence 0
        SDOSetting aSetting0 = (SDOSetting)aSequence.getSettings().get(0);
        assertNotNull(aSetting0);

        // move 2 sequenced objects
        Property beforeMoveProp0 = aSequence.getProperty(0);
        Property beforeMoveProp1 = aSequence.getProperty(1);
        Property beforeMoveProp3 = aSequence.getProperty(3);
        assertNotNull(beforeMoveProp0);
        assertNotNull(beforeMoveProp1);
        assertNotNull(beforeMoveProp3);
        
        // 0,1,2,3 -> 1,2,3,0
        
        //aSequence.move(3, 0);
        aSequence.add(aSequence.size(), aSetting0.getProperty(), aSetting0.getValue());
        
        Property afterMoveProp0 = aSequence.getProperty(0);
        //Property afterMoveProp0 = aSequence.getProperty(0);
        Property afterMovePropEnd = aSequence.getProperty(aSequence.size() - 1);
        assertFalse(beforeMoveProp1 == afterMoveProp0);
        assertTrue(beforeMoveProp0 == afterMovePropEnd);
    }
    */
    public void testMoveIndexEndto0() {
    	
    }
    public void testMoveIndexAdjacent1to2() {
    	
    }
    public void testMoveIndexAdjacent2to1() {
    	
    }

    public void testToString() {
    	// exercise the toString() method for code coverage
    	defineAndLoadRoot(false, false);
    	// get sequence po
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	assertNotNull(po);
        assertTrue(po.getType().isSequenced());
        SDOSequence aSequence = (SDOSequence)po.getSequence();
    	assertNotNull(aSequence);
    	// 4 instead of 5 because the one attribute property cannot be sequenced
    	assertEquals(PO_SEQUENCE_SIZE, aSequence.size());
    	String aString = aSequence.toString();
    	assertNotNull(aString);
    	// we could do some minimal string comparison
    	
    }
    
    public void testEqualDeepSameSequence() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        //compareSequences(aSequence, aSequence, PO_SEQUENCE_SIZE, true);
    	// get sequence po
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	assertNotNull(po);
        assertTrue(po.getType().isSequenced());
        
        // test equalityHelper
        boolean bEqual = equalityHelper.equal(po, po);
        assertTrue(bEqual);
    }

    public void testEqualShallowSameSequence() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        //compareSequences(aSequence, aSequence, PO_SEQUENCE_SIZE, true);
    	// get sequence po
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	assertNotNull(po);
        assertTrue(po.getType().isSequenced());
        
        // test equalityHelper
        boolean bEqual = equalityHelper.equalShallow(po, po);
        assertTrue(bEqual);
    }

    // Via a non-interface method the user is able to set sequenced=true after type definition - not supported
    public void testEqualShallowSameSequenceFlagTrueButSequenceIsNull() {
    	defineAndLoadRoot(false, false);
        SDOSequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        //compareSequences(aSequence, aSequence, PO_SEQUENCE_SIZE, true);
    	// get sequence po
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	assertNotNull(po);
        assertTrue(po.getType().isSequenced());
        
        // test equalityHelper
        boolean bEqual = equalityHelper.equalShallow(po, po);
        assertTrue(bEqual);
        
/*        
    	defineAndLoadRoot(false, false);
        Sequence aSequence = getSequence(root, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
    	DataObject po = (DataObject)root.get(PO_SEQUENCE_PATH);
    	// copy po
    	DataObject rootCopy = (DataObject)copyHelper.copy(root);    	
    	// check sequence was copied
        Sequence aSequenceCopy = getSequence(rootCopy, PO_SEQUENCE_PATH, PO_SEQUENCE_SIZE);
        assertNotNull(aSequenceCopy);
        //compareSequences((SDOSequence)aSequence, (SDOSequence)aSequenceCopy, PO_SEQUENCE_SIZE, true);
        
        // test equalityHelper
    	DataObject poCopy = (DataObject)rootCopy.get(PO_SEQUENCE_PATH);
        boolean bEqual = equalityHelper.equal(po, poCopy);
        assertTrue(bEqual);

        // modify copy sequence state - non public method - both po and poCopy will have their state changed because they share an SDOType
        assertTrue(po.getType().isSequenced());
        assertTrue(poCopy.getType().isSequenced());
        // WARNING: Users should not be doing this - however it is supported
        ((SDOType)poCopy.getType()).setSequenced(false);
        assertFalse(po.getType().isSequenced());
        assertFalse(poCopy.getType().isSequenced());        
        bEqual = equalityHelper.equal(po, poCopy);
        // sequences will not be compared
        assertTrue(bEqual);
*/    }
    
    
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
