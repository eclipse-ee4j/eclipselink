/*
 * Copyright (c) 2023, 2025 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import static jakarta.persistence.EnumType.ORDINAL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name="PERSISTENCE32_TRAINER")
@NamedQuery(name="Trainer.get", query="SELECT t FROM Trainer t WHERE t.id = :id")
@NamedNativeQuery(name="Trainer.deleteAll", query="DELETE FROM PERSISTENCE32_TRAINER")
@NamedEntityGraph(name = "Trainer.fetchGraph",
                  attributeNodes = {
                          @NamedAttributeNode("name"),
                          @NamedAttributeNode(value = "team", subgraph = "team")
                  },
                  subgraphs = @NamedSubgraph(name = "team",
                                             attributeNodes = @NamedAttributeNode("name"))
)
public class Trainer {

    // ID is assigned in tests to avoid collisions
    @Id
    @Column(name = "ID")
    private int id;

    private String name;

    @ManyToOne(fetch = LAZY)
    private Team team;

    @Column(name = "STATUS_ORDINAL")
    @Enumerated(ORDINAL)
    private TrainerStatusOrdinal statusOrdinal;

    @Column(name = "STATUS_STRING")
    @Enumerated(STRING)
    private TrainerStatusString statusString;

    @OneToMany(mappedBy = "trainer")
    private List<Pokemon> pokemons;

    public Trainer() {
    }

    public Trainer(int id, String name, Team team, TrainerStatusOrdinal statusOrdinal, TrainerStatusString statusString) {
        this.id = id;
        this.name = name;
        this.team = team;
        this.statusOrdinal = statusOrdinal;
        this.statusString = statusString;
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

    public TrainerStatusOrdinal getStatusOrdinal() {
        return statusOrdinal;
    }

    public void setStatusOrdinal(TrainerStatusOrdinal statusOrdinal) {
        this.statusOrdinal = statusOrdinal;
    }

    public TrainerStatusString getStatusString() {
        return statusString;
    }

    public void setStatusString(TrainerStatusString statusString) {
        this.statusString = statusString;
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
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Trainer trainer = (Trainer) o;
        return id == trainer.id
                && Objects.equals(name, trainer.name)
                && Objects.equals(team, trainer.team)
                && statusOrdinal == trainer.statusOrdinal
                && statusString == trainer.statusString
                && Objects.equals(pokemons, trainer.pokemons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
