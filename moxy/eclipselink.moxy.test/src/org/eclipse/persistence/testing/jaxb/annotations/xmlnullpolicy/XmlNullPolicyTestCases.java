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
 * dmccann - January 28/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlnullpolicy;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.w3c.dom.Document;

/**
 * Tests XmlDirectMappings via eclipselink-oxm.xml
 * 
 */
public class XmlNullPolicyTestCases extends JAXBTestCases {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlnullpolicy/employee.xml";
    private static final String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlnullpolicy/write-employee.xml";
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
    
    private Employee ctrlEmp;
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlNullPolicyTestCases(String name) throws Exception{
        super(name);
        setClasses(new Class[] { Employee.class });
        setControlDocument(XML_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);
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
    public void testSchemaGen() throws Exception{
	   	List controlSchemas = new ArrayList();
	   	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/annotations/xmlnullpolicy/employees.xsd");
	   	InputStream is2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/annotations/xmlnullpolicy/employees2.xsd");
	   	controlSchemas.add(is);
	   	controlSchemas.add(is2);	   	
	   	super.testSchemaGen(controlSchemas);	  
	}
	
    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/annotations/xmlnullpolicy/eclipselink-oxm.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
		metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.annotations.xmlnullpolicy", new StreamSource(inputStream));
		Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
		properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
	        
	    return properties;
	}

    public void objectToXMLDocumentTest(Document testDocument) throws Exception{
    	super.objectToXMLDocumentTest(testDocument);
        assertTrue("Accessor method was not called as expected", ctrlEmp.wasGetCalled);

    }
        
    public void xmlToObjectTest(Object testObject) throws Exception{
    	super.xmlToObjectTest(testObject);
    	Employee empObj = (Employee)testObject;
        assertTrue("Accessor method was not called as expected", empObj.wasSetCalled);
        assertTrue("Set was not called for absent node as expected", empObj.isAStringSet);
  

    }
	
	public void testRoundTrip(){
		//not applicable with write only mappings
	}

	 public void testObjectToContentHandler() throws Exception {
	   	//See Bug 355143 

	 }

}