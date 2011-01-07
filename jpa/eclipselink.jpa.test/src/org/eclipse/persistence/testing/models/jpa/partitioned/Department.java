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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     11/06/2009-2.0 Guy Pelletier 
 *       - 286317: UniqueConstraint xml element is changing (plus couple other fixes, see bug)
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.partitioned;

import java.io.Serializable;

import java.util.Collection;
import java.util.Vector;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.HashPartitioning;
import org.eclipse.persistence.annotations.Partitioned;
import org.eclipse.persistence.annotations.UnionPartitioning;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.GenerationType.TABLE;

/**
 * <p>
 * <b>Purpose</b>: Represents the department of an Employee
 * <p>
 * <b>Description</b>: Held in a private 1:1 relationship from Employee
 * 
 * @see Employee
 */
@Entity
@Table(name = "PART_DEPT")
@HashPartitioning(
    name="HashPartitioningByID",
    partitionColumn=@Column(name="ID"),
    unionUnpartitionableQueries=true,
    connectionPools={"node2", "node3"})
@UnionPartitioning(
        name="UnionPartitioningAllNodes",
        replicateWrites=true)
@Partitioned("HashPartitioningByID")
public class Department implements Serializable {
    private Integer id;
    private String name;
    private Employee departmentHead;

    private Collection<Employee> employees;
    private Collection<Employee> managers;

    public Department() {
        this("");
    }

    public Department(String name) {
        this.name = name;
        this.managers = new Vector();
    }

    public void addManager(Employee employee) {
        if (employee != null && managers != null && !managers.contains(employee)) {
            this.managers.add(employee);
        }
    }

    @OneToMany(cascade = PERSIST, mappedBy = "department")
    public Collection<Employee> getEmployees() {
        return employees;
    }

    @Id
    @GeneratedValue(strategy = TABLE, generator = "DEPARTMENT_TABLE_GENERATOR")
    public Integer getId() {
        return id;
    }

    // To test default 1-M mapping
    @OneToMany(cascade = PERSIST)
    @Partitioned("UnionPartitioningAllNodes")
    public Collection<Employee> getManagers() {
        return managers;
    }

    public String getName() {
        return name;
    }

    public void setEmployees(Collection<Employee> employees) {
        this.employees = employees;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setManagers(Collection<Employee> managers) {
        this.managers = managers;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @OneToOne(cascade = PERSIST, optional=true)
    @JoinColumns({
        @JoinColumn(name="DEPT_HEAD", referencedColumnName="EMP_ID"),
        @JoinColumn(name="DEPT_HEAD_LOCATION", referencedColumnName="LOCATION")})
    public Employee getDepartmentHead() {
        return this.departmentHead;
    }
    
    public void setDepartmentHead(Employee employee) {
        this.departmentHead = employee;
    }

}
