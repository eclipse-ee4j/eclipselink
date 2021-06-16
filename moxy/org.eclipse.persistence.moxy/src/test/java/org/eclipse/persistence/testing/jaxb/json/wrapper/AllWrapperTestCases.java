/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
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
