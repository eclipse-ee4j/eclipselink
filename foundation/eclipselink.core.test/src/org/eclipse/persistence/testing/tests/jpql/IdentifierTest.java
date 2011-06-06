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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpql;

import java.util.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

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

    public void setup() {
        Vector employees = getSession().readAllObjects(Employee.class);

        setOriginalOject(employees);

        super.setup();
    }
}
