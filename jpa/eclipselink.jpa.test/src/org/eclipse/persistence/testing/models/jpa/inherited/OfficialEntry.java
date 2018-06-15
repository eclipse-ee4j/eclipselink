/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     02/18/2010-2.0.2 Guy Pelletier
//       - 294803: @Column(updatable=false) has no effect on @Basic mappings
package org.eclipse.persistence.testing.models.jpa.inherited;

import static javax.persistence.GenerationType.TABLE;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.TableGenerator;

@Entity(name="JPA_OFFICIAL_ENTRY")
public class OfficialEntry {
    @Id
    @GeneratedValue(strategy=TABLE, generator="OFFICIAL_ENTRY_TABLE_GENERATOR")
    @TableGenerator(
        name="OFFICIAL_ENTRY_TABLE_GENERATOR",
        table="CMP3_BEER_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="OFFICIAL_ENTRY_SEQ")
    private int id;

    public OfficialEntry() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
