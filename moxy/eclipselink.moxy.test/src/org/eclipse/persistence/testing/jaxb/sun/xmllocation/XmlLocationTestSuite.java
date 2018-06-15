/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 19 October 2011 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.sun.xmllocation;

import junit.framework.Test;
import junit.framework.TestSuite;

public class XmlLocationTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("XmlLocation Test Suite");
        suite.addTestSuite(XmlLocationTestCases.class);
        suite.addTestSuite(XmlLocationNonTransientTestCases.class);
        suite.addTestSuite(XmlLocationErrorTestCases.class);
        suite.addTestSuite(XmlLocationSchemaGenTests.class);
        return suite;
    }

}
