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
package org.eclipse.persistence.testing.models.jpa.xml.merge.advanced;

import java.util.*;
import java.io.Serializable;
import javax.persistence.*;
import static javax.persistence.GenerationType.*;
import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

/**
 * Bean class: EmployeeBean
 * Remote interface: Employee
 * Primary key class: EmployeePK
 * Home interface: EmployeeHome
 *
 * Employees have a one-to-many relationship with Employees through the 
 * managedEmployees attribute.
 * Addresses exist in one-to-one relationships with Employees through the
 * address attribute.
 * Employees have a many-to-many relationship with Projects through the
 * projects attribute.
 *  
 * Employee now has invalid annotation fields and data. This is done so that
 * we may test the XML/Annotation merging. Employee has been defined in the
 * XML, therefore, most annotations should not be processed. If they are, then
 * they will force an error, which means something is wrong with our merging.
 *  
 * The invalid annotations that should not be processed have _INVALID
 * appended to some annotation field member. Others will not have this,
 * which means they should be processed (their mappings are not defined in the
 * XML)
 */
@Entity(name="AnnMergeEmplyee")
@EntityListeners(org.eclipse.persistence.testing.models.jpa.xml.merge.advanced.EmployeeListener.class)
@Table(name="CMP3_ANN_MERGE_EMPLOYEE")
@SecondaryTable(name="CMP3_ANN_MERGE_SALARY")
@PrimaryKeyJoinColumn(name="ANN_MERGE_EMP_ID", referencedColumnName="ANN_MERGE_EMP_ID")
@NamedQueries({
@NamedQuery(
	name="ann_merge_findAllEmployeesByFirstName",
	query="SELECT OBJECT(employee) FROM Employee employee WHERE employee.firstName = :firstname"
),
@NamedQuery(
	name="ann_merge_constuctEmployees",
	query="SELECT new org.eclipse.persistence.testing.models.jpa.xml.merge.advanced.Employee(employee.firstName, employee.lastName) FROM Employee employee")
}
)
public class Employee implements Serializable {
	private Integer id;
	private int version;
	private String firstName;
	private String lastName;
	private Address address;
	private Collection<PhoneNumber> phoneNumbers;
	private Collection<Project> projects;
	private int salary;
	private EmploymentPeriod period;
    private Collection<Employee> managedEmployees;
    private Employee manager;
    
	public Employee () {
        this.phoneNumbers = new Vector<PhoneNumber>();
        this.projects = new Vector<Project>();
        this.managedEmployees = new Vector<Employee>();
	}
    
    public Employee(String firstName, String lastName){
        this();
        this.firstName = firstName;
        this.lastName = lastName;
    }

	@Id
    @GeneratedValue(strategy=TABLE, generator="ANN_MERGE_EMPLOYEE_TABLE_GENERATOR")
	@TableGenerator(
        name="ANN_MERGE_EMPLOYEE_TABLE_GENERATOR", 
        table="CMP3_ANN_MERGE_EMPLOYEE_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="ANN_MERGE_EMPLOYEE_SEQ"
    )
    @Column(name="ANN_MERGE_EMP_ID")
	public Integer getId() { 
        return id; 
    }
    
	public void setId(Integer id) { 
        this.id = id; 
    }

	@Version
	@Column(name="ANN_MERGE_VERSION")
	public int getVersion() { 
        return version; 
    }
    
	protected void setVersion(int version) {
		this.version = version;
	}

    @Column(name="ANN_MERGE_F_NAME")
	public String getFirstName() { 
        return firstName; 
    }
    
	public void setFirstName(String name) { 
        this.firstName = name; 
    }

    @Transient
	public String getLastName() { 
        return lastName; 
    }
    
	public void setLastName(String name) { 
        this.lastName = name; 
    }

	@ManyToOne(cascade=PERSIST, fetch=LAZY)
	@JoinColumn(name="ANN_MERGE_ADDR_ID")
	public Address getAddress() { 
        return address; 
    }
    
	public void setAddress(Address address) {
		this.address = address;
	}

	@OneToMany(cascade=ALL, mappedBy="owner")
	public Collection<PhoneNumber> getPhoneNumbers() { 
        return phoneNumbers; 
    }
    
	public void setPhoneNumbers(Collection<PhoneNumber> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	@OneToMany(cascade=ALL, mappedBy="manager")
	public Collection<Employee> getManagedEmployees() { 
        return managedEmployees; 
    }
    
	public void setManagedEmployees(Collection<Employee> managedEmployees) {
		this.managedEmployees = managedEmployees;
	}

    // Not defined in the XML, this should get processed.
	@ManyToOne(cascade=PERSIST, fetch=LAZY)
	public Employee getManager() { 
        return manager; 
    }
    
	public void setManager(Employee manager) {
		this.manager = manager;
	}

	@ManyToMany(cascade=PERSIST)
    @JoinTable(
		name="CMP3_EMP_PROJ",
        // Default for the project side and specify for the employee side
        // Will test both defaulting and set values.
        joinColumns=@JoinColumn(name="ANN_MERGE_EMPLOYEES_EMP_ID", referencedColumnName="ANN_MERGE_EMP_ID")
		//inverseJoinColumns=@JoinColumn(name="PROJECTS_PROJ_ID", referencedColumnName="PROJ_ID")
	)
	public Collection<Project> getProjects() { 
        return projects; 
    }
    
	public void setProjects(Collection<Project> projects) {
		this.projects = projects;
	}

    @Column(table="CMP3_ANN_MERGE_SALARY")
	public int getSalary() { 
        return salary; 
    }
    
	public void setSalary(int salary) { 
        this.salary = salary; 
    }

	@Embedded
    @AttributeOverrides({
		@AttributeOverride(name="startDate", column=@Column(name="ANN_MERGE_START_DATE", nullable=false)),
		@AttributeOverride(name="endDate", column=@Column(name="ANN_MERGE_END_DATE", nullable=true))
	})
	public EmploymentPeriod getPeriod() {
		return period;
	}
    
	public void setPeriod(EmploymentPeriod period) {
		this.period = period;
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

    public String toString() {
        return "Employee: " + getId();
    }

    public String displayString() {
        StringBuffer sbuff = new StringBuffer();
        sbuff.append("Employee ").append(getId()).append(": ").append(getLastName()).append(", ").append(getFirstName()).append(getSalary());

        return sbuff.toString();
    }
    
    // These methods were added for testing purpose only - BUG 4349991
    
    // Static method should be ignored
    static public void getAbsolutelyNothing() {}
    
    // Get methods with parameters should be ignored
    public String getYourStringBack(String str) {
        return str;
    }
    
    // Get methods with no corresponding set method, should be ignored.
    // logs a warning though.
    public String getAnEmptyString() {
        return "";
    }
}
