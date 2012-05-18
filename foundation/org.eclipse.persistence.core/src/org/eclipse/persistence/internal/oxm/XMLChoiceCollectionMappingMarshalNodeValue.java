/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLChoiceCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCollectionReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLMapping;
import org.eclipse.persistence.oxm.mappings.converters.XMLConverter;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
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
            AbstractNullPolicy wrapperNP = xmlChoiceCollectionMapping.getWrapperNullPolicy();
            if (wrapperNP != null && wrapperNP.getMarshalNullRepresentation().equals(XMLNullRepresentationType.XSI_NIL)) {
                marshalRecord.nilSimple(namespaceResolver);
                return true;
            } else {
                return false;
            }
        }
        ContainerPolicy cp = getContainerPolicy();
        Object iterator = cp.iteratorFor(value);
        if (null != iterator && cp.hasNext(iterator)) {
            if(xPathFragment != null) {
                XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
                marshalRecord.closeStartGroupingElements(groupingFragment);
            }
        } else {
        	return marshalRecord.emptyCollection(xPathFragment, namespaceResolver, xmlChoiceCollectionMapping.getWrapperNullPolicy() != null);
        }
        
        if(marshalRecord.getMarshaller().getMediaType() == MediaType.APPLICATION_JSON){
        	List<NodeValue> nodeValues = new ArrayList();
            List<List> values = new ArrayList<List>();
            
            NodeValue mixedNodeValue = null;
            List mixedValues = null;
            
            //sort the elements. Results will be a list of nodevalues and a corresponding list of 
            //collections associated with those nodevalues
            while(cp.hasNext(iterator)) {        	    
        	    Object nextValue = getConvertedValue(cp.next(iterator, session), marshalRecord, session);
		        NodeValue nodeValue = getNodeValueForValue(nextValue);
		        
		        if(nodeValue != null){
		        	if(nodeValue == this){
		        		mixedNodeValue = this;
		        		if(mixedValues == null){
		        			mixedValues = new ArrayList();
		        		}
		        		mixedValues.add(nextValue);
		        	}else{
			            int index = nodeValues.indexOf(nodeValue);
	        	        if(index > -1){
	        	    	    values.get(index).add(nextValue);
	        	        }else{        	        	
		        	    	nodeValues.add(nodeValue);
		        	    	List valuesList = new ArrayList();
		        	    	valuesList.add(nextValue);
		        	    	values.add(valuesList);        	        	
	        	        }
		        	}
		        }        	  
            }
            //always write out mixed values last so we can determine if the textWrapper key needs to be written.
            if(mixedNodeValue != null){
            	nodeValues.add(mixedNodeValue);
            	values.add(mixedValues);
            }
            
            for(int i =0;i < nodeValues.size(); i++){
            	NodeValue associatedNodeValue = nodeValues.get(i);            	
            	List listValue = values.get(i);            	
            	
            	XPathFragment frag = null;
                if(associatedNodeValue == this){
                	frag = marshalRecord.getTextWrapperFragment();
                }else{            	
            	   frag = associatedNodeValue.getXPathNode().getXPathFragment();
            	   if(frag != null){
            		   frag = getOwningFragment(associatedNodeValue, frag);
            		   associatedNodeValue = ((XMLChoiceCollectionMappingUnmarshalNodeValue)associatedNodeValue).getChoiceElementMarshalNodeValue(); 
            	   }
                }
                if(frag != null){
                	
                    marshalRecord.startCollection(); 
                    
                    for(int j=0;j<listValue.size(); j++){              	          

                    	marshalSingleValueWithNodeValue(frag, marshalRecord, object, listValue.get(j), session, namespaceResolver, ObjectMarshalContext.getInstance(), associatedNodeValue);
                    }
                    marshalRecord.endCollection();     
                }    
            }                   
        }        
        else{        
            while(cp.hasNext(iterator)) {
                Object nextValue = cp.next(iterator, session);
                marshalSingleValue(xPathFragment, marshalRecord, object, nextValue, session, namespaceResolver, ObjectMarshalContext.getInstance());
            }
        }
        return true;
    }

    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object value, AbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {        
        value = getConvertedValue(value, marshalRecord, session);
        if(value.getClass() == ClassConstants.STRING && this.xmlChoiceCollectionMapping.isMixedContent()) {
    		marshalMixedContent(marshalRecord, (String)value);
	        return true;
    	}
        NodeValue associatedNodeValue = getNodeValueForValue(value);     
        if(associatedNodeValue != null) {
        	//Find the correct fragment
        	XPathFragment frag = associatedNodeValue.getXPathNode().getXPathFragment();
    		if(frag != null){
    		    frag = getOwningFragment(associatedNodeValue, frag);
        	    NodeValue unwrappedNodeValue = ((XMLChoiceCollectionMappingUnmarshalNodeValue)associatedNodeValue).getChoiceElementMarshalNodeValue();
        	    return marshalSingleValueWithNodeValue(frag, marshalRecord, object, value, session, namespaceResolver, marshalContext, unwrappedNodeValue);
    		}
        }
        return true;
    }

    private boolean marshalSingleValueWithNodeValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object value, AbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext, NodeValue unwrappedNodeValue) {        
        
    	Object fieldValue = value;
    	if(value instanceof XMLRoot){
    		fieldValue = ((XMLRoot)value).getObject();
    	}
    	if(unwrappedNodeValue != null){
    	    unwrappedNodeValue.marshalSingleValue(xPathFragment, marshalRecord, object, fieldValue, session, namespaceResolver, marshalContext);

    	}    	   
    	return true;
    }
    
    private Object getConvertedValue(Object originalValue,MarshalRecord marshalRecord, AbstractSession session){
	    Converter converter = xmlChoiceCollectionMapping.getConverter();
        if (null != converter) {
          if (converter instanceof XMLConverter) {
          	return ((XMLConverter)converter).convertObjectValueToDataValue(originalValue, session, marshalRecord.getMarshaller());
          } else {
        	  return converter.convertObjectValueToDataValue(originalValue, session);
         }
       }
       return originalValue;    	
    }

    private NodeValue getNodeValueForValue(Object value){
    	XMLField associatedField = null;
    	if(value instanceof XMLRoot) {
    		XMLRoot rootValue = (XMLRoot)value;
    		String localName = rootValue.getLocalName();
    		String namespaceUri = rootValue.getNamespaceURI();
    		Object fieldValue = rootValue.getObject();
    		associatedField = getFieldForName(localName, namespaceUri);
    		if(associatedField == null) {
    			associatedField = xmlChoiceCollectionMapping.getClassToFieldMappings().get(fieldValue.getClass());
    		}
    	} else {
    		associatedField = xmlChoiceCollectionMapping.getClassToFieldMappings().get(value.getClass());    		  
    	}
    	if(associatedField == null) {
    	    //check the field associations
    	    List<XMLField> sourceFields = xmlChoiceCollectionMapping.getClassToSourceFieldsMappings().get(value.getClass());
    	    if(sourceFields != null) {
    	        associatedField = sourceFields.get(0);
    	    }
    	}
    	if(associatedField != null){
    		return fieldToNodeValues.get(associatedField);
    	}
    	if (xmlChoiceCollectionMapping.isMixedContent() && value instanceof String){
    		//use this as a placeholder for the nodevalue for mixedcontent
    		return this;
    	}
    	return null;
    }
    
    
    private XPathFragment getOwningFragment(NodeValue nodeValue, XPathFragment frag){
    	while(frag != null) {
   	        if(nodeValue.isOwningNode(frag)) {
   	        	return frag;
   	        }
   	        frag = frag.getNextFragment();            
   		}   
    	return null;
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
