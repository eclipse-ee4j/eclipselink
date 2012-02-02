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
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLChoiceCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCollectionReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLMapping;
import org.eclipse.persistence.oxm.mappings.converters.XMLConverter;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.XMLRoot;
import java.util.Iterator;

public class XMLChoiceCollectionMappingMarshalNodeValue extends NodeValue implements ContainerValue {
    private XMLChoiceCollectionMapping xmlChoiceCollectionMapping;
    private Map<XMLField, NodeValue> fieldToNodeValues;
    private NodeValue choiceElementNodeValue;
    private XMLField xmlField;
    private boolean isMixedNodeValue;
    private int index = -1;

    public XMLChoiceCollectionMappingMarshalNodeValue(XMLChoiceCollectionMapping mapping, XMLField xmlField) {
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

    public void setFieldToNodeValues(Map<XMLField, NodeValue> fieldToNodeValues) {
        this.fieldToNodeValues = fieldToNodeValues;
    }

    private void initializeNodeValue() {
        XMLMapping xmlMapping = xmlChoiceCollectionMapping.getChoiceElementMappings().get(xmlField);
        if(xmlMapping instanceof XMLBinaryDataCollectionMapping) {
            choiceElementNodeValue = new XMLBinaryDataCollectionMappingNodeValue((XMLBinaryDataCollectionMapping)xmlMapping);
        } else if(xmlMapping instanceof XMLCompositeDirectCollectionMapping) {
            choiceElementNodeValue = new XMLCompositeDirectCollectionMappingNodeValue((XMLCompositeDirectCollectionMapping)xmlMapping);
        } else if(xmlMapping instanceof XMLCompositeCollectionMapping) {
            choiceElementNodeValue = new XMLCompositeCollectionMappingNodeValue((XMLCompositeCollectionMapping)xmlMapping);
        } else {
            XMLCollectionReferenceMapping refMapping = ((XMLCollectionReferenceMapping)xmlMapping);
            if(refMapping.usesSingleNode() || refMapping.getFields().size() == 1) {
                choiceElementNodeValue = new XMLCollectionReferenceMappingNodeValue(refMapping, xmlField);
            } else {
                choiceElementNodeValue = new XMLCollectionReferenceMappingMarshalNodeValue((XMLCollectionReferenceMapping)xmlMapping);
            }
        }
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver) {
        if(xmlChoiceCollectionMapping.isReadOnly()) {
            return false;
        }
        
        Object value = xmlChoiceCollectionMapping.getAttributeValueFromObject(object);
        if(value == null) {
            return false;
        }
        ContainerPolicy cp = getContainerPolicy();
        Object iterator = cp.iteratorFor(value);
        if (cp.hasNext(iterator)) {
            if(xPathFragment != null) {
                XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
                marshalRecord.closeStartGroupingElements(groupingFragment);
            }
        } else {
            return false;
        }
        
        while(cp.hasNext(iterator)) {
            Object nextValue = cp.next(iterator, session);
            marshalSingleValue(xPathFragment, marshalRecord, object, nextValue, session, namespaceResolver, ObjectMarshalContext.getInstance());
        }
        return true;
    }

    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object value, AbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        Converter converter = xmlChoiceCollectionMapping.getConverter();
        if (null != converter) {
            if (converter instanceof XMLConverter) {
                value = ((XMLConverter)converter).convertObjectValueToDataValue(value, session, marshalRecord.getMarshaller());
            } else {
                value = converter.convertObjectValueToDataValue(value, session);
            }
        }
    	NodeValue associatedNodeValue = null;
    	XMLField associatedField = null;
    	Object fieldValue = value;
    	if(value instanceof XMLRoot) {
    		XMLRoot rootValue = (XMLRoot)value;
    		String localName = rootValue.getLocalName();
    		String namespaceUri = rootValue.getNamespaceURI();
    		fieldValue = rootValue.getObject();
    		associatedField = getFieldForName(localName, namespaceUri);
    		if(associatedField == null) {
    			associatedField = xmlChoiceCollectionMapping.getClassToFieldMappings().get(fieldValue.getClass());
    		}
    	} else {
    	    if(value.getClass() == ClassConstants.STRING && this.xmlChoiceCollectionMapping.isMixedContent()) {
    	        marshalMixedContent(marshalRecord, (String)value);
    	        return true;
    	    } else {
    	        associatedField = xmlChoiceCollectionMapping.getClassToFieldMappings().get(value.getClass());
    	    }
    	}
    	if(associatedField == null) {
    	    //check the field associations
    	    List<XMLField> sourceFields = xmlChoiceCollectionMapping.getClassToSourceFieldsMappings().get(value.getClass());
    	    if(sourceFields != null) {
    	        associatedField = sourceFields.get(0);
    	    }
    	}
    	if(associatedField != null) {
			associatedNodeValue = this.fieldToNodeValues.get(associatedField);
		}
    	
    	if(associatedNodeValue != null) {
    		//Find the correct fragment
    		XPathFragment frag = associatedField.getXPathFragment();
    		NodeValue unwrappedNodeValue = ((XMLChoiceCollectionMappingUnmarshalNodeValue)associatedNodeValue).getChoiceElementMarshalNodeValue();
  		    while(frag != null) {
   		        if(associatedNodeValue.isOwningNode(frag)) {
   		            ContainerValue nestedNodeValue = (ContainerValue)unwrappedNodeValue;
   		            nestedNodeValue.marshalSingleValue(frag, marshalRecord, object, fieldValue, session, namespaceResolver, marshalContext);
   		            break;
   		        }
   		        frag = frag.getNextFragment();
                //if next frag is null, call node value before the loop breaks
   		        if(frag == null) {
   		            ContainerValue nestedNodeValue = (ContainerValue)associatedNodeValue;
   		            nestedNodeValue.marshalSingleValue(frag, marshalRecord, object, fieldValue, session, namespaceResolver, marshalContext);
   		        }
   		    }
   		}
    	return true;
    }

    private void marshalMixedContent(MarshalRecord record, String value) {
        record.characters(value);
    }

    private XMLField getFieldForName(String localName, String namespaceUri) {
    	Iterator<XMLField> fields = fieldToNodeValues.keySet().iterator(); 
    	while(fields.hasNext()) {
    		XMLField nextField = fields.next();
    		XPathFragment fragment = nextField.getXPathFragment();
    		while(fragment != null && (!fragment.nameIsText())) {
    			if(fragment.getNextFragment() == null || fragment.getHasText()) {
    				if(fragment.getLocalName().equals(localName)) {
    					String fragUri = fragment.getNamespaceURI();
    					if((namespaceUri == null && fragUri == null) || (namespaceUri != null && fragUri != null && namespaceUri.equals(fragUri))) {
    						return nextField;
    					}
    				}
    			}
    			fragment = fragment.getNextFragment();
    		}
    	}
    	return null;
    }
    
    public Collection<NodeValue> getAllNodeValues() {
        return this.fieldToNodeValues.values();
    }

    public boolean isMarshalNodeValue() {
        return true;
    }
    
    public boolean isUnmarshalNodeValue() {
        return false;
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
    
    public XMLChoiceCollectionMapping getMapping() {
        return xmlChoiceCollectionMapping;
    }    

    public boolean getReuseContainer() {
        return getMapping().getReuseContainer();
    }

    /**
     * INTERNAL:
     * Indicates that this is the choice mapping node value that represents the mixed content.
     */
    public void setIsMixedNodeValue(boolean b) {
        this.isMixedNodeValue = b;
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