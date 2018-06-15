/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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

        suite.addTest(EMQueryJUnitTestSuite.suite());
        suite.addTest(ExpressionJUnitTestSuite.suite());
        suite.addTest(IsolatedCacheTestSuite.suite());
        suite.addTest(VirtualAttributeTestSuite.suite());
        //suite.addTest(ValidationTestSuite.suite()); in validation test model
        //suite.addTest(QueryParameterValidationTestSuite.suite()); in validation test model
        suite.addTest(UniAndBiDirectionalMappingTestSuite.suite());
        suite.addTest(RelationshipModelJUnitTestSuite.suite());
        return suite;
    }

}
