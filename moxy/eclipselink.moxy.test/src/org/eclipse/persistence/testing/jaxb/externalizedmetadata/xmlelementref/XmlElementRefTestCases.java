/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;

/**
 * Tests XmlElementRef via eclipselink-oxm.xml
 *
 */
public class XmlElementRefTestCases extends JAXBWithJSONTestCases {
    
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementref/foo.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementref/foo.json";
    private Object writeObject;
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlElementRefTestCases(String name)throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[] { Foo.class });
    }
    
	public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementref/eclipselink-oxm.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementref", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
        
        return properties;
	}
    
    public void testSchemaGen() throws Exception{
    	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementref/schema.xsd");
    	
    	controlSchemas.add(is);
    	
    	super.testSchemaGen(controlSchemas);
    	
    }

	public Object getControlObject() {
		Foo foo = new Foo();
		return foo;
	}
	
	public Object getWriteControlObject() {
		if(writeObject == null){
			Foo foo = new Foo();
			Bar bar = new Bar();
			bar.id = 66;
			foo.item = bar;
			writeObject = foo;
		}
		return writeObject;
	}
	

    public void objectToXMLDocumentTest(Document testDocument) throws Exception {    	
    	super.objectToXMLDocumentTest(testDocument);
        assertTrue("Method accessor was not called as expected.", ((Foo) writeObject).accessedViaMethod);
    }
	
    public void testRoundTrip(){
    	//not applicable with write only mappings.
    }

}
