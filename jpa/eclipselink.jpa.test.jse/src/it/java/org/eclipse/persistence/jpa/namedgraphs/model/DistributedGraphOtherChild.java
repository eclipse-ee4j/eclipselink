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
import jakarta.persistence.Table;

@Entity(name = "DistributedGraphOtherChild")
@Table(name = "JPA_DNG_OTHER_CHILD")
public class DistributedGraphOtherChild extends DistributedGraphChild {
    private String other;

    public DistributedGraphOtherChild() {
    }

    public DistributedGraphOtherChild(int id, DistributedGraphDetail detail) {
        super(id, "other-child", detail);
        this.other = "other-value";
    }

    public String getOther() {
        return other;
    }
}
