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
 *     05/05/2009 Andrei Ilitchev 
 *       - JPA 2.0 - OrderedList support.
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.orderedlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.descriptors.changetracking.*;

public class Employee implements ChangeTracker {
    // implements ChangeTracker for testing

    /** Primary key, mapped as a direct-to-field, BigDecimal -> NUMBER, that makes use of sequence numbers to generate the id. */
    public BigDecimal id;

    /** Direct-to-field mapping, String -> VARCHAR. */
    public String firstName;

    /** Direct-to-field mapping, String -> VARCHAR. */
    public String lastName;

    /** One-to-one mapping (same class relationship), employee references its manager through a foreign key. */
    public ValueHolderInterface manager;

    /** One-to-many mapping (same class relationship), inverse relationship to manager, uses manager foreign key in the target. */
    public List<Employee> managedEmployees;

    /** One-to-many mapping, employee references its collection of phone numbers using a foreign key in the phone's table. */
    public List<PhoneNumber> phoneNumbers;

    /** Many-to-many mapping, employee references its projects through an intermediate join table. */
    public List<Project> projects;

    /** One-to-many mapping, employee references projects they lead. */
    public List<Project> projectsLed;

    /** Direct-collection mapping, employee stores its collection of plain Strings in an intermediate table. */
    public List<String> responsibilitiesList;
    
    /** One-to-many mapping, employee references its collection of children arranged by age.
     * This relationship uses transparent indirection */
    public Vector<Child> children;
    
    int salary;

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
    public Employee() {
        this.firstName = "";
        this.lastName = "";
        this.manager = new ValueHolder();
        this.managedEmployees = new ArrayList();
        this.projects = new ArrayList();
        this.projectsLed = new ArrayList();
        this.responsibilitiesList = new ArrayList();
        this.phoneNumbers = new ArrayList();
        this.children = new Vector();
    }

    public Employee(String firstName, String lastName) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    public Employee(String firstName) {
        this(firstName, "");
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

    /**
     * For bi-directional relationships, it is important to maintain both sides of the relationship when changing it.
     */
    public void addPhoneNumber(PhoneNumber phoneNumber) {
        getPhoneNumbers().add(phoneNumber);
    }

    public void addPhoneNumber(int index, PhoneNumber phoneNumber) {
        getPhoneNumbers().add(index, phoneNumber);
    }

    public PhoneNumber setPhoneNumber(int index, PhoneNumber phoneNumber) {
        return getPhoneNumbers().set(index, phoneNumber);
    }

    public void addProject(Project project) {
        getProjects().add(project);
        project.getEmployees().add(this);
    }

    public void addProject(int index, Project project) {
        getProjects().add(index, project);
        project.getEmployees().add(this);
    }

    public Project setProject(int index, Project project) {
        Project removedProject = getProjects().set(index, project);
        removedProject.getEmployees().remove(this);
        project.getEmployees().add(this);
        return removedProject;
    }

    public void addProjectLed(Project project) {
        getProjectsLed().add(project);
        project.setTeamLeader(this);
    }

    public void addResponsibility(String responsibility) {
        getResponsibilitiesList().add(responsibility);
    }

    public void addResponsibility(int index, String responsibility) {
        getResponsibilitiesList().add(index, responsibility);
    }

    public String setResponsibility(int index, String responsibility) {
        return getResponsibilitiesList().set(index, responsibility);
    }

    public BigDecimal getId() {
        return id;
    }
    
    public String getFirstName() {
        return firstName;
    }

    public Vector getChildren(){
        return children;
    }

    public String getLastName() {
        return lastName;
    }

    public int getSalary() {
        return salary;
    }

    public List<Employee> getManagedEmployees() {
        return managedEmployees;
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public Employee getManager() {
        return (Employee)manager.getValue();
    }

    public List<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public List<Project> getProjectsLed() {
        return projectsLed;
    }

    public List<String> getResponsibilitiesList() {
        return responsibilitiesList;
    }


    /**
     * For bi-directional relationships, it is important to maintain both sides of the relationship when changing it.
     */
    public void removeManagedEmployee(Employee employee) {
        getManagedEmployees().remove(employee);
        employee.setManager(null);
    }

    /**
     * For bi-directional relationships, it is important to maintain both sides of the relationship when changing it.
     */
    public Employee removeManagedEmployee(int i) {
        Employee emp = getManagedEmployees().remove(i);
        emp.setManager(null);
        return emp;
    }

    /**
     * Remove the phone number.
     * The phone number's owner must not be set to null as it is part of it primary key,
     * and you can never change the primary key of an existing object.
     * Only in independent relationships should you null out the back reference.
     */
    public void removePhoneNumber(PhoneNumber phoneNumber) {
        getPhoneNumbers().remove(phoneNumber);
    }

    public PhoneNumber removePhoneNumber(int i) {
        PhoneNumber phone = getPhoneNumbers().remove(i);
        return phone;
    }

    public void removeProject(Project project) {
        getProjects().remove(project);
        project.getEmployees().remove(this);
    }

    public Project removeProject(int i) {
        Project project = getProjects().remove(i);
        project.getEmployees().remove(this);
        return project;
    }

    public void removeProjectLed(Project project) {
        getProjectsLed().remove(project);
        project.setTeamLeader(null);
    }

    public void removeResponsibility(String responsibility) {
        getResponsibilitiesList().remove(responsibility);
    }

    public String removeResponsibility(int i) {
      return getResponsibilitiesList().remove(i);
  }

    public void setChildren(Vector<Child> children){
        propertyChange("children", this.children, children);
        this.children = children;
    }

    public void setFirstName(String firstName) {
        propertyChange("firstName", getFirstName(), firstName);
        this.firstName = firstName;
    }

    public void setId(BigDecimal id) {
        propertyChange("id", this.id, id);
        this.id = id;
    }

    public void setLastName(String lastName) {
        propertyChange("lastName", this.lastName, lastName);
        this.lastName = lastName;
    }

    public void getSalary(int  salary) {
        propertyChange("salary", this.salary, salary);
        this.salary = salary;
    }

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

    public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
        propertyChange("phoneNumbers", this.phoneNumbers, phoneNumbers);
        this.phoneNumbers = phoneNumbers;
    }

    public void setProjects(List<Project> projects) {
        propertyChange("projects", this.projects, projects);
        this.projects = projects;
    }

    public void setProjectsLed(List<Project> projectsLed) {
        this.projectsLed = projectsLed;
    }

    public void setResponsibilitiesList(List<String> responsibilitiesList) {
        propertyChange("responsibilitiesList", this.responsibilitiesList, responsibilitiesList);
        this.responsibilitiesList = responsibilitiesList;
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
