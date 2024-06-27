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

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.helidon.models;

import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "HELIDON_TAB_MASTER")
@NamedQueries({
        @NamedQuery(name = "MasterEntity.findAll", query = "SELECT e FROM MasterEntity e"),
})
public class MasterEntity extends BaseEntity {

    private String name;

    @OneToMany(mappedBy = "master")
    private List<DetailEntity> details = new ArrayList<>();

    public MasterEntity() {
    }

    public MasterEntity(long id) {
        super(id);
    }
    
    public MasterEntity(long id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DetailEntity> getDetails() {
        return details;
    }

    public void setDetails(List<DetailEntity> details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MasterEntity that = (MasterEntity) o;
        return getId() == that.getId() && Objects.equals(name, that.name) && Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), name, details);
    }

    @Override
    public String toString() {
        return "MasterEntity{" +
                "id='" + getId() + '\'' +
                "name='" + name + '\'' +
                ", details=" + details +
                '}';
    }
}
