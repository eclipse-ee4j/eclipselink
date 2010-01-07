/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  

package org.eclipse.persistence.testing.models.jpa.advanced.compositepk;

import java.util.Vector;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.IdClass;
import javax.persistence.OneToMany;

import static javax.persistence.FetchType.EAGER;

@Entity
@Table(name="CMP3_DEPARTMENT")
@IdClass(org.eclipse.persistence.testing.models.jpa.advanced.compositepk.DepartmentPK.class)
public class Department {
    private String name;
    private String role;
    private String location;
    private Collection<Scientist> scientists;
    private Collection<Office> offices;

    public Department() {
        scientists = new Vector<Scientist>();
        offices = new Vector<Office>();
    }

    @Id
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Id
    @Column(name="DROLE")
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Id
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @OneToMany(fetch=EAGER, mappedBy="department")
    @OrderBy // will default to Scientists composite pk.
    public Collection<Scientist> getScientists() {
        return scientists;
    }

    public void setScientists(Collection<Scientist> scientists) {
        this.scientists = scientists;
    }
    

    @OneToMany(mappedBy="department")
    public Collection<Office> getOffices() {
        return offices;
    }

    public void setOffices(Collection<Office> offices) {
        this.offices = offices;
    }

    public Scientist addScientist(Scientist scientist) {
        scientists.add(scientist);
        scientist.setDepartment(this);
        return scientist;
    }

    public Scientist removeScientist(Scientist scientist) {
        scientists.remove(scientist);
        scientist.setDepartment(null);
        return scientist;
    }
    
    public DepartmentPK getPK() {
        return new DepartmentPK(name, role, location);
    }
}
