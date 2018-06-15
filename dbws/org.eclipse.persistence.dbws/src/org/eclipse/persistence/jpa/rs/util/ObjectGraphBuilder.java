/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.jpa.rs.util;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedRest;
import org.eclipse.persistence.jaxb.ObjectGraph;
import org.eclipse.persistence.jaxb.Subgraph;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.features.fieldsfiltering.FieldsFilter;
import org.eclipse.persistence.jpa.rs.features.fieldsfiltering.FieldsFilterType;
import org.eclipse.persistence.jpa.rs.util.list.PageableCollection;
import org.eclipse.persistence.jpa.rs.util.list.ReadAllQueryResultCollection;
import org.eclipse.persistence.jpa.rs.util.list.ReportQueryResultCollection;
import org.eclipse.persistence.jpa.rs.util.list.SingleResultQueryResult;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

import javax.xml.bind.JAXBElement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds object graph for JPARS 2.0. Object graph defines the structure of the returned data. It hides some
 * fields which are not suppose to be returned to the client.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class ObjectGraphBuilder {
    private final PersistenceContext context;
    private final List<String> RESERVED_WORDS = Arrays.asList("hasMore", "limit", "offset", "links", "count", "items", "_persistence_links");

    /**
     * Creates an object graph builder.
     *
     * @param context the persistence context
     */
    public ObjectGraphBuilder(PersistenceContext context) {
        this.context = context;
    }

    /**
     * Builds object graph for specified object using given filter.
     *
     * @param object the object to build object graph for. Mandatory.
     * @param filter the filter (included or excluded fields) to use. Optional.
     * @return constructed object graph.
     */
    public ObjectGraph createObjectGraph(Object object, FieldsFilter filter) {
        final Node root = new Node();

        if (PersistenceWeavedRest.class.isAssignableFrom(object.getClass())) {
            createNodeForEntity(object, root);
        } else if (object instanceof SingleResultQueryResult) {
            root.addAttributeNode("links");

            final SingleResultQueryResult singleResultQueryResult = (SingleResultQueryResult)object;
            processFieldsList(root.addSubNode("fields"), singleResultQueryResult.getFields());
        } else if (object instanceof ReadAllQueryResultCollection) {
            createNodeForPageableCollection((PageableCollection<?>) object, root);
        } else {
            return null;
        }

        ObjectGraph objectGraph = context.getJAXBContext().createObjectGraph(object.getClass());
        fillObjectGraphFromNode(objectGraph, root, filter);

        return objectGraph;
    }

    private void createNodeForPageableCollection(PageableCollection<?> collection, Node node) {
        node.addAttributeNode("hasMore");
        node.addAttributeNode("count");
        node.addAttributeNode("offset");
        node.addAttributeNode("limit");
        node.addAttributeNode("links");
        if (collection.getItems() != null && !collection.getItems().isEmpty()) {
            final Node subNode = node.addSubNode("items");
            if (collection instanceof ReportQueryResultCollection) {
                final ReportQueryResultCollection reportQueryResultCollection = (ReportQueryResultCollection)collection;
                processFieldsList(subNode.addSubNode("fields"), reportQueryResultCollection.getItems().get(0).getFields());
            } else {
                createNodeForEntity(collection.getItems().get(0), subNode);
            }
        }
    }

    private void processFieldsList(Node fieldsNode, List<JAXBElement<?>> elements) {
        for (JAXBElement<?> field : elements) {
            if (field.getValue() instanceof PersistenceWeavedRest) {
                final Node subNode = fieldsNode.addSubNode(field.getName().toString());
                subNode.addAttributeNode("_persistence_links");
            } else {
                fieldsNode.addAttributeNode(field.getName().toString());
            }
        }
    }

    private void createNodeForEntity(final Object object, final Node node) {
        final ClassDescriptor classDescriptor = context.getServerSession().getProject().getDescriptors().get(object.getClass());
        if (classDescriptor == null) {
            return;
        }
        node.addAttributeNode("_persistence_links");

        for (final DatabaseMapping mapping : classDescriptor.getMappings()) {
            if (ForeignReferenceMapping.class.isAssignableFrom(mapping.getClass())) {
                final Node subNode = node.addSubNode(mapping.getAttributeName());
                if (mapping.isCollectionMapping()) {
                    // Add CollectionWrapper links and items properties
                    subNode.addAttributeNode("links");
                    final Node itemsSubNode = subNode.addSubNode("items");
                    itemsSubNode.addAttributeNode("_persistence_links");
                } else {
                    // PersistenceWeavedRest._persistence_links
                    subNode.addAttributeNode("_persistence_links");
                }
            } else {
                node.addAttributeNode(mapping.getAttributeName());
            }
        }
    }

    private void fillObjectGraphFromNode(ObjectGraph objectGraph, Node node, FieldsFilter filter) {
        for (final String attribute : node.getNodesMap().keySet()) {
            if (filter != null) {
                if (filter.getType() == FieldsFilterType.INCLUDE) {
                    if (!(RESERVED_WORDS.contains(attribute) || filter.getFields().contains(attribute))) {
                        continue;
                    }
                } else {
                    if (filter.getFields().contains(attribute)) {
                        continue;
                    }
                }
            }

            final Node value = node.get(attribute);
            if (value != null) {
                final Subgraph subgraph = objectGraph.addSubgraph(attribute);
                fillSubgraphFromNode(subgraph, value, filter);
            } else {
                objectGraph.addAttributeNodes(attribute);
            }
        }
    }

    private void fillSubgraphFromNode(Subgraph subgraph, Node node, FieldsFilter filter) {
        for (final String attribute : node.getNodesMap().keySet()) {
            final Node value = node.get(attribute);
            if (filter != null) {
                if (filter.getType() == FieldsFilterType.INCLUDE) {
                    if (!(RESERVED_WORDS.contains(attribute) || filter.getFields().contains(attribute))) {
                        continue;
                    }
                } else {
                    if (filter.getFields().contains(attribute)) {
                        continue;
                    }
                }
            }

            if (value != null) {
                final Subgraph childSubgraph = subgraph.addSubgraph(attribute);
                fillSubgraphFromNode(childSubgraph, value, filter);
            } else {
                subgraph.addAttributeNodes(attribute);
            }
        }
    }

    /**
     * Internal object graph node.
     */
    private static class Node {
        private final Map<String, Node> nodesMap = new HashMap<String, Node>();

        public void addAttributeNode(final String attribute) {
            nodesMap.put(attribute, null);
        }

        public Node addSubNode(final String attribute) {
            final Node subNode = new Node();
            nodesMap.put(attribute, subNode);
            return subNode;
        }

        public Map<String, Node> getNodesMap() {
            return nodesMap;
        }

        public Node get(final String key) {
            return nodesMap.get(key);
        }

        @Override
        public String toString() {
            return "Node{" +
                    "nodesMap=" + nodesMap +
                    '}';
        }
    }
}
