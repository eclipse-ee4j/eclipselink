/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpa.sessionbean.ha;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Department;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.sessionbean.ha.EmployeeService;

/**
 * EJB 3 SessionBean tests. Testing using EclipseLink JPA in a JEE EJB 3
 * SessionBean environment. These tests can only be run with a server.
 */
public class SessionBeanTest extends JUnitTestCase {
    protected EmployeeService service;

    public SessionBeanTest() {
        super();
    }

    public SessionBeanTest(String name) {
        super(name);
    }

    public SessionBeanTest(String name, boolean shouldRunTestOnServer) {
        super(name);
        this.shouldRunTestOnServer = shouldRunTestOnServer;
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("SessionBeanTest");
        suite.addTest(new SessionBeanTest("testSetup", true));
        suite.addTest(new SessionBeanTest("testFindAll", false));
        suite.addTest(new SessionBeanTest("testFindAllServer", true));
        suite.addTest(new SessionBeanTest("testMerge", false));
        suite.addTest(new SessionBeanTest("testMergeServer", true));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow
     * execution in the server.
     */
    public void testSetup() throws Exception {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession("sessionbean-ha"));
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

    private static final String[] LOOKUP_STRINGS = new String[] {
    // server
    "java:comp/env/ejb/EmployeeService", "ejb/EmployeeService",
    // WLS
    "EmployeeService#org.eclipse.persistence.testing.models.jpa.sessionbean.ha.EmployeeService",
    // WAS
    "org.eclipse.persistence.testing.models.jpa.sessionbean.ha.EmployeeService",
    // jboss
    "org.eclipse.persistence.jpa.testapps.sessionbean.ha/EmployeeServiceBean/remote-org.eclipse.persistence.testing.models.jpa.sessionbean.ha.EmployeeService",
    // wildfly
    "org.eclipse.persistence.jpa.testapps.sessionbean.ha/org.eclipse.persistence.jpa.testapps.sessionbean.ha_ejb/EmployeeServiceBean!org.eclipse.persistence.testing.models.jpa.sessionbean.ha.EmployeeService",
    "java:jboss/exported/org.eclipse.persistence.jpa.testapps.sessionbean.ha/org.eclipse.persistence.jpa.testapps.sessionbean.ha_ejb/EmployeeServiceBean!org.eclipse.persistence.testing.models.jpa.sessionbean.ha.EmployeeService",
    // NetWeaver
    "JavaEE/servertest/REMOTE/EmployeeServiceBean/org.eclipse.persistence.testing.models.jpa.sessionbean.ha.EmployeeService" };

    public EmployeeService getEmployeeService() throws Exception {
        if (service != null) {
            return service;
        }

        Properties properties = new Properties();
        String url = System.getProperty("server.url");
        if (url != null) {
            properties.put("java.naming.provider.url", url);
        }
        Context context = new InitialContext(properties);

        for (String candidate : LOOKUP_STRINGS) {
            try {
                service = (EmployeeService) context.lookup(candidate);
                return service;
            } catch (NamingException namingException) {
                // OK, try next
            }
        }

        throw new RuntimeException("EmployeeService bean could not be looked up under any of the following names:\n" + Arrays.asList(LOOKUP_STRINGS));
    }

    public void testFindAll() throws Exception {
        List<Employee> result = getEmployeeService().findAll();
        for (Iterator<Employee> iterator = result.iterator(); iterator.hasNext();) {
            Employee employee = iterator.next();
            employee.getFirstName();
            employee.getLastName();
            boolean caughtError = false;
            Address address = null;
            try {
                address = employee.getAddress();
            } catch (ValidationException exception) {
                caughtError = true;
                if (exception.getErrorCode() != ValidationException.INSTANTIATING_VALUEHOLDER_WITH_NULL_SESSION) {
                    throw exception;
                }
            }
            // May not serialize on server, so may be ok.
            if (address == null) {
                if (isOnServer() && !caughtError) {
                    fail("INSTANTIATING_VALUEHOLDER_WITH_NULL_SESSION error not thrown.");
                } else {
                    warning("Client serialization nulls non-instantiated 1-1s.");
                }
            }
            if (employee.getDepartment() == null) {
                fail("Department is null, failed to serialize eager.");
            }
            caughtError = false;
            try {
                employee.getPhoneNumbers().size();
            } catch (ValidationException exception) {
                caughtError = true;
                if (exception.getErrorCode() != ValidationException.INSTANTIATING_VALUEHOLDER_WITH_NULL_SESSION) {
                    throw exception;
                }
            }
            if (address == null && !caughtError) {
                fail("INSTANTIATING_VALUEHOLDER_WITH_NULL_SESSION error not thrown.");
            }
        }
    }

    public void testFindAllServer() throws Exception {
        testFindAll();
    }

    public void testMerge() throws Exception {
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

        employee = getEmployeeService().findById(id);
        employee.setLastName("Way");
        employee.getAddress().setCity("Kanata");
        getEmployeeService().update(employee);

        employee = getEmployeeService().findById(id);
        if (!employee.getLastName().equals("Way")) {
            fail("Last name not updated.");
        }
        if (!employee.getAddress().getCity().equals("Kanata")) {
            fail("City not updated.");
        }
        employee = getEmployeeService().fetchById(id);
        if (employee.getManager() == null) {
            if (isOnServer()) {
                fail("Manager merged to null.");
            } else {
                warning("Merge from client serialization nulls non-instantiated 1-1s.");
            }
        }
    }

    public void testMergeServer() throws Exception {
        testMerge();
    }

}
