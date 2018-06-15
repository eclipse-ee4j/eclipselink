/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     pvijayaratnam - cache coordination test implementation
 package org.eclipse.persistence.testing.tests.jpa.sessionbean;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.rmi.PortableRemoteObject;

import junit.framework.*;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.sessionbean.EmployeeService;

/**
 * JPA Cache Coordination SessionBean tests.
 * Testing using EclipseLink JPA in a JEE EJB 3 SessionBean environment.
 * These tests can only be run with a server.
 */
public class SessionBeanTestsRCM extends JUnitTestCase {
    public final int SLEEP = 2000;
    protected List<String> serverURLs;
    protected int server;
    protected EmployeeService service;

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
        this.serverURLs = new ArrayList<String>();
        this.serverURLs.add(properties.getProperty("rcm.wls.server1.url"));
        this.serverURLs.add(properties.getProperty("rcm.wls.server2.url"));
        this.serverURLs.add(properties.getProperty("rcm.wls.server3.url"));
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("SessionBeanTestsRCM");

        suite.addTest(new SessionBeanTestsRCM("testSetup", true));
        suite.addTest(new SessionBeanTestsRCM("testLag", false));

        suite.addTest(new SessionBeanTestsRCM("testUpdates", false));
        suite.addTest(new SessionBeanTestsRCM("testDelete", false));

        return suite;
    }

    /**
     * Return the next server index to use.
     * This cycles through the servers.
     */
    public synchronized int nextServer() {
        this.server++;
        if (this.server >= this.serverURLs.size()) {
            this.server = 0;
        }
        return this.server;
    }

    public EmployeeService getEmployeeService() {
        if (this.service == null) {
            this.service = nextEmployeeService();
        }
        return service;
    }

    public EmployeeService nextEmployeeService() {
        EmployeeService service = null;
        int server = nextServer();
        Properties properties = new Properties();
        String url = this.serverURLs.get(server);
        properties.put("java.naming.provider.url", url);
        System.out.println(server + ":" + url);
        try {
            Context context = new InitialContext(properties);
            service = (EmployeeService)PortableRemoteObject.narrow(context.lookup("EmployeeService#org.eclipse.persistence.testing.models.jpa.sessionbean.EmployeeService"), EmployeeService.class);
        } catch (Exception notFoundException) {
            throw new Error("Lookup failed.", notFoundException);
        }
        return service;
    }

    /**
     * The setup is done as a test, both to record its failure, and to alow execution in the server2.
     */
    public void testSetup() throws Exception {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession("sessionbean"));

        EntityManager em = createEntityManager("sessionbean");
        beginTransaction(em);
        try {
            Employee employee = new Employee();
            em.persist(employee);
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)) {
                closeEntityManagerAndTransaction(em);
            }
        }
    }

    /**
     * Build a test that validates cache coordination updates are working in the cluster.
     */
    public void testUpdates() {
        EmployeeService service = nextEmployeeService();
        Employee employee = (Employee)service.findAll().get(0);

        for (int index = 0; index < 5; index++) {
            service = nextEmployeeService();
            employee = service.findById(employee.getId());
        }

        for (int index = 0; index < 5; index++) {
            service = nextEmployeeService();
            int random = (int)(Math.random() * 1000000);
            employee = service.findById(employee.getId());
            employee.setLastName(String.valueOf(random));
            service.update(employee);
            try {
                Thread.sleep(SLEEP);
            } catch (Exception ignore) {}
        }
    }
    /**
     * Build a test that validates cache coordination deletes are working in the cluster.
     */
    public void testDelete() {
        EmployeeService service = nextEmployeeService();
        Employee employee = new Employee();
        employee.setId(service.insert(employee));

        for (int index = 0; index < 5; index++) {
            service = nextEmployeeService();
            employee = service.findById(employee.getId());
        }

        service = nextEmployeeService();
        employee = service.findById(employee.getId());
        service.delete(employee);
        try {
            Thread.sleep(SLEEP);
        } catch (Exception ignore) {}

        for (int index = 0; index < 5; index++) {
            service = nextEmployeeService();
            Employee result = service.findById(employee.getId());
            if (result != null) {
                fail("Employee should be removed:" + result);
            }
        }

    }

    /**
     * Build a test that attempt to determine the coordination lag in a cluster.
     */
    public void testLag() {
        EmployeeService service = nextEmployeeService();
        Employee employee = (Employee)service.findAll().get(0);

        for (int index = 0; index < 5; index++) {
            service = nextEmployeeService();
            employee = service.findById(employee.getId());
        }

        // Sleep to let RCM to connect.
        try {
            Thread.sleep(20000);
        } catch (Exception ignore) {}

        int sleeps[] = new int[]{1, 50, 100, 500, 1000, 2000, 3000, 4000, 5000, 10000};
        int sleep = 0;
        boolean success = false;
        boolean failed = false;
        while (!success && (sleep < sleeps.length)) {
            for (int index = 0; index < 10; index++) {
                service = nextEmployeeService();
                int random = (int)(Math.random() * 1000000);
                employee = service.findById(employee.getId());
                employee.setLastName(String.valueOf(random));
                try {
                    service.update(employee);
                } catch (Exception lockError) {
                    System.out.println("Failed at sleep of:" + sleeps[sleep] + " on attmept:" + index);
                    sleep = sleep + 1;
                    failed = true;
                    break;
                }
                try {
                    Thread.sleep(sleeps[sleep]);
                } catch (Exception ignore) { }
            }
            if (!failed) {
                success = true;
                System.out.println("Success at sleep of:" + sleeps[sleep]);
                break;
            } else {
                failed = false;
            }
        }
    }

}

