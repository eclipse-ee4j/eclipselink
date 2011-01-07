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
 *     07/16/2009 Andrei Ilitchev 
 *       - Bug 282553: JPA 2.0 JoinTable support for OneToOne and ManyToOne
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.onetoonejointable;

import java.util.*;
import java.io.*;
import java.math.BigDecimal;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.descriptors.changetracking.*;

/**
 * <p><b>Purpose</b>: Represent a employee of an organization.
 * <p><b>Description</b>: An Employee is a root object in the Employee Demo.
 * It maintains relationships to all of the other objects in the system.
 * The employee shows usage of 1-1, 1-m, m-m, aggregate and transformation mappings.
 * The employee also shows usage of value holder to implement indirection for its relationships
 * (note, it is strongly suggested to always use value holders for relationships).
 */
public class Employee implements Serializable, ChangeTracker {
    // implements ChangeTracker for testing

    /** Primary key, mapped as a direct-to-field, BigDecimal -> NUMBER, that makes use of sequence numbers to generate the id. */
    public BigDecimal id;

    /** Direct-to-field mapping, String -> VARCHAR. */
    public String firstName;

    /** Direct-to-field mapping, String -> VARCHAR. */
    public String lastName;

    /** Object-type mapping, maps "Male" -> "M", "Female" -> "F". */
    public String gender;

    /** One-to-one mapping, employee references its address through a foreign key. */
    public ValueHolderInterface address;

    /** One-to-one mapping (same class relationship), employee references its manager through a foreign key. */
    public ValueHolderInterface manager;

    public List<Employee> managedEmployees;

    /** Many-to-many mapping, employee references its projects through an intermediate join table. */
    public List<Project> projects;

    /** Direct-collection mapping, employee stores its collection of plain Strings in an intermediate table. */
    public List<String> responsibilitiesList;

    /** Direct-to-field mapping, int -> NUMBER, salary of the employee in dollars. */
    public int salary;
    
    /** One-to-many mapping, employee references its collection of children arranged by age.
     * This relationship uses transparent indirection */
    public List<Child> children;

    /** One-to-one mapping , inverse relationship to Project.teamLeader */
    public ValueHolderInterface projectLed;
    
    /** For performance testing, how many times primary key extracted. */
    public static int getIdCallCount = 0;
    public PropertyChangeListener listener;

    public PropertyChangeListener _persistence_getPropertyChangeListener() {
        return listener;
    }

    public void _persistence_setPropertyChangeListener(PropertyChangeListener listener) {
        this.listener = listener;
    }

    public void propertyChange(String propertyName, Object oldValue, Object newValue) {
        if (listener != null) {
            if (oldValue != newValue) {
                listener.propertyChange(new PropertyChangeEvent(this, propertyName, oldValue, newValue));
            }
        }
    }

    public void collectionChange(String propertyName, Object oldValue, Object newValue, int changeType) {
        if (listener != null) {
            listener.propertyChange(new CollectionChangeEvent(this, propertyName, oldValue, newValue, changeType));
        }
    }

    /**
     * For fields that make use of indirection the constructor should build the value holders.
     */
    public Employee() {
        this.firstName = "";
        this.lastName = "";
        this.address = new ValueHolder();
        this.manager = new ValueHolder();
        this.managedEmployees = new ArrayList();
        this.projects = new ArrayList();
        this.responsibilitiesList = new ArrayList();
        this.children = new ArrayList();
        this.projectLed = new ValueHolder();
    }

    /**
     * For bi-directional relationships, it is important to maintain both sides of the relationship when changing it.
     */
    public void addManagedEmployee(Employee employee) {
        getManagedEmployees().add(employee);
        employee.setManager(this);
    }

    public void addManagedEmployee(int index, Employee employee) {
        getManagedEmployees().add(index, employee);
        employee.setManager(this);
    }

    public Employee setManagedEmployee(int index, Employee employee) {
        Employee removedEmp = getManagedEmployees().set(index, employee);
        removedEmp.setManager(null);
        employee.setManager(this);
        return removedEmp;
    }

    public void addChild(Child child) {
        getChildren().add(child);
        child.setParent(this);
    }

    public void addProject(Project project) {
        getProjects().add(project);
        project.getEmployees().add(this);
    }

    public void addResponsibility(String responsibility) {
        getResponsibilitiesList().add(responsibility);
    }

    public Address getAddress() {
        return (Address)address.getValue();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getGender() {
        return gender;
    }

    /**
     * Used for performance testing.
     */
    public static int getGetIdCallCount() {
        return getIdCallCount;
    }

    /**
     * Return the persistent identifier of the receiver.
     */
    public BigDecimal getId() {
        getIdCallCount++;
        return id;
    }

    public List<Child> getChildren(){
        return children;
    }

    public String getLastName() {
        return lastName;
    }

    public List<Employee> getManagedEmployees() {
        return managedEmployees;
    }

    public Employee getManager() {
        return (Employee)manager.getValue();
    }

    public Project getProjectLed() {
        return (Project)projectLed.getValue();
    }

    public List<Project> getProjects() {
        return projects;
    }

    public List<String> getResponsibilitiesList() {
        return responsibilitiesList;
    }

    public int getSalary() {
        return salary;
    }

    public void removeManagedEmployee(Employee employee) {
        getManagedEmployees().remove(employee);
        employee.setManager(null);
    }

    public void removeProject(Project project) {
        getProjects().remove(project);
        project.getEmployees().remove(this);
    }

    public void removeResponsibility(String responsibility) {
        getResponsibilitiesList().remove(responsibility);
    }

    public void setAddress(Address address) {
        propertyChange("address", this.address.getValue(), address);
        this.address.setValue(address);
    }
    
    public void setChildren(Vector children){
        this.children = children;
    }

    public void setFemale() {
        propertyChange("gender", this.gender, "Female");
        setGender("Female");
    }

    public void setFirstName(String firstName) {
        propertyChange("firstName", getFirstName(), firstName);
        this.firstName = firstName;
    }

    public void setGender(String gender) {
        propertyChange("gender", this.gender, gender);
        this.gender = gender;
    }

    /**
     * For performance testing.
     */
    public static void setGetIdCallCount(int value) {
        Employee.getIdCallCount = value;
    }

    /**
     * Set the persistent identifier of the receiver.
     * Note this should never be changed.
     * Consider making the primary key set methods protected or not having them.
     * In this demo the setId is required for testing purposes.
     */
    public void setId(BigDecimal id) {
        propertyChange("id", this.id, id);
        this.id = id;
    }

    public void setLastName(String lastName) {
        propertyChange("lastName", this.lastName, lastName);
        this.lastName = lastName;
    }

    public void setMale() {
        propertyChange("gender", this.gender, "Male");
        setGender("Male");
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public void setManagedEmployees(List<Employee> managedEmployees) {
        propertyChange("managedEmployees", this.managedEmployees, managedEmployees);
        this.managedEmployees = managedEmployees;
    }

    /**
     * For bi-directional relationships, it is important to maintain both sides of the relationship when changing it.
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public void setManager(Employee manager) {
        propertyChange("manager", this.manager.getValue(), manager);
        this.manager.setValue(manager);
    }

    public void setProjectLed(Project projectLed) {
        propertyChange("projectLed", this.projectLed.getValue(), projectLed);
        this.projectLed.setValue(projectLed);
    }
    
    public void setProjects(List<Project> projects) {
        propertyChange("projects", this.projects, projects);
        this.projects = projects;
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public void setResponsibilitiesList(Vector responsibilitiesList) {
        propertyChange("responsibilitiesList", this.responsibilitiesList, responsibilitiesList);
        this.responsibilitiesList = responsibilitiesList;
    }

    public void setSalary(int salary) {
        propertyChange("salary", new Integer(this.salary), new Integer(salary));
        this.salary = salary;
    }

    /**
     * Print the first & last name
     */
    public String toString() {
        StringWriter writer = new StringWriter();

        writer.write("Employee: ");
        writer.write(getFirstName());
        writer.write(" ");
        writer.write(getLastName());
        return writer.toString();
    }
}