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
package org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced;

import junit.framework.TestSuite;
import junit.framework.Test;

import org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.AdvancedJPAJunitTest;
import org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.AdvancedJunitTest;
import org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.CallbackEventJUnitTestSuite;
import org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.EntityManagerJUnitTestSuite;
import org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.ExtendedPersistenceContextJUnitTestSuite;
import org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.JoinedAttributeAdvancedJunitTest;
import org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.NamedNativeQueryJUnitTest;
import org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.OptimisticConcurrencyJUnitTestSuite;
import org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.ReportQueryAdvancedJUnitTest;
import org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.ReportQueryConstructorExpressionTestSuite;
import org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.ReportQueryMultipleReturnTestSuite;
import org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.UpdateAllQueryAdvancedJunitTest;

/**
 * <p><b>Purpose</b>: To collect the tests that will run against Application Server only.
 */
public class FAServerTestSuite extends TestSuite {
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("Fieldaccess Advanced ServerTestSuite");
        
        suite.addTest(AdvancedJPAJunitTest.suite());
        suite.addTest(AdvancedJunitTest.suite());
        suite.addTest(CallbackEventJUnitTestSuite.suite());
        suite.addTest(EntityManagerJUnitTestSuite.suite());
        suite.addTest(ExtendedPersistenceContextJUnitTestSuite.suite());
        suite.addTest(JoinedAttributeAdvancedJunitTest.suite());
        suite.addTest(NamedNativeQueryJUnitTest.suite());
        suite.addTest(OptimisticConcurrencyJUnitTestSuite.suite());
        suite.addTest(ReportQueryAdvancedJUnitTest.suite());
        suite.addTest(ReportQueryConstructorExpressionTestSuite.suite());
        suite.addTest(ReportQueryMultipleReturnTestSuite.suite());
        //suite.addTest(SQLResultSetMappingTestSuite.suite()); - hangup for J2SE
        suite.addTest(UpdateAllQueryAdvancedJunitTest.suite());
        
        return suite;
    }
}
