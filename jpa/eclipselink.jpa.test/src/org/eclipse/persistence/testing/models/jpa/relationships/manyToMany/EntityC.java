/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.relationships.manyToMany;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.TableGenerator;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.GenerationType;
import java.util.Collection;
import java.util.HashSet;

@Entity
@Table(name="CMP3_ENTITYC")
public class EntityC
{
    private int id;
    private String name;
    private Collection<EntityD> ds = new HashSet<EntityD>();

    public EntityC() {
    }

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE, generator="ENTITYC_TABLE_GENERATOR")
    @TableGenerator(
        name="ENTITYC_TABLE_GENERATOR",
        table="CMP3_ENTITYC_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="ENTITYC_SEQ"
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

    // From what I can see there are no tests against this model (it looks
    // like it was built for processing testing actually) therefore,
    // no one depends on the cascade setting so I am commenting out the
    // @OneToMany annotation to test our defaulting of that annotation.
    //@OneToMany(cascade={CascadeType.ALL})
    @JoinTable(
            name="CMP3_UNIDIR_ENTITYC_ENTITYD",
            joinColumns=
            @JoinColumn(name="ENTITYC_ID", referencedColumnName="ID"),
            inverseJoinColumns=
            @JoinColumn(name="ENTITYD_ID", referencedColumnName="ID")
    )
    public Collection<EntityD> getDs() {
        return ds;
    }
    public void setDs(Collection<EntityD> ds) {
        this.ds = ds;
    }
}
