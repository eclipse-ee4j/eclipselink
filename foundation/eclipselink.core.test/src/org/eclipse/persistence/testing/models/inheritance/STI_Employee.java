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
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.inheritance;

import java.util.*;
import java.io.*;
import java.math.BigDecimal;
import org.eclipse.persistence.indirection.*;

/**
 * STI stands for Single Table Inheritance.
 * STI_Employee references and referenced by STI_Project class,
 * which is mapped with its subclasses STI_SmallProject and STI_LargeProject 
 * to a single table.
 * STI_Employee is a simplified version of Employee.
 */
public class STI_Employee implements Serializable {
    /** Primary key, maped as a direct-to-field, BigDecimal -> NUMBER, that makes use of sequence numbers to generate the id. */
    public BigDecimal id;

    /** Direct-to-field mapping, String -> VARCHAR. */
    public String firstName;

    /** Direct-to-field mapping, String -> VARCHAR. */
    public String lastName;

    /** One-to-one mapping (same class relationship), employee references its manager through a foreign key. */
    public ValueHolderInterface manager;

    /** One-to-many mapping (same class relationship), inverse relationship to manager, uses manager foreign key in the target. */
    public ValueHolderInterface managedEmployees;

    /** Many-to-many mapping, employee references its projects through an intermediate join table. */
    public ValueHolderInterface projects;

    /**
     * For fields that make use of indirection the constructor should build the value holders.
     */
    public STI_Employee() {
        this.firstName = "";
        this.lastName = "";
        this.manager = new ValueHolder();
        this.managedEmployees = new ValueHolder(new Vector());
        this.projects = new ValueHolder(new Vector());
    }

    /**
     * For bi-directional relationships, it is important to maintain both sides of the relationship when changing it.
     */
    public void addManagedEmployee(STI_Employee employee) {
        getManagedEmployees().addElement(employee);
        employee.setManager(this);
    }

    public void addProject(STI_Project project) {
        getProjects().addElement(project);
    }

    public String getFirstName() {
        return firstName;
    }

    public BigDecimal getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public Vector getManagedEmployees() {
        return (Vector)managedEmployees.getValue();
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public STI_Employee getManager() {
        return (STI_Employee)manager.getValue();
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public Vector getProjects() {
        return (Vector)projects.getValue();
    }

    /**
     * For bi-directional relationships, it is important to maintain both sides of the relationship when changing it.
     */
    public void removeManagedEmployee(STI_Employee employee) {
        getManagedEmployees().removeElement(employee);
        employee.setManager(null);
    }

    public void removeProject(STI_Project project) {
        getProjects().removeElement(project);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Set the persistent identifier of the receiver.
     * Note this should never be changed.
     * Consider making the primary key set methods protected or not having them.
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public void setManagedEmployees(Vector managedEmployees) {
        this.managedEmployees.setValue(managedEmployees);
    }

    /**
     * For bi-directional relationships, it is important to maintain both sides of the relationship when changing it.
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public void setManager(STI_Employee manager) {
        this.manager.setValue(manager);
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public void setProjects(Vector projects) {
        this.projects.setValue(projects);
    }

    /**
     * Print the first & last name
     */
    public String toString() {
        StringWriter writer = new StringWriter();

        writer.write("STI_Employee: ");
        writer.write(getFirstName());
        writer.write(" ");
        writer.write(getLastName());
        return writer.toString();
    }
}
