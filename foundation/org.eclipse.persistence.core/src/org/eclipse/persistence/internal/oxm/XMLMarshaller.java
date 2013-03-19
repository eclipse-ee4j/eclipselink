/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;

import org.eclipse.persistence.core.queries.CoreAttributeGroup;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.record.AbstractMarshalRecord;
import org.eclipse.persistence.internal.oxm.record.namespaces.PrefixMapperNamespaceResolver;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.oxm.JSONWithPadding;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentMarshaller;
import org.eclipse.persistence.oxm.record.ContentHandlerRecord;
import org.eclipse.persistence.oxm.record.FormattedOutputStreamRecord;
import org.eclipse.persistence.oxm.record.FormattedWriterRecord;
import org.eclipse.persistence.oxm.record.JSONFormattedWriterRecord;
import org.eclipse.persistence.oxm.record.JSONWriterRecord;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.NodeRecord;
import org.eclipse.persistence.oxm.record.OutputStreamRecord;
import org.eclipse.persistence.oxm.record.ValidatingMarshalRecord;
import org.eclipse.persistence.oxm.record.WriterRecord;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.platform.xml.XMLTransformer;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

public abstract class XMLMarshaller<
    ABSTRACT_SESSION extends CoreAbstractSession,
    CONTEXT extends Context<ABSTRACT_SESSION, DESCRIPTOR, ?, ?, ?, ?, ?>,
    DESCRIPTOR extends Descriptor,
    MEDIA_TYPE extends MediaType,
    NAMESPACE_PREFIX_MAPPER extends NamespacePrefixMapper,
    OBJECT_BUILDER extends ObjectBuilder<?, ABSTRACT_SESSION, ?, XMLMarshaller>> extends Marshaller<CONTEXT, MEDIA_TYPE, NAMESPACE_PREFIX_MAPPER> {

    protected final static String DEFAULT_XML_VERSION = "1.0";
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

    protected static Class staxResultClass;
    protected static Method staxResultGetStreamWriterMethod;
    protected static Method staxResultGetEventWriterMethod;
    private static Constructor xmlStreamWriterRecordConstructor;
    private static Constructor xmlEventWriterRecordConstructor;
    protected static Method writeToStreamMethod;
    protected static Method writeToEventWriterMethod;
    protected static Class domToStreamWriterClass;
    protected static Class domToEventWriterClass;

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
            writeToStreamMethod = PrivilegedAccessHelper.getMethod(domToStreamWriterClass, WRITE_TO_STREAM_METHOD_NAME, new Class[] {ClassConstants.NODE, ClassConstants.STRING, ClassConstants.STRING, streamWriterClass}, true);
            
            domToEventWriterClass = PrivilegedAccessHelper.getClassForName(DOM_TO_EVENT_WRITER_CLASS_NAME);
            writeToEventWriterMethod = PrivilegedAccessHelper.getMethod(domToEventWriterClass, WRITE_TO_EVENT_WRITER_METHOD_NAME, new Class[] {ClassConstants.NODE, ClassConstants.STRING, ClassConstants.STRING, eventWriterClass}, true);
            
        } catch (Exception ex) {
            // Do nothing
        }
    }

    protected XMLAttachmentMarshaller attachmentMarshaller;
    private String attributePrefix;
    private boolean fragment;
    private boolean includeRoot = true;
    private boolean marshalEmptyCollections = true;
    protected MEDIA_TYPE mediaType;
    private char namespaceSeparator;
    private String noNamespaceSchemaLocation;
    private boolean reduceAnyArrays;
    private Schema schema;
    private String schemaLocation;
    protected XMLTransformer transformer;
    private String valueWrapper;
    private boolean wrapperAsCollectionName = false;
    private String xmlHeader;
    private Object marshalAttributeGroup;

    public XMLMarshaller(CONTEXT context) {
        super(context);
        this.includeRoot = true;
        this.marshalEmptyCollections = true;
        this.namespaceSeparator = Constants.DOT;
        this.reduceAnyArrays = false;
        this.valueWrapper = Constants.VALUE_WRAPPER;
    }

    /**
     * Copy constructor
     */
    protected XMLMarshaller(XMLMarshaller xmlMarshaller) {
        super(xmlMarshaller);
        attachmentMarshaller = xmlMarshaller.getAttachmentMarshaller();
        attributePrefix = xmlMarshaller.getAttributePrefix();
        fragment = xmlMarshaller.isFragment();
        includeRoot = xmlMarshaller.isIncludeRoot();
        marshalEmptyCollections = xmlMarshaller.isMarshalEmptyCollections();
        mediaType = (MEDIA_TYPE) xmlMarshaller.getMediaType();
        namespaceSeparator = xmlMarshaller.getNamespaceSeparator();
        noNamespaceSchemaLocation = xmlMarshaller.getNoNamespaceSchemaLocation();
        reduceAnyArrays = xmlMarshaller.isReduceAnyArrays();
        
        if(null != xmlMarshaller.getSchema()) {
            setSchema(xmlMarshaller.getSchema());
        }

        schemaLocation = xmlMarshaller.getSchemaLocation();
        valueWrapper = xmlMarshaller.getValueWrapper();
        wrapperAsCollectionName = xmlMarshaller.isWrapperAsCollectionName();
        xmlHeader = xmlMarshaller.getXmlHeader();
    }

    protected void addDescriptorNamespacesToXMLRecord(DESCRIPTOR xmlDescriptor, AbstractMarshalRecord record) {
        if (null == xmlDescriptor) {
            return;
        }
        copyNamespaces(xmlDescriptor.getNamespaceResolver(), record.getNamespaceResolver());
    }

    private XPathFragment buildRootFragment(Object object, DESCRIPTOR descriptor, boolean isXMLRoot, MarshalRecord marshalRecord) {
        XPathFragment rootFragment = null;
        if (isXMLRoot) {
            String xmlRootUri = ((Root) object).getNamespaceURI();
            String xmlRootLocalName = ((Root) object).getLocalName();
            rootFragment = new XPathFragment();
            rootFragment.setLocalName(xmlRootLocalName);
            rootFragment.setNamespaceURI(xmlRootUri);
            rootFragment.setNamespaceAware(marshalRecord.isNamespaceAware());
            rootFragment.setNamespaceSeparator(marshalRecord.getNamespaceSeparator());

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
                    if(marshalRecord.isNamespaceAware()){
                        String xmlRootPrefix = "ns0";
                        marshalRecord.getNamespaceResolver().put(xmlRootPrefix, xmlRootUri);
                        rootFragment.setXPath(xmlRootPrefix + marshalRecord.getNamespaceSeparator() + xmlRootLocalName);
                    }else{
                        rootFragment.setXPath(xmlRootLocalName);
                    }
                }
            }
        } else {
            Field defaultRootField = (Field) descriptor.getDefaultRootElementField();
            if(defaultRootField != null){               
                rootFragment = defaultRootField.getXPathFragment();
            }
        }
        return rootFragment;
    }

    protected void copyNamespaces(NamespaceResolver source, NamespaceResolver target) {
        if (null != source && null != target) {
            if(source.hasPrefixesToNamespaces()) {
                target.getPrefixesToNamespaces().putAll(source.getPrefixesToNamespaces());
            }
            target.setDefaultNamespaceURI(source.getDefaultNamespaceURI());
        }
    }

    @Override
    public XMLAttachmentMarshaller getAttachmentMarshaller() {
        return this.attachmentMarshaller;
    }

    /**
     * Value that will be used to prefix attributes.  
     * Ignored marshalling XML.   
     * @since 2.4
     */
    public String getAttributePrefix() {
        return attributePrefix;
    }

    /**
     * INTERNAL:
     * Return the descriptor for the root object.
     */
     protected DESCRIPTOR getDescriptor(Class clazz, ABSTRACT_SESSION session) throws XMLMarshalException {
         DESCRIPTOR descriptor = (DESCRIPTOR) session.getDescriptor(clazz);
         if (descriptor == null) {
             throw XMLMarshalException.descriptorNotFoundInProject(clazz.getName());
         }

         return descriptor;
    }

     /**
      * INTERNAL:
      * Return the descriptor for the root object.
      */
      public DESCRIPTOR getDescriptor(Object object) throws XMLMarshalException {
          DESCRIPTOR descriptor = (DESCRIPTOR) context.getSession(object).getDescriptor(object);
          if (descriptor == null) {
              throw XMLMarshalException.descriptorNotFoundInProject(object.getClass().getName());
          }

          return descriptor;
      }

     /**
      * INTERNAL:
      * Return the descriptor for the root object.
      */
      protected DESCRIPTOR getDescriptor(Object object, ABSTRACT_SESSION session) throws XMLMarshalException {
          DESCRIPTOR descriptor = (DESCRIPTOR) session.getDescriptor(object);
          if (descriptor == null) {
              throw XMLMarshalException.descriptorNotFoundInProject(object.getClass().getName());
          }

          return descriptor;
      }

      protected DESCRIPTOR getDescriptor(Object object, boolean isXMLRoot) {
          if (isXMLRoot) {
              return getDescriptor((Root) object);
          } else {
              return getDescriptor(object);
          }
      }

      protected DESCRIPTOR getDescriptor(Root object) throws XMLMarshalException {
          DESCRIPTOR descriptor = null;

          try {
              ABSTRACT_SESSION session = context.getSession(object.getObject());
              if(null == session) {
                  return null;
              }
              descriptor = (DESCRIPTOR) session.getDescriptor(object.getObject());
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
      
      protected DESCRIPTOR getDescriptor(Root object, ABSTRACT_SESSION session) throws XMLMarshalException {
          DESCRIPTOR descriptor = null;

          try {
              if(null == session) {
                  return null;
              }
              descriptor = (DESCRIPTOR) session.getDescriptor(object.getObject());
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
     * Get the MediaType for this xmlMarshaller.
     * See org.eclipse.persistence.oxm.MediaType for the media types supported by EclipseLink MOXy
     * If not set the default is MediaType.APPLICATION_XML
     * @return MediaType
     */
    @Override
    public MEDIA_TYPE getMediaType(){
        return mediaType;
    }

    protected Node getNode(Object object, Node parentNode, ABSTRACT_SESSION session, DESCRIPTOR descriptor, boolean isRoot) {
        if(isRoot) {
            object = ((Root) object).getObject();
            if(object instanceof Node) {
                return (Node) object;
            }
        }
        return null;
    }

    /**
     * Get the no namespace schema location set on this XMLMarshaller
     * @return the no namespace schema location specified on this XMLMarshaller
     */
    public String getNoNamespaceSchemaLocation() {
       return noNamespaceSchemaLocation;
    }

    public Schema getSchema() {
        return schema;
    }

    /**
     * INTERNAL
     * @return the transformer instance for this marshaller
     */
    @Override
    public XMLTransformer getTransformer() {
        if(null == transformer) {
            XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
            transformer = xmlPlatform.newXMLTransformer();
            transformer.setEncoding(getEncoding());
            transformer.setFormattedOutput(isFormattedOutput());
            transformer.setFragment(fragment);
            
        }
        return transformer;
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
     * Get this Marshaller's XML Header.
     * @since 2.4
     */
    public String getXmlHeader() {
        return xmlHeader;
    }

    /**
     * Get the schema location set on this XMLMarshaller
     * @return the schema location specified on this XMLMarshaller
     */
    public String getSchemaLocation() {
       return schemaLocation;
    }

    /**
     * PUBLIC:
     * Returns if this should marshal to a fragment.  If true an XML header string is not written out.
     * @return if this should marshal to a fragment or not
     */
    public boolean isFragment() {
        return mediaType.isApplicationXML() && fragment;
    }

    /**
     * Determine if the @XMLRootElement should be marshalled when present.  
     * Ignored marshalling XML.   
     * @return
     * @since 2.4
     */
    @Override
    public boolean isIncludeRoot() {
       if(mediaType.isApplicationJSON()){
           return includeRoot;
       }
       return true;
    }

    /**
     * Property to determine if size 1 any collections should be treated as collections
     * Ignored marshalling XML.
     */
    @Override
    public boolean isReduceAnyArrays() {
        return reduceAnyArrays;
    }

    /**
     * Get the namespace separator used during marshal operations.
     * If mediaType is application/json '.' is the default
     * Ignored marshalling XML.   
     * @since 2.4
     */
    public char getNamespaceSeparator() {       
        return namespaceSeparator;
    }

    /**
     * Name of the property to determine if empty collections should be marshalled as []   
     * Ignored marshalling XML.  
     * @since 2.4    
     */
    public boolean isMarshalEmptyCollections() {
        return marshalEmptyCollections;
    }

    public boolean isWrapperAsCollectionName() {
        return wrapperAsCollectionName;
    }

    protected boolean isSimpleXMLRoot(Root xmlRoot) {
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
        if(object instanceof JSONWithPadding && !mediaType.isApplicationJSON()){
            object = ((JSONWithPadding)object).getObject();
        }

        if ((object == null) || (contentHandler == null)) {
            throw XMLMarshalException.nullArgumentException();
        }

        ABSTRACT_SESSION session = null;
        DESCRIPTOR xmlDescriptor = null;

        boolean isXMLRoot = (object instanceof Root);
        if(isXMLRoot){
            try{
                session = context.getSession(((Root)object).getObject());               
                if(session != null){
                    xmlDescriptor = getDescriptor(((Root)object).getObject(), session);
                }
            }catch (XMLMarshalException marshalException) {
                if (!isSimpleXMLRoot((Root) object)) {
                    throw marshalException;    
                }
            }
        }else{
            Class objectClass = object.getClass();
            session = context.getSession(objectClass);
            xmlDescriptor = getDescriptor(objectClass, session);
        }

        ContentHandlerRecord contentHandlerRecord = new ContentHandlerRecord();
        contentHandlerRecord.setMarshaller(this);
        contentHandlerRecord.setContentHandler(contentHandler);
        contentHandlerRecord.setLexicalHandler(lexicalHandler);
        marshal(object, contentHandlerRecord, session, xmlDescriptor,isXMLRoot);
    }

    /**
     * Convert the given object to XML and update the given marshal record with
     * that XML Document.
     * @param object the object to marshal
     * @param marshalRecord the marshalRecord to marshal the object to
     */
    public void marshal(Object object, MarshalRecord marshalRecord) {        
        if(object instanceof JSONWithPadding && !mediaType.isApplicationJSON()){
            object = ((JSONWithPadding)object).getObject();
        }
        if ((object == null) || (marshalRecord == null)) {
            throw XMLMarshalException.nullArgumentException();
        }
        
        boolean isXMLRoot = (object instanceof Root);
        
        ABSTRACT_SESSION session = null;
        DESCRIPTOR xmlDescriptor = null;
        if(isXMLRoot){
            try{
                session = context.getSession(((Root)object).getObject());
                if(session != null){
                    xmlDescriptor = getDescriptor(((Root)object).getObject(), session);
                }
            }catch (XMLMarshalException marshalException) {
                if (!isSimpleXMLRoot((Root) object)) {
                    throw marshalException;
                }
            }
        }else{
            Class objectClass = object.getClass();
            session = context.getSession(objectClass);
            xmlDescriptor = getDescriptor(objectClass, session);
        }

        marshal(object, marshalRecord, session, xmlDescriptor, isXMLRoot);
    }

    /**
     * Convert the given object to XML and update the given marshal record with
     * that XML Document.
     * @param object the object to marshal
     * @param marshalRecord the marshalRecord to marshal the object to
     * @param descriptor the XMLDescriptor for the object being marshalled
     */
    protected void marshal(Object object, MarshalRecord marshalRecord, ABSTRACT_SESSION session, DESCRIPTOR descriptor, boolean isXMLRoot) {
        if(null != schema) {
            marshalRecord = new ValidatingMarshalRecord(marshalRecord, this);
        }
        if (this.attachmentMarshaller != null) {
            marshalRecord.setXOPPackage(this.attachmentMarshaller.isXOPPackage());
        }
        marshalRecord.setMarshaller(this);

        Root root = null;
        if(isXMLRoot) {
            root = (Root) object;
        }
        Node node = getNode(object, marshalRecord.getDOM(), session, descriptor, isXMLRoot);

        if(this.mapper == null) {
            if(null == node) {
                addDescriptorNamespacesToXMLRecord(descriptor, marshalRecord);
            }
        } else {
            if(descriptor == null || null != node){
                marshalRecord.setNamespaceResolver(new PrefixMapperNamespaceResolver(mapper, null));
            }else{
                marshalRecord.setNamespaceResolver(new PrefixMapperNamespaceResolver(mapper, descriptor.getNamespaceResolver()));
            }
            marshalRecord.setCustomNamespaceMapper(true);
        }

        if(this.getMarshalAttributeGroup() != null) {
            if(marshalAttributeGroup.getClass() == ClassConstants.STRING) {
                CoreAttributeGroup group = descriptor.getAttributeGroup((String)marshalAttributeGroup);
                if(group != null) {
                    marshalRecord.pushAttributeGroup(group);
                } else {
                    throw XMLMarshalException.invalidAttributeGroupName((String)marshalAttributeGroup, descriptor.getJavaClassName());
                }
            } else if(marshalAttributeGroup instanceof CoreAttributeGroup) {
                marshalRecord.pushAttributeGroup((CoreAttributeGroup)marshalAttributeGroup);
            } else {
                //Error case
            }
        }
        NamespaceResolver nr = marshalRecord.getNamespaceResolver();
        if(node != null) {
            if(isXMLRoot) {
                if (isFragment()) {
                    marshalRecord.node(node, null,  root.getNamespaceURI(), root.getLocalName());
               } else {
                   String encoding = root.getEncoding();
                   if(null == encoding) {
                       encoding = Constants.DEFAULT_XML_ENCODING;
                   }
                   String version = root.getXMLVersion();
                   if(null == version) {
                       version = DEFAULT_XML_VERSION;
                   }
                   marshalRecord.startDocument(encoding, version);
                   marshalRecord.node(node, marshalRecord.getNamespaceResolver(), root.getNamespaceURI(), root.getLocalName());
                   marshalRecord.endDocument();
               }
            } else {
                marshalRecord.node(node, nr);
            }
            return;
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
        if (getXmlHeader() != null) {
            marshalRecord.writeHeader();
        }
        if(isXMLRoot) {
            if(root.getObject() instanceof Node) {
                marshalRecord.node((Node)root.getObject(), new NamespaceResolver(), root.getNamespaceURI(), root.getLocalName());
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
            xsiPrefix = nr.resolveNamespaceURI(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
            if (null == xsiPrefix) {
                xsiPrefix = Constants.SCHEMA_INSTANCE_PREFIX;
                nr.put(Constants.SCHEMA_INSTANCE_PREFIX, javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
            }
        }

        OBJECT_BUILDER treeObjectBuilder = null;
        if (descriptor != null) {
            treeObjectBuilder = (OBJECT_BUILDER) descriptor.getObjectBuilder();     
        }
        if(session == null){
            session = (ABSTRACT_SESSION) context.getSession();
        }
        marshalRecord.setSession(session);

        if (null != rootFragment && !(rootFragment.getLocalName().equals(Constants.EMPTY_STRING))) {
            marshalRecord.startPrefixMappings(nr);
            if (!isXMLRoot && descriptor != null && descriptor.getNamespaceResolver() == null && rootFragment.hasNamespace()) {
                // throw an exception if the name has a : in it but the namespaceresolver is null
                throw XMLMarshalException.namespaceResolverNotSpecified(rootFragment.getShortName());
            }
            
            if(isIncludeRoot()){
                marshalRecord.openStartElement(rootFragment, nr);
            }
            if (null != schemaLocation) {
                marshalRecord.attributeWithoutQName(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.SCHEMA_LOCATION, xsiPrefix, schemaLocation);
            }
            if (null != noNsSchemaLocation) {
                marshalRecord.attributeWithoutQName(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.NO_NS_SCHEMA_LOCATION, xsiPrefix, noNsSchemaLocation);
            }
            if (isNil) {
                marshalRecord.nilSimple(nr);
            }

            marshalRecord.namespaceDeclarations(nr);

            if (descriptor != null && !isNil) {
                marshalRecord.addXsiTypeAndClassIndicatorIfRequired(descriptor, null, descriptor.getDefaultRootElementField(), root, object, isXMLRoot, true);
                treeObjectBuilder.marshalAttributes(marshalRecord, object, session);
            }
            
            if(isIncludeRoot()) {
                marshalRecord.closeStartElement();
            }
        }else{
            //no rootfragment
            marshalRecord.marshalWithoutRootElement(treeObjectBuilder,object, descriptor, root, isXMLRoot);
        }
        if (treeObjectBuilder != null && !isNil) {
            treeObjectBuilder.buildRow(marshalRecord, object, session, this, rootFragment);
        } else if (isXMLRoot) {
             if(object != null && !isNil) {
                 if(root.getDeclaredType() != null && root.getObject() != null && root.getDeclaredType() != root.getObject().getClass()) {
                      QName type = (QName)XMLConversionManager.getDefaultJavaTypes().get(object.getClass());
                      if(type != null) {
                          xsiPrefix = nr.resolveNamespaceURI(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
                          if (null == xsiPrefix) {
                              xsiPrefix = Constants.SCHEMA_INSTANCE_PREFIX;
                              marshalRecord.namespaceDeclaration(xsiPrefix, javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
                          }
                          marshalRecord.namespaceDeclaration(Constants.SCHEMA_PREFIX, javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
                          String typeValue = type.getLocalPart();
                          if(marshalRecord.isNamespaceAware()){
                              typeValue = Constants.SCHEMA_PREFIX + marshalRecord.getNamespaceSeparator() + typeValue;
                          }
                          marshalRecord.attribute(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.SCHEMA_TYPE_ATTRIBUTE, xsiPrefix + Constants.COLON + Constants.SCHEMA_TYPE_ATTRIBUTE, typeValue);
                      }
                 }
                
                marshalRecord.characters(root.getSchemaType(), object, null, false);
            }
        }

        if (null != rootFragment && !(rootFragment.getLocalName().equals(Constants.EMPTY_STRING)) && isIncludeRoot()) {
            marshalRecord.endElement(rootFragment, nr);
            marshalRecord.endPrefixMappings(nr);
        }
        if (!isFragment() ) {
            marshalRecord.endDocument();
        }
        marshalRecord.afterContainmentMarshal(null, isXMLRoot ? root : object);
    }

    /**
     * PUBLIC:
     * @param object the object to marshal
     * @param node the node which the specified object should be marshalled to
     * @throws XMLMarshalException if an error occurred during marshalling
     */
    public void marshal(Object object, Node node) throws XMLMarshalException {
        if(object instanceof JSONWithPadding && !mediaType.isApplicationJSON()){
            object = ((JSONWithPadding)object).getObject();
        }

        if ((object == null) || (node == null)) {
            throw XMLMarshalException.nullArgumentException();
        }

        ABSTRACT_SESSION session = null;
        DESCRIPTOR xmlDescriptor = null;

        boolean isXMLRoot = (object instanceof Root);
        if(isXMLRoot){
            try{
                session = context.getSession(((Root)object).getObject());               
                if(session != null){
                    xmlDescriptor = getDescriptor(((Root)object).getObject(), session);
                }
            }catch (XMLMarshalException marshalException) {
                if (!isSimpleXMLRoot((Root) object)) {
                    throw marshalException;    
                }
            }
        }else{
            Class objectClass = object.getClass();
            session = context.getSession(objectClass);
            xmlDescriptor = getDescriptor(objectClass, session);
        }

        NodeRecord contentHandlerRecord = new NodeRecord(node);
        contentHandlerRecord.setMarshaller(this);
        if (!isXMLRoot) {
            if ((null == xmlDescriptor.getDefaultRootElement()) && (node.getNodeType() == Node.ELEMENT_NODE) && (xmlDescriptor.getSchemaReference() != null) && (xmlDescriptor.getSchemaReference().getType() == XMLSchemaReference.COMPLEX_TYPE)) {
                Attr typeAttr = ((Element) node).getAttributeNodeNS(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.SCHEMA_TYPE_ATTRIBUTE);
                if (typeAttr == null) {
                    NamespaceResolver namespaceResolver = xmlDescriptor.getNonNullNamespaceResolver();
                    String xsiPrefix = namespaceResolver.resolveNamespaceURI(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);

                    if (null == xsiPrefix) {
                        xsiPrefix = namespaceResolver.generatePrefix(Constants.SCHEMA_INSTANCE_PREFIX);
                    }

                    String value = xmlDescriptor.getSchemaReference().getSchemaContext();

                    ((Element) node).setAttributeNS(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, javax.xml.XMLConstants.XMLNS_ATTRIBUTE + Constants.COLON + xsiPrefix, javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
                    ((Element) node).setAttributeNS(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, xsiPrefix + Constants.COLON + Constants.SCHEMA_TYPE_ATTRIBUTE, value);

                } else {
                    String value = xmlDescriptor.getSchemaReference().getSchemaContext();
                    typeAttr.setValue(value);
                }
            }
        }
        marshal(object, contentHandlerRecord, session, xmlDescriptor,isXMLRoot);
    }

    /**
     * PUBLIC:
     * Convert the given object to XML and update the given outputStream with that XML Document
     * @param object the object to marshal
     * @param outputStream the outputStream to marshal the object to
     * @throws XMLMarshalException if an error occurred during marshalling
     */
    public void marshal(Object object, OutputStream outputStream) throws XMLMarshalException {
        marshal (object, outputStream, null, null);
    }

    private void marshal(Object object, OutputStream outputStream, ABSTRACT_SESSION session, DESCRIPTOR xmlDescriptor) throws XMLMarshalException {
        if(object instanceof JSONWithPadding && !mediaType.isApplicationJSON()){               
           object = ((JSONWithPadding)object).getObject();             
        }
        if ((object == null) || (outputStream == null)) {
           throw XMLMarshalException.nullArgumentException();
        }
        try {
           String encoding = getEncoding();
           boolean isXMLRoot = false;
           String version = DEFAULT_XML_VERSION;
           if (object instanceof Root) {
               isXMLRoot = true;
               Root xroot = (Root) object;
               version = xroot.getXMLVersion() != null ? xroot.getXMLVersion() : version;
               encoding = xroot.getEncoding() != null ? xroot.getEncoding() : encoding;
           }
           if(mediaType.isApplicationJSON()) {
               marshal(object, new OutputStreamWriter(outputStream, encoding), session, xmlDescriptor);
               return;
           }
           if(encoding.equals(Constants.DEFAULT_XML_ENCODING)){
               if(session == null || xmlDescriptor == null) {
                   if(isXMLRoot){
                       try{
                           session = context.getSession(((Root)object).getObject());
                           if(session != null){
                               xmlDescriptor = getDescriptor(((Root)object).getObject(), session);
                           }
                      }catch (XMLMarshalException marshalException) {
                           if (!isSimpleXMLRoot((Root) object)) {
                               throw marshalException;    
                           }                
                       }
                   }else{
                           Class objectClass = object.getClass();
                           session = context.getSession(objectClass);
                           xmlDescriptor = getDescriptor(objectClass, session);
                   }
               }
               OutputStreamRecord record;
               if (isFormattedOutput()) {
                   record = new FormattedOutputStreamRecord();
               } else {
                   record = new OutputStreamRecord();
               }

               record.setMarshaller(this);
               record.setOutputStream(outputStream);

               marshal(object, record, session, xmlDescriptor, isXMLRoot);    
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
     * Convert the given object to XML and update the given result with that XML Document
     * @param object the object to marshal
     * @param result the result to marshal the object to
     * @throws XMLMarshalException if an error occurred during marshalling
     */
    public void marshal(Object object, Result result) throws XMLMarshalException {
        if ((object == null) || (result == null)) {
            throw XMLMarshalException.nullArgumentException();
        }
        DESCRIPTOR xmlDescriptor = null;
        ABSTRACT_SESSION session = null;
        
        boolean isXMLRoot = (object instanceof Root);
        
        if(isXMLRoot){
            try{
                session = context.getSession(((Root)object).getObject());
                if(session != null){
                    xmlDescriptor = getDescriptor(((Root)object).getObject(), session);
                }
           }catch (XMLMarshalException marshalException) {
                if (!isSimpleXMLRoot((Root) object)) {
                    throw marshalException;    
                }
            }
        }else{
            Class objectClass = object.getClass();
            session = context.getSession(objectClass);
            xmlDescriptor = getDescriptor(objectClass, session);
        }

        //if this is a simple xml root, the session and descriptor will be null
        if (result instanceof StreamResult) {
            StreamResult streamResult = (StreamResult) result;
            Writer writer = streamResult.getWriter();
            if (writer != null) {
                marshal(object, writer, session, xmlDescriptor);
            } else if (streamResult.getOutputStream() != null) {
                marshal(object, streamResult.getOutputStream(), session, xmlDescriptor);
            } else {
                try {
                    File f;
                    try {
                        f = new File(new URL(streamResult.getSystemId()).toURI());
                    } catch(MalformedURLException malformedURLException) {
                        try {
                            f = new File(streamResult.getSystemId());
                        } catch(Exception e) {
                            throw malformedURLException;
                        }
                    }
                    writer = new FileWriter(f);
                    try {
                        marshal(object, writer, session, xmlDescriptor);
                    } finally {
                        writer.close();
                    }
                } catch (Exception e) {
                    throw XMLMarshalException.marshalException(e);
                }
            }
        }else if (result instanceof DOMResult) {
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
            getTransformer().transform(source, result);
        }
        return;
    }

    /**
    * PUBLIC:
    * Convert the given object to XML and update the given writer with that XML Document
    * @param object the object to marshal
    * @param writer the writer to marshal the object to
    * @throws XMLMarshalException if an error occurred during marshalling
    */
    public void marshal(Object object, Writer writer) throws XMLMarshalException {
        marshal(object, writer, null, null);        
    }

    private void marshal(Object object, Writer writer, ABSTRACT_SESSION session, DESCRIPTOR xmlDescriptor) throws XMLMarshalException {
        if ((object == null) || (writer == null)) {
            throw XMLMarshalException.nullArgumentException();
        }
        boolean isXMLRoot = false;
        String version = DEFAULT_XML_VERSION;
        String encoding = getEncoding();
        String callbackName = null;
        if(object instanceof JSONWithPadding){
            callbackName = ((JSONWithPadding)object).getCallbackName();
            object = ((JSONWithPadding)object).getObject();
            if(object == null){
                throw XMLMarshalException.nullArgumentException();
            }
        }
        
        if (object instanceof Root) {
            isXMLRoot = true;
            Root xroot = (Root) object;
            version = xroot.getXMLVersion() != null ? xroot.getXMLVersion() : version;
            encoding = xroot.getEncoding() != null ? xroot.getEncoding() : encoding;
        }

        
        MarshalRecord writerRecord;
        writer = wrapWriter(writer);
        if (isFormattedOutput()) {
            if(mediaType.isApplicationJSON()) {                          
                writerRecord = new JSONFormattedWriterRecord(writer, callbackName);                
            } else {
                writerRecord = new FormattedWriterRecord();
                ((FormattedWriterRecord) writerRecord).setWriter(writer);
            }
        } else {
            if(mediaType.isApplicationJSON()) {
                writerRecord = new JSONWriterRecord(writer, callbackName);                
            } else {
                writerRecord = new WriterRecord();
                ((WriterRecord) writerRecord).setWriter(writer);
            }
        }
        writerRecord.setMarshaller(this);

        String rootName = null;
        String rootNamespace = null;
        if(isXMLRoot){
            rootName = ((Root)object).getLocalName();
            rootNamespace = ((Root)object).getNamespaceURI();
            if(session == null || xmlDescriptor == null){
                try{
                    session = context.getSession(((Root)object).getObject());
                    if(session != null){
                        xmlDescriptor = getDescriptor(((Root)object).getObject(), session);
                    }
                }catch (XMLMarshalException marshalException) {
                    if (!isSimpleXMLRoot((Root) object)) {
                        throw marshalException;
                    }
                }
            }
        }else{
            Class objectClass = object.getClass();
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
            } else if(objectClass.isArray()) {
                try {
                    writerRecord.startCollection();
                    int arrayLength = Array.getLength(object);
                    for(int x=0; x<arrayLength; x++) {
                        marshal(Array.get(object, x), writerRecord);
                    }
                    writerRecord.endCollection();
                    writer.flush();
                } catch(IOException e) {
                    throw XMLMarshalException.marshalException(e);
                }
                return;
            }
            if(session == null || xmlDescriptor == null){
                session = context.getSession(objectClass);
                xmlDescriptor = getDescriptor(objectClass, session);
            }
        }

        marshal(object, writerRecord, session, xmlDescriptor, isXMLRoot);

        try {
            writer.flush();
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     * Wrap Writer in a BufferedWriter only if its write() operations may be costly
     * (such as FileWriters and OutputStreamWriters). 
     */
    private Writer wrapWriter(Writer writer) {
        if (writer instanceof OutputStreamWriter || writer instanceof FileWriter) {
            return new BufferedWriter(writer);
        }
        return writer;
    }

    /**
     * PUBLIC:
     * Convert the given object to an XML Document
     * @param object the object to marshal
     * @return the document which the specified object has been marshalled to
     * @throws XMLMarshalException if an error occurred during marshalling
     */
    public Document objectToXML(Object object) throws XMLMarshalException {
        boolean isXMLRoot = (object instanceof Root);
        DESCRIPTOR xmlDescriptor = getDescriptor(object, isXMLRoot);
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
    protected Document objectToXML(Object object, DESCRIPTOR descriptor, boolean isXMLRoot) throws XMLMarshalException {
        ABSTRACT_SESSION session = context.getSession(descriptor);
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
    protected Node objectToXMLNode(Object object, ABSTRACT_SESSION session, DESCRIPTOR descriptor, boolean isXMLRoot) throws XMLMarshalException {
        return objectToXMLNode(object, null, session, descriptor, isXMLRoot);
    }

    protected Node objectToXMLNode(Object object, Node rootNode, ABSTRACT_SESSION session, DESCRIPTOR descriptor, boolean isXMLRoot) throws XMLMarshalException {
        MarshalRecord marshalRecord = new NodeRecord();
        marshalRecord.setMarshaller(this);
        marshalRecord.getNamespaceResolver().setDOM(rootNode);
        marshal(object, marshalRecord, session, descriptor, isXMLRoot);
        return marshalRecord.getDocument();
    }

    public void setAttachmentMarshaller(XMLAttachmentMarshaller atm) {
        this.attachmentMarshaller = atm;
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
     * Set the encoding on this XMLMarshaller
     * If the encoding is not set the default UTF-8 will be used
     * @param newEncoding the encoding to set on this XMLMarshaller
     */
    public void setEncoding(String newEncoding) {
        super.setEncoding(newEncoding);
        if(null != transformer) {
            transformer.setEncoding(newEncoding);
        }
    }

    /**
     * Set if this XMLMarshaller should format the XML
     * By default this is set to true and the XML marshalled will be formatted.
     * @param shouldFormat if this XMLMarshaller should format the XML
     */
    @Override
    public void setFormattedOutput(boolean shouldFormat) {
        super.setFormattedOutput(shouldFormat);
        if(null != transformer) {
            transformer.setFormattedOutput(shouldFormat);
        }
    }

    /**
     * PUBLIC:
     * Set if this should marshal to a fragment.  If true an XML header string is not written out.
     * @param fragment if this should marshal to a fragment or not
     */
    public void setFragment(boolean fragment) {
        this.fragment = fragment;
        if(null != transformer) {
            transformer.setFragment(fragment);
        }
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
     * Name of the property to determine if empty collections should be marshalled as []    
     * Ignored marshalling XML.  
     * @since 2.4    
     */
    public void setMarshalEmptyCollections(Boolean marshalEmptyCollections) {
        this.marshalEmptyCollections = marshalEmptyCollections;
    }

    /**
     * Set the MediaType for this xmlMarshaller.
     * See org.eclipse.persistence.oxm.MediaType for the media types supported by EclipseLink MOXy
     * @param mediaType
     */
    public void setMediaType(MEDIA_TYPE mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * Set the namespace separator used during marshal operations.
     * If mediaType is application/json '.' is the default
     * Ignored marshalling XML.   
     * @since 2.4
     */
    public void setNamespaceSeparator(char namespaceSeparator) {
        this.namespaceSeparator = namespaceSeparator;
    }

   /**
     * Set the no namespace schema location on this XMLMarshaller
     * @param newNoNamespaceSchemaLocation no namespace schema location to be seton this XMLMarshaller
     */
    public void setNoNamespaceSchemaLocation(String newNoNamespaceSchemaLocation) {
       noNamespaceSchemaLocation = newNoNamespaceSchemaLocation;
    }

    /**
     * Property to determine if size 1 any collections should be treated as collections
     * Ignored marshalling XML.
     */
    public void setReduceAnyArrays(boolean reduceAnyArrays) {
        this.reduceAnyArrays = reduceAnyArrays;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    /**
     * Set the schema location on this XMLMarshaller
     * @param newSchemaLocation the schema location to be seton this XMLMarshaller
     */
    public void setSchemaLocation(String newSchemaLocation) {
       schemaLocation = newSchemaLocation;
    }

    public void setWrapperAsCollectionName(boolean wrapperAsCollectionName) {
        this.wrapperAsCollectionName = wrapperAsCollectionName;
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
     * <p>
     * Set this Marshaller's XML Header.  This header string will appear after
     * the XML processing instruction (&lt;?xml ...&gt;), but before the start 
     * of the document's data.
     * </p>
     * 
     * <p>
     * This feature is only supported when marshalling to Stream, Writer,
     * or StreamResult.
     * </p>
     * @since 2.4
     */
    public void setXmlHeader(String xmlHeader) {
        this.xmlHeader = xmlHeader;
    }

    public void setMarshalAttributeGroup(Object group) {
        this.marshalAttributeGroup = group;
        
    }
    
    public Object getMarshalAttributeGroup() {
        return this.marshalAttributeGroup;
    }
}
