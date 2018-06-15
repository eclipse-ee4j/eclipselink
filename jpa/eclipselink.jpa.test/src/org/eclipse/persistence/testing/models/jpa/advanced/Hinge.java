/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     01/15/2015-2.6 Mythily Parthasarathy
//       - initial implementation for bug#457480
package org.eclipse.persistence.testing.models.jpa.advanced;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.config.CacheIsolationType;

@NamedQuery(name = "loadHinges", query = "SELECT h FROM Hinge h WHERE  h.door.id = :doorid")
@Entity
@Table(name = "CMP3_HINGE")
@Cache(isolation = CacheIsolationType.SHARED)
public class Hinge {
    int id;
    Door door;
    int doorId;

    @Id
    public int getId(){
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne(cascade={PERSIST, MERGE}, fetch = FetchType.LAZY, targetEntity = Door.class)
    public Door getDoor() {
        return this.door;
    }

    public void setDoor(Door door) {
        this.door = door;
    }

    @Id
    @Column(name = "DOOR_ID", updatable = false, insertable = false)
    public int getDoorId() {
        return doorId;
    }

    public void setDoorId(int doorId) {
        this.doorId = doorId;
    }
}
