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
package org.eclipse.persistence.testing.tests.performance;

import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.tests.performance.java.CalendarTimeTest;
import org.eclipse.persistence.testing.tests.performance.java.ClassForNameTest;
import org.eclipse.persistence.testing.tests.performance.java.ComplexMethodTest;
import org.eclipse.persistence.testing.tests.performance.java.ConcurrentHashMapGetConcurrentTest;
import org.eclipse.persistence.testing.tests.performance.java.ConcurrentHashMapPutConcurrentTest;
import org.eclipse.persistence.testing.tests.performance.java.DatePrintingTest;
import org.eclipse.persistence.testing.tests.performance.java.DoPrivilegedTest;
import org.eclipse.persistence.testing.tests.performance.java.FieldTest;
import org.eclipse.persistence.testing.tests.performance.java.ForLoopTest;
import org.eclipse.persistence.testing.tests.performance.java.HashMapGetConcurrentTest;
import org.eclipse.persistence.testing.tests.performance.java.HashMapPutConcurrentTest;
import org.eclipse.persistence.testing.tests.performance.java.HashtableGetConcurrentTest;
import org.eclipse.persistence.testing.tests.performance.java.HashtablePutConcurrentTest;
import org.eclipse.persistence.testing.tests.performance.java.InstanceCreationTest;
import org.eclipse.persistence.testing.tests.performance.java.LazyInitConcurrentTest;
import org.eclipse.persistence.testing.tests.performance.java.LinkedHashMapTest;
import org.eclipse.persistence.testing.tests.performance.java.ListTest;
import org.eclipse.persistence.testing.tests.performance.java.MapTest;
import org.eclipse.persistence.testing.tests.performance.java.MethodTest;
import org.eclipse.persistence.testing.tests.performance.java.SynchronizedLazyInitConcurrentTest;
import org.eclipse.persistence.testing.tests.performance.java.UnsynchronizedLazyInitConcurrentTest;
import org.eclipse.persistence.testing.tests.performance.java.VolatileLazyInitConcurrentTest;
import org.eclipse.persistence.testing.tests.performance.jdbc.InheritanceOuterJoinTest;
import org.eclipse.persistence.testing.tests.performance.jdbc.LargeRowFetchTest;
import org.eclipse.persistence.testing.tests.performance.jdbc.RowFetchTest;
import org.eclipse.persistence.testing.tests.performance.jdbc.TimestampDateTest;
import org.eclipse.persistence.testing.tests.performance.jdbc.TimestampTest;

/**
 * Performance tests that compare the performance of two or more ways of doing something.
 * This allows for the performance difference between two task to be determined and verified.
 * This can be used for analyzing which is the best way to do something,
 * or to verify that usage of a optimization feature continues to provide the expected benefit.
 * This model includes the non-TopLink tests used to check base Java performance
 * used to evaluate optimizations in TopLink.
 */
public class JavaPerformanceComparisonModel extends TestModel {
    public JavaPerformanceComparisonModel() {
        setDescription("Non-TopLink Java performance tests that compare/verify the performance of two or more ways of doing something.");
    }

    @Override
    public void addTests() {
        addTest(getJavaTestSuite());
        addTest(getJDBCTestSuite());
    }

    public TestSuite getJavaTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("JavaTestSuite");
        suite.setDescription("This suite tests raw Java performance.");

        suite.addTest(new MapTest(10));
        suite.addTest(new MapTest(100));
        suite.addTest(new MapTest(1000));
        suite.addTest(new ListTest(10));
        suite.addTest(new ListTest(100));
        suite.addTest(new ListTest(1000));
        suite.addTest(new ForLoopTest());
        suite.addTest(new InstanceCreationTest());
        suite.addTest(new ClassForNameTest());
        suite.addTest(new LinkedHashMapTest());
        suite.addTest(new MethodTest());
        suite.addTest(new FieldTest());
        suite.addTest(new ComplexMethodTest());
        suite.addTest(new CalendarTimeTest());
        suite.addTest(new DatePrintingTest());
        suite.addTest(new DoPrivilegedTest());
        suite.addTest(new LazyInitConcurrentTest());
        suite.addTest(new SynchronizedLazyInitConcurrentTest());
        suite.addTest(new UnsynchronizedLazyInitConcurrentTest());
        suite.addTest(new VolatileLazyInitConcurrentTest());
        suite.addTest(new HashMapGetConcurrentTest());
        suite.addTest(new HashtableGetConcurrentTest());
        suite.addTest(new ConcurrentHashMapGetConcurrentTest());
        suite.addTest(new HashMapPutConcurrentTest());
        suite.addTest(new HashtablePutConcurrentTest());
        suite.addTest(new ConcurrentHashMapPutConcurrentTest());

        return suite;
    }

    public TestSuite getJDBCTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("JDBCTestSuite");
        suite.setDescription("This suite tests raw JDBC performance.");

        suite.addTest(new RowFetchTest());
        suite.addTest(new LargeRowFetchTest());
        suite.addTest(new TimestampTest());
        suite.addTest(new TimestampDateTest());
        suite.addTest(new InheritanceOuterJoinTest());

        return suite;
    }
}
