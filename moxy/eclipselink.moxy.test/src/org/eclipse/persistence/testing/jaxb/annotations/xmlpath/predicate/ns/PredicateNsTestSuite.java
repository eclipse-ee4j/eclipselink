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
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate.ns;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PredicateNsTestSuite extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("Predicate NS Test Suite");
        suite.addTestSuite(BinderTestCases.class);
        suite.addTestSuite(ElementTestCases.class);
        suite.addTestSuite(JAXBContextXPathTestCases.class);
        suite.addTestSuite(PositionalTestCases.class);
        suite.addTestSuite(QNameTestCases.class);
        suite.addTestSuite(ReferenceTestCases.class);
        suite.addTestSuite(XPathsTestCases.class);
        return suite;
    }

}
