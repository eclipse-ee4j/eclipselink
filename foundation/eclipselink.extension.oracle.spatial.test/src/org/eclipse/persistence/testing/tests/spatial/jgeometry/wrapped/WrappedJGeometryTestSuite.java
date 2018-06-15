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
package org.eclipse.persistence.testing.tests.spatial.jgeometry.wrapped;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 *  This test suite is designed to be used both in JUnit and the testing browser
 *  It tests CRUD operations for mappings that Map a JGeometry object wrapped an Oracle user defined type
 * It only operates on Oracle
 *
 * This test model requires the following SQL be run prior to running the test suite:
 * CREATE OR REPLACE TYPE MY_GEOMETRY AS OBJECT (id NUMBER, geom MDSYS.SDO_GEOMETRY)
 *
 */
public class WrappedJGeometryTestSuite {

    public static Test suite() {
        TestSuite suite;
        suite = new TestSuite("Wrapped Spatial tests");
        suite.addTest(CreateTests.suite());
        suite.addTest(UpdateTests.suite());
        suite.addTest(Query_Basic_Tests.suite());
        suite.addTest(Query_OrderedHint.suite());
        suite.addTest(Query_SpatialOp_ExpExp_Tests.suite());
        suite.addTest(Query_SpatialExpOp_ExpGeom_Tests.suite());
        // commented out due to a bug in Oracle 12c
//        suite.addTest(Query_SpatialOp_ExpReport_Tests.suite());
        suite.addTest(DeleteTests.suite());
        suite.addTest(NamedQueryTests.suite());
        return suite;
    }

}
