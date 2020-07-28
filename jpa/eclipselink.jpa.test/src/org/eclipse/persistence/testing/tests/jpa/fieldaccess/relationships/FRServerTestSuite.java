/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships;

import junit.framework.TestSuite;
import junit.framework.Test;

/**
 * <p><b>Purpose</b>: To collect the tests that will run against Application Server only.
 */
public class FRServerTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("Fieldaccess Relationships ServerTestSuite");
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships.UniAndBiDirectionalMappingTestSuite.suite());
        suite.addTestSuite(org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships.ExpressionJUnitTestSuite.class);
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships.RelationshipModelJUnitTestSuite.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships.IsolatedCacheTestSuite.suite());

        return suite;
    }
}
