/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.jpa.test.record.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

@Entity
public class RecordEntity {

    @EmbeddedId
    private RecordId id;

    @Embedded
    private RecordValue value;

    private String name;

    public RecordEntity() {
    }

    public RecordEntity(RecordId id, String name, RecordValue value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public RecordId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public RecordValue getValue() {
        return value;
    }
}
