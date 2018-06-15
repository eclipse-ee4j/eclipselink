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
// Denise Smith - September 22 /2009
package org.eclipse.persistence.testing.oxm.mappings.directtofield.converter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.oxm.OXTestCase;

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
