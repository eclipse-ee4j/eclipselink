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
package org.eclipse.persistence.oxm;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.oxm.FragmentContentHandler;
import org.eclipse.persistence.internal.oxm.TreeObjectBuilder;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.XMLObjectBuilder;
import org.eclipse.persistence.internal.oxm.XPathEngine;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.record.DOMReader;
import org.eclipse.persistence.internal.oxm.record.namespaces.PrefixMapperNamespaceResolver;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping.WriteType;
import org.eclipse.persistence.oxm.attachment.*;
import org.eclipse.persistence.oxm.documentpreservation.DocumentPreservationPolicy;
import org.eclipse.persistence.oxm.record.ContentHandlerRecord;
import org.eclipse.persistence.oxm.record.JSONFormattedWriterRecord;
import org.eclipse.persistence.oxm.record.FormattedOutputStreamRecord;
import org.eclipse.persistence.oxm.record.FormattedWriterRecord;
import org.eclipse.persistence.oxm.record.JSONWriterRecord;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.NodeRecord;
import org.eclipse.persistence.oxm.record.OutputStreamRecord;
import org.eclipse.persistence.oxm.record.ValidatingMarshalRecord;
import org.eclipse.persistence.oxm.record.WriterRecord;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformException;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.platform.xml.XMLTransformer;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.ext.LexicalHandler;

/**
 * <p>Class used to marshal object to XML.
 *
 * <p>Create an XMLMarshaller from an XMLContext.<br>
 *  <em>Code Sample</em><br>
 *  <code>
 *  XMLContext context = new XMLContext("mySessionName");<br>
 *  XMLMarshaller marshaller = context.createMarshaller();<br>
 *  <code>
 *
 * <p>Objects can be marshalled to the following outputs:<ul>
 * <li>java.io.OutputStream</li>
 * <li>java.io.Writer</li>
 * <li>javax.xml.transform.Result</li>
 * <li>org.w3c.dom.Node</li>
 * <li>org.xml.sax.ContentHandler</li>
 * </ul>
 *
 * <p>Objects that can be marshalled are those which are mapped in the
 * TopLink project associated with the XMLContext, and which are mapped
 * to an XMLDescriptor that has a default root element specified.
 *
 * @see org.eclipse.persistence.oxm.XMLContext
 */
public class XMLMarshaller implements Cloneable {
    private final static String DEFAULT_XML_VERSION = "1.0";
    private String schemaLocation;
    private String noNamespaceSchemaLocation;
    private XMLTransformer transformer;
    private XMLContext xmlContext;
    private XMLMarshalListener marshalListener;
    private XMLAttachmentMarshaller attachmentMarshaller;
    private ErrorHandler errorHandler;
    private Properties marshalProperties;
    private Schema schema;
    private MediaType mediaType = MediaType.APPLICATION_XML;
    private String attributePrefix;
    private boolean includeRoot;
    private String valueWrapper = XMLConstants.VALUE_WRAPPER;
    private NamespacePrefixMapper mapper;
    private String indentString;
    private CharacterEscapeHandler charEscapeHandler;

    private static final String STAX_RESULT_CLASS_NAME = "javax.xml.transform.stax.StAXResult";
    private static final String GET_XML_STREAM_WRITER_METHOD_NAME = "getXMLStreamWriter";
    private static final String GET_XML_EVENT_WRITER_METHOD_NAME = "getXMLEventWriter";
    private static final String XML_STREAM_WRITER_RECORD_CLASS_NAME = "org.eclipse.persistence.oxm.record.XMLStreamWriterRecord";
    private static final String XML_EVENT_WRITER_RECORD_CLASS_NAME = "org.eclipse.persistence.oxm.record.XMLEventWriterRecord";
    private static final String XML_STREAM_WRITER_CLASS_NAME = "javax.xml.stream.XMLStreamWriter";
    private static final String XML_EVENT_WRITER_CLASS_NAME = "javax.xml.stream.XMLEventWriter";
    private static final String DOM_TO_STREAM_WRITER_CLASS_NAME = "org.eclipse.persistence.internal.oxm.record.DomToXMLStreamWriter";
    private static final String DOM_TO_EVENT_WRITER_CLASS_NAME = "org.eclipse.persistence.internal.oxm.record.DomToXMLEventWriter";
    private static final String WRITE_TO_STREAM_METHOD_NAME = "writeToStream";
    private static final String WRITE_TO_EVENT_WRITER_METHOD_NAME = "writeToEventWriter";

    private static Class staxResultClass;
    private static Method staxResultGetStreamWriterMethod;
    private static Method staxResultGetEventWriterMethod;
    private static Constructor xmlStreamWriterRecordConstructor;
    private static Constructor xmlEventWriterRecordConstructor;
    private static Method writeToStreamMethod;
    private static Method writeToEventWriterMethod;
    private static Class domToStreamWriterClass;
    private static Class domToEventWriterClass;

    static {
        try {
            staxResultClass = PrivilegedAccessHelper.getClassForName(STAX_RESULT_CLASS_NAME);
            if(staxResultClass != null) {
                staxResultGetStreamWriterMethod = PrivilegedAccessHelper.getDeclaredMethod(staxResultClass, GET_XML_STREAM_WRITER_METHOD_NAME, new Class[]{});
                staxResultGetEventWriterMethod = PrivilegedAccessHelper.getDeclaredMethod(staxResultClass, GET_XML_EVENT_WRITER_METHOD_NAME, new Class[]{});
            }
            Class streamWriterRecordClass = PrivilegedAccessHelper.getClassForName(XML_STREAM_WRITER_RECORD_CLASS_NAME);
            Class streamWriterClass = PrivilegedAccessHelper.getClassForName(XML_STREAM_WRITER_CLASS_NAME);
            xmlStreamWriterRecordConstructor = PrivilegedAccessHelper.getConstructorFor(streamWriterRecordClass, new Class[]{streamWriterClass}, true);

            Class eventWriterRecordClass = PrivilegedAccessHelper.getClassForName(XML_EVENT_WRITER_RECORD_CLASS_NAME);
            Class eventWriterClass = PrivilegedAccessHelper.getClassForName(XML_EVENT_WRITER_CLASS_NAME);
            xmlEventWriterRecordConstructor = PrivilegedAccessHelper.getConstructorFor(eventWriterRecordClass, new Class[]{eventWriterClass}, true);
            
            domToStreamWriterClass = PrivilegedAccessHelper.getClassForName(DOM_TO_STREAM_WRITER_CLASS_NAME);
            writeToStreamMethod = PrivilegedAccessHelper.getMethod(domToStreamWriterClass, WRITE_TO_STREAM_METHOD_NAME, new Class[] {ClassConstants.NODE, streamWriterClass}, true);
            
            domToEventWriterClass = PrivilegedAccessHelper.getClassForName(DOM_TO_EVENT_WRITER_CLASS_NAME);
            writeToEventWriterMethod = PrivilegedAccessHelper.getMethod(domToEventWriterClass, WRITE_TO_EVENT_WRITER_METHOD_NAME, new Class[] {ClassConstants.NODE, eventWriterClass}, true);
            
        } catch (Exception ex) {
            // Do nothing
        }
    }

    /**
    * Create a new XMLMarshaller based on the specified session
    * @param session A single session
    */
    public XMLMarshaller(XMLContext xmlContext) {
        setXMLContext(xmlContext);
        initialize();
    }

    private void initialize() {
        XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
        transformer = xmlPlatform.newXMLTransformer();
        setEncoding(XMLConstants.DEFAULT_XML_ENCODING);
        setFormattedOutput(true);
        marshalProperties = new Properties();
        includeRoot = true;
        indentString = "   "; // default indent is three spaces
    }

    /**
     * Return the instance of XMLContext that was used to create this instance
     * of XMLMarshaller.
     */
    public XMLContext getXMLContext() {
        return xmlContext;
    }

    /**
     * Set the XMLContext used by this instance of XMLMarshaller.
     */
    public void setXMLContext(XMLContext value) {
        xmlContext = value;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    /**
    * Returns if this XMLMarshaller should format the XML
    * By default this is set to true and the XML marshalled will be formatted.
    * @return if this XMLMarshaller should format the XML
    */
    public boolean isFormattedOutput() {
        return transformer.isFormattedOutput();
    }

    /**
    * Set if this XMLMarshaller should format the XML
    * By default this is set to true and the XML marshalled will be formatted.
    * @param shouldFormat if this XMLMarshaller should format the XML
    */
    public void setFormattedOutput(boolean shouldFormat) {
        transformer.setFormattedOutput(shouldFormat);
    }

    /**
       * Get the encoding set on this XMLMarshaller
       * If the encoding has not been set the default UTF-8 will be used
       * @return the encoding set on this XMLMarshaller
       */
    public String getEncoding() {
        return transformer.getEncoding();
    }

    /**
       * Set the encoding on this XMLMarshaller
       * If the encoding is not set the default UTF-8 will be used
       * @param newEncoding the encoding to set on this XMLMarshaller
       */
    public void setEncoding(String newEncoding) {
        transformer.setEncoding(newEncoding);        
    }

    /**
     * Set the MediaType for this xmlMarshaller.
     * See org.eclipse.persistence.oxm.MediaType for the media types supported by EclipseLink MOXy
     * @param mediaType
     */
    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }    
    
    /**
     * Get the MediaType for this xmlMarshaller.
     * See org.eclipse.persistence.oxm.MediaType for the media types supported by EclipseLink MOXy
     * If not set the default is MediaType.APPLICATION_XML
     * @return MediaType
     */
    public MediaType getMediaType(){
    	return mediaType;
    }

    /**
      * Get the schema location set on this XMLMarshaller
      * @return the schema location specified on this XMLMarshaller
      */
    public String getSchemaLocation() {
        return schemaLocation;
    }

    /**
      * Set the schema location on this XMLMarshaller
      * @param newSchemaLocation the schema location to be seton this XMLMarshaller
      */
    public void setSchemaLocation(String newSchemaLocation) {
        schemaLocation = newSchemaLocation;
    }

    /**
      * Get the no namespace schema location set on this XMLMarshaller
      * @return the no namespace schema location specified on this XMLMarshaller
      */
    public String getNoNamespaceSchemaLocation() {
        return noNamespaceSchemaLocation;
    }

    /**
     * Return a properties object for a given instance of the
     * XMLMarshaller.
     *
     * @return
     */
    public Properties getProperties() {
        return marshalProperties;
    }

    /**
     * Return the property for a given key, if one exists.
     *
     * @parm key
     * @return
     */
    public Object getProperty(Object key) {
        return marshalProperties.get(key);
    }

    /**
      * Set the no namespace schema location on this XMLMarshaller
      * @param newNoNamespaceSchemaLocation no namespace schema location to be seton this XMLMarshaller
      */
    public void setNoNamespaceSchemaLocation(String newNoNamespaceSchemaLocation) {
        noNamespaceSchemaLocation = newNoNamespaceSchemaLocation;
    }

    public void setXMLMarshalHandler(XMLMarshalListener marshalListener) {
        this.marshalListener = marshalListener;
    }

    public XMLMarshalListener getMarshalListener() {
        return this.marshalListener;
    }

    public void setMarshalListener(XMLMarshalListener listener) {
        this.marshalListener = listener;
    }

    /**
    * PUBLIC:
    * Convert the given object to XML and update the given result with that XML Document
    * @param object the object to marshal
    * @param result the result to marshal the object to
    * @throws XMLMarshalException if an error occurred during marshalling
    */
    public void marshal(Object object, Result result) throws XMLMarshalException {
        if ((object == null) || (result == null)) {
            throw XMLMarshalException.nullArgumentException();
        }

        boolean isXMLRoot = (object instanceof XMLRoot);
        XMLDescriptor xmlDescriptor = getDescriptor(object, isXMLRoot);

        AbstractSession session = xmlContext.getSession(xmlDescriptor);
        //if this is a simple xml root, the session and descriptor will be null
        if (!(isXMLRoot && ((XMLRoot)object).getObject() instanceof Node) && ((session == null) || !xmlContext.getDocumentPreservationPolicy(session).shouldPreserveDocument())) {
            if (result instanceof DOMResult) {
                DOMResult domResult = (DOMResult) result;
                // handle case where the node is null
                if (domResult.getNode() == null) {
                    domResult.setNode(this.objectToXML(object));
                } else {
                    marshal(object, domResult.getNode());
                }
            } else if (result instanceof SAXResult) {
                SAXResult saxResult = (SAXResult) result;
                marshal(object, saxResult.getHandler());
            } else if (result instanceof StreamResult) {
                StreamResult streamResult = (StreamResult) result;
                Writer writer = streamResult.getWriter();
                if (writer != null) {
                    marshal(object, writer);
                } else if (streamResult.getOutputStream() != null) {
                    marshal(object, streamResult.getOutputStream());
                } else {
                    try {
                        File f = new File(new URL(streamResult.getSystemId()).toURI());
                        writer = new FileWriter(f);
                        try {
                            marshal(object, writer);
                        } finally {
                            writer.close();
                        }
                    } catch (Exception e) {
                        throw XMLMarshalException.marshalException(e);
                    }
                }
            } else {
                if (result.getClass().equals(staxResultClass)) {
                    try {
                        Object xmlStreamWriter = PrivilegedAccessHelper.invokeMethod(staxResultGetStreamWriterMethod, result);
                        if (xmlStreamWriter != null) {
                            MarshalRecord record = (MarshalRecord)PrivilegedAccessHelper.invokeConstructor(xmlStreamWriterRecordConstructor, new Object[]{xmlStreamWriter});
                            record.setMarshaller(this);
                            marshal(object, record, session, xmlDescriptor, isXMLRoot);                            
                            return;
                        } else {
                            Object xmlEventWriter = PrivilegedAccessHelper.invokeMethod(staxResultGetEventWriterMethod, result);
                            if(xmlEventWriter != null) {
                                MarshalRecord record = (MarshalRecord)PrivilegedAccessHelper.invokeConstructor(xmlEventWriterRecordConstructor, new Object[]{xmlEventWriter});
                                record.setMarshaller(this);
                                marshal(object, record, session, xmlDescriptor, isXMLRoot);
                                return;
                            }
                        }
                    } catch (Exception e) {
                        throw XMLMarshalException.marshalException(e);
                    }
                }
                java.io.StringWriter writer = new java.io.StringWriter();
                marshal(object, writer);
                javax.xml.transform.stream.StreamSource source = new javax.xml.transform.stream.StreamSource(new java.io.StringReader(writer.toString()));
                transformer.transform(source, result);
            }
            return;
        }
        try {
            Document document = null;
            if(isXMLRoot && session == null) {
                document = ((Node)((XMLRoot)object).getObject()).getOwnerDocument();
            } else {
                document = objectToXML(object, xmlDescriptor, isXMLRoot);
            }
            if ((result instanceof SAXResult) && (isFragment())) {
                FragmentContentHandler fragmentHandler = new FragmentContentHandler(((SAXResult) result).getHandler());
                if (isXMLRoot) {
                    String oldEncoding = transformer.getEncoding();
                    String oldVersion = transformer.getVersion();
                    if (((XMLRoot) object).getEncoding() != null) {
                        transformer.setEncoding(((XMLRoot) object).getEncoding());
                    }
                    if (((XMLRoot) object).getXMLVersion() != null) {
                        transformer.setVersion(((XMLRoot) object).getXMLVersion());
                    }
                    transformer.transform(document, fragmentHandler);
                    transformer.setEncoding(oldEncoding);
                    transformer.setVersion(oldVersion);
                } else {
                    transformer.transform(document, fragmentHandler);

                }
            } else {
                if (result.getClass().equals(staxResultClass)) {
                    try {
                        Object xmlStreamWriter = PrivilegedAccessHelper.invokeMethod(staxResultGetStreamWriterMethod, result);
                        if (xmlStreamWriter != null) {
                            Object domtostax = PrivilegedAccessHelper.newInstanceFromClass(domToStreamWriterClass);
                            PrivilegedAccessHelper.invokeMethod(writeToStreamMethod, domtostax, new Object[]{document, xmlStreamWriter});
                            return;
                        } else {
                            Object xmlEventWriter = PrivilegedAccessHelper.invokeMethod(staxResultGetEventWriterMethod, result);
                            if(xmlEventWriter != null) {
                                Object domToEventWriter = PrivilegedAccessHelper.newInstanceFromClass(domToEventWriterClass);
                                PrivilegedAccessHelper.invokeMethod(writeToEventWriterMethod, domToEventWriter, new Object[]{document, xmlEventWriter});
                                return;
                            }
                        }

                    } catch(Exception e) {
                        throw XMLMarshalException.marshalException(e);
                    }
                }
                if (isXMLRoot) {
                    String oldEncoding = transformer.getEncoding();
                    String oldVersion = transformer.getVersion();
                    if (((XMLRoot) object).getEncoding() != null) {
                        transformer.setEncoding(((XMLRoot) object).getEncoding());
                    }
                    if (((XMLRoot) object).getXMLVersion() != null) {
                        transformer.setVersion(((XMLRoot) object).getXMLVersion());
                    }
                    transformer.transform(document, result);
                    transformer.setEncoding(oldEncoding);
                    transformer.setVersion(oldVersion);
                } else {
                    transformer.transform(document, result);
                }
            }
        } catch (XMLPlatformException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
    * PUBLIC:
    * Convert the given object to XML and update the given outputStream with that XML Document
    * @param object the object to marshal
    * @param outputStream the outputStream to marshal the object to
    * @throws XMLMarshalException if an error occurred during marshalling
    */
    public void marshal(Object object, OutputStream outputStream) throws XMLMarshalException {
        if ((object == null) || (outputStream == null)) {
            throw XMLMarshalException.nullArgumentException();
        }
        try {
            String encoding = getEncoding();
            boolean isXMLRoot = false;
            String version = DEFAULT_XML_VERSION;
            if (object instanceof XMLRoot) {
                isXMLRoot = true;
                XMLRoot xroot = (XMLRoot) object;
                version = xroot.getXMLVersion() != null ? xroot.getXMLVersion() : version;
                encoding = xroot.getEncoding() != null ? xroot.getEncoding() : encoding;
            }
            if(MediaType.APPLICATION_JSON == mediaType) {
                marshal(object, new OutputStreamWriter(outputStream, encoding));
                return;
            }
            if(encoding.equals(XMLConstants.DEFAULT_XML_ENCODING)){
                AbstractSession session = null;
                XMLDescriptor xmlDescriptor = null;
                if(isXMLRoot){
                    try{
            	        session = xmlContext.getSession(((XMLRoot)object).getObject());
            	        if(session != null){
            	            xmlDescriptor = getDescriptor(((XMLRoot)object).getObject(), session);
                	    }
                   }catch (XMLMarshalException marshalException) {
                        if (!isSimpleXMLRoot((XMLRoot) object)) {
                        	throw marshalException;    
                        }                
                    }
                }else{
                	session = xmlContext.getSession(object.getClass());
                	xmlDescriptor = getDescriptor(object.getClass(), session);
                }
            
                OutputStreamRecord record;
                if (isFormattedOutput()) {
                	record = new FormattedOutputStreamRecord();                	
                } else {
            	    record = new OutputStreamRecord();                	
                }
                       
                record.setMarshaller(this);
                record.setOutputStream(outputStream);
            
                //if this is a simple xml root, the session and descriptor will be null
                if (!(isXMLRoot && ((XMLRoot)object).getObject() instanceof Node) && ((session == null) || !xmlContext.getDocumentPreservationPolicy(session).shouldPreserveDocument())) {
                    marshal(object, record, session, xmlDescriptor, isXMLRoot);    
                } else {
                    try {
                        Node xmlDocument = null;
                        if(isXMLRoot && session == null) {
                            xmlDocument = (Node)((XMLRoot)object).getObject();
                        } else {
                            xmlDocument = objectToXMLNode(object, session, xmlDescriptor, isXMLRoot);
                        }
                        record.setSession(session);
                        if (isFragment()) {
                            record.node(xmlDocument, xmlDescriptor.getNamespaceResolver());
                        } else {
                            record.startDocument(encoding, version);
                            record.node(xmlDocument, record.getNamespaceResolver());
                            record.endDocument();
                        }
                    } catch (XMLPlatformException e) {
                        throw XMLMarshalException.marshalException(e);
                    }
                }
                record.flush();
            }else{
            	OutputStreamWriter writer = new OutputStreamWriter(outputStream, encoding);
                marshal(object, writer);
                writer.flush();
            }
        } catch (UnsupportedEncodingException exception) {
            throw XMLMarshalException.marshalException(exception);
        } catch (Exception ex) {
            throw XMLMarshalException.marshalException(ex);
        }
    }

    /**
    * PUBLIC:
    * Convert the given object to XML and update the given writer with that XML Document
    * @param object the object to marshal
    * @param writer the writer to marshal the object to
    * @throws XMLMarshalException if an error occurred during marshalling
    */
    public void marshal(Object object, Writer writer) throws XMLMarshalException {
        if ((object == null) || (writer == null)) {
            throw XMLMarshalException.nullArgumentException();
        }
        boolean isXMLRoot = false;
        String version = DEFAULT_XML_VERSION;
        String encoding = getEncoding();
        if (object instanceof XMLRoot) {
            isXMLRoot = true;
            XMLRoot xroot = (XMLRoot) object;
            version = xroot.getXMLVersion() != null ? xroot.getXMLVersion() : version;
            encoding = xroot.getEncoding() != null ? xroot.getEncoding() : encoding;
        }

        MarshalRecord writerRecord;
        writer = new BufferedWriter(writer);
        if (isFormattedOutput()) {
            if(MediaType.APPLICATION_JSON == mediaType) {
                writerRecord = new JSONFormattedWriterRecord();
                ((JSONFormattedWriterRecord) writerRecord).setWriter(writer);
            } else {
                writerRecord = new FormattedWriterRecord();
                ((FormattedWriterRecord) writerRecord).setWriter(writer);
            }
        } else {
            if(MediaType.APPLICATION_JSON == mediaType) {
                writerRecord = new JSONWriterRecord();
                ((JSONWriterRecord) writerRecord).setWriter(writer);
            } else {
                writerRecord = new WriterRecord();
                ((WriterRecord) writerRecord).setWriter(writer);
            }
        }
        writerRecord.setMarshaller(this);

        AbstractSession session = null;
        XMLDescriptor xmlDescriptor = null;
        if(isXMLRoot){
            try{
                session = xmlContext.getSession(((XMLRoot)object).getObject());
                if(session != null){
                    xmlDescriptor = getDescriptor(((XMLRoot)object).getObject(), session);
                }
            }catch (XMLMarshalException marshalException) {
                if (!isSimpleXMLRoot((XMLRoot) object)) {
                    throw marshalException;
                }
            }
        }else{
            if(object instanceof Collection) {
                try {
                    writerRecord.startCollection();
                    for(Object o : (Collection) object) {
                        marshal(o, writerRecord);
                    }
                    writerRecord.endCollection();
                    writer.flush();
                } catch(IOException e) {
                    throw XMLMarshalException.marshalException(e);
                }
                return;
            }
            session = xmlContext.getSession(object.getClass());
            xmlDescriptor = getDescriptor(object.getClass(), session);
        }

        //if this is a simple xml root, the session and descriptor will be null
        if (!(isXMLRoot && ((XMLRoot)object).getObject() instanceof Node) && ((session == null) || !xmlContext.getDocumentPreservationPolicy(session).shouldPreserveDocument())) {
            marshal(object, writerRecord, session, xmlDescriptor, isXMLRoot);    
        } else {
            try {
                Node xmlDocument = null;
                if(isXMLRoot && session == null) {
                    xmlDocument = (Node)((XMLRoot)object).getObject();
                } else {
                    xmlDocument = objectToXMLNode(object, session, xmlDescriptor, isXMLRoot);
                }
                writerRecord.setSession(session);
                if (isFragment()) {
                    writerRecord.node(xmlDocument, xmlDescriptor.getNamespaceResolver());
                } else {
                    writerRecord.startDocument(encoding, version);
                    writerRecord.node(xmlDocument, writerRecord.getNamespaceResolver());
                    writerRecord.endDocument();
                }
            } catch (XMLPlatformException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }
        try {
            writer.flush();
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
    * PUBLIC:
    * Convert the given object to XML and update the given contentHandler with that XML Document
    * @param object the object to marshal
    * @param contentHandler the contentHandler which the specified object should be marshalled to
    * @throws XMLMarshalException if an error occurred during marshalling
    */
    public void marshal(Object object, ContentHandler contentHandler) throws XMLMarshalException {
        marshal(object, contentHandler, null);
    }

    /**
     * PUBLIC:
     * Convert the given object to XML and update the given contentHandler with that XML Document
     * @param object the object to marshal
     * @param contentHandler the contentHandler which the specified object should be marshalled to
     * @throws XMLMarshalException if an error occurred during marshalling
     */
    public void marshal(Object object, ContentHandler contentHandler, LexicalHandler lexicalHandler) throws XMLMarshalException {
        if ((object == null) || (contentHandler == null)) {
            throw XMLMarshalException.nullArgumentException();
        }
        boolean isXMLRoot = (object instanceof XMLRoot);
        
        AbstractSession session = null;
        XMLDescriptor xmlDescriptor = null;
        if(isXMLRoot){
        	try{
        	    session = xmlContext.getSession(((XMLRoot)object).getObject());        	    
        	    if(session != null){
        	        xmlDescriptor = getDescriptor(((XMLRoot)object).getObject(), session);
        	    }
        	}catch (XMLMarshalException marshalException) {
                if (!isSimpleXMLRoot((XMLRoot) object)) {
                	throw marshalException;    
                }                
            }
        }else{
        	session = xmlContext.getSession(object.getClass());
        	xmlDescriptor = getDescriptor(object.getClass(), session);
        }
        
        //if it's a simple xml root then session and descriptor will be null
        if (!(isXMLRoot && ((XMLRoot)object).getObject() instanceof Node) && ((session == null) || !xmlContext.getDocumentPreservationPolicy(session).shouldPreserveDocument())) {
            ContentHandlerRecord contentHandlerRecord = new ContentHandlerRecord();
            contentHandlerRecord.setMarshaller(this);
            contentHandlerRecord.setContentHandler(contentHandler);
            contentHandlerRecord.setLexicalHandler(lexicalHandler);
            marshal(object, contentHandlerRecord, session, xmlDescriptor,isXMLRoot);
            return;
        }

        try {
            Node xmlDocument = null; 
            if(session == null) {
                //indicated we're marshalling a node
                xmlDocument = (Node)((XMLRoot)object).getObject();
            } else {
                xmlDocument = objectToXML(object, xmlDescriptor, isXMLRoot);
            }
            DOMReader reader = new DOMReader();
            reader.setProperty("http://xml.org/sax/properties/lexical-handler", lexicalHandler);
            if (isFragment()) {
                FragmentContentHandler fragmentHandler = new FragmentContentHandler(contentHandler);
                reader.setContentHandler(fragmentHandler);
            } else {
                reader.setContentHandler(contentHandler);
            }
            reader.parse(xmlDocument);
        } catch (XMLPlatformException e) {
            throw XMLMarshalException.marshalException(e);
        } catch (org.xml.sax.SAXNotRecognizedException e) {
            //won't be thrown
        } catch (SAXNotSupportedException e) {
            //won't be thrown
        } catch (org.xml.sax.SAXException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
    * PUBLIC:
    * Convert the given object to XML and update the given node with that XML Document
    * @param object the object to marshal
    * @param node the node which the specified object should be marshalled to
    * @throws XMLMarshalException if an error occurred during marshalling
    */
    public void marshal(Object object, Node node) throws XMLMarshalException {
        if ((object == null) || (node == null)) {
            throw XMLMarshalException.nullArgumentException();
        }
        try {
            boolean isXMLRoot = (object instanceof XMLRoot);
                                   
            AbstractSession session = null;
            XMLDescriptor xmlDescriptor = null;
            if(isXMLRoot){
            	try{
            	    session = xmlContext.getSession(((XMLRoot)object).getObject());
            	    if(session != null){
            	        xmlDescriptor = getDescriptor(((XMLRoot)object).getObject(), session);
            	    }
            	}catch (XMLMarshalException marshalException) {
                    if (!isSimpleXMLRoot((XMLRoot) object)) {
                    	throw marshalException;    
                    }                
                }
            }else{
            	session = xmlContext.getSession(object.getClass());
            	xmlDescriptor = getDescriptor(object.getClass(), session);
            }
            
            
            //if this is a simple xml root, descriptor and session will be null
            if (!(isXMLRoot && ((XMLRoot)object).getObject() instanceof Node) && ((session == null) || !xmlContext.getDocumentPreservationPolicy(session).shouldPreserveDocument())) {
                NodeRecord nodeRecord = new NodeRecord(node);
                nodeRecord.setMarshaller(this);

                if (!isXMLRoot) {
                    if ((null == xmlDescriptor.getDefaultRootElement()) && (node.getNodeType() == Node.ELEMENT_NODE) && (xmlDescriptor.getSchemaReference() != null) && (xmlDescriptor.getSchemaReference().getType() == XMLSchemaReference.COMPLEX_TYPE)) {
                        Attr typeAttr = ((Element) node).getAttributeNodeNS(XMLConstants.SCHEMA_INSTANCE_URL, XMLConstants.SCHEMA_TYPE_ATTRIBUTE);
                        if (typeAttr == null) {
                            NamespaceResolver namespaceResolver = xmlDescriptor.getNamespaceResolver();
                            String xsiPrefix = null;
                            if (null == namespaceResolver) {
                                namespaceResolver = new NamespaceResolver();
                                xmlDescriptor.setNamespaceResolver(namespaceResolver);
                            } else {
                                xsiPrefix = namespaceResolver.resolveNamespaceURI(XMLConstants.SCHEMA_INSTANCE_URL);
                            }

                            if (null == xsiPrefix) {
                                xsiPrefix = namespaceResolver.generatePrefix(XMLConstants.SCHEMA_INSTANCE_PREFIX);
                            }

                            String value = xmlDescriptor.getSchemaReference().getSchemaContext();

                            ((Element) node).setAttributeNS(XMLConstants.XMLNS_URL, XMLConstants.XMLNS + XMLConstants.COLON + xsiPrefix, XMLConstants.SCHEMA_INSTANCE_URL);
                            ((Element) node).setAttributeNS(XMLConstants.SCHEMA_INSTANCE_URL, xsiPrefix + XMLConstants.COLON + XMLConstants.SCHEMA_TYPE_ATTRIBUTE, value);

                        } else {
                            String value = xmlDescriptor.getSchemaReference().getSchemaContext();
                            typeAttr.setValue(value);
                        }
                    }
                }
                marshal(object, nodeRecord, session, xmlDescriptor, isXMLRoot);
                return;
            }

            //If preserving document, may return the cached doc. Need to
            //Copy contents of the cached doc to the supplied node.
            Node doc = null;
            if(isXMLRoot && session == null) {
                doc = (Node)((XMLRoot)object).getObject();
            } else {
                doc = objectToXMLNode(object, node, session, xmlDescriptor, isXMLRoot);
            }
            DOMResult result = new DOMResult(node);
            if (isXMLRoot) {
                String oldEncoding = transformer.getEncoding();
                String oldVersion = transformer.getVersion();
                if (((XMLRoot) object).getEncoding() != null) {
                    transformer.setEncoding(((XMLRoot) object).getEncoding());
                }
                if (((XMLRoot) object).getXMLVersion() != null) {
                    transformer.setVersion(((XMLRoot) object).getXMLVersion());
                }
                transformer.transform(doc, result);
                if(oldEncoding != null){
                    transformer.setEncoding(oldEncoding);
                }
                if(oldVersion !=null){
                    transformer.setVersion(oldVersion);
                }
            } else {
                transformer.transform(doc, result);
            }
        } catch (Exception exception) {
            if (exception instanceof XMLMarshalException) {
                throw (XMLMarshalException) exception;
            } else {
                throw XMLMarshalException.marshalException(exception);
            }
        }
    }

    /**
     * Convert the given object to XML and update the given marshal record with
     * that XML Document.
     * @param object the object to marshal
     * @param marshalRecord the marshalRecord to marshal the object to
     */
    public void marshal(Object object, MarshalRecord marshalRecord) {
        boolean isXMLRoot = (object instanceof XMLRoot);
        
        AbstractSession session = null;
        XMLDescriptor xmlDescriptor = null;
        if(isXMLRoot){
        	try{
        	    session = xmlContext.getSession(((XMLRoot)object).getObject());
        	    if(session != null){
        	        xmlDescriptor = getDescriptor(((XMLRoot)object).getObject(), session);
        	    }
        	}catch (XMLMarshalException marshalException) {
                if (!isSimpleXMLRoot((XMLRoot) object)) {
                	throw marshalException;    
                }                
            }
        }else{
        	session = xmlContext.getSession(object);
        	xmlDescriptor = getDescriptor(object, session);
        }
        
        marshal(object, marshalRecord, session, xmlDescriptor, isXMLRoot);
    }
    
    /**
     * Convert the given object to XML and update the given marshal record with
     * that XML Document.
     * @param object the object to marshal
     * @param marshalRecord the marshalRecord to marshal the object to
     */
    protected void marshal(Object object, AbstractSession session, MarshalRecord marshalRecord) {
        boolean isXMLRoot = (object instanceof XMLRoot);
        marshal(object, marshalRecord, session, getDescriptor(object, isXMLRoot), isXMLRoot);
    }

    /**
     * Convert the given object to XML and update the given marshal record with
     * that XML Document.
     * @param object the object to marshal
     * @param marshalRecord the marshalRecord to marshal the object to
     * @param descriptor the XMLDescriptor for the object being marshalled
     */
    private void marshal(Object object, MarshalRecord marshalRecord, AbstractSession session, XMLDescriptor descriptor, boolean isXMLRoot) {
        if(null != schema) {
            marshalRecord = new ValidatingMarshalRecord(marshalRecord, this);
        }
        if (getAttachmentMarshaller() != null) {
            marshalRecord.setXOPPackage(getAttachmentMarshaller().isXOPPackage());
        }
        marshalRecord.setMarshaller(this);
        
        if(this.mapper == null) {
            addDescriptorNamespacesToXMLRecord(descriptor, marshalRecord);
        } else if(this.mapper != null) {
            if(descriptor == null){
                marshalRecord.setNamespaceResolver(new PrefixMapperNamespaceResolver(mapper, null));
            }else{
                marshalRecord.setNamespaceResolver(new PrefixMapperNamespaceResolver(mapper, descriptor.getNamespaceResolver()));
            }
            marshalRecord.setCustomNamespaceMapper(true);
        }
        
        NamespaceResolver nr = marshalRecord.getNamespaceResolver();
        XMLRoot root = null;
        if(isXMLRoot) {
            root = (XMLRoot)object;
        }
        marshalRecord.beforeContainmentMarshal(object);
        if (!isFragment()) {
            String encoding = getEncoding();
            String version = DEFAULT_XML_VERSION;
            if (!isXMLRoot && descriptor!= null) {
                marshalRecord.setLeafElementType(descriptor.getDefaultRootElementType());
            } else {
                if (root.getEncoding() != null) {
                    encoding = root.getEncoding();
                }
                if (root.getXMLVersion() != null) {
                    version = root.getXMLVersion();
                }
            }
            marshalRecord.startDocument(encoding, version);
        }
        if(isXMLRoot) {
            if(root.getObject() instanceof Node) {
                marshalRecord.node((Node)root.getObject(), new NamespaceResolver());
                marshalRecord.endDocument();
                return;
            }
        }
        XPathFragment rootFragment = buildRootFragment(object, descriptor, isXMLRoot, marshalRecord);
        
        String schemaLocation = getSchemaLocation();
        String noNsSchemaLocation = getNoNamespaceSchemaLocation();
        boolean isNil = false;
        if (isXMLRoot) {
            object = root.getObject();
            if (root.getSchemaLocation() != null) {
                schemaLocation = root.getSchemaLocation();
            }
            if (root.getNoNamespaceSchemaLocation() != null) {
                noNsSchemaLocation = root.getNoNamespaceSchemaLocation();
            }
            marshalRecord.setLeafElementType(root.getSchemaType());
            isNil = root.isNil();
        }
           
        String xsiPrefix = null;
        if ((null != getSchemaLocation()) || (null != getNoNamespaceSchemaLocation()) || (isNil)) {
            xsiPrefix = nr.resolveNamespaceURI(XMLConstants.SCHEMA_INSTANCE_URL);
            if (null == xsiPrefix) {
                xsiPrefix = XMLConstants.SCHEMA_INSTANCE_PREFIX;
                nr.put(XMLConstants.SCHEMA_INSTANCE_PREFIX, XMLConstants.SCHEMA_INSTANCE_URL);
            }
        }        

        TreeObjectBuilder treeObjectBuilder = null;
        if (descriptor != null) {
        	treeObjectBuilder = (TreeObjectBuilder) descriptor.getObjectBuilder();     
        }
        if(session == null){
        	session = (AbstractSession) xmlContext.getSession(0);
        }
        marshalRecord.setSession(session);
        
        if (null != rootFragment && !(rootFragment.getLocalName().equals(XMLConstants.EMPTY_STRING))) {
            marshalRecord.startPrefixMappings(nr);
            if (!isXMLRoot && descriptor != null && descriptor.getNamespaceResolver() == null && rootFragment.hasNamespace()) {
                // throw an exception if the name has a : in it but the namespaceresolver is null
                throw XMLMarshalException.namespaceResolverNotSpecified(rootFragment.getShortName());
            }
            
            if(isIncludeRoot()){
                marshalRecord.openStartElement(rootFragment, nr);
            }
            if (null != schemaLocation) {
                marshalRecord.attribute(XMLConstants.SCHEMA_INSTANCE_URL, XMLConstants.SCHEMA_LOCATION, xsiPrefix + XMLConstants.COLON + XMLConstants.SCHEMA_LOCATION, schemaLocation);
            }
            if (null != noNsSchemaLocation) {
                marshalRecord.attribute(XMLConstants.SCHEMA_INSTANCE_URL, XMLConstants.NO_NS_SCHEMA_LOCATION, xsiPrefix + XMLConstants.COLON + XMLConstants.NO_NS_SCHEMA_LOCATION, noNsSchemaLocation);
            }
            if (isNil) {
                marshalRecord.nilSimple(nr);
            }

            marshalRecord.namespaceDeclarations(nr);
            
            if (descriptor != null && !isNil) {            	   	        
            	treeObjectBuilder.addXsiTypeAndClassIndicatorIfRequired(marshalRecord, descriptor, null, descriptor.getDefaultRootElementField(), root, object, isXMLRoot, true);
                treeObjectBuilder.marshalAttributes(marshalRecord, object, session);
            }
            
            if(isIncludeRoot()){            	
                marshalRecord.closeStartElement();
            }
        }
        if (treeObjectBuilder != null && !isNil) {
            treeObjectBuilder.buildRow(marshalRecord, object, session, this, rootFragment, WriteType.UNDEFINED);
        } else if (isXMLRoot) {
            //if(null == object) {
                //marshalRecord.attribute(XMLConstants.XMLNS_URL, XMLConstants.SCHEMA_INSTANCE_PREFIX, XMLConstants.XMLNS + XMLConstants.COLON + XMLConstants.SCHEMA_INSTANCE_PREFIX, XMLConstants.SCHEMA_INSTANCE_URL);
                //marshalRecord.attribute(XMLConstants.SCHEMA_INSTANCE_URL, XMLConstants.SCHEMA_NIL_ATTRIBUTE, XMLConstants.SCHEMA_INSTANCE_PREFIX + XMLConstants.COLON + XMLConstants.SCHEMA_NIL_ATTRIBUTE, "true");
            if(object != null && !isNil) {
            	 if(root.getDeclaredType() != null && root.getObject() != null && root.getDeclaredType() != root.getObject().getClass()) {
        	        	
            		  QName type = (QName)XMLConversionManager.getDefaultJavaTypes().get(object.getClass());
                      if(type != null) {
                    	  xsiPrefix = nr.resolveNamespaceURI(XMLConstants.SCHEMA_INSTANCE_URL);
                          if (null == xsiPrefix) {
                              xsiPrefix = XMLConstants.SCHEMA_INSTANCE_PREFIX;                        	  
                        	  marshalRecord.namespaceDeclaration(xsiPrefix, XMLConstants.SCHEMA_INSTANCE_URL);
                          }                    	  
                    	  marshalRecord.namespaceDeclaration(XMLConstants.SCHEMA_PREFIX, XMLConstants.SCHEMA_URL);
                          marshalRecord.attribute(XMLConstants.SCHEMA_INSTANCE_URL, XMLConstants.SCHEMA_TYPE_ATTRIBUTE, xsiPrefix + XMLConstants.COLON + XMLConstants.SCHEMA_TYPE_ATTRIBUTE, "xsd:" + type.getLocalPart());
                      }
            	 }
            	
                marshalRecord.characters(root.getSchemaType(), object, null, false);
            }
        }

        if (null != rootFragment && !(rootFragment.getLocalName().equals(XMLConstants.EMPTY_STRING)) && isIncludeRoot()) {
            
            marshalRecord.endElement(rootFragment, nr);
            marshalRecord.endPrefixMappings(nr);
        }
        if (!isFragment() ) {
            marshalRecord.endDocument();
        }
        marshalRecord.afterContainmentMarshal(null, object);
    }

    private XPathFragment buildRootFragment(Object object, XMLDescriptor descriptor, boolean isXMLRoot, MarshalRecord marshalRecord) {
        XPathFragment rootFragment = null;
        if (isXMLRoot) {
            String xmlRootUri = ((XMLRoot) object).getNamespaceURI();
            String xmlRootLocalName = ((XMLRoot) object).getLocalName();
            rootFragment = new XPathFragment();
            rootFragment.setLocalName(xmlRootLocalName);
            rootFragment.setNamespaceURI(xmlRootUri);
            
            if (xmlRootUri != null) {
                if (descriptor != null) {
                    String xmlRootPrefix = marshalRecord.getNamespaceResolver().resolveNamespaceURI(xmlRootUri);
                    if (xmlRootPrefix == null && !(xmlRootUri.equals(marshalRecord.getNamespaceResolver().getDefaultNamespaceURI()))) {
                        xmlRootPrefix = marshalRecord.getNamespaceResolver().generatePrefix();
                        marshalRecord.getNamespaceResolver().put(xmlRootPrefix, xmlRootUri);
                    }
                    if(xmlRootPrefix == null) {
                        rootFragment.setXPath(xmlRootLocalName);
                    } else {
                    	 rootFragment.setPrefix(xmlRootPrefix);
                    }
                } else {
                    String xmlRootPrefix = "ns0";
                    marshalRecord.getNamespaceResolver().put(xmlRootPrefix, xmlRootUri);
                    rootFragment.setXPath(xmlRootPrefix + XMLConstants.COLON + xmlRootLocalName);
                }
            }
        } else {
            XMLField defaultRootField = descriptor.getDefaultRootElementField();
            if(defaultRootField != null){            	
            	rootFragment = defaultRootField.getXPathFragment();            	
            }
        }
        return rootFragment;
    }
    
    private boolean isSimpleXMLRoot(XMLRoot xmlRoot) {
        Class xmlRootObjectClass = xmlRoot.getObject().getClass();

        if (XMLConversionManager.getDefaultJavaTypes().get(xmlRootObjectClass) != null || ClassConstants.List_Class.isAssignableFrom(xmlRootObjectClass) || ClassConstants.XML_GREGORIAN_CALENDAR.isAssignableFrom(xmlRootObjectClass) || ClassConstants.DURATION.isAssignableFrom(xmlRootObjectClass)) {
            return true;
        } else if(xmlRoot.getObject() instanceof org.w3c.dom.Node) {
            return true;
        }
        return false;
    }

    /**
    * PUBLIC:
    * Convert the given object to an XML Document
    * @param object the object to marshal
    * @return the document which the specified object has been marshalled to
    * @throws XMLMarshalException if an error occurred during marshalling
    */
    public Document objectToXML(Object object) throws XMLMarshalException {
        boolean isXMLRoot = (object instanceof XMLRoot);
        XMLDescriptor xmlDescriptor = getDescriptor(object, isXMLRoot);
        return objectToXML(object, xmlDescriptor, isXMLRoot);
    }

    /**
    * INTERNAL:
    * Convert the given object to an XML Document
    * @param object the object to marshal
    * @return the document which the specified object has been marshalled to
    * @param descriptor the XMLDescriptor for the object being marshalled
    * @throws XMLMarshalException if an error occurred during marshalling
    */
    protected Document objectToXML(Object object, XMLDescriptor descriptor, boolean isXMLRoot) throws XMLMarshalException {
        AbstractSession session = xmlContext.getSession(descriptor);
        DocumentPreservationPolicy docPresPolicy = xmlContext.getDocumentPreservationPolicy(session);
        if (docPresPolicy != null && docPresPolicy.shouldPreserveDocument()) {
            XMLRecord xmlRow = null;
            if (!isXMLRoot) {
                xmlRow = (XMLRecord) ((XMLObjectBuilder) descriptor.getObjectBuilder()).createRecordFor(object, xmlContext.getDocumentPreservationPolicy(session));
                xmlRow.setMarshaller(this);
                if (getAttachmentMarshaller() != null) {
                    xmlRow.setXOPPackage(getAttachmentMarshaller().isXOPPackage());
                }
                addDescriptorNamespacesToXMLRecord(descriptor, xmlRow);
            }
            return objectToXML(object, descriptor, xmlRow, isXMLRoot, docPresPolicy);
        }
        MarshalRecord marshalRecord = new NodeRecord();
        marshalRecord.setMarshaller(this);
        marshal(object, marshalRecord, session, descriptor, isXMLRoot);
        return marshalRecord.getDocument();
    }

    /**
     * INTERNAL:
     * Like ObjectToXML but is may also return a document fragment instead of a document in the
     * case of a non-root object.
     */
    protected Node objectToXMLNode(Object object, AbstractSession session, XMLDescriptor descriptor, boolean isXMLRoot) throws XMLMarshalException {
        return objectToXMLNode(object, null, session, descriptor, isXMLRoot);
    }

    protected Node objectToXMLNode(Object object, Node rootNode, AbstractSession session,XMLDescriptor descriptor, boolean isXMLRoot) throws XMLMarshalException {
        DocumentPreservationPolicy docPresPolicy = xmlContext.getDocumentPreservationPolicy(session);
        if (docPresPolicy != null && docPresPolicy.shouldPreserveDocument()) {
            XMLRecord xmlRow = null;
            if (!isXMLRoot) {
                xmlRow = (XMLRecord) ((XMLObjectBuilder) descriptor.getObjectBuilder()).createRecordFor(object, xmlContext.getDocumentPreservationPolicy(session));
                xmlRow.setMarshaller(this);
                if (getAttachmentMarshaller() != null) {
                    xmlRow.setXOPPackage(getAttachmentMarshaller().isXOPPackage());
                }
                if (xmlRow.getDOM().getNodeType() == Node.ELEMENT_NODE) {
                    addDescriptorNamespacesToXMLRecord(descriptor, xmlRow);
                }
            }
            Document doc = objectToXML(object, rootNode, descriptor, xmlRow, isXMLRoot, docPresPolicy);
            if ((xmlRow != null) && (xmlRow.getDOM().getNodeType() == Node.DOCUMENT_FRAGMENT_NODE)) {
                return xmlRow.getDOM();
            } else {
                return doc;
            }
        }
        MarshalRecord marshalRecord = new NodeRecord();
        marshalRecord.setMarshaller(this);
        marshalRecord.getNamespaceResolver().setDOM(rootNode);
        marshal(object, marshalRecord, session, descriptor, isXMLRoot);
        return marshalRecord.getDocument();
    }

    private void addDescriptorNamespacesToXMLRecord(XMLDescriptor xmlDescriptor, XMLRecord record) {
        if (null == xmlDescriptor) {
            return;
        }
        copyNamespaces(xmlDescriptor.getNamespaceResolver(), record.getNamespaceResolver());
    }
    
    private void copyNamespaces(NamespaceResolver source, NamespaceResolver target) {
        if (null != source && null != target) {            
            if(source.hasPrefixesToNamespaces()) {
                target.getPrefixesToNamespaces().putAll(source.getPrefixesToNamespaces());
            }
            target.setDefaultNamespaceURI(source.getDefaultNamespaceURI());
        }
    }
    
    /**
    * PUBLIC:
    * Convert the given object to descendants of the parent element
    * @param object the object to marshal
    * @param parent the node to marshal the object to
    * @return the document which the specified object has been marshalled to
    * @throws XMLMarshalException if an error occurred during marshalling
    * @deprecated
    */
    public Document objectToXML(Object object, Node parent) throws XMLMarshalException {
        return objectToXML(object, parent, null);
    }

    public Document objectToXML(Object object, Node parent, DocumentPreservationPolicy docPresPolicy) {
        boolean isXMLRoot = (object instanceof XMLRoot);
        
        AbstractSession session = null;
        XMLDescriptor descriptor = null;
        if(isXMLRoot){
        	try{
        	    session = xmlContext.getSession(((XMLRoot)object).getObject());
        	    if(session != null){
        	        descriptor = getDescriptor(((XMLRoot)object).getObject(), session);
        	    }
        	}catch (XMLMarshalException marshalException) {
                if (!isSimpleXMLRoot((XMLRoot) object)) {
                	throw marshalException;    
                }                
            }
        }else{
        	session = xmlContext.getSession(object);
        	descriptor = getDescriptor(object, session);
        }
        
        

        String localRootName = descriptor.getDefaultRootElement();
        if (null == localRootName) {
            throw XMLMarshalException.defaultRootElementNotSpecified(descriptor);
        }

        if(docPresPolicy == null) {
            docPresPolicy = xmlContext.getDocumentPreservationPolicy(session);
        }
        if (docPresPolicy != null && docPresPolicy.shouldPreserveDocument()) {
            XMLRecord xmlRow = (XMLRecord) ((XMLObjectBuilder) descriptor.getObjectBuilder()).createRecord(localRootName, parent, session);
            xmlRow.setMarshaller(this);
            if (getAttachmentMarshaller() != null) {
                xmlRow.setXOPPackage(getAttachmentMarshaller().isXOPPackage());
            }
            return objectToXML(object, descriptor, xmlRow, isXMLRoot, docPresPolicy);
        }
        MarshalRecord marshalRecord = new NodeRecord(localRootName, parent);
        marshalRecord.setMarshaller(this);
        marshal(object, marshalRecord, session, descriptor, isXMLRoot);
        return marshalRecord.getDocument();

    }

    /**
    * INTERNAL:
    * Convert the given object to an XML Document
    */
    public Document objectToXML(Object object, XMLDescriptor descriptor, XMLRecord xmlRow, boolean isXMLRoot, DocumentPreservationPolicy docPresPolicy) {
        return objectToXML(object, null, descriptor, xmlRow, isXMLRoot, docPresPolicy);
    }

    public Document objectToXML(Object object, Node rootNode, XMLDescriptor descriptor, XMLRecord xmlRow, boolean isXMLRoot, DocumentPreservationPolicy docPresPolicy) {
        if(null != rootNode) {
            int rootNodeType = rootNode.getNodeType();
            if(rootNodeType != Node.DOCUMENT_NODE && rootNodeType != Node.ELEMENT_NODE && rootNodeType != Node.DOCUMENT_FRAGMENT_NODE ) {
                throw XMLMarshalException.marshalException(null);
            }
        }
        Document document = null;
        NamespaceResolver resolver = new NamespaceResolver();
        resolver.setDOM(rootNode);
        this.copyNamespaces(descriptor.getNamespaceResolver(), resolver);
        boolean shouldCallSetAttributeNS = false;
        boolean isRootDocumentFragment = false;
        AbstractSession session = xmlContext.getSession(descriptor);
        if (xmlRow != null) {
            isRootDocumentFragment = (xmlRow.getDOM().getNodeType() == Node.DOCUMENT_FRAGMENT_NODE);
        }
        Object originalObject = object;
        if (isXMLRoot) {
            String xmlRootUri = ((XMLRoot) object).getNamespaceURI();
            String xmlRootPrefix = null;
            if (xmlRow == null) {
                String recordName = ((XMLRoot) object).getLocalName();
                if (xmlRootUri != null) {
                    xmlRootPrefix = resolver.resolveNamespaceURI(xmlRootUri);
                    if (xmlRootPrefix == null && !(xmlRootUri.equals(resolver.getDefaultNamespaceURI()))) {
                        xmlRootPrefix = resolver.generatePrefix();
                        resolver.put(xmlRootPrefix, xmlRootUri);
                        shouldCallSetAttributeNS = true;
                    }
                    if(xmlRootPrefix != null) {
                        recordName = xmlRootPrefix + XMLConstants.COLON + recordName;
                    }
                }
                xmlRow = (XMLRecord) ((XMLObjectBuilder) descriptor.getObjectBuilder()).createRecordFor(((XMLRoot) object).getObject(), docPresPolicy, recordName, xmlRootUri);
                xmlRow.setMarshaller(this);
                if (getAttachmentMarshaller() != null) {
                    xmlRow.setXOPPackage(getAttachmentMarshaller().isXOPPackage());
                }
                if (!isRootDocumentFragment) {
                    if (shouldCallSetAttributeNS) {
                        if (xmlRootPrefix != null) {
                            ((Element) xmlRow.getDOM()).setAttributeNS(XMLConstants.XMLNS_URL, XMLConstants.XMLNS + XMLConstants.COLON + xmlRootPrefix, xmlRootUri);
                        }
                        shouldCallSetAttributeNS = false;
                    }
                }
            }

            copyNamespaces(resolver, xmlRow.getNamespaceResolver());
            document = xmlRow.getDocument();
            Element docElement = document.getDocumentElement();
            object = ((XMLRoot) object).getObject();
        }

        XMLObjectBuilder bldr = (XMLObjectBuilder) descriptor.getObjectBuilder();

        AbstractSession objectSession = xmlContext.getSession(object);
        xmlRow.setSession(objectSession);
        bldr.addXsiTypeAndClassIndicatorIfRequired(xmlRow, descriptor, null, null, originalObject, object, isXMLRoot, true);
        xmlRow = (XMLRecord) bldr.buildRow(xmlRow, object,  objectSession, isXMLRoot);
                
        xmlRow.setMarshaller(this);
        if (shouldCallSetAttributeNS && !isRootDocumentFragment) {
            ((Element) xmlRow.getDOM()).setAttributeNS(XMLConstants.XMLNS_URL, XMLConstants.XMLNS + XMLConstants.COLON + XMLConstants.SCHEMA_INSTANCE_PREFIX, XMLConstants.SCHEMA_INSTANCE_URL);
        }
        document = xmlRow.getDocument();

        addSchemaLocations(document, session);
        return document;
    }

    private void addSchemaLocations(Document document, AbstractSession session) {
        Element docElement = document.getDocumentElement();

        NamespaceResolver resolver = new NamespaceResolver();
        resolver.put(XMLConstants.XMLNS, XMLConstants.XMLNS_URL);
        resolver.put(XMLConstants.SCHEMA_INSTANCE_PREFIX, XMLConstants.SCHEMA_INSTANCE_URL);

        if ((getSchemaLocation() != null) || (getNoNamespaceSchemaLocation() != null)) {
            XMLField field = new XMLField("@xmlns:xsi");
            field.setNamespaceResolver(resolver);
            XPathEngine.getInstance().create(field, docElement, XMLConstants.SCHEMA_INSTANCE_URL, session);
        }
        if (getSchemaLocation() != null) {
            XMLField field = new XMLField("@xsi:" + XMLConstants.SCHEMA_LOCATION);
            field.setNamespaceResolver(resolver);
            XPathEngine.getInstance().create(field, docElement, getSchemaLocation(), session);
        }
        if (getNoNamespaceSchemaLocation() != null) {
            XMLField field = new XMLField("@xsi:" + XMLConstants.NO_NS_SCHEMA_LOCATION);
            field.setNamespaceResolver(resolver);
            XPathEngine.getInstance().create(field, docElement, getNoNamespaceSchemaLocation(), session);
        }
    }

    /**
    * INTERNAL:
    * Return the descriptor for the root object.
    */
    protected XMLDescriptor getDescriptor(Object object) throws XMLMarshalException {
        XMLDescriptor descriptor = (XMLDescriptor) xmlContext.getSession(object).getDescriptor(object);
        if (descriptor == null) {
            throw XMLMarshalException.descriptorNotFoundInProject(object.getClass().getName());
        }

        return descriptor;
    }

    /**
     * INTERNAL:
     * Return the descriptor for the root object.
     */
     protected XMLDescriptor getDescriptor(Object object, AbstractSession session) throws XMLMarshalException {
         XMLDescriptor descriptor = (XMLDescriptor) session.getDescriptor(object);
         if (descriptor == null) {
             throw XMLMarshalException.descriptorNotFoundInProject(object.getClass().getName());
         }

         return descriptor;
     }
     
     /**
      * INTERNAL:
      * Return the descriptor for the root object.
      */
      private XMLDescriptor getDescriptor(Class clazz, AbstractSession session) throws XMLMarshalException {
          XMLDescriptor descriptor = (XMLDescriptor) session.getDescriptor(clazz);
          if (descriptor == null) {
              throw XMLMarshalException.descriptorNotFoundInProject(clazz.getName());
          }

          return descriptor;
      }
    
    protected XMLDescriptor getDescriptor(Object object, boolean isXMLRoot) {
        if (isXMLRoot) {
            return getDescriptor((XMLRoot) object);
        } else {
            return getDescriptor(object);
        }
    }
    
    protected XMLDescriptor getDescriptor(Object object, AbstractSession session, boolean isXMLRoot) {
        if (isXMLRoot) {
            return getDescriptor((XMLRoot) object, session);
        } else {
            return getDescriptor(object, session);
        }
    }
    
    protected XMLDescriptor getDescriptor(XMLRoot object) throws XMLMarshalException {
        XMLDescriptor descriptor = null;

        try {
            AbstractSession session = xmlContext.getSession(object.getObject());
            if(null == session) {
                return null;
            }
            descriptor = (XMLDescriptor) session.getDescriptor(object.getObject());
        } catch (XMLMarshalException marshalException) {
            if ((descriptor == null) && isSimpleXMLRoot(object)) {
                return null;
            }
            throw marshalException;
        }

        if (descriptor == null) {
            throw XMLMarshalException.descriptorNotFoundInProject(object.getClass().getName());
        }

        return descriptor;
    }
    
    protected XMLDescriptor getDescriptor(XMLRoot object, AbstractSession session) throws XMLMarshalException {
        XMLDescriptor descriptor = null;

        try {
            if(null == session) {
                return null;
            }
            descriptor = (XMLDescriptor) session.getDescriptor(object.getObject());
        } catch (XMLMarshalException marshalException) {
            if ((descriptor == null) && isSimpleXMLRoot(object)) {
                return null;
            }
            throw marshalException;
        }

        if (descriptor == null) {
            throw XMLMarshalException.descriptorNotFoundInProject(object.getClass().getName());
        }

        return descriptor;
    }

    /**
    * PUBLIC:
    * Set if this should marshal to a fragment.  If true an XML header string is not written out.
    * @param fragment if this should marshal to a fragment or not
    */
    public void setFragment(boolean fragment) {
        transformer.setFragment(fragment);
    }

    /**
    * PUBLIC:
    * Returns if this should marshal to a fragment.  If true an XML header string is not written out.
    * @return if this should marshal to a fragment or not
    */
    public boolean isFragment() {
        return transformer.isFragment();
    }

    public void setAttachmentMarshaller(XMLAttachmentMarshaller atm) {
        this.attachmentMarshaller = atm;
    }

    public XMLAttachmentMarshaller getAttachmentMarshaller() {
        return this.attachmentMarshaller;
    }

    /**
     * INTERNAL
     * @return the transformer instance for this marshaller
     */
    public XMLTransformer getTransformer() {
        return transformer;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    @Override
    public XMLMarshaller clone() {
        XMLMarshaller clone = new XMLMarshaller(xmlContext);
        clone.setAttachmentMarshaller(attachmentMarshaller);
        clone.setEncoding(getEncoding());
        clone.setFormattedOutput(isFormattedOutput());
        clone.setFragment(isFragment());
        clone.setMarshalListener(marshalListener);
        clone.setNoNamespaceSchemaLocation(noNamespaceSchemaLocation);
        for(Entry entry : marshalProperties.entrySet()) {
            clone.getProperties().put(entry.getKey(), entry.getValue());
        }
        if(null != schema) {
            clone.setSchema(schema);
        }
        clone.setSchemaLocation(schemaLocation);
        return clone;
    }
    /**
     * Value that will be used to prefix attributes.  
     * Ignored marshalling XML.   
     * @return
     * @since 2.4
     */
    public String getAttributePrefix() {
        return attributePrefix;
    }
    
    /**
     * Value that will be used to prefix attributes.  
     * Ignored marshalling XML.
     * @since 2.4	 
     */
    public void setAttributePrefix(String attributePrefix) {
        this.attributePrefix = attributePrefix;
    }
    
    /**
     * Name of the property to marshal/unmarshal as a wrapper on the text() mappings   
     * Ignored marshalling XML.  
     * @since 2.4	 
     */	
    public String getValueWrapper() {
        return valueWrapper;
    }

    /**
     * Name of the property to marshal/unmarshal as a wrapper on the text() mappings   
     * Ignored marshalling XML.  
     * @since 2.4	 
     */
    public void setValueWrapper(String valueWrapper) {
        this.valueWrapper = valueWrapper;
    }
    
    /**
     * Determine if the @XMLRootElement should be marshalled when present.  
     * Ignored marshalling XML.   
     * @return
     * @since 2.4
     */
    public boolean isIncludeRoot() {
        if(mediaType == MediaType.APPLICATION_JSON){
            return includeRoot;
        }
        return true;
    }

    /**
     * Determine if the @XMLRootElement should be marshalled when present.  
     * Ignored marshalling XML.   
     * @return
     * @since 2.4
     */
    public void setIncludeRoot(boolean includeRoot) {
         this.includeRoot = includeRoot;
    }

    /**
     * NamespacePrefixMapper that can be used during marshal (instead of those set in the project meta data)
     * @since 2.3.3
     * @return
     */
    public void setNamespacePrefixMapper(NamespacePrefixMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * NamespacePrefixMapper that can be used during marshal (instead of those set in the project meta data)
     * @since 2.3.3
     * @return
     */
    public NamespacePrefixMapper getNamespacePrefixMapper() {
        return this.mapper;
    }

    /**
     * Return the String that will be used to perform indenting in marshalled documents.
     * Default is &quot;   &quot; (three spaces).
     * @since 2.3.3
     */
    public String getIndentString() {
        return this.indentString;
    }

    /**
     * Set the String that will be used to perform indenting in marshalled documents.
     * @since 2.3.3
     */
    public void setIndentString(String s) {
        this.indentString = s;
    }

    /**
     * Return this Marshaller's CharacterEscapeHandler.
     * @since 2.3.3
     */
    public CharacterEscapeHandler getCharacterEscapeHandler() {
        return this.charEscapeHandler;
    }

    /**
     * Set this Marshaller's CharacterEscapeHandler.
     * @since 2.3.3
     */
    public void setCharacterEscapeHandler(CharacterEscapeHandler c) {
        this.charEscapeHandler = c;
    }

}