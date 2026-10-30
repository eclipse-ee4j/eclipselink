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
import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.Fetch;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;

@Entity(name = "DngSpecial")
@Table(name = "JPA_DNG_SPECIAL")
@NamedEntityGraph(name = "DngSpecial")
@NamedEntityGraph(name = "DngInherited")
public class DistributedGraphSpecialChild extends DistributedGraphChild {
    @Fetch(graph = "DngSpecial")
    @Fetch(graph = "DngInherited", batchSize = 13,
            cacheRetrieveMode = CacheRetrieveMode.BYPASS, cacheStoreMode = CacheStoreMode.BYPASS)
    private String special;

    public DistributedGraphSpecialChild() {
    }

    public DistributedGraphSpecialChild(int id, DistributedGraphDetail detail) {
        super(id, "special-child", detail);
        special = "special-value";
    }

    public String getSpecial() {
        return special;
    }
}
