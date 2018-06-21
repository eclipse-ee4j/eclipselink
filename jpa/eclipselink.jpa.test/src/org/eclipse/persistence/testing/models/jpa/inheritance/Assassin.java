/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     12/12/2008-1.1 Guy Pelletier
//       - 249860: Implement table per class inheritance support.
package org.eclipse.persistence.testing.models.jpa.inheritance;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="TPC_ASSASSIN")
public class Assassin extends ContractedPersonel {
    @OneToOne(cascade=PERSIST)
    @JoinColumn(name="WEAPON_ID")
    private Weapon weapon;

    @OneToMany(cascade=PERSIST, mappedBy="assassin", fetch=EAGER)
    private List<Elimination> eliminations;

    public List<Elimination> getEliminations() {
        return eliminations;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setEliminations(List<Elimination> eliminations) {
        this.eliminations = eliminations;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
        weapon.setAssassin(this);
    }
}
