/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     01/15/2015-2.6 Mythily Parthasarathy
//       - initial implementation for bug#457480
package org.eclipse.persistence.testing.models.jpa.advanced;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

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
