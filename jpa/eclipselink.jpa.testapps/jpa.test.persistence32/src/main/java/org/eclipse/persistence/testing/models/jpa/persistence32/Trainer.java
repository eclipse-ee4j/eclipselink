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

import java.util.List;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name="PERSISTENCE32_TRAINER")
@NamedQuery(name="Trainer.get", query="SELECT t FROM Trainer t WHERE t.id = :id")
@NamedNativeQuery(name="Trainer.deleteAll", query="DELETE FROM PERSISTENCE32_TRAINER")
public class Trainer {

    // ID is assigned in tests to avoid collisions
    @Id
    private int id;

    private String name;

    @ManyToOne(fetch = LAZY)
    private Team team;

    @OneToMany(mappedBy = "trainer")
    private List<Pokemon> pokemons;

    public Trainer() {
    }

    public Trainer(int id, String name, Team team) {
        this.id = id;
        this.name = name;
        this.team = team;
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

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public List<Pokemon> getPokemons() {
        return pokemons;
    }

    public void setPokemons(List<Pokemon> pokemons) {
        this.pokemons = pokemons;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        return id == ((Trainer) obj).id
                && Objects.equals(name, ((Trainer) obj).name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

}
