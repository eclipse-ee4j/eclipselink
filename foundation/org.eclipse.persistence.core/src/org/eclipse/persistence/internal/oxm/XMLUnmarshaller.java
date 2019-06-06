/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Blaise Doughan - 2.6 - initial implementation
//     Marcel Valovy - 2.6.0 - added case insensitive unmarshalling
package org.eclipse.persistence.internal.oxm;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.transform.Source;
import javax.xml.validation.Schema;

import org.eclipse.persistence.core.sessions.CoreSession;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.localization.JAXBLocalization;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.record.PlatformUnmarshaller;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.internal.oxm.record.XMLPlatform;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentUnmarshaller;
import org.eclipse.persistence.oxm.record.XMLRootRecord;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * <p>Class used to unmarshal XML & JSON to objects.
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
public class XMLUnmarshaller<
    ABSTRACT_SESSION extends CoreAbstractSession,
    CONTEXT extends Context,
    DESCRIPTOR extends Descriptor,
    ID_RESOLVER extends IDResolver,
    MEDIA_TYPE extends MediaType,
    ROOT extends Root,
    UNMARSHALLER_HANDLER extends UnmarshallerHandler,
    UNMARSHALLER_LISTENER extends Unmarshaller.Listener> extends Unmarshaller<ABSTRACT_SESSION, CONTEXT, DESCRIPTOR, ID_RESOLVER, MEDIA_TYPE, ROOT, UNMARSHALLER_HANDLER, UNMARSHALLER_LISTENER> implements Cloneable {

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

    private JsonTypeConfiguration jsonTypeConfiguration;
    private Boolean logPayload;

    /**
     * @since EclipseLink 2.4
     */
    private static final ErrorHandler DEFAULT_ERROR_HANDLER = new ErrorHandler() {

        @Override
        public void warning(SAXParseException exception)
                throws SAXException {
            if(exception.getException() instanceof EclipseLinkException) {
                throw (EclipseLinkException) exception.getCause();
            }
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            if(exception.getException() instanceof EclipseLinkException) {
                throw exception;
            }
        }

        @Override
        public void fatalError(SAXParseException exception)
                throws SAXException {
            throw exception;

        }

    };

    protected UNMARSHALLER_HANDLER xmlUnmarshallerHandler;
    protected PlatformUnmarshaller platformUnmarshaller;
    protected boolean schemasAreInitialized;
    private XMLAttachmentUnmarshaller attachmentUnmarshaller;
    private Properties unmarshalProperties;

    private Class unmappedContentHandlerClass;
    private StrBuffer stringBuffer;
    private MEDIA_TYPE mediaType;
    private ID_RESOLVER idResolver;
    private String valueWrapper = Constants.VALUE_WRAPPER;
    private char namespaceSeparator = Constants.DOT;
    private String attributePrefix;
    private boolean includeRoot = true;
    private NamespaceResolver namespaceResolver;
    private boolean autoDetectMediaType = false;
    private boolean caseInsensitive = false;
    private Object unmarshalAttributeGroup;
    private boolean wrapperAsCollectionName = false;
    private boolean warnOnUnmappedElement = true;

    /**
     * If there should be xsd prefix when using simple types, e.g. xsd.int.
     */
    private boolean useXsdTypesWithPrefix = false;

    /**
     * If we should treat unqualified type property in JSON as MOXy type discriminator.
     */
    private boolean jsonTypeCompatibility = false;

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

    protected XMLUnmarshaller(CONTEXT xmlContext) {
        this(xmlContext, null);
    }

    protected XMLUnmarshaller(CONTEXT xmlContext, Map<String, Boolean> parserFeatures) {
        super(xmlContext);
        stringBuffer = new StrBuffer();
        initialize(parserFeatures);
        setErrorHandler(DEFAULT_ERROR_HANDLER);
    }

    protected XMLUnmarshaller(XMLUnmarshaller xmlUnmarshaller) {
        super(xmlUnmarshaller);
        stringBuffer = new StrBuffer();
        initialize(null);
        setAttachmentUnmarshaller(xmlUnmarshaller.getAttachmentUnmarshaller());
        setEntityResolver(xmlUnmarshaller.getEntityResolver());
        setErrorHandler(xmlUnmarshaller.getErrorHandler());
        for(Entry entry : xmlUnmarshaller.getProperties().entrySet()) {
            getProperties().put(entry.getKey(), entry.getValue());
        }
        setResultAlwaysXMLRoot(xmlUnmarshaller.platformUnmarshaller.isResultAlwaysXMLRoot());
        try {
            Schema schema = xmlUnmarshaller.getSchema();
            if(null != schema) {
                setSchema(schema);
            }
        } catch(UnsupportedOperationException e) {}
        setUnmappedContentHandlerClass(xmlUnmarshaller.unmappedContentHandlerClass);
    }

    protected void initialize(Map<String, Boolean> parserFeatures) {
        CoreSession session = context.getSession();
        XMLPlatform xmlPlatform = (XMLPlatform)session.getDatasourceLogin().getDatasourcePlatform();
        platformUnmarshaller = xmlPlatform.newPlatformUnmarshaller(this, parserFeatures);
        platformUnmarshaller.setWhitespacePreserving(false);
    }

    /**
     * Set the MediaType for this xmlUnmarshaller.
     * See org.eclipse.persistence.oxm.MediaType for the media types supported by EclipseLink MOXy
     * @since 2.4
     * @param mediaType
     */
    public void setMediaType(MEDIA_TYPE mediaType) {
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
    @Override
    public MEDIA_TYPE getMediaType(){
        return mediaType;
    }

    /**
     * Return the instance of XMLContext that was used to create this instance
     * of XMLUnmarshaller.
     */
    public CONTEXT getXMLContext() {
        return getContext();
    }

    /**
     * Set the XMLContext used by this instance of XMLUnmarshaller.
     */
    public void setXMLContext(CONTEXT value) {
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
    @Override
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

    /**
      * Get the class that will be instantiated to handled unmapped content
      * Class must implement the org.eclipse.persistence.oxm.unmapped.UnmappedContentHandler interface
      */
    @Override
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
    @Override
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
        Object result = platformUnmarshaller.unmarshal(file);
        logUnmarshall(result);
        return result;
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
        Object result = platformUnmarshaller.unmarshal(file, clazz);
        logUnmarshall(result);
        return result;
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
        Object result = platformUnmarshaller.unmarshal(inputStream);
        logUnmarshall(result);
        return result;
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
        Object result = platformUnmarshaller.unmarshal(inputStream, clazz);
        logUnmarshall(result);
        return result;
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
        Object result = platformUnmarshaller.unmarshal(reader);
        logUnmarshall(result);
        return result;
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
        Object result = platformUnmarshaller.unmarshal(reader, clazz);
        logUnmarshall(result);
        return result;
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
        Object result = platformUnmarshaller.unmarshal(url);
        logUnmarshall(result);
        return result;
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
        Object result = platformUnmarshaller.unmarshal(url, clazz);
        logUnmarshall(result);
        return result;
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
        Object result = platformUnmarshaller.unmarshal(inputSource);
        logUnmarshall(result);
        return result;
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
        Object result = platformUnmarshaller.unmarshal(inputSource, clazz);
        logUnmarshall(result);
        return result;
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
            Object result = platformUnmarshaller.unmarshal(node);
            logUnmarshall(result);
            return result;
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
        Object result = platformUnmarshaller.unmarshal(node, clazz);
        logUnmarshall(result);
        return result;
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
                    Object result = platformUnmarshaller.unmarshal(xmlReader, inputSource);
                    logUnmarshall(result);
                    return result;
                } else {
                    Object xmlEventReader = PrivilegedAccessHelper.invokeMethod(this.staxSourceGetEventReaderMethod, source);
                    if(xmlEventReader != null) {
                        InputSource inputSource = (InputSource)PrivilegedAccessHelper.invokeConstructor(xmlEventReaderInputSourceConstructor, new Object[]{xmlEventReader});
                        XMLReader xmlReader = (XMLReader)PrivilegedAccessHelper.invokeConstructor(xmlEventReaderReaderConstructor, new Object[]{});
                        Object result = platformUnmarshaller.unmarshal(xmlReader, inputSource);
                        logUnmarshall(result);
                        return result;
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
    @Override
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
                    Object result = platformUnmarshaller.unmarshal(xmlReader, inputSource, clazz);
                    logUnmarshall(result);
                    return result;
                } else {
                    Object xmlEventReader = PrivilegedAccessHelper.invokeMethod(this.staxSourceGetEventReaderMethod, source);
                    if(xmlEventReader != null) {
                        InputSource inputSource = (InputSource)PrivilegedAccessHelper.invokeConstructor(xmlEventReaderInputSourceConstructor, new Object[]{xmlEventReader});
                        XMLReader xmlReader = (XMLReader)PrivilegedAccessHelper.invokeConstructor(xmlEventReaderReaderConstructor, new Object[]{});
                        Object result = platformUnmarshaller.unmarshal(xmlReader, inputSource, clazz);
                        logUnmarshall(result);
                        return result;
                    }
                }
            } catch(Exception e) {
                throw XMLMarshalException.unmarshalException(e);
            }
        }
        Object result = platformUnmarshaller.unmarshal(source, clazz);
        logUnmarshall(result);
        return result;

    }

    public Object unmarshal(XMLReader xmlReader, InputSource inputSource) {
        Object result = this.platformUnmarshaller.unmarshal(xmlReader, inputSource);
        logUnmarshall(result);
        return result;
    }

    public Object unmarshal(XMLReader xmlReader, InputSource inputSource, Class clazz) {
        Object result = this.platformUnmarshaller.unmarshal(xmlReader, inputSource, clazz);
        logUnmarshall(result);
        return result;
    }

    @Override
    public UNMARSHALLER_HANDLER getUnmarshallerHandler() {
        throw new UnsupportedOperationException();
    }

    @Override
    public XMLAttachmentUnmarshaller getAttachmentUnmarshaller() {
        return attachmentUnmarshaller;
    }

    public void setAttachmentUnmarshaller(XMLAttachmentUnmarshaller atu) {
        attachmentUnmarshaller = atu;
    }

    public void setResultAlwaysXMLRoot(boolean alwaysReturnRoot) {
        platformUnmarshaller.setResultAlwaysXMLRoot(alwaysReturnRoot);
    }

    @Override
    public boolean isResultAlwaysXMLRoot() {
        return platformUnmarshaller.isResultAlwaysXMLRoot();
    }

    public void setSchema(Schema schema) {
        this.platformUnmarshaller.setSchema(schema);
    }

    @Override
    public Schema getSchema() {
        return this.platformUnmarshaller.getSchema();
    }

    /**
     * Value that will be used to prefix attributes.
     * Ignored unmarshalling XML.
     * @return
     * @since 2.4
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
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
     * INTERNAL
     * @return true if the media type is application/json, else false.
     * @since EclipseLink 2.6.0
     */
    @Override
    public boolean isApplicationJSON() {
        return null != mediaType && mediaType.isApplicationJSON();
    }

    /**
     * INTERNAL
     * @return true if the media type is application/xml, else false.
     * @since EclipseLink 2.6.0
     */
    @Override
    public boolean isApplicationXML() {
        return null == mediaType || mediaType.isApplicationXML();
    }

    /**
     * Return if this XMLUnmarshaller should try to automatically determine
     * the MediaType of the document (instead of using the MediaType set
     * by setMediaType)
     */
    @Override
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
     * Return if this Unmarshaller should perform case insensitive unmarshalling.
     */
    @Override
    public boolean isCaseInsensitive(){
        return caseInsensitive;
    }

    /**
     * Set true to make this Unmarshaller perform case insensitive unmarshalling.
     */
    public void setCaseInsensitive(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    /**
     * Name of the NamespaceResolver to be used during unmarshal
     * Ignored unmarshalling XML.
     * @since 2.4
     */
    @Override
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
        return new XMLUnmarshaller(this);
    }

    /**
     * Return this Unmarshaller's custom IDResolver.
     * @see IDResolver
     * @since 2.3.3
     * @return the custom IDResolver, or null if one has not been specified.
     */
    @Override
    public ID_RESOLVER getIDResolver() {
        return idResolver;
    }

    /**
     * Set this Unmarshaller's custom IDResolver.
     * @see IDResolver
     * @since 2.3.3
     */
    @Override
    public void setIDResolver(ID_RESOLVER idResolver) {
        this.idResolver = idResolver;
    }

    /**
     * INTERNAL
     * @since 2.5.0
     */
    @Override
    public ROOT createRoot() {
        throw new UnsupportedOperationException();
    }

    /**
     * INTERNAL
     * @since 2.5.0
     */
    @Override
    public UnmarshalRecord createRootUnmarshalRecord(Class clazz) {
        return new XMLRootRecord(clazz, this);
    }

    /**
     * INTERNAL
     * @since 2.5.0
     */
    @Override
    public UnmarshalRecord createUnmarshalRecord(DESCRIPTOR xmlDescriptor, ABSTRACT_SESSION session) {
        return (UnmarshalRecord) xmlDescriptor.getObjectBuilder().createRecord(session);
    }

    /**
     * INTERNAL:
     * Returns the AttributeGroup or the name of the AttributeGroup to be used to
     * unmarshal.
     */
    @Override
    public Object getUnmarshalAttributeGroup() {
        return this.unmarshalAttributeGroup;
    }

    public void setUnmarshalAttributeGroup(Object attributeGroup) {
        this.unmarshalAttributeGroup = attributeGroup;
    }

    /**
     * INTERNAL:
     * Returns true if a warning exception should be generated when an unmapped element is encountered.
     * @since 2.6.0
     */
    @Override
    public boolean shouldWarnOnUnmappedElement() {
        return this.warnOnUnmappedElement;
    }

    /**
     * INTERNAL:
     * Set to true if a warning exception should be generated when an unmapped element is encountered, false otherwise.
     * @since 2.6.0
     */
    public void setWarnOnUnmappedElement(boolean warnOnUnmappedElement) {
        this.warnOnUnmappedElement = warnOnUnmappedElement;
    }

    /**
     * Returns json type configuration.
     *
     * @return json type configuration
     * @since 2.6.0
     */
    @Override
    public JsonTypeConfiguration getJsonTypeConfiguration() {
        if (null == jsonTypeConfiguration) {
            jsonTypeConfiguration = new JsonTypeConfiguration();
        }

        return jsonTypeConfiguration;
    }

    public final boolean isSecureProcessingDisabled() {
        return platformUnmarshaller.isSecureProcessingDisabled();
    }

    public final void setDisableSecureProcessing(boolean disableSecureProcessing) {
        platformUnmarshaller.setDisableSecureProcessing(disableSecureProcessing);
    }

    public Boolean isLogPayload() {
        return logPayload;
    }

    public void setLogPayload(Boolean logPayload) {
        this.logPayload = logPayload;
    }

    private void logUnmarshall(Object object) {
        SessionLog logger = AbstractSessionLog.getLog();

        if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
            logger.log(SessionLog.FINE, SessionLog.MOXY, "moxy_start_unmarshalling", new Object[] { (object!= null)?object.getClass().getName():"N/A", this.mediaType, platformUnmarshaller.getClass().getName() });
        }
        if (object != null && logPayload != null && this.isLogPayload()) {
            AbstractSessionLog.getLog().log(SessionLog.FINEST, SessionLog.MOXY, object.toString(), new Object[0], false);
        }
    }

}
