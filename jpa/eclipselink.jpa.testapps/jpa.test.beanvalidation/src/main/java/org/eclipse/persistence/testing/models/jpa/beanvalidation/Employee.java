/*
 * Copyright (c) 2009, 2022 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.models.jpa.beanvalidation;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.Collection;

@Entity(name = "CMP3_BV_EMPLOYEE")
//@EntityListeners({MyListner.class})
public class Employee {
    public static final int NAME_MAX_SIZSE = 5;

    @Id
    private int         id;

    @Basic(fetch = FetchType.LAZY)
    @Max(2000)
    private long salary;

    @Size(max = NAME_MAX_SIZSE)
    private String         name;

    private String         surname;

    @Valid
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="CMP3_BV_EMPLOYEE_PROJECT")
    private Collection<Project> projects;

    @Valid
    @ManyToOne(fetch = FetchType.LAZY)
    private Project managedProject;

    @Valid
    @Embedded
    private Address adress;

   // ===========================================================
   // getters and setters for the state fields

    public Employee() {}

    public Employee(int id, String name, String surname, @Min(1337) long salary) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.salary = salary;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String setName(String name) {
        return this.name = name;
    }

    @Size(max = NAME_MAX_SIZSE)
    public String getSurname() {
        return surname;
    }

    public String setSurname(String surname) {
        return this.surname = surname;
    }

    public void setProjects(Collection<Project> projects) {
        this.projects = projects;
    }

    public void setAddress(Address address) {
        this.adress = address;
    }

    public void setManagedProject(Project project) {
        this.managedProject = project;
    }

    //@PrePersist
    void m1() {
        System.out.println("Prepersit m1() called for Employee" + this);
    }

    @Override public String toString() {
        return "Employee {Id:" +  id + " name:" + name + "}";
    }

}
