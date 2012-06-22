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
        //test.getEmployeeService().setup();
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
                
                service = test.nextEmployeeService();
                employee = service.findById(employee.getId());
                service = test.nextEmployeeService();
                employee = service.findById(employee.getId());
                service = test.nextEmployeeService();
                employee = service.findById(employee.getId());
                
                int random = (int)(Math.random() * 1000000);
                employee.setLastName(String.valueOf(random));
                service.update(employee);
                
                service = test.nextEmployeeService();
                random = (int)(Math.random() * 1000000);
                employee = service.findById(employee.getId());
                employee.setLastName(String.valueOf(random));
                service.update(employee);
                
                service = test.nextEmployeeService();
                random = (int)(Math.random() * 1000000);
                employee = service.findById(employee.getId());
                employee.setLastName(String.valueOf(random));
                service.update(employee);
                
                service = test.nextEmployeeService();
                random = (int)(Math.random() * 1000000);
                employee = service.findById(employee.getId());
                employee.setLastName(String.valueOf(random));
                service.update(employee);                
            }
        };
        test.setName("ClusterValidationTest");
        return test;
    }
}
