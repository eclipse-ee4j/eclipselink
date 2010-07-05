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
package org.eclipse.persistence.testing.tests.jpa;

import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.tests.jpa.FullRegressionTestSuite;

/**
 * <p><b>Purpose</b>: Test run for all CMP3 tests.
 */
public class AllCMP3TestRunModel extends TestModel {
        /**
         * Return the JUnit suite to allow JUnit runner to find it.
         * Unfortunately JUnit only allows suite methods to be static,
         * so it is not possible to generically do this.
         */
        public static junit.framework.TestSuite suite() 
        {
            return new AllCMP3TestRunModel();
        }
    
        public AllCMP3TestRunModel() {
            addTest(new org.eclipse.persistence.testing.tests.jpa.advanced.JPAAdvancedTestModel());
            addTest(new org.eclipse.persistence.testing.tests.jpa.inheritance.CMP3InheritanceTestModel());
            addTest(new org.eclipse.persistence.testing.tests.jpa.complexaggregate.CMP3ComplexAggregateTestModel());
            addTest(FullRegressionTestSuite.suite());
        }
}
