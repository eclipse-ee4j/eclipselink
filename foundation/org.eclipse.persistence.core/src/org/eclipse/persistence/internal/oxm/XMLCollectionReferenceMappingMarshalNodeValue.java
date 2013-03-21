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
*     bdoughan - October 21/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.internal.oxm;

import java.util.List;
import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.core.queries.CoreContainerPolicy;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.mappings.CollectionReferenceMapping;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;

public class XMLCollectionReferenceMappingMarshalNodeValue extends MappingNodeValue implements ContainerValue {

    private CollectionReferenceMapping xmlCollectionReferenceMapping;
    private XPathNode branchNode;
    private int index = -1;

    public XMLCollectionReferenceMappingMarshalNodeValue(CollectionReferenceMapping xmlCollectionReferenceMapping) {
        this.xmlCollectionReferenceMapping = xmlCollectionReferenceMapping;
        branchNode = new XPathNode();        
        NamespaceResolver namespaceResolver = ((Descriptor) xmlCollectionReferenceMapping.getDescriptor()).getNamespaceResolver();
        List fkFields = xmlCollectionReferenceMapping.getFields();
        for(int x=0, fkFieldsSize=fkFields.size(); x<fkFieldsSize; x++) {
        	Field fkField = (Field) fkFields.get(x);
            branchNode.addChild(fkField.getXPathFragment(), new XMLCollectionReferenceMappingFKMarshalNodeValue(xmlCollectionReferenceMapping, fkField), namespaceResolver);
        }
    }
    
    @Override
    public boolean isOwningNode(XPathFragment fragment) {
        return true;
    }

    public Object getContainerInstance() {
        return getContainerPolicy().containerInstance();
    }

    public CoreContainerPolicy getContainerPolicy() {
        return xmlCollectionReferenceMapping.getContainerPolicy();
    }

    @Override
    public CollectionReferenceMapping getMapping() {
        return xmlCollectionReferenceMapping;
    }

    public boolean getReuseContainer() {
        return xmlCollectionReferenceMapping.getReuseContainer();
    }

    public void setContainerInstance(Object object, Object containerInstance) {
        xmlCollectionReferenceMapping.setAttributeValueInObject(object, containerInstance);
    }

    @Override
    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver) {
        if (xmlCollectionReferenceMapping.isReadOnly()) {
            return false;
        }

        Object collection = xmlCollectionReferenceMapping.getAttributeAccessor().getAttributeValueFromObject(object);
        if (null == collection) {
            return false;
        }
        CoreContainerPolicy cp = getContainerPolicy();
        Object iterator = cp.iteratorFor(collection);
        if (cp.hasNext(iterator)) {
            XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
            marshalRecord.closeStartGroupingElements(groupingFragment);
        } else {            
        	return marshalRecord.emptyCollection(xPathFragment, namespaceResolver, false);
        }
        marshalRecord.startCollection(); 

        if(xPathFragment != XPathFragment.SELF_FRAGMENT) {
            marshalRecord.openStartElement(xPathFragment, namespaceResolver);
        }
        while (cp.hasNext(iterator)) {
            Object objectValue = cp.next(iterator, session);
            marshalSingleValue(xPathFragment, marshalRecord, object, objectValue, session, namespaceResolver, ObjectMarshalContext.getInstance());
        }
        if(xPathFragment != XPathFragment.SELF_FRAGMENT) {
            marshalRecord.endElement(xPathFragment, namespaceResolver);
        }
        marshalRecord.endCollection();

        return true;
    }

    @Override
    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object value, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        if (xmlCollectionReferenceMapping.usesSingleNode()) {
            XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
            if (xPathFragment.isAttribute()) {
                marshalRecord.attribute(xPathFragment, namespaceResolver, (String) value);
                marshalRecord.closeStartGroupingElements(groupingFragment);
            } else {
                marshalRecord.closeStartGroupingElements(groupingFragment);
                marshalRecord.characters((String) value);
            }
        } else {
            for (int x = 0, size = marshalContext.getNonAttributeChildrenSize(branchNode); x < size; x++) {
                XPathNode xPathNode = (XPathNode)marshalContext.getNonAttributeChild(x, branchNode);
                xPathNode.marshal(marshalRecord, value, session, namespaceResolver, marshalRecord.getMarshaller(), marshalContext.getMarshalContext(x), null);
            }
            
        }
        return true;
    }

    @Override
    public boolean isMarshalNodeValue() {
        return !xmlCollectionReferenceMapping.usesSingleNode();
    }

    @Override
    public boolean isUnmarshalNodeValue() {
        return false;
    }

    @Override
    public boolean isWrapperAllowedAsCollectionName() {
        return true;
    }

    private static class XMLCollectionReferenceMappingFKMarshalNodeValue extends MappingNodeValue {

        private CollectionReferenceMapping xmlCollectionReferenceMapping;
        private Field xmlField;

        public XMLCollectionReferenceMappingFKMarshalNodeValue(CollectionReferenceMapping xmlCollectionReferenceMapping, Field xmlField) {
            this.xmlCollectionReferenceMapping = xmlCollectionReferenceMapping;
            this.xmlField = xmlField;
        }
        
        @Override
        public boolean isUnmarshalNodeValue() {
            return false;
        }        

        @Override
        public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver) {
            XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
            marshalRecord.closeStartGroupingElements(groupingFragment);
            return marshalSingleValue(xPathFragment, marshalRecord, null, object, session, namespaceResolver, ObjectMarshalContext.getInstance());
        }

        @Override
        public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object value, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
            Object fieldValue = xmlCollectionReferenceMapping.buildFieldValue(value, xmlField, session);
            if (fieldValue == null) {
                if(null != value) {
                	Field f2 = (Field) xmlCollectionReferenceMapping.getSourceToTargetKeyFieldAssociations().get(xmlField);
                    fieldValue = marshalRecord.getMarshaller().getContext().getValueByXPath(value, f2.getXPath(), f2.getNamespaceResolver(), Object.class);
                }
                if(null == fieldValue) {
                    return false;
                }
            }
            QName schemaType = xmlField.getSchemaTypeForValue(fieldValue, session);
            if (xPathFragment.isAttribute()) {                    
                marshalRecord.attribute(xPathFragment, namespaceResolver, fieldValue, schemaType);
                marshalRecord.closeStartElement();
            } else {
                marshalRecord.closeStartElement();
                marshalRecord.characters(schemaType, fieldValue, null, false);
            }
            return true;

        }

        @Override
        public CollectionReferenceMapping getMapping() {
            return xmlCollectionReferenceMapping;
        }

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