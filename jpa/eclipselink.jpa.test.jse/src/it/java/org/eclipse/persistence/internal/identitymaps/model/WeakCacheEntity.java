/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.internal.identitymaps.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "JPA_WEAK_CACHE_3014")
public class WeakCacheEntity {

    @Id
    private int id;

    @Column(name = "CACHE_VALUE")
    private String value;

    public WeakCacheEntity() {
    }

    public WeakCacheEntity(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
