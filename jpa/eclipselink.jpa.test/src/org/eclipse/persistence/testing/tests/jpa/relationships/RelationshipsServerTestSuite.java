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
package org.eclipse.persistence.testing.tests.jpa.relationships;

import junit.framework.TestSuite;
import junit.framework.Test;

/**
 * <p><b>Purpose</b>: To collect the tests that will run against server only.
 */

public class RelationshipsServerTestSuite extends TestSuite{

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("ServerTestSuite");

        suite.addTestSuite(EMQueryJUnitTestSuite.class);
        suite.addTestSuite(ExpressionJUnitTestSuite.class);

        suite.addTest(VirtualAttributeTestSuite.suite());
        //suite.addTest(ValidationTestSuite.suite()); in validation test model
        //suite.addTest(QueryParameterValidationTestSuite.suite()); in validation test model
        suite.addTest(UniAndBiDirectionalMappingTestSuite.suite());
        suite.addTest(RelationshipModelJUnitTestSuite.suite());
        return suite;
    }
   
}
