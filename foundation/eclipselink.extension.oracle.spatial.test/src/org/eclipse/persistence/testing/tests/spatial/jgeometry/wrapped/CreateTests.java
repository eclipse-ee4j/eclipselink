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


import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped.WrappedSpatial;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This test requires the following SQL be run prior to running the test suite:
 *
 * CREATE OR REPLACE TYPE MY_GEOMETRY AS OBJECT (id NUMBER, geom MDSYS.SDO_GEOMETRY)
 *
 * SQL samples from C:\oracle\db\10.2\md\demo\examples\eginsert.sql
 * Note: Table re-named from TEST81 to SIMPLE_SPATIAL
 */
public class CreateTests extends WrappedSpatialTestCase {

    public CreateTests(String name){
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CreateTests");
        suite.addTest(new CreateTests("testCreate_SRID_0_WithoutBinding"));
        suite.addTest(new CreateTests("testCreate_SRID_0_WithBinding"));
        suite.addTest(new CreateTests("testInsertNullWithBinding"));
        suite.addTest(new CreateTests("testInsertNullWithoutBinding"));

        return new TestSetup(suite) {
            private boolean shouldBindAllParameters;
            @Override
            protected void setUp(){
                try {
                    shouldBindAllParameters = getSession().getLogin().getShouldBindAllParameters();
                    WrappedSpatialTestCase.repopulate(getSession(), true);
                } catch (Exception e){
                    throw new TestProblemException("Could not setup JGeometry test model. Note: This model requires you to run the following CREATE OR REPLACE TYPE MY_GEOMETRY AS OBJECT (id NUMBER, geom MDSYS.SDO_GEOMETRY): ", e);
                }
            }

            @Override
            protected void tearDown() {
                try {
                    getSession().getLogin().setShouldBindAllParameters(shouldBindAllParameters);
                } catch (Exception e){
                    throw new TestProblemException("Could not clean up JGeometry test model", e);
                }
            }
        };
    }

    public void testCreate_SRID_0_WithoutBinding() throws Exception {
        getSession().getLogin().setShouldBindAllParameters(false);

        WrappedSpatialTestCase.repopulate(getSession(), true);
    }

    public void testCreate_SRID_0_WithBinding() throws Exception {
        getSession().getLogin().setShouldBindAllParameters(true);

        WrappedSpatialTestCase.repopulate(getSession(), true);
    }

    public void testInsertNullWithBinding() throws Exception {
        getSession().getLogin().setShouldBindAllParameters(true);
        UnitOfWork uow = getSession().acquireUnitOfWork();

        WrappedSpatial ss =
            (WrappedSpatial)uow.newInstance(WrappedSpatial.class);
        ss.setId(123456789);

        uow.writeChanges();
        uow.release();
    }

    public void testInsertNullWithoutBinding() throws Exception {
        getSession().getLogin().setShouldBindAllParameters(false);
        UnitOfWork uow = getSession().acquireUnitOfWork();

        WrappedSpatial ss =
            (WrappedSpatial)uow.newInstance(WrappedSpatial.class);
        ss.setId(123456789);

        uow.writeChanges();
        uow.release();
    }
}
