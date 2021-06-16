/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//     05/31/2010-2.1 Guy Pelletier
//       - 314941: multiple joinColumns without referenced column names defined, no error
package org.eclipse.persistence.testing.models.jpa.advanced.compositepk;

import java.util.Vector;
import java.util.Collection;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.OrderBy;
import jakarta.persistence.IdClass;
import jakarta.persistence.OneToMany;

import static jakarta.persistence.FetchType.EAGER;

@Entity
@Table(name="CMP3_DEPARTMENT")
@IdClass(org.eclipse.persistence.testing.models.jpa.advanced.compositepk.DepartmentPK.class)
public class Department {
    private String name;
    private String role;
    private String location;
    private Collection<Scientist> scientists;
    private Collection<Office> offices;
    private Collection<Competency> competencies;

    public Department() {
        scientists = new Vector<Scientist>();
        offices = new Vector<Office>();
        competencies = new Vector<Competency>();
    }

    public void addCompetency(Competency competency) {
        competencies.add(competency);
    }

    public Scientist addScientist(Scientist scientist) {
        scientists.add(scientist);
        scientist.setDepartment(this);
        return scientist;
    }

    @ElementCollection
    @CollectionTable(name="DEPT_COMPETENCIES")
    public Collection<Competency> getCompetencies() {
        return competencies;
    }

    @Id
    public String getLocation() {
        return location;
    }

    @Id
    public String getName() {
        return name;
    }

    @OneToMany(mappedBy="department")
    public Collection<Office> getOffices() {
        return offices;
    }

    public DepartmentPK getPK() {
        return new DepartmentPK(name, role, location);
    }

    @Id
    @Column(name="DROLE")
    public String getRole() {
        return role;
    }

    @OneToMany(fetch=EAGER, mappedBy="department")
    @OrderBy // will default to Scientists composite pk.
    public Collection<Scientist> getScientists() {
        return scientists;
    }

    public Scientist removeScientist(Scientist scientist) {
        scientists.remove(scientist);
        scientist.setDepartment(null);
        return scientist;
    }

    public void setCompetencies(Collection<Competency> competencies) {
        this.competencies = competencies;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOffices(Collection<Office> offices) {
        this.offices = offices;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setScientists(Collection<Scientist> scientists) {
        this.scientists = scientists;
    }
}
