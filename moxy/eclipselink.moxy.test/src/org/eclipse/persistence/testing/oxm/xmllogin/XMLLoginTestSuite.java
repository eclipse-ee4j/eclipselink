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
//     rbarkhou - new test cases for XMLLogin
package org.eclipse.persistence.testing.oxm.xmllogin;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class XMLLoginTestSuite extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("XMLLogin Test Suite");

        suite.addTestSuite(XMLLoginDeploymentXMLTestCases.class);
        suite.addTestSuite(XMLLoginSessionsXMLTestCases.class);

        return suite;
    }

}
