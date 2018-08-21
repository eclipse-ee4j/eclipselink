/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.4.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.wrapper;

import org.eclipse.persistence.testing.jaxb.json.attribute.JSONAttributePrefixOnContextTestCases;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllWrapperTestCases extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Wrapper Test Suite");
        suite.addTestSuite(DefaultTestCases.class);
        suite.addTestSuite(WrapperNameTestCases.class);
        suite.addTestSuite(WrapperNameEmptyAbsentTestCases.class);
        suite.addTestSuite(WrapperNameEmptyPresentTestCases.class);
        suite.addTestSuite(WrapperNameNullAbsentTestCases.class);
        suite.addTestSuite(WrapperXmlPathTestCases.class);
        suite.addTestSuite(WrapperNamespaceTestCases.class);
        suite.addTestSuite(ChoiceCollectionTestCases.class);
        suite.addTestSuite(AnyCollectionTestCases.class);
        suite.addTestSuite(ElementRefsCollectionTestCases.class);
        suite.addTestSuite(BinaryCollectionTestCases.class);
        suite.addTestSuite(WrapperAndXmlPathTestCases.class);
        suite.addTestSuite(WrapperNameSingleItemTestCases.class);
        return suite;
    }

}
