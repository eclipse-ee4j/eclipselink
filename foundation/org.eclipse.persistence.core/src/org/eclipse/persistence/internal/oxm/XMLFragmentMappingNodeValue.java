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

import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.eclipse.persistence.core.sessions.CoreSession;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.mappings.FragmentMapping;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.internal.oxm.record.XMLReader;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is how the XML Fragment Collection Mapping is handled 
 * when used with the TreeObjectBuilder.</p>
 * @author  mmacivor
 */
public class XMLFragmentMappingNodeValue extends MappingNodeValue implements NullCapableValue {
    private FragmentMapping xmlFragmentMapping;
    private boolean selfMapping;

    public XMLFragmentMappingNodeValue(FragmentMapping xmlFragmentMapping) {
        super();
        this.xmlFragmentMapping = xmlFragmentMapping;
        this.selfMapping = XPathFragment.SELF_XPATH.equals(xmlFragmentMapping.getXPath());
    }

    public boolean isOwningNode(XPathFragment xPathFragment) {
        return xPathFragment.getNextFragment() == null;
    }
    
    public void setNullValue(Object object, CoreSession session) {
        Object value = xmlFragmentMapping.getObjectValue(null, session);
        xmlFragmentMapping.setAttributeValueInObject(object, value);
    }

    public boolean isNullCapableValue() {
        return true;
    }
    
    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver) {
        return marshal(xPathFragment, marshalRecord, object, session, namespaceResolver, ObjectMarshalContext.getInstance());   
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        if (xmlFragmentMapping.isReadOnly()) {
            return false;
        }
        Object attributeValue = marshalContext.getAttributeValue(object, xmlFragmentMapping);
        return this.marshalSingleValue(xPathFragment, marshalRecord, object, attributeValue, session, namespaceResolver, marshalContext);
    }

    @Override
    public boolean marshalSelfAttributes(XPathFragment pathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver, Marshaller marshaller) {
        Node node = (Node) xmlFragmentMapping.getAttributeValueFromObject(object);
        NamedNodeMap attributes = node.getAttributes();
        if(null != attributes) {
            for(int x=0, attributesLength=attributes.getLength(); x<attributesLength; x++) {
                Node attribute = attributes.item(x);
                if(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attribute.getNamespaceURI())) {
                    String nsResolverPrefix = namespaceResolver.resolveNamespaceURI(attribute.getNodeValue());;
                    if(attribute.getLocalName().equals(nsResolverPrefix)) {
                        continue;
                    }
                }
                String namespaceURI = attribute.getNamespaceURI();
                String localName = attribute.getLocalName();
                String qualifiedName = localName;
                if(null != namespaceResolver) {
                    String prefix = namespaceResolver.resolveNamespaceURI(namespaceURI);
                    if(null != prefix) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(prefix);
                        stringBuilder.append(':');
                        stringBuilder.append(qualifiedName);
                        qualifiedName = prefix + ':' + qualifiedName;
                    }
                }
                marshalRecord.attribute(namespaceURI, localName, qualifiedName, attribute.getNodeValue());
            }
        }
        return true;
    }

    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object attributeValue, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        marshalRecord.openStartGroupingElements(namespaceResolver);
        if (!(attributeValue instanceof Node)) {
            return false;
        }
        Node nodeValue = (Node) attributeValue;
        if(selfMapping) {
            NodeList childNodes = nodeValue.getChildNodes();
            for(int x=0,childNodesLength=childNodes.getLength(); x<childNodesLength; x++) {
                Node node = childNodes.item(x);
                marshalRecord.node(childNodes.item(x), namespaceResolver);
            }
        } else {
            marshalRecord.node((Node)attributeValue, namespaceResolver);
        }
        return true;
    }
    
    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
        unmarshalRecord.removeNullCapableValue(this);
        SAXFragmentBuilder builder = unmarshalRecord.getFragmentBuilder();
        builder.setOwningRecord(unmarshalRecord);
        try {
            String namespaceURI = Constants.EMPTY_STRING;
            if (xPathFragment.getNamespaceURI() != null) {
                namespaceURI = xPathFragment.getNamespaceURI();
            }
            String qName = xPathFragment.getLocalName();
            if (xPathFragment.getPrefix() != null) {
                qName = xPathFragment.getPrefix() + Constants.COLON + qName;
            }
            if(!(unmarshalRecord.getPrefixesForFragment().isEmpty())) {
                for(Entry<String, String> next:((Map<String, String>) unmarshalRecord.getPrefixesForFragment()).entrySet()) {
                    builder.startPrefixMapping(next.getKey(), next.getValue());
                }
            }            
            builder.startElement(namespaceURI, xPathFragment.getLocalName(), qName, atts);
            XMLReader xmlReader = unmarshalRecord.getXMLReader();
            xmlReader.setContentHandler(builder);
            xmlReader.setLexicalHandler(null);
        } catch (SAXException ex) {
            // Do nothing.
        }
        return true;
    }
    
    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
        unmarshalRecord.removeNullCapableValue(this);
        XPathFragment lastFrag = ((Field)xmlFragmentMapping.getField()).getLastXPathFragment();
        SAXFragmentBuilder builder = unmarshalRecord.getFragmentBuilder();
        if (lastFrag.nameIsText()) {
            Object attributeValue = builder.buildTextNode(unmarshalRecord.getCharacters().toString());
            unmarshalRecord.resetStringBuffer();
            xmlFragmentMapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), attributeValue);
        } else if (!lastFrag.isAttribute()) {
            Object value = builder.getNodes().remove(builder.getNodes().size() -1);
            unmarshalRecord.setAttributeValue(value, xmlFragmentMapping);
        }
    }

    @Override
    public void endSelfNodeValue(UnmarshalRecord unmarshalRecord, UnmarshalRecord selfRecord, Attributes atts) {
        this.endElement(XPathFragment.SELF_FRAGMENT, unmarshalRecord);
    }

    public void attribute(UnmarshalRecord unmarshalRecord, String namespaceURI, String localName, String value) {
        unmarshalRecord.removeNullCapableValue(this);
        if(namespaceURI == null) {
            namespaceURI = Constants.EMPTY_STRING;
        }
        SAXFragmentBuilder builder = unmarshalRecord.getFragmentBuilder();
        Object attributeValue = builder.buildAttributeNode(namespaceURI, localName, value);
        xmlFragmentMapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), attributeValue);       
    }

    public FragmentMapping getMapping() {
        return xmlFragmentMapping;
    }

}