/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.oxm.record.deferred.AnyMappingContentHandler;
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
    private XMLDescriptor workingDescriptor;

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
        XPathFragment rootFragment = null;

        if (xmlAnyObjectMapping.isReadOnly()) {
            return false;
        }

        XMLMarshaller marshaller = marshalRecord.getMarshaller();
        Object objectValue = marshalContext.getAttributeValue(object, xmlAnyObjectMapping);
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
                if ((marshaller != null) && (marshaller.getMarshalListener() != null)) {
                    marshaller.getMarshalListener().beforeMarshal(objectValue);
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
                getXPathNode().startElement(marshalRecord, rootFragment, object, session, descriptor.getNonNullNamespaceResolver(), objectBuilder, objectValue);

                if (xmlAnyObjectMapping.shouldAddXsiType(marshaller, descriptor, originalValue, wasXMLRoot)) {
                    String typeValue = descriptor.getSchemaReference().getSchemaContext();
                    addTypeAttribute(descriptor, marshalRecord, typeValue);
                }

                writeExtraNamespaces(extraNamespaces, marshalRecord, session);

                objectBuilder.buildRow(marshalRecord, objectValue, (org.eclipse.persistence.internal.sessions.AbstractSession) childSession, marshaller);
                marshalRecord.endElement(rootFragment, namespaceResolver);
                objectBuilder.removeExtraNamespacesFromNamespaceResolver(marshalRecord, extraNamespaces, session);
                if ((marshaller != null) && (marshaller.getMarshalListener() != null)) {
                    marshaller.getMarshalListener().afterMarshal(objectValue);
                }
            }
        }

        return true;
    }

    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
        try {
            XMLContext xmlContext = unmarshalRecord.getUnmarshaller().getXMLContext();
            XMLDescriptor xmlDescriptor = null;
            if (xmlAnyObjectMapping.usesXMLRoot()) {
                String schemaType = atts.getValue(XMLConstants.SCHEMA_INSTANCE_URL, XMLConstants.SCHEMA_TYPE_ATTRIBUTE);
                XPathFragment frag = new XPathFragment();
                if ((null != schemaType) && (!schemaType.equals(""))) {
                    frag.setXPath(schemaType);

                    if (frag.hasNamespace()) {
                        String prefix = frag.getPrefix();
                        String url = unmarshalRecord.resolveNamespacePrefix(prefix);
                        frag.setNamespaceURI(url);

                        QName qname = new QName(url, frag.getLocalName());
                        unmarshalRecord.setTypeQName(qname);
                    }
                    xmlDescriptor = xmlContext.getDescriptorByGlobalType(frag);
                }
            }

            if (xmlDescriptor == null) {
                QName qname = new QName(xPathFragment.getNamespaceURI(), xPathFragment.getLocalName());
                xmlDescriptor = xmlContext.getDescriptor(qname);
            }
            workingDescriptor = xmlDescriptor;
            UnmarshalKeepAsElementPolicy policy = xmlAnyObjectMapping.getKeepAsElementPolicy();
            if (((xmlDescriptor == null) && (policy == UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT)) || (policy == UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT)) {
                setupHandlerForKeepAsElementPolicy(unmarshalRecord, xPathFragment, atts);
            }            
            
            else if (xmlDescriptor != null) {            	
                   processChild(xPathFragment, unmarshalRecord, atts, xmlDescriptor);                
            }else{
                AnyMappingContentHandler handler = new AnyMappingContentHandler(unmarshalRecord, xmlAnyObjectMapping.usesXMLRoot());
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
        unmarshalRecord.removeNullCapableValue(this);
        Object childObject = null;
        if (null != unmarshalRecord.getChildRecord()) {
            childObject = unmarshalRecord.getChildRecord().getCurrentObject();
            // OBJECT VALUE
            if(xmlAnyObjectMapping.getConverter() != null) {
            	childObject = xmlAnyObjectMapping.getConverter().convertDataValueToObjectValue(childObject, unmarshalRecord.getSession(), unmarshalRecord.getUnmarshaller());
            }                    	
            if (!xmlAnyObjectMapping.usesXMLRoot()) {
                unmarshalRecord.setAttributeValue(childObject, xmlAnyObjectMapping);
            } else {
                if (workingDescriptor != null) {
                    String prefix = xPathFragment.getPrefix();
                    if ((prefix == null) && (xPathFragment.getNamespaceURI() != null)) {
                        prefix = unmarshalRecord.resolveNamespaceUri(xPathFragment.getNamespaceURI());
                    }
                    childObject = workingDescriptor.wrapObjectInXMLRoot(childObject, xPathFragment.getNamespaceURI(), xPathFragment.getLocalName(), prefix, false);
                    unmarshalRecord.setAttributeValue(childObject, xmlAnyObjectMapping);
                    workingDescriptor = null;
                }
            }
        } else {
            SAXFragmentBuilder builder = unmarshalRecord.getFragmentBuilder();

            UnmarshalKeepAsElementPolicy keepAsElementPolicy = xmlAnyObjectMapping.getKeepAsElementPolicy();

            if ((((keepAsElementPolicy == UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT) || (keepAsElementPolicy == UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT))) && (builder.getNodes().size() != 0)) {
                setOrAddAttributeValueForKeepAsElement(builder, xmlAnyObjectMapping, xmlAnyObjectMapping.getConverter(), unmarshalRecord, false);
            } else {
                // TEXT VALUE             
                endElementProcessText(unmarshalRecord, xPathFragment);
            }
        }
    }

    private void endElementProcessText(UnmarshalRecord unmarshalRecord, XPathFragment xPathFragment) {
        Object value = unmarshalRecord.getStringBuffer().toString().trim();
        if(xmlAnyObjectMapping.getConverter() != null) {
        	value = xmlAnyObjectMapping.getConverter().convertDataValueToObjectValue(value, unmarshalRecord.getSession(), unmarshalRecord.getUnmarshaller());
        }                    	
        unmarshalRecord.resetStringBuffer();
        if (!EMPTY_STRING.equals(value)) {
            QName qname = unmarshalRecord.getTypeQName();
            if (qname != null) {
                Class theClass = (Class) XMLConversionManager.getDefaultXMLTypes().get(qname);
                if (theClass != null) {
                    value = ((XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager()).convertObject(value, theClass, qname);
                }
            }

            if (!xmlAnyObjectMapping.usesXMLRoot()) {
                unmarshalRecord.setAttributeValue(value, xmlAnyObjectMapping);
            } else {
                XMLRoot xmlRoot = new XMLRoot();
                xmlRoot.setNamespaceURI(xPathFragment.getNamespaceURI());
                xmlRoot.setSchemaType(qname);
                xmlRoot.setLocalName(xPathFragment.getLocalName());
                xmlRoot.setObject(value);
                unmarshalRecord.setAttributeValue(xmlRoot, xmlAnyObjectMapping);
            }
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
            if ((prefix == null) || prefix.equals("")) {
                prefix = marshalRecord.getNamespaceResolver().generatePrefix("ns0");
                generatedNamespace = new Namespace(prefix, xmlRootFragment.getNamespaceURI());
            }
            xpath = prefix + ":" + xpath;
        }
        xmlRootFragment.setXPath(xpath);
        return generatedNamespace;
    }

    private void marshalSimpleValue(XPathFragment xmlRootFragment, MarshalRecord marshalRecord, Object originalValue, Object object, Object value, AbstractSession session, NamespaceResolver namespaceResolver) {
        if (xmlRootFragment != null) {
            QName qname = ((XMLRoot) originalValue).getSchemaType();
            value = ((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, ClassConstants.STRING, qname);
            Namespace generatedNamespace = setupFragment(((XMLRoot) originalValue), xmlRootFragment, marshalRecord);
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
    
}