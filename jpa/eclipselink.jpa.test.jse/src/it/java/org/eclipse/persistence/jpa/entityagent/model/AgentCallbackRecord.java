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

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.ExcludedFromVersioning;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeStatement;
import jakarta.persistence.NamedStatement;
import jakarta.persistence.PostDelete;
import jakarta.persistence.PostInsert;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PostUpsert;
import jakarta.persistence.PreDelete;
import jakarta.persistence.PreInsert;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.PreUpsert;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;

@Entity
@Table(name = "EA_CALLBACK_RECORD")
@EntityListeners(AgentCallbackListener.class)
@NamedStatement(name = "agentCallbackRename", statement = "update AgentCallbackRecord r set r.name = :name where r.id = :id")
@NamedStatement(name = "agentCallbackDelete", statement = "delete from AgentCallbackRecord r where r.id = :id")
@NamedNativeStatement(name = "agentCallbackNativeRename", statement = "update EA_CALLBACK_RECORD set NAME = 'native' where ID = 107")
@NamedNativeStatement(name = "agentCallbackNativeDelete", statement = "delete from EA_CALLBACK_RECORD where ID = 107")
public class AgentCallbackRecord {
    @Id
    public Integer id;
    public String name;
    @ExcludedFromVersioning
    public String note;
    @Version
    public Integer version;
    @Transient
    public List<String> events = new ArrayList<>();

    public AgentCallbackRecord() {
    }

    public AgentCallbackRecord(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @PrePersist
    void prePersist() { events.add("pre-persist"); }

    @PostPersist
    void postPersist() { events.add("post-persist"); }

    @PreRemove
    void preRemove() { events.add("pre-remove"); }

    @PostRemove
    void postRemove() { events.add("post-remove"); }

    @PreInsert
    void preInsert() {
        events.add("pre-insert");
        if ("change-before-insert".equals(name)) {
            name = "changed-by-callback";
        }
    }

    @PostInsert
    void postInsert() { events.add("post-insert"); }

    @PreUpdate
    void preUpdate() { events.add("pre-update"); }

    @PostUpdate
    void postUpdate() { events.add("post-update"); }

    @PreUpsert
    void preUpsert() { events.add("pre-upsert"); }

    @PostUpsert
    void postUpsert() { events.add("post-upsert"); }

    @PreDelete
    void preDelete() { events.add("pre-delete"); }

    @PostDelete
    void postDelete() { events.add("post-delete"); }

    @PostLoad
    void postLoad() { events.add("post-load:" + name); }
}
