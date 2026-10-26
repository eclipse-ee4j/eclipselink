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

import java.util.function.Consumer;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostUpsert;
import jakarta.persistence.PreUpsert;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;

@Entity
@Table(name = "EA_RACE_RECORD")
public class AgentRaceRecord {
    public static volatile Consumer<AgentRaceRecord> beforeUpsert;

    @Id
    public long id;
    public String name;
    @Version
    public int version;
    @Transient
    public int preUpserts;
    @Transient
    public int postUpserts;

    public AgentRaceRecord() {
    }

    public AgentRaceRecord(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @PreUpsert
    public void beforeUpsert() {
        preUpserts++;
        Consumer<AgentRaceRecord> callback = beforeUpsert;
        if (callback != null) {
            callback.accept(this);
        }
    }

    @PostUpsert
    public void afterUpsert() {
        postUpserts++;
    }

    public String getName() {
        return name;
    }

    public int getVersion() {
        return version;
    }
}
