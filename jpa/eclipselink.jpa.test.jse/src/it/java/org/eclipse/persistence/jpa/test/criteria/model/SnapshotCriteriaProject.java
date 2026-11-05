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
package org.eclipse.persistence.jpa.test.criteria.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "JPA_CQS_PROJECT")
public class SnapshotCriteriaProject {
    @Id
    @Column(name = "PROJECT_ID")
    public int id;

    @Column(name = "PROJECT_NAME")
    public String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_LEADER_ID")
    public SnapshotCriteriaEmployee teamLeader;

    public SnapshotCriteriaProject() {
    }

    public SnapshotCriteriaProject(int id, String name, SnapshotCriteriaEmployee teamLeader) {
        this.id = id;
        this.name = name;
        this.teamLeader = teamLeader;
    }
}
