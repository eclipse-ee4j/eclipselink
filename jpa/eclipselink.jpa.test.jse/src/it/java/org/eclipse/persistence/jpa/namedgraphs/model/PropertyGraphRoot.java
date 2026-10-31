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

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Entity;
import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.Fetch;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity(name = "DngProperty")
@Table(name = "JPA_DNG_PROPERTY")
@Access(AccessType.PROPERTY)
@NamedEntityGraph
public class PropertyGraphRoot extends PropertyGraphBase {
    private int id;
    private String label;

    // Property access is selected; this invalid field contribution is ignored.
    @Fetch(graph = "DngProperty", subgraph = "must-not-resolve")
    private DistributedGraphDetail target;

    @Access(AccessType.FIELD)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXPLICIT_ID")
    @Fetch(graph = "DngProperty")
    private DistributedGraphDetail explicitTarget;

    public PropertyGraphRoot() {
    }

    public PropertyGraphRoot(int id, DistributedGraphDetail target) {
        this.id = id;
        this.label = "property";
        this.target = target;
        this.explicitTarget = target;
        setInheritedTarget(target);
    }

    @Id
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    @Fetch(graph = "DngProperty")
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TARGET_ID")
    @Fetch(graph = "DngProperty", subgraph = "DngDetail", batchSize = 11,
            cacheRetrieveMode = CacheRetrieveMode.BYPASS, cacheStoreMode = CacheStoreMode.BYPASS)
    public DistributedGraphDetail getTarget() {
        return target;
    }
    public void setTarget(DistributedGraphDetail target) {
        this.target = target;
    }

    @Transient
    public DistributedGraphDetail getExplicitTarget() {
        return explicitTarget;
    }

    @Transient
    public String getIgnored() {
        return "ignored";
    }
}
