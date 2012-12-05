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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.eclipse.persistence.core.descriptors.CoreInheritancePolicy;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.mappings.AnyAttributeMapping;
import org.eclipse.persistence.internal.oxm.mappings.AnyCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.AnyObjectMapping;
import org.eclipse.persistence.internal.oxm.mappings.BinaryDataCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.BinaryDataMapping;
import org.eclipse.persistence.internal.oxm.mappings.ChoiceCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.ChoiceObjectMapping;
import org.eclipse.persistence.internal.oxm.mappings.CollectionReferenceMapping;
import org.eclipse.persistence.internal.oxm.mappings.CompositeCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.CompositeObjectMapping;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.DirectCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.DirectMapping;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.mappings.FragmentCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.FragmentMapping;
import org.eclipse.persistence.internal.oxm.mappings.InverseReferenceMapping;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.mappings.ObjectReferenceMapping;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.oxm.record.SequencedMarshalContext;
import org.eclipse.persistence.internal.oxm.record.XMLRecord;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DatabaseMapping.WriteType;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLMapping;
import org.eclipse.persistence.oxm.record.NodeRecord;
import org.eclipse.persistence.oxm.sequenced.SequencedObject;
import org.w3c.dom.Node;

/**
 * INTERNAL:
 * <p><b>Purpose</b>:  Perform the unmarshal and marshal operations based on the
 * object-to-XML mapping metadata.</p>
 * <p><b>Responsibilities</b>:<ul>
 * <li>Convert mapping metadata to a tree of XPathNodes.  This tree is then
 * traversed during unmarshal and marshal operations.</li>
 * <li>Create records appropriate to this implementation of ObjectBuilder.</li>
 * </ul>
 */
public class TreeObjectBuilder extends XMLObjectBuilder {
    private XPathNode rootXPathNode;
    private List transformationMappings;
    private List containerValues;
    private List nullCapableValues;
    private List defaultEmptyContainerValues; //a list of container values that have isDefaultEmptyContainer() set to true
    private volatile boolean initialized = false;
    private int counter = 0;

    // Used for CycleRecoverable support
    Class cycleRecoverableClass = null;
    Class cycleRecoverableContextClass = null;
    public static final String CYCLE_RECOVERABLE = "com.sun.xml.bind.CycleRecoverable";
    public static final String CYCLE_RECOVERABLE_CONTEXT = "com.sun.xml.bind.CycleRecoverable$Context";    
    public static final String ON_CYCLE_DETECTED = "onCycleDetected";

    public TreeObjectBuilder(ClassDescriptor descriptor) {
        super(descriptor);
    }

    @Override
    protected void initialize(ClassDescriptor descriptor) {
        rootXPathNode = new XPathNode();

        int descriptorMappingsSize = descriptor.getMappings().size();
        this.mappingsByField = new HashMap(descriptorMappingsSize);
        this.fieldsMap = new HashMap(descriptorMappingsSize);
        this.cloningMappings = new ArrayList(descriptorMappingsSize);
    }

    public XPathNode getRootXPathNode() {
        lazyInitialize();
        return this.rootXPathNode;
    }

    @Override
    public List<DatabaseMapping> getPrimaryKeyMappings() {
        if(null == primaryKeyMappings) {
            primaryKeyMappings = new ArrayList<DatabaseMapping>(1);
        }
        return primaryKeyMappings;
    }

    public void addTransformationMapping(AbstractTransformationMapping transformationMapping) {
        if (null == this.transformationMappings) {
            this.transformationMappings = new ArrayList();
        }
        transformationMappings.add(transformationMapping);
    }

    public List getTransformationMappings() {
        return this.transformationMappings;
    }

    public List getContainerValues() {
        return this.containerValues;
    }

    public void addDefaultEmptyContainerValue(ContainerValue containerValue){
        if (null == this.defaultEmptyContainerValues) {        	
            this.defaultEmptyContainerValues = new ArrayList();            
        }
        this.defaultEmptyContainerValues.add(containerValue);
    }
   
    public void addContainerValue(ContainerValue containerValue) {
        if (null == this.containerValues) {        	
            this.containerValues = new ArrayList();     
        }
        containerValue.setIndex(counter++);
        this.containerValues.add(containerValue);
        
        if(containerValue.isDefaultEmptyContainer()){
        	addDefaultEmptyContainerValue(containerValue);
        }
    }
    public List getNullCapableValues() {
        return this.nullCapableValues;
    }
    public List getDefaultEmptyContainerValues() {
        return this.defaultEmptyContainerValues;
    }
    public void addNullCapableValue(NullCapableValue nullCapableValue) {
        if (null == this.nullCapableValues) {
            this.nullCapableValues = new ArrayList();
        }
        this.nullCapableValues.add(nullCapableValue);
    }

    public void initialize(org.eclipse.persistence.internal.sessions.AbstractSession session) {
        super.initialize(session);
        Descriptor xmlDescriptor = (Descriptor)getDescriptor();

        // INHERITANCE
        if (xmlDescriptor.hasInheritance()) {
            CoreInheritancePolicy inheritancePolicy = xmlDescriptor.getInheritancePolicy();
            
            if (!inheritancePolicy.hasClassExtractor()) {
                Field classIndicatorField = new XMLField(inheritancePolicy.getClassIndicatorFieldName());
                classIndicatorField.setNamespaceResolver(xmlDescriptor.getNamespaceResolver());
            }
        }

        if(!xmlDescriptor.isLazilyInitialized()) {
            lazyInitialize();
        }
    }

    private void lazyInitialize() {
        if(initialized) {
            return;
        }
        synchronized(this) {
            if(initialized) {
                return;
            }
            Descriptor xmlDescriptor = (Descriptor)getDescriptor();
    
            // MAPPINGS
            Iterator mappingIterator = xmlDescriptor.getMappings().iterator();
            Iterator fieldTransformerIterator;
            Mapping xmlMapping;
    
            // Transformation Mapping
            AbstractTransformationMapping transformationMapping;
            FieldTransformerNodeValue fieldTransformerNodeValue;
            Object[] nextFieldToTransformer;
    
            // Simple Type Translator
            TypeNodeValue typeNodeValue;
    
            NodeValue mappingNodeValue = null;
            Field xmlField;
            while (mappingIterator.hasNext()) {
                xmlMapping = (Mapping)mappingIterator.next();
                
                if (xmlMapping instanceof InverseReferenceMapping) {
                    continue;
                }
                
                xmlField = (Field)xmlMapping.getField();
                if (xmlMapping.isTransformationMapping()) {
                    transformationMapping = (AbstractTransformationMapping)xmlMapping;
                    addTransformationMapping(transformationMapping);
                    fieldTransformerIterator = transformationMapping.getFieldToTransformers().iterator();
                    while (fieldTransformerIterator.hasNext()) {
                        fieldTransformerNodeValue = new FieldTransformerNodeValue();
                        nextFieldToTransformer = (Object[])fieldTransformerIterator.next();
                        xmlField = (Field)nextFieldToTransformer[0];
                        fieldTransformerNodeValue.setXMLField(xmlField);
                        fieldTransformerNodeValue.setFieldTransformer((FieldTransformer)nextFieldToTransformer[1]);
                        addChild(xmlField.getXPathFragment(), fieldTransformerNodeValue, xmlDescriptor.getNamespaceResolver());
                    }
                } else {
                    if (xmlMapping.isAbstractDirectMapping()) {
                        mappingNodeValue = new XMLDirectMappingNodeValue((DirectMapping)xmlMapping);
                    } else if (xmlMapping.isAbstractCompositeObjectMapping()) {
                        mappingNodeValue = new XMLCompositeObjectMappingNodeValue((CompositeObjectMapping)xmlMapping);
                    } else if (xmlMapping.isAbstractCompositeDirectCollectionMapping()) {
                        DirectCollectionMapping collectionMapping = (DirectCollectionMapping) xmlMapping;
                        mappingNodeValue = new XMLCompositeDirectCollectionMappingNodeValue(collectionMapping);
                        if (collectionMapping.getWrapperNullPolicy() != null) {
                            addChild(xmlField.getXPathFragment(), new CollectionGroupingElementNodeValue((ContainerValue) mappingNodeValue), xmlDescriptor.getNamespaceResolver());
                        }
                    } else if (xmlMapping.isAbstractCompositeCollectionMapping()) {
                        CompositeCollectionMapping collectionMapping = (CompositeCollectionMapping) xmlMapping;
                        mappingNodeValue = new XMLCompositeCollectionMappingNodeValue(collectionMapping);
                        if (collectionMapping.getWrapperNullPolicy() != null) {
                            addChild(xmlField.getXPathFragment(), new CollectionGroupingElementNodeValue((ContainerValue) mappingNodeValue), xmlDescriptor.getNamespaceResolver());
                        }
                    } else if (xmlMapping instanceof AnyObjectMapping) {
                        mappingNodeValue = new XMLAnyObjectMappingNodeValue((AnyObjectMapping)xmlMapping);
                    } else if (xmlMapping instanceof AnyCollectionMapping) {
                        mappingNodeValue = new XMLAnyCollectionMappingNodeValue((AnyCollectionMapping)xmlMapping);
                    } else if (xmlMapping instanceof AnyAttributeMapping) {
                        mappingNodeValue = new XMLAnyAttributeMappingNodeValue((AnyAttributeMapping)xmlMapping);
                    } else if (xmlMapping instanceof BinaryDataMapping) {
                        mappingNodeValue = new XMLBinaryDataMappingNodeValue((BinaryDataMapping)xmlMapping);
                    } else if (xmlMapping instanceof BinaryDataCollectionMapping) {
                        mappingNodeValue = new XMLBinaryDataCollectionMappingNodeValue((BinaryDataCollectionMapping)xmlMapping);
                    } else if (xmlMapping instanceof FragmentMapping) {
                        mappingNodeValue = new XMLFragmentMappingNodeValue((FragmentMapping)xmlMapping);
                    } else if (xmlMapping instanceof FragmentCollectionMapping) {
                        mappingNodeValue = new XMLFragmentCollectionMappingNodeValue((FragmentCollectionMapping)xmlMapping);
                    } else if (xmlMapping instanceof CollectionReferenceMapping) {
                        CollectionReferenceMapping xmlColMapping = (CollectionReferenceMapping)xmlMapping;
                        
                        List fields = xmlColMapping.getFields();
                        Field xmlColMappingField = (Field) xmlColMapping.getField();
                        XPathNode branchNode;
                        if(null == xmlColMappingField) {
                            if(fields.size() > 1 && !xmlColMapping.usesSingleNode()) {                                
                                addChild(XPathFragment.SELF_FRAGMENT, new XMLCollectionReferenceMappingMarshalNodeValue(xmlColMapping), xmlDescriptor.getNamespaceResolver());
                            }
                            branchNode = rootXPathNode;
                        } else {
                            branchNode = addChild(((Field) xmlColMapping.getField()).getXPathFragment(), new XMLCollectionReferenceMappingMarshalNodeValue(xmlColMapping), xmlDescriptor.getNamespaceResolver());
                        }                        
                    
                        int containerIndex = -1;
                        for (int i = 0, size = fields.size(); i < size; i++) {
                        	Field xmlFld = (Field)fields.get(i);
                            mappingNodeValue = new XMLCollectionReferenceMappingNodeValue(xmlColMapping, xmlFld);
                            if(i == 0){
                                addContainerValue((ContainerValue)mappingNodeValue);
                                containerIndex = ((ContainerValue)mappingNodeValue).getIndex();
                            }else{
                                ((ContainerValue)mappingNodeValue).setIndex(containerIndex);
                            }
                            if (mappingNodeValue.isNullCapableValue()) {
                                addNullCapableValue((NullCapableValue)mappingNodeValue);
                            }
                            branchNode.addChild(xmlFld.getXPathFragment(), mappingNodeValue, xmlDescriptor.getNamespaceResolver());
                        }
                       
                      
                        continue;
                    } else if (xmlMapping instanceof ObjectReferenceMapping) {
                        ObjectReferenceMapping xmlORMapping = (ObjectReferenceMapping)xmlMapping;
                        Iterator fieldIt = xmlORMapping.getFields().iterator();
                        while (fieldIt.hasNext()) {
                        	Field xmlFld = (Field)fieldIt.next();
                            mappingNodeValue = new XMLObjectReferenceMappingNodeValue(xmlORMapping, xmlFld);
                            if (mappingNodeValue.isContainerValue()) {
                                addContainerValue((ContainerValue)mappingNodeValue);
                            }
                            if (mappingNodeValue.isNullCapableValue()) {
                                addNullCapableValue((NullCapableValue)mappingNodeValue);
                            }
                            addChild(xmlFld.getXPathFragment(), mappingNodeValue, xmlDescriptor.getNamespaceResolver());
                        }
                        continue;
                    } else if (xmlMapping instanceof ChoiceObjectMapping) {
                        ChoiceObjectMapping xmlChoiceMapping = (ChoiceObjectMapping)xmlMapping;
                        Iterator fields = xmlChoiceMapping.getChoiceElementMappings().keySet().iterator();
                        Field firstField = (Field)fields.next();
                        XMLChoiceObjectMappingNodeValue firstNodeValue = new XMLChoiceObjectMappingNodeValue(xmlChoiceMapping, firstField);
                        firstNodeValue.setNullCapableNodeValue(firstNodeValue);
                        this.addNullCapableValue(firstNodeValue);
                        addChild(firstField.getXPathFragment(), firstNodeValue, xmlDescriptor.getNamespaceResolver());
                        while(fields.hasNext()) {
                        	Field next = (Field)fields.next();
                            XMLChoiceObjectMappingNodeValue nodeValue = new XMLChoiceObjectMappingNodeValue(xmlChoiceMapping, next);
                            nodeValue.setNullCapableNodeValue(firstNodeValue);
                            addChild(next.getXPathFragment(), nodeValue, xmlDescriptor.getNamespaceResolver());
                        }
                        continue;
                    } else if(xmlMapping instanceof ChoiceCollectionMapping) {
                        ChoiceCollectionMapping xmlChoiceMapping = (ChoiceCollectionMapping)xmlMapping;

                        Iterator<Entry<Field, XMLMapping>> fields = xmlChoiceMapping.getChoiceElementMappings().entrySet().iterator();
                        Entry<Field, XMLMapping> firstEntry = fields.next();
                        Field firstField = firstEntry.getKey();

                        XMLChoiceCollectionMappingUnmarshalNodeValue unmarshalValue = new XMLChoiceCollectionMappingUnmarshalNodeValue(xmlChoiceMapping, firstField);
                        XMLChoiceCollectionMappingMarshalNodeValue marshalValue = new XMLChoiceCollectionMappingMarshalNodeValue(xmlChoiceMapping, firstField);

                        HashMap<Field, NodeValue> fieldToNodeValues = new HashMap<Field, NodeValue>();
                        unmarshalValue.setContainerNodeValue(unmarshalValue);
                        unmarshalValue.setFieldToNodeValues(fieldToNodeValues);
                        if(xmlChoiceMapping.isMixedContent() && (xmlChoiceMapping.getMixedContentMapping() == firstEntry.getValue())) {
                            unmarshalValue.setIsMixedNodeValue(true);
                            marshalValue.setIsMixedNodeValue(true);
                        }
                        this.addContainerValue(unmarshalValue);
                        ((ContainerValue)unmarshalValue.getChoiceElementNodeValue()).setIndex(unmarshalValue.getIndex());
                        fieldToNodeValues.put(firstField, unmarshalValue);
                        addChild(firstField.getXPathFragment(), unmarshalValue, xmlDescriptor.getNamespaceResolver());
                        addChild(firstField.getXPathFragment(), marshalValue, xmlDescriptor.getNamespaceResolver());
                        while(fields.hasNext()) {
                            Entry<Field, XMLMapping> nextEntry = fields.next();
                            Field nextField = nextEntry.getKey();
                            XMLChoiceCollectionMappingUnmarshalNodeValue nodeValue = new XMLChoiceCollectionMappingUnmarshalNodeValue(xmlChoiceMapping, nextField);
                            nodeValue.setContainerNodeValue(unmarshalValue);
                            nodeValue.setIndex(unmarshalValue.getIndex());
                            ((ContainerValue)nodeValue.getChoiceElementNodeValue()).setIndex(unmarshalValue.getIndex());
                            addChild(nextField.getXPathFragment(), nodeValue, xmlDescriptor.getNamespaceResolver());
                            fieldToNodeValues.put(nextField, nodeValue);
                            if(xmlChoiceMapping.isMixedContent() && (xmlChoiceMapping.getMixedContentMapping() == nextEntry.getValue())) {
                                nodeValue.setIsMixedNodeValue(true);
                            }
                        }
                        marshalValue.setFieldToNodeValues(fieldToNodeValues);
                        continue;
                    }
                    if (mappingNodeValue.isContainerValue()) {
                        addContainerValue((ContainerValue)mappingNodeValue);
                    }
                    if (mappingNodeValue.isNullCapableValue()) {
                        addNullCapableValue((NullCapableValue)mappingNodeValue);
                    }
                    if (xmlField != null) {
                        addChild(xmlField.getXPathFragment(), mappingNodeValue, xmlDescriptor.getNamespaceResolver());
                    } else {
                        addChild(null, mappingNodeValue, xmlDescriptor.getNamespaceResolver());
                    }
                    if (xmlMapping.isAbstractDirectMapping() && xmlField.isTypedTextField()) {
                        XPathFragment nextFragment = xmlField.getXPathFragment();
                        StringBuilder typeXPathStringBuilder = new StringBuilder();
                        while (nextFragment.getNextFragment() != null) {
                            typeXPathStringBuilder.append(nextFragment.getXPath());
                            nextFragment = nextFragment.getNextFragment();
                        }
                        Field typeField = new XMLField();
                        if(typeXPathStringBuilder.length() > 0) {
                            typeXPathStringBuilder.append('/');
                        }
                        typeField.setXPath(typeXPathStringBuilder.toString() + XMLConstants.ATTRIBUTE + xmlDescriptor.getNonNullNamespaceResolver().resolveNamespaceURI(XMLConstants.SCHEMA_INSTANCE_URL) + XMLConstants.COLON + XMLConstants.SCHEMA_TYPE_ATTRIBUTE);
                        typeNodeValue = new TypeNodeValue();
                        typeNodeValue.setDirectMapping((AbstractDirectMapping)xmlMapping);
                        addChild(typeField.getXPathFragment(), typeNodeValue, xmlDescriptor.getNamespaceResolver());
                    }
                }
            }
            initialized = true;
        }
    }

    public XPathNode addChild(XPathFragment xPathFragment, NodeValue nodeValue, NamespaceResolver namespaceResolver) {
        return rootXPathNode.addChild(xPathFragment, nodeValue, namespaceResolver);
    }

    @Override
    public AbstractRecord buildRow(AbstractRecord record, Object object, org.eclipse.persistence.internal.sessions.AbstractSession session, WriteType writeType) {
        return (AbstractRecord) buildRow((XMLRecord) record, object, session, null, null, writeType);
    }

    public org.eclipse.persistence.internal.oxm.record.XMLRecord buildRow(org.eclipse.persistence.internal.oxm.record.XMLRecord record, Object object, CoreAbstractSession session, XMLMarshaller marshaller, XPathFragment rootFragment, WriteType writeType) {
        lazyInitialize();
        XPathNode textNode = rootXPathNode.getTextNode();
        List<XPathNode> nonAttributeChildren = rootXPathNode.getNonAttributeChildren();
        if (null == textNode && null == nonAttributeChildren) {
            return record;
        }

        Descriptor xmlDescriptor = (Descriptor) descriptor;
        XPathNode node = rootXPathNode;
        MarshalRecord marshalRecord = (MarshalRecord) record;
        QName schemaType = null;

        if (marshalRecord.getCycleDetectionStack().contains(object, marshaller.isEqualUsingIdenity())) {
            if (cycleRecoverableClass == null) {
                initCycleRecoverableClasses();
            }
            if (cycleRecoverableClass.isAssignableFrom(object.getClass())) {
                try {
                    Object jaxbMarshaller = marshaller.getProperty(XMLConstants.JAXB_MARSHALLER);
                    // Create a proxy instance of CycleRecoverable$Context, a parameter to
                    // the onCycleDetected method
                    Object contextProxy = CycleRecoverableContextProxy.getProxy(cycleRecoverableContextClass, jaxbMarshaller);
                    // Invoke onCycleDetected method, passing in proxy, and reset
                    // 'object' to the returned value
                    Method onCycleDetectedMethod = object.getClass().getMethod(ON_CYCLE_DETECTED, new Class[] { cycleRecoverableContextClass });
                    object = PrivilegedAccessHelper.invokeMethod(onCycleDetectedMethod, object, new Object[] { contextProxy });
                } catch (Exception e) {
                    throw XMLMarshalException.marshalException(e);
                }

                // Returned object might have a different descriptor
                xmlDescriptor = (Descriptor) session.getDescriptor(object.getClass());
                if (xmlDescriptor != null) {
                    node = ((TreeObjectBuilder) xmlDescriptor.getObjectBuilder()).getRootXPathNode();
                } else {
                    node = null;
                }

                // Push new object
                marshalRecord.getCycleDetectionStack().push(object);

                // Write xsi:type if onCycleDetected returned an object of a type different than the one mapped
                if (xmlDescriptor != descriptor) {
                    if (xmlDescriptor == null) {
                        schemaType = (QName) XMLConversionManager.getDefaultJavaTypes().get(object.getClass());
                    } else {
                        schemaType = xmlDescriptor.getSchemaReference().getSchemaContextAsQName();
                    }
                    writeXsiTypeAttribute(xmlDescriptor, marshalRecord, schemaType.getNamespaceURI(), schemaType.getLocalPart(), schemaType.getPrefix(), false);
                }
            } else {
                // Push the duplicate object anyway, so that we can get the complete cycle string
                marshalRecord.getCycleDetectionStack().push(object);
                throw XMLMarshalException.objectCycleDetected(marshalRecord.getCycleDetectionStack().getCycleString());
            }
        } else {
            marshalRecord.getCycleDetectionStack().push(object);
        }

        NamespaceResolver namespaceResolver = null;
        if (xmlDescriptor != null) {
            namespaceResolver = xmlDescriptor.getNamespaceResolver();
        }
        MarshalContext marshalContext = null;
        if (xmlDescriptor != null && xmlDescriptor.isSequencedObject()) {
            SequencedObject sequencedObject = (SequencedObject) object;
            marshalContext = new SequencedMarshalContext(sequencedObject.getSettings());
        } else {
            marshalContext = ObjectMarshalContext.getInstance();
        }
        if (null == nonAttributeChildren) {
            textNode.marshal((MarshalRecord) record, object, session, namespaceResolver, marshaller, marshalContext, rootFragment);
        } else {
            if (node == null) {
                // No descriptor for this object, so manually create a MappingNodeValue and marshal it
                XPathNode n = new XPathNode();
                CompositeObjectMapping m = new XMLCompositeObjectMapping();
                m.setXPath(".");
                XMLCompositeObjectMappingNodeValue nv = new XMLCompositeObjectMappingNodeValue(m);
                n.setMarshalNodeValue(nv);
                nv.marshalSingleValue(new XPathFragment("."), marshalRecord, null, object, session, namespaceResolver, marshalContext);
            } else {
                for (int x = 0, size = marshalContext.getNonAttributeChildrenSize(node); x < size; x++) {
                    XPathNode xPathNode = (XPathNode) marshalContext.getNonAttributeChild(x, node);
                    xPathNode.marshal((MarshalRecord) record, object, session, namespaceResolver, marshaller, marshalContext.getMarshalContext(x), rootFragment);
                }
            }
        }
        marshalRecord.getCycleDetectionStack().pop();
        return record;
    }

    private void initCycleRecoverableClasses() {
        try {
            this.cycleRecoverableClass = PrivilegedAccessHelper.getClassForName(CYCLE_RECOVERABLE);
            this.cycleRecoverableContextClass = PrivilegedAccessHelper.getClassForName(CYCLE_RECOVERABLE_CONTEXT);
        } catch (Exception e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    public boolean marshalAttributes(MarshalRecord marshalRecord, Object object, CoreAbstractSession session) {
        lazyInitialize();
        boolean hasValue = false;
        NamespaceResolver namespaceResolver = ((Descriptor)descriptor).getNamespaceResolver();

        List<XPathNode> attributeChildren = rootXPathNode.getAttributeChildren();
        if (null != attributeChildren) {
            ObjectMarshalContext objectMarshalContext = ObjectMarshalContext.getInstance();
            for (int x = 0, attributeChildrenSize=attributeChildren.size(); x < attributeChildrenSize; x++) {
                hasValue = attributeChildren.get(x).marshal(marshalRecord, object, session, namespaceResolver, null, objectMarshalContext, null) || hasValue;
            }
        }

        if (rootXPathNode.getAnyAttributeNode() != null) {
            hasValue = rootXPathNode.getAnyAttributeNode().marshal(marshalRecord, object, session, namespaceResolver, null, ObjectMarshalContext.getInstance(), null) || hasValue;
        }

        List<XPathNode> selfChildren = rootXPathNode.getSelfChildren();
        if (null != selfChildren) {
            for (XPathNode selfXPathNode : selfChildren) {
                NodeValue marshalNodeValue = selfXPathNode.getMarshalNodeValue();
                if(marshalNodeValue instanceof MappingNodeValue) {
                    Mapping selfMapping = ((MappingNodeValue) marshalNodeValue).getMapping();
                    Object value = selfMapping.getAttributeValueFromObject(object);
                    Descriptor referenceDescriptor = (Descriptor)selfMapping.getReferenceDescriptor();
                    Descriptor valueDescriptor;
                    if(value != null && (referenceDescriptor == null || referenceDescriptor.hasInheritance())){
                        valueDescriptor = (Descriptor)session.getDescriptor(value.getClass());
                    } else {
                        valueDescriptor = referenceDescriptor;
                    }
                    if(null != valueDescriptor) {
                    	((XMLObjectBuilder)valueDescriptor.getObjectBuilder()).addXsiTypeAndClassIndicatorIfRequired(marshalRecord, valueDescriptor, referenceDescriptor, (Field) selfMapping.getField(), false);
                    }
                }
                selfXPathNode.marshalSelfAttributes(marshalRecord, object, session, namespaceResolver, marshalRecord.getMarshaller());
            }
        }

        return hasValue;
    }

    /**
     * Create a new row/record for the object builder.
     * This allows subclasses to define different record types.
     */
    public AbstractRecord createRecord(AbstractSession session) {
        lazyInitialize();
        org.eclipse.persistence.oxm.record.UnmarshalRecord uRec = new org.eclipse.persistence.oxm.record.UnmarshalRecord(this);
        uRec.setSession(session);
        return uRec;
    }

    /**
     * Create a new row/record for the object builder with the given name.
     * This allows subclasses to define different record types.
     */
    public AbstractRecord createRecord(String rootName, AbstractSession session) {
        NodeRecord nRec = new NodeRecord(rootName, getNamespaceResolver());
        nRec.setSession(session);
        return nRec;
    }

    /**
     * Create a new row/record for the object builder with the given name.
     * This allows subclasses to define different record types.
     */
    public AbstractRecord createRecord(String rootName, Node parent, AbstractSession session) {
        NodeRecord nRec = new NodeRecord(rootName, getNamespaceResolver(), parent);
        nRec.setSession(session);
        return nRec;
    }

    /**
     * Create a new row/record for the object builder.
     * This allows subclasses to define different record types.
     */
    public AbstractRecord createRecord(int size, AbstractSession session) {
        return createRecord(session);
    }

    @Override
    protected List addExtraNamespacesToNamespaceResolver(Descriptor desc, XMLRecord marshalRecord, CoreAbstractSession session, boolean allowOverride, boolean ignoreEqualResolvers) {
        if (rootXPathNode.getNonAttributeChildren() == null) {
            return null;
        } else {
            return super.addExtraNamespacesToNamespaceResolver(desc, marshalRecord, session, allowOverride, ignoreEqualResolvers);
        }
    }

}