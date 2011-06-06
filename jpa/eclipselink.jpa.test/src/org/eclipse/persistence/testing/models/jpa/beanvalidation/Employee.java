/*******************************************************************************
 * Copyright (c) 2009 Sun Microsystems, Inc. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 ******************************************************************************/

package org.eclipse.persistence.testing.models.jpa.beanvalidation;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.validation.constraints.Max;
import javax.validation.Valid;
import java.util.Collection;

@Entity(name = "CMP3_BV_EMPLOYEE")
//@EntityListeners({MyListner.class})
public class Employee {
    public static final int NAME_MAX_SIZSE = 5;

    @Id
    private int	     id;

    @Basic(fetch = FetchType.LAZY)
    @Max(1000)
    private long salary;

    @Size(max = NAME_MAX_SIZSE)
    private String	     name;

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

    public Employee(int id, String name, long salary) {
        this.id = id;
        this.name = name;
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