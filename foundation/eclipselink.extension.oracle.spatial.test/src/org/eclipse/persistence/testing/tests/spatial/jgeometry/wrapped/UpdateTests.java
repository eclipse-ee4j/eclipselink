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

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.UpdateAllQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped.MyGeometry;
import org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped.WrappedSpatial;
import org.eclipse.persistence.testing.tests.spatial.jgeometry.SampleGeometries;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This test requires the following SQL be run prior to running the test suite:
 * CREATE OR REPLACE TYPE MY_GEOMETRY AS OBJECT (id NUMBER, geom MDSYS.SDO_GEOMETRY)
 *
 * SQL samples from C:\oracle\db\10.2\md\demo\examples\eginsert.sql
 * Note: Table re-named from TEST81 to SIMPLE_SPATIAL
 */
public class UpdateTests extends WrappedSpatialTestCase {

    public UpdateTests(String name){
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("UpdateTests");
        suite.addTest(new UpdateTests("testUpdateSingleToNull"));
        suite.addTest(new UpdateTests("testUpdateAllToNull"));
        suite.addTest(new UpdateTests("testReplaceExisting"));

        return new TestSetup(suite) {
            private boolean shouldBindAllParameters;
            @Override
            protected void setUp(){
                try{
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

    public void testUpdateSingleToNull() throws Exception {
        UnitOfWork uow = session.acquireUnitOfWork();

        WrappedSpatial ws =
            (WrappedSpatial)uow.readObject(WrappedSpatial.class, new ExpressionBuilder().get("geometry").notNull());
        assertNotNull("No WrappedSpatial instances found", ws);

        ws.setGeometry(null);

        uow.writeChanges();
        uow.release();
    }

    public void testUpdateAllToNull() throws Exception {
        session.getLogin().setShouldBindAllParameters(false);
        UnitOfWork uow = session.acquireUnitOfWork();

        UpdateAllQuery uaq = new UpdateAllQuery(WrappedSpatial.class);
        uaq.addUpdate("geometry", (Object)null);
        // BUG: 5102352 - workaround requires selection criteria
        uaq.setSelectionCriteria(uaq.getExpressionBuilder().get("id").notNull());

        try {
            uow.executeQuery(uaq);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        uow.writeChanges();
        uow.release();
    }

    public void testReplaceExisting() {
        UnitOfWork uow = session.acquireUnitOfWork();

        WrappedSpatial ws =
            (WrappedSpatial)uow.readObject(WrappedSpatial.class, new ExpressionBuilder().get("id").equal(1001));
        assertNotNull("No WrappedSpatial instances found", ws);

        MyGeometry geom = new MyGeometry(30, new SampleGeometries().simplyPolygon());
        ws.setGeometry(geom);

        uow.writeChanges();
        uow.release();
    }

}
