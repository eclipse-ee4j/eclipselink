/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.spatial.jgeometry;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 *  This test suite is designed to be used both in JUnit and the testing browser
 *  It tests CRUD operations for mappings that Directly map a JGeometry object
 *
 * It only operates on Oracle
 */
public class SimpleJGeometryTestSuite {
    public static Test suite() {
        TestSuite suite;
        suite = new TestSuite("SimpleSpatialTests");

        suite.addTest(CreateTests.suite());
        suite.addTest(UpdateTests.suite());
        suite.addTest(Query_Basic_Tests.suite());
        suite.addTest(Query_OrderedHint.suite());
        suite.addTest(Query_SpatialOp_ExpExp_Tests.suite());
        suite.addTest(Query_SpatialExpOp_ExpGeom_Tests.suite());
        suite.addTest(Query_SpatialOp_ExpReport_Tests.suite());
        suite.addTest(DeleteTests.suite());
        suite.addTest(NamedQueryTests.suite());

        return suite;
    }

}
