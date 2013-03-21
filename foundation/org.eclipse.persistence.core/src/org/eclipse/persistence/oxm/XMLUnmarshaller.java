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
package org.eclipse.persistence.oxm;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.transform.Source;
import javax.xml.validation.Schema;

import org.eclipse.persistence.core.sessions.CoreSession;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.StrBuffer;
import org.eclipse.persistence.internal.oxm.Unmarshaller;
import org.eclipse.persistence.internal.oxm.record.PlatformUnmarshaller;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentUnmarshaller;
import org.eclipse.persistence.oxm.platform.XMLPlatform;
import org.eclipse.persistence.oxm.record.XMLRootRecord;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * <p>Class used to unmarshal XML to objects.
 *
 * <p>Create an XMLUnmarshaller from an XMLContext.<br>
 *  <em>Code Sample</em><br>
 *  <code>
 *  XMLContext context = new XMLContext("mySessionName");<br>
 *  XMLUnmarshaller unmarshaller = context.createUnmarshaller();<br>
 *  <code>
 *
 * <p>XML can be unmarshalled from the following inputs:<ul>
 * <li>java.io.File</li>
 * <li>java.io.InputStream</li>
 * <li>java.io.Reader</li>
 * <li>java.net.URL</li>
 * <li>javax.xml.transform.Source</li>
 * <li>org.w3c.dom.Node</li>
 * <li>org.xml.sax.InputSource</li>
 * </ul>
 *
 * <p>XML that can be unmarshalled is XML which has a root tag that corresponds
 * to a default root element on an XMLDescriptor in the TopLink project associated
 * with the XMLContext.
 *
 * @see org.eclipse.persistence.oxm.XMLContext
 */
public class XMLUnmarshaller extends Unmarshaller<AbstractSession, XMLContext, XMLDescriptor, IDResolver, MediaType, XMLRoot, XMLUnmarshallerHandler> implements Cloneable {
    public static final int NONVALIDATING = XMLParser.NONVALIDATING;
    public static final int SCHEMA_VALIDATION = XMLParser.SCHEMA_VALIDATION;
    public static final int DTD_VALIDATION = XMLParser.DTD_VALIDATION;

    private static final String STAX_SOURCE_CLASS_NAME = "javax.xml.transform.stax.StAXSource";
    private static final String XML_STREAM_READER_CLASS_NAME = "javax.xml.stream.XMLStreamReader";
    private static final String XML_EVENT_READER_CLASS_NAME = "javax.xml.stream.XMLEventReader";
    private static final String GET_XML_STREAM_READER_METHOD_NAME = "getXMLStreamReader";
    private static final String GET_XML_EVENT_READER_METHOD_NAME = "getXMLEventReader";
    private static final String XML_STREAM_READER_READER_CLASS_NAME = "org.eclipse.persistence.internal.oxm.record.XMLStreamReaderReader";
    private static final String XML_EVENT_READER_READER_CLASS_NAME = "org.eclipse.persistence.internal.oxm.record.XMLEventReaderReader";
    private static final String XML_STREAM_READER_INPUT_SOURCE_CLASS_NAME = "org.eclipse.persistence.internal.oxm.record.XMLStreamReaderInputSource";
    private static final String XML_EVENT_READER_INPUT_SOURCE_CLASS_NAME = "org.eclipse.persistence.internal.oxm.record.XMLEventReaderInputSource";

    private static Class staxSourceClass;
    private static Method staxSourceGetStreamReaderMethod;
    private static Method staxSourceGetEventReaderMethod;
    private static Constructor xmlStreamReaderReaderConstructor;
    private static Constructor xmlStreamReaderInputSourceConstructor;
    private static Constructor xmlEventReaderReaderConstructor;
    private static Constructor xmlEventReaderInputSourceConstructor;
    
   	/**
     * @since EclipseLink 2.4
     */
    private static final ErrorHandler DEFAULT_ERROR_HANDLER = new ErrorHandler() {

        public void warning(SAXParseException exception)
                throws SAXException {
            if(exception.getException() instanceof EclipseLinkException) {
                throw (EclipseLinkException) exception.getCause();
            }
        }

        public void error(SAXParseException exception) throws SAXException {
            if(exception.getException() instanceof EclipseLinkException) {
                throw exception;
            }
        }

        public void fatalError(SAXParseException exception)
                throws SAXException {
            throw exception;
            
        }

    };

    private XMLUnmarshallerHandler xmlUnmarshallerHandler;
    private PlatformUnmarshaller platformUnmarshaller;
    private boolean schemasAreInitialized;
    private XMLUnmarshalListener unmarshalListener;
    private XMLAttachmentUnmarshaller attachmentUnmarshaller;
    private Properties unmarshalProperties;

    private Class unmappedContentHandlerClass;
    private StrBuffer stringBuffer;
    private MediaType mediaType = MediaType.APPLICATION_XML;
    private IDResolver idResolver;
    private String valueWrapper = XMLConstants.VALUE_WRAPPER;
    private char namespaceSeparator = XMLConstants.DOT;
    private String attributePrefix;
    private boolean includeRoot = true;
    private NamespaceResolver namespaceResolver;    
    private boolean autoDetectMediaType = false;
    private Object unmarshalAttributeGroup;
    private boolean wrapperAsCollectionName = false;

    static {
        try {
            staxSourceClass = PrivilegedAccessHelper.getClassForName(STAX_SOURCE_CLASS_NAME);
            if(staxSourceClass != null) {
                staxSourceGetStreamReaderMethod = PrivilegedAccessHelper.getDeclaredMethod(staxSourceClass, GET_XML_STREAM_READER_METHOD_NAME, new Class[]{});
                staxSourceGetEventReaderMethod = PrivilegedAccessHelper.getDeclaredMethod(staxSourceClass, GET_XML_EVENT_READER_METHOD_NAME, new Class[]{});
                Class xmlStreamReaderInputSourceClass = PrivilegedAccessHelper.getClassForName(XML_STREAM_READER_INPUT_SOURCE_CLASS_NAME);
                Class xmlEventReaderInputSourceClass = PrivilegedAccessHelper.getClassForName(XML_EVENT_READER_INPUT_SOURCE_CLASS_NAME);
                Class xmlStreamReaderClass = PrivilegedAccessHelper.getClassForName(XML_STREAM_READER_CLASS_NAME);
                xmlStreamReaderInputSourceConstructor = PrivilegedAccessHelper.getConstructorFor(xmlStreamReaderInputSourceClass, new Class[]{xmlStreamReaderClass}, true);

                Class xmlEventReaderClass = PrivilegedAccessHelper.getClassForName(XML_EVENT_READER_CLASS_NAME);
                xmlEventReaderInputSourceConstructor = PrivilegedAccessHelper.getConstructorFor(xmlEventReaderInputSourceClass, new Class[]{xmlEventReaderClass}, true);

                Class xmlStreamReaderReaderClass = PrivilegedAccessHelper.getClassForName(XML_STREAM_READER_READER_CLASS_NAME);
                xmlStreamReaderReaderConstructor = PrivilegedAccessHelper.getConstructorFor(xmlStreamReaderReaderClass, new Class[0], true);
                
                Class xmlEventReaderReaderClass = PrivilegedAccessHelper.getClassForName(XML_EVENT_READER_READER_CLASS_NAME);
                xmlEventReaderReaderConstructor = PrivilegedAccessHelper.getConstructorFor(xmlEventReaderReaderClass, new Class[0], true);
            }
        } catch(Exception ex) {
        }
    }

    protected XMLUnmarshaller(XMLContext xmlContext) {
        this(xmlContext, null);
    }

    protected XMLUnmarshaller(XMLContext xmlContext, Map<String, Boolean> parserFeatures) {
        super(xmlContext);
        stringBuffer = new StrBuffer();
        initialize(parserFeatures);
        setErrorHandler(DEFAULT_ERROR_HANDLER);
    }

    private void initialize(Map<String, Boolean> parserFeatures) {
	    CoreSession session = context.getSession();
	    XMLPlatform xmlPlatform = (XMLPlatform)session.getDatasourceLogin().getDatasourcePlatform();
	    platformUnmarshaller = xmlPlatform.newPlatformUnmarshaller(this, parserFeatures);
	    platformUnmarshaller.setWhitespacePreserving(false);
	
	    // Waiting on XDK to fix bug #3697940 to enable this code
	    //initializeSchemas();
	    setValidationMode(NONVALIDATING);
	}

    private void initializeSchemas() {
        if (!schemasAreInitialized) {
            HashSet schemas = new HashSet();
            Iterator xmlDescriptors;
            XMLDescriptor xmlDescriptor;
            XMLSchemaReference xmlSchemaReference;
            int numberOfSessions = context.getSessions().size();
            for (int x = 0; x < numberOfSessions; x++) {
                xmlDescriptors = ((CoreSession)context.getSessions().get(x)).getDescriptors().values().iterator();
                URL schemaURL;
                while (xmlDescriptors.hasNext()) {
                    xmlDescriptor = (XMLDescriptor)xmlDescriptors.next();
                    xmlSchemaReference = xmlDescriptor.getSchemaReference();
                    if (null != xmlSchemaReference) {
                        schemaURL = xmlSchemaReference.getURL();
                        if (null != schemaURL) {
                            schemas.add(schemaURL.toString());
                        }
                    }
                }
            }
            schemas.remove(null);
            platformUnmarshaller.setSchemas(schemas.toArray());
            schemasAreInitialized = true;
        }
    }
    
    /**
     * Set the MediaType for this xmlUnmarshaller.
     * See org.eclipse.persistence.oxm.MediaType for the media types supported by EclipseLink MOXy
     * @since 2.4
     * @param mediaType
     */
    public void setMediaType(MediaType mediaType) {
    	if(this.mediaType != mediaType){
    		this.mediaType = mediaType;
            if(platformUnmarshaller != null){
            	platformUnmarshaller.mediaTypeChanged();
            }	
    	}    	
    }

    /**
     * Get the MediaType for this xmlUnmarshaller.
     * See org.eclipse.persistence.oxm.MediaType for the media types supported by EclipseLink MOXy
     * If not set the default is MediaType.APPLICATION_XML
     * @since 2.4
     * @return MediaType
     */
    public MediaType getMediaType(){
    	return mediaType;
    }
    
    /**
     * Return the instance of XMLContext that was used to create this instance
     * of XMLUnmarshaller.
     */
    public XMLContext getXMLContext() {
        return getContext();
    }

    /** 
     * Set the XMLContext used by this instance of XMLUnmarshaller.
     */
    public void setXMLContext(XMLContext value) {
        context =  value;
    }
    
    /**
    * Get the validation mode set on this XMLUnmarshaller
    * By default, the unmarshaller is set to be NONVALIDATING
    * @return the validation mode
    */
    public int getValidationMode() {
        return platformUnmarshaller.getValidationMode();
    }

    /**
    * Set the validation mode.
    * This method sets the validation mode of the parser to one of the 3 types:
    * NONVALIDATING, DTD_VALIDATION and SCHEMA_VALIDATION.
    * By default, the unmarshaller is set to be NONVALIDATING
    * @param validationMode sets the type of the validation mode to be used
    **/
    public void setValidationMode(int validationMode) {
        if (validationMode == SCHEMA_VALIDATION) {
            initializeSchemas();
        }
        platformUnmarshaller.setValidationMode(validationMode);
    }

    /**
     * Get the EntityResolver set on this XMLUnmarshaller
     * @return the EntityResolver set on this XMLUnmarshaller
     */
    public EntityResolver getEntityResolver() {
        return platformUnmarshaller.getEntityResolver();
    }

    /**
     * Set the EntityResolver on this XMLUnmarshaller
     * @param entityResolver the EntityResolver to set on this XMLUnmarshaller
     */
    public void setEntityResolver(EntityResolver entityResolver) {
        platformUnmarshaller.setEntityResolver(entityResolver);
    }

    /**
     * Get the ErrorHandler set on this XMLUnmarshaller
     * @return the ErrorHandler set on this XMLUnmarshaller
     */
    public ErrorHandler getErrorHandler() {
        return platformUnmarshaller.getErrorHandler();
    }

    /**
     * Set the ErrorHandler on this XMLUnmarshaller
     * @param errorHandler the ErrorHandler to set on this XMLUnmarshaller
     */
    public void setErrorHandler(ErrorHandler errorHandler) {
        if(null == errorHandler) {
            platformUnmarshaller.setErrorHandler(DEFAULT_ERROR_HANDLER);
        } else {
            platformUnmarshaller.setErrorHandler(errorHandler);
        }
    }

    public XMLUnmarshalListener getUnmarshalListener() {
        return this.unmarshalListener;
    }

    public void setUnmarshalListener(XMLUnmarshalListener listener) {
        this.unmarshalListener = listener;
    }

    /**
      * Get the class that will be instantiated to handled unmapped content
      * Class must implement the org.eclipse.persistence.oxm.unmapped.UnmappedContentHandler interface
      */
    public Class getUnmappedContentHandlerClass() {
        return this.unmappedContentHandlerClass;
    }

    /**
     * Set the class that will be instantiated to handled unmapped content
     * Class must implement the org.eclipse.persistence.oxm.unmapped.UnmappedContentHandler interface
     * @param aClass
     */
    public void setUnmappedContentHandlerClass(Class aClass) {
        this.unmappedContentHandlerClass = aClass;
    }

    /**
     * INTERNAL:
     * This is the text handler during unmarshal operations.
     */
    public StrBuffer getStringBuffer() {
        return stringBuffer;
    }

    /**
    * PUBLIC:
    * Read and parse the XML document from the file and map the XML data into an object.
    * The file must contain a valid XML document, and be mapped by a project used to
    * create the XMLContext.  The type of object returned will be based on the root
    * element of the XML document.
    * @param file The file to unmarshal from
    * @return the object which resulted from unmarshalling the given file
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(File file) throws XMLMarshalException {
        if (file == null) {
            throw XMLMarshalException.nullArgumentException();
        }
        return platformUnmarshaller.unmarshal(file);
    }

    /**
    * PUBLIC:
    * Read and parse the XML document from the file and map the XML data into an object.
    * The file must contain a valid XML document, and be mapped by a project used to
    * create the XMLContext.
    * @param file The file to unmarshal from
    * @param clazz The type of object to return.
    * @return the object which resulted from unmarshalling the given file
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(File file, Class clazz) throws XMLMarshalException {
        if ((null == file) || (null == clazz)) {
            throw XMLMarshalException.nullArgumentException();
        }
        return platformUnmarshaller.unmarshal(file, clazz);
    }

    /**
    * PUBLIC:
    * Read and parse the XML document from the inputStream and map the XML data into an object.
    * The inputStream must contain a valid XML document, and be mapped by a project used to
    * create the XMLContext.  The type of object returned will be based on the root
    * element of the XML document.
    * @param inputStream The inputStream to unmarshal from
    * @return the object which resulted from unmarshalling the given inputStream
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(InputStream inputStream) throws XMLMarshalException {
        if (inputStream == null) {
            throw XMLMarshalException.nullArgumentException();
        }
        return platformUnmarshaller.unmarshal(inputStream);
    }

    /**
    * PUBLIC:
    * Read and parse the XML document from the inputStream and map the XML data into an object.
    * The file must contain a valid XML document, and be mapped by a project used to
    * create the XMLContext.
    * @param inputStream The inputStream to unmarshal from
    * @param clazz The type of object to return.
    * @return the object which resulted from unmarshalling the given inputStream
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(InputStream inputStream, Class clazz) throws XMLMarshalException {
        if ((null == inputStream) || (null == clazz)) {
            throw XMLMarshalException.nullArgumentException();
        }
        return platformUnmarshaller.unmarshal(inputStream, clazz);
    }

    /**
    * PUBLIC:
    * Read and parse the XML document from the reader and map the XML data into an object.
    * The reader must contain a valid XML document, and be mapped by a project used to
    * create the XMLContext.  The type of object returned will be based on the root
    * element of the XML document.
    * @param reader The reader to unmarshal from
    * @return the object which resulted from unmarshalling the given reader
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(Reader reader) throws XMLMarshalException {
        if (reader == null) {
            throw XMLMarshalException.nullArgumentException();
        }
        return platformUnmarshaller.unmarshal(reader);
    }

    /**
    * PUBLIC:
    * Read and parse the XML document from the reader and map the XML data into an object.
    * The file must contain a valid XML document, and be mapped by a project used to
    * create the XMLContext.
    * @param reader The reader to unmarshal from
    * @param clazz The type of object to return.
    * @return the object which resulted from unmarshalling the given reader
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(Reader reader, Class clazz) throws XMLMarshalException {
        if ((null == reader) || (null == clazz)) {
            throw XMLMarshalException.nullArgumentException();
        }
        return platformUnmarshaller.unmarshal(reader, clazz);
    }

    /**
    * PUBLIC:
    * Read and parse the XML document from the url and map the XML data into an object.
    * The url must reference a valid XML document, and be mapped by a project used to
    * create the XMLContext.  The type of object returned will be based on the root
    * element of the XML document.
    * @param url The url to unmarshal from
    * @return the object which resulted from unmarshalling the given url
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(URL url) throws XMLMarshalException {
        if (url == null) {
            throw XMLMarshalException.nullArgumentException();
        }
        return platformUnmarshaller.unmarshal(url);
    }

    /**
    * PUBLIC:
    * Read and parse the XML document from the url and map the XML data into an object.
    * The url must reference a valid XML document, and be mapped by a project used to
    * create the XMLContext.
    * @param url The url to unmarshal from
    * @param clazz The type of object to return.
    * @return the object which resulted from unmarshalling the given url
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(URL url, Class clazz) throws XMLMarshalException {
        if ((null == url) || (null == clazz)) {
            throw XMLMarshalException.nullArgumentException();
        }
        return platformUnmarshaller.unmarshal(url, clazz);
    }

    /**
    * PUBLIC:
    * Read and parse the XML document from the inputSource and map the XML data into an object.
    * The inputSource must contain a valid XML document, and be mapped by a project used to
    * create the XMLContext.  The type of object returned will be based on the root
    * element of the XML document.
    * @param inputSource The inputSource to unmarshal from
    * @return the object which resulted from unmarshalling the given inputSource
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(InputSource inputSource) throws XMLMarshalException {
        if (inputSource == null) {
            throw XMLMarshalException.nullArgumentException();
        }
        return platformUnmarshaller.unmarshal(inputSource);
    }

    /**
    * PUBLIC:
    * Read and parse the XML document from the inputSource and map the XML data into an object.
    * The inputSource must contain a valid XML document, and be mapped by a project used to
    * create the XMLContext.
    * @param inputSource The inputSource to unmarshal from
    * @param clazz The type of object to return.
    * @return the object which resulted from unmarshalling the given inputSource
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(InputSource inputSource, Class clazz) throws XMLMarshalException {
        if ((null == inputSource) || (null == clazz)) {
            throw XMLMarshalException.nullArgumentException();
        }
        return platformUnmarshaller.unmarshal(inputSource, clazz);
    }

    /**
    * PUBLIC:
    * Map the XML node into an object.
    * The node must be a valid XML document, and be mapped by a project used to
    * create the XMLContext.
    * @param node The node to unmarshal from
    * @return the object which resulted from unmarshalling the given node
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(Node node) throws XMLMarshalException {
        if (node == null) {
            throw XMLMarshalException.nullArgumentException();
        }
        if ((node.getNodeType() == Node.DOCUMENT_NODE) || (node.getNodeType() == Node.ELEMENT_NODE) || (node.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE)) {
            return platformUnmarshaller.unmarshal(node);
        } else {
            throw XMLMarshalException.unmarshalException();
        }
    }

    /**
    * PUBLIC:
    * Map the XML node into an object.
    * The node must be a valid XML document, and be mapped by a project used to
    * create the XMLContext.
    * @param node The node to unmarshal from
    * @param clazz The type of object to return.
    * @return the object which resulted from unmarshalling the given node
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(Node node, Class clazz) throws XMLMarshalException {
        if ((null == node) || (null == clazz)) {
            throw XMLMarshalException.nullArgumentException();
        }
        return platformUnmarshaller.unmarshal(node, clazz);
    }

    /**
    * PUBLIC:
    * Read and parse the XML document from the source and map the XML data into an object.
    * The source must contain a valid XML document, and be mapped by a project used to
    * create the XMLContext.  The type of object returned will be based on the root
    * element of the XML document.
    * @param source The source to unmarshal from
    * @return the object which resulted from unmarshalling the given source
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(Source source) throws XMLMarshalException {
        if (source == null) {
            throw XMLMarshalException.nullArgumentException();
        }
        if (source.getClass() == this.staxSourceClass) {
            try {
                Object xmlStreamReader = PrivilegedAccessHelper.invokeMethod(this.staxSourceGetStreamReaderMethod, source);
                if(xmlStreamReader != null) {
                    InputSource inputSource = (InputSource) PrivilegedAccessHelper.invokeConstructor(xmlStreamReaderInputSourceConstructor, new Object[]{xmlStreamReader});
                    XMLReader xmlReader = (XMLReader) PrivilegedAccessHelper.invokeConstructor(xmlStreamReaderReaderConstructor, new Object[0]);
                    return platformUnmarshaller.unmarshal(xmlReader, inputSource);
                } else {
                    Object xmlEventReader = PrivilegedAccessHelper.invokeMethod(this.staxSourceGetEventReaderMethod, source);
                    if(xmlEventReader != null) {
                        InputSource inputSource = (InputSource)PrivilegedAccessHelper.invokeConstructor(xmlEventReaderInputSourceConstructor, new Object[]{xmlEventReader});
                        XMLReader xmlReader = (XMLReader)PrivilegedAccessHelper.invokeConstructor(xmlEventReaderReaderConstructor, new Object[]{});
                        return platformUnmarshaller.unmarshal(xmlReader, inputSource);
                    }
                }
            } catch(Exception e) {
                throw XMLMarshalException.unmarshalException(e);
            }
        }
        return platformUnmarshaller.unmarshal(source);
    }

    /**
     * Return a properties object for a given instance of the
     * XMLUnmarshaller.
     *
     * @return
     */
    public Properties getProperties() {
        if(null == unmarshalProperties) {
            unmarshalProperties = new Properties();
        }
        return unmarshalProperties;
    }

    /**
     * Return the property for a given key, if one exists.
     *
     * @parm key
     * @return
     */
    public Object getProperty(Object key) {
        if(null == unmarshalProperties) {
            return null;
        }
        return unmarshalProperties.get(key);
    }

    /**
    * PUBLIC:
    * Read and parse the XML document from the source and map the XML data into an object.
    * The source must contain a valid XML document, and be mapped by a project used to
    * create the XMLContext.
    * @param source The inputSource to unmarshal from
    * @param clazz The type of object to return.
    * @return the object which resulted from unmarshalling the given source
    * @throws XMLMarshalException if an error occurred during unmarshalling
    */
    public Object unmarshal(Source source, Class clazz) throws XMLMarshalException {
        if ((null == source) || (null == clazz)) {
            throw XMLMarshalException.nullArgumentException();
        }
        if (source.getClass() == this.staxSourceClass) {        	
            try {
                Object xmlStreamReader = PrivilegedAccessHelper.invokeMethod(this.staxSourceGetStreamReaderMethod, source);
                if(xmlStreamReader != null) {
                    InputSource inputSource = (InputSource) PrivilegedAccessHelper.invokeConstructor(xmlStreamReaderInputSourceConstructor, new Object[]{xmlStreamReader});
                    XMLReader xmlReader = (XMLReader) PrivilegedAccessHelper.invokeConstructor(xmlStreamReaderReaderConstructor, new Object[]{});
                    return platformUnmarshaller.unmarshal(xmlReader, inputSource, clazz);
                } else {
                    Object xmlEventReader = PrivilegedAccessHelper.invokeMethod(this.staxSourceGetEventReaderMethod, source);
                    if(xmlEventReader != null) {
                        InputSource inputSource = (InputSource)PrivilegedAccessHelper.invokeConstructor(xmlEventReaderInputSourceConstructor, new Object[]{xmlEventReader});
                        XMLReader xmlReader = (XMLReader)PrivilegedAccessHelper.invokeConstructor(xmlEventReaderReaderConstructor, new Object[]{});
                        return platformUnmarshaller.unmarshal(xmlReader, inputSource, clazz);
                    }
                }
            } catch(Exception e) {
                throw XMLMarshalException.unmarshalException(e);
            }
        }
        return platformUnmarshaller.unmarshal(source, clazz);
    }

    public Object unmarshal(XMLReader xmlReader, InputSource inputSource) {
        return this.platformUnmarshaller.unmarshal(xmlReader, inputSource);
    }

    public Object unmarshal(XMLReader xmlReader, InputSource inputSource, Class clazz) {
        return this.platformUnmarshaller.unmarshal(xmlReader, inputSource, clazz);
    }

    public XMLUnmarshallerHandler getUnmarshallerHandler() {
        if (null == xmlUnmarshallerHandler) {
            xmlUnmarshallerHandler = new XMLUnmarshallerHandler(this);
        }
        return xmlUnmarshallerHandler;
    }

    public XMLAttachmentUnmarshaller getAttachmentUnmarshaller() {
        return attachmentUnmarshaller;
    }

    public void setAttachmentUnmarshaller(XMLAttachmentUnmarshaller atu) {
        attachmentUnmarshaller = atu;
    }

    public void setResultAlwaysXMLRoot(boolean alwaysReturnRoot) {
        platformUnmarshaller.setResultAlwaysXMLRoot(alwaysReturnRoot);
    }

    public boolean isResultAlwaysXMLRoot() {
        return platformUnmarshaller.isResultAlwaysXMLRoot();
    }
    
    public void setSchema(Schema schema) {
        this.platformUnmarshaller.setSchema(schema);
    }
    
    public Schema getSchema() {
        return this.platformUnmarshaller.getSchema();
    }
    
    /**
     * Value that will be used to prefix attributes.  
     * Ignored unmarshalling XML.   
     * @return
     * @since 2.4
     */
    public String getAttributePrefix() {
        return attributePrefix;
    }
    
    /**
     * Value that will be used to prefix attributes.  
     * Ignored unmarshalling XML.
     * @since 2.4	 
     */
    public void setAttributePrefix(String attributePrefix) {
        this.attributePrefix = attributePrefix;
    }
    
    /**
     * Name of the property to marshal/unmarshal as a wrapper on the text() mappings   
     * Ignored unmarshalling XML.  
     * @since 2.4	 
     */	
    public String getValueWrapper() {
        return valueWrapper;
    }

    /**
     * Name of the property to marshal/unmarshal as a wrapper on the text() mappings   
     * Ignored unmarshalling XML.  
     * @since 2.4	 
     */
    public void setValueWrapper(String valueWrapper) {
        this.valueWrapper = valueWrapper;
    }
        
    /**
     * Get the namespace separator used during unmarshal operations.
     * If mediaType is application/json '.' is the default
     * Ignored unmarshalling XML.   
     * @since 2.4
     */
    public char getNamespaceSeparator() {    	
        return namespaceSeparator;
    }

    /**
     * Set the namespace separator used during unmarshal operations.
     * If mediaType is application/json '.' is the default
     * Ignored unmarshalling XML.   
     * @since 2.4
     */
	public void setNamespaceSeparator(char namespaceSeparator) {
		this.namespaceSeparator = namespaceSeparator;
	}
    
    /**
     * Determine if the @XMLRootElement should be marshalled when present.  
     * Ignored unmarshalling XML.   
     * @return
     * @since 2.4
     */
    public boolean isIncludeRoot() {
            return includeRoot;
    }

    /**
     * Determine if the @XMLRootElement should be marshalled when present.  
     * Ignored unmarshalling XML.   
     * @return
     * @since 2.4
     */
    public void setIncludeRoot(boolean includeRoot) {
         this.includeRoot = includeRoot;
    }
    
    /**
     * Return if this XMLUnmarshaller should try to automatically determine
     * the MediaType of the document (instead of using the MediaType set
     * by setMediaType)
     */
    public boolean isAutoDetectMediaType() {
		return autoDetectMediaType;
	}

    /**
     * Set if this XMLUnmarshaller should try to automatically determine
     * the MediaType of the document (instead of using the MediaType set
     * by setMediaType)
     */
	public void setAutoDetectMediaType(boolean autoDetectMediaType) {
		this.autoDetectMediaType = autoDetectMediaType;
	}

    
    
    /**
     * Name of the NamespaceResolver to be used during unmarshal
     * Ignored unmarshalling XML.  
     * @since 2.4	 
     */	
    public NamespaceResolver getNamespaceResolver() {
        return namespaceResolver;
    }

    /**
     * Get the NamespaceResolver to be used during unmarshal
     * Ignored unmarshalling XML.  
     * @since 2.4	 
     */
    public void setNamespaceResolver(NamespaceResolver namespaceResolver) {
        this.namespaceResolver = namespaceResolver;
    }
    
    /**
     * @since 2.4.2
     */
    @Override
    public boolean isWrapperAsCollectionName() {
        return wrapperAsCollectionName;
    }

    /**
     * @since 2.4.2
     */
    public void setWrapperAsCollectionName(boolean wrapperAsCollectionName) {
        this.wrapperAsCollectionName = wrapperAsCollectionName;
    }

    @Override
    public XMLUnmarshaller clone() {
        XMLUnmarshaller clone = new XMLUnmarshaller(context);
        clone.setAttachmentUnmarshaller(attachmentUnmarshaller);
        clone.setEntityResolver(getEntityResolver());
        clone.setErrorHandler(getErrorHandler());
        for(Entry entry : unmarshalProperties.entrySet()) {
            clone.getProperties().put(entry.getKey(), entry.getValue());
        }
        clone.setResultAlwaysXMLRoot(platformUnmarshaller.isResultAlwaysXMLRoot());
        try {
            Schema schema = getSchema();
            if(null != schema) {
                clone.setSchema(schema);
            }
        } catch(UnsupportedOperationException e) {}
        clone.setUnmappedContentHandlerClass(unmappedContentHandlerClass);
        clone.setUnmarshalListener(unmarshalListener);
        clone.setValidationMode(getValidationMode());
        return clone;
    }

    /**
     * Return this Unmarshaller's custom IDResolver.
     * @see IDResolver
     * @since 2.3.3
     * @return the custom IDResolver, or null if one has not been specified.
     */
    public IDResolver getIDResolver() {
        return idResolver;
    }

    /**
     * Set this Unmarshaller's custom IDResolver.
     * @see IDResolver
     * @since 2.3.3
     */
    public void setIDResolver(IDResolver idResolver) {
        this.idResolver = idResolver;
    }

    /**
     * INTERNAL
     * @since 2.5.0
     */
    public XMLRoot createRoot() {
        return new XMLRoot();
    }

    /**
     * INTERNAL
     * @since 2.5.0
     */
    @Override
    public UnmarshalRecord createRootUnmarshalRecord(Class clazz) {
        XMLRootRecord rootUnmarshalRecord = new XMLRootRecord(clazz);
        rootUnmarshalRecord.setSession((AbstractSession) context.getSession());
        return rootUnmarshalRecord;
    }

    /**
     * INTERNAL
     * @since 2.5.0
     */
    @Override
    public UnmarshalRecord createUnmarshalRecord(XMLDescriptor xmlDescriptor, AbstractSession session) {
        org.eclipse.persistence.oxm.record.UnmarshalRecord wrapper = (org.eclipse.persistence.oxm.record.UnmarshalRecord) xmlDescriptor.getObjectBuilder().createRecord((AbstractSession) session);
        return wrapper.getUnmarshalRecord();
    }

    /**
     * INTERNAL:
     * Returns the AttributeGroup or the name of the AttributeGroup to be used to 
     * unmarshal. 
     */
    public Object getUnmarshalAttributeGroup() {
        return this.unmarshalAttributeGroup;
    }
    
    public void setUnmarshalAttributeGroup(Object attributeGroup) {
        this.unmarshalAttributeGroup = attributeGroup;
    }

}