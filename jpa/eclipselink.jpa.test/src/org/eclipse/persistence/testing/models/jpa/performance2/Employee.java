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
 *              dclarke - initial JPA Employee example using XML (bug 217884)
 *              mbraeuer - annotated version, transformation
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.performance2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.*;

/**
 * The Employee class demonstrates several JPA, JPA 2.0, and EclipseLink mapping features including:
 * <ul>
 * <li>Generated Id
 * <li>Version locking
 * <li>OneToOne relationships (dependent, and independent)
 * <li>OneToOne relationship using JoinTable
 * <li>ManyToMany relationship
 * <li>OneToMany relationship (dependent, and independent)
 * <li>Embedded relationship
 * <li>ElementCollection relationships (Basic, and Embeddable) (JPA 2.0)
 * <li>enums
 * <li>OrderColumn (JPA 2.0)
 * <li>MapKeyColumn (JPA 2.0)
 * <li>orphanRemoval (JPA 2.0)
 * </ul>
 */
@Entity
@Table(name="P2_EMPLOYEE")
@SecondaryTable(name = "P2_SALARY")
public class Employee implements Serializable {
    @Id
    @Column(name = "EMP_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    
    @Version
    private long version;

    @Column(name = "F_NAME")
    private String firstName;

    @Column(name = "L_NAME")
    private String lastName;

    @Basic
    @Column(name = "GENDER")
    @Enumerated(EnumType.STRING)
    private Gender gender = Gender.Male;

    @Column(table = "P2_SALARY")
    private double salary;

    @Embedded
    @AttributeOverrides( {
        @AttributeOverride(name = "startDate", column = @Column(name = "START_DATE")),
        @AttributeOverride(name = "endDate", column = @Column(name = "END_DATE")) })
    private EmploymentPeriod period;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "ADDR_ID")
    private Address address;
    
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "P2_EMP_JOB", joinColumns = @JoinColumn(name = "EMP_ID"), inverseJoinColumns = @JoinColumn(name = "TITLE_ID"))
    private JobTitle jobTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MANAGER_ID")
    private Employee manager;

    @OneToMany(mappedBy = "manager")
    private List<Employee> managedEmployees = new ArrayList<Employee>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="EMP_ID")
    private List<Degree> degrees = new ArrayList<Degree>();

    @ManyToMany
    @JoinTable(name = "P2_PROJ_EMP", joinColumns = @JoinColumn(name = "EMP_ID"), inverseJoinColumns = @JoinColumn(name = "PROJ_ID"))
    private List<Project> projects = new ArrayList<Project>();

    @ElementCollection
    @CollectionTable(name = "P2_RESPONS", joinColumns = @JoinColumn(name = "EMP_ID"))
    @Column(name = "RESPONSIBILITY")
    @OrderColumn(name = "PRIORITY")
    private List<String> responsibilities = new ArrayList<String>();

    @ElementCollection
    @CollectionTable(name = "P2_EMAIL", joinColumns = @JoinColumn(name = "EMP_ID"))
    @Column(name = "EMAIL_ADDRESS")
    @MapKeyColumn(name = "EMAIL_TYPE")
    private Map<String, EmailAddress> emailAddresses = new HashMap<String, EmailAddress>();

    public Employee() {
    }

    public long getId() {
        return id;
    }

    public void setId(long empId) {
        this.id = empId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String fName) {
        this.firstName = fName;
    }

    public Gender getGender() {
        return this.gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lName) {
        this.lastName = lName;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
    
    public List<Degree> getDegrees() {
        return degrees;
    }

    public void setDegrees(List<Degree> degrees) {
        this.degrees = degrees;
    }

    public Degree addDegree(String degree) {
        return addDegree(new Degree(degree));
    }

    public Degree addDegree(Degree degree) {
        getDegrees().add(degree);
        return degree;
    }

    public Degree removeDegree(Degree degree) {
        getDegrees().remove(degree);
        return degree;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projectList) {
        this.projects = projectList;
    }

    public Project addProject(Project project) {
        getProjects().add(project);
        return project;
    }

    public Project removeProject(Project project) {
        getProjects().remove(project);
        return project;
    }

    public Employee getManager() {
        return manager;
    }

    public void setManager(Employee employee) {
        this.manager = employee;
    }

    public List<Employee> getManagedEmployees() {
        return this.managedEmployees;
    }

    public void setManagedEmployees(List<Employee> employeeList) {
        this.managedEmployees = employeeList;
    }

    public Employee addManagedEmployee(Employee employee) {
        getManagedEmployees().add(employee);
        employee.setManager(this);
        return employee;
    }

    public Employee removeManagedEmployee(Employee employee) {
        getManagedEmployees().remove(employee);
        employee.setManager(null);
        return employee;
    }

    public List<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<PhoneNumber> phoneNumberList) {
        this.phoneNumbers = phoneNumberList;
    }

    public PhoneNumber addPhoneNumber(PhoneNumber phoneNumber) {
        getPhoneNumbers().add(phoneNumber);
        phoneNumber.setOwner(this);
        return phoneNumber;
    }

    public PhoneNumber addPhoneNumber(String type, String areaCode, String number) {
        PhoneNumber phoneNumber = new PhoneNumber(type, areaCode, number);
        return addPhoneNumber(phoneNumber);
    }

    public PhoneNumber removePhoneNumber(PhoneNumber phoneNumber) {
        getPhoneNumbers().remove(phoneNumber);
        return phoneNumber;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    public void setPeriod(EmploymentPeriod period) {
        this.period = period;
    }

    public EmploymentPeriod getPeriod() {
        return period;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public List<String> getResponsibilities() {
        return this.responsibilities;
    }

    public void setResponsibilities(List<String> responsibilities) {
        this.responsibilities = responsibilities;
    }

    public void addResponsibility(String responsibility) {
        getResponsibilities().add(responsibility);
    }

    public void removeResponsibility(String responsibility) {
        getResponsibilities().remove(responsibility);
    }
    
    public Map<String, EmailAddress> getEmailAddresses() {
        return emailAddresses;
    }

    public void setEmailAddresses(Map<String, EmailAddress> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    public EmailAddress addEmailAddress(String type, String address) {
        return addEmailAddress(type, new EmailAddress(address));
    }

    public EmailAddress addEmailAddress(String type, EmailAddress address) {
        return getEmailAddresses().put(type, address);
    }

    public EmailAddress removeEmailAddress(String type) {
        return getEmailAddresses().remove(type);
    }

    public EmailAddress getEmailAddress(String type) {
        return getEmailAddresses().get(type);
    }
    
    public JobTitle getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(JobTitle jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String toString() {
        return "Employee(" + getId() + ": " + getLastName() + ", " + getFirstName() + ")";
    }
}
