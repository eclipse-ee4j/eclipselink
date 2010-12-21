/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.UnmarshalException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.listofobjects.JAXBListOfObjectsTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class JAXBSingleObjectObjectNoXsiTestCases extends JAXBTestCases {

	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/singleobject/singleObject.xml";

	public JAXBSingleObjectObjectNoXsiTestCases(String name) throws Exception {
		super(name);
		init();
	}

	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);
		Class[] classes = new Class[1];
		classes[0] = Object.class;
		setClasses(classes);
	}

	public void testSchemaGen() throws Exception {
		MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
		getJAXBContext().generateSchema(outputResolver);
		
		assertEquals("A Schema was generated but should not have been", 0, outputResolver.getSchemaFiles().size()); 
	}
	
	public java.util.List<InputStream> getControlSchemaFiles() {
		//not applicable for this test since we override testSchemaGen
		return null;  		
	}

	protected Object getControlObject() {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        Document doc = null;
		try {
			doc = builderFactory.newDocumentBuilder().newDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}
        Element elem = doc.createElementNS("rootNamespace", "ns0:root");
        elem.setAttributeNS(XMLConstants.XMLNS_URL, "xmlns:ns0", "rootNamespace");
        elem.setTextContent("25");
        doc.appendChild(elem);
        
        Node testNode = doc.getDocumentElement();
		
		QName qname = new QName("rootNamespace", "root");				
		JAXBElement jaxbElement = new JAXBElement(qname, Object.class, testNode);		
		return jaxbElement;
	}

	protected Type getTypeToUnmarshalTo() throws Exception {
		return Object.class;
	}
    public Object getWriteControlObject() {
        return getControlObject();
    }

	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE;
	}
	
    public void testXMLToObjectFromXMLStreamReader() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);
            
            Object testObject = jaxbUnmarshaller.unmarshal(xmlStreamReader, Object.class);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }

    public void testXMLToObjectFromXMLEventReader() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            XMLEventReader xmlEventReader = XML_INPUT_FACTORY.createXMLEventReader(instream);
            Object testObject = jaxbUnmarshaller.unmarshal(xmlEventReader, Object.class);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }
    public void testXMLToObjectFromURL() throws Exception {
    }
    
    
    public void testUnmarshallerHandler() throws Exception {
    }
    public void testRoundTrip() throws Exception{
    	if(isUnmarshalTest()) {
    		InputStream instream = null;
    		if(writeControlDocumentLocation !=null){
    			instream = ClassLoader.getSystemResourceAsStream(writeControlDocumentLocation);
    		}else{
    			instream = ClassLoader.getSystemResourceAsStream(resourceName);
    		}
    	    Object testObject = jaxbUnmarshaller.unmarshal(new StreamSource(instream), Object.class);
    	    instream.close();
    	    xmlToObjectTest(testObject);
            
            objectToXMLStringWriter(testObject);
        }    	
    }
    
    public void testXMLToObjectFromInputStream() throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        Object testObject = jaxbUnmarshaller.unmarshal(new StreamSource(instream), Object.class);
        instream.close();
        xmlToObjectTest(testObject);
    }
    public void compareJAXBElementObjects(JAXBElement controlObj, JAXBElement testObj) {
        assertEquals(controlObj.getName().getLocalPart(), testObj.getName().getLocalPart());
        assertEquals(controlObj.getName().getNamespaceURI(), testObj.getName().getNamespaceURI());
        assertEquals(controlObj.getDeclaredType(), testObj.getDeclaredType());

        Node controlValue = (Node)controlObj.getValue();
        Node testValue = (Node)testObj.getValue();
        
        assertEquals(controlValue.getNamespaceURI(), testValue.getNamespaceURI());
        assertEquals(controlValue.getLocalName(), testValue.getLocalName());
        
        Text controlText = (Text)controlValue.getFirstChild();
        Text testText = (Text)testValue.getFirstChild();
        assertEquals(controlText.getTextContent(), testText.getTextContent());
        
        assertEquals(controlValue.getChildNodes().getLength(), testValue.getChildNodes().getLength());
    }
}
