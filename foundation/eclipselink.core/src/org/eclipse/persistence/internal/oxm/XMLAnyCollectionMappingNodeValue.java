/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm;

import java.util.List;
import javax.xml.namespace.QName;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.descriptors.Namespace;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.UnmappedContentHandlerWrapper;
import org.eclipse.persistence.internal.oxm.record.deferred.AnyMappingContentHandler;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is how the XML Any Collection Mapping is handled when
 * used with the TreeObjectBuilder.</p>
 */
public class XMLAnyCollectionMappingNodeValue extends XMLRelationshipMappingNodeValue implements ContainerValue {
    private XMLAnyCollectionMapping xmlAnyCollectionMapping;
    private XMLDescriptor workingDescriptor;

    public XMLAnyCollectionMappingNodeValue(XMLAnyCollectionMapping xmlAnyCollectionMapping) {
        super();
        this.xmlAnyCollectionMapping = xmlAnyCollectionMapping;
    }

    public boolean isOwningNode(XPathFragment xPathFragment) {
        return null == xPathFragment;
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver) {
        if (xmlAnyCollectionMapping.isReadOnly()) {
            return false;
        }
        XMLDescriptor descriptor;
        TreeObjectBuilder objectBuilder;
        AbstractSession childSession;
        ContainerPolicy cp = xmlAnyCollectionMapping.getContainerPolicy();
        Object collection = xmlAnyCollectionMapping.getAttributeAccessor().getAttributeValueFromObject(object);
        if (null == collection) {
            return false;
        }
        Object iterator = cp.iteratorFor(collection);
        if (cp.hasNext(iterator)) {
            XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
            marshalRecord.closeStartGroupingElements(groupingFragment);
        } else {
            return false;
        }
        Object objectValue;
        while (cp.hasNext(iterator)) {
            objectValue = cp.next(iterator, (org.eclipse.persistence.internal.sessions.AbstractSession)session);
            marshalSingleValue(xPathFragment, marshalRecord, object, objectValue, session, namespaceResolver, ObjectMarshalContext.getInstance());
        }

        return true;
    }

    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
        try {
            // Mixed Content
            Object collection = unmarshalRecord.getContainerInstance(this);
            startElementProcessText(unmarshalRecord, collection);
            XMLDescriptor xmlDescriptor = null;

            XMLContext xmlContext = unmarshalRecord.getUnmarshaller().getXMLContext();

            if (xmlAnyCollectionMapping.usesXMLRoot()) {
                String schemaType = atts.getValue(XMLConstants.SCHEMA_INSTANCE_URL, XMLConstants.SCHEMA_TYPE_ATTRIBUTE);
                XPathFragment frag = new XPathFragment();
                if ((null != schemaType) && (!schemaType.equals(""))) {
                    frag.setXPath(schemaType);

                    if (frag.hasNamespace()) {
                        String prefix = frag.getPrefix();
                        String url = unmarshalRecord.resolveNamespacePrefix(prefix);
                        frag.setNamespaceURI(url);
                    }
                    xmlDescriptor = xmlContext.getDescriptorByGlobalType(frag);
                }
            }
            if (xmlDescriptor == null) {
                QName qname = new QName(xPathFragment.getNamespaceURI(), xPathFragment.getLocalName());
                xmlDescriptor = xmlContext.getDescriptor(qname);
            }
            workingDescriptor = xmlDescriptor;
            UnmarshalKeepAsElementPolicy policy = xmlAnyCollectionMapping.getKeepAsElementPolicy();
            if (((xmlDescriptor == null) && (policy == UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT)) || (policy == UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT)) {
                //setup handler stuff
                SAXFragmentBuilder builder = unmarshalRecord.getFragmentBuilder();
                builder.setOwningRecord(unmarshalRecord);
                try {
                    String namespaceURI = "";
                    if (xPathFragment.getNamespaceURI() != null) {
                        namespaceURI = xPathFragment.getNamespaceURI();
                    }
                    String qName = xPathFragment.getLocalName();
                    if (xPathFragment.getPrefix() != null) {
                        qName = xPathFragment.getPrefix() + ":" + qName;
                    }

                    builder.startElement(namespaceURI, xPathFragment.getLocalName(), qName, atts);
                    unmarshalRecord.getXMLReader().setContentHandler(builder);
                } catch (SAXException ex) {
                }
            } else if (xmlDescriptor != null) {
                processChild(xPathFragment, unmarshalRecord, atts, xmlDescriptor);
            } else {
                //need to give to special handler, let it find out what to do depending on if this is simple or complex content
                AnyMappingContentHandler handler = new AnyMappingContentHandler(unmarshalRecord, xmlAnyCollectionMapping.usesXMLRoot());
                String qnameString = xPathFragment.getLocalName();
                if (xPathFragment.getPrefix() != null) {
                    qnameString = xPathFragment.getPrefix() + ":" + qnameString;
                }
                handler.startElement(xPathFragment.getNamespaceURI(), xPathFragment.getLocalName(), qnameString, atts);
                unmarshalRecord.getXMLReader().setContentHandler(handler);
                return true;
            }
        } catch (SAXException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
        return true;
    }

    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
        Object collection = unmarshalRecord.getContainerInstance(this);
        if (null != unmarshalRecord.getChildRecord()) {
            // OBJECT VALUE
            if (!xmlAnyCollectionMapping.usesXMLRoot()) {
                unmarshalRecord.addAttributeValue(this, unmarshalRecord.getChildRecord().getCurrentObject());
            }
            if (xmlAnyCollectionMapping.usesXMLRoot()) {
                Object childObject = unmarshalRecord.getChildRecord().getCurrentObject();

                if (workingDescriptor != null) {
                    String prefix = xPathFragment.getPrefix();
                    if ((prefix == null) && (xPathFragment.getNamespaceURI() != null)) {
                        prefix = unmarshalRecord.resolveNamespaceUri(xPathFragment.getNamespaceURI());
                    }
                    childObject = workingDescriptor.wrapObjectInXMLRoot(childObject, xPathFragment.getNamespaceURI(), xPathFragment.getLocalName(), prefix, false);
                    unmarshalRecord.addAttributeValue(this, childObject);
                }
            }
            unmarshalRecord.setChildRecord(null);
            workingDescriptor = null;
        } else {
            SAXFragmentBuilder builder = unmarshalRecord.getFragmentBuilder();
            if (builder.getNodes().size() != 0) {
                //Grab the fragment and put it into the collection
                unmarshalRecord.addAttributeValue(this, builder.getNodes().pop());
            } else {
                //TEXT VALUE
                endElementProcessText(unmarshalRecord, collection, xPathFragment);
            }
        }
    }

    private void startElementProcessText(UnmarshalRecord unmarshalRecord, Object collection) {
        Object value = unmarshalRecord.getStringBuffer().toString().trim();
        unmarshalRecord.resetStringBuffer();
        if (!EMPTY_STRING.equals(value) && xmlAnyCollectionMapping.isMixedContent()) {
            unmarshalRecord.addAttributeValue(this, value);
        }
    }

    private void endElementProcessText(UnmarshalRecord unmarshalRecord, Object collection, XPathFragment xPathFragment) {
        Object value = unmarshalRecord.getStringBuffer().toString().trim();
        unmarshalRecord.resetStringBuffer();
        if (!EMPTY_STRING.equals(value) && xmlAnyCollectionMapping.isMixedContent()) {
            if (!xmlAnyCollectionMapping.usesXMLRoot()) {
                unmarshalRecord.addAttributeValue(this, value);
            } else {
                XMLRoot xmlRoot = new XMLRoot();
                xmlRoot.setNamespaceURI(xPathFragment.getNamespaceURI());
                xmlRoot.setLocalName(xPathFragment.getLocalName());
                xmlRoot.setObject(value);
                unmarshalRecord.addAttributeValue(this, xmlRoot);
            }
        }
    }

    public Object getContainerInstance() {
        return getContainerPolicy().containerInstance();
    }

    public void setContainerInstance(Object object, Object containerInstance) {
        xmlAnyCollectionMapping.setAttributeValueInObject(object, containerInstance);
    }

    public ContainerPolicy getContainerPolicy() {
        return xmlAnyCollectionMapping.getContainerPolicy();
    }
    
    public boolean isContainerValue() {
        return true;
    }

    private Namespace setupFragment(XMLRoot originalValue, XPathFragment xmlRootFragment, MarshalRecord marshalRecord) {
        Namespace generatedNamespace = null;
        String xpath = originalValue.getLocalName();
        if (originalValue.getNamespaceURI() != null) {
            xmlRootFragment.setNamespaceURI(((XMLRoot)originalValue).getNamespaceURI());
            String prefix = marshalRecord.getNamespaceResolver().resolveNamespaceURI(((XMLRoot)originalValue).getNamespaceURI());
            if ((prefix == null) || prefix.equals("")) {
                prefix = marshalRecord.getNamespaceResolver().generatePrefix();
                generatedNamespace = new Namespace(prefix, xmlRootFragment.getNamespaceURI());
            }
            xpath = prefix + ":" + xpath;
        }
        xmlRootFragment.setXPath(xpath);
        return generatedNamespace;
    }

    public void marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object value, AbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        if(null == value) {
            return;
        }
        
        boolean wasXMLRoot = false;
        XPathFragment xmlRootFragment = null;
        Object originalValue = value;

        XMLDescriptor descriptor;
        TreeObjectBuilder objectBuilder;
        AbstractSession childSession;
        XMLMarshaller marshaller = marshalRecord.getMarshaller();
        XPathFragment rootFragment;

        if (xmlAnyCollectionMapping.usesXMLRoot() && (value instanceof XMLRoot)) {
            xmlRootFragment = new XPathFragment();
            wasXMLRoot = true;
            value = ((XMLRoot)value).getObject();
        }
        UnmarshalKeepAsElementPolicy keepAsElementPolicy = xmlAnyCollectionMapping.getKeepAsElementPolicy();
        if (value instanceof String) {
            marshalSimpleValue(xmlRootFragment, marshalRecord, originalValue, object, value, session, namespaceResolver);
        } else if (((keepAsElementPolicy == UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT) || (keepAsElementPolicy == UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT)) && value instanceof org.w3c.dom.Node) {
            marshalRecord.node((org.w3c.dom.Node)value, marshalRecord.getNamespaceResolver());
        } else {
            try {
                childSession = marshaller.getXMLContext().getSession(value);
            } catch (XMLMarshalException e) {                
                marshalSimpleValue(xmlRootFragment, marshalRecord, originalValue, object, value, session, namespaceResolver);
                return;
            }
            descriptor = (XMLDescriptor)childSession.getDescriptor(value);
            objectBuilder = (TreeObjectBuilder)descriptor.getObjectBuilder();
            List extraNamespaces = objectBuilder.addExtraNamespacesToNamespaceResolver(descriptor, marshalRecord, session);
            if (wasXMLRoot) {
                Namespace generatedNamespace = setupFragment(((XMLRoot)originalValue), xmlRootFragment, marshalRecord);
                if (generatedNamespace != null) {
                    if (extraNamespaces == null) {
                        extraNamespaces = new java.util.ArrayList();
                    }
                    extraNamespaces.add(generatedNamespace);
                }
            }

            /*
             * B5112171: 25 Apr 2006
             * During marshalling - XML AnyObject and AnyCollection
             * mappings throw a NullPointerException when the
             * "document root element" on child object descriptors are not
             * all defined.  These nodes will be ignored with a warning.
             */
            String defaultRootElementString = descriptor.getDefaultRootElement();

            if (!wasXMLRoot && (defaultRootElementString == null)) {
                AbstractSessionLog.getLog().log(SessionLog.WARNING, "marshal_warning_null_document_root_element", new Object[] { Helper.getShortClassName(this.getClass()), descriptor });
            } else {
                if ((marshaller != null) && (marshaller.getMarshalListener() != null)) {
                    marshaller.getMarshalListener().beforeMarshal(value);
                }

                if (xmlRootFragment != null) {
                    rootFragment = xmlRootFragment;
                } else {
                    rootFragment = new XPathFragment(defaultRootElementString);
                    //resolve URI
                    if ((rootFragment.getNamespaceURI() == null) && (rootFragment.getPrefix() != null)) {
                        String uri = descriptor.getNonNullNamespaceResolver().resolveNamespacePrefix(rootFragment.getPrefix());
                        rootFragment.setNamespaceURI(uri);
                    }
                }

                if (!wasXMLRoot) {
                    marshalRecord.setLeafElementType(descriptor.getDefaultRootElementType());
                }

                getXPathNode().startElement(marshalRecord, rootFragment, object, childSession, marshalRecord.getNamespaceResolver(), objectBuilder, value);

                writeExtraNamespaces(extraNamespaces, marshalRecord, session);
                if (xmlAnyCollectionMapping.shouldAddXsiType(marshaller, descriptor, originalValue, wasXMLRoot)) {
                    String typeValue = descriptor.getSchemaReference().getSchemaContext();
                    addTypeAttribute(descriptor, marshalRecord, typeValue);
                }

                objectBuilder.buildRow(marshalRecord, value, (AbstractSession)session, marshaller);

                objectBuilder.removeExtraNamespacesFromNamespaceResolver(marshalRecord, extraNamespaces, session);

                marshalRecord.endElement(rootFragment, namespaceResolver);
                if ((marshaller != null) && (marshaller.getMarshalListener() != null)) {
                    marshaller.getMarshalListener().afterMarshal(value);
                }
            }
        }
    }

    private void marshalSimpleValue(XPathFragment xmlRootFragment, MarshalRecord marshalRecord, Object originalValue, Object object, Object value, AbstractSession session, NamespaceResolver namespaceResolver) {
        if (xmlRootFragment != null) {
            // QName qname = ((XMLRoot)originalValue).getSchemaType();
			QName qname = null;
                          
            XMLConversionManager xmlConversionManager = (XMLConversionManager)session.getDatasourcePlatform().getConversionManager();                
            value = xmlConversionManager.convertObject(value, ClassConstants.STRING, qname);
            
            Namespace generatedNamespace = setupFragment((XMLRoot)originalValue, xmlRootFragment, marshalRecord);

            getXPathNode().startElement(marshalRecord, xmlRootFragment, object, session, namespaceResolver, null, null);
            if (generatedNamespace != null) {
                marshalRecord.attribute(XMLConstants.XMLNS_URL, XMLConstants.XMLNS_URL, XMLConstants.XMLNS + ":" + generatedNamespace.getPrefix(), generatedNamespace.getNamespaceURI());
            }
            if (qname != null) {                
                String prefix = marshalRecord.getNamespaceResolver().resolveNamespaceURI(qname.getNamespaceURI());
                if ((prefix == null) || prefix.equals("")) {
                    prefix = marshalRecord.getNamespaceResolver().generatePrefix();
                    marshalRecord.attribute(XMLConstants.XMLNS_URL, XMLConstants.XMLNS_URL, XMLConstants.XMLNS + ":" + prefix, qname.getNamespaceURI());
                }
                String typeValue = prefix + ":" + qname.getLocalPart();
                addTypeAttribute(marshalRecord, typeValue);
            }
        }

        marshalRecord.characters((String)value);

        if (xmlRootFragment != null) {
            marshalRecord.endElement(xmlRootFragment, namespaceResolver);
        }
    }
    
    public XMLAnyCollectionMapping getMapping() {
        return xmlAnyCollectionMapping;
    }
 
}