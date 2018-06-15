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
//     James Sutherland - initial impl
 package org.eclipse.persistence.testing.tests.jpa.performance.concurrent;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

import org.eclipse.persistence.testing.models.jpa.performance.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the concurrency of a cluster using a remote SessionBean.
 */
public class JPAClusteredEJBConcurrencyComparisonTest extends ConcurrentPerformanceComparisonTest {
    private List<String> serverURLs;

    protected long ids[];
    protected ThreadLocal local;
    protected int errors;
    protected int server;
    protected double percentUpdate = 1.0;
    protected int batchSize = 20;

    public JPAClusteredEJBConcurrencyComparisonTest() {
        setDescription("This test compares the concurrency of a cluster using a remote SessionBean.");

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

        //this.serverURLs.add(properties.getProperty("rcm.wls.server4.url"));
        //this.serverURLs.add(properties.getProperty("rcm.wls.server5.url"));
        //this.serverURLs.add(properties.getProperty("rcm.wls.server6.url"));

        this.local = new ThreadLocal();
    }

    public JPAClusteredEJBConcurrencyComparisonTest(double percentUpdate) {
        this();
        this.percentUpdate = percentUpdate;
        setName("JPAClusteredEJBConcurrencyComparisonTest-%update=" + percentUpdate);
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
        EmployeeService service = (EmployeeService)local.get();
        if (service == null) {
            service = nextEmployeeService();
            this.local.set(service);
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
            service = (EmployeeService)PortableRemoteObject.narrow(context.lookup("EmployeeService#org.eclipse.persistence.testing.models.jpa.performance.EmployeeService"), EmployeeService.class);
        } catch (Exception notFoundException) {
            throw new Error("Lookup failed.", notFoundException);
        }
        return service;
    }

    /**
     * Get employees.
     */
    @Override
    public void setup() {
        super.setup();
        this.local = new ThreadLocal();
        this.server = -1;
        EmployeeService service = getEmployeeService();
        List<Employee> employees = service.findAll();
        this.ids = new long[employees.size()];
        for (int index = 0; index < employees.size(); index++) {
            this.ids[index] = employees.get(index).getId();
        }
        this.errors = 0;
    }

    /**
     * Update employee at random.
     */
    @Override
    public void runTask() throws Exception {
        runBatchTask();
    }

    /**
     * Update employee at random.
     */
    public void runBatchTask() throws Exception {
        try {
            EmployeeService service = getEmployeeService();
            long ids[] = new long[this.batchSize];
            for (int index = 0; index < this.batchSize; index++) {
                ids[index] = this.ids[(int)(Math.random() * this.ids.length)];
            }
            double update = Math.random();
            if (update <= this.percentUpdate) {
                int failures = service.batchUpdate(ids, 20);
                this.errors = this.errors + failures;
            } else {
                service.batchFind(ids);
            }
        } catch (Exception error) {
            error.printStackTrace();
            throw error;
        }
    }

    /**
     * Update employee at random.
     */
    public void runSingleTask() throws Exception {
        try {
            EmployeeService service = getEmployeeService();
            int random = (int)(Math.random() * 1000000);
            int index = (int)(Math.random() * this.ids.length);
            double update = Math.random();
            boolean success = false;
            int count = 0;
            while (!success && (count < 20)) {
                count++;
                Employee employee = service.findById(this.ids[index]);
                if (update <= this.percentUpdate) {
                    employee.setFirstName(String.valueOf(random));
                    employee.setLastName(String.valueOf(random));
                    employee.setSalary(random);
                    try {
                        service.update(employee);
                        success = true;
                    } catch (Exception exception) {
                        this.errors++;
                    }
                } else {
                    success = true;
                }
            }
        } catch (Exception error) {
            error.printStackTrace();
            throw error;
        }
    }

    @Override
    public void endTest() {
        super.endTest();
        System.out.println("Errors:" + this.errors);
        this.errors = 0;
    }
}
