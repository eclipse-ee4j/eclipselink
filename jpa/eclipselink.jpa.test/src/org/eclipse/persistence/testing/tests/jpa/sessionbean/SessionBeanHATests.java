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
 *     etang - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
 package org.eclipse.persistence.testing.tests.jpa.sessionbean;

import java.sql.Date;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.rmi.PortableRemoteObject;

import junit.framework.*;

import javax.persistence.EntityManager;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.*;
import org.eclipse.persistence.testing.models.jpa.sessionbean.EmployeeService;

/**
 * EJB 3 SessionBean tests using EclipseLink JPA
 * These tests can only be run in Oracle High Availability environment -  
 * a WebLogic server configured with Multi Data Sources to connect to Oracle RAC database, the test steps are:
 * 1. Only run testSetup() with JTA to insert a certain amount of objects like defined below 2514
 * 2. Test Read using JTA and NonJTA
 * 2.1 JTA:    Repeat 300 times, swap/crash 5 times RAC DB, you will get java.sql.SQLRecoverableException: No more data to read from 
 *             socket immediately when shutdown happended in middle of transaction during read query
 * 2.2 NonJTA: repeat 121 times, swap or crash RAC DB 5 times, you will get "No more data to read from socket" exception, but tests continue
 * 3. Test Update only using JTA
 * 3.1 JTA:    repeat 60 times, stop/kill one instance, keep the instance down, you will see unexpected exception when trying to insert
 *             address, the tests should continue after ignore the first exception
 */
public class SessionBeanHATests extends JUnitTestCase {
    protected EmployeeService service;
    static ServerSession serverSession;
    static int jtaCount = 2514;
    static int nonJTACount = 2514;

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
        //suite.addTest(new SessionBeanHATests("testSetup", true));
        //suite.addTest(new SessionBeanHATests("testFindAll", true));
        //suite.addTest(new SessionBeanHATests("testFindAllNonJTA", true));
        suite.addTest(new SessionBeanHATests("testUpdate", false));
        //following test is not used in RAC testing
        //suite.addTest(new SessionBeanHATests("testFindAllMultipleThread", true));
        return suite;
    }
    
    public void testSetup() throws Exception {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession("sessionbean"));
        for (int i = 1; i <= jtaCount; i++)
        {
            Employee bob = new Employee();
            bob.setFirstName("Bob");
            String lastName = ""+System.currentTimeMillis();
            bob.setLastName(lastName);
            bob.setAddress(new Address());
            bob.setDepartment(new Department());
            getEmployeeService().insert(bob);
        }
    }

    public void testFindAll() throws Exception {
        int repeatTimes = 50;

        for (int i = 1; i <= repeatTimes; i++) {
            try {
                System.out.println("===FindAll iteration " + i);
                List result = getEmployeeService().findAll();
                int employCount = 0;
                for (Iterator iterator = result.iterator(); iterator.hasNext(); ) {
                    Employee employee = (Employee)iterator.next();
                    employCount++;
                }
                if (employCount != jtaCount) {
                    fail("The count is " + employCount +", Failed to find all employees");
                }
                Thread.currentThread().sleep(3000);
            } catch (Exception e) {
                fail(e.toString());
            }
        }
    }

    public void testFindAllNonJTA() throws Exception {
        int repeatTimes = 121;

        for (int i = 1; i <= repeatTimes; i++) {
            EntityManager em = getServerPlatform().getEntityManagerFactory("sessionbean").createEntityManager();
            try {
                System.out.println("===FindAllNonJTA iteration " + i);
                List result = em.createQuery("Select e from Employee e").getResultList();
                int employCount = 0;
                for (Iterator iterator = result.iterator(); iterator.hasNext(); ) {
                    Employee employee = (Employee)iterator.next();
                    employCount++;
                }
                if (employCount != nonJTACount) {
                    fail("The count is " + employCount +", Failed to find all employees");
                }
                Thread.currentThread().sleep(3000);
            } catch (Exception e) {
                fail(e.toString());
            }
        }
    }

    public void testUpdate() throws Exception {
        //create a new employee for updating
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
        
        //int repeatTimes = 500;
        int repeatTimes = 60;
        int exceptTimes = 0;

        for (int i = 1; i <= repeatTimes; i++) {
            try {
                System.out.println("===Update iteration " + i);
                employee = getEmployeeService().findById(id);
                
                employee.setFirstName("Small Romantic Moon Over The Lake");
                employee.setLastName("Big White Horse who gallops the grass");

                Address address = new Address();
                address.setCountry("Canada");
                address.setProvince("Newfoundland and Labrador");
                address.setCity("Harbour Main-Chapel Cove-Lakeview");
                address.setStreet("96 Brownskill");
                address.setPostalCode("K1P1A6");
                employee.setAddress(address);

                employee.setMale();
                employee.setSalary(150000);
                employee.setRoomNumber(407);

                Department dept  = new Department();
                dept.setName("Software Quality Assurance");
                dept.addManager(employee);
                employee.setDepartment(dept);

                Vector reps = new Vector(2);
                reps.add("scrum everyday, weekly status report and abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz");
                reps.add("unit test, integration and system test abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz");
                employee.setResponsibilities(reps);

                EmploymentPeriod period = new EmploymentPeriod();
                period.setStartDate(Date.valueOf("2010-11-12"));
                period.setEndDate(Date.valueOf("2010-11-13"));
                employee.setPeriod(period);

                employee.setWorkWeek(new HashSet<Employee.Weekdays>());
                employee.getWorkWeek().add(Employee.Weekdays.MONDAY);
                employee.getWorkWeek().add(Employee.Weekdays.TUESDAY);
                employee.getWorkWeek().add(Employee.Weekdays.WEDNESDAY);
                employee.getWorkWeek().add(Employee.Weekdays.THURSDAY);

                employee.setStatus(Employee.EmployeeStatus.FULL_TIME);
                employee.setPayScale(Employee.SalaryRate.EXECUTIVE);
                employee.setFormerEmployment(new FormerEmployment("Original", new EmploymentPeriod(Helper.dateFromYearMonthDate(1990, 0, 1), Helper.dateFromYearMonthDate(1993, 11, 31))));

                Vector emps = new Vector(2);
                Employee emp1 = new Employee();
                emp1.setFirstName("Hemavatinandan");
                emp1.setLastName("Padmabandhu");
                emp1.setManager(employee);
                emps.add(emp1);
                Employee emp2 = new Employee();
                emp2.setFirstName("Deepaprabha");
                emp2.setLastName("Kanchanprabha");
                emp2.setManager(employee);
                emps.add(emp2);
                employee.setManagedEmployees(emps);

                Vector projs = new Vector(3);
                Project proj1 = new SmallProject();
                proj1.setName("Persistence Enabled in Cloud");
                projs.add(proj1);
                Project proj2 = new LargeProject();
                proj2.setName("Session Data Affinity Test");
                projs.add(proj2);
                Project proj3 = new LargeProject();
                proj3.setName("Release Exit Criteria Testing");
                proj3.setTeamLeader(employee);
                projs.add(proj3);
                employee.setProjects(projs);

                Vector phones = new Vector(2);
                PhoneNumber phone = new PhoneNumber("work", "613", "1234567");
                phones.add(phone);
                phone.setOwner(employee);
                phone = new PhoneNumber("home", "613", "8888666");
                phones.add(phone);
                phone.setOwner(employee);
                employee.setPhoneNumbers(phones);

                getEmployeeService().update(employee);
                Thread.currentThread().sleep(3000);
            } catch (Exception e) {
                exceptTimes++;
                //When shutdown one instance, and keep it down forever, we should except one exception during the shutdown, tests should continue after that
                System.out.println("=====the exceptTimes is " + exceptTimes);
                if (exceptTimes != 1)
                {
                    fail(e.toString());
                }
            }
        }
        
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

}
