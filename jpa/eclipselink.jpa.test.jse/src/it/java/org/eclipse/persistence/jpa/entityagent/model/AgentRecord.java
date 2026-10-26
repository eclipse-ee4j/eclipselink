/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 2021, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.entityagent.model;

import jakarta.persistence.Basic;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ExcludedFromVersioning;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "EA_RECORD")
@SecondaryTable(name = "EA_DETAILS")
@Inheritance(strategy = InheritanceType.JOINED)
public class AgentRecord {
    @Id public Long id;
    @Version public Integer version;
    public String name;
    @Basic(fetch = FetchType.LAZY)
    public String lazyNote;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FAVORITE_ID")
    public AgentTarget favorite;
    public String getLazyNote() { return lazyNote; }
    public AgentTarget getFavorite() { return favorite; }
    @ExcludedFromVersioning
    @Column(table = "EA_DETAILS")
    public String note;
    @Embedded public AgentAddress address;
    @ElementCollection
    @CollectionTable(name = "EA_TAG", joinColumns = @JoinColumn(name = "RECORD_ID"))
    @Column(name = "TAG_VALUE")
    public List<String> tags = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "EA_LINK", joinColumns = @JoinColumn(name = "RECORD_ID"), inverseJoinColumns = @JoinColumn(name = "TARGET_ID"))
    public List<AgentTarget> targets = new ArrayList<>();
    public AgentRecord() { }
    public AgentRecord(long id, String name) { this.id = id; this.name = name; }
}
