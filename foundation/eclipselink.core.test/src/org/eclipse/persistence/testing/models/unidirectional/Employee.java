/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     ailitchev - Uni-directional OneToMany
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.unidirectional;

import java.util.*;
import java.io.*;
import org.eclipse.persistence.indirection.*;

/**
 * This is a scaled-down version of Employee that uses UnidirectionalOneToManyMappings for managedEmployees and phoneNumbers.
 * Everything else (ValueHolderInterface for example) is kept the same as in original Employee to simplify comparative debugging.
 */
public class Employee implements Serializable {
    /** Primary key, mapped as a direct-to-field, BigDecimal -> NUMBER, that makes use of sequence numbers to generate the id. */
    public int id;

    /** Direct-to-field mapping, String -> VARCHAR. */
    public String firstName;

    /** Direct-to-field mapping, String -> VARCHAR. */
    public String lastName;

    /** One-to-many unidirectional mapping */
    public ValueHolderInterface managedEmployees;

    /** One-to-many mapping unidirectional, employee references its collection of phone numbers using a foreign key in the phone's table. */
    public ValueHolderInterface phoneNumbers;

    /**
     * For fields that make use of indirection the constructor should build the value holders.
     */
    public Employee() {
        this.firstName = "";
        this.lastName = "";
        this.managedEmployees = new ValueHolder(new ArrayList());
        this.phoneNumbers = new ValueHolder(new ArrayList());
    }

    public void addManagedEmployee(Employee employee) {
        getManagedEmployees().add(employee);
    }

    public void addPhoneNumber(PhoneNumber phoneNumber) {
        getPhoneNumbers().add(phoneNumber);
    }

    public String getFirstName() {
        return firstName;
    }

    /**
     * Return the persistent identifier of the receiver.
     */
    public int getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public List getManagedEmployees() {
        return (List)managedEmployees.getValue();
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public List getPhoneNumbers() {
        return (List)phoneNumbers.getValue();
    }

    /**
     * 
     */
    public void removeManagedEmployee(Employee employee) {
        getManagedEmployees().remove(employee);
    }

    /**
     * Remove the phone number.
     */
    public void removePhoneNumber(PhoneNumber phoneNumber) {
        getPhoneNumbers().remove(phoneNumber);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Set the persistent identifier of the receiver.
     * Note this should never be changed.
     * Consider making the primary key set methods protected or not having them.
     * In this demo the setId is required for testing purposes.
     */
    public void setId(int id) {
        this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public void setManagedEmployees(List managedEmployees) {
        this.managedEmployees.setValue(managedEmployees);
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public void setPhoneNumbers(List phoneNumbers) {
        this.phoneNumbers.setValue(phoneNumbers);
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
