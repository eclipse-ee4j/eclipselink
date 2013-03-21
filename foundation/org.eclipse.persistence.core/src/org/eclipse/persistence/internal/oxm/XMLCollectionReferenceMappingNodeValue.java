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


import javax.xml.namespace.QName;

import org.xml.sax.Attributes;

import org.eclipse.persistence.internal.core.queries.CoreContainerPolicy;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.mappings.CollectionReferenceMapping;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: Class to handle (un)marshal operations for 
 * XMLCollectionReferenceMappings.</p>  
 * 
 * <p>An instance of this class is required for each XMLField set on the 
 * mapping, that is, for each source field in the source-target key field 
 * association list.</p>
 * 
 * <p>When unmarshalling, an instance of org.eclipse.persistence.internal.oxm.Reference is
 * created on a per mapping basis (keyed on source object instance) and sorted 
 * on the associated session's org.eclipse.persistence.internal.oxm.ReferenceResolver 
 * instance.  Each target primary key value is stored in the Reference instance 
 * for use during mapping resolution phase after unmarshalling completes.</p>
 * 
 * <p>When marshalling, the target object's primary key value that is mapped to 
 * this NodeValue's XMLField (in the XMLObjectReferenceMapping's source-target 
 * key field association list) is retrieved and written out.</p>
 * 
 * @see org.eclipse.persistence.internal.oxm.Reference
 * @see org.eclipse.persistence.internal.oxm.ReferenceResolver
 * @see org.eclipse.persistence.oxm.mappings.XMLObjectReferenceMapping
 */

public class XMLCollectionReferenceMappingNodeValue extends MappingNodeValue implements ContainerValue {
    private CollectionReferenceMapping xmlCollectionReferenceMapping;
    private Field xmlField;
    private static final String SPACE = " ";
    private int index = -1;

    /**
     * This constructor sets the XMLCollectionReferenceMapping and XMLField members to
     * the provided values.
     * 
     * @param xmlCollectionReferenceMapping
     */
    public XMLCollectionReferenceMappingNodeValue(CollectionReferenceMapping xmlCollectionReferenceMapping, Field xmlField) {
        super();
        this.xmlCollectionReferenceMapping = xmlCollectionReferenceMapping;
        this.xmlField = xmlField;
    }

    /**
     * Handle attribute operation.  Here we will create and populate an 
     * org.eclipse.persistence.internal.oxm.Reference instance to be used during 
     * the mapping resolution stage.  In particular, the primary key value
     * for this element will be added to the Reference object's map of
     * target primary key values - based on the target key field name.  Note 
     * that if a reference already exists for the xmlCollectionReferenceMapping's 
     * source object instance, we will simply add to the target pk value list.  
     * The Reference object is stored on the ReferenceResolver associated with 
     * the UnmarshalRecord's session.
     */
    public void attribute(UnmarshalRecord unmarshalRecord, String namespaceURI, String localName, String value) {
        if (value != null) {
            Object realValue = unmarshalRecord.getXMLReader().convertValueBasedOnSchemaType(xmlField, value, (XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager(), unmarshalRecord);
            Object container = unmarshalRecord.getContainerInstance(this);		
            // build a reference which will be resolved after unmarshalling is complete
            xmlCollectionReferenceMapping.buildReference(unmarshalRecord, xmlField, realValue, unmarshalRecord.getSession(), container);
        }
    }

    /**
     * Handle endElement operation.  Here we will create and populate an 
     * org.eclipse.persistence.internal.oxm.Reference instance to be used during 
     * the mapping resolution stage.  In particular, the primary key value
     * for this element will be added to the Reference object's map of
     * target primary key values - based on the target key field name.  Note 
     * that if a reference already exists for the xmlCollectionReferenceMapping's 
     * source object instance, we will simply add to the target pk value list.  
     * The Reference object is stored on the ReferenceResolver associated with 
     * the UnmarshalRecord's session.
     */
    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
        if (!xmlField.getLastXPathFragment().nameIsText()) {
            return;
        }

        Object value = unmarshalRecord.getCharacters().toString();
        unmarshalRecord.resetStringBuffer();

        XMLConversionManager xmlConversionManager = (XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager();
        if (unmarshalRecord.getTypeQName() != null) {
            Class typeClass = xmlField.getJavaClass(unmarshalRecord.getTypeQName());
            value = xmlConversionManager.convertObject(value, typeClass, unmarshalRecord.getTypeQName());
        } else {            
            value = unmarshalRecord.getXMLReader().convertValueBasedOnSchemaType(xmlField, value, xmlConversionManager, unmarshalRecord);
        }

        Object container = unmarshalRecord.getContainerInstance(this);
        
        // build a reference which will be resolved after unmarshalling is complete
        xmlCollectionReferenceMapping.buildReference(unmarshalRecord, xmlField, value, unmarshalRecord.getSession(), container);
    }

    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Object container) {
        this.endElement(xPathFragment, unmarshalRecord);
    }
    
    /**
     * Indicate if the next XPathFragment is an attribute or text() node.
     */
    public boolean isOwningNode(XPathFragment xPathFragment) {
        if(isMarshalNodeValue()) {
            if (xmlCollectionReferenceMapping.usesSingleNode()) {
                return xPathFragment.nameIsText() || xPathFragment.isAttribute();
            }
            XPathFragment nextFragment = xPathFragment.getNextFragment();
            return (nextFragment != null) && (nextFragment.nameIsText() || nextFragment.isAttribute());
        }
        return super.isOwningNode(xPathFragment);
    }

    @Override
    public boolean isWrapperAllowedAsCollectionName() {
        return true;
    }

    public boolean isContainerValue() {
        return true;
    }

    public Object getContainerInstance() {
        return getContainerPolicy().containerInstance();
    }

    public void setContainerInstance(Object object, Object containerInstance) {
        xmlCollectionReferenceMapping.setAttributeValueInObject(object, containerInstance);
    }

    public CoreContainerPolicy getContainerPolicy() {
        return xmlCollectionReferenceMapping.getContainerPolicy();
    }

    /**
     * Handle the marshal operation for this NodeValue.  Each of the target
     * object's primary key values that are mapped to the collection mapping's fields 
     * (in the XMLCollectionReferenceMapping's source-target key field association list)
     * are retrieved and written out. 
     */
    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver) {
        if(this.xmlCollectionReferenceMapping.isReadOnly()) {
            return false;
        }
        CoreContainerPolicy cp = xmlCollectionReferenceMapping.getContainerPolicy();
        Object collection = xmlCollectionReferenceMapping.getAttributeAccessor().getAttributeValueFromObject(object);
        if (collection == null) {
            return false;
        }

        Object iterator = cp.iteratorFor(collection);
        if (cp.hasNext(iterator)) {
            XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
            marshalRecord.closeStartGroupingElements(groupingFragment);
        } else {
        	return marshalRecord.emptyCollection(xPathFragment, namespaceResolver, false);
        }
        Object objectValue;

        if (xmlCollectionReferenceMapping.usesSingleNode()) {       	  
            StringBuilder stringValueStringBuilder = new StringBuilder();
            String newValue;
            QName schemaType;
            while (cp.hasNext(iterator)) {
                objectValue = cp.next(iterator, session);
                Object fieldValue = xmlCollectionReferenceMapping.buildFieldValue(objectValue, xmlField, session);
                if (fieldValue == null) {
                    if(null != objectValue) {
                    	Field fkField = (Field) xmlCollectionReferenceMapping.getSourceToTargetKeyFieldAssociations().get(xmlField);
                        fieldValue = marshalRecord.getMarshaller().getContext().getValueByXPath(objectValue, fkField.getXPath(), fkField.getNamespaceResolver(), Object.class);
                    }
                    if(null == fieldValue) {
                        break;
                    }
                }
                schemaType = xmlField.getSchemaTypeForValue(fieldValue, session);
                newValue = marshalRecord.getValueToWrite(schemaType, fieldValue, (XMLConversionManager) session.getDatasourcePlatform().getConversionManager());
                if (newValue != null) {
                    stringValueStringBuilder.append(newValue);
                    if (cp.hasNext(iterator)) {
                        stringValueStringBuilder.append(SPACE);
                    }
                }
            }
            marshalSingleValue(xPathFragment, marshalRecord, object, stringValueStringBuilder.toString(), session, namespaceResolver, ObjectMarshalContext.getInstance());
        } else {
            marshalRecord.startCollection(); 
            while (cp.hasNext(iterator)) {
                objectValue = cp.next(iterator, session);
                marshalSingleValue(xPathFragment, marshalRecord, object, objectValue, session, namespaceResolver, ObjectMarshalContext.getInstance());
            }
            marshalRecord.endCollection();
        }
        return true;
    }

    /**
     * @override
     * @param xPathFragment
     * @param unmarshalRecord
     * @param atts
     */
    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
        if (xmlField.getLastXPathFragment().isAttribute()) {
            if (!this.xmlCollectionReferenceMapping.usesSingleNode()) {
                String namespaceURI = xmlField.getLastXPathFragment().getNamespaceURI();
                String value;
                if (namespaceURI == null) {
                    value = atts.getValue(xmlField.getLastXPathFragment().getLocalName());
                } else {
                    value = atts.getValue(namespaceURI, xmlField.getLastXPathFragment().getLocalName());
                }
                
                xmlCollectionReferenceMapping.buildReference(unmarshalRecord, xmlField, value, unmarshalRecord.getSession(), unmarshalRecord.getContainerInstance(this));
                return true;
            }
        }
        return true;
    }

    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object value, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        if (xmlCollectionReferenceMapping.usesSingleNode()) {
            XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
            if (xPathFragment.isAttribute()) {                
                marshalRecord.attribute(xPathFragment, namespaceResolver, (String) value, null);
                marshalRecord.closeStartGroupingElements(groupingFragment);
            } else {
                marshalRecord.closeStartGroupingElements(groupingFragment);
                marshalRecord.characters(null, (String)value, null, false);
            }
        } else {
            QName schemaType;
            Object fieldValue = xmlCollectionReferenceMapping.buildFieldValue(value, xmlField, session);
            if (fieldValue == null) {
                return false;
            }
            schemaType = xmlField.getSchemaTypeForValue(fieldValue, session);
        
            marshalRecord.openStartElement(xPathFragment, namespaceResolver);
            XPathFragment nextFragment = xPathFragment.getNextFragment();
            if (nextFragment.isAttribute()) {
                marshalRecord.attribute(nextFragment, namespaceResolver, fieldValue, schemaType);                    
                marshalRecord.closeStartElement();
            } else {
                marshalRecord.predicateAttribute(xPathFragment, namespaceResolver);
                marshalRecord.closeStartElement();
                marshalRecord.characters(schemaType, fieldValue, null, false);
            }
            marshalRecord.endElement(xPathFragment, namespaceResolver);
        }
        return true;
    }

    public CollectionReferenceMapping getMapping() {
        return xmlCollectionReferenceMapping;
    }

    public boolean getReuseContainer() {
        return getMapping().getReuseContainer();
    }

    @Override
    public boolean isMarshalNodeValue() {
        return xmlCollectionReferenceMapping.getFields().size() == 1 || xmlCollectionReferenceMapping.usesSingleNode();
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