/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.typevariable;

import org.eclipse.persistence.testing.jaxb.typemappinginfo.parray.StringArrayTestsCases;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TypeVariableTestSuite extends TestCase {

    public TypeVariableTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Type Variable Test Suite");
        suite.addTestSuite(PropertyTestCases.class);
        suite.addTestSuite(ExtendedList1TestCases.class);
        suite.addTestSuite(ExtendedList2TestCases.class);
        suite.addTestSuite(ExtendedList3TestCases.class);
        suite.addTestSuite(ExtendedList4TestCases.class);
        suite.addTestSuite(ExtendedList5TestCases.class);
        suite.addTestSuite(ExtendedList6TestCases.class);
        suite.addTestSuite(ExtendedList7TestCases.class);
        suite.addTestSuite(ExtendedList8TestCases.class);
        suite.addTestSuite(ExtendedList8ChildTestCases.class);
        suite.addTestSuite(ExtendedList9TestCases.class);
        suite.addTestSuite(ExtendedList10TestCases.class);
        suite.addTestSuite(ExtendedMap1TestCases.class);
        suite.addTestSuite(ExtendedMap2TestCases.class);
        suite.addTestSuite(ExtendedMap3TestCases.class);
        suite.addTestSuite(ExtendedMap4TestCases.class);
        suite.addTestSuite(ExtendedMap5TestCases.class);
        suite.addTestSuite(ExtendedMap6TestCases.class);
        suite.addTestSuite(ExtendedMap7TestCases.class);
        return suite;
    }

}
