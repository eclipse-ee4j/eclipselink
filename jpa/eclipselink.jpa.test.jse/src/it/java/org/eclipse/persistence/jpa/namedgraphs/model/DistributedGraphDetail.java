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
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;

@Entity(name = "DngDetail")
@Table(name = "JPA_DNG_DETAIL")
@Inheritance(strategy = InheritanceType.JOINED)
@NamedEntityGraph(name = "DngDetail")
public class DistributedGraphDetail {
    @Id
    private int id;
    @Fetch(graph = "DngDetail")
    private String label;

    public DistributedGraphDetail() {
    }

    public DistributedGraphDetail(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }
    public String getLabel() {
        return label;
    }
}
