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
package org.eclipse.persistence.internal.jpa;

import jakarta.persistence.AttributeNode;
import jakarta.persistence.FetchOption;
import jakarta.persistence.FetchType;
import jakarta.persistence.Subgraph;
import jakarta.persistence.metamodel.Attribute;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Map;
import java.util.Set;


/**
 * Concrete Jakarta Persistence AttributeNode class. For this implementation the AttributeNode includes information from live
 * AttributeItems.
 */
@SuppressWarnings({"rawtypes"})
public class AttributeNodeImpl<X> implements AttributeNode<X>{

    protected String currentAttribute;
    protected EntityGraphImpl<?> parentGraph;
    protected Map<Class<?>, Subgraph<?>> subgraphs;
    protected Map<Class<?>, Subgraph<?>> keySubgraphs;

    protected AttributeNodeImpl(){
    }

    protected AttributeNodeImpl(String attribute){
        this.currentAttribute = attribute;
    }

    protected AttributeNodeImpl(String attribute, EntityGraphImpl<?> parentGraph) {
        this(attribute);
        this.parentGraph = parentGraph;
    }

    @Override
    public String getAttributeName() {
        return currentAttribute;
    }

    @Override
    public Map<Class<?>, Subgraph<?>> getSubgraphs() {
        if (subgraphs == null){
            subgraphs = new HashMap<>();
        }

        return new HashMap<>(subgraphs);
    }

    @Override
    public Map<Class<?>, Subgraph<?>> getKeySubgraphs() {
        if (this.keySubgraphs == null){
            this.keySubgraphs = new HashMap<>();
        }

        return new HashMap<>(this.keySubgraphs);
    }

    public void addSubgraph(EntityGraphImpl entityGraphImpl) {
        if (subgraphs == null){
            subgraphs = new HashMap<>();
        }

        subgraphs.put(entityGraphImpl.getClassType(), entityGraphImpl);
    }

    public void addKeySubgraph(EntityGraphImpl entityGraphImpl) {
        if (keySubgraphs == null){
            keySubgraphs = new HashMap<>();
        }

        keySubgraphs.put(entityGraphImpl.getClassType(), entityGraphImpl);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Attribute<?, X> getAttribute() {
        return (Attribute<?, X>) parentGraph.getGraphedType().getAttribute(currentAttribute);
    }

    @Override
    public Subgraph<X> addSubgraph() {
        return parentGraph.addSubgraph(currentAttribute);
    }

    @Override
    public <S extends X> Subgraph<S> addTreatedSubgraph(Class<S> type) {
        return parentGraph.addSubgraph(currentAttribute, Objects.requireNonNull(type));
    }

    @Override
    public AttributeNode<X> addOption(FetchOption option) {
        parentGraph.setNodeOption(this, Objects.requireNonNull(option));
        return this;
    }

    @Override
    public Set<FetchOption> getOptions() {
        Set<FetchOption> options = new HashSet<>();
        if (parentGraph != null) {
            parentGraph.getAttributeGroup().getAttributeOptions().getOrDefault(currentAttribute, Map.of())
                    .values().forEach(value -> options.add((FetchOption) value));
        }
        if (options.stream().noneMatch(FetchType.class::isInstance)) {
            options.add(FetchType.EAGER);
        }
        return options;
    }
}
