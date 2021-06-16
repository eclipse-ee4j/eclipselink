/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     12/12/2008-1.1 Guy Pelletier
//       - 249860: Implement table per class inheritance support.
package org.eclipse.persistence.testing.models.jpa.inheritance;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import org.eclipse.persistence.annotations.IdValidation;
import org.eclipse.persistence.annotations.PrimaryKey;

@Entity
@Table(name="TPC_IND_ELIMINATION")
@PrimaryKey(validation=IdValidation.NULL)
public class IndirectElimination extends Elimination {
    @OneToOne
    @JoinColumn(name="WEAPON_ID")
    private IndirectWeapon indirectWeapon;

    public IndirectElimination() {}

    public IndirectWeapon getIndirectWeapon() {
        return indirectWeapon;
    }

    public boolean isIndirectElimination() {
        return true;
    }

    @Override
    public void setAssassin(Assassin assassin) {
        super.setAssassin(assassin);
        setIndirectWeapon((IndirectWeapon) assassin.getWeapon());
    }

    public void setIndirectWeapon(IndirectWeapon indirectWeapon) {
        this.indirectWeapon = indirectWeapon;
    }
}

