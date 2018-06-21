/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate;

import org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate.ns.PredicateNsTestSuite;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PredicateTestSuite extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("Predicate Test Suite");
        suite.addTestSuite(BinderTestCases.class);
        suite.addTestSuite(ElementTestCases.class);
        suite.addTestSuite(JAXBContextXPathTestCases.class);
        suite.addTestSuite(PositionalTestCases.class);
        suite.addTestSuite(QNameTestCases.class);
        suite.addTestSuite(ReferenceTestCases.class);
        suite.addTestSuite(XPathsTestCases.class);
        suite.addTest(PredicateNsTestSuite.suite());
        suite.addTestSuite(AttributeTestCases.class);
        suite.addTestSuite(PredicateWithSlashTestCases.class);
        return suite;
    }

}
