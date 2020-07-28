/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.nativeapitest;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.nativeapitest.Address;
import org.eclipse.persistence.testing.models.nativeapitest.Department;
import org.eclipse.persistence.testing.models.nativeapitest.Employee;
import org.eclipse.persistence.testing.models.nativeapitest.EmployeeService;
import org.eclipse.persistence.testing.models.nativeapitest.NativeAPITestTableCreator;

/**
 * EJB 3 NativeAPITests tests. Testing using EclipseLink Native ORM API in a JEE
 * EJB 3 SessionBean environment. These tests can only be run with a server.
 */
public class NativeAPITests extends JUnitTestCase {
    protected EmployeeService service;

    public NativeAPITests() {
        super();
    }

    public NativeAPITests(String name) {
        super(name);
    }

    public NativeAPITests(String name, boolean shouldRunTestOnServer) {
        super(name);
        this.shouldRunTestOnServer = shouldRunTestOnServer;
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("NativeAPITests");
        suite.addTest(new NativeAPITests("testSetup", true));
        suite.addTest(new NativeAPITests("testFindAll", false));
        suite.addTest(new NativeAPITests("testFindAllServer", true));
        suite.addTest(new NativeAPITests("testMerge", false));
        suite.addTest(new NativeAPITests("testMergeServer", true));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to alow
     * execution in the server.
     */
    public void testSetup() throws Exception {
        new NativeAPITestTableCreator().replaceTables((ServerSession)SessionManager.getManager().getSession("NativeAPITest", this.getClass().getClassLoader()));
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
    // server, Oc4j
    "java:comp/env/ejb/EmployeeService", "ejb/EmployeeService",
    // WLS
    "EmployeeService#org.eclipse.persistence.testing.models.nativeapitest.EmployeeService",
    // WAS
    "org.eclipse.persistence.testing.models.nativeapitest.EmployeeService",
    // jboss
    "eclipselink-nativeapitest-model/EmployeeServiceBean/remote-org.eclipse.persistence.testing.models.nativeapitest.EmployeeService",
    // wildfly
    "eclipselink-nativeapitest-model/eclipselink-nativeapitest-model_ejb/EmployeeServiceBean!org.eclipse.persistence.testing.models.nativeapitest.EmployeeService",
    // NetWeaver
    "JavaEE/servertest/REMOTE/EmployeeServiceBean/org.eclipse.persistence.testing.models.nativeapitest.EmployeeService" };

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
                service = (EmployeeService) PortableRemoteObject.narrow(context.lookup(candidate), EmployeeService.class);
                return service;
            } catch (NamingException namingException) {
                // OK, try next
            }
        }

        throw new RuntimeException("EmployeeService bean could not be looked up under any of the following names:\n" + Arrays.asList(LOOKUP_STRINGS));
    }

    public void testFindAll() throws Exception {
        List result = getEmployeeService().findAll();
        for (Iterator iterator = result.iterator(); iterator.hasNext();) {
            Employee employee = (Employee) iterator.next();
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
        employee.setLastName("Wayy");
        employee.getAddress().setCity("Kanata");
        getEmployeeService().update(employee);

        employee = getEmployeeService().findById(id);
        if (!employee.getLastName().equals("Wayy")) {
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
