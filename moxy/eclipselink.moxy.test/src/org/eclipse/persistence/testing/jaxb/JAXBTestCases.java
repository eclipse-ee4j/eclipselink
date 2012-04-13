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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.persistence.internal.oxm.record.XMLStreamReaderInputSource;
import org.eclipse.persistence.internal.oxm.record.XMLStreamReaderReader;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshallerHandler;
import org.eclipse.persistence.jaxb.compiler.CompilerHelper;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public abstract class JAXBTestCases extends XMLMappingTestCases {

    public static final String ECLIPSELINK_OXM_XSD = "eclipselink_oxm_2_4.xsd";
    protected JAXBContext jaxbContext;
    protected Marshaller jaxbMarshaller;
    protected Unmarshaller jaxbUnmarshaller;
    protected Class[] classes;
    protected Type[] types;
    protected String contextPath;
    
    protected ClassLoader classLoader;
    protected Source bindingsFileXSDSource;

    public JAXBTestCases(String name) throws Exception {
        super(name);
    } 

    public XMLContext getXMLContext(Project project) {
        return new XMLContext(project, classLoader);
    }

    public void setUp() throws Exception {
        setupParser();
        setupControlDocs();

        InputStream bindingsFileXSDInputStream = getClass().getClassLoader().getResourceAsStream(ECLIPSELINK_OXM_XSD);
        if(bindingsFileXSDInputStream == null){
            bindingsFileXSDInputStream = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/jaxb/" + ECLIPSELINK_OXM_XSD);
        }
        if(bindingsFileXSDInputStream == null){
            fail("ERROR LOADING " + ECLIPSELINK_OXM_XSD);
        }
        bindingsFileXSDSource = new StreamSource(bindingsFileXSDInputStream);
    }

    public void tearDown() {
        super.tearDown();
        jaxbContext = null;
        jaxbMarshaller = null;
        jaxbUnmarshaller = null;
        classLoader = null;
        bindingsFileXSDSource = null;
    }

    protected void setProject(Project project) {
        this.project = project;
    }

    public void setClasses(Class[] newClasses) throws Exception {

        classLoader = Thread.currentThread().getContextClassLoader();
        jaxbContext = JAXBContextFactory.createContext(newClasses, getProperties(), classLoader);
        classes = newClasses;
  
        xmlContext = ((org.eclipse.persistence.jaxb.JAXBContext)jaxbContext).getXMLContext();
        setProject(xmlContext.getSession(0).getProject());
        jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbUnmarshaller = jaxbContext.createUnmarshaller();

    }

    public void setContextPath(String contextPath) throws Exception {
    	 classLoader = Thread.currentThread().getContextClassLoader();
    	 this.contextPath = contextPath;
         Map props = getProperties();
         if(props != null){
             Map overrides = (Map) props.get(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY);
             if(overrides != null){
                 Iterator valuesIter = overrides.values().iterator();
                 while(valuesIter.hasNext()){
                 	Object next = valuesIter.next();
                 	validateBindingsFileAgainstSchema(next);
                 }
             }
         }         
         jaxbContext = JAXBContextFactory.createContext(contextPath, classLoader, getProperties());

         xmlContext = ((org.eclipse.persistence.jaxb.JAXBContext)jaxbContext).getXMLContext();
         setProject(xmlContext.getSession(0).getProject());
         jaxbMarshaller = jaxbContext.createMarshaller();
         jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    }

    
    public void setTypes(Type[] newTypes) throws Exception {

    	types = newTypes;
        classLoader = Thread.currentThread().getContextClassLoader();

        Map props = getProperties();
        if(props != null){
            Map overrides = (Map) props.get(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY);
            if(overrides != null){
                Iterator valuesIter = overrides.values().iterator();
                while(valuesIter.hasNext()){
                	Object next = valuesIter.next();
                	validateBindingsFileAgainstSchema(next);
                }
            }
        }

        jaxbContext = JAXBContextFactory.createContext(newTypes, getProperties(), classLoader);
   
        xmlContext = ((org.eclipse.persistence.jaxb.JAXBContext)jaxbContext).getXMLContext();
        setProject(xmlContext.getSession(0).getProject());
        jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    }

    protected Map getProperties() throws JAXBException{    	
        return null;        
    }

    private Map getPropertiesFromJSON() throws JAXBException{    	    	
    	Map props = new HashMap(getProperties());
    	    	
    	if(props !=null){
    		
    	    Object bindingFilesObject = props.get(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY);
    	    if(bindingFilesObject != null){
    			JAXBContext jaxbContext = CompilerHelper.getXmlBindingsModelContext();
    			//unmarshal XML
    	        Unmarshaller u =jaxbContext.createUnmarshaller();
    	        Marshaller jsonMarshaller = jaxbContext.createMarshaller();
    	        //Marshal bindings to JSON (with no root)
    	        jsonMarshaller.setProperty(JAXBMarshaller.MEDIA_TYPE, "application/json");
    	        jsonMarshaller.setProperty(JAXBMarshaller.JSON_INCLUDE_ROOT, false);
    	    	if(bindingFilesObject instanceof Map){
    	    		Map<String, Object> bindingFiles = (Map<String, Object>) bindingFilesObject;
    	    	
	    	    	Iterator<String> keyIter = bindingFiles.keySet().iterator();
	    	    	while(keyIter.hasNext()){
	    	    		String nextKey = keyIter.next();
	    	    		Object nextBindings = bindingFiles.get(nextKey);;
	    	    		if(nextBindings instanceof List){
	    	    			List nextList = (List) bindingFiles.get(nextKey);
	    	    			for(int i=0;i< nextList.size();i++){
	    	    				Object o = nextList.get(i);
	    	    				if(o instanceof Source){
	    	    				    Source nextSource = (Source)o;
	    	    				    if(nextSource instanceof StreamSource){
	    	    				    	StreamSource ss= (StreamSource)nextSource;
	    	    				    	StreamSource ss2= new StreamSource(ss.getInputStream());
		    	    				    Object unmarshalledFromXML = u.unmarshal(ss2);
		    	    	    			StringWriter sw = new StringWriter();
		    		    	    	    StreamResult newResult = new StreamResult(sw);
		    		    	    	    jsonMarshaller.marshal(unmarshalledFromXML, newResult);		    	    	        
		    		    	    	    StreamSource newSource = new StreamSource(new StringReader(sw.toString()));		    	    	    				    		    	    	   
		    	    	    			nextList.set(i, newSource);
	    	    				    }
	    	    				}
	    	    			}
	    	    		}else if(nextBindings instanceof Source){	    
	    	    			Object unmarshalledFromXML = u.unmarshal((Source)nextBindings);
		    	    	      
	    	    			StringWriter sw = new StringWriter();
		    	    	    StreamResult newResult = new StreamResult(sw);
		    	    	    jsonMarshaller.marshal(unmarshalledFromXML, newResult);		    	    	        
		    	    	    StreamSource newSource = new StreamSource(new StringReader(sw.toString()));
	    	    			bindingFiles.put(nextKey, newSource);
	    	    		}
	    	    	}  
    	    	}else if(bindingFilesObject instanceof List){
    	    		List bindingFilesList = (List) bindingFilesObject;
    	    		for(int i=0; i<bindingFilesList.size(); i++){
    	    			Object next = bindingFilesList.get(i);
    	    			Object unmarshalledFromXML = getXmlBindings(next);    	    		    	    
        	    		StringWriter sw = new StringWriter();
        	    	    StreamResult newResult = new StreamResult(sw);
        	    	    jsonMarshaller.marshal(unmarshalledFromXML, newResult);		    	    	        
        	    	    StreamSource newSource = new StreamSource(new StringReader(sw.toString()));
        	    	    bindingFilesList.set(i, newSource);
    	    		}
    	    		props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, bindingFilesList);
    	    	}else{
    	    		Object unmarshalledFromXML = getXmlBindings(bindingFilesObject);    	    		    	    
    	    		StringWriter sw = new StringWriter();
    	    	    StreamResult newResult = new StreamResult(sw);
    	    	    jsonMarshaller.marshal(unmarshalledFromXML, newResult);		    	    	        
    	    	    StreamSource newSource = new StreamSource(new StringReader(sw.toString()));
    	    		props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, newSource);
    	    		
    	    	}
    	            	  
    	    }
    	}
    	return props;

    }
    
    private static XmlBindings getXmlBindings(Object metadata) {
        XmlBindings xmlBindings = null;
        Unmarshaller unmarshaller;
        // only create the JAXBContext for our XmlModel once
        JAXBContext jaxbContext = CompilerHelper.getXmlBindingsModelContext();
        try {
            unmarshaller = jaxbContext.createUnmarshaller();
            if (metadata instanceof File) {
                xmlBindings = (XmlBindings) unmarshaller.unmarshal((File) metadata);
            } else if (metadata instanceof InputSource) {
                xmlBindings = (XmlBindings) unmarshaller.unmarshal((InputSource) metadata);
            } else if (metadata instanceof BufferedInputStream) {            	
                   xmlBindings = (XmlBindings) unmarshaller.unmarshal((BufferedInputStream) metadata);
            } else if (metadata instanceof InputStream) {            	
                xmlBindings = (XmlBindings) unmarshaller.unmarshal((InputStream) metadata);
            } else if (metadata instanceof Node) {
                xmlBindings = (XmlBindings) unmarshaller.unmarshal((Node) metadata);
            } else if (metadata instanceof Reader) {
                xmlBindings = (XmlBindings) unmarshaller.unmarshal((Reader) metadata);
            } else if (metadata instanceof Source) {
                xmlBindings = (XmlBindings) unmarshaller.unmarshal((Source) metadata);
            } else if (metadata instanceof URL) {
                xmlBindings = (XmlBindings) unmarshaller.unmarshal((URL) metadata);
            } else if (metadata instanceof XMLEventReader) {
                xmlBindings = (XmlBindings) unmarshaller.unmarshal((XMLEventReader) metadata);
            } else if (metadata instanceof XMLStreamReader) {
                xmlBindings = (XmlBindings) unmarshaller.unmarshal((XMLStreamReader) metadata);
            } else if (metadata instanceof String) {
                if(((String)metadata).length() == 0) {
                    throw org.eclipse.persistence.exceptions.JAXBException.unableToLoadMetadataFromLocation((String)metadata);
                }
                URL url = null;
                try {
                    url = new URL((String)metadata);
                } catch(MalformedURLException ex) {
                    url = Thread.currentThread().getContextClassLoader().getResource((String)metadata);
                }
                if(url != null) {
                    xmlBindings = (XmlBindings)unmarshaller.unmarshal(url);
                } else {
                    //throw exception
                    throw org.eclipse.persistence.exceptions.JAXBException.unableToLoadMetadataFromLocation((String)metadata);
                }
            } else {
                throw org.eclipse.persistence.exceptions.JAXBException.incorrectValueParameterTypeForOxmXmlKey();
            }
        } catch (JAXBException jaxbEx) {
            throw org.eclipse.persistence.exceptions.JAXBException.couldNotUnmarshalMetadata(jaxbEx);
        }
        return xmlBindings;
    }
 
    
    
    
    public JAXBContext getJAXBContext() {
        return jaxbContext;
    }

    public Marshaller getJAXBMarshaller() {
        return jaxbMarshaller;
    }

    public Unmarshaller getJAXBUnmarshaller() {
        return jaxbUnmarshaller;
    }
    
    public Class getUnmarshalClass(){
    	return null;
    }

    public void testXMLToObjectFromInputStream() throws Exception {
    	
        if(isUnmarshalTest()) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            jaxbUnmarshaller.setProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, "application/xml");
            Object testObject = null;
            if(getUnmarshalClass() != null){
               testObject = ((JAXBUnmarshaller)jaxbUnmarshaller).unmarshal(new StreamSource(instream), getUnmarshalClass());	
            }else{
               testObject = jaxbUnmarshaller.unmarshal(instream);
            }
            
            instream.close();
            xmlToObjectTest(testObject);
            
            if(getProperties() != null){
            	JAXBContext jaxbContextFromJSONBindings = createJaxbContextFromJSONBindings();
               	Unmarshaller jaxbUnmarshallerFromJSONBindings = jaxbContextFromJSONBindings.createUnmarshaller();
               	jaxbUnmarshallerFromJSONBindings.setAttachmentUnmarshaller(jaxbUnmarshaller.getAttachmentUnmarshaller());
               	jaxbUnmarshallerFromJSONBindings.setProperty(JAXBUnmarshaller.JSON_NAMESPACE_PREFIX_MAPPER, jaxbMarshaller.getProperty(JAXBUnmarshaller.JSON_NAMESPACE_PREFIX_MAPPER));
            	Object testObject2 = null;
           	     log("************test with JSON bindings*********");
           	     InputStream instream2 = ClassLoader.getSystemResourceAsStream(resourceName);
           	     if(getUnmarshalClass() != null){
                     testObject2 = ((JAXBUnmarshaller)jaxbUnmarshallerFromJSONBindings).unmarshal(new StreamSource(instream2), getUnmarshalClass());	
                 }else{
                     testObject2 = jaxbUnmarshallerFromJSONBindings.unmarshal(instream2);
                 }
                 instream2.close();
                 xmlToObjectTest(testObject2);
            }
        }
    }
    
    private JAXBContext createJaxbContextFromJSONBindings() throws JAXBException{
    	if(classes != null){
    		return JAXBContextFactory.createContext(classes, getPropertiesFromJSON(), classLoader);
    	}else if (types != null){
    		return JAXBContextFactory.createContext(types, getPropertiesFromJSON(), classLoader);
    	}else if(contextPath != null){
    		return JAXBContextFactory.createContext(contextPath, classLoader, getPropertiesFromJSON());
    	}
    	return null;
    }
    
    public void testRoundTrip() throws Exception{
        if(isUnmarshalTest()) {
            InputStream instream = null;
            if(writeControlDocumentLocation !=null){
                instream = ClassLoader.getSystemResourceAsStream(writeControlDocumentLocation);
            }else{
                instream = ClassLoader.getSystemResourceAsStream(resourceName);
            }
            jaxbUnmarshaller.setProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, "application/xml");
            Object testObject = null;
            if(getUnmarshalClass() != null){
            	testObject = jaxbUnmarshaller.unmarshal(new StreamSource(instream), getUnmarshalClass());
            }else{
            	testObject = jaxbUnmarshaller.unmarshal(instream);
            }
                
                instream.close();
                xmlToObjectTest(testObject);

                objectToXMLStringWriter(testObject);
        }
    }

    public void testObjectToOutputStream() throws Exception {
        Object objectToWrite = getWriteControlObject();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        XMLDescriptor desc = null;
        if (objectToWrite instanceof XMLRoot) {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
        } else {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
        }

        int sizeBefore = getNamespaceResolverSize(desc);
        jaxbMarshaller.setProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, "application/xml");
        jaxbMarshaller.marshal(objectToWrite, stream);

        int sizeAfter = getNamespaceResolverSize(desc);

        assertEquals(sizeBefore, sizeAfter);

        InputStream is = new ByteArrayInputStream(stream.toByteArray());
        Document testDocument = parser.parse(is);
        stream.close();
        is.close();

        objectToXMLDocumentTest(testDocument);     
        
        if(getProperties() != null){
        	 log("************test with JSON bindings*********");
        	 ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
        	 JAXBContext jaxbContextFromJSONBindings = createJaxbContextFromJSONBindings();
        	 Marshaller jaxbMarshallerFromJSONBindings = jaxbContextFromJSONBindings.createMarshaller();
        	 jaxbMarshallerFromJSONBindings.setAttachmentMarshaller(jaxbMarshaller.getAttachmentMarshaller());
        	 
        	 
     		 jaxbMarshallerFromJSONBindings.setProperty(JAXBMarshaller.NAMESPACE_PREFIX_MAPPER, jaxbMarshaller.getProperty(JAXBMarshaller.NAMESPACE_PREFIX_MAPPER));
    		 

        	 
             jaxbMarshallerFromJSONBindings.marshal(objectToWrite, stream2);        	         	 
             InputStream is2 = new ByteArrayInputStream(stream2.toByteArray());
             Document testDocument2 = parser.parse(is2);
             stream2.close();
             is2.close();

             objectToXMLDocumentTest(testDocument2);     
             
        }
    }

    public void testObjectToOutputStreamASCIIEncoding() throws Exception {
        Object objectToWrite = getWriteControlObject();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        XMLDescriptor desc = null;
        if (objectToWrite instanceof XMLRoot) {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
        } else {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
        }
        jaxbMarshaller.setProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, "application/xml");

        int sizeBefore = getNamespaceResolverSize(desc);
        String originalEncoding = (String)jaxbMarshaller.getProperty(Marshaller.JAXB_ENCODING);
        jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "US-ASCII");
        jaxbMarshaller.marshal(objectToWrite, stream);
        jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, originalEncoding);
        int sizeAfter = getNamespaceResolverSize(desc);

        assertEquals(sizeBefore, sizeAfter);

        InputStream is = new ByteArrayInputStream(stream.toByteArray());
        Document testDocument = parser.parse(is);
        stream.close();
        is.close();

        objectToXMLDocumentTest(testDocument);
    }


    public void testObjectToXMLStringWriter() throws Exception {
        objectToXMLStringWriter(getWriteControlObject());
    }

    @Override
    public void testValidatingMarshal() {
    }

    public void objectToXMLStringWriter(Object objectToWrite) throws Exception {
        StringWriter writer = new StringWriter();

        XMLDescriptor desc = null;
        if (objectToWrite instanceof XMLRoot) {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
        } else {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
        }

        int sizeBefore = getNamespaceResolverSize(desc);
        jaxbMarshaller.setProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, "application/xml");

        jaxbMarshaller.marshal(objectToWrite, writer);

        int sizeAfter = getNamespaceResolverSize(desc);

        assertEquals(sizeBefore, sizeAfter);

        StringReader reader = new StringReader(writer.toString());        
        InputSource inputSource = new InputSource(reader);
        Document testDocument = parser.parse(inputSource);
        writer.close();
        reader.close();

        objectToXMLDocumentTest(testDocument);
    }

    public void testObjectToXMLStreamWriter() throws Exception {
        if(XML_OUTPUT_FACTORY != null) {
            StringWriter writer = new StringWriter();

            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            factory.setProperty(factory.IS_REPAIRING_NAMESPACES, new Boolean(false));
            XMLStreamWriter streamWriter= factory.createXMLStreamWriter(writer);

            Object objectToWrite = getWriteControlObject();
            XMLDescriptor desc = null;
            if (objectToWrite instanceof XMLRoot) {
                desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
            } else {
                desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
            }
            jaxbMarshaller.setProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, "application/xml");

            int sizeBefore = getNamespaceResolverSize(desc);
            jaxbMarshaller.marshal(objectToWrite, streamWriter);

            streamWriter.flush();
            int sizeAfter = getNamespaceResolverSize(desc);

            assertEquals(sizeBefore, sizeAfter);
            StringReader reader = new StringReader(writer.toString());
            InputSource inputSource = new InputSource(reader);
            Document testDocument = parser.parse(inputSource);
            writer.close();
            reader.close();
            objectToXMLDocumentTest(testDocument);
        }
    }

    public void testObjectToXMLEventWriter() throws Exception {
        if(XML_OUTPUT_FACTORY != null) {
            StringWriter writer = new StringWriter();

            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            factory.setProperty(factory.IS_REPAIRING_NAMESPACES, new Boolean(false));
            XMLEventWriter eventWriter= factory.createXMLEventWriter(writer);

            Object objectToWrite = getWriteControlObject();
            XMLDescriptor desc = null;
            if (objectToWrite instanceof XMLRoot) {
                desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
            } else {
                desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
            }
            jaxbMarshaller.setProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, "application/xml");

            int sizeBefore = getNamespaceResolverSize(desc);
            jaxbMarshaller.marshal(objectToWrite, eventWriter);

            eventWriter.flush();
            int sizeAfter = getNamespaceResolverSize(desc);

            assertEquals(sizeBefore, sizeAfter);
            StringReader reader = new StringReader(writer.toString());
            InputSource inputSource = new InputSource(reader);
            Document testDocument = parser.parse(inputSource);
            writer.close();
            reader.close();
            objectToXMLDocumentTest(testDocument);
        }
    }

    public void testObjectToContentHandler() throws Exception {
        SAXDocumentBuilder builder = new SAXDocumentBuilder();
        Object objectToWrite = getWriteControlObject();
        XMLDescriptor desc = null;
        if (objectToWrite instanceof XMLRoot) {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
        } else {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
        }
        int sizeBefore = getNamespaceResolverSize(desc);
        jaxbMarshaller.setProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, "application/xml");

        jaxbMarshaller.marshal(objectToWrite, builder);

        int sizeAfter = getNamespaceResolverSize(desc);

        assertEquals(sizeBefore, sizeAfter);

        Document controlDocument = getWriteControlDocument();
        Document testDocument = builder.getDocument();

        log("**testObjectToContentHandler**");
        log("Expected:");
        log(controlDocument);
        log("\nActual:");
        log(testDocument);

        //Diff diff = new Diff(controlDocument, testDocument);
        //this.assertXMLEqual(diff, true);
        assertXMLIdentical(controlDocument, testDocument);
    }

    public void testXMLToObjectFromURL() throws Exception {
        if(isUnmarshalTest()) {
            java.net.URL url = ClassLoader.getSystemResource(resourceName);
            jaxbUnmarshaller.setProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, "application/xml");
            
            Object testObject = null;
            if(getUnmarshalClass() != null){
               testObject = ((JAXBUnmarshaller)jaxbUnmarshaller).unmarshal(new StreamSource(url.openStream()), getUnmarshalClass());	
            }else{
            	testObject = jaxbUnmarshaller.unmarshal(url);
            }
            xmlToObjectTest(testObject);
        }
    }

    public void testXMLToObjectFromXMLStreamReader() throws Exception {
        if(null != XML_INPUT_FACTORY && isUnmarshalTest()) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);
            jaxbUnmarshaller.setProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, "application/xml");
            
            Object testObject = null;
            if(getUnmarshalClass() != null){                            
                testObject = getJAXBUnmarshaller().unmarshal(xmlStreamReader, getUnmarshalClass());
            }else{
            	testObject = jaxbUnmarshaller.unmarshal(xmlStreamReader);
            }
            instream.close();
            xmlToObjectTest(testObject);
        }
    }
    public void testXMLToObjectFromNode() throws Exception {
        if(isUnmarshalTest()) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);            
            Node node  = parser.parse(instream);
            jaxbUnmarshaller.setProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, "application/xml");
            Object testObject = null;
            if(getUnmarshalClass() != null){
               testObject = jaxbUnmarshaller.unmarshal(node, getUnmarshalClass());	
            }else{
            	testObject = jaxbUnmarshaller.unmarshal(node);
            }
            instream.close();
            xmlToObjectTest(testObject);
        }
    }
  
    
    public void testXMLToObjectFromXMLStreamReaderEx() throws Exception {
        if(null != XML_INPUT_FACTORY && isUnmarshalTest()) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);

            ExtendedXMLStreamReaderReader xmlStreamReaderReaderEx = new ExtendedXMLStreamReaderReader();
            XMLStreamReaderInputSource xmlStreamReaderInputSource = new XMLStreamReaderInputSource(xmlStreamReader);
            SAXSource saxSource = new SAXSource(xmlStreamReaderReaderEx, xmlStreamReaderInputSource);
            jaxbUnmarshaller.setProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, "application/xml");
            Object testObject = null;
            if(getUnmarshalClass() != null){
            	 testObject = jaxbUnmarshaller.unmarshal(saxSource, getUnmarshalClass()); 	
            }else{
            	 testObject = jaxbUnmarshaller.unmarshal(saxSource);
            }
            
            instream.close();
            xmlToObjectTest(testObject);
        }
    }

    public void testXMLToObjectFromXMLEventReader() throws Exception {
        if(null != XML_INPUT_FACTORY && isUnmarshalTest()) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            XMLEventReader xmlEventReader = XML_INPUT_FACTORY.createXMLEventReader(instream);
            jaxbUnmarshaller.setProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, "application/xml");
            Object testObject = null;
            if(getUnmarshalClass() != null){
               testObject = jaxbUnmarshaller.unmarshal(xmlEventReader, getUnmarshalClass());	
            }else{
            	testObject = jaxbUnmarshaller.unmarshal(xmlEventReader);
            }
            instream.close();
            xmlToObjectTest(testObject);
        }
    }

    public void testObjectToXMLDocument() throws Exception {
        Object objectToWrite = getWriteControlObject();
        XMLDescriptor desc = null;
        if (objectToWrite instanceof XMLRoot) {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
        } else {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
        }
        jaxbMarshaller.setProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, "application/xml");

        int sizeBefore = getNamespaceResolverSize(desc);
        Document testDocument = XMLPlatformFactory.getInstance().getXMLPlatform().createDocument();
        jaxbMarshaller.marshal(objectToWrite, testDocument);
        int sizeAfter = getNamespaceResolverSize(desc);
        assertEquals(sizeBefore, sizeAfter);
        objectToXMLDocumentTest(testDocument);
    }


    public void xmlToObjectTest(Object testObject) throws Exception {
    	xmlToObjectTest(testObject, getReadControlObject());
    }

    public void xmlToObjectTest(Object testObject, Object controlObject) throws Exception {
        log("\n**xmlToObjectTest**");
        log("Expected:");
        log(controlObject.toString());
        log("Actual:");
        log(testObject.toString());

        if ((controlObject instanceof JAXBElement) && (testObject instanceof JAXBElement)) {
            JAXBElement controlObj = (JAXBElement)controlObject;
            JAXBElement testObj = (JAXBElement)testObject;
            compareJAXBElementObjects(controlObj, testObj);
        } else {
            super.xmlToObjectTest(testObject);
        }
    }
    public void testUnmarshallerHandler() throws Exception {
        if(isUnmarshalTest()) {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setNamespaceAware(true);
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            jaxbUnmarshaller.setProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, "application/xml");

            JAXBUnmarshallerHandler jaxbUnmarshallerHandler = (JAXBUnmarshallerHandler)jaxbUnmarshaller.getUnmarshallerHandler();
            xmlReader.setContentHandler(jaxbUnmarshallerHandler);
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourceName);
            InputSource inputSource = new InputSource(inputStream);
            xmlReader.parse(inputSource);
    
            xmlToObjectTest(jaxbUnmarshallerHandler.getResult());
        }
    }

    public void testSchemaGen(List<InputStream> controlSchemas) throws Exception {
        MyStreamSchemaOutputResolver outputResolver = new MyStreamSchemaOutputResolver();
        getJAXBContext().generateSchema(outputResolver);

        List<Writer> generatedSchemas = outputResolver.getSchemaFiles();
        compareSchemas(controlSchemas, generatedSchemas);
    }
    
    public void compareSchemas(List<InputStream> controlSchemas, List<Writer> generatedSchemas) throws Exception {
        assertEquals(controlSchemas.size(), generatedSchemas.size());

        for(int i=0;i<controlSchemas.size(); i++){
            InputStream nextControlValue = controlSchemas.get(i);

            Writer sw = generatedSchemas.get(i);
            InputSource nextGeneratedValue = new InputSource(new StringReader(sw.toString()));

            assertNotNull("Generated Schema not found.", nextGeneratedValue);

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

    protected String validateAgainstSchema(InputStream src, Source schemaSource) {
    	return validateAgainstSchema(src, schemaSource, null);
    }
    protected String validateAgainstSchema(InputStream src, Source schemaSource, MyMapStreamSchemaOutputResolver outputResolver) {
        SchemaFactory sFact = SchemaFactory.newInstance(XMLConstants.SCHEMA_URL);
        sFact.setResourceResolver(new ResourceResolver(outputResolver));

        Schema theSchema;
        try {            
            theSchema = sFact.newSchema(schemaSource);
            Validator validator = theSchema.newValidator();            
            StreamSource ss = new StreamSource(src);             
            validator.validate(ss);
        } catch (Exception e) {
            if (e.getMessage() == null) {
                return "An unknown exception occurred.";
            }
            return e.getMessage();
        }
        return null;
    }
    
    public class MySchemaOutputResolver extends SchemaOutputResolver {
        // keep a list of processed schemas for the validation phase of the
        // test(s)
        public List<File> schemaFiles;

        public MySchemaOutputResolver() {
            schemaFiles = new ArrayList<File>();
        }

        public Result createOutput(String namespaceURI, String suggestedFileName)throws IOException {
            File schemaFile = new File(suggestedFileName);
            if(namespaceURI == null){
                namespaceURI ="";
            }
            schemaFiles.add(schemaFile);
            return new StreamResult(schemaFile);
        }

        public List<File> getSchemaFiles() {
            return schemaFiles;
        }
    }
    
    /**
     * SchemaOutputResolver for writing out the generated schema.  Returns a StreamResult
     * wrapping a StringWriter.
     *
     */
    public static class MyStreamSchemaOutputResolver extends SchemaOutputResolver {
        // keep a list of processed schemas for the validation phase of the test(s)
        private List<Writer> schemaFiles;
        
        public MyStreamSchemaOutputResolver() {
            schemaFiles = new ArrayList<Writer>();
        }
        
        public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
            if (namespaceURI == null) {
                namespaceURI = "";
            }
            
            StringWriter sw = new StringWriter();
            schemaFiles.add(sw);
            Result res = new StreamResult(sw);
            res.setSystemId(suggestedFileName);
            return res;
        }
        
        public List<Writer> getSchemaFiles() {
            return schemaFiles;
        }
    }

    public static class MyMapStreamSchemaOutputResolver extends SchemaOutputResolver {
        // keep a list of processed schemas for the validation phase of the test(s)
        public Map<String, Writer> schemaFiles;
        
        public MyMapStreamSchemaOutputResolver() {
            schemaFiles = new HashMap<String, Writer>();
        }
        
        public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
            //return new StreamResult(System.out);
            if (namespaceURI == null) {
                namespaceURI = "";
            }
            
            StringWriter sw = new StringWriter();
            schemaFiles.put(namespaceURI, sw);
            Result res = new StreamResult(sw);
            res.setSystemId(suggestedFileName);
            return res;
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


    protected void validateBindingsFileAgainstSchema(Object obj) {
    	if(obj instanceof List){
    		List theList = (List)obj;
    		for(int i=0; i<theList.size(); i++){
    		    Object next = theList.get(i);
    		    if(next instanceof Source) {
    		        Source src = (Source)theList.get(i);
    		        validateBindingsFileAgainstSchema(src);
    		    }
    		}
    	}
    }
    
    protected void validateBindingsFileAgainstSchema(Source src) {
        String result = null;
        SchemaFactory sFact = SchemaFactory.newInstance(XMLConstants.SCHEMA_URL);
        Schema theSchema;
        try {
            InputStream bindingsFileXSDInputStream = getClass().getClassLoader().getResourceAsStream(ECLIPSELINK_OXM_XSD);
            if (bindingsFileXSDInputStream == null){
                bindingsFileXSDInputStream = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/jaxb/" + ECLIPSELINK_OXM_XSD);
            }
            if (bindingsFileXSDInputStream == null){
                fail("ERROR LOADING " + ECLIPSELINK_OXM_XSD);
            }
            Source bindingsFileXSDSource = new StreamSource(bindingsFileXSDInputStream);
            theSchema = sFact.newSchema(bindingsFileXSDSource);
            Validator validator = theSchema.newValidator();
                   
            validator.validate(src);
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage() == null) {
                result = "An unknown exception occurred.";
            }
            result = e.getMessage();
        }
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    public static class ExtendedXMLStreamReaderReader extends XMLStreamReaderReader {

        @Override
        protected void parseCharactersEvent(XMLStreamReader xmlStreamReader) throws SAXException {
            try {
                contentHandler.characters(xmlStreamReader.getElementText());
            } catch(XMLStreamException e) {
                super.parseCharactersEvent(xmlStreamReader);
            }
        }

    }
    
    /**
     * Class responsible from resolving schema imports during schema
     * validation.
     *
     */
    class ResourceResolver implements LSResourceResolver {
        private MyMapStreamSchemaOutputResolver oResolver;
        
        public ResourceResolver(MyMapStreamSchemaOutputResolver resolver) {
            oResolver = resolver;
        }
        public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseUri) {
            return new MyLSInput(namespaceURI, oResolver);
        }
    }
    
    /**
     * Class which will be returned to from resolveResource() call in 
     * ResourceResolver.
     * 
     */
    class MyLSInput implements LSInput {
        private String sValue;
        private MyMapStreamSchemaOutputResolver oResolver;

        public MyLSInput(String value, MyMapStreamSchemaOutputResolver resolver) {
            sValue = value;
            oResolver = resolver;
        }
        public void setSystemId(String arg0) {}
        public void setStringData(String arg0) {}
        public void setPublicId(String arg0) {}
        public void setEncoding(String arg0) {}
        public void setCharacterStream(Reader arg0) {}
        public void setCertifiedText(boolean arg0) {}
        public void setByteStream(InputStream arg0) {}
        public void setBaseURI(String arg0) {}
        public String getSystemId() {return null;}
        public String getStringData() {
            return oResolver.schemaFiles.get(sValue).toString();
        }
        public String getPublicId() {return null;}
        public String getEncoding() {return null;}
        public Reader getCharacterStream() {return null;}
        public boolean getCertifiedText() {return false;}
        public InputStream getByteStream() {return null;}
        public String getBaseURI() {return null;}
    }

}
