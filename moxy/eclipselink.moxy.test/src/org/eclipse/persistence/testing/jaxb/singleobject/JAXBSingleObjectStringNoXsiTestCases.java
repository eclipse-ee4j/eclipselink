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
 *     Denise Smith -  July 9, 2009 Initial test
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.singleobject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.internal.oxm.record.XMLStreamReaderInputSource;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class JAXBSingleObjectStringNoXsiTestCases extends JAXBWithJSONTestCases {

	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/singleobject/singleObject.xml";
	protected final static String XML_RESOURCE_WRITE = "org/eclipse/persistence/testing/jaxb/singleobject/singleObjectWrite.xml";
	protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/singleobject/singleObjectString.json";

	public JAXBSingleObjectStringNoXsiTestCases(String name) throws Exception {
		super(name);
		init();
	}

	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);
		setControlDocument(XML_RESOURCE_WRITE);
		setControlJSON(JSON_RESOURCE);
		Class[] classes = new Class[1];
		classes[0] = Object.class;
		setClasses(classes);
	}

	public void testSchemaGen() throws Exception {
		MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
		getJAXBContext().generateSchema(outputResolver);
		
		assertEquals("A Schema was generated but should not have been", 0, outputResolver.getSchemaFiles().size()); 
	}
	
	public List<InputStream> getControlSchemaFiles() {
		//not applicable for this test since we override testSchemaGen
		return null;  		
	}
	
    public Object getWriteControlObject() {
        return getControlObject();
    }

	protected Object getControlObject() {		
		String testString = "25";		
		QName qname = new QName("rootNamespace", "root");				
		JAXBElement jaxbElement = new JAXBElement(qname, String.class, testString);		
		return jaxbElement;
	}

	public Map getProperties(){
    	Map props = new HashMap();
	    	
    	Map namespaces = new HashMap();
    	namespaces.put("rootNamespace", "ns1");
    	props.put(JAXBContextProperties.NAMESPACE_PREFIX_MAPPER, namespaces);
    	
    	props.put(JAXBContextProperties.JSON_INCLUDE_ROOT, true);
	    return props;
}
	
	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE;
	}

    public void testXMLToObjectFromXMLStreamReader() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);
            Object testObject = jaxbUnmarshaller.unmarshal(xmlStreamReader, String.class);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }

    @Override
    public void testXMLToObjectFromXMLStreamReaderEx() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);

            ExtendedXMLStreamReaderReader xmlStreamReaderReaderEx = new ExtendedXMLStreamReaderReader();
            XMLStreamReaderInputSource xmlStreamReaderInputSource = new XMLStreamReaderInputSource(xmlStreamReader);
            SAXSource saxSource = new SAXSource(xmlStreamReaderReaderEx, xmlStreamReaderInputSource);

            Object testObject = jaxbUnmarshaller.unmarshal(saxSource, String.class);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }
    

    public void testXMLToObjectFromXMLEventReader() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            XMLEventReader xmlEventReader = XML_INPUT_FACTORY.createXMLEventReader(instream);
            Object testObject = jaxbUnmarshaller.unmarshal(xmlEventReader, String.class);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }

    public void testXMLToObjectFromNode() throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        InputSource inputSource = new InputSource(instream);
        Document testDocument = parser.parse(inputSource);
        Object testObject = getJAXBUnmarshaller().unmarshal(testDocument, String.class);
        instream.close();
        xmlToObjectTest(testObject);
    }
    
    
    public void testXMLToObjectFromURL() throws Exception {
    }
    
    
    public void testUnmarshallerHandler() throws Exception {
    }

    
    public void testXMLToObjectFromInputStream() throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        Object testObject = jaxbUnmarshaller.unmarshal(new StreamSource(instream), String.class);
        instream.close();
        xmlToObjectTest(testObject);
    }
    
    public void testRoundTrip() throws Exception{
    	if(isUnmarshalTest()) {
    		InputStream instream = null;
    		if(writeControlDocumentLocation !=null){
    			instream = ClassLoader.getSystemResourceAsStream(writeControlDocumentLocation);
    		}else{
    			instream = ClassLoader.getSystemResourceAsStream(resourceName);
    		}
    	    Object testObject = jaxbUnmarshaller.unmarshal(new StreamSource(instream), String.class);
    	    instream.close();
    	    xmlToObjectTest(testObject);
            
            objectToXMLStringWriter(testObject);
        }    	
    }
    
    public void testJSONUnmarshalFromInputStream() throws Exception {
    	jaxbUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
        Object testObject = jaxbUnmarshaller.unmarshal(new StreamSource(inputStream), String.class);
        inputStream.close();
        jsonToObjectTest(testObject);
    }

    public void testJSONUnmarshalFromInputSource() throws Exception {
        
    }

    public void testJSONUnmarshalFromReader() throws Exception {
    }

    public void testJSONUnmarshalFromURL() throws Exception {     
    }

}
