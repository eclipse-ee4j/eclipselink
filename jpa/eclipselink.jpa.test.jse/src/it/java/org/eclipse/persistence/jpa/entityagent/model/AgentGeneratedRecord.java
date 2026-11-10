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

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PostInsert;
import jakarta.persistence.PostUpsert;
import jakarta.persistence.PreInsert;
import jakarta.persistence.PreUpsert;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;

@MappedSuperclass
public abstract class AgentGeneratedRecord {
    @Column(nullable = false)
    public String name;
    @Version
    public Integer version;
    @Transient
    public List<String> events = new ArrayList<>();
    @Transient
    public Object postInsertId;

    public abstract Object identifier();
    public abstract void assignIdentifier(Object id);

    @PreInsert
    void beforeInsert() { events.add("pre-insert"); }

    @PostInsert
    void afterInsert() {
        events.add("post-insert");
        postInsertId = identifier();
    }

    @PreUpsert
    void beforeUpsert() { events.add("pre-upsert"); }

    @PostUpsert
    void afterUpsert() { events.add("post-upsert"); }
}
