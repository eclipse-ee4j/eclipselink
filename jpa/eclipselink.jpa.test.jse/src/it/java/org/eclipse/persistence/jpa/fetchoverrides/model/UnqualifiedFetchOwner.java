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
package org.eclipse.persistence.jpa.fetchoverrides.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Basic;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Fetch;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "JPA_UF_OWNER")
@NamedEntityGraph(name = "UfNamedOnly")
public class UnqualifiedFetchOwner {
    @Id
    private int id;

    @Basic(fetch = FetchType.LAZY)
    @Fetch
    @Column(name = "EAGER_TEXT")
    private String eagerText;

    @Basic(fetch = FetchType.EAGER)
    @Fetch(type = FetchType.LAZY)
    @Column(name = "LAZY_TEXT")
    private String lazyText;

    @ManyToOne(fetch = FetchType.LAZY)
    @Fetch
    @JoinColumn(name = "EAGER_MANY_ID")
    private UnqualifiedFetchTarget eagerMany;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(type = FetchType.LAZY)
    @JoinColumn(name = "LAZY_MANY_ID")
    private UnqualifiedFetchTarget lazyMany;

    @OneToOne(fetch = FetchType.LAZY)
    @Fetch
    @JoinColumn(name = "EAGER_ONE_ID")
    private UnqualifiedFetchTarget eagerOne;

    @OneToOne(fetch = FetchType.EAGER)
    @Fetch(type = FetchType.LAZY)
    @JoinColumn(name = "LAZY_ONE_ID")
    private UnqualifiedFetchTarget lazyOne;

    @ManyToOne
    @JoinColumn(name = "DEFAULT_MANY_ID")
    private UnqualifiedFetchTarget defaultMany;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(type = FetchType.DEFAULT)
    @JoinColumn(name = "DEFAULT_OVERRIDE_MANY_ID")
    private UnqualifiedFetchTarget defaultOverrideMany;

    @OneToOne(fetch = FetchType.EAGER)
    @Fetch(type = FetchType.DEFAULT)
    @JoinColumn(name = "DEFAULT_OVERRIDE_ONE_ID")
    private UnqualifiedFetchTarget defaultOverrideOne;

    @ManyToOne(fetch = FetchType.LAZY)
    @Fetch(graph = "UfNamedOnly")
    @JoinColumn(name = "NAMED_ONLY_ID")
    private UnqualifiedFetchTarget namedOnlyMany;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PLAIN_EAGER_ID")
    private UnqualifiedFetchTarget plainEagerMany;

    @ManyToOne(fetch = FetchType.LAZY)
    @Fetch
    @Fetch(graph = "UfNamedOnly", type = FetchType.LAZY)
    @JoinColumn(name = "REPEATED_ID")
    private UnqualifiedFetchTarget repeatedMany;

    @OneToMany(fetch = FetchType.LAZY)
    @Fetch
    @JoinTable(name = "JPA_UF_OTM_EAGER", joinColumns = @JoinColumn(name = "OWNER_ID"),
            inverseJoinColumns = @JoinColumn(name = "TARGET_ID"))
    private List<UnqualifiedFetchTarget> eagerOneMany = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER)
    @Fetch(type = FetchType.LAZY)
    @JoinTable(name = "JPA_UF_OTM_LAZY", joinColumns = @JoinColumn(name = "OWNER_ID"),
            inverseJoinColumns = @JoinColumn(name = "TARGET_ID"))
    private List<UnqualifiedFetchTarget> lazyOneMany = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @Fetch
    @JoinTable(name = "JPA_UF_MTM_EAGER", joinColumns = @JoinColumn(name = "OWNER_ID"),
            inverseJoinColumns = @JoinColumn(name = "TARGET_ID"))
    private List<UnqualifiedFetchTarget> eagerManyMany = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(type = FetchType.LAZY)
    @JoinTable(name = "JPA_UF_MTM_LAZY", joinColumns = @JoinColumn(name = "OWNER_ID"),
            inverseJoinColumns = @JoinColumn(name = "TARGET_ID"))
    private List<UnqualifiedFetchTarget> lazyManyMany = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @Fetch
    @CollectionTable(name = "JPA_UF_TAG_EAGER", joinColumns = @JoinColumn(name = "OWNER_ID"))
    @Column(name = "TAG_VALUE")
    private List<String> eagerTags = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(type = FetchType.LAZY)
    @CollectionTable(name = "JPA_UF_TAG_LAZY", joinColumns = @JoinColumn(name = "OWNER_ID"))
    @Column(name = "TAG_VALUE")
    private List<String> lazyTags = new ArrayList<>();

    public UnqualifiedFetchOwner() {
    }

    public UnqualifiedFetchOwner(int id, List<UnqualifiedFetchTarget> targets) {
        this.id = id;
        eagerText = "eager-text";
        lazyText = "lazy-text";
        eagerMany = targets.get(0);
        lazyMany = targets.get(1);
        eagerOne = targets.get(2);
        lazyOne = targets.get(3);
        defaultMany = targets.get(4);
        defaultOverrideMany = targets.get(5);
        defaultOverrideOne = targets.get(6);
        namedOnlyMany = targets.get(7);
        plainEagerMany = targets.get(8);
        repeatedMany = targets.get(9);
        eagerOneMany.add(targets.get(19));
        lazyOneMany.add(targets.get(20));
        eagerManyMany.add(targets.get(21));
        lazyManyMany.add(targets.get(22));
        eagerTags.add("eager-tag");
        lazyTags.add("lazy-tag");
    }

    public String getEagerText() {
        return eagerText;
    }

    public String getLazyText() {
        return lazyText;
    }

    public UnqualifiedFetchTarget getEagerMany() {
        return eagerMany;
    }

    public UnqualifiedFetchTarget getLazyMany() {
        return lazyMany;
    }

    public UnqualifiedFetchTarget getEagerOne() {
        return eagerOne;
    }

    public UnqualifiedFetchTarget getLazyOne() {
        return lazyOne;
    }

    public UnqualifiedFetchTarget getDefaultMany() {
        return defaultMany;
    }

    public UnqualifiedFetchTarget getDefaultOverrideMany() {
        return defaultOverrideMany;
    }

    public UnqualifiedFetchTarget getDefaultOverrideOne() {
        return defaultOverrideOne;
    }

    public UnqualifiedFetchTarget getNamedOnlyMany() {
        return namedOnlyMany;
    }

    public UnqualifiedFetchTarget getPlainEagerMany() {
        return plainEagerMany;
    }

    public UnqualifiedFetchTarget getRepeatedMany() {
        return repeatedMany;
    }

    public List<UnqualifiedFetchTarget> getEagerOneMany() {
        return eagerOneMany;
    }

    public List<UnqualifiedFetchTarget> getLazyOneMany() {
        return lazyOneMany;
    }

    public List<UnqualifiedFetchTarget> getEagerManyMany() {
        return eagerManyMany;
    }

    public List<UnqualifiedFetchTarget> getLazyManyMany() {
        return lazyManyMany;
    }

    public List<String> getEagerTags() {
        return eagerTags;
    }

    public List<String> getLazyTags() {
        return lazyTags;
    }
}
