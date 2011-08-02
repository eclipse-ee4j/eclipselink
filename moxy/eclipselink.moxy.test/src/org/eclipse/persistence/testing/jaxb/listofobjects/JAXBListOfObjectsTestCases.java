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
 *     Denise Smith  June 05, 2009 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.jaxb.JAXBTypeElement;
import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public abstract class JAXBListOfObjectsTestCases extends JAXBTestCases {

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
				
	 	Map props = getProperties();
                if(props != null){
    		Map overrides = (Map) props.get(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY);
    		if(overrides != null){
    			Iterator valuesIter = overrides.values().iterator();
    			while(valuesIter.hasNext()){
    				Source theSource = (Source) valuesIter.next();
    				validateBindingsFileAgainstSchema(theSource);
    			}
    		}
    	}
    	
		
		jaxbContext = factory.createContext(newTypes, props, classLoader);
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
    
    public void testXMLToObjectFromXMLStreamReaderWithType() throws Exception { 
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);
            Object testObject = ((JAXBUnmarshaller)jaxbUnmarshaller).unmarshal(xmlStreamReader, getTypeToUnmarshalTo());
            instream.close();

            JAXBElement controlObj = (JAXBElement)getControlObject();            
            JAXBElement newControlObj = new JAXBElement(controlObj.getName(), getClassForDeclaredTypeOnUnmarshal(), controlObj.getScope(), controlObj.getValue());
            xmlToObjectTest(testObject, newControlObj);                      
        } 
    } 
    /*
    public void testXMLToObjectFromXMLStreamReaderWithTypeMappingInfo() throws Exception { 
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);
            Object testObject = ((JAXBUnmarshaller)jaxbUnmarshaller).unmarshal(xmlStreamReader, getTypeMappingInfoToUnmarshalTo());
            instream.close();

            JAXBElement controlObj = (JAXBElement)getControlObject();            
            JAXBElement newControlObj = new JAXBElement(controlObj.getName(), getClassForDeclaredTypeOnUnmarshal(), controlObj.getScope(), controlObj.getValue());
            xmlToObjectTest(testObject, newControlObj);                      
        } 
    } */
    
    public void testXMLToObjectFromXMLStreamReaderWithClass() throws Exception { 
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);
            Object testObject = ((JAXBUnmarshaller)jaxbUnmarshaller).unmarshal(xmlStreamReader, getClassForDeclaredTypeOnUnmarshal());
            instream.close();

            JAXBElement controlObj = (JAXBElement)getControlObject();            
            JAXBElement newControlObj = new JAXBElement(controlObj.getName(), getClassForDeclaredTypeOnUnmarshal(), controlObj.getScope(), controlObj.getValue());
            xmlToObjectTest(testObject, newControlObj);                      
        } 
    } 
    
  
    public void testXMLToObjectFromXMLEventReader() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            javax.xml.stream.XMLEventReader reader = XML_INPUT_FACTORY.createXMLEventReader(instream);
            Object obj = jaxbUnmarshaller.unmarshal(reader);
            this.xmlToObjectTest(obj);
        }
    }
    
    public void testXMLToObjectFromXMLEventReaderWithType() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            javax.xml.stream.XMLEventReader reader = XML_INPUT_FACTORY.createXMLEventReader(instream);
            Object obj = ((JAXBUnmarshaller)jaxbUnmarshaller).unmarshal(reader, getTypeToUnmarshalTo());

            JAXBElement controlObj = (JAXBElement)getControlObject();            
            JAXBElement newControlObj = new JAXBElement(controlObj.getName(), getClassForDeclaredTypeOnUnmarshal(), controlObj.getScope(), controlObj.getValue());
            xmlToObjectTest(obj, newControlObj);    
        }
    }   
    /*
    public void testXMLToObjectFromXMLEventReaderWithTypeMappingInfo() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            javax.xml.stream.XMLEventReader reader = XML_INPUT_FACTORY.createXMLEventReader(instream);
            Object obj = ((JAXBUnmarshaller)jaxbUnmarshaller).unmarshal(reader, getTypeMappingInfoToUnmarshalTo());

            JAXBElement controlObj = (JAXBElement)getControlObject();            
            JAXBElement newControlObj = new JAXBElement(controlObj.getName(), getClassForDeclaredTypeOnUnmarshal(), controlObj.getScope(), controlObj.getValue());
            xmlToObjectTest(obj, newControlObj);    
        }
    }   
    */
    
    public void testXMLToObjectFromXMLEventReaderWithClass() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            javax.xml.stream.XMLEventReader reader = XML_INPUT_FACTORY.createXMLEventReader(instream);
            Object obj = ((JAXBUnmarshaller)jaxbUnmarshaller).unmarshal(reader, getClassForDeclaredTypeOnUnmarshal());

            JAXBElement controlObj = (JAXBElement)getControlObject();            
            JAXBElement newControlObj = new JAXBElement(controlObj.getName(), getClassForDeclaredTypeOnUnmarshal(), controlObj.getScope(), controlObj.getValue());
            xmlToObjectTest(obj, newControlObj);    
        }
    } 
    
    public void testXMLToObjectFromNode() throws Exception {
        if(isUnmarshalTest()) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);            
            Node node  = parser.parse(instream);
            Object testObject = jaxbUnmarshaller.unmarshal(node);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }
    
    
    public void testXMLToObjectFromNodeWithClass() throws Exception {
        if(isUnmarshalTest()) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);            
            Node node  = parser.parse(instream);
            Object obj = ((JAXBUnmarshaller)jaxbUnmarshaller).unmarshal(node, getClassForDeclaredTypeOnUnmarshal());

            JAXBElement controlObj = (JAXBElement)getControlObject();            
            JAXBElement newControlObj = new JAXBElement(controlObj.getName(), getClassForDeclaredTypeOnUnmarshal(), controlObj.getScope(), controlObj.getValue());
            xmlToObjectTest(obj, newControlObj);  
            
            instream.close();
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
	
    public void testObjectToXMLEventWriter() throws Exception {
        if (System.getProperty("java.version").contains("1.6")) {
            StringWriter writer = new StringWriter();
            Object objectToWrite = getWriteControlObject();
            javax.xml.stream.XMLOutputFactory factory = javax.xml.stream.XMLOutputFactory
                    .newInstance();
            javax.xml.stream.XMLEventWriter eventWriter = factory
                    .createXMLEventWriter(writer);

            getJAXBMarshaller().marshal(objectToWrite, eventWriter);

            StringReader reader = new StringReader(writer.toString());
            InputSource inputSource = new InputSource(reader);
            Document testDocument = parser.parse(inputSource);
            writer.close();
            reader.close();

            objectToXMLDocumentTest(testDocument);
        }
    }	
/*
    public void testObjectToXMLStreamWriterWithTypeMappingInfo() throws Exception {
        if(XML_OUTPUT_FACTORY != null) {
            StringWriter writer = new StringWriter();

            javax.xml.stream.XMLOutputFactory factory = javax.xml.stream.XMLOutputFactory.newInstance();
            factory.setProperty(factory.IS_REPAIRING_NAMESPACES, new Boolean(false));
            javax.xml.stream.XMLStreamWriter streamWriter= factory.createXMLStreamWriter(writer);

            Object objectToWrite = getWriteControlObject();
        
            ((JAXBMarshaller)jaxbMarshaller).marshal(objectToWrite, streamWriter, getTypeMappingInfoToUnmarshalTo());

            streamWriter.flush();
          
            StringReader reader = new StringReader(writer.toString());
            InputSource inputSource = new InputSource(reader);
            Document testDocument = parser.parse(inputSource);
            writer.close();
            reader.close();
            objectToXMLDocumentTest(testDocument);
        }
    }
    
    public void testObjectToXMLEventWriterWithTypeMappingInfo() throws Exception {
        if(XML_OUTPUT_FACTORY != null) {
            StringWriter writer = new StringWriter();

            javax.xml.stream.XMLOutputFactory factory = javax.xml.stream.XMLOutputFactory.newInstance();
            factory.setProperty(factory.IS_REPAIRING_NAMESPACES, new Boolean(false));
            javax.xml.stream.XMLEventWriter eventWriter= factory.createXMLEventWriter(writer);

            Object objectToWrite = getWriteControlObject();
        
            ((JAXBMarshaller)jaxbMarshaller).marshal(objectToWrite, eventWriter, getTypeMappingInfoToUnmarshalTo());

            eventWriter.flush();
        
            StringReader reader = new StringReader(writer.toString());
            InputSource inputSource = new InputSource(reader);
            Document testDocument = parser.parse(inputSource);
            writer.close();
            reader.close();
            objectToXMLDocumentTest(testDocument);
        }
    }    
    	*/
    
    
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
	
	 public void testObjectToOutputStream() throws Exception {
	        Object objectToWrite = getWriteControlObject();
	        ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    	    
	        jaxbMarshaller.marshal(objectToWrite, stream);

	        InputStream is = new ByteArrayInputStream(stream.toByteArray());
	        Document testDocument = parser.parse(is);
	        stream.close();
	        is.close();
	               
	        objectToXMLDocumentTest(testDocument);
	    }
	    
	    public void testObjectToOutputStreamASCIIEncoding() throws Exception {
	        Object objectToWrite = getWriteControlObject();
	        ByteArrayOutputStream stream = new ByteArrayOutputStream();
	        
	        String originalEncoding = (String)jaxbMarshaller.getProperty(Marshaller.JAXB_ENCODING);
	        jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "US-ASCII");
	        jaxbMarshaller.marshal(objectToWrite, stream);
	        jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, originalEncoding);
	        
	        InputStream is = new ByteArrayInputStream(stream.toByteArray());
	        Document testDocument = parser.parse(is);
	        stream.close();
	        is.close();
	               
	        objectToXMLDocumentTest(testDocument);
	    }
	
	
	public void testRoundTrip() throws Exception {
		//This test is not applicable because to Marshal we need a specialized jaxbelement
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
	
	public void testXMLToObjectFromStreamSourceWithClass() throws Exception {
		InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
		StreamSource source = new StreamSource();
		source.setInputStream(instream);
		Object obj = getJAXBUnmarshaller().unmarshal(source, getClassForDeclaredTypeOnUnmarshal());
		JAXBElement controlObj = (JAXBElement)getControlObject();            
		JAXBElement newControlObj = new JAXBElement(controlObj.getName(), getClassForDeclaredTypeOnUnmarshal(), controlObj.getScope(), controlObj.getValue());
                xmlToObjectTest(obj, newControlObj);  
	}

	public abstract List<InputStream> getControlSchemaFiles();
		
	public void testSchemaGen() throws Exception {
		testSchemaGen(getControlSchemaFiles());
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
	
	private Class getClassForDeclaredTypeOnUnmarshal() throws Exception{
		Type typeToUse = getTypeToUnmarshalTo();
		if(typeToUse instanceof Class){
			return (Class)typeToUse;
		}
		return Object.class;		
	}

	protected abstract Type getTypeToUnmarshalTo() throws Exception;

	protected TypeMappingInfo getTypeMappingInfoToUnmarshalTo() throws Exception {
		TypeMappingInfo info = new TypeMappingInfo();		
		info.setType(getTypeToUnmarshalTo());
		return info;
	}
	
	protected abstract String getNoXsiTypeControlResourceName();
	
	public void xmlToObjectTest(Object testObject, Object controlObject) throws Exception {
            log("\n**xmlToObjectTest**");
            log("Expected:");
            log(controlObject.toString());
            log("Actual:");
            log(testObject.toString());

            JAXBElement controlObj = (JAXBElement)controlObject;
            JAXBElement testObj = (JAXBElement)testObject;
            compareJAXBElementObjects(controlObj, testObj);
    }
	

}
