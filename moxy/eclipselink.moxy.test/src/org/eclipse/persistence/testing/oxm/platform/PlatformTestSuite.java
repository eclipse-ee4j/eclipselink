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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.platform;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PlatformTestSuite extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("Platform Test Suite");
        suite.addTestSuite(PlatformCreateDocumentTestCases.class);
        suite.addTestSuite(PlatformNamespaceTestCases.class);
        suite.addTestSuite(PlatformTransformerTestCases.class);
        suite.addTestSuite(PlatformWhitespaceNodeTestCases.class);
        suite.addTestSuite(XMLComparerTestCases.class);
        suite.addTestSuite(PlatformAdvancedXPathTestCases.class);
        suite.addTestSuite(PlatformValidateDocumentTestCases.class);

        return suite;
    }

}
