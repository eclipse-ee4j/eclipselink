/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     02/08/2012-2.4 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     09/27/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     10/09/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     02/13/2013-2.5 Guy Pelletier
//       - 397772: JPA 2.1 Entity Graph Support (XML support)
//     06/20/2014-2.5.2 Rick Curtis
//       - 437760: AttributeOverride with no column name defined doesn't work.
package org.eclipse.persistence.testing.models.jpa21.advanced;

import java.io.Serializable;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.ParameterMode;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.eclipse.persistence.annotations.TypeConverter;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;
import static javax.persistence.ParameterMode.IN;
import static javax.persistence.ParameterMode.INOUT;
import static javax.persistence.ParameterMode.OUT;
import static javax.persistence.ParameterMode.REF_CURSOR;

@Entity
@Table(name="JPA21_EMPLOYEE")
@SecondaryTable(
    name="JPA21_SALARY",
    pkJoinColumns=@PrimaryKeyJoinColumn(name="EMP_ID", referencedColumnName="EMP_ID")
)
@NamedQueries({
@NamedQuery(
    name="jpa21Employee.findAllEmployeesByFirstNameAndLastNamePos",
    query="SELECT employee FROM Employee employee WHERE employee.firstName = ?1 AND employee.lastName = ?2"
),
@NamedQuery(
        name="jpa21Employee.findAllEmployeesByFirstNameAndLastNameName",
        query="SELECT employee FROM Employee employee WHERE employee.firstName = :firstName AND employee.lastName = :lastName"
    )
})
@NamedStoredProcedureQueries({
    @NamedStoredProcedureQuery(
        name="ReadUsingMultipleResultSetMappings",
        procedureName="Read_Multiple_Result_Sets",
        resultSetMappings={"EmployeeResultSetMapping", "AddressResultSetMapping", "ProjectResultSetMapping", "EmployeeConstructorResultSetMapping"}
    ),
    @NamedStoredProcedureQuery(
        name="ReadUsingUnNamedRefCursor",
        procedureName="Read_Using_UnNamed_Cursor",
        resultClasses={Employee.class},
        parameters = {
            @StoredProcedureParameter(mode=REF_CURSOR, type=void.class)
        }
    ),
    @NamedStoredProcedureQuery(
        name="ReadUsingNamedRefCursors",
        procedureName="Read_Using_Named_Cursor",
        resultClasses={Employee.class, Address.class},
        parameters = {
            @StoredProcedureParameter(mode=REF_CURSOR, name="CUR1", type=void.class),
            @StoredProcedureParameter(mode=REF_CURSOR, name="CUR2", type=void.class)
        }
    ),
    @NamedStoredProcedureQuery(
        name="read_using_sys_cursor",
        procedureName="Read_Using_Sys_Cursor",
        parameters = {
            @StoredProcedureParameter(mode=IN, name="f_name_v", type=String.class),
            @StoredProcedureParameter(mode=REF_CURSOR, name="p_recordset", type=void.class)
        }
    )
})
@SqlResultSetMappings({
    @SqlResultSetMapping(
        name = "EmployeeResultSetMapping",
        entities = {
            @EntityResult(entityClass = Employee.class)
        }
    ),
    @SqlResultSetMapping(
        name = "EmployeeConstructorResultSetMapping",
        classes = {
            @ConstructorResult(
                targetClass = EmployeeDetails.class,
                columns = {
                    @ColumnResult(name="EMP_ID", type=Integer.class),
                    @ColumnResult(name="F_NAME", type=String.class),
                    @ColumnResult(name="L_NAME", type=String.class),
                    @ColumnResult(name="R_COUNT", type=Integer.class)
                }
            )
        }
    )
})
@ObjectTypeConverter(
    name="sex",
    dataType=String.class,
    objectType=org.eclipse.persistence.testing.models.jpa21.advanced.Employee.Gender.class,
    conversionValues={
        @ConversionValue(dataValue="F", objectValue="Female"),
        @ConversionValue(dataValue="M", objectValue="Male")
    }
)
@NamedEntityGraphs({
    @NamedEntityGraph(
        attributeNodes={
            @NamedAttributeNode("address"),
            @NamedAttributeNode(value="projects", subgraph="projects")
        },
        subgraphs={
            @NamedSubgraph(
                name="projects",
                attributeNodes={@NamedAttributeNode("properties")}
            ),
            @NamedSubgraph(
                name="projects",
                type=LargeProject.class,
                attributeNodes={@NamedAttributeNode("executive")}
            )
        }
    )
})
public class Employee implements Serializable, Cloneable {
    public enum EmployeeStatus {FULL_TIME, PART_TIME, CONTRACT}
    public enum Gender { Female, Male }

    private Long salary;
    private Long previousSalary;

    private Integer id;
    private Integer version;

    private Gender gender;
    private EmployeeStatus status;

    private String lastName;
    private String firstName;

    private Address m_address;
    private Department department;
    private Employee manager;
    private EmploymentPeriod period;

    private Collection<Project> projects;
    private Collection<String> responsibilities;
    private Collection<PhoneNumber> m_phoneNumbers;
    private Collection<Employee> managedEmployees;

    public Employee () {
        this.m_phoneNumbers = new Vector<PhoneNumber>();
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

    @ManyToOne(cascade={PERSIST, MERGE}, fetch=LAZY)
    @JoinColumn(name="ADDR_ID")
    public Address getAddress() {
        return m_address;
    }

    @ManyToOne(fetch=EAGER)
    @JoinColumn(name="DEPT_ID")
    public Department getDepartment() {
        return department;
    }

    @Column(name="F_NAME")
    public String getFirstName() {
        return firstName;
    }

    @Convert("sex")
    public Gender getGender() {
        return gender;
    }

    @Id
    @GeneratedValue(strategy=TABLE, generator="EMPLOYEE_TABLE_GENERATOR")
    @TableGenerator(
        name="EMPLOYEE_TABLE_GENERATOR",
        table="JPA21_EMPLOYEE_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="EMPLOYEE_SEQ",
        initialValue=50
    )
    @Column(name="EMP_ID", length=21)
    public Integer getId() {
        return id;
    }

    @Column(name="L_NAME")
    public String getLastName() {
        return lastName;
    }

    @OneToMany(cascade=ALL, mappedBy="manager")
    public Collection<Employee> getManagedEmployees() {
        return managedEmployees;
    }

    @ManyToOne(cascade=PERSIST, fetch=LAZY)
    public Employee getManager() {
        return manager;
    }

    @Embedded
    @AttributeOverrides({
        // This should use the spec defined column name, not the column name defined in the EmploymentPeriod
        @AttributeOverride(name="startDate", column=@Column(nullable=false)),
        @AttributeOverride(name="endDate", column=@Column(name="END_DATE", nullable=true))
    })
    public EmploymentPeriod getPeriod() {
        return period;
    }

    @OneToMany(cascade=ALL, mappedBy="owner", orphanRemoval=true)
    public Collection<PhoneNumber> getPhoneNumbers() {
        return m_phoneNumbers;
    }

    @Column(table="JPA21_SALARY")
    @javax.persistence.Convert(disableConversion=true)
    public Long getPreviousSalary() {
        return previousSalary;
    }

    @ManyToMany(cascade={PERSIST, MERGE})
    @JoinTable(
        name="JPA21_EMP_PROJ",
        joinColumns=@JoinColumn(name="EMPLOYEES_EMP_ID", referencedColumnName="EMP_ID"),
        inverseJoinColumns=@JoinColumn(name="PROJECTS_PROJ_ID", referencedColumnName="PROJ_ID")
    )
    public Collection<Project> getProjects() {
        return projects;
    }

    @ElementCollection(targetClass=String.class)
    @Column(name="DESCRIPTION")
    @CollectionTable(
        name="JPA21_RESPONS",
        joinColumns=@JoinColumn(name="EMP_ID")
    )
    public Collection getResponsibilities() {
        return responsibilities;
    }

    @Column(table="JPA21_SALARY")
    public Long getSalary() {
        return salary;
    }

    @Enumerated
    @Column(name="STATUS")
    public EmployeeStatus getStatus() {
        return status;
    }

    @Version
    @Column(name="VERSION")
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
        this.m_phoneNumbers = phoneNumbers;
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
