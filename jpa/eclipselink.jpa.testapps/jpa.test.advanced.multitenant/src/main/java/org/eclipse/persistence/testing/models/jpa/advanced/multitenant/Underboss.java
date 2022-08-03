/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.Collection;
import java.util.Vector;

import static jakarta.persistence.CascadeType.ALL;

@Entity
@DiscriminatorValue("LITTLE_DON")
@Table(name="JPA_UNDERBOSS")
public class Underboss extends Mafioso {
    private Boss boss;
    private Collection<Capo> capos;

    public Underboss() {
        this.capos = new Vector<>();
    }

    public void addCapo(Capo capo) {
        this.capos.add(capo);
        capo.setUnderboss(this);
    }

    @OneToOne(cascade=ALL, mappedBy="underboss")
    public Boss getBoss() {
        return boss;
    }

    @OneToMany(mappedBy="underboss")
    public Collection<Capo> getCapos() {
        return capos;
    }

    @Override
    public boolean isBoss() {
        return false;
    }

    @Override
    public boolean isUnderboss() {
        return true;
    }

    public void setBoss(Boss boss) {
        this.boss = boss;
    }

    public void setCapos(Collection<Capo> capos) {
        this.capos = capos;
    }
}
