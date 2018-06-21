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
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Employee {
    private int id;
    private String name;
    private Address homeAddress;
    private Address workAddress;
    private Phone phone;
    private Badge badge;
    private String empCode;

    public Employee() {
        super();
    }

    @XmlElement(name="id")
    public int getID() {
        return id;
    }

    public void setID(int newID) {
        id = newID;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone newPhone) {
        phone = newPhone;
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
    }

    public Badge getBadge() {
        return badge;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address newAddress) {
        homeAddress = newAddress;
    }

    public Address getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(Address newAddress) {
        workAddress = newAddress;
    }

    @XmlAttribute
    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public String toString() {
        return "Employee: " + getID() + ", " + getName();
    }

    public boolean equals(Object object) {
        // System.out.println(this.getClass() + " #" + this.getClass().hashCode() + " " + this.getClass().getClassLoader());
        // System.out.println(object.getClass() + " #" + object.getClass().hashCode() + " " + object.getClass().getClassLoader());
        try {
            Employee employee = (Employee)object;
            return getID() == employee.getID();
        } catch (ClassCastException e) {
            // e.printStackTrace();
            return false;
        }
    }

}
