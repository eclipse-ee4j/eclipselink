/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//              ljungmann - initial implementation
package org.eclipse.persistence.testing.perf.jpa.model.metaannotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Version;

import org.eclipse.persistence.testing.perf.jpa.model.basic.Address;
import org.eclipse.persistence.testing.perf.jpa.model.basic.Degree;
import org.eclipse.persistence.testing.perf.jpa.model.basic.EmailAddress;
import org.eclipse.persistence.testing.perf.jpa.model.basic.EmploymentPeriod;
import org.eclipse.persistence.testing.perf.jpa.model.basic.Gender;
import org.eclipse.persistence.testing.perf.jpa.model.basic.JobTitle;
import org.eclipse.persistence.testing.perf.jpa.model.basic.Project;
import org.eclipse.samples.LoggableEntity;

@LoggableEntity
public class MetaEmployee {
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

    public MetaEmployee() {
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

    @Override
    public String toString() {
        return "Employee(" + getId() + ": " + getLastName() + ", " + getFirstName() + ")";
    }

}
