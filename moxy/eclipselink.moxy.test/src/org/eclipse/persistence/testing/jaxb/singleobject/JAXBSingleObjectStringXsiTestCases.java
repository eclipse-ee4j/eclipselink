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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.internal.oxm.record.SAXUnmarshallerHandler;
import org.eclipse.persistence.internal.oxm.record.XMLStreamReaderReader;
import org.eclipse.persistence.internal.oxm.record.namespaces.UnmarshalNamespaceContext;
import org.eclipse.persistence.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.listofobjects.JAXBListOfObjectsTestCases;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

public class JAXBSingleObjectStringXsiTestCases extends JAXBTestCases {

	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/singleobject/singleObjectStringXsiType.xml";

	public JAXBSingleObjectStringXsiTestCases(String name) throws Exception {
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

    public Object getWriteControlObject() {
        return getControlObject();
    }
	protected Object getControlObject() {		
		String testString = "25";		
		QName qname = new QName("rootNamespace", "root");				
		JAXBElement jaxbElement = new JAXBElement(qname, Object.class, testString);		
		return jaxbElement;
	}
	
	public void testXMLToObjectFromXMLStreamReaderToObject() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);            
            Object testObject = jaxbUnmarshaller.unmarshal(xmlStreamReader, Object.class);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }
	
	public void testXMLToObjectFromXMLStreamReaderFromSourceToObject() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            StreamSource ss = new StreamSource(instream);
            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(ss);            
            Object testObject = jaxbUnmarshaller.unmarshal(xmlStreamReader, Object.class);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }
	
	public void testXMLToObjectFromStreamSourceToObject() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            StreamSource ss = new StreamSource(instream);            
            Object testObject = jaxbUnmarshaller.unmarshal(ss, Object.class);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }
	
	public void testXMLToObjectFromNodeObject() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            org.w3c.dom.Node n = parser.parse(instream);
            Object testObject = jaxbUnmarshaller.unmarshal(n, Object.class);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }
	
	public void testXMLToObjectFromSystemId() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            URL url = ClassLoader.getSystemResource(resourceName);
            String urlExternalForm = url.toExternalForm();
            
            StreamSource ss = new StreamSource(urlExternalForm);            
            Object testObject = jaxbUnmarshaller.unmarshal(ss, Object.class);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }
	
	public void testXMLToObjectFromSAXSourceWithReaderToObject() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);     
            SAXSource ss = new SAXSource(new BridgeReader(xmlStreamReader), new InputSource(instream));
            Object testObject = jaxbUnmarshaller.unmarshal(ss, Object.class);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }
	
	
	  public class BridgeReader implements XMLReader {
		
		    private ContentHandler contentHandler;
		    private XMLStreamReader rdr;
		    private XMLStreamReaderReader srr;

		    public BridgeReader(XMLStreamReader rdr) {
		      this.rdr = rdr;
		    }

		    public void setFeature(String name, boolean value)
		        throws SAXNotRecognizedException, SAXNotSupportedException {
		    }

		    public boolean getFeature(String name) throws SAXNotRecognizedException,
		        SAXNotSupportedException {
		      return false;
		    }

		    public Object getProperty(String name) throws SAXNotRecognizedException,
		        SAXNotSupportedException {
		      return null;
		    }

		    public void setProperty(String name, Object value)
		        throws SAXNotRecognizedException, SAXNotSupportedException {
		    }

		    public void setEntityResolver(EntityResolver resolver) {
		    }

		    public EntityResolver getEntityResolver() {
		      return null;
		    }

		    public void setDTDHandler(DTDHandler handler) {
		    }

		    public DTDHandler getDTDHandler() {
		      return null;
		    }

		    public void setContentHandler(ContentHandler handler) {
		      try {
		        this.contentHandler = handler;
		        if (srr == null){
		        	srr = new XMLStreamReaderReader();
		        }        
		        srr.setContentHandler(handler);	
		      } catch (FactoryConfigurationError e) {
		        e.printStackTrace();
		      }
		    }

		    public ContentHandler getContentHandler() {
		      return contentHandler;
		    }

		    public void setErrorHandler(ErrorHandler handler) {
		    }

		    public ErrorHandler getErrorHandler() {
		      return null;
		    }

		    public void parse(InputSource input) throws IOException, SAXException {
		      doParse();
		    }

		    public void parse(String systemId) throws IOException, SAXException {
		      doParse();
		    }

		    private void doParse() throws SAXException {
		      try {
		    	  org.eclipse.persistence.internal.oxm.record.XMLStreamReaderInputSource sris = new org.eclipse.persistence.internal.oxm.record.XMLStreamReaderInputSource(rdr);
		    	  ((SAXUnmarshallerHandler)getContentHandler()).setUnmarshalNamespaceResolver(new UnmarshalNamespaceContext(rdr));
		          srr.parse(sris);		    
		      } catch (Exception e) {
		        throw new SAXException("parse error", e);
		      }
		    }
		  }
	
	
}
