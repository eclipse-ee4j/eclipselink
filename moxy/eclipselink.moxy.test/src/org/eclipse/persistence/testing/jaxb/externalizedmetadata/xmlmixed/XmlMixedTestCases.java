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

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Element;

/**
 * Tests XmlMixed via eclipselink-oxm.xml
 *
 */
public class XmlMixedTestCases extends JAXBTestCases {
    private boolean shouldGenerateSchema = true;
    private MySchemaOutputResolver outputResolver; 
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmixed";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlmixed/";
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlmixed/employee.xml";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlMixedTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setClasses(new Class[]{Employee.class});
    }

    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlmixed/eclipselink-oxm.xml");
		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmixed", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
        
        return properties;
	}    

	public void testSchemaGen() throws Exception{
    	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlmixed/schema.xsd");    	
    	controlSchemas.add(is);    	
    	
    	super.testSchemaGen(controlSchemas);
    	
    	InputStream schemaInputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlmixed/schema.xsd");
    	InputStream controlDocStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
    	validateAgainstSchema(controlDocStream, new StreamSource(schemaInputStream));

    	
    }
   
    
    /**
     * 
     * Positive test.
     *//*
    public void testXmlMixedUnmarshal() {
    	
    	Class[] classes = new Class[] { Employee.class };
    	MySchemaOutputResolver outputResolver = generateSchema(classes, CONTEXT_PATH, PATH, 1);

        // test unmarshal
        Employee emp = null;
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        try {
            String src = PATH + "employee.xml";
            emp = (Employee) unmarshaller.unmarshal(getControlDocument(src));
            assertNotNull("The Employee object is null after unmarshal.", emp);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred unmarshalling the document.");
        }

        assertNotNull("The Employee did not umnmarshal correctly: 'stuff' is null.", emp.stuff);
        assertTrue("The Employee did not umnmarshal correctly: expected 'stuff' size of [3] but was [" + emp.stuff.size() + "]", emp.stuff.size() == 3);
        assertTrue("The Employee did not umnmarshal correctly: expected 'stuff.0' to be instanceof [String] but was [" + emp.stuff.get(0) + "]", emp.stuff.get(0) instanceof String);
        assertTrue("The Employee did not umnmarshal correctly: expected 'stuff.1' to be instanceof [Element] but was [" + emp.stuff.get(1) + "]", emp.stuff.get(1) instanceof Element);
        assertTrue("The Employee did not umnmarshal correctly: expected 'stuff.2' to be instanceof [String] but was [" + emp.stuff.get(2) + "]", emp.stuff.get(2) instanceof String);
    }
*/	
	protected Object getControlObject() {
		Employee emp = new Employee();
		emp.a = 1;
		emp.b = "3";
		List stuffList= new ArrayList();
		stuffList.add("\n ");
		stuffList.add("\n blah. \n");
		stuffList.add("\n ");
		
		Element elem = parser.newDocument().createElementNS("extra", "e:stuff");
		elem.setTextContent("This is my stuff.");
		stuffList.add(elem); 
		
		stuffList.add("\n lame. \n");

		emp.stuff = stuffList;
		
		return emp;
	}
}
