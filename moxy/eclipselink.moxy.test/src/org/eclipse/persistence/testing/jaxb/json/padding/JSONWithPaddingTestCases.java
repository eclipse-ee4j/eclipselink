/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - November 2012
package org.eclipse.persistence.testing.jaxb.json.padding;

import junit.framework.Test;
import junit.framework.TestSuite;

public class JSONWithPaddingTestCases extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("JSON With Padding Test Suite");
        suite.addTestSuite(JSONWithPaddingSimpleTestCases.class);
        suite.addTestSuite(JAXBElementJSONPaddingTestCases.class);
        suite.addTestSuite(JSONWithNullObjectTestCases.class);
        suite.addTestSuite(JSONWithNullNameTestCases.class);
        suite.addTestSuite(JSONWithUnsetNameTestCases.class);
        suite.addTestSuite(JSONWithPaddingSimpleListTestCases.class);
        suite.addTestSuite(JAXBElementListJSONPaddingTestCases.class);
        return suite;
    }

}
