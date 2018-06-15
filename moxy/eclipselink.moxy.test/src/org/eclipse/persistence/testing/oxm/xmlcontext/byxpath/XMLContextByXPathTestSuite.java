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
//     rbarkhou - 21 Sept 2009 - 1.2 - Initial implementation
package org.eclipse.persistence.testing.oxm.xmlcontext.byxpath;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class XMLContextByXPathTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("XML Context 'Get Value By XPath' Test Suite");
        suite.addTestSuite(ByXPathTestCases.class);
        suite.addTestSuite(ByXPathNSTestCases.class);
        return suite;
    }
}
