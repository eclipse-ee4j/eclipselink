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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.Entity;
import jakarta.persistence.Fetch;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyJoinColumn;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.QueryHint;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity(name = "DngRoot")
@Table(name = "JPA_DNG_ROOT")
@NamedEntityGraph(name = "DngRoot")
@NamedEntityGraph(name = "DngRepeat")
@NamedEntityGraph(name = "DngLazy")
@NamedEntityGraph(name = "DngLazyNested")
@NamedEntityGraph(name = "DngTyped")
@NamedEntityGraph(name = "DngMultiple")
@NamedEntityGraph(name = "DngAll", includeAllAttributes = true)
@NamedEntityGraph(name = "DngMixed", attributeNodes = @NamedAttributeNode(value = "child", subgraph = "localChild"),
        subgraphs = @NamedSubgraph(name = "localChild", type = DistributedGraphChild.class,
                attributeNodes = @NamedAttributeNode("label")))
@NamedEntityGraph(name = "DngLegacy", attributeNodes = @NamedAttributeNode(value = "keyed", subgraph = "legacyChild", keySubgraph = "legacyKey"),
        subgraphs = {@NamedSubgraph(name = "legacyChild", type = DistributedGraphChild.class, attributeNodes = @NamedAttributeNode("label")),
                @NamedSubgraph(name = "legacyKey", type = DistributedGraphDetail.class, attributeNodes = @NamedAttributeNode("label"))})
@NamedEntityGraph(name = "DngTypedKeys", attributeNodes = @NamedAttributeNode(value = "typedKeys", subgraph = "typedDetail", keySubgraph = "typedKey"),
        subgraphs = {@NamedSubgraph(name = "typedDetail", type = DistributedGraphDetail.class, attributeNodes = @NamedAttributeNode("label")),
                @NamedSubgraph(name = "typedKey", type = DistributedGraphChild.class, attributeNodes = @NamedAttributeNode("label")),
                @NamedSubgraph(name = "typedKey", type = DistributedGraphSpecialChild.class, attributeNodes = @NamedAttributeNode("special"))})
@NamedEntityGraph(name = "DngMixedLazy", attributeNodes = @NamedAttributeNode(value = "child", subgraph = "mixedChild"),
        subgraphs = @NamedSubgraph(name = "mixedChild", type = DistributedGraphChild.class,
                attributeNodes = @NamedAttributeNode("detail")))
@NamedEntityGraph(name = "DngDistributedSubclass")
@NamedEntityGraph(name = "DngLazyFirst")
@NamedEntityGraph(name = "DngLazyLast")
@NamedEntityGraph(name = "DngDeepDefaultFirst")
@NamedEntityGraph(name = "DngDeepSubtypeFirst")
@NamedEntityGraph(name = "DngDeepBaseFirst")
@NamedEntityGraph(name = "DngDeepDefaultLast")
@NamedEntityGraph(name = "DngDeepExplicitFirst")
@NamedEntityGraph(name = "DngDeepExplicitLast")
@NamedEntityGraph(name = "DngReusedLazy", attributeNodes = {@NamedAttributeNode(value = "child", subgraph = "reused"),
        @NamedAttributeNode(value = "otherChild", subgraph = "reused")},
        subgraphs = @NamedSubgraph(name = "reused", type = DistributedGraphChild.class, attributeNodes = @NamedAttributeNode("detail")))
@NamedEntityGraph(name = "DngReusedAdd", attributeNodes = {@NamedAttributeNode(value = "child", subgraph = "reused"),
        @NamedAttributeNode(value = "otherChild", subgraph = "reused")},
        subgraphs = @NamedSubgraph(name = "reused", type = DistributedGraphChild.class, attributeNodes = @NamedAttributeNode("label")))
@NamedEntityGraph(name = "DngReusedDeep", attributeNodes = {@NamedAttributeNode(value = "child", subgraph = "reused"),
        @NamedAttributeNode(value = "otherChild", subgraph = "reused")},
        subgraphs = {@NamedSubgraph(name = "reused", type = DistributedGraphChild.class,
                attributeNodes = {@NamedAttributeNode(value = "detail", subgraph = "deep"),
                        @NamedAttributeNode(value = "details", subgraph = "deep", keySubgraph = "deep")}),
                @NamedSubgraph(name = "deep", type = DistributedGraphDetail.class, attributeNodes = @NamedAttributeNode("label"))})
public class DistributedGraphRoot {
    @Id
    private int id;

    @Fetch(graph = "DngRoot")
    private String label;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHILD_ID")
    @Fetch(graph = "DngRoot", subgraph = "DngChild", cacheRetrieveMode = CacheRetrieveMode.BYPASS,
            cacheStoreMode = CacheStoreMode.BYPASS, batchSize = 7, hints = @QueryHint(name = "unrecognized.dng.hint", value = "ignored"))
    @Fetch(graph = "DngRepeat", subgraph = "DngChild")
    @Fetch(graph = "DngLazy", type = FetchType.LAZY, batchSize = 7,
            cacheRetrieveMode = CacheRetrieveMode.BYPASS, cacheStoreMode = CacheStoreMode.BYPASS)
    @Fetch(graph = "DngLazyNested", subgraph = "DngChildLazy")
    @Fetch(graph = "DngTyped", subgraph = {"DngChild", "DngSpecial"})
    @Fetch(graph = "DngMultiple", subgraph = {"DngChild", "DngChildLabels"})
    @Fetch(graph = "DngAll", subgraph = "DngChild")
    @Fetch(graph = "DngMixed", subgraph = "DngChild")
    @Fetch(graph = "DngMixedLazy", subgraph = "DngChildLazy")
    @Fetch(graph = "DngDistributedSubclass", subgraph = {"DngSubclass"})
    @Fetch(graph = "DngLazyFirst", subgraph = {"DngChildLazy", "DngChildNested"})
    @Fetch(graph = "DngLazyLast", subgraph = {"DngChildNested", "DngChildLazy"})
    @Fetch(graph = "DngDeepDefaultFirst", subgraph = {"DngDeepDefault", "DngDeepSubtype"})
    @Fetch(graph = "DngDeepSubtypeFirst", subgraph = {"DngDeepSubtype", "DngDeepDefault"})
    @Fetch(graph = "DngDeepBaseFirst", subgraph = {"DngDeepBase", "DngDeepDefault"})
    @Fetch(graph = "DngDeepDefaultLast", subgraph = {"DngDeepDefault", "DngDeepBase"})
    @Fetch(graph = "DngDeepExplicitFirst", subgraph = {"DngDeepBase", "DngDeepSubtype"})
    @Fetch(graph = "DngDeepExplicitLast", subgraph = {"DngDeepSubtype", "DngDeepBase"})
    @Fetch(graph = "DngReusedLazy", subgraph = {"DngChildLazy"})
    @Fetch(graph = "DngReusedAdd", subgraph = {"DngChild"})
    @Fetch(graph = "DngReusedDeep", subgraph = {"DngDeepBase"})
    private DistributedGraphChild child;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OTHER_CHILD_ID")
    private DistributedGraphChild otherChild;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OMITTED_ID")
    @Fetch
    private DistributedGraphDetail omitted;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EAGER_ID")
    private DistributedGraphDetail eager;

    @ManyToMany
    @JoinTable(name = "JPA_DNG_LIST", joinColumns = @JoinColumn(name = "ROOT_ID"), inverseJoinColumns = @JoinColumn(name = "CHILD_ID"))
    @Fetch(graph = "DngTyped", subgraph = {"DngChild", "DngSpecial"})
    private List<DistributedGraphChild> children = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "JPA_DNG_MAP", joinColumns = @JoinColumn(name = "ROOT_ID"), inverseJoinColumns = @JoinColumn(name = "CHILD_ID"))
    @MapKeyColumn(name = "GRAPH_KEY")
    @Fetch(graph = "DngTyped", subgraph = "DngChild")
    private Map<String, DistributedGraphChild> byName = new LinkedHashMap<>();

    @ManyToMany
    @JoinTable(name = "JPA_DNG_KEYS", joinColumns = @JoinColumn(name = "ROOT_ID"), inverseJoinColumns = @JoinColumn(name = "CHILD_ID"))
    @MapKeyJoinColumn(name = "DETAIL_ID")
    private Map<DistributedGraphDetail, DistributedGraphChild> keyed = new LinkedHashMap<>();

    @ManyToMany
    @JoinTable(name = "JPA_DNG_TYPED_KEYS", joinColumns = @JoinColumn(name = "ROOT_ID"), inverseJoinColumns = @JoinColumn(name = "DETAIL_ID"))
    @MapKeyJoinColumn(name = "CHILD_ID")
    private Map<DistributedGraphChild, DistributedGraphDetail> typedKeys = new LinkedHashMap<>();

    @Transient
    private String transientValue;

    public DistributedGraphRoot() {
    }

    public DistributedGraphRoot(int id, DistributedGraphChild child, DistributedGraphDetail omitted, DistributedGraphDetail eager) {
        this.id = id;
        this.label = "root-" + id;
        this.child = child;
        this.otherChild = child;
        this.omitted = omitted;
        this.eager = eager;
        children.add(child);
        byName.put("child", child);
        keyed.put(omitted, child);
        typedKeys.put(child, omitted);
    }

    public DistributedGraphChild getOtherChild() {
        return otherChild;
    }
    public void setOtherChild(DistributedGraphChild otherChild) {
        this.otherChild = otherChild;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public DistributedGraphChild getChild() {
        return child;
    }
    public List<DistributedGraphChild> getChildren() {
        return children;
    }
    public Map<String, DistributedGraphChild> getByName() {
        return byName;
    }
    public DistributedGraphDetail getEager() {
        return eager;
    }
    public Map<DistributedGraphDetail, DistributedGraphChild> getKeyed() {
        return keyed;
    }
    public String getLabel() {
        return label;
    }
    public DistributedGraphDetail getOmitted() {
        return omitted;
    }

    // Field access is selected. This invalid getter contribution must be ignored.
    @Fetch(graph = "DngRoot", subgraph = "must-not-resolve")
    public String getTransientValue() {
        return transientValue;
    }
}
