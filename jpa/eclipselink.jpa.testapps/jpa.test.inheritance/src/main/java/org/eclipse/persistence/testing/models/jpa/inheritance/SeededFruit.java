/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.models.jpa.inheritance;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.JoinFetchType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="CMP3_SEEDED_FRUIT")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(length=1, name="CLASS_TYPE", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("F")
public class SeededFruit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected int id;

    protected String name;

    @OneToMany(mappedBy="seededFruit", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinFetch(JoinFetchType.INNER)
    protected List<Seed> seeds;

    public SeededFruit() {
        super();
        setSeeds(new ArrayList<>());
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

    public List<Seed> getSeeds() {
        return seeds;
    }

    public void setSeeds(List<Seed> seeds) {
        this.seeds = seeds;
    }

    public void addSeed(Seed seed) {
        getSeeds().add(seed);
        seed.setSeededFruit(this);
    }

    public void removeSeed(Seed seed) {
        seed.setSeededFruit(null);
        getSeeds().remove(seed);
    }

}
