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
 * dmccann - January 28/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.direct;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.w3c.dom.Document;

/**
 * Tests XmlDirectMappings via eclipselink-oxm.xml
 * 
 */
public class DirectMappingTestCases extends JAXBTestCases {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/direct/employee.xml";
    private static final String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/direct/write-employee.xml";
    private static final String FNAME = "Joe";
    private static final String LNAME = "Oracle";
    private static final String PNAME = "XML External Metadata Support";
    private static final String DATA1 = "data one";
    private static final String DATA2 = "data two";
    private static final int EMPID = 66;
    private static final int MGRID = 99;
    private static final int PROJECT_ID = 999;
    private static final Double SALARY = 123456.78;
    private static final String CHARACTER_DATA = "<characters>a b c d e f g</characters>";
    private static final String PRIVATE_DATA = "This is some private data";
    private static final String EMPLOYEES_NS = "http://www.example.com/employees";
    
    private static final int PROPCOUNT = 3;
    private static final String PROPKEY_1 = "1";
    private static final String PROPKEY_2 = "2";
    private static final String PROPKEY_3 = "3";
    private static final String PROPVAL_1 = "A";
    private static final int PROPVAL_2 = 66;
    private static final boolean PROPVAL_3 = true;
    private Employee ctrlEmp;

    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public DirectMappingTestCases(String name) throws Exception{
        super(name);
        setControlDocument(XML_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setClasses(new Class[]{Employee.class});
    }

    public Object getControlObject() {
        Employee ctrlEmp = new Employee();
        ctrlEmp.firstName = FNAME;
        ctrlEmp.lastName = LNAME;
        ctrlEmp.empId = EMPID;
        ctrlEmp.mgrId = MGRID;
        ctrlEmp.setProject(PNAME);
        ctrlEmp.data1 = DATA1;
        ctrlEmp.data2 = DATA2;
        ctrlEmp.salary = SALARY;
        ctrlEmp.privateData = PRIVATE_DATA;
        ctrlEmp.characterData = CHARACTER_DATA;
        ctrlEmp.projectId = PROJECT_ID;
        
        // 'privateData' is write only
        ctrlEmp.privateData = null;
        // JAXB will default a null String to "" 
        ctrlEmp.someString = "";
        return ctrlEmp;
    }
    
    public Object getWriteControlObject() {
    	if(ctrlEmp == null){
        ctrlEmp = new Employee();
        ctrlEmp.firstName = FNAME;
        ctrlEmp.lastName = LNAME;
        ctrlEmp.empId = EMPID;
        ctrlEmp.mgrId = MGRID;
        ctrlEmp.setProject(PNAME);
        ctrlEmp.data1 = DATA1;
        ctrlEmp.data2 = DATA2;
        ctrlEmp.salary = SALARY;
        ctrlEmp.privateData = PRIVATE_DATA;
        ctrlEmp.characterData = CHARACTER_DATA;
        ctrlEmp.projectId = PROJECT_ID;
        ctrlEmp.setSomeString(null);
    	}
       
        return ctrlEmp;
    }
    
    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/direct/eclipselink-oxm.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
		metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.direct", new StreamSource(inputStream));
		Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
		properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
	        
	    return properties;
	}
	    
	    
	public void testSchemaGen() throws Exception{
	   	List controlSchemas = new ArrayList();
	   	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/direct/employees.xsd");
	   	InputStream is2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/direct/projects.xsd");
	   	controlSchemas.add(is);
	   	controlSchemas.add(is2);
	   	
	   	super.testSchemaGen(controlSchemas);	  
	}
	
	public void xmlToObjectTest(Object testObject) throws Exception{
		super.xmlToObjectTest(testObject);
		Employee empObj=(Employee)testObject;
	     assertTrue("Accessor method was not called as expected", empObj.wasSetCalled);
         assertTrue("Set was not called for absent node as expected", empObj.isAStringSet);
  
	}
	public void objectToXMLDocumentTest(Document testDocument) throws Exception{
	   super.objectToXMLDocumentTest(testDocument);
       assertTrue("Accessor method was not called as expected", ctrlEmp.wasGetCalled);
	}
	
	public void testRoundTrip(){
		//not applicable with write only mappings
	}

	 public void testObjectToContentHandler() throws Exception {
	    	//See Bug 355143 

	    }

	 
    /**
     * Test setting property-level properties via xml-element.
     * Tests setting a String, Integer and Boolean.
     * 
     * Positive test.
     */
    public void testMappingProperties() {
        // get the Employee descriptor
        XMLDescriptor xdesc = xmlContext.getDescriptor(new QName(EMPLOYEES_NS, "employee"));

        DatabaseMapping projectNameMapping = xdesc.getMappingForAttributeName("projectName");
        assertNotNull("No user-defined properties exist on the mapping for [projectName]", projectNameMapping.getProperties());
        validateProperties(projectNameMapping.getProperties());
    }
    
    /**
     * Test setting descriptor-level properties via XML metadata.
     * Tests setting a String, Integer and Boolean.
     * 
     * Positive test.
     */
    public void testDescriptorProperties() {
        XMLDescriptor xdesc = xmlContext.getDescriptor(new QName(EMPLOYEES_NS, "employee"));
        assertNotNull("Employee descriptor is null", xdesc);
        assertNotNull("No user-defined properties exist on the descriptor for Employee", xdesc.getProperties());
        validateProperties(xdesc.getProperties());
    }
    
    /**
     * Validates user-defined properties set via xml metadata.
     * 
     * @param props
     */
    private void validateProperties(Map props) {
        assertTrue("Expected [" + PROPCOUNT + "] user-defined properties, but there were [" + props.size() + "]", props.size() == PROPCOUNT);
        // verify entries exist for each key
        assertNotNull("No property found for key [" + PROPKEY_1 + "]", props.get(PROPKEY_1));
        assertNotNull("No property found for key [" + PROPKEY_2 + "]", props.get(PROPKEY_2));
        assertNotNull("No property found for key [" + PROPKEY_3 + "]", props.get(PROPKEY_3));
        // verify value-types
        assertTrue("Expected value-type [String] for key [" + PROPKEY_1 + "] but was [" + props.get(PROPKEY_1).getClass().getName() + "]", props.get(PROPKEY_1) instanceof String);
        assertTrue("Expected value-type [Integer] for key [" + PROPKEY_2 + "] but was [" + props.get(PROPKEY_2).getClass().getName() + "]", props.get(PROPKEY_2) instanceof Integer);
        assertTrue("Expected value-type [Boolean] for key [" + PROPKEY_3 + "] but was [" + props.get(PROPKEY_3).getClass().getName() + "]", props.get(PROPKEY_3) instanceof Boolean);
        // verify values
        assertTrue("Expected property value [" + PROPVAL_1 + "] for key [" + PROPKEY_1 + "] but was [" + props.get(PROPKEY_1) + "]", PROPVAL_1.equals(props.get(PROPKEY_1)));
        assertTrue("Expected property value [" + PROPVAL_2 + "] for key [" + PROPKEY_2 + "] but was [" + props.get(PROPKEY_2) + "]", PROPVAL_2 == (Integer) props.get(PROPKEY_2));
        assertTrue("Expected property value [" + PROPVAL_3 + "] for key [" + PROPKEY_3 + "] but was [" + props.get(PROPKEY_3) + "]", PROPVAL_3 == (Boolean) props.get(PROPKEY_3));
    }
}