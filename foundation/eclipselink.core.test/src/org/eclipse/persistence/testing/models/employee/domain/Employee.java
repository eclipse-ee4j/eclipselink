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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     08/15/2008-1.0.1 Chris Delahunt 
 *       - 237545: List attribute types on OneToMany using @OrderBy does not work with attribute change tracking
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.employee.domain;

import java.util.*;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Time;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.descriptors.changetracking.*;

/**
 * <p><b>Purpose</b>: Represent a employee of an organization.
 * <p><b>Description</b>: An Employee is a root object in the Employee Demo.
 * It maintains relationships to all of the other objects in the system.
 * The employee shows usage of 1-1, 1-m, m-m, aggregate and transformation mappings.
 * The employee also shows usage of value holder to implement indirection for its relationships
 * (note, it is strongly suggested to always use value holders for relationships).
 */
public class Employee implements org.eclipse.persistence.testing.models.employee.interfaces.Employee, Serializable, ChangeTracker {
    // implements ChangeTracker for testing

    /** Primary key, mapped as a direct-to-field, BigDecimal -> NUMBER, that makes use of sequence numbers to generate the id. */
    public BigDecimal id;

    /** Direct-to-field mapping, String -> VARCHAR. */
    public String firstName;

    /** Direct-to-field mapping, String -> VARCHAR. */
    public String lastName;

    /** Object-type mapping, maps "Male" -> "M", "Female" -> "F". */
    public String gender;

    /** Primary key, mapped as a direct-to-field, BigDecimal -> NUMBER. */
    public BigDecimal addressId;

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
    
    /** One-to-many mapping, employee references its collection of children arranged by age.
     * This relationship uses transparent indirection */
    public Vector children;

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
        this.managedEmployees = new ValueHolder(new Vector());
        this.projects = new ValueHolder(new Vector());
        this.responsibilitiesList = new ValueHolder(new Vector());
        this.phoneNumbers = new ValueHolder(new Vector());
        this.normalHours = new Time[2];
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.MILLISECOND, 0);
        startTime.set(1970, 0, 1, 9, 0, 0);
        this.normalHours[0] = new Time(startTime.getTime().getTime());
        Calendar endTime = Calendar.getInstance();
        endTime.set(Calendar.MILLISECOND, 0);
        endTime.set(1970, 0, 1, 17, 0, 0);
        this.normalHours[1] = new Time(endTime.getTime().getTime());
        this.children = new Vector();
    }

    /**
     * For bi-directional relationships, it is important to maintain both sides of the relationship when changing it.
     */
    public void addManagedEmployee(org.eclipse.persistence.testing.models.employee.interfaces.Employee employee) {
        if(getManagedEmployees().add(employee)) {
            collectionChange("managedEmployees", managedEmployees, employee, 0);
            employee.setManager(this);
        }
    }

    /**
     * For bi-directional relationships, it is important to maintain both sides of the relationship when changing it.
     */
    public void addPhoneNumber(PhoneNumber phoneNumber) {
        if(getPhoneNumbers().add(phoneNumber)) {
            collectionChange("phoneNumbers", phoneNumbers, phoneNumber, 0);
            phoneNumber.setOwner(this);
        }
    }

    public void addProject(org.eclipse.persistence.testing.models.employee.interfaces.Project project) {
        if(getProjects().add(project)) {
            collectionChange("projects", projects, project, 0);
        }
    }

    public void addResponsibility(String responsibility) {
        if(getResponsibilitiesList().add(responsibility)) {
            collectionChange("responsibilitiesList", responsibilitiesList, responsibility, 0);
        }
    }

    /**
     * Builds the normalHours Vector.
     * IMPORTANT: This method builds the value but does not set it.
     * The mapping will set it using method or direct access as defined in the descriptor.
     */
    public Time[] buildNormalHours(Record row, Session session) {
        Time[] hours = new Time[2];

        /** This conversion allows for the database type not to match, i.e. may be a Timestamp or String. */
        try {
            hours[0] = (Time) session.getDatasourcePlatform().convertObject(row.get("START_TIME"), java.sql.Time.class);
            hours[1] = (Time) session.getDatasourcePlatform().convertObject(row.get("END_TIME"), java.sql.Time.class);
        } catch (Throwable in904) {
            // Allow performance tests to be run in 904.
            hours[0] = (Time)ConversionManager.getDefaultManager().convertObject(row.get("START_TIME"), java.sql.Time.class);
            hours[1] = (Time)ConversionManager.getDefaultManager().convertObject(row.get("END_TIME"), java.sql.Time.class);
        }
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
     * Return the last element of the Transformation mapped normalHours.
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

    public BigDecimal getAddressId() {
        //addressID will not exist until Address is committed
        if (addressId == null) {
            if (getAddress() != null) {
                addressId = getAddress().getId();
            }
        }
        return addressId;
    }
    
    public Vector getChildren(){
        return children;
    }

    public void setAddressId(BigDecimal newAddressId) {
        addressId = newAddressId;
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
        return (Employee)manager.getValue();
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
     * Return the first element of the Transformation mapped normalHours.
     */
    public java.sql.Time getStartTime() {
        return getNormalHours()[0];
    }

    /**
     * For bi-directional relationships, it is important to maintain both sides of the relationship when changing it.
     */
    public void removeManagedEmployee(org.eclipse.persistence.testing.models.employee.interfaces.Employee employee) {
        if(getManagedEmployees().removeElement(employee)) {
            collectionChange("managedEmployees", managedEmployees, employee, 1);
            employee.setManager(null);
        }
    }

    /**
     * Remove the phone number.
     * The phone number's owner must not be set to null as it is part of it primary key,
     * and you can never change the primary key of an existing object.
     * Only in independent relationships should you null out the back reference.
     */
    public void removePhoneNumber(PhoneNumber phoneNumber) {
        if(getPhoneNumbers().removeElement(phoneNumber)) {
            collectionChange("phoneNumbers", phoneNumbers, phoneNumber, 1);
        }
    }

    public void removeProject(org.eclipse.persistence.testing.models.employee.interfaces.Project project) {
        if(getProjects().removeElement(project)) {
            collectionChange("projects", projects, project, 1);
        }
    }

    public void removeResponsibility(String responsibility) {
        if(getResponsibilitiesList().removeElement(responsibility)) {
            collectionChange("responsibilitiesList", responsibilitiesList, responsibility, 1);
        }
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public void setAddress(Address address) {
        propertyChange("address", this.address.getValue(), address);
        this.address.setValue(address);
        if (address == null) {
            this.setAddressId(null);
        } else {
            this.setAddressId(address.getId());
        }
    }
    
    public void setChildren(Vector children){
        this.children = children;
    }

    /**
     * Set the last element of the Transformation mapped normalHours.
     */
    public void setEndTime(Time endTime) {
        Time[] oldHours = new Time[] { getNormalHours()[0], getNormalHours()[1] };
        getNormalHours()[1] = endTime;
        propertyChange("normalHours", oldHours, normalHours);
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
    public void setManagedEmployees(Vector managedEmployees) {
        propertyChange("managedEmployees", this.managedEmployees.getValue(), managedEmployees);
        this.managedEmployees.setValue(managedEmployees);
    }

    /**
     * For bi-directional relationships, it is important to maintain both sides of the relationship when changing it.
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public void setManager(org.eclipse.persistence.testing.models.employee.interfaces.Employee manager) {
        propertyChange("manager", this.manager.getValue(), manager);
        this.manager.setValue(manager);
    }

    public void setNormalHours(Time[] normalHours) {
        Time[] oldHours = this.normalHours;
        this.normalHours = normalHours;
        propertyChange("normalHours", oldHours, normalHours);
    }

    public void setPeriod(EmploymentPeriod period) {
        propertyChange("period", this.period, period);
        this.period = period;
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public void setPhoneNumbers(Vector phoneNumbers) {
        propertyChange("phoneNumbers", this.phoneNumbers.getValue(), phoneNumbers);
        this.phoneNumbers.setValue(phoneNumbers);
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public void setProjects(Vector projects) {
        propertyChange("projects", this.projects.getValue(), projects);
        this.projects.setValue(projects);
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public void setResponsibilitiesList(Vector responsibilitiesList) {
        propertyChange("responsibilitiesList", this.responsibilitiesList.getValue(), responsibilitiesList);
        this.responsibilitiesList.setValue(responsibilitiesList);
    }

    public void setSalary(int salary) {
        propertyChange("salary", new Integer(this.salary), new Integer(salary));
        this.salary = salary;
    }

    /**
     * Set the first element of the Transformation mapped normalHours.
     */
    public void setStartTime(Time startTime) {
        Time[] oldHours = new Time[] { getNormalHours()[0], getNormalHours()[1] };
        getNormalHours()[0] = startTime;
        propertyChange("normalHours", oldHours, normalHours);
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
