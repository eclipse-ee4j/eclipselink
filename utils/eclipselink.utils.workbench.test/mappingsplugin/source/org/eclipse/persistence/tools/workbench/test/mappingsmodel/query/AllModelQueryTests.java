/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.query;


import org.eclipse.persistence.tools.workbench.test.mappingsmodel.query.xml.MWEisInteractionTests;

import junit.framework.*;

/**
 * decentralize test creation code
 */
public class AllModelQueryTests {

public static Test suite() {
    TestSuite suite = new TestSuite("test.org.eclipse.persistence.tools.workbench.mappingsmodel.query");

    suite.addTest(MWUserDefinedQueryKeyTests.suite());
    suite.addTest(MWQueryableTests.suite());
    suite.addTest(MWQueryTests.suite());
    suite.addTest(MWExpressionTests.suite());
    suite.addTest(MWExpressionUndoableTests.suite());
    suite.addTest(MWEisInteractionTests.suite());

    return suite;
}

/**
 * suppress instantiation
 */
private AllModelQueryTests(String name) {
    super();
}

}
