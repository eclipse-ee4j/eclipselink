/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     02/13/2013-2.5 Guy Pelletier
//       - 397772: JPA 2.1 Entity Graph Support (XML support)
package org.eclipse.persistence.internal.jpa.metadata.graphs;

import java.util.Map;

import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.queries.AttributeGroup;

/**
 * Object to hold onto named attribute node metadata.
 *
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - any metadata mapped from XML to this class must be initialized in the
 *   initXMLObject method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5
 */
public class NamedAttributeNodeMetadata extends ORMetadata {
    protected String m_name;
    protected String m_subgraph;
    protected String m_keySubgraph;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public NamedAttributeNodeMetadata() {
        super("<named-entity-graph>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public NamedAttributeNodeMetadata(MetadataAnnotation namedAttributeNode, ClassAccessor accessor) {
        super(namedAttributeNode, accessor);

        m_name = namedAttributeNode.getAttributeString("value");
        m_subgraph = namedAttributeNode.getAttributeString("subgraph");
        m_keySubgraph = namedAttributeNode.getAttributeString("key-subgraph");
    }

    /**
     * INTERNAL:
     * Used for XML merging and overriding.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof NamedAttributeNodeMetadata) {
            NamedAttributeNodeMetadata attributeNode = (NamedAttributeNodeMetadata) objectToCompare;

            if (! valuesMatch(m_name, attributeNode.getName())) {
                return false;
            }

            if (! valuesMatch(m_subgraph, attributeNode.getSubgraph())) {
                return false;
            }

            return valuesMatch(m_keySubgraph, attributeNode.getKeySubgraph());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = m_name != null ? m_name.hashCode() : 0;
        result = 31 * result + (m_subgraph != null ? m_subgraph.hashCode() : 0);
        result = 31 * result + (m_keySubgraph != null ? m_keySubgraph.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     * The unique identifier of named attribute mode metadata.
     */
    @Override
    public String getIdentifier() {
        return getName();
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getKeySubgraph() {
        return m_keySubgraph;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getName() {
        return m_name;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getSubgraph() {
        return m_subgraph;
    }

    /**
     * INTERNAL:
     * Process the named attribute node metadata.
     */
    public void process(Map<String, Map<String, AttributeGroup>> attributeGraphs, AttributeGroup graph, AttributeGroup rootGraph) {
        // Process the subgraph.
        if (getSubgraph() != null) {
            if (attributeGraphs.containsKey(getSubgraph())) {
                graph.addAttribute(getName(), attributeGraphs.get(getSubgraph()).values());
            } else {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("managed_component_not_found", new Object[]{graph.getName(), getName(), getSubgraph()}));
            }
        }else {
            graph.addAttribute(getName());
        }

        if (getKeySubgraph() != null) {
            if (attributeGraphs.containsKey(getKeySubgraph())) {
                graph.getItem(getName()).addKeyGroups(attributeGraphs.get(getKeySubgraph()).values());
            } else {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("managed_component_not_found", new Object[]{graph.getName(), getName(), getKeySubgraph()}));
            }
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setKeySubgraph(String keySubgraph) {
        m_keySubgraph = keySubgraph;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setName(String name) {
        m_name = name;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setSubgraph(String subgraph) {
        m_subgraph = subgraph;
    }
}
