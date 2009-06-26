/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith  June 05, 2009 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.jaxb.JAXBTypeElement;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBXMLComparer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public abstract class JAXBListOfObjectsTestCases extends JAXBTestCases {
	private Class[] classes;

	public JAXBListOfObjectsTestCases(String name) throws Exception {
		super(name);
	}

	public void setClasses(Class[] newClasses) throws Exception {
		classLoader = new JaxbClassLoader(Thread.currentThread()
				.getContextClassLoader());
		JAXBContextFactory factory = new JAXBContextFactory();
		jaxbContext = factory.createContext(newClasses, null, classLoader);
		jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	}

	public void setTypes(Type[] newTypes) throws Exception {
		classLoader = new JaxbClassLoader(Thread.currentThread()
				.getContextClassLoader());
		JAXBContextFactory factory = new JAXBContextFactory();
		jaxbContext = factory.createContext(newTypes, null, classLoader);
		jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	}

	protected Object getControlObject() {
		return null;
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
  
    public void testXMLToObjectFromXMLEventReader() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(getNoXsiTypeControlResourceName());
            javax.xml.stream.XMLEventReader reader = XML_INPUT_FACTORY.createXMLEventReader(instream);
            Object obj = ((JAXBUnmarshaller) getJAXBUnmarshaller()).unmarshal(reader, getTypeToUnmarshalTo());
            this.xmlToObjectTest(obj);
        }
    }

	public void testObjectToXMLStreamWriter() throws Exception {
		if (System.getProperty("java.version").contains("1.6")) {
			StringWriter writer = new StringWriter();
			Object objectToWrite = getWriteControlObject();
			javax.xml.stream.XMLOutputFactory factory = javax.xml.stream.XMLOutputFactory
					.newInstance();
			javax.xml.stream.XMLStreamWriter streamWriter = factory
					.createXMLStreamWriter(writer);

			getJAXBMarshaller().marshal(objectToWrite, streamWriter);

			StringReader reader = new StringReader(writer.toString());
			InputSource inputSource = new InputSource(reader);
			Document testDocument = parser.parse(inputSource);
			writer.close();
			reader.close();

			objectToXMLDocumentTest(testDocument);
		}
	}

	//Override and don't compare namespaceresolver size
	public void testObjectToXMLStringWriter() throws Exception {
        StringWriter writer = new StringWriter();
        Object objectToWrite = getWriteControlObject();        
  
        jaxbMarshaller.marshal(objectToWrite, writer);
               
        StringReader reader = new StringReader(writer.toString());
        InputSource inputSource = new InputSource(reader);
        Document testDocument = parser.parse(inputSource);
        writer.close();
        reader.close();

        objectToXMLDocumentTest(testDocument);
    }

	//Override and don't compare namespaceresolver size
	 public void testObjectToContentHandler() throws Exception {
	        SAXDocumentBuilder builder = new SAXDocumentBuilder();
	        Object objectToWrite = getWriteControlObject();
	      
	        jaxbMarshaller.marshal(objectToWrite, builder);
	        
            Document controlDocument = getWriteControlDocument();
	        Document testDocument = builder.getDocument();

	        log("**testObjectToContentHandler**");
	        log("Expected:");
	        log(controlDocument);
	        log("\nActual:");
	        log(testDocument);

	        assertXMLIdentical(controlDocument, testDocument);
	    }

		//Override and don't compare namespaceresolver size
	    public void testObjectToXMLDocument() throws Exception {
	        Object objectToWrite = getWriteControlObject();
	     
	        Document testDocument = XMLPlatformFactory.getInstance().getXMLPlatform().createDocument(); 
	        jaxbMarshaller.marshal(objectToWrite, testDocument);	     
	        objectToXMLDocumentTest(testDocument);
	    }

	 
	public void testXMLToObjectFromStreamSource() throws Exception {
		InputStream instream = ClassLoader
				.getSystemResourceAsStream(resourceName);
		StreamSource source = new StreamSource();
		source.setInputStream(instream);
		Object obj = getJAXBUnmarshaller().unmarshal(source);
		this.xmlToObjectTest(obj);
	}

	public abstract Map<String, InputStream> getControlSchemaFiles();
	
	public void testSchemaGen() throws Exception {
		MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
		getJAXBContext().generateSchema(outputResolver);
		
		Map<String, File> generatedSchemas = outputResolver.getSchemaFiles();		
		Map<String,InputStream> controlSchemas = getControlSchemaFiles();
		
		assertEquals(controlSchemas.size(), generatedSchemas.size());
		
		Iterator<String> keyIter = controlSchemas.keySet().iterator();
		while(keyIter.hasNext()){
			String nextKey = keyIter.next();
			InputStream nextControlValue = controlSchemas.get(nextKey);
			File nextGeneratedValue =generatedSchemas.get(nextKey);
			assertNotNull("Generated Schema for namespace not found:" + nextKey, nextGeneratedValue);
			Document control = parser.parse(nextControlValue);
			Document test = parser.parse(nextGeneratedValue);
			
			JAXBXMLComparer xmlComparer = new JAXBXMLComparer();	        
			boolean isEqual = xmlComparer.isSchemaEqual(control, test);
			if(!isEqual){
				
				logDocument(control);
				logDocument(test);
			}
			assertTrue("generated schema did not match control schema", isEqual);
		}
	}
	
	 protected void logDocument(Document document){
  	   try {
             TransformerFactory transformerFactory = TransformerFactory.newInstance();
             Transformer transformer = transformerFactory.newTransformer();
             DOMSource source = new DOMSource(document);
             StreamResult result = new StreamResult(System.out);
             transformer.transform(source, result);
         } catch (Exception e) {
             e.printStackTrace();
         }
  }

	
	public Object getWriteControlObject(){
		JAXBElement jaxbElement = (JAXBElement)getControlObject();

		try{
			Type typeToUse = getTypeToUnmarshalTo();
			if(typeToUse instanceof ParameterizedType){
				JAXBTypeElement typed = new JAXBTypeElement(jaxbElement.getName(), jaxbElement.getValue(), (ParameterizedType)typeToUse);
				return typed;
			}else if(typeToUse instanceof Class){
				JAXBTypeElement typed = new JAXBTypeElement(jaxbElement.getName(), jaxbElement.getValue(), (Class)typeToUse);
				return typed;
			}
		}catch(Exception e){
			fail(e.getMessage());
		}
		return null;
	}

	protected abstract Type getTypeToUnmarshalTo() throws Exception;

	protected abstract String getNoXsiTypeControlResourceName();
	
	class MySchemaOutputResolver extends SchemaOutputResolver {
		// keep a list of processed schemas for the validation phase of the
		// test(s)
		public Map<String, File> schemaFiles;

		public MySchemaOutputResolver() {
			schemaFiles = new java.util.HashMap<String, File>();
		}

		public Result createOutput(String namespaceURI, String suggestedFileName)
				throws IOException {
			File schemaFile = new File(suggestedFileName);
			if(namespaceURI == null){
				namespaceURI ="";
			}
			schemaFiles.put(namespaceURI, schemaFile);
			return new StreamResult(schemaFile);
		}

		public Map<String,File> getSchemaFiles() {
			return schemaFiles;
		}
	}
	

}
