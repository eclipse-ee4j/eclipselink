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
//     09/27/2026 - Andreas Lemmer
//       - 2885: eager batch fetching in both directions must not overflow the stack
package org.eclipse.persistence.testing.models.jpa.batchfetch;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;

@Entity
@Table(name = "BATCH_IN_WOVEN_CHILD")
public class WovenEagerChild {
    @Id
    private long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "PARENT_ID")
    @BatchFetch(value = BatchFetchType.IN)
    private WovenEagerParent parent;

    public WovenEagerChild() {
    }

    public WovenEagerChild(long id, WovenEagerParent parent) {
        this.id = id;
        this.parent = parent;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public WovenEagerParent getParent() {
        return parent;
    }

    public void setParent(WovenEagerParent parent) {
        this.parent = parent;
    }
}
