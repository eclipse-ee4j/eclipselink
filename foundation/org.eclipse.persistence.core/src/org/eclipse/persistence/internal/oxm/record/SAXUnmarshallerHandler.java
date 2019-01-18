/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.oxm.record;

import java.lang.reflect.Modifier;

import javax.xml.namespace.QName;

import org.eclipse.persistence.core.queries.CoreAttributeGroup;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.Context;
import org.eclipse.persistence.internal.oxm.ConversionManager;
import org.eclipse.persistence.internal.oxm.Root;
import org.eclipse.persistence.internal.oxm.Unmarshaller;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.XPathQName;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.internal.oxm.record.namespaces.StackUnmarshalNamespaceResolver;
import org.eclipse.persistence.internal.oxm.record.namespaces.UnmarshalNamespaceResolver;
import org.eclipse.persistence.internal.oxm.unmapped.UnmappedContentHandler;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.Locator2;

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
    private Context xmlContext;
    private UnmarshalRecord rootRecord;
    private Object object;
    private Descriptor descriptor;
    private boolean shouldWrap;
    private Unmarshaller unmarshaller;
    private CoreAbstractSession session;
    private UnmarshalNamespaceResolver unmarshalNamespaceResolver;

    private UnmarshalKeepAsElementPolicy keepAsElementPolicy = new UnmarshalKeepAsElementPolicy() {

        @Override
        public boolean isKeepAllAsElement() {
            return false;
        }

        @Override
        public boolean isKeepNoneAsElement() {
            return true;
        }

        @Override
        public boolean isKeepUnknownAsElement() {
            return false;
        }

    };
    private SAXDocumentBuilder documentBuilder;
    private Locator2 locator;
    private boolean isNil;

    public SAXUnmarshallerHandler(Context xmlContext) {
        super();
        this.xmlContext = xmlContext;
        this.shouldWrap = true;
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
                if(this.unmarshaller.isResultAlwaysXMLRoot() || descriptor.isResultAlwaysXMLRoot() || shouldWrap){
                    object = this.descriptor.wrapObjectInXMLRoot(this.rootRecord, (this.unmarshaller.isResultAlwaysXMLRoot() || descriptor.isResultAlwaysXMLRoot()));
                }else {
                    object = this.rootRecord.getCurrentObject();
                }
            } else if(documentBuilder != null) {
                Node node = documentBuilder.getDocument().getDocumentElement();
                Root root = unmarshaller.createRoot();
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

    @Override
    public void setDocumentLocator(Locator locator) {
        if (locator instanceof Locator2) {
            this.locator = (Locator2)locator;
            if(xmlReader != null){
                xmlReader.setLocator(locator);
            }
        }
    }

    public UnmarshalNamespaceResolver getUnmarshalNamespaceResolver() {
        return this.unmarshalNamespaceResolver;
    }

    public void setUnmarshalNamespaceResolver(UnmarshalNamespaceResolver unmarshalNamespaceResolver) {
        this.unmarshalNamespaceResolver = unmarshalNamespaceResolver;
    }

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        unmarshalNamespaceResolver.push(prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        unmarshalNamespaceResolver.pop(prefix);
    }

    /**
     * INTERNAL:
     *
     * Resolve any mapping references.
     */
    public void resolveReferences() {
        if(null != rootRecord) {
            rootRecord.resolveReferences(session, unmarshaller.getIDResolver());
        }
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        try {
            String name;
            if (localName == null || localName.length() == 0) {
                name = qName;
            } else {
                name = localName;
            }

            XPathQName rootQName;
            if (namespaceURI == null || namespaceURI.length() == 0) {
                rootQName = new XPathQName(name, xmlReader.isNamespaceAware() );
            } else {
                rootQName = new XPathQName(namespaceURI, name, xmlReader.isNamespaceAware() );
            }

            Class primitiveWrapperClass = null;
            Descriptor xmlDescriptor = xmlContext.getDescriptor(rootQName);

            //if no match on root element look for xsi:type
            if (xmlDescriptor == null || (unmarshaller.getMediaType() == MediaType.APPLICATION_JSON && unmarshaller.getJsonTypeConfiguration().getJsonTypeAttributeName() != null &&
                    !Constants.SCHEMA_TYPE_ATTRIBUTE.equals(unmarshaller.getJsonTypeConfiguration().getJsonTypeAttributeName()))) {
                boolean isPrimitiveType = false;
                String type = null;
                if(xmlReader.isNamespaceAware()){
                    type = atts.getValue(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.SCHEMA_TYPE_ATTRIBUTE);
                } else if (unmarshaller.getMediaType() != MediaType.APPLICATION_JSON || unmarshaller.getJsonTypeConfiguration().useJsonTypeCompatibility()) {
                    type = atts.getValue(Constants.EMPTY_STRING, Constants.SCHEMA_TYPE_ATTRIBUTE);
                } else if (unmarshaller.getMediaType() == MediaType.APPLICATION_JSON && unmarshaller.getJsonTypeConfiguration().getJsonTypeAttributeName() != null) {
                        type = atts.getValue(Constants.EMPTY_STRING, unmarshaller.getJsonTypeConfiguration().getJsonTypeAttributeName());
                }
                if (null != type) {
                    XPathFragment typeFragment = new XPathFragment(type, xmlReader.getNamespaceSeparator(), xmlReader.isNamespaceAware());
                    // set the prefix using a reverse key lookup by uri value on namespaceMap
                    if (xmlReader.isNamespaceAware() && null != unmarshalNamespaceResolver) {
                        typeFragment.setNamespaceURI(unmarshalNamespaceResolver.getNamespaceURI(typeFragment.getPrefix()));
                    }
                    Descriptor lookupDescriptor = xmlContext.getDescriptorByGlobalType(typeFragment);
                    if(lookupDescriptor == null) {
                        QName lookupQName = null;
                        if(typeFragment.getNamespaceURI() == null){
                            lookupQName= new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, typeFragment.getLocalName());
                        }else{
                            lookupQName= new QName(typeFragment.getNamespaceURI(), typeFragment.getLocalName());
                        }
                        //check to see if type attribute represents simple type
                        if(null == session) {
                           session = (CoreAbstractSession) xmlContext.getSession();
                        }
                        ConversionManager conversionManager = (ConversionManager) session.getDatasourcePlatform().getConversionManager();
                        primitiveWrapperClass = conversionManager.javaType(lookupQName);
                    }else{
                        //found descriptor based on type attribute
                        xmlDescriptor = lookupDescriptor;
                        session = xmlContext.getSession(xmlDescriptor);
                    }
                }
            } else {
                if(null != xmlDescriptor.getDefaultRootElementField() && !unmarshaller.isResultAlwaysXMLRoot()){
                    String descLocalName = xmlDescriptor.getDefaultRootElementField().getXPathFragment().getLocalName();
                    if( descLocalName != null && descLocalName.equals(localName) ){
                        String descUri = xmlDescriptor.getDefaultRootElementField().getXPathFragment().getNamespaceURI();
                        if(!xmlReader.isNamespaceAware() || (xmlReader.isNamespaceAware() && ((namespaceURI == null && descUri == null ) || (namespaceURI !=null &&namespaceURI.length() == 0 && descUri == null ) || (namespaceURI != null && namespaceURI.equals(descUri))))){
                            //found a descriptor based on root element then know we won't need to wrap in an XMLRoot
                           shouldWrap = false;
                        }
                    }
                }

                if(xmlDescriptor.hasInheritance()){
                    //if descriptor has inheritance check class indicator
                    session = xmlContext.getSession(xmlDescriptor);
                    UnmarshalRecord tmpUnmarshalRecord = new UnmarshalRecordImpl(null);
                    tmpUnmarshalRecord.setUnmarshaller(unmarshaller);
                    tmpUnmarshalRecord.setUnmarshalNamespaceResolver(unmarshalNamespaceResolver);
                    tmpUnmarshalRecord.setXMLReader(this.getXMLReader());
                    tmpUnmarshalRecord.setAttributes(atts);

                    Class classValue = xmlDescriptor.getInheritancePolicy().classFromRow(new org.eclipse.persistence.oxm.record.UnmarshalRecord(tmpUnmarshalRecord), session);
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
                       xmlDescriptor = (Descriptor)session.getDescriptor(classValue);
                    } else {
                       // since there is no xsi:type attribute, we'll use the descriptor
                       // that was retrieved based on the rootQName -  we need to make
                       // sure it is non-abstract
                       if (Modifier.isAbstract(xmlDescriptor.getJavaClass().getModifiers())) {
                           // need to throw an exception here
                           throw DescriptorException.missingClassIndicatorField((XMLRecord) tmpUnmarshalRecord, (org.eclipse.persistence.oxm.XMLDescriptor)xmlDescriptor.getInheritancePolicy().getDescriptor());
                       }
                   }
                }
            }

            if (null == xmlDescriptor) {
                //check for a cached object and look for descriptor by class
                Object obj = this.xmlReader.getCurrentObject(session, null);
                if (obj != null) {
                    xmlDescriptor = (Descriptor)xmlContext.getSession(obj.getClass()).getDescriptor(obj.getClass());
                }
            }

            if (null == xmlDescriptor && primitiveWrapperClass == null){
                if(!this.keepAsElementPolicy.isKeepNoneAsElement()) {
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
                    unmappedContentHandler.startElement(namespaceURI, localName, qName, atts);
                    xmlReader.setContentHandler(unmappedContentHandler);
                    setObject(unmappedContentHandlerWrapper.getCurrentObject());
                    return;
                }
            }

            if (xmlDescriptor == null && primitiveWrapperClass != null) {
                session = xmlContext.getSession((Descriptor) null);
                rootRecord = unmarshaller.createRootUnmarshalRecord(primitiveWrapperClass);
                rootRecord.setSession((CoreAbstractSession) unmarshaller.getContext().getSession());
            } else{
                if(session == null){
                    session = xmlContext.getSession(xmlDescriptor);
                }
                rootRecord = unmarshaller.createUnmarshalRecord(xmlDescriptor, session);
            }
            this.descriptor = xmlDescriptor;

            rootRecord.setUnmarshaller(this.unmarshaller);
            rootRecord.setXMLReader(this.getXMLReader());

            if (locator != null) {
                rootRecord.setDocumentLocator(xmlReader.getLocator());
            }
            rootRecord.setAttributes(atts);

            boolean hasNilAttribute = (atts != null && null != atts.getValue(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.SCHEMA_NIL_ATTRIBUTE));
            rootRecord.setNil(isNil || hasNilAttribute);

            rootRecord.setUnmarshalNamespaceResolver(unmarshalNamespaceResolver);

            rootRecord.startDocument();
            rootRecord.initializeRecord((Mapping) null);
            xmlReader.setContentHandler(rootRecord);
            xmlReader.setLexicalHandler(rootRecord);

            Object attributeGroup = this.unmarshaller.getUnmarshalAttributeGroup();
            if(attributeGroup != null) {
                if(attributeGroup.getClass() == CoreClassConstants.STRING) {
                    CoreAttributeGroup group = descriptor.getAttributeGroup((String)attributeGroup);
                    if(group != null) {
                        rootRecord.setUnmarshalAttributeGroup(group);
                    } else {
                        //Error
                    }
                } else if(attributeGroup instanceof CoreAttributeGroup) {
                    rootRecord.setUnmarshalAttributeGroup((CoreAttributeGroup)attributeGroup);
                } else {
                    //Error case
                }
            }

            rootRecord.startElement(namespaceURI, localName, qName, atts);

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

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
    }

    @Override
    public void characters(CharSequence characters) throws SAXException {
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
    }

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    public Unmarshaller getUnmarshaller() {
        return this.unmarshaller;
    }

    public void setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy policy) {
        this.keepAsElementPolicy = policy;
    }

    public UnmarshalKeepAsElementPolicy getKeepAsElementPolicy() {
        return this.keepAsElementPolicy;
    }

    @Override
    public void setNil(boolean isNil) {
        this.isNil = isNil;

    }

}
