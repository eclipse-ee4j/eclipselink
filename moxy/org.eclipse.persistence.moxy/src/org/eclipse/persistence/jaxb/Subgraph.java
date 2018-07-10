/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Matt MacIvor - 2.5 - initial implementation
package org.eclipse.persistence.jaxb;

import java.util.List;

/**
 * This type represents a AttributeNode of an EntityGraph that corresponds to a
 * Managed Type. Using this class an entity graph can be embedded within an
 * EntityGraph.
 */

public interface Subgraph extends AttributeNode {

    /**
     * Add an AttributeNode attribute to the entity graph.
     *
     * @throws IllegalArgumentException if the attribute is not an attribute of
     * this managed type.
     * @throws IllegalStateException
     *             if this EntityGraph has been statically defined
     */
    public void  addAttributeNodes(String ... attributeName);

    /**
     * Used to add a node of the graph that corresponds to a managed type. This
     * allows for construction of multi-node Entity graphs that include related
     * managed types.
     *
     * @throws IllegalArgumentException if the attribute is not an attribute of
     * this managed type.
     * @throws IllegalArgumentException
     *             if the attribute's target type is not a managed type
     * @throws IllegalStateException
     *             if this EntityGraph has been statically defined
     */
    public Subgraph addSubgraph(String attribute);

    /**
     * Used to add a node of the graph that corresponds to a managed type with
     * inheritance.  This allows for multiple subclass sub-graphs to be defined
     * for this node of the entity graph. Subclass sub-graphs will include the
     * specified attributes of superclass sub-graphs
     *
     * @throws IllegalArgumentException if the attribute is not an attribute of
     * this managed type.
     * @throws IllegalArgumentException
     *             if the attribute's target type is not a managed type
     * @throws IllegalStateException
     *             if this EntityGraph has been statically defined
     */
    public Subgraph addSubgraph(String attribute, Class type);



    /**
     * returns the attributes of this managed type that are included in the
     * sub-graph
     */
    public List<AttributeNode> getAttributeNodes();

    /**
     * returns the type of this sub-graph if it was used to extend a superclass
     * sub-graph definition.
     */
    public Class getClassType();
}
