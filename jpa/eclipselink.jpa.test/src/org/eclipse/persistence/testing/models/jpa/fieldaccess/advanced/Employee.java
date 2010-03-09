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
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import java.util.*;
import java.io.Serializable;
import javax.persistence.*;

import org.eclipse.persistence.annotations.BasicCollection;
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CollectionTable;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Customizer;
import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.JoinFetchType;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.eclipse.persistence.annotations.PrivateOwned;
import org.eclipse.persistence.annotations.TypeConverter;
import org.eclipse.persistence.annotations.OptimisticLocking;

import static org.eclipse.persistence.annotations.CacheCoordinationType.SEND_NEW_OBJECTS_WITH_CHANGES;//INVALIDATE_CHANGED_OBJECTS;
import static org.eclipse.persistence.annotations.CacheType.FULL;//SOFT_WEAK;
import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;
import static org.eclipse.persistence.annotations.OptimisticLockingType.VERSION_COLUMN;

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
@Entity(name="Employee")
@EntityListeners(org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.EmployeeListener.class)
@Table(name="CMP3_FA_EMPLOYEE")
@SecondaryTable(name="CMP3_FA_SALARY")
@PrimaryKeyJoinColumn(name="EMP_ID", referencedColumnName="EMP_ID")
@NamedNativeQuery(
    name="findAllFieldAccessSQLEmployees", 
    query="select * from CMP3_FA_EMPLOYEE",
    resultClass=org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Employee.class
)
@NamedQueries({
@NamedQuery(
	name="findAllFieldAccessEmployeesByFirstName",
	query="SELECT OBJECT(employee) FROM Employee employee WHERE employee.firstName = :firstname"
),
@NamedQuery(
	name="constuctFieldAccessEmployees",
	query="SELECT new org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Employee(employee.firstName, employee.lastName) FROM Employee employee"
),
@NamedQuery(
	name="findFieldAccessEmployeeByPK",
	lockMode=LockModeType.PESSIMISTIC_READ,
	query="SELECT OBJECT(employee) FROM Employee employee WHERE employee.id = :id"
), 
@NamedQuery(
    name="findFieldAccessEmployeeByPostalCode",
    query="SELECT e FROM Employee e where e.address.postalCode = :postalCode"
),
@NamedQuery(
    name="findAllFieldAccessEmployeesOrderById",
    query="SELECT e FROM Employee e order by e.id"
)
}
)
@OptimisticLocking(
    type=VERSION_COLUMN
)
@ObjectTypeConverter(
    name="sex",
    dataType=String.class,
    objectType=org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Employee.Gender.class,
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
    type=FULL,
    shared=true,
    expiry=100000,
    alwaysRefresh=false, // some test dependencies for this to be false.
    disableHits=true, // Employee customizer should set it back to false.
    coordinationType=SEND_NEW_OBJECTS_WITH_CHANGES
)
@Customizer(org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.EmployeeCustomizer.class)
public class Employee implements Serializable, Cloneable {

    public enum EmployeeStatus {FULL_TIME, PART_TIME, CONTRACT}
    public enum Gender { Female, Male }
    public enum SalaryRate {JUNIOR, SENIOR, MANAGER, EXECUTIVE}
    public enum Weekdays { SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY }
    
    @Column(table="CMP3_FA_SALARY")
    private int salary;
    @Column(name="ROOM_NUM")
    private int roomNumber;
    
    @Id
    @GeneratedValue(strategy=TABLE, generator="FA_EMP_TABLE_GENERATOR")
    @TableGenerator(
        name="FA_EMP_TABLE_GENERATOR", 
        table="CMP3_FA_EMPLOYEE_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="EMPLOYEE_SEQ",
        initialValue=50
    )
    @Column(name="EMP_ID")
    protected Integer id;
    
    @Version
    @Column(name="VERSION")
    private Integer version;
    
    @Convert("sex")
    private Gender gender;
    
    @Enumerated
    @Column(name="STATUS")
    private EmployeeStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(name="PAY_SCALE")
    private SalaryRate payScale;
    
    @Column(name="L_NAME")
    private String lastName;
    
    @Column(name="F_NAME")
    private String firstName;
	
    @ManyToOne(cascade={PERSIST, MERGE}, fetch=LAZY)
    @JoinColumn(name="ADDR_ID")
    private Address address;
    
    @ManyToOne(fetch=EAGER)
    @JoinColumn(name="DEPT_ID")
    @JoinFetch(JoinFetchType.OUTER)
    private Department department;
    
    @ManyToOne(cascade=PERSIST, fetch=LAZY)
    private Employee manager;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="startDate", column=@Column(name="START_DATE", nullable=false)),
        @AttributeOverride(name="endDate", column=@Column(name="END_DATE", nullable=true))
    })
    private EmploymentPeriod period;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="formerCompany", column=@Column(name="FORMER_COMPANY", nullable=false)),
        @AttributeOverride(name="startDate", column=@Column(name="FORMER_START_DATE", nullable=false)),
        @AttributeOverride(name="endDate", column=@Column(name="FORMER_END_DATE", nullable=true))
    })
    private FormerEmployment formerEmployment;
    
    @BasicCollection
    @CollectionTable(name="CMP3_FA_EMP_WORKWEEK")
    private Set<Weekdays> workWeek;
    
    @ManyToMany(cascade={PERSIST, MERGE})
    @JoinTable(
        name="CMP3_FA_EMP_PROJ",
        // Default for the project side and specify for the employee side
        // Will test both defaulting and set values.
        joinColumns=@JoinColumn(name="EMPLOYEES_EMP_ID", referencedColumnName="EMP_ID")
        //inverseJoinColumns=@JoinColumn(name="PROJECTS_PROJ_ID", referencedColumnName="PROJ_ID")
    )
    private Collection<Project> projects;
    
    @BasicCollection(valueColumn=@Column(name="DESCRIPTION"))
    @CollectionTable(name="CMP3_FA_RESPONS")
    private Collection<String> responsibilities;
    
    @OneToMany(cascade=ALL, mappedBy="owner")
    @PrivateOwned
    private Collection<PhoneNumber> phoneNumbers;
    
    @OneToMany(cascade=ALL, mappedBy="manager")
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
    
    public Employee clone() {
        try {
            return (Employee)super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new InternalError(exception.toString());
        }
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
        StringBuffer sbuff = new StringBuffer();
        sbuff.append("Employee ").append(getId()).append(": ").append(getLastName()).append(", ").append(getFirstName()).append(getSalary());

        return sbuff.toString();
    }
    
    // Testing - Static method should be ignored
    static public void getAbsolutelyNothing() {}    
    
    public Address getAddress() { 
        return address; 
    }
    
    // Testing - Get methods with no corresponding set method, should be 
    // ignored. Logs a warning though.
    public String getAnEmptyString() {
        return "";
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
    
    // Not defined in the XML, this should get processed.   
    public String getLastName() { 
        return lastName; 
    }
    
    public Collection<Employee> getManagedEmployees() { 
        return managedEmployees; 
    }
    
    // Not defined in the XML, this should get processed.    
    public Employee getManager() { 
        return manager; 
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
    
    // generics left off the Collection on purpose ...
    public Collection getResponsibilities() {
        return responsibilities;
    }
    
    public int getRoomNumber() {
        return roomNumber;
    }    
    
    public int getSalary() { 
        return salary; 
    }
        
    public EmployeeStatus getStatus() {
        return status;
    }    
    
    public Integer getVersion() {
        return version; 
    }    
    
    public Set<Weekdays> getWorkWeek() {
        return workWeek;
    }
    
    // Testing - Get methods with parameters should be ignored
    public String getYourStringBack(String str) {
        return str;
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
    
    public void setAddressField(Address address){
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
    
    public void setManager(Employee manager) {
        this.manager = manager;
    }
    
    public void setMondayToFridayWorkWeek() {
        this.workWeek = EnumSet.of(Weekdays.MONDAY, Weekdays.FRIDAY);
    }
    
    public void setPayScale(SalaryRate payScale) {
        this.payScale = payScale;
    }
    
    public void setPeriod(EmploymentPeriod period) {
        this.period = period;
    }
    
	public FormerEmployment getFormerEmployment() {
		return formerEmployment;
	}
    
	public void setFormerEmployment(FormerEmployment formerEmployment) {
		this.formerEmployment = formerEmployment;
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
    
    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }
    
    public void setSalary(int salary) { 
        this.salary = salary; 
    }
    
    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }
    
    public void setVersion(Integer version) {
        this.version = version;
    }
    
    public void setWorkWeek(Set<Weekdays> workWeek) {
        this.workWeek = workWeek;
    }
    
    public String toString() {
        return "Employee: " + getId();
    }
    
    public boolean worksMondayToFriday() {
        if (getWorkWeek() == null) {
            return false;
        } else {
            return getWorkWeek().equals(EnumSet.of(Weekdays.MONDAY, Weekdays.FRIDAY));
        }
    }
}
