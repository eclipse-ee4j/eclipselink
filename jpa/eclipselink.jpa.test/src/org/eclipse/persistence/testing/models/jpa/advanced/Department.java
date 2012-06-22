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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     11/06/2009-2.0 Guy Pelletier 
 *       - 286317: UniqueConstraint xml element is changing (plus couple other fixes, see bug)
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.io.Serializable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
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

/**
 * <p>
 * <b>Purpose</b>: Represents the department of an Employee
 * <p>
 * <b>Description</b>: Held in a private 1:1 relationship from Employee
 * 
 * @see Employee
 */
@Entity(name = "ADV_DEPT")
@Table(name = "CMP3_DEPT")
@NamedNativeQuery(
        name = "findAllSQLDepartments",
        query = "select * from CMP3_DEPT",
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
            table = "CMP3_DEPARTMENT_SEQ",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_COUNT",
            pkColumnValue = "DEPT_SEQ")
    public Integer getId() {
        return id;
    }

    // To test default 1-M mapping
    @OneToMany(cascade = PERSIST)
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
