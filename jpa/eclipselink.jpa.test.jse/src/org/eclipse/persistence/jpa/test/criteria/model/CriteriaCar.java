/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2020 IBM Corporation. All rights reserved.
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
//     01/21/2020: Will Dazey
//       - #559346: ClassCastException: DataReadQuery incompatible with ObjectLevelReadQuery
package org.eclipse.persistence.jpa.test.criteria.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;

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
