/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//      gonural - Initial implementation
package org.eclipse.persistence.jpars.test.service;

import org.eclipse.persistence.jpars.test.BaseJparsTest;
import org.eclipse.persistence.jpars.test.model.employee.Employee;
import org.eclipse.persistence.jpars.test.model.employee.EmployeeAddress;
import org.eclipse.persistence.jpars.test.model.employee.EmploymentPeriod;
import org.eclipse.persistence.jpars.test.model.employee.Expertise;
import org.eclipse.persistence.jpars.test.model.employee.Gender;
import org.eclipse.persistence.jpars.test.model.employee.PhoneNumber;
import org.eclipse.persistence.jpars.test.server.RestCallFailedException;
import org.eclipse.persistence.jpars.test.util.RestUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import jakarta.persistence.EntityManager;
import jakarta.ws.rs.core.MediaType;
import jakarta.xml.bind.JAXBException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * JPARS tests on employee model.
 *
 * @author gonural
 */
public class EmployeeTestBase extends BaseJparsTest {

    @BeforeClass
    public static void setup() throws Exception {
        initContext("jpars_employee-static", null);
    }

    @Test
    public void testMarshalUnMarshalEmployeeJSON() throws RestCallFailedException, UnsupportedEncodingException, JAXBException {
        EntityManager em = context.getEmf().createEntityManager();
        em.getTransaction().begin();

        Employee employee = new Employee();
        employee.setId(768);
        employee.setFirstName("John");
        employee.setLastName("Smith");
        employee.setGender(Gender.Male);
        employee.setSalary(35000);

        // Embeddable
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();
        employmentPeriod.setStartDate(Calendar.getInstance());
        employee.setPeriod(employmentPeriod);

        // PrivateOwned
        EmployeeAddress address = new EmployeeAddress();
        address.setCity("NYC");
        address.setCountry("US");
        address.setProvince("NY");
        employee.setAddress(address);

        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(new PhoneNumber("Home", "613", "1234567"));
        phoneNumbers.add(new PhoneNumber("Work", "613", "9876543"));
        employee.setPhoneNumbers(phoneNumbers);

        for (PhoneNumber ph:phoneNumbers){
            ph.setEmployee(employee);
        }

        em.persist(employee);

        Expertise expertise = new Expertise();
        expertise.setSubject("REST");
        em.persist(expertise);

        Employee manager = new Employee();
        manager.setId(121);
        manager.setFirstName("Bill");
        manager.setLastName("Anderson");
        manager.setGender(Gender.Male);
        List<Employee> managedEmployees = new ArrayList<>();
        managedEmployees.add(employee);
        manager.setManagedEmployees(managedEmployees);
        employee.setManager(manager);

        expertise.setEmployee(manager);
        manager.getExpertiseAreas().add(expertise);
        em.persist(manager);

        em.getTransaction().commit();

        String mgrMsg = RestUtils.marshal(context, manager, MediaType.APPLICATION_JSON_TYPE);
        Employee mgr = RestUtils.unmarshal(context, mgrMsg, Employee.class, MediaType.APPLICATION_JSON_TYPE);

        List<Employee> employees = mgr.getManagedEmployees();
        assertTrue("Incorrectly unmarshalled managed employees.", employees.size() == 1);

        if ((employees!= null) && (!employees.isEmpty())) {
            for (Employee emp : employees) {
                String firstName = emp.getFirstName();
                assertTrue("Unmarshalled employee first name is incorrect.", "John".equals(firstName));
            }
        }

        assertTrue("Incorrectly marshallet Set of Expertise Areas", mgr.getExpertiseAreas().size() == 1);

        em.getTransaction().begin();
        em.remove(employee);
        em.remove(manager);
        em.getTransaction().commit();
    }

    @Test
    public void testMarshalUnMarshalEmployeeXML() throws RestCallFailedException, UnsupportedEncodingException, JAXBException {
        EntityManager em = context.getEmf().createEntityManager();
        em.getTransaction().begin();

        Employee employee = new Employee();
        employee.setId(768);
        employee.setFirstName("John");
        employee.setLastName("Smith");
        employee.setGender(Gender.Male);
        employee.setSalary(35000);

        // Embeddable
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();
        employmentPeriod.setStartDate(Calendar.getInstance());
        employee.setPeriod(employmentPeriod);

        // PrivateOwned
        EmployeeAddress address = new EmployeeAddress();
        address.setCity("NYC");
        address.setCountry("US");
        address.setProvince("NY");
        employee.setAddress(address);

        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(new PhoneNumber("Home", "613", "1234567"));
        phoneNumbers.add(new PhoneNumber("Work", "613", "9876543"));
        employee.setPhoneNumbers(phoneNumbers);

        for (PhoneNumber ph : phoneNumbers) {
            ph.setEmployee(employee);
        }

        em.persist(employee);

        Expertise expertise = new Expertise();
        expertise.setSubject("REST");
        em.persist(expertise);

        Employee manager = new Employee();
        manager.setId(121);
        manager.setFirstName("Bill");
        manager.setLastName("Anderson");
        manager.setGender(Gender.Male);
        List<Employee> managedEmployees = new ArrayList<>();
        managedEmployees.add(employee);
        manager.setManagedEmployees(managedEmployees);
        employee.setManager(manager);

        expertise.setEmployee(manager);
        manager.getExpertiseAreas().add(expertise);
        em.persist(manager);

        em.getTransaction().commit();

        String mgrMsg = RestUtils.marshal(context, manager, MediaType.APPLICATION_XML_TYPE);
        Employee mgr = RestUtils.unmarshal(context, mgrMsg, Employee.class, MediaType.APPLICATION_XML_TYPE);

        List<Employee> employees = mgr.getManagedEmployees();
        assertTrue("Incorrectly unmarshalled managed employees.", employees.size() == 1);

        if ((employees != null) && (!employees.isEmpty())) {
            for (Employee emp : employees) {
                String firstName = emp.getFirstName();
                assertTrue("Unmarshalled employee first name is incorrect.", "John".equals(firstName));
            }
        }
        assertTrue("Incorrectly marshalled Set of Expertise Areas", mgr.getExpertiseAreas().size() == 1);

        em.getTransaction().begin();
        em.remove(employee);
        em.remove(manager);
        em.getTransaction().commit();
    }
}
