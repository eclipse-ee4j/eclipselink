/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//     11/06/2009-2.0 Guy Pelletier
//       - 286317: UniqueConstraint xml element is changing (plus couple other fixes, see bug)
package org.eclipse.persistence.testing.models.jpa.composite.advanced.member_1;

import java.io.Serializable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKey;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.GenerationType.TABLE;
import static org.eclipse.persistence.annotations.CacheType.SOFT_WEAK;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.PrivateOwned;
import org.eclipse.persistence.testing.models.jpa.composite.advanced.member_2.Employee;
import org.eclipse.persistence.testing.models.jpa.composite.advanced.member_3.Equipment;

/**
 * <p>
 * <b>Purpose</b>: Represents the department of an Employee
 * <p>
 * <b>Description</b>: Held in a private 1:1 relationship from Employee
 *
 * @see Employee
 */
@Entity
@Table(name = "MBR1_DEPT")
@NamedNativeQuery(
        name = "findAllSQLDepartments",
        query = "select * from MBR1_DEPT",
        resultClass = Department.class)
@Cache(
    type=SOFT_WEAK,
    size=777
)
public class Department implements Serializable {
    private Integer id;
    private String name;
    private Employee departmentHead;

    private Collection<Employee> employees;
    private Collection<Employee> managers;

    private Map<Integer, Equipment> equipment;

    public Department() {
        this("");
    }

    public Department(String name) {
        this.name = name;
        this.managers = new Vector();
        this.equipment = new HashMap<Integer, Equipment>();
    }

    public void addEquipment(Equipment e) {
        this.equipment.put(e.getId(), e);
        e.setDepartment(this);
    }

    public void addManager(Employee employee) {
        if (employee != null && managers != null && !managers.contains(employee)) {
            this.managers.add(employee);
        }
    }

    @OneToMany(mappedBy = "department")
    public Collection<Employee> getEmployees() {
        return employees;
    }

    @OneToMany(mappedBy = "department")
    @MapKey
    @PrivateOwned
    public Map<Integer, Equipment> getEquipment() {
        return equipment;
    }

    @Id
    @GeneratedValue(strategy = TABLE, generator = "DEPARTMENT_TABLE_GENERATOR")
    @TableGenerator(
            name = "DEPARTMENT_TABLE_GENERATOR",
            table = "MBR1_SEQ",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_COUNT",
            pkColumnValue = "DEPT_SEQ")
    public Integer getId() {
        return id;
    }

    // To test default 1-M mapping
    @OneToMany(cascade = PERSIST)
    // Explicitly specified name for JoinTable to keep the naming pattern: all tables' names defined in a i-th composite member commence with MBRi_
    @JoinTable(name="MBR2_DEPT_EMP")
    @PrivateOwned
    public Collection<Employee> getManagers() {
        return managers;
    }

    public String getName() {
        return name;
    }

    public void setEmployees(Collection<Employee> employees) {
        this.employees = employees;
    }

    public void setEquipment(Map<Integer, Equipment> equipment) {
        this.equipment = equipment;
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

    @OneToOne(optional=true)
    @JoinColumn(name="DEPT_HEAD", nullable=true)
    public Employee getDepartmentHead() {
        return this.departmentHead;
    }

    public void setDepartmentHead(Employee employee) {
        this.departmentHead = employee;
    }

}
