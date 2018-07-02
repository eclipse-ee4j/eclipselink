/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     08/28/2008-1.1 Guy Pelletier
//       - 245120: unidir one-to-many within embeddable fails to deploy for missing primary key field
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import static javax.persistence.GenerationType.TABLE;

import javax.persistence.*;

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
