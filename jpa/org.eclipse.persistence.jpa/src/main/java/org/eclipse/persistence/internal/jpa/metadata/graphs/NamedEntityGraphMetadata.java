/*
 * Copyright (c) 2013, 2026 Oracle and/or its affiliates. All rights reserved.
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
//     02/13/2013-2.5 Guy Pelletier
//       - 397772: JPA 2.1 Entity Graph Support (XML support)
package org.eclipse.persistence.internal.jpa.metadata.graphs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jakarta.persistence.FetchType;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.queries.AttributeItem;
import org.eclipse.persistence.queries.AttributeGroup;

/**
 * Object to hold onto named entity graph metadata.
 * <p>
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
    // Runtime declaration identity is separate from the XML-mapped state.
    private EntityAccessor entityAccessor;
    private String resolvedName;
    protected Boolean m_includeAllAttributes;

    protected List<NamedAttributeNodeMetadata> m_namedAttributeNodes = new ArrayList<>();
    protected List<NamedSubgraphMetadata> m_subgraphs = new ArrayList<>();
    protected List<NamedSubgraphMetadata> m_subclassSubgraphs = new ArrayList<>();

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

        m_name = namedEntityGraph.getAttributeString("name");
        m_includeAllAttributes = namedEntityGraph.getAttributeBooleanDefaultFalse("includeAllAttributes");

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

    private AttributeGroup buildGraph(Map<NamedEntityGraphMetadata, Map<FetchMetadata, List<NamedEntityGraphMetadata>>> links,
            Map<NamedEntityGraphMetadata, AttributeGroup> completed, Set<NamedEntityGraphMetadata> active) {
        if (completed.containsKey(this)) {
            return completed.get(this);
        }
        if (!active.add(this)) {
            throw invalidGraph("cyclic subgraph reference");
        }
        AttributeGroup graph = new AttributeGroup(resolvedName, entityAccessor.getJavaClassName(), true);
        Map<String, Map<String, AttributeGroup>> nested = new HashMap<>();
        for (NamedSubgraphMetadata subgraph : getSubgraphs()) {
            subgraph.process(nested);
        }
        if (includeAllAttributes()) {
            for (MappingAccessor mapping : getEffectiveMappings().values()) {
                if (!mapping.isTransient()) {
                    graph.addAttribute(mapping.getAttributeName());
                }
            }
        }
        for (NamedAttributeNodeMetadata node : getNamedAttributeNodes()) {
            node.process(nested, graph, graph);
        }
        for (NamedSubgraphMetadata subgraph : getSubgraphs()) {
            subgraph.processAttributeNodes(nested, nested.get(subgraph.getName()).get(subgraph.getTypeClassName()), graph);
        }
        for (NamedSubgraphMetadata subclass : getSubclassSubgraphs()) {
            AttributeGroup group = new AttributeGroup(subclass.getName(), subclass.getTypeClassName(), true);
            subclass.processAttributeNodes(nested, group, graph);
            graph.getSubClassGroups().put(group.getTypeName(), group);
        }
        for (Map.Entry<FetchMetadata, List<NamedEntityGraphMetadata>> entry : links.get(this).entrySet()) {
            FetchMetadata fetch = entry.getKey();
            String attribute = fetch.getMapping().getAttributeName();
            Map<Class<?>, Object> previous = graph.getAttributeOptions().get(attribute);
            if (previous != null && !previous.equals(fetch.getOptions())) {
                throw invalidGraph("conflicting Fetch options for attribute " + attribute);
            }
            graph.setAttributeOptions(attribute, fetch.getOptions());
            // Resolve even lazy links to reject invalid references and cycles deterministically.
            for (NamedEntityGraphMetadata target : entry.getValue()) {
                AttributeGroup child = target.buildGraph(links, completed, active);
                if (!fetch.isLazy()) {
                    graph.addAttribute(attribute);
                    mergeSubgraph(graph, attribute, child.clone(), fetch.getMapping().getReferenceClassName(), false,
                            Collections.newSetFromMap(new IdentityHashMap<>()));
                }
            }
            if (fetch.isLazy()) {
                graph.removeAttribute(attribute);
            } else {
                graph.addAttribute(attribute);
            }
        }
        active.remove(this);
        completed.put(this, graph);
        return graph;
    }

    private static void collectDescendants(NamedEntityGraphMetadata declaration,
            Map<NamedEntityGraphMetadata, Map<FetchMetadata, List<NamedEntityGraphMetadata>>> links,
            Set<NamedEntityGraphMetadata> descendants) {
        for (List<NamedEntityGraphMetadata> targets : links.get(declaration).values()) {
            for (NamedEntityGraphMetadata target : targets) {
                if (descendants.add(target)) {
                    collectDescendants(target, links, descendants);
                }
            }
        }
    }

    private List<FetchMetadata> collectFetches() {
        List<FetchMetadata> fetches = new ArrayList<>();
        for (MappingAccessor mapping : getEffectiveMappings().values()) {
            // XML replaces graph membership. FetchMetadata checks annotation eligibility
            // on each mapping's declaring type, including inherited mappings.
            if (mapping.isTransient() || (loadedFromXML() && !includeAllAttributes()
                    && getNamedAttributeNodes().stream().noneMatch(node -> node.getName().equals(mapping.getAttributeName())))) {
                continue;
            }
            for (FetchMetadata fetch : FetchMetadata.collect(mapping)) {
                if (!fetch.getGraph().isEmpty() && resolvedName.equals(fetch.getGraph())) {
                    fetches.add(fetch);
                }
            }
        }
        return fetches;
    }

    /**
     * INTERNAL:
     * Used for XML merging and overriding.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof NamedEntityGraphMetadata entityGraph) {

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

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (m_name != null ? m_name.hashCode() : 0);
        result = 31 * result + (m_includeAllAttributes != null ? m_includeAllAttributes.hashCode() : 0);
        result = 31 * result + (m_namedAttributeNodes != null ? m_namedAttributeNodes.hashCode() : 0);
        result = 31 * result + (m_subgraphs != null ? m_subgraphs.hashCode() : 0);
        result = 31 * result + (m_subclassSubgraphs != null ? m_subclassSubgraphs.hashCode() : 0);
        return result;
    }

    private Map<String, MappingAccessor> getEffectiveMappings() {
        Map<String, MappingAccessor> mappings = new TreeMap<>();
        MetadataDescriptor descriptor = entityAccessor.getDescriptor();
        while (descriptor != null) {
            for (MappingAccessor mapping : descriptor.getMappingAccessors()) {
                mappings.putIfAbsent(mapping.getAttributeName(), mapping);
            }
            descriptor = descriptor.isInheritanceSubclass() ? descriptor.getInheritanceParentDescriptor() : null;
        }
        return mappings;
    }

    /** INTERNAL: Preserve the declaring entity for canonical metadata retrieval. */
    public EntityAccessor getEntityAccessor() {
        return entityAccessor;
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

    private String getReferenceType(String ownerType, String attribute, boolean key) {
        ClassAccessor accessor = getProject().getAccessor(ownerType);
        if (accessor == null && getProject().getCompositeProcessor() != null) {
            for (MetadataProject peer : getProject().getCompositeProcessor().getPearProjects(getProject())) {
                accessor = peer.getAccessor(ownerType);
                if (accessor != null) {
                    break;
                }
            }
        }
        MappingAccessor mapping = accessor == null ? null : accessor.getDescriptor().getMappingAccessor(attribute);
        if (mapping == null) {
            throw invalidGraph("no persistent attribute " + attribute + " on subgraph type " + ownerType);
        }
        return key ? mapping.getMapKeyReferenceClassName() : mapping.getReferenceClassName();
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
       return getName() != null && !getName().isEmpty();
    }

    /** INTERNAL: Bounded provider validation for undefined or invalid graph definitions. */
    public IllegalStateException invalidGraph(String reason) {
        return new IllegalStateException(ExceptionLocalization.buildMessage("invalid_named_entity_graph",
                new Object[]{resolvedName, entityAccessor.getJavaClassName(), reason}));
    }

    private void mergeGraph(AttributeGroup target, AttributeGroup source, String ownerType, Set<AttributeGroup> merging) {
        if (!merging.add(source)) {
            throw invalidGraph("cyclic nested subgraph composition");
        }
        for (Map.Entry<String, Map<Class<?>, Object>> options : source.getAttributeOptions().entrySet()) {
            Map<Class<?>, Object> previous = target.getAttributeOptions().get(options.getKey());
            if (previous != null && !previous.equals(options.getValue())) {
                throw invalidGraph("conflicting subgraph options for attribute " + options.getKey());
            }
            target.setAttributeOptions(options.getKey(), options.getValue());
        }
        for (AttributeItem item : source.getItems().values()) {
            target.addAttribute(item.getAttributeName());
            if (item.getGroups() != null) {
                String referenceType = getReferenceType(ownerType, item.getAttributeName(), false);
                for (AttributeGroup group : item.getGroups().values()) {
                    mergeSubgraph(target, item.getAttributeName(), group, referenceType, false, merging);
                }
            }
            if (item.getKeyGroups() != null) {
                String referenceType = getReferenceType(ownerType, item.getAttributeName(), true);
                for (AttributeGroup group : item.getKeyGroups().values()) {
                    mergeSubgraph(target, item.getAttributeName(), group, referenceType, true, merging);
                }
            }
        }
        target.getAttributeOptions().forEach((attribute, options) -> {
            if (options.get(FetchType.class) == FetchType.LAZY) {
                target.removeAttribute(attribute);
            }
        });
        source.getSubClassGroups().forEach((type, group) -> {
            AttributeGroup existing = (AttributeGroup) target.getSubClassGroups().get(type);
            if (existing == null) {
                target.getSubClassGroups().put(type, group);
            } else {
                AttributeGroup detached = existing.clone();
                mergeGraph(detached, (AttributeGroup) group, group.getTypeName(), merging);
                target.getSubClassGroups().put(type, detached);
            }
        });
        merging.remove(source);
    }

    private void mergeSubgraph(AttributeGroup parent, String attribute, AttributeGroup source, String referenceType,
            boolean key, Set<AttributeGroup> merging) {
        AttributeItem item = parent.getItem(attribute);
        Map<Object, AttributeGroup> groups = key ? item.getKeyGroups() : item.getGroups();
        if (groups != null) {
            String sourceType = "java.lang.Object".equals(source.getTypeName()) ? referenceType : source.getTypeName();
            for (AttributeGroup existing : groups.values()) {
                String existingType = "java.lang.Object".equals(existing.getTypeName()) ? referenceType : existing.getTypeName();
                if (sourceType.equals(existingType)) {
                    // A local label may be shared by sibling attributes, or by key and
                    // value branches. Replace only this attachment before changing it.
                    AttributeGroup detached = existing.clone();
                    mergeGraph(detached, source, sourceType, merging);
                    AttributeItem replacement = new AttributeItem(parent, attribute);
                    if (item.getGroups() != null) {
                        for (AttributeGroup group : item.getGroups().values()) {
                            replacement.addSubGroup(!key && group == existing ? detached : group);
                        }
                    }
                    if (item.getKeyGroups() != null) {
                        for (AttributeGroup group : item.getKeyGroups().values()) {
                            replacement.addKeyGroup(key && group == existing ? detached : group);
                        }
                    }
                    parent.getItems().put(attribute, replacement);
                    return;
                }
            }
        }
        if (key) {
            item.addKeyGroup(source);
        } else {
            item.addSubGroup(source);
        }
    }

    /**
     * INTERNAL:
     * Collect declarations during both runtime processing and canonical preprocessing.
     * Runtime assembly is deferred until the whole persistence unit is processed.
     */
    public void process(EntityAccessor accessor) {
        entityAccessor = accessor;
        resolvedName = hasName() ? getName() : accessor.getEntityName();
        if (!hasName()) {
            getLogger().logConfigMessage(MetadataLogger.NAMED_ENTITY_GRAPH_NAME, resolvedName, accessor.getJavaClassName());
        }
        getProject().addNamedEntityGraph(resolvedName, this);
    }

    /** INTERNAL: Resolve privately, then return only the unique public roots. */
    public static Map<NamedEntityGraphMetadata, AttributeGroup> resolve(
            Map<String, List<NamedEntityGraphMetadata>> declarations) {
        List<NamedEntityGraphMetadata> ordered = declarations.values().stream().flatMap(List::stream)
                .sorted(Comparator.comparing((NamedEntityGraphMetadata graph) -> graph.resolvedName)
                        .thenComparing(graph -> graph.entityAccessor.getJavaClassName()))
                .toList();
        Map<NamedEntityGraphMetadata, Map<FetchMetadata, List<NamedEntityGraphMetadata>>> links = new IdentityHashMap<>();
        for (NamedEntityGraphMetadata declaration : ordered) {
            Map<FetchMetadata, List<NamedEntityGraphMetadata>> attributes = new LinkedHashMap<>();
            for (FetchMetadata fetch : declaration.collectFetches()) {
                List<NamedEntityGraphMetadata> targets = new ArrayList<>();
                for (String name : fetch.getSubgraphs()) {
                    MappingAccessor mapping = fetch.getMapping();
                    if (!mapping.isRelationship()) {
                        throw declaration.invalidGraph("attribute " + mapping.getAttributeName() + " is not an association for subgraph " + name);
                    }
                    List<NamedEntityGraphMetadata> matches = declarations.getOrDefault(name, List.of()).stream()
                            .filter(target -> target.entityAccessor.getJavaClass().extendsClass(mapping.getReferenceClassName()))
                            .sorted(Comparator.comparing(target -> target.entityAccessor.getJavaClassName())).toList();
                    if (matches.isEmpty()) {
                        throw declaration.invalidGraph("attribute " + mapping.getAttributeName() + " has no subgraph " + name
                                + " compatible with " + mapping.getReferenceClassName());
                    }
                    targets.addAll(matches);
                }
                attributes.put(fetch, targets);
            }
            links.put(declaration, attributes);
        }
        Map<NamedEntityGraphMetadata, AttributeGroup> completed = new IdentityHashMap<>();
        Set<NamedEntityGraphMetadata> active = Collections.newSetFromMap(new IdentityHashMap<>());
        for (NamedEntityGraphMetadata declaration : ordered) {
            declaration.buildGraph(links, completed, active);
        }
        Map<NamedEntityGraphMetadata, AttributeGroup> roots = new IdentityHashMap<>();
        for (List<NamedEntityGraphMetadata> sameName : new TreeMap<>(declarations).values()) {
            List<NamedEntityGraphMetadata> candidates = new ArrayList<>(sameName);
            for (NamedEntityGraphMetadata declaration : sameName) {
                Set<NamedEntityGraphMetadata> descendants = Collections.newSetFromMap(new IdentityHashMap<>());
                collectDescendants(declaration, links, descendants);
                candidates.removeIf(descendants::contains);
            }
            if (candidates.size() != 1) {
                throw sameName.get(0).invalidGraph("the declarations do not have a unique root entity");
            }
            NamedEntityGraphMetadata root = candidates.get(0);
            Set<NamedEntityGraphMetadata> descendants = Collections.newSetFromMap(new IdentityHashMap<>());
            collectDescendants(root, links, descendants);
            if (sameName.stream().anyMatch(graph -> graph != root && !descendants.contains(graph))) {
                throw root.invalidGraph("not every declaration is reachable from the root entity");
            }
            roots.put(root, completed.get(root));
        }
        return roots;
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
