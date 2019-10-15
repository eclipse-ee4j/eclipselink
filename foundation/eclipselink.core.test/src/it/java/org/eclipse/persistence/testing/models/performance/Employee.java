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
package org.eclipse.persistence.testing.models.performance;

import java.util.*;
import java.io.*;

/**
 * <p><b>Purpose</b>: Represent a employee of an organization.
 * <p><b>Description</b>: An Employee is a root object in the Employee Demo.
 * It maintains relationships to all of the other objects in the system.
 */
public class Employee implements Serializable {

    protected long id;
    protected long version;
    protected String firstName;
    protected String lastName;
    protected String gender;
    protected EmploymentPeriod period;
    protected Address address;
    protected Employee manager;
    protected Collection managedEmployees;
    protected Collection phoneNumbers;
    public Collection projects;
    protected int salary;

    public Employee() {
        this.firstName = "";
        this.lastName = "";
        this.managedEmployees = new HashSet();
        this.projects = new HashSet();
        this.phoneNumbers = new HashSet();
    }

    /**
     * For bi-directional relationships, it is important to maintain both sides of the relationship when changing it.
     */
    public void addManagedEmployee(Employee employee) {
        getManagedEmployees().add(employee);
        employee.setManager(this);
    }

    /**
     * For bi-directional relationships, it is important to maintain both sides of the relationship when changing it.
     */
    public void addPhoneNumber(PhoneNumber phoneNumber) {
        getPhoneNumbers().add(phoneNumber);
        phoneNumber.setOwner(this);
    }

    public void addProject(Project project) {
        getProjects().add(project);
    }

    public Address getAddress() {
        return address;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getGender() {
        return gender;
    }

    /**
     * Return the persistent identifier of the receiver.
     */
    public long getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public Collection getManagedEmployees() {
        return managedEmployees;
    }

    public Employee getManager() {
        return manager;
    }

    public EmploymentPeriod getPeriod() {
        return period;
    }

    public Collection getPhoneNumbers() {
        return phoneNumbers;
    }

    public Collection getProjects() {
        return projects;
    }

    public int getSalary() {
        return salary;
    }

    public long getVersion() {
        return version;
    }

    public void removeManagedEmployee(Employee employee) {
        getManagedEmployees().remove(employee);
        employee.setManager(null);
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

    public void removeProject(Project project) {
        getProjects().remove(project);
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setFemale() {
        setGender("Female");
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Set the persistent identifier of the receiver.
     * Note this should never be changed.
     * Consider making the primary key set methods protected or not having them.
     * In this demo the setId is required for testing purposes.
     */
    public void setId(long id) {
        this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMale() {
        setGender("Male");
    }

    public void setManagedEmployees(Collection managedEmployees) {
        this.managedEmployees = managedEmployees;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    public void setPeriod(EmploymentPeriod period) {
        this.period = period;
    }

    public void setPhoneNumbers(Collection phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public void setProjects(Collection projects) {
        this.projects = projects;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public void setVersion(long version) {
        this.version = version;
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
