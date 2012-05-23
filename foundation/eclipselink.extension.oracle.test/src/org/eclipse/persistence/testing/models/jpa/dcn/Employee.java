/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.dcn;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.OptimisticLocking;

@Entity
@IdClass(EmployeeID.class)
@Table(name="DCN_EMPLOYEE")
@SecondaryTable(name="DCN_SALARY")
@OptimisticLocking(cascade=true)
public class Employee implements Serializable {

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Integer id;

    @Id
    // Test composite ids
    private int id2 = 12345;

    @Column(name="NAME")
    private String name;
    
    @Column(name="SALARY", table="DCN_SALARY")
    private long salary;
    
    @ElementCollection
    @CollectionTable(name="DCN_RESPONS")
    private List<String> responsiblities = new ArrayList<String>();
    
    @ManyToMany
    @JoinTable(name="DCN_PROJ_EMP")
    private List<Project> projects = new ArrayList<Project>();
    
    @OneToMany(mappedBy="manager")
    private List<Employee> managedEmployees = new ArrayList<Employee>();
    
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name="MG_ID", referencedColumnName="ID"),
        @JoinColumn(name="MG_ID2", referencedColumnName="ID2")})
    private Employee manager;

    @Version
    @Column(name="VERSION")
    private Integer version;
     
    public Employee() {}

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVersion() {
        return version; 
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
    
    public int getId2() {
        return id2;
    }

    public void setId2(int id2) {
        this.id2 = id2;
    }

    public List<String> getResponsiblities() {
        return responsiblities;
    }

    public void setResponsiblities(List<String> responsiblities) {
        this.responsiblities = responsiblities;
    }

    public long getSalary() {
        return salary;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public List<Employee> getManagedEmployees() {
        return managedEmployees;
    }

    public void setManagedEmployees(List<Employee> managedEmployees) {
        this.managedEmployees = managedEmployees;
    }

    public Employee getManager() {
        return manager;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }
    
    public boolean equals(Object object) {
        if (object instanceof Employee) {
            if (((Employee)object).getId() != null && ((Employee)object).getId().equals(this.id)) {
                return true;
            }
        }
        return super.equals(object);
    }
}

