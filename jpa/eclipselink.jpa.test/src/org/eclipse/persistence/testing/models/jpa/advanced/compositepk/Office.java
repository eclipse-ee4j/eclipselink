/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - test for bug 280436
 *     02/02/2011-2.3 Chris Delahunt 
 *       - 336122: ValidationException thrown for JoinColumns on OneToMany with composite primary key
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced.compositepk;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="CMP3_OFFICE")
@IdClass(org.eclipse.persistence.testing.models.jpa.advanced.compositepk.OfficePK.class)
public class Office {

    @Id
    protected int id;
    
    @Id
    protected String location;
    
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name="LOCATION", referencedColumnName="LOCATION", insertable=false, updatable=false),
        @JoinColumn(name="NAME", referencedColumnName="name"), //<- this is testing case insensitivity
        @JoinColumn(name="DROLE", referencedColumnName="drole") //<- this is testing case insensitivity
        })
    private Department department;
    
    //added for bug 336122
    @OneToMany
    @JoinColumns({
        @JoinColumn(name="OFFICE_ID", referencedColumnName="ID"),
        @JoinColumn(name="OFFICE_LOC", referencedColumnName="LOCATION")
    })
    private Collection<Cubicle> cubicles;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Collection<Cubicle> getCubicles() {
        return cubicles;
    }

    public void setCubicles(Collection<Cubicle> cubicles) {
        this.cubicles = cubicles;
    }
}

