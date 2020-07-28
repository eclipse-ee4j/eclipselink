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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.relationships.manyToMany;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.TableGenerator;
import javax.persistence.GenerationType;

@Entity
@Table(name="CMP3_ENTITYD")
public class EntityD
{
    private int id;
    private String name;

    public EntityD() {
    }

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE, generator="ENTITYD_TABLE_GENERATOR")
    @TableGenerator(
        name="ENTITYD_TABLE_GENERATOR",
        table="CMP3_ENTITYD_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="ENTITYD_SEQ"
    )
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
