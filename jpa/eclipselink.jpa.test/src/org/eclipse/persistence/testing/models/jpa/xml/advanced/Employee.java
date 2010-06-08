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
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     02/25/2009-2.0 Guy Pelletier 
 *       - 265359: JPA 2.0 Element Collections - Metadata processing portions
 *     11/06/2009-2.0 Guy Pelletier 
 *       - 286317: UniqueConstraint xml element is changing (plus couple other fixes, see bug)
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.advanced;

import java.sql.Time;
import java.util.*;
import java.io.Serializable;

import javax.persistence.Transient;

import org.eclipse.persistence.annotations.Properties;
import org.eclipse.persistence.annotations.Property;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

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
@Properties({
    // This @Property should be overridden by a property with the same name defined in xml.
    @Property(name="ToBeOverriddenByXml", value="false", valueType=Boolean.class),
    // This property should be set - there's no property with the same name defined in xml.
    @Property(name="ToBeProcessed", value="true", valueType=Boolean.class)
})
public class Employee implements Serializable {
	public enum Gender { Female, Male }
	public enum SalaryRate {JUNIOR, SENIOR, MANAGER, EXECUTIVE}
	
	private int salary;
	private int version;
	private Integer id;
	
	private String firstName;
	
    // Currently the property annotation on an attribute should ignored in case there's orm xml.
    @Property(name="ToBeIgnored", value="true", valueType=Boolean.class)
	private String lastName;
	
	private Gender gender;
	private Address address;
	private Employee manager;
	private SalaryRate payScale;
	private EmploymentPeriod period;
    private Collection<Employee> managedEmployees;
    
    private Collection<PhoneNumber> phoneNumbers;
	private Collection<Project> projects;
	private Collection<String> responsibilities;
    private List<Dealer> dealers;
	
	private Map<String, Long> creditCards;
    private static final String AMEX = "Amex";
    private static final String DINERS = "DinersClub";
    private static final String MASTERCARD = "Mastercard";
    private static final String VISA = "Visa";
    
    private Map<String, Long> creditLines;
    private static final String ROYAL_BANK = "RoyalBank";
    private static final String CANADIAN_IMPERIAL = "CanadianImperial";
    private static final String SCOTIABANK = "Scotiabank";
    private static final String TORONTO_DOMINION = "TorontoDominion";
    
    /** Transformation mapping, a two(2) element array holding the employee's normal working hours (START_TIME & END_TIME),
    this is stored into two different fields in the employee table. */
    private Time[] normalHours;
    /** Transformation mapping, a two(2) element array holding the employee's overtime hours (OVERTIME_START_TIME & OVERTIME_END_TIME),
    this is stored into two different fields in the employee table. */
    private Time[] overtimeHours;
    
    private String socialInsuranceNumber;
    //Transient value used to keep track of how many times enterSIN method was called
    private int sinChangeCounter = 0;
    
	public Employee () {
        phoneNumbers = new Vector<PhoneNumber>();
        projects = new Vector<Project>();
        managedEmployees = new Vector<Employee>();
        responsibilities = new Vector<String>();
        dealers = new ArrayList<Dealer>();
        creditCards = new HashMap<String, Long>();
        creditLines = new HashMap<String, Long>();
        normalHours = new Time[2];
        overtimeHours = new Time[2];
	}
    
    public Employee(String firstName, String lastName){
        this();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void addAmex(long number) {
        getCreditCards().put(AMEX, new Long(number));
    }
    
    public void addCanadianImperialCreditLine(long number) {
        getCreditLines().put(CANADIAN_IMPERIAL, new Long(number));
    }
    
    public void addDealer(Dealer dealer) {
        dealers.add(dealer);
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
        phone.owner = this;
        getPhoneNumbers().add(phone);
    }

    public void addProject(Project theProject) {
        getProjects().add(theProject);
    }
    
    public void addResponsibility(String responsibility) {
        getResponsibilities().add(responsibility);
    }

    public void addRoyalBankCreditLine(long number) {
        getCreditLines().put(ROYAL_BANK, new Long(number));
    }
    
    public void addScotiabankCreditLine(long number) {
        getCreditLines().put(SCOTIABANK, new Long(number));
    }
    
    public void addTorontoDominionCreditLine(long number) {
        getCreditLines().put(TORONTO_DOMINION, new Long(number));
    }
    
    public void addVisa(long number) {
        getCreditCards().put(VISA, new Long(number));
    }
    
    /**
     * Builds the normalHours Vector.
     * IMPORTANT: This method builds the value but does not set it.
     * The mapping will set it using method or direct access as defined in the descriptor.
     */
    public Time[] buildNormalHours(Record row, Session session) {
        Time[] hours = new Time[2];

        /** This conversion allows for the database type not to match, i.e. may be a Timestamp or String. */
        hours[0] = (Time) session.getDatasourcePlatform().convertObject(row.get("START_TIME"), java.sql.Time.class);
        hours[1] = (Time) session.getDatasourcePlatform().convertObject(row.get("END_TIME"), java.sql.Time.class);
        return hours;
    }

    public String displayString() {
        StringBuffer sbuff = new StringBuffer();
        sbuff.append("Employee ").append(getId()).append(": ").append(getLastName()).append(", ").append(getFirstName()).append(getSalary());

        return sbuff.toString();
    }
    
    //used for test user defined getter and setter methods
    public void enterSIN(String socialInsuranceNumber){
        this.sinChangeCounter++;
        this.socialInsuranceNumber=socialInsuranceNumber;
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
    
    // EclipseLink feature, mark it transient so JPA ORM doesn't process it.
    @Transient
    public Map<String, Long> getCreditLines() {
        return creditLines;
    }
    
    public List<Dealer> getDealers() {
        return dealers;
    }
    
    /**
     * Return the last element of the Transformation mapped overtimeHours.
     */
    @Transient
    public Time getEndOvertime() {
        return getOvertimeHours()[1];
    }

    /**
     * Return the last element of the Transformation mapped normalHours.
     */
    @Transient
    public Time getEndTime() {
        return getNormalHours()[1];
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
	
	@Transient
	protected Time[] getNormalHours() {
        return normalHours;
    }

    @Transient
    protected Time[] getOvertimeHours() {
        return overtimeHours;
    }

    public SalaryRate getPayScale() {
        return payScale;
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
    
    public Collection getResponsibilities() {
        return responsibilities;
    }
    
    public int getSalary() { 
        return salary; 
    }
    
    /**
     * Return the first element of the Transformation mapped overtimeHours.
     */
    @Transient
    public java.sql.Time getStartOvertime() {
        return getOvertimeHours()[0];
    }

    /**
     * Return the first element of the Transformation mapped normalHours.
     */
    @Transient
    public java.sql.Time getStartTime() {
        return getNormalHours()[0];
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
    
    public boolean hasCanadianImperialCreditLine(long number) {
        return hasCreditLine(creditLines.get(CANADIAN_IMPERIAL), number);
    }
    
    private boolean hasCard(Long cardNumber, long number) {
        if (cardNumber == null) {
            return false;
        } else {
            return cardNumber.longValue() == number;
        }
    }
    
    private boolean hasCreditLine(Long creditLineNumber, long number) {
        if (creditLineNumber == null) {
            return false;
        } else {
            return creditLineNumber.longValue() == number;
        }
    }
    
    public boolean hasDinersClub(long number) {
        return hasCard(creditCards.get(DINERS), number);
    }
    
    public boolean hasMastercard(long number) {
        return hasCard(creditCards.get(MASTERCARD), number);
    }
    
    public boolean hasRoyalBankCreditLine(long number) {
        return hasCreditLine(creditLines.get(ROYAL_BANK), number);
    }
    
    public boolean hasScotiabankCreditLine(long number) {
        return hasCreditLine(creditLines.get(SCOTIABANK), number);
    }
    
    public boolean hasTorontoDominionCreditLine(long number) {
        return hasCreditLine(creditLines.get(TORONTO_DOMINION), number);
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
    
    public void removeDealer(Dealer dealer) {
        dealers.remove(dealer);
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
    
    //used for test user defined getter and setter methods
    public String returnSIN(){
        return socialInsuranceNumber;
    }
    
    //used to keep track of how many times enterSIN was called
    public int returnSinChangeCounter(){
        return this.sinChangeCounter;
    }

    public void setAddress(Address address) {
		this.address = address;
	}
    
    protected void setCreditCards(Map<String, Long> creditCards) {
        this.creditCards = creditCards;
    }  
    
    protected void setCreditLines(Map<String, Long> creditLines) {
        this.creditLines = creditLines;
    } 
    
    public void setDealers(List<Dealer> dealers) {
        this.dealers = dealers;
    }
    
    /**
     * Set the last element of the Transformation mapped overtimeHours.
     * In order to have change tracking, the transformation mapping is not mutable,
     * therefore the whole new array should be created in case either element is changed.
     */
    public void setEndOvertime(Time endOvertime) {
        Time[] newOvertimeHours = new Time[] {getStartOvertime(), endOvertime};
        setOvertimeHours(newOvertimeHours);
    }

    /**
     * Set the last element of the Transformation mapped normalHours.
     * In order to have change tracking, the transformation mapping is not mutable,
     * therefore the whole new array should be created in case either element is changed.
     */
    public void setEndTime(Time endTime) {
        Time[] newNormalHours = new Time[] {getStartTime(), endTime};
        setNormalHours(newNormalHours);
    }

    public void setFemale() {
        this.gender = Gender.Female;
    }
    
    // This mapping has been marked as having field access. I've therefore
    // renamed the set method to test that the processing does not process
    // the mapping as PROPERTY and look for an equivalent set method for the
    // get. Otherwise a processing error will occur.
    public void setGivenName(String name) { 
        this.firstName = name; 
    }
    
    public void setGender(Gender gender) { 
        this.gender = gender; 
    }
    
	public void setId(Integer id) { 
        this.id = id; 
    }
	
	// This mapping has been marked as having field access. I've therefore
    // renamed the set method to test that the processing does not process
    // the mapping as PROPERTY and look for an equivalent set method for the
    // get. Otherwise a processing error will occur.
	public void setFamilyName(String name) { 
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
	
    public void setNormalHours(Time[] normalHours) {
        this.normalHours = normalHours;
    }

    public void setOvertimeHours(Time[] overtimeHours) {
        this.overtimeHours = overtimeHours;
    }

    public void setPayScale(SalaryRate payScale) {
        this.payScale = payScale;
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
	
    /**
     * Set the first element of the Transformation mapped overtimeHours.
     * In order to have change tracking, the transformation mapping is not mutable,
     * therefore the whole new array should be created in case either element is changed.
     */
    public void setStartOvertime(Time startOvertime) {
        Time[] newOvertimeHours = new Time[] {startOvertime, getEndOvertime()};
        setOvertimeHours(newOvertimeHours);
    }

    /**
     * Set the first element of the Transformation mapped normalHours.
     * In order to have change tracking, the transformation mapping is not mutable,
     * therefore the whole new array should be created in case either element is changed.
     */
    public void setStartTime(Time startTime) {
        Time[] newNormalHours = new Time[] {startTime, getEndTime()};
        setNormalHours(newNormalHours);
    }

	protected void setVersion(int version) {
		this.version = version;
	}    
    
    public String toString() {
        return "Employee: " + getId();
    }
}
