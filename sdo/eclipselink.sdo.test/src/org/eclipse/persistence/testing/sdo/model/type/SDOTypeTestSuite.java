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
package org.eclipse.persistence.testing.sdo.model.type;

import org.eclipse.persistence.testing.sdo.substitution.SubstitutionInheritanceTestCases;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SDOTypeTestSuite {

    public SDOTypeTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Inherited suite method for generating all test cases.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("All SDO Type Tests");

        suite.addTest(new TestSuite(SDOTypeInstanceClassTestCases.class));
        suite.addTest(new TestSuite(AddBaseTypeTestCases.class));
        suite.addTest(new TestSuite(DefaultPackageFromTypeGenerationTestCases.class));
        suite.addTest(new TestSuite(ElementWithBuiltInTypeNameTestCases.class));
        suite.addTest(new TestSuite(SubstitutionInheritanceTestCases.class));

        return suite;
    }

}
