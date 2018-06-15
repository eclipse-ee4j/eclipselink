/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     01/23/2013-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
package org.eclipse.persistence.testing.models.jpa21.advanced.xml;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.eclipse.persistence.annotations.TypeConverter;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

@ObjectTypeConverter(
        name="sex",
        dataType=String.class,
        objectType=org.eclipse.persistence.testing.models.jpa21.advanced.xml.Employee.Gender.class,
        conversionValues={
            @ConversionValue(dataValue="F", objectValue="Female"),
            @ConversionValue(dataValue="M", objectValue="Male")
        }
    )
public class Employee implements Serializable, Cloneable {
    public enum EmployeeStatus {FULL_TIME, PART_TIME, CONTRACT}
    public enum Gender { Female, Male }

    private Long salary;
    private Long previousSalary;

    private Integer id;
    private Integer version;

    @Convert("sex")
    private Gender gender;
    private EmployeeStatus status;

    private String lastName;
    private String firstName;

    private Address address;
    private Department department;
    private Employee manager;
    private EmploymentPeriod period;

    private Collection<Project> projects;
    private Collection<String> responsibilities;
    private Collection<PhoneNumber> phoneNumbers;
    private Collection<Employee> managedEmployees;

    public Employee () {
        this.phoneNumbers = new Vector<PhoneNumber>();
        this.projects = new Vector<Project>();
        this.managedEmployees = new Vector<Employee>();
        this.responsibilities = new Vector<String>();
    }

    public Employee(String firstName, String lastName){
        this();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void addManagedEmployee(Employee emp) {
        getManagedEmployees().add(emp);
        emp.setManager(this);
    }

    public void addPhoneNumber(PhoneNumber phone) {
        phone.setOwner(this);
        getPhoneNumbers().add(phone);
    }

    public void addProject(Project theProject) {
        getProjects().add(theProject);
    }

    public void addResponsibility(String responsibility) {
        getResponsibilities().add(responsibility);
    }

    public Employee clone() {
        Employee clone = null;
        try {
            clone = (Employee)super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new InternalError(exception.toString());
        }
        clone.projects = new Vector(this.projects);
        clone.managedEmployees = new Vector(this.managedEmployees);
        clone.responsibilities = new Vector(this.responsibilities);
        return clone;
    }

    public Address getAddress() {
        return address;
    }

    public Department getDepartment() {
        return department;
    }

    public String getFirstName() {
        return firstName;
    }

    public Gender getGender() {
        return gender;
    }

    public Integer getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public Collection<Employee> getManagedEmployees() {
        return managedEmployees;
    }

    public Employee getManager() {
        return manager;
    }

    public EmploymentPeriod getPeriod() {
        return period;
    }

    public Collection<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public Long getPreviousSalary() {
        return previousSalary;
    }

    public Collection<Project> getProjects() {
        return projects;
    }

    public Collection getResponsibilities() {
        return responsibilities;
    }

    public Long getSalary() {
        return salary;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public Integer getVersion() {
        return version;
    }

    public boolean isFemale() {
        return gender.equals(Gender.Female);
    }

    public boolean isMale() {
        return gender.equals(Gender.Male);
    }

    public void removeManagedEmployee(Employee emp) {
        getManagedEmployees().remove(emp);
    }

    public void removePhoneNumber(PhoneNumber phone) {
        // Note that getPhoneNumbers() will not have a phone number identical to
        // "phone", (because it's serialized) and this will take advantage of
        // equals() in PhoneNumber to remove properly
        getPhoneNumbers().remove(phone);
    }

    public void removeProject(Project theProject) {
        getProjects().remove(theProject);
    }

    public void removeResponsibility(String responsibility) {
        getResponsibilities().remove(responsibility);
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public void setFemale() {
        this.gender = Gender.Female;
    }

    public void setFirstName(String name) {
        this.firstName = name;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setLastName(String name) {
        this.lastName = name;
    }

    public void setMale() {
        this.gender = Gender.Male;
    }

    public void setManagedEmployees(Collection<Employee> managedEmployees) {
        this.managedEmployees = managedEmployees;
    }

    public void setManagerField(Employee manager) {
        this.manager = manager;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    public void setPeriod(EmploymentPeriod period) {
        this.period = period;
    }

    public void setPhoneNumbers(Collection<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public void setPreviousSalary(Long previousSalary) {
        this.previousSalary = previousSalary;
    }

    public void setProjects(Collection<Project> projects) {
        this.projects = projects;
    }

    public void setResponsibilities(Collection<String> responsibilities) {
        this.responsibilities = responsibilities;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String toString() {
        return "Employee: " + getId();
    }
}
