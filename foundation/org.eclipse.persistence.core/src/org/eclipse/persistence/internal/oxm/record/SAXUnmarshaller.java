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
package org.eclipse.persistence.internal.oxm.record;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.ValidatorHandler;

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.oxm.record.XMLRootRecord;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.platform.xml.DefaultErrorHandler;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.oxm.XMLUnmarshallerHandler;
import org.eclipse.persistence.platform.xml.XMLTransformer;

import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.eclipse.persistence.internal.oxm.record.XMLReader;

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
    private int validationMode;
    private Object[] schemas;
    private SAXParser saxParser;
    private XMLReader xmlReader;
    private XMLUnmarshaller xmlUnmarshaller;
    private XMLParser xmlParser;
    private boolean isResultAlwaysXMLRoot;
    private SAXParserFactory saxParserFactory;

    public SAXUnmarshaller(XMLUnmarshaller xmlUnmarshaller, Map<String, Boolean> parserFeatures) throws XMLMarshalException {
        super();
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

            saxParser = saxParserFactory.newSAXParser();

            xmlReader = new XMLReader(saxParser.getXMLReader());
            xmlReader.setErrorHandler(new DefaultErrorHandler());
            xmlParser = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLParser();
            xmlParser.setNamespaceAware(true);
            xmlParser.setValidationMode(XMLParser.NONVALIDATING);

            this.xmlUnmarshaller = xmlUnmarshaller;
        } catch (Exception e) {
            throw XMLMarshalException.errorInstantiatingSchemaPlatform(e);
        }
    }

    public EntityResolver getEntityResolver() {
        return xmlReader.getEntityResolver();
    }

    public void setEntityResolver(EntityResolver entityResolver) {
        xmlReader.setEntityResolver(entityResolver);
        xmlParser.setEntityResolver(entityResolver);
    }

    public ErrorHandler getErrorHandler() {
        return xmlParser.getErrorHandler();
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        xmlReader.setErrorHandler(errorHandler);
        xmlParser.setErrorHandler(errorHandler);
    }

    public int getValidationMode() {
        return validationMode;
    }

    public void setValidationMode(int validationMode) {
        try {
            this.validationMode = validationMode;
            xmlParser.setValidationMode(validationMode);
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
        xmlParser.setWhitespacePreserving(isWhitespacePreserving);
    }

    public void setSchemas(Object[] schemas) {
        this.schemas = schemas;
    }

    public void setSchema(Schema schema) {
        xmlParser.setXMLSchema(schema);
        saxParserFactory.setSchema(schema);
        try {
            saxParser = saxParserFactory.newSAXParser();
            XMLReader newXmlReader = new XMLReader(saxParser.getXMLReader());
            newXmlReader.setFeature(VALIDATING, xmlReader.getFeature(VALIDATING));
            newXmlReader.setEntityResolver(xmlReader.getEntityResolver());
            newXmlReader.setErrorHandler(xmlReader.getErrorHandler());
            xmlReader = newXmlReader;
            xmlParser.setXMLSchema(schema);
        } catch (Exception e) {
            throw XMLMarshalException.errorInstantiatingSchemaPlatform(e);
        }
    }
    
    public Schema getSchema() {
        Schema schema = null;
        try {
            schema = xmlParser.getXMLSchema();
        } catch(UnsupportedOperationException ex) {
            //if this parser doesn't support the setSchema/getSchema API, just return null;
        }
        return schema;
    }
    
    public Object unmarshal(File file) {
        try {
            if (xmlUnmarshaller.getXMLContext().hasDocumentPreservation()) {
                Node domElement = xmlParser.parse(file).getDocumentElement();
                return unmarshal(domElement);
            }
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
        }
    }

    public Object unmarshal(File file, Class clazz) {
        try {
            if (xmlUnmarshaller.getXMLContext().hasDocumentPreservation()) {
                Node domElement = xmlParser.parse(file).getDocumentElement();
                return unmarshal(domElement, clazz);
            }
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
        }
    }

    public Object unmarshal(InputStream inputStream) {
        if (xmlUnmarshaller.getXMLContext().hasDocumentPreservation()) {
            Node domElement = xmlParser.parse(inputStream).getDocumentElement();
            return unmarshal(domElement);
        }
        InputSource inputSource = new InputSource(inputStream);
        return unmarshal(inputSource);
    }

    public Object unmarshal(InputStream inputStream, Class clazz) {
        if (xmlUnmarshaller.getXMLContext().hasDocumentPreservation()) {
            Node domElement = xmlParser.parse(inputStream).getDocumentElement();
            return unmarshal(domElement, clazz);
        }
        InputSource inputSource = new InputSource(inputStream);
        return unmarshal(inputSource, clazz);
    }

    public Object unmarshal(InputSource inputSource) {
        return unmarshal(xmlReader, inputSource);
    }

    public Object unmarshal(InputSource inputSource, XMLReader xmlReader) {
        try {
            SAXUnmarshallerHandler saxUnmarshallerHandler = new SAXUnmarshallerHandler(xmlUnmarshaller.getXMLContext());
            saxUnmarshallerHandler.setXMLReader(xmlReader);
            saxUnmarshallerHandler.setUnmarshaller(xmlUnmarshaller);
            xmlReader.setContentHandler(saxUnmarshallerHandler);
            xmlReader.parse(inputSource);

            // resolve any mapping references
            saxUnmarshallerHandler.resolveReferences();
            return saxUnmarshallerHandler.getObject();
        } catch (IOException e) {
            throw XMLMarshalException.unmarshalException(e);
        } catch (SAXException e) {
            throw convertSAXException(e);
        }
    }

    public Object unmarshal(InputSource inputSource, Class clazz) {
        return unmarshal(xmlReader, inputSource, clazz);
    }

    public Object unmarshal(InputSource inputSource, Class clazz, XMLReader xmlReader) {
        boolean isPrimitiveWrapper = isPrimitiveWrapper(clazz); 
        
        UnmarshalRecord unmarshalRecord;
        XMLDescriptor xmlDescriptor = null;

        // for XMLObjectReferenceMappings we need a non-shared cache, so
        // try and get a Unit Of Work from the XMLContext
        AbstractSession session = null;

        // check for case where the reference class is a primitive wrapper - in this case, we 
        // need to use the conversion manager to convert the node's value to the primitive 
        // wrapper class, then create, populate and return an XMLRoot.  This will be done
        // via XMLRootRecord.
        if (isPrimitiveWrapper) {
            unmarshalRecord = new XMLRootRecord(clazz);
            unmarshalRecord.setSession((AbstractSession) xmlUnmarshaller.getXMLContext().getSession(0));
        } else if(clazz == ClassConstants.OBJECT) {
    	    try{
                SAXUnmarshallerHandler saxUnmarshallerHandler = new SAXUnmarshallerHandler(xmlUnmarshaller.getXMLContext());
                saxUnmarshallerHandler.setXMLReader(xmlReader);
                saxUnmarshallerHandler.setUnmarshaller(xmlUnmarshaller);
                saxUnmarshallerHandler.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
                xmlReader.setContentHandler(saxUnmarshallerHandler);
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
            session = xmlUnmarshaller.getXMLContext().getReadSession(clazz);
            xmlDescriptor = (XMLDescriptor) session.getDescriptor(clazz);
            unmarshalRecord = (UnmarshalRecord) xmlDescriptor.getObjectBuilder().createRecord(session);
        }

        try {
            unmarshalRecord.setXMLReader(xmlReader);
            unmarshalRecord.setUnmarshaller(xmlUnmarshaller);
            xmlReader.setContentHandler(unmarshalRecord);
            xmlReader.setLexicalHandler(unmarshalRecord);
            xmlReader.parse(inputSource);
        } catch (IOException e) {
            throw XMLMarshalException.unmarshalException(e);
        } catch (SAXException e) {
            throw convertSAXException(e);
        }

        // resolve mapping references
        xmlUnmarshaller.resolveReferences(session);

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
            SAXUnmarshallerHandler handler = new SAXUnmarshallerHandler(xmlUnmarshaller.getXMLContext());
            reader.setContentHandler(handler);
            handler.setXMLReader(reader);
            handler.setUnmarshaller(xmlUnmarshaller);
            reader.parse(node);


            handler.resolveReferences();
            return handler.getObject();
        } catch (SAXException e) {
            throw convertSAXException(e);
        }

    }

    public Object unmarshal(Node node, Class clazz) {
        DOMReader reader = new DOMReader(xmlUnmarshaller);
        return unmarshal(reader, node, clazz);
    }

    public Object unmarshal(DOMReader domReader, Node node, Class clazz) {
        boolean isPrimitiveWrapper = isPrimitiveWrapper(clazz);
        UnmarshalRecord unmarshalRecord;
        XMLDescriptor xmlDescriptor = null;

        AbstractSession session = null;

        // check for case where the reference class is a primitive wrapper - in this case, we 
        // need to use the conversion manager to convert the node's value to the primitive 
        // wrapper class, then create, populate and return an XMLRoot.  This will be done
        // via XMLRootRecord.
        if (isPrimitiveWrapper) {
            unmarshalRecord = new XMLRootRecord(clazz);
            unmarshalRecord.setSession((AbstractSession)xmlUnmarshaller.getXMLContext().getSession(0));
        } else if(clazz == ClassConstants.OBJECT) {
            SAXUnmarshallerHandler saxUnmarshallerHandler = new SAXUnmarshallerHandler(xmlUnmarshaller.getXMLContext());
            saxUnmarshallerHandler.setXMLReader(domReader);
            saxUnmarshallerHandler.setUnmarshaller(xmlUnmarshaller);
            saxUnmarshallerHandler.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);          
            domReader.setContentHandler(saxUnmarshallerHandler);
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
            session = xmlUnmarshaller.getXMLContext().getReadSession(clazz);
            xmlDescriptor = (XMLDescriptor) session.getDescriptor(clazz);
            unmarshalRecord = (UnmarshalRecord) xmlDescriptor.getObjectBuilder().createRecord(session);
        }
        try {
            unmarshalRecord.setXMLReader(domReader);
            unmarshalRecord.setUnmarshaller(xmlUnmarshaller);
            domReader.setContentHandler(unmarshalRecord);
            domReader.setLexicalHandler(unmarshalRecord);
            domReader.parse(node);
        } catch (SAXException e) {
            throw convertSAXException(e);
        }

        // resolve mapping references
        xmlUnmarshaller.resolveReferences(session);

        if (isPrimitiveWrapper) {
            return unmarshalRecord.getCurrentObject();
        }
        return xmlDescriptor.wrapObjectInXMLRoot(unmarshalRecord, this.isResultAlwaysXMLRoot);
    }

    public Object unmarshal(Reader reader) {
        if (xmlUnmarshaller.getXMLContext().hasDocumentPreservation()) {
            Node domElement = xmlParser.parse(reader).getDocumentElement();
            return unmarshal(domElement);
        }
        InputSource inputSource = new InputSource(reader);
        return unmarshal(inputSource);
    }

    public Object unmarshal(Reader reader, Class clazz) {
        if (xmlUnmarshaller.getXMLContext().hasDocumentPreservation()) {
            Node domElement = xmlParser.parse(reader).getDocumentElement();
            return unmarshal(domElement, clazz);
        }
        InputSource inputSource = new InputSource(reader);
        return unmarshal(inputSource, clazz);
    }

    public Object unmarshal(Source source) {
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
        	XMLUnmarshallerHandler handler = this.xmlUnmarshaller.getUnmarshallerHandler();
        	XMLTransformer transformer = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLTransformer();
        	SAXResult result = new SAXResult(handler);
        	transformer.transform(source, result);
        	return handler.getResult();
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

        boolean hasThrownException = false;
        try {
            return unmarshal(inputStream);
        } catch (RuntimeException runtimeException) {
            hasThrownException = true;
            throw runtimeException;
        } finally {
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
        try {
            InputStream inputStream = url.openStream();
            Object result = unmarshal(inputStream, clazz);
            inputStream.close();
            return result;
        } catch (IOException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
    }

    public Object unmarshal(String systemId) {
        try {
            SAXUnmarshallerHandler saxUnmarshallerHandler = new SAXUnmarshallerHandler(xmlUnmarshaller.getXMLContext());
            saxUnmarshallerHandler.setXMLReader(xmlReader);
            saxUnmarshallerHandler.setUnmarshaller(xmlUnmarshaller);
            xmlReader.setContentHandler(saxUnmarshallerHandler);
            xmlReader.parse(systemId);

            // resolve mapping references
            saxUnmarshallerHandler.resolveReferences();
            return saxUnmarshallerHandler.getObject();
        } catch (IOException e) {
            throw XMLMarshalException.unmarshalException(e);
        } catch (SAXException e) {
            throw convertSAXException(e);
        }
    }

    public Object unmarshal(String systemId, Class clazz) {
        boolean isPrimitiveWrapper = isPrimitiveWrapper(clazz);
        UnmarshalRecord unmarshalRecord;
        XMLDescriptor xmlDescriptor = null;

        AbstractSession session = null;

        // check for case where the reference class is a primitive wrapper - in this case, we 
        // need to use the conversion manager to convert the node's value to the primitive 
        // wrapper class, then create, populate and return an XMLRoot.  This will be done
        // via XMLRootRecord.
        if (isPrimitiveWrapper) {
            unmarshalRecord = new XMLRootRecord(clazz);
        } else if(clazz == ClassConstants.OBJECT) {
           
            SAXUnmarshallerHandler saxUnmarshallerHandler = new SAXUnmarshallerHandler(xmlUnmarshaller.getXMLContext());
            try {
            	saxUnmarshallerHandler.setXMLReader(xmlReader);
            	saxUnmarshallerHandler.setUnmarshaller(xmlUnmarshaller);
            	saxUnmarshallerHandler.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
                xmlReader.setContentHandler(saxUnmarshallerHandler);
                xmlReader.parse(systemId);
            } catch (IOException e) {
                throw XMLMarshalException.unmarshalException(e);
            } catch (SAXException e) {
                throw convertSAXException(e);
            }
            // resolve any mapping references
            saxUnmarshallerHandler.resolveReferences();
            return saxUnmarshallerHandler.getObject();
        } else {
            // for XMLObjectReferenceMappings we need a non-shared cache, so
            // try and get a Unit Of Work from the XMLContext
            session = xmlUnmarshaller.getXMLContext().getReadSession(clazz);
            xmlDescriptor = (XMLDescriptor) session.getDescriptor(clazz);
            unmarshalRecord = (UnmarshalRecord) xmlDescriptor.getObjectBuilder().createRecord(session);
        }

        try {
            unmarshalRecord.setXMLReader(xmlReader);
            unmarshalRecord.setUnmarshaller(xmlUnmarshaller);
            xmlReader.setContentHandler(unmarshalRecord);
            xmlReader.setLexicalHandler(unmarshalRecord);
            xmlReader.parse(systemId);
        } catch (IOException e) {
            throw XMLMarshalException.unmarshalException(e);
        } catch (SAXException e) {
            throw convertSAXException(e);
        }

        // resolve mapping references
        xmlUnmarshaller.resolveReferences(session);

        if (isPrimitiveWrapper) {
            return unmarshalRecord.getCurrentObject();
        }
        return xmlDescriptor.wrapObjectInXMLRoot(unmarshalRecord, this.isResultAlwaysXMLRoot);
    }

    public Object unmarshal(org.xml.sax.XMLReader xmlReader, InputSource inputSource) {
        try {
            XMLContext xmlContext = xmlUnmarshaller.getXMLContext();
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
            extendedXMLReader.setContentHandler(saxUnmarshallerHandler);
            extendedXMLReader.parse(inputSource);

            // resolve any mapping references
            saxUnmarshallerHandler.resolveReferences();
            return saxUnmarshallerHandler.getObject();
        } catch (IOException e) {
            throw XMLMarshalException.unmarshalException(e);
        } catch (SAXException e) {
            throw convertSAXException(e);
        }
    }

    public Object unmarshal(org.xml.sax.XMLReader xmlReader, InputSource inputSource, Class clazz) {
        try {
            XMLContext xmlContext = xmlUnmarshaller.getXMLContext();

            if (xmlContext.hasDocumentPreservation()) {
                SAXDocumentBuilder saxDocumentBuilder = new SAXDocumentBuilder();
                xmlReader.setContentHandler(saxDocumentBuilder);
                xmlReader.parse(inputSource);
                return unmarshal(saxDocumentBuilder.getDocument().getDocumentElement(), clazz);
            }
            boolean isPrimitiveWrapper = isPrimitiveWrapper(clazz);
            UnmarshalRecord unmarshalRecord;
            XMLDescriptor xmlDescriptor = null;

            AbstractSession session = null;

            // check for case where the reference class is a primitive wrapper - in this case, we 
            // need to use the conversion manager to convert the node's value to the primitive 
            // wrapper class, then create, populate and return an XMLRoot.  This will be done
            // via XMLRootRecord.
            if (isPrimitiveWrapper) {
                unmarshalRecord = new XMLRootRecord(clazz);
                unmarshalRecord.setSession((AbstractSession) xmlUnmarshaller.getXMLContext().getSession(0));
            } else if(clazz == ClassConstants.OBJECT) {
                SAXUnmarshallerHandler saxUnmarshallerHandler = new SAXUnmarshallerHandler(xmlUnmarshaller.getXMLContext());
                saxUnmarshallerHandler.setXMLReader((XMLReader)xmlReader);
                saxUnmarshallerHandler.setUnmarshaller(xmlUnmarshaller);
                saxUnmarshallerHandler.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
                xmlReader.setContentHandler(saxUnmarshallerHandler);
                xmlReader.parse(inputSource);

                // resolve any mapping references
                saxUnmarshallerHandler.resolveReferences();
                return saxUnmarshallerHandler.getObject();
            } else {
                // for XMLObjectReferenceMappings we need a non-shared cache, so
                // try and get a Unit Of Work from the XMLContext
                session = xmlContext.getReadSession(clazz);
                xmlDescriptor = (XMLDescriptor) session.getDescriptor(clazz);
                unmarshalRecord = (UnmarshalRecord) xmlDescriptor.getObjectBuilder().createRecord(session);
            }
            XMLReader extendedXMLReader;
            if(xmlReader instanceof XMLReader) {
                extendedXMLReader = (XMLReader) xmlReader;
            } else {
                extendedXMLReader = new XMLReader(xmlReader);
            }
            unmarshalRecord.setXMLReader(extendedXMLReader);
            unmarshalRecord.setUnmarshaller(xmlUnmarshaller);
            extendedXMLReader.setContentHandler(unmarshalRecord);
            extendedXMLReader.setLexicalHandler(unmarshalRecord);
            extendedXMLReader.parse(inputSource);

            // resolve mapping references
            xmlUnmarshaller.resolveReferences(session);

            if (isPrimitiveWrapper || clazz == ClassConstants.OBJECT) {
                return unmarshalRecord.getCurrentObject();
            }
            return xmlDescriptor.wrapObjectInXMLRoot(unmarshalRecord, this.isResultAlwaysXMLRoot);
        } catch (IOException e) {
            throw XMLMarshalException.unmarshalException(e);
        } catch (SAXException e) {
            throw convertSAXException(e);
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
	    ||ClassConstants.XML_GREGORIAN_CALENDAR.isAssignableFrom(clazz)
	    ||ClassConstants.DURATION.isAssignableFrom(clazz);
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
        Schema schema = null;
        try {
            schema = saxParserFactory.getSchema();
        } catch (UnsupportedOperationException e) {
            // Oracle XDK does not support getSchema()
        }

        if (null != schema) {
            ValidatorHandler validatorHandler = schema.newValidatorHandler();
            xmlReader.setValidatorHandler(validatorHandler);
            validatorHandler.setErrorHandler(getErrorHandler());
        }
        
    }

}