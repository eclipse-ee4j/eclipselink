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
 * dmccann - June 17/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltype;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 * Tests XmlType via eclipselink-oxm.xml
 *
 */
public class XmlTypeTestCases extends JAXBWithJSONTestCases {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltype/employee.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltype/employee.json";    
    static final int EMP_ID = 101;
    static final String EMP_FIRST = "Joe";
    static final String EMP_LAST = "Oracle";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlTypeTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[] { Employee.class });
    }
    
    /**
     * Create the control Employee.
     */
    public Object getControlObject() {
        Employee ctrlEmp = new Employee();
        ctrlEmp.id = EMP_ID;
        ctrlEmp.firstName = EMP_FIRST;
        ctrlEmp.lastName = EMP_LAST;
        return ctrlEmp;
    }
    
    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltype/eclipselink-oxm.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
		metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltype", new StreamSource(inputStream));
		Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
		properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
	        
	    return properties;
	}
	    
	    
	public void testSchemaGen() throws Exception{
	   	List controlSchemas = new ArrayList();
	   	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltype/employee.xsd");	    	
	   	controlSchemas.add(is);
	   	
	   	super.testSchemaGen(controlSchemas);	  
	}
    

    /**
     * Validate instance document(s).
     * 
     * Validates @XmlType override via eclipselink-oxm.xml.  Overrides type 
     * name (my-employee-type) with (employee-type) and propOrder
     * ("id", "firstName", "lastName") with ("id", "lastName", "firstName")
     */
    public void testInstanceDocValidation() {
    	
    	InputStream schemaInputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltype/employee.xsd");
    	InputStream controlDocStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
    	String result = validateAgainstSchema(controlDocStream, new StreamSource(schemaInputStream));
    	assertTrue("Instance doc validation (employee.xml) failed unxepectedly: " + result, result == null);
        
    	InputStream schemaInputStream2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltype/employee.xsd");
    	InputStream controlDocStream2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltype/employee-invalid.xml");
    	
    	result = validateAgainstSchema(controlDocStream2, new StreamSource(schemaInputStream2));
        assertTrue("Instance doc validation (employee-invalid) succeeded unxepectedly: " + result, result != null);
    }
}
