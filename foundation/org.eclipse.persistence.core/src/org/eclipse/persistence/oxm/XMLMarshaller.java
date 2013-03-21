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

import java.util.Properties;

import javax.xml.transform.Result;
import javax.xml.transform.sax.SAXResult;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.FragmentContentHandler;
import org.eclipse.persistence.internal.oxm.Root;
import org.eclipse.persistence.internal.oxm.TreeObjectBuilder;
import org.eclipse.persistence.internal.oxm.XMLObjectBuilder;
import org.eclipse.persistence.internal.oxm.XPathEngine;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.documentpreservation.DocumentPreservationPolicy;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.NodeRecord;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.platform.xml.XMLPlatformException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
public class XMLMarshaller extends org.eclipse.persistence.internal.oxm.XMLMarshaller<AbstractSession, XMLContext, XMLDescriptor, MediaType, NamespacePrefixMapper, TreeObjectBuilder> implements Cloneable {

    private Object marshalAttributeGroup;

    /**
     * Create a new XMLMarshaller based on the specified session
     * @param session A single session
     */
    public XMLMarshaller(XMLContext xmlContext) {
        super(xmlContext);
        setMediaType(MediaType.APPLICATION_XML);
    }

    /**
     * Copy constructor
     */
    protected XMLMarshaller(XMLMarshaller xmlMarshaller) {
        super(xmlMarshaller);
    }

    /**
     * Return the instance of XMLContext that was used to create this instance
     * of XMLMarshaller.
     */
    public XMLContext getXMLContext() {
        return getContext();
    }

    /**
     * Set the XMLContext used by this instance of XMLMarshaller.
     */
    public void setXMLContext(XMLContext value) {
        context = value;
    }

    /**
     * Return a properties object for a given instance of the
     * XMLMarshaller.
     *
     * @return
     */
    public Properties getProperties() {
        if(null == marshalProperties) {
            marshalProperties = new Properties();
        }
        return marshalProperties;
    }

    public void setXMLMarshalHandler(XMLMarshalListener marshalListener) {
        setMarshalListener(marshalListener);
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
        XMLDescriptor xmlDescriptor = null;
        AbstractSession session = null;
        
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
        if (session == null ||  !context.getDocumentPreservationPolicy(session).shouldPreserveDocument()) {
            super.marshal(object, result);
            return;
        }
        try {
            Document document = objectToXML(object, xmlDescriptor, isXMLRoot);
            
            if ((result instanceof SAXResult) && (isFragment())) {
                FragmentContentHandler fragmentHandler = new FragmentContentHandler(((SAXResult) result).getHandler());
                getTransformer(); // Ensure transformer is initialized
                if (isXMLRoot) {
                    String oldEncoding = transformer.getEncoding();
                    String oldVersion = transformer.getVersion();
                    if (((Root) object).getEncoding() != null) {
                        transformer.setEncoding(((Root) object).getEncoding());
                    }
                    if (((Root) object).getXMLVersion() != null) {
                        transformer.setVersion(((Root) object).getXMLVersion());
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
                        String namespace = null;
                        String localName = null;
                        if(isXMLRoot){
                        	Root xmlRootObject = (Root)object;
                            if(xmlRootObject.getObject() != null && xmlRootObject.getObject() instanceof Node){
                                namespace = ((Root)object).getNamespaceURI();
                                localName = ((Root)object).getLocalName();
                            }
                        }
                        
                        Object xmlStreamWriter = PrivilegedAccessHelper.invokeMethod(staxResultGetStreamWriterMethod, result);
                        if (xmlStreamWriter != null) {
                            Object domtostax = PrivilegedAccessHelper.newInstanceFromClass(domToStreamWriterClass);
                            PrivilegedAccessHelper.invokeMethod(writeToStreamMethod, domtostax, new Object[]{document,namespace, localName,xmlStreamWriter});
                            return;
                        } else {
                            Object xmlEventWriter = PrivilegedAccessHelper.invokeMethod(staxResultGetEventWriterMethod, result);
                            if(xmlEventWriter != null) {
                                Object domToEventWriter = PrivilegedAccessHelper.newInstanceFromClass(domToEventWriterClass);
                                PrivilegedAccessHelper.invokeMethod(writeToEventWriterMethod, domToEventWriter, new Object[]{document, namespace, localName, xmlEventWriter});
                                return;
                            }
                        }
                    } catch(Exception e) {
                        throw XMLMarshalException.marshalException(e);
                    }
                }
                getTransformer(); // Ensure transformer is initialized 
                if (isXMLRoot) {
                    String oldEncoding = transformer.getEncoding();
                    String oldVersion = transformer.getVersion();
                    if (((Root) object).getEncoding() != null) {
                        transformer.setEncoding(((Root) object).getEncoding());
                    }
                    if (((Root) object).getXMLVersion() != null) {
                        transformer.setVersion(((Root) object).getXMLVersion());
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

    @Override
    protected Node getNode(Object object, Node parentNode, AbstractSession session, XMLDescriptor xmlDescriptor, boolean isXMLRoot) {
        Node node = super.getNode(object, parentNode, session, xmlDescriptor, isXMLRoot);
        if(null != node) {
            return node;
        }
        if(null != session && context.getDocumentPreservationPolicy(session).shouldPreserveDocument()) {
            return objectToXMLNode(object, parentNode, session, xmlDescriptor, isXMLRoot);
        }
        return null;
    }

    
    /**
     * Convert the given object to XML and update the given marshal record with
     * that XML Document.
     * @param object the object to marshal
     * @param marshalRecord the marshalRecord to marshal the object to
     */
    protected void marshal(Object object, AbstractSession session, MarshalRecord marshalRecord) {
        boolean isXMLRoot = (object instanceof Root);
        marshal(object, marshalRecord, session, getDescriptor(object, isXMLRoot), isXMLRoot);
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
        AbstractSession session = context.getSession(descriptor);
        DocumentPreservationPolicy docPresPolicy = context.getDocumentPreservationPolicy(session);
        if (docPresPolicy != null && docPresPolicy.shouldPreserveDocument()) {
            XMLRecord xmlRow = null;
            if (!isXMLRoot) {
                xmlRow = (XMLRecord) ((XMLObjectBuilder) descriptor.getObjectBuilder()).createRecordFor(object, context.getDocumentPreservationPolicy(session));
                xmlRow.setMarshaller(this);
                if (this.attachmentMarshaller != null) {
                    xmlRow.setXOPPackage(this.attachmentMarshaller.isXOPPackage());
                }
                addDescriptorNamespacesToXMLRecord(descriptor, xmlRow);
            }
            return objectToXML(object, descriptor, xmlRow, isXMLRoot, docPresPolicy);
        }
        return super.objectToXML(object, descriptor, isXMLRoot);
    }

    protected Node objectToXMLNode(Object object, Node rootNode, AbstractSession session,XMLDescriptor descriptor, boolean isXMLRoot) throws XMLMarshalException {
        DocumentPreservationPolicy docPresPolicy = context.getDocumentPreservationPolicy(session);
        if (docPresPolicy != null && docPresPolicy.shouldPreserveDocument()) {
            XMLRecord xmlRow = null;
            if (!isXMLRoot) {
                xmlRow = (XMLRecord) ((XMLObjectBuilder) descriptor.getObjectBuilder()).createRecordFor(object, context.getDocumentPreservationPolicy(session));
                xmlRow.setMarshaller(this);
                if (this.attachmentMarshaller != null) {
                    xmlRow.setXOPPackage(this.attachmentMarshaller.isXOPPackage());
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
        return super.objectToXMLNode(object, rootNode, session, descriptor, isXMLRoot);
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
        boolean isXMLRoot = (object instanceof Root);
        
        AbstractSession session = null;
        XMLDescriptor descriptor = null;
        if(isXMLRoot){
        	try{
        	    session = context.getSession(((Root)object).getObject());
        	    if(session != null){
        	        descriptor = getDescriptor(((Root)object).getObject(), session);
        	    }
        	}catch (XMLMarshalException marshalException) {
                if (!isSimpleXMLRoot((Root) object)) {
                	throw marshalException;    
                }                
            }
        }else{
            Class objectClass = object.getClass();
        	session = context.getSession(objectClass);
        	descriptor = getDescriptor(objectClass, session);
        }

        String localRootName = descriptor.getDefaultRootElement();
        if (null == localRootName) {
            throw XMLMarshalException.defaultRootElementNotSpecified(descriptor);
        }

        if(docPresPolicy == null) {
            docPresPolicy = context.getDocumentPreservationPolicy(session);
        }
        if (docPresPolicy != null && docPresPolicy.shouldPreserveDocument()) {
            XMLRecord xmlRow = (XMLRecord) ((XMLObjectBuilder) descriptor.getObjectBuilder()).createRecord(localRootName, parent, session);
            xmlRow.setMarshaller(this);
            if (this.attachmentMarshaller != null) {
                xmlRow.setXOPPackage(this.attachmentMarshaller.isXOPPackage());
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
        AbstractSession session = context.getSession(descriptor);
        if (xmlRow != null) {
            isRootDocumentFragment = (xmlRow.getDOM().getNodeType() == Node.DOCUMENT_FRAGMENT_NODE);
        }
        Object originalObject = object;
        if (isXMLRoot) {
            String xmlRootUri = ((Root) object).getNamespaceURI();
            String xmlRootPrefix = null;
            if (xmlRow == null) {
                String recordName = ((Root) object).getLocalName();
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
                xmlRow = (XMLRecord) ((XMLObjectBuilder) descriptor.getObjectBuilder()).createRecordFor(((Root) object).getObject(), docPresPolicy, recordName, xmlRootUri);
                xmlRow.setMarshaller(this);
                if (this.attachmentMarshaller != null) {
                    xmlRow.setXOPPackage(this.attachmentMarshaller.isXOPPackage());
                }
                if (!isRootDocumentFragment) {
                    if (shouldCallSetAttributeNS) {
                        if (xmlRootPrefix != null) {
                            ((Element) xmlRow.getDOM()).setAttributeNS(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, javax.xml.XMLConstants.XMLNS_ATTRIBUTE + XMLConstants.COLON + xmlRootPrefix, xmlRootUri);
                        }
                        shouldCallSetAttributeNS = false;
                    }
                }
            }

            copyNamespaces(resolver, xmlRow.getNamespaceResolver());
            document = xmlRow.getDocument();
            Element docElement = document.getDocumentElement();
            object = ((Root) object).getObject();
        }

        XMLObjectBuilder bldr = (XMLObjectBuilder) descriptor.getObjectBuilder();

        AbstractSession objectSession = context.getSession(object);
        xmlRow.setSession(objectSession);
        xmlRow.addXsiTypeAndClassIndicatorIfRequired(descriptor, null, null, originalObject, object, isXMLRoot, true);
        xmlRow.setMarshaller(this);
        if (shouldCallSetAttributeNS && !isRootDocumentFragment) {
            ((Element) xmlRow.getDOM()).setAttributeNS(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, javax.xml.XMLConstants.XMLNS_ATTRIBUTE + XMLConstants.COLON + XMLConstants.SCHEMA_INSTANCE_PREFIX, javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
        }
        xmlRow = (XMLRecord) bldr.buildRow(xmlRow, object,  objectSession, isXMLRoot);
      
        document = xmlRow.getDocument();

        addSchemaLocations(document, session);
        return document;
    }

    private void addSchemaLocations(Document document, AbstractSession session) {
        Element docElement = document.getDocumentElement();

        NamespaceResolver resolver = new NamespaceResolver();
        resolver.put(javax.xml.XMLConstants.XMLNS_ATTRIBUTE, javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
        resolver.put(XMLConstants.SCHEMA_INSTANCE_PREFIX, javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);

        if ((getSchemaLocation() != null) || (getNoNamespaceSchemaLocation() != null)) {
            XMLField field = new XMLField("@xmlns:xsi");
            field.setNamespaceResolver(resolver);
            XPathEngine.getInstance().create(field, docElement, javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, session);
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

    protected XMLDescriptor getDescriptor(Object object, AbstractSession session, boolean isXMLRoot) {
        if (isXMLRoot) {
            return getDescriptor((Root) object, session);
        } else {
            return getDescriptor(object, session);
        }
    }

    @Override
    public XMLMarshaller clone() {
        return new XMLMarshaller(this);
    }

	/**
     * NamespacePrefixMapper that can be used during marshal (instead of those set in the project meta data)
     * @since 2.3.3
     * @return
     */
    public void setNamespacePrefixMapper(NamespacePrefixMapper mapper) {
        super.setNamespacePrefixMapper(mapper);
    }

    /**
     * NamespacePrefixMapper that can be used during marshal (instead of those set in the project meta data)
     * @since 2.3.3
     * @return
     */
    public NamespacePrefixMapper getNamespacePrefixMapper() {
        return super.getNamespacePrefixMapper();
    }
}