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

// Contributors:
//     07/23/2026 - Andreas Lemmer
//       - 1455: Do not calculate changes for aggregates registered in the UnitOfWork
package org.eclipse.persistence.jpa.test.mapping.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;

@Entity
public class BatchFetchAggregateChangeEntity {

    @Id
    private int id;

    @ElementCollection(fetch = FetchType.LAZY)
    @BatchFetch(BatchFetchType.EXISTS)
    @CollectionTable(name = "BATCH_AGG_CHANGE_EMBEDS", joinColumns = @JoinColumn(name = "ENTITY_ID"))
    private List<AggregateChangeEmbeddable> embeds = new ArrayList<>();

    public BatchFetchAggregateChangeEntity() { }

    public BatchFetchAggregateChangeEntity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<AggregateChangeEmbeddable> getEmbeds() {
        return embeds;
    }

    public void setEmbeds(List<AggregateChangeEmbeddable> embeds) {
        this.embeds = embeds;
    }
}
