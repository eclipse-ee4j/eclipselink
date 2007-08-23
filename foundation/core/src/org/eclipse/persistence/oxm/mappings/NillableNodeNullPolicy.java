/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.oxm.mappings;

import org.eclipse.persistence.internal.oxm.NullCapableValue;
import org.eclipse.persistence.internal.oxm.XPathEngine;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.XPathNode;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.sessions.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;

public class NillableNodeNullPolicy implements NodeNullPolicy {
    private static final String TRUE = "true";

    public boolean directMarshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Session session, NamespaceResolver namespaceResolver) {
        XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
        String xsiPrefix;
        if(null == namespaceResolver) {
            xsiPrefix = XMLConstants.SCHEMA_INSTANCE_PREFIX;
            namespaceResolver = new NamespaceResolver();
            namespaceResolver.put(xsiPrefix, XMLConstants.SCHEMA_INSTANCE_URL);
        } else {
            xsiPrefix = namespaceResolver.resolveNamespaceURI(XMLConstants.SCHEMA_INSTANCE_URL);
            if(null == xsiPrefix) {
                xsiPrefix = namespaceResolver.generatePrefix();
            }
        }
        XPathFragment nilFragment = new XPathFragment('@' + xsiPrefix + ':' +  XMLConstants.SCHEMA_NIL_ATTRIBUTE);
        marshalRecord.attribute(nilFragment, namespaceResolver, TRUE);
        marshalRecord.closeStartGroupingElements(groupingFragment);            
        
        return true;
    }

    public boolean compositeObjectMarshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Session session, NamespaceResolver namespaceResolver) {
        XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
        marshalRecord.closeStartGroupingElements(groupingFragment);
        marshalRecord.openStartElement(xPathFragment, namespaceResolver);
        String xsiPrefix;
        if(null == namespaceResolver) {
            xsiPrefix = XMLConstants.SCHEMA_INSTANCE_PREFIX;
            namespaceResolver = new NamespaceResolver();
            namespaceResolver.put(xsiPrefix, XMLConstants.SCHEMA_INSTANCE_URL);
        } else {
            xsiPrefix = namespaceResolver.resolveNamespaceURI(XMLConstants.SCHEMA_INSTANCE_URL);
            if(null == xsiPrefix) {
                xsiPrefix = namespaceResolver.generatePrefix();
            }
        }
        XPathFragment nilFragment = new XPathFragment('@' + xsiPrefix + ':' +  XMLConstants.SCHEMA_NIL_ATTRIBUTE);
        marshalRecord.attribute(nilFragment, namespaceResolver, TRUE);
        marshalRecord.closeStartElement();
        marshalRecord.endElement(xPathFragment, namespaceResolver);        
        return true;
    }
    
    /**
     * When using the DOM Platform, this method is responsible for marshalling
     * null values for the XML Composite Object Mapping.
     * @param record
     * @param object
     * @param field
     * @return true if this method caused any objects to be marshalled, else 
     * false.
     */
    public boolean compositeObjectMarshal(XMLRecord record, Object object, XMLField field) {
        Node root = record.getDOM();
        Element nested = (Element)XPathEngine.getInstance().create(field, root);
        nested.setAttributeNS(XMLConstants.SCHEMA_INSTANCE_URL, "xsi:nil", "true");
        return true;
    }
    
    public boolean valueIsNull(Attributes attributes) {
        int index=attributes.getIndex(XMLConstants.SCHEMA_INSTANCE_URL, XMLConstants.SCHEMA_NIL_ATTRIBUTE);
        if(index>=0) {
            return true;
        }
        return false;
    }

    public boolean valueIsNull(Element element) {
        return null == element || element.hasAttributeNS(XMLConstants.SCHEMA_INSTANCE_URL, XMLConstants.SCHEMA_NIL_ATTRIBUTE);
    }

    public boolean isNullCapabableValue() {
        return true;
    }
    
    public void xPathNode(XPathNode xPathNode, NullCapableValue nullCapableValue) {
    }
    
}
