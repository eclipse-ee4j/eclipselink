/*
 * Copyright (c) 2014, 2020 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2014, 2015 IBM Corporation.
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
//     06/25/2014-2.5.2 Rick Curtis
//       - 438177: Test M2M map
package org.eclipse.persistence.testing.models.jpa.relationships;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapKeyClass;
import jakarta.persistence.Table;

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
