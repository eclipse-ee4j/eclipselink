/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     03/04/09 tware - test for bug 350599 copied from advanced model
package org.eclipse.persistence.testing.models.jpa.xml.advanced;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.GenerationType.TABLE;
import static org.eclipse.persistence.annotations.CacheType.SOFT_WEAK;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKey;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.Transient;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * <p>
 * <b>Purpose</b>: Represents the department of an Employee
 * <p>
 * <b>Description</b>: Held in a private 1:1 relationship from Employee
 *
 * @see Employee
 */
public class Department implements Serializable {
    private Integer id;
    private String name;
    private Employee departmentHead;

    private Collection<Employee> employees;

    private Map<Integer, Equipment> equipment;

    public Department() {
        this("");
    }

    public Department(String name) {
        this.name = name;
        this.equipment = new HashMap<Integer, Equipment>();
    }

    public void addEquipment(Equipment e) {
        this.equipment.put(e.getId(), e);
        e.setDepartment(this);
    }

    public Collection<Employee> getEmployees() {
        return employees;
    }

    @Transient
    public Map<Integer, Equipment> getEquipment() {
        return equipment;
    }

    public Integer getId() {
        return id;
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

    public void setName(String name) {
        this.name = name;
    }

    public Employee getDepartmentHead() {
        return this.departmentHead;
    }

    public void setDepartmentHead(Employee employee) {
        this.departmentHead = employee;
    }

}
