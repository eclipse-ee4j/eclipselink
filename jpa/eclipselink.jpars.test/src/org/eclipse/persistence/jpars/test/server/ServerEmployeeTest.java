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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
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

    /**
     * Test update employee with project json.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     * @throws JAXBException the jAXB exception
     */
    @Ignore
    public void testUpdateEmployeeWithProjectJSON() throws RestCallFailedException, URISyntaxException, JAXBException {
        updateEmployeeWithProject(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test update employee with project xml.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     * @throws JAXBException the jAXB exception
     */
    @Ignore
    public void testUpdateEmployeeWithProjectXML() throws RestCallFailedException, URISyntaxException, JAXBException {
        updateEmployeeWithProject(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test create employee address with binary map json.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testCreateEmployeeAddressWithBinaryMapJSON() throws IOException, RestCallFailedException, URISyntaxException {
        createEmployeeAddressWithBinaryMap(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test create employee address with binary map xml.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testCreateEmployeeAddressWithBinaryMapXML() throws IOException, RestCallFailedException, URISyntaxException {
        createEmployeeAddressWithBinaryMap(MediaType.APPLICATION_XML_TYPE);
    }

    @Ignore
    public void testExecuteNamedQueryWithBinaryDataXML() throws IOException, RestCallFailedException, URISyntaxException {
        executeNamedQueryWithBinaryData(MediaType.APPLICATION_XML_TYPE);
    }

    @Ignore
    public void testExecuteNamedQueryWithBinaryDataJSON() throws IOException, RestCallFailedException, URISyntaxException {
        executeNamedQueryWithBinaryData(MediaType.APPLICATION_JSON_TYPE);
    }

    @Test
    public void testExecuteSingleResultQueryXML() throws IOException, RestCallFailedException, URISyntaxException {
        executeSingleResultQuery(MediaType.APPLICATION_XML_TYPE);
    }

    @Test
    public void testExecuteSingleResultQueryJSON() throws IOException, RestCallFailedException, URISyntaxException {
        executeSingleResultQuery(MediaType.APPLICATION_JSON_TYPE);
    }

    @SuppressWarnings("unused")
    private void executeNamedQueryWithBinaryData(MediaType mediaType) throws URISyntaxException, IOException {
        EmployeeAddress address = new EmployeeAddress("Newyork City", "USA", "NY", "10005", "Wall Street");
        address.setAreaPicture(RestUtils.convertImageToByteArray("manhattan.png"));

        address = RestUtils.restUpdate(context, address, EmployeeAddress.class.getSimpleName(), EmployeeAddress.class, DEFAULT_PU, null, mediaType, true);

        assertNotNull("EmployeeAddress create failed.", address);
        assertNotNull("EmployeeAddress area picture is null", address.getAreaPicture());
        assertTrue("Newyork City".equals(address.getCity()));
        assertTrue("Newyork City".equals(address.getCity()));
        assertTrue("USA".equals(address.getCountry()));
        assertTrue("NY".equals(address.getProvince()));
        assertTrue("10005".equals(address.getPostalCode()));
        assertTrue("Wall Street".equals(address.getStreet()));
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", address.getId());

        Object result = RestUtils.restNamedQuery(context, "EmployeeAddress.getPicture", "List<Object>", DEFAULT_PU, parameters, null, mediaType);

        // delete employee address
        RestUtils.restDelete(address.getId(), EmployeeAddress.class.getSimpleName(), EmployeeAddress.class, DEFAULT_PU, null, null, mediaType);

    }

    private void executeSingleResultQuery(MediaType mediaType) throws URISyntaxException, IOException {
        EmployeeAddress address = new EmployeeAddress("Newyork City", "USA", "NY", "10005", "Wall Street");
        address.setId(9112);
        address.setAreaPicture(RestUtils.convertImageToByteArray("manhattan.png"));

        address = RestUtils.restCreate(context, address, EmployeeAddress.class.getSimpleName(), EmployeeAddress.class, DEFAULT_PU, null, mediaType, true);

        assertNotNull("EmployeeAddress create failed.", address);
        assertNotNull("EmployeeAddress area picture is null", address.getAreaPicture());
        assertTrue("Newyork City".equals(address.getCity()));
        assertTrue("Newyork City".equals(address.getCity()));
        assertTrue("USA".equals(address.getCountry()));
        assertTrue("NY".equals(address.getProvince()));
        assertTrue("10005".equals(address.getPostalCode()));
        assertTrue("Wall Street".equals(address.getStreet()));
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", address.getId());

        String result = RestUtils.restNamedSingleResultQuery(context, "EmployeeAddress.getById", "Object", DEFAULT_PU, parameters, null, mediaType);
        assertNotNull(result);

        String expected = null;
        if (mediaType == MediaType.APPLICATION_JSON_TYPE) {
            expected = "{\"areaPicture\":\"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\",\"city\":\"Newyork City\",\"country\":\"USA\",\"id\":9112,\"postalCode\":\"10005\",\"province\":\"NY\",\"street\":\"Wall Street\",\"_relationships\":[]}";
        } else {
            expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<employeeAddress xmlns:atom=\"http://www.w3.org/2005/Atom\"><areaPicture>iVBORw0KGgoAAAANSUhEUgAAAJYAAACWCAMAAAAL34HQAAADAFBMVEXP3uLv7+/m5ub39/f////e3t73/f8AxPFj2/YIxvFT1/bW9v3r+v2Z5/nw/P4wz/S27vshzPMQyPJY2PaL5PnP9Pzf+P194fjm+f687/s70vTD8fyE4/hK1fU60fRr3fcXyfKV5vkZyvJy3veo6/qm6vqs7PopzfNF0PFD1PWG4faI2u5u2fHp9Paj3erV3d+i4O/X8ffP5uoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA6TXZvAAAKCElEQVR42uVc23LcNhJFN+BYluQkvsiWK0kl2a1N7dP+/2fsW97XTnmTUnzZtWVJg0ZvAwQ5IIcXcIjhqGrHN0kzJg9Pd5++ACQYdR9fqP7/YLHj+wjLaQTiewcL5I9BRzV5dJ98S5tImTPZRoUDRiIn10wA4Vurj86WSylD4NquR4bVgaDvicv34dB8bFj9cecDcxqaWc2GCYcSmQjHisTxExM/cMcwIo+bVxs+Cixl9/S+w8IC/Xf913E+DQxRqg+E61w9/3zl8Eu/zmLFFSqGddl6TFdnl3hFI8HAZtgBDxWID5S60YM+TaISFnymXNeIL3/3pFjuTzgIAkfeo0H1OpAR/WEvvwzaiG2Q1WENOQxbF1dKPfu3IjNcW1Ru71nr092DwELcKLUh1sMfUBBcC3xlvZYRf7gWhbgdOzaF9BMrMl6HrfN33k5EOJovnRCmRbeYcR227ry/345qjxBkoPpXrxWJr+TPrZpqcxzESpFXgiXR//IqR6iBBotYPIBmbdTG0uiB63dhICkegK1nn9XZ7UQRGBkCu16qfq/UiZvoc3iiGyoP6wT82Ti/hVwFFj5zXpZgSZ1/AJfXn7yN7iZOzBPIirP1aJwMqtngVY346HpBy30wWGgmWhzuaOk6sL6+7q0JNFGFjnK6seKwTs8H4l+HSaAOtTuRY2ldWa8G69HbxnE6ZBgESZUAcj7wFTwAOFoJ1vP/xC8+/9wEo7XV2dlXyVIg29jtOOkz3DoTmw+NT23Q+aZLUrJQQ1p+SZls2xEIwKuw9XB7mrcX6skv/qpZfoaMVXGlIybmKa0vqfLppI/v6MPf/tACxw+zQkNR/dx3FhoI4oe5H1tBVC8TF37xSb1wrzGkR2Nq5ybnkIK2GWQIwsX9cl+SrKTkeqsufj/h+gzauDjE0QLTJvmaB4qJcrBCfx9fH9VfPuDD68CIqxRLqUqp9LYvFBOj7g+6crASY0hxeuXO3vmDC0/orLXSsMbGS3f47Z3OFZudniejLJGC829+C/1y3VqzfCN+PtgCdWr6UpF4AslVX1zz3X+rCQR6bZD+WYMf1DgcgEXawSGMeHnTromZ67mIKIEx4uo80BIGXQFxNCwPC//szNzctsSJ63VII17pu2vm4rAep9Xf6eakxUYEaGhA1nV8IwFeBtZFGk5o390NtKwDSdDEnxtXFNbJaepZz/DW9amRJG1t7Whm1vU6aBFYlykLL79Iq+yGekNDoxmibj1KyNaTNwms7/D2tn/Cp4kzVBLD+mwJWCYl693d3dDMVE93PP5oHlcBI55+TL75/saEdYAlV+mKqDwlVeZP1/xZ8cJjSmIoAOtsk7j1p+vhsXd+1ocCRkxi6/v3N2XctQCsrROfvhbBpBJHXA4Lt1J67nzOZVsA2GJciYPfxKSsHC4MouVsnV40X77yxRNw9t6Q4aHcclgnW2e6rQsZsLSIrAKw3Na3zqrCSfKeRreErAKwPlz06DQr2M/xyTpf1i9vMZIVVH3X+L/V+2g91RXXYrZOfkxq1LSggT3cq5nhLIb16l+pNptEY/fYFFiuOk3aw+/+bAiyat5+LdUZ4SyGleRpdu3DLgjGxbCeBp7g28dfff2akx6L9/GuhmBTwog/vbHvFX1K5lvSrkqjg6SPxZZ40fmbx/9gRtNeyteShZDWh/Uw/P3HVz9ubp+G0xvYiat9Nw8Xmdg8vfxVDcxi6lKV8gITXEGXP/t1wBRczduUZZxnz71hPX1S9RQfX6jn34wPPSqfy7InLfatcJonv3mh/+eYWayIJLuZvr93dfol1DM/fHj1lq4G3UaqVOenuKwZIKc2rIeCC13+IdywGz5EJfsctmplNWoOCkXi+MnCANJEm0M+rMWROH5ZSDWZec4FpXTL5eYXcNNWbLaaLO/HcmNsEzWsgA2ylI+ya6v8VFRgYCOxn4PKzJGuxbC0Vi53az6uaEQHOvNzwYhUrWP3/5+mYlvMFuXW7JCs5Wt9sJxYN+ZaHeC1dJ6Yjwrq2n7Q9aWh1nul6nCvWppDbIZv1hrp14F9gmTXT4ZL+nMzIDCwA8hfsL+IdOWGc6oC3dTQ4eMekuvbMgITRTMZ0hbrbeVhLT61FdUlSvaUQaJPfvt5e3MdO4mIWhT2GFFyK/qBh98oICRpcQnTWuurrZI985O2CG04bdLfdv53m79dWM11IIq1EcPG6G5xNy9e0PMa/CpUqtV1AbeAidviiBGnLVOTNWNSRFCh8ku/jI29EktS+74MnI1qd7wy1S/I6aHawGwhFhyVmuvtvg1suzzOPdc2sia1IW4mi2HsncGnReeXf+P6HMCONOwvp9n1ia4qLAljFMFj55yEYxAJ64OS/N0PQp+Dyc4nx4jRuXI0C21cGA9uLv0P+ojz3u00oTgb+CULLpJ85lRORnsDUeUfFHbv22oTv9+x4dnc9A3Lzf4wZ0yJIGgpgGom0AIWA3+iiz33w6Ka7/PxWjJczPu8/5jxptPgjedlQHuhIBdPZzMqCHbZTq+nBUL7nOJb6yqLST5DNDGrYVQuNoaYqT0O2K+wwazZaLzRor5SCC4GGw78oGReskjgrOQ2AVtij02Ir6mFCsFBTLaphC1AqD9cFH4tFQnuFsx9sHJhuupIbmjNKXRpNqyTQCUA3rmNw1g5EoiUAW2SM6JalBM7/o79N5YTb6M8fEKEKoq6UM1GUqP4vZaPNUUTL4zEVvQ4tP0pajv/Tjf2sPAk1hPJEH/yQdqbw7psZZLVzhc9wwVf9kGzIaJZy/GUqXCjRvduDb/1bFhOOUspO3bTvSW92bK40Q0+XRV8aU2zWwV2YXGWcudMaeTKtvO/5AvnywWOPApFD3pr3I4RKxtOkLXr4z1WtKGJRqguoWX0eFVjt6Kb+emu501Ie2uqOicGQ9hJUSIY5PfEBkhjXtxhy4vcRPu3cxeWrwugNYmgqsDz4ELPFFddmqWDaVfpwAJiSJSkd1bqRvq7XUMT1LaKtrOYUah1YDnvi2PTjp471loBQNFBd25e5oqizId6tGFxJR7DuHrvowtnrPDAePDaB5lLn21YzYUP+PyuBbOGmK5yi2xQXVgwEYv7rMNBtp8PVQy823XtkS+3NIGjKFjSCM6ag7XYGh+cE8zY0ZMuuUw+wmNETkUWJvbFzEFFzTBFMO1RatZszVhhyHaoPVjqsDWNatZOMZ9k+MGCAWhgi6ejxM7yWF68scxMUCXZSM8zYImXmRgmEM6mag1YXN3dsfYLp9/nIzyWDkfbUOs0AKjjwBqkjLS0LPZosAbU3WJ203EQWP315XEAjbUYc+uQw8DiHUx45IdCeljdbR/u+A+qxN0lwaOEXnewgjZ96KDrX1M7ghFTboxT94CqYESdirq6Jy9Mlo5J3xdUfnHFm82q/ocCHY8tP0v0C0QA9wfVQR8suuD1P0iRvjbgSsgzAAAAAElFTkSuQmCC</areaPicture><city>Newyork City</city><country>USA</country><id>9112</id><postalCode>10005</postalCode><province>NY</province><street>Wall Street</street></employeeAddress>";
        }

        assertTrue(result.contains(expected));

        // delete employee address
        RestUtils.restDelete(address.getId(), EmployeeAddress.class.getSimpleName(), EmployeeAddress.class, DEFAULT_PU, null, null, mediaType);
    }

    private void createEmployeeAddressWithBinaryMap(MediaType mediaType) throws IOException, RestCallFailedException, URISyntaxException {
        EmployeeAddress address = new EmployeeAddress("Newyork City", "USA", "NY", "10005", "Wall Street");
        address.setAreaPicture(RestUtils.convertImageToByteArray("manhattan.png"));

        address = RestUtils.restUpdate(context, address, EmployeeAddress.class.getSimpleName(), EmployeeAddress.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("EmployeeAddress create failed.", address);

        assertNotNull("EmployeeAddress area picture is null", address.getAreaPicture());
        assertTrue("Newyork City".equals(address.getCity()));

        assertTrue("Newyork City".equals(address.getCity()));
        assertTrue("USA".equals(address.getCountry()));
        assertTrue("NY".equals(address.getProvince()));
        assertTrue("10005".equals(address.getPostalCode()));
        assertTrue("Wall Street".equals(address.getStreet()));

        // delete employee address
        RestUtils.restDelete(address.getId(), EmployeeAddress.class.getSimpleName(), EmployeeAddress.class, DEFAULT_PU, null, null, mediaType);
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
