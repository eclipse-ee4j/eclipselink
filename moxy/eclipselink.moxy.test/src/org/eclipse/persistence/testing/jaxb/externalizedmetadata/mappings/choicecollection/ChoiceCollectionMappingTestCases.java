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
 * dmccann - April 01/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choicecollection;

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
 * Tests XmlChoiceObjectMappings via eclipselink-oxm.xml
 * 
 */
public class ChoiceCollectionMappingTestCases extends JAXBWithJSONTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choicecollection";
	private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/choicecollection/employee.xml";
	private static final String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/choicecollection/write-employee.xml";
	private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/choicecollection/employee.json";
	private static final String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/choicecollection/write-employee.json";
    private static final String INT_VAL66 = "66";
    private static final String INT_VAL99 = "99";
    private static final String FLOAT_VAL = "101.1";
    private static final String RO_INT_VAL = "77";
    private static final String RO_FLOAT_VAL = "88.8";
    private static final String WO_INT_VAL = "3";
    
    private Employee writeCtrlObject;

    public ChoiceCollectionMappingTestCases(String name) throws Exception {
        super(name);
        setContextPath(CONTEXT_PATH);
        setControlDocument(XML_RESOURCE);        
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setWriteControlJSON(JSON_WRITE_RESOURCE);
    }
    
    /**
     * Return the control Employee.
     * 
     * @return
     */
    public Object getWriteControlObject() {
    	if(writeCtrlObject == null){    
            List<Object> things = new ArrayList<Object>();
            things.add(new Integer(INT_VAL66));
            things.add(new Integer(INT_VAL99));
            things.add(new Float(FLOAT_VAL));
            List<Object> roThings = new ArrayList<Object>();
            roThings.add(new Integer(RO_INT_VAL));
            roThings.add(new Float(RO_FLOAT_VAL));
            List<Object> woThings = new ArrayList<Object>();
            woThings.add(new Integer(WO_INT_VAL));
            Employee emp = new Employee();
            emp.things = things;
            emp.readOnlyThings = roThings;
            emp.writeOnlyThings = woThings;
            writeCtrlObject = emp;
    	}
        return writeCtrlObject;
    }

    public Object getControlObject() {
    	
        List<Object> things = new ArrayList<Object>();
        things.add(new Integer(INT_VAL66));
        things.add(new Integer(INT_VAL99));
        things.add(new Float(FLOAT_VAL));
        List<Object> roThings = new ArrayList<Object>();
        roThings.add(new Integer(RO_INT_VAL));
        roThings.add(new Float(RO_FLOAT_VAL));
        
        Employee emp = new Employee();
        emp.things = things;
        emp.readOnlyThings = roThings;        
        return emp;
    }
    
    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/choicecollection/employee-oxm.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choicecollection", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
		
        
        return properties;
	}
	
    public void testSchemaGen() throws Exception{
    	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/choicecollection/employee.xsd");
    	controlSchemas.add(is);
    	super.testSchemaGen(controlSchemas);
    }
    
    public void testInstanceDocValidation() {
    	InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/choicecollection/employee.xsd");        
        StreamSource schemaSource = new StreamSource(schema); 
                
        InputStream instanceDocStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/choicecollection/employee.xml");
        String result = validateAgainstSchema(instanceDocStream, schemaSource);        
        assertTrue("Instance doc validation (employee.xml) failed unxepectedly: " + result, result == null);
    }
    
    public void testWriteInstanceDocValidation() {
    	InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/choicecollection/employee.xsd");        
        StreamSource schemaSource = new StreamSource(schema); 
                               
        InputStream instanceDocStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/choicecollection/write-employee.xml");        
        String result = validateAgainstSchema(instanceDocStream, schemaSource);
        assertTrue("Instance doc validation (write-employee) failed unxepectedly: " + result, result == null);
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
}