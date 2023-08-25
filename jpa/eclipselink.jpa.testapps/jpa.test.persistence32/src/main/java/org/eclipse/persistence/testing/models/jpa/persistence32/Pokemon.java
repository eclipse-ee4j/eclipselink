/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Collection;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@Table(name="PERSISTENCE32_POKEMON")
@NamedQuery(name="Pokemon.get", query="SELECT p FROM Pokemon p WHERE p.id = :id")
public class Pokemon {

    // ID is assigned in tests to avoid collisions
    @Id
    private int id;

    private String name;

    @ManyToMany
    @JoinTable(name = "PERSISTENCE32_POKEMON_TYPE",
               joinColumns = @JoinColumn(
                       name = "POKEMON_ID",
                       referencedColumnName = "ID"
               ),
               inverseJoinColumns = @JoinColumn(
                       name = "TYPE_ID",
                       referencedColumnName = "ID"
               ))
    private Collection<Type> types;

    public Pokemon() {
    }

    public Pokemon(String name, Collection<Type> types) {
        this.name = name;
        this.types = types;
    }

    public Pokemon(int id, String name, Collection<Type> types) {
        this(name, types);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Type> getTypes() {
        return types;
    }

    public void setTypes(Collection<Type> types) {
        this.types = types;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        return id == ((Pokemon) obj).id
                && Objects.equals(name, ((Pokemon) obj).name)
                && Objects.deepEquals(types, ((Pokemon) obj).types);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name);
        for (Type type : types) {
            result = 31 * result + type.hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pokemon {id=");
        sb.append(id);
        sb.append(", name=");
        sb.append(name);
        sb.append(", types=[");
        boolean first = true;
        for (Type type : types) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(type.toString());
        }
        sb.append("]}");
        return sb.toString();
    }

}
