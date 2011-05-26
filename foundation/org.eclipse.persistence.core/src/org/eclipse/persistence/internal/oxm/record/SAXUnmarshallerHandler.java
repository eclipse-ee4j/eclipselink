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

import java.lang.reflect.Modifier;

import javax.xml.namespace.QName;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.oxm.record.XMLRootRecord;
import org.eclipse.persistence.oxm.unmapped.UnmappedContentHandler;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.Locator2;
import org.eclipse.persistence.internal.oxm.record.XMLReader;
import org.eclipse.persistence.internal.oxm.record.namespaces.StackUnmarshalNamespaceResolver;
import org.eclipse.persistence.internal.oxm.record.namespaces.UnmarshalNamespaceResolver;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>An implementation of ContentHandler used to handle the root element of an
 * XML Document during unmarshal.
 * <p><b>Responsibilities:</b><ul>
 * <li>Implement ContentHandler interface</li>
 * <li>Handle startElement event for the root-level element of an xml document</li>
 * <li>Handle inheritance, and descriptor lookup to determine the correct class associated with
 * this XML Element.</li>
 * </ul>
 *
 * @author bdoughan
 *
 */
public class SAXUnmarshallerHandler implements ExtendedContentHandler {
    private XMLReader xmlReader;
    private XMLContext xmlContext;
    private UnmarshalRecord rootRecord;
    private Object object;
    private XMLDescriptor descriptor;
    private XMLUnmarshaller unmarshaller;
    private AbstractSession session;
    private Locator2 locator;
    private UnmarshalNamespaceResolver unmarshalNamespaceResolver;
    private UnmarshalKeepAsElementPolicy keepAsElementPolicy = UnmarshalKeepAsElementPolicy.KEEP_NONE_AS_ELEMENT;
    private SAXDocumentBuilder documentBuilder;

    public SAXUnmarshallerHandler(XMLContext xmlContext) {
        super();
        this.xmlContext = xmlContext;
        unmarshalNamespaceResolver = new StackUnmarshalNamespaceResolver();
    }

    public XMLReader getXMLReader() {
        return this.xmlReader;
    }

    public void setXMLReader(XMLReader xmlReader) {
        this.xmlReader = xmlReader;
    }

    public Object getObject() {
        if(object == null) {
            if(this.descriptor != null) {
                object = this.descriptor.wrapObjectInXMLRoot(this.rootRecord, this.unmarshaller.isResultAlwaysXMLRoot());
            } else if(documentBuilder != null) {
                Node node = documentBuilder.getDocument().getDocumentElement();
                XMLRoot root = new XMLRoot();
                root.setLocalName(node.getLocalName());
                root.setNamespaceURI(node.getNamespaceURI());
                root.setObject(node);
                object = root;
            } else {
                if(rootRecord != null) {
                    object = this.rootRecord.getCurrentObject();
                }
            }
        }
        return this.object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public void setDocumentLocator(Locator locator) {
        if (locator instanceof Locator2) {
            this.locator = (Locator2)locator;
        }
    }

    public UnmarshalNamespaceResolver getUnmarshalNamespaceResolver() {
        return this.unmarshalNamespaceResolver;
    }

    public void setUnmarshalNamespaceResolver(UnmarshalNamespaceResolver unmarshalNamespaceResolver) {
        this.unmarshalNamespaceResolver = unmarshalNamespaceResolver;
    }

    public void startDocument() throws SAXException {
    }

    public void endDocument() throws SAXException {
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        unmarshalNamespaceResolver.push(prefix, uri);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        unmarshalNamespaceResolver.pop(prefix);
    }

    /**
     * INTERNAL:
     *
     * Resolve any mapping references.
     */
    public void resolveReferences() {
        unmarshaller.resolveReferences(session);
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        try {           
            XMLDescriptor xmlDescriptor = null;
            boolean isPrimitiveType = false;
            Class primitiveWrapperClass = null;
            String type = atts.getValue(XMLConstants.SCHEMA_INSTANCE_URL, XMLConstants.SCHEMA_TYPE_ATTRIBUTE);
            if (null != type) {
                XPathFragment typeFragment = new XPathFragment(type);

                // set the prefix using a reverse key lookup by uri value on namespaceMap 
                if (null != unmarshalNamespaceResolver) {
                    typeFragment.setNamespaceURI(unmarshalNamespaceResolver.getNamespaceURI(typeFragment.getPrefix()));
                }
                xmlDescriptor = xmlContext.getDescriptorByGlobalType(typeFragment);
                if(xmlDescriptor == null) {
                    //check to see if type attribute represents simple type
                    primitiveWrapperClass = (Class)XMLConversionManager.getDefaultXMLTypes().get(new QName(typeFragment.getNamespaceURI(), typeFragment.getLocalName()));
                }
            }
            
            if(xmlDescriptor == null){
            	String name;
                if (localName == null || localName.length() == 0) {
                    name = qName;
                } else {
                    name = localName;
                }

                QName rootQName;
                if (namespaceURI == null || namespaceURI.length() == 0) {
                    rootQName = new QName(name);
                } else {
                    rootQName = new QName(namespaceURI, name);
                }
            	
            	xmlDescriptor = xmlContext.getDescriptor(rootQName);
            	
                if (null == xmlDescriptor) {
                    //check for a cached object and look for descriptor by class
                    Object obj = this.xmlReader.getCurrentObject(session, null);
                    if (obj != null) {
                        xmlDescriptor = (XMLDescriptor)xmlContext.getSession(obj.getClass()).getDescriptor(obj.getClass());
                    }
                    if(xmlDescriptor == null) {
                        isPrimitiveType = primitiveWrapperClass != null;
                    }
                }
                if (null == xmlDescriptor && !isPrimitiveType) {
                    if(this.keepAsElementPolicy != UnmarshalKeepAsElementPolicy.KEEP_NONE_AS_ELEMENT) {
                        this.documentBuilder = new SAXDocumentBuilder();
                        documentBuilder.startDocument();
                        //start any prefixes that have already been started
                        for(String prefix:this.unmarshalNamespaceResolver.getPrefixes()) {
                            documentBuilder.startPrefixMapping(prefix, this.unmarshalNamespaceResolver.getNamespaceURI(prefix));
                        }
                        documentBuilder.startElement(namespaceURI, localName, qName, atts);
                        this.xmlReader.setContentHandler(documentBuilder);
                        return;
                    }
                    Class unmappedContentHandlerClass = unmarshaller.getUnmappedContentHandlerClass();
                    if (null == unmappedContentHandlerClass) {
                        throw XMLMarshalException.noDescriptorWithMatchingRootElement(rootQName.toString());
                    } else {
                        UnmappedContentHandler unmappedContentHandler;

                        try {
                            PrivilegedNewInstanceFromClass privilegedNewInstanceFromClass = new PrivilegedNewInstanceFromClass(unmappedContentHandlerClass);
                            unmappedContentHandler = (UnmappedContentHandler)privilegedNewInstanceFromClass.run();
                        } catch (ClassCastException e) {
                            throw XMLMarshalException.unmappedContentHandlerDoesntImplement(e, unmappedContentHandlerClass.getName());
                        } catch (IllegalAccessException e) {
                            throw XMLMarshalException.errorInstantiatingUnmappedContentHandler(e, unmappedContentHandlerClass.getName());
                        } catch (InstantiationException e) {
                            throw XMLMarshalException.errorInstantiatingUnmappedContentHandler(e, unmappedContentHandlerClass.getName());
                        }
                        
                        UnmappedContentHandlerWrapper unmappedContentHandlerWrapper = new UnmappedContentHandlerWrapper(unmappedContentHandler, this);
                        unmappedContentHandler.setUnmarshalRecord(unmappedContentHandlerWrapper);

                        unmappedContentHandler.startElement(namespaceURI, localName, qName, atts);
                        xmlReader.setContentHandler(unmappedContentHandler);

                        setObject(unmappedContentHandlerWrapper.getCurrentObject());
                        return;

                    }
                }
            }
            
            // for XMLObjectReferenceMappings we need a non-shared cache, so
            // try and get a Unit Of Work from the XMLContext
            session = xmlContext.getReadSession(xmlDescriptor);

            UnmarshalRecord unmarshalRecord;
            if (isPrimitiveType) {
                unmarshalRecord = new XMLRootRecord(primitiveWrapperClass);
                unmarshalRecord.setSession((AbstractSession) unmarshaller.getXMLContext().getSession(0));
            } else if (xmlDescriptor.hasInheritance()) {
                unmarshalRecord = new UnmarshalRecord(null);
                unmarshalRecord.setUnmarshalNamespaceResolver(unmarshalNamespaceResolver);
                unmarshalRecord.setAttributes(atts);
                Class classValue = xmlDescriptor.getInheritancePolicy().classFromRow(unmarshalRecord, session);
                if (classValue == null) {
                    // no xsi:type attribute - look for type indicator on the default root element
                    QName leafElementType = xmlDescriptor.getDefaultRootElementType();

                    // if we have a user-set type, try to get the class from the inheritance policy
                    if (leafElementType != null) {
                        Object indicator = xmlDescriptor.getInheritancePolicy().getClassIndicatorMapping().get(leafElementType);
                        if(indicator != null) {
                            classValue = (Class)indicator;
                        }
                    }
                }
                if (classValue != null) {
                    xmlDescriptor = (XMLDescriptor)session.getDescriptor(classValue);
                } else {
                    // since there is no xsi:type attribute, we'll use the descriptor
                    // that was retrieved based on the rootQName -  we need to make 
                    // sure it is non-abstract
                    if (Modifier.isAbstract(xmlDescriptor.getJavaClass().getModifiers())) {
                        // need to throw an exception here
                        throw DescriptorException.missingClassIndicatorField(unmarshalRecord, xmlDescriptor.getInheritancePolicy().getDescriptor());
                    }
                }
                unmarshalRecord = (UnmarshalRecord)xmlDescriptor.getObjectBuilder().createRecord(session);
            } else {
                unmarshalRecord = (UnmarshalRecord)xmlDescriptor.getObjectBuilder().createRecord(session);
            }
            this.descriptor = xmlDescriptor;
            this.rootRecord = unmarshalRecord;
            if (locator != null) {
                unmarshalRecord.setDocumentLocator(locator);
            }
            unmarshalRecord.setUnmarshaller(this.unmarshaller);
            unmarshalRecord.setXMLReader(this.getXMLReader());
            unmarshalRecord.setAttributes(atts);

            if(atts != null && null != atts.getValue(XMLConstants.SCHEMA_INSTANCE_URL, XMLConstants.SCHEMA_NIL_ATTRIBUTE)) {
                unmarshalRecord.setNil(true);
            }
            unmarshalRecord.setUnmarshalNamespaceResolver(unmarshalNamespaceResolver);
            
            unmarshalRecord.startDocument();
            unmarshalRecord.initializeRecord(null);
            xmlReader.setContentHandler(unmarshalRecord);
            xmlReader.setLexicalHandler(unmarshalRecord);
            unmarshalRecord.startElement(namespaceURI, localName, qName, atts);

            // if we located the descriptor via xsi:type attribute, create and 
            // return an XMLRoot object 
        } catch (EclipseLinkException e) {
            if (null == xmlReader.getErrorHandler()) {
                throw e;
            } else {
                SAXParseException saxParseException = new SAXParseException(null, null, null, 0, 0, e);
                xmlReader.getErrorHandler().error(saxParseException);
            }
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
    }

    public void characters(CharSequence characters) throws SAXException {
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void skippedEntity(String name) throws SAXException {
    }

    public void setUnmarshaller(XMLUnmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    public XMLUnmarshaller getUnmarshaller() {
        return this.unmarshaller;
    }
    
    public void setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy policy) {
        this.keepAsElementPolicy = policy;
    }
    
    public UnmarshalKeepAsElementPolicy getKeepAsElementPolicy() {
        return this.keepAsElementPolicy;
    }

}