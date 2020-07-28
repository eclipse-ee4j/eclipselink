/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.internal.jaxb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.core.queries.CoreAttributeGroup;
import org.eclipse.persistence.jaxb.AttributeNode;
import org.eclipse.persistence.jaxb.ObjectGraph;
import org.eclipse.persistence.jaxb.Subgraph;

public class ObjectGraphImpl extends AttributeNodeImpl implements ObjectGraph, Subgraph {

    private CoreAttributeGroup attributeGroup;
    private Map<String, AttributeNode> attributeNodes;


    public ObjectGraphImpl(CoreAttributeGroup group) {
        super();
        this.attributeGroup = group;
        this.attributeNodes = new HashMap<String, AttributeNode>();
    }
    public Class getClassType() {
        return attributeGroup.getType();
    }

    public String getName() {
        return attributeGroup.getName();
    }

    public void addAttributeNodes(String... attributeName) {
        for(String attribute:attributeName) {
            AttributeNodeImpl impl = new AttributeNodeImpl(attribute);
            this.attributeNodes.put(attribute, impl);
            attributeGroup.addAttribute(attribute);
        }
    }

    public Subgraph addSubgraph(String attribute) {
        CoreAttributeGroup group = new CoreAttributeGroup();
        if(attributeGroup.getItem(attribute) == null) {
            AttributeNodeImpl impl = new AttributeNodeImpl(attribute);
            this.attributeNodes.put(attribute,  impl);
        }
        this.attributeGroup.addAttribute(attribute, group);

        return new ObjectGraphImpl(group);
    }

    public Subgraph addSubgraph(String attribute, Class type) {
        CoreAttributeGroup group = new CoreAttributeGroup(null, type, true);
        if(attributeGroup.getItem(attribute) == null) {
            AttributeNodeImpl impl = new AttributeNodeImpl(attribute);
            this.attributeNodes.put(attribute,  impl);
        }
        this.attributeGroup.addAttribute(attribute, group);

        return new ObjectGraphImpl(group);
    }

    public List<AttributeNode> getAttributeNodes() {
        ArrayList<AttributeNode> nodes = new ArrayList<AttributeNode>();
        for(AttributeNode next:this.attributeNodes.values()) {
            nodes.add(next);
        }
        return nodes;
    }

    public CoreAttributeGroup getAttributeGroup() {
        return this.attributeGroup;
    }

}
