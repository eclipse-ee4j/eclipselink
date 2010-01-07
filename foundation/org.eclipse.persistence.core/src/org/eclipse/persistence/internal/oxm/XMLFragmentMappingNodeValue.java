/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLFragmentMapping;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.sessions.Session;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is how the XML Fragment Collection Mapping is handled 
 * when used with the TreeObjectBuilder.</p>
 * @author  mmacivor
 */
public class XMLFragmentMappingNodeValue extends MappingNodeValue implements NullCapableValue {
    private XMLFragmentMapping xmlFragmentMapping;

    public XMLFragmentMappingNodeValue(XMLFragmentMapping xmlFragmentMapping) {
        super();
        this.xmlFragmentMapping = xmlFragmentMapping;
    }

    public boolean isOwningNode(XPathFragment xPathFragment) {
        return xPathFragment.getNextFragment() == null;
    }
    
    public void setNullValue(Object object, Session session) {
        Object value = xmlFragmentMapping.getAttributeValue(null, session);
        xmlFragmentMapping.setAttributeValueInObject(object, value);
    }

    public boolean isNullCapableValue() {
        return true;
    }
    
    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver) {
        return marshal(xPathFragment, marshalRecord, object, session, namespaceResolver, ObjectMarshalContext.getInstance());   
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        if (xmlFragmentMapping.isReadOnly()) {
            return false;
        }
        Object attributeValue = marshalContext.getAttributeValue(object, xmlFragmentMapping);
        return this.marshalSingleValue(xPathFragment, marshalRecord, object, attributeValue, session, namespaceResolver, marshalContext);
    }

    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object attributeValue, AbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        marshalRecord.openStartGroupingElements(namespaceResolver);
        if (!(attributeValue instanceof Node)) {
            return false;
        }
        marshalRecord.node((Node)attributeValue, namespaceResolver);
        return true;
    }
    
    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
        unmarshalRecord.removeNullCapableValue(this);
        SAXFragmentBuilder builder = unmarshalRecord.getFragmentBuilder();
        builder.setOwningRecord(unmarshalRecord);
        try {
            String namespaceURI = XMLConstants.EMPTY_STRING;
            if (xPathFragment.getNamespaceURI() != null) {
                namespaceURI = xPathFragment.getNamespaceURI();
            }
            String qName = xPathFragment.getLocalName();
            if (xPathFragment.getPrefix() != null) {
                qName = xPathFragment.getPrefix() + XMLConstants.COLON + qName;
            }
            builder.startElement(namespaceURI, xPathFragment.getLocalName(), qName, atts);
            unmarshalRecord.getXMLReader().setContentHandler(builder);
        } catch (SAXException ex) {
            // Do nothing.
        }
        return true;
    }
    
    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
        unmarshalRecord.removeNullCapableValue(this);
        XPathFragment lastFrag = ((XMLField)xmlFragmentMapping.getField()).getLastXPathFragment();
        SAXFragmentBuilder builder = unmarshalRecord.getFragmentBuilder();
        if (lastFrag.nameIsText()) {
            Object attributeValue = builder.buildTextNode(unmarshalRecord.getStringBuffer().toString());
            unmarshalRecord.resetStringBuffer();
            xmlFragmentMapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), attributeValue);
        } else if (!lastFrag.isAttribute()) {
            Object value = builder.getNodes().remove(builder.getNodes().size() -1);
            unmarshalRecord.setAttributeValue(value, xmlFragmentMapping);
        }
    }
    
    public void attribute(UnmarshalRecord unmarshalRecord, String namespaceURI, String localName, String value) {
        unmarshalRecord.removeNullCapableValue(this);
        if(namespaceURI == null) {
            namespaceURI = XMLConstants.EMPTY_STRING;
        }
        SAXFragmentBuilder builder = unmarshalRecord.getFragmentBuilder();
        Object attributeValue = builder.buildAttributeNode(namespaceURI, localName, value);
        xmlFragmentMapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), attributeValue);       
    }

    public XMLFragmentMapping getMapping() {
        return xmlFragmentMapping;
    }

}
