/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     02/13/2013-2.5 Guy Pelletier 
 *       - 397772: JPA 2.1 Entity Graph Support (XML support)
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.queries.AttributeGroup;

/**
 * Object to hold onto named entity graph metadata.
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
public class NamedEntityGraphMetadata extends ORMetadata {
    protected String m_name;
    protected Boolean m_includeAllAttributes;
    
    protected List<NamedAttributeNodeMetadata> m_namedAttributeNodes = new ArrayList<NamedAttributeNodeMetadata>();
    protected List<NamedSubgraphMetadata> m_subgraphs = new ArrayList<NamedSubgraphMetadata>();
    protected List<NamedSubgraphMetadata> m_subclassSubgraphs = new ArrayList<NamedSubgraphMetadata>();
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public NamedEntityGraphMetadata() {
        super("<named-entity-graph>");
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public NamedEntityGraphMetadata(MetadataAnnotation namedEntityGraph, EntityAccessor accessor) {
        super(namedEntityGraph, accessor);
        
        m_name = (String) namedEntityGraph.getAttribute("name");
        m_includeAllAttributes = (Boolean) namedEntityGraph.getAttributeBooleanDefaultFalse("include-all-attributes");

        for (Object namedAttributeNode : namedEntityGraph.getAttributeArray("attributeNodes")) {
            m_namedAttributeNodes.add(new NamedAttributeNodeMetadata((MetadataAnnotation) namedAttributeNode, accessor));
        }
        
        for (Object subgraph : namedEntityGraph.getAttributeArray("subgraphs")) {
            m_subgraphs.add(new NamedSubgraphMetadata((MetadataAnnotation) subgraph, accessor));
        }
        
        for (Object subclassSubgraph : namedEntityGraph.getAttributeArray("subclassSubgraphs")) {
            m_subclassSubgraphs.add(new NamedSubgraphMetadata((MetadataAnnotation) subclassSubgraph, accessor));
        }
    }
    
    /**
     * INTERNAL:
     * Used for XML merging and overriding.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof NamedEntityGraphMetadata) {
            NamedEntityGraphMetadata entityGraph = (NamedEntityGraphMetadata) objectToCompare;
            
            if (! valuesMatch(m_name, entityGraph.getName())) {
                return false;
            }
            
            if (! valuesMatch(m_includeAllAttributes, entityGraph.getIncludeAllAttributes())) {
                return false;
            }
            
            if (! valuesMatch(m_namedAttributeNodes, entityGraph.getNamedAttributeNodes())) {
                return false;
            }
            
            if (! valuesMatch(m_subgraphs, entityGraph.getSubgraphs())) {
                return false;
            }
            
            return valuesMatch(m_subclassSubgraphs, entityGraph.getSubclassSubgraphs());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getIncludeAllAttributes() {
        return m_includeAllAttributes;
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
     * Used for OX mapping.
     */
    public List<NamedSubgraphMetadata> getSubclassSubgraphs() {
        return m_subclassSubgraphs;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<NamedSubgraphMetadata> getSubgraphs() {
        return m_subgraphs;
    }
    
    /**
     * INTERNAL:
     */
    public boolean includeAllAttributes() {
        return m_includeAllAttributes != null && m_includeAllAttributes;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        // Initialize lists of ORMetadata objects.
        initXMLObjects(m_namedAttributeNodes, accessibleObject);
        initXMLObjects(m_subgraphs, accessibleObject);
        initXMLObjects(m_subclassSubgraphs, accessibleObject);
    }
    
    /**
     * INTERNAL:
     * Return true is a name was provided through metadata. 
     */
    protected boolean hasName() {
       return getName() != null && ! getName().equals("");
    }
    
    /**
     * INTERNAL:
     * Process the entity graph metadata.
     */
    public void process(EntityAccessor entityAccessor) {
        String entityGraphName;
        
        if (hasName()) {
            entityGraphName = getName();
        } else {
            entityGraphName = entityAccessor.getEntityName();
            getLogger().logConfigMessage(MetadataLogger.NAMED_ENTITY_GRAPH_NAME, entityGraphName, entityAccessor.getJavaClassName());
        }
        
        // Check for an existing entity graph and throw an exception if there is one.
        if (getProject().hasEntityGraph(entityGraphName)) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("named_entity_graph_exists", new Object[]{ entityGraphName, entityAccessor.getJavaClassName()}));
        } else {
            AttributeGroup entityGraph = new AttributeGroup(entityGraphName, entityAccessor.getJavaClassName());
            Map<String, Map<String, AttributeGroup>> attributeGraphs = new HashMap<String, Map<String, AttributeGroup>>();
        
            // Process the subgraph metadata (build attribute graphs for each).
            for (NamedSubgraphMetadata subgraph : getSubgraphs()) {
                subgraph.process(attributeGraphs);
            }
        
            // Process the include all attributes flag.
            if (includeAllAttributes()) {
                for (MappingAccessor accessor : entityAccessor.getDescriptor().getMappingAccessors()) {
                    entityGraph.addAttribute(accessor.getAttributeName());
                }
            }
        
            // Process the attribute nodes.
            for (NamedAttributeNodeMetadata attributeNode : getNamedAttributeNodes()) {
                attributeNode.process(attributeGraphs, entityGraph, entityGraph);
            }

            // Process the subgraphs attribute nodes (into the groups built previously).
            for (NamedSubgraphMetadata subgraph : getSubgraphs()) {
                subgraph.processAttributeNodes(attributeGraphs, entityGraph);
            }
    
            // Finally, add the entity graph to the project.
            getProject().addEntityGraph(entityGraph);
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setIncludeAllAttributes(Boolean includeAllAttributes) {
        m_includeAllAttributes = includeAllAttributes;
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
    public void setNamedAttributeNodes(List<NamedAttributeNodeMetadata> attributeNodes) {
        m_namedAttributeNodes = attributeNodes;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setSubclassSubgraphs(List<NamedSubgraphMetadata> subclassSubgraphs) {
        m_subclassSubgraphs = subclassSubgraphs;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setSubgraphs(List<NamedSubgraphMetadata> subgraphs) {
        m_subgraphs = subgraphs;
    }
}
