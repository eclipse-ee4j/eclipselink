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
package org.eclipse.persistence.jpa.namedgraphs.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Fetch;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;

@Entity(name = "DngSharedChild")
@Table(name = "JPA_DNG_SHARED_CHILD")
@NamedEntityGraph(name = "DngShared")
public class SharedGraphChild {
    @Id
    private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DETAIL_ID")
    @Fetch(graph = "DngShared", subgraph = "DngDetail")
    private DistributedGraphDetail detail;

    public SharedGraphChild() {
    }

    public SharedGraphChild(int id, DistributedGraphDetail detail) {
        this.id = id;
        this.detail = detail;
    }

    public DistributedGraphDetail getDetail() {
        return detail;
    }
}
