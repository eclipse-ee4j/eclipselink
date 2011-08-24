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
 * dmccann - March 31/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.binarydata;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.w3c.dom.Document;

/**
 * Tests XmlBinaryDataMappings via eclipselink-oxm.xml
 * 
 */
public class BinaryDataMappingTestCases extends JAXBTestCases  {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/binarydata/mydata.xml";
    private static final String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/binarydata/write-mydata.xml";
    
    private static final byte[] BYTES0123 = new byte[] { 0, 1, 2, 3 };
    private static final byte[] BYTES1234 = new byte[] { 1, 2, 3, 4 };
    private static final byte[] BYTES2345 = new byte[] { 2, 3, 4, 5 };

    private MyData ctrlObject;
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public BinaryDataMappingTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setClasses(new Class[] { MyData.class });
    }
    

    /**
     * Return the control MyData.
     * 
     * @return
     */
    public Object getControlObject() {
        // setup control object
        MyData ctrlData = new MyData();
        ctrlData.bytes = BYTES0123;
        ctrlData.readOnlyBytes = BYTES1234;        
     // writeOnlyBytes will not be read in
        ctrlData.writeOnlyBytes = null;
        
        return ctrlData;
    }
    
    public Object getWriteControlObject() {
    	if(ctrlObject == null){
        // setup control object
        MyData ctrlData = new MyData();
        ctrlData.bytes = BYTES0123;
        ctrlData.readOnlyBytes = BYTES1234;
        ctrlData.writeOnlyBytes = BYTES2345;
        
        ctrlObject = ctrlData;
    	}
    	return ctrlObject;
    }
    
    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/binarydata/mydata-oxm.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
		metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.binarydata", new StreamSource(inputStream));
		Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
		properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
	        
	    return properties;
	}
    
	public void xmlToObjectTest(Object testObject) throws Exception{
		super.xmlToObjectTest(testObject);
		MyData myObj=(MyData)testObject;
	    assertTrue("Accessor method was not called as expected", myObj.wasSetCalled);

	}
	
	public void objectToXMLDocumentTest(Document testDocument) throws Exception{
		  super.objectToXMLDocumentTest(testDocument);
		 assertTrue("Accessor method was not called as expected", ctrlObject.wasGetCalled);
    }
	    
	public void testSchemaGen() throws Exception{
	   	List controlSchemas = new ArrayList();
	   	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/binarydata/mydata.xsd");
	   	
	   	controlSchemas.add(is);
	   		   	
	   	super.testSchemaGen(controlSchemas);	  
	}
	public void testRoundTrip(){
		//not applicable with write only mappings
	}
}