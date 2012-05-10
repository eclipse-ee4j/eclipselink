/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
        suite.addTest(Query_Basic_Tests.suite());
        suite.addTest(Query_OrderedHint.suite());       
        suite.addTest(Query_SpatialOp_ExpExp_Tests.suite());
        suite.addTest(Query_SpatialExpOp_ExpGeom_Tests.suite());
        suite.addTest(Query_SpatialOp_ExpReport_Tests.suite());
        suite.addTest(UpdateTests.suite());
        suite.addTest(DeleteTests.suite());
        suite.addTest(NamedQueryTests.suite());
        
        return suite;
    }

}
