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
//    Vikram Bhatia - initial implementation
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.eclipse.persistence.config.QueryHints;

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
    @Id
    private int id;
    private int width;
    private int length;
    private int height;

    @OneToMany(mappedBy="room", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private Collection<Door> doors;

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

    public Collection<Door> getDoors() {
        return doors;
    }

    public void setDoors(Collection<Door> doors) {
        this.doors = doors;
    }

    public void addDoor(Door door) {
        if (doors == null) {
            doors = new ArrayList<Door>();
        }
        doors.add(door);
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Room)) {
            return false;
        }

        Room r = (Room) obj;
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
