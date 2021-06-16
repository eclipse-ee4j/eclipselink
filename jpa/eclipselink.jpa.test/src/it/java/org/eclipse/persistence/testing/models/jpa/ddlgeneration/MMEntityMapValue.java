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
//     07/17/2009 - tware - added tests for DDL generation of maps
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import static jakarta.persistence.CascadeType.ALL;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

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

