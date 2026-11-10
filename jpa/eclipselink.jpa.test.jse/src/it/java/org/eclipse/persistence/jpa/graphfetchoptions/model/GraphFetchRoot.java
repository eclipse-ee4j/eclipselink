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
package org.eclipse.persistence.jpa.graphfetchoptions.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Basic;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyJoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.JoinFetchType;

@Entity
@Table(name = "JPA_GFO_ROOT")
public class GraphFetchRoot {
    @Id
    private int id;

    @Version
    private int version;

    @Basic(fetch = FetchType.EAGER)
    private String note;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MANY_ID")
    private GraphFetchChild many;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ONE_ID")
    private GraphFetchChild one;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "UNLISTED_ID")
    private GraphFetchChild unlisted;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "JPA_GFO_MEMBERS", joinColumns = @JoinColumn(name = "ROOT_ID"),
            inverseJoinColumns = @JoinColumn(name = "CHILD_ID"))
    private List<GraphFetchChild> members = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "JPA_GFO_PEERS", joinColumns = @JoinColumn(name = "ROOT_ID"),
            inverseJoinColumns = @JoinColumn(name = "CHILD_ID"))
    private List<GraphFetchChild> peers = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "JPA_GFO_KEYED", joinColumns = @JoinColumn(name = "ROOT_ID"),
            inverseJoinColumns = @JoinColumn(name = "VALUE_CHILD_ID"))
    @MapKeyJoinColumn(name = "KEY_CHILD_ID")
    private Map<GraphFetchChild, GraphFetchChild> keyed = new LinkedHashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "JPA_GFO_TAGS", joinColumns = @JoinColumn(name = "ROOT_ID"))
    @Column(name = "TAG_VALUE")
    private List<String> tags = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "JPA_GFO_LAZY_TAGS", joinColumns = @JoinColumn(name = "ROOT_ID"))
    @Column(name = "TAG_VALUE")
    private List<String> lazyTags = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LAZY_ID")
    private GraphFetchChild lazy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinFetch(JoinFetchType.OUTER)
    @JoinColumn(name = "JOINED_ID")
    private GraphFetchChild joined;

    @ManyToOne(fetch = FetchType.EAGER)
    @BatchFetch(BatchFetchType.IN)
    @JoinColumn(name = "BATCHED_ID")
    private GraphFetchChild batched;

    public GraphFetchRoot() {
    }

    public GraphFetchRoot(int id, List<GraphFetchChild> children) {
        this.id = id;
        note = "root-" + id;
        many = children.get(0);
        one = children.get(1);
        unlisted = children.get(2);
        members.add(children.get(3));
        peers.add(children.get(4));
        keyed.put(children.get(1), children.get(0));
        lazy = children.get(5);
        joined = children.get(6);
        batched = children.get(7);
        tags.add("tag-" + id);
        lazyTags.add("lazy-tag-" + id);
    }

    public int getId() { return id; }
    public int getVersion() { return version; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public GraphFetchChild getMany() { return many; }
    public void setMany(GraphFetchChild many) { this.many = many; }
    public GraphFetchChild getOne() { return one; }
    public GraphFetchChild getUnlisted() { return unlisted; }
    public List<GraphFetchChild> getMembers() { return members; }
    public List<GraphFetchChild> getPeers() { return peers; }
    public Map<GraphFetchChild, GraphFetchChild> getKeyed() { return keyed; }
    public List<String> getTags() { return tags; }
    public List<String> getLazyTags() { return lazyTags; }
    public GraphFetchChild getLazy() { return lazy; }
    public GraphFetchChild getJoined() { return joined; }
    public GraphFetchChild getBatched() { return batched; }
}
