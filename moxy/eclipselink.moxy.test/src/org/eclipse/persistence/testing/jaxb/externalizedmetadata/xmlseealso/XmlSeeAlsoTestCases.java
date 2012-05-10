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
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlseealso;

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
 * Tests generation for Employee when xml-see-also is defined.  Overrides the
 * @XmlSeeAlso on Employee (XmlSeeAlsoTestCases.class) with (MySimpleClass, 
 * MyOtherClass)
 * 
 * Positive test.
 */
public class XmlSeeAlsoTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlseealso/employee.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlseealso/employee.json";
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlSeeAlsoTestCases(String name) throws Exception{
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{Employee.class});
    }
    
    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlseealso/eclipselink-oxm.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlseealso", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
        
        return properties;
	}
    
    
    public void testSchemaGen() throws Exception{
    	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlseealso/schema1.xsd");
    	InputStream is2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlseealso/schema2.xsd");
    	controlSchemas.add(is);
    	controlSchemas.add(is2);
    	super.testSchemaGen(controlSchemas);
    	
     	InputStream schemaInputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlseealso/schema1.xsd");
    	InputStream controlDocStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
    	String result = validateAgainstSchema(controlDocStream, new StreamSource(schemaInputStream));
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);

    }

	protected Object getControlObject() {
		Employee emp = new Employee();
		emp.firstName ="firstName";
		emp.lastName = "LastName";	
		return emp;
	}
    
    /**
     * Tests generation of an xml-see-also class in the same package as Employee
     * 
     * Positive test.
     */
    
    public void testXmlSeeAlsoSamePackage() {
        
        String src = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlseealso/mysimpleclass.xml";
     	InputStream controlDocStream = ClassLoader.getSystemResourceAsStream(src);
     	InputStream schemaInputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlseealso/schema1.xsd");

        String result = validateAgainstSchema(controlDocStream, new StreamSource(schemaInputStream));
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
        
}
