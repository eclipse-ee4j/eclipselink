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
package org.eclipse.persistence.testing.models.jpa.partitioned;

import java.util.*;
import java.io.Serializable;
import javax.persistence.*;

import org.eclipse.persistence.annotations.Partitioned;
import org.eclipse.persistence.annotations.ValuePartitioning;
import org.eclipse.persistence.annotations.ValuePartition;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;

/**
 * Employees is partitioned by department.
 */
@Entity
@Table(name = "PART_EMPLOYEE")
@IdClass(EmployeePK.class)
@ValuePartitioning(
        name="ValuePartitioningByLOCATION",
        partitionColumn=@Column(name="LOCATION"),
        unionUnpartitionableQueries=true,
        defaultConnectionPool="default",
        partitions={
            @ValuePartition(connectionPool="node2", value="Ottawa"),
            @ValuePartition(connectionPool="node3", value="Toronto")
        })
@Partitioned("ValuePartitioningByLOCATION")
public class Employee implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = TABLE)
    @Column(name = "EMP_ID")
    private Integer id;

    @Id
    private String location;

    @Version
    @Column(name = "VERSION")
    private Integer version;

    @Column(name = "L_NAME")
    private String lastName;

    @Column(name = "F_NAME")
    private String firstName;

    @ManyToOne(cascade = { ALL }, fetch = LAZY)
    @JoinColumn(name = "ADDR_ID")
    private Address address;

    @ManyToOne(fetch=EAGER, cascade=PERSIST)
    @JoinColumn(name="DEPT_ID")    
    private Department department;
    
    @ManyToOne(fetch = LAZY)
    @JoinColumns({
        @JoinColumn(name="MANAGER_EMP_ID", referencedColumnName = "EMP_ID"),
        @JoinColumn(name="MANAGER_LOCATION", referencedColumnName = "LOCATION")})
    private Employee manager;

    @ManyToMany(cascade = { PERSIST, MERGE })
    @JoinTable(
            name = "PART_EMP_PROJ",
            joinColumns = {@JoinColumn(name = "EMPLOYEES_EMP_ID", referencedColumnName = "EMP_ID"),@JoinColumn(name = "LOCATION", referencedColumnName = "LOCATION")},
            inverseJoinColumns = @JoinColumn(name = "PROJECTS_PROJ_ID", referencedColumnName = "PROJ_ID"))
    @Partitioned("UnionPartitioningAllNodes")
    private Collection<Project> projects;

    @ElementCollection
    @CollectionTable(
            name = "PART_RESPONS",
            joinColumns={@JoinColumn(name = "EMP_ID", referencedColumnName = "EMP_ID"),@JoinColumn(name = "LOCATION", referencedColumnName = "LOCATION")})
    @Column(name="DESCRIPTION")    
    @Partitioned("ValuePartitioningByLOCATION")
    private List<String> responsibilities;

    @OneToMany(cascade = ALL, mappedBy = "owner", orphanRemoval = true)
    private Collection<PhoneNumber> phoneNumbers;

    @OneToMany(mappedBy = "manager")
    @Partitioned("UnionPartitioningAllNodes")
    private Collection<Employee> managedEmployees;

    public Employee() {
        this.phoneNumbers = new ArrayList<PhoneNumber>();
        this.projects = new ArrayList<Project>();
        this.managedEmployees = new Vector<Employee>();
        this.responsibilities = new Vector<String>();
    }

    public Employee(String firstName, String lastName) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Employee clone() {
        Employee clone = null;
        try {
            clone = (Employee) super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new InternalError(exception.toString());
        }
        return clone;
    }
    
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public Address getAddress() {
        return address;
    }

    public Department getDepartment() { 
        return department; 
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
    
    public EmployeePK pk() {
        EmployeePK pk = new EmployeePK();
        pk.setId(id);
        pk.setLocation(location);
        return pk;
    }

    public Integer getId() {
        return id;
    }

    public Collection<Employee> getManagedEmployees() {
        return managedEmployees;
    }

    public Employee getManager() {
        return manager;
    }

    public Collection<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public Collection<Project> getProjects() {
        return projects;
    }

    public List<String> getResponsibilities() {
        return responsibilities;
    }

    public Integer getVersion() {
        return version;
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

    public void setFirstName(String name) {
        this.firstName = name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setLastName(String name) {
        this.lastName = name;
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

    public void setPhoneNumbers(Collection<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public void setProjects(Collection<Project> projects) {
        this.projects = projects;
    }

    public void setResponsibilities(List<String> responsibilities) {
        this.responsibilities = responsibilities;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String toString() {
        return "Employee: " + getId();
    }
}
