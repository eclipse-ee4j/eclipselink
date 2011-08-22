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
 * dmccann - July 14/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlattribute;

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
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

/**
 * Tests XmlAttribute via eclipselink-oxm.xml
 *
 */
public class XmlAttributeTestCases extends JAXBTestCases {
	
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlattribute/employee.xml";
    private static final String XML_RESOURCE_INVALID = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlattribute/employee-invalid.xml";

    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlAttributeTestCases(String name) throws Exception{
        super(name);
        setControlDocument(XML_RESOURCE);
        setClasses(new Class[]{Employee.class});
    }
    
    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlattribute/eclipselink-oxm.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlattribute", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
        
        return properties;
	}
    
    
    public void testSchemaGen() throws Exception{
    	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlattribute/schema.xsd");
    	
    	controlSchemas.add(is);
    	
    	super.testSchemaGen(controlSchemas);
    	
    	
    }
 

    /**
     * Tests @XmlAttribute override via eclipselink-oxm.xml.  The id attribute
     * is set to 'required' in the xml file, but the instance document does
     * not have an id attribute.
     * 
     * Negative test.
     */
    public void testXmlAttributeOverrideInvalid() {
    	InputStream is2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlattribute/schema.xsd");
    	
    	String result =super.validateAgainstSchema(ClassLoader.getSystemResourceAsStream(XML_RESOURCE_INVALID), new StreamSource(is2));
        assertTrue("Schema validation passed unxepectedly", result != null);
    }
  
    /**
     * Test setting the container class via container-type attribute.
     * 
     * Positive test.
     */
    public void testContainerType() {
        XMLDescriptor xDesc =xmlContext.getDescriptor(new QName("employee"));
        assertNotNull("No descriptor was generated for Employee.", xDesc);
        DatabaseMapping mapping = xDesc.getMappingForAttributeName("things");
        assertNotNull("No mapping exists on Employee for attribute [things].", mapping);
        assertTrue("Expected an XMLCompositeDirectCollectionMapping for attribute [things], but was [" + mapping.toString() +"].", mapping instanceof XMLCompositeDirectCollectionMapping);
        assertTrue("Expected container class [java.util.LinkedList] but was ["+((XMLCompositeDirectCollectionMapping) mapping).getContainerPolicy().getContainerClassName()+"]", ((XMLCompositeDirectCollectionMapping) mapping).getContainerPolicy().getContainerClassName().equals("java.util.LinkedList"));
    }

	@Override
	protected Object getControlObject() {
		Employee emp = new Employee();
		emp.id = 66;
		emp.firstName = "Joe";
		emp.lastName = "Oracle";
		List thingsList = new ArrayList();
		thingsList.add("thinga");
		thingsList.add("thingb");
		emp.things = thingsList;
		
		return emp;
	}
}
