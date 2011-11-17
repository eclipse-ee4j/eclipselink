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

import java.util.Collection;
import java.util.Map;

import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.UnmarshalContext;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLChoiceCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCollectionReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLMapping;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.internal.queries.ContainerPolicy;

import org.xml.sax.Attributes;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is how the XML Choice Collection Mapping is 
 * handled when used with the TreeObjectBuilder.</p> 
 * @author mmacivor
 */
public class XMLChoiceCollectionMappingUnmarshalNodeValue extends NodeValue implements ContainerValue {
    private NodeValue choiceElementNodeValue;
    private NodeValue choiceElementMarshalNodeValue;
    private XMLChoiceCollectionMapping xmlChoiceCollectionMapping;
    private Map<XMLField, NodeValue> fieldToNodeValues;
    private XMLField xmlField;
    private ContainerValue containerNodeValue;
    private boolean isMixedNodeValue;
    private int index = -1;

    public XMLChoiceCollectionMappingUnmarshalNodeValue(XMLChoiceCollectionMapping mapping, XMLField xmlField) {
        this.xmlChoiceCollectionMapping = mapping;
        this.xmlField = xmlField;
        initializeNodeValue();
    }
    
    public boolean isOwningNode(XPathFragment xPathFragment) {
        if(isMixedNodeValue) {
            if(xPathFragment.nameIsText()) {
                return true;
            } else {
                return false;
            }
        }
        return choiceElementNodeValue.isOwningNode(xPathFragment);
    }
    
    private void initializeNodeValue() {
        XMLMapping xmlMapping = xmlChoiceCollectionMapping.getChoiceElementMappings().get(xmlField);
        if(xmlMapping instanceof XMLBinaryDataCollectionMapping) {
            choiceElementNodeValue = new XMLBinaryDataCollectionMappingNodeValue((XMLBinaryDataCollectionMapping)xmlMapping);
            choiceElementMarshalNodeValue = choiceElementNodeValue;
        } else if(xmlMapping instanceof XMLCompositeDirectCollectionMapping) {
            choiceElementNodeValue = new XMLCompositeDirectCollectionMappingNodeValue((XMLCompositeDirectCollectionMapping)xmlMapping);
            choiceElementMarshalNodeValue = choiceElementNodeValue;
        } else if(xmlMapping instanceof XMLCompositeCollectionMapping){
            choiceElementNodeValue = new XMLCompositeCollectionMappingNodeValue((XMLCompositeCollectionMapping)xmlMapping);
            choiceElementMarshalNodeValue = choiceElementNodeValue;
        } else {
            choiceElementNodeValue = new XMLCollectionReferenceMappingNodeValue((XMLCollectionReferenceMapping)xmlMapping, xmlField);
            choiceElementMarshalNodeValue = new XMLCollectionReferenceMappingMarshalNodeValue((XMLCollectionReferenceMapping)xmlMapping);
        }
    }
    
    public void setContainerNodeValue(XMLChoiceCollectionMappingUnmarshalNodeValue nodeValue) {
        this.containerNodeValue = nodeValue;
    }
    
    public void setNullValue(Object object, Session session) {
        xmlChoiceCollectionMapping.setAttributeValueInObject(object, null);
    }

    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
        Object collection = unmarshalRecord.getContainerInstance(this.containerNodeValue);

        if(null != xmlChoiceCollectionMapping.getConverter()) {
            UnmarshalContext unmarshalContext = unmarshalRecord.getUnmarshalContext();
            unmarshalRecord.setUnmarshalContext(new ChoiceUnmarshalContext(unmarshalContext, xmlChoiceCollectionMapping.getConverter()));
            this.choiceElementNodeValue.endElement(xPathFragment, unmarshalRecord, collection);
            unmarshalRecord.setUnmarshalContext(unmarshalContext);            
        } else {
            this.choiceElementNodeValue.endElement(xPathFragment, unmarshalRecord, collection);
        }
    }
    
    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
        return this.choiceElementNodeValue.startElement(xPathFragment, unmarshalRecord, atts);
    }
    
    public void setXPathNode(XPathNode xPathNode) {
        super.setXPathNode(xPathNode);
        this.choiceElementNodeValue.setXPathNode(xPathNode);
    }
    
    public Object getContainerInstance() {
        return getContainerPolicy().containerInstance();
    }

    public void setContainerInstance(Object object, Object containerInstance) {
        xmlChoiceCollectionMapping.setAttributeValueInObject(object, containerInstance);
    }

    public ContainerPolicy getContainerPolicy() {
        return xmlChoiceCollectionMapping.getContainerPolicy();
    }

    public boolean isContainerValue() {
        return true;
    }  
    
    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object value, AbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        //empty impl in the unmarshal node value
        return false;
    }
    
    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver) {
        //dummy impl in the unmarshal node value
        return false;
    }
    
    public NodeValue getChoiceElementNodeValue() {
        return this.choiceElementNodeValue;
    }
    
    public NodeValue getChoiceElementMarshalNodeValue() {
        return this.choiceElementMarshalNodeValue;
    }
    
    public boolean isUnmarshalNodeValue() {
        return true;
    }
    
    public boolean isMarshalNodeValue() {
        return false;
    }
    
    public XMLChoiceCollectionMapping getMapping() {
        return xmlChoiceCollectionMapping;
    }    

    public boolean getReuseContainer() {
        return getMapping().getReuseContainer();
    }

    public void setFieldToNodeValues(Map<XMLField, NodeValue> fieldToNodeValues) {
        this.fieldToNodeValues = fieldToNodeValues;
    }    
    
    public Collection<NodeValue> getAllNodeValues() {
        return this.fieldToNodeValues.values();
    }
    
    /**
     * The underlying choice element node value will handle attributes.
     * 
     */
    public void attribute(UnmarshalRecord unmarshalRecord, String URI, String localName, String value) {
        this.choiceElementNodeValue.attribute(unmarshalRecord, URI, localName, value);
    }
    
    /**
     * INTERNAL:
     * Indicates that this is the choice mapping node value that represents the mixed content.
     */
    public void setIsMixedNodeValue(boolean isMixed) {
        this.isMixedNodeValue = isMixed;
    }

    /**
     * INTERNAL:
     * Return true if this is the node value representing mixed content.
     */
    public boolean isMixedContentNodeValue() {
        return this.isMixedNodeValue;
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
}
