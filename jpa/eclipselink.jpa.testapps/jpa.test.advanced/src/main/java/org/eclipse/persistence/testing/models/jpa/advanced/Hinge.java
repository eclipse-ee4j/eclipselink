/*
 * Copyright (c) 2012, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     01/15/2015-2.6 Mythily Parthasarathy
//       - initial implementation for bug#457480
package org.eclipse.persistence.testing.models.jpa.advanced;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.config.CacheIsolationType;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;

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
