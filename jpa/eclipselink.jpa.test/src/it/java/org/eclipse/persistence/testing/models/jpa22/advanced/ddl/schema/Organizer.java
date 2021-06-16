/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     02/04/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
package org.eclipse.persistence.testing.models.jpa22.advanced.ddl.schema;

import static jakarta.persistence.GenerationType.TABLE;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;

@Entity
@Table(name="JPA22_DDL_ORGANIZER", schema="PERSON")
public class Organizer {
    @Id
    @GeneratedValue(strategy=TABLE, generator="JPA22_ORGANIZER_GENERATOR")
    @TableGenerator(
        name="JPA22_ORGANIZER_GENERATOR",
        table="SCHEMA_PK_SEQ",
        schema="GENERATOR",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="ORGANIZER_SEQ"
    )
    public Integer id;
    public String name;

    @ManyToOne
    @JoinColumn(
        name="RACE_ID",
        foreignKey=@ForeignKey(
            name="Organizer_Race_Foreign_Key",
            foreignKeyDefinition="FOREIGN KEY (RACE_ID) REFERENCES JPA22_DDL_RACE (ID)"
        )
    )
    public Race race;

    public Organizer() {}

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Race getRace() {
        return race;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRace(Race race) {
        this.race = race;
    }
}
