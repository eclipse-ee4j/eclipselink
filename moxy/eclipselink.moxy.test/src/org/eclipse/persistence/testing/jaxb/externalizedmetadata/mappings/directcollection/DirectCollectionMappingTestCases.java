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
 * dmccann - March 24/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.directcollection;

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
 * Tests XmlDirectCollectionMapping via eclipselink-oxm.xml
 *
 */
public class DirectCollectionMappingTestCases extends JAXBWithJSONTestCases {
    
	private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/directcollection/employee.xml";
	private static final String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/directcollection/write-employee.xml";
	private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/directcollection/employee.json";
	private static final String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/directcollection/write-employee.json";

    private static final int EMPID = 101;
    private static final String PRJ_ID1 = "01";
    private static final String PRJ_ID2 = "10";
    private static final String PRJ_ID3 = "11";
    private static final String SAL_1 = "123456.78";
    private static final String SAL_2 = "234567.89";
    private static final String PDATA_1 = "This is some private data";
    private static final String PDATA_2 = "This is more private data";
    private static final String CDATA_1 = "<characters>a b c d e f g</characters>";
    private static final String CDATA_2 = "<characters>h i j k l m n</characters>";
    
    private Employee writeCtrlObject;
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public DirectCollectionMappingTestCases(String name) throws Exception{
        super(name);
        setControlDocument(XML_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setWriteControlJSON(JSON_WRITE_RESOURCE);
        setClasses(new Class[]{});
    }

    /**
     * Create the control Employee.
     */
    public Object getControlObject() {
        List<String> prjIds = new ArrayList<String>();
        prjIds.add(PRJ_ID1);
        prjIds.add("");
        prjIds.add(PRJ_ID2);
        prjIds.add(PRJ_ID3);
        
        List<Float> sals = new ArrayList<Float>();
        sals.add(Float.valueOf(SAL_1));
        sals.add(Float.valueOf(SAL_2));
        

        List<String> cData = new ArrayList<String>();
        cData.add(CDATA_1);
        cData.add(CDATA_2);

        Employee ctrlEmp = new Employee();
        ctrlEmp.id = EMPID;
        ctrlEmp.projectIds = prjIds;
        ctrlEmp.salaries = sals;
        
        ctrlEmp.characterData = cData;
        return ctrlEmp;
    }
 
    
    public Object getWriteControlObject() {
    	if(writeCtrlObject == null){
    		writeCtrlObject = (Employee)getControlObject();
    		List<String> pData = new ArrayList<String>();
    	    pData.add(PDATA_1);
    	    pData.add(PDATA_2);
    	    writeCtrlObject.privateData = pData;
    	    
    	    writeCtrlObject.projectIds.remove(1);
    	    writeCtrlObject.projectIds.add(1, null);
    	}
    	return writeCtrlObject;        
   
    }
    
    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/directcollection/eclipselink-oxm.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.directcollection", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
        
        return properties;
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
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/directcollection/employees.xsd");
    	controlSchemas.add(is);
    	super.testSchemaGen(controlSchemas);
    }
    
    public void testInstanceDocValidation() throws Exception {
    	InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/directcollection/employees.xsd");        
        StreamSource schemaSource = new StreamSource(schema); 
                
        MyMapStreamSchemaOutputResolver outputResolver = new MyMapStreamSchemaOutputResolver();
        getJAXBContext().generateSchema(outputResolver);
        
        InputStream instanceDocStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/directcollection/employee.xml");
        String result = validateAgainstSchema(instanceDocStream, schemaSource, outputResolver );        
        assertTrue("Instance doc validation (employee.xml) failed unxepectedly: " + result, result == null);
    }
    
    public void testWriteInstanceDocValidation() throws Exception {
    	InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/directcollection/employees.xsd");        
        StreamSource schemaSource = new StreamSource(schema); 
        
        MyMapStreamSchemaOutputResolver outputResolver = new MyMapStreamSchemaOutputResolver();
        getJAXBContext().generateSchema(outputResolver);
        
        InputStream instanceDocStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/directcollection/write-employee.xml");        
        String result = validateAgainstSchema(instanceDocStream, schemaSource, outputResolver);
        assertTrue("Instance doc validation (write-employee) failed unxepectedly: " + result, result == null);
    }
       
    public void testObjectToContentHandler() throws Exception {
    	//See Bug 355143 

    }
}