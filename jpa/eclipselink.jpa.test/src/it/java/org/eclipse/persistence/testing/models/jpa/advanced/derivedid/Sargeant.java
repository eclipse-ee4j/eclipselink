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
//     04/24/2009-2.0 Guy Pelletier
//       - 270011: JPA 2.0 MappedById support
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import static jakarta.persistence.GenerationType.TABLE;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;

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
