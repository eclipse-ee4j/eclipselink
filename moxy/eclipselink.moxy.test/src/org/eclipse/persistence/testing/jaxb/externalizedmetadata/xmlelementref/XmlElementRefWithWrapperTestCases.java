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
 * dmccann - December 03/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementref;

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
import org.eclipse.persistence.oxm.mappings.XMLChoiceCollectionMapping;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlElementRefWithWrapperTestCases extends JAXBWithJSONTestCases{

	private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementref/foo-wrapper.xml";
	private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementref/foo-wrapper.json";
	
    public XmlElementRefWithWrapperTestCases(String name) throws Exception {
		super(name);
		setClasses(new Class[] { Foos.class, Bar.class });
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);
}

	public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementref/eclipselink-oxm-wrapper.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementref", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
        
        return properties;
	}
    
	public void testContainerType(){
	   // make sure container-type was processed correctly
        XMLDescriptor xDesc = xmlContext.getDescriptor(new QName("foos"));
        assertNotNull("No descriptor was generated for Foos.", xDesc);
        DatabaseMapping mapping = xDesc.getMappingForAttributeName("items");
        assertNotNull("No mapping exists on Foos for attribute [items].", mapping);
        assertTrue("Expected an XMLChoiceCollectionMapping for attribute [items], but was [" + mapping.toString() +"].", mapping instanceof XMLChoiceCollectionMapping);
        assertTrue("Expected container class [java.util.LinkedList] but was ["+((XMLChoiceCollectionMapping) mapping).getContainerPolicy().getContainerClassName()+"]", ((XMLChoiceCollectionMapping) mapping).getContainerPolicy().getContainerClassName().equals("java.util.LinkedList"));
	}
	
    public void testSchemaGen() throws Exception{
    	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementref/schema_wrapper.xsd");
    	
    	controlSchemas.add(is);
    	
    	super.testSchemaGen(controlSchemas);
    	
    	
    }

	@Override
	protected Object getControlObject() {
		Foos foos = new Foos();
		List itemsList = new ArrayList();
		Bar bar = new Bar();
		bar.id = 66;
		
		Bar bar2 = new Bar();
		bar2.id = 99;
     
		itemsList.add(bar);
		itemsList.add(bar2);
		foos.items = itemsList;
		return foos;
	}
}
