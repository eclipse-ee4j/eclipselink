/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Vikram Bhatia - initial implementation 
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="CMP3_ROOM")
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
}
