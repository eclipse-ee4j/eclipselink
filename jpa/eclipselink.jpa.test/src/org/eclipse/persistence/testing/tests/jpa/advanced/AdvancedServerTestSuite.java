/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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

import junit.framework.TestSuite;
import junit.framework.Test;

import org.eclipse.persistence.testing.tests.jpa.advanced.AdvancedJPAJunitTest;
import org.eclipse.persistence.testing.tests.jpa.advanced.AdvancedJunitTest;
import org.eclipse.persistence.testing.tests.jpa.advanced.NamedNativeQueryJUnitTest;
import org.eclipse.persistence.testing.tests.jpa.advanced.CallbackEventJUnitTestSuite;
import org.eclipse.persistence.testing.tests.jpa.advanced.EntityManagerJUnitTestSuite;
//import org.eclipse.persistence.testing.tests.jpa.advanced.SQLResultSetMappingTestSuite;
import org.eclipse.persistence.testing.tests.jpa.advanced.JoinedAttributeAdvancedJunitTest;
import org.eclipse.persistence.testing.tests.jpa.advanced.ReportQueryMultipleReturnTestSuite;
import org.eclipse.persistence.testing.tests.jpa.advanced.ExtendedPersistenceContextJUnitTestSuite;
import org.eclipse.persistence.testing.tests.jpa.advanced.ReportQueryConstructorExpressionTestSuite;
import org.eclipse.persistence.testing.tests.jpa.advanced.OptimisticConcurrencyJUnitTestSuite;
import org.eclipse.persistence.testing.tests.jpa.advanced.compositepk.AdvancedCompositePKJunitTest;
import org.eclipse.persistence.testing.tests.jpa.advanced.ReportQueryAdvancedJUnitTest;

/**
 * <p><b>Purpose</b>: To collect the tests that will run against Application Server only.
 */
public class AdvancedServerTestSuite extends TestSuite {
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("Advanced ServerTestSuite");
        
        suite.addTest(EntityManagerJUnitTestSuite.suite());
        suite.addTest(NamedNativeQueryJUnitTest.suite());
        suite.addTest(CallbackEventJUnitTestSuite.suite());
        //suite.addTest(SQLResultSetMappingTestSuite.suite()); - hangup for J2SE
        suite.addTest(JoinedAttributeAdvancedJunitTest.suite());
        suite.addTest(ReportQueryMultipleReturnTestSuite.suite());
        suite.addTest(ReportQueryAdvancedJUnitTest.suite());
        suite.addTest(ExtendedPersistenceContextJUnitTestSuite.suite());
        suite.addTest(ReportQueryConstructorExpressionTestSuite.suite());
        suite.addTest(OptimisticConcurrencyJUnitTestSuite.suite());
        suite.addTest(AdvancedJPAJunitTest.suite());
        suite.addTest(AdvancedJunitTest.suite());
        suite.addTest(AdvancedCompositePKJunitTest.suite());

        return suite;
    }
}
