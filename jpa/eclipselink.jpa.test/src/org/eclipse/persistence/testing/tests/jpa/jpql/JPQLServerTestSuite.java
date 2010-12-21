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
package org.eclipse.persistence.testing.tests.jpa.jpql;

import junit.framework.TestSuite;
import junit.framework.Test;

/**
 * <p><b>Purpose</b>: To collect the tests that will run against Application Server only.
 */
public class JPQLServerTestSuite extends TestSuite {
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("JPQL ServerTestSuite");
        suite.addTest(AdvancedQueryTestSuite.suite());
        suite.addTest(JUnitJPQLComplexAggregateTestSuite.suite());
        suite.addTest(JUnitJPQLComplexTestSuite.suite());
        suite.addTest(JUnitJPQLDateTimeTestSuite.suite());
        suite.addTest(JUnitJPQLExamplesTestSuite.suite());
        suite.addTest(JUnitJPQLInheritanceTestSuite.suite());
        suite.addTest(JUnitJPQLModifyTestSuite.suite());
        suite.addTest(JUnitJPQLParameterTestSuite.suite());
        suite.addTest(JUnitJPQLSimpleTestSuite.suite());
        suite.addTest(JUnitJPQLUnitTestSuite.suite());
        suite.addTest(JUnitJPQLValidationTestSuite.suite());
        suite.addTest(JUnitNativeQueryTestSuite.suite());

        return suite;
    }
}
