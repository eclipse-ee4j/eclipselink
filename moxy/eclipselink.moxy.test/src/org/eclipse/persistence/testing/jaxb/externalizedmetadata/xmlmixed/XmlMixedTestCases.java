/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - November 04/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmixed;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Element;

/**
 * Tests XmlMixed via eclipselink-oxm.xml
 *
 */
public class XmlMixedTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlmixed/employee.xml";
    private static final String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlmixed/employee_write.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlmixed/employee.json";
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     * @throws Exception 
     */
    public XmlMixedTestCases(String name) throws Exception {
        super(name);                
        setClasses(new Class[] { Employee.class });
        setControlDocument(XML_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

	 public Map getProperties(){
			InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlmixed/eclipselink-oxm.xml");

			HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
		    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmixed", new StreamSource(inputStream));
		    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
		    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
	        
	        return properties;
		}
    
    protected Object getControlObject() {
    	Employee emp = new Employee();
    	emp.a = 1;
    	emp.b = "3";
    	emp.stuff = new ArrayList();    
    	emp.stuff.add("blah.");
    	Element elem = parser.newDocument().createElementNS("extra","stuff");
    	elem.setTextContent("This is my stuff.");
    	emp.stuff.add(elem);
    	emp.stuff.add("lame.");

		return emp;
	}
    
   
    
    public void xmlToObjectTest(Object testObject) throws Exception {
    	assertTrue(testObject instanceof Employee);
    	Employee emp = (Employee)testObject;
    	 assertNotNull("The Employee did not umnmarshal correctly: 'stuff' is null.", emp.stuff);
        assertTrue("The Employee did not umnmarshal correctly: expected 'stuff' size of [3] but was [" + emp.stuff.size() + "]", emp.stuff.size() == 3);
        assertTrue("The Employee did not umnmarshal correctly: expected 'stuff.0' to be instanceof [String] but was [" + emp.stuff.get(0) + "]", emp.stuff.get(0) instanceof String);
        assertTrue("The Employee did not umnmarshal correctly: expected 'stuff.1' to be instanceof [Element] but was [" + emp.stuff.get(1) + "]", emp.stuff.get(1) instanceof Element);
        assertTrue("The Employee did not umnmarshal correctly: expected 'stuff.2' to be instanceof [String] but was [" + emp.stuff.get(2) + "]", emp.stuff.get(2) instanceof String);

    }
    
    
    public void jsonToObjectTest(Object testObject) throws Exception {
    	assertTrue(testObject instanceof Employee);
    	Employee emp = (Employee)testObject;
    	assertNotNull("The Employee did not umnmarshal correctly: 'stuff' is null.", emp.stuff);
        assertTrue("The Employee did not umnmarshal correctly: expected 'stuff' size of [3] but was [" + emp.stuff.size() + "]", emp.stuff.size() == 3);
        assertTrue("The Employee did not umnmarshal correctly: expected 'stuff.2' to be instanceof [String] but was [" + emp.stuff.get(2) + "]", emp.stuff.get(2) instanceof String);        
        assertTrue("The Employee did not umnmarshal correctly: expected 'stuff.1' to be instanceof [String] but was [" + emp.stuff.get(1) + "]", emp.stuff.get(1) instanceof String);
        assertTrue("The Employee did not umnmarshal correctly: expected 'stuff.0' to be instanceof [Element] but was [" + emp.stuff.get(0) + "]", emp.stuff.get(0) instanceof Element);

    }
    public void testSchemaGen() throws Exception{
    	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlmixed/schema.xsd");	    	
    	controlSchemas.add(is);	    	
    	super.testSchemaGen(controlSchemas);
    	
    }
    
    public void testInstanceDocValidation() {
    	InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlmixed/schema.xsd");        
        StreamSource schemaSource = new StreamSource(schema); 
                
        InputStream instanceDocStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        String result = validateAgainstSchema(instanceDocStream, schemaSource);        
        assertTrue("Instance doc validation (employee.xml) failed unxepectedly: " + result, result == null);
    }
    
}
