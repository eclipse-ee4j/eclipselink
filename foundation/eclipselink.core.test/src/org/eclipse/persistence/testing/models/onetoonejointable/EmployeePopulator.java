/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.onetoonejointable;

import java.sql.*;
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
    protected Calendar startCalendar = Calendar.getInstance();
    protected Calendar endCalendar = Calendar.getInstance();

    public EmployeePopulator() {
        this.populationManager = PopulationManager.getDefaultManager();
        this.startCalendar = Calendar.getInstance();
        this.startCalendar.set(Calendar.MILLISECOND, 0);
        this.endCalendar = Calendar.getInstance();
        this.endCalendar.set(Calendar.MILLISECOND, 0);

    }

    public Address addressExample1() {
        Address address = new Address();

        address.setCity("Toronto");
        address.setPostalCode("L5J2B5");
        address.setProvince("ONT");
        address.setStreet("1450 Acme Cr., suite 4");
        address.setCountry("Canada");
        return address;
    }

    public Address addressExample10() {
        Address address = new Address();

        address.setCity("Calgary");
        address.setPostalCode("J5J2B5");
        address.setProvince("ALB");
        address.setStreet("1111 Moose Rd.");
        address.setCountry("Canada");
        return address;
    }

    public Address addressExample11() {
        Address address = new Address();

        address.setCity("Arnprior");
        address.setPostalCode("W1A2B5");
        address.setProvince("ONT");
        address.setStreet("1 Nowhere Drive");
        address.setCountry("Canada");
        return address;
    }

    public Address addressExample12() {
        Address address = new Address();

        address.setCity("Yellow Knife");
        address.setPostalCode("Y5J2N5");
        address.setProvince("YK");
        address.setStreet("1112 Gold Rush rd.");
        address.setCountry("Canada");
        return address;
    }

    public Address addressExample2() {
        Address address = new Address();

        address.setCity("Ottawa");
        address.setPostalCode("K5J2B5");
        address.setProvince("ONT");
        address.setStreet("12 Merival Rd., suite 5");
        address.setCountry("Canada");
        return address;
    }

    public Address addressExample3() {
        Address address = new Address();

        address.setCity("Perth");
        address.setPostalCode("Y3Q2N9");
        address.setProvince("ONT");
        address.setStreet("234 I'm Lost Lane");
        address.setCountry("Canada");
        return address;
    }

    public Address addressExample4() {
        Address address = new Address();

        address.setCity("Prince Rupert");
        address.setPostalCode("K3k5DD");
        address.setProvince("BC");
        address.setStreet("3254 Real Cold Place");
        address.setCountry("Canada");
        return address;
    }

    public Address addressExample5() {
        Address address = new Address();

        address.setCity("Vancouver");
        address.setPostalCode("N5J2N5");
        address.setProvince("BC");
        address.setStreet("1111 Mountain Blvd. Floor 53, suite 6");
        address.setCountry("Canada");
        return address;
    }

    public Address addressExample6() {
        Address address = new Address();

        address.setCity("Montreal");
        address.setPostalCode("Q2S5Z5");
        address.setProvince("QUE");
        address.setStreet("1 Habs Place");
        address.setCountry("Canada");
        return address;
    }

    public Address addressExample7() {
        Address address = new Address();

        address.setCity("Metcalfe");
        address.setPostalCode("Y4F7V6");
        address.setProvince("ONT");
        address.setStreet("2 Anderson Rd.");
        address.setCountry("Canada");
        return address;
    }

    public Address addressExample8() {
        Address address = new Address();

        address.setCity("Victoria");
        address.setPostalCode("Z5J2N5");
        address.setProvince("BC");
        address.setStreet("382 Hyde Park");
        address.setCountry("Canada");
        return address;
    }

    public Address addressExample9() {
        Address address = new Address();

        address.setCity("Smith Falls");
        address.setPostalCode("C6C6C6");
        address.setProvince("ONT");
        address.setStreet("1 Chocolate Drive");
        address.setCountry("Canada");
        return address;
    }

    public Employee basicEmployeeExample1() {
        Employee employee = createEmployee();

        try {
            employee.setFirstName("Bob");
            employee.setLastName("Smith");
            employee.setMale();
            employee.setSalary(35000);
            employee.setAddress(addressExample1());
            employee.addResponsibility("Make the coffee.");
            employee.addResponsibility("Clean the kitchen.");
            
            addChildren(employee, 2);

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
            employee.setFemale();
            employee.setAddress(addressExample10());
            employee.setSalary(56232);
            
            addChildren(employee, 2);

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
            employee.setFemale();
            employee.setAddress(addressExample11());
            employee.setSalary(75000);
            
            addChildren(employee, 2);

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
            employee.setMale();
            employee.setAddress(addressExample12());
            employee.setSalary(50000);
            
            addChildren(employee, 2);

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
            employee.setMale();
            employee.setSalary(53000);
            employee.setAddress(addressExample2());
            employee.addResponsibility("Fire people for goofing off.");
            employee.addResponsibility("Hire people when more people are required.");
            
            addChildren(employee, 2);


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
            employee.setMale();
            employee.setSalary(43000);
            employee.setAddress(addressExample6());
            employee.addResponsibility("Write lots of Java code.");
            
            addChildren(employee, 2);

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
            employee.setMale();
            employee.setSalary(49631);
            employee.setAddress(addressExample5());
            employee.addResponsibility("Have to fix the Database problem.");
            
            addChildren(employee, 2);

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
            employee.setFemale();
            employee.setSalary(87000);
            employee.setAddress(addressExample4());
            employee.addResponsibility("Write code documentation.");
            
            addChildren(employee, 2);

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
            employee.setMale();
            employee.setSalary(54300);
            employee.setAddress(addressExample3());
            employee.addResponsibility("Write user specifications.");
            
            addChildren(employee, 2);

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
            employee.setFemale();
            employee.setSalary(31000);
            employee.setAddress(addressExample7());
            
            addChildren(employee, 2);

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
            employee.setMale();
            employee.setSalary(500000);
            employee.setAddress(addressExample8());
            
            addChildren(employee, 2);

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
            employee.setFemale();
            employee.setSalary(500001);
            employee.setAddress(addressExample9());
            
            addChildren(employee, 2);

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public LargeProject basicLargeProjectExample1() {
        LargeProject largeProject = createLargeProject();

        try {
            largeProject.setName("Sales Reporting");
            largeProject.setDescription("A reporting application to report on the corporations database through TopLink.");
            largeProject.setBudget(5000);
            startCalendar.set(1991, 10, 11, 12, 0, 0);
            largeProject.setMilestoneVersion(new Timestamp(startCalendar.getTime().getTime()));

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return largeProject;
    }

    public LargeProject basicLargeProjectExample2() {
        LargeProject largeProject = createLargeProject();

        try {
            largeProject.setName("Swirly Dirly");
            largeProject.setDescription("A swirly application to report on the corporations database through TopLink.");
            largeProject.setBudget(100.98);
            startCalendar.set(1999, 11, 25, 11, 40, 44);
            largeProject.setMilestoneVersion(new Timestamp(startCalendar.getTime().getTime()));

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return largeProject;
    }

    public LargeProject basicLargeProjectExample3() {
        LargeProject largeProject = createLargeProject();

        try {
            largeProject.setName("TOPEmployee Management");
            largeProject.setDescription("A management application to report on the corporations database through TopLink.");
            largeProject.setBudget(4000.98);
            startCalendar.set(1997, 10, 12, 1, 0, 0);
            largeProject.setMilestoneVersion(new Timestamp(startCalendar.getTime().getTime()));

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return largeProject;
    }

    public LargeProject basicLargeProjectExample4() {
        LargeProject largeProject = createLargeProject();

        try {
            largeProject.setName("Enterprise System");
            largeProject.setDescription("A enterprise wide application to report on the corporations database through TopLink.");
            largeProject.setBudget(40.98);
            startCalendar.set(1996, 8, 6, 6, 40, 44);
            largeProject.setMilestoneVersion(new Timestamp(startCalendar.getTime().getTime()));

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return largeProject;
    }

    public LargeProject basicLargeProjectExample5() {
        LargeProject largeProject = createLargeProject();

        try {
            largeProject.setName("Problem Reporting System");
            largeProject.setDescription("A PRS application to report on the corporations database through TopLink.");
            largeProject.setBudget(101.98);
            startCalendar.set(1997, 9, 6, 1, 40, 44);
            largeProject.setMilestoneVersion(new Timestamp(startCalendar.getTime().getTime()));

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return largeProject;
    }

    public SmallProject basicSmallProjectExample1() {
        SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("Enterprise");
            smallProject.setDescription("A enterprise wide application to report on the corporations database through TopLink.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return smallProject;
    }

    public SmallProject basicSmallProjectExample10() {
        SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("Staff Query Tool");
            smallProject.setDescription("A tool to help staff query things.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return smallProject;
    }

    public SmallProject basicSmallProjectExample2() {
        SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("Sales Reporter");
            smallProject.setDescription("A reporting application using JDK to report on the corporations database through TopLink.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return smallProject;
    }

    public SmallProject basicSmallProjectExample3() {
        SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("TOPEmployee Manager");
            smallProject.setDescription("A management application to report on the corporations database through TopLink.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return smallProject;
    }

    public SmallProject basicSmallProjectExample4() {
        SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("Problem Reporter");
            smallProject.setDescription("A PRS application to report on the corporations database through TopLink.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return smallProject;
    }

    public SmallProject basicSmallProjectExample5() {
        SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("Swirly Dirl");
            smallProject.setDescription("A swirlly application to report on the corporations database through TopLink.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return smallProject;
    }

    public SmallProject basicSmallProjectExample6() {
        SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("Bleep Blob");
            smallProject.setDescription("Bleep blob is just a nice toy.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return smallProject;
    }

    public SmallProject basicSmallProjectExample7() {
        SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("Marketing Query Tool");
            smallProject.setDescription("A tool to help marketing query things.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return smallProject;
    }

    public SmallProject basicSmallProjectExample8() {
        SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("Shipping Query Tool");
            smallProject.setDescription("A tool to help shipping query things.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return smallProject;
    }

    public SmallProject basicSmallProjectExample9() {
        SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("Accounting Query Tool");
            smallProject.setDescription("A tool to help accounting query things.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return smallProject;
    }

    /**
     * Call all of the example methods in this system to guarantee that all our objects
     * are registered in the population manager
     */
    public void buildExamples() {
        // First ensure that no preivous examples are hanging around.
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(Employee.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(SmallProject.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(LargeProject.class);

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
        largeProjectExample1();
        largeProjectExample2();
        largeProjectExample3();
        largeProjectExample4();
        largeProjectExample5();
        smallProjectExample1();
        smallProjectExample2();
        smallProjectExample3();
        smallProjectExample4();
        smallProjectExample5();
        smallProjectExample6();
        smallProjectExample7();
        smallProjectExample8();
        smallProjectExample9();
        smallProjectExample10();
    }

    protected boolean containsObject(Class domainClass, String identifier) {
        return populationManager.containsObject(domainClass, identifier);
    }

    public Employee createEmployee() {
        return new Employee();
    }

    public LargeProject createLargeProject() {
        return new LargeProject();
    }

    public SmallProject createSmallProject() {
        return new SmallProject();
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

            employee.addProject(smallProjectExample1());
            employee.addProject(smallProjectExample2());
            employee.addProject(smallProjectExample3());

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

            employee.addProject(smallProjectExample4());
            employee.addProject(smallProjectExample5());
            employee.addProject(largeProjectExample1());

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

        try {
            employee.addProject(smallProjectExample4());
            employee.addProject(largeProjectExample4());
            employee.addProject(largeProjectExample5());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

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

        try {
            employee.addProject(smallProjectExample4());
            employee.addProject(largeProjectExample1());
            employee.addProject(largeProjectExample3());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public Employee employeeExample6() {
        if (containsObject(Employee.class, "0006")) {
            return (Employee)getObject(Employee.class, "0006");
        }

        Employee employee = basicEmployeeExample6();
        registerObject(Employee.class, employee, "0006");

        try {
            employee.addProject(largeProjectExample2());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public Employee employeeExample7() {
        if (containsObject(Employee.class, "0007")) {
            return (Employee)getObject(Employee.class, "0007");
        }

        Employee employee = basicEmployeeExample7();
        registerObject(Employee.class, employee, "0007");

        try {
            employee.addProject(largeProjectExample2());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

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

    public LargeProject largeProjectExample1() {
        if (containsObject(LargeProject.class, "0001")) {
            return (LargeProject)getObject(LargeProject.class, "0001");
        }

        LargeProject largeProject = basicLargeProjectExample1();
        registerObject(largeProject, "0001");

        try {
            largeProject.setTeamLeader(employeeExample2());
            largeProject.getTeamLeader().setProjectLed(largeProject);

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return largeProject;
    }

    public LargeProject largeProjectExample2() {
        if (containsObject(LargeProject.class, "0002")) {
            return (LargeProject)getObject(LargeProject.class, "0002");
        }

        LargeProject largeProject = basicLargeProjectExample2();
        registerObject(largeProject, "0002");
        return largeProject;
    }

    public LargeProject largeProjectExample3() {
        if (containsObject(LargeProject.class, "0003")) {
            return (LargeProject)getObject(LargeProject.class, "0003");
        }

        LargeProject largeProject = basicLargeProjectExample3();
        registerObject(largeProject, "0003");
        return largeProject;
    }

    public LargeProject largeProjectExample4() {
        if (containsObject(LargeProject.class, "0004")) {
            return (LargeProject)getObject(LargeProject.class, "0004");
        }

        LargeProject largeProject = basicLargeProjectExample4();
        registerObject(largeProject, "0004");

        try {
            largeProject.setTeamLeader(employeeExample3());
            largeProject.getTeamLeader().setProjectLed(largeProject);

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return largeProject;
    }

    public LargeProject largeProjectExample5() {
        if (containsObject(LargeProject.class, "0005")) {
            return (LargeProject)getObject(LargeProject.class, "0005");
        }

        LargeProject largeProject = basicLargeProjectExample5();
        registerObject(largeProject, "0005");

        try {
            largeProject.setTeamLeader(employeeExample5());
            largeProject.getTeamLeader().setProjectLed(largeProject);

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return largeProject;
    }

    protected void registerObject(Class domainClass, Object domainObject, String identifier) {
        populationManager.registerObject(domainClass, domainObject, identifier);
    }

    protected void registerObject(Object domainObject, String identifier) {
        populationManager.registerObject(domainObject, identifier);
    }

    public SmallProject smallProjectExample1() {
        if (containsObject(SmallProject.class, "0001")) {
            return (SmallProject)getObject(SmallProject.class, "0001");
        }

        SmallProject smallProject = basicSmallProjectExample1();
        registerObject(smallProject, "0001");
        return smallProject;
    }

    public SmallProject smallProjectExample10() {
        if (containsObject(SmallProject.class, "0010")) {
            return (SmallProject)getObject(SmallProject.class, "0010");
        }

        SmallProject smallProject = basicSmallProjectExample10();
        registerObject(smallProject, "0010");

        return smallProject;
    }

    public SmallProject smallProjectExample2() {
        if (containsObject(SmallProject.class, "0002")) {
            return (SmallProject)getObject(SmallProject.class, "0002");
        }

        SmallProject smallProject = basicSmallProjectExample2();
        registerObject(smallProject, "0002");
        return smallProject;
    }

    public SmallProject smallProjectExample3() {
        if (containsObject(SmallProject.class, "0003")) {
            return (SmallProject)getObject(SmallProject.class, "0003");
        }

        SmallProject smallProject = basicSmallProjectExample3();
        registerObject(smallProject, "0003");
        return smallProject;
    }

    public SmallProject smallProjectExample4() {
        if (containsObject(SmallProject.class, "0004")) {
            return (SmallProject)getObject(SmallProject.class, "0004");
        }

        SmallProject smallProject = basicSmallProjectExample4();
        registerObject(smallProject, "0004");
        return smallProject;
    }

    public SmallProject smallProjectExample5() {
        if (containsObject(SmallProject.class, "0005")) {
            return (SmallProject)getObject(SmallProject.class, "0005");
        }

        SmallProject smallProject = basicSmallProjectExample5();
        registerObject(smallProject, "0005");
        return smallProject;
    }

    public SmallProject smallProjectExample6() {
        if (containsObject(SmallProject.class, "0006")) {
            return (SmallProject)getObject(SmallProject.class, "0006");
        }

        SmallProject smallProject = basicSmallProjectExample6();
        registerObject(smallProject, "0006");
        return smallProject;
    }

    public SmallProject smallProjectExample7() {
        if (containsObject(SmallProject.class, "0007")) {
            return (SmallProject)getObject(SmallProject.class, "0007");
        }

        SmallProject smallProject = basicSmallProjectExample7();
        registerObject(smallProject, "0007");
        return smallProject;
    }

    public SmallProject smallProjectExample8() {
        if (containsObject(SmallProject.class, "0008")) {
            return (SmallProject)getObject(SmallProject.class, "0008");
        }

        SmallProject smallProject = basicSmallProjectExample8();
        registerObject(smallProject, "0008");
        return smallProject;
    }

    public SmallProject smallProjectExample9() {
        if (containsObject(SmallProject.class, "0009")) {
            return (SmallProject)getObject(SmallProject.class, "0009");
        }

        SmallProject smallProject = basicSmallProjectExample9();
        registerObject(smallProject, "0009");
        return smallProject;
    }
    
    protected void addChildren(Employee emp, int num) {
        for(int i=0; i < num; i++) {
            emp.addChild(new Child(Integer.toString(i), emp.getLastName()));
        }
    }
}