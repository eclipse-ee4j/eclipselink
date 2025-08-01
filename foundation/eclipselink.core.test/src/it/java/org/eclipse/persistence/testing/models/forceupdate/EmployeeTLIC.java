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
package org.eclipse.persistence.testing.models.forceupdate;

import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;

import java.io.Serializable;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Vector;

public class EmployeeTLIC implements Serializable {
    /** Primary key, maped as a direct-to-field, BigDecimal -{@literal >} NUMBER, that makes use of sequence numbers to generate the id. */
    public BigDecimal id;

    /** Direct-to-field mapping, String -{@literal >} VARCHAR. */
    public String firstName;

    /** Direct-to-field mapping, String -{@literal >} VARCHAR. */
    public String lastName;

    /** Object-type mapping, maps "Male" -{@literal >} "M", "Female" -{@literal >} "F". */
    public String gender;

    /** One-to-one mapping, employee references its address through a foreign key. */
    public ValueHolderInterface address;

    /** One-to-many mapping, employee references its collection of phone numbers using a foreign key in the phone's table. */
    public ValueHolderInterface phoneNumbers;

    /** Direct-to-field mapping, int -{@literal >} NUMBER, salary of the employee in dollars. */
    public int salary;

    public EmployeeTLIC() {
        this.firstName = "";
        this.lastName = "";
        this.address = new ValueHolder();
        this.phoneNumbers = new ValueHolder(new Vector());
    }

    public void addPhoneNumber(PhoneNumberTLIC phoneNumber) {
        getPhoneNumbers().add(phoneNumber);
        phoneNumber.setOwner(this);
    }

    public AddressTLIC getAddress() {
        return (AddressTLIC)address.getValue();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getGender() {
        return gender;
    }

    public BigDecimal getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public Vector getPhoneNumbers() {
        return (Vector)phoneNumbers.getValue();
    }

    public int getSalary() {
        return salary;
    }

    public void removePhoneNumber(PhoneNumberTLIC phoneNumber) {
        getPhoneNumbers().remove(phoneNumber);
    }

    public void setAddress(AddressTLIC address) {
        this.address.setValue(address);
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

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMale() {
        setGender("Male");
    }

    public void setPhoneNumbers(Vector phoneNumbers) {
        this.phoneNumbers.setValue(phoneNumbers);
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    /**
     * Print the first &amp; last name
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
