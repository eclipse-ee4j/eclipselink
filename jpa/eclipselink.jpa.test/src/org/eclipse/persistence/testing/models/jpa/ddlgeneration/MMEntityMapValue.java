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
//     07/17/2009 - tware - added tests for DDL generation of maps
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import static javax.persistence.CascadeType.ALL;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name="DDL_MVMM")
public class MMEntityMapValue {

    private int id;
    private List<MapHolder> holders;

    public MMEntityMapValue(){
        holders = new ArrayList<MapHolder>();
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

