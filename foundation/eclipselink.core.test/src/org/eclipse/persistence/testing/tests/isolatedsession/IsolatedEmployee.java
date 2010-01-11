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
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.isolatedsession;

import java.util.*;

import java.io.*;

import java.math.BigDecimal;

import java.sql.Time;

import org.eclipse.persistence.indirection.*;

/**
 * <p><b>Purpose</b>: Represent a employee of an organization.
 * <p><b>Description</b>: An Employee is a root object in the Employee Demo.
 * It maintains relationships to all of the other objects in the system.
 * The employee shows usage of 1-1, 1-m, m-m, aggregate and transformation mappings.
 * The employee also shows usage of value holder to implement indirection for its relationships
 * (note, it is strongly suggested to always use value holders for relationships).
 */
public class IsolatedEmployee implements Serializable {

    /** Primary key, maped as a direct-to-field, BigDecimal -> NUMBER, that makes use of sequence numbers to generate the id. */
    public BigDecimal id;

    /** Direct-to-field mapping, String -> VARCHAR. */
    public String firstName;

    /** Direct-to-field mapping, String -> VARCHAR. */
    public String lastName;

    /** Object-type mapping, maps "Male" -> "M", "Female" -> "F". */
    public String gender;

    /** Aggregate-object mapping, stores the object in the employee's table. */
    public IsolatedEmploymentPeriod period;

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
    public boolean hasChanges = false;

    /** For performance testing, how many times primary key extracted. */
    public static int getIdCallCount = 0;

    /**
     *    For fields that make use of indirection the constructor should build the value holders.
     */
    public IsolatedEmployee() {
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
    }

    public boolean hasChanges() {
        return hasChanges;
    }

    public void clearChanges() {
        hasChanges = false;
    }

    public void setChanged() {
        hasChanges = true;
    }

    public static IsolatedEmployee buildEmployeeExample1() {
        IsolatedEmployee employee = new IsolatedEmployee();
        employee.setFirstName("Bob");
        employee.setLastName("Smith");
        employee.setMale();
        employee.setSalary(35000);
        employee.setPeriod(IsolatedEmploymentPeriod.EmploymentPeriodExample1());
        employee.setAddress(IsolatedAddress.AddressExample1());
        employee.addResponsibility("Make the coffee.");
        employee.addResponsibility("Clean the kitchen.");
        employee.addPhoneNumber(new IsolatedPhoneNumber("Home", "613", "555-9878"));
        return employee;
    }

    public static IsolatedEmployee buildEmployeeExample2() {
        IsolatedEmployee employee = new IsolatedEmployee();
        employee.setFirstName("Andy");
        employee.setLastName("McDurmont");
        employee.setMale();
        employee.setSalary(1500);
        employee.setPeriod(IsolatedEmploymentPeriod.EmploymentPeriodExample2());
        employee.setAddress(IsolatedAddress.AddressExample2());
        employee.addResponsibility("Shred reports");
        employee.addPhoneNumber(new IsolatedPhoneNumber("Home", "613", "555-9878"));
        return employee;
    }

    /**
     * For bi-directional relationships, it is important to maintain both sides of the relationship when changing it.
     */
    public void addManagedEmployee(IsolatedEmployee employee) {
        setChanged();
        getManagedEmployees().addElement(employee);
        employee.setManager(this);
    }

    /**
     * For bi-directional relationships, it is important to maintain both sides of the relationship when changing it.
     */
    public void addPhoneNumber(IsolatedPhoneNumber phoneNumber) {
        setChanged();
        getPhoneNumbers().addElement(phoneNumber);
        phoneNumber.setOwner(this);
    }

    public void addProject(org.eclipse.persistence.testing.models.employee.interfaces.Project project) {
        setChanged();
        getProjects().addElement(project);
    }

    public void addResponsibility(String responsibility) {
        setChanged();
        getResponsibilitiesList().addElement(responsibility);
    }

    /**
     *    Builds the normalHours Vector.
     *    IMPORTANT:     This method builds the value but does not set it.
     *                        The mapping will set it using method or direct access
     *                        as defined in the descriptor.
     */
    public Time[] buildNormalHours(org.eclipse.persistence.sessions.Record row, org.eclipse.persistence.sessions.Session session) {
        Time[] hours = new Time[2];

        /** This conversion allows for the database type not to match, i.e. may be a Timestamp or String. */
        hours[0] = (Time)session.getDatasourcePlatform().convertObject(row.get("START_TIME"), java.sql.Time.class);
        hours[1] = (Time)session.getDatasourcePlatform().convertObject(row.get("END_TIME"), java.sql.Time.class);
        return hours;
    }

    /**
     * Builds the table definitions for this class
     */
    public static org.eclipse.persistence.tools.schemaframework.TableDefinition buildIsolatedTableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition tabledefinition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("ISOLATED_EMPLOYEE");

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        tabledefinition.addField(field);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field1 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field1.setName("F_NAME");
        field1.setTypeName("VARCHAR");
        field1.setSize(40);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        tabledefinition.addField(field1);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field2 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field2.setName("L_NAME");
        field2.setTypeName("VARCHAR");
        field2.setSize(40);
        field2.setShouldAllowNull(true);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        tabledefinition.addField(field2);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field3 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field3.setName("START_DATE");
        field3.setTypeName("DATE");
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        tabledefinition.addField(field3);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field4 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field4.setName("END_DATE");
        field4.setTypeName("DATE");
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        tabledefinition.addField(field4);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field5 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field5.setName("START_TIME");
        field5.setTypeName("DATE");
        field5.setShouldAllowNull(true);
        field5.setIsPrimaryKey(false);
        field5.setUnique(false);
        field5.setIsIdentity(false);
        tabledefinition.addField(field5);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field6 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field6.setName("END_TIME");
        field6.setTypeName("DATE");
        field6.setShouldAllowNull(true);
        field6.setIsPrimaryKey(false);
        field6.setUnique(false);
        field6.setIsIdentity(false);
        tabledefinition.addField(field6);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field7 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field7.setName("GENDER");
        field7.setTypeName("VARCHAR");
        field7.setSize(1);
        field7.setShouldAllowNull(true);
        field7.setIsPrimaryKey(false);
        field7.setUnique(false);
        field7.setIsIdentity(false);
        tabledefinition.addField(field7);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field8 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field8.setName("ADDR_ID");
        field8.setTypeName("NUMERIC");
        field8.setSize(15);
        field8.setShouldAllowNull(true);
        field8.setIsPrimaryKey(false);
        field8.setUnique(false);
        field8.setIsIdentity(false);
        field8.setForeignKeyFieldName("ISOLATED_ADDRESS.ADDRESS_ID");
        tabledefinition.addField(field8);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field9 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field9.setName("MANAGER_ID");
        field9.setTypeName("NUMERIC");
        field9.setSize(15);
        field9.setShouldAllowNull(true);
        field9.setIsPrimaryKey(false);
        field9.setUnique(false);
        field9.setIsIdentity(false);
        field9.setForeignKeyFieldName("ISOLATED_EMPLOYEE.EMP_ID");
        tabledefinition.addField(field9);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field10 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field10.setName("VERSION");
        field10.setTypeName("NUMERIC");
        field10.setSize(15);
        field10.setShouldAllowNull(true);
        field10.setIsPrimaryKey(false);
        field10.setUnique(false);
        field10.setIsIdentity(false);
        tabledefinition.addField(field10);
        return tabledefinition;
    }

    /**
     * TopLink generated method.
     * <b>WARNING</b>: This code was generated by an automated tool.
     * Any changes will be lost when the code is re-generated
     */
    public static org.eclipse.persistence.tools.schemaframework.TableDefinition buildISOLATEDRESPONSTable() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition tabledefinition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("ISOLATED_RESPONS");

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("EMPLOYEE.EMP_ID");
        tabledefinition.addField(field);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field1 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field1.setName("DESCRIP");
        field1.setTypeName("VARCHAR");
        field1.setSize(200);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(true);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        tabledefinition.addField(field1);
        return tabledefinition;
    }

    /**
     * TopLink generated method.
     * <b>WARNING</b>: This code was generated by an automated tool.
     * Any changes will be lost when the code is re-generated
     */
    public static org.eclipse.persistence.tools.schemaframework.TableDefinition buildISOLATEDSALARYTable() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition tabledefinition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("ISOLATED_SALARY");

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("EMPLOYEE.EMP_ID");
        tabledefinition.addField(field);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field1 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field1.setName("SALARY");
        field1.setTypeName("NUMBER");
        field1.setSize(10);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        tabledefinition.addField(field1);

        return tabledefinition;
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public IsolatedAddress getAddress() {
        return (IsolatedAddress)address.getValue();
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
    public IsolatedEmployee getManager() {
        return (IsolatedEmployee)manager.getValue();
    }

    public Time[] getNormalHours() {
        return normalHours;
    }

    public IsolatedEmploymentPeriod getPeriod() {
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
    public void removeManagedEmployee(IsolatedEmployee employee) {
        setChanged();
        getManagedEmployees().removeElement(employee);
        employee.setManager(null);
    }

    /**
     * Remove the phone number.
     * The phone number's owner must not be set to null as it is part of it primary key,
     * and you can never change the primary key of an existing object.
     * Only in independent relationships should you null out the back reference.
     */
    public void removePhoneNumber(IsolatedPhoneNumber phoneNumber) {
        setChanged();
        getPhoneNumbers().removeElement(phoneNumber);
    }

    public void removeProject(org.eclipse.persistence.testing.models.employee.interfaces.Project project) {
        setChanged();
        getProjects().removeElement(project);
    }

    public void removeResponsibility(String responsibility) {
        setChanged();
        getResponsibilitiesList().removeElement(responsibility);
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public void setAddress(IsolatedAddress address) {
        setChanged();
        this.address.setValue(address);
    }

    /**
     *    Set the last element of the Transformation mapped normalHours.
     */
    public void setEndTime(Time endTime) {
        setChanged();
        getNormalHours()[1] = endTime;
    }

    public void setFemale() {
        setChanged();
        setGender("Female");
    }

    public void setFirstName(String firstName) {
        setChanged();
        this.firstName = firstName;
    }

    public void setGender(String gender) {
        setChanged();
        this.gender = gender;
    }

    /**
     * For performance testing.
     */
    public static void setGetIdCallCount(int value) {
        IsolatedEmployee.getIdCallCount = value;
    }

    /**
     * Set the persistent identifier of the receiver.
     * Note this should never be changed.
     * Consider making the primary key set methods protected or not having them.
     * In this demo the setId is required for testing purposes.
     */
    public void setId(BigDecimal id) {
        setChanged();
        this.id = id;
    }

    public void setLastName(String lastName) {
        setChanged();
        this.lastName = lastName;
    }

    public void setMale() {
        setChanged();
        setGender("Male");
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public void setManagedEmployees(Vector managedEmployees) {
        setChanged();
        this.managedEmployees.setValue(managedEmployees);
    }

    /**
     * For bi-directional relationships, it is important to maintain both sides of the relationship when changing it.
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public void setManager(IsolatedEmployee manager) {
        setChanged();
        this.manager.setValue(manager);
    }

    public void setNormalHours(Time[] normalHours) {
        setChanged();
        this.normalHours = normalHours;
    }

    public void setPeriod(IsolatedEmploymentPeriod period) {
        setChanged();
        this.period = period;
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public void setPhoneNumbers(Vector phoneNumbers) {
        setChanged();
        this.phoneNumbers.setValue(phoneNumbers);
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public void setProjects(Vector projects) {
        setChanged();
        this.projects.setValue(projects);
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */
    public void setResponsibilitiesList(Vector responsibilitiesList) {
        setChanged();
        this.responsibilitiesList.setValue(responsibilitiesList);
    }

    public void setSalary(int salary) {
        setChanged();
        this.salary = salary;
    }

    /**
     *    Set the first element of the Transformation mapped normalHours.
     */
    public void setStartTime(Time startTime) {
        setChanged();
        getNormalHours()[0] = startTime;
    }

    /**
     * Print the first & last name
     */
    public String toString() {
        StringWriter writer = new StringWriter();

        writer.write("IsolatedEmployee: ");
        writer.write(getFirstName());
        writer.write(" ");
        writer.write(getLastName());
        return writer.toString();
    }
}
