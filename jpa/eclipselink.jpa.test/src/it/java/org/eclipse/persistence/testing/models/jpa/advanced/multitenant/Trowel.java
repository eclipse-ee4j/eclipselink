/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     14/05/2012-2.4 Guy Pelletier
//       - 376603: Provide for table per tenant support for multitenant applications
package org.eclipse.persistence.testing.models.jpa.advanced.multitenant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import org.eclipse.persistence.annotations.Multitenant;

import static jakarta.persistence.TemporalType.DATE;
import static org.eclipse.persistence.annotations.MultitenantType.TABLE_PER_TENANT;

@Entity
@Table(name="JPA_TROWEL")
@Multitenant(TABLE_PER_TENANT)
// Default to suffix.
public class Trowel {
    public int id;
    public String type;
    public Mason mason;

    public Trowel() {}

    @Id
    @GeneratedValue
    public int getId() {
        return id;
    }

    @OneToOne(mappedBy="trowel")
    public Mason getMason() {
        return mason;
    }

    @Column(name="TROWEL_TYPE")
    public String getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMason(Mason mason) {
        this.mason = mason;
    }

    public void setType(String type) {
        this.type = type;
    }
}

