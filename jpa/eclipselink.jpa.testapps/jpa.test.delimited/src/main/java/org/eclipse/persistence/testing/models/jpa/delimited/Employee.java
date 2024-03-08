/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     tware - add for testing JPA 2.0 delimited identifiers
package org.eclipse.persistence.testing.models.jpa.delimited;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.Version;
import org.eclipse.persistence.annotations.BasicCollection;
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.ChangeTracking;
import org.eclipse.persistence.annotations.CollectionTable;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ExistenceChecking;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.PrivateOwned;
import org.eclipse.persistence.annotations.Properties;
import org.eclipse.persistence.annotations.Property;
import org.eclipse.persistence.annotations.TypeConverter;
import org.eclipse.persistence.config.CacheIsolationType;

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.TABLE;
import static org.eclipse.persistence.annotations.CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS;
import static org.eclipse.persistence.annotations.CacheType.SOFT_WEAK;
import static org.eclipse.persistence.annotations.ExistenceType.CHECK_DATABASE;
import static org.eclipse.persistence.annotations.OptimisticLockingType.VERSION_COLUMN;

/**
 * Employees have a one-to-many relationship with Employees through the
 * managedEmployees attribute.
 * Addresses exist in one-to-one relationships with Employees through the
 * address attribute.
 * Employees have a many-to-many relationship with Projects through the
 * projects attribute.
 */
@SuppressWarnings("deprecation")
@Entity
@Table(name="\"CMP3_DEL_EMPLOYEE\"")
@SecondaryTable(name="CMP3_DEL_SALARY")
@PrimaryKeyJoinColumn(name="EMP_ID", referencedColumnName="EMP_ID")
@NamedNativeQuery(
    name="findAllSQLEmployees",
    query="select * from \"CMP3_DEL_EMPLOYEE\"",
    resultClass=org.eclipse.persistence.testing.models.jpa.delimited.Employee.class
)
@OptimisticLocking(
    type=VERSION_COLUMN
)
@ObjectTypeConverter(
    name="sex",
    dataType=String.class,
    objectType=org.eclipse.persistence.testing.models.jpa.delimited.Employee.Gender.class,
    conversionValues={
        @ConversionValue(dataValue="F", objectValue="Female"),
        @ConversionValue(dataValue="M", objectValue="Male")
    }
)
@TypeConverter(
    name="Long2String",
    dataType=String.class,
    objectType=Long.class
)
@Cache(
    type=SOFT_WEAK,
    size=730,
    isolation=CacheIsolationType.SHARED,
    expiry=100000,
    alwaysRefresh=false, // some test dependencies for this to be false.
    disableHits=true, // Employee customizer should set it back to false.
    coordinationType=INVALIDATE_CHANGED_OBJECTS
)
@ChangeTracking
@ExistenceChecking(CHECK_DATABASE)
@Properties({
    @Property(name="entityName", value="Employee"),
    @Property(name="entityIntegerProperty", value="1", valueType=Integer.class)
}
)
public class Employee implements Serializable, Cloneable {
    public enum Gender { Female, Male }

    private int salary;

    private Integer id;
    private Integer version;

    private Gender gender;

    /** The field names intentionally do not match the property names to test method weaving. */
    private String m_lastName;
    private String m_firstName;

    private Address m_address;
    private Employee manager;
    private EmploymentPeriod period;

    private Collection<Project> projects;
    private Collection<String> responsibilities;
    private Collection<PhoneNumber> m_phoneNumbers;
    private Collection<Employee> managedEmployees;

    public Employee () {
        this.m_phoneNumbers = new Vector<>();
        this.projects = new Vector<>();
        this.managedEmployees = new Vector<>();
        this.responsibilities = new Vector<>();
    }

    public Employee(String firstName, String lastName){
        this();
        this.m_firstName = firstName;
        this.m_lastName = lastName;
    }

    @Override
    public Employee clone() {
        Employee clone = null;
        try {
            clone = (Employee)super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new InternalError(exception.toString());
        }
        clone.projects = new Vector<>(this.projects);
        clone.managedEmployees = new Vector<>(this.managedEmployees);
        clone.responsibilities = new Vector<>(this.responsibilities);
        return clone;
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

    public String displayString() {
        StringBuilder sbuff = new StringBuilder();
        sbuff.append("Employee ").append(getId()).append(": ").append(getLastName()).append(", ").append(getFirstName()).append(getSalary());

        return sbuff.toString();
    }

    // Testing - Static method should be ignored
    static public void getAbsolutelyNothing() {}

    @ManyToOne(cascade={PERSIST, MERGE}, fetch=LAZY)
    @JoinColumn(name="ADDR_ID")
    @Property(name="attributeName", value="address")
    public Address getAddress() {
        return m_address;
    }

    @Column(name="\"F_NAME\"")
    @Property(name="attributeName", value="firstName")
    public String getFirstName() {
        return m_firstName;
    }

    @Convert("sex")
    @Property(name="attributeName", value="gender")
    public Gender getGender() {
        return gender;
    }

    @Id
    @GeneratedValue(strategy=TABLE, generator="EMPLOYEE_TABLE_GENERATOR")
    @TableGenerator(
        name="EMPLOYEE_TABLE_GENERATOR",
        table="CMP3_DEL_EMPLOYEE_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="EMPLOYEE_SEQ",
        initialValue=50
    )
    @Column(name="EMP_ID")
    @Property(name="attributeName", value="id")
    public Integer getId() {
        return id;
    }

    // Not defined in the XML, this should get processed.
    @Column(name="L_NAME")
    public String getLastName() {
        return m_lastName;
    }

    @OneToMany(cascade=ALL, mappedBy="manager")
    @Property(name="attributeName", value="managedEmployees")
    public Collection<Employee> getManagedEmployees() {
        return managedEmployees;
    }

    // Not defined in the XML, this should get processed.
    @ManyToOne(cascade=PERSIST, fetch=LAZY)
    @Property(name="attributeName", value="manager")
    public Employee getManager() {
        return manager;
    }


    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="startDate", column=@Column(name="START_DATE")),
        @AttributeOverride(name="endDate", column=@Column(name="END_DATE", nullable=true))
    })
    @Property(name="attributeName", value="period")
    public EmploymentPeriod getPeriod() {
        return period;
    }

    @OneToMany(cascade=ALL, mappedBy="owner")
    @PrivateOwned
    @Property(name="attributeName", value="phoneNumbers")
    public Collection<PhoneNumber> getPhoneNumbers() {
        return m_phoneNumbers;
    }

    @ManyToMany(cascade={PERSIST, MERGE})
    @JoinTable(
        name="CMP3_DEL_EMP_PROJ",
        // Default for the project side and specify for the employee side
        // Will test both defaulting and set values.
        joinColumns=@JoinColumn(name="EMPLOYEES_EMP_ID", referencedColumnName="EMP_ID")
    )
    @Property(name="attributeName", value="projects")
    public Collection<Project> getProjects() {
        return projects;
    }

    @BasicCollection(valueColumn=@Column(name="DESCRIPTION"))
    @CollectionTable(name="CMP3_DEL_RESPONS")
    // generics left off the Collection on purpose ...
    @Property(name="attributeName", value="responsibilities")
    public Collection<String> getResponsibilities() {
        return responsibilities;
    }

    @Column(table="CMP3_DEL_SALARY")
    @Property(name="attributeName", value="salary")
    public int getSalary() {
        return salary;
    }

    @Version
    @Column(name="VERSION")
    @Property(name="attributeName", value="version")
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
        this.m_address = address;
    }

    /**
     * This tests having multiple getAddress methods, to ensure the weaver doesn't get confused.
     */
    public Address getAddress(String type) {
        if (type.equals("Home")) {
            return getAddress();
        } else return new Address();
    }

    /**
     * This tests having multiple setAddress methods, to ensure the weaver doesn't get confused.
     */
    public void setAddress(String city) {
        getAddress().setCity(city);
    }

    public void setFemale() {
        this.gender = Gender.Female;
    }

    public void setFirstName(String name) {
        this.m_firstName = name;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setLastName(String name) {
        this.m_lastName = name;
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
        this.m_phoneNumbers = phoneNumbers;
    }

    public void setProjects(Collection<Project> projects) {
        this.projects = projects;
    }

    public void setResponsibilities(Collection<String> responsibilities) {
        this.responsibilities = responsibilities;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Employee: " + getId();
    }

}

