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
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.interfaces;

import java.util.Vector;

import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.tools.schemaframework.*;

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
        example.setSalary(new Float(77235.00));

        Vector employees = new Vector(5);
        employees.addElement(CourseDeveloper.example1());
        employees.addElement(ProductDeveloper.example1());
        employees.addElement(Secretary.example1());
        employees.addElement(Receptionist.example1());
        employees.addElement(ProductManager.example1());

        example.setManagedEmployees(employees);

        return example;
    }

    public static PersonnelManager example2() {
        PersonnelManager example = new PersonnelManager();

        example.setDepartment("Division B");
        example.setSalary(new Float(97235.00));

        Vector employees = new Vector(5);
        employees.addElement(CourseDeveloper.example2());
        employees.addElement(ProductDeveloper.example2());
        employees.addElement(Secretary.example2());
        employees.addElement(Receptionist.example2());
        employees.addElement(ProductManager.example2());

        example.setManagedEmployees(employees);

        return example;
    }

    public static PersonnelManager example3() {
        PersonnelManager example = new PersonnelManager();

        example.setDepartment("Division C");
        example.setSalary(new Float(87265.00));

        Vector employees = new Vector(5);
        employees.addElement(CourseDeveloper.example3());
        employees.addElement(ProductDeveloper.example3());
        employees.addElement(Secretary.example3());
        employees.addElement(Receptionist.example3());
        employees.addElement(ProductManager.example3());

        example.setManagedEmployees(employees);

        return example;
    }

    public String getDepartment() {
        return department;
    }

    public Number getJobCode() {
        return jobCode;
    }

    public Vector getManagedEmployees() {
        return (Vector)managedEmployees.getValue();
    }

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

    public void setJobCode(Number jobCode) {
        this.jobCode = jobCode;
    }

    public void setManagedEmployees(Vector employees) {
        managedEmployees.setValue(employees);
    }

    public void setSalary(Float salary) {
        this.salary = salary;
    }

    public String toString() {
        return new String("Personnnel Manager: " + getJobCode());
    }
}
