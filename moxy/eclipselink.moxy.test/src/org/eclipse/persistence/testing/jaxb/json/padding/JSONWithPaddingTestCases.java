/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - November 2012
 ******************************************************************************/
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
