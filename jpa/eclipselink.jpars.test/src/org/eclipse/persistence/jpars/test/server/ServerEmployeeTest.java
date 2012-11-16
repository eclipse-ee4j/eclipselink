/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      gonural - initial
 ******************************************************************************/
package org.eclipse.persistence.jpars.test.server;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Persistence;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.PersistenceFactoryBase;
import org.eclipse.persistence.jpars.test.model.employee.Employee;
import org.eclipse.persistence.jpars.test.model.employee.EmploymentPeriod;
import org.eclipse.persistence.jpars.test.model.employee.Gender;
import org.eclipse.persistence.jpars.test.model.employee.LargeProject;
import org.eclipse.persistence.jpars.test.model.employee.PhoneNumber;
import org.eclipse.persistence.jpars.test.model.employee.SmallProject;
import org.eclipse.persistence.jpars.test.util.DBUtils;
import org.eclipse.persistence.jpars.test.util.ExamplePropertiesLoader;
import org.eclipse.persistence.jpars.test.util.RestUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class ServerEmployeeTest {
    private static final String DEFAULT_PU = "employee-static";
    private static PersistenceContext context = null;
    private static PersistenceFactoryBase factory = null;
    private static long THREE_YEARS = 94608000000L;

    /**
     * Setup.
     *
     * @throws URISyntaxException the uRI syntax exception
     */
    @BeforeClass
    public static void setup() throws URISyntaxException {
        Map<String, Object> properties = new HashMap<String, Object>();
        ExamplePropertiesLoader.loadProperties(properties);
        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, null);
        properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
        properties.put(PersistenceUnitProperties.CLASSLOADER, new DynamicClassLoader(Thread.currentThread().getContextClassLoader()));
        factory = new PersistenceFactoryBase();
        context = factory.bootstrapPersistenceContext(DEFAULT_PU, Persistence.createEntityManagerFactory(DEFAULT_PU, properties), RestUtils.getServerURI(), true);
    }

    /**
     * Tear down.
     */
    @AfterClass
    public static void tearDown() {
    }


    /**
     * Cleanup.
     */
    @After
    public void cleanup() {
        /*
        if (context != null) {
            if (context.getEmf() != null) {
                EntityManager em = context.getEmf().createEntityManager();
                if (em != null) {
                    em.getTransaction().begin();
                    em.createQuery("delete from EmployeeAddress a").executeUpdate();
                    em.createQuery("delete from PhoneNumber b").executeUpdate();
                    //em.createQuery("delete from Project c").executeUpdate();
                    em.createQuery("delete from LargeProject c").executeUpdate();
                    em.createQuery("delete from SmallProject d").executeUpdate();
                    em.createQuery("delete from Employee e").executeUpdate();
                    em.getTransaction().commit();
                }
            }
        }
        */
    }

    /**
     * Test read employee json.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testReadEmployeeJSON() throws RestCallFailedException, URISyntaxException {
        readEmployee(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test read employee xml.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testReadEmployeeXML() throws RestCallFailedException, URISyntaxException {
        readEmployee(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test update employee with employment period json.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testUpdateEmployeeWithEmploymentPeriodJSON() throws RestCallFailedException, URISyntaxException {
        updateEmployeeWithEmploymentPeriod(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test update employee with employment period xml.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testUpdateEmployeeWithEmploymentPeriodXML() throws RestCallFailedException, URISyntaxException {
        updateEmployeeWithEmploymentPeriod(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test create employee with phone numbers json.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     * @throws JAXBException the jAXB exception
     */
    @Test
    public void testCreateEmployeeWithPhoneNumbersJSON() throws RestCallFailedException, URISyntaxException, JAXBException {
        createEmployeeWithPhoneNumbers(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test create employee with phone numbers xml.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     * @throws JAXBException the jAXB exception
     */
    @Test
    public void testCreateEmployeeWithPhoneNumbersXML() throws RestCallFailedException, URISyntaxException, JAXBException {
        createEmployeeWithPhoneNumbers(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test update employee with manager json.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     * @throws JAXBException the jAXB exception
     */
    @Test
    public void testUpdateEmployeeWithManagerJSON() throws RestCallFailedException, URISyntaxException, JAXBException {
        updateEmployeeWithManager(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test update employee with manager xml.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     * @throws JAXBException the jAXB exception
     */
    @Test
    public void testUpdateEmployeeWithManagerXML() throws RestCallFailedException, URISyntaxException, JAXBException {
        updateEmployeeWithManager(MediaType.APPLICATION_XML_TYPE);
    }

    @Ignore
    public void testUpdateEmployeeWithProjectJSON() throws RestCallFailedException, URISyntaxException, JAXBException {
        updateEmployeeWithProject(MediaType.APPLICATION_JSON_TYPE);
    }

    @Ignore
    public void testUpdateEmployeeWithProjectXML() throws RestCallFailedException, URISyntaxException, JAXBException {
        updateEmployeeWithProject(MediaType.APPLICATION_XML_TYPE);
    }

    private void updateEmployeeWithProject(MediaType mediaType) throws RestCallFailedException, URISyntaxException, JAXBException {
        // create an employee
        Employee employee = new Employee();
        employee.setId(8809);
        employee.setFirstName("Charles");
        employee.setLastName("Mingus");
        employee.setGender(Gender.Male);

        employee = RestUtils.restCreate(context, employee, Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("Employee create failed.", employee);

        // create a small project
        SmallProject smallProject = new SmallProject("SmallProject", "This is a small project.");
        smallProject.setId(109);

        smallProject = RestUtils.restCreate(context, smallProject, SmallProject.class.getSimpleName(), SmallProject.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("SmallProject create failed.", smallProject);

        // update employee with small project
        RestUtils.restUpdateBidirectionalRelationship(context, String.valueOf(employee.getId()), Employee.class.getSimpleName(), "projects", smallProject, DEFAULT_PU, mediaType, "teamLeader", true);

        // create a large project
        LargeProject largeProject = new LargeProject();
        largeProject.setId(110);
        largeProject.setName("LargeProject");
        largeProject.setBudget(100000);

        largeProject = RestUtils.restCreate(context, largeProject, LargeProject.class.getSimpleName(), LargeProject.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("LargeProject create failed.", largeProject);

        // update employee with large project
        RestUtils.restUpdateBidirectionalRelationship(context, String.valueOf(employee.getId()), Employee.class.getSimpleName(), "projects", largeProject, DEFAULT_PU, mediaType, "teamLeader", true);


        // read employee and verify that the relationship is set correctly for the projects
        //employee = RestUtils.restRead(context, employee.getId(), Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType);
        /*assertNotNull("Employee read failed.", employee);
        assertNotNull("Employee's project list is null", employee.getProjects());
        assertTrue("Employee's project list is empty", employee.getManagedEmployees().size() > 0);
         */

        // remove projects from the employee
        RestUtils.restUpdateBidirectionalRelationship(context, String.valueOf(employee.getId()), Employee.class.getSimpleName(), "projects", null, DEFAULT_PU, mediaType, "teamLeader", true);

        // delete large project
        RestUtils.restDelete(largeProject.getId(), LargeProject.class.getSimpleName(), LargeProject.class, DEFAULT_PU, null, null, mediaType);

        // delete small project
        RestUtils.restDelete(smallProject.getId(), SmallProject.class.getSimpleName(), SmallProject.class, DEFAULT_PU, null, null, mediaType);

        //delete employee
        RestUtils.restDelete(employee.getId(), Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, null, mediaType);
    }

    /**
     * Update employee with manager.
     *
     * @param mediaType the media type
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     * @throws JAXBException the jAXB exception
     */
    private void updateEmployeeWithManager(MediaType mediaType) throws RestCallFailedException, URISyntaxException, JAXBException {
        // create an employee
        Employee employee = new Employee();
        employee.setId(90909);
        employee.setFirstName("Miles");
        employee.setLastName("Davis");
        employee.setGender(Gender.Male);

        employee = RestUtils.restCreate(context, employee, Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("Employee create failed.", employee);

        // create a manager
        Employee manager = new Employee();
        manager.setId(1010);
        manager.setFirstName("Charlie");
        manager.setLastName("Parker");
        manager.setGender(Gender.Male);

        manager = RestUtils.restCreate(context, manager, Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("Employee manager create failed.", manager);

        // update employee with manager
        RestUtils.restUpdateBidirectionalRelationship(context, String.valueOf(employee.getId()), Employee.class.getSimpleName(), "manager", manager, DEFAULT_PU, mediaType, "managedEmployees", true);

        // read manager and verify that the relationship is set correctly
        manager = RestUtils.restRead(context, manager.getId(), Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType);
        assertNotNull("Manager read failed.", manager);
        assertNotNull("Manager's managed employee list is null", manager.getManagedEmployees());
        assertTrue("Manager's managed employee list is empty", manager.getManagedEmployees().size() > 0);

        employee = RestUtils.restRead(context, employee.getId(), Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType);
        assertNotNull("Manager read failed.", employee);

        for (Employee emp : manager.getManagedEmployees()) {
            assertNotNull("Managed employee's first name is null", emp.getFirstName());
            assertNotNull("Managed employee's last name is null", emp.getLastName());
        }

        // delete employee
        RestUtils.restDelete(employee.getId(), Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, null, mediaType);

        // delete manager
        RestUtils.restDelete(manager.getId(), Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, null, mediaType);
    }

    /**
     * Creates the employee with phone numbers.
     *
     * @param mediaType the media type
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     * @throws JAXBException the jAXB exception
     */
    private void createEmployeeWithPhoneNumbers(MediaType mediaType) throws RestCallFailedException, URISyntaxException, JAXBException {
        // create an employee
        Employee employee = new Employee();
        employee.setId(90909);
        employee.setFirstName("Miles");
        employee.setLastName("Davis");
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();
        Calendar now = GregorianCalendar.getInstance();
        employmentPeriod.setStartDate(now);
        employee.setPeriod(employmentPeriod);

        employee = RestUtils.restCreate(context, employee, Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("Employee create failed.", employee);

        // create a phone number
        PhoneNumber cell = new PhoneNumber();
        cell.setId(employee.getId());
        cell.setNumber("123-123 1234");
        cell.setType("cell");
        cell.setEmployee(employee);

        cell = RestUtils.restCreate(context, cell, PhoneNumber.class.getSimpleName(), PhoneNumber.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("Phone number create failed.", cell);

        // read cell phone number and verify that it belongs to the right employee
        Object cellId = new String(employee.getId() + "+" + cell.getType());
        cell = RestUtils.restRead(context, cellId, PhoneNumber.class.getSimpleName(), PhoneNumber.class, DEFAULT_PU, null, mediaType);
        assertNotNull("Phone Number read failed.", cell);
        assertNotNull("Phone number does not have employee.", cell.getEmployee());
        assertTrue("Phone Number has wrong employee id", cell.getEmployee().getId() == employee.getId());

        // delete phone number
        RestUtils.restDelete(cellId, PhoneNumber.class.getSimpleName(), PhoneNumber.class, DEFAULT_PU, null, null, mediaType);

        // delete employee
        RestUtils.restDelete(new Integer(90909), Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, null, mediaType);
    }

    /**
     * Update employee with employment period.
     *
     * @param mediaType the media type
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    private void updateEmployeeWithEmploymentPeriod(MediaType mediaType) throws RestCallFailedException, URISyntaxException {
        Employee employee = new Employee();
        employee.setId(10234);
        employee.setFirstName("John");
        employee.setLastName("Travolta");
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();
        Calendar now = GregorianCalendar.getInstance();
        employmentPeriod.setStartDate(now);
        employee.setPeriod(employmentPeriod);

        employee = RestUtils.restCreate(context, employee, Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("Employee create failed.", employee);

        Calendar threeYearsLater = GregorianCalendar.getInstance();
        long end = (now.getTimeInMillis() + THREE_YEARS);
        threeYearsLater.setTimeInMillis(end);

        employmentPeriod.setEndDate(threeYearsLater);
        employee.setPeriod(employmentPeriod);

        employee = RestUtils.restUpdate(context, employee, Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType, true);

        assertNotNull("Employee update failed.", employee);
        assertNotNull("Employee's employment period update failed", employee.getPeriod());
        assertNotNull("Employee's employment period end date is null", employee.getPeriod().getEndDate());
        assertTrue("Incorrect end date for employee", employee.getPeriod().getEndDate().getTimeInMillis() == threeYearsLater.getTimeInMillis());

        RestUtils.restDelete(new Integer(10234), Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, null, mediaType);
    }

    /**
     * Read employee.
     *
     * @param mediaType the media type
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    private void readEmployee(MediaType mediaType) throws RestCallFailedException, URISyntaxException {
        Employee employee = new Employee();
        employee.setId(18234);
        employee.setFirstName("Pat");
        employee.setLastName("Metheny");
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();
        employmentPeriod.setStartDate(GregorianCalendar.getInstance());
        employee.setPeriod(employmentPeriod);

        Employee employeeCreated = RestUtils.restCreate(context, employee, Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType, true);
        Employee employeeRead = RestUtils.restRead(context, new Integer(18234), Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType);
        assertNotNull("Employee create failed.", employeeCreated);
        assertNotNull("Employee read failed.", employeeRead);
        assertTrue("Employee created and employee read is different", employeeCreated.getLastName().equals(employeeRead.getLastName()));
        RestUtils.restDelete(new Integer(18234), Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, null, mediaType);
        Employee emp = DBUtils.dbRead(new Integer(10234), Employee.class, context.getEmf().createEntityManager());
        assertNull("Employee could not be deleted", emp);
    }
}
