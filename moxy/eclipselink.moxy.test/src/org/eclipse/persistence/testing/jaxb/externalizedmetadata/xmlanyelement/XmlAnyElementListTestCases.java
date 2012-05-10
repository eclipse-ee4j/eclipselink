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
 * Denise Smith - 2.4 - April 2012
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlAnyElementListTestCases extends JAXBWithJSONTestCases{
	
	private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/employee-with-list.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/employee-with-list.json";
		
	
    public XmlAnyElementListTestCases(String name) throws Exception{
	   super(name);
	   setControlDocument(XML_RESOURCE);
	   setControlJSON(JSON_RESOURCE);
	   setClasses(new Class[]{EmployeeWithList.class});
	    	
    }  
		
	public Map getProperties(){
	    InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/eclipselink-oxm-xml-list.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
		metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement", new StreamSource(inputStream));
		Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
		properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
		        
		return properties;
    }
		
	public Object getControlObject(){
	    EmployeeWithList ctrlEmpWithList = new EmployeeWithList();
	    ctrlEmpWithList.a = 1;
	    ctrlEmpWithList.b = "3";
	    ctrlEmpWithList.stuff = new ArrayList<Object>();
	    Element elt1 = null;
	    Element elt2 = null;
	    try {
	            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	            elt1 = doc.createElement(MyDomAdapter.STUFF_STR);
	            elt1.appendChild(doc.createTextNode("This is some stuff"));
	            elt2 = doc.createElement(MyDomAdapter.STUFF_STR);
	            elt2.appendChild(doc.createTextNode("This is some more stuff"));
	    } catch (ParserConfigurationException e) {
	             e.printStackTrace();
	    }
	    ctrlEmpWithList.stuff.add(elt1);
	    ctrlEmpWithList.stuff.add(elt2);
	    return ctrlEmpWithList;
     }
		
    public void testContainerType() {
	    XMLDescriptor xDesc = xmlContext.getDescriptor(new QName("employee"));
		assertNotNull("No descriptor was generated for EmployeeWithList.", xDesc);
		DatabaseMapping mapping = xDesc.getMappingForAttributeName("stuff");
		assertNotNull("No mapping exists on EmployeeWithList for attribute [stuff].", mapping);
		assertTrue("Expected an XMLAnyCollectionMapping for attribute [stuff], but was [" + mapping.toString() +"].", mapping instanceof XMLAnyCollectionMapping);
		assertTrue("Expected container class [java.util.LinkedList] but was ["+((XMLAnyCollectionMapping) mapping).getContainerPolicy().getContainerClassName()+"]", ((XMLAnyCollectionMapping) mapping).getContainerPolicy().getContainerClassName().equals("java.util.LinkedList"));
    }

	public void testSchemaGen() throws Exception{
	    java.util.List<InputStream> controlSchemas = new ArrayList<InputStream>();
		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/schema.xsd");
		controlSchemas.add(is);
		super.testSchemaGen(controlSchemas);
   }
	
	 public void testInstanceDocValidation() throws Exception {
	    	InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/schema.xsd");        
	        StreamSource schemaSource = new StreamSource(schema); 
	                
	        MyMapStreamSchemaOutputResolver outputResolver = new MyMapStreamSchemaOutputResolver();
	        getJAXBContext().generateSchema(outputResolver);
	        
	        InputStream instanceDocStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/employee.xml");
	        String result = validateAgainstSchema(instanceDocStream, schemaSource, outputResolver );        
	        assertTrue("Instance doc validation (employee.xml) failed unxepectedly: " + result, result == null);
	    }
		    

}
