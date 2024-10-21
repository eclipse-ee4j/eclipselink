/*
 * Copyright (c) 2012, 2024 Oracle and/or its affiliates. All rights reserved.
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
//    Vikram Bhatia - initial implementation
package org.eclipse.persistence.testing.models.jpa.advanced;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.QueryHint;
import jakarta.persistence.Table;
import org.eclipse.persistence.config.QueryHints;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="CMP3_ROOM")
@NamedQueries({
    //Bug 501272
    @NamedQuery(
        name = "room.findSimpleRoomByWidthLength",
        query = "SELECT NEW org.eclipse.persistence.testing.models.jpa.advanced.SimpleRoom(r.id, r.width, r.length) " +
                "FROM Room r WHERE r.width = :width AND r.length = :length",
        hints = {
                @QueryHint(name = QueryHints.QUERY_RESULTS_CACHE, value = "true"),
        })
})
public class Room implements Serializable, Cloneable {

    public enum Status {
        FREE, OCCUPIED;
    }

    @Id
    private int id;
    private int width;
    private int length;
    private int height;
    private Status status;

    @OneToMany(mappedBy="room", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private List<Door> doors;

    public Room() {
    }

    public Room(int id, int width, int length, int height, Status status) {
        this.id = id;
        this.width = width;
        this.length = length;
        this.height = height;
        this.status = status;
        this.doors = doors;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Door> getDoors() {
        return doors;
    }

    public void setDoors(List<Door> doors) {
        this.doors = doors;
    }

    public void addDoor(Door door) {
        if (doors == null) {
            doors = new ArrayList<>();
        }
        doors.add(door);
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Room r)) {
            return false;
        }

        if (this.id == r.id && this.width == r.width
                && this.height == r.height && this.length == r.length) {
            return true;
        }

        return false;
    }

    public int hashCode() {
        return (this.height + 100);
    }

    public boolean isHeightSetGreaterThanZero() {
        try {
            Field f = this.getClass().getDeclaredField("height");
            f.setAccessible(true);

            return f.getInt(this) > 0;
        } catch (Exception ex) {
            return false;
        }
    }
}
