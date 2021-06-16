/*
 * Copyright (c) 2020, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     01/21/2020: Will Dazey
//       - #559346: ClassCastException: DataReadQuery incompatible with ObjectLevelReadQuery
package org.eclipse.persistence.jpa.test.criteria.model;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;

@Entity
@IdClass(value = CriteriaCarId.class)
public class CriteriaCar {

    @Id
    @Column(name = "CAR_ID")
    private String id;

    @Id
    @Column(name = "CAR_VER")
    private int version;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "component")
    @Column(name = "origin", updatable = true, insertable = true)
    @CollectionTable(name = "criteria_car_origin", joinColumns = {
        @JoinColumn(name = "CAR_ID", referencedColumnName = "CAR_ID"),
        @JoinColumn(name = "CAR_VER", referencedColumnName = "CAR_VER")
    })
    private Map<String, String> origin = new HashMap<String, String>();

    public CriteriaCar() { }

    public CriteriaCar(String id, int version, Map<String, String> origin) {
        this.id = id;
        this.version = version;
        this.origin = origin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Map<String, String> getOrigin() {
        return origin;
    }

    public void setOrigin(Map<String, String> origin) {
        this.origin = origin;
    }
}
