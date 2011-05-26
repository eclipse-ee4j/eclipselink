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
package org.eclipse.persistence.internal.oxm;

import java.util.List;
import javax.xml.namespace.QName;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.descriptors.Namespace;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.XMLReader;
import org.eclipse.persistence.internal.oxm.record.deferred.AnyMappingContentHandler;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.DatabaseMapping.WriteType;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
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
            objectValue = cp.next(iterator, session);
            if(xmlAnyCollectionMapping.getConverter() != null) {
                objectValue = xmlAnyCollectionMapping.getConverter().convertObjectValueToDataValue(objectValue, session, marshalRecord.getMarshaller());
            }
            marshalSingleValue(xPathFragment, marshalRecord, object, objectValue, session, namespaceResolver, ObjectMarshalContext.getInstance());
        }

        return true;
    }

    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
        try {
            // Mixed Content
            Object collection = unmarshalRecord.getContainerInstance(this);
            startElementProcessText(unmarshalRecord, collection);
            XMLContext xmlContext = unmarshalRecord.getUnmarshaller().getXMLContext();

            //used to only check xsitype when usesXMLRoot was true???
            XMLDescriptor workingDescriptor = findReferenceDescriptor(xPathFragment, unmarshalRecord, atts, xmlAnyCollectionMapping, xmlAnyCollectionMapping.getKeepAsElementPolicy());
            if (workingDescriptor == null) {         
                QName qname = new QName(xPathFragment.getNamespaceURI(), xPathFragment.getLocalName());
                workingDescriptor = xmlContext.getDescriptor(qname);
                // Check if descriptor is for a wrapper, if it is null it out and let continue
                if (workingDescriptor != null && workingDescriptor.isWrapper()) {
                    workingDescriptor = null;
                }
            }

            UnmarshalKeepAsElementPolicy policy = xmlAnyCollectionMapping.getKeepAsElementPolicy();
            if (((workingDescriptor == null) && (policy == UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT)) || (policy == UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT)) {
                setupHandlerForKeepAsElementPolicy(unmarshalRecord, xPathFragment, atts);
            } else if (workingDescriptor != null) {
                processChild(xPathFragment, unmarshalRecord, atts, workingDescriptor, xmlAnyCollectionMapping);
            } else {
                //need to give to special handler, let it find out what to do depending on if this is simple or complex content
                AnyMappingContentHandler handler = new AnyMappingContentHandler(unmarshalRecord, xmlAnyCollectionMapping.usesXMLRoot());
                String qnameString = xPathFragment.getLocalName();
                if (xPathFragment.getPrefix() != null) {
                    qnameString = xPathFragment.getPrefix() + XMLConstants.COLON + qnameString;
                }
                handler.startElement(xPathFragment.getNamespaceURI(), xPathFragment.getLocalName(), qnameString, atts);
                XMLReader xmlReader = unmarshalRecord.getXMLReader();
                xmlReader.setContentHandler(handler);
                xmlReader.setLexicalHandler(handler);
                return true;
            }
        } catch (SAXException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
        return true;
    }

    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
        Object collection = unmarshalRecord.getContainerInstance(this);
        UnmarshalRecord childRecord = unmarshalRecord.getChildRecord();
        if (null != childRecord) {
            // OBJECT VALUE
            if (!xmlAnyCollectionMapping.usesXMLRoot()) {
                Object objectValue = childRecord.getCurrentObject();
                if(xmlAnyCollectionMapping.getConverter() != null) {
                    objectValue = xmlAnyCollectionMapping.getConverter().convertDataValueToObjectValue(objectValue, unmarshalRecord.getSession(), unmarshalRecord.getUnmarshaller());
                }
                unmarshalRecord.addAttributeValue(this, objectValue);
            } else {
                Object childObject = childRecord.getCurrentObject();
                XMLDescriptor workingDescriptor = childRecord.getDescriptor();
                if (workingDescriptor != null) {
                    String prefix = xPathFragment.getPrefix();
                    if ((prefix == null) && (xPathFragment.getNamespaceURI() != null)) {
                        prefix = unmarshalRecord.resolveNamespaceUri(xPathFragment.getNamespaceURI());
                    }
                    childObject = workingDescriptor.wrapObjectInXMLRoot(childObject, xPathFragment.getNamespaceURI(), xPathFragment.getLocalName(), prefix, false);
                    if(xmlAnyCollectionMapping.getConverter() != null) {
                        childObject = xmlAnyCollectionMapping.getConverter().convertDataValueToObjectValue(childObject, unmarshalRecord.getSession(), unmarshalRecord.getUnmarshaller());
                    }
                    unmarshalRecord.addAttributeValue(this, childObject);
                }
            }
            unmarshalRecord.setChildRecord(null);
        } else {
            SAXFragmentBuilder builder = unmarshalRecord.getFragmentBuilder();

            UnmarshalKeepAsElementPolicy keepAsElementPolicy = xmlAnyCollectionMapping.getKeepAsElementPolicy();
            if ((((keepAsElementPolicy == UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT) || (keepAsElementPolicy == UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT))) && (builder.getNodes().size() > 1)) {
                setOrAddAttributeValueForKeepAsElement(builder, xmlAnyCollectionMapping, xmlAnyCollectionMapping.getConverter(), unmarshalRecord, true, null);
            } else {
                //TEXT VALUE
                if(xmlAnyCollectionMapping.isMixedContent()) {
                    endElementProcessText(unmarshalRecord, xmlAnyCollectionMapping.getConverter(), xPathFragment, null);
                } else {
                    unmarshalRecord.resetStringBuffer();
                }
            }
        }
    }

    private void startElementProcessText(UnmarshalRecord unmarshalRecord, Object collection) {
        String value = unmarshalRecord.getCharacters().toString();
        unmarshalRecord.resetStringBuffer();
        //if (!XMLConstants.EMPTY_STRING.equals(value) && xmlAnyCollectionMapping.isMixedContent()) {
        if (value.length() > 0 && xmlAnyCollectionMapping.isMixedContent()) {
            unmarshalRecord.addAttributeValue(this, value);
        }
    }
    
    protected void setOrAddAttributeValue(UnmarshalRecord unmarshalRecord, Object value, XPathFragment xPathFragment, Object collection){
        if (!xmlAnyCollectionMapping.usesXMLRoot() || xPathFragment.getLocalName() == null) {
            unmarshalRecord.addAttributeValue(this, value);
        } else {
            XMLRoot xmlRoot = new XMLRoot();
            xmlRoot.setNamespaceURI(xPathFragment.getNamespaceURI());
            xmlRoot.setSchemaType(unmarshalRecord.getTypeQName());
            xmlRoot.setLocalName(xPathFragment.getLocalName());
            xmlRoot.setObject(value);
            unmarshalRecord.addAttributeValue(this, xmlRoot);
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
            xmlRootFragment.setNamespaceURI((originalValue).getNamespaceURI());
            String prefix = marshalRecord.getNamespaceResolver().resolveNamespaceURI((originalValue).getNamespaceURI());
            if (prefix == null || prefix.length() == 0) {
                prefix = marshalRecord.getNamespaceResolver().generatePrefix();
                generatedNamespace = new Namespace(prefix, xmlRootFragment.getNamespaceURI());
            }
            xpath = prefix + XMLConstants.COLON + xpath;
        }
        xmlRootFragment.setXPath(xpath);
        return generatedNamespace;
    }

    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object value, AbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        if (null == value) {
            return false;
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
            value = ((XMLRoot) value).getObject();
            if(null == value){
                return false;
            }
        }
        UnmarshalKeepAsElementPolicy keepAsElementPolicy = xmlAnyCollectionMapping.getKeepAsElementPolicy();
        if (value instanceof String) {
            marshalSimpleValue(xmlRootFragment, marshalRecord, originalValue, object, value, session, namespaceResolver);
        } else if (((keepAsElementPolicy == UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT) || (keepAsElementPolicy == UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT)) && value instanceof org.w3c.dom.Node) {
            marshalRecord.node((org.w3c.dom.Node) value, marshalRecord.getNamespaceResolver());
        } else {
            try {
                childSession = marshaller.getXMLContext().getSession(value);
            } catch (XMLMarshalException e) {               
                marshalSimpleValue(xmlRootFragment, marshalRecord, originalValue, object, value, session, namespaceResolver);
                return true;
            }
            descriptor = (XMLDescriptor) childSession.getDescriptor(value);
            objectBuilder = (TreeObjectBuilder) descriptor.getObjectBuilder();
            List extraNamespaces = objectBuilder.addExtraNamespacesToNamespaceResolver(descriptor, marshalRecord, session);
            if (wasXMLRoot) {
                Namespace generatedNamespace = setupFragment(((XMLRoot) originalValue), xmlRootFragment, marshalRecord);
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
                marshalRecord.beforeContainmentMarshal(value);

                if (xmlRootFragment != null) {
                    rootFragment = xmlRootFragment;
                } else {
                    rootFragment = new XPathFragment(defaultRootElementString);
                    //resolve URI
                    if (rootFragment.getNamespaceURI() == null) {
                        String uri = descriptor.getNonNullNamespaceResolver().resolveNamespacePrefix(rootFragment.getPrefix());
                        rootFragment.setNamespaceURI(uri);
                    }
                }

                if (!wasXMLRoot) {
                    marshalRecord.setLeafElementType(descriptor.getDefaultRootElementType());
                }

                getXPathNode().startElement(marshalRecord, rootFragment, object, childSession, marshalRecord.getNamespaceResolver(), objectBuilder, value);                

                writeExtraNamespaces(extraNamespaces, marshalRecord, session);

                objectBuilder.addXsiTypeAndClassIndicatorIfRequired(marshalRecord, descriptor, descriptor, (XMLField)xmlAnyCollectionMapping.getField(), originalValue, value, wasXMLRoot, false);               
                objectBuilder.buildRow(marshalRecord, value, session, marshaller, null, WriteType.UNDEFINED);
                marshalRecord.afterContainmentMarshal(object, value);
                objectBuilder.removeExtraNamespacesFromNamespaceResolver(marshalRecord, extraNamespaces, session);

                marshalRecord.endElement(rootFragment, namespaceResolver);
            }
        }
        return true;
    }

    private void marshalSimpleValue(XPathFragment xmlRootFragment, MarshalRecord marshalRecord, Object originalValue, Object object, Object value, AbstractSession session, NamespaceResolver namespaceResolver) {
        if (xmlRootFragment != null) {
            QName qname = ((XMLRoot) originalValue).getSchemaType();
            value = getValueToWrite(qname, value, (XMLConversionManager) session.getDatasourcePlatform().getConversionManager(), marshalRecord);

            Namespace generatedNamespace = setupFragment((XMLRoot) originalValue, xmlRootFragment, marshalRecord);
            getXPathNode().startElement(marshalRecord, xmlRootFragment, object, session, namespaceResolver, null, null);
            if (generatedNamespace != null) {
                marshalRecord.attribute(XMLConstants.XMLNS_URL, generatedNamespace.getPrefix(), XMLConstants.XMLNS + XMLConstants.COLON + generatedNamespace.getPrefix(), generatedNamespace.getNamespaceURI());
            }
            updateNamespaces(qname, marshalRecord, null);                        
        }

        marshalRecord.characters((String) value);

        if (xmlRootFragment != null) {
            marshalRecord.endElement(xmlRootFragment, namespaceResolver);
        }
    }

    public XMLAnyCollectionMapping getMapping() {
        return xmlAnyCollectionMapping;
    }
    
    public boolean isWhitespaceAware() {
        return this.xmlAnyCollectionMapping.isMixedContent() && this.xmlAnyCollectionMapping.isWhitespacePreservedForMixedContent();
    }
    
    public boolean isAnyMappingNodeValue() {
        return true;
    }

    public boolean getReuseContainer() {
        return getMapping().getReuseContainer();
    }

}
