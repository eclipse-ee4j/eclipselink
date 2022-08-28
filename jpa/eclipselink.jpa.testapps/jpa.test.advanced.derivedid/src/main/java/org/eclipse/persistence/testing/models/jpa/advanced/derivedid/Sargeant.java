/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     04/24/2009-2.0 Guy Pelletier
//       - 270011: JPA 2.0 MappedById support
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;

import static jakarta.persistence.GenerationType.TABLE;

/**
 * This model tests Example #1 of the mapsId cases (mapped from MasterCorporal)
 *
 * @author gpelleti
 */
@Entity
@Table(name="JPA_SARGEANT")
public class Sargeant {
    @Id
    @Column(name="ID")
    @GeneratedValue(strategy=TABLE, generator="SARGEANT_TABLE_GENERATOR")
    @TableGenerator(
        name="SARGEANT_TABLE_GENERATOR",
        table="JPA_SARGEANT_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="SARGEANT_SEQ",
        initialValue=50
    )
    long sargeantId;

    @Basic
    @Column(name="NAME")
    String name;

    public String getName() {
        return name;
    }

    public long getSargeantId() {
        return sargeantId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSargeantId(long sargeantId) {
        this.sargeantId = sargeantId;
    }
}
