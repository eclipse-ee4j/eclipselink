/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     02/04/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
package org.eclipse.persistence.testing.models.jpa22.advanced.ddl.schema;

import static javax.persistence.GenerationType.TABLE;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

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
