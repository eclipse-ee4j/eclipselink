/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.interfaces;

import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

import java.util.Vector;

public class PersonnelManager implements ManagerialJob, java.io.Serializable {
    public Number jobCode;
    public Float salary;
    public String department;
    public ValueHolderInterface managedEmployees;

    public PersonnelManager() {
        this.managedEmployees = new ValueHolder();
    }

    public static PersonnelManager example1() {
        PersonnelManager example = new PersonnelManager();

        example.setDepartment("Division A");
        example.setSalary(77235.00f);

        Vector employees = new Vector(5);
        employees.add(CourseDeveloper.example1());
        employees.add(ProductDeveloper.example1());
        employees.add(Secretary.example1());
        employees.add(Receptionist.example1());
        employees.add(ProductManager.example1());

        example.setManagedEmployees(employees);

        return example;
    }

    public static PersonnelManager example2() {
        PersonnelManager example = new PersonnelManager();

        example.setDepartment("Division B");
        example.setSalary(97235.00f);

        Vector employees = new Vector(5);
        employees.add(CourseDeveloper.example2());
        employees.add(ProductDeveloper.example2());
        employees.add(Secretary.example2());
        employees.add(Receptionist.example2());
        employees.add(ProductManager.example2());

        example.setManagedEmployees(employees);

        return example;
    }

    public static PersonnelManager example3() {
        PersonnelManager example = new PersonnelManager();

        example.setDepartment("Division C");
        example.setSalary(87265.00f);

        Vector employees = new Vector(5);
        employees.add(CourseDeveloper.example3());
        employees.add(ProductDeveloper.example3());
        employees.add(Secretary.example3());
        employees.add(Receptionist.example3());
        employees.add(ProductManager.example3());

        example.setManagedEmployees(employees);

        return example;
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public Number getJobCode() {
        return jobCode;
    }

    @Override
    public Vector getManagedEmployees() {
        return (Vector)managedEmployees.getValue();
    }

    @Override
    public Float getSalary() {
        return salary;
    }

    public static TableDefinition personnelManagerTable() {
        TableDefinition table = new TableDefinition();

        table.setName("PER_MGR");
        table.addField("CODE", java.math.BigDecimal.class, 15);
        table.addField("SALARY", Float.class);
        table.addField("DEPT", String.class, 30);

        return table;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public void setJobCode(Number jobCode) {
        this.jobCode = jobCode;
    }

    public void setManagedEmployees(Vector employees) {
        managedEmployees.setValue(employees);
    }

    @Override
    public void setSalary(Float salary) {
        this.salary = salary;
    }

    public String toString() {
        return "Personnnel Manager: " + getJobCode();
    }
}
