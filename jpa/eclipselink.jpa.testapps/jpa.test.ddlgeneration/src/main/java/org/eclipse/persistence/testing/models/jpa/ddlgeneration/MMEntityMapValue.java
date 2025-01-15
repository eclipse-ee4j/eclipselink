/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     07/17/2009 - tware - added tests for DDL generation of maps
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;

@Entity
@Table(name="DDL_MVMM")
public class MMEntityMapValue {

    private int id;
    private List<MapHolder> holders;

    public MMEntityMapValue(){
        holders = new ArrayList<>();
    }

    @Id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToMany(cascade=ALL, mappedBy="MTMMap")
    public List<MapHolder> getHolders() {
        return holders;
    }

    public void setHolders(List<MapHolder> holders) {
        this.holders = holders;
    }


}

