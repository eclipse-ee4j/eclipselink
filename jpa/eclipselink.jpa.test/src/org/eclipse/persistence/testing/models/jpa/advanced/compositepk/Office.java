/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - test for bug 280436
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced.compositepk;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
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
        @JoinColumn(name="NAME", referencedColumnName="NAME"),
        @JoinColumn(name="DROLE", referencedColumnName="DROLE")
        })
    private Department department;
    

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
}

