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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreMerge;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "EA_MERGE_CALLBACK")
public class AgentMergeCallbackRecord {
    public record Callback(String event, AgentMergeCallbackRecord entity, String name) { }
    public static final List<Callback> CALLBACKS = new CopyOnWriteArrayList<>();

    @Id
    public Integer id;
    public String name;
    @ManyToOne(cascade = CascadeType.MERGE)
    public AgentMergeCallbackRecord child;
    @ManyToOne(cascade = CascadeType.MERGE)
    public AgentMergeCallbackRecord repeatedChild;
    @ManyToOne(cascade = CascadeType.MERGE)
    public AgentMergeCallbackRecord parent;
    @Transient
    public boolean failPreMerge;

    public AgentMergeCallbackRecord() {
    }

    public AgentMergeCallbackRecord(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @PreMerge
    void beforeMerge() {
        CALLBACKS.add(new Callback("pre-merge", this, name));
        if (failPreMerge) {
            throw new IllegalStateException("pre-merge callback failure");
        }
        name = "merged:" + name;
    }

    @PrePersist
    void beforePersist() {
        CALLBACKS.add(new Callback("pre-persist", this, name));
    }
}
