/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     01/23/2013-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     02/13/2013-2.5 Guy Pelletier
//       - 397772: JPA 2.1 Entity Graph Support (XML support)
package org.eclipse.persistence.testing.models.jpa22.advanced.xml;

import java.util.Calendar;
import java.util.Vector;

import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.tools.schemaframework.PackageDefinition;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.schemaframework.StoredProcedureDefinition;

/**
 * <p><b>Purpose</b>: To build and populate the database for example and testing
 * purposes. This population routine is fairly complex and makes use of the
 * population manager to resolve interrelated objects as the employee objects
 * are an interconnection graph of objects.
 *
 * This is not the recommended way to create new objects in your application,
 * this is just the easiest way to create interconnected new example objects
 * from code. Normally in your application the objects will be defined as part
 * of a transactional and user interactive process.
 *
 * Be careful in changing any of the examples (names, projects etc) as they may
 * be used and relied on in testing.
 */
public class EmployeePopulator {
    protected static boolean useFastTableCreatorAfterInitialCreate = Boolean.getBoolean("eclipselink.test.toggle-fast-table-creator");
    protected static boolean isFirstCreation = true;

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
        address.setPostalCode("L5J 2B5");
        address.setProvince("ONT");
        address.setStreet("1450 Acme Cr., suite 4");
        address.setCountry("Canada");
        return address;
    }

    public Address addressExample10() {
        Address address = new Address();
        address.setCity("Calgary");
        address.setPostalCode("J5J 2B5");
        address.setProvince("ALB");
        address.setStreet("1111 Moose Rd.");
        address.setCountry("Canada");
        return address;
    }

    public Address addressExample11() {
        Address address = new Address();
        address.setCity("Arnprior");
        address.setPostalCode("W1A 2B5");
        address.setProvince("ONT");
        address.setStreet("1 Nowhere Drive");
        address.setCountry("Canada");
        return address;
    }

    public Address addressExample12() {
        Address address = new Address();
        address.setCity("Yellow Knife");
        address.setPostalCode("Y5J 2N5");
        address.setProvince("YK");
        address.setStreet("1112 Gold Rush rd.");
        address.setCountry("Canada");
        return address;
    }

    public Address addressExample2() {
        Address address = new Address();
        address.setCity("Ottawa");
        address.setPostalCode("K5J 2B5");
        address.setProvince("ONT");
        address.setStreet("12 Merival Rd., suite 5");
        address.setCountry("Canada");
        return address;
    }

    public Address addressExample3() {
        Address address = new Address();
        address.setCity("Perth");
        address.setPostalCode("Y3Q 2N9");
        address.setProvince("ONT");
        address.setStreet("234 I'm Lost Lane");
        address.setCountry("Canada");
        return address;
    }

    public Address addressExample4() {
        Address address = new Address();
        address.setCity("Prince Rupert");
        address.setPostalCode("K3K 5DD");
        address.setProvince("BC");
        address.setStreet("3254 Real Cold Place");
        address.setCountry("Canada");
        return address;
    }

    public Address addressExample5() {
        Address address = new Address();
        address.setCity("Vancouver");
        address.setPostalCode("N5J 2N5");
        address.setProvince("BC");
        address.setStreet("1111 Mountain Blvd. Floor 53, suite 6");
        address.setCountry("Canada");
        return address;
    }

    public Address addressExample6() {
        Address address = new Address();
        address.setCity("Montreal");
        address.setPostalCode("Q2S 5Z5");
        address.setProvince("QUE");
        address.setStreet("1 Habs Place");
        address.setCountry("Canada");
        return address;
    }

    public Address addressExample7() {
        Address address = new Address();
        address.setCity("Metcalfe");
        address.setPostalCode("Y4F 7V6");
        address.setProvince("ONT");
        address.setStreet("2 Anderson Rd.");
        address.setCountry("Canada");
        return address;
    }

    public Address addressExample8() {
        Address address = new Address();
        address.setCity("Victoria");
        address.setPostalCode("Z5J 2N5");
        address.setProvince("BC");
        address.setStreet("382 Hyde Park");
        address.setCountry("Canada");
        return address;
    }

    public Address addressExample9() {
        Address address = new Address();
        address.setCity("Smith Falls");
        address.setPostalCode("C6C 6C6");
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
            employee.setSalary((long) 35000);
            employee.setPeriod(employmentPeriodExample1());
            employee.setAddress(addressExample1());
            employee.setDepartment(departmentExample1());
            employee.addResponsibility("Make the coffee.");
            employee.addResponsibility("Clean the kitchen.");
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
            employee.setFemale();
            employee.setPeriod(employmentPeriodExample10());
            employee.setAddress(addressExample10());
            employee.setSalary((long) 56232);
            employee.addPhoneNumber(phoneNumberExample1());
            employee.addPhoneNumber(phoneNumberExample2());
            employee.addResponsibility("Sort files");
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
            employee.setPeriod(employmentPeriodExample11());
            employee.setAddress(addressExample11());
            employee.setSalary((long) 75000);
            employee.addPhoneNumber(phoneNumberExample2());
            employee.addPhoneNumber(phoneNumberExample3());
            employee.addPhoneNumber(phoneNumberExample4());
            employee.addResponsibility("Test applications");
            employee.addResponsibility("Write test cases");
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
            employee.setPeriod(employmentPeriodExample12());
            employee.setAddress(addressExample12());
            employee.setSalary((long) 50000);
            employee.addPhoneNumber(phoneNumberExample3());
            employee.addPhoneNumber(phoneNumberExample4());
            employee.addResponsibility("Bug fixes");
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public Employee basicEmployeeExample13() {
        Employee employee = createEmployee();

        try {
            employee.setFirstName("SquareRoot");
            employee.setLastName("TestCase1");
            employee.setSalary((long) 36);
            employee.setPeriod(employmentPeriodExample1());
            employee.setAddress(addressExample1());
            employee.addPhoneNumber(phoneNumberExample1());
            employee.addResponsibility("Manage development group");
            employee.addResponsibility("Administer performance reviews");
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

     public Employee basicEmployeeExample14() {
        Employee employee = createEmployee();

        try {
            employee.setFirstName("SquareRoot");
            employee.setLastName("TestCase2");
            employee.setSalary((long) 49);
            employee.setPeriod(employmentPeriodExample1());
            employee.setAddress(addressExample1());
            employee.addPhoneNumber(phoneNumberExample1());
            employee.addResponsibility("Attend technology conferences");
            employee.addResponsibility("Review design specifications");
            employee.addResponsibility("Critique coding styles");
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public Employee basicEmployeeExample15() {
        Employee employee = createEmployee();

        try {
            employee.setFirstName("No Phone Number");
            employee.setLastName("Test case");
            employee.setSalary((long) 555);
            employee.setPeriod(employmentPeriodExample1());
            employee.setAddress(addressExample1());
            employee.addResponsibility("Find ways to make the days go by faster");
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
            employee.setSalary((long) 53000);
            startCalendar.set(1970, 0, 1, 8, 0, 0);
            endCalendar.set(1970, 0, 1, 17, 30, 0);
            employee.setPeriod(employmentPeriodExample2());
            employee.setAddress(addressExample2());
            employee.setDepartment(departmentExample2());
            employee.addResponsibility("Fire people for goofing off.");
            employee.addResponsibility("Hire people when more people are required.");
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
            employee.setMale();
            employee.setSalary((long) 43000);
            startCalendar.set(1970, 0, 1, 7, 0, 0);
            endCalendar.set(1970, 0, 1, 15, 30, 0);
            employee.setPeriod(employmentPeriodExample6());
            employee.setAddress(addressExample6());
            employee.setDepartment(departmentExample3());
            employee.addResponsibility("Write lots of Java code.");
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
            employee.setMale();
            employee.setSalary((long) 49631);
            startCalendar.set(1970, 0, 1, 6, 45, 0);
            endCalendar.set(1970, 0, 1, 16, 32, 0);
            employee.setPeriod(employmentPeriodExample5());
            employee.setAddress(addressExample5());
            employee.addResponsibility("Has to fix Database problems.");
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
            employee.setFemale();
            employee.setSalary((long) 87000);
            startCalendar.set(1970, 0, 1, 12, 0, 0);
            endCalendar.set(1970, 0, 1, 20, 0, 30);
            employee.setPeriod(employmentPeriodExample4());
            employee.setAddress(addressExample4());
            employee.addResponsibility("Write code documentation.");
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
            employee.setMale();
            employee.setSalary((long) 54300);
            employee.setPeriod(employmentPeriodExample3());
            employee.setAddress(addressExample3());
            employee.addResponsibility("Write user specifications.");
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
            employee.setFemale();
            employee.setSalary((long) 31000);
            employee.setPeriod(employmentPeriodExample7());
            employee.setAddress(addressExample7());
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
            employee.setMale();
            employee.setSalary((long) 500000);
            employee.setPeriod(employmentPeriodExample8());
            employee.setAddress(addressExample8());
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
            employee.setFemale();
            employee.setSalary((long) 500001);
            startCalendar.set(1970, 0, 1, 22, 0, 0);
            endCalendar.set(1970, 0, 1, 5, 30, 0);
            employee.setPeriod(employmentPeriodExample9());
            employee.setAddress(addressExample9());
            employee.addPhoneNumber(phoneNumberExample1());
            employee.addPhoneNumber(phoneNumberExample6());
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
     * Call all of the example methods in this system to guarantee that all our
     * objects are registered in the population manager
     */
    public void buildExamples() {
        // First ensure that no previous examples are hanging around.
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
        employeeExample13();
        employeeExample14();
        employeeExample15();
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

    public StoredProcedureDefinition buildMySQLResultSetProcedure() {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("XML_Read_Multiple_Result_Sets");

        proc.addStatement("SELECT E.*, S.* FROM JPA22_XML_EMPLOYEE E, JPA22_XML_SALARY S WHERE E.EMP_ID = S.EMP_ID");
        proc.addStatement("SELECT A.* FROM JPA22_XML_ADDRESS A");
        proc.addStatement("SELECT (t1.BUDGET/t0.PROJ_ID) AS BUDGET_SUM, t0.PROJ_ID, t0.PROJ_TYPE, t0.PROJ_NAME, t0.DESCRIP, t0.LEADER_ID, t0.VERSION, t1.BUDGET, t2.PROJ_ID AS SMALL_ID, t2.PROJ_TYPE AS SMALL_DESCRIM, t2.PROJ_NAME AS SMALL_NAME, t2.DESCRIP AS SMALL_DESCRIPTION, t2.LEADER_ID AS SMALL_TEAMLEAD, t2.VERSION AS SMALL_VERSION FROM JPA22_XML_PROJECT t0, JPA22_XML_PROJECT t2, JPA22_XML_LPROJECT t1 WHERE t1.PROJ_ID = t0.PROJ_ID AND t2.PROJ_TYPE='S'");
        proc.addStatement("SELECT t0.EMP_ID, t0.F_NAME, t0.L_NAME, COUNT(t2.DESCRIPTION) AS R_COUNT FROM JPA22_XML_EMPLOYEE t0, JPA22_XML_RESPONS t2, JPA22_XML_SALARY t1 WHERE ((t1.EMP_ID = t0.EMP_ID) AND (t2.EMP_ID = t0.EMP_ID)) GROUP BY t0.EMP_ID, t0.F_NAME, t0.L_NAME");

        return proc;
    }

    public PackageDefinition buildOraclePackage() {
        PackageDefinition types = new PackageDefinition();
        types.setName("Cursor_Type");
        types.addStatement("Type Any_Cursor is REF CURSOR");
        return types;
    }

    public StoredProcedureDefinition buildStoredProcedureReadAllAddresses() {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("XML_Read_All_Addresses");
        proc.addStatement("SELECT ADDRESS_ID from JPA22_XML_ADDRESS");
        return proc;
    }

    public StoredProcedureDefinition buildStoredProcedureReadFromAddress(DatabasePlatform platform) {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("XML_Read_Address");
        proc.addArgument("address_id_v", Integer.class);

        String statement = null;
        if (platform.isSQLServer() || platform.isSybase()) {
            // 260263: SQLServer 2005/2008 requires parameter matching in the select clause for stored procedures
            statement = "SELECT ADDRESS_ID, STREET, CITY, COUNTRY, PROVINCE, P_CODE FROM JPA22_XML_ADDRESS WHERE ADDRESS_ID = @address_id_v";
        } else {
            statement = "SELECT ADDRESS_ID, STREET, CITY, COUNTRY, PROVINCE, P_CODE FROM JPA22_XML_ADDRESS WHERE (ADDRESS_ID = address_id_v)";
        }

        proc.addStatement(statement);
        return proc;
    }

    public StoredProcedureDefinition buildStoredProcedureReadFromAddressMappedNamed(DatabasePlatform platform) {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("XML_Read_Address_Named");

        proc.addArgument("address_id_v", Integer.class);

        String statement = null;
        if (platform.isSQLServer() || platform.isSybase()) {
            // 260263: SQLServer 2005/2008 requires parameter matching in the select clause for stored procedures
            statement = "SELECT ADDRESS_ID AS 'address_id_v', STREET AS 'street_v', CITY AS 'city_v', COUNTRY AS 'country_v', PROVINCE AS 'province_v', P_CODE AS 'p_code_v' FROM JPA22_XML_ADDRESS WHERE ADDRESS_ID = @address_id_v";
        } else {
            statement = "SELECT ADDRESS_ID AS 'address_id_v', STREET AS 'street_v', CITY AS 'city_v', COUNTRY AS 'country_v', PROVINCE AS 'province_v', P_CODE AS 'p_code_v' FROM JPA22_XML_ADDRESS WHERE (ADDRESS_ID = address_id_v)";
        }

        proc.addStatement(statement);
        return proc;
    }

    public StoredProcedureDefinition buildStoredProcedureReadFromAddressMappedNumbered(DatabasePlatform platform) {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("XML_Read_Address_Numbered");

        proc.addArgument("address_id_v", Integer.class);

        String statement = null;
        if (platform.isSQLServer() || platform.isSybase()) {
            // 260263: SQLServer 2005/2008 requires parameter matching in the select clause for stored procedures
            statement = "SELECT ADDRESS_ID AS '1', STREET AS '2', CITY AS '3', COUNTRY AS '4', PROVINCE AS '5', P_CODE AS '6' FROM JPA22_XML_ADDRESS WHERE ADDRESS_ID = @ADDRESS_ID_V";
        } else {
            statement = "SELECT ADDRESS_ID AS '1', STREET AS '2', CITY AS '3', COUNTRY AS '4', PROVINCE AS '5', P_CODE AS '6' FROM JPA22_XML_ADDRESS WHERE (ADDRESS_ID = address_id_v)";
        }

        proc.addStatement(statement);
        return proc;
    }

    public StoredProcedureDefinition buildStoredProcedureReadUsingNamedRefCursor() {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("XML_Read_Using_Named_Cursor");

        proc.addOutputArgument("CUR1", "CURSOR_TYPE.ANY_CURSOR");
        proc.addOutputArgument("CUR2", "CURSOR_TYPE.ANY_CURSOR");

        proc.addStatement("OPEN CUR1 FOR Select E.*, S.* from JPA22_XML_EMPLOYEE E, JPA22_XML_SALARY S WHERE E.EMP_ID = S.EMP_ID");
        proc.addStatement("OPEN CUR2 FOR Select a.* from JPA22_XML_ADDRESS a");

        return proc;
    }

    public StoredProcedureDefinition buildStoredProcedureReadUsingUnNamedRefCursor() {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("XML_Read_Using_UnNamed_Cursor");

        proc.addOutputArgument("RESULT_CURSOR", "CURSOR_TYPE.ANY_CURSOR");

        proc.addStatement("OPEN RESULT_CURSOR FOR Select E.*, S.* from JPA22_XML_EMPLOYEE E, JPA22_XML_SALARY S WHERE E.EMP_ID = S.EMP_ID");

        return proc;
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

    public Department departmentExample1() {
        Department department = new Department();
        department.setName("Department 1");
        return department;
    }

    public Department departmentExample2() {
        Department department = new Department();
        department.setName("Department 2");
        return department;
    }

    public Department departmentExample3() {
        Department department = new Department();
        department.setName("Department 3");
        return department;
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
        } catch (Exception exception) {}

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
        } catch (Exception exception) {}

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

    public Employee employeeExample13() {
        if (containsObject(Employee.class, "0013")) {
            return (Employee)getObject(Employee.class, "0013");
        }

        Employee employee = basicEmployeeExample13();
        registerObject(Employee.class, employee, "0013");

        return employee;
    }

     public Employee employeeExample14() {
        if (containsObject(Employee.class, "0014")) {
            return (Employee)getObject(Employee.class, "0014");
        }

        Employee employee = basicEmployeeExample14();
        registerObject(Employee.class, employee, "0014");

        return employee;
    }

    public Employee employeeExample15() {
        if (containsObject(Employee.class, "0015")) {
            return (Employee)getObject(Employee.class, "0015");
        }

        Employee employee = basicEmployeeExample15();
        registerObject(Employee.class, employee, "0015");

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

    public EmploymentPeriod employmentPeriodExample1() {
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();
        startCalendar.set(1996, 0, 1, 0, 0, 0);
        endCalendar.set(1993, 0, 1, 0, 0, 0);
        employmentPeriod.setEndDate(new java.sql.Date(endCalendar.getTime().getTime()));
        employmentPeriod.setStartDate(new java.sql.Date(startCalendar.getTime().getTime()));
        return employmentPeriod;
    }

    public EmploymentPeriod employmentPeriodExample10() {
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();
        startCalendar.set(1991, 10, 11, 0, 0, 0);
        employmentPeriod.setStartDate(new java.sql.Date(endCalendar.getTime().getTime()));
        return employmentPeriod;
    }

    public EmploymentPeriod employmentPeriodExample11() {
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();
        startCalendar.set(1996, 0, 1, 0, 0, 0);
        endCalendar.set(1993, 0, 1, 0, 0, 0);
        employmentPeriod.setEndDate(new java.sql.Date(endCalendar.getTime().getTime()));
        employmentPeriod.setStartDate(new java.sql.Date(startCalendar.getTime().getTime()));
        return employmentPeriod;
    }

    public EmploymentPeriod employmentPeriodExample12() {
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();
        startCalendar.set(1901, 11, 31, 0, 0, 0);
        endCalendar.set(1995, 0, 12, 0, 0, 0);
        employmentPeriod.setEndDate(new java.sql.Date(endCalendar.getTime().getTime()));
        employmentPeriod.setStartDate(new java.sql.Date(startCalendar.getTime().getTime()));
        return employmentPeriod;
    }

    public EmploymentPeriod employmentPeriodExample13() {
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();
        startCalendar.set(1990, 1, 1, 0, 0, 0);
        endCalendar.set(1995, 5, 30, 0, 0, 0);
        employmentPeriod.setEndDate(new java.sql.Date(endCalendar.getTime().getTime()));
        employmentPeriod.setStartDate(new java.sql.Date(startCalendar.getTime().getTime()));
        return employmentPeriod;
    }

    public EmploymentPeriod employmentPeriodExample2() {
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();
        startCalendar.set(1991, 10, 11, 0, 0, 0);
        employmentPeriod.setStartDate(new java.sql.Date(startCalendar.getTime().getTime()));
        return employmentPeriod;
    }

    public EmploymentPeriod employmentPeriodExample3() {
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();
        startCalendar.set(1901, 11, 31, 0, 0, 0);
        endCalendar.set(1995, 0, 12, 0, 0, 0);
        employmentPeriod.setEndDate(new java.sql.Date(endCalendar.getTime().getTime()));
        employmentPeriod.setStartDate(new java.sql.Date(startCalendar.getTime().getTime()));
        return employmentPeriod;
    }

    public EmploymentPeriod employmentPeriodExample4() {
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();
        startCalendar.set(2001, 6, 31, 0, 0, 0);
        endCalendar.set(1995, 4, 1, 0, 0, 0);
        employmentPeriod.setEndDate(new java.sql.Date(endCalendar.getTime().getTime()));
        employmentPeriod.setStartDate(new java.sql.Date(startCalendar.getTime().getTime()));
        return employmentPeriod;
    }

    public EmploymentPeriod employmentPeriodExample5() {
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();
        startCalendar.set(1901, 11, 31, 0, 0, 0);
        endCalendar.set(1895, 0, 1, 0, 0, 0);
        employmentPeriod.setEndDate(new java.sql.Date(endCalendar.getTime().getTime()));
        employmentPeriod.setStartDate(new java.sql.Date(startCalendar.getTime().getTime()));
        return employmentPeriod;
    }

    public EmploymentPeriod employmentPeriodExample6() {
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();
        startCalendar.set(1901, 11, 31, 0, 0, 0);
        endCalendar.set(1995, 0, 12, 0, 0, 0);
        employmentPeriod.setEndDate(new java.sql.Date(endCalendar.getTime().getTime()));
        employmentPeriod.setStartDate(new java.sql.Date(startCalendar.getTime().getTime()));
        return employmentPeriod;
    }

    public EmploymentPeriod employmentPeriodExample7() {
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();
        startCalendar.set(1996, 0, 1, 0, 0, 0);
        endCalendar.set(1993, 0, 1, 0, 0, 0);
        employmentPeriod.setEndDate(new java.sql.Date(endCalendar.getTime().getTime()));
        employmentPeriod.setStartDate(new java.sql.Date(startCalendar.getTime().getTime()));
        return employmentPeriod;
    }

    public EmploymentPeriod employmentPeriodExample8() {
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();
        startCalendar.set(1901, 11, 31, 0, 0, 0);
        endCalendar.set(1895, 0, 1, 0, 0, 0);
        employmentPeriod.setEndDate(new java.sql.Date(endCalendar.getTime().getTime()));
        employmentPeriod.setStartDate(new java.sql.Date(startCalendar.getTime().getTime()));
        return employmentPeriod;
    }

    public EmploymentPeriod employmentPeriodExample9() {
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();
        startCalendar.set(1901, 11, 31, 0, 0, 0);
        endCalendar.set(1895, 0, 1, 0, 0, 0);
        employmentPeriod.setEndDate(new java.sql.Date(endCalendar.getTime().getTime()));
        employmentPeriod.setStartDate(new java.sql.Date(startCalendar.getTime().getTime()));
        return employmentPeriod;
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
            largeProject.setExecutive(employeeExample15());
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
        largeProject.setExecutive(employeeExample15());
        registerObject(largeProject, "0002");
        return largeProject;
    }

    public LargeProject largeProjectExample3() {
        if (containsObject(LargeProject.class, "0003")) {
            return (LargeProject)getObject(LargeProject.class, "0003");
        }

        LargeProject largeProject = basicLargeProjectExample3();
        largeProject.setExecutive(employeeExample15());
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
            largeProject.setExecutive(employeeExample15());
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
            largeProject.setExecutive(employeeExample15());
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return largeProject;
    }

    public void persistExample(Session session) {
        Vector allObjects = new Vector();
        UnitOfWork unitOfWork = session.acquireUnitOfWork();

        PopulationManager.getDefaultManager().addAllObjectsForClass(Employee.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(SmallProject.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(LargeProject.class, allObjects);
        unitOfWork.registerAllObjects(allObjects);
        unitOfWork.commit();

        DatabasePlatform platform = session.getLogin().getPlatform();

        if (TestCase.supportsStoredProcedures(session)) {
            boolean orig_FAST_TABLE_CREATOR = SchemaManager.FAST_TABLE_CREATOR;
            // on Symfoware, to avoid table locking issues only the first invocation
            // of an instance of this class (drops & re-)creates the tables.
            if (useFastTableCreatorAfterInitialCreate && !isFirstCreation) {
                SchemaManager.FAST_TABLE_CREATOR = true;
            }

            try {
                SchemaManager schema = new SchemaManager((DatabaseSession) session);
                schema.replaceObject(buildStoredProcedureReadFromAddress(platform));
                schema.replaceObject(buildStoredProcedureReadFromAddressMappedNamed(platform));
                schema.replaceObject(buildStoredProcedureReadFromAddressMappedNumbered(platform));
                schema.replaceObject(buildStoredProcedureReadAllAddresses());

                if (platform.isOracle()) {
                    schema.replaceObject(buildOraclePackage());
                    schema.replaceObject(buildStoredProcedureReadUsingNamedRefCursor());
                    schema.replaceObject(buildStoredProcedureReadUsingUnNamedRefCursor());
                }

                if (platform.isMySQL()) {
                    schema.replaceObject(buildMySQLResultSetProcedure());
                }
            } finally {
                if (useFastTableCreatorAfterInitialCreate && !isFirstCreation) {
                    SchemaManager.FAST_TABLE_CREATOR = orig_FAST_TABLE_CREATOR;
                }
            }
            // next time it deletes the rows instead.
            isFirstCreation = false;
        }

        // Force uppercase for Postgres.
        if (platform.isPostgreSQL()) {
            session.getLogin().setShouldForceFieldNamesToUpperCase(true);
        }
    }

    public PhoneNumber phoneNumberExample1() {
        return new PhoneNumber("Work", "613", "2258812");
    }

    public PhoneNumber phoneNumberExample2() {
        return new PhoneNumber("Work Fax", "613", "2255943");
    }

    public PhoneNumber phoneNumberExample3() {
        return new PhoneNumber("Home", "613", "5551234");
    }

    public PhoneNumber phoneNumberExample4() {
        return new PhoneNumber("Cellular", "416", "5551111");
    }

    public PhoneNumber phoneNumberExample5() {
        return new PhoneNumber("Pager", "976", "5556666");
    }

    public PhoneNumber phoneNumberExample6() {
        return new PhoneNumber("ISDN", "905", "5553691");
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
}
