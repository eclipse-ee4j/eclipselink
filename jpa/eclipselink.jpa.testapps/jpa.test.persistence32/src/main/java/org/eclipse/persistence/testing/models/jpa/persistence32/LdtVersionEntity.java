/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.models.jpa.persistence32;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

// Bug #2343 test to verify LocalDateTime as @Version attribute
@Entity
@Table(name="PERSISTENCE32_LDT_VERSION_ENTITY")
public class LdtVersionEntity {

    @Id
    private int id;

    @Version
    private LocalDateTime lastUpdated;

    private String name;

    public LdtVersionEntity(int id, LocalDateTime lastUpdated, String name) {
        this.id = id;
        this.lastUpdated = lastUpdated;
        this.name = name;
    }

    public LdtVersionEntity() {
        this(-1, null, null);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        return id == ((LdtVersionEntity) obj).id
                && Objects.equals(name, ((LdtVersionEntity) obj).name)
                && Objects.equals(lastUpdated, ((LdtVersionEntity) obj).lastUpdated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lastUpdated);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LdtVersionEntity {id=");
        sb.append(id);
        sb.append(", name=");
        sb.append(name);
        sb.append(", lastUpdated=");
        sb.append(lastUpdated);
        sb.append("}");
        return sb.toString();
    }
}
