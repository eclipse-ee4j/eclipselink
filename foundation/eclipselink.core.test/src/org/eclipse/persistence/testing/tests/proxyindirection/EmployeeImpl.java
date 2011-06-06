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
package org.eclipse.persistence.testing.tests.proxyindirection;

import java.util.Collection;
import java.util.Vector;

/*
 * Employee implementation.
 *
 * Implementation of the Employee interface.
 *
 * @author        Rick Barkhouse
 * @since        08/15/2000 15:51:05
 */
public class EmployeeImpl implements Employee {
    public int id;
    public String firstName;
    public String lastName;
    public String gender;
    public int age;
    public Address address;
    public Collection managedEmployees;
    public Employee manager;
    public Project project;
    public Contact contact;
    public int cubicleID;
    public LargeProject largeProject;

    public EmployeeImpl() {
        this.managedEmployees = new Vector();
    }

    public void addManagedEmployee(Employee value) {
        getManagedEmployees().add(value);
        value.setManager(this);
    }

    public Address getAddress() {
        return this.address;
    }

    public int getAge() {
        return this.age;
    }

    public Contact getContact() {
        return this.contact;
    }

    public int getCubicleID() {
        return this.cubicleID;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getGender() {
        return this.gender;
    }

    public int getID() {
        return this.id;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Collection getManagedEmployees() {
        return this.managedEmployees;
    }

    public Employee getManager() {
        return this.manager;
    }

    public Project getProject() {
        return this.project;
    }

    public LargeProject getLargeProject() {
        return this.largeProject;
    }

    public void setAddress(Address value) {
        this.address = value;
    }

    public void setAge(int value) {
        this.age = value;
    }

    public void setContact(Contact value) {
        this.contact = value;
    }

    public void setCubicleID(int value) {
        this.cubicleID = value;
    }

    public void setFirstName(String value) {
        this.firstName = value;
    }

    public void setGender(String value) {
        this.gender = value;
    }

    public void setID(int value) {
        this.id = value;
    }

    public void setLastName(String value) {
        this.lastName = value;
    }

    public void setManagedEmployees(Collection value) {
        this.managedEmployees = value;
    }

    public void setManager(Employee value) {
        this.manager = value;
    }

    public void setProject(Project value) {
        this.project = value;
    }

    public void setLargeProject(LargeProject value) {
        this.largeProject = value;
    }

    public String toString() {
        return "[Employee #" + getID() + "] " + getFirstName() + " " + getLastName() + ", lives at " + getAddress() + ", works on " + getProject();
    }
}
