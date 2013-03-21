/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.oxm.record;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.ValidatorHandler;

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.Context;
import org.eclipse.persistence.internal.oxm.MediaType;
import org.eclipse.persistence.internal.oxm.Root;
import org.eclipse.persistence.internal.oxm.Unmarshaller;
import org.eclipse.persistence.internal.oxm.UnmarshallerHandler;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.internal.oxm.record.json.JSONReader;
import org.eclipse.persistence.platform.xml.DefaultErrorHandler;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.platform.xml.XMLTransformer;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>Provide an implementation of PlatformUnmarshaller that makes use of the SAX parser
 * to build Java Objects from SAX Events.
 * <p><b>Responsibilities:</b><ul>
 * <li>Implement the required unmarshal methods from PlatformUnmarshaller</li>
 * <li>Check to see if document preservation is enabled, and if so, always unmarshal from a node</li>
 * </ul>
 * 
 * @author bdoughan
 * @see org.eclipse.persistence.oxm.platform.SAXPlatform
 */
public class SAXUnmarshaller implements PlatformUnmarshaller {
    private static final String VALIDATING = "http://xml.org/sax/features/validation";
    private static final String SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    private static final String SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    private static final String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    private static final UnmarshalKeepAsElementPolicy KEEP_UNKNOWN_AS_ELEMENT = new UnmarshalKeepAsElementPolicy() {

        @Override
        public boolean isKeepAllAsElement() {
            return false;
        }

        @Override
        public boolean isKeepNoneAsElement() {
            return false;
        }

        @Override
        public boolean isKeepUnknownAsElement() {
            return true;
        }

    };

    private int validationMode = XMLParser.NONVALIDATING;
    private Schema schema;
    private Object[] schemas;
    private EntityResolver entityResolver;
    private ErrorHandler errorHandler = new DefaultErrorHandler();

    private SAXParser saxParser;
    private XMLReader xmlReader;
    private Unmarshaller xmlUnmarshaller;
    private XMLParser xmlParser;
    private boolean isResultAlwaysXMLRoot, isWhitespacePreserving;
    private SAXParserFactory saxParserFactory;
    private String systemId = null;
    private Map<String, Boolean> parserFeatures;

    public SAXUnmarshaller(Unmarshaller xmlUnmarshaller, Map<String, Boolean> parserFeatures) throws XMLMarshalException {
        super();
        this.parserFeatures = parserFeatures;
        try {

            this.xmlUnmarshaller = xmlUnmarshaller;
        } catch (Exception e) {
            throw XMLMarshalException.errorInstantiatingSchemaPlatform(e);
        }
    }

    private SAXParserFactory getSAXParserFactory() throws XMLMarshalException {
        if(null == saxParserFactory) {
            try {
                saxParserFactory = SAXParserFactory.newInstance();
                saxParserFactory.setNamespaceAware(true);
                saxParserFactory.setFeature(XMLReader.NAMESPACE_PREFIXES_FEATURE, true);
                try {
                    saxParserFactory.setFeature(XMLReader.REPORT_IGNORED_ELEMENT_CONTENT_WHITESPACE_FEATURE, true);
                } catch(org.xml.sax.SAXNotRecognizedException ex) {
                    //ignore if the parser doesn't recognize or support this feature
                } catch(org.xml.sax.SAXNotSupportedException ex) {
                }

                if(null != parserFeatures) {
                    for(Map.Entry<String, Boolean> parserFeature : parserFeatures.entrySet()) {
                        try {
                            saxParserFactory.setFeature(parserFeature.getKey(), parserFeature.getValue());
                        } catch(org.xml.sax.SAXNotRecognizedException ex) {
                            //ignore if the parser doesn't recognize or support this feature
                        } catch(org.xml.sax.SAXNotSupportedException ex) {
                        }
                    }
                }
                return saxParserFactory;
            } catch (Exception e) {
                throw XMLMarshalException.errorInstantiatingSchemaPlatform(e);
            }
        }
        return saxParserFactory;
    }

    private SAXParser getSAXParser() {
        if(null == saxParser) {
            try {
                saxParser = getSAXParserFactory().newSAXParser();
            } catch (Exception e) {
                throw XMLMarshalException.errorInstantiatingSchemaPlatform(e);
            }
        }
        return saxParser;
    }

    private XMLParser getXMLParser() {
        xmlParser = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLParser();
        xmlParser.setNamespaceAware(true);
        if(null != errorHandler) {
            xmlParser.setErrorHandler(errorHandler);
        }
        if(null != entityResolver) {
            xmlParser.setEntityResolver(entityResolver);
        }
        xmlParser.setValidationMode(validationMode);
        xmlParser.setWhitespacePreserving(isWhitespacePreserving);
        return xmlParser;
    }

    private XMLReader getXMLReader() {
    	return getXMLReader(null);
    }
    
    private XMLReader getXMLReader(Class clazz) {
        if(null == xmlReader) {        	
        	 xmlReader = getNewXMLReader(clazz, xmlUnmarshaller.getMediaType() );
        }
        return xmlReader;
    }
    
    private XMLReader getNewXMLReader(MediaType mediaType) {
    	return getNewXMLReader(null, mediaType);
    }
    
    private XMLReader getNewXMLReader(Class clazz, MediaType mediaType) {
              	
        	if(mediaType.isApplicationJSON()){        	
        	 	return new JSONReader(xmlUnmarshaller.getAttributePrefix(), xmlUnmarshaller.getNamespaceResolver(), xmlUnmarshaller.getNamespaceResolver() != null, xmlUnmarshaller.isIncludeRoot(), xmlUnmarshaller.getNamespaceSeparator(), xmlUnmarshaller.getErrorHandler(), xmlUnmarshaller.getValueWrapper(), clazz);        	 	
        	}
            try {
            	XMLReader xmlReader = new XMLReader(getSAXParser().getXMLReader());
                if(null != errorHandler) {
                    xmlReader.setErrorHandler(errorHandler);
                }
                if(null != entityResolver) {
                    xmlReader.setEntityResolver(entityResolver);
                }
                setValidationMode(xmlReader, getValidationMode());
                if(null != getSchema()) {
                    xmlReader.setFeature(VALIDATING, xmlReader.getFeature(VALIDATING));
                }
                return xmlReader;
            } catch (Exception e) {
                throw XMLMarshalException.errorInstantiatingSchemaPlatform(e);
            }
    }

    public EntityResolver getEntityResolver() {
        return entityResolver;
    }

    public void setEntityResolver(EntityResolver entityResolver) {
        if(null != xmlReader) {
            xmlReader.setEntityResolver(entityResolver);
        }
        if(null != xmlParser) {
            xmlParser.setEntityResolver(entityResolver);
        }
        this.entityResolver = entityResolver;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        if(null != xmlReader) {
            xmlReader.setErrorHandler(errorHandler);
        }
        if(null != xmlParser) {
            xmlParser.setErrorHandler(errorHandler);
        }
        this.errorHandler = errorHandler;
    }

    public int getValidationMode() {
        return validationMode;
    }

    public void setValidationMode(int validationMode) {
    	setValidationMode(xmlReader, validationMode);
    }

    public void setValidationMode(XMLReader xmlReader, int validationMode) {
        try {
            this.validationMode = validationMode;
            if(null != xmlParser) {
                xmlParser.setValidationMode(validationMode);
            }
            if(null == xmlReader) {
                return;
            }
            switch (validationMode) {
            case XMLParser.NONVALIDATING: {
                xmlReader.setFeature(VALIDATING, false);
                break;
            }
            case XMLParser.DTD_VALIDATION: {
                xmlReader.setFeature(VALIDATING, true);
                break;
            }
            case XMLParser.SCHEMA_VALIDATION: {
                try {
                    xmlReader.setFeature(VALIDATING, true);
                    saxParser.setProperty(SCHEMA_LANGUAGE, XML_SCHEMA);
                    saxParser.setProperty(SCHEMA_SOURCE, schemas);
                } catch (Exception e) {
                    xmlReader.setFeature(VALIDATING, false);
                }
                break;
            }
            }
        } catch (Exception e) {
            // Don't change the validation mode.
        }
    }
    
    public void setWhitespacePreserving(boolean isWhitespacePreserving) {
        this.isWhitespacePreserving =  isWhitespacePreserving;
        if(null != xmlParser) {
            xmlParser.setWhitespacePreserving(isWhitespacePreserving);
        }
    }

    public void setSchemas(Object[] schemas) {
        this.schemas = schemas;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
        if(null != xmlParser) {
            xmlParser.setXMLSchema(schema);
        }
    }
    
    public Schema getSchema() {
        return schema;
    }

    public Object unmarshal(File file) {
        try {
            if (xmlUnmarshaller.getContext().hasDocumentPreservation()) {
                Node domElement = getXMLParser().parse(file).getDocumentElement();
                return unmarshal(domElement);
            }

            this.systemId = file.toURI().toURL().toExternalForm();

            FileInputStream inputStream = new FileInputStream(file);
            try {
                return unmarshal(inputStream);
            } finally {
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            throw XMLMarshalException.unmarshalException(e);
        } catch (IOException e) {
            throw XMLMarshalException.unmarshalException(e);
        } finally {
        	xmlUnmarshaller.getStringBuffer().reset();
        }
    }

    public Object unmarshal(File file, Class clazz) {
        try {
            if (xmlUnmarshaller.getContext().hasDocumentPreservation()) {
                Node domElement = getXMLParser().parse(file).getDocumentElement();
                return unmarshal(domElement, clazz);
            }

            this.systemId = file.toURI().toURL().toExternalForm();

            FileInputStream inputStream = new FileInputStream(file);
            try {
                return unmarshal(inputStream, clazz);
            } finally {
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            throw XMLMarshalException.unmarshalException(e);
        } catch (IOException e) {
            throw XMLMarshalException.unmarshalException(e);
        } finally {
        	xmlUnmarshaller.getStringBuffer().reset();
        }
    }

    public Object unmarshal(InputStream inputStream) {
        if (xmlUnmarshaller.getContext().hasDocumentPreservation()) {
            Node domElement = getXMLParser().parse(inputStream).getDocumentElement();
            return unmarshal(domElement);
        }
        InputSource inputSource = new InputSource(inputStream);
        return unmarshal(inputSource);
    }

    public Object unmarshal(InputStream inputStream, Class clazz) {
        if (xmlUnmarshaller.getContext().hasDocumentPreservation()) {
            Node domElement = getXMLParser().parse(inputStream).getDocumentElement();
            return unmarshal(domElement, clazz);
        }
        InputSource inputSource = new InputSource(inputStream);
        return unmarshal(inputSource, clazz);
    }

    public Object unmarshal(InputSource inputSource) {
        if (inputSource != null && null == inputSource.getSystemId()) {
            inputSource.setSystemId(this.systemId);
        }
        
        if(xmlUnmarshaller.isAutoDetectMediaType()){
        	BufferedReader bufferedReader = getBufferedReaderForInputSource(inputSource);        	
        	MediaType mediaType = getMediaType(bufferedReader);				
            return unmarshal(getNewXMLReader(mediaType), new InputSource(bufferedReader));			
        }
        return unmarshal(getXMLReader(), inputSource);
    }

    public Object unmarshal(InputSource inputSource, XMLReader xmlReader) {
        try {
            if (inputSource != null && null == inputSource.getSystemId()) {
                inputSource.setSystemId(this.systemId);
            }

            SAXUnmarshallerHandler saxUnmarshallerHandler = new SAXUnmarshallerHandler(xmlUnmarshaller.getContext());
            saxUnmarshallerHandler.setXMLReader(xmlReader);
            saxUnmarshallerHandler.setUnmarshaller(xmlUnmarshaller);
            setContentHandler(xmlReader, saxUnmarshallerHandler);
            xmlReader.parse(inputSource);

            // resolve any mapping references
            saxUnmarshallerHandler.resolveReferences();
            return saxUnmarshallerHandler.getObject();
        } catch (IOException e) {
            throw XMLMarshalException.unmarshalException(e);
        } catch (SAXException e) {
            throw convertSAXException(e);
        } finally {
        	xmlUnmarshaller.getStringBuffer().reset();
        }
    }

    public Object unmarshal(InputSource inputSource, Class clazz) {
        if (inputSource != null && null == inputSource.getSystemId()) {
            inputSource.setSystemId(this.systemId);
        }

        if(xmlUnmarshaller.isAutoDetectMediaType()){
        	BufferedReader bufferedReader = getBufferedReaderForInputSource(inputSource);        	
        	MediaType mediaType = getMediaType(bufferedReader);				
            return unmarshal(getNewXMLReader(clazz, mediaType), new InputSource(bufferedReader), clazz);        	 
        }
        return unmarshal(getXMLReader(clazz), inputSource, clazz);
    }

    public Object unmarshal(InputSource inputSource, Class clazz, XMLReader xmlReader) {
        if (inputSource != null && null == inputSource.getSystemId()) {
            inputSource.setSystemId(this.systemId);
        }
        
        UnmarshalRecord unmarshalRecord =null;
        Descriptor xmlDescriptor = null;

        // for XMLObjectReferenceMappings we need a non-shared cache, so
        // try and get a Unit Of Work from the XMLContext
        CoreAbstractSession session = null;

        // check for case where the reference class is a primitive wrapper - in this case, we 
        // need to use the conversion manager to convert the node's value to the primitive 
        // wrapper class, then create, populate and return an XMLRoot.  This will be done
        // via XMLRootRecord.
        boolean isPrimitiveWrapper = false;
        if(clazz == CoreClassConstants.OBJECT) {
    	    try{
                SAXUnmarshallerHandler saxUnmarshallerHandler = new SAXUnmarshallerHandler(xmlUnmarshaller.getContext());
                saxUnmarshallerHandler.setXMLReader(xmlReader);
                saxUnmarshallerHandler.setUnmarshaller(xmlUnmarshaller);
                saxUnmarshallerHandler.setKeepAsElementPolicy(KEEP_UNKNOWN_AS_ELEMENT);
                setContentHandler(xmlReader, saxUnmarshallerHandler);
                xmlReader.parse(inputSource);
            
                // resolve any mapping references
                saxUnmarshallerHandler.resolveReferences();
                return saxUnmarshallerHandler.getObject();
            } catch (IOException e) {
                throw XMLMarshalException.unmarshalException(e);
            } catch (SAXException e) {
                throw convertSAXException(e);
            }
        } else {
            // for XMLObjectReferenceMappings we need a non-shared cache, so
            // try and get a Unit Of Work from the XMLContext
        	try{
            session = xmlUnmarshaller.getContext().getSession(clazz);
            xmlDescriptor = (Descriptor)session.getDescriptor(clazz);
            unmarshalRecord = xmlUnmarshaller.createUnmarshalRecord(xmlDescriptor, session);

            
        	}catch(XMLMarshalException xme){
        		if(xme.getErrorCode() == XMLMarshalException.DESCRIPTOR_NOT_FOUND_IN_PROJECT){            			 
 	                isPrimitiveWrapper = isPrimitiveWrapper(clazz);
 	                if (isPrimitiveWrapper) {
 	                       unmarshalRecord = xmlUnmarshaller.createRootUnmarshalRecord(clazz);
 	                }else{
 	                	throw xme;
 	                }
 	                
        		}else{
        		   throw xme;
        		}
               
        	}           
        }

        try {
            unmarshalRecord.setXMLReader(xmlReader);
            unmarshalRecord.setUnmarshaller(xmlUnmarshaller);
            setContentHandler(xmlReader, unmarshalRecord);
            xmlReader.setLexicalHandler(unmarshalRecord);
            xmlReader.parse(inputSource);
        } catch (IOException e) {
            throw XMLMarshalException.unmarshalException(e);
        } catch (SAXException e) {
            throw convertSAXException(e);
        } finally {
        	xmlUnmarshaller.getStringBuffer().reset();
        }

        // resolve mapping references
        unmarshalRecord.resolveReferences(session, xmlUnmarshaller.getIDResolver());

        if (isPrimitiveWrapper) {
            return unmarshalRecord.getCurrentObject();
        }
        return xmlDescriptor.wrapObjectInXMLRoot(unmarshalRecord, this.isResultAlwaysXMLRoot);
    }

    public Object unmarshal(Node node) {
        DOMReader reader = new DOMReader(xmlUnmarshaller);
        return unmarshal(reader, node);
    }

    public Object unmarshal(DOMReader reader, Node node) {
        try {
            SAXUnmarshallerHandler handler = new SAXUnmarshallerHandler(xmlUnmarshaller.getContext());
            setContentHandler(reader, handler);
            handler.setXMLReader(reader);
            handler.setUnmarshaller(xmlUnmarshaller);
            reader.parse(node);


            handler.resolveReferences();
            return handler.getObject();
        } catch (SAXException e) {
            throw convertSAXException(e);
        } finally {
        	xmlUnmarshaller.getStringBuffer().reset();
        }

    }

    public Object unmarshal(Node node, Class clazz) {
        DOMReader reader = new DOMReader(xmlUnmarshaller);
        return unmarshal(reader, node, clazz);
    }

    public Object unmarshal(DOMReader domReader, Node node, Class clazz) {
        UnmarshalRecord unmarshalRecord = null;
        Descriptor xmlDescriptor = null;

        CoreAbstractSession session = null;

        // check for case where the reference class is a primitive wrapper - in this case, we 
        // need to use the conversion manager to convert the node's value to the primitive 
        // wrapper class, then create, populate and return an XMLRoot.  This will be done
        // via XMLRootRecord.
        boolean isPrimitiveWrapper = false;
if(clazz == CoreClassConstants.OBJECT) {
            SAXUnmarshallerHandler saxUnmarshallerHandler = new SAXUnmarshallerHandler(xmlUnmarshaller.getContext());
            saxUnmarshallerHandler.setXMLReader(domReader);
            saxUnmarshallerHandler.setUnmarshaller(xmlUnmarshaller);
            saxUnmarshallerHandler.setKeepAsElementPolicy(KEEP_UNKNOWN_AS_ELEMENT);
            setContentHandler(domReader, saxUnmarshallerHandler);
            try{
                domReader.parse(node);
            } catch (SAXException e) {
                throw convertSAXException(e);
            }

            // resolve any mapping references
            saxUnmarshallerHandler.resolveReferences();
            return saxUnmarshallerHandler.getObject();
        } else {
            // for XMLObjectReferenceMappings we need a non-shared cache, so
            // try and get a Unit Of Work from the XMLContext
            try{
                session = xmlUnmarshaller.getContext().getSession(clazz);
                xmlDescriptor = (Descriptor) session.getDescriptor(clazz);
                unmarshalRecord = xmlUnmarshaller.createUnmarshalRecord(xmlDescriptor, session);
            }catch(XMLMarshalException xme){
                if(xme.getErrorCode() == XMLMarshalException.DESCRIPTOR_NOT_FOUND_IN_PROJECT){            			 
                    isPrimitiveWrapper = isPrimitiveWrapper(clazz);
                    if (isPrimitiveWrapper) {
                        unmarshalRecord = xmlUnmarshaller.createRootUnmarshalRecord(clazz);
                    }else if (Node.class.isAssignableFrom(clazz)) {
                          return createXMLRootForNode(node);
                    }else{
                        throw xme;
                    }
                    
                }else{
                    throw xme;
                }
               
            }
        }
        try {
            unmarshalRecord.setXMLReader(domReader);
            unmarshalRecord.setUnmarshaller(xmlUnmarshaller);
            setContentHandler(domReader, unmarshalRecord);
            domReader.setLexicalHandler(unmarshalRecord);
            domReader.parse(node);
        } catch (SAXException e) {
            throw convertSAXException(e);
        } finally {
        	xmlUnmarshaller.getStringBuffer().reset();
        }

        // resolve mapping references
        unmarshalRecord.resolveReferences(session, xmlUnmarshaller.getIDResolver());

        if (isPrimitiveWrapper) {
            return unmarshalRecord.getCurrentObject();
        }
        return xmlDescriptor.wrapObjectInXMLRoot(unmarshalRecord, this.isResultAlwaysXMLRoot);
    }
    
    private Object createXMLRootForNode(Node node) {
        Root xmlRoot = xmlUnmarshaller.createRoot();
    	xmlRoot.setObject(node);
    	if (node != null) {
    	    xmlRoot.setLocalName(node.getLocalName());
    	    xmlRoot.setNamespaceURI(node.getNamespaceURI());
    	}
    	return xmlRoot;
    }

    public Object unmarshal(Reader reader) {
        if (xmlUnmarshaller.getContext().hasDocumentPreservation()) {
            Node domElement = getXMLParser().parse(reader).getDocumentElement();
            return unmarshal(domElement);
        }
        InputSource inputSource = new InputSource(reader);
        return unmarshal(inputSource);
    }

    public Object unmarshal(Reader reader, Class clazz) {
        if (xmlUnmarshaller.getContext().hasDocumentPreservation()) {
            Node domElement = getXMLParser().parse(reader).getDocumentElement();
            return unmarshal(domElement, clazz);
        }
        InputSource inputSource = new InputSource(reader);
        return unmarshal(inputSource, clazz);
    }

    public Object unmarshal(Source source) {    	
    	try{
	        if (source instanceof SAXSource) {
	            SAXSource saxSource = (SAXSource) source;
	            XMLReader xmlReader = null;
	            if (saxSource.getXMLReader() != null) {
	                if(saxSource.getXMLReader() instanceof XMLReader) {
	                    xmlReader = (XMLReader) saxSource.getXMLReader();
	                } else {
	                    xmlReader = new XMLReader(saxSource.getXMLReader());
	                }
	                setValidatorHandler(xmlReader);
	            }
	            if (null == xmlReader) {
	                return unmarshal(saxSource.getInputSource());
	            } else {
	                return unmarshal(saxSource.getInputSource(), xmlReader);
	            }
	        } else if (source instanceof DOMSource) {
	            DOMSource domSource = (DOMSource) source;
	            return unmarshal(domSource.getNode());
	        } else if (source instanceof StreamSource) {
	            StreamSource streamSource = (StreamSource) source;
	            if (null != streamSource.getReader()) {
	                return unmarshal(streamSource.getReader());
	            } else if (null != streamSource.getInputStream()) {
	                return unmarshal(streamSource.getInputStream());
	            } else {
	                return unmarshal(streamSource.getSystemId());
	            }
	        } else {
	        	UnmarshallerHandler handler = this.xmlUnmarshaller.getUnmarshallerHandler();
	        	XMLTransformer transformer = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLTransformer();
	        	SAXResult result = new SAXResult(handler);
	        	transformer.transform(source, result);
	        	return handler.getResult();        	
	        }
    	}finally {
        	xmlUnmarshaller.getStringBuffer().reset();
        }
    }

    public Object unmarshal(Source source, Class clazz) {
        if (source instanceof SAXSource) {
            SAXSource saxSource = (SAXSource) source;
            XMLReader xmlReader = null;
            if (saxSource.getXMLReader() != null) {
                if(saxSource.getXMLReader() instanceof XMLReader) {
                    xmlReader = (XMLReader) saxSource.getXMLReader();
                } else {
                    xmlReader = new XMLReader(saxSource.getXMLReader());
                }
                setValidatorHandler(xmlReader);
            }
            if (null == saxSource.getXMLReader()) {
                return unmarshal(saxSource.getInputSource(), clazz);
            } else {
                return unmarshal(saxSource.getInputSource(), clazz, xmlReader);
            }
        } else if (source instanceof DOMSource) {
            DOMSource domSource = (DOMSource) source;           
            return unmarshal(domSource.getNode(), clazz);
        } else if (source instanceof StreamSource) {
            StreamSource streamSource = (StreamSource) source;
            if (null != streamSource.getReader()) {
          	return unmarshal(streamSource.getReader(), clazz);	
            } else if (null != streamSource.getInputStream()) {
            	return unmarshal(streamSource.getInputStream(), clazz);
            } else {
            	return unmarshal(streamSource.getSystemId(), clazz);
            }
        } else {
        	DOMResult result = new DOMResult();
        	XMLTransformer transformer = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLTransformer();
        	transformer.transform(source, result);
        	return unmarshal(result.getNode(), clazz);
        	
        }
    }

    public Object unmarshal(URL url) {
        InputStream inputStream = null;
        try {
            inputStream = url.openStream();
        } catch (Exception e) {
            throw XMLMarshalException.unmarshalException(e);
        }

        this.systemId = url.toExternalForm();

        boolean hasThrownException = false;
        try {
            return unmarshal(inputStream);
        } catch (RuntimeException runtimeException) {
            hasThrownException = true;
            throw runtimeException;
        } finally {
        	xmlUnmarshaller.getStringBuffer().reset();
            try {
                inputStream.close();                              
            } catch (IOException e) {
                if (!hasThrownException) {
                    throw XMLMarshalException.unmarshalException(e);
                }
            }
        }
    }

    public Object unmarshal(URL url, Class clazz) {
    	InputStream inputStream = null;
        try {
            inputStream = url.openStream();
        }catch (IOException e) {
            throw XMLMarshalException.unmarshalException(e);
        }            
        this.systemId = url.toExternalForm();
        try {
            return unmarshal(inputStream, clazz);                        
        }  finally {
        	xmlUnmarshaller.getStringBuffer().reset();
        	try{
        	    inputStream.close();
        	}catch (IOException e) {
                throw XMLMarshalException.unmarshalException(e);
            }
        }
    }

    public Object unmarshal(String systemId) {
        try {        	
            if(xmlUnmarshaller.isAutoDetectMediaType()){
            	InputSource inputSource = new InputSource(systemId);                
                return unmarshal(inputSource);        	     	
            }
        	
            XMLReader xmlReader = getXMLReader();
            SAXUnmarshallerHandler saxUnmarshallerHandler = new SAXUnmarshallerHandler(xmlUnmarshaller.getContext());
            saxUnmarshallerHandler.setXMLReader(xmlReader);
            saxUnmarshallerHandler.setUnmarshaller(xmlUnmarshaller);
            setContentHandler(xmlReader, saxUnmarshallerHandler);
            xmlReader.parse(systemId);

            // resolve mapping references
            saxUnmarshallerHandler.resolveReferences();
            return saxUnmarshallerHandler.getObject();
        } catch (IOException e) {
            throw XMLMarshalException.unmarshalException(e);
        } catch (SAXException e) {
            throw convertSAXException(e);
        } finally {
        	xmlUnmarshaller.getStringBuffer().reset();
        }
    }

    public Object unmarshal(String systemId, Class clazz) {
    	if(xmlUnmarshaller.isAutoDetectMediaType()){   
            return unmarshal(new InputSource(systemId), clazz);        	     	
        }
    
        UnmarshalRecord unmarshalRecord = null;
        boolean isPrimitiveWrapper = false;
        Descriptor xmlDescriptor = null;

        CoreAbstractSession session = null;

        // check for case where the reference class is a primitive wrapper - in this case, we 
        // need to use the conversion manager to convert the node's value to the primitive 
        // wrapper class, then create, populate and return an XMLRoot.  This will be done
        // via XMLRootRecord.
if(clazz == CoreClassConstants.OBJECT) {
           
            SAXUnmarshallerHandler saxUnmarshallerHandler = new SAXUnmarshallerHandler(xmlUnmarshaller.getContext());
            try {
                XMLReader xmlReader = getXMLReader(clazz);
                saxUnmarshallerHandler.setXMLReader(xmlReader);
                saxUnmarshallerHandler.setUnmarshaller(xmlUnmarshaller);
                saxUnmarshallerHandler.setKeepAsElementPolicy(KEEP_UNKNOWN_AS_ELEMENT);
                setContentHandler(xmlReader, saxUnmarshallerHandler);
                xmlReader.parse(systemId);
            } catch (IOException e) {
                throw XMLMarshalException.unmarshalException(e);
            } catch (SAXException e) {
                throw convertSAXException(e);
            } finally {
            	xmlUnmarshaller.getStringBuffer().reset();
            }
            // resolve any mapping references
            saxUnmarshallerHandler.resolveReferences();
            return saxUnmarshallerHandler.getObject();
        } else {
            // for XMLObjectReferenceMappings we need a non-shared cache, so
            // try and get a Unit Of Work from the XMLContext
        	try{
            session = xmlUnmarshaller.getContext().getSession(clazz);
            xmlDescriptor = (Descriptor) session.getDescriptor(clazz);
            unmarshalRecord = xmlUnmarshaller.createUnmarshalRecord(xmlDescriptor, session);
        	}catch(XMLMarshalException xme){
        		if(xme.getErrorCode() == XMLMarshalException.DESCRIPTOR_NOT_FOUND_IN_PROJECT){            			 
 	                isPrimitiveWrapper = isPrimitiveWrapper(clazz);
 	                if (isPrimitiveWrapper) {
 	                       unmarshalRecord = xmlUnmarshaller.createRootUnmarshalRecord(clazz);
 	                }else{
 	                	throw xme;
 	                }
 	                
        		}else{
        		   throw xme;
        		}
        		
        	}
            
        }

        try {
            XMLReader xmlReader = getXMLReader(clazz);
            unmarshalRecord.setXMLReader(xmlReader);
            unmarshalRecord.setUnmarshaller(xmlUnmarshaller);
            setContentHandler(xmlReader, unmarshalRecord);
            xmlReader.setLexicalHandler(unmarshalRecord);
            xmlReader.parse(systemId);
        } catch (IOException e) {
            throw XMLMarshalException.unmarshalException(e);
        } catch (SAXException e) {
            throw convertSAXException(e);
        } finally {
        	xmlUnmarshaller.getStringBuffer().reset();
        }

        // resolve mapping references
        unmarshalRecord.resolveReferences(session, xmlUnmarshaller.getIDResolver());
        

        if (isPrimitiveWrapper) {
            return unmarshalRecord.getCurrentObject();
        }
        return xmlDescriptor.wrapObjectInXMLRoot(unmarshalRecord, this.isResultAlwaysXMLRoot);
    }

    public Object unmarshal(org.xml.sax.XMLReader xmlReader, InputSource inputSource) {
        try {
            Context xmlContext = xmlUnmarshaller.getContext();
            if (xmlContext.hasDocumentPreservation()) {
                SAXDocumentBuilder saxDocumentBuilder = new SAXDocumentBuilder();
                xmlReader.setContentHandler(saxDocumentBuilder);
                xmlReader.parse(inputSource);                
                return unmarshal(saxDocumentBuilder.getDocument().getDocumentElement());
            }
            XMLReader extendedXMLReader;
            if(xmlReader instanceof XMLReader) {
                extendedXMLReader = (XMLReader) xmlReader;
            } else {
                extendedXMLReader = new XMLReader(xmlReader);
            }
            SAXUnmarshallerHandler saxUnmarshallerHandler = new SAXUnmarshallerHandler(xmlContext);
            saxUnmarshallerHandler.setXMLReader(extendedXMLReader);
            saxUnmarshallerHandler.setUnmarshaller(xmlUnmarshaller);
            setContentHandler(extendedXMLReader, saxUnmarshallerHandler);
            extendedXMLReader.parse(inputSource);

            // resolve any mapping references
            saxUnmarshallerHandler.resolveReferences();
            return saxUnmarshallerHandler.getObject();
        } catch (IOException e) {
            throw XMLMarshalException.unmarshalException(e);
        } catch (SAXException e) {
            throw convertSAXException(e);
        } finally {
        	xmlUnmarshaller.getStringBuffer().reset();
        }
    }

    public Object unmarshal(org.xml.sax.XMLReader xmlReader, InputSource inputSource, Class clazz) {
        try {
            Context xmlContext = xmlUnmarshaller.getContext();

            if (xmlContext.hasDocumentPreservation() || (Node.class.isAssignableFrom(clazz) && xmlUnmarshaller.getMediaType().isApplicationXML())) {
                SAXDocumentBuilder saxDocumentBuilder = new SAXDocumentBuilder();
                xmlReader.setContentHandler(saxDocumentBuilder);
                xmlReader.parse(inputSource);
                return unmarshal(saxDocumentBuilder.getDocument().getDocumentElement(), clazz);
            }
            
            UnmarshalRecord unmarshalRecord = null;
            Descriptor xmlDescriptor = null;

            CoreAbstractSession session = null;
            boolean isPrimitiveWrapper = false;
            // check for case where the reference class is a primitive wrapper - in this case, we 
            // need to use the conversion manager to convert the node's value to the primitive 
            // wrapper class, then create, populate and return an XMLRoot.  This will be done
            // via XMLRootRecord.
if(clazz == CoreClassConstants.OBJECT) {
                SAXUnmarshallerHandler saxUnmarshallerHandler = new SAXUnmarshallerHandler(xmlUnmarshaller.getContext());
                saxUnmarshallerHandler.setXMLReader((XMLReader)xmlReader);
                saxUnmarshallerHandler.setUnmarshaller(xmlUnmarshaller);
                saxUnmarshallerHandler.setKeepAsElementPolicy(KEEP_UNKNOWN_AS_ELEMENT);
                xmlReader.setContentHandler(saxUnmarshallerHandler);
                xmlReader.parse(inputSource);

                // resolve any mapping references
                saxUnmarshallerHandler.resolveReferences();
                return saxUnmarshallerHandler.getObject();
            } else {
                // for XMLObjectReferenceMappings we need a non-shared cache, so
                // try and get a Unit Of Work from the XMLContext
            	try{
                session = xmlContext.getSession(clazz);
                xmlDescriptor = (Descriptor) session.getDescriptor(clazz);
                unmarshalRecord = xmlUnmarshaller.createUnmarshalRecord(xmlDescriptor, session); 
            	}catch(XMLMarshalException xme){            		
            		if(xme.getErrorCode() == XMLMarshalException.DESCRIPTOR_NOT_FOUND_IN_PROJECT){            			 
     	                isPrimitiveWrapper = isPrimitiveWrapper(clazz);
     	                if (isPrimitiveWrapper) {
     	                       unmarshalRecord = xmlUnmarshaller.createRootUnmarshalRecord(clazz);
     	                }else{
     	                	throw xme;
     	                }
     	                
            		}else{
            		   throw xme;
            		}
	               
            	}
            }
            XMLReader extendedXMLReader;
            if(xmlReader instanceof XMLReader) {
                extendedXMLReader = (XMLReader) xmlReader;
            } else {
                extendedXMLReader = new XMLReader(xmlReader);
            }
            unmarshalRecord.setXMLReader(extendedXMLReader);
            unmarshalRecord.setUnmarshaller(xmlUnmarshaller);
            setContentHandler(extendedXMLReader, unmarshalRecord);
            extendedXMLReader.setLexicalHandler(unmarshalRecord);
            extendedXMLReader.parse(inputSource);

            // resolve mapping references
            unmarshalRecord.resolveReferences(session, xmlUnmarshaller.getIDResolver());

            if (isPrimitiveWrapper || clazz == CoreClassConstants.OBJECT) {
                return unmarshalRecord.getCurrentObject();
            }
            return xmlDescriptor.wrapObjectInXMLRoot(unmarshalRecord, this.isResultAlwaysXMLRoot);
        } catch (IOException e) {
            throw XMLMarshalException.unmarshalException(e);
        } catch (SAXException e) {
            throw convertSAXException(e);
        } finally {
        	xmlUnmarshaller.getStringBuffer().reset();
        }
    }

    private EclipseLinkException convertSAXException(SAXException saxException) {
        Exception internalException = saxException.getException();
        if (internalException != null) {
            if (EclipseLinkException.class.isAssignableFrom(internalException.getClass())) {
                return (EclipseLinkException) internalException;
            } else {
                return XMLMarshalException.unmarshalException(internalException);
            }
        }
        return XMLMarshalException.unmarshalException(saxException);
    }

    public boolean isResultAlwaysXMLRoot() {
        return this.isResultAlwaysXMLRoot;
    }

    public void setResultAlwaysXMLRoot(boolean alwaysReturnRoot) {
        this.isResultAlwaysXMLRoot = alwaysReturnRoot;
    }
    
    private boolean isPrimitiveWrapper(Class clazz){
	    return  XMLConversionManager.getDefaultJavaTypes().get(clazz) != null    
	    ||CoreClassConstants.XML_GREGORIAN_CALENDAR.isAssignableFrom(clazz)
	    ||CoreClassConstants.DURATION.isAssignableFrom(clazz);
    }

    /**
     * If a Schema was set on the unmarshaller then wrap the ContentHandler in
     * a ValidatorHandler.
     */
    private void setContentHandler(XMLReader xmlReader, ContentHandler contentHandler) {
        setValidatorHandler(xmlReader);
        xmlReader.setContentHandler(contentHandler);
    }
    
    private void setValidatorHandler(XMLReader xmlReader) {
        Schema schema = getSchema();
        if (null != schema) {
            ValidatorHandler validatorHandler = schema.newValidatorHandler();
            xmlReader.setValidatorHandler(validatorHandler);
            validatorHandler.setErrorHandler(getErrorHandler());
        }
    }

	@Override
	public void mediaTypeChanged() {
         xmlReader = null;		
	}

    private InputStream getInputStreamFromString(String stringValue){
   	    if(stringValue.length() == 0) {
   	    	throw org.eclipse.persistence.exceptions.XMLMarshalException.unmarshalFromStringException(stringValue, null);
        }
        URL url = null;
        try {
            url = new URL(stringValue);
            if(url != null){
                try {
                   return url.openStream();
                } catch (IOException e) {
              	  	throw org.eclipse.persistence.exceptions.XMLMarshalException.unmarshalFromStringException(stringValue, e);
                }
            }   
        } catch(MalformedURLException ex) {     
            try {
                  return new FileInputStream(stringValue);
            } catch (FileNotFoundException e) {            
            	throw org.eclipse.persistence.exceptions.XMLMarshalException.unmarshalFromStringException(stringValue, e);
            }          
        }		
	    throw org.eclipse.persistence.exceptions.XMLMarshalException.unmarshalFromStringException(stringValue, null);
   }

	
	private BufferedReader getBufferedReaderForInputSource(InputSource inputSource){
   	    if(inputSource.getByteStream() != null){                	
         	return new BufferedReader(new InputStreamReader(inputSource.getByteStream()));
        }else if(inputSource.getCharacterStream() != null){
        	return new BufferedReader(inputSource.getCharacterStream());
        }else if (inputSource.getSystemId() != null){        	
        	InputStream is = getInputStreamFromString(inputSource.getSystemId());        	
			return new BufferedReader(new InputStreamReader(is));			
        }   
   	    throw XMLMarshalException.unmarshalException();
	}
	
	private MediaType getMediaType(BufferedReader br) {
	    	int READ_AHEAD_LIMIT = 25;
	    	try{
	    	    br.mark(READ_AHEAD_LIMIT);
		    	try {
		            char c = 0;
		            for (int i = 0; c != -1 && i < READ_AHEAD_LIMIT; i++) {
		                c = (char) br.read();
		                if (c == '[' || c == '{') {
		                    return Constants.APPLICATION_JSON;
		                }else if (c == '<'){
		                	return Constants.APPLICATION_XML;	
		                }
		                
		            }
		            
		        } finally {	            
						br.reset();				
		        }
	    	}catch(IOException ioException){
	    		throw XMLMarshalException.unmarshalException(ioException);
	    	}	    
	    	return xmlUnmarshaller.getMediaType();
	    }
}