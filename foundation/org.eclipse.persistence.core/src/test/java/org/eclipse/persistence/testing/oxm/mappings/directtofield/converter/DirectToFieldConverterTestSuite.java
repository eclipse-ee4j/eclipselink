/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
// Denise Smith - September 22 /2009
package org.eclipse.persistence.testing.oxm.mappings.directtofield.converter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DirectToFieldConverterTestSuite extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("DirecToField Converter  Test Suite");

        suite.addTestSuite(ConverterAbsentElementTestCases.class);
        suite.addTestSuite(ConverterEmptyElementTestCases.class);


        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.directtofield.converter.DirectToFieldConverterTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }
}
