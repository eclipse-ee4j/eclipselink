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
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.sql.Time;
import java.util.*;
import java.io.Serializable;
import javax.persistence.*;

import org.eclipse.persistence.annotations.BasicCollection;
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.ChangeTracking;
import org.eclipse.persistence.annotations.CollectionTable;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Customizer;
import org.eclipse.persistence.annotations.ExistenceChecking;
import org.eclipse.persistence.annotations.IdValidation;
import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.JoinFetchType;
import org.eclipse.persistence.annotations.Mutable;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.PrimaryKey;
import org.eclipse.persistence.annotations.PrivateOwned;
import org.eclipse.persistence.annotations.Property;
import org.eclipse.persistence.annotations.Properties;
import org.eclipse.persistence.annotations.ReadTransformer;
import org.eclipse.persistence.annotations.TypeConverter;
import org.eclipse.persistence.annotations.WriteTransformer;
import org.eclipse.persistence.annotations.WriteTransformers;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;

import static org.eclipse.persistence.annotations.CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS;
import static org.eclipse.persistence.annotations.CacheType.SOFT_WEAK;
import static org.eclipse.persistence.annotations.ChangeTrackingType.AUTO;
import static org.eclipse.persistence.annotations.ExistenceType.CHECK_DATABASE;
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
@Entity
@EntityListeners(org.eclipse.persistence.testing.models.jpa.advanced.EmployeeListener.class)
@Table(name="CMP3_EMPLOYEE")
@SecondaryTable(name="CMP3_SALARY")
@PrimaryKeyJoinColumn(name="EMP_ID", referencedColumnName="EMP_ID")
@NamedNativeQuery(
    name="findAllSQLEmployees", 
    query="select * from CMP3_EMPLOYEE",
    resultClass=org.eclipse.persistence.testing.models.jpa.advanced.Employee.class
)
@NamedQueries({
@NamedQuery(
	name="findAllEmployeesByFirstName",
	query="SELECT OBJECT(employee) FROM Employee employee WHERE employee.firstName = :firstname"
),
@NamedQuery(
	name="constuctEmployees",
	query="SELECT new org.eclipse.persistence.testing.models.jpa.advanced.Employee(employee.firstName, employee.lastName) FROM Employee employee"
),
@NamedQuery(
	name="findEmployeeByPK",
	query="SELECT OBJECT(employee) FROM Employee employee WHERE employee.id = :id"
),
@NamedQuery(
        name="findEmployeeByPostalCode",
        query="SELECT e FROM Employee e where e.address.postalCode = :postalCode"
),
@NamedQuery(
        name="findAllEmployeesOrderById",
        query="SELECT e FROM Employee e order by e.id"
),
@NamedQuery(
        name="findAllEmployeesJoinAddressPhones",
        query="SELECT e FROM Employee e",
        hints={
                @QueryHint(name=QueryHints.FETCH, value="e.address"),
                @QueryHint(name=QueryHints.FETCH, value="e.phoneNumbers")
        }
),
@NamedQuery(
        name="CachedAllEmployees",
        query="SELECT e FROM Employee e",
        hints={
                @QueryHint(name=QueryHints.QUERY_RESULTS_CACHE, value="true"),
                @QueryHint(name=QueryHints.QUERY_RESULTS_CACHE_TYPE, value="FULL"),
                @QueryHint(name=QueryHints.QUERY_RESULTS_CACHE_IGNORE_NULL, value="false"),
                @QueryHint(name=QueryHints.QUERY_RESULTS_CACHE_RANDOMIZE_EXPIRY, value="true"),
                @QueryHint(name=QueryHints.QUERY_RESULTS_CACHE_SIZE, value="200"),
                @QueryHint(name=QueryHints.QUERY_RESULTS_CACHE_EXPIRY, value="50000")
        }
),
@NamedQuery(
        name="CachedNoEmployees",
        query="SELECT e FROM Employee e where 1 <> 1",
        hints={
                @QueryHint(name=QueryHints.QUERY_RESULTS_CACHE, value="true")
        }
),
@NamedQuery(
        name="CachedTimeOfDayAllEmployees",
        query="SELECT e FROM Employee e",
        hints={
                @QueryHint(name=QueryHints.QUERY_RESULTS_CACHE, value="true"),
                @QueryHint(name=QueryHints.QUERY_RESULTS_CACHE_SIZE, value="200"),
                @QueryHint(name=QueryHints.QUERY_RESULTS_CACHE_EXPIRY_TIME_OF_DAY, value="23:59:59")
        }
),
@NamedQuery(
	name="findAllEmployeesByIdAndFirstName",
	query="Select employee from Employee employee where employee.id = :id and employee.firstName = :firstName",
    hints={
                @QueryHint(name=QueryHints.PESSIMISTIC_LOCK_TIMEOUT, value="15")
    }
),
// BUG 259329 - Update named queries have a lock mode type defaulted to NONE need to 
// internally not only check for null but NONE as well.
@NamedQuery(
        name="UpdateEmployeeQueryWithLockModeNONE",
        query="UPDATE Employee e set e.salary = 100 where e.firstName like 'blah'"
)
}
)
@OptimisticLocking(
    type=VERSION_COLUMN
)
@ObjectTypeConverter(
    name="sex",
    dataType=String.class,
    objectType=org.eclipse.persistence.testing.models.jpa.advanced.Employee.Gender.class,
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
    shared=true,
    expiry=1000000,
    alwaysRefresh=false, // some test dependencies for this to be false.
    disableHits=true, // Employee customizer should set it back to false.
    coordinationType=INVALIDATE_CHANGED_OBJECTS
)
@ChangeTracking(AUTO)
@ExistenceChecking(CHECK_DATABASE)
@Customizer(org.eclipse.persistence.testing.models.jpa.advanced.EmployeeCustomizer.class)
@Properties({
    @Property(name="entityName", value="Employee"),
    @Property(name="entityIntegerProperty", value="1", valueType=Integer.class)
}
)
// overrides IdValidation.NEGATIVE set in persistence.xml
@PrimaryKey(validation=IdValidation.ZERO)
public class Employee implements Serializable, Cloneable {
    public enum EmployeeStatus {FULL_TIME, PART_TIME, CONTRACT}
    public enum Gender { Female, Male }
    public enum SalaryRate {JUNIOR, SENIOR, MANAGER, EXECUTIVE}
    public enum Weekdays { SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY }
    
    private int salary;
    private int roomNumber;
    
    private Integer id;
    private Integer version;
    
    private Gender gender;
    private EmployeeStatus status;
    private SalaryRate payScale;
    
    /** The field names intentionally do not match the property names to test method weaving. */
    private String m_lastName;
    private String m_firstName;
	
    /** Transformation mapping, a two(2) element array holding the employee's normal working hours (START_TIME & END_TIME),
    this is stored into two different fields in the employee table. */
    private Time[] normalHours;
    /** Transformation mapping, a two(2) element array holding the employee's overtime hours (OVERTIME_START_TIME & OVERTIME_END_TIME),
    this is stored into two different fields in the employee table. */
    private Time[] overtimeHours;
    
    private Address m_address;
    private Department department;
    private Employee manager;
    private EmploymentPeriod period;
    private FormerEmployment formerEmployment;
    private Set<Weekdays> workWeek;
    
    private Collection<Project> projects;
    private Collection<String> responsibilities;
    private Collection<PhoneNumber> m_phoneNumbers;
    private Collection<Employee> managedEmployees;
    private List<Dealer> dealers;
    
    private HugeProject hugeProject;
    
    public Employee () {
        this.normalHours = new Time[2];
        this.overtimeHours = new Time[2];
        this.m_phoneNumbers = new Vector<PhoneNumber>();
        this.projects = new Vector<Project>();
        this.managedEmployees = new Vector<Employee>();
        this.responsibilities = new Vector<String>();
        this.dealers = new ArrayList<Dealer>();
    }
    
    public Employee(String firstName, String lastName){
        this();
        this.m_firstName = firstName;
        this.m_lastName = lastName;
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
    
    public void addDealer(Dealer dealer) {
        dealers.add(dealer);
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
    
    // Testing - Static method should be ignored
    static public void getAbsolutelyNothing() {}
    
    @ManyToOne(cascade={PERSIST, MERGE}, fetch=LAZY)
    @JoinColumn(name="ADDR_ID")
    @Property(name="attributeName", value="address")
    public Address getAddress() { 
        return m_address; 
    }
    
    // Testing - Get methods with no corresponding set method, should be 
    // ignored. Logs a warning though.
    public String getAnEmptyString() {
        return "";
    }
    
    @OneToMany(cascade={PERSIST, MERGE})
    @JoinColumn(name="FK_EMP_ID")
    @Property(name="attributeName", value="dealers")
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

    @ManyToOne(fetch=EAGER)
    @JoinColumn(name="DEPT_ID")
    @JoinFetch(JoinFetchType.OUTER)
    @Property(name="attributeName", value="department")
    public Department getDepartment() { 
        return department; 
    }
    
    @Column(name="F_NAME")
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
        table="CMP3_EMPLOYEE_SEQ", 
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
    @Properties({
        @Property(name="attributeName", value="lastName"),
        @Property(name="BooleanProperty", value="true", valueType=Boolean.class),
        @Property(name="ByteProperty", value="1", valueType=Byte.class),
        @Property(name="CharacterProperty", value="A", valueType=Character.class),
        @Property(name="DoubleProperty", value="1", valueType=Double.class),
        @Property(name="FloatProperty", value="1", valueType=Float.class),
        @Property(name="IntegerProperty", value="1", valueType=Integer.class),
        @Property(name="LongProperty", value="1", valueType=Long.class),
        @Property(name="ShortProperty", value="1", valueType=Short.class),
        @Property(name="BigDecimalProperty", value="1", valueType=java.math.BigDecimal.class),
        @Property(name="BigIntegerProperty", value="1", valueType=java.math.BigInteger.class),
        // it's a HEX string therefore it has to have an even number of bytes 
        @Property(name="byte[]Property", value="01020304", valueType=byte[].class),
        @Property(name="char[]Property", value="abc", valueType=char[].class),
        @Property(name="Byte[]Property", value="01020304", valueType=Byte[].class),
        @Property(name="Character[]Property", value="abc", valueType=Character[].class),
        @Property(name="TimeProperty", value="13:59:59", valueType=java.sql.Time.class),
        @Property(name="TimeStampProperty", value="2008-04-10 13:59:59", valueType=java.sql.Timestamp.class),
        @Property(name="DateProperty", value="2008-04-10", valueType=java.sql.Date.class)
    })
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
    
    @Mutable(false)
    @ReadTransformer(method="buildNormalHours")
    @WriteTransformers({
        @WriteTransformer(method="getStartTime", column=@Column(name="START_TIME")),
        @WriteTransformer(method="getEndTime", column=@Column(name="END_TIME"))
    })
    @Property(name="attributeName", value="normalHours")
    protected Time[] getNormalHours() {
        return normalHours;
    }

    @Mutable(false)
    @ReadTransformer(transformerClass=org.eclipse.persistence.testing.models.jpa.advanced.AdvancedReadTransformer.class)
    @WriteTransformers({
        @WriteTransformer(transformerClass=org.eclipse.persistence.testing.models.jpa.advanced.AdvancedWriteTransformer.class, column=@Column(name="START_OVERTIME", columnDefinition="TIME")),
        @WriteTransformer(transformerClass=org.eclipse.persistence.testing.models.jpa.advanced.AdvancedWriteTransformer.class, column=@Column(name="END_OVERTIME", columnDefinition="TIME"))
    })
    @Property(name="attributeName", value="overtimeHours")
    protected Time[] getOvertimeHours() {
        return overtimeHours;
    }

    @Enumerated(EnumType.STRING)
    @Column(name="PAY_SCALE")
    @Property(name="attributeName", value="payScale")
    public SalaryRate getPayScale() {
        return payScale;
    }
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="startDate", column=@Column(name="START_DATE", nullable=false)),
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
        name="CMP3_EMP_PROJ",
        // Default for the project side and specify for the employee side
        // Will test both defaulting and set values.
        joinColumns=@JoinColumn(name="EMPLOYEES_EMP_ID", referencedColumnName="EMP_ID")
        //inverseJoinColumns=@JoinColumn(name="PROJECTS_PROJ_ID", referencedColumnName="PROJ_ID")
    )
    @Property(name="attributeName", value="projects")
    public Collection<Project> getProjects() { 
        return projects; 
    }
    
    @BasicCollection(valueColumn=@Column(name="DESCRIPTION"))
    @CollectionTable(name="CMP3_RESPONS")
    // generics left off the Collection on purpose ...
    @Property(name="attributeName", value="responsibilities")
    public Collection getResponsibilities() {
        return responsibilities;
    }

    @Column(name="ROOM_NUM")
    @Property(name="attributeName", value="roomNumber")
    public int getRoomNumber() {
        return roomNumber;
    }
    
    @Column(table="CMP3_SALARY")
    @Property(name="attributeName", value="salary")
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

    @Enumerated
    @Column(name="STATUS")
    @Property(name="attributeName", value="status")
    public EmployeeStatus getStatus() {
        return status;
    }
    
    @Version
    @Column(name="VERSION")
    @Property(name="attributeName", value="version")
    public Integer getVersion() {
        return version; 
    }
    
    @BasicCollection
    @Property(name="attributeName", value="workWeek")
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
    
    public void setAddressField(Address address){
        // Set the field directly to test if the change is still detected.
        this.m_address = address;
    }
    
    public void setDealers(List<Dealer> dealers) {
        this.dealers = dealers;
    }
    
    public void setDepartment(Department department) {
        this.department = department;
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
    
    public void setMondayToFridayWorkWeek() {
        setWorkWeek(EnumSet.of(Weekdays.MONDAY, Weekdays.FRIDAY));
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

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="formerCompany", column=@Column(name="FORMER_COMPANY", nullable=false)),
        @AttributeOverride(name="startDate", column=@Column(name="FORMER_START_DATE", nullable=false)),
        @AttributeOverride(name="endDate", column=@Column(name="FORMER_END_DATE", nullable=true))
    })
    @Property(name="attributeName", value="formerEmployment")
    public FormerEmployment getFormerEmployment() {
            return formerEmployment;
    }

    public void setFormerEmployment(FormerEmployment formerEmployment) {
            this.formerEmployment = formerEmployment;
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
    
    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
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

    @OneToOne(fetch=LAZY)
    @JoinColumn(name="HUGE_PROJ_ID")
    public HugeProject getHugeProject() { 
        return this.hugeProject; 
    }
    
    public void setHugeProject(HugeProject hugeProject) {
        this.hugeProject = hugeProject;
    }
}
