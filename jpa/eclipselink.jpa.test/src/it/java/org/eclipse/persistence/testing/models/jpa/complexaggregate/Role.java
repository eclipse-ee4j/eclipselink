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
//     08/28/2008-1.1 Guy Pelletier
//       - 245120: unidir one-to-many within embeddable fails to deploy for missing primary key field
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import static jakarta.persistence.GenerationType.TABLE;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name="CMP3_ROLE")
public class Role implements Serializable {
    private int id;
    private String description;

    public Role() {}

    public Role(String description) {
        this.description = description;
    }

    @Column(name="DESCRIP")
    public String getDescription() {
        return description;
    }

    @Id
    @GeneratedValue(strategy=TABLE, generator="ROLE_TABLE_GENERATOR")
    @TableGenerator(
        name="ROLE_TABLE_GENERATOR",
        table="CMP3_HOCKEY_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="ROLE_SEQ"
    )
    public int getId() {
        return id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
        return "Role: Id [" + id + "], Description [" + description + "]";
    }
}
