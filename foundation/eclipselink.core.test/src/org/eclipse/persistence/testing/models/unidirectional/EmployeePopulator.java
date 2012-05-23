/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     ailitchev - Uni-directional OneToMany
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.unidirectional;

import java.util.*;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;

/**
 * <p><b>Purpose</b>: To build and populate the database for example and testing purposes.
 * This population routine is fairly complex and makes use of the population manager to
 * resolve iterated objects as the employee objects are an interconnection graph of objects.
 *
 * This is not the recommended way to create new objects in your application,
 * this is just the easiest way to create interconnected new example objects from code.
 * Normally in your application the objects will be defined as part of a transactional and user interactive process.
 */
public class EmployeePopulator {
    protected PopulationManager populationManager;
    static HashMap<String, Integer> phoneNumbers = new HashMap<String, Integer>();

    public EmployeePopulator() {
        this.populationManager = PopulationManager.getDefaultManager();
    }

    public Employee basicEmployeeExample1() {
        Employee employee = createEmployee();

        try {
            employee.setFirstName("Bob");
            employee.setLastName("Smith");
            employee.addPhoneNumber(phoneNumberExample1());
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public Employee basicEmployeeExample10() {
        Employee employee = createEmployee();

        try {
            employee.setFirstName("Jill");
            employee.setLastName("May");
            employee.addPhoneNumber(phoneNumberExample1());
            employee.addPhoneNumber(phoneNumberExample2());
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public Employee basicEmployeeExample11() {
        Employee employee = createEmployee();

        try {
            employee.setFirstName("Sarah-loo");
            employee.setLastName("Smitty");
            employee.addPhoneNumber(phoneNumberExample2());
            employee.addPhoneNumber(phoneNumberExample3());
            employee.addPhoneNumber(phoneNumberExample4());
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public Employee basicEmployeeExample12() {
        Employee employee = createEmployee();

        try {
            employee.setFirstName("Jim-bob");
            employee.setLastName("Jefferson");
            employee.addPhoneNumber(phoneNumberExample3());
            employee.addPhoneNumber(phoneNumberExample4());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public Employee basicEmployeeExample2() {
        Employee employee = createEmployee();

        try {
            employee.setFirstName("John");
            employee.setLastName("Way");
            employee.addPhoneNumber(phoneNumberExample1());
            employee.addPhoneNumber(phoneNumberExample6());
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public Employee basicEmployeeExample3() {
        Employee employee = createEmployee();

        try {
            employee.setFirstName("Charles");
            employee.setLastName("Chanley");
            employee.addPhoneNumber(phoneNumberExample5());
            employee.addPhoneNumber(phoneNumberExample6());
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public Employee basicEmployeeExample4() {
        Employee employee = createEmployee();

        try {
            employee.setFirstName("Emanual");
            employee.setLastName("Smith");
            employee.addPhoneNumber(phoneNumberExample2());
            employee.addPhoneNumber(phoneNumberExample4());
            employee.addPhoneNumber(phoneNumberExample5());
            employee.addPhoneNumber(phoneNumberExample6());
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public Employee basicEmployeeExample5() {
        Employee employee = createEmployee();

        try {
            employee.setFirstName("Sarah");
            employee.setLastName("Way");
            employee.addPhoneNumber(phoneNumberExample1());
            employee.addPhoneNumber(phoneNumberExample6());
            employee.addPhoneNumber(phoneNumberExample3());
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public Employee basicEmployeeExample6() {
        Employee employee = createEmployee();

        try {
            employee.setFirstName("Marcus");
            employee.setLastName("Saunders");
            employee.addPhoneNumber(phoneNumberExample6());
            employee.addPhoneNumber(phoneNumberExample1());
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public Employee basicEmployeeExample7() {
        Employee employee = createEmployee();

        try {
            employee.setFirstName("Nancy");
            employee.setLastName("White");
            employee.addPhoneNumber(phoneNumberExample3());
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public Employee basicEmployeeExample8() {
        Employee employee = createEmployee();

        try {
            employee.setFirstName("Fred");
            employee.setLastName("Jones");
            employee.addPhoneNumber(phoneNumberExample4());
            employee.addPhoneNumber(phoneNumberExample6());
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public Employee basicEmployeeExample9() {
        Employee employee = createEmployee();

        try {
            employee.setFirstName("Betty");
            employee.setLastName("Jones");
            employee.addPhoneNumber(phoneNumberExample1());
            employee.addPhoneNumber(phoneNumberExample6());
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    /**
     * Call all of the example methods in this system to guarantee that all our objects
     * are registered in the population manager
     */
    public void buildExamples() {
        // First ensure that no previous examples are hanging around.
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(Employee.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(PhoneNumber.class);
        
        clearPhoneNumbers();

        employeeExample1();
        employeeExample2();
        employeeExample3();
        employeeExample4();
        employeeExample5();
        employeeExample6();
        employeeExample7();
        employeeExample8();
        employeeExample9();
        employeeExample10();
        employeeExample11();
        employeeExample12();
    }

    protected boolean containsObject(Class domainClass, String identifier) {
        return populationManager.containsObject(domainClass, identifier);
    }

    public Employee createEmployee() {
        return new Employee();
    }

    public Employee employeeExample1() {
        if (containsObject(Employee.class, "0001")) {
            return (Employee)getObject(Employee.class, "0001");
        }

        Employee employee = basicEmployeeExample1();
        registerObject(Employee.class, employee, "0001");

        try {
            employee.addManagedEmployee(employeeExample3());
            employee.addManagedEmployee(employeeExample4());
            employee.addManagedEmployee(employeeExample5());
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public Employee employeeExample10() {
        if (containsObject(Employee.class, "0010")) {
            return (Employee)getObject(Employee.class, "0010");
        }

        Employee employee = basicEmployeeExample10();
        try {
            employee.addManagedEmployee(employeeExample12());
        } catch (Exception exception) {
        }
        registerObject(Employee.class, employee, "0010");

        return employee;
    }

    public Employee employeeExample11() {
        if (containsObject(Employee.class, "0011")) {
            return (Employee)getObject(Employee.class, "0011");
        }

        Employee employee = basicEmployeeExample11();
        try {
            employee.addManagedEmployee(employeeExample7());
        } catch (Exception exception) {
        }
        registerObject(Employee.class, employee, "0011");

        return employee;
    }

    public Employee employeeExample12() {
        if (containsObject(Employee.class, "0012")) {
            return (Employee)getObject(Employee.class, "0012");
        }

        Employee employee = basicEmployeeExample12();
        registerObject(Employee.class, employee, "0012");

        try {
            employee.addManagedEmployee(employeeExample2());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public Employee employeeExample2() {
        if (containsObject(Employee.class, "0002")) {
            return (Employee)getObject(Employee.class, "0002");
        }

        Employee employee = basicEmployeeExample2();
        registerObject(Employee.class, employee, "0002");

        try {
            employee.addManagedEmployee(employeeExample6());
            employee.addManagedEmployee(employeeExample1());
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public Employee employeeExample3() {
        if (containsObject(Employee.class, "0003")) {
            return (Employee)getObject(Employee.class, "0003");
        }

        Employee employee = basicEmployeeExample3();
        registerObject(Employee.class, employee, "0003");

        return employee;
    }

    public Employee employeeExample4() {
        if (containsObject(Employee.class, "0004")) {
            return (Employee)getObject(Employee.class, "0004");
        }

        Employee employee = basicEmployeeExample4();
        registerObject(Employee.class, employee, "0004");

        return employee;
    }

    public Employee employeeExample5() {
        if (containsObject(Employee.class, "0005")) {
            return (Employee)getObject(Employee.class, "0005");
        }

        Employee employee = basicEmployeeExample5();
        registerObject(Employee.class, employee, "0005");

        return employee;
    }

    public Employee employeeExample6() {
        if (containsObject(Employee.class, "0006")) {
            return (Employee)getObject(Employee.class, "0006");
        }

        Employee employee = basicEmployeeExample6();
        registerObject(Employee.class, employee, "0006");

        return employee;
    }

    public Employee employeeExample7() {
        if (containsObject(Employee.class, "0007")) {
            return (Employee)getObject(Employee.class, "0007");
        }

        Employee employee = basicEmployeeExample7();
        registerObject(Employee.class, employee, "0007");

        return employee;
    }

    public Employee employeeExample8() {
        if (containsObject(Employee.class, "0008")) {
            return (Employee)getObject(Employee.class, "0008");
        }

        Employee employee = basicEmployeeExample8();
        registerObject(Employee.class, employee, "0008");

        return employee;
    }

    public Employee employeeExample9() {
        if (containsObject(Employee.class, "0009")) {
            return (Employee)getObject(Employee.class, "0009");
        }

        Employee employee = basicEmployeeExample9();
        registerObject(Employee.class, employee, "0009");

        return employee;
    }

    protected Vector getAllObjects() {
        return populationManager.getAllObjects();
    }

    public Vector getAllObjectsForClass(Class domainClass) {
        return populationManager.getAllObjectsForClass(domainClass);
    }

    protected Object getObject(Class domainClass, String identifier) {
        return populationManager.getObject(domainClass, identifier);
    }

    public void clearPhoneNumbers() {
        phoneNumbers.clear();
    }
    
    public PhoneNumber phoneNumberExample1() {
        return new PhoneNumber("Work", "613",  getPhoneNumber("613"));
    }

    public PhoneNumber phoneNumberExample2() {
        return new PhoneNumber("Work Fax", "613", getPhoneNumber("613"));
    }

    public PhoneNumber phoneNumberExample3() {
        return new PhoneNumber("Home", "613", getPhoneNumber("613"));
    }

    public PhoneNumber phoneNumberExample4() {
        return new PhoneNumber("Cellular", "416", getPhoneNumber("416"));
    }

    public PhoneNumber phoneNumberExample5() {
        return new PhoneNumber("Pager", "976", getPhoneNumber("976"));
    }

    public PhoneNumber phoneNumberExample6() {
        return new PhoneNumber("ISDN", "905", getPhoneNumber("905"));
    }
    
    public String getPhoneNumber(String areaCode) {
        Integer phoneNumber = phoneNumbers.get(areaCode);
        if(phoneNumber == null) {
            phoneNumber = 1000001;
        }
        phoneNumbers.put(areaCode, phoneNumber + 1);
        return phoneNumber.toString();
    }

    protected void registerObject(Class domainClass, Object domainObject, String identifier) {
        populationManager.registerObject(domainClass, domainObject, identifier);
    }

    protected void registerObject(Object domainObject, String identifier) {
        populationManager.registerObject(domainObject, identifier);
    }

}
