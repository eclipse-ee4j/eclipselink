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
//     02/18/2010-2.0.2 Guy Pelletier
//       - 294803: @Column(updatable=false) has no effect on @Basic mappings
package org.eclipse.persistence.testing.models.jpa.inherited;

import static jakarta.persistence.GenerationType.TABLE;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.TableGenerator;

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
