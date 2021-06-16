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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="TPC_BOMB")
public class Bomb extends IndirectWeapon {
    public enum BOMBTYPE { NUCLEAR, DIRTY, RADIOACTIVE }

    @Column(name="B_TYPE")
    private BOMBTYPE bombType;

    public Bomb() {}

    public BOMBTYPE getBombType() {
        return bombType;
    }

    public boolean isBomb() {
        return true;
    }

    public void setBombType(BOMBTYPE bombType) {
        this.bombType = bombType;
    }
}
