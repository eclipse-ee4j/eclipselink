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
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;

import java.util.Collection;

@Entity
@Table(name = "BATCH_IN_EAGER_PARENT")
public class EagerParent {
    @Id
    private long id;

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    @BatchFetch(value = BatchFetchType.IN)
    private Collection<EagerChild> children;

    public EagerParent() {
    }

    public EagerParent(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Collection<EagerChild> getChildren() {
        return children;
    }

    public void setChildren(Collection<EagerChild> children) {
        this.children = children;
    }
}
