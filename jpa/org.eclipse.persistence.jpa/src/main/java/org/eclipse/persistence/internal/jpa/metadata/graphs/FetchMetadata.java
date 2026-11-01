/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.internal.jpa.metadata.graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.BatchFetch;
import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.FetchType;

import org.eclipse.persistence.internal.jpa.metadata.MetadataConstants;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;

/** INTERNAL: One fetch declaration on an effective persistent attribute. */
public final class FetchMetadata {
    private final MappingAccessor mapping;
    private final String graph;
    private final List<String> subgraphs;
    private final Map<Class<?>, Object> options;

    private FetchMetadata(MetadataAnnotation annotation, MappingAccessor mapping) {
        this.mapping = mapping;
        graph = annotation.getAttributeString("graph", "");
        List<String> names = new ArrayList<>();
        for (Object name : annotation.getAttributeArray("subgraph")) {
            names.add((String) name);
        }
        subgraphs = List.copyOf(names);
        Map<Class<?>, Object> values = new HashMap<>();
        values.put(FetchType.class, FetchType.valueOf(annotation.getAttributeString("type", "EAGER")));
        values.put(CacheRetrieveMode.class, CacheRetrieveMode.valueOf(annotation.getAttributeString("cacheRetrieveMode", "USE")));
        values.put(CacheStoreMode.class, CacheStoreMode.valueOf(annotation.getAttributeString("cacheStoreMode", "USE")));
        Integer batchSize = annotation.getAttributeInteger("batchSize");
        if (batchSize != null && batchSize >= 0) {
            values.put(BatchFetch.class, new BatchFetch(batchSize));
        }
        options = Map.copyOf(values);
    }

    /** Read through the owning accessor so metadata-complete remains effective. */
    public static List<FetchMetadata> collect(MappingAccessor mapping) {
        List<FetchMetadata> declarations = new ArrayList<>();
        if (!mapping.isTransient()) {
            MetadataAnnotation fetch = mapping.getAccessibleObject().getAnnotation(
                    MetadataConstants.JPA_FETCH, mapping.getClassAccessor());
            if (fetch != null) {
                declarations.add(new FetchMetadata(fetch, mapping));
            }
            MetadataAnnotation fetches = mapping.getAccessibleObject().getAnnotation(
                    MetadataConstants.JPA_FETCHES, mapping.getClassAccessor());
            if (fetches != null) {
                for (Object annotation : fetches.getAttributeArray("value")) {
                    declarations.add(new FetchMetadata((MetadataAnnotation) annotation, mapping));
                }
            }
        }
        return declarations;
    }

    /** Return the type override for the default fetch plan, excluding named graphs. */
    public static FetchType getUnqualifiedFetchType(MappingAccessor mapping) {
        FetchType type = null;
        for (FetchMetadata declaration : collect(mapping)) {
            if (declaration.graph.isEmpty()) {
                if (type != null) {
                    throw new IllegalStateException(ExceptionLocalization.buildMessage(
                            "multiple_unqualified_fetch_annotations",
                            new Object[] {mapping.getClassAccessor().getJavaClassName(), mapping.getAttributeName()}));
                }
                type = (FetchType) declaration.options.get(FetchType.class);
            }
        }
        return type;
    }

    public String getGraph() {
        return graph;
    }

    public MappingAccessor getMapping() {
        return mapping;
    }

    public Map<Class<?>, Object> getOptions() {
        return options;
    }

    public List<String> getSubgraphs() {
        return subgraphs;
    }

    public boolean isLazy() {
        return options.get(FetchType.class) == FetchType.LAZY;
    }
}
