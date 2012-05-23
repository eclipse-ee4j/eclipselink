/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
 package org.eclipse.persistence.testing.tests.jpa.performance;

import org.eclipse.persistence.testing.models.jpa.performance.Employee;
import org.eclipse.persistence.testing.models.jpa.performance.EmployeeService;
import org.eclipse.persistence.testing.tests.jpa.performance.concurrent.JPAClusteredEJBConcurrencyComparisonTest;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestModel;

/**
 * Performance tests that compare JPA concurrency in a JEE cluster using SessionBeans.
 */
public class JPAClusteredConcurrencyComparisonModel extends TestModel {

    
    public void addTests() {
        addTest(buildClusterValidationTest());
        addTest(buildComputeLagTest());
        //addTest(new JPAClusteredEJBConcurrencyComparisonTest());
        //addTest(new JPAClusteredEJBConcurrencyComparisonTest(0.5));
        //addTest(new JPAClusteredEJBConcurrencyComparisonTest(0.1));
        //addTest(new JPAClusteredEJBConcurrencyComparisonTest(0.0));
    }
    
    /**
     * Create/populate database.
     */
    public void setup() {
        JPAClusteredEJBConcurrencyComparisonTest test = new JPAClusteredEJBConcurrencyComparisonTest();
        test.getEmployeeService().createTables();
        //for (int index = 0; index < 10; index++) {
            test.getEmployeeService().populate();
        //}
        test.nextEmployeeService();
        test.nextEmployeeService();
        test.nextEmployeeService();
    }
    
    /**
     * Build a test that validates cache coordination is working in the cluster.
     */
    public TestCase buildClusterValidationTest() {
        TestCase test = new TestCase() {
            public void test() {
                JPAClusteredEJBConcurrencyComparisonTest test = new JPAClusteredEJBConcurrencyComparisonTest();
                EmployeeService service = test.nextEmployeeService();
                Employee employee = (Employee)service.findAll().get(0);
                
                for (int index = 0; index < 5; index++) {
                    service = test.nextEmployeeService();
                    employee = service.findById(employee.getId());
                }

                for (int index = 0; index < 5; index++) {
                    service = test.nextEmployeeService();
                    int random = (int)(Math.random() * 1000000);
                    employee = service.findById(employee.getId());
                    employee.setLastName(String.valueOf(random));
                    service.update(employee);
                    try {
                        Thread.sleep(2000);
                    }
                    catch (Exception ignore) {}
                }
            }
        };
        test.setName("ClusterValidationTest");
        return test;
    }
    
    /**
     * Build a test that attempt to determine the coordination lag in a cluster.
     */
    public TestCase buildComputeLagTest() {
        TestCase test = new TestCase() {
            public void test() {
                JPAClusteredEJBConcurrencyComparisonTest test = new JPAClusteredEJBConcurrencyComparisonTest();
                EmployeeService service = test.nextEmployeeService();
                Employee employee = (Employee)service.findAll().get(0);
                
                for (int index = 0; index < 5; index++) {
                    service = test.nextEmployeeService();
                    employee = service.findById(employee.getId());
                }

                int sleeps[] = new int[]{1, 50, 100, 500, 1000, 2000, 3000, 4000, 5000, 10000};
                int sleep = 0;
                boolean success = false;
                boolean failed = false;
                while (!success && (sleep < sleeps.length)) {
                    for (int index = 0; index < 10; index++) {
                        service = test.nextEmployeeService();
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
        };
        test.setName("ComputeLagTest");
        return test;
    }
}
