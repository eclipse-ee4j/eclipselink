/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.spatial.jgeometry.wrapped;

import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.tests.spatial.jgeometry.SimpleJGeometryTestModel;
import org.eclipse.persistence.testing.tests.spatial.jgeometry.SimpleSpatialTestCase;

/**
 * This test model tests the Oracle JGeometry type.  It only operates on Oracle
 * 
 * This test model requires the following SQL be run prior to running the test suite:
 * CREATE OR REPLACE TYPE MY_GEOMETRY AS OBJECT (id NUMBER, geom MDSYS.SDO_GEOMETRY)
 *
 */
public class WrappedJGeometryTestModel extends TestModel {
   
    public WrappedJGeometryTestModel () {
        setDescription("Test CRUD operations on objects containing attributes of type JGeometry wrapped in Oracle user defined types.");
    }
    
    public void addTests() {
        if (getSession().getDatasourceLogin().getPlatform().isOracle()){
            SimpleSpatialTestCase.setIsJunit(false);
            SimpleJGeometryTestModel.session = getSession();
            addTest(WrappedJGeometryTestSuite.suite());
        }
    }

}
