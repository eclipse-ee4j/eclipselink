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
package org.eclipse.persistence.testing.models.jpa.xml.relationships.unidirectional;

import java.util.*;
import java.io.Serializable;

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

	public Integer getId() { 
        return id; 
    }
    
	public void setId(Integer id) { 
        this.id = id; 
    }

	public int getVersion() { 
        return version; 
    }
    
	protected void setVersion(int version) {
		this.version = version;
	}

	public String getFirstName() { 
        return firstName; 
    }
    
	public void setFirstName(String name) { 
        this.firstName = name; 
    }

	public String getLastName() { 
        return lastName; 
    }
    
	public void setLastName(String name) { 
        this.lastName = name; 
    }

	public Address getAddress() { 
        return address; 
    }
    
	public void setAddress(Address address) {
		this.address = address;
	}

	public Collection<PhoneNumber> getPhoneNumbers() { 
        return phoneNumbers; 
    }
    
	public void setPhoneNumbers(Collection<PhoneNumber> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public Collection<Employee> getManagedEmployees() { 
        return managedEmployees; 
    }
    
	public void setManagedEmployees(Collection<Employee> managedEmployees) {
		this.managedEmployees = managedEmployees;
	}

	public Employee getManager() { 
        return manager; 
    }
    
	public void setManager(Employee manager) {
		this.manager = manager;
	}

	/*@ManyToMany(cascade=PERSIST)
    @JoinTable(
		name="CMP3_XML_UNI_PROJ_EMP",
        joinColumns=@JoinColumn(name="EMP_ID", referencedColumnName="EMP_ID"),
		inverseJoinColumns=@JoinColumn(name="PROJ_ID", referencedColumnName="PROJ_ID")
	)*/
	public Collection<Project> getProjects() { 
        return projects; 
    }
    
	public void setProjects(Collection<Project> projects) {
		this.projects = projects;
	}

	public int getSalary() { 
        return salary; 
    }
    
	public void setSalary(int salary) { 
        this.salary = salary; 
    }

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
