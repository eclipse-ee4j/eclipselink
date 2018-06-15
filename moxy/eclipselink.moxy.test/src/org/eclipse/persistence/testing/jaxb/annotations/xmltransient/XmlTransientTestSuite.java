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
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmltransient;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class XmlTransientTestSuite extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("XmlTransient Test Suite");
        suite.addTestSuite(XmlTransientFieldTestCases.class);
        suite.addTestSuite(XmlTransientPropertyTestCases.class);
        suite.addTestSuite(DoubleTransientTestCases.class);
        suite.addTestSuite(PropOrderTestCases.class);
        suite.addTestSuite(TransientClassTestCases.class);
        suite.addTestSuite(PropertyOverrideTestCases.class);
        suite.addTestSuite(ObjectWithTransientTestCases.class);
        suite.addTestSuite(ObjectWithTransientListTestCases.class);
        return suite;
    }

}
