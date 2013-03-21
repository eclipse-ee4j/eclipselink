/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb;

import java.util.List;

/**
 * This type represents the root of an object graph that will be used
 * as a template to define the attribute nodes and boundaries of a
 * graph of JAXB objects and relationships. The root must be an
 * root-level JAXB type.
 *
 * @param <T> The type of the root entity.
 *
 * @see XmlNamedObjectGraph
 *
 * @since EclipseLink 2.5
 */
public interface ObjectGraph {
    /**
     * Returns the name of the static EntityGraph.  Will return null if the
     * EntityGraph is not a named EntityGraph.
     */
    public String getName();

    /*
     * Add an AttributeNode attribute to the entity graph.
     *
     * @throws IllegalArgumentException if the attribute is not an attribute of
     * this entity.
     * @throws IllegalStateException if this EntityGraph has been statically
     * defined
     */
    public void addAttributeNodes(String ... attributeName);


    /*
     * Used to add a node of the graph that corresponds to a managed type. This
     * allows for construction of multi-node Entity graphs that include related
     * managed types.
     *
     * @throws IllegalArgumentException if the attribute is not an attribute of
     * this entity.
     * @throws IllegalArgumentException if the attribute's target type is not a
     * managed type
     *
     * @throws IllegalStateException if this EntityGraph has been statically
     * defined
     */
    public Subgraph addSubgraph(String attribute);

    /**
     * Used to add a node of the graph that corresponds to a managed type with
     * inheritance.  This allows for multiple subclass sub-graphs to be defined
     * for this node of the entity graph.  Subclass sub-graphs will include the
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

    /*
     * returns the attributes of this entity that are included in the entity
     * graph
     */
    public List<AttributeNode> getAttributeNodes();

}

