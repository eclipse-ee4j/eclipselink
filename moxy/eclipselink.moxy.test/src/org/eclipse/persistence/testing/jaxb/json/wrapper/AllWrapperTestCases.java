/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4.2 - initial implementation
 ******************************************************************************/
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
        return suite;
    }

}
