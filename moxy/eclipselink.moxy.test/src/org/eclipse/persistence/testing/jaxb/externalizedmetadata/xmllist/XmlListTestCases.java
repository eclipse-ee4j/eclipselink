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
 * dmccann - October 15/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmllist;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

/**
 * Tests XmlList via eclipselink-oxm.xml
 *
 */
public class XmlListTestCases extends JAXBTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmllist";
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmllist/employee.xml";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlListTestCases(String name) throws Exception{
        super(name);
        setControlDocument(XML_RESOURCE);
        setContextPath(CONTEXT_PATH);
    }
    
    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmllist/eclipselink-oxm.xml");
		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmllist", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
        
        return properties;
	}    

	public void testSchemaGen() throws Exception{
    	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmllist/schema.xsd");    	
    	controlSchemas.add(is);    	
    	
    	super.testSchemaGen(controlSchemas);
    	
    	InputStream schemaInputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmllist/schema.xsd");
    	InputStream controlDocStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
    	validateAgainstSchema(controlDocStream, new StreamSource(schemaInputStream));

    	
    }

    /**
     * Tests exception handling for @XmlList via eclipselink-oxm.xml.
     * This test should cause an exception as xml-list can only be used
     * with a collection or array, and in this case it is being set on
     * a String property.
     * 
     * Negative test.
     */
    public void testXmlListInvalid() {
    	
    	InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmllist/eclipselink-oxm-invalid.xml");
		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmllist", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> invalidProperties = new HashMap<String, Map<String, Source>>();
	    invalidProperties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
        
        boolean exception = false;

	    try {
            JAXBContext jaxbContext = (JAXBContext) JAXBContextFactory.createContext(CONTEXT_PATH, getClass().getClassLoader(), invalidProperties);
            MySchemaOutputResolver msor = new MySchemaOutputResolver();
            jaxbContext.generateSchema(msor);
        } catch (Exception ex) {
            exception = true;
        }
        assertTrue("The expected exception was not thrown", exception);

    }

	protected Object getControlObject() {
		  
        Employee emp = new Employee();
        java.util.List<String> data = new ArrayList<String>();
        data.add("xxx");
        data.add("yyy");
        data.add("zzz");
        emp.data = data;
        return emp;
        
	}
}
