/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.composite;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.w3c.dom.Document;

/**
 * Tests XmlCompositeObjectMappings via eclipselink-oxm.xml
 * 
 */
public class CompositeMappingTestCases extends JAXBTestCases {
	private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/composite/employee.xml";
	private static final String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/composite/write-employee.xml";
		    
    private static final String HOME_CITY = "Kanata";
    private static final String HOME_STREET = "66 Lakview Drive";
    private static final String HOME_PROVINCE = "ON";
    private static final String HOME_POSTAL = "K2M2K7";
    private static final String WORK_CITY = "Ottawa";
    private static final String WORK_STREET = "45 O'Connor St.";
    private static final String WORK_PROVINCE = "ON";
    private static final String WORK_POSTAL = "K1P1A4";
    private static final String ALT_CITY = "Austin";
    
    private static final String PHONE_1 = "613.288.0001";
    private static final String PHONE_2 = "613.288.0002";
    private static final String PRIVATE_NUMBER = "000.000.0000";
    private static final String FOO_NAME = "myfoo";
    private static final String DEPT_ID = "101";
    private static final String DEPT_NAME = "Sanitation";
    
    private Employee writeCtrlObject;

    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     * @throws Exception 
     */
    public CompositeMappingTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Employee.class});
        setControlDocument(XML_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);        
    }
    
    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/composite/employee-oxm.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.composite", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
		
        
        return properties;
	}
    
    public Map getPropertiesMultipleNS(){
	    InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/composite/multiplenamespaces/employee-oxm.xml");

	    HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.composite.multiplenamespaces", new StreamSource(inputStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
	   
        return properties;
    }
    
    public Map getPropertiesCyclic(){
	    InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/composite/cyclic/cyclic-oxm.xml");

	    HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.composite.cyclic", new StreamSource(inputStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
	
        return properties;
    }
    
    public Map getPropertiesMultipleNamespacesCyclic(){
    	InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/composite/cyclic/multiplenamespaces/employee-oxm.xml");

    	HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.composite.cyclic.multiplenamespaces", new StreamSource(inputStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
    	
        
        return properties;
    }
    
    public void testSchemaGen() throws Exception{
    	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/composite/employee.xsd");
    	InputStream is2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/composite/contacts.xsd");
    	controlSchemas.add(is);
    	controlSchemas.add(is2);
    	super.testSchemaGen(controlSchemas);
    }
    

    /**
     * Return the control Employee.
     * 
     * @return
     */
    public Object getControlObject() {
        Address hAddress = new Address();
        hAddress.city = HOME_CITY;
        hAddress.street = HOME_STREET;
        hAddress.province = HOME_PROVINCE;
        hAddress.postalCode = HOME_POSTAL;
        
        Address wAddress = new Address();
        wAddress.city = WORK_CITY;
        wAddress.street = WORK_STREET;
        wAddress.province = WORK_PROVINCE;
        wAddress.postalCode = WORK_POSTAL;
        
        Address aAddress = new Address();
        aAddress.city = ALT_CITY;

        Address pAddress = new Address();
        pAddress.city = WORK_CITY;
        pAddress.street = WORK_STREET;
        pAddress.province = WORK_PROVINCE;
        pAddress.postalCode = WORK_POSTAL;

        Phone pOne = new Phone();
        pOne.number = PHONE_1;
        Phone pTwo = new Phone();
        pTwo.number = PHONE_2;
        
        Department dept = new Department();
        dept.deptId = DEPT_ID;
        dept.deptName = DEPT_NAME;

        // foodata is write only 
        Foo foo = new Foo();
     
        Employee emp = new Employee();
        emp.homeAddress = hAddress;
        emp.workAddress = wAddress;
        emp.alternateAddress = aAddress;
        emp.phone1 = pOne;
        emp.phone2 = pTwo;
        emp.foo = foo;
        
        emp.department = dept;

        return emp;
    }
    
    public Object getWriteControlObject(){
    	if(writeCtrlObject == null){
    	    Employee emp = (Employee)getControlObject();
    	    emp.foo.foodata = FOO_NAME;
    	    writeCtrlObject = emp;
    	    
    	    Phone pPhone = new Phone();
            pPhone.number = PRIVATE_NUMBER;
            emp.privatePhone = pPhone;
    	}
    	return writeCtrlObject;
    }
    
    public void xmlToObjectTest(Object testObject) throws Exception {
    	super.xmlToObjectTest(testObject);
   	    assertTrue("Accessor method was not called as expected", ((Employee)testObject).wasSetCalled);
   	    assertTrue("Set was not called for absent node as expected", ((Employee)testObject).isADeptSet);   	    
    }

    public void testRoundTrip() throws Exception{
    	//doesn't apply since read and write only mappings are present    	
    }
    
    public void objectToXMLDocumentTest(Document testDocument) throws Exception {
        super.objectToXMLDocumentTest(testDocument);
        assertTrue("Accessor method was not called as expected", writeCtrlObject.wasGetCalled);
    }  
 
    public void testInstanceDocValidation() throws Exception {
    	InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/composite/employee.xsd");        
        StreamSource schemaSource = new StreamSource(schema); 
                
        MyMapStreamSchemaOutputResolver outputResolver = new MyMapStreamSchemaOutputResolver();
        getJAXBContext().generateSchema(outputResolver);
        
        InputStream instanceDocStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/composite/employee.xml");
        String result = validateAgainstSchema(instanceDocStream, schemaSource, outputResolver );        
        assertTrue("Instance doc validation (employee.xml) failed unxepectedly: " + result, result == null);
    }
    
    public void testWriteInstanceDocValidation() throws Exception {
    	InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/composite/employee.xsd");        
        StreamSource schemaSource = new StreamSource(schema); 
        
        MyMapStreamSchemaOutputResolver outputResolver = new MyMapStreamSchemaOutputResolver();
        getJAXBContext().generateSchema(outputResolver);
        
        InputStream instanceDocStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/composite/write-employee.xml");        
        String result = validateAgainstSchema(instanceDocStream, schemaSource, outputResolver);
        assertTrue("Instance doc validation (write-employee) failed unxepectedly: " + result, result == null);
    }
    
    public void testSchemaGenThreeNamespaces() throws Exception{
    	
      	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/composite/multiplenamespaces/employee.xsd");
    	InputStream is2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/composite/multiplenamespaces/contacts.xsd");
    	InputStream is3 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/composite/multiplenamespaces/address.xsd");
    	controlSchemas.add(is);
    	controlSchemas.add(is3);
    	controlSchemas.add(is2);
        
        MyStreamSchemaOutputResolver outputResolver = new MyStreamSchemaOutputResolver();
        JAXBContext ctx = JAXBContextFactory.createContext(new Class[]{org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.composite.multiplenamespaces.Employee.class}, getPropertiesMultipleNS());
        ctx.generateSchema(outputResolver);
        
        compareSchemas(controlSchemas, outputResolver.getSchemaFiles());
    }
    
    public void testSchemaGenCyclic() throws Exception{
    	
      	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/composite/cyclic/employee.xsd");
    	InputStream is2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/composite/cyclic/contacts.xsd");
    	controlSchemas.add(is);
    	controlSchemas.add(is2);
        
        MyStreamSchemaOutputResolver outputResolver = new MyStreamSchemaOutputResolver();
        JAXBContext ctx = JAXBContextFactory.createContext(new Class[]{org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.composite.cyclic.Employee.class}, getPropertiesCyclic());
        ctx.generateSchema(outputResolver);
        
        compareSchemas(controlSchemas, outputResolver.getSchemaFiles());
    }
    
    public void testSchemaGen3NamespacesCyclic() throws Exception{
   
      	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/composite/cyclic/multiplenamespaces/employee.xsd");
    	InputStream is2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/composite/cyclic/multiplenamespaces/contacts.xsd");
    	InputStream is3 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/composite/cyclic/multiplenamespaces/address.xsd");
    	controlSchemas.add(is);
    	controlSchemas.add(is2);
        controlSchemas.add(is3);
        
        MyStreamSchemaOutputResolver outputResolver = new MyStreamSchemaOutputResolver();
        JAXBContext ctx = JAXBContextFactory.createContext(new Class[]{org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.composite.cyclic.multiplenamespaces.Employee.class}, getPropertiesMultipleNamespacesCyclic());
        ctx.generateSchema(outputResolver);
        
        compareSchemas(controlSchemas, outputResolver.getSchemaFiles());
    }  
}