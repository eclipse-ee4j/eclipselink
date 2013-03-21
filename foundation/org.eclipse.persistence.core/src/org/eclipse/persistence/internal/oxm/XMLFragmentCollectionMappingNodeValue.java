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

import org.eclipse.persistence.internal.core.queries.CoreContainerPolicy;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.mappings.FragmentCollectionMapping;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.internal.oxm.record.XMLReader;

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
    private FragmentCollectionMapping xmlFragmentCollectionMapping;
    private int index = -1;

    public XMLFragmentCollectionMappingNodeValue(FragmentCollectionMapping xmlFragmentCollectionMapping) {
        super();
        this.xmlFragmentCollectionMapping = xmlFragmentCollectionMapping;
    }

    /**
     * Override the method in XPathNode such that the marshaller can be set on the
     * marshalRecord - this is required for XMLConverter usage.
     */
    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver) {
        if (xmlFragmentCollectionMapping.isReadOnly()) {
            return false;
        }

        CoreContainerPolicy cp = xmlFragmentCollectionMapping.getContainerPolicy();
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
            marshalSingleValue(xPathFragment, marshalRecord, object, objectValue, session, namespaceResolver, ObjectMarshalContext.getInstance());
        }
        return true;
    }
    
    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
        SAXFragmentBuilder builder = unmarshalRecord.getFragmentBuilder();
        builder.setOwningRecord(unmarshalRecord);
        try {
            String namespaceURI = Constants.EMPTY_STRING;
            if(xPathFragment.getNamespaceURI() != null) {
                namespaceURI = xPathFragment.getNamespaceURI();
            }
            String qName = xPathFragment.getLocalName();
            if(xPathFragment.getPrefix() != null) {
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
        } catch(SAXException ex) {
            // Do nothing.
        }
        return true;
    }
    
    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
    	SAXFragmentBuilder builder = unmarshalRecord.getFragmentBuilder();
        Object value = builder.getNodes().remove(builder.getNodes().size() -1);
        unmarshalRecord.addAttributeValue(this, value);
    }

    public Object getContainerInstance() {
        return getContainerPolicy().containerInstance();
    }

    public void setContainerInstance(Object object, Object containerInstance) {
        xmlFragmentCollectionMapping.setAttributeValueInObject(object, containerInstance);
    }

    public CoreContainerPolicy getContainerPolicy() {
        return xmlFragmentCollectionMapping.getContainerPolicy();
    }

    public boolean isContainerValue() {
        return true;
    }

    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object value, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        if (value instanceof Node) {
            marshalRecord.node((org.w3c.dom.Node)value, namespaceResolver);
        }
        return true;
    }

    public FragmentCollectionMapping getMapping() {
        return xmlFragmentCollectionMapping;
    }

    public boolean getReuseContainer() {
        return getMapping().getReuseContainer();
    }
    
    /**
     *  INTERNAL:
     *  Used to track the index of the corresponding containerInstance in the containerInstances Object[] on UnmarshalRecord 
     */  
    public void setIndex(int index){
    	this.index = index;
    }
    
    /**
     * INTERNAL:
     * Set to track the index of the corresponding containerInstance in the containerInstances Object[] on UnmarshalRecord
     * Set during TreeObjectBuilder initialization 
     */
    public int getIndex(){
    	return index;
    }

    /**
     * INTERNAL
     * Return true if an empty container should be set on the object if there
     * is no presence of the collection in the XML document.
     * @since EclipseLink 2.3.3
     */
    public boolean isDefaultEmptyContainer() {
        return getMapping().isDefaultEmptyContainer();
    }

    @Override
    public boolean isWrapperAllowedAsCollectionName() {
        return false;
    }

}
