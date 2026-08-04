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

package org.eclipse.persistence.jpa.test.uuid.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Entity with a nullable (non-PK) UUID field, used to verify that reading a NULL
 * value from the database does not throw NullPointerException in UUIDConverter.
 */
@Entity
@Table(name = "UUID_NULLABLE_FIELD")
public class UUIDNullableFieldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "OPTIONAL_UUID")
    private UUID optionalUuid;

    @Column(name = "NAME")
    private String name;

    public UUIDNullableFieldEntity() {
    }

    public Long getId() {
        return id;
    }

    public UUID getOptionalUuid() {
        return optionalUuid;
    }

    public void setOptionalUuid(UUID optionalUuid) {
        this.optionalUuid = optionalUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UUIDNullableFieldEntity{id=" + id + ", optionalUuid=" + optionalUuid + ", name='" + name + "'}";
    }
}
