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
 * dmccann - April 01/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choice;

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

public class ChoiceMappingEmployeeTestCases extends JAXBWithJSONTestCases{
	private static final String INT_VAL = "66";
	private static final String FLT_VAL = "66.66";
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/choice/employee.xml";
    private static final String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/choice/write-employee.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/choice/employee.json";
    private static final String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/choice/write-employee.json";

    private Employee ctrlObject;
    
	public ChoiceMappingEmployeeTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		setWriteControlDocument(XML_WRITE_RESOURCE);
		setControlJSON(JSON_RESOURCE);
		setWriteControlJSON(JSON_WRITE_RESOURCE);
		setContextPath("org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choice");	
	}

	public Object getWriteControlObject() {
		if(ctrlObject ==null){
	        Employee emp = new Employee();
	        emp.thing = new Integer(INT_VAL);
	        emp.readOnlyThing = new Float(FLT_VAL);
	        emp.writeOnlyThing = new Integer(INT_VAL);
	        ctrlObject = emp;
		}
	    return ctrlObject;
	}
	
	public Object getControlObject() {		
		if(ctrlObject ==null){
	        Employee emp = new Employee();
	        emp.thing = new Integer(INT_VAL);
	        emp.readOnlyThing = new Float(FLT_VAL);	        
	        ctrlObject = emp;
		}
	    return ctrlObject;
	}
	
	public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/choice/employee-oxm.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choice", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
		
        
        return properties;
	}
	
    public void objectToXMLDocumentTest(Document testDocument) throws Exception {
    	super.objectToXMLDocumentTest(testDocument);
        assertTrue("Accessor method was not called as expected", ctrlObject.wasGetCalled);
    }

    public void testRoundTrip() throws Exception{
    	//doesn't apply since read and write only mappings are present    	
    }
    
    public void testSchemaGen() throws Exception{
    	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/choice/employee.xsd");
    	controlSchemas.add(is);
    	super.testSchemaGen(controlSchemas);
    }
    
    public void testInstanceDocValidation() {
    	InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/choice/employee.xsd");        
        StreamSource schemaSource = new StreamSource(schema); 
                
        InputStream instanceDocStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/choice/employee.xml");
        String result = validateAgainstSchema(instanceDocStream, schemaSource);        
        assertTrue("Instance doc validation (employee.xml) failed unxepectedly: " + result, result == null);
    }
    
    public void testWriteInstanceDocValidation() {
    	InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/choice/employee.xsd");        
        StreamSource schemaSource = new StreamSource(schema); 
                               
        InputStream instanceDocStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/choice/write-employee.xml");        
        String result = validateAgainstSchema(instanceDocStream, schemaSource);
        assertTrue("Instance doc validation (write-employee) failed unxepectedly: " + result, result == null);
    }
	
}
