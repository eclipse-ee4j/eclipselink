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
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.oxm.record.XMLReader;
import org.eclipse.persistence.internal.oxm.record.deferred.AnyMappingContentHandler;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DatabaseMapping.WriteType;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.oxm.mappings.XMLAnyObjectMapping;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.sessions.Session;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is how the XML Any Object Mapping is handled when
 * used with the TreeObjectBuilder.</p>
 */
public class XMLAnyObjectMappingNodeValue extends XMLRelationshipMappingNodeValue implements NullCapableValue {
    private XMLAnyObjectMapping xmlAnyObjectMapping;

    public XMLAnyObjectMappingNodeValue(XMLAnyObjectMapping xmlAnyObjectMapping) {
        super();
        this.xmlAnyObjectMapping = xmlAnyObjectMapping;
    }

    public boolean isOwningNode(XPathFragment xPathFragment) {
        return null == xPathFragment;
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver) {
        return marshal(xPathFragment, marshalRecord, object, session, namespaceResolver, ObjectMarshalContext.getInstance());
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        if (xmlAnyObjectMapping.isReadOnly()) {
            return false;
        }
        Object objectValue = marshalContext.getAttributeValue(object, xmlAnyObjectMapping);
        return this.marshalSingleValue(xPathFragment, marshalRecord, object, objectValue, session, namespaceResolver, marshalContext);
    }

    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object objectValue, AbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {        
        XPathFragment rootFragment = null;

        XMLMarshaller marshaller = marshalRecord.getMarshaller();
        if(xmlAnyObjectMapping.getConverter() != null) {
            objectValue = xmlAnyObjectMapping.getConverter().convertObjectValueToDataValue(objectValue, session, marshalRecord.getMarshaller());
        }

        if (null == objectValue) {
            return false;
        }
        XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
        marshalRecord.closeStartGroupingElements(groupingFragment);

        boolean wasXMLRoot = false;
        XPathFragment xmlRootFragment = null;
        Object originalValue = objectValue;

        if (xmlAnyObjectMapping.usesXMLRoot() && (objectValue instanceof XMLRoot)) {
            xmlRootFragment = new XPathFragment();
            wasXMLRoot = true;
            objectValue = ((XMLRoot) objectValue).getObject();
        }

        if (objectValue instanceof String) {
            marshalSimpleValue(xmlRootFragment, marshalRecord, originalValue, object, objectValue, session, namespaceResolver);

        } else {
            Session childSession = null;
            try {
                childSession = marshaller.getXMLContext().getSession(objectValue);
            } catch (XMLMarshalException e) {
                marshalSimpleValue(xmlRootFragment, marshalRecord, originalValue, object, objectValue, session, namespaceResolver);
                return true;
            }
            XMLDescriptor descriptor = (XMLDescriptor) childSession.getDescriptor(objectValue);
            TreeObjectBuilder objectBuilder = (TreeObjectBuilder) descriptor.getObjectBuilder();

            List extraNamespaces = objectBuilder.addExtraNamespacesToNamespaceResolver(descriptor, marshalRecord, session);
            if (wasXMLRoot) {
                Namespace generatedNamespace = setupFragment(((XMLRoot) originalValue), xmlRootFragment, marshalRecord);
                if (generatedNamespace != null) {
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
                marshalRecord.beforeContainmentMarshal(objectValue);

                if (xmlRootFragment != null) {
                    rootFragment = xmlRootFragment;
                } else {
                    rootFragment = new XPathFragment(defaultRootElementString);
                    //resolve URI
                    if (rootFragment.getNamespaceURI() == null) {
                        if(rootFragment.getPrefix() != null) {
                            String uri = descriptor.getNonNullNamespaceResolver().resolveNamespacePrefix(rootFragment.getPrefix());
                            rootFragment.setNamespaceURI(uri);
                        } else {
                            rootFragment.setNamespaceURI(descriptor.getNonNullNamespaceResolver().getDefaultNamespaceURI());
                        }
                    }
                }

                if (!wasXMLRoot) {
                    marshalRecord.setLeafElementType(descriptor.getDefaultRootElementType());
                }
                getXPathNode().startElement(marshalRecord, rootFragment, object, session, descriptor.getNonNullNamespaceResolver(), objectBuilder, objectValue);               
              
                writeExtraNamespaces(extraNamespaces, marshalRecord, session);
                objectBuilder.addXsiTypeAndClassIndicatorIfRequired(marshalRecord, descriptor, descriptor, (XMLField)xmlAnyObjectMapping.getField(), originalValue, objectValue, wasXMLRoot, false);
                objectBuilder.buildRow(marshalRecord, objectValue, (org.eclipse.persistence.internal.sessions.AbstractSession) childSession, marshaller, null, WriteType.UNDEFINED);
                marshalRecord.afterContainmentMarshal(object, objectValue);
                marshalRecord.endElement(rootFragment, namespaceResolver);
                objectBuilder.removeExtraNamespacesFromNamespaceResolver(marshalRecord, extraNamespaces, session);
            }
        }

        return true;
    }

    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
        try {
            XMLDescriptor workingDescriptor = findReferenceDescriptor(xPathFragment, unmarshalRecord, atts, xmlAnyObjectMapping, xmlAnyObjectMapping.getKeepAsElementPolicy());

            UnmarshalKeepAsElementPolicy policy = xmlAnyObjectMapping.getKeepAsElementPolicy();
            if (((workingDescriptor == null) && (policy == UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT)) || (policy == UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT)) {
                setupHandlerForKeepAsElementPolicy(unmarshalRecord, xPathFragment, atts);
            }else if (workingDescriptor != null) {
                processChild(xPathFragment, unmarshalRecord, atts, workingDescriptor, xmlAnyObjectMapping);
            }else{
                AnyMappingContentHandler handler = new AnyMappingContentHandler(unmarshalRecord, xmlAnyObjectMapping.usesXMLRoot());
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
        unmarshalRecord.removeNullCapableValue(this);
        UnmarshalRecord childRecord = unmarshalRecord.getChildRecord();
        if (null != childRecord) {
            Object childObject = childRecord.getCurrentObject();
            // OBJECT VALUE
            if (xmlAnyObjectMapping.usesXMLRoot()) {
                XMLDescriptor workingDescriptor = childRecord.getDescriptor();
                if (workingDescriptor != null) {
                    String prefix = xPathFragment.getPrefix();
                    if ((prefix == null) && (xPathFragment.getNamespaceURI() != null)) {
                        prefix = unmarshalRecord.resolveNamespaceUri(xPathFragment.getNamespaceURI());
                    }
                    childObject = workingDescriptor.wrapObjectInXMLRoot(childObject, xPathFragment.getNamespaceURI(), xPathFragment.getLocalName(), prefix, false);
                    workingDescriptor = null;
                }
            }
            if(xmlAnyObjectMapping.getConverter() != null) {
                childObject = xmlAnyObjectMapping.getConverter().convertDataValueToObjectValue(childObject, unmarshalRecord.getSession(), unmarshalRecord.getUnmarshaller());
            }
            unmarshalRecord.setAttributeValue(childObject, xmlAnyObjectMapping);
        } else {
            SAXFragmentBuilder builder = unmarshalRecord.getFragmentBuilder();

            UnmarshalKeepAsElementPolicy keepAsElementPolicy = xmlAnyObjectMapping.getKeepAsElementPolicy();

            if ((((keepAsElementPolicy == UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT) || (keepAsElementPolicy == UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT))) && (builder.getNodes().size() > 1)) {
                setOrAddAttributeValueForKeepAsElement(builder, xmlAnyObjectMapping, xmlAnyObjectMapping.getConverter(), unmarshalRecord, false, null);
            } else {
                // TEXT VALUE   
                if(xmlAnyObjectMapping.isMixedContent()) {
                    endElementProcessText(unmarshalRecord, xmlAnyObjectMapping.getConverter(), xPathFragment, null);
                } else {
                    unmarshalRecord.resetStringBuffer();
                }
            }
        }
    }

    protected void setOrAddAttributeValue(UnmarshalRecord unmarshalRecord, Object value, XPathFragment xPathFragment, Object collection){
        if (!xmlAnyObjectMapping.usesXMLRoot()) {
            unmarshalRecord.setAttributeValue(value, xmlAnyObjectMapping);
        } else {
            XMLRoot xmlRoot = new XMLRoot();
            xmlRoot.setNamespaceURI(xPathFragment.getNamespaceURI());
            xmlRoot.setSchemaType(unmarshalRecord.getTypeQName());
            xmlRoot.setLocalName(xPathFragment.getLocalName());
            xmlRoot.setObject(value);
            unmarshalRecord.setAttributeValue(xmlRoot, xmlAnyObjectMapping);
        }
    }
    

    public void setNullValue(Object object, Session session) {
        xmlAnyObjectMapping.setAttributeValueInObject(object, null);
    }

    public boolean isNullCapableValue() {
        return true;
    }

    private Namespace setupFragment(XMLRoot originalValue, XPathFragment xmlRootFragment, MarshalRecord marshalRecord) {
        Namespace generatedNamespace = null;
        String xpath = originalValue.getLocalName();
        if (originalValue.getNamespaceURI() != null) {
            xmlRootFragment.setNamespaceURI((originalValue).getNamespaceURI());
            String prefix = marshalRecord.getNamespaceResolver().resolveNamespaceURI((originalValue).getNamespaceURI());
            if (prefix == null || prefix.length() == 0) {
                prefix = marshalRecord.getNamespaceResolver().generatePrefix("ns0");
                generatedNamespace = new Namespace(prefix, xmlRootFragment.getNamespaceURI());
            }
            xpath = prefix + XMLConstants.COLON + xpath;
        }
        xmlRootFragment.setXPath(xpath);
        return generatedNamespace;
    }

    private void marshalSimpleValue(XPathFragment xmlRootFragment, MarshalRecord marshalRecord, Object originalValue, Object object, Object value, AbstractSession session, NamespaceResolver namespaceResolver) {
        if (xmlRootFragment != null) {
            QName qname = ((XMLRoot) originalValue).getSchemaType();
            value = getValueToWrite(qname, value, (XMLConversionManager) session.getDatasourcePlatform().getConversionManager(), marshalRecord);
            Namespace generatedNamespace = setupFragment(((XMLRoot) originalValue), xmlRootFragment, marshalRecord);
            getXPathNode().startElement(marshalRecord, xmlRootFragment, object, session, namespaceResolver, null, null);
            if (generatedNamespace != null) {
                marshalRecord.attribute(XMLConstants.XMLNS_URL, generatedNamespace.getPrefix(), XMLConstants.XMLNS + XMLConstants.COLON + generatedNamespace.getPrefix(), generatedNamespace.getNamespaceURI());
            }
            updateNamespaces(qname, marshalRecord, null);
        }

        if (value instanceof String) {
            marshalRecord.characters((String) value);
        } else {
            marshalRecord.node((org.w3c.dom.Node) value, marshalRecord.getNamespaceResolver());
        }

        if (xmlRootFragment != null) {
            marshalRecord.endElement(xmlRootFragment, namespaceResolver);
        }
    }

    public XMLAnyObjectMapping getMapping() {
        return xmlAnyObjectMapping;
    }

    public boolean isWhitespaceAware() {
        return false;
    }

    public boolean isAnyMappingNodeValue() {
        return true;
    }

    protected XMLDescriptor findReferenceDescriptor(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts, DatabaseMapping mapping, UnmarshalKeepAsElementPolicy policy) {
        XMLDescriptor referenceDescriptor = super.findReferenceDescriptor(xPathFragment, unmarshalRecord, atts, mapping, policy);
        if (referenceDescriptor == null) {
            XMLContext xmlContext = unmarshalRecord.getUnmarshaller().getXMLContext(); 
            QName qname = new QName(xPathFragment.getNamespaceURI(), xPathFragment.getLocalName());
            referenceDescriptor = xmlContext.getDescriptor(qname);
            // Check if descriptor is for a wrapper, if it is null it out and let continue
            if (referenceDescriptor != null && referenceDescriptor.isWrapper()) {
                referenceDescriptor = null;
            }
        }
        return referenceDescriptor;
    }

}