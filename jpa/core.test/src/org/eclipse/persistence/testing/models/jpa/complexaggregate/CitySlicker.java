/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import java.io.Serializable;

import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.EmbeddedId;

@Entity
@Table(name="CMP3_CITYSLICKER")
public class CitySlicker implements Serializable {
    private int age;
    private Name name;
    private World world;
    private String gender;
    
	public CitySlicker () {}

    @Column(name="AGE")
	public int getAge() { 
        return age; 
    }
    
    @Column(name="GENDER")
	public String getGender() { 
        return gender; 
    }
        
    @EmbeddedId
	public Name getName() { 
        return name; 
    }
    
    @ManyToOne
    public World getWorld() {
        return world;
    }
    
	public void setAge(int age) { 
        this.age = age; 
    }

	public void setGender(String gender) { 
        this.gender = gender; 
    }
    
    public void setName(Name name) { 
        this.name = name; 
    }
    
    public void setWorld(World world) {
        this.world = world;
    }
    
    public String toString() {
        return "CitySlicker: " + getName().toString();
    }
}
