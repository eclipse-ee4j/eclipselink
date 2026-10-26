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
package org.eclipse.persistence.jpa.entityagent.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PessimisticLockScope;
import jakarta.persistence.QueryHint;
import jakarta.persistence.Table;

@Entity
@Table(name = "EA_NAMED_OPTIONS")
@NamedEntityGraph(name = "EANamed.tags", attributeNodes = @NamedAttributeNode("tags"))
@NamedEntityGraph(name = "EANamed.notes", attributeNodes = @NamedAttributeNode("notes"))
@NamedQuery(name = "EANamed.extended", query = "select r from AgentNamedOptionsRecord r where r.id=1",
        lockMode = LockModeType.PESSIMISTIC_WRITE, lockScope = PessimisticLockScope.EXTENDED)
@NamedQuery(name = "EANamed.scopeHint", query = "select r from AgentNamedOptionsRecord r where r.id=1",
        lockMode = LockModeType.PESSIMISTIC_WRITE, lockScope = PessimisticLockScope.EXTENDED,
        hints = @QueryHint(name = "jakarta.persistence.lock.scope", value = "NORMAL"))
@NamedQuery(name = "EANamed.graph", query = "select r from AgentNamedOptionsRecord r where r.id=1", entityGraph = "EANamed.tags")
@NamedQuery(name = "EANamed.graphHint", query = "select r from AgentNamedOptionsRecord r where r.id=1", entityGraph = "EANamed.tags",
        hints = @QueryHint(name = "jakarta.persistence.loadgraph", value = "EANamed.notes"))
@NamedQuery(name = "EANamed.fetchHint", query = "select r from AgentNamedOptionsRecord r where r.id=1", entityGraph = "EANamed.tags",
        hints = @QueryHint(name = "jakarta.persistence.fetchgraph", value = "EANamed.notes"))
public class AgentNamedOptionsRecord {
    @Id
    public int id;
    public String name;
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "EA_NAMED_TAG", joinColumns = @JoinColumn(name = "OWNER_ID"))
    @Column(name = "TAG_VALUE")
    public List<String> tags = new ArrayList<>();
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "EA_NAMED_NOTE", joinColumns = @JoinColumn(name = "OWNER_ID"))
    @Column(name = "NOTE_VALUE")
    public List<String> notes = new ArrayList<>();

    public AgentNamedOptionsRecord() {
    }
}
