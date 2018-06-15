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
import javax.persistence.Table;

@Entity
@Table(name="TPC_IND_WEAPON")
public class IndirectWeapon extends Weapon {
    public IndirectWeapon() {}

    public boolean isPoison() {
        return false;
    }

    public boolean isBomb() {
        return false;
    }

    public boolean isIndirectWeapon() {
        return true;
    }
}
