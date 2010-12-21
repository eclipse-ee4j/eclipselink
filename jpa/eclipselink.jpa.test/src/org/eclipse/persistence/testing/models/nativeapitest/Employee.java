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
package org.eclipse.persistence.testing.models.nativeapitest;

import java.util.*;
import java.io.Serializable;

public class Employee implements Serializable, Cloneable {

    public enum EmployeeStatus {FULL_TIME, PART_TIME, CONTRACT}
    public enum Gender { Female, Male }
    public enum SalaryRate {JUNIOR, SENIOR, MANAGER, EXECUTIVE}
    public enum Weekdays { SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY }
    
    private int salary;
    private int roomNumber;
    protected Integer id;
    private Integer version;
    private Gender gender;
    private EmployeeStatus status;
    private SalaryRate payScale;
    private String lastName;
    private String firstName;
    private Address address;
    private Department department;
    private Employee manager;
    private Set<Weekdays> workWeek;
    private Collection<String> responsibilities;
    private Collection<PhoneNumber> phoneNumbers;
    private Collection<Employee> managedEmployees;
    
    public Employee () {
        this.phoneNumbers = new Vector<PhoneNumber>();
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
    
    public void addResponsibility(String responsibility) {
        getResponsibilities().add(responsibility);
    }
    
    public String displayString() {
        StringBuffer sbuff = new StringBuffer();
        sbuff.append("Employee ").append(getId()).append(": ").append(getLastName()).append(", ").append(getFirstName());

        return sbuff.toString();
    }
    
    public Address getAddress() { 
        return address; 
    }
    
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
    
    public String getLastName() { 
        return lastName; 
    }
    
    public Collection<Employee> getManagedEmployees() { 
        return managedEmployees; 
    }
    
    public Employee getManager() { 
        return manager; 
    }
    
    public SalaryRate getPayScale() {
        return payScale;
    }
    
    public Collection<PhoneNumber> getPhoneNumbers() { 
        return phoneNumbers; 
    }
    
    public Collection getResponsibilities() {
        return responsibilities;
    }
    
    public int getRoomNumber() {
        return roomNumber;
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
        getPhoneNumbers().remove(phone);
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
    public void setPhoneNumbers(Collection<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
    
    public void setResponsibilities(Collection<String> responsibilities) {
        this.responsibilities = responsibilities;
    }
    
    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
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
