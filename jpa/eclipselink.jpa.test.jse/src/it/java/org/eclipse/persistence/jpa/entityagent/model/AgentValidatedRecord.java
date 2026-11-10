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

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostInsert;
import jakarta.persistence.PreDelete;
import jakarta.persistence.PreInsert;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.PreUpsert;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "EA_VALIDATED_RECORD")
public class AgentValidatedRecord {
    public interface InsertChecks { }
    public interface ExtraChecks { }
    public interface DeleteChecks { }

    public static final List<String> EVENTS = new CopyOnWriteArrayList<>();

    @Id
    public Integer id;
    @Size(min = 3)
    public String name;
    @Size(min = 3, groups = InsertChecks.class)
    public String insertCheck = "valid";
    @Size(min = 3, groups = ExtraChecks.class)
    public String extraCheck = "valid";
    @Size(min = 3, groups = DeleteChecks.class)
    public String deleteCheck = "valid";
    @Transient
    public boolean repairInCallback;

    public AgentValidatedRecord() {
    }

    public AgentValidatedRecord(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @PreInsert
    void beforeInsert() {
        EVENTS.add("pre-insert");
        if (repairInCallback) {
            name = "repaired";
        }
    }

    @PostInsert
    void afterInsert() { EVENTS.add("post-insert"); }

    @PreUpdate
    void beforeUpdate() { EVENTS.add("pre-update"); }

    @PreUpsert
    void beforeUpsert() { EVENTS.add("pre-upsert"); }

    @PreDelete
    void beforeDelete() { EVENTS.add("pre-delete"); }
}
