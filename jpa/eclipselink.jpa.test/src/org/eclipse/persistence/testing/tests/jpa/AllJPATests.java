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
package org.eclipse.persistence.testing.tests.jpa;

import junit.framework.Test;

import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.tests.jpa.advanced.JPAAdvancedTestModel;
import org.eclipse.persistence.testing.tests.jpa.remote.RemoteEntityManagerTestSuite;

/**
 * <p><b>Purpose</b>: Test run for all JPA tests.
 */
public class AllJPATests extends TestModel {
        /**
         * Return the JUnit suite to allow JUnit runner to find it.
         * Unfortunately JUnit only allows suite methods to be static,
         * so it is not possible to generically do this.
         */
        public static junit.framework.TestSuite suite() 
        {
            return new AllJPATests();
        }
    
        public AllJPATests() {
            addTest(new JPAAdvancedTestModel());
            addTest(FullRegressionTestSuite.suite());
            addTest(RemoteEntityManagerTestSuite.suite());
            try {
                addTest((Test)Class.forName("org.eclipse.persistence.testing.tests.jpa21.advanced.JPA21TestSuite").getMethod("suite", null).invoke(null, null));
            } catch (Exception exception) {
                System.out.println("WARNING: " + exception);                
            }
        }
}
