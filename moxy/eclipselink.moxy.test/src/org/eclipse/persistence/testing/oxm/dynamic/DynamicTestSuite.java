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
//     rbarkhouse - 2009-11-16 14:08:13 - initial implementation
package org.eclipse.persistence.testing.oxm.dynamic;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DynamicTestSuite extends TestCase {
    public DynamicTestSuite(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.main(new String[] { "-c", "org.eclipse.persistence.testing.oxm.dynamic.DynamicTestSuite" });
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Dynamic Persistence Test Cases");
        suite.addTestSuite(DynamicTestCases.class);
        return suite;
    }

}
