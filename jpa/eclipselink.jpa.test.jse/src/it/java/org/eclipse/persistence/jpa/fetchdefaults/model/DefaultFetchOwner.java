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
package org.eclipse.persistence.jpa.fetchdefaults.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "JPA_DTF_OWNER")
public class DefaultFetchOwner {
    @Id
    private int id;
    private String label;

    @ManyToOne
    @JoinColumn(name = "OMITTEDMANY_ID")
    private DefaultFetchTarget omittedMany;

    @ManyToOne(fetch = FetchType.DEFAULT)
    @JoinColumn(name = "DEFAULTMANY_ID")
    private DefaultFetchTarget defaultMany;

    @OneToOne
    @JoinColumn(name = "OMITTEDONE_ID")
    private DefaultFetchTarget omittedOne;

    @OneToOne(fetch = FetchType.DEFAULT)
    @JoinColumn(name = "DEFAULTONE_ID")
    private DefaultFetchTarget defaultOne;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LAZYMANY_ID")
    private DefaultFetchTarget lazyMany;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EAGERMANY_ID")
    private DefaultFetchTarget eagerMany;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LAZYONE_ID")
    private DefaultFetchTarget lazyOne;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EAGERONE_ID")
    private DefaultFetchTarget eagerOne;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "XMLLAZY_ID")
    private DefaultFetchTarget xmlLazy;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "XMLEAGER_ID")
    private DefaultFetchTarget xmlEager;

    @ElementCollection
    @CollectionTable(name = "JPA_DTF_TAGS", joinColumns = @JoinColumn(name = "OWNER_ID"))
    private List<String> tags = new ArrayList<>();

    @OneToMany
    @JoinTable(name = "JPA_DTF_LINKS", joinColumns = @JoinColumn(name = "OWNER_ID"),
            inverseJoinColumns = @JoinColumn(name = "TARGET_ID"))
    private List<DefaultFetchTarget> targets = new ArrayList<>();

    public DefaultFetchOwner() {
    }

    public DefaultFetchOwner(int id, List<DefaultFetchTarget> references) {
        this.id = id;
        label = "owner";
        omittedMany = references.get(0);
        defaultMany = references.get(1);
        omittedOne = references.get(2);
        defaultOne = references.get(3);
        lazyMany = references.get(4);
        eagerMany = references.get(5);
        lazyOne = references.get(6);
        eagerOne = references.get(7);
        xmlLazy = references.get(8);
        xmlEager = references.get(9);
        tags.add("tag");
        targets.add(references.get(10));
    }

    public String getLabel() {
        return label;
    }

    public DefaultFetchTarget getOmittedMany() {
        return omittedMany;
    }

    public DefaultFetchTarget getDefaultMany() {
        return defaultMany;
    }

    public DefaultFetchTarget getOmittedOne() {
        return omittedOne;
    }

    public DefaultFetchTarget getDefaultOne() {
        return defaultOne;
    }

    public DefaultFetchTarget getLazyMany() {
        return lazyMany;
    }

    public DefaultFetchTarget getEagerMany() {
        return eagerMany;
    }

    public DefaultFetchTarget getLazyOne() {
        return lazyOne;
    }

    public DefaultFetchTarget getEagerOne() {
        return eagerOne;
    }

    public DefaultFetchTarget getXmlLazy() {
        return xmlLazy;
    }

    public DefaultFetchTarget getXmlEager() {
        return xmlEager;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<DefaultFetchTarget> getTargets() {
        return targets;
    }
}
