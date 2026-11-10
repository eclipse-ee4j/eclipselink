/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.namedgraphs.model;

import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.persistence.Entity;
import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.Fetch;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapKeyJoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.Table;

@Entity(name = "DngChild")
@Table(name = "JPA_DNG_CHILD")
@Inheritance(strategy = InheritanceType.JOINED)
@NamedEntityGraph(name = "DngChild")
@NamedEntityGraph(name = "DngChildLabels")
@NamedEntityGraph(name = "DngChildLazy")
@NamedEntityGraph(name = "DngSubclass", attributeNodes = @NamedAttributeNode("label"),
        subclassSubgraphs = {@NamedSubgraph(name = "special", type = DistributedGraphSpecialChild.class,
                attributeNodes = @NamedAttributeNode("special")),
                @NamedSubgraph(name = "other", type = DistributedGraphOtherChild.class, attributeNodes = @NamedAttributeNode("other"))})
@NamedEntityGraph(name = "DngChildNested", attributeNodes = @NamedAttributeNode("detail"))
@NamedEntityGraph(name = "DngDeepDefault", attributeNodes = {@NamedAttributeNode(value = "detail", subgraph = "deep"),
        @NamedAttributeNode(value = "details", subgraph = "deep", keySubgraph = "deep")},
        subgraphs = @NamedSubgraph(name = "deep", attributeNodes = @NamedAttributeNode("label")))
@NamedEntityGraph(name = "DngDeepBase", attributeNodes = {@NamedAttributeNode(value = "detail", subgraph = "deep"),
        @NamedAttributeNode(value = "details", subgraph = "deep", keySubgraph = "deep")},
        subgraphs = @NamedSubgraph(name = "deep", type = DistributedGraphDetail.class, attributeNodes = @NamedAttributeNode("id")))
@NamedEntityGraph(name = "DngDeepSubtype", attributeNodes = {@NamedAttributeNode(value = "detail", subgraph = "deep"),
        @NamedAttributeNode(value = "details", subgraph = "deep", keySubgraph = "deep")},
        subgraphs = @NamedSubgraph(name = "deep", type = DistributedGraphSpecialDetail.class, attributeNodes = @NamedAttributeNode("specialDetail")))
public class DistributedGraphChild {
    @Id
    private int id;

    @Fetch(graph = "DngChildLabels")
    @Fetch(graph = "DngInherited")
    private String label;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DETAIL_ID")
    @Fetch(graph = "DngChild", subgraph = "DngDetail")
    @Fetch(graph = "DngChildLazy", type = FetchType.LAZY)
    @Fetch(graph = "DngInherited", subgraph = "DngDetail", batchSize = 19,
            cacheRetrieveMode = CacheRetrieveMode.BYPASS, cacheStoreMode = CacheStoreMode.BYPASS)
    private DistributedGraphDetail detail;

    @ManyToMany
    @JoinTable(name = "JPA_DNG_CHILD_DETAILS", joinColumns = @JoinColumn(name = "CHILD_ID"),
            inverseJoinColumns = @JoinColumn(name = "VALUE_ID"))
    @MapKeyJoinColumn(name = "KEY_ID")
    private Map<DistributedGraphDetail, DistributedGraphDetail> details = new LinkedHashMap<>();

    public DistributedGraphChild() {
    }

    public DistributedGraphChild(int id, String label, DistributedGraphDetail detail) {
        this.id = id;
        this.label = label;
        this.detail = detail;
        details.put(detail, detail);
    }

    public Map<DistributedGraphDetail, DistributedGraphDetail> getDetails() {
        return details;
    }

    public DistributedGraphDetail getDetail() {
        return detail;
    }
    public int getId() {
        return id;
    }
    public String getLabel() {
        return label;
    }
}
