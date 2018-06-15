/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.models.jpa.inheritance;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Id;

import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.JoinFetchType;

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
        setSeeds(new ArrayList<Seed>());
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
