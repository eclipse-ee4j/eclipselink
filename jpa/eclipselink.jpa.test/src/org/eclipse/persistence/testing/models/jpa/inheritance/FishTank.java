/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Model a simple Entity referencing an abstract Entity with Joined Inheritance,
 * via a unidirectional lazy 1:M mapping.
 */
@Entity
@Table(name="JPA_FISH_TANK")
@NamedQueries( {
    @NamedQuery(name="findAllFishTanks", query="select o from FishTank o order by o.id") })
public class FishTank {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(nullable=false)
    private Long id;

    @Version
    protected int version;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name="TANK_ID", insertable=false, updatable=false)
    private List<Fish> fishes;

    public FishTank() {
        super();
        this.fishes = new ArrayList<Fish>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<Fish> getFishes() {
        return this.fishes;
    }

    public void setFishes(List<Fish> fishes) {
        this.fishes = fishes;
    }

}
