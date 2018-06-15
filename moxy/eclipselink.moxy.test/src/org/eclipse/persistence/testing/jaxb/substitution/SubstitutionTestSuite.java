/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     rbarkhouse - 2010-03-04 12:22:11 - initial implementation
package org.eclipse.persistence.testing.jaxb.substitution;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SubstitutionTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB Substitution Groups Test Suite");
        suite.addTestSuite(SubstitutionEnglishTestCases.class);
        suite.addTestSuite(SubstitutionFrenchTestCases.class);
        suite.addTestSuite(SubstitutionTestCases.class);
        suite.addTestSuite(SubstitutionBindingsTestCases.class);
        return suite;
    }

}
