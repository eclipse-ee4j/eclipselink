/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class JAXBDOMTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB Test Suite");

        String contextPath = "org.eclipse.persistence.testing.oxm.jaxb.dom";
        System.setProperty("jaxb.test.contextpath", contextPath);
        boolean jaxpPlatform = true;


        suite.addTestSuite(MarshallerTestCases.class);
        suite.addTestSuite(UnmarshallerTestCases.class);
        if (!jaxpPlatform) {
            suite.addTestSuite(ValidatorTestCases.class);
        }
        suite.addTestSuite(UnmarshallValidationTestCases.class);
        suite.addTestSuite(MarshallerPropertiesTestCases.class);
        suite.addTestSuite(CharacterEscapeHandlerTestCases.class);
        suite.addTestSuite(MarshallerFormattingTestCases.class);
        suite.addTest(MarshallerEncodingTestCases.suite());
        suite.addTestSuite(MarshallerFragmentTestCases.class);
        suite.addTestSuite(XMLDeclarationTestCases.class);
        suite.addTestSuite(UnmarshalConversionExceptionTestCases.class);

        return suite;
    }
}
