/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - June 17/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 * Tests XmlTransient via eclipselink-oxm.xml
 * 
 */
public class XmlTransientTestCases extends JAXBWithJSONTestCases{    
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltransient/employee.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltransient/employee.json";

    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlTransientTestCases(String name) throws Exception{
        super(name);
        setClasses(new Class[]{Employee.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }
    
	 public Map getProperties(){
			InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltransient/eclipselink-oxm.xml");

			HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
			metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient", new StreamSource(inputStream));
			Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
			properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
		        
		    return properties;
		}

	public void testSchemaGen() throws Exception{
	   	List controlSchemas = new ArrayList();
	   	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltransient/employee.xsd");	    	
	   	controlSchemas.add(is);
	   	
	   	super.testSchemaGen(controlSchemas);
	   	
	   	InputStream src = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
	   	InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltransient/employee.xsd");
	   	String result = validateAgainstSchema(src, new StreamSource(schema));
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
  
	}
   
    /**
     * Test marking the Address class as transient. Validation for address.xml should fail as
     * Address is marked transient.
     * 
     * Negative test.
     */
    
    public void testXmlTransientOnClassInvalid() {
      	InputStream src = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltransient/address.xml");
	   	InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltransient/employee.xsd");
	   	String result = validateAgainstSchema(src, new StreamSource(schema));
	   	assertTrue("Schema validation succeeded unxepectedly", result != null);    	    	
    }

    /**
     * Test marking the myInt property on Employee as transient. Validation for
     * employee-invalidproperty.xml should fail.
     * 
     * Negative test.
     */
    public void testXmlTransientOnProperty() {
        
      	InputStream src = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltransient/employee-invalidproperty.xml");
	   	InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltransient/employee.xsd");
	   	String result = validateAgainstSchema(src, new StreamSource(schema));
        assertTrue("Schema validation succeeded unxepectedly", result != null);
    	    	

    }

    /**
     * Test marking the lastName field on Employee as transient. Validation for
     * employee-invalidfield.xml should fail.
     * 
     * Negative test.
     */
    
    public void testXmlTransientOnField() {
     	InputStream src = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltransient/employee-invalidfield.xml");
	   	InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltransient/employee.xsd");
	   	String result = validateAgainstSchema(src, new StreamSource(schema));
        assertTrue("Schema validation succeeded unxepectedly", result != null);
   
    }

   
    /**
     * Test a reference to a transient class.  Here, ContactInfo has a List<Address> where
     * Address is marked transient via XML metadata.  A JAXBException should be thrown.
     * 
     * Negative test.
     */
    public void testReferenceToTransientClassException() {
        try {
        	InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltransient/contactinfo-oxm.xml");
    		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
    		metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient.inheritance", new StreamSource(inputStream));
    		Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
    		properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
    	        
        	JAXBContextFactory.createContext(new Class[] { ContactInfo.class }, properties);
        } catch (JAXBException jaxbex) {
            return;
        }
        fail("The expected exception was not thrown.");
    }
	
	protected Object getControlObject() {
		Employee emp = new Employee();
		emp.firstName = "firstName";
		return emp;
	}
}
