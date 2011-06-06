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
package org.eclipse.persistence.testing.models.inheritance;


import java.util.*;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;

/**
 * <p><b>Purpose</b>: To build and populate the database for example and testing purposes.
 * This population routine is fairly complex and makes use of the population manager to
 * resolve interrated objects as the employee objects are an interconnection graph of objects.
 *
 * This is not the recomended way to create new objects in your application,
 * this is just the easiest way to create interconnected new example objects from code.
 * Normally in your application the objects will be defined as part of a transactional and user interactive process.
 */
public class STI_EmployeePopulator {
    protected PopulationManager populationManager;

    public STI_EmployeePopulator() {
        this.populationManager = PopulationManager.getDefaultManager();
    }

    public STI_Employee basicEmployeeExample1() {
        STI_Employee employee = createEmployee();

        try {
            employee.setFirstName("Bob");
            employee.setLastName("Smith");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public STI_Employee basicEmployeeExample10() {
        STI_Employee employee = createEmployee();

        try {
            employee.setFirstName("Jill");
            employee.setLastName("May");
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public STI_Employee basicEmployeeExample11() {
        STI_Employee employee = createEmployee();

        try {
            employee.setFirstName("Sarah-loo");
            employee.setLastName("Smitty");
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public STI_Employee basicEmployeeExample12() {
        STI_Employee employee = createEmployee();

        try {
            employee.setFirstName("Jim-bob");
            employee.setLastName("Jefferson");
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public STI_Employee basicEmployeeExample2() {
        STI_Employee employee = createEmployee();

        try {
            employee.setFirstName("John");
            employee.setLastName("Way");
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public STI_Employee basicEmployeeExample3() {
        STI_Employee employee = createEmployee();

        try {
            employee.setFirstName("Charles");
            employee.setLastName("Chanley");
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public STI_Employee basicEmployeeExample4() {
        STI_Employee employee = createEmployee();

        try {
            employee.setFirstName("Emanual");
            employee.setLastName("Smith");
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public STI_Employee basicEmployeeExample5() {
        STI_Employee employee = createEmployee();

        try {
            employee.setFirstName("Sarah");
            employee.setLastName("Way");
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public STI_Employee basicEmployeeExample6() {
        STI_Employee employee = createEmployee();

        try {
            employee.setFirstName("Marcus");
            employee.setLastName("Saunders");
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public STI_Employee basicEmployeeExample7() {
        STI_Employee employee = createEmployee();

        try {
            employee.setFirstName("Nancy");
            employee.setLastName("White");
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public STI_Employee basicEmployeeExample8() {
        STI_Employee employee = createEmployee();

        try {
            employee.setFirstName("Fred");
            employee.setLastName("Jones");
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public STI_Employee basicEmployeeExample9() {
        STI_Employee employee = createEmployee();

        try {
            employee.setFirstName("Betty");
            employee.setLastName("Jones");
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public STI_LargeProject basicLargeProjectExample1() {
        STI_LargeProject largeProject = createLargeProject();

        try {
            largeProject.setName("Sales Reporting");
            largeProject.setDescription("A reporting application to report on the corporations database through TopLink.");
            largeProject.setBudget(5000);
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return largeProject;
    }

    public STI_LargeProject basicLargeProjectExample2() {
        STI_LargeProject largeProject = createLargeProject();

        try {
            largeProject.setName("Swirly Dirly");
            largeProject.setDescription("A swirly application to report on the corporations database through TopLink.");
            largeProject.setBudget(100.98);
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return largeProject;
    }

    public STI_LargeProject basicLargeProjectExample3() {
        STI_LargeProject largeProject = createLargeProject();

        try {
            largeProject.setName("TOPEmployee Management");
            largeProject.setDescription("A management application to report on the corporations database through TopLink.");
            largeProject.setBudget(4000.98);
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return largeProject;
    }

    public STI_LargeProject basicLargeProjectExample4() {
        STI_LargeProject largeProject = createLargeProject();

        try {
            largeProject.setName("Enterprise System");
            largeProject.setDescription("A enterprise wide application to report on the corporations database through TopLink.");
            largeProject.setBudget(40.98);
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return largeProject;
    }

    public STI_LargeProject basicLargeProjectExample5() {
        STI_LargeProject largeProject = createLargeProject();

        try {
            largeProject.setName("Problem Reporting System");
            largeProject.setDescription("A PRS application to report on the corporations database through TopLink.");
            largeProject.setBudget(101.98);
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return largeProject;
    }

    public STI_SmallProject basicSmallProjectExample1() {
        STI_SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("Enterprise");
            smallProject.setDescription("A enterprise wide application to report on the corporations database through TopLink.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return smallProject;
    }

    public STI_SmallProject basicSmallProjectExample10() {
        STI_SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("Staff Query Tool");
            smallProject.setDescription("A tool to help staff query things.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return smallProject;
    }

    public STI_SmallProject basicSmallProjectExample2() {
        STI_SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("Sales Reporter");
            smallProject.setDescription("A reporting application using JDK to report on the corporations database through TopLink.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return smallProject;
    }

    public STI_SmallProject basicSmallProjectExample3() {
        STI_SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("TOPEmployee Manager");
            smallProject.setDescription("A management application to report on the corporations database through TopLink.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return smallProject;
    }

    public STI_SmallProject basicSmallProjectExample4() {
        STI_SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("Problem Reporter");
            smallProject.setDescription("A PRS application to report on the corporations database through TopLink.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return smallProject;
    }

    public STI_SmallProject basicSmallProjectExample5() {
        STI_SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("Swirly Dirl");
            smallProject.setDescription("A swirlly application to report on the corporations database through TopLink.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return smallProject;
    }

    public STI_SmallProject basicSmallProjectExample6() {
        STI_SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("Bleep Blob");
            smallProject.setDescription("Bleep blob is just a nice toy.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return smallProject;
    }

    public STI_SmallProject basicSmallProjectExample7() {
        STI_SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("Marketing Query Tool");
            smallProject.setDescription("A tool to help marketing query things.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return smallProject;
    }

    public STI_SmallProject basicSmallProjectExample8() {
        STI_SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("Shipping Query Tool");
            smallProject.setDescription("A tool to help shipping query things.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return smallProject;
    }

    public STI_SmallProject basicSmallProjectExample9() {
        STI_SmallProject smallProject = createSmallProject();

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
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(STI_Employee.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(STI_SmallProject.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(STI_LargeProject.class);

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

    public STI_Employee createEmployee() {
        return new STI_Employee();
    }

    public STI_LargeProject createLargeProject() {
        return new STI_LargeProject();
    }

    public STI_SmallProject createSmallProject() {
        return new STI_SmallProject();
    }

    public STI_Employee employeeExample1() {
        if (containsObject(STI_Employee.class, "0001")) {
            return (STI_Employee)getObject(STI_Employee.class, "0001");
        }

        STI_Employee employee = basicEmployeeExample1();
        registerObject(STI_Employee.class, employee, "0001");

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

    public STI_Employee employeeExample10() {
        if (containsObject(STI_Employee.class, "0010")) {
            return (STI_Employee)getObject(STI_Employee.class, "0010");
        }

        STI_Employee employee = basicEmployeeExample10();
        try {
            employee.addManagedEmployee(employeeExample12());
        } catch (Exception exception) {
        }
        registerObject(STI_Employee.class, employee, "0010");

        return employee;
    }

    public STI_Employee employeeExample11() {
        if (containsObject(STI_Employee.class, "0011")) {
            return (STI_Employee)getObject(STI_Employee.class, "0011");
        }

        STI_Employee employee = basicEmployeeExample11();
        try {
            employee.addManagedEmployee(employeeExample7());
        } catch (Exception exception) {
        }
        registerObject(STI_Employee.class, employee, "0011");

        return employee;
    }

    public STI_Employee employeeExample12() {
        if (containsObject(STI_Employee.class, "0012")) {
            return (STI_Employee)getObject(STI_Employee.class, "0012");
        }

        STI_Employee employee = basicEmployeeExample12();
        registerObject(STI_Employee.class, employee, "0012");

        try {
            employee.addManagedEmployee(employeeExample2());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public STI_Employee employeeExample2() {
        if (containsObject(STI_Employee.class, "0002")) {
            return (STI_Employee)getObject(STI_Employee.class, "0002");
        }

        STI_Employee employee = basicEmployeeExample2();
        registerObject(STI_Employee.class, employee, "0002");

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

    public STI_Employee employeeExample3() {
        if (containsObject(STI_Employee.class, "0003")) {
            return (STI_Employee)getObject(STI_Employee.class, "0003");
        }

        STI_Employee employee = basicEmployeeExample3();
        registerObject(STI_Employee.class, employee, "0003");

        try {
            employee.addProject(smallProjectExample4());
            employee.addProject(largeProjectExample4());
            employee.addProject(largeProjectExample5());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public STI_Employee employeeExample4() {
        if (containsObject(STI_Employee.class, "0004")) {
            return (STI_Employee)getObject(STI_Employee.class, "0004");
        }

        STI_Employee employee = basicEmployeeExample4();
        registerObject(STI_Employee.class, employee, "0004");

        return employee;
    }

    public STI_Employee employeeExample5() {
        if (containsObject(STI_Employee.class, "0005")) {
            return (STI_Employee)getObject(STI_Employee.class, "0005");
        }

        STI_Employee employee = basicEmployeeExample5();
        registerObject(STI_Employee.class, employee, "0005");

        try {
            employee.addProject(smallProjectExample4());
            employee.addProject(largeProjectExample1());
            employee.addProject(largeProjectExample3());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public STI_Employee employeeExample6() {
        if (containsObject(STI_Employee.class, "0006")) {
            return (STI_Employee)getObject(STI_Employee.class, "0006");
        }

        STI_Employee employee = basicEmployeeExample6();
        registerObject(STI_Employee.class, employee, "0006");

        try {
            employee.addProject(largeProjectExample2());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public STI_Employee employeeExample7() {
        if (containsObject(STI_Employee.class, "0007")) {
            return (STI_Employee)getObject(STI_Employee.class, "0007");
        }

        STI_Employee employee = basicEmployeeExample7();
        registerObject(STI_Employee.class, employee, "0007");

        try {
            employee.addProject(largeProjectExample2());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return employee;
    }

    public STI_Employee employeeExample8() {
        if (containsObject(STI_Employee.class, "0008")) {
            return (STI_Employee)getObject(STI_Employee.class, "0008");
        }

        STI_Employee employee = basicEmployeeExample8();
        registerObject(STI_Employee.class, employee, "0008");

        return employee;
    }

    public STI_Employee employeeExample9() {
        if (containsObject(STI_Employee.class, "0009")) {
            return (STI_Employee)getObject(STI_Employee.class, "0009");
        }

        STI_Employee employee = basicEmployeeExample9();
        registerObject(STI_Employee.class, employee, "0009");

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

    public STI_LargeProject largeProjectExample1() {
        if (containsObject(STI_LargeProject.class, "0001")) {
            return (STI_LargeProject)getObject(STI_LargeProject.class, "0001");
        }

        STI_LargeProject largeProject = basicLargeProjectExample1();
        registerObject(largeProject, "0001");

        try {
            largeProject.setTeamLeader(employeeExample2());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return largeProject;
    }

    public STI_LargeProject largeProjectExample2() {
        if (containsObject(STI_LargeProject.class, "0002")) {
            return (STI_LargeProject)getObject(STI_LargeProject.class, "0002");
        }

        STI_LargeProject largeProject = basicLargeProjectExample2();
        registerObject(largeProject, "0002");
        return largeProject;
    }

    public STI_LargeProject largeProjectExample3() {
        if (containsObject(STI_LargeProject.class, "0003")) {
            return (STI_LargeProject)getObject(STI_LargeProject.class, "0003");
        }

        STI_LargeProject largeProject = basicLargeProjectExample3();
        registerObject(largeProject, "0003");
        return largeProject;
    }

    public STI_LargeProject largeProjectExample4() {
        if (containsObject(STI_LargeProject.class, "0004")) {
            return (STI_LargeProject)getObject(STI_LargeProject.class, "0004");
        }

        STI_LargeProject largeProject = basicLargeProjectExample4();
        registerObject(largeProject, "0004");

        try {
            largeProject.setTeamLeader(employeeExample3());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }

        return largeProject;
    }

    public STI_LargeProject largeProjectExample5() {
        if (containsObject(STI_LargeProject.class, "0005")) {
            return (STI_LargeProject)getObject(STI_LargeProject.class, "0005");
        }

        STI_LargeProject largeProject = basicLargeProjectExample5();
        registerObject(largeProject, "0005");

        try {
            largeProject.setTeamLeader(employeeExample5());

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

    public STI_SmallProject smallProjectExample1() {
        if (containsObject(STI_SmallProject.class, "0001")) {
            return (STI_SmallProject)getObject(STI_SmallProject.class, "0001");
        }

        STI_SmallProject smallProject = basicSmallProjectExample1();
        registerObject(smallProject, "0001");
        return smallProject;
    }

    public STI_SmallProject smallProjectExample10() {
        if (containsObject(STI_SmallProject.class, "0010")) {
            return (STI_SmallProject)getObject(STI_SmallProject.class, "0010");
        }

        STI_SmallProject smallProject = basicSmallProjectExample10();
        registerObject(smallProject, "0010");

        return smallProject;
    }

    public STI_SmallProject smallProjectExample2() {
        if (containsObject(STI_SmallProject.class, "0002")) {
            return (STI_SmallProject)getObject(STI_SmallProject.class, "0002");
        }

        STI_SmallProject smallProject = basicSmallProjectExample2();
        registerObject(smallProject, "0002");
        return smallProject;
    }

    public STI_SmallProject smallProjectExample3() {
        if (containsObject(STI_SmallProject.class, "0003")) {
            return (STI_SmallProject)getObject(STI_SmallProject.class, "0003");
        }

        STI_SmallProject smallProject = basicSmallProjectExample3();
        registerObject(smallProject, "0003");
        return smallProject;
    }

    public STI_SmallProject smallProjectExample4() {
        if (containsObject(STI_SmallProject.class, "0004")) {
            return (STI_SmallProject)getObject(STI_SmallProject.class, "0004");
        }

        STI_SmallProject smallProject = basicSmallProjectExample4();
        registerObject(smallProject, "0004");
        return smallProject;
    }

    public STI_SmallProject smallProjectExample5() {
        if (containsObject(STI_SmallProject.class, "0005")) {
            return (STI_SmallProject)getObject(STI_SmallProject.class, "0005");
        }

        STI_SmallProject smallProject = basicSmallProjectExample5();
        registerObject(smallProject, "0005");
        return smallProject;
    }

    public STI_SmallProject smallProjectExample6() {
        if (containsObject(STI_SmallProject.class, "0006")) {
            return (STI_SmallProject)getObject(STI_SmallProject.class, "0006");
        }

        STI_SmallProject smallProject = basicSmallProjectExample6();
        registerObject(smallProject, "0006");
        return smallProject;
    }

    public STI_SmallProject smallProjectExample7() {
        if (containsObject(STI_SmallProject.class, "0007")) {
            return (STI_SmallProject)getObject(STI_SmallProject.class, "0007");
        }

        STI_SmallProject smallProject = basicSmallProjectExample7();
        registerObject(smallProject, "0007");
        return smallProject;
    }

    public STI_SmallProject smallProjectExample8() {
        if (containsObject(STI_SmallProject.class, "0008")) {
            return (STI_SmallProject)getObject(STI_SmallProject.class, "0008");
        }

        STI_SmallProject smallProject = basicSmallProjectExample8();
        registerObject(smallProject, "0008");
        return smallProject;
    }

    public STI_SmallProject smallProjectExample9() {
        if (containsObject(STI_SmallProject.class, "0009")) {
            return (STI_SmallProject)getObject(STI_SmallProject.class, "0009");
        }

        STI_SmallProject smallProject = basicSmallProjectExample9();
        registerObject(smallProject, "0009");
        return smallProject;
    }

    public void persistExample(Session session) {        
        Vector allObjects = new Vector();        
        UnitOfWork unitOfWork = session.acquireUnitOfWork();        
        PopulationManager.getDefaultManager().addAllObjectsForClass(STI_Employee.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(STI_SmallProject.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(STI_LargeProject.class, allObjects);
        unitOfWork.registerAllObjects(allObjects);
        unitOfWork.commit();
        
    }
}
