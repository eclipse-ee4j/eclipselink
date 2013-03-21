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

import java.util.Collection;
import java.util.Map;

import org.eclipse.persistence.core.sessions.CoreSession;
import org.eclipse.persistence.internal.core.queries.CoreContainerPolicy;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.mappings.BinaryDataCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.ChoiceCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.CollectionReferenceMapping;
import org.eclipse.persistence.internal.oxm.mappings.CompositeCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.DirectCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.UnmarshalContext;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;

import org.xml.sax.Attributes;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is how the XML Choice Collection Mapping is 
 * handled when used with the TreeObjectBuilder.</p> 
 * @author mmacivor
 */
public class XMLChoiceCollectionMappingUnmarshalNodeValue extends MappingNodeValue implements ContainerValue {
    private NodeValue choiceElementNodeValue;
    private NodeValue choiceElementMarshalNodeValue;
    private ChoiceCollectionMapping xmlChoiceCollectionMapping;
    private Mapping nestedMapping;
    private Map<Field, NodeValue> fieldToNodeValues;
    private Field xmlField;
    private ContainerValue containerNodeValue;
    private boolean isMixedNodeValue;
    private int index = -1;

    public XMLChoiceCollectionMappingUnmarshalNodeValue(ChoiceCollectionMapping mapping, Field xmlField) {
        this.xmlChoiceCollectionMapping = mapping;
        this.xmlField = xmlField;
        this.nestedMapping = (Mapping) mapping.getChoiceElementMappings().get(xmlField);
        initializeNodeValue();
    }
    
    public XMLChoiceCollectionMappingUnmarshalNodeValue(ChoiceCollectionMapping mapping, Field xmlField, Mapping nestedMapping) {
        this.xmlChoiceCollectionMapping = mapping;
        this.xmlField = xmlField;
        this.nestedMapping = nestedMapping;
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
        Mapping xmlMapping = this.nestedMapping;
        if(xmlMapping instanceof BinaryDataCollectionMapping) {
            choiceElementNodeValue = new XMLBinaryDataCollectionMappingNodeValue((BinaryDataCollectionMapping)xmlMapping);
            choiceElementMarshalNodeValue = choiceElementNodeValue;
        } else if(xmlMapping instanceof DirectCollectionMapping) {
            choiceElementNodeValue = new XMLCompositeDirectCollectionMappingNodeValue((DirectCollectionMapping)xmlMapping);
            choiceElementMarshalNodeValue = choiceElementNodeValue;
        } else if(xmlMapping instanceof CompositeCollectionMapping){
            choiceElementNodeValue = new XMLCompositeCollectionMappingNodeValue((CompositeCollectionMapping)xmlMapping);
            choiceElementMarshalNodeValue = choiceElementNodeValue;
        } else {
            choiceElementNodeValue = new XMLCollectionReferenceMappingNodeValue((CollectionReferenceMapping)xmlMapping, xmlField);
        	 CollectionReferenceMapping refMapping = ((CollectionReferenceMapping)xmlMapping);
             if(refMapping.usesSingleNode() || refMapping.getFields().size() == 1) {
            	 choiceElementMarshalNodeValue = new XMLCollectionReferenceMappingNodeValue(refMapping, xmlField);

             } else {
            	 choiceElementMarshalNodeValue = new XMLCollectionReferenceMappingMarshalNodeValue((CollectionReferenceMapping)xmlMapping);
             }
        }
    }
    
    public void setContainerNodeValue(XMLChoiceCollectionMappingUnmarshalNodeValue nodeValue) {
        this.containerNodeValue = nodeValue;
    }
    
    public void setNullValue(Object object, CoreSession session) {
        xmlChoiceCollectionMapping.setAttributeValueInObject(object, null);
    }

    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
        Object collection = unmarshalRecord.getContainerInstance(this.containerNodeValue);

        if(null != xmlChoiceCollectionMapping.getConverter()) {
            UnmarshalContext unmarshalContext = unmarshalRecord.getUnmarshalContext();
            unmarshalRecord.setUnmarshalContext(new ChoiceUnmarshalContext(unmarshalContext, xmlChoiceCollectionMapping));
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

    public CoreContainerPolicy getContainerPolicy() {
        return xmlChoiceCollectionMapping.getContainerPolicy();
    }

    public boolean isContainerValue() {
        return true;
    }  
    
    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object value, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        //empty impl in the unmarshal node value
        return false;
    }
    
    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver) {
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

    public boolean isWrapperAllowedAsCollectionName() {
        return false;
    }

    public boolean isMarshalNodeValue() {
        return false;
    }
    
    public ChoiceCollectionMapping getMapping() {
        return xmlChoiceCollectionMapping;
    }    

    public boolean getReuseContainer() {
        return getMapping().getReuseContainer();
    }

    public void setFieldToNodeValues(Map<Field, NodeValue> fieldToNodeValues) {
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

    /**
     * INTERNAL
     * Return true if an empty container should be set on the object if there
     * is no presence of the collection in the XML document.
     * @since EclipseLink 2.3.3
     */
    public boolean isDefaultEmptyContainer() {
        return getMapping().isDefaultEmptyContainer();
    }

}
