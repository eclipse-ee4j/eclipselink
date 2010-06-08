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
 *     etang - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
 package org.eclipse.persistence.testing.tests.jpa.sessionbean;

import java.util.Random;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.rmi.PortableRemoteObject;

import junit.framework.*;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Department;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.sessionbean.EmployeeService;
import org.eclipse.persistence.sessions.server.ServerSession;

/**
 * EJB 3 SessionBean tests using EclipseLink JPA
 * These tests can only be run in Oracle High Availability environment -  
 * a WebLogic server configured with Multi Data Sources to connect to Oracle RAC database
 * They read/write entity bean repeatly for 300 times, in the meantime, RAC database failures can be simulated,
 * to ensure no exceptions would be caused the RAC database failures.
 */
public class SessionBeanHATests extends JUnitTestCase {
    protected EmployeeService service;
    static ServerSession serverSession;
    
    public SessionBeanHATests() {
        super();
    }

    public SessionBeanHATests(String name) {
        super(name);
    }

    public SessionBeanHATests(String name, boolean shouldRunTestOnServer) {
        super(name);
        this.shouldRunTestOnServer = shouldRunTestOnServer;
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite("SessionBeanHATests");
        suite.addTest(new SessionBeanHATests("testSetup", true));
//        suite.addTest(new SessionBeanHATests("testFindAll", true));
        suite.addTest(new SessionBeanHATests("testFindAllMultipleThread", true));
//        suite.addTest(new SessionBeanHATests("testUpdate", false));

        return suite;
    }
    
    public void testSetup() throws Exception {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession("sessionbean"));
        Employee bob = new Employee();
        bob.setFirstName("Bob");
        bob.setLastName("Jones");
        bob.setAddress(new Address());
        bob.setDepartment(new Department());
        getEmployeeService().insert(bob);
        Employee joe = new Employee();
        joe.setFirstName("Joe");
        joe.setLastName("Smith");
        joe.setAddress(new Address());
        joe.setDepartment(new Department());
        getEmployeeService().insert(joe);
    }
    
    public EmployeeService getEmployeeService() throws Exception {
        if (service == null) {
            Properties properties = new Properties();
            String url = System.getProperty("server.url");
            if (url != null) {
                properties.put("java.naming.provider.url", url);
            }
            Context context = new InitialContext(properties);

            try {
                service = (EmployeeService) PortableRemoteObject.narrow(context.lookup("EmployeeService#org.eclipse.persistence.testing.models.jpa.sessionbean.EmployeeService"), EmployeeService.class);
            } catch (NameNotFoundException notFoundException) {
                throw new Error("Lookup failed.", notFoundException);
            }
        }
        return service;
    }

    public void testFindAll() throws Exception {
        int repeatTimes = 300;

        for (int i = 1; i <= repeatTimes; i++) {
            try {
                System.out.println("===FindAll iteration " + i);
                List result = getEmployeeService().findAll();
                int employCount = 0;
                for (Iterator iterator = result.iterator(); iterator.hasNext(); ) {
                    Employee employee = (Employee)iterator.next();
                    employCount++;
                }
                if (employCount != 2) {
                    fail("The count is " + employCount +", Failed to find all employees");
                }
                Thread.currentThread().sleep(3000);
            } catch (Exception e) {
                fail(e.toString());
            }
        }
    }

    public void testFindAllMultipleThread() throws Exception {
        // Initialize and launch threads to find Employees
        FindAllThread[] helperRunnable = new FindAllThread[10];
        for (int i = 0; i < helperRunnable.length; i++) {
            helperRunnable[i] = new FindAllThread(i);
        }
        org.eclipse.persistence.platform.server.ServerPlatform platform = JUnitTestCase.getServerSession("sessionbean").getServerPlatform();

        for (int i = 0; i < helperRunnable.length; i++) {
            platform.launchContainerRunnable(helperRunnable[i]);
        }

        // wait all FindAllConcurrentlyThread's for finishing FindAll queries
        while(true){
            Thread.currentThread().sleep(3000);

            boolean finished = true;
            for (int i = 0; i < helperRunnable.length; i++) 
            {
                finished = finished && helperRunnable[i].finished;
            }
            if (finished) break;
        }
    
        // verify if all FindAllConcurrentlyThread's are succeeded
        for (int i = 0; i < helperRunnable.length; i++) {
            if (!helperRunnable[i].succeeded) 
                fail("RAC failure caused some other exception: "+helperRunnable[i].relatedException);
        }
    }

    public void testUpdate() throws Exception {
        Employee employee = new Employee();
        employee.setFirstName("Bob");
        employee.setLastName("Smith");
        Employee manager = new Employee();
        manager.setFirstName("Jon");
        manager.setLastName("Way");
        employee.setAddress(new Address());
        employee.getAddress().setCity("Nepean");
        employee.setManager(manager);
        
        int id = getEmployeeService().insert(employee);
        
        int repeatTimes = 300;
        Random generator = new Random();

        for (int i = 1; i <= repeatTimes; i++) {
            try {
                System.out.println("===Update iteration " + i);

                employee = getEmployeeService().findById(id);
                employee.setLastName(String.valueOf(generator.nextInt()));
                employee.getAddress().setCity(String.valueOf(generator.nextInt()));
                getEmployeeService().update(employee);
                Thread.currentThread().sleep(3000);
            } catch (Exception e) {
                fail(e.toString());
            }
        }
    }
}
