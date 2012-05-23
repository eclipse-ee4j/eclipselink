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
 * dmccann - March 24/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.objectreference;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;


import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

import org.w3c.dom.Document;

/**
 * Tests XmlObjectReferenceMapping via eclipselink-oxm.xml
 *
 */
public class ObjectReferenceMappingTestCases extends JAXBWithJSONTestCases {
    
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/objectreference/root.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/objectreference/root.json";    
    private static final String ADD_ID1 = "a100";
    private static final String ADD_ID2 = "a101";
    private static final String ADD_ID3 = "a102";
    private Root ctrlObject;
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public ObjectReferenceMappingTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Root.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    /**
     * Create the control Root.
     */
    public Object getControlObject() {
    	if(ctrlObject ==null){
            Root root = new Root();
            List<Employee> emps = new ArrayList<Employee>();
            List<Address> adds = new ArrayList<Address>();

            Address wAddress1 = new Address();
            wAddress1.id = ADD_ID1;
            Address wAddress2 = new Address();
            wAddress2.id = ADD_ID2;
            Address wAddress3 = new Address();
            wAddress3.id = ADD_ID3;
            adds.add(wAddress1);
            adds.add(wAddress2);
            adds.add(wAddress3);
        
            Employee ctrlEmp = new Employee();
            ctrlEmp.workAddress = wAddress2;
            emps.add(ctrlEmp);
        
            root.addresses = adds;
            root.employees = emps;
            ctrlObject = root;
    	}
        
        return ctrlObject;
    }
    
    public void testSchemaGen() throws Exception{
    	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/objectreference/root.xsd");
    	controlSchemas.add(is);
    	super.testSchemaGen(controlSchemas);
    }

    public void xmlToObjectTest(Object testObject) throws Exception {
    	super.xmlToObjectTest(testObject);
   	    assertTrue("Accessor method was not called as expected", ((Root)testObject).employees.get(0).wasSetCalled);
    }

    public void testRoundTrip() throws Exception{
    	//doesn't apply since read and write only mappings are present    	
    }
    
    public void objectToXMLDocumentTest(Document testDocument) throws Exception {
        super.objectToXMLDocumentTest(testDocument);
        assertTrue("Accessor method was not called as expected", ctrlObject.employees.get(0).wasGetCalled);
    } 
    
    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/objectreference/root-oxm.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.objectreference", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
        
        return properties;
	}
    
}