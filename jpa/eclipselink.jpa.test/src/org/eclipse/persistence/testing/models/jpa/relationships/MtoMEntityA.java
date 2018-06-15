/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2014, 2015 IBM Corporation.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     06/25/2014-2.5.2 Rick Curtis
//       - 438177: Test M2M map
package org.eclipse.persistence.testing.models.jpa.relationships;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyClass;
import javax.persistence.Table;

@Entity
@Table(name="MTOMENTITYA")
@Cacheable(false)
public class MtoMEntityA {
    @Id
    private int id;

    private String name;

    @ManyToMany
    @JoinTable(name = "MM_MNMK_JT")
    private Map<Integer, MtoMEntityB> entityB;

    @ManyToMany
    private Map<Integer, MtoMEntityB> entityBDefault;

    public MtoMEntityA() {
        entityB = new HashMap<Integer, MtoMEntityB>();
        entityBDefault = new HashMap<Integer, MtoMEntityB>();
    }

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

    public Map<Integer, MtoMEntityB> getEntityB() {
        return entityB;
    }

    public void setEntityB(Map<Integer, MtoMEntityB> entityB) {
        this.entityB = entityB;
    }



    public Map<Integer, MtoMEntityB> getEntityBDefault() {
        return entityBDefault;
    }

    public void setEntityBDefault(Map<Integer, MtoMEntityB> entityBDefault) {
        this.entityBDefault = entityBDefault;
    }

    @Override
    public String toString() {
        return "MtoMEntityA [id=" + id + ", name=" + name + ", entityB="
                + entityB + "]";
    }
}
