/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.oxm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
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
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;

public class XMLChoiceCollectionMappingMarshalNodeValue extends MappingNodeValue implements ContainerValue {
    private ChoiceCollectionMapping xmlChoiceCollectionMapping;
    private Map<Field, NodeValue> fieldToNodeValues;
    private Map<Class, List<FieldNodeValue>> classToNodeValues;
    private NodeValue choiceElementNodeValue;
    private Field xmlField;
    private boolean isMixedNodeValue;
    private boolean isAny;
    private NodeValue anyNodeValue;
    private int index = -1;

    /**
     * This class is needed to hold field and nodeValue relationship.
     * If we have choice with different fields with the same (java class) type (e.g. via XmlAdapter),
     * there is need to know to which field we are holding the nodeValue.
     *
     * It is used in getNodeValueForValue method. If we knew only class relationship to nodeValue,
     * there is no way how to say that this nodeValue is related to the first or second field (or any other field with given java class).
     *
     * @see AdapterWithElementsTestCases
     *
     *
     * @author Martin Vojtek (martin.vojtek@oracle.com)
     *
     */
    private static class FieldNodeValue {
        private final Field field;
        private final NodeValue nodeValue;
        public FieldNodeValue(Field field, NodeValue nodeValue) {
            super();
            this.field = field;
            this.nodeValue = nodeValue;
        }
        public Field getField() {
            return field;
        }
        public NodeValue getNodeValue() {
            return nodeValue;
        }

    }

    public XMLChoiceCollectionMappingMarshalNodeValue(ChoiceCollectionMapping mapping, Field xmlField) {
        this.xmlChoiceCollectionMapping = mapping;
        this.xmlField = xmlField;
        isAny = mapping.isAny();
        initializeNodeValue();
    }

    @Override
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


    public void setFieldToNodeValues(Map<Field, NodeValue> fieldToNodeValues) {
        this.fieldToNodeValues = fieldToNodeValues;
        this.classToNodeValues = new HashMap<>();
        for(Field nextField:fieldToNodeValues.keySet()) {
            Class associatedClass = ((Map<Field, Class>)this.xmlChoiceCollectionMapping.getFieldToClassMappings()).get(nextField);

            if (classToNodeValues.containsKey(associatedClass)) {
                classToNodeValues.get(associatedClass).add(new FieldNodeValue(nextField, fieldToNodeValues.get(nextField)));
            } else {
                List<FieldNodeValue> newFieldToNodeValuesList = new ArrayList<>();
                newFieldToNodeValuesList.add(new FieldNodeValue(nextField, fieldToNodeValues.get(nextField)));
                this.classToNodeValues.put(associatedClass, newFieldToNodeValuesList);
            }


        }

        Collection classes = this.classToNodeValues.keySet();
        for(Class nextClass:((Map<Class, Mapping>)this.xmlChoiceCollectionMapping.getChoiceElementMappingsByClass()).keySet()) {
            //Create node values for any classes that aren't already processed
            if(!(classes.contains(nextClass))) {
        Field field = (Field) xmlChoiceCollectionMapping.getClassToFieldMappings().get(nextClass);
                NodeValue nodeValue = new XMLChoiceCollectionMappingUnmarshalNodeValue(xmlChoiceCollectionMapping, xmlField, (Mapping) xmlChoiceCollectionMapping.getChoiceElementMappingsByClass().get(nextClass));
                List<FieldNodeValue> newFieldToNodeValuesList = new ArrayList<>();
                newFieldToNodeValuesList.add(new FieldNodeValue(field, nodeValue));
                this.classToNodeValues.put(nextClass, newFieldToNodeValuesList);
                NodeValue nodeValueForField = fieldToNodeValues.get(field);
                nodeValue.setXPathNode(nodeValueForField.getXPathNode());
            }
        }
    }

    private void initializeNodeValue() {
        Mapping xmlMapping = (Mapping) xmlChoiceCollectionMapping.getChoiceElementMappings().get(xmlField);
        if(xmlMapping instanceof BinaryDataCollectionMapping) {
            choiceElementNodeValue = new XMLBinaryDataCollectionMappingNodeValue((BinaryDataCollectionMapping)xmlMapping);
        } else if(xmlMapping instanceof DirectCollectionMapping) {
            choiceElementNodeValue = new XMLCompositeDirectCollectionMappingNodeValue((DirectCollectionMapping)xmlMapping);
        } else if(xmlMapping instanceof CompositeCollectionMapping) {
            choiceElementNodeValue = new XMLCompositeCollectionMappingNodeValue((CompositeCollectionMapping)xmlMapping);
        } else {
            CollectionReferenceMapping refMapping = ((CollectionReferenceMapping)xmlMapping);
            if(refMapping.usesSingleNode() || refMapping.getFields().size() == 1) {
                choiceElementNodeValue = new XMLCollectionReferenceMappingNodeValue(refMapping, xmlField);
            } else {
                choiceElementNodeValue = new XMLCollectionReferenceMappingMarshalNodeValue((CollectionReferenceMapping)xmlMapping);
            }
        }
        if(isAny){
            anyNodeValue = new XMLChoiceCollectionMappingUnmarshalNodeValue(xmlChoiceCollectionMapping, null, xmlChoiceCollectionMapping.getAnyMapping());
        }

    }

    @Override
    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver) {
        if(xmlChoiceCollectionMapping.isReadOnly()) {
            return false;
        }

        Object value = xmlChoiceCollectionMapping.getAttributeValueFromObject(object);
        if(value == null) {
            AbstractNullPolicy wrapperNP = xmlChoiceCollectionMapping.getWrapperNullPolicy();
            if (wrapperNP != null && wrapperNP.getMarshalNullRepresentation() == XMLNullRepresentationType.XSI_NIL) {
                marshalRecord.nilSimple(namespaceResolver);
                return true;
            } else {
                return false;
            }
        }
        CoreContainerPolicy cp = getContainerPolicy();
        Object iterator = cp.iteratorFor(value);
        if (null != iterator && cp.hasNext(iterator)) {
            if(xPathFragment != null) {
                XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
                marshalRecord.closeStartGroupingElements(groupingFragment);
            }
        } else {
            return marshalRecord.emptyCollection(xPathFragment, namespaceResolver, xmlChoiceCollectionMapping.getWrapperNullPolicy() != null);
        }

        if(marshalRecord.getMarshaller().isApplicationJSON()){
            List<NodeValue> nodeValues = new ArrayList();
            List<List> values = new ArrayList<>();

            NodeValue mixedNodeValue = null;
            List mixedValues = null;

            //sort the elements. Results will be a list of nodevalues and a corresponding list of
            //collections associated with those nodevalues
            while(cp.hasNext(iterator)) {
                Object nextValue = xmlChoiceCollectionMapping.convertObjectValueToDataValue(cp.next(iterator, session), session, marshalRecord.getMarshaller());
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
                if(frag != null || associatedNodeValue.isAnyMappingNodeValue()){
                    int valueSize = listValue.size();
                    if(valueSize > 1 || !marshalRecord.getMarshaller().isReduceAnyArrays()) {
                        marshalRecord.startCollection();
                    }

                    for(int j=0;j<valueSize; j++){
                        marshalSingleValueWithNodeValue(frag, marshalRecord, object, listValue.get(j), session, namespaceResolver, ObjectMarshalContext.getInstance(), associatedNodeValue);
                    }
                    if(valueSize > 1 || !marshalRecord.getMarshaller().isReduceAnyArrays()) {
                        marshalRecord.endCollection();
                    }
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

    @Override
    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object value, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        value = xmlChoiceCollectionMapping.convertObjectValueToDataValue(value, session, marshalRecord.getMarshaller());
        if(value !=null && value.getClass() == CoreClassConstants.STRING && this.xmlChoiceCollectionMapping.isMixedContent()) {
            marshalMixedContent(marshalRecord, (String)value);
            return true;
        }
        NodeValue associatedNodeValue = getNodeValueForValue(value);
        if(associatedNodeValue != null) {
            if(associatedNodeValue.isAnyMappingNodeValue()){
                //NodeValue unwrappedNodeValue = ((XMLChoiceCollectionMappingUnmarshalNodeValue)associatedNodeValue).getChoiceElementMarshalNodeValue();
                return marshalSingleValueWithNodeValue(null, marshalRecord, object, value, session, namespaceResolver, marshalContext, associatedNodeValue);
            }
            else{
            //Find the correct fragment
            XPathFragment frag = associatedNodeValue.getXPathNode().getXPathFragment();
            if(frag != null){
                frag = getOwningFragment(associatedNodeValue, frag);
                NodeValue unwrappedNodeValue = ((XMLChoiceCollectionMappingUnmarshalNodeValue)associatedNodeValue).getChoiceElementMarshalNodeValue();
                return marshalSingleValueWithNodeValue(frag, marshalRecord, object, value, session, namespaceResolver, marshalContext, unwrappedNodeValue);
            }
            }
        }
        return true;
    }

    private boolean marshalSingleValueWithNodeValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object value, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext, NodeValue unwrappedNodeValue) {

        if(unwrappedNodeValue != null){
            unwrappedNodeValue.marshalSingleValue(xPathFragment, marshalRecord, object, value, session, namespaceResolver, marshalContext);
        }
        return true;
    }

    private NodeValue getNodeValueForValue(Object value){
        if(value == null){
            Iterator<NodeValue> nodeValues= fieldToNodeValues.values().iterator();
            while(nodeValues.hasNext()) {

                XMLChoiceCollectionMappingUnmarshalNodeValue unmarshalNodeValue = (XMLChoiceCollectionMappingUnmarshalNodeValue)nodeValues.next();
                NodeValue nextNodeValue = unmarshalNodeValue.getChoiceElementMarshalNodeValue();

                if(nextNodeValue instanceof MappingNodeValue){
                    Mapping nextMapping = ((MappingNodeValue)nextNodeValue).getMapping();
                    if(nextMapping.isAbstractCompositeCollectionMapping()){
                        if(((CompositeCollectionMapping)nextMapping).getNullPolicy().isNullRepresentedByXsiNil()){
                            return unmarshalNodeValue;
                        }
                    }else if(nextMapping.isAbstractCompositeDirectCollectionMapping()){
                        if(((DirectCollectionMapping)nextMapping).getNullPolicy().isNullRepresentedByXsiNil()){
                            return unmarshalNodeValue;
                        }
                    }else if(nextMapping instanceof BinaryDataCollectionMapping){
                        if(((BinaryDataCollectionMapping)nextMapping).getNullPolicy().isNullRepresentedByXsiNil()){
                            return unmarshalNodeValue;
                        }
                    }
                }

            }
            return null;
        }

        Field associatedField = null;
        NodeValue nodeValue = null;
        if(value instanceof Root) {
            Root rootValue = (Root)value;
            String localName = rootValue.getLocalName();
            String namespaceUri = rootValue.getNamespaceURI();
            Object fieldValue = rootValue.getObject();
            associatedField = getFieldForName(localName, namespaceUri);
            if(associatedField == null) {
                if(xmlChoiceCollectionMapping.isAny()) {
                    return this.anyNodeValue;
                }
                Class theClass = fieldValue.getClass();
                while(associatedField == null) {
                    associatedField = (Field) xmlChoiceCollectionMapping.getClassToFieldMappings().get(theClass);
                    if(theClass.getSuperclass() != null) {
                        theClass = theClass.getSuperclass();
                    } else {
                        break;
                    }
                }
            }
            if(associatedField != null) {
                nodeValue = this.fieldToNodeValues.get(associatedField);
            }
        } else {
            Class theClass = value.getClass();
            while(associatedField == null) {
                associatedField = (Field) xmlChoiceCollectionMapping.getClassToFieldMappings().get(theClass);
                List<FieldNodeValue> fieldNodes = classToNodeValues.get(theClass);

                nodeValue = null;
                if (null != fieldNodes) {

                    //match also field
                    if (null != associatedField && fieldNodes.size() > 1) {
                        for (FieldNodeValue fieldNode : fieldNodes) {
                            if (fieldNode.getField().equals(associatedField)) {
                                nodeValue = fieldNode.getNodeValue();
                            }
                        }
                    }

                    if (null == nodeValue && fieldNodes.size() > 0) {
                        nodeValue = fieldNodes.get(0).getNodeValue();
                    }
                }

                if(theClass.getSuperclass() != null) {
                    theClass = theClass.getSuperclass();
                } else {
                    break;
                }
            }
        }
        if(associatedField == null) {
            //check the field associations
            List<Field> sourceFields = null;
            Class theClass = value.getClass();
            while(theClass != null) {
                sourceFields = (List<Field>) xmlChoiceCollectionMapping.getClassToSourceFieldsMappings().get(theClass);
                if(sourceFields != null) {
                    break;
                }
                theClass = theClass.getSuperclass();
            }
            if(sourceFields != null) {
                associatedField = sourceFields.get(0);
                nodeValue = fieldToNodeValues.get(associatedField);
            }
        }
        if(nodeValue != null){
            return nodeValue;
        }
        if(associatedField != null) {
            return fieldToNodeValues.get(associatedField);
        }
        if (xmlChoiceCollectionMapping.isMixedContent() && value instanceof String){
            //use this as a placeholder for the nodevalue for mixedcontent
            return this;
        }
        if (xmlChoiceCollectionMapping.isAny()){
            return anyNodeValue;
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

    private Field getFieldForName(String localName, String namespaceUri) {
        Iterator<Field> fields = fieldToNodeValues.keySet().iterator();
        while(fields.hasNext()) {
            Field nextField = fields.next();
            if(nextField != null){
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
        }
        return null;
    }

    public Collection<NodeValue> getAllNodeValues() {
        return this.fieldToNodeValues.values();
    }

    @Override
    public boolean isMarshalNodeValue() {
        return true;
    }

    @Override
    public boolean isUnmarshalNodeValue() {
        return false;
    }

    @Override
    public boolean isWrapperAllowedAsCollectionName() {
        return false;
    }

    @Override
    public Object getContainerInstance() {
        return getContainerPolicy().containerInstance();
    }

    @Override
    public void setContainerInstance(Object object, Object containerInstance) {
        xmlChoiceCollectionMapping.setAttributeValueInObject(object, containerInstance);
    }

    @Override
    public CoreContainerPolicy getContainerPolicy() {
        return xmlChoiceCollectionMapping.getContainerPolicy();
    }

    @Override
    public boolean isContainerValue() {
        return true;
    }

    @Override
    public ChoiceCollectionMapping getMapping() {
        return xmlChoiceCollectionMapping;
    }

    @Override
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
    @Override
    public boolean isMixedContentNodeValue() {
        return this.isMixedNodeValue;
    }

    /**
     *  INTERNAL:
     *  Used to track the index of the corresponding containerInstance in the containerInstances Object[] on UnmarshalRecord
     */
    @Override
    public void setIndex(int index){
        this.index = index;
    }

    /**
     * INTERNAL:
     * Set to track the index of the corresponding containerInstance in the containerInstances Object[] on UnmarshalRecord
     * Set during TreeObjectBuilder initialization
     */
    @Override
    public int getIndex(){
        return index;
    }

    /**
     * INTERNAL
     * Return true if an empty container should be set on the object if there
     * is no presence of the collection in the XML document.
     * @since EclipseLink 2.3.3
     */
    @Override
    public boolean isDefaultEmptyContainer() {
        return getMapping().isDefaultEmptyContainer();
    }

    @Override
    public void setXPathNode(XPathNode xPathNode) {
        super.setXPathNode(xPathNode);
        if(this.anyNodeValue != null) {
            this.anyNodeValue.setXPathNode(xPathNode);
        }
    }

}
