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

import static javax.persistence.CascadeType.*;
import static javax.persistence.GenerationType.TABLE;

import javax.persistence.*;

import java.util.Collection;
import java.util.HashSet;

@Entity
@Table(name="CMP3_ENTITYB")
public class EntityB
{
    private int id;
    private String name;
    private Collection<EntityA> as;

    public EntityB() {
        as = new HashSet<EntityA>();
    }

    @Id
    @GeneratedValue(strategy=TABLE, generator="ENTITYB_TABLE_GENERATOR")
    @TableGenerator(
        name="ENTITYB_TABLE_GENERATOR",
        table="CMP3_ENTITYB_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="ENTITYB_SEQ"
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

    @ManyToMany(cascade=REMOVE, mappedBy="bs")
    public Collection<EntityA> getAs() {
        return as;
    }
    public void setAs(Collection<EntityA> as) {
        this.as = as;
    }
}
