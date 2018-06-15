/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     09 Jan 2013-2.5 Gordon Yorke
//       - 397772: JPA 2.1 Entity Graph Support
package org.eclipse.persistence.internal.jpa;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.AttributeNode;
import javax.persistence.Subgraph;


/**
 * Concrete JPA AttributeNode class. For this implementation the AttributeNode includes information from live
 * AttributeItems.
 */
public class AttributeNodeImpl<X> implements AttributeNode<X>{


    protected String currentAttribute;

    protected Map<Class, Subgraph> subgraphs;

    protected Map<Class, Subgraph> keySubgraphs;

    protected AttributeNodeImpl(){
    }

    protected AttributeNodeImpl(String attribute){
        this.currentAttribute = attribute;
    }

    @Override
    public String getAttributeName() {
        return currentAttribute;
    }

    @Override
    public Map<Class, Subgraph> getSubgraphs() {
        if (this.subgraphs == null){
            this.subgraphs = new HashMap<Class, Subgraph>();
        }
        return this.subgraphs;
    }

    @Override
    public Map<Class, Subgraph> getKeySubgraphs() {
        if (this.keySubgraphs == null){
            this.keySubgraphs = new HashMap<Class, Subgraph>();
        }
        return this.keySubgraphs;
    }

    public void addSubgraph(EntityGraphImpl entityGraphImpl) {
        if (this.subgraphs == null){
            this.subgraphs = new HashMap<Class, Subgraph>();
        }
        this.subgraphs.put(entityGraphImpl.getClassType(), entityGraphImpl);
    }

    public void addKeySubgraph(EntityGraphImpl entityGraphImpl) {
        if (this.keySubgraphs == null){
            this.keySubgraphs = new HashMap<Class, Subgraph>();
        }
        this.keySubgraphs.put(entityGraphImpl.getClassType(), entityGraphImpl);
    }
}
