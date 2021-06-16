/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     04/28/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 6)
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant;

import java.util.Collection;
import java.util.Vector;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import static jakarta.persistence.CascadeType.ALL;

@Entity
@DiscriminatorValue("CAPO")
@Table(name="DDL_CAPO")
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

    public void setSoldiers(Collection<Soldier> soldiers) {
        this.soldiers = soldiers;
    }

    public void setUnderboss(Underboss underboss) {
        this.underboss = underboss;
    }
}
