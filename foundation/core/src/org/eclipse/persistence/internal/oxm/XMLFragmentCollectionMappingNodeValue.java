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

import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.mappings.XMLFragmentCollectionMapping;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;

import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is how the XML Fragment Collection Mapping is handled 
 * when used with the TreeObjectBuilder.</p>
 * @author  mmacivor
 */
public class XMLFragmentCollectionMappingNodeValue extends NodeValue implements ContainerValue {
    private XMLFragmentCollectionMapping xmlFragmentCollectionMapping;

    public XMLFragmentCollectionMappingNodeValue(XMLFragmentCollectionMapping xmlFragmentCollectionMapping) {
        super();
        this.xmlFragmentCollectionMapping = xmlFragmentCollectionMapping;
    }

    /**
     * Override the method in XPathNode such that the marshaller can be set on the
     * marshalRecord - this is required for XMLConverter usage.
     */
    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver, XMLMarshaller marshaller) {
        if (xmlFragmentCollectionMapping.isReadOnly()) {
            return false;
        }

        ContainerPolicy cp = xmlFragmentCollectionMapping.getContainerPolicy();
        Object collection = xmlFragmentCollectionMapping.getAttributeAccessor().getAttributeValueFromObject(object);
        if (null == collection) {
            return false;
        }
        Object iterator = cp.iteratorFor(collection);
        if (cp.hasNext(iterator)) {
            marshalRecord.openStartGroupingElements(namespaceResolver);
        } else {
            return false;
        }
        Object objectValue;
        while (cp.hasNext(iterator)) {
            objectValue = cp.next(iterator, session);
            if (objectValue instanceof Node) {
                marshalRecord.node((org.w3c.dom.Node)objectValue, namespaceResolver);
            }
        }
        return true;
    }
    
    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver) {
        return marshal(xPathFragment, marshalRecord, object, session, namespaceResolver, null);
    }
    
    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
        SAXFragmentBuilder builder = unmarshalRecord.getFragmentBuilder();
        builder.setOwningRecord(unmarshalRecord);
        try {
            String namespaceURI = "";
            if(xPathFragment.getNamespaceURI() != null) {
                namespaceURI = xPathFragment.getNamespaceURI();
            }
            String qName = xPathFragment.getLocalName();
            if(xPathFragment.getPrefix() != null) {
                qName = xPathFragment.getPrefix() + ":" + qName;
            }
            builder.startElement(namespaceURI, xPathFragment.getLocalName(), qName, atts);
            unmarshalRecord.getXMLReader().setContentHandler(builder);
        } catch(SAXException ex) {
            // Do nothing.
        }
        return true;
    }
    
    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
        SAXFragmentBuilder builder = unmarshalRecord.getFragmentBuilder();
        Object value = builder.getNodes().pop();
        Object collection = unmarshalRecord.getContainerInstance(this);
        xmlFragmentCollectionMapping.getContainerPolicy().addInto(value, collection, unmarshalRecord.getSession());
    }

    public Object getContainerInstance() {
        return xmlFragmentCollectionMapping.getContainerPolicy().containerInstance();
    }

    public void setContainerInstance(Object object, Object containerInstance) {
        xmlFragmentCollectionMapping.setAttributeValueInObject(object, containerInstance);
    }

    public boolean isContainerValue() {
        return true;
    }    
}
