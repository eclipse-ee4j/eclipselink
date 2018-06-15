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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="TPC_KNIFE")
public class Knife extends DirectWeapon {
    private String name;
    private Integer blade;

    @Column(name="KNIFE_TYPE")
    private String type;

    public Knife() {}

    public Integer getBlade() {
        return blade;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isKnife() {
        return true;
    }

    public void setBlade(Integer blade) {
        this.blade = blade;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }
}
