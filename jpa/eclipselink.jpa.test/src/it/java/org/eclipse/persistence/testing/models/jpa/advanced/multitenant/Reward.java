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
//     06/1/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 9)
package org.eclipse.persistence.testing.models.jpa.advanced.multitenant;

import static org.eclipse.persistence.annotations.MultitenantType.SINGLE_TABLE;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.TenantDiscriminatorColumn;

@Entity
@Multitenant(SINGLE_TABLE)
@TenantDiscriminatorColumn(name="T_ID", contextProperty="tenant.id")
@Table(name="JPA_REWARD")
public class Reward {
    private int id;
    private String description;
    private Mafioso mafioso;

    public Reward() {}

    public Reward(String description) {
        this.description = description;
    }

    @Column(name="DESCRIP")
    public String getDescription() {
        return description;
    }

    @Id
    @Column(name="ID")
    @GeneratedValue
    public int getId() {
        return id;
    }

    @ManyToOne
    @JoinColumn(name="MAFIOSO_ID")
    public Mafioso getMafioso() {
        return mafioso;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMafioso(Mafioso mafioso) {
        this.mafioso = mafioso;
    }
}
