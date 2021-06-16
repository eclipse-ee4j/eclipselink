/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Radek Felcman - 2.7.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.array;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ArrayTestCases extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("ArrayTestCases");
        suite.addTestSuite(BooleanArrayTestCases.class);
        suite.addTestSuite(BooleanListTestCases.class);
        suite.addTestSuite(IntegerArrayTestCases.class);
        suite.addTestSuite(IntegerListTestCases.class);
        suite.addTestSuite(StringArrayTestCases.class);
        suite.addTestSuite(StringListTestCases.class);
        suite.addTestSuite(Nested2dArrayTestCases.class);
        suite.addTestSuite(Nested3dArrayTestCases.class);
        return suite;
    }

}
