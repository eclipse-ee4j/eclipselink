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
package org.eclipse.persistence.internal.oxm;

import java.util.List;
import javax.xml.namespace.QName;

import org.eclipse.persistence.core.sessions.CoreSession;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.oxm.mappings.AnyObjectMapping;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.internal.oxm.record.XMLReader;
import org.eclipse.persistence.internal.oxm.record.deferred.AnyMappingContentHandler;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is how the XML Any Object Mapping is handled when
 * used with the TreeObjectBuilder.</p>
 */
public class XMLAnyObjectMappingNodeValue extends XMLRelationshipMappingNodeValue {
    private AnyObjectMapping xmlAnyObjectMapping;

    public XMLAnyObjectMappingNodeValue(AnyObjectMapping xmlAnyObjectMapping) {
        super();
        this.xmlAnyObjectMapping = xmlAnyObjectMapping;
    }

    public boolean isOwningNode(XPathFragment xPathFragment) {
        return null == xPathFragment;
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver) {
        return marshal(xPathFragment, marshalRecord, object, session, namespaceResolver, ObjectMarshalContext.getInstance());
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        if (xmlAnyObjectMapping.isReadOnly()) {
            return false;
        }
        Object objectValue = marshalContext.getAttributeValue(object, xmlAnyObjectMapping);
        return this.marshalSingleValue(xPathFragment, marshalRecord, object, objectValue, session, namespaceResolver, marshalContext);
    }

    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object objectValue, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {        
        XPathFragment rootFragment = null;

        Marshaller marshaller = marshalRecord.getMarshaller();
        objectValue = xmlAnyObjectMapping.convertObjectValueToDataValue(objectValue, session, marshalRecord.getMarshaller());

        if (null == objectValue) {
            return false;
        }
        XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
        marshalRecord.closeStartGroupingElements(groupingFragment);

        boolean wasXMLRoot = false;
        XPathFragment xmlRootFragment = null;
        Object originalValue = objectValue;

        if (xmlAnyObjectMapping.usesXMLRoot() && (objectValue instanceof Root)) {
            xmlRootFragment = new XPathFragment();
            xmlRootFragment.setNamespaceAware(marshalRecord.isNamespaceAware());            
            wasXMLRoot = true;
            objectValue = ((Root) objectValue).getObject();
        }

        if (objectValue instanceof String) {
            marshalSimpleValue(xmlRootFragment, marshalRecord, originalValue, object, objectValue, session, namespaceResolver);

        } else {
            CoreSession childSession = null;
            try {
                childSession = marshaller.getContext().getSession(objectValue);
            } catch (XMLMarshalException e) {
                marshalSimpleValue(xmlRootFragment, marshalRecord, originalValue, object, objectValue, session, namespaceResolver);
                return true;
            }
            Descriptor descriptor = (Descriptor) childSession.getDescriptor(objectValue);
            ObjectBuilder objectBuilder = (ObjectBuilder) descriptor.getObjectBuilder();

            List extraNamespaces = objectBuilder.addExtraNamespacesToNamespaceResolver(descriptor, marshalRecord, session, true, true);
            if (wasXMLRoot) {
                Namespace generatedNamespace = setupFragment(((Root) originalValue), xmlRootFragment, marshalRecord);
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
                marshalRecord.addXsiTypeAndClassIndicatorIfRequired(descriptor, descriptor, (Field)xmlAnyObjectMapping.getField(), originalValue, objectValue, wasXMLRoot, false);
                objectBuilder.buildRow(marshalRecord, objectValue, (org.eclipse.persistence.internal.sessions.AbstractSession) childSession, marshaller, null);
                marshalRecord.afterContainmentMarshal(object, objectValue);
                marshalRecord.endElement(rootFragment, namespaceResolver);
                marshalRecord.removeExtraNamespacesFromNamespaceResolver(extraNamespaces, session);
            }
        }

        return true;
    }

    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
        try {
        	Descriptor workingDescriptor = findReferenceDescriptor(xPathFragment, unmarshalRecord, atts, xmlAnyObjectMapping, xmlAnyObjectMapping.getKeepAsElementPolicy());

            UnmarshalKeepAsElementPolicy policy = xmlAnyObjectMapping.getKeepAsElementPolicy();
            if (null != policy && ((workingDescriptor == null && policy.isKeepUnknownAsElement()) || policy.isKeepAllAsElement())) {
                setupHandlerForKeepAsElementPolicy(unmarshalRecord, xPathFragment, atts);
            }else if (workingDescriptor != null) {
                processChild(xPathFragment, unmarshalRecord, atts, workingDescriptor, xmlAnyObjectMapping);
            }else{
                AnyMappingContentHandler handler = new AnyMappingContentHandler(unmarshalRecord, xmlAnyObjectMapping.usesXMLRoot());
                String qnameString = xPathFragment.getLocalName();
                if (xPathFragment.getPrefix() != null) {
                    qnameString = xPathFragment.getPrefix() + Constants.COLON + qnameString;
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
        UnmarshalRecord childRecord = unmarshalRecord.getChildRecord();
        if (null != childRecord) {
            Object childObject = childRecord.getCurrentObject();
            // OBJECT VALUE
            if (xmlAnyObjectMapping.usesXMLRoot()) {
            	Descriptor workingDescriptor = childRecord.getDescriptor();
                if (workingDescriptor != null) {
                    String prefix = xPathFragment.getPrefix();
                    if ((prefix == null) && (xPathFragment.getNamespaceURI() != null)) {
                        prefix = unmarshalRecord.resolveNamespaceUri(xPathFragment.getNamespaceURI());
                    }
                    childObject = workingDescriptor.wrapObjectInXMLRoot(childObject, xPathFragment.getNamespaceURI(), xPathFragment.getLocalName(), prefix, false, unmarshalRecord.isNamespaceAware(), unmarshalRecord.getUnmarshaller());
                    workingDescriptor = null;
                }
            }
            childObject = xmlAnyObjectMapping.convertDataValueToObjectValue(childObject, unmarshalRecord.getSession(), unmarshalRecord.getUnmarshaller());
            unmarshalRecord.setAttributeValue(childObject, xmlAnyObjectMapping);
        } else {
            SAXFragmentBuilder builder = unmarshalRecord.getFragmentBuilder();

            UnmarshalKeepAsElementPolicy keepAsElementPolicy = xmlAnyObjectMapping.getKeepAsElementPolicy();

            if (null != keepAsElementPolicy && (keepAsElementPolicy.isKeepUnknownAsElement() || keepAsElementPolicy.isKeepAllAsElement()) && builder.getNodes().size() > 1) {
                setOrAddAttributeValueForKeepAsElement(builder, xmlAnyObjectMapping, xmlAnyObjectMapping, unmarshalRecord, false, null);
            } else {
                // TEXT VALUE   
                if(xmlAnyObjectMapping.isMixedContent()) {
                    endElementProcessText(unmarshalRecord, xmlAnyObjectMapping, xPathFragment, null);
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
            Root xmlRoot = unmarshalRecord.createRoot();
            xmlRoot.setNamespaceURI(xPathFragment.getNamespaceURI());
            xmlRoot.setSchemaType(unmarshalRecord.getTypeQName());
            xmlRoot.setLocalName(xPathFragment.getLocalName());
            xmlRoot.setObject(value);
           // xmlRoot.setDeclaredType(type);
            unmarshalRecord.setAttributeValue(xmlRoot, xmlAnyObjectMapping);
        }
    }
    
    
    private Namespace setupFragment(Root originalValue, XPathFragment xmlRootFragment, MarshalRecord marshalRecord) {
        Namespace generatedNamespace = null;
        String xpath = originalValue.getLocalName();
        if (originalValue.getNamespaceURI() != null) {
            xmlRootFragment.setNamespaceURI((originalValue).getNamespaceURI());
            String prefix = marshalRecord.getNamespaceResolver().resolveNamespaceURI((originalValue).getNamespaceURI());
            if (prefix == null || prefix.length() == 0) {
                prefix = marshalRecord.getNamespaceResolver().generatePrefix("ns0");
                generatedNamespace = new Namespace(prefix, xmlRootFragment.getNamespaceURI());
            }
            xpath = prefix + Constants.COLON + xpath;
        }
        xmlRootFragment.setXPath(xpath);
        return generatedNamespace;
    }

    private void marshalSimpleValue(XPathFragment xmlRootFragment, MarshalRecord marshalRecord, Object originalValue, Object object, Object value, CoreAbstractSession session, NamespaceResolver namespaceResolver) {
    	QName qname = null;
        if (xmlRootFragment != null) {
            qname = ((Root) originalValue).getSchemaType();
            Namespace generatedNamespace = setupFragment(((Root) originalValue), xmlRootFragment, marshalRecord);
            getXPathNode().startElement(marshalRecord, xmlRootFragment, object, session, namespaceResolver, null, null);
            if (generatedNamespace != null) {                
                marshalRecord.namespaceDeclaration(generatedNamespace.getPrefix(),  generatedNamespace.getNamespaceURI());
            }
            updateNamespaces(qname, marshalRecord, null);
        }
        
        if (value instanceof org.w3c.dom.Node) {
        	marshalRecord.node((org.w3c.dom.Node) value, marshalRecord.getNamespaceResolver());        
        } else {
            marshalRecord.characters(qname, value, null, false);
        }

        if (xmlRootFragment != null) {
            marshalRecord.endElement(xmlRootFragment, namespaceResolver);
        }
    }

    public AnyObjectMapping getMapping() {
        return xmlAnyObjectMapping;
    }

    public boolean isWhitespaceAware() {
        return false;
    }

    public boolean isAnyMappingNodeValue() {
        return true;
    }
    
    @Override
    public boolean isMixedContentNodeValue() {
        return this.xmlAnyObjectMapping.isMixedContent();
    }

    @Override
    protected Descriptor findReferenceDescriptor(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts, Mapping mapping, UnmarshalKeepAsElementPolicy policy) {
    	Descriptor referenceDescriptor = super.findReferenceDescriptor(xPathFragment, unmarshalRecord, atts, mapping, policy);
        if (referenceDescriptor == null) {
            Context xmlContext = unmarshalRecord.getUnmarshaller().getContext(); 
            XPathQName xpathQName = new XPathQName(xPathFragment.getNamespaceURI(), xPathFragment.getLocalName(), unmarshalRecord.isNamespaceAware());
            referenceDescriptor = (Descriptor) xmlContext.getDescriptor(xpathQName);
            // Check if descriptor is for a wrapper, if it is null it out and let continue
            if (referenceDescriptor != null && referenceDescriptor.isWrapper()) {
                referenceDescriptor = null;
            }
        }
        return referenceDescriptor;
    }

}