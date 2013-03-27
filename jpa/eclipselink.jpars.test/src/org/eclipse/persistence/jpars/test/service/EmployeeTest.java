/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 ******************************************************************************/
package org.eclipse.persistence.jpars.test.service;

import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.PersistenceFactoryBase;
import org.eclipse.persistence.jpars.test.model.employee.Employee;
import org.eclipse.persistence.jpars.test.model.employee.EmployeeAddress;
import org.eclipse.persistence.jpars.test.model.employee.EmploymentPeriod;
import org.eclipse.persistence.jpars.test.model.employee.Gender;
import org.eclipse.persistence.jpars.test.model.employee.PhoneNumber;
import org.eclipse.persistence.jpars.test.server.RestCallFailedException;
import org.eclipse.persistence.jpars.test.util.DBUtils;
import org.eclipse.persistence.jpars.test.util.ExamplePropertiesLoader;
import org.eclipse.persistence.jpars.test.util.RestUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class EmployeeTest {
    private static final String DEFAULT_PU = "jpars_employee-static";
    private static PersistenceContext context = null;
    private static PersistenceFactoryBase factory = null;

    @BeforeClass
    public static void setup() throws URISyntaxException {
        Map<String, Object> properties = new HashMap<String, Object>();
        ExamplePropertiesLoader.loadProperties(properties); 
        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, null);
        properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
        properties.put(PersistenceUnitProperties.CLASSLOADER, new DynamicClassLoader(Thread.currentThread().getContextClassLoader()));
        factory = new PersistenceFactoryBase();
        context = factory.bootstrapPersistenceContext(DEFAULT_PU, Persistence.createEntityManagerFactory(DEFAULT_PU, properties),
                RestUtils.getServerURI(), null, true);
    }

    @AfterClass
    public static void tearDown() {
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

        List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
        phoneNumbers.add(new PhoneNumber("Home", "613", "1234567"));
        phoneNumbers.add(new PhoneNumber("Work", "613", "9876543"));
        employee.setPhoneNumbers(phoneNumbers);

        for (PhoneNumber ph:phoneNumbers){
            ph.setEmployee(employee);
        }

        em.persist(employee);
        em.getTransaction().commit();

        Employee manager = new Employee();
        manager.setId(121);
        manager.setFirstName("Bill");
        manager.setLastName("Anderson");
        manager.setGender(Gender.Male);
        List<Employee> managedEmployees = new ArrayList<Employee>();
        managedEmployees.add(employee);
        manager.setManagedEmployees(managedEmployees);
        employee.setManager(manager);

        String mgrMsg = RestUtils.marshal(context, manager, MediaType.APPLICATION_JSON_TYPE);
        Employee mgr = RestUtils.unmarshal(context, mgrMsg, Employee.class.getSimpleName(), MediaType.APPLICATION_JSON_TYPE);

        List<Employee> employees = mgr.getManagedEmployees();
        assertTrue("Incorrectly unmarshalled managed employees.", employees.size() == 1);

        if ((employees!= null) && (!employees.isEmpty())) {
            for (Employee emp : employees) {
                String firstName = emp.getFirstName();
                assertTrue("Unmarshalled employee first name is incorrect.", "John".equals(firstName));
            }
        }

        DBUtils.dbDelete(employee, em);
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

        List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
        phoneNumbers.add(new PhoneNumber("Home", "613", "1234567"));
        phoneNumbers.add(new PhoneNumber("Work", "613", "9876543"));
        employee.setPhoneNumbers(phoneNumbers);

        for (PhoneNumber ph : phoneNumbers) {
            ph.setEmployee(employee);
        }

        em.persist(employee);
        em.getTransaction().commit();

        Employee manager = new Employee();
        manager.setId(121);
        manager.setFirstName("Bill");
        manager.setLastName("Anderson");
        manager.setGender(Gender.Male);
        List<Employee> managedEmployees = new ArrayList<Employee>();
        managedEmployees.add(employee);
        manager.setManagedEmployees(managedEmployees);
        employee.setManager(manager);

        String mgrMsg = RestUtils.marshal(context, manager, MediaType.APPLICATION_XML_TYPE);
        Employee mgr = RestUtils.unmarshal(context, mgrMsg, Employee.class.getSimpleName(), MediaType.APPLICATION_XML_TYPE);

        List<Employee> employees = mgr.getManagedEmployees();
        assertTrue("Incorrectly unmarshalled managed employees.", employees.size() == 1);

        if ((employees != null) && (!employees.isEmpty())) {
            for (Employee emp : employees) {
                String firstName = emp.getFirstName();
                assertTrue("Unmarshalled employee first name is incorrect.", "John".equals(firstName));
            }
        }

        DBUtils.dbDelete(employee, em);
    }
}
