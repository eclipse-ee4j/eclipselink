/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpql;

import org.eclipse.persistence.exceptions.JPQLException;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Vector;

public class IdentifierTest extends JPQLTestCase {
    public static IdentifierTest underscoreIdentTest() {
        IdentifierTest theTest = new IdentifierTest();
        theTest.setName("Underscore identifier test");
        theTest.setEjbqlString("SELECT OBJECT(emp) FROM Employee emp");

        return theTest;
    }

    public static IdentifierTest dollarSignIdentTest() {
        IdentifierTest theTest = new IdentifierTest();
        theTest.setName("Dollar sign identifier test");
        theTest.setEjbqlString("SELECT OBJECT($emp) FROM Employee $emp");

        return theTest;
    }

    public static IdentifierTest underscoreSchemaNameTest() {
        IdentifierTest theTest = new IdentifierTest();
        theTest.setName("Underscore Schema name test");
        theTest.setEjbqlString("SELECT OBJECT(emp) FROM _Employee emp");

        return theTest;
    }

    public static IdentifierTest dollarSignSchemaNameTest() {
        IdentifierTest theTest = new IdentifierTest();
        theTest.setName("Dollar sign Schema name test");
        theTest.setEjbqlString("SELECT OBJECT(emp) FROM $Employee emp");

        return theTest;
    }

    public static IdentifierTest numericIdentTest() {
        IdentifierTest theTest = new IdentifierTest();
        theTest.setName("Numeric Identifier test");
        theTest.setEjbqlString("SELECT OBJECT(emp1) FROM Employee emp1");

        return theTest;
    }

    public static JPQLExceptionTest badIdentifierTest1() {
        JPQLExceptionTest theTest = new JPQLExceptionTest();
        theTest.setName("Bad Identifier test1");
        theTest.setEjbqlString("SELECT OBJECT(+emp1) FROM Employee +emp1");
        theTest.expectedException = JPQLException.syntaxErrorAt(null, 0, 0, null, null);

        return theTest;
    }

    public static JPQLExceptionTest badIdentifierTest2() {
        JPQLExceptionTest theTest = new JPQLExceptionTest();
        theTest.setName("Bad Identifier test2");
        theTest.setEjbqlString("SELECT OBJECT(emp 1) FROM Employee emp 1");
        theTest.expectedException = JPQLException.syntaxErrorAt(null, 0, 0, null, null);

        return theTest;
    }

    public static IdentifierTest complexIdentTest() {
        IdentifierTest theTest = new IdentifierTest();
        theTest.setName("Complex Identifier test");
        theTest.setEjbqlString("SELECT OBJECT(_$emp1) FROM Employee _$emp1");

        return theTest;
    }

    public static void addTestsTo(TestSuite theSuite) {
        TestSuite newSuite = new TestSuite();
        newSuite.setName("Identifier test suite");

        newSuite.addTest(IdentifierTest.complexIdentTest());
        newSuite.addTest(IdentifierTest.dollarSignIdentTest());
        newSuite.addTest(IdentifierTest.dollarSignSchemaNameTest());
        newSuite.addTest(IdentifierTest.numericIdentTest());
        newSuite.addTest(IdentifierTest.underscoreIdentTest());
        newSuite.addTest(IdentifierTest.underscoreSchemaNameTest());

        theSuite.addTest(newSuite);
    }

    @Override
    public void setup() {
        Vector employees = getSession().readAllObjects(Employee.class);

        setOriginalOject(employees);

        super.setup();
    }
}
