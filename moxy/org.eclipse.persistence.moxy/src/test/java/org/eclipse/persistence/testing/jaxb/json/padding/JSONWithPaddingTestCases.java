/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
