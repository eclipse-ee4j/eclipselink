/*******************************************************************************
 * Copyright (c) 1998, 2015 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.advanced;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.IsolatedHashMapTest;
import org.eclipse.persistence.testing.tests.jpa.advanced.compositepk.AdvancedCompositePKJunitTest;
import org.eclipse.persistence.testing.tests.jpa.advanced.fetchgroup.AdvancedFetchGroupJunitTest;
import org.eclipse.persistence.testing.tests.jpa.complexaggregate.ComplexAggregateTestSuite;

/**
 * <p><b>Purpose</b>: To collect the tests that will run against Application Server only.
 */
public class AdvancedServerTestSuite extends TestSuite {
    public static Test suite() {
        JUnitTestCase.initializePlatform();
        TestSuite suite = new TestSuite();
        suite.setName("Advanced ServerTestSuite");
        if (System.getProperty("run.metadata.cache.test.suite").compareTo("true") == 0) {
            suite.addTest(new org.eclipse.persistence.testing.tests.jpa.advanced.MetadataCachingTestSuite("testProjectCacheWithDefaultPU"));
        }
        suite.addTest(IsolatedHashMapTest.suite());
        suite.addTest(EntityManagerJUnitTestSuite.suite());
        suite.addTest(CallbackEventJUnitTestSuite.suite());
        suite.addTest(SQLResultSetMappingTestSuite.suite());
        suite.addTest(JoinedAttributeAdvancedJunitTest.suite());
        suite.addTest(ReportQueryMultipleReturnTestSuite.suite());
        suite.addTest(ReportQueryAdvancedJUnitTest.suite());
        suite.addTest(ExtendedPersistenceContextJUnitTestSuite.suite());
        suite.addTest(ReportQueryConstructorExpressionTestSuite.suite());
        suite.addTest(OptimisticConcurrencyJUnitTestSuite.suite());
        suite.addTest(AdvancedJPAJunitTest.suite());
        suite.addTest(AdvancedJunitTest.suite());
        suite.addTest(AdvancedCompositePKJunitTest.suite());
        suite.addTest(QueryCastTestSuite.suite());
        suite.addTest(ComplexAggregateTestSuite.suite()); 
        if (! JUnitTestCase.isJPA10()) {
            suite.addTest(AdvancedFetchGroupJunitTest.suite());
        }
        return suite;
    }
}
