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
 *     09 Jan 2013-2.5 Gordon Yorke
 *       - 397772: JPA 2.1 Entity Graph Support
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.AttributeNode;
import javax.persistence.EntityGraph;
import javax.persistence.Subgraph;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.jpa.metamodel.EmbeddableTypeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.EntityTypeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.ManagedTypeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.MetamodelImpl;
import org.eclipse.persistence.internal.jpa.metamodel.TypeImpl;
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

    protected Map<String, Map<Class, AttributeNodeImpl>> attributeNodes;

    protected EntityGraphImpl(AttributeGroup group, ClassDescriptor descriptor) {
        super();
        this.attributeGroup = group;
        this.classType = descriptor.getJavaClass();
        this.isMutable = true;
    }

    public EntityGraphImpl(AttributeGroup group) {
        super();
        this.attributeGroup = group;
        this.classType = group.getType();
    }

    protected EntityGraphImpl(AttributeGroup group, ClassDescriptor descriptor, String attribute) {
        this(group, descriptor);
        this.currentAttribute = attribute;
    }

    public String getName() {
        return attributeGroup.getName();
    }

    public void addAttributeNodes(String... attributeNames) {
        if (this.isMutable) {
            throw new IllegalStateException("immutable_entitygraph");
        }
        for (String attrName : attributeNames) {
            this.addAttributeNodeImpl(new AttributeNodeImpl<X>(attrName));
            //order is important here, must add attribute node to node list before adding to group or it will appear in node list twice.
            this.attributeGroup.addAttribute(attrName, (AttributeGroup) null);
        }

    }

    protected void addAttributeNodeImpl(AttributeNodeImpl attributeNode) {
        if (this.attributeNodes == null) {
            buildAttributeNodes();
        }
        Map map = new HashMap();
        map.put(ClassConstants.Object_Class, attributeNode);
        this.attributeNodes.put(attributeNode.getAttributeName(), map);
    }

    protected void addAttributeNodeImpl(EntityGraphImpl attributeNode) {
        if (this.attributeNodes == null) {
            buildAttributeNodes();
        }
        Map<Class, AttributeNodeImpl> map = this.attributeNodes.get(attributeNode.getAttributeName());
        if (map == null) {
            map = new HashMap<Class, AttributeNodeImpl>();
            this.attributeNodes.put(attributeNode.getAttributeName(), map);
        }
        map.put(attributeNode.classType, attributeNode);
    }

    public void addAttributeNodes(Attribute<X, ?>... attribute) {
        if (this.isMutable) {
            throw new IllegalStateException("immutable_entitygraph");
        }
        for (Attribute<X, ?> attrNode : attribute) {
            this.addAttributeNodeImpl(new AttributeNodeImpl<X>(attrNode.getName()));
            //order is important here, must add attribute node to node list before adding to group or it will appear in node list twice.
            this.attributeGroup.addAttribute(attrNode.getName());
        }
    }

    public <T> Subgraph<T> addSubgraph(Attribute<X, T> attribute) {
        Class type = attribute.getJavaType();
        if (attribute.isCollection()) {
            type = ((PluralAttribute) attribute).getBindableJavaType();
        }
        return addSubgraph(attribute.getName(), type);
    }

    public <T> Subgraph<? extends T> addSubgraph(Attribute<X, T> attribute, Class<? extends T> type) {
        return addSubgraph(attribute.getName(), type);
    }

    public <X> Subgraph<X> addSubgraph(String attributeName) {
        return this.addSubgraph(attributeName, null);
    }

    public <X> Subgraph<X> addSubgraph(String attributeName, Class<X> type) {
        if (this.isMutable) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("immutable_entitygraph"));
        }
        AttributeGroup localGroup = null;
        DatabaseMapping mapping = descriptor.getMappingForAttributeName(attributeName);
        if (mapping == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("metamodel_managed_type_attribute_not_present", new Object[] { this.descriptor.getJavaClassName(), attributeName }));
        }

        localGroup = new AttributeGroup(attributeName, type);

        ClassDescriptor targetDesc = mapping.getDescriptor();
        if (type != null && targetDesc.hasInheritance()) {
            targetDesc = targetDesc.getInheritancePolicy().getDescriptor(type);
            if (targetDesc == null) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("type_unkown_for_this_attribute", new Object[] { type.getName(), attributeName }));
            }
        }
        EntityGraphImpl entityGraph = new EntityGraphImpl(localGroup, targetDesc, attributeName);
        addAttributeNodeImpl(entityGraph);
        //order is important here, must add entity graph to node list before adding to group or it will appear in node list twice.
        this.attributeGroup.addAttribute(attributeName, localGroup);
        return entityGraph;
    }

    public <T> Subgraph<T> addKeySubgraph(Attribute<X, T> attribute) {
        if (this.isMutable) {
            throw new IllegalStateException("immutable_entitygraph");
        }
        Class type = attribute.getJavaType();
        if (attribute.isCollection()) {
            type = ((PluralAttribute) attribute).getBindableJavaType();
        }
        return addKeySubgraph(attribute.getName(), type);
    }

    public <T> Subgraph<? extends T> addKeySubgraph(Attribute<X, T> attribute, Class<? extends T> type) {
        return addKeySubgraph(attribute.getName(), type);
    }

    public <X> Subgraph<X> addKeySubgraph(String attributeName) {
        return this.addKeySubgraph(attributeName, null);
    }

    public <X> Subgraph<X> addKeySubgraph(String attributeName, Class<X> type) {
        if (this.isMutable) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("immutable_entitygraph"));
        }
        AttributeGroup localGroup = null;
        DatabaseMapping mapping = descriptor.getMappingForAttributeName(attributeName);
        if (mapping == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("metamodel_managed_type_attribute_not_present", new Object[] { this.descriptor.getJavaClassName(), attributeName }));
        }
        if (!mapping.getContainerPolicy().isMappedKeyMapPolicy() && !((MappedKeyMapContainerPolicy) mapping.getContainerPolicy()).isMapKeyAttribute()) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("attribute_is_not_map_with_managed_key", new Object[] { attributeName, descriptor.getJavaClassName() }));
        }

        localGroup = new AttributeGroup(attributeName, type);

        ClassDescriptor targetDesc = ((MappedKeyMapContainerPolicy) mapping.getContainerPolicy()).getDescriptorForMapKey();
        if (type != null && targetDesc.hasInheritance()) {
            targetDesc = targetDesc.getInheritancePolicy().getDescriptor(type);
            if (targetDesc == null) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("type_unkown_for_this_attribute", new Object[] { type.getName(), attributeName }));
            }
        }
        EntityGraphImpl entityGraph = new EntityGraphImpl(localGroup, targetDesc, attributeName);
        addAttributeNodeImpl(entityGraph);
        //order is important here, must add entity graph to node list before adding to group or it will appear in node list twice.
        this.attributeGroup.addAttributeKey(attributeName, localGroup);
        return entityGraph;
    }

    public <T> Subgraph<? extends T> addSubclassSubgraph(Class<? extends T> type) {
        if (this.isMutable) {
            throw new IllegalStateException("immutable_entitygraph");
        }
        ClassDescriptor targetDesc = this.descriptor;
        if (targetDesc.hasInheritance()) {
            targetDesc = targetDesc.getInheritancePolicy().getDescriptor(type);
            if (targetDesc == null) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("type_unkown_for_this_entity", new Object[] { type.getName(), this.descriptor.getJavaClassName() }));
            }
        }
        AttributeGroup subGroup = new AttributeGroup(this.attributeGroup.getName(), type);
        this.attributeGroup.insertSubClass(subGroup);
        return new EntityGraphImpl(subGroup, targetDesc);
    }

    public List<AttributeNode<?>> getAttributeNodes() {
        if (this.attributeNodes == null) {
            buildAttributeNodes();
        }
        List<AttributeNode<?>> nodes = new ArrayList<AttributeNode<?>>();
        for (Map<Class, AttributeNodeImpl> map : this.attributeNodes.values()) {
            nodes.addAll((Collection<? extends AttributeNode<?>>) map.values());
        }
        return nodes;
    }

    public Class<X> getClassType() {
        return this.classType;
    }

    /**
     * @return the attributeGroup
     */
    public AttributeGroup getAttributeGroup() {
        return attributeGroup;
    }

    public String getAttributeName() {
        return currentAttribute;
    }

    protected void buildAttributeNodes() {
        //this instance was built from a pre-existing attribute group so we need to rebuild
        //and entity graph
        this.attributeNodes = new HashMap<String, Map<Class, AttributeNodeImpl>>();
        for (AttributeItem item : this.attributeGroup.getItems().values()) {
            boolean complex = false;
            ClassDescriptor localDescriptor = null;
            if (this.descriptor != null) {
                localDescriptor = this.descriptor.getMappingForAttributeName(item.getAttributeName()).getReferenceDescriptor();
            }
            if (item.getGroups() != null) {
                Map<Class, AttributeNodeImpl> subGraphs = new HashMap<Class, AttributeNodeImpl>();
                complex = true;
                for (AttributeGroup subGroup : item.getGroups().values()) {
                    Class type = subGroup.getType();
                    if (type == null) {
                        type = ClassConstants.Object_Class;
                    }
                    EntityGraphImpl node = null;
                    if (localDescriptor != null) {
                        if (!type.equals(ClassConstants.Object_Class) && localDescriptor.hasInheritance()) {
                            localDescriptor = localDescriptor.getInheritancePolicy().getDescriptor(type);
                        }
                        node = new EntityGraphImpl(subGroup, localDescriptor);
                    } else {
                        node = new EntityGraphImpl(subGroup);
                    }
                    subGraphs.put(type, node);

                }
                this.attributeNodes.put(item.getAttributeName(), subGraphs);
            }
            if (item.getKeyGroups() != null) {
                Map<Class, AttributeNodeImpl> subGraphs = new HashMap<Class, AttributeNodeImpl>();
                complex = true;
                for (AttributeGroup subGroup : item.getKeyGroups().values()) {
                    Class type = subGroup.getType();
                    if (type == null) {
                        type = ClassConstants.Object_Class;
                    }
                    EntityGraphImpl node = null;
                    if (localDescriptor != null) {
                        if (!type.equals(ClassConstants.Object_Class) && localDescriptor.hasInheritance()) {
                            localDescriptor = localDescriptor.getInheritancePolicy().getDescriptor(type);
                        }
                        node = new EntityGraphImpl(subGroup, localDescriptor);
                    } else {
                        node = new EntityGraphImpl(subGroup);
                    }
                    subGraphs.put(type, node);
                }
                this.attributeNodes.put(item.getAttributeName(), subGraphs);
            }
            if (!complex) {
                this.addAttributeNodeImpl(new AttributeNodeImpl(item.getAttributeName()));
            }
        }

    }

}
