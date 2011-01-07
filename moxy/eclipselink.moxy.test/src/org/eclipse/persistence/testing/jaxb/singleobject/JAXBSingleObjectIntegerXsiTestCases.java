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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBUnmarshallerHandler;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.listofobjects.JAXBListOfObjectsTestCases;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class JAXBSingleObjectIntegerXsiTestCases extends JAXBTestCases {

	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/singleobject/singleObjectXsiType.xml";

	public JAXBSingleObjectIntegerXsiTestCases(String name) throws Exception {
		super(name);
		init();
	}

	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);
		setWriteControlDocument(XML_RESOURCE);
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
		Integer testInteger = 25;		
		QName qname = new QName("rootNamespace", "root");				
		JAXBElement jaxbElement = new JAXBElement(qname, Object.class, testInteger);		
		return jaxbElement;
	}

    public void testXMLToObjectFromXMLStreamReader() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);
            Object testObject = jaxbUnmarshaller.unmarshal(xmlStreamReader);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }
    
    public void testXMLToObjectFromURL() throws Exception {
        java.net.URL url = ClassLoader.getSystemResource(resourceName);
        Object testObject = jaxbUnmarshaller.unmarshal(url);
        xmlToObjectTest(testObject);
    }
    
    
    public void testUnmarshallerHandler() throws Exception {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        SAXParser saxParser = saxParserFactory.newSAXParser();
        XMLReader xmlReader = saxParser.getXMLReader();

        JAXBUnmarshallerHandler jaxbUnmarshallerHandler = (JAXBUnmarshallerHandler)jaxbUnmarshaller.getUnmarshallerHandler();
        xmlReader.setContentHandler(jaxbUnmarshallerHandler);

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourceName);
        InputSource inputSource = new InputSource(inputStream);
        xmlReader.parse(inputSource);

        xmlToObjectTest(jaxbUnmarshallerHandler.getResult());
    }

    
    public void testXMLToObjectFromInputStream() throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        Object testObject = jaxbUnmarshaller.unmarshal(instream);
        instream.close();
        xmlToObjectTest(testObject);
    }
	

}
