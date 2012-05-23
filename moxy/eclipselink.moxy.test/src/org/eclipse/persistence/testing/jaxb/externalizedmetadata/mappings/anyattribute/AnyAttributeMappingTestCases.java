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
 * dmccann - March 25/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anyattribute;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 * Tests XmlAnyAttributeMapping via eclipselink-oxm.xml
 *
 */
public class AnyAttributeMappingTestCases extends JAXBWithJSONTestCases {
    
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/anyattribute/employee.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/anyattribute/employee.json";
    
    private static final String FNAME = "Joe";
    private static final String LNAME = "Oracle";
    private static final String FIRST_NAME = "first-name";
    private static final String LAST_NAME = "last-name";
    private static final String OTHER_NS = "http://www.example.com/other";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public AnyAttributeMappingTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{Employee.class});
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("http://www.example.com/other", "ns0");
        namespaces.put("http://www.example.com/stuff", "s");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, namespaces);
    }

    public JAXBMarshaller getJSONMarshaller() throws Exception{
    	JAXBMarshaller jsonMarshaller = (JAXBMarshaller) jaxbContext.createMarshaller();
    	jsonMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
    	 Map<String, String> namespaces = new HashMap<String, String>();
         namespaces.put("http://www.example.com/other", "ns0");
         namespaces.put("http://www.example.com/stuff", "s");
    	
    	jsonMarshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespaces);
    	return jsonMarshaller;

    }
    
    /**
     * Create the control Employee.
     */
    public Object getControlObject() {
        Employee ctrlEmp = new Employee();
        HashMap stuff = new HashMap();
        QName qname = new QName(OTHER_NS, FIRST_NAME);
        stuff.put(qname, FNAME);
        qname = new QName(OTHER_NS, LAST_NAME);
        stuff.put(qname, LNAME);
        ctrlEmp.stuff = stuff;
        return ctrlEmp;
    }

    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/anyattribute/employee-oxm.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
		metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anyattribute", new StreamSource(inputStream));
		Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
		properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
	        
	    return properties;
	}
	    
public void testSchemaGen() throws Exception{
   	List controlSchemas = new ArrayList();
   	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/anyattribute/employee.xsd");
   	InputStream is2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/anyattribute/stuff.xsd");
   	controlSchemas.add(is);
   	controlSchemas.add(is2);	   	
   	
   	super.testSchemaGen(controlSchemas);	  
}
    
}