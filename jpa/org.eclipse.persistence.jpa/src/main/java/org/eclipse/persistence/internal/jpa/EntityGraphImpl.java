/*
 * Copyright (c) 1998, 2026 Oracle and/or its affiliates. All rights reserved.
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
//     09 Jan 2013-2.5 Gordon Yorke
//       - 397772: JPA 2.1 Entity Graph Support
//     08/23/2023: Tomas Kraus
//       - New Jakarta Persistence 3.2 Features
package org.eclipse.persistence.internal.jpa;

import jakarta.persistence.AttributeNode;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.FetchOption;
import jakarta.persistence.FetchType;
import jakarta.persistence.Subgraph;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.metamodel.MapAttribute;
import jakarta.persistence.metamodel.PluralAttribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.jpa.metamodel.AttributeImpl;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.queries.AttributeItem;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.AttributeGroup;

/**
 * Concrete Jakarta Persistence EntityGraph class. For this implementation the EntityGraphImpl
 * wraps the EclipseLink AttributeGroup type.
 */
public class EntityGraphImpl<X> extends AttributeNodeImpl<X> implements EntityGraph<X>, Subgraph<X> {

    protected AttributeGroup attributeGroup;

    protected transient boolean isMutable = false;

    protected transient ClassDescriptor descriptor;

    protected Class<X> classType;

    protected transient Metamodel metamodel;

    protected Map<String, AttributeNodeImpl<?>> attributeNodes;

    protected EntityGraphImpl(AttributeGroup group, ClassDescriptor descriptor) {
        super();
        this.attributeGroup = group;
        this.classType = descriptor.getJavaClass();
        this.isMutable = true;
        this.descriptor = descriptor;
    }

    public EntityGraphImpl(AttributeGroup group, ClassDescriptor descriptor, Metamodel metamodel) {
        this(group, descriptor);
        this.metamodel = Objects.requireNonNull(metamodel);
    }

    @SuppressWarnings("unchecked")
    public EntityGraphImpl(AttributeGroup group) {
        super();
        this.attributeGroup = group;
        this.classType = (Class<X>) group.getType();
        if (this.classType == null){
            this.classType = (Class<X>) CoreClassConstants.OBJECT;
        }
    }

    protected EntityGraphImpl(AttributeGroup group, ClassDescriptor descriptor, String attribute) {
        this(group, descriptor);
        this.currentAttribute = attribute;
    }

    @Override
    public String getName() {
        return attributeGroup.getName();
    }

    @Override
    public <Y> AttributeNode<Y> addAttributeNode(String attributeName) {
        if (!isMutable) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("immutable_entitygraph"));
        }

        return addAttributeNodeImpl(attributeName);
    }

    @Override
    public <Y> AttributeNode<Y> addAttributeNode(Attribute<? super X, Y> attribute) {
        if (!isMutable) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("immutable_entitygraph"));
        }

        validateAttribute(attribute);
        return addAttributeNodeImpl(attribute.getName());
    }

    @Override
    public void addAttributeNodes(String... attributeNames) {
        if (!isMutable) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("immutable_entitygraph"));
        }

        for (String attrName : attributeNames) {
            addAttributeNodeImpl(attrName);
        }
    }

    @Override
    @SafeVarargs
    public final void addAttributeNodes(Attribute<? super X, ?>... attributes) {
        if (!isMutable) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("immutable_entitygraph"));
        }

        for (Attribute<? super X, ?> attribute : attributes) {
            validateAttribute(attribute);
            addAttributeNodeImpl(attribute.getName());
        }
    }

    @Override
    public boolean hasAttributeNode(String attributeName) {
        validateAttribute(attributeName);
        initializeAttributeNodes();
        return attributeNodes.containsKey(attributeName);
    }

    @Override
    public boolean hasAttributeNode(Attribute<? super X, ?> attribute) {
        validateAttribute(attribute);
        return hasAttributeNode(attribute.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Y> AttributeNode<Y> getAttributeNode(String attributeName) {
        validateAttribute(attributeName);
        initializeAttributeNodes();
        AttributeNode<?> node = attributeNodes.get(attributeName);
        if (node == null) {
            throw new NoSuchElementException(attributeName);
        }
        return (AttributeNode<Y>) node;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Y> AttributeNode<Y> getAttributeNode(Attribute<? super X, Y> attribute) {
        validateAttribute(attribute);
        return getAttributeNode(attribute.getName());
    }

    @Override
    public void removeAttributeNode(String attributeName) {
        removeAttributeNodeImpl(attributeName);
    }

    @Override
    public void removeAttributeNode(Attribute<? super X, ?> attribute) {
        validateAttribute(attribute);
        removeAttributeNodeImpl(attribute.getName());
    }

    @Override
    public void removeAttributeNodes(Attribute.PersistentAttributeType nodeTypes) {
        verifyMutable();
        Objects.requireNonNull(nodeTypes);
        for (DatabaseMapping mapping : descriptor.getMappings()) {
            if (AttributeImpl.getPersistentAttributeType(mapping) == nodeTypes) {
                removeAttributeNodeImpl(mapping.getAttributeName());
            }
        }
    }

    private void verifyMutable() {
        if (!isMutable) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("immutable_entitygraph"));
        }
    }

    private void initializeAttributeNodes() {
        if (attributeNodes == null) {
            buildAttributeNodes();
        }
    }

    private DatabaseMapping validateAttribute(String attributeName) {
        DatabaseMapping mapping = descriptor == null || attributeName == null
                ? null : descriptor.getMappingForAttributeName(attributeName);
        if (mapping == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                    "metamodel_managed_type_attribute_not_present", new Object[] {attributeName, classType}));
        }
        return mapping;
    }

    private void validateAttribute(Attribute<?, ?> attribute) {
        if (attribute == null || !attribute.getDeclaringType().getJavaType().isAssignableFrom(classType)) {
            throw new IllegalArgumentException("The attribute does not belong to this graph type");
        }
        validateAttribute(attribute.getName());
    }

    @SuppressWarnings("unchecked")
    private <Y> AttributeNode<Y> addAttributeNodeImpl(String attributeName) {
        verifyMutable();
        validateAttribute(attributeName);
        initializeAttributeNodes();
        AttributeNodeImpl<Y> node = (AttributeNodeImpl<Y>) attributeNodes.computeIfAbsent(attributeName,
                name -> new AttributeNodeImpl<>(name, this));
        setNodeOption(node, FetchType.EAGER);
        return node;
    }

    void setNodeOption(AttributeNodeImpl<?> node, FetchOption option) {
        verifyMutable();
        Map<Class<?>, Object> options = new HashMap<>();
        node.getOptions().forEach(value -> options.put(value.getClass(), value));
        options.put(option.getClass(), option);
        attributeGroup.setAttributeOptions(node.getAttributeName(), options);
        if (option == FetchType.LAZY) {
            attributeGroup.removeAttribute(node.getAttributeName());
            node.subgraphs = null;
            node.keySubgraphs = null;
        } else if (option instanceof FetchType && !attributeGroup.containsAttribute(node.getAttributeName())) {
            attributeGroup.addAttribute(node.getAttributeName(), (AttributeGroup) null);
        }
    }

    protected void addAttributeNodeImpl(AttributeNodeImpl<?> attributeNode) {
        if (attributeNodes == null) {
            buildAttributeNodes();
        }
        attributeNodes.put(attributeNode.getAttributeName(), attributeNode);
    }

    // Remove an attribute node of given name from the entity graph.
    private void removeAttributeNodeImpl(String attributeName) {
        AttributeNodeImpl<?> node = (AttributeNodeImpl<?>) addAttributeNodeImpl(attributeName);
        setNodeOption(node, FetchType.LAZY);
    }

    @Override
    public <T> Subgraph<T> addSubgraph(Attribute<? super X, T> attribute) {
        validateAttribute(attribute);
        Class<T> type = attribute.getJavaType();
        if (attribute.isCollection()) {
            type = ((PluralAttribute) attribute).getBindableJavaType();
        }

        return addSubgraph(attribute.getName(), type);
    }

    @Override
    public <S extends X> Subgraph<S> addTreatedSubgraph(Class<S> type) {
        if (!this.isMutable) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("immutable_entitygraph"));
        }

        ClassDescriptor targetDesc = resolveTargetDescriptor(descriptor, Objects.requireNonNull(type), null);

        AttributeGroup subGroup = new AttributeGroup(this.attributeGroup.getName(), type, true);
        this.attributeGroup.getSubClassGroups().put(type, subGroup);
        subGroup.setAllSubclasses(this.attributeGroup.getSubClassGroups());
        this.attributeGroup.insertSubClass(subGroup);
        return newSubgraph(subGroup, targetDesc, null);
    }

    @Override
    public <Y> Subgraph<Y> addTreatedSubgraph(Attribute<? super X, ? super Y> attribute, Class<Y> type) {
        validateAttribute(attribute);
        return addSubgraph(attribute.getName(), type);
    }

    @Override
    public <T> Subgraph<T> addSubgraph(String attributeName) {
        return this.addSubgraph(attributeName, null);
    }

    @Override
    public <T> Subgraph<T> addSubgraph(String attributeName, Class<T> type) {
        if (!this.isMutable) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("immutable_entitygraph"));
        }

        DatabaseMapping mapping = validateAttribute(attributeName);
        ClassDescriptor targetDesc = resolveTargetDescriptor(mapping.getReferenceDescriptor(), type, attributeName);
        AttributeNodeImpl<?> node = (AttributeNodeImpl<?>) addAttributeNodeImpl(attributeName);
        Subgraph<?> existing = node.getSubgraphs().get(targetDesc.getJavaClass());
        if (existing != null) {
            return (Subgraph<T>) existing;
        }
        AttributeGroup localGroup = new AttributeGroup(attributeName, targetDesc.getJavaClass(), true);
        EntityGraphImpl<T> entityGraph = newSubgraph(localGroup, targetDesc, attributeName);
        node.addSubgraph(entityGraph);
        attributeGroup.addAttribute(attributeName, localGroup);
        return entityGraph;
    }

    private ClassDescriptor resolveTargetDescriptor(ClassDescriptor target, Class<?> type, String attributeName) {
        if (target == null) {
            throw new IllegalArgumentException("The graph attribute must have a managed type: " + attributeName);
        }
        if (type == null || target.getJavaClass().equals(type)) {
            return target;
        }
        if (!target.getJavaClass().isAssignableFrom(type) || !target.hasInheritance()) {
            throw new IllegalArgumentException("Invalid graph subtype: " + type.getName());
        }
        ClassDescriptor treated = target.getInheritancePolicy().getDescriptor(type);
        if (treated == null) {
            throw new IllegalArgumentException("Invalid graph subtype: " + type.getName());
        }
        return treated;
    }

    private <T> EntityGraphImpl<T> newSubgraph(AttributeGroup group, ClassDescriptor target, String attribute) {
        EntityGraphImpl<T> graph = new EntityGraphImpl<>(group, target, attribute);
        graph.metamodel = metamodel;
        graph.isMutable = isMutable;
        return graph;
    }

    /*
     * The four addElementSubgraph/addTreatedElementSubgraph overloads describe the element of a
     * collection:
     *
     * "a node to the graph representing a collection element that is a managed type"
     *
     * This is the value side, and so they delegate to addSubgraph.
     *
     * They used to delegate to addKeySubgraph, which describes the key of a Map instead; that
     * resolved the target against getDescriptorForMapKey() and recorded it as a key group, and
     * for any attribute that was not
     *
     * a Map at all it failed outright. addKeySubgraph, addMapKeySubgraph and
     * addTreatedMapKeySubgraph are the map key methods and stay as they are.
     */

    @Override
    public <X1> Subgraph<X1> addElementSubgraph(String attributeName) {
        return addElementSubgraph(attributeName, null);
    }

    @Override
    public <X1> Subgraph<X1> addElementSubgraph(String attributeName, Class<X1> type) {
        verifyMutable();
        if (!validateAttribute(attributeName).isCollectionMapping()) {
            throw new IllegalArgumentException("An element subgraph requires a collection attribute: " + attributeName);
        }
        return addSubgraph(attributeName, type);
    }

    @Override
    public <E> Subgraph<E> addElementSubgraph(PluralAttribute<? super X, ?, E> attribute) {
        validateAttribute(attribute);
        return addElementSubgraph(attribute.getName(), attribute.getBindableJavaType());
    }

    @Override
    public <E> Subgraph<E> addTreatedElementSubgraph(PluralAttribute<? super X, ?, ? super E> attribute, Class<E> type) {
        validateAttribute(attribute);
        return addElementSubgraph(attribute.getName(), type);
    }

    @Override
    public <K> Subgraph<K> addMapKeySubgraph(MapAttribute<? super X, K, ?> attribute) {
        validateAttribute(attribute);
        return addKeySubgraph(attribute.getName(), attribute.getKeyJavaType());
    }

    @Override
    public <K> Subgraph<K> addTreatedMapKeySubgraph(MapAttribute<? super X, ? super K, ?> attribute, Class<K> type) {
        validateAttribute(attribute);
        return addKeySubgraph(attribute.getName(), type);
    }

    @Override
    public <T> Subgraph<T> addKeySubgraph(String attributeName) {
        return this.addKeySubgraph(attributeName, null);
    }

    @Override
    public <T> Subgraph<T> addKeySubgraph(String attributeName, Class<T> type) {
        if (!this.isMutable) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("immutable_entitygraph"));
        }

        DatabaseMapping mapping = validateAttribute(attributeName);
        if (!mapping.isCollectionMapping()) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                    "attribute_is_not_map_with_managed_key", new Object[] {attributeName, descriptor.getJavaClassName()}));
        }
        ContainerPolicy containerPolicy = mapping.getContainerPolicy();
        if (containerPolicy == null || !containerPolicy.isMappedKeyMapPolicy()
                || ((MappedKeyMapContainerPolicy) containerPolicy).isMapKeyAttribute()) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                    "attribute_is_not_map_with_managed_key", new Object[] {attributeName, descriptor.getJavaClassName()}));
        }
        ClassDescriptor targetDesc = resolveTargetDescriptor(containerPolicy.getDescriptorForMapKey(), type, attributeName);
        AttributeNodeImpl<?> node = (AttributeNodeImpl<?>) addAttributeNodeImpl(attributeName);
        Subgraph<?> existing = node.getKeySubgraphs().get(targetDesc.getJavaClass());
        if (existing != null) {
            return (Subgraph<T>) existing;
        }
        AttributeGroup localGroup = new AttributeGroup(attributeName, targetDesc.getJavaClass(), true);
        EntityGraphImpl<T> entityGraph = newSubgraph(localGroup, targetDesc, attributeName);
        node.addKeySubgraph(entityGraph);
        attributeGroup.addAttributeKey(attributeName, localGroup);
        return entityGraph;
    }

    @Override
    public List<AttributeNode<?>> getAttributeNodes() {
        if (this.attributeNodes == null) {
            buildAttributeNodes();
        }
        return new ArrayList<>(this.attributeNodes.values());
    }

    @Override
    public Class<X> getClassType() {
        return this.classType;
    }

    /**
     * @return the attributeGroup
     */
    public AttributeGroup getAttributeGroup() {
        return attributeGroup;
    }

    @Override
    public String getAttributeName() {
        return currentAttribute;
    }

    protected void buildAttributeNodes() {
        //this instance was built from a pre-existing attribute group so we need to rebuild
        //and entity graph
        this.attributeNodes = new HashMap<>();
        for (AttributeItem item : this.attributeGroup.getItems().values()) {
            AttributeNodeImpl<?> node = new AttributeNodeImpl<>(item.getAttributeName(), this);
            ClassDescriptor localDescriptor = null;
            if (this.descriptor != null) {
                localDescriptor = this.descriptor.getMappingForAttributeName(item.getAttributeName()).getReferenceDescriptor();
            }
            if (item.getGroups() != null && ! item.getGroups().isEmpty()) {
                for (AttributeGroup subGroup : item.getGroups().values()) {
                    Class<?> type = subGroup.getType();
                    if (type == null) {
                        type = CoreClassConstants.OBJECT;
                    }
                    if (localDescriptor != null) {
                        if (!type.equals(CoreClassConstants.OBJECT) && localDescriptor.hasInheritance()) {
                            localDescriptor = localDescriptor.getInheritancePolicy().getDescriptor(type);
                        }
                        node.addSubgraph(newSubgraph(subGroup, localDescriptor, item.getAttributeName()));
                    } else {
                        node.addSubgraph(new EntityGraphImpl<>(subGroup));
                    }

                }
            }
            if (item.getKeyGroups() != null && ! item.getKeyGroups().isEmpty()) {
                if (descriptor != null) {
                    ContainerPolicy policy = descriptor.getMappingForAttributeName(item.getAttributeName()).getContainerPolicy();
                    localDescriptor = policy == null ? null : policy.getDescriptorForMapKey();
                }
                for (AttributeGroup subGroup : item.getKeyGroups().values()) {
                    Class<?> type = subGroup.getType();
                    if (type == null) {
                        type = CoreClassConstants.OBJECT;
                    }
                    if (localDescriptor != null) {
                        if (!type.equals(CoreClassConstants.OBJECT) && localDescriptor.hasInheritance()) {
                            localDescriptor = localDescriptor.getInheritancePolicy().getDescriptor(type);
                        }
                        node.addKeySubgraph(newSubgraph(subGroup, localDescriptor, item.getAttributeName()));
                    } else {
                        node.addKeySubgraph(new EntityGraphImpl<>(subGroup));
                    }
                }
            }
            this.attributeNodes.put(item.getAttributeName(), node);
        }
        for (String attributeName : attributeGroup.getAttributeOptions().keySet()) {
            attributeNodes.computeIfAbsent(attributeName, name -> new AttributeNodeImpl<>(name, this));
        }
    }

    @Override
    public ManagedType<X> getGraphedType() {
        if (metamodel == null) {
            throw new IllegalStateException("This graph has no persistence-unit metamodel");
        }
        return metamodel.managedType(classType);
    }

}
