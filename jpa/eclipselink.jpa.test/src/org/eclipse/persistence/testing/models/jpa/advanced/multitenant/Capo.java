/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     06/1/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 9)
package org.eclipse.persistence.testing.models.jpa.advanced.multitenant;

import java.util.Collection;
import java.util.Vector;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import static javax.persistence.CascadeType.ALL;

@Entity
@DiscriminatorValue("CAPO")
@Table(name="JPA_CAPO")
public class Capo extends Mafioso {
    private Underboss underboss;
    private Collection<Soldier> soldiers;

    public Capo() {
        this.soldiers = new Vector<Soldier>();
    }

    public void addSoldier(Soldier soldier) {
        soldiers.add(soldier);
        soldier.setCapo(this);
    }

    @OneToMany(mappedBy="capo")
    public Collection<Soldier> getSoldiers() {
        return soldiers;
    }

    @ManyToOne
    public Underboss getUnderboss() {
        return underboss;
    }

    @Override
    public boolean isBoss() {
        return false;
    }

    @Override
    public boolean isUnderboss() {
        return false;
    }

    @Override
    public boolean isCapo() {
        return true;
    }

    public void setSoldiers(Collection<Soldier> soldiers) {
        this.soldiers = soldiers;
    }

    public void setUnderboss(Underboss underboss) {
        this.underboss = underboss;
    }
}
