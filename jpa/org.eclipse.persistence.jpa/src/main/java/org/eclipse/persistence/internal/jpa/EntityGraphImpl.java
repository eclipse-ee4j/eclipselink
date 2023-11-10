/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.AttributeNode;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.Subgraph;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.MapAttribute;
import jakarta.persistence.metamodel.PluralAttribute;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.queries.AttributeItem;
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.AttributeGroup;

/**
 * Concrete JPA EntityGraph class. For this implementation the EntityGraphImpl
 * wraps the EclipseLink AttributeGroup type.
 */
public class EntityGraphImpl<X> extends AttributeNodeImpl<X> implements EntityGraph<X>, Subgraph<X> {

    protected AttributeGroup attributeGroup;

    protected transient boolean isMutable = false;

    protected transient ClassDescriptor descriptor;

    protected Class<X> classType;

    protected Map<String, AttributeNodeImpl<?>> attributeNodes;

    protected EntityGraphImpl(AttributeGroup group, ClassDescriptor descriptor) {
        super();
        this.attributeGroup = group;
        this.classType = descriptor.getJavaClass();
        this.isMutable = true;
        this.descriptor = descriptor;
    }

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
            addAttributeNodeImpl(attribute.getName());
        }
    }

    @Override
    public boolean hasAttributeNode(String attributeName) {
        return attributeNodes.containsKey(attributeName);
    }

    @Override
    public boolean hasAttributeNode(Attribute<? super X, ?> attribute) {
        return hasAttributeNode(attribute.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Y> AttributeNode<Y> getAttributeNode(String attributeName) {
        return (AttributeNode<Y>) attributeNodes.get(attributeName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Y> AttributeNode<Y> getAttributeNode(Attribute<? super X, Y> attribute) {
        return (AttributeNode<Y>) attributeNodes.get(attribute.getName());
    }

    @Override
    public void removeAttributeNode(String attributeName) {
        if (!isMutable) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("immutable_entitygraph"));
        }
        removeAttributeNodeImpl(attributeName);
    }

    @Override
    public void removeAttributeNode(Attribute<? super X, ?> attribute) {
        removeAttributeNodeImpl(attribute.getName());
    }

    // TODO-API-3.2
    @Override
    public void removeAttributeNodes(Attribute.PersistentAttributeType nodeTypes) {
        throw new UnsupportedOperationException("Jakarta Persistence 3.2 API was not implemented yet");
    }

    // Add an attribute node of given name to the entity graph.
    private <Y> AttributeNode<Y> addAttributeNodeImpl(String attributeName) {
        if (descriptor.getMappingForAttributeName(attributeName) == null) {
            throw new IllegalArgumentException(
                    ExceptionLocalization.buildMessage(
                            "metamodel_managed_type_attribute_not_present",
                            new Object[] {attributeName, this.getClassType()}));
        }
        AttributeNodeImpl<Y> attributeNode = new AttributeNodeImpl<>(attributeName);
        addAttributeNodeImpl(attributeNode);
        //order is important here, must add attribute node to node list before adding to group or it will appear in node list twice.
        attributeGroup.addAttribute(attributeName, (AttributeGroup) null);
        return attributeNode;
    }

    protected void addAttributeNodeImpl(AttributeNodeImpl<?> attributeNode) {
        if (attributeNodes == null) {
            buildAttributeNodes();
        }
        attributeNodes.put(attributeNode.getAttributeName(), attributeNode);
    }

    // Remove an attribute node of given name from the entity graph.
    private void removeAttributeNodeImpl(String attributeName) {
        attributeGroup.removeAttribute(attributeName);
        attributeNodes.remove(attributeName);
    }

    @Override
    public <T> Subgraph<T> addSubgraph(Attribute<? super X, T> attribute) {
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
        ClassDescriptor targetDesc = this.descriptor;
        if (targetDesc.hasInheritance()) {
            targetDesc = targetDesc.getInheritancePolicy().getDescriptor(type);
            if (targetDesc == null) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("type_unkown_for_this_entity", new Object[] { type.getName(), this.descriptor.getJavaClassName() }));
            }
        }
        AttributeGroup subGroup = new AttributeGroup(this.attributeGroup.getName(), type, true);
        this.attributeGroup.getSubClassGroups().put(type, subGroup);
        subGroup.setAllSubclasses(this.attributeGroup.getSubClassGroups());
        this.attributeGroup.insertSubClass(subGroup);
        return new EntityGraphImpl<>(subGroup, targetDesc);
    }

    @Override
    @SuppressWarnings({"removal", "rawtypes", "unchecked"})
    public <T> Subgraph<? extends T> addSubclassSubgraph(Class<? extends T> type) {
        return addTreatedSubgraph((Class)type);
    }

    @Override
    public <Y> Subgraph<Y> addTreatedSubgraph(Attribute<? super X, ? super Y> attribute, Class<Y> type) {
        return addSubgraph(attribute.getName(), type);
    }

    @Override
    @SuppressWarnings("removal")
    public <T> Subgraph<? extends T> addSubgraph(Attribute<? super X, T> attribute, Class<? extends T> type) {
        return addSubgraph(attribute.getName(), type);
    }

    @Override
    public <X> Subgraph<X> addSubgraph(String attributeName) {
        return this.addSubgraph(attributeName, null);
    }

    @Override
    public <X> Subgraph<X> addSubgraph(String attributeName, Class<X> type) {
        if (!this.isMutable) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("immutable_entitygraph"));
        }
        AttributeNodeImpl node = null;
        if (this.attributeNodes  != null){
            node = this.attributeNodes.get(attributeName);
        }
        if (node == null){
            node = new AttributeNodeImpl<X>(attributeName);
            addAttributeNodeImpl(node);
        }
        AttributeGroup localGroup = null;
        DatabaseMapping mapping = descriptor.getMappingForAttributeName(attributeName);
        if (mapping == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("metamodel_managed_type_attribute_not_present", new Object[] { attributeName, this.descriptor.getJavaClassName() }));
        }

        localGroup = new AttributeGroup(attributeName, type, true);

        ClassDescriptor targetDesc = mapping.getReferenceDescriptor();
        if (type != null && targetDesc.hasInheritance()) {
            targetDesc = targetDesc.getInheritancePolicy().getDescriptor(type);
            if (targetDesc == null) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("type_unkown_for_this_attribute", new Object[] { type.getName(), attributeName }));
            }
        }
        EntityGraphImpl entityGraph = new EntityGraphImpl(localGroup, targetDesc, attributeName);
        node.addSubgraph(entityGraph);
        //order is important here, must add entity graph to node list before adding to group or it will appear in node list twice.
        this.attributeGroup.addAttribute(attributeName, localGroup);
        return entityGraph;
    }

    @Override
    public <X1> Subgraph<X1> addElementSubgraph(String attributeName) {
        return addKeySubgraph(attributeName, null);
    }

    @Override
    public <X1> Subgraph<X1> addElementSubgraph(String attributeName, Class<X1> type) {
        return addKeySubgraph(attributeName, type);
    }

    @Override
    public <E> Subgraph<E> addElementSubgraph(PluralAttribute<? super X, ?, E> attribute) {
        return addKeySubgraph(attribute.getName(), attribute.getBindableJavaType());
    }

    @Override
    public <E> Subgraph<E> addTreatedElementSubgraph(PluralAttribute<? super X, ?, ? super E> attribute, Class<E> type) {
        return addKeySubgraph(attribute.getName(), type);
    }

    @Override
    public <K> Subgraph<K> addMapKeySubgraph(MapAttribute<? super X, K, ?> attribute) {
        return addKeySubgraph(attribute.getName(), attribute.getKeyJavaType());
    }

    @Override
    public <K> Subgraph<K> addTreatedMapKeySubgraph(MapAttribute<? super X, ? super K, ?> attribute, Class<K> type) {
        return addKeySubgraph(attribute.getName(), type);
    }

    @Override
    @SuppressWarnings({"removal", "unchecked"})
    public <T> Subgraph<T> addKeySubgraph(Attribute<? super X, T> attribute) {
        if (!this.isMutable) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("immutable_entitygraph"));
        }
        Class<T> type = attribute.getJavaType();
        if (attribute.isCollection()) {
            type = ((PluralAttribute<X, ?, T>) attribute).getBindableJavaType();
        }
        return addKeySubgraph(attribute.getName(), type);
    }

    @Override
    @SuppressWarnings("removal")
    public <T> Subgraph<? extends T> addKeySubgraph(Attribute<? super X, T> attribute, Class<? extends T> type) {
        return addKeySubgraph(attribute.getName(), type);
    }

    @Override
    public <X> Subgraph<X> addKeySubgraph(String attributeName) {
        return this.addKeySubgraph(attributeName, null);
    }

    @Override
    public <X> Subgraph<X> addKeySubgraph(String attributeName, Class<X> type) {
        if (!this.isMutable) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("immutable_entitygraph"));
        }
        AttributeNodeImpl node = null;
        if (this.attributeNodes  != null){
            node = this.attributeNodes.get(attributeName);
        }
        if (node == null){
            node = new AttributeNodeImpl<X>(attributeName);
            addAttributeNodeImpl(node);
        }
        AttributeGroup localGroup = null;
        DatabaseMapping mapping = descriptor.getMappingForAttributeName(attributeName);
        if (mapping == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("metamodel_managed_type_attribute_not_present", new Object[] { this.descriptor.getJavaClassName(), attributeName }));
        }
        if (!mapping.getContainerPolicy().isMappedKeyMapPolicy() && !((MappedKeyMapContainerPolicy) mapping.getContainerPolicy()).isMapKeyAttribute()) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("attribute_is_not_map_with_managed_key", new Object[] { attributeName, descriptor.getJavaClassName() }));
        }

        localGroup = new AttributeGroup(attributeName, type, true);

        ClassDescriptor targetDesc = mapping.getContainerPolicy().getDescriptorForMapKey();
        if (type != null && targetDesc.hasInheritance()) {
            targetDesc = targetDesc.getInheritancePolicy().getDescriptor(type);
            if (targetDesc == null) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("type_unkown_for_this_attribute", new Object[] { type.getName(), attributeName }));
            }
        }
        EntityGraphImpl entityGraph = new EntityGraphImpl(localGroup, targetDesc, attributeName);
        node.addKeySubgraph(entityGraph);
        //order is important here, must add entity graph to node list before adding to group or it will appear in node list twice.
        this.attributeGroup.addAttributeKey(attributeName, localGroup);
        return entityGraph;
    }

    @Override
    public List<AttributeNode<?>> getAttributeNodes() {
        if (this.attributeNodes == null) {
            buildAttributeNodes();
        }
        return new ArrayList(this.attributeNodes.values());
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
            AttributeNodeImpl<?> node = new AttributeNodeImpl<>(item.getAttributeName());
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
                        node.addSubgraph(new EntityGraphImpl<>(subGroup, localDescriptor));
                    } else {
                        node.addSubgraph(new EntityGraphImpl<>(subGroup));
                    }

                }
            }
            if (item.getKeyGroups() != null && ! item.getKeyGroups().isEmpty()) {
                for (AttributeGroup subGroup : item.getKeyGroups().values()) {
                    Class<?> type = subGroup.getType();
                    if (type == null) {
                        type = CoreClassConstants.OBJECT;
                    }
                    if (localDescriptor != null) {
                        if (!type.equals(CoreClassConstants.OBJECT) && localDescriptor.hasInheritance()) {
                            localDescriptor = localDescriptor.getInheritancePolicy().getDescriptor(type);
                        }
                        node.addKeySubgraph(new EntityGraphImpl<>(subGroup, localDescriptor));
                    } else {
                        node.addKeySubgraph(new EntityGraphImpl<>(subGroup));
                    }
                }
            }
            this.attributeNodes.put(item.getAttributeName(), node);
        }

    }

}
