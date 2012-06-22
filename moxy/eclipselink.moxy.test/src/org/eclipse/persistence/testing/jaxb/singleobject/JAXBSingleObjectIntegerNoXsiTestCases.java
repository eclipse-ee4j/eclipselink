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
 *     Denise Smith -  July 9, 2009 Initial test
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.singleobject;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.UnmarshalException;
import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.record.XMLStreamReaderInputSource;
import org.eclipse.persistence.jaxb.JAXBUnmarshallerHandler;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases.ExtendedXMLStreamReaderReader;
import org.eclipse.persistence.testing.jaxb.listofobjects.JAXBListOfObjectsTestCases;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class JAXBSingleObjectIntegerNoXsiTestCases extends JAXBTestCases {

	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/singleobject/singleObject.xml";

	public JAXBSingleObjectIntegerNoXsiTestCases(String name) throws Exception {
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
		
	public List<InputStream> getControlSchemaFiles() {
		//not applicable for this test since we override testSchemaGen
		return null;  		
	}

	protected Object getControlObject() {		
		Integer testInteger = 25;		
		QName qname = new QName("rootNamespace", "root");				
		JAXBElement jaxbElement = new JAXBElement(qname, Integer.class, testInteger);		
		return jaxbElement;
	}

	protected Type getTypeToUnmarshalTo() throws Exception {
		return Integer.class;
	}

	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE;
	}
	
    public void testXMLToObjectFromXMLStreamReader() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);
            Object testObject = jaxbUnmarshaller.unmarshal(xmlStreamReader, Integer.class);
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

            Object testObject = jaxbUnmarshaller.unmarshal(saxSource, Integer.class);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }

    public void testXMLToObjectFromXMLEventReader() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            XMLEventReader xmlEventReader = XML_INPUT_FACTORY.createXMLEventReader(instream);
            Object testObject = jaxbUnmarshaller.unmarshal(xmlEventReader, Integer.class);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }    
	
    public void testXMLToObjectFromURL() throws Exception {
    }
	
	
    public void testUnmarshallerHandler() throws Exception {
    }

    public Object getWriteControlObject() {
        return getControlObject();
    }
	
    public void testXMLToObjectFromInputStream() throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        Object testObject = jaxbUnmarshaller.unmarshal(new StreamSource(instream), Integer.class);
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
    	    Object testObject = jaxbUnmarshaller.unmarshal(new StreamSource(instream), Integer.class);
    	    instream.close();
    	    xmlToObjectTest(testObject);
            
            objectToXMLStringWriter(testObject);
        }    	
    }

}
