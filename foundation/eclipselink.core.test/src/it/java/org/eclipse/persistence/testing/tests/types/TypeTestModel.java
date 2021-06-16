/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.types;

import java.util.*;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;

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

    public void addRequiredSystems() {
        addRequiredSystem(new TypeTestSystem());
    }

    public void addTests() {
        addTest(getBooleanTestSuite());
        addTest(getTimeDateTestSuite());
        addTest(getStringTestSuite());
        addTest(getNumericTestSuite());
        addTest(getBLOBTestSuite());
        addTest(getCLOBTestSuite());
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

    public void setup() {
        DatabaseSession session = (DatabaseSession)getSession();
        if (useAccessors) {
            (session).addDescriptor(BooleanTester.descriptorWithAccessors());
            session.addDescriptor(TimeDateTester.descriptorWithAccessors());
            session.addDescriptor(NumericTester.descriptorWithAccessors());
            session.addDescriptor(StringTester.descriptorWithAccessors());
            session.addDescriptor(CLOBTester.descriptorWithAccessors());
            session.addDescriptor(BLOBTester.descriptorWithAccessors());
        } else {
            session.addDescriptor(BooleanTester.descriptor());
            session.addDescriptor(TimeDateTester.descriptor());
            session.addDescriptor(NumericTester.descriptor());
            session.addDescriptor(StringTester.descriptor());
            session.addDescriptor(CLOBTester.descriptor());
            session.addDescriptor(BLOBTester.descriptor());
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
