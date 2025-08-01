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
package org.eclipse.persistence.testing.tests.types;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.TestSuite;

import java.util.Enumeration;

/**
 * Provide tests for all database types.
 */
public class TypeTestModel extends org.eclipse.persistence.testing.framework.TestModel {
    protected boolean useAccessors;

    public TypeTestModel() {
        this(true);
    }

    public TypeTestModel(boolean usingAccessors) {
        useAccessors = usingAccessors;
        setName(toString());
        if (useAccessors) {
            setDescription("Testing of all the JAVA types and their mappings in TopLink (using Accessors)");
        } else {
            setDescription("Testing of all the JAVA types and their mappings in TopLink (without Accessors)");
        }
    }

    @Override
    public void addRequiredSystems() {
        addRequiredSystem(new TypeTestSystem());
    }

    @Override
    public void addTests() {
        addTest(getBooleanTestSuite());
        addTest(getTimeDateTestSuite());
        addTest(getStringTestSuite());
        addTest(getNumericTestSuite());
        addTest(getBLOBTestSuite());
        addTest(getCLOBTestSuite());
        addTest(getUUIDTestSuite());
    }

    public static TestSuite getBLOBTestSuite() {
        TestSuite suite = new TestSuite();
        Enumeration examples = BLOBTester.testInstances().elements();

        suite.setName("BLOB Types Test Suite");
        suite.setDescription("Tests the use of large byte arrays in JAVA with TopLink");

        while (examples.hasMoreElements()) {
            suite.addTest(new WriteTypeObjectTest((TypeTester)examples.nextElement()));
        }
        return suite;
    }

    public static TestSuite getBooleanTestSuite() {
        TestSuite suite = new TestSuite();
        Enumeration examples = BooleanTester.testInstances().elements();

        suite.setName("Boolean Types Test Suite");
        suite.setDescription("Tests the use of JAVA types boolean & Boolean");

        while (examples.hasMoreElements()) {
            suite.addTest(new WriteTypeObjectTest((TypeTester)examples.nextElement()));
        }
        return suite;
    }

    public static TestSuite getCLOBTestSuite() {
        TestSuite suite = new TestSuite();
        Enumeration examples = CLOBTester.testInstances().elements();

        suite.setName("CLOB Types Test Suite");
        suite.setDescription("Tests the use of long string with TopLink");

        while (examples.hasMoreElements()) {
            suite.addTest(new WriteTypeObjectTest((TypeTester)examples.nextElement()));
        }
        return suite;
    }

    public TestSuite getNumericTestSuite() {
        TestSuite suite = new TestSuite();
        Enumeration examples = NumericTester.testInstances(getSession()).elements();

        suite.setName("Numeric Types Test Suite");
        suite.setDescription("Tests the use of all the numeric types in JAVA with TopLink");

        while (examples.hasMoreElements()) {
            suite.addTest(new WriteTypeObjectTest((TypeTester)examples.nextElement()));
        }
        return suite;
    }

    public static TestSuite getStringTestSuite() {
        TestSuite suite = new TestSuite();
        Enumeration examples = StringTester.testInstances().elements();

        suite.setName("String Types Test Suite");
        suite.setDescription("Tests the use of fixed & variable length strings");

        while (examples.hasMoreElements()) {
            suite.addTest(new WriteTypeObjectTest((TypeTester)examples.nextElement()));
        }
        return suite;
    }

    public static TestSuite getTimeDateTestSuite() {
        TestSuite suite = new TestSuite();
        Enumeration examples = TimeDateTester.testInstances().elements();

        suite.setName("Date & Time Types Test Suite");
        suite.setDescription("Tests the use of Date(s)/Time/Timestamp with TopLink");

        while (examples.hasMoreElements()) {
            suite.addTest(new WriteTypeObjectTest((TypeTester)examples.nextElement()));
        }
        return suite;
    }

    public static TestSuite getUUIDTestSuite() {
        TestSuite suite = new TestSuite();
        Enumeration examples = UUIDTester.testInstances().elements();

        suite.setName("UUID Types Test Suite");
        suite.setDescription("Tests the use of UUID with EclipseLink");

        while (examples.hasMoreElements()) {
            suite.addTest(new WriteTypeObjectTest((TypeTester)examples.nextElement()));
        }
        return suite;
    }

    @Override
    public void setup() {
        DatabaseSession session = (DatabaseSession)getSession();
        if (useAccessors) {
            (session).addDescriptor(BooleanTester.descriptorWithAccessors());
            session.addDescriptor(TimeDateTester.descriptorWithAccessors());
            session.addDescriptor(NumericTester.descriptorWithAccessors());
            session.addDescriptor(StringTester.descriptorWithAccessors());
            session.addDescriptor(CLOBTester.descriptorWithAccessors());
            session.addDescriptor(BLOBTester.descriptorWithAccessors());
            session.addDescriptor(UUIDTester.descriptorWithAccessors());
        } else {
            session.addDescriptor(BooleanTester.descriptor());
            session.addDescriptor(TimeDateTester.descriptor());
            session.addDescriptor(NumericTester.descriptor());
            session.addDescriptor(StringTester.descriptor());
            session.addDescriptor(CLOBTester.descriptor());
            session.addDescriptor(BLOBTester.descriptor());
            session.addDescriptor(UUIDTester.descriptor());
        }
    }

    public String toString() {
        if (useAccessors) {
            return super.toString() + " with Accessors";
        } else {
            return super.toString() + " without Accessors";
        }
    }
}
