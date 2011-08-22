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
 * dmccann - October 26/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyattribute;

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
import org.eclipse.persistence.oxm.mappings.XMLAnyAttributeMapping;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

/**
 * Tests XmlAnyElement via eclipselink-oxm.xml
 *
 */
public class XmlAnyAttributeTestCases extends JAXBTestCases {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyattribute/employee.xml";
    public static final String RETURN_STRING = "Giggity";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlAnyAttributeTestCases(String name) throws Exception{
        super(name);
        setControlDocument(XML_RESOURCE);
        setClasses(new Class[]{Employee.class});
    }
    
    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyattribute/eclipselink-oxm.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyattribute", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
        
        return properties;
	}
	
  
    /**
     * Test setting the map class via container-type attribute.
     * 
     * Positive test.
     */
    public void testContainerType() {
        XMLDescriptor xDesc = xmlContext.getDescriptor(new QName("employee"));
        assertNotNull("No descriptor was generated for Employee.", xDesc);
        DatabaseMapping mapping = xDesc.getMappingForAttributeName("stuff");
        assertNotNull("No mapping exists on Employee for attribute [stuff].", mapping);
        assertTrue("Expected an XMLAnyAttributeMapping for attribute [stuff], but was [" + mapping.toString() +"].", mapping instanceof XMLAnyAttributeMapping);
        assertTrue("Expected map class [java.util.LinkedHashMap] but was ["+((XMLAnyAttributeMapping) mapping).getContainerPolicy().getContainerClassName()+"]", ((XMLAnyAttributeMapping) mapping).getContainerPolicy().getContainerClassName().equals("java.util.LinkedHashMap"));
    }

	protected Object getControlObject() {
        Employee ctrlEmp = new Employee();
	    ctrlEmp.a = 1;
	    ctrlEmp.b = "3";
	    HashMap<QName, Object> someStuff = new HashMap<QName, Object>();
	    someStuff.put(new QName("www.example.com", "stuff"), "blah");
	    ctrlEmp.stuff = someStuff;
	    return ctrlEmp;
	}
	
    public void testSchemaGen() throws Exception {
    	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyattribute/schema.xsd");
    	controlSchemas.add(is);
    	super.testSchemaGen(controlSchemas);
    }
}
