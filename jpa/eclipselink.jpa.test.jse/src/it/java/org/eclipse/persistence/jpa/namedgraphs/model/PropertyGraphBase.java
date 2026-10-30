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
import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.Fetch;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@Access(AccessType.PROPERTY)
public class PropertyGraphBase {
    private DistributedGraphDetail inheritedTarget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INHERITED_ID")
    @Fetch(graph = "DngProperty", subgraph = "DngDetail", batchSize = 17,
            cacheRetrieveMode = CacheRetrieveMode.BYPASS, cacheStoreMode = CacheStoreMode.BYPASS)
    public DistributedGraphDetail getInheritedTarget() {
        return inheritedTarget;
    }
    public void setInheritedTarget(DistributedGraphDetail inheritedTarget) {
        this.inheritedTarget = inheritedTarget;
    }
}
