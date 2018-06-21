/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     31/05/2012-2.4 Guy Pelletier
//       - 381196: Multitenant persistence units with a dedicated emf should allow for DDL generation.
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyTemporal;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Multitenant;

import static javax.persistence.TemporalType.DATE;
import static org.eclipse.persistence.annotations.MultitenantType.TABLE_PER_TENANT;

@Entity
@Table(name="GEN_TROWEL")
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

