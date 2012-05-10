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
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessororder;

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
 * Tests XmlAccessorOrder via eclipselink-oxm.xml
 *
 */

public class XmlAccessorOrderTestCases extends JAXBWithJSONTestCases {
 
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessororder";
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlaccessororder/employee-ordered.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlaccessororder/employee-ordered.json";
    private static final String XML_UNORDERED_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlaccessororder/employee-unordered.xml";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlAccessorOrderTestCases(String name) throws Exception{
        super(name);
        setControlDocument(XML_RESOURCE);
        setContextPath(CONTEXT_PATH);
        setControlJSON(JSON_RESOURCE);
    }
    
    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlaccessororder/eclipselink-oxm.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessororder", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
        
        return properties;
	}
    
    public void testSchemaGen() throws Exception{
    	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlaccessororder/employee.xsd");
    	controlSchemas.add(is);
    	super.testSchemaGen(controlSchemas);
    }
    
    public void testInstanceDocValidation() throws Exception {
    	InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlaccessororder/employee.xsd");        
        StreamSource schemaSource = new StreamSource(schema); 
                
        MyMapStreamSchemaOutputResolver outputResolver = new MyMapStreamSchemaOutputResolver();
        getJAXBContext().generateSchema(outputResolver);
        
        InputStream instanceDocStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        String result = validateAgainstSchema(instanceDocStream, schemaSource, outputResolver );        
        assertTrue("Instance doc validation (employee.xml) failed unxepectedly: " + result, result == null);
    }
    
    
    public void testNegativeInstanceDocValidation() throws Exception {
    	InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlaccessororder/employee.xsd");       
        StreamSource schemaSource = new StreamSource(schema); 
                
        MyMapStreamSchemaOutputResolver outputResolver = new MyMapStreamSchemaOutputResolver();
        getJAXBContext().generateSchema(outputResolver);
        
        InputStream instanceDocStream = ClassLoader.getSystemResourceAsStream(XML_UNORDERED_RESOURCE);
        String result = validateAgainstSchema(instanceDocStream, schemaSource, outputResolver );        
        assertTrue("Schema validation passed unxepectedly", result != null);
    }   

	
	protected Object getControlObject() {
		Employee emp = new Employee();
		emp.a = "A-String";
	    emp.b = "B-string";
	    emp.g =	"G-String";
		return emp;
	}

}
