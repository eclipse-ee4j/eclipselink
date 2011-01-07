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
package org.eclipse.persistence.testing.tests.nls.japanese;

import java.util.*;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Time;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;

/**
 * <p><b>Purpose</b>: Represent a employee of an organization.
 * <p><b>Description</b>: An Employee is a root object in the Employee Demo.
 * It maintains relationships to all of the other objects in the system.
 * The employee shows usage of 1-1, 1-m, m-m, aggregate and transformation mappings.
 * The employee also shows usage of value holder to implement indirection for its relationships
 * (note, it is strongly suggested to always use value holders for relationships).
 */

//public class Employee implements org.eclipse.persistence.testing.models.employee.interfaces.Employee, Serializable {
public class NLSEmployee implements org.eclipse.persistence.testing.models.employee.interfaces.Employee, Serializable {

    /** Primary key, maped as a direct-to-field, BigDecimal -> NUMBER, that makes use of sequence numbers to generate the id. */
    public BigDecimal id;

    /** Direct-to-field mapping, String -> VARCHAR. */
    public String firstName;

    /** Direct-to-field mapping, String -> VARCHAR. */
    public String lastName;

    /** Object-type mapping, maps "Male" -> "M", "Female" -> "F". */
    public String gender;

    /** Aggregate-object mapping, stores the object in the employee's table. */
    public EmploymentPeriod period;

    /** One-to-one mapping, employee references its address through a foreign key. */
    public ValueHolderInterface address;

    /** One-to-one mapping (same class relationship), employee references its manager through a foreign key. */
    public ValueHolderInterface manager;

    /** One-to-many mapping (same class relationship), inverse relationship to manager, uses manager foreign key in the target. */
    public ValueHolderInterface managedEmployees;

    /** One-to-many mapping, employee references its collection of phone numbers using a foreign key in the phone's table. */
    public ValueHolderInterface phoneNumbers;

    /** Many-to-many mapping, employee references its projects through an intermediate join table. */
    public ValueHolderInterface projects;

    /** Direct-collection mapping, employee stores its collection of plain Strings in an intermediate table. */
    public ValueHolderInterface responsibilitiesList;

    /** Transformation mapping, a two(2) element array holding the employee's normal working hours (START_TIME & END_TIME),
        this is stored into two different fields in the employee table. */
    public Time[] normalHours;

    /** Direct-to-field mapping, int -> NUMBER, salary of the employee in dollars. */
    public int salary;

    /**
     *    For fields that make use of indirection the constructor should build the value holders.
     */

    //public Employee()
    public NLSEmployee() {
        this.firstName = "";
        this.lastName = "";
        this.address = new ValueHolder();
        this.manager = new ValueHolder();
        this.managedEmployees = new ValueHolder(new Vector());
        this.projects = new ValueHolder(new Vector());
        this.responsibilitiesList = new ValueHolder(new Vector());
        this.phoneNumbers = new ValueHolder(new Vector());
        this.normalHours = new Time[] { Helper.timeFromHourMinuteSecond(9, 0, 0), Helper.timeFromHourMinuteSecond(17, 0, 0) };
    }

    /**
     * For bi-directional relationships, it is important to maintain both sides of the relationship when changing it.
     */
    public void addManagedEmployee(org.eclipse.persistence.testing.models.employee.interfaces.Employee employee) {
        getManagedEmployees().addElement(employee);
        employee.setManager(this);
    }

    /**
     * For bi-directional relationships, it is important to maintain both sides of the relationship when changing it.
     */
    public void addPhoneNumber(PhoneNumber phoneNumber) {
        getPhoneNumbers().addElement(phoneNumber);
        phoneNumber.setOwner(this);
    }

    public void addProject(org.eclipse.persistence.testing.models.employee.interfaces.Project project) {
        getProjects().addElement(project);
    }

    public void addResponsibility(String responsibility) {
        getResponsibilitiesList().addElement(responsibility);
    }

    /**
     * Builds the normalHours Vector.
     * IMPORTANT: This method builds the value but does not set it.
     * The mapping will set it using method or direct access as defined in the descriptor.
     */
    public Time[] buildNormalHours(Record row, Session session) {
        Time[] hours = new Time[2];

        /** This conversion allows for the database type not to match, i.e. may be a Timestamp or String. */
        hours[0] = (Time)session.getPlatform().convertObject(row.get("\u3066\u3068\u3042\u3064\u3068_\u3068\u3051\u3059\u304a"), java.sql.Time.class);
        hours[1] = (Time)session.getPlatform().convertObject(row.get("\u304a\u305b\u3048_\u3068\u3051\u3059\u304a"), java.sql.Time.class);
        return hours;
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public Address getAddress() {
        return (Address)address.getValue();
    }

    /**
     *    Return the last element of the Transformation mapped normalHours.
     */
    public Time getEndTime() {
        return getNormalHours()[1];
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
    public org.eclipse.persistence.testing.models.employee.interfaces.Employee getManager() {
        //return (Employee) manager.getValue();
        return (NLSEmployee)manager.getValue();
    }

    public Time[] getNormalHours() {
        return normalHours;
    }

    public EmploymentPeriod getPeriod() {
        return period;
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public Vector getPhoneNumbers() {
        return (Vector)phoneNumbers.getValue();
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public Vector getProjects() {
        return (Vector)projects.getValue();
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public Vector getResponsibilitiesList() {
        return (Vector)responsibilitiesList.getValue();
    }

    public int getSalary() {
        return salary;
    }

    /**
     *    Return the first element of the Transformation mapped normalHours.
     */
    public java.sql.Time getStartTime() {
        return getNormalHours()[0];
    }

    /**
     * For bi-directional relationships, it is important to maintain both sides of the relationship when changing it.
     */
    public void removeManagedEmployee(org.eclipse.persistence.testing.models.employee.interfaces.Employee employee) {
        getManagedEmployees().removeElement(employee);
        employee.setManager(null);
    }

    /**
     * Remove the phone number.
     * The phone number's owner must not be set to null as it is part of it primary key,
     * and you can never change the primary key of an existing object.
     * Only in independent relationships should you null out the back reference.
     */
    public void removePhoneNumber(PhoneNumber phoneNumber) {
        getPhoneNumbers().removeElement(phoneNumber);
    }

    public void removeProject(org.eclipse.persistence.testing.models.employee.interfaces.Project project) {
        getProjects().removeElement(project);
    }

    public void removeResponsibility(String responsibility) {
        getResponsibilitiesList().removeElement(responsibility);
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public void setAddress(Address address) {
        this.address.setValue(address);
    }

    /**
     *    Set the last element of the Transformation mapped normalHours.
     */
    public void setEndTime(Time endTime) {
        getNormalHours()[1] = endTime;
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
    public void setId(BigDecimal id) {
        this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMale() {
        setGender("Male");
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
    public void setManager(org.eclipse.persistence.testing.models.employee.interfaces.Employee manager) {
        this.manager.setValue(manager);
    }

    public void setNormalHours(Time[] normalHours) {
        this.normalHours = normalHours;
    }

    public void setPeriod(EmploymentPeriod period) {
        this.period = period;
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public void setPhoneNumbers(Vector phoneNumbers) {
        this.phoneNumbers.setValue(phoneNumbers);
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public void setProjects(Vector projects) {
        this.projects.setValue(projects);
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public void setResponsibilitiesList(Vector responsibilitiesList) {
        this.responsibilitiesList.setValue(responsibilitiesList);
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    /**
     *    Set the first element of the Transformation mapped normalHours.
     */
    public void setStartTime(Time startTime) {
        getNormalHours()[0] = startTime;
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
