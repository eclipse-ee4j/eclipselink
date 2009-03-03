/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial impl
 ******************************************************************************/  
 package org.eclipse.persistence.testing.tests.jpa.performance.concurrent;

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
    protected List<Employee> employees;
    protected ThreadLocal local;

    public JPAClusteredEJBConcurrencyComparisonTest() {
        setDescription("This test compares the concurrency of a cluster using a remote SessionBean.");
    }
    
    public EmployeeService getEmployeeService() {
        EmployeeService service = (EmployeeService)local.get();
        if (service == null) {
            Properties properties = new Properties();
            String url = System.getProperty("server-url");
            if (url != null) {
                properties.put("java.naming.provider.url", url);
            }
            try {
                Context context = new InitialContext(properties);
                service = (EmployeeService)PortableRemoteObject.narrow(context.lookup("EmployeeService#org.eclipse.persistence.testing.models.jpa.sessionbean.EmployeeService"), EmployeeService.class);
            } catch (Exception notFoundException) {
                throw new Error("Lookup failed.", notFoundException);
            }
            local.set(service);
        }
        return service;
    }

    /**
     * Get employees.
     */
    public void setup() {
        super.setup();
        this.local = new ThreadLocal();
        EmployeeService service = getEmployeeService();
        this.employees = service.findAll();
    }

    /**
     * Update employee at random.
     */
    public void runTask() throws Exception {
        EmployeeService service = getEmployeeService();
        int random = (int)(Math.random() * 1000000);
        int index = (int)(Math.random() * 100);
        Employee employee = service.findById(this.employees.get(index).getId());
        employee.setFirstName(String.valueOf(random));
        employee.setLastName(String.valueOf(random));
        employee.setSalary(random);
        try {
            service.update(employee);
        } catch (Exception exception) {
            System.out.println(exception);
        }
    }
}