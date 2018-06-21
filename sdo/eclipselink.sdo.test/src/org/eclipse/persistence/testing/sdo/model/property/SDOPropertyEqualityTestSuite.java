/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.sdo.model.property;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SDOPropertyEqualityTestSuite extends TestCase {
    public SDOPropertyEqualityTestSuite(String name) {
        super(name);
    }

    /**
     * Inherited suite method for generating all test cases.
     * @return
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("SDOProperty Equality Test Suite");
        suite.addTestSuite(SDOPropertyEqualityTests.class);
        suite.addTestSuite(XmlElementPropertyTestCases.class);
        return suite;
    }
}
