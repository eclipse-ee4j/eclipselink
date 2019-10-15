/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class JAXBSAXTestSuite extends TestCase {
    public static final String CONTEXT_PATH = "org.eclipse.persistence.testing.oxm.jaxb.sax";

    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB Test Suite");

        String contextPath = "org.eclipse.persistence.testing.oxm.jaxb.sax";
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
        suite.addTestSuite(ListenerTestCases.class);
        suite.addTestSuite(FlushTestCases.class);
        suite.addTestSuite(CrNewLineTestCases.class);

        return suite;
    }
}
