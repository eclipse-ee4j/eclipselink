/*
 * Copyright (c) 2013, 2020 Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

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
