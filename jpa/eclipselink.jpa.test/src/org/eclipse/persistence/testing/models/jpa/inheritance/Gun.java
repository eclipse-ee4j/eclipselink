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

import static org.eclipse.persistence.annotations.OptimisticLockingType.ALL_COLUMNS;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.OptimisticLocking;

@Entity
@Table(name="TPC_GUN")
@OptimisticLocking(type=ALL_COLUMNS)
public class Gun extends DirectWeapon {
    private Integer caliber;
    private String model;

    public Gun() {}

    public Integer getCaliber() {
        return caliber;
    }

    public String getModel() {
        return model;
    }

    public boolean isGun() {
        return true;
    }

    public void setCaliber(Integer caliber) {
        this.caliber = caliber;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
