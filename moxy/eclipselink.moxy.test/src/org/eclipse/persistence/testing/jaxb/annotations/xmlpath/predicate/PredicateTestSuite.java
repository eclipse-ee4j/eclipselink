/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
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
        return suite;
    }

}