/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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

    @Override
    public void addManagedEmployee(Employee value) {
        getManagedEmployees().add(value);
        value.setManager(this);
    }

    @Override
    public Address getAddress() {
        return this.address;
    }

    @Override
    public int getAge() {
        return this.age;
    }

    @Override
    public Contact getContact() {
        return this.contact;
    }

    @Override
    public int getCubicleID() {
        return this.cubicleID;
    }

    @Override
    public String getFirstName() {
        return this.firstName;
    }

    @Override
    public String getGender() {
        return this.gender;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public String getLastName() {
        return this.lastName;
    }

    @Override
    public Collection getManagedEmployees() {
        return this.managedEmployees;
    }

    @Override
    public Employee getManager() {
        return this.manager;
    }

    @Override
    public Project getProject() {
        return this.project;
    }

    @Override
    public LargeProject getLargeProject() {
        return this.largeProject;
    }

    @Override
    public void setAddress(Address value) {
        this.address = value;
    }

    @Override
    public void setAge(int value) {
        this.age = value;
    }

    @Override
    public void setContact(Contact value) {
        this.contact = value;
    }

    @Override
    public void setCubicleID(int value) {
        this.cubicleID = value;
    }

    @Override
    public void setFirstName(String value) {
        this.firstName = value;
    }

    @Override
    public void setGender(String value) {
        this.gender = value;
    }

    @Override
    public void setID(int value) {
        this.id = value;
    }

    @Override
    public void setLastName(String value) {
        this.lastName = value;
    }

    @Override
    public void setManagedEmployees(Collection value) {
        this.managedEmployees = value;
    }

    @Override
    public void setManager(Employee value) {
        this.manager = value;
    }

    @Override
    public void setProject(Project value) {
        this.project = value;
    }

    @Override
    public void setLargeProject(LargeProject value) {
        this.largeProject = value;
    }

    public String toString() {
        return "[Employee #" + getID() + "] " + getFirstName() + " " + getLastName() + ", lives at " + getAddress() + ", works on " + getProject();
    }
}
