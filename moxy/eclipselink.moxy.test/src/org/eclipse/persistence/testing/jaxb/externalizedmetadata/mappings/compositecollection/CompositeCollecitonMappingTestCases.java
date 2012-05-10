/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - March 19/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.compositecollection;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;

/**
 * Tests XmlCompositeCollectionMappings via eclipselink-oxm.xml
 * 
 */
public class CompositeCollecitonMappingTestCases extends JAXBWithJSONTestCases {
	
	private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/compositecollection/employee.xml";
	private static final String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/compositecollection/write-employee.xml";
	private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/compositecollection/employee.json";
	private static final String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/compositecollection/write-employee.json";
    private static final int HOME_ID = 67;
    private static final String HOME_RO_STRING = "some garbage text";
    private static final String HOME_CITY = "Kanata";
    private static final String HOME_STREET = "66 Lakview Drive";
    private static final String HOME_PROVINCE = "ON";
    private static final String HOME_POSTAL = "K2M2K7";
    private static final int WORK_ID = 76;
    private static final String WORK_CITY = "Ottawa";
    private static final String WORK_STREET = "45 O'Connor St.";
    private static final String WORK_PROVINCE = "ON";
    private static final String WORK_POSTAL = "K1P1A4";
    private static final int RO_ID = 66;
    private static final String RO_CITY = "Woodlawn";
    private static final String RO_STREET = "465 Bayview Dr.";
    private static final String RO_PROVINCE = "ON";
    private static final String RO_POSTAL = "K0A3M0";
    private static final int WO_ID = 77;
    private static final String WO_CITY = "Woodlawn";
    private static final String WO_STREET = "463 Bayview Dr.";
    private static final String WO_PROVINCE = "ON";
    private static final String WO_POSTAL = "K0A3M0";
        
    private Employee writeCtrlObject;

    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public CompositeCollecitonMappingTestCases(String name) throws Exception{
        super(name);
        setClasses(new Class[]{Employee.class});
        setControlDocument(XML_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setWriteControlJSON(JSON_WRITE_RESOURCE);
    }
    
    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/compositecollection/employee-oxm.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.compositecollection", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
		        
        return properties;
	}
      
    /**
     * Create the control Employee object for reading.
     * 
     */
    public Object getControlObject() {
        // setup Addresses
        Address hAddress = new Address();
        hAddress.id = HOME_ID;
        hAddress.readOnlyString = HOME_RO_STRING;
        hAddress.city = HOME_CITY;
        hAddress.street = HOME_STREET;
        hAddress.province = HOME_PROVINCE;
        hAddress.postalCode = HOME_POSTAL;
        
        Address wAddress = new Address();
        wAddress.id = WORK_ID;
        wAddress.city = WORK_CITY;
        wAddress.street = WORK_STREET;
        wAddress.province = WORK_PROVINCE;
        wAddress.postalCode = WORK_POSTAL;

        List<Address> adds = new ArrayList<Address>();
        adds.add(hAddress);
        adds.add(wAddress);              
       
        // setup read-only Address list
        Address roAddress = new Address();
        roAddress.id = RO_ID;
        roAddress.city = RO_CITY;
        roAddress.street = RO_STREET;
        roAddress.province = RO_PROVINCE;
        roAddress.postalCode = RO_POSTAL;

        List<Address> roAdds = new ArrayList<Address>();
        roAdds.add(roAddress);
        
        Employee emp = new Employee();
        emp.id = 101;
        emp.addresses = adds;
        emp.readOnlyAddressList = roAdds;
        
        return emp;
    }
    
    /**
     * Create the control Employee object for writing.
     * 
     * For null policy test of ABSENT_NODE we will add some null addresses.
     * 
     */
    public Object getWriteControlObject() {
    	if(writeCtrlObject == null){
            Employee emp = (Employee)getReadControlObject();
            emp.addresses.add(null);
            emp.addresses.add(null);            
            
         // setup write-only Address list
            Address woAddress = new Address();
            woAddress.id = WO_ID;
            woAddress.city = WO_CITY;
            woAddress.street = WO_STREET;
            woAddress.province = WO_PROVINCE;
            woAddress.postalCode = WO_POSTAL;
            
            List<Address> woAdds = new ArrayList<Address>();
            woAdds.add(woAddress);
            emp.writeOnlyAddressList = woAdds;
            writeCtrlObject = emp;
    	}
        return writeCtrlObject;
    }
    
    public void xmlToObjectTest(Object testObject) throws Exception {
    	super.xmlToObjectTest(testObject);
   	    assertTrue("Accessor method was not called as expected", ((Employee)testObject).wasSetCalled);   	    	   
    }

    public void testRoundTrip() throws Exception{
    	//doesn't apply since read and write only mappings are present    	
    }
    
    public void objectToXMLDocumentTest(Document testDocument) throws Exception {
        super.objectToXMLDocumentTest(testDocument);
        assertTrue("Accessor method was not called as expected", writeCtrlObject.wasGetCalled);
    }

    public void testSchemaGen() throws Exception{
    	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/compositecollection/employee.xsd");
    	InputStream is2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/compositecollection/contacts.xsd");
    	controlSchemas.add(is);
    	controlSchemas.add(is2);
    	super.testSchemaGen(controlSchemas);
    }
    
    public void testInstanceDocValidation() throws Exception {
    	InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/compositecollection/employee.xsd");        
        StreamSource schemaSource = new StreamSource(schema); 
                
        MyMapStreamSchemaOutputResolver outputResolver = new MyMapStreamSchemaOutputResolver();
        getJAXBContext().generateSchema(outputResolver);
        
        InputStream instanceDocStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/compositecollection/employee.xml");
        String result = validateAgainstSchema(instanceDocStream, schemaSource, outputResolver );        
        assertTrue("Instance doc validation (employee.xml) failed unxepectedly: " + result, result == null);
    }
    
    public void testWriteInstanceDocValidation() throws Exception {
    	InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/compositecollection/employee.xsd");        
        StreamSource schemaSource = new StreamSource(schema); 
        
        MyMapStreamSchemaOutputResolver outputResolver = new MyMapStreamSchemaOutputResolver();
        getJAXBContext().generateSchema(outputResolver);
        
        InputStream instanceDocStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/compositecollection/write-employee.xml");        
        String result = validateAgainstSchema(instanceDocStream, schemaSource, outputResolver);
        assertTrue("Instance doc validation (write-employee) failed unxepectedly: " + result, result == null);
    }

}