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
package org.eclipse.persistence.testing.tests.clientserver;

import java.util.*;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;

/**
 * <p><b>Purpose</b>: To build and populate the database for example and testing purposes.
 * This population routine is fairly complex and makes use of the population manager to
 * resolve interrated objects as the employee objects are an interconnection graph of objects.
 *
 * This is not the recomended way to create new objects in your application,
 * this is just the easiest way to create interconnected new example objects from code.
 * Normally in your application the objects will be defined as part of a transactional and user interactive process.
 */
public class DeadLockEmployeePopulator {
    protected PopulationManager populationManager;
    protected Calendar startCalendar = Calendar.getInstance();
    protected Calendar endCalendar = Calendar.getInstance();

    public DeadLockEmployeePopulator() {
        this.populationManager = PopulationManager.getDefaultManager();
        this.startCalendar = Calendar.getInstance();
        this.startCalendar.set(Calendar.MILLISECOND, 0);
        this.endCalendar = Calendar.getInstance();
        this.endCalendar.set(Calendar.MILLISECOND, 0);

    }

    public DeadLockAddress addressExample1() {
        DeadLockAddress address = new DeadLockAddress();

        address.setCity("Toronto");
        address.setPostalCode("L5J2B5");
        address.setProvince("ONT");
        address.setStreet("1450 Acme Cr., suite 4");
        address.setCountry("Canada");
        return address;
    }

    public DeadLockAddress addressExample2() {
        DeadLockAddress address = new DeadLockAddress();

        address.setCity("Calgary");
        address.setPostalCode("J5J2B5");
        address.setProvince("ALB");
        address.setStreet("1111 Moose Rd.");
        address.setCountry("Canada");
        return address;
    }

    public DeadLockAddress addressExample3() {
        DeadLockAddress address = new DeadLockAddress();

        address.setCity("Arnprior");
        address.setPostalCode("W1A2B5");
        address.setProvince("ONT");
        address.setStreet("1 Nowhere Drive");
        address.setCountry("Canada");
        return address;
    }

    public DeadLockEmployee basicEmployeeExample1() {
        DeadLockEmployee employee = createEmployee();

        try {
            employee.setFirstName("Bob");
            employee.setLastName("Smith");
            employee.setMale();
            employee.setAddress(addressExample1());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return employee;
    }

    public DeadLockEmployee basicEmployeeExample2() {
        DeadLockEmployee employee = createEmployee();

        try {
            employee.setFirstName("Jill");
            employee.setLastName("May");
            employee.setFemale();
            employee.setAddress(addressExample2());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return employee;
    }

    public DeadLockEmployee basicEmployeeExample3() {
        DeadLockEmployee employee = createEmployee();

        try {
            employee.setFirstName("Sarah-loo");
            employee.setLastName("Smitty");
            employee.setFemale();
            employee.setAddress(addressExample3());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return employee;
    }

    /**
    *    Call all of the example methods in this system to guarantee that all our objects
    *    are registered in the population manager
    */
    public void buildExamples() {
        // First ensure that no preivous examples are hanging around.
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(DeadLockEmployee.class);

        employeeExample1();
        employeeExample2();
        employeeExample3();
    }

    protected boolean containsObject(Class domainClass, String identifier) {
        return populationManager.containsObject(domainClass, identifier);
    }

    public DeadLockEmployee createEmployee() {
        return new DeadLockEmployee();
    }

    public DeadLockEmployee employeeExample1() {
        if (containsObject(DeadLockEmployee.class, "0001")) {
            return (DeadLockEmployee)getObject(DeadLockEmployee.class, "0001");
        }

        DeadLockEmployee employee = basicEmployeeExample1();
        registerObject(DeadLockEmployee.class, employee, "0001");

        return employee;
    }

    public DeadLockEmployee employeeExample2() {
        if (containsObject(DeadLockEmployee.class, "0002")) {
            return (DeadLockEmployee)getObject(DeadLockEmployee.class, "0002");
        }

        DeadLockEmployee employee = basicEmployeeExample2();
        registerObject(DeadLockEmployee.class, employee, "0002");

        return employee;
    }

    public DeadLockEmployee employeeExample3() {
        if (containsObject(DeadLockEmployee.class, "0003")) {
            return (DeadLockEmployee)getObject(DeadLockEmployee.class, "0003");
        }

        DeadLockEmployee employee = basicEmployeeExample3();
        registerObject(DeadLockEmployee.class, employee, "0003");

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

    protected void registerObject(Class domainClass, Object domainObject, String identifier) {
        populationManager.registerObject(domainClass, domainObject, identifier);
    }

    protected void registerObject(Object domainObject, String identifier) {
        populationManager.registerObject(domainObject, identifier);
    }
}
