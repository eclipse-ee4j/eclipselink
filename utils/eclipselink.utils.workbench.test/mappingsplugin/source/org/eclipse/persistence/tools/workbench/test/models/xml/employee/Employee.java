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
package org.eclipse.persistence.tools.workbench.test.models.xml.employee;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * <p><b>Purpose</b>: Represent a employee of an organization.
 * <p><b>Description</b>: An Employee is a root object in the Employee Demo.
 * It maintains relationships to all of the other objects in the system.
 * The employee shows usage of 1-1, 1-m, m-m, aggregate and transformation mappings.
 * The employee also shows usage of value holder to implement indirection for its relationships
 * (note, it is strongly suggested to always use value holders for relationships).
 */

public class Employee
    implements Serializable
{
    /** Direct Mapping */
    private String firstName;

    /** Direct Mapping */
    private String lastName;

    /** Object Type Mapping */
    private String gender;

    /** Composite Object Mapping */
    private Address address;

    /** Composite Collection Mapping */
    private Map phoneNumbers;

    /** Composite Collection Mapping */
    private Collection dependents;

    /** Direct Collection Mapping */
    private Collection responsibilities;

    /** Transformation Mapping */
    private Calendar[] normalHours = new Calendar[5];


    public Employee() {
        this.phoneNumbers = new HashMap();
        this.dependents = new Vector();
        this.responsibilities = new Vector();

        Calendar startTime = Calendar.getInstance();
        startTime.set(2000, 1, 1, 9, 0, 0);
        normalHours[0] = startTime;

        Calendar endTime = Calendar.getInstance();
        endTime.set(2000, 1, 1, 17, 0, 0);
        normalHours[1] = endTime;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return this.gender;
    }

    private void setGender(String gender) {
        this.gender = gender;
    }

    public void setFemale() {
        this.setGender("Female");
    }

    public void setMale() {
        this.setGender("Male");
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Map getPhoneNumbers() {
        return this.phoneNumbers;
    }

    public void setPhoneNumbers(Map phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public Collection getDependents() {
        return this.dependents;
    }

    public void setDependents(Collection dependents) {
        this.dependents = dependents;
    }

    public Collection getResponsibilities() {
        return this.responsibilities;
    }

    public void setResponsibilities(Collection responsibilities) {
        this.responsibilities = responsibilities;
    }

    public Calendar[] getNormalHours() {
        return this.normalHours;
    }

    public void setNormalHours(Calendar[] normalHours) {
        this.normalHours = normalHours;
    }

    public Calendar getStartTime() {
        return this.getNormalHours()[0];
    }

    public void setStartTime(Calendar startTime) {
        this.getNormalHours()[0] = startTime;
    }

    public Calendar getEndTime() {
        return this.getNormalHours()[1];
    }

    public void setEndTime(Calendar endTime) {
        this.getNormalHours()[1] = endTime;
    }

    @Override
    public String toString() {
        StringWriter writer = new StringWriter();

        writer.write("Employee: ");
        writer.write(getFirstName());
        writer.write(" ");
        writer.write(getLastName());
        return writer.toString();
    }
}
