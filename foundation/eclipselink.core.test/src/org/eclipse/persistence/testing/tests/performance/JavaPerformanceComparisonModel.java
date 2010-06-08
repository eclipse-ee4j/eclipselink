/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.performance;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.tests.performance.java.*;
import org.eclipse.persistence.testing.tests.performance.jdbc.*;

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
