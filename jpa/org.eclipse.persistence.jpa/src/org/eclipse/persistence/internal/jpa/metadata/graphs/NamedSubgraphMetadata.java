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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.queries.AttributeGroup;

/**
 * Object to hold onto named sub graph metadata.
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
public class NamedSubgraphMetadata extends ORMetadata {
    protected String m_name;

    protected MetadataClass m_type;
    protected String m_typeName;

    protected List<NamedAttributeNodeMetadata> m_namedAttributeNodes = new ArrayList<NamedAttributeNodeMetadata>();

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public NamedSubgraphMetadata() {
        super("<named-subgraph>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public NamedSubgraphMetadata(MetadataAnnotation namedSubgraph, ClassAccessor accessor) {
        super(namedSubgraph, accessor);

        m_name = namedSubgraph.getAttributeString("name");
        m_type = getMetadataClass(namedSubgraph.getAttributeClass("type", ClassConstants.Object_Class));

        for (Object attributeNode : namedSubgraph.getAttributeArray("attributeNodes")) {
            m_namedAttributeNodes.add(new NamedAttributeNodeMetadata((MetadataAnnotation) attributeNode, accessor));
        }
    }

    /**
     * INTERNAL:
     * Used for XML merging and overriding.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof NamedSubgraphMetadata) {
            NamedSubgraphMetadata namedSubgraph = (NamedSubgraphMetadata) objectToCompare;

            if (! valuesMatch(m_name, namedSubgraph.getName())) {
                return false;
            }

            if (! valuesMatch(m_typeName, namedSubgraph.getTypeName())) {
                return false;
            }

            return valuesMatch(m_namedAttributeNodes, namedSubgraph.getNamedAttributeNodes());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = m_name != null ? m_name.hashCode() : 0;
        result = 31 * result + (m_typeName != null ? m_typeName.hashCode() : 0);
        result = 31 * result + (m_namedAttributeNodes != null ? m_namedAttributeNodes.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     * The unique identifier of named subgraph metadata.
     */
    @Override
    public String getIdentifier() {
        return getName();
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
    public List<NamedAttributeNodeMetadata> getNamedAttributeNodes() {
        return m_namedAttributeNodes;
    }

    /**
     * INTERNAL:
     */
    public String getTypeClassName() {
        return m_type.getName();
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getTypeName() {
        return m_typeName;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);

        // Initialize lists of ORMetadata objects.
        initXMLObjects(m_namedAttributeNodes, accessibleObject);

        // Initialize simple class objects.
        if (m_typeName != null && ! m_typeName.equals("")) {
            m_type = initXMLClassName(m_typeName);
        } else {
            m_type = getMetadataClass(ClassConstants.Object_Class);
        }
    }

    /**
     * INTERNAL:
     * Process the named subgraph metadata into a new attribute group.
     */
    public void process(Map<String, Map<String, AttributeGroup>> attributeGraphs) {
        AttributeGroup attributeGraph = new AttributeGroup(getName(), getTypeClassName(), true);

        if (! attributeGraphs.containsKey(getName())) {
            attributeGraphs.put(getName(), new HashMap<String, AttributeGroup>());
        }

        attributeGraphs.get(getName()).put(getTypeClassName(), attributeGraph);
    }

    /**
     * INTERNAL:
     * Process the named subgraph metadata attribute nodes.
     */
    public void processAttributeNodes(Map<String, Map<String, AttributeGroup>> attributeGraphs, AttributeGroup subgraph, AttributeGroup entityGraph) {

        for (NamedAttributeNodeMetadata attributeNode : getNamedAttributeNodes()) {
            attributeNode.process(attributeGraphs, subgraph, entityGraph);
        }
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
    public void setNamedAttributeNodes(List<NamedAttributeNodeMetadata> namedAttributeNodes) {
        m_namedAttributeNodes = namedAttributeNodes;
    }

    /**
     * INTERNAL:
     */
    public void setType(MetadataClass type) {
        m_type = type;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setTypeName(String typeName) {
        m_typeName = typeName;
    }
}
