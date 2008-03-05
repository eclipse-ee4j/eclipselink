/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.xml.advanced;

import java.util.*;
import java.io.Serializable;

import javax.persistence.Transient;

/**
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
	public enum Gender { Female, Male }
	
	private int salary;
	private int version;
	private Integer id;
	
	private String firstName;
	private String lastName;
	
	private Gender gender;
	private Address address;
	private Employee manager;
	private EmploymentPeriod period;
    private Collection<Employee> managedEmployees;
    
    private Collection<PhoneNumber> phoneNumbers;
	private Collection<Project> projects;
	private Collection<String> responsibilities;
	
	private Map<String, Long> creditCards;
    private static final String AMEX = "Amex";
    private static final String DINERS = "DinersClub";
    private static final String MASTERCARD = "Mastercard";
    private static final String VISA = "Visa";
    
	public Employee () {
        phoneNumbers = new Vector<PhoneNumber>();
        projects = new Vector<Project>();
        managedEmployees = new Vector<Employee>();
        responsibilities = new Vector<String>();
        creditCards = new HashMap<String, Long>();
	}
    
    public Employee(String firstName, String lastName){
        this();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void addAmex(long number) {
        getCreditCards().put(AMEX, new Long(number));
    }
    
    public void addDinersClub(long number) {
        getCreditCards().put(DINERS, new Long(number));
    }
    
    public void addManagedEmployee(Employee emp) {
        getManagedEmployees().add(emp);
        emp.setManager(this);
    }

    public void addMastercard(long number) {
        getCreditCards().put(MASTERCARD, new Long(number));
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

    public void addVisa(long number) {
        getCreditCards().put(VISA, new Long(number));
    }
    
    public String displayString() {
        StringBuffer sbuff = new StringBuffer();
        sbuff.append("Employee ").append(getId()).append(": ").append(getLastName()).append(", ").append(getFirstName()).append(getSalary());

        return sbuff.toString();
    }
    
    // Static method should be ignored
    static public void getAbsolutelyNothing() {}
    
	public Address getAddress() { 
        return address; 
    }
    
    // Get methods with no corresponding set method, should be ignored.
    // logs a warning though.
    public String getAnEmptyString() {
        return "";
    }
    
    // EclipseLink feature, mark it transient so JPA ORM doesn't process it.
    @Transient
    public Map<String, Long> getCreditCards() {
        return creditCards;
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
    
    public Collection<Project> getProjects() { 
        return projects; 
    }
    
    // EclipseLink feature, mark it transient so JPA ORM doesn't process it.
    @Transient
    public Collection getResponsibilities() {
        return responsibilities;
    }
    
    public int getSalary() { 
        return salary; 
    }
    
    public int getVersion() { 
        return version; 
    }
    
    // Get methods with parameters should be ignored
    public String getYourStringBack(String str) {
        return str;
    }
    
    
    public boolean hasAmex(long number) {
        return hasCard(creditCards.get(AMEX), number);
    }
    
    private boolean hasCard(Long cardNumber, long number) {
        if (cardNumber == null) {
            return false;
        } else {
            return cardNumber.longValue() == number;
        }
    }
    
    public boolean hasDinersClub(long number) {
        return hasCard(creditCards.get(DINERS), number);
    }
    
    public boolean hasMastercard(long number) {
        return hasCard(creditCards.get(MASTERCARD), number);
    }
    
    public boolean hasVisa(long number) {
        return hasCard(creditCards.get(VISA), number);
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
    
    protected void setCreditCards(Map<String, Long> creditCards) {
        this.creditCards = creditCards;
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
    
	public void setManager(Employee manager) {
		this.manager = manager;
	}
	
	public void setPeriod(EmploymentPeriod period) {
		this.period = period;
	}
	
	public void setPhoneNumbers(Collection<PhoneNumber> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
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
	
	protected void setVersion(int version) {
		this.version = version;
	}    
    
    public String toString() {
        return "Employee: " + getId();
    }
}
