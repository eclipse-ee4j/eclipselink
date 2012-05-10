/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Denise Smith - September 22 /2009
 ******************************************************************************/
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
