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
*     pvijayaratnam - cache coordination test implementation
 ******************************************************************************/
 package org.eclipse.persistence.testing.tests.jpa.sessionbean;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.rmi.PortableRemoteObject;

import junit.framework.*;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Employee.EmployeeStatus;
import org.eclipse.persistence.testing.models.jpa.sessionbean.EmployeeService;

/**
 * EJB 3 SessionBean tests.
 * Testing using EclipseLink JPA in a JEE EJB 3 SessionBean environment.
 * These tests can only be run with a server.
 */
public class SessionBeanTestsRCM extends JUnitTestCase {
    protected EmployeeService service;
    private String wlsUserName;
    private String wlsPassword;
    private String server1Url;
    private String server2Url;
    private String server3Url;
    int empId = 0;
    private Employee employeeCached = null;

    public SessionBeanTestsRCM() {
        super();
    }

    public SessionBeanTestsRCM(String name) {
        super(name);
    }

    public SessionBeanTestsRCM(String name, boolean shouldRunTestOnServer) {
        super(name);
        this.shouldRunTestOnServer = shouldRunTestOnServer;

        URL url = getClass().getResource("/weblogic.properties");
        Properties properties = new Properties();
        try {
            properties.load(url.openStream());
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
        server1Url = properties.getProperty("rcm.wls.server1.url");
        server2Url = properties.getProperty("rcm.wls.server2.url");
        server3Url = properties.getProperty("rcm.wls.server3.url");
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("SessionBeanTestsRCM");

        suite.addTest(new SessionBeanTestsRCM("testSetupRcmOnServer2", true));
        suite.addTest(new SessionBeanTestsRCM("testSetupForDeleteOnServer2", false));

        suite.addTest(new SessionBeanTestsRCM("testSetupRcmOnServer1", false));
        suite.addTest(new SessionBeanTestsRCM("testPerformDeleteOnServer1", false));

        suite.addTest(new SessionBeanTestsRCM("testConfirmUpdateOnServer2", false));
        suite.addTest(new SessionBeanTestsRCM("testConfirmDeleteOnServer2", false));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to alow execution in the server2.
     */
    public void testSetupRcmOnServer2() throws Exception {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession("sessionbean"));
    }

    public EmployeeService getEmployeeService(String url) throws Exception {
        Properties properties = new Properties();
        properties.put("java.naming.provider.url", url);
        Context context = new InitialContext(properties);

        try {
            return (EmployeeService) PortableRemoteObject.narrow(context.lookup("java:comp/env/ejb/EmployeeService"), EmployeeService.class);
        } catch (NameNotFoundException notFoundException) {
            try {
                return (EmployeeService) PortableRemoteObject.narrow(context.lookup("ejb/EmployeeService"), EmployeeService.class);
            } catch (NameNotFoundException notFoundException2) {
                try {
                    // WLS likes this one.
                    return (EmployeeService) PortableRemoteObject.narrow(context.lookup("EmployeeService#org.eclipse.persistence.testing.models.jpa.sessionbean.EmployeeService"), EmployeeService.class);
                } catch (NameNotFoundException notFoundException3) {
                    try {
                         //jboss likes this one
                         return (EmployeeService) PortableRemoteObject.narrow(context.lookup("EmployeeService/remote"), EmployeeService.class);
                    } catch (NameNotFoundException notFoundException4) {
                         throw new Error("All lookups failed.", notFoundException);
                    }
                }
            }
        }
    }


    /* CacheCoordination Delete Test Setup on Server2:
     *  This test insert an employee record, which will be deleted later using Server1
     */
    public void testSetupForDeleteOnServer2() throws Exception {
        // Ensure each session starts to enable cache synch.
        getEmployeeService(server1Url).findAll();
        getEmployeeService(server2Url).findAll();
        Thread.sleep(5000);
        /* Create an Employee record using Server2 */
        Employee employee = new Employee();
        employee.setFirstName("Jane2");
        employee.setLastName("Doe2");
        employee.setAddress(new Address());
        employee.getAddress().setCity("Ottawa2");
        Employee manager = new Employee();
        manager.setFirstName("John2");
        manager.setLastName("Done2");
        employee.setManager(manager);

        int empID = getEmployeeService(server2Url).insert(employee);

        employee = getEmployeeService(server2Url).findById(empID);
        if (employee == null) {
          fail("Server2 CacheCoordination Setup Failure: New employee added from Server2 is not found in cache or DB.");
        }
    }

    /**
     * CacheCoordination Update Test setup on Server1:
     * This test insert an employee record, which is then updated using Server1. Later on,  this update will be verified on Server2.
     * The setup is done as a test, both to record its failure, and to alow execution in the server1.
     */
    public void testSetupRcmOnServer1() throws Exception {
        Thread.sleep(1000);

        /* Create an Employee record in Server1 */
        Employee employee = new Employee();
        employee.setFirstName("Jane1");
        employee.setLastName("Doe1");
        employee.setAddress(new Address());
        employee.getAddress().setCity("Ottawa1");
        Employee manager = new Employee();
        manager.setFirstName("John1");
        manager.setLastName("Done1");
        employee.setManager(manager);
        employee.setStatus(EmployeeStatus.FULL_TIME);

        empId = getEmployeeService(server1Url).insert(employee);

        /* read Employee from cache and/or DB */
        Employee jane1 = getEmployeeService(server1Url).findById(empId);
        /* update employee on Server1 */
        jane1.setLastName("LastNameUpdatedOnServer1");
        jane1.getAddress().setCity("newCity");
        getEmployeeService(server1Url).update(jane1);

        if (!jane1.getLastName().equals("LastNameUpdatedOnServer1")) {
            fail("UpdateTest Setup on Server1 failed");
        }

    }

    /**
     * CacheCoordination Test setup for Delete on Server1:
     * Find employee created on Server2, then delete it using Server1.
     */
    public void testPerformDeleteOnServer1() throws Exception {
        Thread.sleep(1000);
        List result = getEmployeeService(server1Url).findByFirstName("Jane2");
        int count = 0;
        for (Iterator i = result.iterator(); i.hasNext();) {
             employeeCached = (Employee) i.next();
        }

        if (employeeCached == null){
          fail("Perform Delete Test failed: New employee was not found in distributed cache to delete");
        }
        getEmployeeService(server1Url).delete(employeeCached);
    }

    /* CacheCoordination Test - Verify that Object Update done on Server1 is sync with Server2 thru cache:
     *  This test uses JPQL to read object on Server2.
     */
    public void testConfirmUpdateOnServer2() throws Exception {
        Thread.sleep(1000);
        /* verify updates are in sync: read Employee from using Server2 URL */
        List result = getEmployeeService(server2Url).findByFirstName("Jane1");
        int count = 0;
        for (Iterator i = result.iterator(); i.hasNext();){
             employeeCached = (Employee) i.next();
        }
        if (employeeCached == null){
            fail("Object Update Test verification failed: New employee was not found in distributed cache");
        }
        if (!employeeCached.getLastName().equals("LastNameUpdatedOnServer1")) {
            fail("Object Update Test verification failed: Changes from server1 is not seen by server2 from distributed cache");
        }
    }

   /* CacheCoordination Test - Verify that Object Delete done on Server1 is sync with Server2 thru cache:
    *    This test uses JPQL to read object on Server2.
    */
   public void testConfirmDeleteOnServer2() throws Exception {
        Thread.sleep(1000);

        /* verify deletes are in sync: read Employee from using Server2 URL */
        List result = getEmployeeService(server2Url).findByFirstName("Jane2");
        for (Iterator i = result.iterator(); i.hasNext();) {
            employeeCached = (Employee) i.next();
        }
        if (employeeCached != null) {
            fail("Object Delete Test verification failed: employee was not removed from cache as expected" );
        }

   }

}

