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
//     12/12/2008-1.1 Guy Pelletier
//       - 249860: Implement table per class inheritance support.
package org.eclipse.persistence.testing.models.jpa.inheritance;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.IdValidation;
import org.eclipse.persistence.annotations.PrimaryKey;

@Entity
@Table(name="TPC_DIR_ELIMINATION")
@PrimaryKey(validation=IdValidation.NULL)
public class DirectElimination extends Elimination {
    @OneToOne
    @JoinColumn(name="WEAPON_ID")
    private DirectWeapon directWeapon;

    public DirectElimination() {}

    public DirectWeapon getDirectWeapon() {
        return directWeapon;
    }

    public boolean isDirectElimination() {
        return true;
    }

    @Override
    public void setAssassin(Assassin assassin) {
        super.setAssassin(assassin);
        setDirectWeapon((DirectWeapon) assassin.getWeapon());
    }

    public void setDirectWeapon(DirectWeapon directWeapon) {
        this.directWeapon = directWeapon;
    }
}

